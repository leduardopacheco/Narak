package com.example.obrasacessiveis

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class AtualizarObrasActivity : Activity() {

    private val PICK_IMAGE_REQUEST = 1
    private var filePath: Uri? = null
    private var imagemUrlAtual: String? = null
    private lateinit var id: String

    private fun exibirDialogoSair() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Excluir obra")
        builder.setMessage("Tem certeza que deseja excluir esta obra?")
        builder.setPositiveButton("Sim") { dialogInterface: DialogInterface, i: Int ->
            deletarObra()
        }
        builder.setNegativeButton("Não") { dialogInterface: DialogInterface, i: Int ->
            dialogInterface.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun deletarObra() {
        val db = FirebaseFirestore.getInstance()
        db.collection("Obras").document(id).delete()
            .addOnSuccessListener {
                setResultAndFinish(RESULT_OK)
            }
            .addOnFailureListener { e ->
                Log.e("AtualizarObrasActivity", "Erro ao apagar obra", e)
            }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_atualizar_obra)

        val ok = findViewById<ImageButton>(R.id.checkButton)
        val voltar = findViewById<ImageButton>(R.id.voltarButton)
        val trash = findViewById<ImageButton>(R.id.trashButton)
        val titulo = findViewById<EditText>(R.id.titleTextView)
        val descricao = findViewById<EditText>(R.id.textEditText)
        val ano = findViewById<EditText>(R.id.yearEditText)
        val autor = findViewById<EditText>(R.id.authorEditText)
        val imageView = findViewById<ImageView>(R.id.imageView)

        id = intent.getStringExtra("id") ?: ""

        if (id.isEmpty()) {
            Toast.makeText(this, "ID da obra não foi encontrado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val tituloObra = intent.getStringExtra("obra_titulo") ?: ""
        val descricaoObra = intent.getStringExtra("obra_descricao") ?: ""
        val autorObra = intent.getStringExtra("obra_autor") ?: ""
        val anoObra = intent.getStringExtra("obra_ano") ?: ""
        imagemUrlAtual = intent.getStringExtra("obra_imagemUrl")

        titulo.setText(tituloObra)
        descricao.setText(descricaoObra)
        ano.setText(anoObra)
        autor.setText(autorObra)

        imagemUrlAtual?.let {
            Glide.with(this).load(it).into(imageView)
        }

        imageView.setOnClickListener {
            abrirSelecaoDeImagem()
        }

        ok.setOnClickListener {
            atualizarObra(titulo.text.toString(), descricao.text.toString(), autor.text.toString(), ano.text.toString())
        }

        voltar.setOnClickListener {
            setResultAndFinish(RESULT_CANCELED)
        }

        trash.setOnClickListener {
            exibirDialogoSair()
        }
    }

    private fun atualizarObra(titulo: String, descricao: String, autor: String, ano: String) {
        val novaObra = hashMapOf(
            "titulo" to titulo,
            "descricao" to descricao,
            "autor" to autor,
            "ano" to ano,
        )

        if (filePath != null) {
            val storageReference = FirebaseStorage.getInstance().reference.child("images/${UUID.randomUUID()}")
            storageReference.putFile(filePath!!)
                .addOnSuccessListener {
                    storageReference.downloadUrl.addOnSuccessListener { uri ->
                        novaObra["imagemUrl"] = uri.toString()
                        FirebaseFirestore.getInstance().collection("Obras").document(id).update(
                            novaObra as Map<String, Any>
                        )
                            .addOnSuccessListener {
                                Log.d("AtualizarObrasActivity", "Obra atualizada com sucesso com nova imagem.")
                                setResultAndFinish(RESULT_OK, titulo, descricao, autor, ano, uri.toString())
                            }
                            .addOnFailureListener { e ->
                                Log.e("AtualizarObrasActivity", "Erro ao atualizar obra com nova imagem", e)
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("AtualizarObrasActivity", "Erro ao fazer upload da nova imagem: $e")
                }
        } else {
            FirebaseFirestore.getInstance().collection("Obras").document(id).update(novaObra as Map<String, Any>)
                .addOnSuccessListener {
                    Log.d("AtualizarObrasActivity", "Obra atualizada com sucesso.")
                    setResultAndFinish(RESULT_OK, titulo, descricao, autor, ano, imagemUrlAtual)
                }
                .addOnFailureListener { e ->
                    Log.e("AtualizarObrasActivity", "Erro ao atualizar obra", e)
                }
        }
    }

    private fun deletarObra(tituloObra: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("Obras").whereEqualTo("titulo", tituloObra).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    db.collection("Obras").document(document.id).delete()
                        .addOnSuccessListener {
                            setResultAndFinish(RESULT_OK)
                        }
                        .addOnFailureListener { e ->
                            Log.e("AtualizarObrasActivity", "Erro ao apagar obra", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("AtualizarObrasActivity", "Erro ao buscar obra", e)
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            filePath = data.data
            try {
                Glide.with(this).load(filePath).into(findViewById<ImageView>(R.id.imageView))
            } catch (e: Exception) {
                Log.e("AtualizarObrasActivity", "Erro ao carregar imagem: $e")
            }
        }
    }

    private fun setResultAndFinish(resultCode: Int, titulo: String = "", descricao: String = "", autor: String = "", ano: String = "", imagemUrl: String? = null) {
        if (resultCode == RESULT_OK) {
            val intent = Intent(this, ListarObrasAdminActivity::class.java)
            startActivity(intent)
        }
        setResult(resultCode)
        finish()
    }


    private fun abrirSelecaoDeImagem() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Selecione uma foto"), PICK_IMAGE_REQUEST)
    }
}
