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

/**
 * Adapter para exibir lista de anúncios no RecyclerView
 * Suporta modo normal (feed) e modo edição (perfil do vendedor)
 */
class AnuncioAdapter(
    private var listaAnuncios: List<Anuncio>, // Dados a serem exibidos
    private var favoritosIds: Set<String>, // Cache local de favoritos para UI rápida
    private val onAnuncioClick: (Anuncio) -> Unit, // Ação ao tocar no card
    private val onFavoritoClick: (Anuncio) -> Unit, // Ação ao tocar no coração
    private val onEditarClick: ((Anuncio) -> Unit)? = null, // Botão editar (só no perfil)
    private val onDeletarClick: ((Anuncio) -> Unit)? = null, // Botão excluir (só no perfil)
    private val mostrarBotoes: Boolean = false, // Ativa modo edição
) : RecyclerView.Adapter<AnuncioAdapter.AnuncioViewHolder>() {

    // ViewHolder reutilizável para cada item da lista
    inner class AnuncioViewHolder(
        val binding: ItemAnuncioBinding, // View Binding para acesso direto às views
    ) : RecyclerView.ViewHolder(binding.root) {
        
        // Popula views com dados do anúncio e estado de favorito
        fun bind(
            anuncio: Anuncio, // Dados do anúncio
            isFavorito: Boolean, // Se está na lista de favoritos
        ) = with(binding) {
            // Preenche textos principais do card
            tvTitulo.text = anuncio.titulo // Nome do produto
            tvPreco.text = "R$ ${String.format("%.2f", anuncio.preco)}" // Preço formatado
            tvLocalizacao.text = anuncio.cidade.ifEmpty { "Não informado" } // Cidade ou fallback
            tvCategoria.text = anuncio.categoria // Chip de categoria

            // Lógica de carregamento de imagem (suporta Base64 e URLs)
            if (anuncio.fotos.isNotEmpty()) {
                val fotoString = anuncio.fotos[0] // Sempre usa primeira foto como thumbnail

                // Detecta formato da imagem pelo prefixo
                if (fotoString.startsWith("data:image")) {
                    try {
                        // Processa imagem Base64 (armazenada no Firestore)
                        val base64Clean = fotoString.substringAfter(",") // Remove header
                        val decodedBytes = Base64.decode(base64Clean, Base64.DEFAULT) // Decodifica
                        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size) // Cria bitmap
                        ivAnuncioFoto.setImageBitmap(bitmap) // Exibe diretamente
                    } catch (e: Exception) {
                        e.printStackTrace()
                        ivAnuncioFoto.setImageResource(android.R.drawable.ic_menu_report_image) // Erro na decodificação
                    }
                } else {
                    // Processa URL externa (Firebase Storage, etc)
                    Glide
                        .with(root.context) // Contexto para Glide
                        .load(fotoString) // URL da imagem
                        .into(ivAnuncioFoto) // ImageView de destino
                }
            } else {
                // Placeholder quando não há fotos
                ivAnuncioFoto.setImageResource(android.R.drawable.ic_menu_gallery)
            }

            // Atualiza visual do ícone de favorito baseado no estado
            if (isFavorito) {
                ivFavorito.setColorFilter(Color.RED) // Coração vermelho = favoritado
            } else {
                ivFavorito.colorFilter = null // Coração normal = não favoritado
            }

            // Configura ações de toque
            root.setOnClickListener {
                onAnuncioClick(anuncio) // Toque no card = ver detalhes
            }

            ivFavorito.setOnClickListener {
                onFavoritoClick(anuncio) // Toque no coração = alternar favorito
            }

            // Controla visibilidade dos botões de edição
            if (mostrarBotoes) {
                containerBotoes.visibility = View.VISIBLE // Modo edição ativo

                btnEditarAnuncio.setOnClickListener {
                    onEditarClick?.invoke(anuncio) // Abre tela de edição
                }

                btnDeletarAnuncio.setOnClickListener {
                    onDeletarClick?.invoke(anuncio) // Confirma exclusão
                }
            } else {
                containerBotoes.visibility = View.GONE // Modo visualização normal
            }
        }
    }

    // Método obrigatório: cria ViewHolder quando RecyclerView precisa
    override fun onCreateViewHolder(
        parent: ViewGroup, // Container pai (RecyclerView)
        viewType: Int, // Tipo de item (não usado aqui)
    ): AnuncioViewHolder {
        val binding =
            ItemAnuncioBinding.inflate(
                LayoutInflater.from(parent.context), // Inflater do contexto
                parent, // ViewGroup pai
                false, // Não anexar imediatamente
            )
        return AnuncioViewHolder(binding) // Retorna ViewHolder configurado
    }

    // Método obrigatório: vincula dados a um ViewHolder existente
    override fun onBindViewHolder(
        holder: AnuncioViewHolder, // ViewHolder a ser populado
        position: Int, // Posição na lista
    ) {
        val anuncio = listaAnuncios[position] // Pega item da posição
        val isFavorito = favoritosIds.contains(anuncio.id) // Verifica se é favorito
        holder.bind(anuncio, isFavorito) // Popula ViewHolder
    }

    // Método público para atualizar dados e redesenhar lista
    fun atualizarLista(
        novaLista: List<Anuncio>, // Novos anúncios
        novosFavoritos: Set<String>, // Novos favoritos
    ) {
        listaAnuncios = novaLista // Atualiza dados
        favoritosIds = novosFavoritos // Atualiza favoritos
        notifyDataSetChanged() // Força redesenho completo
    }

    // Método obrigatório: informa quantos itens existem
    override fun getItemCount(): Int = listaAnuncios.size
}