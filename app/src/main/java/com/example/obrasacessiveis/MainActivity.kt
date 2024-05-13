package com.example.obrasacessiveis

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.math.BigInteger
import java.security.MessageDigest

fun hashPassword(password: String): String {
    val md = MessageDigest.getInstance("MD5")
    val digest = md.digest(password.toByteArray())
    return BigInteger(1, digest).toString(16).padStart(32, '0')
}

class MainActivity : Activity() {
    private val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val button = findViewById<Button>(R.id.loginButton)
        val souUsuario = findViewById<TextView>(R.id.souUsuario)
        val cadastrese = findViewById<TextView>(R.id.criarcadastro)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)

        button.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            //Log.d("MainActivity", "EMAIL: $email")
            //Log.d("NICOLE", "SENHA: $password")

            if (email.isNotEmpty() && password.isNotEmpty()) {
                login(email, password)
            } else {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }


        cadastrese.setOnClickListener() {
            FazerCadastro()
        }
        souUsuario.setOnClickListener() {
            TrocarDeTela2()
        }


    }
    private fun login(email: String, password: String) {
        db.collection("Cadastro").whereEqualTo("email", email).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val savedPassword = document.getString("senha")
                    val md5Password = hashPassword(password)
                    if (savedPassword == md5Password) {
                        val intent = Intent(this, SessionAdmin::class.java)
                        startActivity(intent)
                        Toast.makeText(this, "Bem-vindo ao ObrasApp", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }
                }
                Toast.makeText(this, "Credenciais inválidas", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Erro ao realizar login: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
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