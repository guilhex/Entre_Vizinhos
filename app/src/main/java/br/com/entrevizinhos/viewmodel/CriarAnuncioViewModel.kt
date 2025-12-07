package br.com.entrevizinhos.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel // Mudança de ViewModel para AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import br.com.entrevizinhos.data.repository.AnuncioRepository
import br.com.entrevizinhos.data.repository.AuthRepository
import br.com.entrevizinhos.model.Anuncio
import kotlinx.coroutines.launch
import java.util.Date

// Mudei para AndroidViewModel para ter acesso ao "getApplication()"
class CriarAnuncioViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AnuncioRepository()
    private val authRepository = AuthRepository()
    // Contexto para ler a imagem
    private val context = application.applicationContext

    private val _resultadoPublicacao = MutableLiveData<Boolean>()
    val resultadoPublicacao: LiveData<Boolean> = _resultadoPublicacao

    fun publicarAnuncio(
        titulo: String,
        preco: Double,
        descricao: String,
        categoria: String,
        entrega: String,
        formasPagamento: String,
        fotoUri: Uri?
    ) {
        val usuarioAtual = authRepository.getCurrentUser()

        if (usuarioAtual != null) {
            viewModelScope.launch {
                var imagemBase64 = ""

                // Conversão da foto
                if (fotoUri != null) {
                    val resultado = repository.converterImagemParaBase64(context, fotoUri)
                    if (resultado != null) {
                        imagemBase64 = resultado
                    }
                }

                val listaFotos = if (imagemBase64.isNotEmpty()) listOf(imagemBase64) else emptyList()

                val novoAnuncio = Anuncio(
                    titulo = titulo,
                    preco = preco,
                    descricao = descricao,
                    cidade = "Urutaí",
                    categoria = categoria,
                    entrega = entrega,
                    formasPagamento = formasPagamento,
                    vendedorId = usuarioAtual.uid,
                    dataPublicacao = Date(),
                    fotos = listaFotos
                )

                val sucesso = repository.salvarAnuncio(novoAnuncio)
                _resultadoPublicacao.value = sucesso
            }
        } else {
            _resultadoPublicacao.value = false
        }
    }
}