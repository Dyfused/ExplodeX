package explode2.booster.resource

interface ResourceProvider {

	fun getMusic(id: String): ByteArray
	fun getPreviewMusic(id: String): ByteArray
	fun getCoverPicture(id: String): ByteArray
	fun getStoreCoverPicture(id: String): ByteArray
	fun getChartXML(cid: String, sid: String): ByteArray
	fun getUserAvatar(id: String): ByteArray
}