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
import com.google.firebase.firestore.FirebaseFirestore

class ListarObrasUsuarioActivity : Activity() {

    private lateinit var obrasRecyclerView: RecyclerView
    private lateinit var ObrasAdapter: ObrasAdapter
    private val obrasList = mutableListOf<Obras>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listar_obras_usuario)

        val buscar = findViewById<ImageButton>(R.id.checkButton)
        val titulo = findViewById<EditText>(R.id.searchEditText)
        val settings = findViewById<ImageButton>(R.id.settingsButton)

        buscar.setOnClickListener {
            buscarObraPorTitulo(titulo.text.toString())
        }

        settings.setOnClickListener {
            trocar()
        }

        val sessao = intent.getIntExtra("sessao", -1)
        if (sessao != -1) {
            val scanLabel = findViewById<TextView>(R.id.scanLabel)
            val sessionText = getString(R.string.session, sessao)
            scanLabel.text = sessionText
        }

        obrasRecyclerView = findViewById(R.id.obrasRecyclerView)
        ObrasAdapter = ObrasAdapter(this, obrasList)
        obrasRecyclerView.layoutManager = LinearLayoutManager(this)
        obrasRecyclerView.adapter = ObrasAdapter

        listarObras()
    }

    private fun trocar() {
        val telaAdicionar = Intent(this, SairActivityUser::class.java)
        startActivity(telaAdicionar)
    }

    private fun listarObras() {
        val db = FirebaseFirestore.getInstance()
        db.collection("Obras")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val obra = document.toObject(Obras::class.java)
                    obrasList.add(obra)
                }
                ObrasAdapter.notifyDataSetChanged()
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
                        Log.d("Buscar", "Obras encontrada - Título: $titulo, Descrição: $descricao, Autor: $autor, Ano: $ano")

                        val intent = Intent(this, ObrasInfoUsuarioActivity::class.java).apply {
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

data class Obras(
    val titulo: String = "",
    val descricao: String = "",
    val autor: String = "",
    val ano: String = "",
    val imagemUrl: String = ""
)

class ObrasAdapter(private val context: Context, private val obras: List<Obras>) : RecyclerView.Adapter<ObrasAdapter.ObraViewHolder>() {

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
