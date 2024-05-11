package com.example.obrasacessiveis

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = View(this)
        setContentView(R.layout.activity_login)

        val button = findViewById<Button>(R.id.loginButton)
        val souUsuario = findViewById<TextView>(R.id.souUsuario)
        val cadastrese = findViewById<TextView>(R.id.criarcadastro)

        button.setOnClickListener() {
            TrocarDeTela()
        }
        cadastrese.setOnClickListener() {
            FazerCadastro()
        }
        souUsuario.setOnClickListener() {
            TrocarDeTela2()
        }

        Firebase.firestore.collection("Obras").add(mapOf("nome" to "kasnd"))
    }
    private fun TrocarDeTela() {
        val outraTela = Intent(this, SessionAdmin::class.java)
        startActivity(outraTela)
    }
    private fun TrocarDeTela2() {
        val outraTela = Intent(this, Session::class.java)
        startActivity(outraTela)
    }
    private fun FazerCadastro() {
        val outraTela = Intent(this, FazerCadastroActivity::class.java)
        startActivity(outraTela)
    }
}