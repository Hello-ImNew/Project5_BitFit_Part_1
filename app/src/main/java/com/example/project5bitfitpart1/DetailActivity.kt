package com.example.project5bitfitpart1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

const val TAG = "DetailActivity"

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val itemNameET = findViewById<EditText>(R.id.ItemNameEditText)
        val itemCaloriesET = findViewById<EditText>(R.id.ItemCaloriesEditText)
        val recordItemBtn = findViewById<Button>(R.id.RecordItemButton)

        fun closeKeyBoard() {
            val view = this.currentFocus
            if (view != null) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }

        recordItemBtn.setOnClickListener {
            val itemName = itemNameET.text.toString()
            val itemCalories = itemCaloriesET.text.toString()

            if (itemName.isNotEmpty() && itemCalories.isNotEmpty()) {
                closeKeyBoard()
                val item = BitFitItem(itemName, itemCalories)
                val data = Intent()
                data.putExtra(TAG, item)
                setResult(RESULT_OK, data)
                finish()
            }
        }
    }
}