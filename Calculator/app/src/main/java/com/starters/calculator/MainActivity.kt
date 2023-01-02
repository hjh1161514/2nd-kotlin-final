package com.starters.calculator

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.starters.calculator.databinding.ActivityMainBinding
import com.starters.calculator.util.shortToast
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.ParseException
import kotlin.math.sqrt

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var isFourOperationButtonClicked: Boolean = false
    private var isOtherOperationButtonClicked: Boolean = false
    private var isEqualButtonClicked: Boolean = false

    private var isAvailDivK: Boolean = false
    private var isAvailMinusK: Boolean = false
    private var isAvailPlusK: Boolean = false
    private var isAvailMulK: Boolean = false
    private var numberK: Double = 0.0

    private var currentNumber: Double = 0.0
    private var currentResult: Double = 0.0
    private var memoryPlus: Double = 0.0
    private var memoryMinus: Double = 0.0

    private var historyText = ""
    private var historyOperationText = ""
    private var historyList: ArrayList<String> = ArrayList()

    private var currentOperation = ""
    private var resultList: ArrayList<Double> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        initBtnChangeListener()

        setContentView(binding.root)
    }

    // 버튼 클릭 이벤트
    private fun initBtnChangeListener() {
        with(binding) {
            // 검정색
            initBlackNumberBtnChangeListener(button0)
            initBlackNumberBtnChangeListener(button00)
            initBlackNumberBtnChangeListener(button1)
            initBlackNumberBtnChangeListener(button2)
            initBlackNumberBtnChangeListener(button3)
            initBlackNumberBtnChangeListener(button4)
            initBlackNumberBtnChangeListener(button5)
            initBlackNumberBtnChangeListener(button6)
            initBlackNumberBtnChangeListener(button7)
            initBlackNumberBtnChangeListener(button8)
            initBlackNumberBtnChangeListener(button9)
            initBlackOperatorBtnChangeListener(buttonEqual)
            initBlackOperatorBtnChangeListener(buttonPoint)
            initBlackOperatorBtnChangeListener(buttonPlusMinus)
            initBlackOperatorBtnChangeListener(buttonAddition)
            initBlackOperatorBtnChangeListener(buttonSubtraction)
            initBlackOperatorBtnChangeListener(buttonMultiplication)
            initBlackOperatorBtnChangeListener(buttonDivision)
            initBlackOperatorBtnChangeListener(buttonBackspace)
            initBlackOperatorBtnChangeListener(buttonGt)
            initBlackBtnNothingChangeListener(buttonHms)
            initBlackOperatorBtnChangeListener(buttonRoot)
            initBlackBtnNothingChangeListener(buttonPercentage)

            // 파란색
            initBlueBtnChangeListener(buttonMemoryAdd)
            initBlueBtnChangeListener(buttonMemoryClear)
            initBlueBtnChangeListener(buttonMemoryRecall)
            initBlueBtnChangeListener(buttonMemorySubtract)

            // 빨간색
            initRedBtnChangeListener(buttonAc)
            initRedBtnChangeListener(buttonC)
        }
    }

    private fun availableK() {
        isAvailDivK = false
        isAvailMinusK = false
        isAvailPlusK = false
        isAvailMulK = false
    }

    // 검정색 버튼 색상, 글자 크기 변경
    @SuppressLint("ClickableViewAccessibility")
    private fun initBlackOperatorBtnChangeListener(btn: Button) {
        with(binding) {
            btn.setOnTouchListener { _, motionEvent ->
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        btn.textSize = 10F
                        btn.setBackgroundResource(R.drawable.rectangle_fill_light_black)
                        when (btn) {
                            buttonEqual -> {
                                calculateEqual()
                            }
                            buttonPoint -> {
                                calculatePoint()
                                availableK()
                            }
                            buttonPlusMinus -> {
                                calculateSign()
                                availableK()
                            }
                            buttonAddition -> {
                                calculateFourOperation(" + ")
                                if (isAvailPlusK) {
                                    binding.tvK.visibility = View.VISIBLE
                                    numberK =
                                        formatStringToDouble(binding.numberCurrent.text.toString())
                                } else {
                                    binding.tvK.visibility = View.INVISIBLE
                                }
                                isAvailPlusK = true
                                binding.tvPlus.visibility = View.VISIBLE
                                binding.tvMinus.visibility = View.INVISIBLE
                                binding.tvMul.visibility = View.INVISIBLE
                                binding.tvDiv.visibility = View.INVISIBLE
                            }
                            buttonSubtraction -> {
                                if (isAvailMinusK) {
                                    binding.tvK.visibility = View.VISIBLE
                                } else {
                                    binding.tvK.visibility = View.INVISIBLE
                                }
                                isAvailMinusK = true
                                calculateFourOperation(" - ")
                                binding.tvPlus.visibility = View.INVISIBLE
                                binding.tvMinus.visibility = View.VISIBLE
                                binding.tvMul.visibility = View.INVISIBLE
                                binding.tvDiv.visibility = View.INVISIBLE
                            }
                            buttonMultiplication -> {
                                if (isAvailMulK) {
                                    binding.tvK.visibility = View.VISIBLE
                                } else {
                                    binding.tvK.visibility = View.INVISIBLE
                                }
                                isAvailMulK = true
                                calculateFourOperation(" × ")
                                binding.tvPlus.visibility = View.INVISIBLE
                                binding.tvMinus.visibility = View.INVISIBLE
                                binding.tvMul.visibility = View.VISIBLE
                                binding.tvDiv.visibility = View.INVISIBLE
                            }
                            buttonDivision -> {
                                if (isAvailDivK) {
                                    binding.tvK.visibility = View.VISIBLE
                                } else {
                                    binding.tvK.visibility = View.INVISIBLE
                                }
                                isAvailDivK = true
                                calculateFourOperation(" ÷ ")
                                binding.tvPlus.visibility = View.INVISIBLE
                                binding.tvMinus.visibility = View.INVISIBLE
                                binding.tvMul.visibility = View.INVISIBLE
                                binding.tvDiv.visibility = View.VISIBLE
                            }
                            buttonBackspace -> {
                                calculateBack()
                                availableK()
                            }
                            buttonRoot -> {
                                calculateRoot()
                                availableK()
                            }
                            buttonGt -> {
                                calculateGt()
                                availableK()
                            }
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        btn.textSize = 14F
                        btn.setBackgroundResource(R.drawable.rectangle_fill_black)
                    }
                }
                true
            }
        }
    }

    // 검정색 숫자 버튼 색상, 글자 크기 변경
    @SuppressLint("ClickableViewAccessibility")
    private fun initBlackNumberBtnChangeListener(btn: Button) {
        with(binding) {
            btn.setOnTouchListener { _, motionEvent ->
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        btn.textSize = 10F
                        btn.setBackgroundResource(R.drawable.rectangle_fill_light_black)
                        when (btn) {
                            button0 -> onNumberButtonClick("0")
                            button00 -> onNumberButtonClick("00")
                            button1 -> onNumberButtonClick("1")
                            button2 -> onNumberButtonClick("2")
                            button3 -> onNumberButtonClick("3")
                            button4 -> onNumberButtonClick("4")
                            button5 -> onNumberButtonClick("5")
                            button6 -> onNumberButtonClick("6")
                            button7 -> onNumberButtonClick("7")
                            button8 -> onNumberButtonClick("8")
                            button9 -> onNumberButtonClick("9")
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        btn.textSize = 14F
                        btn.setBackgroundResource(R.drawable.rectangle_fill_black)
                    }
                }
                true
            }
        }
    }

    // 숫자 버튼 클릭
    private fun onNumberButtonClick(number: String, isHistory: Boolean = false) {
        var currentValue: String = binding.numberCurrent.text.toString()
        if (currentValue.replace(",", "").replace(".", "").length < 14) {
            currentValue =
                if (currentValue == "0" || isFourOperationButtonClicked || isOtherOperationButtonClicked || isEqualButtonClicked || isHistory) number else StringBuilder().append(
                    currentValue
                ).append(number).toString()

            try {
                currentNumber = formatStringToDouble(currentValue)
            } catch (e: ParseException) {
                throw IllegalArgumentException("String must be number.")
            }

            binding.numberCurrent.text = formatDoubleToString(formatStringToDouble(currentValue))

            // = 버튼 누르면 연산자 초기화, history 초기화
            if (isEqualButtonClicked) {
                currentOperation = ""
                historyText = ""
            }

            if (isOtherOperationButtonClicked) {
                historyOperationText = ""
                isOtherOperationButtonClicked = false
            }

            isFourOperationButtonClicked = false
            isEqualButtonClicked = false
        } else if (isFourOperationButtonClicked && currentValue.replace(",", "").length == 14) {
            binding.numberCurrent.text = number
            currentValue = number
        }
    }

    // 사칙 연산 버튼 클릭
    private fun calculateFourOperation(operation: String) {
        if (!isFourOperationButtonClicked && !isEqualButtonClicked) {
            calculateResult()
        }

        currentOperation = operation

        if (isOtherOperationButtonClicked) {
            isOtherOperationButtonClicked = false
        }

        isFourOperationButtonClicked = true
        isEqualButtonClicked = false
    }

    // = 버튼 클릭
    private fun calculateEqual() {
        if (isFourOperationButtonClicked) {
            currentNumber = currentResult
        }

        val historyAllText = calculateResult()
        historyList.add(historyAllText)

        historyText = StringBuilder().append(formatDoubleToString(currentResult)).toString()

        if (binding.numberCurrent.text != "ERROR") {
            resultList.add(currentResult)
            binding.tvGt.visibility = View.VISIBLE
            binding.tvPlus.visibility = View.INVISIBLE
            binding.tvMinus.visibility = View.INVISIBLE
            binding.tvMul.visibility = View.INVISIBLE
            binding.tvDiv.visibility = View.INVISIBLE
        }

        isFourOperationButtonClicked = false
        isEqualButtonClicked = true
    }

    // 플마 부호 클릭
    private fun calculateSign() {
        val currentValue: String = binding.numberCurrent.text.toString()

        currentNumber = formatStringToDouble(currentValue)
        if (currentNumber == 0.0) return

        currentNumber *= -1
        binding.numberCurrent.text = formatDoubleToString(currentNumber)

        if (isOtherOperationButtonClicked) {
            historyOperationText = "($historyOperationText)"
            historyOperationText = StringBuilder().append(historyOperationText).toString()
        }

        if (isEqualButtonClicked) {
            currentOperation = ""
        }

        isFourOperationButtonClicked = false
        isEqualButtonClicked = false
    }

    // 소수점 버튼 클릭
    private fun calculatePoint() {
        var currentValue: String = binding.numberCurrent.text.toString()

        if (isFourOperationButtonClicked || isOtherOperationButtonClicked || isEqualButtonClicked) {
            currentValue = StringBuilder().append("").append(".").toString()
            if (isOtherOperationButtonClicked) {
                historyOperationText = ""
            }
            if (isEqualButtonClicked) currentOperation = ""
            currentNumber = 0.0
        } else if (currentValue.contains(".")) {
            return
        } else currentValue = StringBuilder().append(currentValue).append(".").toString()

        binding.numberCurrent.text = currentValue

        isFourOperationButtonClicked = false
        isOtherOperationButtonClicked = false
        isEqualButtonClicked = false
    }

    // gt 버튼
    private fun calculateGt() {
        var result = 0.0
        for (i in 0 until resultList.size) {
            result += resultList[i]
        }
        binding.numberCurrent.text = formatDoubleToString(result)
    }

    // 뒤로가기 버튼
    private fun calculateBack() {
        if (isFourOperationButtonClicked || isOtherOperationButtonClicked || isEqualButtonClicked) return

        var currentValue: String = binding.numberCurrent.text.toString()

        if (currentValue.length == 1) {
            currentValue = "0"
        } else {
            currentValue = currentValue.substring(0, currentValue.length - 1)
        }

        binding.numberCurrent.text = useNumberFormat().format(formatStringToDouble(currentValue))
        currentNumber = formatStringToDouble(currentValue)
    }

    // 결과 계산하기
    private fun calculateResult(): String {
        when (currentOperation) {
            "" -> {
                currentResult = currentNumber
            }
            " + " -> {
                currentResult += currentNumber
            }
            " - " -> {
                currentResult -= currentNumber
            }
            " × " -> {
                currentResult *= currentNumber
            }
            " ÷ " -> {
                currentResult /= currentNumber
            }
        }

        if (formatDoubleToString(currentResult).replace(",", "").replace(".", "").length > 14) {
            binding.numberCurrent.text = "ERROR"
        } else {
            binding.numberCurrent.text = pointDoubleToString(currentResult)
        }

        if (isOtherOperationButtonClicked) {
            isOtherOperationButtonClicked = false
            if (isEqualButtonClicked) historyText =
                StringBuilder().append(historyText).append(currentOperation)
                    .append(formatDoubleToString(currentNumber)).toString()
        } else {
            historyText = StringBuilder().append(historyText).append(currentOperation)
                .append(formatDoubleToString(currentNumber)).toString()
        }

        return StringBuilder().append(historyText).append(" = ")
            .append(formatDoubleToString(currentResult)).toString()
    }

    // 검정색 버튼 - 기능 미구현 색상, 글자 크기 변경
    @SuppressLint("ClickableViewAccessibility")
    private fun initBlackBtnNothingChangeListener(btn: Button) {
        with(binding) {
            btn.setOnTouchListener { _, motionEvent ->
                availableK()

                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        btn.textSize = 10F
                        btn.setBackgroundResource(R.drawable.rectangle_fill_light_black)
                        shortToast("기능 구현 예정입니다.")
                    }
                    MotionEvent.ACTION_UP -> {
                        btn.textSize = 14F
                        btn.setBackgroundResource(R.drawable.rectangle_fill_black)
                    }
                }
                true
            }
        }
    }

    // 루트 버튼
    private fun calculateRoot() {
        var currentValue: String = binding.numberCurrent.text.toString()
        var thisOperationNumber: Double = formatStringToDouble(currentValue)

        // (9)처럼 루트 사용 시에 history에서 잘 보기 위해서
        currentValue = "(${formatDoubleToString(thisOperationNumber)})"
        thisOperationNumber = sqrt(thisOperationNumber)

        historyOperationText = StringBuilder().append("√").append(currentValue).toString()

        binding.numberCurrent.text = formatDoubleToString(thisOperationNumber)
        // resultList.add(thisOperationNumber)

        if (isEqualButtonClicked) currentResult = thisOperationNumber else currentNumber =
            thisOperationNumber

        isOtherOperationButtonClicked = true
        isFourOperationButtonClicked = false
    }

    // 파란색 버튼 색상, 글자 크기 변경
    @SuppressLint("ClickableViewAccessibility")
    private fun initBlueBtnChangeListener(btn: Button) {
        with(binding) {
            btn.setOnTouchListener { _, motionEvent ->
                availableK()

                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        btn.textSize = 10F
                        btn.setBackgroundResource(R.drawable.rectangle_fill_light_blue)
                        when (btn) {
                            buttonMemoryAdd -> addMemoryNumber()
                            buttonMemoryClear -> clearMemoryNumber()
                            buttonMemoryRecall -> recallMemoryNumber()
                            buttonMemorySubtract -> subtrackMemoryNumber()
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        btn.textSize = 14F
                        btn.setBackgroundResource(R.drawable.rectangle_fill_blue)
                    }
                }
                true
            }
        }
    }

    // M+ 버튼 클릭
    private fun addMemoryNumber() {
        val currentValue: String = binding.numberCurrent.text.toString()
        val thisOperationNumber: Double = pointStringToDouble(currentValue)
        binding.numberCurrent.text = pointDoubleToString(thisOperationNumber)

        val newMemory = memoryPlus + thisOperationNumber
        memoryPlus = newMemory

        binding.tvM.visibility = View.VISIBLE
        binding.tvPlus.visibility = View.INVISIBLE
        binding.tvMinus.visibility = View.INVISIBLE
        binding.tvMul.visibility = View.INVISIBLE
        binding.tvDiv.visibility = View.INVISIBLE
    }

    // M- 버튼 클릭
    private fun subtrackMemoryNumber() {
        val currentValue: String = binding.numberCurrent.text.toString()
        val thisOperationNumber: Double = pointStringToDouble(currentValue)
        binding.numberCurrent.text = pointDoubleToString(thisOperationNumber)

        val newMemory = memoryMinus - thisOperationNumber
        memoryMinus = newMemory

        binding.tvM.visibility = View.VISIBLE
        binding.tvPlus.visibility = View.INVISIBLE
        binding.tvMinus.visibility = View.INVISIBLE
        binding.tvMul.visibility = View.INVISIBLE
        binding.tvDiv.visibility = View.INVISIBLE
    }

    // MC 버튼 클릭
    private fun clearMemoryNumber() {
        memoryPlus = 0.0
        memoryMinus = 0.0
        binding.tvM.visibility = View.INVISIBLE
    }

    // MR 버튼 클릭
    private fun recallMemoryNumber() {
        historyOperationText = ""

        if (isEqualButtonClicked) {
            currentOperation = ""
            historyText = ""
        }

        isOtherOperationButtonClicked = false
        isFourOperationButtonClicked = false
        isEqualButtonClicked = false

        currentNumber = memoryPlus + memoryMinus
        binding.numberCurrent.text = pointDoubleToString(memoryPlus + memoryMinus)
    }

    // 빨간색 버튼 색상, 글자 크기 변경
    @SuppressLint("ClickableViewAccessibility")
    private fun initRedBtnChangeListener(btn: Button) {
        with(binding) {
            btn.setOnTouchListener { _, motionEvent ->
                availableK()

                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        btn.textSize = 10F
                        btn.setBackgroundResource(R.drawable.rectangle_fill_light_red)
                        when (btn) {
                            buttonAc -> calculateAC()
                            buttonC -> calculateC()
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        btn.textSize = 14F
                        btn.setBackgroundResource(R.drawable.rectangle_fill_red)
                    }
                }
                true
            }
        }
    }

    // ac 버튼 클릭
    private fun calculateAC() {
        currentNumber = 0.0
        currentResult = 0.0
        currentOperation = ""

        historyText = ""
        historyOperationText = ""

        binding.numberCurrent.text = formatDoubleToString(currentNumber)

        resultList.clear()
        binding.tvGt.visibility = View.INVISIBLE

        isFourOperationButtonClicked = false
        isEqualButtonClicked = false
        isOtherOperationButtonClicked = false

        binding.tvPlus.visibility = View.INVISIBLE
        binding.tvMinus.visibility = View.INVISIBLE
        binding.tvMul.visibility = View.INVISIBLE
        binding.tvDiv.visibility = View.INVISIBLE
        binding.tvK.visibility = View.INVISIBLE
    }

    // c 버튼 클릭
    private fun calculateC() {
        historyOperationText = ""

        if (isEqualButtonClicked) {
            currentOperation = ""
            historyText = ""
        }

        isOtherOperationButtonClicked = false
        isFourOperationButtonClicked = false
        isEqualButtonClicked = false

        currentNumber = 0.0
        binding.numberCurrent.text = formatDoubleToString(0.0)

        binding.tvPlus.visibility = View.INVISIBLE
        binding.tvMinus.visibility = View.INVISIBLE
        binding.tvMul.visibility = View.INVISIBLE
        binding.tvDiv.visibility = View.INVISIBLE
    }

    private fun pointNumberFormat(): DecimalFormat {
        var format = DecimalFormat("##,###,###,###,###.##############")

        with(binding) {
            if (rbRoundingCut.isChecked) {
                if (rbCount0.isChecked) {
                    format = DecimalFormat("##,###,###,###,###")
                    format.roundingMode = RoundingMode.DOWN
                }
                if (rbCount1.isChecked) {
                    format = DecimalFormat("#,###,###,###,###.#")
                    format.roundingMode = RoundingMode.DOWN
                }
                if (rbCount2.isChecked) {
                    format = DecimalFormat("###,###,###,###.##")
                    format.roundingMode = RoundingMode.DOWN
                }
                if (rbCount3.isChecked) {
                    format = DecimalFormat("##,###,###,###.###")
                    format.roundingMode = RoundingMode.DOWN
                }
                if (rbCount4.isChecked) {
                    format = DecimalFormat("#,###,###,###.####")
                    format.roundingMode = RoundingMode.DOWN
                }
                if (rbCountAdd.isChecked) {
                    format = DecimalFormat("###,###,###,###.00")
                    format.roundingMode = RoundingMode.DOWN
                }
            } else if (rbRoundingFi.isChecked) {
                if (rbCount0.isChecked) {
                    format = DecimalFormat("##,###,###,###,###")
                }
                if (rbCount1.isChecked) {
                    format = DecimalFormat("#,###,###,###,###.#")
                }
                if (rbCount2.isChecked) {
                    format = DecimalFormat("###,###,###,###.##")
                }
                if (rbCount3.isChecked) {
                    format = DecimalFormat("##,###,###,###.###")
                }
                if (rbCount4.isChecked) {
                    format = DecimalFormat("#,###,###,###.####")
                }
                if (rbCountAdd.isChecked) {
                    format = DecimalFormat("###,###,###,###.00")
                }
            }
        }
        return format
    }

    private fun useNumberFormat(): DecimalFormat {
        var format = DecimalFormat("##,###,###,###,###.##############")
        return format
    }

    private fun formatDoubleToString(number: Double): String {
        return useNumberFormat().format(number)
    }

    private fun formatStringToDouble(number: String): Double {
        return useNumberFormat().parse(number).toDouble()
    }

    private fun pointDoubleToString(number: Double): String {
        return pointNumberFormat().format(number)
    }

    private fun pointStringToDouble(number: String): Double {
        return pointNumberFormat().parse(number).toDouble()
    }
}
