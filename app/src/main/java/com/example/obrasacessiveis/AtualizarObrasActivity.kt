package com.example.obrasacessiveis

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import com.google.firebase.firestore.FirebaseFirestore
import android.net.Uri
import android.provider.MediaStore
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.util.*


class AtualizarObrasActivity : Activity() {

    private val PICK_IMAGE_REQUEST = 1
    private var filePath: Uri? = null
    private var imagemUrlAtual: String? = null
    private lateinit var id:String

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

            val novaObra = hashMapOf(
                "titulo" to titulo.text.toString(),
                "descricao" to descricao.text.toString(),
                "autor" to autor.text.toString(),
                "ano" to ano.text.toString(),
            )

            if (filePath != null) {
                val storageReference = FirebaseStorage.getInstance().reference.child("images/${UUID.randomUUID()}")
                storageReference.putFile(filePath!!)
                    .addOnSuccessListener {
                        storageReference.downloadUrl.addOnSuccessListener { uri ->
                            // Adiciona a URL da nova imagem ao documento da obra no Firestore
                            novaObra["imagemUrl"] = uri.toString()

                            FirebaseFirestore.getInstance().collection("Obras").document(id).update(
                                novaObra as Map<String, Any>
                            )
                                .addOnSuccessListener {
                                    Log.d("AtualizarObrasActivity", "Obra atualizada com sucesso com nova imagem.")
                                    setResultAndFinish(RESULT_OK)
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
                // Se não houver nova imagem, apenas atualiza os outros campos da obra
                FirebaseFirestore.getInstance().collection("Obras").document(id).update(novaObra as Map<String, Any>)
                    .addOnSuccessListener {
                        Log.d("AtualizarObrasActivity", "Obra atualizada com sucesso.")
                        setResultAndFinish(RESULT_OK)
                    }
                    .addOnFailureListener { e ->
                        Log.e("AtualizarObrasActivity", "Erro ao atualizar obra", e)
                    }
            }
        }


        voltar.setOnClickListener {
            setResultAndFinish(RESULT_CANCELED)
        }

        trash.setOnClickListener {
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
                                // Tratar erro, se necessário
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("AtualizarObrasActivity", "Erro ao buscar obra", e)
                    // Tratar erro, se necessário
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            filePath = data.data
            try {
                // Carregue a imagem usando Glide diretamente do Uri
                Glide.with(this).load(filePath).into(findViewById<ImageView>(R.id.imageView))

            } catch (e: Exception) {
                Log.e("AtualizarObrasActivity", "Erro ao carregar imagem: $e")
                // Tratar erro, se necessário
            }
        }
    }




    private fun setResultAndFinish(resultCode: Int) {
        setResult(resultCode)
        finish()
        // Retorna para ListarObrasActivity após excluir ou cancelar a edição
        val intent = Intent(this, ListarObrasAdminActivity::class.java)
        startActivity(intent)
    }
    // Método adicionado para abrir a seleção de imagem
    private fun abrirSelecaoDeImagem() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Selecione uma foto"), PICK_IMAGE_REQUEST)
    }
}
