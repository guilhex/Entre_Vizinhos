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

            val lista = snapshot.toObjects(Anuncio::class.java) // pega o json e transforma para ler em variaveis os dados
            Log.d("AnuncioRepo", "Sucesso! Encontrados: ${lista.size} anúncios")

            // Debug extra: Se veio vazio, avisa
            if (lista.isEmpty()) {
                Log.d("AnuncioRepo", "Aviso: A coleção 'anuncios' foi encontrada mas está vazia.")
            } else {
                // Se achou algo, mostra o título do primeiro para confirmar que os dados vieram certos
                Log.d("AnuncioRepo", "Primeiro item: ${lista[0].titulo}")
            }
            return lista
        } catch (e: Exception) {
            Log.e("AnuncioRepo", "Erro ao buscar dados", e)
            emptyList()
        }
    }

    suspend fun salvarAnuncio(anuncio: Anuncio): Boolean =
        try {
            // O .add() envia o objeto e gera um ID automático no servidor, .set() quando vc quer definir o ID
            collection.add(anuncio).await()
            true // Se deu certo
        } catch (e: Exception) {
            // Se a internet cair ou o servidor rejeitar
            e.printStackTrace()
            false // Se deu errado
        }

    suspend fun deletarAnuncio(id: String): Boolean {
        try {
            collection.document(id).delete().await()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    suspend fun atualizarAnuncio(anuncio: Anuncio): Boolean {
        try {
            collection.document(anuncio.id).set(anuncio).await()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    suspend fun buscarAnuncioPorId(id: String): Anuncio? {
        try {
            val snapshot = collection.document(id).get().await()
            return snapshot.toObject(Anuncio::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}
