package com.example.obrasacessiveis

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
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
    private val PICK_VIDEO_REQUEST = 2
    private val PICK_AUDIO_REQUEST = 3
    private var filePath: Uri? = null
    private var videoPath: Uri? = null
    private var audioPath: Uri? = null

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
        val selectVideoButton = findViewById<Button>(R.id.selectVideoButton)
        val selectAudioButton = findViewById<Button>(R.id.selectAudioButton)

        addImage.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Selecione uma foto"), PICK_IMAGE_REQUEST)
        }

        selectVideoButton.setOnClickListener {
            val intent = Intent()
            intent.type = "video/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Selecione um vídeo"), PICK_VIDEO_REQUEST)
        }

        selectAudioButton.setOnClickListener {
            val intent = Intent()
            intent.type = "audio/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Selecione um áudio"), PICK_AUDIO_REQUEST)
        }

        ok.setOnClickListener {
            val obra = hashMapOf(
                "titulo" to titulo.text.toString(),
                "descricao" to descricao.text.toString(),
                "autor" to autor.text.toString(),
                "ano" to ano.text.toString()
            )

            FirebaseFirestore.getInstance().collection("Obras").add(obra)
                .addOnSuccessListener { documentReference ->
                    val id = documentReference.id

                    if (filePath != null) {
                        uploadFileToFirebaseStorage(filePath!!, "images/${UUID.randomUUID()}", "imagemUrl", obra, id)
                    }
                    if (videoPath != null) {
                        uploadFileToFirebaseStorage(videoPath!!, "videos/${UUID.randomUUID()}", "videoUrl", obra, id)
                    }
                    if (audioPath != null) {
                        uploadFileToFirebaseStorage(audioPath!!, "audios/${UUID.randomUUID()}", "audioUrl", obra, id)
                    }
                    TrocarParaObraCadastrada()
                }
                .addOnFailureListener { e ->
                    Log.e("AdicionarObrasActivity", "Erro ao adicionar obra: $e")
                }
        }

        cancel.setOnClickListener {
            VoltarParaEscanearObra()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && data != null && data.data != null) {
            when (requestCode) {
                PICK_IMAGE_REQUEST -> {
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
                PICK_VIDEO_REQUEST -> {
                    videoPath = data.data
                    Toast.makeText(this, "Vídeo selecionado", Toast.LENGTH_SHORT).show()
                }
                PICK_AUDIO_REQUEST -> {
                    audioPath = data.data
                    Toast.makeText(this, "Áudio selecionado", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun uploadFileToFirebaseStorage(filePath: Uri, storagePath: String, fieldName: String, obra: HashMap<String, String>, id: String) {
        val storageReference = FirebaseStorage.getInstance().reference.child(storagePath)
        storageReference.putFile(filePath)
            .addOnSuccessListener {
                storageReference.downloadUrl.addOnSuccessListener { uri ->
                    obra[fieldName] = uri.toString()
                    FirebaseFirestore.getInstance().collection("Obras").document(id).set(obra)
                        .addOnSuccessListener {
                            Log.d("AdicionarObrasActivity", "Obra salva com sucesso com URL do $fieldName.")
                        }
                        .addOnFailureListener { e ->
                            Log.e("AdicionarObrasActivity", "Erro ao salvar URL do $fieldName: $e")
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("AdicionarObrasActivity", "Erro ao fazer upload do $fieldName: $e")
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
