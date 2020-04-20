package com.example.android.snake.model;

import android.graphics.Point;

import java.util.Random;

public class SnakeTable {
    public static final int BONUS = -3;
    public static final int HEAD = -1;
    public static final int EMPTY = 0;

    public static final int LEFT = 1;
    public static final int UP = 2;
    public static final int RIGHT = 3;
    public static final int DOWN = 4;

    public static final int DEFAULT_WIDTH = 40;
    public static final int DEFAULT_HEIGHT = 20;

    public static final int SCORE_SMALL = 5;
    public static final int SCORE_MEDIUM = 10;
    public static final int SCORE_LARGE = 20;

    public SnakeTable() {
        table = new int[DEFAULT_HEIGHT][DEFAULT_WIDTH];
        width = DEFAULT_WIDTH;
        height = DEFAULT_HEIGHT;
        bonusScore = SCORE_MEDIUM;
        initStartSnake();


    }

    public SnakeTable(int w, int h) {
        table = new int[h][w];
        width = w;
        height = h;
        bonusScore = SCORE_MEDIUM;
        initStartSnake();
    }

    private void initStartSnake() {
        Point pt = new Point(width / 2, height / 2);
        head = new Point(pt);
        put(head, HEAD);

        pt.x--;
        put(pt, RIGHT);

        pt.x--;
        put(pt, RIGHT);

        pt.x--;
        put(pt, RIGHT);

        tail = new Point(pt);
        bonus = generateBonus();
        put(bonus, BONUS);
        moveDirection = RIGHT;

    }

    private SnakeListener listener;

    private int width;
    private int height;

    private Point head;
    private Point tail;
    private Point bonus;
    private int bonusScore;
    private int score = 0;

    private int moveDirection = RIGHT;

    //In each element of the matrix we will memorize EMPTY if there is no snake or bonus located there
    //or the direction of the next point going from tail to head
    private int[][] table;



    public void advance() {
        Point newHead = next(head, moveDirection);
        if (bonus.equals(newHead)) {
            score += bonusScore;
            put(newHead, HEAD);
            Point append = next(tail, opposite(at(tail)));
            put(append, at(tail));
            tail = new Point(append);
            moveSnake();

            bonus = generateBonus();
            put(bonus, BONUS);
            return;
        }
        if (at(newHead) != EMPTY) /*if snake hits itself*/ {
            listener.onSnakeDead();
            return;
        }

        moveSnake();

    }

    private Point next(Point pt) {
        int element = table[pt.x][pt.y];
        if (!isSnakeBody(pt))
            throw new UnsupportedOperationException("This operation is available only for snake body");

        return next(pt, element);

    }

    private void moveSnake() {
        int direction = moveDirection;
        Point h = next(head, direction);
        put(head, direction);
        head = h;
        Point t = new Point(tail);
        tail = next(tail, at(tail));
        put(t, EMPTY);
        put(h, HEAD);
    }

    private int opposite(int direction) {
        return (direction + 1) % 4 + 1;
    }

    private Point generateBonus() {
        Random rand = new Random();
        int randX = rand.nextInt(width);
        int randY = rand.nextInt(height);
        boolean notEmpty = at(new Point(randX, randY)) != EMPTY;
        while (notEmpty) {
            randX = rand.nextInt(width);
            randY = rand.nextInt(height);
            notEmpty = at(new Point(randX, randY)) != EMPTY;
        }

        return new Point(randX, randY);

    }

    /**
     * @param pt
     * @param direction
     * @return The neighboring point relative to the direction parameter, snake wall-through behavior is included
     */
    private Point next(Point pt, int direction) {
        int x = pt.x, y = pt.y;
        switch (direction) {
            case UP: {
                y--;
            }break;
            case DOWN: {
                y++;
            }break;
            case LEFT: {
                x--;
            }break;
            case RIGHT: {
                x++;
            }break;
        }

        if (x < 0) x = width - 1;
        if (x == width) x = 0;
        if (y < 0) y = height - 1;
        if (y == height) y = 0;
        return new Point(x, y);
    }

    public int at(Point pt) {
        return table[pt.y][pt.x];
    }

    public int at(int x, int y) { return table[y][x];}

    private void put(Point pt, int val) {
        table[pt.y][pt.x] = val;
    }

    private boolean isSnakeBody(Point pt) {
        int x = pt.x, y = pt.y;
        return table[x][y] == LEFT || table[x][y] == RIGHT || table[x][y] == UP || table[x][y] == DOWN;
    }

    public SnakeListener getListener() {
        return listener;
    }

    public void setListener(SnakeListener listener) {
        this.listener = listener;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Point getBonus() {
        return bonus;
    }

    public void setBonus(Point bonus) {
        this.bonus = bonus;
    }

    public int getMoveDirection() {
        return moveDirection;
    }

    public void setMoveDirection(int moveDirection) {
        this.moveDirection = moveDirection;
    }

    public interface SnakeListener {
        void onSnakeDead();
    }

}
