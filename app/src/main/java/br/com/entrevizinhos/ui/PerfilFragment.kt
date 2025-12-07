package br.com.entrevizinhos.ui

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import br.com.entrevizinhos.databinding.FragmentPerfilBinding
import br.com.entrevizinhos.ui.adapter.AnuncioAdapter
import br.com.entrevizinhos.viewmodel.PerfilViewModel
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Locale

class PerfilFragment : Fragment() {
    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!

    // ViewModel instanciado com a extensão 'by viewModels()'
    private val viewModel: PerfilViewModel by viewModels()

    private lateinit var meusAnunciosAdapter: AnuncioAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupObservers()

        // Busca os dados iniciais
        viewModel.carregarDados()
        viewModel.carregarMeusAnuncios()
    }

    private fun setupUI() {
        // Botão Sair
        binding.btnSair.setOnClickListener {
            viewModel.deslogar()
        }

        // Botão Anunciar
        binding.btnAnunciar.setOnClickListener {
            val action = PerfilFragmentDirections.actionPerfilToCriarAnuncio()
            findNavController().navigate(action)
        }

        // Botão Editar Perfil - Causa do crash se 'usuarioAtual' for nulo
        binding.btnEditarPerfil.setOnClickListener {
            val usuarioAtual = viewModel.dadosUsuario.value
            if (usuarioAtual != null) {
                val action = PerfilFragmentDirections.actionPerfilToEditarPerfil(usuarioAtual)
                findNavController().navigate(action)
            } else {
                Toast.makeText(context, "Aguarde o carregamento dos dados...", Toast.LENGTH_SHORT).show()
            }
        }

        // Lista de Anúncios (Grid)
        meusAnunciosAdapter = AnuncioAdapter(emptyList()) { anuncio ->
            // Ação ao clicar no meu anúncio
            val action = PerfilFragmentDirections.actionPerfilToDetalhesAnuncio(anuncio)
            findNavController().navigate(action)
        }

        binding.rvMeusAnuncios.layoutManager = GridLayoutManager(context, 2)
        binding.rvMeusAnuncios.adapter = meusAnunciosAdapter
    }

    private fun setupObservers() {
        viewModel.dadosUsuario.observe(viewLifecycleOwner) { usuario ->
            // Preenche os textos na tela
            binding.tvNomeUsuario.text = usuario.nome.ifEmpty { "Usuário" }
            binding.tvEmail.text = usuario.email
            binding.tvTelefone.text = usuario.telefone.ifEmpty { "Sem telefone" }
            binding.tvEndereco.text = usuario.endereco.ifEmpty { "Endereço não informado" }
            binding.tvCnpj.text = usuario.cnpj.ifEmpty { "-" }

            // Data membro
            val df = SimpleDateFormat("yyyy", Locale("pt", "BR"))
            binding.tvMembroDesde.text = "Membro desde ${df.format(usuario.membroDesde)}"

            // Foto
            if (usuario.fotoUrl.isNotEmpty()) {
                if (usuario.fotoUrl.startsWith("data:image")) {
                    try {
                        val base64Clean = usuario.fotoUrl.substringAfter(",")
                        val decodedBytes = Base64.decode(base64Clean, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                        binding.ivPerfilFoto.setImageBitmap(bitmap)
                    } catch (e: Exception) { }
                } else {
                    Glide.with(this).load(usuario.fotoUrl).circleCrop().into(binding.ivPerfilFoto)
                }
            }
        }

        viewModel.meusAnuncios.observe(viewLifecycleOwner) { lista ->
            meusAnunciosAdapter.atualizarLista(lista)
        }

        viewModel.estadoLogout.observe(viewLifecycleOwner) { saiu ->
            if (saiu) {
                val action = PerfilFragmentDirections.actionPerfilToLogin()
                findNavController().navigate(action)
                Toast.makeText(context, "Sessão encerrada", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}