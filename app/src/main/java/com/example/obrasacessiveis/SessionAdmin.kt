package com.example.obrasacessiveis

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton

class SessionAdmin : Activity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.session_admin)
        val voltar = findViewById<ImageButton>(R.id.voltarButton)

        val sessao1 = findViewById<Button>(R.id.buttonSession1)
        val sessao2 = findViewById<Button>(R.id.buttonSession2)
        val sessao3 = findViewById<Button>(R.id.buttonSession3)
        val sessao4 = findViewById<Button>(R.id.buttonSession4)
        val sessao5 = findViewById<Button>(R.id.buttonSession5)

        val settings = findViewById<ImageButton>(R.id.settingsButton)

        voltar.setOnClickListener() {
            finish()
        }

        sessao1.setOnClickListener {
            MudarTelaSessao()
        }

        sessao2.setOnClickListener {
            MudarTelaSessao()
        }

        sessao3.setOnClickListener {
            MudarTelaSessao()

        }

        sessao4.setOnClickListener {
            MudarTelaSessao()
        }

        sessao5.setOnClickListener {
            MudarTelaSessao()
        }

        voltar.setOnClickListener() {
            finish()
        }

        settings.setOnClickListener(){
            SettingsTela()
        }
    }
    private fun MudarTelaSessao() {
        val telaAdicionar = Intent(this, ListarObrasAdminActivity::class.java)
        startActivity(telaAdicionar)
    }
    private fun SettingsTela() {
        val telaAdicionar = Intent(this, SairActivity::class.java)
        startActivity(telaAdicionar)
    }
    private fun VoltarParaEscanearObra() {
        val telaAdicionar = Intent(this, MainActivity::class.java)
        startActivity(telaAdicionar)
    }
    }

