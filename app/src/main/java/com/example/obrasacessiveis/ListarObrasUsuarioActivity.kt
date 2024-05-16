package com.example.obrasacessiveis

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

class ListarObrasUsuarioActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listar_obras_usuario)

        val buscar = findViewById<ImageButton>(R.id.checkButton)
        val titulo = findViewById<EditText>(R.id.searchEditText)
        val settings = findViewById<ImageButton>(R.id.settingsButton)

        buscar.setOnClickListener() {
            buscarObraPorTitulo(titulo.text.toString())
        }

        settings.setOnClickListener(){
            trocar()
        }

        val sessao = intent.getIntExtra("sessao", -1)
        if (sessao != -1) {
            val scanLabel = findViewById<TextView>(R.id.scanLabel)
            val sessionText = getString(R.string.session, sessao)
            scanLabel.text = sessionText
        }
    }
    private fun trocar() {
        val telaAdicionar = Intent(this, SairActivityUser::class.java)
        startActivity(telaAdicionar)
    }
    fun buscarObraPorTitulo(tituloBuscado: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("Obras")
            .whereEqualTo("titulo", tituloBuscado)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Log.d("Buscar", "Nenhum documento encontrado com o título: $tituloBuscado")
                } else {
                    for (document in documents) {
                        val titulo = document.getString("titulo") ?: "Título não encontrado"
                        val descricao = document.getString("descricao") ?: "Descrição não encontrada"
                        val autor = document.getString("autor") ?: "Autor não encontrado"
                        val ano = document.getString("ano") ?: "Ano não encontrado"

                        if(titulo.isEmpty()) {
                            Toast.makeText(this, "A obra não foi encontrada", Toast.LENGTH_SHORT).show()
                        }

                        Log.d("Buscar", "Obra encontrada - Título: $titulo, Descrição: $descricao, Autor: $autor, Ano: $ano")

                        val intent = Intent(this, ObrasInfoUsuarioActivity::class.java).apply {
                            putExtra("titulo", tituloBuscado)
                            putExtra("descricao", descricao)
                            putExtra("autor", autor)
                            putExtra("ano", ano)
                        }
                        startActivity(intent)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Buscar", "Erro ao buscar documentos: ", exception)
            }
    }
}