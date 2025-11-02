package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    // Biến để lưu trạng thái
    private var currentOperand: String = "0"
    private var firstOperand: Double = 0.0
    private var pendingOperation: String = ""
    private var shouldResetDisplay: Boolean = false

    // Dùng lateinit để khởi tạo view sau
    private lateinit var textViewResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Lấy tham chiếu đến TextView
        textViewResult = findViewById(R.id.textViewResult)
        updateDisplay()

        // === Thiết lập Listener cho các nút số ===
        val numberButtons = listOf(
            R.id.button0 to "0",
            R.id.button1 to "1",
            R.id.button2 to "2",
            R.id.button3 to "3",
            R.id.button4 to "4",
            R.id.button5 to "5",
            R.id.button6 to "6",
            R.id.button7 to "7",
            R.id.button8 to "8",
            R.id.button9 to "9"
        )

        for ((buttonId, digit) in numberButtons) {
            findViewById<Button>(buttonId).setOnClickListener { onNumberClick(digit) }
        }

        // === Thiết lập Listener cho các nút phép toán ===
        findViewById<Button>(R.id.buttonPlus).setOnClickListener { onOperationClick("+") }
        findViewById<Button>(R.id.buttonMinus).setOnClickListener { onOperationClick("-") }
        findViewById<Button>(R.id.buttonMultiply).setOnClickListener { onOperationClick("x") }
        findViewById<Button>(R.id.buttonDivide).setOnClickListener { onOperationClick("/") }

        // === Nút dấu chấm (decimal) ===
        findViewById<Button>(R.id.buttonDecimal).setOnClickListener {
            if (!currentOperand.contains(".")) {
                currentOperand += "."
                updateDisplay()
            }
        }

        // === Nút bằng ===
        findViewById<Button>(R.id.buttonEquals).setOnClickListener { onEqualsClick() }

        // === Nút đặc biệt ===

        // Nút C: Xóa toàn bộ phép toán, nhập lại từ đầu
        findViewById<Button>(R.id.buttonC).setOnClickListener {
            currentOperand = "0"
            firstOperand = 0.0
            pendingOperation = ""
            shouldResetDisplay = false
            updateDisplay()
        }

        // Nút CE: Xóa giá trị toán hạng hiện tại về 0
        findViewById<Button>(R.id.buttonCE).setOnClickListener {
            currentOperand = "0"
            updateDisplay()
        }

        // Nút BS: Xóa chữ số hàng cuối của toán hạng hiện tại
        findViewById<Button>(R.id.buttonBS).setOnClickListener {
            if (currentOperand.length > 1) {
                currentOperand = currentOperand.dropLast(1)
            } else {
                currentOperand = "0"
            }
            updateDisplay()
        }

        // Nút +/-: Đổi dấu của số hiện tại
        findViewById<Button>(R.id.buttonToggleSign).setOnClickListener {
            val value = currentOperand.toDoubleOrNull() ?: 0.0
            currentOperand = if (value > 0) "-$value" else value.toString().removePrefix("-")
            updateDisplay()
        }
    }

    // Hàm cập nhật màn hình hiển thị
    private fun updateDisplay() {
        textViewResult.text = currentOperand
    }

    // Hàm xử lý khi nhấn nút số
    private fun onNumberClick(digit: String) {
        if (shouldResetDisplay) {
            currentOperand = digit
            shouldResetDisplay = false
        } else {
            if (currentOperand == "0" && digit != ".") {
                currentOperand = digit
            } else {
                currentOperand += digit
            }
        }
        updateDisplay()
    }

    // Hàm xử lý khi nhấn nút phép toán
    private fun onOperationClick(operation: String) {
        // Nếu đã có phép toán chờ, thực hiện nó trước
        if (pendingOperation.isNotEmpty() && !shouldResetDisplay) {
            onEqualsClick()
        }

        firstOperand = currentOperand.toDoubleOrNull() ?: 0.0
        pendingOperation = operation
        shouldResetDisplay = true
    }

    // Hàm xử lý khi nhấn nút '='
    private fun onEqualsClick() {
        if (pendingOperation.isEmpty()) {
            return
        }

        val secondOperand = currentOperand.toDoubleOrNull() ?: 0.0
        var result: Double = 0.0

        when (pendingOperation) {
            "+" -> result = firstOperand + secondOperand
            "-" -> result = firstOperand - secondOperand
            "x" -> result = firstOperand * secondOperand
            "/" -> {
                result = if (secondOperand != 0.0) {
                    firstOperand / secondOperand
                } else {
                    textViewResult.text = "Error"
                    pendingOperation = ""
                    currentOperand = "0"
                    return
                }
            }
        }

        // Loại bỏ .0 nếu là số nguyên
        currentOperand = if (result == result.toLong().toDouble()) {
            result.toLong().toString()
        } else {
            String.format("%.2f", result)
        }

        pendingOperation = ""
        shouldResetDisplay = true
        updateDisplay()
    }
}