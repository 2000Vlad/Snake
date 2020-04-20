package com.example.android.snake

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.core.view.GestureDetectorCompat
import com.example.android.snake.OptionsActivity.Companion.SPEED
import com.example.android.snake.model.SnakeTable
import com.example.android.snake.model.SnakeTable.*
import com.example.android.snake.view.SnakeView
import com.example.android.snake.view.SnakeView.Companion.MEDIUM
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.abs

class MainActivity : AppCompatActivity(), GestureDetector.OnGestureListener {

    companion object {
        const val SWIPE_LENGTH = 8
        const val SWIPE_EPSILON = 88
    }

    private val detector: GestureDetectorCompat by lazy { GestureDetectorCompat(this, this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /*snakeTable.scoreListener = this::updateScore
        snakeTable.endListener = {

        }*/
        snakeTable.setOnTouchListener { v, event ->
            detector.onTouchEvent(event)
             true
        }
        snakeTable.scoreListener = this::updateScore
        snakeTable.speed = intent.getLongExtra(SPEED, MEDIUM)
        snakeTable.start()

    }

    private fun updateScore(score: Int) {
        scoreView.text = score.toString()
    }

    override fun onShowPress(e: MotionEvent?) {
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        return true
    }

    override fun onDown(e: MotionEvent?): Boolean {
        return true
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        if(e1 == null || e2 == null) return true
        if(isSwipeLeft(e1, e2) && snakeTable.table.moveDirection != RIGHT)
            snakeTable.table.moveDirection = LEFT
        if(isSwipeRight(e1, e2) && snakeTable.table.moveDirection != LEFT)
            snakeTable.table.moveDirection = RIGHT
        if(isSwipeUp(e1, e2) && snakeTable.table.moveDirection != DOWN)
            snakeTable.table.moveDirection = UP
        if(isSwipeDown(e1, e2) && snakeTable.table.moveDirection != UP)
            snakeTable.table.moveDirection = DOWN
        return true
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        return true
    }

    override fun onLongPress(e: MotionEvent?) {
        TODO("Not yet implemented")
    }

    fun isSwipeLeft(start: MotionEvent, end: MotionEvent): Boolean {
        val x1 = start.x
        val x2 = end.x
        val y1 = start.y
        val y2 = end.y

        if (x1 < x2) return false
        val xDelta = abs(x1 - x2)
        val yDelta = abs(y1 - y2)

        return xDelta > SWIPE_LENGTH && yDelta < SWIPE_EPSILON
    }

    fun isSwipeRight(start: MotionEvent, end: MotionEvent): Boolean {
        val x1 = start.x
        val x2 = end.x
        val y1 = start.y
        val y2 = end.y

        if (x2 < x1) return false
        val xDelta = abs(x1 - x2)
        val yDelta = abs(y1 - y2)

        return xDelta > SWIPE_LENGTH && yDelta < SWIPE_EPSILON
    }

    fun isSwipeUp(start: MotionEvent, end: MotionEvent): Boolean {
        val x1 = start.x
        val x2 = end.x
        val y1 = start.y
        val y2 = end.y

        if(y2 > y1) return false

        val xDelta = abs(x1 - x2)
        val yDelta = abs(y1 - y2)

        return yDelta > SWIPE_LENGTH && xDelta < SWIPE_EPSILON

    }

    fun isSwipeDown(start: MotionEvent, end: MotionEvent): Boolean {
        val x1 = start.x
        val x2 = end.x
        val y1 = start.y
        val y2 = end.y

        if(y2 < y1) return false

        val xDelta = abs(x1 - x2)
        val yDelta = abs(y1 - y2)

        return yDelta > SWIPE_LENGTH && xDelta < SWIPE_EPSILON

    }

}
