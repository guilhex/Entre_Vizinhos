package br.com.entrevizinhos.model

import java.io.Serializable

data class Usuario(
    val id: String = "",
    val nome: String = "",
    val email: String = "",
    val fotoUrl: String = "",
    val telefone: String = "",
    val rating: Float = 0.0f
) : Serializable