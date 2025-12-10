package br.com.entrevizinhos.data.repository

import android.util.Log
import br.com.entrevizinhos.model.Usuario
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Repository responsável por autenticação e gerenciamento de usuários
 * Integra Firebase Auth com Firestore para dados completos
 */
class AuthRepository {
    private val auth = FirebaseAuth.getInstance() // Serviço de autenticação do Firebase
    private val db = FirebaseFirestore.getInstance() // Banco de dados para perfis

    // Retorna usuário logado no momento (null se deslogado)
    fun getCurrentUser() = auth.currentUser

    // Desloga usuário e limpa sessão local
    fun signOut() {
        auth.signOut() // Remove autenticação ativa
    }

    // Busca perfil completo do usuário no Firestore
    suspend fun carregarDadosUsuario(): Usuario? {
        val userId = auth.currentUser?.uid ?: return null // Verifica se está logado

        return try {
            // Consulta documento do usuário na coleção "usuarios"
            val document =
                db
                    .collection("usuarios") // Coleção de perfis
                    .document(userId) // Documento com UID como chave
                    .get() // Busca síncrona
                    .await() // Aguarda resultado

            if (document.exists()) {
                document.toObject(Usuario::class.java) // Converte para objeto Kotlin
            } else {
                // Primeiro acesso: cria perfil básico com dados do Auth
                criarUsuarioBasico(userId)
            }
        } catch (e: Exception) {
            Log.e("AuthRepo", "Erro ao carregar dados: ${e.message}")
            // Modo offline: usa dados mínimos do Firebase Auth
            criarUsuarioBasico(userId)
        }
    }

    // Persiste alterações do perfil no banco de dados
    suspend fun salvarPerfil(usuario: Usuario): Boolean {
        val userId = auth.currentUser?.uid ?: return false // Verifica autenticação
        return try {
            db
                .collection("usuarios") // Coleção de perfis
                .document(userId) // Documento do usuário atual
                .set(usuario) // Substitui dados completos
                .await() // Aguarda confirmação
            true // Sucesso na operação
        } catch (e: Exception) {
            Log.e("AuthRepo", "Erro ao salvar perfil", e)
            false // Falha ao salvar
        }
    }

    // Permite acesso como visitante (sem cadastro)
    suspend fun loginAnonymously(): Boolean =
        try {
            auth.signInAnonymously().await() // Cria sessão temporária
            true // Login anônimo bem-sucedido
        } catch (e: Exception) {
            Log.e("AuthRepo", "Erro login anônimo", e)
            false // Falha no login visitante
        }

    // Processo completo de login com Google (Auth + Firestore)
    suspend fun loginComGoogle(credential: AuthCredential): Boolean {
        Log.d("AuthRepo", "Iniciando login com credencial...")
        return try {
            // Etapa 1: Autenticação no Firebase Auth
            val result = auth.signInWithCredential(credential).await()
            val user = result.user ?: return false // Falha na autenticação
            val userId = user.uid
            Log.d("AuthRepo", "Login Auth OK! ID: $userId")

            // Etapa 2: Verifica existência do perfil no Firestore
            val userDocRef = db.collection("usuarios").document(userId)
            val document = userDocRef.get().await()

            if (!document.exists()) {
                // Etapa 3: Primeiro login - cria perfil inicial
                val novoUsuario =
                    Usuario(
                        id = userId, // UID do Firebase Auth
                        nome = user.displayName ?: "Novo Vizinho", // Nome do Google
                        email = user.email ?: "", // Email do Google
                        fotoUrl = user.photoUrl?.toString() ?: "", // Avatar do Google
                    )
                userDocRef.set(novoUsuario).await() // Salva perfil inicial
                Log.d("AuthRepo", "Novo usuário salvo no Firestore")
            } else {
                Log.d("AuthRepo", "Usuário já existia no Firestore") // Login recorrente
            }

            true // Processo completo bem-sucedido
        } catch (e: Exception) {
            Log.e("AuthRepo", "Erro fatal na autenticação/banco", e)
            false // Falha em qualquer etapa
        }
    }

    // Cria objeto Usuario com dados mínimos do Firebase Auth
    private fun criarUsuarioBasico(userId: String): Usuario {
        val email = auth.currentUser?.email ?: "" // Email da conta Google
        val nome = auth.currentUser?.displayName ?: "Vizinho Visitante" // Nome ou padrão
        val foto = auth.currentUser?.photoUrl?.toString() ?: "" // Avatar ou vazio
        return Usuario(id = userId, email = email, nome = nome, fotoUrl = foto) // Perfil mínimo
    }
}
