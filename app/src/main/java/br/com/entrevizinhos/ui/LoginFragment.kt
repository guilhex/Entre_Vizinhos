package br.com.entrevizinhos.ui

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import br.com.entrevizinhos.R // <--- ESSE IMPORT É OBRIGATÓRIO PARA O VERMELHO SUMIR
import br.com.entrevizinhos.data.repository.AuthRepository
import br.com.entrevizinhos.databinding.FragmentLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val authRepository = AuthRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        // Se já estiver logado, vai direto pro Feed
        if (authRepository.getCurrentUser() != null) {
            findNavController().navigate(R.id.action_login_to_feed)
            return
        }

        // Configuração do Google
        val gso =
            GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        val googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        val launcherGoogle =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    try {
                        val conta = task.getResult(ApiException::class.java)
                        val credencial = GoogleAuthProvider.getCredential(conta.idToken, null)

                        // Chama o repositório e aguarda o sucesso
                        authRepository.loginComGoogle(credencial) { sucesso ->
                            if (sucesso) {
                                Toast.makeText(context, "Bem-vindo!", Toast.LENGTH_SHORT).show()
                                // Navega para o Feed
                                findNavController().navigate(R.id.action_login_to_feed)
                            } else {
                                Toast.makeText(context, "Falha no Login", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: ApiException) {
                        Toast.makeText(context, "Erro Google: ${e.statusCode}", Toast.LENGTH_LONG).show()
                    }
                }
            }

        binding.btnGoogleLogin.setOnClickListener {
            launcherGoogle.launch(googleSignInClient.signInIntent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
