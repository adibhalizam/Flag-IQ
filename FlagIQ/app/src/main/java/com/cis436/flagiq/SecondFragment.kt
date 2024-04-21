package com.cis436.flagiq

import android.content.Context
import android.os.Bundle
import android.util.Log
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
    private var countriesList2: List<MainViewModel.Country>? = null
    private var currentIndex = 0 // Keep track of the current question index
    private var correctCountry : String = ""
    private var currentScore : Int = 0

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
//        viewModel.countries.observe(viewLifecycleOwner) { countries ->
//            countriesList = countries
//            displayQuestion()
//            // Display the first question
//        }
        countriesList = viewModel.countries.value
        countriesList2 = viewModel.countries.value
        displayQuestion()

    }
    private fun displayQuestion() {

        // Check if countriesList is not null and currentIndex is within bounds
        countriesList?.let { countries ->
            if(countries.size == 0){
                Toast.makeText(requireContext(), "Congratulations Flag Master! You solved all questions", Toast.LENGTH_SHORT).show()
            }
            else if (countries.size > 0) {
                currentIndex = (0 until countries.size).random()
                val currentCountry = countries[currentIndex]
                Log.e("Country", "Name: ${countries[currentIndex].name}")
                // Load flag image using Glide or Picasso (replace the code below)
                // You need to replace "imageView" with the actual ID of your ImageView
                Picasso.get().load(currentCountry.flagUrl).into(binding.imageView)
                correctCountry = currentCountry.name
                // Randomly select a button index to place the correct answer
                val correctButtonIndex = (1..4).random()
                viewModel.removeCountry(currentIndex)
                Log.e("e","${viewModel.countries.value?.size}")
                // Set correct answer button text
                when (correctButtonIndex) {
                    1 -> binding.button.text = currentCountry.name
                    2 -> binding.button2.text = currentCountry.name
                    3 -> binding.button3.text = currentCountry.name
                    4 -> binding.button4.text = currentCountry.name
                }

                var updatedList = countriesList2?.toMutableList()!!
                updatedList.remove(currentCountry)

                // Set incorrect answers for other buttons
                for (i in 1..4) {
                    if (i != correctButtonIndex) {
                        var randomIndex = (0 until updatedList?.size!!).random()
                        var incorrectCountry = updatedList!![randomIndex]
                        when (i) {
                            1 -> binding.button.text = incorrectCountry.name
                            2 -> binding.button2.text = incorrectCountry.name
                            3 -> binding.button3.text = incorrectCountry.name
                            4 -> binding.button4.text = incorrectCountry.name
                        }
                        updatedList.removeAt(randomIndex)
                    }
                }

                binding.button.setOnClickListener { checkAnswer(binding.button) }
                binding.button2.setOnClickListener { checkAnswer(binding.button2) }
                binding.button3.setOnClickListener { checkAnswer(binding.button3) }
                binding.button4.setOnClickListener { checkAnswer(binding.button4) }

            } else {
                // All questions have been displayed
                // You can navigate to the next destination or handle quiz completion here
//                findNavController().navigate(R.id.action_SecondFragment_to_QuizCompletedFragment)
            }
            countriesList = viewModel.countries.value

        }
    }

    private fun checkAnswer(button: Button) {

            val selectedCountryName = button.text.toString()
            if (selectedCountryName == correctCountry) {
                // Correct answer
                Toast.makeText(requireContext(), "Your answer is correct!", Toast.LENGTH_SHORT).show()
                currentScore++
                binding.score.text = currentScore.toString()
                // Display next question
                displayQuestion()
            } else {
                // Incorrect answer
                // Handle incorrect answer (e.g., display message, navigate to a different fragment)
                Toast.makeText(requireContext(), "Wrong answer! Game over", Toast.LENGTH_SHORT).show()
                val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                val highScore = sharedPreferences.getInt("score", 0)
                if(currentScore > highScore) {
                    val editor = sharedPreferences.edit()
                    editor.putInt("score", currentScore)
                    editor.apply()
                }
                binding.button.isEnabled = false
                binding.button2.isEnabled = false
                binding.button3.isEnabled = false
                binding.button4.isEnabled = false

            }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}