package com.example.mediaplayer

import android.app.Activity
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

        // Referência ao vídeo no Firebase Storage
        val storage = Firebase.storage
        val storageRef = storage.reference
        val videoRef = storageRef.child("videos/teste.mp4")

        // Obter a URL do vídeo
        videoRef.downloadUrl.addOnSuccessListener { uri ->
            // Configurando o VideoView com a URL do vídeo
            videoView.setVideoURI(uri)
        }.addOnFailureListener {
            // Lidar com qualquer erro aqui
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
