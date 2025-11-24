package br.com.entrevizinhos.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import br.com.entrevizinhos.databinding.FragmentFeedBinding // O Android vai gerar isso depois do XML

class PerfilFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // AQUI VIRÁ A LÓGICA DA LISTA (RecyclerView) QUE ESTAVA NA MAIN
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}