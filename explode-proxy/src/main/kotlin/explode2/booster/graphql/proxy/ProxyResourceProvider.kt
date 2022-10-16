package explode2.booster.graphql.proxy

import explode2.booster.resource.RedirectResourceProvider

class ProxyResourceProvider : RedirectResourceProvider {

	override fun getMusic(id: String, token: String?): String {
		requireNotNull(token)
		val res = ProxyMaze.getResourceLink(token)
		return "$res/download/music/encoded/$id"
	}

	override fun getPreviewMusic(id: String, token: String?): String {
		requireNotNull(token)
		val res = ProxyMaze.getResourceLink(token)
		return "$res/download/preview/encoded/$id"
	}

	override fun getCoverPicture(id: String, token: String?): String {
		requireNotNull(token)
		val res = ProxyMaze.getResourceLink(token)
		return "$res/download/cover/encoded/$id"
	}

	override fun getStoreCoverPicture(id: String, token: String?): String {
		requireNotNull(token)
		val res = ProxyMaze.getResourceLink(token)
		return "$res/download/cover/480x270_jpg/$id"
	}

	override fun getChartXML(cid: String, sid: String, token: String?): String {
		requireNotNull(token)
		val res = ProxyMaze.getResourceLink(token)
		return "$res/download/chart/encoded/$cid"
	}

	override fun getUserAvatar(id: String, token: String?): String {
		requireNotNull(token)
		val res = ProxyMaze.getResourceLink(token)
		return "$res/download/avatar/256x256_jpg/$id"
	}
}