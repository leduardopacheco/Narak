package com.example.obrasacessiveis

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import android.graphics.BitmapFactory
import android.widget.Toast
import java.io.FileNotFoundException
import java.io.InputStream


class AdicionarObrasActivity : Activity() {
    private val PICK_IMAGE_REQUEST = 1
    private var filePath: Uri? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adicionar_obra)
        val ok = findViewById<ImageButton>(R.id.checkButton)
        val cancel = findViewById<ImageButton>(R.id.cancelButton)
        val titulo = findViewById<EditText>(R.id.titleTextView)
        val descricao = findViewById<EditText>(R.id.textEditText)
        val autor = findViewById<EditText>(R.id.authorEditText)
        val ano = findViewById<EditText>(R.id.yearEditText)

        val addImage = findViewById<ImageView>(R.id.imageView)
        addImage.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Selecione uma foto"), PICK_IMAGE_REQUEST)
        }

        var id: String
        ok.setOnClickListener() {
            // Salvando dados da obra no Firestore
            val obra = hashMapOf(
                "titulo" to titulo.text.toString(),
                "descricao" to descricao.text.toString(),
                "autor" to autor.text.toString(),
                "ano" to ano.text.toString()
            )

            FirebaseFirestore.getInstance().collection("Obras").add(obra)
                .addOnSuccessListener { documentReference ->
                    id = documentReference.id


                    // Verifica se há uma imagem selecionada
                    if (filePath != null) {
                        // Faz o upload da imagem para o Firebase Storage
                        val storageReference = FirebaseStorage.getInstance().reference.child("images/${UUID.randomUUID()}")
                        storageReference.putFile(filePath!!)
                            .addOnSuccessListener {
                                storageReference.downloadUrl.addOnSuccessListener { uri ->
                                    // Adiciona a URL da imagem ao documento da obra no Firestore
                                    obra["imagemUrl"] = uri.toString()
                                    FirebaseFirestore.getInstance().collection("Obras").document(id).set(obra)
                                        .addOnSuccessListener {
                                            Log.d("AdicionarObrasActivity", "Obra salva com sucesso com URL da imagem.")
                                            val intent = Intent(this, ObraCadastradaActivity::class.java)
                                            intent.putExtra("imagemUrl", uri.toString()) // Substitua "uri.toString()" pelo valor real da URL da imagem
                                            startActivity(intent)
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e("AdicionarObrasActivity", "Erro ao salvar URL da imagem: $e")
                                        }
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.e("AdicionarObrasActivity", "Erro ao fazer upload da imagem: $e")
                            }
                    } else {
                        Log.d("AdicionarObrasActivity", "Obra salva sem imagem.")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("AdicionarObrasActivity", "Erro ao adicionar obra: $e")
                }
            TrocarParaObraCadastrada()
        }

        cancel.setOnClickListener() {
            VoltarParaEscanearObra()
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            filePath = data.data
            try {
                val imageStream: InputStream? = contentResolver.openInputStream(filePath!!)
                val selectedImage = BitmapFactory.decodeStream(imageStream)
                findViewById<ImageView>(R.id.imageView).setImageBitmap(selectedImage)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                Toast.makeText(this, "Erro ao carregar imagem", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun TrocarParaObraCadastrada() {
        val telaAdicionar = Intent(this, ObraCadastradaActivity::class.java)
        startActivity(telaAdicionar)
    }
    private fun VoltarParaEscanearObra() {
        val intent = Intent(this, ListarObrasAdminActivity::class.java)
        startActivity(intent)
    }
}