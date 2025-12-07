package br.com.entrevizinhos.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import br.com.entrevizinhos.model.Anuncio
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class AnuncioRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("anuncios")

    // --- MUDANÇA: Não usamos mais o Storage, usamos conversão local ---

    suspend fun converterImagemParaBase64(context: Context, uri: Uri): String? {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Abre a imagem original
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmapOriginal = BitmapFactory.decodeStream(inputStream)

                // 2. Redimensiona para ficar leve (Max 600px de largura)
                val bitmapReduzido = redimensionarBitmap(bitmapOriginal, 600)

                // 3. Comprime para JPEG e converte para ByteArray
                val outputStream = ByteArrayOutputStream()
                // Qualidade 70 é um bom equilíbrio entre tamanho e visualização
                bitmapReduzido.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
                val bytes = outputStream.toByteArray()

                // 4. Converte os bytes para String Base64
                // O prefixo é necessário para o Glide entender que é uma imagem
                "data:image/jpeg;base64," + Base64.encodeToString(bytes, Base64.DEFAULT)
            } catch (e: Exception) {
                Log.e("AnuncioRepo", "Erro ao converter imagem", e)
                null
            }
        }
    }

    // Função auxiliar para diminuir o tamanho da imagem
    private fun redimensionarBitmap(bitmap: Bitmap, larguraMaxima: Int): Bitmap {
        val razao = bitmap.width.toFloat() / bitmap.height.toFloat()
        val largura = larguraMaxima
        val altura = (largura / razao).toInt()
        return Bitmap.createScaledBitmap(bitmap, largura, altura, true)
    }

    suspend fun salvarAnuncio(anuncio: Anuncio): Boolean =
        try {
            if (anuncio.id.isNotBlank()) {
                collection.document(anuncio.id).set(anuncio).await()
            } else {
                collection.add(anuncio).await()
            }
            true // Se deu certo
        } catch (e: Exception) {
            // Se a internet cair ou o servidor rejeitar
            Log.e("AnuncioRepo", "Erro ao salvar anuncio.", e)
            false // Se deu errado
        }

    suspend fun deletarAnuncio(id: String): Boolean {
        try {
            collection.document(id).delete().await()
            return true
        } catch (e: Exception) {
            Log.e("AnuncioRepo", "Erro ao deletar anuncio.", e)
            return false
        }
    }

    // ... manter as outras funções (buscarAnuncios, deletar, etc) iguais ...
    suspend fun buscarAnuncios(): List<Anuncio> {
        return try {
            val snapshot = collection.get().await()
            snapshot.toObjects(Anuncio::class.java)
        } catch (e: Exception) {
            Log.e("AnuncioRepo", "Erro ao buscar anuncio", e)
            return emptyList()
        }
    }

    suspend fun buscarAnunciosPorVendedor(vendedorId: String): List<Anuncio> {
        try {
            val snapshot = collection.whereEqualTo("vendedorId", vendedorId).get().await()

            val lista = snapshot.toObjects(Anuncio::class.java)
            return lista
        } catch (e: Exception) {
            Log.e("AnuncioRepo", "Erro ao buscar anuncio do vendedor", e)
            return emptyList()
        }
    }
}
