package com.example.obrasacessiveis

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class AdicionarObrasActivity : Activity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adicionar_obra)
        val ok = findViewById<ImageButton>(R.id.checkButton)
        val cancel = findViewById<ImageButton>(R.id.cancelButton)
        val titulo = findViewById<EditText>(R.id.titleTextView)
        val descricao = findViewById<EditText>(R.id.textEditText)
        val ano = findViewById<EditText>(R.id.authorEditText)
        val autor = findViewById<EditText>(R.id.yearEditText)
        var id: String
        ok.setOnClickListener() {
            FirebaseFirestore.getInstance().collection("Obras").add(mapOf(
                "titulo" to titulo.text.toString(),
                "descricao" to descricao.text.toString(),
                "autor" to autor.text.toString(),
                "ano" to ano.text.toString())).addOnSuccessListener {
                dr->id = dr.id
            }.addOnFailureListener {
                dt->Log.e("test", "error" + dt)
            }
            TrocarParaObraCadastrada()
        }
        cancel.setOnClickListener() {
            VoltarParaEscanearObra()
        }
    }

    private fun TrocarParaObraCadastrada() {
        val telaAdicionar = Intent(this, ObraCadastradaActivity::class.java)
        startActivity(telaAdicionar)
    }
    private fun VoltarParaEscanearObra() {
        val intent = Intent(this, EscanearObrasAdminActivity::class.java)
        startActivity(intent)
    }
}