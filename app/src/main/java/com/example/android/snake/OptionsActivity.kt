package com.example.android.snake

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.android.snake.view.SnakeView.Companion.FAST
import com.example.android.snake.view.SnakeView.Companion.MEDIUM
import com.example.android.snake.view.SnakeView.Companion.SLOW
import com.example.android.snake.view.SnakeView.Companion.VERY_FAST
import kotlinx.android.synthetic.main.activity_options.*

class OptionsActivity : AppCompatActivity() {
    companion object {
        const val SPEED = "speed"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options)
        startButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            val speed = when(speedBar.progress) {
                0 -> SLOW
                1 -> MEDIUM
                2 -> FAST
                3 -> VERY_FAST
                else -> throw IllegalArgumentException("To satisfy compiler")
            }
            intent.putExtra(SPEED, speed)
            startActivity(intent)
        }
    }
}
