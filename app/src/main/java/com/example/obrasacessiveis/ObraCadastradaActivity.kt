package com.example.obrasacessiveis

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton

class ObraCadastradaActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_obra_cadastrada)
         val ok = findViewById<ImageButton>(R.id.checkButton)

        ok.setOnClickListener() {
            TrocarParaEscanearObra()
        }
    }

    private fun TrocarParaEscanearObra() {
        val outraTela = Intent(this, EscanearObrasAdminActivity::class.java)
        startActivity(outraTela)
    }
}
