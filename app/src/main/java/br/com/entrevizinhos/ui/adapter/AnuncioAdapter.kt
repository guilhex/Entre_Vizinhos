package br.com.entrevizinhos.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.entrevizinhos.databinding.ItemAnuncioBinding
import br.com.entrevizinhos.model.Anuncio

// Adaptador padrão conforme Aula 08
class AnuncioAdapter(
    private var listaAnuncios: List<Anuncio>,
    private val onAnuncioClick: (Anuncio) -> Unit, // Lambda para tratar o clique
) : RecyclerView.Adapter<AnuncioAdapter.AnuncioViewHolder>() {
    // ViewHolder usando ViewBinding (Aula 13)
    inner class AnuncioViewHolder(
        val binding: ItemAnuncioBinding,
    ) : RecyclerView.ViewHolder(binding.root)

    // Cria o visual do item (infla o layout)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): AnuncioViewHolder {
        val binding =
            ItemAnuncioBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        return AnuncioViewHolder(binding)
    }

    // Preenche os dados (bind)
    override fun onBindViewHolder(
        holder: AnuncioViewHolder,
        position: Int,
    ) {
        val anuncio = listaAnuncios[position]

        // Usando o 'apply' para acessar as views diretamente (Aula 01 - Kotlin Features)
        holder.binding.apply {
            tvTitulo.text = anuncio.titulo
            tvPreco.text = "R$ ${String.format("%.2f", anuncio.preco)}"

            // Configura o clique no item inteiro
            root.setOnClickListener {
                onAnuncioClick(anuncio)
            }
        }
    }

    fun atualizarLista(novaLista: List<Anuncio>) {
        listaAnuncios = novaLista
        notifyDataSetChanged()
    }

    // Retorna o tamanho da lista
    override fun getItemCount(): Int = listaAnuncios.size
}
