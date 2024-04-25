package com.cis436.flagiq

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.cis436.flagiq.databinding.FragmentSecondBinding
import com.shashank.sony.fancytoastlib.FancyToast
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
    private var correctButtonIndex : Int = 0

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
        binding.buttonSecond.isEnabled = true
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.return_dialog)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val confirmBtn = dialog.findViewById<Button>(R.id.confirm_button)
        val cancelBtn = dialog.findViewById<Button>(R.id.cancel_btn)

        confirmBtn.setOnClickListener {
            // Reduce opacity
            confirmBtn.alpha = 0.5f

            // Delay action for a short duration (e.g., 200 milliseconds)
            Handler(Looper.getMainLooper()).postDelayed({
                // Restore opacity
                confirmBtn.alpha = 1.0f

                // Navigate to the desired fragment
                findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)

                // Dismiss the dialog
                dialog.dismiss()
            }, 20)
        }

        cancelBtn.setOnClickListener{
            cancelBtn.alpha = 0.5f

            // Delay action for a short duration (e.g., 200 milliseconds)
            Handler(Looper.getMainLooper()).postDelayed({
                // Restore opacity
                cancelBtn.alpha = 1.0f

                // Navigate to the desired fragment
                // Dismiss the dialog
                dialog.dismiss()
            }, 20)
        }

        binding.buttonSecond.setOnClickListener {
            dialog.show()
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
            if(countries.isEmpty()){
                val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                val highScore = sharedPreferences.getInt("score", 0)
                val editor = sharedPreferences.edit()
                editor.putInt("score", currentScore)
                editor.apply()

                val dialog = Dialog(requireContext())
                dialog.setContentView(R.layout.win_dialog)
                dialog.setCanceledOnTouchOutside(false)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                val confirmBtn = dialog.findViewById<Button>(R.id.confirm_button)

                confirmBtn.setOnClickListener {
                    // Reduce opacity
                    confirmBtn.alpha = 0.5f
                    // Delay action for a short duration (e.g., 200 milliseconds)
                    Handler(Looper.getMainLooper()).postDelayed({
                        // Restore opacity
                        confirmBtn.alpha = 1.0f

                        // Navigate to the desired fragment
                        findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)

                        // Dismiss the dialog
                        dialog.dismiss()
                    }, 20)
                }
                dialog.show()


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
                correctButtonIndex = (1..4).random()
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
                FancyToast.makeText(requireContext(), "Your answer is correct!", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS,true).show()
                currentScore++
                binding.score.text = currentScore.toString()
                // Display next question
                displayQuestion()
            } else {
                binding.buttonSecond.isEnabled = false
                // Incorrect answer
                // Handle incorrect answer (e.g., display message, navigate to a different fragment)
                FancyToast.makeText(requireContext(), "Wrong answer! Game over", FancyToast.LENGTH_SHORT, FancyToast.ERROR,true).show()
                val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                val highScore = sharedPreferences.getInt("score", 0)
                if(currentScore > highScore) {
                    val editor = sharedPreferences.edit()
                    editor.putInt("score", currentScore)
                    editor.apply()
                }
                binding.button.isEnabled = false
                binding.button.setBackgroundResource(R.drawable.disable_button)
                binding.button.setTextColor(Color.rgb(169, 169, 169))
                binding.button2.isEnabled = false
                binding.button2.setBackgroundResource(R.drawable.disable_button)
                binding.button2.setTextColor(Color.rgb(169, 169, 169))
                binding.button3.isEnabled = false
                binding.button3.setBackgroundResource(R.drawable.disable_button)
                binding.button3.setTextColor(Color.rgb(169, 169, 169))
                binding.button4.isEnabled = false
                binding.button4.setBackgroundResource(R.drawable.disable_button)
                binding.button4.setTextColor(Color.rgb(169, 169, 169))

                when (correctButtonIndex) {
                    1 -> {
                        binding.button.setBackgroundResource(R.drawable.correct_answer)
                        binding.button.setTextColor(Color.BLACK)
                    }
                    2 -> {
                        binding.button2.setBackgroundResource(R.drawable.correct_answer)
                        binding.button2.setTextColor(Color.BLACK)
                    }
                    3 -> {
                        binding.button3.setBackgroundResource(R.drawable.correct_answer)
                        binding.button3.setTextColor(Color.BLACK)
                    }
                    4 -> {
                        binding.button4.setBackgroundResource(R.drawable.correct_answer)
                        binding.button4.setTextColor(Color.BLACK)
                    }
                }

                val dialog = Dialog(requireContext())
                dialog.setContentView(R.layout.lose_dialog)
                dialog.setCanceledOnTouchOutside(false)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                val confirmBtn = dialog.findViewById<Button>(R.id.confirm_button)

                confirmBtn.setOnClickListener {
                    // Reduce opacity
                    confirmBtn.alpha = 0.5f

                    // Delay action for a short duration (e.g., 200 milliseconds)
                    Handler(Looper.getMainLooper()).postDelayed({
                        // Restore opacity
                        confirmBtn.alpha = 1.0f

                        // Navigate to the desired fragment
                        findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)

                        // Dismiss the dialog
                        dialog.dismiss()
                    }, 280)
                }

                Handler(Looper.getMainLooper()).postDelayed({
                    dialog.show()
                }, 2000)
            }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}