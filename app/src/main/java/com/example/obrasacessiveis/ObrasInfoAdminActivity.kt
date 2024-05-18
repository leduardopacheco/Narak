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


class ObrasInfoAdminActivity : Activity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_obras_info_admin)

        val editButton = findViewById<ImageButton>(R.id.editButton)
        val voltar = findViewById<ImageButton>(R.id.voltarButton)
        val titulo = findViewById<TextView>(R.id.titleTextView)
        val libras = findViewById<ImageButton>(R.id.librasButton)
        val descricao = findViewById<TextView>(R.id.descriptionEditText)
        val autor = findViewById<TextView>(R.id.authorEditText)
        val ano = findViewById<TextView>(R.id.yearEditText)
        val imagem = findViewById<ImageView>(R.id.obraImageView)

        val id = intent.getStringExtra("id") ?: ""
        val tituloBuscado = intent.getStringExtra("titulo")
        val descricaoBuscada = intent.getStringExtra("descricao")
        val autorBuscado = intent.getStringExtra("autor")
        val anoBuscado = intent.getStringExtra("ano")

        titulo.text = tituloBuscado
        descricao.text = descricaoBuscada
        autor.text = autorBuscado
        ano.text = anoBuscado

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
                        Glide.with(this@ObrasInfoAdminActivity)
                            .load(it)
                            .into(imagem)
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Tratamento de falha ao buscar a imagem
            }

        voltar.setOnClickListener {
            finish()
        }
        libras.setOnClickListener {
            abrirVideo()
        }



        editButton.setOnClickListener {
            val intent = Intent(this, AtualizarObrasActivity::class.java)
            intent.putExtra("id", id)
            intent.putExtra("obra_titulo", tituloBuscado)
            intent.putExtra("obra_descricao", descricaoBuscada)
            intent.putExtra("obra_autor", autorBuscado)
            intent.putExtra("obra_ano", anoBuscado)
            startActivity(intent)
        }

    }

    private fun abrirVideo() {
        val telaAdicionar = Intent(this, VideoViewActivity::class.java)
        startActivity(telaAdicionar)
    }
}
