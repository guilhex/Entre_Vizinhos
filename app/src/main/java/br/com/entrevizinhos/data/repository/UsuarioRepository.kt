package br.com.entrevizinhos.data.repository

import android.util.Log
import br.com.entrevizinhos.model.Usuario
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
}
