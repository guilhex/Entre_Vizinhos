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

/**
 * ViewModel responsável por carregar e gerenciar anúncios e favoritos
 * Compartilhado entre FeedFragment e outros fragments que exibem listas
 */
class LerAnuncioViewModel : ViewModel() {
    private val anuncioRepository = AnuncioRepository() // Fonte de dados dos anúncios
    private val usuarioRepository = UsuarioRepository() // Gerencia favoritos
    private val authRepository = AuthRepository() // Estado de autenticação

    // Cache local da lista completa de anúncios
    private val _anuncios = MutableLiveData<List<Anuncio>>()
    val anuncios: LiveData<List<Anuncio>> get() = _anuncios // Observável pela UI

    // Cache local dos IDs favoritados (para UI rápida)
    private val _favoritosIds = MutableLiveData<Set<String>>(emptySet())
    val favoritosIds: LiveData<Set<String>> get() = _favoritosIds // Observável pela UI

    init {
        buscarAnuncios() // Carrega dados iniciais
        carregarFavoritosDoUsuario() // Carrega preferências do usuário
    }

    // Busca e atualiza cache local com todos os anúncios disponíveis
    fun buscarAnuncios() {
        viewModelScope.launch { // Operação assíncrona
            val lista = anuncioRepository.getAnuncios() // Consulta Firestore
            _anuncios.value = lista // Atualiza LiveData (notifica observers)
        }
    }

    // Carrega preferências de favoritos do usuário atual
    private fun carregarFavoritosDoUsuario() {
        val usuarioAtual = authRepository.getCurrentUser() ?: return // Sai se não logado

        viewModelScope.launch { // Operação assíncrona
            val usuario = usuarioRepository.getUsuario(usuarioAtual.uid) // Busca perfil
            val favoritos = usuario?.favoritos ?: emptyList() // Lista ou vazia
            _favoritosIds.value = favoritos.toSet() // Converte para Set (busca rápida)
        }
    }

    // Adiciona ou remove anúncio dos favoritos
    fun onFavoritoClick(anuncioId: String) {
        val usuarioAtual = authRepository.getCurrentUser()

        if (usuarioAtual == null) {
            Log.e("AnuncioViewModel", "Usuário não logado. Não é possível favoritar.")
            return
        }

        if (anuncioId.isBlank()) {
            Log.e("AnuncioViewModel", "onFavoritoClick chamado com anuncioId vazio; ignorando.")
            return
        }

        viewModelScope.launch {
            // Verifica estado atual dos favoritos
            val favoritosAtuais = _favoritosIds.value ?: emptySet()
            val estaFavoritado = anuncioId in favoritosAtuais
            val adicionar = !estaFavoritado
            val anterior = favoritosAtuais

            // Atualiza UI otimisticamente
            val novos = if (adicionar) favoritosAtuais + anuncioId else favoritosAtuais - anuncioId
            _favoritosIds.value = novos
            Log.d("AnuncioViewModel", "[optimistic] anuncioId=$anuncioId adicionar=$adicionar novosFavoritos=$novos")

            // Persiste no Firestore
            val sucesso =
                try {
                    usuarioRepository.setFavorito(usuarioId = usuarioAtual.uid, anuncioId = anuncioId, adicionar = adicionar)
                } catch (e: Exception) {
                    Log.e("AnuncioViewModel", "Erro ao persistir favorito (ex):", e)
                    false
                }

            // Reverte se falhou
            if (!sucesso) {
                Log.e("AnuncioViewModel", "Falha ao persistir favorito para $anuncioId. Revertendo UI.")
                _favoritosIds.value = anterior
            } else {
                Log.d("AnuncioViewModel", "[persisted] anuncioId=$anuncioId adicionar=$adicionar")
            }
        }
    }
}
