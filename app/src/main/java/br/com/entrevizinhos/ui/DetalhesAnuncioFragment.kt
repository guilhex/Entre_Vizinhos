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

/**
 * Fragment que exibe detalhes completos de um anúncio
 * Inclui carrossel de fotos, informações do vendedor e ações
 */
class DetalhesAnuncioFragment : Fragment() {
    private var bindingNullable: FragmentDetalhesAnuncioBinding? = null // Binding nullável
    private val binding get() = bindingNullable!! // Acesso seguro

    private val args: DetalhesAnuncioFragmentArgs by navArgs() // Argumentos da navegação

    // ViewModel local para dados do vendedor
    private val perfilViewModel: PerfilViewModel by viewModels()

    // ViewModel compartilhado para gerenciar favoritos
    private val anuncioViewModel: LerAnuncioViewModel by activityViewModels()

    private var vendedorAtual: Usuario? = null // Cache dos dados do vendedor

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

        val anuncio = args.anuncio // Anúncio recebido via navegação

        // Configuração inicial da tela
        setupDados(anuncio) // Preenche informações básicas
        setupChipsEntrega(anuncio.entrega) // Cria chips de entrega
        setupChipsPagamento(anuncio.formasPagamento) // Cria chips de pagamento
        setupListeners(anuncio) // Configura ações dos botões
        setupToolbar() // Configura barra superior
        setupObservers(anuncio) // Observa mudanças nos dados
    }

    // Configura toolbar com botão de voltar
    private fun setupToolbar() {
        binding.toolbarDetalhes.setNavigationOnClickListener { 
            findNavController().popBackStack() // Volta para tela anterior
        }
    }

    // Configura carrossel de fotos do anúncio
    private fun setupFotos(fotos: List<String>) {
        if (fotos.isNotEmpty()) {
            binding.vpFotos.adapter = FotosPagerAdapter(fotos) // ViewPager2 com fotos
        }
    }

    // Preenche todas as informações do anúncio na tela
    private fun setupDados(anuncio: Anuncio) {
        binding.apply {
            tvDetalheTitulo.text = anuncio.titulo // Nome do produto
            // Formata preço com localização brasileira
            val precoFormatado = String.format(java.util.Locale.getDefault(), "%.2f", anuncio.preco)
            tvDetalhePreco.text = getString(br.com.entrevizinhos.R.string.preco_format, precoFormatado)
            tvDetalheDescricao.text = anuncio.descricao // Detalhes do item
            tvDetalheCategoria.text = anuncio.categoria // Classificação
            tvDetalheLocal.text = anuncio.cidade // Localização
            // Configura carrossel se houver fotos
            if (anuncio.fotos.isNotEmpty()) {
                setupFotos(anuncio.fotos)
            }
        }
    }

    // Cria chip visual para modalidade de entrega
    private fun setupChipsEntrega(entrega: String) {
        binding.chipGroupEntregaDetalhes.removeAllViews() // Limpa chips anteriores
        if (entrega.isNotEmpty()) {
            val chip = Chip(requireContext()) // Cria novo chip
            chip.text = entrega // Define texto (ex: "Retirada", "Entrega")
            chip.isClickable = false // Apenas visual, não interativo
            chip.isCheckable = false // Não pode ser selecionado
            // Aplica cores temáticas
            chip.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green_chip_background))
            chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.green_chip_text))
            binding.chipGroupEntregaDetalhes.addView(chip) // Adiciona ao grupo
        }
    }

    // Cria chips visuais para formas de pagamento aceitas
    private fun setupChipsPagamento(formasPagamento: String) {
        binding.chipGroupPagamentoDetalhes.removeAllViews() // Limpa chips anteriores
        if (formasPagamento.isNotEmpty()) {
            // Separa string por vírgulas (ex: "PIX, Dinheiro, Cartão")
            val pagamentos = formasPagamento.split(",").map { it.trim() }
            for (pagamento in pagamentos) {
                val chip = Chip(requireContext()) // Cria chip para cada forma
                chip.text = pagamento // Define texto do pagamento
                chip.isClickable = false // Apenas informativo
                chip.isCheckable = false // Não selecionável
                // Aplica estilo visual consistente
                chip.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green_chip_background))
                chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.green_chip_text))
                binding.chipGroupPagamentoDetalhes.addView(chip) // Adiciona ao grupo
            }
        }
    }

    // Configura ações dos botões da tela
    private fun setupListeners(anuncio: Anuncio) {
        // Botão de contato via WhatsApp (funcionalidade futura)
        binding.btnWhatsapp.setOnClickListener {
            Toast.makeText(context, "Chat será implementado!", Toast.LENGTH_SHORT).show()
        }

        // Botão para denunciar anúncio (funcionalidade futura)
        binding.btnDenunciar.setOnClickListener {
            Toast.makeText(context, "Denúncia será implementada!", Toast.LENGTH_SHORT).show()
        }

        // Ícone de favorito - adiciona/remove da coleção
        binding.ivDetalheFavorito.setOnClickListener {
            anuncioViewModel.onFavoritoClick(anuncio.id) // Alterna estado via ViewModel
            // Feedback imediato para o usuário
            Toast.makeText(context, "Favoritos atualizado!", Toast.LENGTH_SHORT).show()
        }
    }

    // Configura observadores para reagir a mudanças nos dados
    private fun setupObservers(anuncio: Anuncio) {
        // Observer 1: Dados do vendedor
        perfilViewModel.vendedor.observe(viewLifecycleOwner) { usuarioVendedor ->
            usuarioVendedor?.let { usuario ->
                binding.tvNomeVendedor.text = usuario.nome // Nome do vendedor
                binding.tvLocalVendedor.text = usuario.endereco // Localização
                vendedorAtual = usuario // Cache local
            }
        }

        // Observer 2: Estado dos favoritos
        // Atualiza visual do ícone quando favoritos mudam
        anuncioViewModel.favoritosIds.observe(viewLifecycleOwner) { favoritosSet ->
            val estaFavoritado = anuncio.id in favoritosSet // Verifica se está favoritado
            atualizarIconeFavorito(estaFavoritado) // Atualiza cor do ícone
        }

        // Inicia carregamento dos dados do vendedor
        perfilViewModel.carregarVendedor(anuncio.vendedorId)
    }

    // Atualiza visual do ícone de favorito baseado no estado
    private fun atualizarIconeFavorito(favoritado: Boolean) {
        if (favoritado) {
            // Estado favoritado: ícone vermelho
            binding.ivDetalheFavorito.imageTintList = ColorStateList.valueOf(Color.RED)
            // Alternativa: trocar ícone por coração preenchido
            // binding.ivDetalheFavorito.setImageResource(R.drawable.ic_heart_filled)
        } else {
            // Estado normal: ícone cinza
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
