package explode2.booster.resource

import explode2.booster.subscribeEvents
import explode2.labyrinth.event.SongCreatedEvent
import org.greenrobot.eventbus.Subscribe
import java.io.File

class FileSystemProvider(private val root: File) : ResourceProvider {

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

	override fun getMusic(id: String): ByteArray =
		getSongFolder(id).resolve("music.mp3").readBytes()

	override fun getPreviewMusic(id: String): ByteArray =
		getSongFolder(id).resolve("preview.mp3").readBytes()

	override fun getCoverPicture(id: String): ByteArray =
		getSongFolder(id).resolve("cover.jpg").readBytes()

	override fun getStoreCoverPicture(id: String): ByteArray =
		getSongFolder(id).resolve("store.jpg").readBytes()

	override fun getChartXML(cid: String, sid: String): ByteArray =
		getSongFolder(sid).resolve("$cid.xml").readBytes()

	override fun getUserAvatar(id: String): ByteArray =
		userAvatarFolder.resolve("$id.jpg").readBytes()
}