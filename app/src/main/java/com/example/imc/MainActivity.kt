package com.example.imc

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.imc.model.Usuario
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var etNome: EditText
    private lateinit var etIdade: EditText
    private lateinit var etAltura: EditText
    private lateinit var etPeso: EditText
    private lateinit var rgSexo: RadioGroup
    private lateinit var tvResultadoIMC: TextView
    private lateinit var btnCalcular: Button
    private lateinit var btnLimpar: Button
    private lateinit var btnAdicionar: Button // Botão para adicionar usuário

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = AppDatabase.getInstance(this)

        etNome = findViewById(R.id.nome)
        etIdade = findViewById(R.id.idade)
        etAltura = findViewById(R.id.altura)
        etPeso = findViewById(R.id.peso)
        rgSexo = findViewById(R.id.sexo)
        tvResultadoIMC = findViewById(R.id.resultadoIMC)
        btnCalcular = findViewById(R.id.btnEnviar)
        btnLimpar = findViewById(R.id.btnLimpar)
        btnAdicionar = findViewById(R.id.btnAdicionar) // Inicializa o botão de adicionar

        btnCalcular.setOnClickListener {
            calcularIMC()
        }

        btnLimpar.setOnClickListener {
            limparCampos()
        }

        btnAdicionar.setOnClickListener {
            adicionarUsuario() // Chama a função para adicionar usuário
        }
    }

    private fun calcularIMC() {
        val idadeDigitada = etIdade.text.toString().toIntOrNull()
        val alturaDigitada = etAltura.text.toString().toDoubleOrNull()
        val pesoDigitado = etPeso.text.toString().toDoubleOrNull()

        if (idadeDigitada == null || alturaDigitada == null || pesoDigitado == null) {
            tvResultadoIMC.text = "Preencha todos os campos corretamente"
            return
        }

        val sexoSelecionado = when (rgSexo.checkedRadioButtonId) {
            R.id.masculino -> "Masculino"
            R.id.feminino -> "Feminino"
            else -> "Não especificado"
        }

        if (alturaDigitada > 3) {
            tvResultadoIMC.text = "Digite a altura em metros, por exemplo: 1.73"
        } else {
            val imc = pesoDigitado / (alturaDigitada * alturaDigitada)
            val classificacao = if (idadeDigitada < 18) {
                when {
                    imc < 14 -> "Abaixo do peso"
                    imc in 14.0..18.4 -> "Peso normal"
                    imc in 18.5..22.9 -> "Sobrepeso"
                    imc in 23.0..29.9 -> "Obesidade"
                    else -> "Obesidade grave"
                }
            } else {
                when {
                    imc < 18.5 -> "Abaixo do peso"
                    imc in 18.5..24.9 -> "Peso normal"
                    imc in 25.0..29.9 -> "Sobrepeso"
                    imc in 30.0..39.9 -> "Obesidade"
                    else -> "Obesidade grave"
                }
            }

            tvResultadoIMC.text = "IMC: %.2f - %s\nIdade: %d anos\nSexo: %s".format(imc, classificacao, idadeDigitada, sexoSelecionado)
        }
    }

    private fun adicionarUsuario() {
        val nome = etNome.text.toString()
        val idade = etIdade.text.toString().toIntOrNull()
        val altura = etAltura.text.toString().toDoubleOrNull()
        val peso = etPeso.text.toString().toDoubleOrNull()
        val sexo = if (rgSexo.checkedRadioButtonId == R.id.masculino) "Masculino" else "Feminino"

        if (nome.isEmpty() || idade == null || altura == null || peso == null) {
            Toast.makeText(this, "Preencha todos os campos corretamente", Toast.LENGTH_SHORT).show()
            return
        }

        val usuario = Usuario(nome, idade, altura, peso, sexo)
        lifecycleScope.launch {
            db.usuarioDao().insert(usuario)
            limparCampos() // Limpa os campos após a adição
            // Adicione aqui uma chamada para atualizar a lista de usuários, se necessário
        }
    }

    private fun limparCampos() {
        etNome.text.clear()
        etIdade.text.clear()
        etAltura.text.clear()
        etPeso.text.clear()
        rgSexo.clearCheck()
        tvResultadoIMC.text = ""
    }
}
