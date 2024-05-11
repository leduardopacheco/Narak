package com.example.obrasacessiveis

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView

class ObrasEscaneadasUsuarioActivity : Activity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_obras_escaneadas_usuario)
        val voltar = findViewById<ImageButton>(R.id.voltarButton)

        val titulo = findViewById<TextView>(R.id.titleTextView)
        val descricao = findViewById<TextView>(R.id.descriptionEditText)
        val autor = findViewById<TextView>(R.id.authorEditText)
        val ano = findViewById<TextView>(R.id.yearEditText)


        val tituloBuscado = intent.getStringExtra("titulo")
        val descricaoBuscada = intent.getStringExtra("descricao")
        val autorBuscado = intent.getStringExtra("autor")
        val anoBuscado = intent.getStringExtra("ano")

        titulo.text = tituloBuscado
        descricao.text = descricaoBuscada
        autor.text = autorBuscado
        ano.text = anoBuscado

        voltar.setOnClickListener() {
        finish()
        }
    }
    private fun VoltarParaEscanearObra() {
        val telaAdicionar = Intent(this, EscanearObrasUsuarioActivity::class.java)
        startActivity(telaAdicionar)
    }
}

