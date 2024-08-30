package com.example.calculator

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.calculator.databinding.ActivityMainBinding
import com.example.calculator.databinding.TextBinding

class RecyclerViewAdapter(
    private val onItemClick: (TextView) -> Unit
) : RecyclerView.Adapter<RecyclerViewAdapter.TextHolder>() {

    class TextHolder(item: View, val onItemClick: (TextView) -> Unit) : RecyclerView.ViewHolder(item) {
        val binding = TextBinding.bind(item)

        fun bind(text: String) = with(binding) {
            textView.text = text
            itemView.setOnClickListener {
                onItemClick(textView)
            }
        }
    }

    private val textList: MutableList<String> = mutableListOf()
 // ViewGroup є базовим класом для всіх контейнерів, які можуть містити інші вью (наприклад, LinearLayout, RelativeLayout, ConstraintLayout). Він надає методу для додавання і видалення дочірніх вью.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.text, parent, false)
        return TextHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: TextHolder, position: Int) {
        holder.bind(textList[position])
    }

    override fun getItemCount(): Int {
        return textList.size
    }

    fun addText(text: String) {
        textList.add(text)
        notifyItemInserted(textList.size - 1) //  використовується для сповіщення адаптера, що елемент був вставлений у певну позицію. У твоєму випадку:
    }

    fun delete(text: TextView) {
        val position = textList.indexOf(text.text.toString())
        if (position != -1) {
            textList.removeAt(position)
            notifyItemRemoved(position) // користовується для сповіщення адаптера, що елемент був видалений з певної позиції:
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteAll(){
        textList.clear()
        notifyDataSetChanged() // повіщає адаптер про те, що дані змінилися, і весь список слід перерисувати.
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(newItems: List<String>) {
        textList.clear() // Очистка поточного списку
        textList.addAll(newItems) // Додавання нових елементів
        notifyDataSetChanged() // Оновлення адаптера
    }
}
