package explode2.booster.resource

import explode2.booster.subscribeEvents
import explode2.labyrinth.LabyrinthPlugin.Companion.labyrinth
import explode2.labyrinth.event.SongCreatedEvent
import org.greenrobot.eventbus.Subscribe
import java.io.File

/**
 * A provider and receiver interacts with file system.
 *
 * Files for certain songs should be saved in `root/${songId}/` folder,
 * and avatars should be saved in `root/avatars/` folder.
 *
 * No token checks are available for now.
 */
class FileSystemProvider(private val root: File) : ByteArrayResourceProvider, ResourceReceiver {

	init {
		root.mkdirs()
		subscribeEvents()
	}

	@Subscribe
	fun onSongCreate(e: SongCreatedEvent) {
		val sid = e.set.id
		val cidList = e.charts.map { it.id }

		getSongFolder(sid).resolve("music.mp3").createNewFile()
		getSongFolder(sid).resolve("preview.mp3").createNewFile()
		getSongFolder(sid).resolve("cover.jpg").createNewFile()
		getSongFolder(sid).resolve("store.jpg").createNewFile()
		cidList.forEach { cid ->
			getSongFolder(sid).resolve("$cid.xml").createNewFile()
		}
	}

	private fun getSongFolder(songId: String): File =
		root.resolve(songId).apply { mkdirs() }

	private val userAvatarFolder =
		root.resolve("avatars").apply { mkdirs() }

	override fun getMusic(id: String, token: String?): ByteArray {
		return getSongFolder(id).resolve("music.mp3").readBytes()
	}

	override fun getPreviewMusic(id: String, token: String?): ByteArray {
		return getSongFolder(id).resolve("preview.mp3").readBytes()
	}

	override fun getCoverPicture(id: String, token: String?): ByteArray {
		return getSongFolder(id).resolve("cover.jpg").readBytes()
	}

	override fun getStoreCoverPicture(id: String, token: String?): ByteArray {
		return getSongFolder(id).resolve("store.jpg").readBytes()
	}

	override fun getChartXML(cid: String, sid: String, token: String?): ByteArray {
		return getSongFolder(sid).resolve("$cid.xml").readBytes()
	}

	override fun getUserAvatar(id: String, token: String?): ByteArray {
		return userAvatarFolder.resolve("$id.jpg").readBytes()
	}

	private fun String?.getUser() = this?.let { labyrinth.gameUserFactory.getGameUserById(it) }
		?: throw IllegalStateException("invalid token")

	override fun uploadMusic(id: String, token: String?, content: ByteArray) {
		token.getUser().isReviewer
		getSongFolder(id).resolve("music.mp3").writeBytes(content)
	}

	override fun uploadPreviewMusic(id: String, token: String?, content: ByteArray) {
		getSongFolder(id).resolve("preview.mp3").writeBytes(content)
	}

	override fun uploadCoverPicture(id: String, token: String?, content: ByteArray) {
		getSongFolder(id).resolve("cover.jpg").writeBytes(content)
	}

	override fun uploadStoreCoverPicture(id: String, token: String?, content: ByteArray) {
		getSongFolder(id).resolve("store.jpg").writeBytes(content)
	}

	override fun uploadChartXML(cid: String, sid: String, token: String?, content: ByteArray) {
		getSongFolder(sid).resolve("$cid.xml").writeBytes(content)
	}

	override fun uploadUserAvatar(id: String, token: String?, content: ByteArray) {
		userAvatarFolder.resolve("$id.jpg").writeBytes(content)
	}
}