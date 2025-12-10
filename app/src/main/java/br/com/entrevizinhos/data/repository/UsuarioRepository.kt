package br.com.entrevizinhos.data.repository

import android.util.Log
import br.com.entrevizinhos.model.Usuario
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Repository responsável por operações de usuários no Firestore
 * Gerencia perfis e lista de favoritos
 */
class UsuarioRepository {
    private val db = FirebaseFirestore.getInstance() // Conexão com banco NoSQL
    private val collection = db.collection("usuarios") // Coleção específica de perfis

    // Busca perfil completo de qualquer usuário por ID
    suspend fun getUsuario(userId: String): Usuario? {
        try {
            val snapshot = collection.document(userId).get().await() // Consulta documento
            val usuario = snapshot.toObject(Usuario::class.java) // Deserializa objeto
            return usuario // Retorna perfil encontrado ou null
        } catch (e: Exception) {
            Log.e("UserRepo", "Erro ao buscar Usuario", e)
            return null // Falha na consulta
        }
    }

    // Gerencia lista de favoritos com operações atômicas
    suspend fun setFavorito(
        usuarioId: String, // ID do usuário que está favoritando
        anuncioId: String, // ID do anúncio a ser favoritado/desfavoritado
        adicionar: Boolean, // true = adicionar, false = remover
    ): Boolean {
        try {
            // Escolhe operação atômica baseada na ação desejada
            val operacao = if (adicionar) 
                FieldValue.arrayUnion(anuncioId) // Adiciona se não existir
            else 
                FieldValue.arrayRemove(anuncioId) // Remove se existir

            // Atualiza apenas o campo "favoritos" do documento
            collection.document(usuarioId).update("favoritos", operacao).await()
            return true // Operação bem-sucedida
        } catch (e: Exception) {
            Log.e("UserRepo", "Erro ao alterar favorito.", e)
            return false // Falha na operação
        }
    }
}
