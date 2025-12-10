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

/**
 * Repository responsável por gerenciar operações de anúncios no Firestore
 * Inclui CRUD completo e processamento de imagens
 */
class AnuncioRepository {
    companion object {
        private const val TAG = "AnuncioRepo" // Tag para logs de debug
    }

    private val db = FirebaseFirestore.getInstance() // Conexão com banco NoSQL
    private val collection = db.collection("anuncios") // Coleção específica de anúncios

    /**
     * Converte imagem URI para Base64 com compressão para economizar espaço
     * Reduz qualidade e tamanho antes de armazenar no Firestore
     */
    suspend fun converterImagemParaBase64(
        context: Context, // Contexto para acessar ContentResolver
        uri: Uri, // URI da imagem selecionada pelo usuário
        larguraMaxima: Int = 600, // Limite de pixels para redimensionar
        qualidadeJpeg: Int = 70, // Compressão de 0-100 (70 = boa qualidade)
    ): String? =
        withContext(Dispatchers.IO) { // Thread de I/O para não travar UI
            try {
                // Abre stream da imagem do dispositivo
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmapOriginal = BitmapFactory.decodeStream(inputStream)

                // Reduz tamanho mantendo proporção original
                val bitmapReduzido = redimensionarBitmap(bitmapOriginal, larguraMaxima)

                // Converte bitmap para array de bytes comprimido
                val outputStream = ByteArrayOutputStream()
                bitmapReduzido.compress(Bitmap.CompressFormat.JPEG, qualidadeJpeg, outputStream)
                val bytes = outputStream.toByteArray()

                // Transforma bytes em string Base64 para Firestore
                val base64 = Base64.encodeToString(bytes, Base64.DEFAULT)
                Log.d(TAG, "Imagem convertida. Tamanho Base64: ${base64.length} chars")

                "data:image/jpeg;base64,$base64" // Formato padrão data URL
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao converter imagem", e)
                null // Retorna null em caso de falha
            }
        }

    // Redimensiona bitmap mantendo proporção original
    private fun redimensionarBitmap(
        bitmap: Bitmap, // Imagem original
        larguraMaxima: Int, // Novo tamanho máximo
    ): Bitmap {
        val proporcao = bitmap.width.toFloat() / bitmap.height.toFloat() // Calcula ratio
        val largura = larguraMaxima // Define nova largura
        val altura = (largura / proporcao).toInt() // Calcula altura proporcional
        return Bitmap.createScaledBitmap(bitmap, largura, altura, true) // Cria bitmap redimensionado
    }

    // Método alias para manter compatibilidade com código existente
    suspend fun getAnuncios(): List<Anuncio> = buscarAnuncios()

    // Busca todos os anúncios disponíveis na plataforma
    suspend fun buscarAnuncios(): List<Anuncio> =
        try {
            val snapshot = collection.get().await() // Busca todos documentos da coleção
            // Converte documentos Firestore para objetos Kotlin
            snapshot.documents.mapNotNull { doc ->
                val a = doc.toObject(Anuncio::class.java) // Deserializa documento
                if (a == null) {
                    null // Ignora documentos inválidos
                } else if (a.id.isBlank()) {
                    a.copy(id = doc.id) // Preenche ID se estiver vazio
                } else {
                    a // Retorna objeto completo
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao buscar dados", e)
            emptyList() // Retorna lista vazia em caso de erro
        }

    // Busca anúncio específico por ID (para tela de detalhes)
    suspend fun buscarAnuncioPorId(id: String): Anuncio? =
        try {
            val snapshot = collection.document(id).get().await() // Busca documento único
            val a = snapshot.toObject(Anuncio::class.java) // Converte para objeto
            if (a == null) {
                null // Documento não existe ou é inválido
            } else if (a.id.isBlank()) {
                a.copy(id = snapshot.id) // Garante que ID está preenchido
            } else {
                a // Retorna anúncio encontrado
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao buscar anuncio", e)
            null // Retorna null se não encontrar
        }

    // Busca todos os anúncios de um vendedor (para tela de perfil)
    suspend fun buscarAnunciosPorVendedor(vendedorId: String): List<Anuncio> =
        try {
            val snapshot =
                collection
                    .whereEqualTo("vendedorId", vendedorId) // Filtra por proprietário
                    .get()
                    .await()

            // Processa resultados da consulta filtrada
            snapshot.documents.mapNotNull { doc ->
                val a = doc.toObject(Anuncio::class.java)
                if (a == null) {
                    null // Pula documentos corrompidos
                } else if (a.id.isBlank()) {
                    a.copy(id = doc.id) // Corrige ID ausente
                } else {
                    a // Anúncio válido do vendedor
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao buscar anuncio do vendedor", e)
            emptyList() // Lista vazia se falhar
        }

    // Método alias para manter compatibilidade com código legado
    suspend fun setAnuncio(anuncio: Anuncio): Boolean = salvarAnuncio(anuncio)

    // Salva novo anúncio ou atualiza existente (operação upsert)
    suspend fun salvarAnuncio(anuncio: Anuncio): Boolean =
        try {
            // Lógica de upsert: ID vazio = criar, ID existente = atualizar
            if (anuncio.id.isNotBlank()) {
                collection.document(anuncio.id).set(anuncio).await() // Substitui documento
            } else {
                collection.add(anuncio).await() // Cria novo com ID automático
            }
            Log.d(TAG, "Anúncio salvo com ${anuncio.fotos.size} foto(s)") // Log de sucesso
            true // Operação bem-sucedida
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao salvar anuncio.", e)
            false // Falha na operação
        }

    // Atualiza campos específicos sem substituir documento inteiro
    suspend fun atualizarAnuncio(
        id: String, // ID do documento a ser atualizado
        dados: Map<String, Any>, // Mapa com campos e novos valores
    ): Boolean =
        try {
            collection.document(id).update(dados).await() // Operação atômica de update
            Log.d(TAG, "Anúncio $id atualizado com sucesso")
            true // Confirma sucesso da operação
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao atualizar anúncio: $id", e)
            false // Indica falha na atualização
        }

    // Remove anúncio permanentemente do banco de dados
    suspend fun deletarAnuncio(id: String): Boolean =
        try {
            collection.document(id).delete().await() // Exclusão definitiva
            Log.d(TAG, "Anúncio $id deletado com sucesso")
            true // Confirma exclusão
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao deletar anúncio: $id", e)
            false // Falha na exclusão
        }
}
