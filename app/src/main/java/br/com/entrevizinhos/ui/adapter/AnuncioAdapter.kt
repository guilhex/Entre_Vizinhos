package br.com.entrevizinhos.ui.adapter

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.entrevizinhos.databinding.ItemAnuncioBinding
import br.com.entrevizinhos.model.Anuncio
import com.bumptech.glide.Glide

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

        // Usando o 'apply' para acessar as views diretamente
        holder.binding.apply {
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

            // Configura o clique no item inteiro
            root.setOnClickListener {
                onAnuncioClick(anuncio)
            }

            // clique no coração
            ivFavorito.setOnClickListener {
                // finalizar logica
                // Por enquanto, pode apenas avisar ou mudar a cor
                if (ivFavorito.colorFilter == null) {
                    ivFavorito.setColorFilter(android.graphics.Color.RED)
                } else {
                    ivFavorito.setColorFilter(null)
                }
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
