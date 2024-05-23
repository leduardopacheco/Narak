package com.example.mediaplayer

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.VideoView
import com.example.obrasacessiveis.R
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class VideoViewActivity : Activity() {

    private lateinit var videoView: VideoView
    private lateinit var btnPlay: Button
    private lateinit var btnPause: Button
    private lateinit var btnExit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.video_view)

        videoView = findViewById(R.id.videoView)
        btnPlay = findViewById(R.id.btnPlay)
        btnPause = findViewById(R.id.btnPause)
        btnExit = findViewById(R.id.btnExit)

        // Obter a referência do vídeo do extra da intenção
        val videoReference = intent.getStringExtra("videoReference")

        // Verificar se a referência do vídeo não está vazia
        if (!videoReference.isNullOrEmpty()) {
            // Configurar o VideoView com a referência do vídeo
            val videoUri = Uri.parse(videoReference)
            videoView.setVideoURI(videoUri)
        }

        btnPlay.setOnClickListener {
            videoView.start()
        }

        btnPause.setOnClickListener {
            videoView.pause()
        }

        btnExit.setOnClickListener {
            finish()
        }
    }
}
