package com.cis436.flagiq

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.cis436.flagiq.databinding.FragmentSecondBinding
import com.squareup.picasso.Picasso

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private lateinit var viewModel : MainViewModel
    private var countriesList: List<MainViewModel.Country>? = null
    private var currentIndex = 0 // Keep track of the current question index

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }
        // Observe countries LiveData
        viewModel.countries.observe(viewLifecycleOwner, { countries ->
            countriesList = countries
            // Display the first question
            displayQuestion()
        })
    }
    private fun displayQuestion() {
        // Check if countriesList is not null and currentIndex is within bounds
        countriesList?.let { countries ->
            if (currentIndex < countries.size) {
                currentIndex = (0..(countries.size - 1)).random()
                val currentCountry = countries[currentIndex]
                // Load flag image using Glide or Picasso (replace the code below)
                // You need to replace "imageView" with the actual ID of your ImageView
                Picasso.get().load(currentCountry.flagUrl).into(binding.imageView)

                // Randomly select a button index to place the correct answer
                val correctButtonIndex = (1..4).random()

                // Set correct answer button text
                when (correctButtonIndex) {
                    1 -> binding.button.text = currentCountry.name
                    2 -> binding.button2.text = currentCountry.name
                    3 -> binding.button3.text = currentCountry.name
                    4 -> binding.button4.text = currentCountry.name
                }

                // Set incorrect answers for other buttons
                for (i in 1..4) {
                    if (i != correctButtonIndex) {
                        val randomIndex = (0 until countries.size).random()
                        val incorrectCountry = countries[randomIndex]
                        when (i) {
                            1 -> binding.button.text = incorrectCountry.name
                            2 -> binding.button2.text = incorrectCountry.name
                            3 -> binding.button3.text = incorrectCountry.name
                            4 -> binding.button4.text = incorrectCountry.name
                        }
                    }
                }

                binding.button.setOnClickListener { checkAnswer(binding.button) }
                binding.button2.setOnClickListener { checkAnswer(binding.button2) }
                binding.button3.setOnClickListener { checkAnswer(binding.button3) }
                binding.button4.setOnClickListener { checkAnswer(binding.button4) }

                // Increment currentIndex for the next question
                currentIndex++
            } else {
                // All questions have been displayed
                // You can navigate to the next destination or handle quiz completion here
//                findNavController().navigate(R.id.action_SecondFragment_to_QuizCompletedFragment)
            }
        }
    }

    private fun checkAnswer(button: Button) {
        val selectedCountryName = button.text.toString()
        countriesList?.let { countries ->
            val currentCountry = countries[currentIndex - 1] // Adjust index by -1 due to 0-based indexing
            if (selectedCountryName == currentCountry.name) {
                // Correct answer
                Toast.makeText(requireContext(), "Your answer is correct!", Toast.LENGTH_SHORT).show()
                // Display next question
                displayQuestion()
            } else {
                // Incorrect answer
                // Handle incorrect answer (e.g., display message, navigate to a different fragment)
                Toast.makeText(requireContext(), "Wrong answer! Game over", Toast.LENGTH_SHORT).show()
                binding.button.isEnabled = false
                binding.button2.isEnabled = false
                binding.button3.isEnabled = false
                binding.button4.isEnabled = false

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}