package br.com.entrevizinhos.data.repository

import android.util.Log
import br.com.entrevizinhos.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // Retorna o usuário logado (Auth) ou null
    fun getCurrentUser() = auth.currentUser

    // Deslogar
    fun signOut() {
        auth.signOut()
    }

    // Busca dados detalhados no Firestore (Nome, Telefone, Foto)
    fun carregarDadosUsuario(callback: (Usuario?) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            callback(null)
            return
        }

        db.collection("usuarios").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val usuario = document.toObject(Usuario::class.java)
                    callback(usuario)
                } else {
                    // Se não tiver dados no banco ainda, retorna vazio mas com o email do Auth
                    callback(Usuario(id = userId, email = auth.currentUser?.email ?: ""))
                }
            }
            .addOnFailureListener { e ->
                Log.e("AuthRepo", "Erro ao buscar perfil", e)
                callback(null)
            }
    }

    // Salvar/Atualizar dados do perfil
    fun salvarPerfil(usuario: Usuario, callback: (Boolean) -> Unit) {
        val userId = auth.currentUser?.uid ?: return

        // Garante que o ID está certo
        val dadosParaSalvar = usuario.copy(id = userId)

        db.collection("usuarios").document(userId)
            .set(dadosParaSalvar)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }
}