package br.com.entrevizinhos.ui.adapter

import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.entrevizinhos.databinding.ItemAnuncioBinding
import br.com.entrevizinhos.model.Anuncio
import com.bumptech.glide.Glide

// Adaptador padrão conforme Aula 08 + reatividade de favoritos (MVVM)
class AnuncioAdapter(
    private var listaAnuncios: List<Anuncio>,
    private var favoritosIds: Set<String>, // <-- estado vindo do ViewModel
    private val onAnuncioClick: (Anuncio) -> Unit, // clique no card
    private val onFavoritoClick: (Anuncio) -> Unit, // clique no coração
) : RecyclerView.Adapter<AnuncioAdapter.AnuncioViewHolder>() {
    // ViewHolder usando ViewBinding (Aula 13)
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
                val fotoString = anuncio.fotos[0] // Pega a primeira foto da lista

                if (fotoString.startsWith("data:image")) {
                    // CASO 1: Imagem salva como Texto (Base64) - Estratégia sem Storage
                    try {
                        // Limpa o prefixo se necessário (ex: "data:image/jpeg;base64,")
                        val base64Clean = fotoString.substringAfter(",")

                        // Converte o texto para bytes
                        val decodedBytes = Base64.decode(base64Clean, Base64.DEFAULT)

                        // Converte bytes para Bitmap (Imagem)
                        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

                        // Define na tela
                        ivAnuncioFoto.setImageBitmap(bitmap)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        // Se der erro na conversão, mostra ícone de erro
                        ivAnuncioFoto.setImageResource(android.R.drawable.ic_menu_report_image)
                    }
                } else {
                    // CASO 2: É um Link/URL (Se você ativar o Storage no futuro)
                    Glide
                        .with(root.context)
                        .load(fotoString)
                        .into(ivAnuncioFoto)
                }
            } else {
                // CASO 3: Nenhuma foto salva
                ivAnuncioFoto.setImageResource(android.R.drawable.ic_menu_gallery)
            }
            // ------------------------------------

            // Define o estado do coração de forma determinística
            if (isFavorito) {
                ivFavorito.setColorFilter(Color.RED)
            } else {
                ivFavorito.colorFilter = null
            }

            // 2) Clique no item inteiro -> navegação para detalhes
            root.setOnClickListener {
                onAnuncioClick(anuncio)
            }

            // 3) Clique no coração -> delega para o ViewModel (via Fragment)
            ivFavorito.setOnClickListener {
                onFavoritoClick(anuncio)
            }
        }
    }

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
        val isFavorito = favoritosIds.contains(anuncio.id)
        holder.bind(anuncio, isFavorito)
    }

    // Atualiza lista + estado de favoritos de forma reativa
    fun atualizarLista(
        novaLista: List<Anuncio>,
        novosFavoritos: Set<String>,
    ) {
        listaAnuncios = novaLista
        favoritosIds = novosFavoritos
        notifyDataSetChanged()
    }

    // Retorna o tamanho da lista
    override fun getItemCount(): Int = listaAnuncios.size
}
