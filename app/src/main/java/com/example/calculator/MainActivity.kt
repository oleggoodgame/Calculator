package com.example.calculator

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.calculator.databinding.ActivityMainBinding
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import java.math.RoundingMode
import kotlin.math.pow

//  це клас у Kotlin (і Java), який дозволяє створювати та маніпулювати змінними рядками більш ефективно, ніж звичайний рядковий тип (String). Це особливо корисно, коли потрібно часто змінювати рядки, як у нашому випадку при побудові чисел.
 class MainActivity : AppCompatActivity() {
     private lateinit var binding: ActivityMainBinding
     private lateinit var adapter: RecyclerViewAdapter
    private lateinit var viewModel: RecyclerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(RecyclerViewModel::class.java)
        adapter = RecyclerViewAdapter { text ->
            binding.textView4.text = text.text
            viewModel.setText(text.text.toString())
            adapter.delete(text)
        }
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        layoutManager.stackFromEnd =
            true // Цей рядок налаштовує RecyclerView так, щоб нові елементи додавалися до кінця списку, а не на початок
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter
        adapter.addText(viewModel.items.toString())

        val buttons = listOf(
            binding.button1, binding.button2, binding.button3, binding.button4, binding.button5,
            binding.button6, binding.button7, binding.button8, binding.button9, binding.button10,

            )
        val buttonsAdditions = listOf(
            binding.plus, binding.minus, binding.mnoj, binding.dil
        )

        viewModel.items.observe(this) { items ->
            adapter.setItems(items)
        }

        viewModel.text.observe(this) { text ->
            binding.textView4.text = text
        }

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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel = ViewModelProvider(this).get(RecyclerViewModel::class.java)
        viewModel.setText(binding.textView4.text.toString())
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
            var currentText = binding.textView4.text.toString()

            if (currentText.first() == '0' && !currentText.contains(Regex("[.+\\-/*]"))) {
                currentText = ""
            }

            val lastChar = currentText.lastOrNull()
            val isOperator = lastChar != null && lastChar in "+-/*" // Це важливо для правильного додавання нових символів. Якщо останній символ — оператор, то це означає, що наступний символ має бути частиною нового числа, а не частиною попереднього.

            val parts = currentText.split(Regex("(?=[+\\-/*])|(?<=[+\\-/*])")).toMutableList()
            // шукає позиції, де за поточною позицією йде будь-який з символів
            // шукає позиції, де перед поточною позицією є будь-який з цих символів.
            var currentNumber = if (isOperator) "" else parts.last().replace(" ", "")
            //Цей рядок визначає, з яким числом працювати далі. Якщо останній символ — оператор, то це означає, що нове число ще не було введене, тому currentNumber починається з порожнього рядка. Якщо оператор відсутній, тобто останній символ — це частина числа, то береться остання частина числа для продовження вводу.
            val buttonText = button.text.toString()
            // Регулярний вираз (?=[+\\-/*])|(?<=[+\\-/*])
            //Цей вираз використовується для розділення рядка на частини, використовуючи оператори як роздільники.
            //
            //(?=[+\\-/*]): Це позитивний погляд вперед. Він знаходить позиції перед будь-яким оператором +, -, *, або / без включення самого оператора в роздільник.
            //(?<=[+\\-/*]): Це позитивний погляд назад. Він знаходить позиції після будь-якого оператора +, -, *, або / без включення самого оператора в роздільник.
            //Використання обох поглядів дозволяє розділити рядок таким чином, щоб оператори залишалися окремими частинами.
            //
            //Чому не використати [.+\\-/*]?
            //
            //[.+\\-/*] - це регулярний вираз, який би працював як набір символів (жоден з них), а не як роздільник. Це б не дозволило вам р
            val hasDecimal = currentNumber.contains(".")
            var integerPart = if (hasDecimal) currentNumber.split(".")[0] else currentNumber
            var decimalPart = if (hasDecimal) currentNumber.split(".")[1] else ""
            // . Якщо currentNumber = 532.234:
            //hasDecimal буде true, оскільки в числі є десяткова точка.
            //currentNumber.split(".") розділить число на дві частини:
            //Перша частина (до крапки) — 532, тобто currentNumber.split(".")[0] буде "532".
            //Друга частина (після крапки) — 234, тобто currentNumber.split(".")[1] буде "234".

            //integerPart = "532"
            //decimalPart = "" (порожній рядок, оскільки немає десяткової частини).
            // Перевіряємо, чи можна додавати нові символи
            if (hasDecimal) {
                if (decimalPart.length < 8) { // Ліміт на 8 символів після крапки
                    decimalPart += buttonText
                }
            } else {
                if (integerPart.length < 8) { // Ліміт на 8 символів до крапки
                    integerPart += buttonText
                }
            }

            // Форматуємо число з урахуванням обмежень
            val formattedIntegerPart = integerPart.reversed()
                .chunked(3)
                .joinToString(" ")
                .reversed()

            // Об'єднуємо цілу і десяткову частини
            currentNumber = if (hasDecimal || decimalPart.isNotEmpty()) {
                "$formattedIntegerPart.$decimalPart"
            } else {
                formattedIntegerPart
            }

            // Оновлюємо список частин виразу
            if (isOperator) {
                parts.add(currentNumber)
            } else {
                parts[parts.lastIndex] = currentNumber
            }

            // Оновлюємо текст у TextView
            binding.textView4.text = parts.joinToString("")
            binding.AC.text = "C"
        }
    }












    fun AC(view: View) {

        if (binding.AC.text == "C") {
            binding.textView4.text = "0"
            binding.AC.text = "AC"
        } else if (adapter.getItemCount() != 0 && binding.AC.text == "AC") {
            adapter.deleteAll()
        }
    }


    fun result(view: View) {
        var bool = true
        val expression = binding.textView4.text.toString()

        if (!expression.contains(Regex("[+\\-/*]"))) {
            return
        }

        Log.d("Calculator", "Original expression: $expression")
        val (numbers, operators) = splitExpression(expression)
        Log.d("Calculator", "Numbers: $numbers")
        Log.d("Calculator", "Operators: $operators")

        if (numbers.size == 1 && operators.size == 2) {
            numbers.add("0")
        }

        for (i in numbers) {
            if (i.contains('.') || i.contains('E', true)) {
                bool = false
                break
            }
        }

        val resultText = if (bool) {
            val finalResultInt = findAllInt(numbers, operators)
            if(finalResultInt=="infinity"){
                finalResultInt
            }
            else {
                formatResults(BigDecimal(finalResultInt))
            }
        } else {
            val numberWithE = numbers.find { it.contains('E', ignoreCase = true) }

            if (numberWithE != null) {
                val index = numbers.indexOf(numberWithE)
                val nextChar = operators[index]
                val nextNumber = BigDecimal(numbers[index + 1])
                val exponent = nextNumber.toBigInteger()
                if (nextChar == "+") {
                    val newNumber = 10.0.pow(exponent.toDouble()) // 10^n
                    numbers[index] = BigDecimal(newNumber)
                        .setScale(exponent.toInt(), RoundingMode.HALF_UP)
                        .stripTrailingZeros()
                        .toPlainString()
                }
                else if (nextChar == "-") {
                    val newNumber = 10.0.pow(-exponent.toDouble()) // 10^(-n)
                    numbers[index] = BigDecimal(newNumber)
                        .setScale(exponent.toInt(), RoundingMode.HALF_UP) // Округляємо до 14 знаків
                        .stripTrailingZeros()
                        .toPlainString()

                }
                numbers.removeAt(index + 1)
                operators.removeAt(index)
                Log.d("Calculator", "Numbers: $numbers")
                Log.d("Calculator", "Operators: $operators")
            }

            if(numbers.size==1){
                numbers.add("0")
                operators.add("+")

                val finalResultDouble = findAllDouble(numbers, operators)
                if(finalResultDouble!="Infinity"){
                    formatResults(finalResultDouble.toBigDecimal())
                }
                finalResultDouble
            }
            else{
                val finalResultDouble = findAllDouble(numbers, operators)
                if(finalResultDouble!="Infinity"){
                    formatResults(finalResultDouble.toBigDecimal())
                }
                finalResultDouble
            }
        }


        adapter.addText(binding.textView4.text.toString())
        viewModel.addItem(binding.textView4.text.toString())
        binding.textView4.text = formatResult(resultText)

        bool = true
    }


    fun formatResult(result: String): String {
        val parts = result.split('.')
        val integerPart = parts[0] // 5 555 щоб це получити ми реверсуємо щоб легше працювати 5555
        //Метод .chunked(n) в Kotlin розбиває рядок або список на частини (чанки) розміром n. Це дозволяє легко групувати символи або елементи в менші частини.
        // і тоді розділяємо і забираємо назад reverse і буде 5 555
        val formattedIntegerPart = integerPart.reversed().chunked(3).joinToString(" ").reversed()
        return if (parts.size > 1) {
            "$formattedIntegerPart.${parts[1]}"
        } else {
            formattedIntegerPart
        }
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
                        // Видаляємо пробіли перед додаванням числа
                        numbers.add(currentNumber.toString().replace(" ", ""))
                        currentNumber = StringBuilder()
                    }

                    // Додаємо оператор тільки якщо попередній символ не був оператором
                    if (char in operatorList) {
                        operators.add(char.toString())
                    }
                }
                else -> {
                    // Додаємо цифри до поточного числа (включаючи пробіли)
                    currentNumber.append(char)
                }
            }
        }

        if (currentNumber.isNotEmpty()) {
            // Видаляємо пробіли перед додаванням останнього числа
            numbers.add(currentNumber.toString().replace(" ", ""))
        }

        return Pair(numbers, operators)
    }


    private fun findAllInt(numbers: MutableList<String>, operators: MutableList<String>): String {
        try {


            if (numbers.size != operators.size) {
                var index = 0
                while (index < operators.size) {
                    when (operators[index]) {
                        "*" -> {
                            val result =
                                numbers[index].toBigInteger() * numbers[index + 1].toBigInteger()
                            numbers[index] = result.toString()
                            numbers.removeAt(index + 1)
                            operators.removeAt(index)
                        }

                        "/" -> {
                            val result = numbers[index].toBigDecimal()
                                .divide(numbers[index + 1].toBigDecimal(), MathContext.DECIMAL128)
                            val scale = result.scale()  // Перевіряємо кількість цифр після коми

                            // Заокруглюємо до 10 знаків після коми, якщо їх більше
                            val roundedResult = if (scale > 10) {
                                result.setScale(10, RoundingMode.HALF_UP)
                            } else {
                                result
                            }

                            numbers[index] = roundedResult.toString()
                            numbers.removeAt(index + 1)
                            operators.removeAt(index)

                            val fifi = formatResults(roundedResult)

                            if (fifi.contains(".")) {
                                val resultFin = findAllDouble(numbers, operators)
                                return resultFin
                            }
                        }

                        else -> index++
                    }
                }
                var result = numbers[0].toBigInteger()
                for (i in operators.indices) {
                    when (operators[i]) {
                        "+" -> result += numbers[i + 1].toBigInteger()
                        "-" -> result -= numbers[i + 1].toBigInteger()
                    }
                }
                return result.toString()
            } else {
                operators.removeAt(0)
                var index = 0
                while (index < operators.size) {
                    when (operators[index]) {
                        "*" -> {
                            val result =
                                numbers[index].toBigInteger() * numbers[index + 1].toBigInteger()
                            numbers[index] = result.toString()
                            numbers.removeAt(index + 1)
                            operators.removeAt(index)
                        }

                        "/" -> {
                            val result = numbers[index].toBigDecimal()
                                .divide(numbers[index + 1].toBigDecimal(), MathContext.DECIMAL128)
                            val scale = result.scale()  // Перевіряємо кількість цифр після коми

                            // Заокруглюємо до 10 знаків після коми, якщо їх більше
                            val roundedResult = if (scale > 10) {
                                result.setScale(8, RoundingMode.HALF_UP)
                            } else {
                                result
                            }

                            numbers[index] = roundedResult.toString()
                            numbers.removeAt(index + 1)
                            operators.removeAt(index)

                            val fifi = formatResults(roundedResult)

                            if (fifi.contains(".")) {
                                val resultFin = findAllDouble(numbers, operators)
                                return resultFin
                            }
                        }

                        else -> index++
                    }
                }
                var result = numbers[0].toBigInteger().negate()
                for (i in operators.indices) {
                    when (operators[i]) {
                        "+" -> result += numbers[i + 1].toBigInteger()
                        "-" -> result -= numbers[i + 1].toBigInteger()
                    }
                }
                return result.toString()
            }
        }catch (ex: Exception){
            val result = "infinity"
            return result
        }
     }

     private fun findAllDouble(numbers: MutableList<String>, operators: MutableList<String>): String {
         try{


         if (numbers.size != operators.size) {
                 var index = 0
                 while (index < operators.size) {
                     when (operators[index]) {
                         "*" -> {
                             val result = numbers[index].toBigDecimal() * numbers[index + 1].toBigDecimal()
                             numbers[index] = result.toString()
                             numbers.removeAt(index + 1)
                             operators.removeAt(index)
                         }
                         "/" -> {
                             val result = numbers[index].toBigDecimal().divide(numbers[index + 1].toBigDecimal(), MathContext.DECIMAL128)
                             val scale = result.scale()  // Перевіряємо кількість цифр після коми

                             // Заокруглюємо до 10 знаків після коми, якщо їх більше
                             val roundedResult = if (scale > 14) {
                                 result.setScale(8, RoundingMode.HALF_UP)
                             } else {
                                 result
                             }
                             numbers[index] = roundedResult.toString()
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
                // Використання MathContext.DECIMAL128 забезпечує високу точність при виконанні обчислень
                 return result.toString()

         } else {
             operators.removeAt(0)
             var index = 0
             while (index < operators.size) {
                 when (operators[index]) {
                     "*" -> {
                         val result = numbers[index].toBigDecimal() * numbers[index + 1].toBigDecimal()
                         numbers[index] = result.toString()
                         numbers.removeAt(index + 1)
                         operators.removeAt(index)
                     }
                     "/" -> {
                         val result = numbers[index].toBigDecimal().divide(numbers[index + 1].toBigDecimal(), MathContext.DECIMAL128)
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
             return result.toString()
         }
         }catch (ex: Exception){
             val result = "infinity"
             return result
         }
     }

    private fun formatResults(result: BigDecimal): String {
        val resultString = result.stripTrailingZeros().toPlainString()
        val parts = resultString.split(".")

        return if (parts.size == 2 && parts[1].toBigInteger() == BigInteger.ZERO) {
            parts[0]  // Якщо після крапки лише нулі, повертаємо тільки цілу частину
        } else {
            resultString  // Повертаємо як є, якщо є не лише нулі після крапки
        }
    }

    fun procent(view: View) {
        val resultStr = binding.textView4.text.toString()
        val (numbers, operators) = splitExpression(resultStr)

        if (numbers.isNotEmpty()) {
            val lastNumber = numbers.last()

            if (lastNumber.isNotEmpty()) {
                // Перетворюємо останнє число в BigDecimal і ділимо його на 100
                var updatedNumber = lastNumber.toBigDecimal().divide(BigDecimal(100))

                // Перетворюємо результат у звичайний рядок без наукової нотації
                var updatedNumberStr = updatedNumber.stripTrailingZeros().toPlainString()

                // Перевіряємо, чи результат не містить експоненціальну нотацію (E)
                if (updatedNumberStr.contains("E")) {
                    // Якщо є "E", перетворюємо його на звичайний рядок за допомогою toPlainString()
                    updatedNumberStr = updatedNumber.toPlainString()
                }

                // Розбиваємо рядок на цілу і дробову частини для перевірки кількості символів після крапки
                if (updatedNumberStr.contains(".")) {
                    val parts = updatedNumberStr.split(".")
                    val integerPart = parts[0]
                    val decimalPart = parts[1]

                    // Перевіряємо, чи кількість символів після крапки не більше 8
                    if (decimalPart.length > 8) {
                        // Обрізаємо дробову частину до 8 символів
                        updatedNumberStr = "$integerPart.${decimalPart.substring(0, 8)}"
                    }
                }

                // Оновлюємо останнє число у списку numbers
                numbers[numbers.size - 1] = updatedNumberStr

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

    fun Information(view: View) {
        val bottomSheet = DialogsFragment.newInstance()
        bottomSheet.show((view.context as AppCompatActivity).supportFragmentManager, bottomSheet.tag)

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

