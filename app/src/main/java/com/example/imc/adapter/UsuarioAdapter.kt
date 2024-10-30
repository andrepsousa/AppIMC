package com.example.imc

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.imc.model.Usuario

class UsuarioAdapter(private var usuarios: List<Usuario>, private val onUsuarioEditado: (Usuario) -> Unit) : RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_usuario, parent, false)
        return UsuarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        val usuario = usuarios[position]
        holder.txtNome.text = usuario.nome
        holder.txtPeso.text = "Peso: ${usuario.peso} kg"
        holder.txtAltura.text = "Altura: ${usuario.altura} m"
        holder.txtIMC.text = "IMC: ${usuario.calcularIMC()}"
        holder.txtCategoria.text = "Categoria: ${usuario.categoriaIMC()}"

        holder.btnEditar.setOnClickListener {
            // Exemplo de edição (abre um diálogo para editar o usuário)
            val context = holder.itemView.context
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_editar_usuario, null)

            val nomeEditText = dialogView.findViewById<EditText>(R.id.editNome)
            val pesoEditText = dialogView.findViewById<EditText>(R.id.editPeso)
            val alturaEditText = dialogView.findViewById<EditText>(R.id.editAltura)

            // Preencher os campos do diálogo com os dados atuais do usuário
            nomeEditText.setText(usuario.nome)
            pesoEditText.setText(usuario.peso.toString())
            alturaEditText.setText(usuario.altura.toString())

            AlertDialog.Builder(context)
                .setTitle("Editar Usuário")
                .setView(dialogView)
                .setPositiveButton("Salvar") { _, _ ->
                    // Atualizando o objeto usuário com os novos dados
                    usuario.nome = nomeEditText.text.toString()
                    usuario.peso = pesoEditText.text.toString().toFloatOrNull() ?: usuario.peso
                    usuario.altura = alturaEditText.text.toString().toFloatOrNull() ?: usuario.altura

                    notifyItemChanged(position) // Atualizar a exibição do item alterado
                    onUsuarioEditado(usuario) // Callback para atualizar o banco de dados
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        // Lógica para deletar o usuário
        holder.btnDeletar.setOnClickListener {
            usuarios.toMutableList().removeAt(position) // Remove o usuário da lista
            notifyItemRemoved(position) // Notifica o adapter para atualizar a lista
            notifyItemRangeChanged(position, usuarios.size) // Atualiza a posição dos itens restantes
        }
    }

    override fun getItemCount(): Int = usuarios.size

    fun setUsuarios(usuarios: List<Usuario>) {
        this.usuarios = usuarios
        notifyDataSetChanged()
    }

    inner class UsuarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNome: TextView = itemView.findViewById(R.id.txtNome)
        val txtPeso: TextView = itemView.findViewById(R.id.txtPeso)
        val txtAltura: TextView = itemView.findViewById(R.id.txtAltura)
        val txtIMC: TextView = itemView.findViewById(R.id.txtIMC)
        val txtCategoria: TextView = itemView.findViewById(R.id.txtCategoria)
        val btnEditar: Button = itemView.findViewById(R.id.btnEditar)
        val btnDeletar: Button = itemView.findViewById(R.id.btnDeletar)
    }
}
