package com.example.obrasacessiveis

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton

class SairActivityUser : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.configs_user)
        val voltar = findViewById<ImageButton>(R.id.voltarButton)

        voltar.setOnClickListener() {
        finish()
        }
    }

    private fun VoltarParaEscanearObra() {
        val telaAdicionar = Intent(this, EscanearObrasUsuarioActivity::class.java)
        startActivity(telaAdicionar)
    }

}
