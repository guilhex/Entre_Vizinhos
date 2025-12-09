package br.com.entrevizinhos.ui

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.entrevizinhos.R
import br.com.entrevizinhos.databinding.FragmentDetalhesAnuncioBinding
import br.com.entrevizinhos.model.Anuncio
import br.com.entrevizinhos.model.Usuario
import br.com.entrevizinhos.ui.adapter.FotosPagerAdapter
import br.com.entrevizinhos.viewmodel.LerAnuncioViewModel
import br.com.entrevizinhos.viewmodel.PerfilViewModel
import com.google.android.material.chip.Chip

class DetalhesAnuncioFragment : Fragment() {
    private var bindingNullable: FragmentDetalhesAnuncioBinding? = null
    private val binding get() = bindingNullable!!

    private val args: DetalhesAnuncioFragmentArgs by navArgs()

    // ViewModel para buscar dados do vendedor
    private val perfilViewModel: PerfilViewModel by viewModels()

    // [NOVO] ViewModel para gerenciar favoritos (Compartilhado com a Activity)
    private val anuncioViewModel: LerAnuncioViewModel by activityViewModels()

    private var vendedorAtual: Usuario? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        bindingNullable = FragmentDetalhesAnuncioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        val anuncio = args.anuncio

        setupDados(anuncio)
        setupChipsEntrega(anuncio.entrega)
        setupChipsPagamento(anuncio.formasPagamento)

        // [ALTERADO] Passamos o anúncio para configurar o clique do favorito
        setupListeners(anuncio)

        setupToolbar()
        setupObservers(anuncio)
    }

    // ... (setupToolbar, setupFotos, setupDados, setupChips... mantêm-se iguais) ...
    private fun setupToolbar() {
        binding.toolbarDetalhes.setNavigationOnClickListener { findNavController().popBackStack() }
    }

    private fun setupFotos(fotos: List<String>) {
        if (fotos.isNotEmpty()) {
            binding.vpFotos.adapter = FotosPagerAdapter(fotos)
        }
    }

    private fun setupDados(anuncio: Anuncio) {
        binding.apply {
            tvDetalheTitulo.text = anuncio.titulo
            val precoFormatado = String.format(java.util.Locale.getDefault(), "%.2f", anuncio.preco)
            tvDetalhePreco.text = getString(br.com.entrevizinhos.R.string.preco_format, precoFormatado)
            tvDetalheDescricao.text = anuncio.descricao
            tvDetalheCategoria.text = anuncio.categoria
            tvDetalheLocal.text = anuncio.cidade
            if (anuncio.fotos.isNotEmpty()) {
                setupFotos(anuncio.fotos)
            }
        }
    }

    private fun setupChipsEntrega(entrega: String) {
        binding.chipGroupEntregaDetalhes.removeAllViews()
        if (entrega.isNotEmpty()) {
            val chip = Chip(requireContext())
            chip.text = entrega
            chip.isClickable = false
            chip.isCheckable = false
            chip.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green_chip_background))
            chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.green_chip_text))
            binding.chipGroupEntregaDetalhes.addView(chip)
        }
    }

    private fun setupChipsPagamento(formasPagamento: String) {
        binding.chipGroupPagamentoDetalhes.removeAllViews()
        if (formasPagamento.isNotEmpty()) {
            val pagamentos = formasPagamento.split(",").map { it.trim() }
            for (pagamento in pagamentos) {
                val chip = Chip(requireContext())
                chip.text = pagamento
                chip.isClickable = false
                chip.isCheckable = false
                chip.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green_chip_background))
                chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.green_chip_text))
                binding.chipGroupPagamentoDetalhes.addView(chip)
            }
        }
    }

    // [CORREÇÃO] Recebe o anúncio para saber qual ID favoritar
    private fun setupListeners(anuncio: Anuncio) {
        binding.btnWhatsapp.setOnClickListener {
            Toast.makeText(context, "Chat será implementado!", Toast.LENGTH_SHORT).show()
        }

        binding.btnDenunciar.setOnClickListener {
            Toast.makeText(context, "Denúncia será implementada!", Toast.LENGTH_SHORT).show()
        }

        // [NOVO] Lógica do clique no coração
        binding.ivDetalheFavorito.setOnClickListener {
            anuncioViewModel.onFavoritoClick(anuncio.id)
            // Feedback visual imediato (opcional, o observer já fará isso)
            Toast.makeText(context, "Favoritos atualizado!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupObservers(anuncio: Anuncio) {
        // Observer do Vendedor (mantido)
        perfilViewModel.vendedor.observe(viewLifecycleOwner) { usuarioVendedor ->
            usuarioVendedor?.let { usuario ->
                binding.tvNomeVendedor.text = usuario.nome
                binding.tvLocalVendedor.text = usuario.endereco
                vendedorAtual = usuario
            }
        }

        // [NOVO] Observer dos Favoritos
        // Verifica se o ID deste anúncio está na lista de favoritos e pinta o coração
        anuncioViewModel.favoritosIds.observe(viewLifecycleOwner) { favoritosSet ->
            val estaFavoritado = anuncio.id in favoritosSet
            atualizarIconeFavorito(estaFavoritado)
        }

        perfilViewModel.carregarVendedor(anuncio.vendedorId)
    }

    // [NOVO] Método auxiliar para pintar o coração
    private fun atualizarIconeFavorito(favoritado: Boolean) {
        if (favoritado) {
            // Se favorito: Cor Vermelha (ou a cor de destaque do seu app)
            binding.ivDetalheFavorito.imageTintList = ColorStateList.valueOf(Color.RED)
            // Se quiser mudar o ícone para um coração preenchido, usaria:
            // binding.ivDetalheFavorito.setImageResource(R.drawable.ic_heart_filled)
        } else {
            // Se não favorito: Cor Cinza original
            binding.ivDetalheFavorito.imageTintList =
                ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), R.color.gray_text),
                )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingNullable = null
    }
}
