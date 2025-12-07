package br.com.entrevizinhos.model

import java.io.Serializable
import java.util.Date

data class Usuario(
    val id: String = "",
    val nome: String = "",
    val email: String = "",
    val fotoUrl: String = "",
    val telefone: String = "",
    val endereco: String = "",
    val cnpj: String = "",       // Novo campo
    val membroDesde: Date = Date(), // Novo campo
    val rating: Float = 0.0f
) : Serializable