package br.com.entrevizinhos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.entrevizinhos.data.repository.AnuncioRepository
import br.com.entrevizinhos.model.Anuncio
import kotlinx.coroutines.launch

class AnuncioViewModel : ViewModel() {
    private val anuncioRepository = AnuncioRepository()

    private val _anuncios = MutableLiveData<List<Anuncio>>() // serve pra apenas o viewmodel alterar
    val anuncios: LiveData<List<Anuncio>> = _anuncios // permite ler mas n alterar

    // executa uma vez quando iniciar o viewmodel
    init {
        buscarAnuncios()
    }

    fun buscarAnuncios() {
        // serve para fazer uma tarefa em segundo plano (.wait do repository)
        viewModelScope.launch {
            val lista = anuncioRepository.getAnuncios() // salvar em uma val diferente para caso der erro achar facil
            _anuncios.value = lista
        }
    }
}