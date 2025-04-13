package com.example.bmicalculator

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var currentWeight = 0
    private var currentAge = 0
    private var currentHeight = 100
    private var selectedGender: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get user's name from intent and update greeting
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

            val bmiIntPart = bmi.toInt()
            val bmiDecimalPart = "%.2f".format(bmi - bmiIntPart).substring(1) // keeps ".54"

            tvBmiValue.text = bmiIntPart.toString()
            tvBmiDecimal.text = bmiDecimalPart
            tvBmiCategory.text = bmiCategory

            val dialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create()

            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            okButton.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }
    }
}
