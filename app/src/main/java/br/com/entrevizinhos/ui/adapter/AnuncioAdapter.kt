package br.com.entrevizinhos.ui.adapter

import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.entrevizinhos.databinding.ItemAnuncioBinding
import br.com.entrevizinhos.model.Anuncio
import com.bumptech.glide.Glide

class AnuncioAdapter(
    private var listaAnuncios: List<Anuncio>,
    private var favoritosIds: Set<String>,
    private val onAnuncioClick: (Anuncio) -> Unit,
    private val onFavoritoClick: (Anuncio) -> Unit,
    private val onEditarClick: ((Anuncio) -> Unit)? = null,
    private val onDeletarClick: ((Anuncio) -> Unit)? = null,
    private val mostrarBotoes: Boolean = false,
) : RecyclerView.Adapter<AnuncioAdapter.AnuncioViewHolder>() {

    inner class AnuncioViewHolder(
        val binding: ItemAnuncioBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            anuncio: Anuncio,
            isFavorito: Boolean,
        ) = with(binding) {
            tvTitulo.text = anuncio.titulo
            tvPreco.text = "R$ ${String.format("%.2f", anuncio.preco)}"
            tvLocalizacao.text = anuncio.cidade.ifEmpty { "Não informado" }
            tvCategoria.text = anuncio.categoria

            // --- LÓGICA DE IMAGEM ATUALIZADA ---
            if (anuncio.fotos.isNotEmpty()) {
                val fotoString = anuncio.fotos[0]

                if (fotoString.startsWith("data:image")) {
                    try {
                        val base64Clean = fotoString.substringAfter(",")
                        val decodedBytes = Base64.decode(base64Clean, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                        ivAnuncioFoto.setImageBitmap(bitmap)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        ivAnuncioFoto.setImageResource(android.R.drawable.ic_menu_report_image)
                    }
                } else {
                    Glide
                        .with(root.context)
                        .load(fotoString)
                        .into(ivAnuncioFoto)
                }
            } else {
                ivAnuncioFoto.setImageResource(android.R.drawable.ic_menu_gallery)
            }

            // Define o estado do coração
            if (isFavorito) {
                ivFavorito.setColorFilter(Color.RED)
            } else {
                ivFavorito.colorFilter = null
            }

            // Clique no item inteiro
            root.setOnClickListener {
                onAnuncioClick(anuncio)
            }

            // Clique no coração
            ivFavorito.setOnClickListener {
                onFavoritoClick(anuncio)
            }

            // ===== BOTÕES DE EDITAR/DELETAR =====
            if (mostrarBotoes) {
                containerBotoes.visibility = View.VISIBLE

                btnEditarAnuncio.setOnClickListener {
                    onEditarClick?.invoke(anuncio)
                }

                btnDeletarAnuncio.setOnClickListener {
                    onDeletarClick?.invoke(anuncio)
                }
            } else {
                containerBotoes.visibility = View.GONE
            }
        }
    }

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

    override fun onBindViewHolder(
        holder: AnuncioViewHolder,
        position: Int,
    ) {
        val anuncio = listaAnuncios[position]
        val isFavorito = favoritosIds.contains(anuncio.id)
        holder.bind(anuncio, isFavorito)
    }

    fun atualizarLista(
        novaLista: List<Anuncio>,
        novosFavoritos: Set<String>,
    ) {
        listaAnuncios = novaLista
        favoritosIds = novosFavoritos
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = listaAnuncios.size
}