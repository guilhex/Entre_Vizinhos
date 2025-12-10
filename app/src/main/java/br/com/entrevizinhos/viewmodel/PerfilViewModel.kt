package br.com.entrevizinhos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.entrevizinhos.data.repository.AnuncioRepository
import br.com.entrevizinhos.data.repository.AuthRepository
import br.com.entrevizinhos.data.repository.UsuarioRepository
import br.com.entrevizinhos.model.Anuncio
import br.com.entrevizinhos.model.Usuario
import kotlinx.coroutines.launch

/**
 * ViewModel responsável por gerenciar perfil do usuário e seus anúncios
 */
class PerfilViewModel : ViewModel() {
    private val repository = AuthRepository() // Repository de autenticação
    private val anuncioRepository = AnuncioRepository() // Repository de anúncios
    private val usuarioRepository = UsuarioRepository() // Repository de usuários

    // Dados de um vendedor específico (para tela de detalhes)
    private val _vendedor = MutableLiveData<Usuario?>()
    val vendedor: LiveData<Usuario?> = _vendedor

    // Dados do usuário logado
    private val _dadosUsuario = MutableLiveData<Usuario?>()
    val dadosUsuario: LiveData<Usuario?> = _dadosUsuario

    // Estado do logout
    private val _estadoLogout = MutableLiveData<Boolean>()
    val estadoLogout: LiveData<Boolean> = _estadoLogout

    // Estado de carregamento
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Anúncios do usuário logado
    private val _meusAnuncios = MutableLiveData<List<Anuncio>>()
    val meusAnuncios: LiveData<List<Anuncio>> = _meusAnuncios

    // Carrega dados do usuário logado
    fun carregarDados() {
        viewModelScope.launch {
            _isLoading.value = true
            val usuario = repository.carregarDadosUsuario()
            if (usuario != null) {
                _dadosUsuario.value = usuario
            }
            _isLoading.value = false
        }
    }

    // Salva alterações no perfil
    fun salvarPerfil(usuario: Usuario) {
        viewModelScope.launch {
            _isLoading.value = true
            val sucesso = repository.salvarPerfil(usuario)
            _isLoading.value = false
            if (sucesso) {
                _dadosUsuario.value = usuario
            }
        }
    }

    // Faz logout do usuário
    fun deslogar() {
        repository.signOut()
        _estadoLogout.value = true
    }

    // Carrega anúncios do usuário logado
    fun carregarMeusAnuncios() {
        val usuarioAtual = repository.getCurrentUser()
        if (usuarioAtual == null) {
            _meusAnuncios.value = emptyList()
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            val lista = anuncioRepository.buscarAnunciosPorVendedor(usuarioAtual.uid)
            _meusAnuncios.value = lista
            _isLoading.value = false
        }
    }

    // Carrega dados de um vendedor específico
    fun carregarVendedor(id: String) {
        viewModelScope.launch {
            val usuario = usuarioRepository.getUsuario(id)
            _vendedor.value = usuario
        }
    }

    // Deleta anúncio e atualiza lista local
    suspend fun deletarAnuncio(anuncioId: String): Boolean {
        val sucesso = anuncioRepository.deletarAnuncio(anuncioId)

        // Atualiza lista local se sucesso
        if (sucesso) {
            val listaAtual = _meusAnuncios.value.orEmpty().toMutableList()
            listaAtual.removeAll { it.id == anuncioId }
            _meusAnuncios.postValue(listaAtual)
        }

        return sucesso
    }
}
