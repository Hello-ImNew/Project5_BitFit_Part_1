package com.example.project5bitfitpart1

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    var items = mutableListOf<BitFitItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val itemRv = findViewById<RecyclerView>(R.id.ItemsListRecyclerView)
        val addButton = findViewById<Button>(R.id.AddItemButton)

        val adapter = BitFitAdapter(items)
        itemRv.adapter = adapter

        itemRv.layoutManager = LinearLayoutManager(this).also {
            val dividerItemDecoration = DividerItemDecoration(this, it.orientation)
            itemRv.addItemDecoration(dividerItemDecoration)
        }

        lifecycleScope.launch {
            (application as BitFitApplication).db.bitfitDao().getAll().collect { databaseList ->
                databaseList.map { entity ->
                    BitFitItem(
                        entity.itemName,
                        entity.calories
                    )
                }.also { mappedList ->
                    items.clear()
                    items.addAll(mappedList)
                    adapter.notifyDataSetChanged()
                }
            }
        }

        var editActivityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            // If the user comes back to this activity from EditActivity
            // with no error or cancellation
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                // Get the data passed from EditActivity
                if (data != null) {
                    val newItem = data.extras!!.getSerializable(TAG) as BitFitItem
                    items.add(newItem)
                    newItem.let { item ->
                        lifecycleScope.launch(IO) {
                            (application as BitFitApplication).db.bitfitDao().insert(
                                BitFitEntity(
                                    itemName = item.itemName,
                                    calories = item.calories
                                )
                            )
                        }
                    }
                }
            }
        }

        addButton.setOnClickListener {
            val intent = Intent(this, DetailActivity::class.java)
            editActivityResultLauncher.launch(intent)
        }

    }
}