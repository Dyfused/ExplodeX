import explode2.logging.Colors
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

internal class TestColorOutput {

	private val logger: Logger = LoggerFactory.getLogger("TestColorOutput")

	@Test
	fun testColor() {
		println(Colors.Miku)
		logger.info(Colors.Miku +"AAA")
	}

}