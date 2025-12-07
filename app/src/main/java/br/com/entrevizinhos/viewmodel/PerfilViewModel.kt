package br.com.entrevizinhos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.entrevizinhos.data.repository.AnuncioRepository
import br.com.entrevizinhos.data.repository.AuthRepository
import br.com.entrevizinhos.model.Anuncio
import br.com.entrevizinhos.model.Usuario
import kotlinx.coroutines.launch

class PerfilViewModel : ViewModel() {
    private val repository = AuthRepository()
    private val anuncioRepository = AnuncioRepository()

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

    // ANUNCIOS
    private val _meusAnuncios = MutableLiveData<List<Anuncio>>()
    val meusAnuncios: LiveData<List<Anuncio>> = _meusAnuncios

    fun carregarMeusAnuncios() {
        val usuarioAtual = repository.getCurrentUser()

        if (usuarioAtual == null) {
            // Usuário deslogado: não tem "meus anúncios"
            _meusAnuncios.value = emptyList()
            return
        }

        viewModelScope.launch {
            val lista = anuncioRepository.buscarAnunciosPorVendedor(usuarioAtual.uid)
            _meusAnuncios.value = lista
        }
    }
}