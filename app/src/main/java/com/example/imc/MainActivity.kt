package com.example.imc

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.imc.AppDatabase
import com.example.imc.R
import com.example.imc.model.Usuario
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var usuarios: List<Usuario>

    private lateinit var etNome: EditText
    private lateinit var etIdade: EditText
    private lateinit var etAltura: EditText
    private lateinit var etPeso: EditText
    private lateinit var rgSexo: RadioGroup
    private lateinit var btnCalcular: Button
    private lateinit var userContainer: LinearLayout
    private lateinit var resultadoIMC: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = AppDatabase.getInstance(this)

        etNome = findViewById(R.id.nome)
        etIdade = findViewById(R.id.idade)
        etAltura = findViewById(R.id.altura)
        etPeso = findViewById(R.id.peso)
        rgSexo = findViewById(R.id.sexo)
        btnCalcular = findViewById(R.id.btnEnviar)
        userContainer = findViewById(R.id.userContainer)
        resultadoIMC = findViewById(R.id.resultadoIMC)

        val btnLimpar: Button = findViewById(R.id.btnLimpar)
        btnLimpar.setOnClickListener {
            limparCampos() // Chama o método que limpa os campos e resultados
            resultadoIMC.visibility = View.GONE // Esconde o resultado, se você desejar
        }

        btnCalcular.setOnClickListener {
            adicionarUsuario()
        }

        atualizarListaUsuarios()
    }

    private fun adicionarUsuario() {
        val nome = etNome.text.toString()
        val idade = etIdade.text.toString().toLongOrNull()
        val altura = etAltura.text.toString().toDoubleOrNull()
        val peso = etPeso.text.toString().toDoubleOrNull()
        val sexo = if (rgSexo.checkedRadioButtonId == R.id.masculino) "Masculino" else "Feminino"

        if (nome.isNotEmpty() && idade != null && altura != null && peso != null) {
            val usuario = Usuario(nome = nome, idade = idade, altura = altura, peso = peso, sexo = sexo)

            // Calcula o IMC e a classificação
            val (imc, classificacao) = calcularIMC(peso, altura, idade)
            usuario.imc = imc
            usuario.classificacao = classificacao // Adiciona a classificação ao usuário

            lifecycleScope.launch {
                db.usuarioDao().insert(usuario)
                Toast.makeText(this@MainActivity, "Usuário adicionado com sucesso!", Toast.LENGTH_SHORT).show()
                atualizarListaUsuarios()
                // limparCampos()
            }

            val resultadoTexto = "Seu IMC é: %.2f\nClassificação: $classificacao".format(imc)
            resultadoIMC.text = resultadoTexto
            resultadoIMC.visibility = View.VISIBLE
        } else {
            Toast.makeText(this, "Preencha todos os campos corretamente", Toast.LENGTH_SHORT).show()
        }
    }

    private fun calcularIMC(peso: Double, altura: Double, idade: Long): Pair<Double, String> {
        val imc = peso / (altura * altura)
        val classificacao = if (idade < 18) {
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
        return Pair(imc, classificacao)
    }

    private fun atualizarListaUsuarios() {
        lifecycleScope.launch {
            usuarios = db.usuarioDao().getAll()
            exibirUsuarios()
        }
    }

    private fun exibirUsuarios() {
        userContainer.removeAllViews() // Limpa a visualização anterior

        if (usuarios.isNotEmpty()) {
            userContainer.visibility = View.VISIBLE // Mostra o contêiner se houver usuários
            for (usuario in usuarios) {
                // Infla o layout `activity_item_usuario.xml`
                val itemView = layoutInflater.inflate(R.layout.activity_item_usuario, userContainer, false)

                // Acessa o TextView e os botões de cada item
                val userName = itemView.findViewById<TextView>(R.id.userName)
                val editButton = itemView.findViewById<Button>(R.id.editButton)
                val deleteButton = itemView.findViewById<Button>(R.id.deleteButton)

                // Define o nome do usuário e exibe as informações
                userName.text = "${usuario.nome}, ${usuario.idade} anos, Altura: ${usuario.altura}, Peso: ${usuario.peso}, IMC: %.2f, Classificação: ${usuario.classificacao}".format(usuario.imc)

                // Define o comportamento do botão de editar
                editButton.setOnClickListener {
                    editarUsuario(usuario)
                }

                // Define o comportamento do botão de deletar
                deleteButton.setOnClickListener {
                    deletarUsuario(usuario)
                }

                // Adiciona a visualização do item ao container
                userContainer.addView(itemView)
            }
        } else {
            userContainer.visibility = View.GONE // Esconde o contêiner se não houver usuários
        }
    }


    private fun editarUsuario(usuario: Usuario) {
        // Preenche os campos com os dados do usuário
        etNome.setText(usuario.nome)
        etIdade.setText(usuario.idade.toString())
        etAltura.setText(usuario.altura.toString())
        etPeso.setText(usuario.peso.toString())
        rgSexo.check(if (usuario.sexo == "Masculino") R.id.masculino else R.id.feminino)

        btnCalcular.setOnClickListener {
            val nome = etNome.text.toString()
            val idade = etIdade.text.toString().toLongOrNull()
            val altura = etAltura.text.toString().toDoubleOrNull()
            val peso = etPeso.text.toString().toDoubleOrNull()
            val sexo = if (rgSexo.checkedRadioButtonId == R.id.masculino) "Masculino" else "Feminino"

            if (nome.isNotEmpty() && idade != null && altura != null && peso != null) {
                usuario.nome = nome
                usuario.idade = idade
                usuario.altura = altura
                usuario.peso = peso
                usuario.sexo = sexo

                // Recalcula o IMC e a classificação
                val (imc, classificacao) = calcularIMC(peso, altura, idade)
                usuario.imc = imc
                usuario.classificacao = classificacao // Atualiza a classificação

                lifecycleScope.launch {
                    db.usuarioDao().update(usuario)
                    Toast.makeText(this@MainActivity, "Usuário atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                    atualizarListaUsuarios()
                    limparCampos()
                }
            } else {
                Toast.makeText(this, "Preencha todos os campos corretamente", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deletarUsuario(usuario: Usuario) {
        lifecycleScope.launch {
            db.usuarioDao().delete(usuario)
            Toast.makeText(this@MainActivity, "Usuário deletado com sucesso!", Toast.LENGTH_SHORT).show()
            atualizarListaUsuarios()
            limparCampos()
        }
    }

    private fun limparCampos() {
        etNome.text.clear()
        etIdade.text.clear()
        etAltura.text.clear()
        etPeso.text.clear()
        rgSexo.clearCheck()
        // resultadoIMC.visibility = View.GONE
    }
}
