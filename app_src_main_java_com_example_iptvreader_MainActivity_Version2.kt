package com.example.iptvreader

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.iptvreader.model.IPTVItem
import com.example.iptvreader.parser.M3UParser
import com.example.iptvreader.ui.M3UAdapter
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: M3UAdapter
    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adapter = M3UAdapter(emptyList()) { item -> play(item) }
        recycler.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }

        // Exemplo: trocar para a sua URL de lista M3U
        val sampleUrl = "https://exemplo.com/playlist.m3u"

        lifecycleScope.launchWhenCreated {
            try {
                progressBar.visibility = View.VISIBLE
                val text = loadTextFromUrl(sampleUrl)
                val items = M3UParser.parse(text)
                adapter.update(items)
            } catch (e: Exception) {
                e.printStackTrace()
                text_status.text = "Erro: ${e.message}"
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    private suspend fun loadTextFromUrl(urlStr: String): String = withContext(Dispatchers.IO) {
        val url = URL(urlStr)
        val conn = url.openConnection()
        conn.connectTimeout = 15_000
        conn.readTimeout = 15_000
        BufferedReader(InputStreamReader(conn.getInputStream())).use { reader ->
            val sb = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                sb.append(line).append('\n')
            }
            sb.toString()
        }
    }

    private fun play(item: IPTVItem) {
        if (player == null) {
            player = ExoPlayer.Builder(this).build()
            player_view.player = player
        }
        val mediaItem = MediaItem.fromUri(Uri.parse(item.url))
        player!!.setMediaItem(mediaItem)
        player!!.prepare()
        player!!.playWhenReady = true
    }

    override fun onStop() {
        super.onStop()
        player?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
    }
}