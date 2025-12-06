package br.com.entrevizinhos.model

import java.io.Serializable
import java.util.Date

data class Anuncio(
    val id: String = "",
    val titulo: String = "",
    val descricao: String = "",
    val categoria: String = "",
    val preco: Double = 0.0,
    val cidade: String = "",
    val fotos: List<String> = emptyList(),
    val vendedorId: String = "",
    val dataPublicacao: Date = Date(),
) : Serializable
