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

    suspend fun converterImagemParaBase64(context: Context, uri: Uri): String? {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmapOriginal = BitmapFactory.decodeStream(inputStream)

                val bitmapReduzido = redimensionarBitmap(bitmapOriginal, 600)

                val outputStream = ByteArrayOutputStream()
                bitmapReduzido.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
                val bytes = outputStream.toByteArray()

                val base64 = Base64.encodeToString(bytes, Base64.DEFAULT)
                Log.d("AnuncioRepo", "Imagem convertida. Tamanho Base64: ${base64.length} chars")
                "data:image/jpeg;base64,$base64"
            } catch (e: Exception) {
                Log.e("AnuncioRepo", "Erro ao converter imagem", e)
                null
            }
        }
    }

    private fun redimensionarBitmap(bitmap: Bitmap, larguraMaxima: Int): Bitmap {
        val razao = bitmap.width.toFloat() / bitmap.height.toFloat()
        val largura = larguraMaxima
        val altura = (largura / razao).toInt()
        return Bitmap.createScaledBitmap(bitmap, largura, altura, true)
    }

    suspend fun buscarAnuncios(): List<Anuncio> {
        return try {
            val snapshot = collection.get().await()
            snapshot.toObjects(Anuncio::class.java)
        } catch (e: Exception) {
            Log.e("AnuncioRepo", "Erro ao buscar dados", e)
            emptyList()
        }
    }

    suspend fun salvarAnuncio(anuncio: Anuncio): Boolean =
        try {
            if (anuncio.id.isNotBlank()) {
                collection.document(anuncio.id).set(anuncio).await()
            } else {
                collection.add(anuncio).await()
            }
            Log.d("AnuncioRepo", "Anúncio salvo com ${anuncio.fotos.size} foto(s)")
            true
        } catch (e: Exception) {
            Log.e("AnuncioRepo", "Erro ao salvar anuncio.", e)
            false
        }

    suspend fun deletarAnuncio(id: String): Boolean {
        return try {
            collection.document(id).delete().await()
            true
        } catch (e: Exception) {
            Log.e("AnuncioRepo", "Erro ao deletar anuncio.", e)
            false
        }
    }

    suspend fun buscarAnuncioPorId(id: String): Anuncio? {
        return try {
            val snapshot = collection.document(id).get().await()
            snapshot.toObject(Anuncio::class.java)
        } catch (e: Exception) {
            Log.e("AnuncioRepo", "Erro ao buscar anuncio", e)
            null
        }
    }

    suspend fun buscarAnunciosPorVendedor(vendedorId: String): List<Anuncio> {
        return try {
            val snapshot = collection.whereEqualTo("vendedorId", vendedorId).get().await()
            snapshot.toObjects(Anuncio::class.java)
        } catch (e: Exception) {
            Log.e("AnuncioRepo", "Erro ao buscar anuncio do vendedor", e)
            emptyList()
        }
    }
}