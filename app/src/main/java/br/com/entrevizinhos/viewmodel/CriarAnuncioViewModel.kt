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

/**
 * ViewModel responsável por criar e editar anúncios
 * Gerencia validação, processamento de imagens e persistência
 */
class CriarAnuncioViewModel : ViewModel() {
    private val repository = AnuncioRepository() // CRUD de anúncios
    private val authRepository = AuthRepository() // Dados do usuário logado
    private val usuarioRepository = UsuarioRepository() // Perfis de usuários

    // Estado da operação de publicação (sucesso/falha)
    private val _resultadoPublicacao = MutableLiveData<Boolean>()
    val resultadoPublicacao: LiveData<Boolean> = _resultadoPublicacao // Observável pela UI
    // Processo completo de publicação de anúncio
    fun publicarAnuncio(
        titulo: String, // Nome do produto/serviço
        preco: Double, // Valor em reais
        descricao: String, // Detalhes do item
        categoria: String, // Classificação selecionada
        entrega: String, // Modalidade de entrega
        formasPagamento: String, // Métodos aceitos
        fotos: List<Uri> = emptyList(), // Imagens selecionadas
        context: Context, // Para acessar ContentResolver
    ) {
        val usuarioAtual = authRepository.getCurrentUser() // Verifica autenticação

        if (usuarioAtual != null) {
            viewModelScope.launch { // Corrotina para operações assíncronas
                try {
                    // Etapa 1: Busca localização do vendedor
                    val dadosUsuario = usuarioRepository.getUsuario(usuarioAtual.uid)
                    val cidadeDoUsuario = dadosUsuario?.endereco?.takeIf { it.isNotEmpty() } 
                        ?: "Localização não informada" // Fallback se não tiver endereço

                    // Etapa 2: Processa imagens (converte para Base64)
                    val fotosBase64 =
                        fotos.mapNotNull { uri -> // Filtra URIs válidas
                            repository.converterImagemParaBase64(context, uri) // Compressão + Base64
                        }

                    // Etapa 3: Monta objeto completo do anúncio
                    val novoAnuncio =
                        Anuncio(
                            titulo = titulo,
                            preco = preco,
                            descricao = descricao,
                            cidade = cidadeDoUsuario, // Localização do vendedor
                            categoria = categoria,
                            entrega = entrega,
                            formasPagamento = formasPagamento,
                            vendedorId = usuarioAtual.uid, // Proprietário do anúncio
                            dataPublicacao = Date(), // Timestamp atual
                            fotos = fotosBase64, // Imagens processadas
                        )

                    // Etapa 4: Persiste no Firestore
                    val sucesso = repository.setAnuncio(novoAnuncio)
                    _resultadoPublicacao.postValue(sucesso) // Notifica UI
                } catch (e: Exception) {
                    e.printStackTrace()
                    _resultadoPublicacao.postValue(false) // Falha na operação
                }
            }
        } else {
            _resultadoPublicacao.postValue(false) // Usuário não autenticado
        }
    }

    // Atualiza anúncio existente (mantém fotos originais)
    fun atualizarAnuncio(
        anuncioId: String, // ID do anúncio a ser editado
        titulo: String, // Novo título
        preco: Double, // Novo preço
        descricao: String, // Nova descrição
        categoria: String, // Nova categoria
        entrega: String, // Nova modalidade de entrega
        formasPagamento: String, // Novas formas de pagamento
        context: Context, // Contexto (não usado nesta versão)
    ) {
        // Monta mapa apenas com campos que podem ser editados
        val anuncioAtualizado =
            mapOf(
                "titulo" to titulo, // Atualiza nome
                "preco" to preco, // Atualiza valor
                "descricao" to descricao, // Atualiza detalhes
                "categoria" to categoria, // Atualiza classificação
                "entrega" to entrega, // Atualiza modalidade
                "formasPagamento" to formasPagamento, // Atualiza pagamentos
                // Nota: fotos, vendedorId e dataPublicacao não são alterados
            )

        viewModelScope.launch { // Operação assíncrona
            // Executa update parcial no Firestore
            val sucesso = repository.atualizarAnuncio(anuncioId, anuncioAtualizado)
            _resultadoPublicacao.postValue(sucesso) // Notifica resultado
        }
    }
}
