package br.com.entrevizinhos.ui.adapter

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.entrevizinhos.databinding.ItemFotoPagerBinding
import com.bumptech.glide.Glide

class FotosPagerAdapter(
    private val fotos: List<String>,
) : RecyclerView.Adapter<FotosPagerAdapter.FotoVH>() {

    inner class FotoVH(val binding: ItemFotoPagerBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FotoVH {
        val binding = ItemFotoPagerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FotoVH(binding)
    }

    override fun onBindViewHolder(holder: FotoVH, position: Int) {
        val foto = fotos[position]
        val iv = holder.binding.ivFotoPager

        if (foto.isNullOrBlank()) {
            iv.setImageResource(android.R.drawable.ic_menu_gallery)
            return
        }

        if (foto.startsWith("data:image")) {
            try {
                val base64Clean = foto.substringAfter(",")
                val decoded = Base64.decode(base64Clean, Base64.DEFAULT)
                val bmp = BitmapFactory.decodeByteArray(decoded, 0, decoded.size)
                iv.setImageBitmap(bmp)
            } catch (_: Exception) {
                iv.setImageResource(android.R.drawable.ic_menu_report_image)
            }
        } else {
            Glide.with(iv.context)
                .load(foto)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .into(iv)
        }
    }

    override fun getItemCount() = fotos.size
}