package br.com.entrevizinhos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.entrevizinhos.data.repository.AnuncioRepository
import br.com.entrevizinhos.data.repository.AuthRepository
import br.com.entrevizinhos.model.Anuncio
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
                // fotos = emptyList() (Implementar upload de fotos é um passo avançado para depois)
            )

            repository.salvarAnuncio(novoAnuncio) { sucesso ->
                _resultadoPublicacao.value = sucesso
            }
        } else {
            // Se não estiver logado, não pode publicar (teoricamente a UI já bloqueia, mas é bom garantir)
            _resultadoPublicacao.value = false
        }
    }
}