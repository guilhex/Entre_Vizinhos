package br.com.entrevizinhos.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.entrevizinhos.data.repository.AnuncioRepository
import br.com.entrevizinhos.data.repository.AuthRepository
import br.com.entrevizinhos.data.repository.UsuarioRepository
import br.com.entrevizinhos.model.Anuncio
import kotlinx.coroutines.launch
import java.util.Date

class CriarAnuncioViewModel : ViewModel() {
    private val repository = AnuncioRepository()
    private val authRepository = AuthRepository()
    private val usuarioRepository = UsuarioRepository()

    private val _resultadoPublicacao = MutableLiveData<Boolean>()
    val resultadoPublicacao: LiveData<Boolean> = _resultadoPublicacao

    // ... (método publicarAnuncio mantém igual ao que você já corrigiu) ...
    fun publicarAnuncio(
        titulo: String,
        preco: Double,
        descricao: String,
        categoria: String,
        entrega: String,
        formasPagamento: String,
        fotos: List<Uri> = emptyList(),
        context: Context,
    ) {
        // ... seu código de publicar existente ...
        val usuarioAtual = authRepository.getCurrentUser()

        if (usuarioAtual != null) {
            viewModelScope.launch {
                try {
                    val dadosUsuario = usuarioRepository.getUsuario(usuarioAtual.uid)
                    val cidadeDoUsuario = dadosUsuario?.endereco?.takeIf { it.isNotEmpty() } ?: "Localização não informada"

                    val fotosBase64 =
                        fotos.mapNotNull { uri ->
                            repository.converterImagemParaBase64(context, uri)
                        }

                    val novoAnuncio =
                        Anuncio(
                            titulo = titulo,
                            preco = preco,
                            descricao = descricao,
                            cidade = cidadeDoUsuario,
                            categoria = categoria,
                            entrega = entrega,
                            formasPagamento = formasPagamento,
                            vendedorId = usuarioAtual.uid,
                            dataPublicacao = Date(),
                            fotos = fotosBase64,
                        )

                    val sucesso = repository.setAnuncio(novoAnuncio)
                    _resultadoPublicacao.postValue(sucesso)
                } catch (e: Exception) {
                    e.printStackTrace()
                    _resultadoPublicacao.postValue(false)
                }
            }
        } else {
            _resultadoPublicacao.postValue(false)
        }
    }

    fun atualizarAnuncio(
        anuncioId: String,
        titulo: String,
        preco: Double,
        descricao: String,
        categoria: String,
        entrega: String,
        formasPagamento: String,
        context: Context, // Mantido para compatibilidade, mas não é usado na atualização simples
    ) {
        // Prepara o mapa de dados a serem atualizados
        val anuncioAtualizado =
            mapOf(
                "titulo" to titulo,
                "preco" to preco,
                "descricao" to descricao,
                "categoria" to categoria,
                "entrega" to entrega,
                "formasPagamento" to formasPagamento,
            )

        viewModelScope.launch {
            // Chama o repositório de forma assíncrona
            val sucesso = repository.atualizarAnuncio(anuncioId, anuncioAtualizado)

            // Atualiza a LiveData com o resultado
            _resultadoPublicacao.postValue(sucesso)
        }
    }
}
