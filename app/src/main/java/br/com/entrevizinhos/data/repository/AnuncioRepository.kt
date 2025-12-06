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
            // O .add() envia o objeto e gera um ID automático no servidor, .set() quando vc quer definir o ID
            collection.add(anuncio).await()
            true // Se deu certo
        } catch (e: Exception) {
            // Se a internet cair ou o servidor rejeitar
            e.printStackTrace()
            false // Se deu errado
        }
    fun salvarAnuncio(anuncio: Anuncio, callback: (Boolean) -> Unit) {
        // Gera um ID novo automaticamente
        val novoDocumento = db.collection("anuncios").document()
        val anuncioComId = anuncio.copy(id = novoDocumento.id)

        novoDocumento.set(anuncioComId)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Erro ao salvar anuncio", e)
                callback(false)
            }
    }
}
