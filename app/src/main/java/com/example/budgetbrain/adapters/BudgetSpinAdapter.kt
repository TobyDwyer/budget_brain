package com.example.budgetbrain.adapters

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.budgetbrain.models.BudgetItem


class BudgetSpinAdapter(// Your sent context
    private val context: Context, textViewResourceId: Int,
    values: List<BudgetItem>
) : ArrayAdapter<BudgetItem>(context, textViewResourceId, values) {
    // Your custom values for the spinner (User)
    private val values: List<BudgetItem> = values

    override fun getCount(): Int {
        return values.size
    }

    override fun getItem(position: Int): BudgetItem {
        return values[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    // And the "magic" goes here
    // This is for the "passive" state of the spinner
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
        val label = super.getView(position, convertView, parent) as TextView
        label.setTextColor(Color.BLACK)
        // Then you can get the current item using the values array (Users array) and the current position
        // You can NOW reference each method you has created in your bean object (User class)
        label.setText(values[position].name)

        // And finally return your dynamic (or custom) view for each spinner item
        return label
    }

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val label = super.getDropDownView(position, convertView, parent) as TextView
        label.setTextColor(Color.BLACK)
        label.setText(values[position].name)

        return label
    }
}