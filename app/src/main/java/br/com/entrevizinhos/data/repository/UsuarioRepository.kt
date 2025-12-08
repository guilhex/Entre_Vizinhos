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

    suspend fun setFavorito(
        usuarioId: String,
        anuncioId: String,
    ): Boolean {
        try {
            val usuario = getUsuario(usuarioId) ?: return false

            // 2. Verificamos se o anúncio está na lista de favoritos
            val isFavoritoAtual = anuncioId in usuario.favoritos // atribui boolean

            // 3. Escolhemos a operação atômica a ser aplicada
            val operacao =
                if (isFavoritoAtual) {
                    // Se já é favorito, vamos remover (Unfavorite)
                    FieldValue.arrayRemove(anuncioId)
                } else {
                    // Se não é favorito, vamos adicionar (Favorite)
                    FieldValue.arrayUnion(anuncioId)
                }

            // 4. Aplicamos a atualização atômica no Firestore
            collection
                .document(usuarioId)
                .update("favoritos", operacao)
                .await() // salva no banco

            return true // Sucesso
        } catch (e: Exception) {
            Log.e("UserRepo", "Erro ao alterar favorito.", e)
            return false // Falha
        }
    }
}
