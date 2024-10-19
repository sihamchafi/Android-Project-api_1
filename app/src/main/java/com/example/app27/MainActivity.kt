package com.example.app27

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    companion object {
        const val REQUEST_CODE_UPDATE_CAR = 100
    }
    private var cars: List<Car> = listOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getData()
        addData()
    }
    private fun getData() {
        val listView = findViewById<ListView>(R.id.lv)
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedCar = cars[position]
            Log.d("MainActivity", "Car selected: $selectedCar")
            val intent = Intent(this, UpdateCarActivity::class.java).apply {
                putExtra("car_id", selectedCar.id)
                putExtra("car_name", selectedCar.name)
                putExtra("car_price", selectedCar.price)
                putExtra("car_image", selectedCar.image)
                putExtra("car_isFulloption", selectedCar.isFullOptions)
            }
            startActivityForResult(intent, REQUEST_CODE_UPDATE_CAR)
        }
        val retrofit = Retrofit.Builder()
            .baseUrl("https://apiyes.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiService = retrofit.create(ApiService::class.java)
        apiService.getCars().enqueue(object : Callback<List<Car>> {
            override fun onResponse(call: Call<List<Car>>, response: Response<List<Car>>) {
                if (response.isSuccessful) {
                    cars = response.body() ?: emptyList()
                    Log.d("MainActivity", "Cars loaded: $cars")
                    val carNames = cars.map { "${it.name} - ${it.price} MAD" }
                    listView.adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_list_item_1, carNames)
                } else {
                    Toast.makeText(this@MainActivity, "Error retrieving data", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<Car>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Failed to connect to API", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun addData() {
        val editTextName = findViewById<EditText>(R.id.editText1)
        val editTextPrice = findViewById<EditText>(R.id.editText2)
        val editTextImageUrl = findViewById<EditText>(R.id.editText3)
        val checkbox = findViewById<CheckBox>(R.id.checkBox)
        val buttonAddCar = findViewById<Button>(R.id.buttonAdd)
        val retrofit = Retrofit.Builder()
            .baseUrl("https://apiyes.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiService = retrofit.create(ApiService::class.java)
        buttonAddCar.setOnClickListener {
            val name = editTextName.text.toString().trim()
            val priceStr = editTextPrice.text.toString().trim()
            val imageUrl = editTextImageUrl.text.toString().trim()
            val isFulloption = checkbox.isChecked
            if (name.isEmpty() || priceStr.isEmpty() || imageUrl.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                val price = priceStr.toDouble()
                val car = Car(0, name, price, imageUrl, isFulloption)
                apiService.addCar(car).enqueue(object : Callback<AddResponse> {
                    override fun onResponse(call: Call<AddResponse>, response: Response<AddResponse>) {
                        if (response.isSuccessful) {
                            Toast.makeText(applicationContext, response.body()?.status_message, Toast.LENGTH_LONG).show()
                            getData()
                        } else {
                            Toast.makeText(applicationContext, "Failed to add car", Toast.LENGTH_LONG).show()
                        }
                    }
                    override fun onFailure(call: Call<AddResponse>, t: Throwable) {
                        Toast.makeText(applicationContext, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                    }
                })
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_UPDATE_CAR && resultCode == RESULT_OK) {
            getData()
        }
    }
}