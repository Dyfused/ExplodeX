package net.minecraftforge.fml.loading.log4j;

import net.minecrell.terminalconsole.TerminalConsoleAppender;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.HighlightConverter;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.PerformanceSensitive;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Plugin(
	name = "highlightForge",
	category = "Converter"
)
@ConverterKeys({"highlightForge"})
@PerformanceSensitive({"allocation"})
public class ForgeHighlight {
	protected static final Logger LOGGER = StatusLogger.getLogger();

	public ForgeHighlight() {
	}

	@SuppressWarnings("unused")
	public static HighlightConverter newInstance(Configuration config, String[] options) {
		try {
			Method method = TerminalConsoleAppender.class.getDeclaredMethod("initializeTerminal");
			method.setAccessible(true);
			method.invoke(null);
		} catch (ReflectiveOperationException var3) {
			LOGGER.warn("Failed to invoke initializeTerminal on TCA", var3);
		}

		if (!TerminalConsoleAppender.isAnsiSupported() && Arrays.stream(options).noneMatch((s) -> s.equals("disableAnsi=true"))) {
			List<String> optionList = new ArrayList<>();
			optionList.add(options[0]);
			optionList.add("disableAnsi=true");
			options = optionList.toArray(new String[0]);
		}

		return HighlightConverter.newInstance(config, options);
	}
}

