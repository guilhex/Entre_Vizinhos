package br.com.entrevizinhos.data.repository

import android.util.Log
import br.com.entrevizinhos.model.Usuario
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UsuarioRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("usuarios")

    suspend fun getUsuario(userId: String): Usuario? {
        try {
            val snapshot = collection.document(userId).get().await()

            val usuario = snapshot.toObject(Usuario::class.java)
            return usuario
        } catch (e: Exception) {
            Log.e("UserRepo", "Erro ao buscar Usuario", e)
            return null
        }
    }

    // Aplica operação atômica: se adicionar==true -> arrayUnion, se false -> arrayRemove
    suspend fun setFavorito(
        usuarioId: String,
        anuncioId: String,
        adicionar: Boolean,
    ): Boolean {
        try {
            val operacao = if (adicionar) FieldValue.arrayUnion(anuncioId) else FieldValue.arrayRemove(anuncioId)

            collection.document(usuarioId).update("favoritos", operacao).await()
            return true
        } catch (e: Exception) {
            Log.e("UserRepo", "Erro ao alterar favorito.", e)
            return false
        }
    }
}
