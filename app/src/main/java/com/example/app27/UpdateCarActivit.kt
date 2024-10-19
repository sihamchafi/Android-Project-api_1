package com.example.app27

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UpdateCarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_car)

        val editTextName = findViewById<EditText>(R.id.editTextName)
        val editTextPrice = findViewById<EditText>(R.id.editTextPrice)
        val editTextImageUrl = findViewById<EditText>(R.id.editTextImageUrl)
        val checkboxFullOption = findViewById<CheckBox>(R.id.checkboxFullOption)
        val buttonUpdateCar = findViewById<Button>(R.id.buttonUpdateCar)
        val buttonDeleteCar = findViewById<Button>(R.id.buttonDeleteCar)

        val carId = intent.getIntExtra("car_id", 0)
        val carName = intent.getStringExtra("car_name")
        val carPrice = intent.getDoubleExtra("car_price", 0.0)
        val carImage = intent.getStringExtra("car_image")
        val carIsFulloption = intent.getBooleanExtra("car_isFulloption", false)

        editTextName.setText(carName)
        editTextPrice.setText(carPrice.toString())
        editTextImageUrl.setText(carImage)
        checkboxFullOption.isChecked = carIsFulloption

        val retrofit = Retrofit.Builder()
            .baseUrl("https://apiyes.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        buttonUpdateCar.setOnClickListener {
            val updatedName = editTextName.text.toString().trim()
            val updatedPriceStr = editTextPrice.text.toString().trim()
            val updatedImageUrl = editTextImageUrl.text.toString().trim()
            val isFullOption = checkboxFullOption.isChecked

            if (updatedName.isEmpty() || updatedPriceStr.isEmpty() || updatedImageUrl.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                val updatedPrice = updatedPriceStr.toDouble()

                val updatedCar = Car(carId, updatedName, updatedPrice, updatedImageUrl, isFullOption)

                apiService.updateCar(updatedCar).enqueue(object : Callback<AddResponse> {
                    override fun onResponse(call: Call<AddResponse>, response: Response<AddResponse>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@UpdateCarActivity, "Car updated successfully", Toast.LENGTH_LONG).show()
                            setResult(RESULT_OK)
                            finish()
                        } else {
                            Toast.makeText(this@UpdateCarActivity, "Failed to update car", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<AddResponse>, t: Throwable) {
                        Toast.makeText(this@UpdateCarActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                    }
                })
            }
        }

        // Delete car functionality
        buttonDeleteCar.setOnClickListener {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://apiyes.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val apiService1 = retrofit.create(ApiService::class.java)
            val carToDelete = Car(id = carId, name = "", price = 0.0, image = "",isFullOptions = false)



            apiService1.deleteCar(carToDelete).enqueue(object : Callback<AddResponse> {
                override fun onResponse(call: Call<AddResponse>, response: Response<AddResponse>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@UpdateCarActivity, "Car deleted successfully", Toast.LENGTH_LONG).show()
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(this@UpdateCarActivity, "Failed to delete car", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<AddResponse>, t: Throwable) {
                    Toast.makeText(this@UpdateCarActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }
    }
}