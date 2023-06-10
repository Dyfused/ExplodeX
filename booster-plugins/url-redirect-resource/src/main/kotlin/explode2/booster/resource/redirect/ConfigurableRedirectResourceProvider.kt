package explode2.booster.resource.redirect

import com.github.taskeren.config.Configuration
import explode2.booster.resource.RedirectResourceProvider
import org.slf4j.LoggerFactory
import java.io.File

class ConfigurableRedirectResourceProvider : RedirectResourceProvider {
	override fun getMusic(id: String, token: String?): String {
		return RedirectMusicPattern.replace("{sid}", id)
	}

	override fun getPreviewMusic(id: String, token: String?): String {
		return RedirectPreviewMusicPattern.replace("{sid}", id)
	}

	override fun getCoverPicture(id: String, token: String?): String {
		return RedirectCoverPattern.replace("{sid}", id)
	}

	override fun getStoreCoverPicture(id: String, token: String?): String {
		return RedirectStoreCoverPattern.replace("{sid}", id)
	}

	override fun getChartXML(cid: String, sid: String, token: String?): String {
		return RedirectChartPattern.replace("{cid}", cid)
	}

	override fun getUserAvatar(id: String, token: String?): String {
		return RedirectAvatarPattern.replace("{uid}", id)
	}

	companion object {
		private val logger = LoggerFactory.getLogger("RedirectResource")

		private val config = Configuration(File("redirect.resource.cfg"))

		val RedirectMusicPattern: String =
			config.getString("music", "pattern", "http://example.com/download/{sid}", "音乐下载路径")
		val RedirectPreviewMusicPattern: String =
			config.getString("preview-music", "pattern", "http://example.com/download/{sid}", "预览音乐下载路径")
		val RedirectCoverPattern: String =
			config.getString("cover", "pattern", "http://example.com/download/{sid}", "封面下载路径")
		val RedirectStoreCoverPattern: String =
			config.getString("store-cover", "pattern", "http://example.com/download/{sid}", "商店封面下载路径")
		val RedirectChartPattern: String =
			config.getString("chart", "pattern", "http://example.com/download/{cid}", "谱面下载路径")
		val RedirectAvatarPattern: String =
			config.getString("user-avatar", "pattern", "http://example.com/download/{uid}", "头像下载路径")

		init {
			config.save()

			logger.info("Using Simple Redirecting Resource")
			logger.info("Music Pattern = $RedirectMusicPattern")
			logger.info("Preview Music Pattern = $RedirectPreviewMusicPattern")
			logger.info("Cover Pattern = $RedirectCoverPattern")
			logger.info("Store Cover Pattern = $RedirectStoreCoverPattern")
			logger.info("Chart Pattern = $RedirectChartPattern")
			logger.info("User Avtar Pattern = $RedirectAvatarPattern")
		}
	}
}