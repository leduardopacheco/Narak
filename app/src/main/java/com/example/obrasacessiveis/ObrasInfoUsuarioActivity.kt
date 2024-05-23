package com.example.obrasacessiveis

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.mediaplayer.VideoViewActivity
import com.google.firebase.firestore.FirebaseFirestore

class ObrasInfoUsuarioActivity : Activity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_obras_info_usuario)
        val voltar = findViewById<ImageButton>(R.id.voltarButton)

        val titulo = findViewById<TextView>(R.id.titleTextView)
        val descricao = findViewById<TextView>(R.id.descriptionEditText)
        val autor = findViewById<TextView>(R.id.authorEditText)
        val libras = findViewById<ImageButton>(R.id.librasButton)
        val ano = findViewById<TextView>(R.id.yearEditText)
        val imagem = findViewById<ImageView>(R.id.obraImageView)
        val audioButton = findViewById<ImageButton>(R.id.audioButton)



        val tituloBuscado = intent.getStringExtra("titulo")
        val descricaoBuscada = intent.getStringExtra("descricao")
        val autorBuscado = intent.getStringExtra("autor")
        val anoBuscado = intent.getStringExtra("ano")

        titulo.text = tituloBuscado
        descricao.text = descricaoBuscada
        autor.text = autorBuscado
        ano.text = anoBuscado

        audioButton.setOnClickListener {
            abrirAudio(tituloBuscado)
        }


        // Recuperando a URL da imagem do Firestore
        val db = FirebaseFirestore.getInstance()
        db.collection("Obras")
            .whereEqualTo("titulo", tituloBuscado)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val imageUrl = document.getString("imagemUrl")
                    imageUrl?.let {
                        // Carregando a imagem usando Glide (ou outra biblioteca de carregamento de imagens)
                        Glide.with(this@ObrasInfoUsuarioActivity)
                            .load(it)
                            .into(imagem)
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Tratamento de falha ao buscar a imagem
            }

        voltar.setOnClickListener() {
        finish()
        }
        libras.setOnClickListener {
            abrirVideo()
        }
    }
    private fun VoltarParaEscanearObra() {
        val telaAdicionar = Intent(this, ListarObrasUsuarioActivity::class.java)
        startActivity(telaAdicionar)
    }
    private fun abrirVideo() {
        val titulo = intent.getStringExtra("titulo")

        // Recuperar a referência do vídeo pelo título da obra no Firestore
        val db = FirebaseFirestore.getInstance()
        db.collection("Obras")
            .whereEqualTo("titulo", titulo)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val videoReference = document.getString("videoUrl")

                    // Verificar se a referência do vídeo não está vazia
                    if (!videoReference.isNullOrEmpty()) {
                        val intent = Intent(this, VideoViewActivity::class.java)
                        // Passar a referência do vídeo como um extra na intenção
                        intent.putExtra("videoReference", videoReference)
                        startActivity(intent)
                    } else {
                        // Se não houver referência de vídeo para esta obra
                        // Manipule o caso em que não há vídeo disponível
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Tratar falha ao buscar a referência do vídeo
            }
    }
    private fun abrirAudio(tituloObra: String?) {
        val intent = Intent(this, AudioPlayerActivity::class.java)
        intent.putExtra("titulo", tituloObra)
        startActivity(intent)
    }
}

