package com.example.obrasacessiveis

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import java.math.BigInteger
import java.security.MessageDigest

fun calcularHashMD5(senha: String): String {
    val md = MessageDigest.getInstance("MD5")
    val messageDigest = md.digest(senha.toByteArray())
    val no = BigInteger(1, messageDigest)
    var hashtext = no.toString(16)
    while (hashtext.length < 32) {
        hashtext = "0$hashtext"
    }
    return hashtext
}


class FazerCadastroActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)
        val cadastrar = findViewById<Button>(R.id.cadastrarAdmin)
        val nome = findViewById<EditText>(R.id.nameEditText)
        val email = findViewById<EditText>(R.id.emailEditText)
        val senha = findViewById<EditText>(R.id.passwordEditText)
        val confirmarSenha = findViewById<EditText>(R.id.confirmPasswordEditText)

        cadastrar.setOnClickListener(){
            if(senha.text.toString() != confirmarSenha.text.toString()) {
                Toast.makeText(this, "As senhas não estão iguais", Toast.LENGTH_SHORT).show()
            } else {
                val senhaCriptografada = calcularHashMD5(senha.text.toString())
                val confirmarSenhaCriptografada = calcularHashMD5(confirmarSenha.text.toString())
                Cadastrar(nome, email, senhaCriptografada, confirmarSenhaCriptografada)
                TrocarParaLogin()
            }
        }
    }
    private fun TrocarParaLogin() {
        val telaAdicionar = Intent(this, MainActivity::class.java)
        startActivity(telaAdicionar)
    }
    private fun Cadastrar(nome: EditText, email: EditText, senha: String, confirmarSenha: String,) {
        FirebaseFirestore.getInstance().collection("Cadastro").add(mapOf(
            "nome" to nome.text.toString(),
            "email" to email.text.toString(),
            "senha" to senha,
            "confirmarSenha" to confirmarSenha))
    }

}
