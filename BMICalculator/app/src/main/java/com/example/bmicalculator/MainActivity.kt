package com.example.bmicalculator

import android.animation.ValueAnimator
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class MainActivity : AppCompatActivity() {

    private var currentWeight = 0
    private var currentAge = 0
    private var currentHeight = 100
    private var selectedGender: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Theme switch setup
        val sharedPreferences = getSharedPreferences("themePrefs", MODE_PRIVATE)
        val isNightMode = sharedPreferences.getBoolean("night_mode", false)

        if (isNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        val darkSwitch = findViewById<Switch>(R.id.dswitch)
        darkSwitch.isChecked = isNightMode

        darkSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                sharedPreferences.edit().putBoolean("night_mode", true).apply()
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                sharedPreferences.edit().putBoolean("night_mode", false).apply()
            }
        }

        // Greeting setup
        val greetingText = findViewById<TextView>(R.id.userGreeting)
        val userName = intent.getStringExtra("user_name") ?: "User"
        greetingText.text = "Hello, $userName"

        val weightText = findViewById<TextView>(R.id.currentweight)
        val ageText = findViewById<TextView>(R.id.currentage)
        val heightText = findViewById<TextView>(R.id.Currentselectedheight)
        val heightSeekBar = findViewById<SeekBar>(R.id.heightseakbar)

        val maleLayout = findViewById<RelativeLayout>(R.id.male)
        val femaleLayout = findViewById<RelativeLayout>(R.id.female)

        val decreaseWeight = findViewById<ImageView>(R.id.decreaseweight)
        val increaseWeight = findViewById<ImageView>(R.id.increaseweight)

        val decreaseAge = findViewById<ImageView>(R.id.decreaseage)
        val increaseAge = findViewById<ImageView>(R.id.increaseage)

        val calculateButton = findViewById<Button>(R.id.calculationbutton)

        weightText.text = currentWeight.toString()
        ageText.text = currentAge.toString()
        heightText.text = currentHeight.toString()
        heightSeekBar.max = 250
        heightSeekBar.progress = currentHeight

        heightSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                currentHeight = progress
                heightText.text = currentHeight.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        increaseWeight.setOnClickListener {
            currentWeight++
            weightText.text = currentWeight.toString()
        }

        decreaseWeight.setOnClickListener {
            if (currentWeight > 1) {
                currentWeight--
                weightText.text = currentWeight.toString()
            }
        }

        increaseAge.setOnClickListener {
            currentAge++
            ageText.text = currentAge.toString()
        }

        decreaseAge.setOnClickListener {
            if (currentAge > 1) {
                currentAge--
                ageText.text = currentAge.toString()
            }
        }

        maleLayout.setOnClickListener {
            selectedGender = "Male"
            maleLayout.setBackgroundResource(R.drawable.malefemaleselection)
            femaleLayout.setBackgroundResource(R.drawable.malefemalenotselected)
        }

        femaleLayout.setOnClickListener {
            selectedGender = "Female"
            femaleLayout.setBackgroundResource(R.drawable.malefemaleselection)
            maleLayout.setBackgroundResource(R.drawable.malefemalenotselected)
        }

        calculateButton.setOnClickListener {
            if (selectedGender == null) {
                Toast.makeText(this, "Please select your gender", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (currentWeight == 0 || currentAge == 0) {
                Toast.makeText(this, "Please enter your weight and age", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val heightInMeters = currentHeight / 100.0
            val bmi = currentWeight / (heightInMeters * heightInMeters)

            val bmiCategory = when {
                bmi < 18.5 -> "UNDERWEIGHT"
                bmi in 18.5..24.9 -> "NORMAL BMI"
                bmi in 25.0..29.9 -> "OVERWEIGHT"
                else -> "OBESE"
            }

            val dialogView = LayoutInflater.from(this).inflate(R.layout.bmi_result_popup, null)

            val tvBmiValue = dialogView.findViewById<TextView>(R.id.tvBmiValue)
            val tvBmiDecimal = dialogView.findViewById<TextView>(R.id.tvBmiDecimal)
            val tvBmiCategory = dialogView.findViewById<TextView>(R.id.tvBmiCategory)
            val okButton = dialogView.findViewById<Button>(R.id.okbtn)
            val foregroundProgressBar = dialogView.findViewById<ProgressBar>(R.id.foregroundProgressBar)

            val bmiIntPart = bmi.toInt()
            val bmiDecimalPart = "%.2f".format(bmi - bmiIntPart).substring(1) // keeps ".54"

            tvBmiValue.text = bmiIntPart.toString()
            tvBmiDecimal.text = bmiDecimalPart
            tvBmiCategory.text = bmiCategory

            // Calculate progress percentage based on BMI value
            val progressValue = calculateProgressFromBmi(bmi)

            // Animate the progress bar filling
            val progressAnimator = ValueAnimator.ofInt(0, progressValue)
            progressAnimator.duration = 1500 // Animation duration in milliseconds
            progressAnimator.interpolator = AccelerateDecelerateInterpolator()
            progressAnimator.addUpdateListener { animation ->
                val animatedValue = animation.animatedValue as Int
                foregroundProgressBar.progress = animatedValue
            }

            val dialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create()

            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            okButton.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()

            // Start the animation after dialog shows
            progressAnimator.start()
        }
    }

    /**
     * Calculates a progress percentage (0-100) based on BMI value.
     * BMI scale: Underweight (<18.5), Normal (18.5-24.9), Overweight (25-29.9), Obese (>30)
     */
    private fun calculateProgressFromBmi(bmi: Double): Int {
        return when {
            bmi <= 0 -> 0
            bmi < 15 -> ((bmi / 15) * 25).toInt()  // First quarter (0-25%)
            bmi < 18.5 -> 25 + (((bmi - 15) / 3.5) * 25).toInt()  // Second quarter (25-50%)
            bmi < 25 -> 50 + (((bmi - 18.5) / 6.5) * 25).toInt()  // Third quarter (50-75%)
            bmi < 30 -> 75 + (((bmi - 25) / 5) * 15).toInt()  // Up to 90%
            bmi < 40 -> 90 + (((bmi - 30) / 10) * 10).toInt()  // Last 10%
            else -> 100  // Max out at 100%
        }
    }
}