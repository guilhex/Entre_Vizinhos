package br.com.entrevizinhos.ui

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import br.com.entrevizinhos.R
import br.com.entrevizinhos.data.repository.AuthRepository
import br.com.entrevizinhos.databinding.FragmentLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

/**
 * Fragment responsável pela tela de login com Google e acesso como visitante
 */
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val authRepository = AuthRepository() // Repository de autenticação

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

        // Redireciona se já estiver logado
        if (authRepository.getCurrentUser() != null) {
            findNavController().navigate(R.id.action_login_to_feed)
            return
        }

        // Configura Google Sign-In
        val gso =
            GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        val googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        // Launcher para resultado do Google Sign-In
        val launcherGoogle =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    try {
                        val conta = task.getResult(ApiException::class.java)
                        val credencial = GoogleAuthProvider.getCredential(conta.idToken, null)

                        // Executa login em corrotina
                        lifecycleScope.launch {
                            val sucesso = authRepository.loginComGoogle(credencial)

                            if (sucesso) {
                                Toast.makeText(context, "Bem-vindo!", Toast.LENGTH_SHORT).show()
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

        // Configura listeners dos botões
        binding.btnGoogleLogin.setOnClickListener {
            launcherGoogle.launch(googleSignInClient.signInIntent)
        }

        binding.btnGuestLogin.setOnClickListener {
            findNavController().navigate(R.id.feedFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
