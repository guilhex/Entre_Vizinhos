package br.com.entrevizinhos.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import br.com.entrevizinhos.R
import br.com.entrevizinhos.databinding.FragmentPerfilBinding
import br.com.entrevizinhos.model.Usuario
import br.com.entrevizinhos.viewmodel.PerfilViewModel
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
// ... outros imports
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.entrevizinhos.model.Anuncio
import br.com.entrevizinhos.ui.adapter.AnuncioAdapter
import androidx.navigation.fragment.findNavController

class PerfilFragment : Fragment() {

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: PerfilViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(PerfilViewModel::class.java)

        setupObservadores()
        setupListeners()

        setupMeusAnuncios()

        // Busca os dados iniciais
        viewModel.carregarDados()
    }

    private fun setupObservadores() {
        viewModel.dadosUsuario.observe(viewLifecycleOwner) { usuario ->
            // Atualiza a tela com os dados do usuário
            binding.tvNomeUsuario.text = if (usuario.nome.isNotEmpty()) usuario.nome else "Usuário"
            binding.tvEmailUsuario.text = usuario.email
            binding.tvTelefone.text = if (usuario.telefone.isNotEmpty()) usuario.telefone else "Toque em editar para adicionar"

            // Carrega a foto (Se tiver URL)
            if (usuario.fotoUrl.isNotEmpty()) {
                Glide.with(this).load(usuario.fotoUrl).circleCrop().into(binding.ivPerfilFoto)
            }
        }

        viewModel.estadoLogout.observe(viewLifecycleOwner) { saiu ->
            if (saiu) {
                // Aqui você deve navegar para a tela de Login
                Toast.makeText(context, "Saiu da conta", Toast.LENGTH_SHORT).show()
                binding.tvNomeUsuario.text = "Visitante"
                binding.tvEmailUsuario.text = "-"
                binding.tvTelefone.text = "-"
            }
        }
    }

    private fun setupListeners() {
        binding.btnSair.setOnClickListener {
            viewModel.deslogar()
        }

        binding.btnEditarPerfil.setOnClickListener {
            val usuarioAtual = viewModel.dadosUsuario.value
            if (usuarioAtual != null) {
                mostrarDialogoEdicao(usuarioAtual)
            } else {
                Toast.makeText(context, "Aguardando dados...", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnAnunciar.setOnClickListener {
            // Navega para a tela de criação
            // Certifique-se de importar: androidx.navigation.fragment.findNavController
            findNavController().navigate(R.id.action_perfil_to_criarAnuncio)
        }
    }

    private fun mostrarDialogoEdicao(usuario: Usuario) {
        // 1. Prepara o Layout do Diálogo
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_editar_perfil, null)

        val etNome = dialogView.findViewById<TextInputEditText>(R.id.etEditarNome)
        val etTelefone = dialogView.findViewById<TextInputEditText>(R.id.etEditarTelefone)

        // 2. Preenche com os dados atuais
        etNome.setText(usuario.nome)
        etTelefone.setText(usuario.telefone)

        // 3. Constrói o Alerta
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Salvar") { _, _ ->
                val novoNome = etNome.text.toString()
                val novoTelefone = etTelefone.text.toString()

                val usuarioAtualizado = usuario.copy(
                    nome = novoNome,
                    telefone = novoTelefone
                )

                viewModel.salvarPerfil(usuarioAtualizado)
                Toast.makeText(context, "Perfil Atualizado!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()

        // (Opcional) Mudar a cor do botão do diálogo para Verde
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.green_primary, null))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.gray_text, null))
    }
    private fun setupMeusAnuncios() {
        // Dados de teste para ver a lista funcionando (depois virá do Firebase)
        val listaTeste = listOf(
            Anuncio(id = "1", titulo = "Meu Produto Exemplo", preco = 100.0, cidade = "Urutaí"),
            Anuncio(id = "2", titulo = "Outra venda minha", preco = 50.0, cidade = "Urutaí")
        )

        val adapter = AnuncioAdapter(listaTeste) { anuncio ->
            Toast.makeText(context, "Editar anúncio: ${anuncio.titulo}", Toast.LENGTH_SHORT).show()
        }

        binding.rvMeusAnuncios.layoutManager = LinearLayoutManager(context)
        binding.rvMeusAnuncios.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}