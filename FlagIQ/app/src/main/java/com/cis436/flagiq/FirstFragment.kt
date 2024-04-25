package com.cis436.flagiq

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.cis436.flagiq.databinding.FragmentFirstBinding
import kotlin.properties.Delegates

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private lateinit var viewModel : MainViewModel
    private var score : Int = 0

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        viewModel.fetchCountryData()
        binding.buttonFirst.setOnClickListener {
            binding.buttonFirst.alpha = 0.5f

            // Delay action for a short duration (e.g., 200 milliseconds)
            Handler(Looper.getMainLooper()).postDelayed({
                // Restore opacity
                binding.buttonFirst.alpha = 1.0f

                // Navigate to the desired fragment
                findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)

                // Dismiss the dialog
            }, 200)
        }
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        score = sharedPreferences.getInt("score", 0)
        binding.highScore.text = score.toString()
        binding.pbScore.setProgress(score, true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}