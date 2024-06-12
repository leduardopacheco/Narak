package com.example.obrasacessiveis

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mediaplayer.VideoViewActivity
import com.google.firebase.firestore.FirebaseFirestore

class ListarObrasAdminActivity : Activity() {

    private lateinit var obrasRecyclerView: RecyclerView
    private lateinit var obraAdapter: ObraAdapter
    private val obrasList = mutableListOf<Obra>()

    @SuppressLint("StringFormatInvalid")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listar_obras_admin)

        val addButton = findViewById<ImageButton>(R.id.addButton)
//        val libras = findViewById<ImageButton>(R.id.librasButton)
        val settings = findViewById<ImageButton>(R.id.settingsButton)
        val buscar = findViewById<ImageButton>(R.id.checkButton)
        val titulo = findViewById<EditText>(R.id.searchEditText)

        settings.setOnClickListener {
            trocar()
        }

        addButton.setOnClickListener {
            TrocarParaAdicionarObra()
        }

        buscar.setOnClickListener {
            buscarObraPorTitulo(titulo.text.toString())
        }

//        libras.setOnClickListener {
//            abrirVideo()
//        }


        val sessao = intent.getIntExtra("sessao", -1)
        if (sessao != -1) {
            val scanLabel = findViewById<TextView>(R.id.scanLabel)
            val sessionText = getString(R.string.session, sessao)
            scanLabel.text = sessionText
        }

        obrasRecyclerView = findViewById(R.id.obrasRecyclerView)
        obraAdapter = ObraAdapter(this, obrasList)
        obrasRecyclerView.layoutManager = LinearLayoutManager(this)
        obrasRecyclerView.adapter = obraAdapter

        listarObras()
    }
//    private fun abrirVideo() {
//        val telaAdicionar = Intent(this, VideoViewActivity::class.java)
//        startActivity(telaAdicionar)
//    }

    private fun TrocarParaAdicionarObra() {
        val telaAdicionar = Intent(this, AdicionarObrasActivity::class.java)
        startActivity(telaAdicionar)
    }

    private fun trocar() {
        val telaAdicionar = Intent(this, SairActivity::class.java)
        startActivity(telaAdicionar)
    }

    private fun listarObras() {
        val db = FirebaseFirestore.getInstance()
        db.collection("Obras")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val obra = document.toObject(Obra::class.java)
                    obrasList.add(obra)
                }
                obraAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("ListarObras", "Erro ao listar obras: ", exception)
            }
    }

    private fun buscarObraPorTitulo(tituloBuscado: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("Obras")
            .whereEqualTo("titulo", tituloBuscado)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Toast.makeText(this, "A obra não foi encontrada", Toast.LENGTH_SHORT).show()
                    Log.d("Buscar", "Nenhum documento encontrado com o título: $tituloBuscado")
                } else {
                    for (document in documents) {
                        val id = document.id
                        val titulo = document.getString("titulo") ?: "Título não encontrado"
                        val descricao = document.getString("descricao") ?: "Descrição não encontrada"
                        val autor = document.getString("autor") ?: "Autor não encontrado"
                        val ano = document.getString("ano") ?: "Ano não encontrado"
                        val imagemUrl = document.getString("imagemUrl") ?: ""

                        if (titulo.isEmpty()) {
                            Toast.makeText(this, "A obra não foi encontrada", Toast.LENGTH_SHORT).show()
                        }
                        Toast.makeText(this, "Obra encontrada com sucesso", Toast.LENGTH_SHORT).show()
                        Log.d("Buscar", "Obra encontrada - Título: $titulo, Descrição: $descricao, Autor: $autor, Ano: $ano")

                        val intent = Intent(this, ObrasInfoAdminActivity::class.java).apply {
                            putExtra("id", id)
                            putExtra("titulo", titulo)
                            putExtra("descricao", descricao)
                            putExtra("autor", autor)
                            putExtra("ano", ano)
                            putExtra("imagemUrl", imagemUrl)
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

data class Obra(
    val id: String = "",
    val titulo: String = "",
    val descricao: String = "",
    val autor: String = "",
    val ano: String = "",
    val imagemUrl: String = ""
)

class ObraAdapter(private val context: Context, private val obras: List<Obra>) : RecyclerView.Adapter<ObraAdapter.ObraViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObraViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_obra, parent, false)
        return ObraViewHolder(view)
    }

    override fun onBindViewHolder(holder: ObraViewHolder, position: Int) {

        val obra = obras[position]
        holder.tituloTextView.text = obra.titulo
        holder.descricaoTextView.text = obra.descricao

        Glide.with(context)
            .load(obra.imagemUrl)
            .into(holder.obraImageView)

        holder.obraImageView.setOnClickListener {
            val intent = Intent(context, ObrasInfoUsuarioActivity::class.java).apply {
                putExtra("id", obra.id)
                putExtra("titulo", obra.titulo)
                putExtra("descricao", obra.descricao)
                putExtra("autor", obra.autor)
                putExtra("ano", obra.ano)
                putExtra("imagemUrl", obra.imagemUrl)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = obras.size

    class ObraViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val obraImageView: ImageView = itemView.findViewById(R.id.obraImageView)
        val tituloTextView: TextView = itemView.findViewById(R.id.tituloTextView)
        val descricaoTextView: TextView = itemView.findViewById(R.id.descricaoTextView)
    }
}

