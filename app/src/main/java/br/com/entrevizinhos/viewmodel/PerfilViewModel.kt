package br.com.entrevizinhos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.entrevizinhos.data.repository.AuthRepository
import br.com.entrevizinhos.model.Usuario

class PerfilViewModel : ViewModel() {

    private val repository = AuthRepository()

    // --- DADOS DO USUÁRIO ---
    private val _dadosUsuario = MutableLiveData<Usuario>()
    val dadosUsuario: LiveData<Usuario> = _dadosUsuario

    // --- ESTADO DO LOGOUT (A variável que estava faltando) ---
    private val _estadoLogout = MutableLiveData<Boolean>()
    val estadoLogout: LiveData<Boolean> = _estadoLogout

    // Busca dados no Firebase
    fun carregarDados() {
        repository.carregarDadosUsuario { usuario ->
            if (usuario != null) {
                _dadosUsuario.postValue(usuario!!)
            }
        }
    }

    // Salva alterações (Nome/Telefone)
    fun salvarPerfil(usuario: Usuario) {
        repository.salvarPerfil(usuario) { sucesso ->
            if (sucesso) {
                _dadosUsuario.value = usuario
            }
        }
    }

    // Desloga do Firebase
    fun deslogar() {
        repository.signOut()
        _estadoLogout.value = true // Avisa a tela que saiu
    }
}