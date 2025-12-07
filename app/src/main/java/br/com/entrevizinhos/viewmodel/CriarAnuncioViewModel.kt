package br.com.entrevizinhos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.entrevizinhos.data.repository.AnuncioRepository
import br.com.entrevizinhos.data.repository.AuthRepository
import br.com.entrevizinhos.model.Anuncio
import kotlinx.coroutines.launch
import java.util.Date

class CriarAnuncioViewModel : ViewModel() {
    private val repository = AnuncioRepository()
    private val authRepository = AuthRepository()

    private val _resultadoPublicacao = MutableLiveData<Boolean>()
    val resultadoPublicacao: LiveData<Boolean> = _resultadoPublicacao

    // Função atualizada para aceitar TODOS os campos do formulário
    fun publicarAnuncio(
        titulo: String,
        preco: Double,
        descricao: String,
        categoria: String,
        entrega: String,
        formasPagamento: String,
    ) {
        val usuarioAtual = authRepository.getCurrentUser()

        if (usuarioAtual != null) {
            val novoAnuncio =
                Anuncio(
                    titulo = titulo,
                    preco = preco,
                    descricao = descricao,
                    cidade = "Urutaí", // Cidade fixa conforme combinamos
                    categoria = categoria,
                    entrega = entrega,
                    formasPagamento = formasPagamento,
                    vendedorId = usuarioAtual.uid,
                    dataPublicacao = Date(),
                )

            viewModelScope.launch {
                val sucesso = repository.salvarAnuncio(novoAnuncio)
                _resultadoPublicacao.value = sucesso
            }
        } else {
            _resultadoPublicacao.value = false
        }
    }
}
