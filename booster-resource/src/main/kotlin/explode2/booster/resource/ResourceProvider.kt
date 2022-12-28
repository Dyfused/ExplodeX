package explode2.booster.resource

sealed interface ResourceProvider {
	fun getMusic(id: String, token: String?): Any
	fun getPreviewMusic(id: String, token: String?): Any
	fun getCoverPicture(id: String, token: String?): Any
	fun getStoreCoverPicture(id: String, token: String?): Any
	fun getChartXML(cid: String, sid: String, token: String?): Any
	fun getUserAvatar(id: String, token: String?): Any
}

interface ByteArrayResourceProvider : ResourceProvider {
	override fun getMusic(id: String, token: String?): ByteArray
	override fun getPreviewMusic(id: String, token: String?): ByteArray
	override fun getCoverPicture(id: String, token: String?): ByteArray
	override fun getStoreCoverPicture(id: String, token: String?): ByteArray
	override fun getChartXML(cid: String, sid: String, token: String?): ByteArray
	override fun getUserAvatar(id: String, token: String?): ByteArray
}

interface RedirectResourceProvider : ResourceProvider {
	override fun getMusic(id: String, token: String?): String
	override fun getPreviewMusic(id: String, token: String?): String
	override fun getCoverPicture(id: String, token: String?): String
	override fun getStoreCoverPicture(id: String, token: String?): String
	override fun getChartXML(cid: String, sid: String, token: String?): String
	override fun getUserAvatar(id: String, token: String?): String
}

@Deprecated("legacy")
interface SimpleResourceProvider : ByteArrayResourceProvider {

	fun getMusic(id: String): ByteArray
	fun getPreviewMusic(id: String): ByteArray
	fun getCoverPicture(id: String): ByteArray
	fun getStoreCoverPicture(id: String): ByteArray
	fun getChartXML(cid: String, sid: String): ByteArray
	fun getUserAvatar(id: String): ByteArray

	override fun getMusic(id: String, token: String?): ByteArray = getMusic(id)
	override fun getPreviewMusic(id: String, token: String?): ByteArray = getPreviewMusic(id)
	override fun getCoverPicture(id: String, token: String?): ByteArray = getCoverPicture(id)
	override fun getStoreCoverPicture(id: String, token: String?): ByteArray = getStoreCoverPicture(id)
	override fun getChartXML(cid: String, sid: String, token: String?): ByteArray = getChartXML(cid, sid)
	override fun getUserAvatar(id: String, token: String?): ByteArray = getUserAvatar(id)
}