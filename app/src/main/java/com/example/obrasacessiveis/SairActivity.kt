package com.example.obrasacessiveis

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton

class SairActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.configs)
        val sair = findViewById<Button>(R.id.buttonSair)
        val voltar = findViewById<ImageButton>(R.id.voltarButton)

        sair.setOnClickListener() {
            exibirDialogoSair()
        }
        voltar.setOnClickListener() {
            finish()
        }
    }

    private fun exibirDialogoSair() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Sair")
        builder.setMessage("Tem certeza que deseja sair?")
        builder.setPositiveButton("Sim") { dialogInterface: DialogInterface, i: Int ->
            voltarTelaInicial()
        }
        builder.setNegativeButton("Não") { dialogInterface: DialogInterface, i: Int ->
            dialogInterface.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }
    private fun voltarTelaInicial() {
        val telaAdicionar = Intent(this, MainActivity::class.java)
        startActivity(telaAdicionar)
    }
    private fun VoltarParaEscanearObra() {
        val telaAdicionar = Intent(this, EscanearObrasAdminActivity::class.java)
        startActivity(telaAdicionar)
    }

}
