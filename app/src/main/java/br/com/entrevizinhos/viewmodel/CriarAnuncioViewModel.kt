package br.com.entrevizinhos.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.entrevizinhos.data.repository.AnuncioRepository
import br.com.entrevizinhos.data.repository.AuthRepository
import br.com.entrevizinhos.model.Anuncio
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.util.Date

class CriarAnuncioViewModel : ViewModel() {
    private val repository = AnuncioRepository()
    private val authRepository = AuthRepository()

    private val _resultadoPublicacao = MutableLiveData<Boolean>()
    val resultadoPublicacao: LiveData<Boolean> = _resultadoPublicacao

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
        val usuarioAtual = authRepository.getCurrentUser()

        if (usuarioAtual != null) {
            viewModelScope.launch {
                try {
                    val fotosBase64 = fotos.mapNotNull { uri ->
                        repository.converterImagemParaBase64(context, uri)
                    }

                    val novoAnuncio =
                        Anuncio(
                            titulo = titulo,
                            preco = preco,
                            descricao = descricao,
                            cidade = "Urutaí",
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

    // ===== ATUALIZAR ANÚNCIO =====
    fun atualizarAnuncio(
        anuncioId: String,
        titulo: String,
        preco: Double,
        descricao: String,
        categoria: String,
        entrega: String,
        formasPagamento: String,
        context: Context,
    ) {
        val db = FirebaseFirestore.getInstance()

        val anuncioAtualizado = mapOf(
            "titulo" to titulo,
            "preco" to preco,
            "descricao" to descricao,
            "categoria" to categoria,
            "entrega" to entrega,
            "formasPagamento" to formasPagamento,
        )

        db.collection("anuncios").document(anuncioId).update(anuncioAtualizado)
            .addOnSuccessListener {
                _resultadoPublicacao.postValue(true)
            }
            .addOnFailureListener {
                _resultadoPublicacao.postValue(false)
            }
    }
}