package org.example;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SnakeGame extends JPanel implements Runnable {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int BOX_SIZE = 20;
    private static final int DELAY = 100;

    private List<Point> snake;
    private Point food;
    private int dx = BOX_SIZE;
    private int dy = 0;
    private boolean running;
    private Random rand;

    public SnakeGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        addKeyListener(new TAdapter());
        initGame();
    }

    private void initGame() {
        snake = new ArrayList<>();
        snake.add(new Point(WIDTH / 2, HEIGHT / 2));
        rand = new Random();
        generateFood();
        running = true;
    }

    private void generateFood() {
        int x = rand.nextInt(WIDTH / BOX_SIZE) * BOX_SIZE;
        int y = rand.nextInt(HEIGHT / BOX_SIZE) * BOX_SIZE;
        food = new Point(x, y);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (running) {
            drawSnake(g);
            drawFood(g);
        } else {
            gameOver(g);
        }
        Toolkit.getDefaultToolkit().sync();
    }

    private void drawSnake(Graphics g) {
        g.setColor(Color.GREEN);
        for (Point point : snake) {
            g.fillRect(point.x, point.y, BOX_SIZE, BOX_SIZE);
        }
    }

    private void drawFood(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(food.x, food.y, BOX_SIZE, BOX_SIZE);
    }

    private void gameOver(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        g.drawString("Game Over", WIDTH / 2 - 120, HEIGHT / 2);
    }

    @Override
    public void run() {
        long startTime;
        long elapsedTime;
        long waitTime;

        while (running) {
            startTime = System.nanoTime();

            update();
            repaint();

            elapsedTime = System.nanoTime() - startTime;
            waitTime = DELAY - elapsedTime / 1000000;
            if (waitTime < 0) {
                waitTime = 5;
            }

            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void update() {
        Point head = new Point(snake.get(0).x + dx, snake.get(0).y + dy);
        snake.add(0, head);
        if (head.x == food.x && head.y == food.y) {
            generateFood();
        } else {
            snake.remove(snake.size() - 1);
        }
        checkCollision();
    }

    private void checkCollision() {
        Point head = snake.get(0);
        if (head.x < 0 || head.x >= WIDTH || head.y < 0 || head.y >= HEIGHT) {
            running = false;
        }
        for (int i = 1; i < snake.size(); i++) {
            if (head.x == snake.get(i).x && head.y == snake.get(i).y) {
                running = false;
                break;
            }
        }
    }

    private class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_UP && dy == 0) {
                dx = 0;
                dy = -BOX_SIZE;
            } else if (key == KeyEvent.VK_DOWN && dy == 0) {
                dx = 0;
                dy = BOX_SIZE;
            } else if (key == KeyEvent.VK_LEFT && dx == 0) {
                dx = -BOX_SIZE;
                dy = 0;
            } else if (key == KeyEvent.VK_RIGHT && dx == 0) {
                dx = BOX_SIZE;
                dy = 0;
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("贪吃蛇游戏");
        SnakeGame game = new SnakeGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        new Thread(game).start();
    }
}
