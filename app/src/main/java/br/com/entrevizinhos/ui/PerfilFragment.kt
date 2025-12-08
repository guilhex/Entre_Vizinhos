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

class PerfilFragment : Fragment() {
    private var bindingNullable: FragmentPerfilBinding? = null
    private val binding get() = bindingNullable!!

    private val viewModel: PerfilViewModel by viewModels()

    private lateinit var meusAnunciosAdapter: AnuncioAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        bindingNullable = FragmentPerfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupUI()
        setupObservers()

        viewModel.carregarDados()
        viewModel.carregarMeusAnuncios()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupUI() {
        binding.btnSair.setOnClickListener {
            viewModel.deslogar()
        }

        binding.btnAnunciar.setOnClickListener {
            val action = PerfilFragmentDirections.actionPerfilToCriarAnuncio()
            findNavController().navigate(action)
        }

        binding.btnEditarPerfil.setOnClickListener {
            val usuarioAtual = viewModel.dadosUsuario.value
            if (usuarioAtual != null) {
                val action = PerfilFragmentDirections.actionPerfilToEditarPerfil(usuarioAtual)
                findNavController().navigate(action)
            } else {
                Toast.makeText(context, "Aguarde o carregamento dos dados...", Toast.LENGTH_SHORT).show()
            }
        }

        meusAnunciosAdapter =
            AnuncioAdapter(
                listaAnuncios = emptyList(),
                favoritosIds =
                    viewModel.dadosUsuario.value
                        ?.favoritos
                        ?.toSet() ?: emptySet(),
                onAnuncioClick = { anuncio ->
                    val action = PerfilFragmentDirections.actionPerfilToDetalhesAnuncio(anuncio)
                    findNavController().navigate(action)
                },
                onFavoritoClick = { _ ->
                    // Favoritar a partir do perfil não implementado — mostrar toast
                    Toast.makeText(requireContext(), "Favoritar não implementado aqui", Toast.LENGTH_SHORT).show()
                },
            )

        binding.rvMeusAnuncios.layoutManager = GridLayoutManager(context, 2)
        binding.rvMeusAnuncios.adapter = meusAnunciosAdapter
    }

    private fun setupObservers() {
        viewModel.dadosUsuario.observe(viewLifecycleOwner) { usuario ->
            binding.tvNomeUsuario.text = usuario.nome.ifEmpty { "Usuário" }
            binding.tvEmail.text = usuario.email
            binding.tvTelefone.text = usuario.telefone.ifEmpty { "Sem telefone" }
            binding.tvEndereco.text = usuario.endereco.ifEmpty { "Endereço não informado" }
            binding.tvCnpj.text = usuario.cnpj.ifEmpty { "-" }

            val localePtBr =
                java.util.Locale
                    .Builder()
                    .setLanguage("pt")
                    .setRegion("BR")
                    .build()
            val df = SimpleDateFormat("yyyy", localePtBr)
            val membroDesdeStr = df.format(usuario.membroDesde)
            binding.tvMembroDesde.text = getString(br.com.entrevizinhos.R.string.membro_desde_format, membroDesdeStr)

            if (usuario.fotoUrl.isNotEmpty()) {
                if (usuario.fotoUrl.startsWith("data:image")) {
                    try {
                        val base64Clean = usuario.fotoUrl.substringAfter(",")
                        val decodedBytes = Base64.decode(base64Clean, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                        binding.ivPerfilFoto.setImageBitmap(bitmap)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    Glide
                        .with(this)
                        .load(usuario.fotoUrl)
                        .circleCrop()
                        .into(binding.ivPerfilFoto)
                }
            }
        }

        viewModel.meusAnuncios.observe(viewLifecycleOwner) { lista ->
            meusAnunciosAdapter.atualizarLista(
                lista,
                viewModel.dadosUsuario.value
                    ?.favoritos
                    ?.toSet() ?: emptySet(),
            )
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
        bindingNullable = null
    }
}
