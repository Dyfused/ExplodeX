package explode2.booster.resource

interface ResourceReceiver {
	fun uploadMusic(id: String, token: String?, content: ByteArray)
	fun uploadPreviewMusic(id: String, token: String?, content: ByteArray)
	fun uploadCoverPicture(id: String, token: String?, content: ByteArray)
	fun uploadStoreCoverPicture(id: String, token: String?, content: ByteArray)
	fun uploadChartXML(cid: String, sid: String, token: String?, content: ByteArray)
	fun uploadUserAvatar(id: String, token: String?, content: ByteArray)
}