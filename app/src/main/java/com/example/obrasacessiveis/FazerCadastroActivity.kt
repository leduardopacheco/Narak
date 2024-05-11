package com.example.obrasacessiveis

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

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
                Cadastrar(nome, email, senha, confirmarSenha)
                TrocarParaLogin()
            }
        }
    }
    private fun TrocarParaLogin() {
        val telaAdicionar = Intent(this, MainActivity::class.java)
        startActivity(telaAdicionar)
    }
    private fun Cadastrar(nome: EditText, email: EditText, senha: EditText, confirmarSenha: EditText,) {
        FirebaseFirestore.getInstance().collection("Cadastro").add(mapOf(
            "nome" to nome.text.toString(),
            "email" to email.text.toString(),
            "senha" to senha.text.toString(),
            "confirmarSenha" to confirmarSenha.text.toString()))
    }

}
