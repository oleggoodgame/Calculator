package com.example.calculator

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.calculator.databinding.ActivityMainBinding
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

//  це клас у Kotlin (і Java), який дозволяє створювати та маніпулювати змінними рядками більш ефективно, ніж звичайний рядковий тип (String). Це особливо корисно, коли потрібно часто змінювати рядки, як у нашому випадку при побудові чисел.
 class MainActivity : AppCompatActivity() {
     private lateinit var binding: ActivityMainBinding
     private lateinit var adapter: RecyclerViewAdapter

     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         binding = ActivityMainBinding.inflate(layoutInflater)
         setContentView(binding.root)

         adapter = RecyclerViewAdapter { text ->
             binding.textView4.text = text.text
             adapter.delete(text)
         }
         val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
         layoutManager.stackFromEnd = true // Цей рядок налаштовує RecyclerView так, щоб нові елементи додавалися до кінця списку, а не на початок
         binding.recyclerView.layoutManager = layoutManager
         binding.recyclerView.adapter = adapter

         val buttons = listOf(
             binding.button1, binding.button2, binding.button3, binding.button4, binding.button5,
             binding.button6, binding.button7, binding.button8, binding.button9, binding.button10,

         )
         val buttonsAdditions = listOf(
             binding.plus, binding.minus, binding.mnoj, binding.dil
         )
         buttons.forEach { button ->
             button.setOnClickListener {
                 onNumberButtonClick(button)
             }
         }
         buttonsAdditions.forEach { button ->
             button.setOnClickListener {
                 onNumberButtonClickAddition(button)
             }
         }
     }

     private fun onNumberButtonClickAddition(button: View) {
         if (button is Button) {
             val currentText = binding.textView4.text.toString()
             val previousChar = currentText[currentText.length - 1]
             val buttonText = button.text.toString()
             if (previousChar != '+' && previousChar != '-' && previousChar != '*' && previousChar != '/') {
                 binding.textView4.append(buttonText)
             } else {
                 val updatedText = currentText.dropLast(1)
                 binding.textView4.text = updatedText + buttonText
             }

         binding.AC.text = "C"
         }
     }


     private fun onNumberButtonClick(button: View) {
         if (button is Button) {
             val currentText = binding.textView4.text.toString()
             if(binding.textView4.text.toString().first() == '0' && !currentText.contains(Regex("[.+\\-/*]") ))
             {
                 binding.textView4.text = ""
             }
             binding.AC.text = "C"
             val buttonText = button.text.toString()
             binding.textView4.append(buttonText)
         }
     }

     fun AC(view: View) {
         val currentText = binding.textView4.text.toString()

         if (currentText.isNotEmpty() && binding.textView4.text != "0" && binding.AC.text=="C") {
             val updatedText = currentText.dropLast(1)
             binding.textView4.text = if (updatedText.isEmpty()) "0" else updatedText

             binding.AC.text = if (updatedText.isEmpty()) "AC" else "C"
         }
         else if(adapter.getItemCount() != 0 && binding.AC.text=="AC"){
             adapter.deleteAll()
         }
     }

     fun result(view: View) {
         var bool = true
         val expression = binding.textView4.text.toString()
         Log.d("Calculator", "Original expression: $expression")
         val (numbers, operators) = splitExpression(expression)
         Log.d("Calculator", "Numbers: $numbers")
         Log.d("Calculator", "Operators: $operators")

         for (i in numbers) {
             if (i.contains('.')) {
                 bool = false
                 break
             }
         }

         if (bool) {
             val finalResultInt = findAllInt(numbers, operators)
             adapter.addText(binding.textView4.text.toString())
             binding.textView4.text = finalResultInt.toString()
         } else {
             val finalResultDouble = findAllDouble(numbers, operators)
             adapter.addText(binding.textView4.text.toString())
             binding.textView4.text = formatResult(finalResultDouble)
         }

         bool = true
     }

     fun splitExpression(expression: String): Pair<MutableList<String>, MutableList<String>> {
         val numbers = mutableListOf<String>()
         val operators = mutableListOf<String>()
         var currentNumber = StringBuilder()
         val operatorList = arrayListOf('+', '-', '*', '/', '%')
         for (char in expression) {
             when (char) {
                 '+', '-', '*', '/', '%' -> {
                     // Додаємо попереднє число, якщо є
                     if (currentNumber.isNotEmpty()) {
                         numbers.add(currentNumber.toString())
                         currentNumber = StringBuilder()
                     }

                     // Додаємо оператор тільки якщо попередній символ не був оператором
                     if (char in operatorList) {
                         operators.add(char.toString())
                     }
                 }
                 else -> {
                     // Додаємо цифри до поточного числа
                     currentNumber.append(char)
                 }
             }
         }

         if (currentNumber.isNotEmpty()) {
             numbers.add(currentNumber.toString())
         }

         return Pair(numbers, operators)
     }

     private fun findAllInt(numbers: MutableList<String>, operators: MutableList<String>): Int {
         if (numbers.size != operators.size) {
             var index = 0
             while (index < operators.size) {
                 when (operators[index]) {
                     "*" -> {
                         val result = numbers[index].toInt() * numbers[index + 1].toInt()
                         numbers[index] = result.toString()
                         numbers.removeAt(index + 1)
                         operators.removeAt(index)
                     }
                     "/" -> {
                         val result = numbers[index].toInt() / numbers[index + 1].toInt()
                         numbers[index] = result.toString()
                         numbers.removeAt(index + 1)
                         operators.removeAt(index)
                     }
                     else -> index++
                 }
             }
             var result = numbers[0].toInt()
             for (i in operators.indices) {
                 when (operators[i]) {
                     "+" -> result += numbers[i + 1].toInt()
                     "-" -> result -= numbers[i + 1].toInt()
                 }
             }
             return result
         } else {
             operators.removeAt(0)
             var index = 0
             while (index < operators.size) {
                 when (operators[index]) {
                     "*" -> {
                         val result = numbers[index].toInt() * numbers[index + 1].toInt()
                         numbers[index] = result.toString()
                         numbers.removeAt(index + 1)
                         operators.removeAt(index)
                     }
                     "/" -> {
                         val result = numbers[index].toInt() / numbers[index + 1].toInt()
                         numbers[index] = result.toString()
                         numbers.removeAt(index + 1)
                         operators.removeAt(index)
                     }
                     else -> index++
                 }
             }
             var result = numbers[0].toInt() * -1
             for (i in operators.indices) {
                 when (operators[i]) {
                     "+" -> result += numbers[i + 1].toInt()
                     "-" -> result -= numbers[i + 1].toInt()
                 }
             }
             return result
         }
     }

     private fun findAllDouble(numbers: MutableList<String>, operators: MutableList<String>): Double {
         if (numbers.size != operators.size) {
                 // Виконуємо множення і ділення перш ніж додавання і віднімання
                 var index = 0
                 while (index < operators.size) {
                     when (operators[index]) {
                         "*" -> {
                             val result = BigDecimal(numbers[index]).multiply(BigDecimal(numbers[index + 1]), MathContext.DECIMAL128).toDouble()
                             numbers[index] = result.toString()
                             numbers.removeAt(index + 1)
                             operators.removeAt(index)
                         }
                         "/" -> {
                             val result = BigDecimal(numbers[index]).divide(BigDecimal(numbers[index + 1]), MathContext.DECIMAL128).toDouble()
                             numbers[index] = result.toString()
                             numbers.removeAt(index + 1)
                             operators.removeAt(index)
                         }
                         else -> index++
                     }
                 }

                 // Виконуємо додавання і віднімання
                 var result = BigDecimal(numbers[0])
                 for (i in operators.indices) {
                     result = when (operators[i]) {
                         "+" -> result.add(BigDecimal(numbers[i + 1]))
                         "-" -> result.subtract(BigDecimal(numbers[i + 1]))
                         else -> result
                     }
                 }
                // Використання MathContext.DECIMAL128 забезпечує високу точність при виконанні обчислень.
                 return result.toDouble()

         } else {
             operators.removeAt(0)
             var index = 0
             while (index < operators.size) {
                 when (operators[index]) {
                     "*" -> {
                         val result = BigDecimal(numbers[index]).multiply(BigDecimal(numbers[index + 1],  MathContext.DECIMAL128)).toDouble()
                         numbers[index] = result.toString()
                         numbers.removeAt(index + 1)
                         operators.removeAt(index)
                     }
                     "/" -> {
                         val result = BigDecimal(numbers[index]).divide(BigDecimal(numbers[index + 1]),  MathContext.DECIMAL128).toDouble()
                         numbers[index] = result.toString()
                         numbers.removeAt(index + 1)
                         operators.removeAt(index)
                     }
                     else -> index++
                 }
             }
             var result = BigDecimal(numbers[0]).multiply(BigDecimal(-1))
             for (i in operators.indices) {
                 result = when (operators[i]) {
                     "+" -> result.add(BigDecimal(numbers[i + 1]))
                     "-" -> result.subtract(BigDecimal(numbers[i + 1]))
                     else -> result
                 }
             }
             return result.toDouble()
         }
     }

     private fun formatResult(result: Double): String {
         return if (result == result.toInt().toDouble()) {
             result.toInt().toString()
         } else {
             result.toString()

         }
     }

    fun procent(view: View) {
        val resultStr = binding.textView4.text.toString()
        val (numbers, operators) = splitExpression(resultStr)
        if (numbers.isNotEmpty()) {
            val lastNumber = numbers.last()

            if (lastNumber.isNotEmpty()) {
                // Перетворюємо останнє число в BigDecimal і ділимо
                val updatedNumber = lastNumber.toBigDecimal().divide(BigDecimal(100))

                // Цей метод видаляє зайві нулі в кінці дробової частини числа, якщо такі є. Наприклад, число 1.2300 буде перетворене в 1.23
                numbers[numbers.size - 1] = updatedNumber.stripTrailingZeros().toPlainString() // toPlainString() перетворює BigDecimal в звичайний рядок без наукової нотації.

                // Оновлюємо вираз і відображаємо його
                val updatedExpression = joinText(numbers, operators)
                binding.textView4.text = updatedExpression
            }
        }
    }




    fun joinText(numbers: MutableList<String>, operators: MutableList<String>): String {
         val result = StringBuilder()

         for (i in numbers.indices) {
             if (i > 0) {
                 if (i - 1 < operators.size) {
                     result.append(operators[i - 1])
                 }
             }
             result.append(numbers[i])
         }

         return result.toString()
     }

     fun Point(view: View) {
         val currentText = binding.textView4.text.toString()
         val previousChar = currentText[currentText.length - 1]
         if (previousChar != '+' && previousChar != '-' && previousChar != '*' && previousChar != '/') {
             val lastNumber = currentText.split("+", "-", "*", "/").last()
             if (!lastNumber.contains('.')) {
                 val buttonText = binding.buttonPoint.text.toString()
                 binding.textView4.append(buttonText)

             }
         }


     }

     fun minus_plus(view: View) {
         val currentText = binding.textView4.text.toString()

         // Перевірка, чи є значущі цифри після `0.` знизу пояснюєтсья як працює
         val hasSignificantDigits = Regex("^0\\.\\d+$").matches(currentText)

         if (currentText.first() != '0' || hasSignificantDigits) {
             if (currentText.first() == '-') {
                 binding.textView4.text = currentText.drop(1)
             } else {
                 binding.textView4.text = "-$currentText"
             }
         }
     }

 }

//Регулярний вираз ^0\\.\\d+$ в Kotlin використовується для перевірки, чи рядок відповідає певному шаблону. Давайте розглянемо, як саме він працює:
//
//Регулярний вираз
//^: Позначає початок рядка. Це означає, що перевірка починається з самого початку рядка.
//
//0: Вимога, що рядок повинен починатися з цифри 0.
//
//\\.: \\. представляє крапку (декілька слешів через необхідність екранізувати крапку в регулярних виразах). Крапка використовується для розділення цілої частини та дробової частини числа.
//
//\\d+: \\d представляє будь-яку цифру, а + означає, що має бути одна або більше цифр. Це перевіряє, чи є після крапки одна або більше цифр.
//
//$: Позначає кінець рядка. Це забезпечує, що рядок закінчується після цифр.
//
//Як це працює
//Регулярний вираз перевіряє, чи рядок починається з 0. і чи після крапки йдуть одна або більше цифр.
//Наприклад, рядки 0.1, 0.123456, і 0.000 відповідатимуть цьому регулярному виразу, а рядки 0., 10.5, і 01.23 не відповідатимуть.
//Приклади
//0.005: Підходить, бо починається з 0. і має значущі цифри 005 після крапки.
//0.0: Також підходить, бо після 0. йдуть цифри 0.
//0.: Не підходить, бо після крапки немає цифр.
//5.678: Не підходить, бо не починається з 0..

