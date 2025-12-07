package br.com.entrevizinhos.data.repository

import android.util.Log
import br.com.entrevizinhos.model.Usuario
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun getCurrentUser() = auth.currentUser

    fun signOut() {
        auth.signOut()
    }

    // Busca dados do usuário
    fun carregarDadosUsuario(callback: (Usuario?) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            callback(null)
            return
        }

        db
            .collection("usuarios")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val usuario = document.toObject(Usuario::class.java)
                    callback(usuario)
                } else {
                    // Usuário sem dados no banco, retorna dados do Google
                    val email = auth.currentUser?.email ?: ""
                    val nome = auth.currentUser?.displayName ?: "Vizinho"
                    val foto = auth.currentUser?.photoUrl?.toString() ?: ""
                    callback(Usuario(id = userId, email = email, nome = nome, fotoUrl = foto))
                }
            }.addOnFailureListener {
                // Se der erro (ex: sem internet), libera o acesso com dados básicos
                val email = auth.currentUser?.email ?: ""
                callback(Usuario(id = userId, email = email))
            }
    }

    fun salvarPerfil(
        usuario: Usuario,
        callback: (Boolean) -> Unit,
    ) {
        val userId = auth.currentUser?.uid ?: return
        db
            .collection("usuarios")
            .document(userId)
            .set(usuario)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    // --- CORREÇÃO DO LOGIN TRAVADO ---
    fun loginComGoogle(
        credential: AuthCredential,
        callback: (Boolean) -> Unit,
    ) {
        Log.d("AuthRepo", "Iniciando login com credencial...")
        auth
            .signInWithCredential(credential)
            .addOnSuccessListener { result ->
                val user = result.user
                if (user != null) {
                    val userId = user.uid
                    Log.d("AuthRepo", "Login Auth OK! ID: $userId")

                    // Tenta salvar no banco
                    val userDoc = db.collection("usuarios").document(userId)

                    userDoc
                        .get()
                        .addOnSuccessListener { document ->
                            if (!document.exists()) {
                                // Cria usuário novo
                                val novoUsuario =
                                    Usuario(
                                        id = userId,
                                        nome = user.displayName ?: "Novo Vizinho",
                                        email = user.email ?: "",
                                        fotoUrl = user.photoUrl.toString(),
                                    )
                                userDoc
                                    .set(novoUsuario)
                                    .addOnSuccessListener {
                                        Log.d("AuthRepo", "Salvo no Firestore com sucesso")
                                        callback(true) // SUCESSO COMPLETO
                                    }.addOnFailureListener { e ->
                                        Log.e("AuthRepo", "Erro ao salvar no Firestore", e)
                                        callback(true) // IMPORTANTE: Deixa entrar mesmo com erro no banco
                                    }
                            } else {
                                Log.d("AuthRepo", "Usuário já existia no Firestore")
                                callback(true) // SUCESSO (Já existia)
                            }
                        }.addOnFailureListener { e ->
                            Log.e("AuthRepo", "Erro ao ler Firestore", e)
                            callback(true) // IMPORTANTE: Deixa entrar mesmo com erro de leitura
                        }
                } else {
                    Log.e("AuthRepo", "Usuário veio nulo do Google")
                    callback(false)
                }
            }.addOnFailureListener { e ->
                Log.e("AuthRepo", "Erro fatal na autenticação", e)
                callback(false)
            }
    }
}
