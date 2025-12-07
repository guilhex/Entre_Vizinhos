package br.com.entrevizinhos.data.repository

import android.util.Log
import br.com.entrevizinhos.model.Anuncio
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AnuncioRepository {
    // 1. A Instância do Banco
    private val db = FirebaseFirestore.getInstance()

    // 2. A Referência da Coleção
    private val collection = db.collection("anuncios")

    // 3. busca com suspend
    suspend fun buscarAnuncios(): List<Anuncio> {
        return try {
            val snapshot = collection.get().await()

            val lista = snapshot.toObjects(Anuncio::class.java)
            return lista
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
                collection
                    .add(anuncio)
                    .await() // O .add() envia o objeto e gera um ID automático no servidor, .set() quando vc quer definir o ID
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

//    suspend fun atualizarAnuncio(anuncio: Anuncio): Boolean {
//        try {
//            collection.document(anuncio.id).set(anuncio).await()
//            return true
//        } catch (e: Exception) {
//            e.printStackTrace()
//            return false
//        }
//    }

    suspend fun buscarAnuncioPorId(id: String): Anuncio? {
        try {
            val snapshot = collection.document(id).get().await()
            return snapshot.toObject(Anuncio::class.java)
        } catch (e: Exception) {
            Log.e("AnuncioRepo", "Erro ao buscar anuncio", e)
            return null
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