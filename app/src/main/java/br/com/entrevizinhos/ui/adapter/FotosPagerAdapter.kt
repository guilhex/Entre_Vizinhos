package br.com.entrevizinhos.ui.adapter

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.entrevizinhos.databinding.ItemFotoPagerBinding
import com.bumptech.glide.Glide

/**
 * Adapter para exibir fotos em um ViewPager2 (carrossel de imagens)
 * Usado na tela de detalhes do anúncio para navegar entre fotos
 */
class FotosPagerAdapter(
    private val fotos: List<String>, // Coleção de imagens (Base64 ou URLs)
) : RecyclerView.Adapter<FotosPagerAdapter.FotoVH>() {

    // ViewHolder simples para cada página do carrossel
    inner class FotoVH(val binding: ItemFotoPagerBinding) : RecyclerView.ViewHolder(binding.root)

    // Cria ViewHolder para cada página do ViewPager2
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FotoVH {
        val binding = ItemFotoPagerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FotoVH(binding) // ViewHolder com layout da foto
    }

    // Popula cada página com a foto correspondente
    override fun onBindViewHolder(holder: FotoVH, position: Int) {
        val foto = fotos[position] // Foto da posição atual
        val iv = holder.binding.ivFotoPager // ImageView da página

        // Validação de segurança para fotos inválidas
        if (foto.isNullOrBlank()) {
            iv.setImageResource(android.R.drawable.ic_menu_gallery) // Placeholder padrão
            return // Sai da função
        }

        // Lógica de carregamento baseada no formato
        if (foto.startsWith("data:image")) {
            try {
                // Processamento de imagem Base64 (offline)
                val base64Clean = foto.substringAfter(",") // Remove prefixo data:image
                val decoded = Base64.decode(base64Clean, Base64.DEFAULT) // Decodifica string
                val bmp = BitmapFactory.decodeByteArray(decoded, 0, decoded.size) // Cria bitmap
                iv.setImageBitmap(bmp) // Exibe imagem decodificada
            } catch (_: Exception) {
                iv.setImageResource(android.R.drawable.ic_menu_report_image) // Erro na decodificação
            }
        } else {
            // Processamento de URL externa (online)
            Glide.with(iv.context) // Biblioteca de carregamento de imagens
                .load(foto) // URL da imagem
                .placeholder(android.R.drawable.ic_menu_gallery) // Enquanto carrega
                .error(android.R.drawable.ic_menu_report_image) // Se falhar
                .into(iv) // ImageView de destino
        }
    }

    // Informa quantas páginas o ViewPager2 deve criar
    override fun getItemCount() = fotos.size
}