package com.example.checkanswer

import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import com.example.checkanswer.databinding.ActivityMainBinding
import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var operand1: Int = 0
    private var operand2: Int = 0
    private var operator: Char = '+'
    private var correctAnswer: Double = 0.0
    private var isCorrectAnswerGiven: Boolean = false
    private var startTime: Long = 0
    private var endTime: Long = 0
    private var correctChoices: Int = 0
    private var wrongChoices: Int = 0
    private var totalExamples: Int = 0
    private var timeList: MutableList<Long> = mutableListOf()
    private val decimalFormat = DecimalFormat("#.##")

    private var timer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRight.isEnabled = false
        binding.btnWrong.isEnabled = false

        binding.btnStart.setOnClickListener {
            generateExample()
        }

        binding.btnRight.setOnClickListener {
            endTime = System.currentTimeMillis()
            stopTimer()
            isCorrectAnswerGiven = true
            checkAnswer()
        }

        binding.btnWrong.setOnClickListener {
            endTime = System.currentTimeMillis()
            stopTimer()
            isCorrectAnswerGiven = false
            checkAnswer()
        }
    }

    private fun generateExample() {
        operand1 = Random().nextInt(90) + 10 // Range 10-99
        operand2 = Random().nextInt(90) + 10 // Range 10-99
        val operators = arrayOf('+', '-', '*', '/')
        operator = operators[Random().nextInt(operators.size)]

        val shouldBeCorrect = Random().nextBoolean()

        var result: Double = when (operator) {
            '+' -> (operand1 + operand2).toDouble()
            '-' -> (operand1 - operand2).toDouble()
            '*' -> (operand1 * operand2).toDouble()
            '/' -> {
                val divisionResult = operand1.toDouble() / operand2.toDouble()
                if (divisionResult % 1.0 != 0.0) {
                    decimalFormat.format(divisionResult).toDouble()
                } else {
                    divisionResult
                }
            }
            else -> 0.0
        }

        correctAnswer = result
        var displayedResult: Double = result

        if (!shouldBeCorrect) {
            do {
                displayedResult = (Random().nextInt(200) - 100).toDouble()
                if (operator == '/') {
                    displayedResult = decimalFormat.format(displayedResult).toDouble()
                }
            } while (displayedResult == result)
        }

        binding.txtFirstOperand.text = operand1.toString()
        binding.txtOperation.text = operator.toString()
        binding.txtTwoOperand.text = operand2.toString()
        binding.txtResult.text = decimalFormat.format(displayedResult).toString()

        binding.btnRight.isEnabled = true
        binding.btnWrong.isEnabled = true
        binding.txtCorrectNotCorrect.text = " "
        startTime = System.currentTimeMillis()
        startTimer()
        totalExamples++
    }

    private fun startTimer() {
        startTime = System.currentTimeMillis()

        timer = object : CountDownTimer(Long.MAX_VALUE, 10) {
            override fun onTick(millisUntilFinished: Long) {
                val elapsedTime = System.currentTimeMillis() - startTime
                binding.textView.text = "ВРЕМЯ: ${formatTime(elapsedTime)}"
            }

            override fun onFinish() {
            }
        }.start()
    }

    private fun stopTimer() {
        timer?.cancel()
    }

    private fun checkAnswer() {
        binding.btnRight.isEnabled = false
        binding.btnWrong.isEnabled = false

        val timeTaken = endTime - startTime
        timeList.add(timeTaken)

        val isActuallyCorrect = binding.txtResult.text.toString().toDouble() == correctAnswer

        val userIsCorrect = (isCorrectAnswerGiven && isActuallyCorrect) || (!isCorrectAnswerGiven && !isActuallyCorrect)

        if (userIsCorrect) {
            binding.txtCorrectNotCorrect.text = "ПРАВИЛЬНО"
            correctChoices++
        } else {
            binding.txtCorrectNotCorrect.text = "НЕ ПРАВИЛЬНО"
            wrongChoices++
        }

        updateStatistics(timeTaken)
    }

    private fun updateStatistics(timeTaken: Long) {
        val percentageCorrect = if (totalExamples > 0) {
            decimalFormat.format((correctChoices.toDouble() / totalExamples.toDouble()) * 100)
        } else {
            "0.00"
        }

        val minTime = timeList.minOrNull() ?: 0
        val maxTime = timeList.maxOrNull() ?: 0
        val averageTime = if (timeList.isNotEmpty()) {
            timeList.average()
        } else {
            0.0
        }

        binding.txtAllExamples.text = totalExamples.toString()
        binding.txtNumberRight.text = correctChoices.toString()
        binding.txtNumberWrong.text = wrongChoices.toString()
        binding.txtPercentageCorrectAnswers.text = "$percentageCorrect%"
        binding.txtTimeMin.text = formatTime(minTime)
        binding.txtTimeMax.text = formatTime(maxTime)
        binding.txtTimeAverage.text = formatTime(averageTime.toLong())
    }


    private fun formatTime(millis: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
        val milliseconds = millis % 1000 / 10  // Get the first two digits of milliseconds
        return String.format("%02d:%02d.%02d", minutes, seconds, milliseconds)
    }
}