package br.com.entrevizinhos.model

import java.io.Serializable
import java.util.Date

/**
 * Modelo de dados para representar um usuário do app
 * Contém informações pessoais e preferências
 */
data class Usuario(
    val id: String = "", // UID do Firebase Auth (chave primária)
    val nome: String = "", // Nome de exibição nos anúncios
    val email: String = "", // Email usado no login (Google/manual)
    val fotoUrl: String = "", // Avatar do Google ou upload personalizado
    val telefone: String = "", // Contato para negociações (opcional)
    val endereco: String = "", // Localização para cálculo de entrega
    val cnpj: String = "", // Documento para vendedores empresariais
    val membroDesde: Date = Date(), // Antigüidade na plataforma
    val rating: Float = 0.0f, // Nota de 0 a 5 baseada em avaliações
    val favoritos: List<String> = emptyList(), // Lista de IDs de anúncios salvos
) : Serializable // Permite navegação com dados do usuário
