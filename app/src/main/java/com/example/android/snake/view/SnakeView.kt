package com.example.android.snake.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import android.view.View.MeasureSpec.*
import com.example.android.snake.R
import com.example.android.snake.model.SnakeTable
import com.example.android.snake.model.SnakeTable.*
import java.util.concurrent.*
import kotlin.math.min

class SnakeView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    companion object {
        const val DEFAULT_BONUS_COLOR = Color.GREEN
        const val DEFAULT_SNAKE_HEAD_COLOR = Color.YELLOW
        const val DEFAULT_SNAKE_BODY_COLOR = Color.RED
        const val PREF_WIDTH = 800
        const val PREF_HEIGHT = 800
        const val PREF_CELL_SIZE = 40

        const val VERY_FAST = 42L
        const val FAST = 84L
        const val MEDIUM = 128L
        const val SLOW = 216L
    }

    lateinit var table: SnakeTable
    private var executor: ScheduledExecutorService

    var snakeFuture: ScheduledFuture<*>? = null
    var tableWidth: Int = DEFAULT_WIDTH
    var tableHeight: Int = DEFAULT_HEIGHT
    var bonusColor: Int
    var snakeHeadColor: Int
    var snakeBodyColor: Int
    var scoreListener: (Int) -> Unit = {}
    var endListener: () -> Unit = {}
    var speed = MEDIUM


    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.SnakeView)
        tableWidth = ta.getInteger(R.styleable.SnakeView_tableWidth, DEFAULT_WIDTH)
        tableHeight = ta.getInteger(R.styleable.SnakeView_tableHeight, DEFAULT_HEIGHT)
        bonusColor = ta.getColor(R.styleable.SnakeView_bonusColor, DEFAULT_BONUS_COLOR)
        snakeHeadColor = ta.getColor(R.styleable.SnakeView_snakeHeadColor, DEFAULT_SNAKE_HEAD_COLOR)
        snakeBodyColor = ta.getColor(R.styleable.SnakeView_snakeBodyColor, DEFAULT_SNAKE_BODY_COLOR)
        executor = Executors.newScheduledThreadPool(5)

        table = SnakeTable(tableWidth, tableHeight)

        ta.recycle()
    }

    fun start() {
        table.listener = SnakeTable.SnakeListener(this::onEndListener)
        snakeFuture = executor.scheduleAtFixedRate(this::update, 0, speed, TimeUnit.MILLISECONDS)
    }

    private fun update() {
        table.advance()

        val handler = Handler(Looper.getMainLooper())
        handler.post(){
            scoreListener(table.score)
        }


        postInvalidate()

    }

    private fun onEndListener() {
        if (snakeFuture != null) {
            snakeFuture!!.cancel(true)
            snakeFuture = null
            endListener()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = getMode(widthMeasureSpec)
        val heightMode = getMode(heightMeasureSpec)

        val widthSize = getSize(widthMeasureSpec)
        val heightSize = getSize(heightMeasureSpec)
        var width = 0
        var height = 0

        width = when (widthMode) {
            AT_MOST -> min(PREF_WIDTH, widthSize)
            EXACTLY -> widthSize
            UNSPECIFIED -> PREF_WIDTH
            else -> -1
        }

        when (heightMode) {
            AT_MOST -> {
                val smoothHeight = (width * tableHeight) / tableWidth
                if (smoothHeight <= heightSize) height = smoothHeight
                else {
                    val smoothWidth = (heightSize * tableWidth) / tableHeight
                    if (smoothWidth <= widthSize) {
                        width = smoothWidth
                        height = heightSize
                    }

                }
            }
            EXACTLY -> height = heightSize
            UNSPECIFIED -> height = (width * tableHeight) / tableWidth
            else -> height = -1
        }

        setMeasuredDimension(width, height)

    }

    private val cellRect = Rect(-1, -1, -1, -1)
    private val cellPaint = Paint()
    override fun onDraw(canvas: Canvas?) {
        if (canvas == null) return
        val cellWidth = width / tableWidth
        val cellHeight = height / tableHeight
        //var x = 0
        //var y = 0
        var left = 0
        var top = 0
        var right: Int
        var bottom: Int

        for (y in 0 until tableHeight)
            for (x in 0 until tableWidth) {
                left = cellWidth * x
                top = cellHeight * y
                right = cellWidth * (x + 1)
                bottom = cellHeight * (y + 1)
                cellRect.left = left
                cellRect.top = top
                cellRect.right = right
                cellRect.bottom = bottom
                when {
                    table.at(x, y) == HEAD ->
                        cellPaint.color = snakeHeadColor
                    table.at(x, y) == BONUS ->
                        cellPaint.color = bonusColor
                    table.at(x, y) != EMPTY ->
                        cellPaint.color = snakeBodyColor
                    else ->
                        cellPaint.color = Color.WHITE
                }
                canvas.drawRect(cellRect, cellPaint)
            }


    }

    interface OnScoreUpdateListener {
        fun onScoreUpdate(score: Int)
    }

    interface OnEndListener {
        fun onSnakeDead()
    }

}

