package com.example.obrasacessiveis

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

class ListarObrasAdminActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listar_obras_admin)

        val addButton = findViewById<ImageButton>(R.id.addButton)
        //teste
        val settings = findViewById<ImageButton>(R.id.settingsButton)
        val buscar = findViewById<ImageButton>(R.id.checkButton)
        val titulo = findViewById<EditText>(R.id.searchEditText)


        //teste
        settings.setOnClickListener(){
            trocar()
        }

        addButton.setOnClickListener(){
            TrocarParaAdicionarObra()
        }

        buscar.setOnClickListener() {
            buscarObraPorTitulo(titulo.text.toString())
        }
    }
    private fun TrocarParaAdicionarObra() {
        val telaAdicionar = Intent(this, AdicionarObrasActivity::class.java)
        startActivity(telaAdicionar)
    }
    //teste
    private fun trocar() {
        val telaAdicionar = Intent(this, SairActivity::class.java)
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

                        val intent = Intent(this, ObrasInfoAdminActivity::class.java).apply {
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

