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
    companion object {
        private const val TAG = "AnuncioRepo"
    }

    // Instância do Firestore
    private val db = FirebaseFirestore.getInstance()

    // Referência da coleção
    private val collection = db.collection("anuncios")

    // ---------- IMAGEM / BASE64 ----------

    /**
     * Converte uma imagem (Uri) para Base64, reduzindo o tamanho.
     */
    suspend fun converterImagemParaBase64(
        context: Context,
        uri: Uri,
        larguraMaxima: Int = 600,
        qualidadeJpeg: Int = 70,
    ): String? =
        withContext(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmapOriginal = BitmapFactory.decodeStream(inputStream)

                val bitmapReduzido = redimensionarBitmap(bitmapOriginal, larguraMaxima)

                val outputStream = ByteArrayOutputStream()
                bitmapReduzido.compress(Bitmap.CompressFormat.JPEG, qualidadeJpeg, outputStream)
                val bytes = outputStream.toByteArray()

                val base64 = Base64.encodeToString(bytes, Base64.DEFAULT)
                Log.d(TAG, "Imagem convertida. Tamanho Base64: ${base64.length} chars")

                "data:image/jpeg;base64,$base64"
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao converter imagem", e)
                null
            }
        }

    private fun redimensionarBitmap(
        bitmap: Bitmap,
        larguraMaxima: Int,
    ): Bitmap {
        val proporcao = bitmap.width.toFloat() / bitmap.height.toFloat()
        val largura = larguraMaxima
        val altura = (largura / proporcao).toInt()
        return Bitmap.createScaledBitmap(bitmap, largura, altura, true)
    }

    // ---------- LEITURA ----------

    /**
     * Alias em inglês – se já tiver código usando esse nome, continua funcionando.
     */
    suspend fun getAnuncios(): List<Anuncio> = buscarAnuncios()

    /**
     * Busca todos os anúncios.
     */
    suspend fun buscarAnuncios(): List<Anuncio> =
        try {
            val snapshot = collection.get().await()
            // Mapear documentos para garantir que o campo id seja preenchido com document.id
            snapshot.documents.mapNotNull { doc ->
                val a = doc.toObject(Anuncio::class.java)
                if (a == null) {
                    null
                } else if (a.id.isBlank()) {
                    a.copy(id = doc.id)
                } else {
                    a
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao buscar dados", e)
            emptyList()
        }

    suspend fun buscarAnuncioPorId(id: String): Anuncio? =
        try {
            val snapshot = collection.document(id).get().await()
            val a = snapshot.toObject(Anuncio::class.java)
            if (a == null) {
                null
            } else if (a.id.isBlank()) {
                a.copy(id = snapshot.id)
            } else {
                a
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao buscar anuncio", e)
            null
        }

    suspend fun buscarAnunciosPorVendedor(vendedorId: String): List<Anuncio> =
        try {
            val snapshot =
                collection
                    .whereEqualTo("vendedorId", vendedorId)
                    .get()
                    .await()

            snapshot.documents.mapNotNull { doc ->
                val a = doc.toObject(Anuncio::class.java)
                if (a == null) {
                    null
                } else if (a.id.isBlank()) {
                    a.copy(id = doc.id)
                } else {
                    a
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao buscar anuncio do vendedor", e)
            emptyList()
        }

    // ---------- ESCRITA (CREATE/UPDATE) ----------

    /**
     * Nome em inglês usado no primeiro arquivo.
     * Mantido por compatibilidade. Usa salvarAnuncio internamente.
     */
    suspend fun setAnuncio(anuncio: Anuncio): Boolean = salvarAnuncio(anuncio)

    /**
     * Salva/atualiza o anúncio. Se tiver id, dá set; se não tiver, dá add.
     */
    suspend fun salvarAnuncio(anuncio: Anuncio): Boolean =
        try {
            if (anuncio.id.isNotBlank()) {
                collection.document(anuncio.id).set(anuncio).await()
            } else {
                collection.add(anuncio).await()
            }
            Log.d(TAG, "Anúncio salvo com ${anuncio.fotos.size} foto(s)")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao salvar anuncio.", e)
            false
        }

    // ---------- DELETE ----------

    suspend fun deletarAnuncio(id: String): Boolean =
        try {
            collection.document(id).delete().await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao deletar anuncio.", e)
            false
        }
}
