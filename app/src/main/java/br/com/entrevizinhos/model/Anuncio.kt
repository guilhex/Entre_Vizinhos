package br.com.entrevizinhos.model

import java.io.Serializable
import java.util.Date

data class Anuncio(
    val id: String = "",
    val titulo: String = "",
    val descricao: String = "",
    val preco: Double = 0.0,
    val cidade: String = "",
    val vendedorId: String = "",
    val dataPublicacao: Date? = null,
    val fotos: List<String> = emptyList(),
    val categoria: String = "",
    val entrega: String = "",
    val formasPagamento: String = ""
) : Serializable
