package io.papermc.paper.console;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecrell.terminalconsole.MinecraftFormattingConverter;
import net.minecrell.terminalconsole.TerminalConsoleAppender;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.pattern.AbstractPatternConverter;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.core.pattern.PatternFormatter;
import org.apache.logging.log4j.core.pattern.PatternParser;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.fusesource.jansi.AnsiConsole;

@SuppressWarnings({"deprecation", "SpellCheckingInspection"})
@ConverterKeys({"paperMinecraftFormatting"})
@Plugin(name = "paperMinecraftFormatting", category = "Converter")
@PerformanceSensitive({"allocation"})
public final class HexFormattingConverter extends LogEventPatternConverter {
	private static final char COLOR_CHAR = 167;
	private static final String LOOKUP = "0123456789abcdefklmnor";
	private static final String RGB_ANSI = "\u001b[38;2;%d;%d;%dm";
	private final boolean ansi;
	private final List<PatternFormatter> formatters;
	private static final boolean KEEP_FORMATTING = PropertiesUtil.getProperties().getBooleanProperty(MinecraftFormattingConverter.KEEP_FORMATTING_PROPERTY);
	private static final Pattern NAMED_PATTERN = Pattern.compile("§[0-9a-fk-orA-FK-OR]");
	private static final Pattern RGB_PATTERN = Pattern.compile("§x(§[0-9a-fA-F]){6}");
	private static final String ANSI_RESET = "\u001b[m";
	private static final String[] ansiCodes = {"\u001b[0;30m", "\u001b[0;34m", "\u001b[0;32m", "\u001b[0;36m", "\u001b[0;31m", "\u001b[0;35m", "\u001b[0;33m", "\u001b[0;37m", "\u001b[0;30;1m", "\u001b[0;34;1m", "\u001b[0;32;1m", "\u001b[0;36;1m", "\u001b[0;31;1m", "\u001b[0;35;1m", "\u001b[0;33;1m", "\u001b[0;37;1m", "\u001b[5m", "\u001b[21m", "\u001b[9m", "\u001b[4m", "\u001b[3m", ANSI_RESET};

	private HexFormattingConverter(List<PatternFormatter> formatters, boolean strip) {
		super("paperMinecraftFormatting", null);
		this.formatters = formatters;
		this.ansi = !strip;
	}

	@Override
	public void format(LogEvent event, StringBuilder toAppendTo) {
		int start = toAppendTo.length();
		for(PatternFormatter formatter : this.formatters) {
			formatter.format(event, toAppendTo);
		}
		if (KEEP_FORMATTING || toAppendTo.length() == start) {
			return;
		}
		boolean useAnsi = this.ansi && TerminalConsoleAppender.isAnsiSupported();
		String content = toAppendTo.substring(start);
		format(useAnsi ? convertRGBColors(content) : stripRGBColors(content), toAppendTo, start, useAnsi);
	}

	private static String convertRGBColors(String input) {
		Matcher matcher = RGB_PATTERN.matcher(input);
		StringBuilder buffer = new StringBuilder();
		while (matcher.find()) {
			String s = matcher.group().replace(String.valueOf((char) 167), "").replace('x', '#');
			int hex = Integer.decode(s);
			int red = (hex >> 16) & 255;
			int green = (hex >> 8) & 255;
			int blue = hex & 255;
			String replacement = String.format(RGB_ANSI, red, green, blue);
			matcher.appendReplacement(buffer, replacement);
		}
		matcher.appendTail(buffer);
		return buffer.toString();
	}

	private static String stripRGBColors(String input) {
		Matcher matcher = RGB_PATTERN.matcher(input);
		StringBuilder buffer = new StringBuilder();
		while (matcher.find()) {
			matcher.appendReplacement(buffer, "");
		}
		matcher.appendTail(buffer);
		return buffer.toString();
	}

	static void format(String content, StringBuilder result, int start, boolean ansi) {
		int next = content.indexOf(167);
		int last = content.length() - 1;
		if (next == -1 || next == last) {
			result.setLength(start);
			result.append(content);
			if (ansi) {
				result.append(ANSI_RESET);
				return;
			}
			return;
		}
		Matcher matcher = NAMED_PATTERN.matcher(content);
		StringBuilder buffer = new StringBuilder();
		while (matcher.find()) {
			int format = LOOKUP.indexOf(Character.toLowerCase(matcher.group().charAt(1)));
			if (format != -1) {
				matcher.appendReplacement(buffer, ansi ? ansiCodes[format] : "");
			}
		}
		matcher.appendTail(buffer);
		result.setLength(start);
		result.append(buffer);
		if (ansi) {
			result.append(ANSI_RESET);
		}
	}

	@SuppressWarnings("unused")
	public static HexFormattingConverter newInstance(Configuration config, String[] options) {
		if (options.length < 1 || options.length > 2) {
			AbstractPatternConverter.LOGGER.error("Incorrect number of options on paperMinecraftFormatting. Expected at least 1, max 2 received " + options.length);
			return null;
		} else if (options[0] == null) {
			AbstractPatternConverter.LOGGER.error("No pattern supplied on paperMinecraftFormatting");
			return null;
		} else {
			PatternParser parser = PatternLayout.createPatternParser(config);
			List<PatternFormatter> formatters = parser.parse(options[0]);
			boolean strip = options.length > 1 && AnsiConsole.JANSI_MODE_STRIP.equals(options[1]);
			return new HexFormattingConverter(formatters, strip);
		}
	}
}