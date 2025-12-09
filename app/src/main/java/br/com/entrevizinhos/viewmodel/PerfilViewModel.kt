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
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class PerfilViewModel : ViewModel() {
    private val repository = AuthRepository()
    private val anuncioRepository = AnuncioRepository()

    private val usuarioRepository = UsuarioRepository()
    private val _vendedor = MutableLiveData<Usuario?>() // Pode ser nulo se não encontrar
    val vendedor: LiveData<Usuario?> = _vendedor

    // --- DADOS DO USUÁRIO ---
    private val _dadosUsuario = MutableLiveData<Usuario>()
    val dadosUsuario: LiveData<Usuario> = _dadosUsuario

    // --- ESTADO DO LOGOUT ---
    private val _estadoLogout = MutableLiveData<Boolean>()
    val estadoLogout: LiveData<Boolean> = _estadoLogout

    // --- ESTADO DE CARREGAMENTO ---
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Busca dados no Firebase
    fun carregarDados() {
        repository.carregarDadosUsuario { usuario ->
            if (usuario != null) {
                _dadosUsuario.postValue(usuario)
            }
        }
    }

    // Salva alterações (Nome/Telefone/Foto)
    fun salvarPerfil(usuario: Usuario) {
        _isLoading.value = true
        repository.salvarPerfil(usuario) { sucesso ->
            _isLoading.value = false
            if (sucesso) {
                _dadosUsuario.value = usuario
            }
        }
    }
    fun deletarAnuncio(anuncioId: String, callback: (Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance()

        db.collection("anuncios").document(anuncioId).delete()
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }


    // Desloga do Firebase
    fun deslogar() {
        repository.signOut()
        _estadoLogout.value = true
    }

    // ANUNCIOS
    private val _meusAnuncios = MutableLiveData<List<Anuncio>>()
    val meusAnuncios: LiveData<List<Anuncio>> = _meusAnuncios

    fun carregarMeusAnuncios() {
        val usuarioAtual = repository.getCurrentUser()

        if (usuarioAtual == null) {
            _meusAnuncios.value = emptyList()
            return
        }

        viewModelScope.launch {
            val lista = anuncioRepository.buscarAnunciosPorVendedor(usuarioAtual.uid)
            _meusAnuncios.value = lista
        }
    }

    fun carregarVendedor(id: String) {
        viewModelScope.launch {
            val usuario = usuarioRepository.getUsuario(id)
            _vendedor.value = usuario
        }
    }
}
