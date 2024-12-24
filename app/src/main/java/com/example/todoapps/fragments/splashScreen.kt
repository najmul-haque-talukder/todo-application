package com.example.todoapps.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.todoapps.R

class SplashScreen : Fragment() {

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the splash screen layout
        return inflater.inflate(R.layout.fragment_splash_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize navigation controller
        navController = Navigation.findNavController(view)

        // Delay for 2 seconds, then navigate to the next screen
        Handler(Looper.getMainLooper()).postDelayed({
            navController.navigate(R.id.action_splashScreen_to_signInFragment)
        }, 1000) // Delay increased to 2 seconds
    }
}
