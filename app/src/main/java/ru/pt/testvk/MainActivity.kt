package ru.pt.testvk

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout

class MainActivity : AppCompatActivity() {

    private lateinit var layout: ConstraintLayout
    private lateinit var clock3: CustomClockView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        layout = findViewById(R.id.layout_main_activity)
        //Программное создание View
        val customClockView = CustomClockView(this)
        layout.addView(customClockView)

        //Тестирование параметра isCircle на нижних растянутых часах
        clock3 = findViewById(R.id.clock)
        clock3.isCircle = true
    }

}