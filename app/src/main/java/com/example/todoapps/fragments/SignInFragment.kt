package com.example.todoapps.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.todoapps.R
import com.example.todoapps.databinding.FragmentSignInBinding
import com.google.firebase.auth.FirebaseAuth

class SignInFragment : Fragment() {

    private lateinit var navController: NavController
    private lateinit var binding: FragmentSignInBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Firebase Auth and NavController
        auth = FirebaseAuth.getInstance()
        navController = Navigation.findNavController(view)

        // Navigate to SignUpFragment when "Sign Up" text is clicked
        binding.singUpText.setOnClickListener {
            navController.navigate(R.id.action_signInFragment_to_signUpFragment)
        }

        // Handle login button click
        binding.btn.setOnClickListener {
            val email = binding.signInEmail.text.toString().trim()
            val password = binding.signInPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                loginUser(email, password)
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Navigate to HomeFragment upon successful login
                    Toast.makeText(requireContext(), "Login successful!", Toast.LENGTH_SHORT).show()
                    navController.navigate(R.id.action_signInFragment_to_homeFragment)
                } else {
                    // Display error message
                    Toast.makeText(
                        requireContext(),
                        "Login failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    override fun onStart() {
        super.onStart()
        // Automatically navigate to HomeFragment if the user is already logged in
        val currentUser = auth.currentUser
        if (currentUser != null) {
            navController.navigate(R.id.action_signInFragment_to_homeFragment)
        }
    }
}
