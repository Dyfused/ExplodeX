package explode2.booster.resource.oss.aliyun

import com.aliyun.oss.OSS
import com.aliyun.oss.OSSClientBuilder
import com.github.taskeren.config.Configuration
import explode2.booster.resource.RedirectResourceProvider
import explode2.booster.resource.ResourceReceiver
import explode2.labyrinth.LabyrinthPlugin.Companion.labyrinth
import explode2.logging.Colors
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream
import java.io.File
import java.util.*

private const val MUSIC_TYPE = "music"
private const val PREVIEW_MUSIC_TYPE = "preview-music"
private const val COVER_PICTURE_TYPE = "cover-picture"
private const val STORE_COVER_PICTURE_TYPE = "store-cover"
private const val CHART_MAP_TYPE = "chart-map"
private const val USER_AVATAR_TYPE = "user-avatar"

class AliyunOSSResourceProvider : RedirectResourceProvider, ResourceReceiver {

	companion object {

		private val logger = LoggerFactory.getLogger(AliyunOSSResourceProvider::class.java)

		private val config = Configuration(File("aliyun-oss.resource.cfg"))

		private val endpoint: String =
			config.getString("endpoint", "oss", "http://oss-cn-hangzhou.aliyuncs.com", "地域节点地址")

		private val accessKeyId: String =
			config.getString("access-key-id", "user-access", "", "用户 AccessKeyId")
		private val accessKeySecret: String =
			config.getString("access-key-secret", "user-access", "", "用户 AccessKeySecret")

		private val bucketName: String =
			config.getString("bucket-name", "bucket", "explode-oss-resource", "Bucket 名称")
		private val resourceKeyPattern: String =
			config.getString(
				"resource-key-pattern",
				"bucket",
				"{type}-{id}",
				"资源 Key 模板（{type} 为资源类型，{id} 为资源ID）"
			)

		init {
			config.save()
		}

		private val oss: OSS = OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret)

		init {
			if(oss.doesBucketExist(bucketName)) {
				logger.info("${Colors.Light.Green}Aliyun OSS bucket $bucketName is available")
			} else {
				logger.error("${Colors.Light.Red}Aliyun OSS bucket $bucketName was not ready!")
				throw IllegalStateException("Aliyun OSS bucket $bucketName was not ready!")
			}
		}
	}

	private fun getResourceKey(type: String, id: String): String {
		return resourceKeyPattern.replace("{type}", type).replace("{id}", id)
	}

	private fun getResourceUrl(type: String, id: String): String {
		val resourceKey = getResourceKey(type, id)

		if(oss.doesObjectExist(bucketName, resourceKey)) {
			// TODO: 调整过期时间
			val expiration = Date(Date().time + 3600 * 1000)
			return oss.generatePresignedUrl(bucketName, resourceKey, expiration).toString()
		} else {
			throw IllegalStateException("requested resource $resourceKey does not exist in bucket $bucketName")
		}
	}

	/**
	 * 检查 Token 不为空，切用户存在
	 */
	private fun validateToken(token: String?) {
		labyrinth.gameUserFactory.getGameUserById(token ?: error("soudayo is undefined")) ?: error("invalid token")
	}

	private fun validateTokenUpload(token: String?) {
		labyrinth.gameUserFactory.getGameUserById(token ?: error("soudayo is undefined")) ?: error("invalid token")
	}

	override fun getMusic(id: String, token: String?): String {
		validateToken(token)
		return getResourceUrl(MUSIC_TYPE, id)
	}

	override fun getPreviewMusic(id: String, token: String?): String {
		validateToken(token)
		return getResourceUrl(PREVIEW_MUSIC_TYPE, id)
	}

	override fun getCoverPicture(id: String, token: String?): String {
		validateToken(token)
		return getResourceUrl(COVER_PICTURE_TYPE, id)
	}

	override fun getStoreCoverPicture(id: String, token: String?): String {
		validateToken(token)
		return getResourceUrl(STORE_COVER_PICTURE_TYPE, id)
	}

	override fun getChartXML(cid: String, sid: String, token: String?): String {
		validateToken(token)
		return getResourceUrl(CHART_MAP_TYPE, cid)
	}

	override fun getUserAvatar(id: String, token: String?): String {
		validateToken(token)
		return getResourceUrl(USER_AVATAR_TYPE, id)
	}

	override fun uploadMusic(id: String, token: String?, content: ByteArray) {
		validateTokenUpload(token)
		oss.putObject(bucketName, getResourceKey(MUSIC_TYPE, id), ByteArrayInputStream(content))
	}

	override fun uploadPreviewMusic(id: String, token: String?, content: ByteArray) {
		validateTokenUpload(token)
		oss.putObject(bucketName, getResourceKey(PREVIEW_MUSIC_TYPE, id), ByteArrayInputStream(content))
	}

	override fun uploadCoverPicture(id: String, token: String?, content: ByteArray) {
		validateTokenUpload(token)
		oss.putObject(bucketName, getResourceKey(COVER_PICTURE_TYPE, id), ByteArrayInputStream(content))
	}

	override fun uploadStoreCoverPicture(id: String, token: String?, content: ByteArray) {
		validateTokenUpload(token)
		oss.putObject(bucketName, getResourceKey(STORE_COVER_PICTURE_TYPE, id), ByteArrayInputStream(content))
	}

	override fun uploadChartXML(cid: String, sid: String, token: String?, content: ByteArray) {
		validateTokenUpload(token)
		oss.putObject(bucketName, getResourceKey(CHART_MAP_TYPE, cid), ByteArrayInputStream(content))
	}

	override fun uploadUserAvatar(id: String, token: String?, content: ByteArray) {
		validateTokenUpload(token)
		oss.putObject(bucketName, getResourceKey(USER_AVATAR_TYPE, id), ByteArrayInputStream(content))
	}
}