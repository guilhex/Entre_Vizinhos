package br.com.entrevizinhos.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.entrevizinhos.data.repository.AnuncioRepository
import br.com.entrevizinhos.data.repository.AuthRepository
import br.com.entrevizinhos.data.repository.UsuarioRepository
import br.com.entrevizinhos.model.Anuncio
import kotlinx.coroutines.launch

class AnuncioViewModel : ViewModel() {
    // Repositórios
    private val anuncioRepository = AnuncioRepository()
    private val usuarioRepository = UsuarioRepository()
    private val authRepository = AuthRepository()

    // LiveData: anúncios
    private val _anuncios = MutableLiveData<List<Anuncio>>()
    val anuncios: LiveData<List<Anuncio>> get() = _anuncios

    // LiveData: favoritos (ids)
    private val _favoritosIds = MutableLiveData<Set<String>>(emptySet())
    val favoritosIds: LiveData<Set<String>> get() = _favoritosIds

    init {
        buscarAnuncios()
        carregarFavoritosDoUsuario()
    }

    // Busca anúncios
    fun buscarAnuncios() {
        viewModelScope.launch {
            val lista = anuncioRepository.getAnuncios()
            _anuncios.value = lista
        }
    }

    // Carrega favoritos do usuário
    private fun carregarFavoritosDoUsuario() {
        val usuarioAtual = authRepository.getCurrentUser() ?: return

        viewModelScope.launch {
            val usuario = usuarioRepository.getUsuario(usuarioAtual.uid)

            // Garante lista vazia se null
            val favoritos = usuario?.favoritos ?: emptyList()
            _favoritosIds.value = favoritos.toSet()
        }
    }

    // Alterna favorito
    fun onFavoritoClick(anuncioId: String) {
        val usuarioAtual = authRepository.getCurrentUser()

        if (usuarioAtual == null) {
            Log.e("AnuncioViewModel", "Usuário não logado. Não é possível favoritar.")
            // poderia notificar erro
            return
        }

        if (anuncioId.isBlank()) {
            Log.e("AnuncioViewModel", "onFavoritoClick chamado com anuncioId vazio; ignorando.")
            return
        }

        viewModelScope.launch {
            // Lê favoritos
            val favoritosAtuais = _favoritosIds.value ?: emptySet()
            val estaFavoritado = anuncioId in favoritosAtuais

            // Decide operação: true = adicionar, false = remover
            val adicionar = !estaFavoritado

            // Estado anterior para rollback se falhar
            val anterior = favoritosAtuais

            // Atualiza otimista no LiveData
            val novos = if (adicionar) favoritosAtuais + anuncioId else favoritosAtuais - anuncioId
            _favoritosIds.value = novos
            Log.d("AnuncioViewModel", "[optimistic] anuncioId=$anuncioId adicionar=$adicionar novosFavoritos=$novos")

            // Persiste via UsuarioRepository (agora recebe operação)
            val sucesso =
                try {
                    usuarioRepository.setFavorito(usuarioId = usuarioAtual.uid, anuncioId = anuncioId, adicionar = adicionar)
                } catch (e: Exception) {
                    Log.e("AnuncioViewModel", "Erro ao persistir favorito (ex):", e)
                    false
                }

            if (!sucesso) {
                Log.e("AnuncioViewModel", "Falha ao persistir favorito para $anuncioId. Revertendo UI.")
                _favoritosIds.value = anterior
            } else {
                Log.d("AnuncioViewModel", "[persisted] anuncioId=$anuncioId adicionar=$adicionar")
                // Atualiza estado com o que está no banco para manter consistência (opcional)
                // Poderíamos recarregar do repo, mas aqui confiamos na operação atômica
            }
        }
    }
}
