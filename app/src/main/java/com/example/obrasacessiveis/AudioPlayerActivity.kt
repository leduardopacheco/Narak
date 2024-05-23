package com.example.obrasacessiveis

import android.app.Activity
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class AudioPlayerActivity : Activity() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var btnPlay: Button
    private lateinit var btnPause: Button
    private lateinit var btnExit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.audio_player)

        btnPlay = findViewById(R.id.btnPlay)
        btnPause = findViewById(R.id.btnPause)
        btnExit = findViewById(R.id.btnExit)

        // Recuperar o título da obra da intent
        val tituloObra = intent.getStringExtra("titulo")

        // Referência ao áudio no Firebase Storage
        val storage = Firebase.storage
        val storageRef = storage.reference

        // Recuperar a referência do áudio pelo título da obra no Firestore
        val db = FirebaseFirestore.getInstance()
        db.collection("Obras")
            .whereEqualTo("titulo", tituloObra)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val audioReference = document.getString("audioUrl")
                    audioReference?.let {
                        // Inicializar o MediaPlayer e preparar o áudio
                        mediaPlayer = MediaPlayer().apply {
                            setDataSource(it)
                            prepareAsync() // Preparação assíncrona
                        }

                        // Listener para preparação assíncrona do MediaPlayer
                        mediaPlayer.setOnPreparedListener {
                            Log.d("AudioPlayerActivity", "MediaPlayer preparado e pronto para reprodução.")
                        }

                        // Definindo listeners para os botões
                        btnPlay.setOnClickListener {
                            if (!mediaPlayer.isPlaying) {
                                mediaPlayer.start()
                                Log.d("AudioPlayerActivity", "Reprodução iniciada.")
                            }
                        }

                        btnPause.setOnClickListener {
                            if (mediaPlayer.isPlaying) {
                                mediaPlayer.pause()
                                Log.d("AudioPlayerActivity", "Reprodução pausada.")
                            }
                        }

                        btnExit.setOnClickListener {
                            mediaPlayer.stop()
                            mediaPlayer.release()
                            Log.d("AudioPlayerActivity", "MediaPlayer liberado.")
                            finish()
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("AudioPlayerActivity", "Erro ao buscar o áudio: ${exception.message}")
                // Lidar com falha na busca do áudio
            }
    }

    override fun onStop() {
        super.onStop()
        // Liberar recursos do MediaPlayer quando a atividade é interrompida
        if (this::mediaPlayer.isInitialized) {
            mediaPlayer.release()
            Log.d("AudioPlayerActivity", "MediaPlayer liberado durante onStop.")
        }
    }
}
