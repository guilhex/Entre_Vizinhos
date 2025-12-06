package br.com.entrevizinhos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope // Importante: Permite usar corrotinas no ViewModel
import br.com.entrevizinhos.data.repository.AnuncioRepository
import br.com.entrevizinhos.data.repository.AuthRepository
import br.com.entrevizinhos.model.Anuncio
import kotlinx.coroutines.launch // Importante: Para iniciar a corrotina
import java.util.Date

class CriarAnuncioViewModel : ViewModel() {

    private val repository = AnuncioRepository()
    private val authRepository = AuthRepository()

    private val _resultadoPublicacao = MutableLiveData<Boolean>()
    val resultadoPublicacao: LiveData<Boolean> = _resultadoPublicacao

    fun publicarAnuncio(titulo: String, preco: Double, cidade: String, descricao: String) {
        val usuarioAtual = authRepository.getCurrentUser()

        if (usuarioAtual != null) {
            val novoAnuncio = Anuncio(
                titulo = titulo,
                preco = preco,
                cidade = cidade,
                descricao = descricao,
                vendedorId = usuarioAtual.uid,
                dataPublicacao = Date()
            )

            // 1. Abrimos o escopo de corrotina
            viewModelScope.launch {
                // 2. Chamamos a função suspend (que espera e retorna true/false)
                val sucesso = repository.salvarAnuncio(novoAnuncio)

                // 3. Atualizamos o LiveData com o resultado
                _resultadoPublicacao.value = sucesso
            }

        } else {
            _resultadoPublicacao.value = false
        }
    }
}