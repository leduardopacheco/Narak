package com.example.obrasacessiveis

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import com.bumptech.glide.Glide

class ObraCadastradaActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_obra_cadastrada)

        val ok = findViewById<ImageButton>(R.id.checkButton)
        val imagemObra = findViewById<ImageView>(R.id.imageView) // Supondo que haja uma ImageView com o ID "imagemObra" na sua layout XML

        ok.setOnClickListener() {
            TrocarParaEscanearObra()
        }

        // Recebendo a URL da imagem dos extras da intenção
        val imageUrl = intent.getStringExtra("imagemUrl")
        imageUrl?.let {
            // Carregando a imagem usando Glide (ou outra biblioteca de carregamento de imagens)
            Glide.with(this@ObraCadastradaActivity)
                .load(it)
                .into(imagemObra)
        }
    }

    private fun TrocarParaEscanearObra() {
        val outraTela = Intent(this, ListarObrasAdminActivity::class.java)
        startActivity(outraTela)
    }

}
