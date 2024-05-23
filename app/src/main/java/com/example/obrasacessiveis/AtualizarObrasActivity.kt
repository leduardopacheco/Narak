package com.example.obrasacessiveis

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
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
    private val PICK_VIDEO_REQUEST = 2
    private val PICK_AUDIO_REQUEST = 3
    private var filePath: Uri? = null
    private var videoPath: Uri? = null
    private var audioPath: Uri? = null
    private var imagemUrlAtual: String? = null
    private var videoUrlAtual: String? = null
    private var audioUrlAtual: String? = null
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
        val selectVideoButton = findViewById<Button>(R.id.selectVideoButton)
        val selectAudioButton = findViewById<Button>(R.id.selectAudioButton)

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
        videoUrlAtual = intent.getStringExtra("obra_videoUrl")
        audioUrlAtual = intent.getStringExtra("obra_audioUrl")

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

        selectVideoButton.setOnClickListener {
            abrirSelecaoDeVideo()
        }

        selectAudioButton.setOnClickListener {
            abrirSelecaoDeAudio()
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

        var uploadCount = 0
        val totalUploads = 3

        fun checkUploadCompletion() {
            uploadCount++
            if (uploadCount == totalUploads) {
                FirebaseFirestore.getInstance().collection("Obras").document(id).update(novaObra as Map<String, Any>)
                    .addOnSuccessListener {
                        Log.d("AtualizarObrasActivity", "Obra atualizada com sucesso.")
                        setResultAndFinish(RESULT_OK, titulo, descricao, autor, ano, imagemUrlAtual, videoUrlAtual, audioUrlAtual)
                    }
                    .addOnFailureListener { e ->
                        Log.e("AtualizarObrasActivity", "Erro ao atualizar obra", e)
                    }
            }
        }

        if (filePath != null) {
            val storageReference = FirebaseStorage.getInstance().reference.child("images/${UUID.randomUUID()}")
            storageReference.putFile(filePath!!)
                .addOnSuccessListener {
                    storageReference.downloadUrl.addOnSuccessListener { uri ->
                        novaObra["imagemUrl"] = uri.toString()
                        imagemUrlAtual = uri.toString()
                        checkUploadCompletion()
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("AtualizarObrasActivity", "Erro ao fazer upload da nova imagem: $e")
                    checkUploadCompletion()
                }
        } else {
            checkUploadCompletion()
        }

        if (videoPath != null) {
            val storageReference = FirebaseStorage.getInstance().reference.child("videos/${UUID.randomUUID()}")
            storageReference.putFile(videoPath!!)
                .addOnSuccessListener {
                    storageReference.downloadUrl.addOnSuccessListener { uri ->
                        novaObra["videoUrl"] = uri.toString()
                        videoUrlAtual = uri.toString()
                        checkUploadCompletion()
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("AtualizarObrasActivity", "Erro ao fazer upload do novo vídeo: $e")
                    checkUploadCompletion()
                }
        } else {
            checkUploadCompletion()
        }

        if (audioPath != null) {
            val storageReference = FirebaseStorage.getInstance().reference.child("audios/${UUID.randomUUID()}")
            storageReference.putFile(audioPath!!)
                .addOnSuccessListener {
                    storageReference.downloadUrl.addOnSuccessListener { uri ->
                        novaObra["audioUrl"] = uri.toString()
                        audioUrlAtual = uri.toString()
                        checkUploadCompletion()
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("AtualizarObrasActivity", "Erro ao fazer upload do novo áudio: $e")
                    checkUploadCompletion()
                }
        } else {
            checkUploadCompletion()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && data != null && data.data != null) {
            when (requestCode) {
                PICK_IMAGE_REQUEST -> {
                    filePath = data.data
                    try {
                        Glide.with(this).load(filePath).into(findViewById<ImageView>(R.id.imageView))
                    } catch (e: Exception) {
                        Log.e("AtualizarObrasActivity", "Erro ao carregar imagem: $e")
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

    private fun setResultAndFinish(resultCode: Int, titulo: String = "", descricao: String = "", autor: String = "", ano: String = "", imagemUrl: String? = null, videoUrl: String? = null, audioUrl: String? = null) {
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

    private fun abrirSelecaoDeVideo() {
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Selecione um vídeo"), PICK_VIDEO_REQUEST)
    }

    private fun abrirSelecaoDeAudio() {
        val intent = Intent()
        intent.type = "audio/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Selecione um áudio"), PICK_AUDIO_REQUEST)
    }
}
