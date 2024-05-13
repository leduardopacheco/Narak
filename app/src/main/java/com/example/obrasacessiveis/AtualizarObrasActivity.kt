package com.example.obrasacessiveis

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import com.google.firebase.firestore.FirebaseFirestore


class AtualizarObrasActivity : Activity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_atualizar_obra)
        val ok = findViewById<ImageButton>(R.id.checkButton)
        val voltar = findViewById<ImageButton>(R.id.voltarButton)
        val cancel = findViewById<ImageButton>(R.id.trashButton)
        val titulo = findViewById<EditText>(R.id.titleTextView)
        val descricao = findViewById<EditText>(R.id.textEditText)
        val ano = findViewById<EditText>(R.id.yearEditText)
        val autor = findViewById<EditText>(R.id.authorEditText)
        var id: String

        FirebaseFirestore.getInstance().collection("Obras").document().update(mapOf(
            "titulo" to titulo.text.toString(),
            "descricao" to descricao.text.toString(),
            "autor" to autor.text.toString(),
            "ano" to ano.text.toString())).addOnSuccessListener {

        }.addOnFailureListener {
                dt->Log.e("test", "error" + dt)
        }

        voltar.setOnClickListener() {
            finish()
        }
    }
    private fun DeletarParaEscanearObra() {
        val intent = Intent(this, ListarObrasAdminActivity::class.java)
        startActivity(intent)
    }
    private fun VoltarParaEscanearObra() {
        val intent = Intent(this, ObrasInfoAdminActivity::class.java)
        startActivity(intent)
    }
}