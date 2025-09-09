import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.Random;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class ScenePanel extends JPanel {
    // Board (Grid)
    public static final int TILE = 20;
    public static final int COLS = 33;
    public static final int ROWS = 30;

    // Runtime state
    private volatile boolean running;
    private volatile boolean paused;
    private volatile boolean gameOver;

    // Players (snakes)
    private Snake[] snakes = new Snake[0];
    private int players = 0;

    // Food
    private Point food;
    private final Random rng = new Random();

    // Base speed by difficulty
    private long baseSpeedMillis = 120; // NORMAL default
    private final long minSpeedMillis = 60;

    public ScenePanel (int x, int y, int width, int height) {
        this.setBounds(x, y, width, height);
        this.setLayout(null);
        this.running = false;
        this.paused = false;
        this.gameOver = false;

        this.setFocusable(true);
        this.addKeyListener(new MovementListener(this));

        // Initial background paint
        repaint();
    }

    // === Menu control ===
    public void startGame(int players, String difficulty, Color[] colors) {
        this.players = players;
        this.baseSpeedMillis = switch (difficulty) {
            case "EASY" -> 160;
            case "HARD" -> 90;
            case "INSANE" -> 70;
            default -> 120; // NORMAL
        };

        // Create snakes
        snakes = new Snake[players];
        Point[] starts = new Point[] {
                new Point(COLS/4, ROWS/2),
                new Point(3*COLS/4, ROWS/2),
                new Point(COLS/2, ROWS/4),
                new Point(COLS/2, 3*ROWS/4)
        };
        Snake.Direction[] dirs = new Snake.Direction[] {
                Snake.Direction.RIGHT, Snake.Direction.LEFT,
                Snake.Direction.DOWN,  Snake.Direction.UP
        };
        for (int i = 0; i < players; i++) {
            snakes[i] = new Snake(i+1, starts[i], dirs[i], colors[i], baseSpeedMillis);
        }

        spawnFood();
        this.running = true;
        this.paused = false;
        this.gameOver = false;

        startGameLoop();
    }

    public void togglePause() {
        if (running && !gameOver) {
            paused = !paused;
            repaint();
        }
    }

    public void resetGame() {
        running = false;
        paused = false;
        gameOver = false;
        snakes = new Snake[0];
        repaint();
        Toolkit.getDefaultToolkit().beep();
    }

    // === Keyboard input ===
    public void setDirectionForPlayer(int playerId, Snake.Direction nd) {
        for (Snake s : snakes) {
            if (s.getId() == playerId && s.isAlive() && !Snake.isOpposite(s.getDir(), nd)) {
                s.setDir(nd);
            }
        }
    }

    public boolean isRunning() { return running; }

    // === Game loop ===
    private void startGameLoop () {
        new Thread(() -> {
            long[] nextStepAt = new long[snakes.length];
            long now = System.currentTimeMillis();
            for (int i = 0; i < snakes.length; i++) nextStepAt[i] = now + snakes[i].getSpeedMillis();

            while (running && !gameOver) {
                if (!paused) {
                    now = System.currentTimeMillis();
                    for (int i = 0; i < snakes.length; i++) {
                        Snake s = snakes[i];
                        if (!s.isAlive()) continue;
                        if (now >= nextStepAt[i]) {
                            stepSnake(s);
                            nextStepAt[i] = now + s.getSpeedMillis();
                        }
                    }
                    repaint();
                }
                try { Thread.sleep(10); } catch (InterruptedException ignored) {}
            }
        }, "GameLoop").start();
    }

    private void stepSnake(Snake s) {
        Point head = new Point(s.getHead());
        switch (s.getDir()) {
            case UP -> head.y -= 1;
            case DOWN -> head.y += 1;
            case LEFT -> head.x -= 1;
            case RIGHT -> head.x += 1;
        }

        // Walls
        if (head.x < 0 || head.x >= COLS || head.y < 0 || head.y >= ROWS) {
            s.setAlive(false); checkGameOver(); return;
        }

        // Any snake body / self
        if (isOccupied(head)) {
            s.setAlive(false); checkGameOver(); return;
        }

        // Add head
        s.getBody().addFirst(head);

        // Food?
        if (food != null && head.equals(food)) {
            s.setScore(s.getScore()+1);
            accelerate(s);
            Toolkit.getDefaultToolkit().beep(); // eat effect
//            playEatSound();
            spawnFood();
        } else {
            // Move tail (to advance forward)
            s.getBody().removeLast();
        }
    }

    private void accelerate(Snake s) {
        long ns = Math.max(minSpeedMillis, (long)(s.getSpeedMillis() * 0.94)); // ~6%
        s.setSpeedMillis(ns);
    }

    private boolean isOccupied(Point cell) {
        for (Snake other : snakes) {
            LinkedList<Point> body = other.getBody();
            for (Point p : body) {
                if (p.equals(cell)) return true;
            }
        }
        return false;
    }

    private void spawnFood() {
        for (int tries = 0; tries < 5000; tries++) {
            Point p = new Point(rng.nextInt(COLS), rng.nextInt(ROWS));
            if (!isOccupied(p)) { food = p; return; }
        }
        food = null;
    }

    private void checkGameOver() {
        int aliveCount = 0;
        for (Snake s : snakes) if (s.isAlive()) aliveCount++;
        if (aliveCount <= 1) {
            gameOver = true;
            Toolkit.getDefaultToolkit().beep();
            new Thread(() -> {
                try { Thread.sleep(200);} catch (Exception ignored) {}
                Toolkit.getDefaultToolkit().beep();
            }).start();
        }
    }

    private void drawFood(Graphics2D g2, Point food) {
        int x = food.x * TILE;
        int y = food.y * TILE;

        // fruit
        g2.setColor(new Color(220, 40, 40));
        g2.fillOval(x + 3, y + 4, TILE - 6, TILE - 6);

        // leaf
        g2.setColor(new Color(80, 160, 80));
        g2.fillOval(x + TILE/2, y + 2, TILE/4, TILE/4);
    }

    // === Painting ===
    public void paintComponent (Graphics g) {
        super.paintComponent(g);

        // Background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Grid
        g.setColor(new Color(30,30,30));
        for (int x=0;x<=COLS;x++) g.drawLine(x*TILE, 0, x*TILE, ROWS*TILE);
        for (int y=0;y<=ROWS;y++) g.drawLine(0, y*TILE, COLS*TILE, y*TILE);

        // Food
        if (food != null) {
//            g.setColor(new Color(0xff6b6b));
//            fillCell(g, food.x, food.y);
            drawFood((Graphics2D) g, food);
        }

        // Snakes
        for (Snake s : snakes) {
            g.setColor(s.getColor());
            for (Point p : s.getBody()) fillCell(g, p.x, p.y);
            if (s.isAlive() && s.getHead()!=null) {
                g.setColor(Color.WHITE);
                drawCell(g, s.getHead().x, s.getHead().y);
            }
        }

        // HUD – scores
        g.setColor(Color.WHITE);
        int yy = 16;
        for (Snake s : snakes) {
            String status = s.isAlive() ? "" : " (מת)";
            g.drawString("P"+s.getId()+" score: "+s.getScore()+status, 8, yy);
            yy += 16;
        }

        if (paused)   drawCenterText(g, "PAUSED (Space)", 26);
        if (gameOver) drawCenterText(g, "GAME OVER", 28);
    }

    private void fillCell(Graphics g, int cx, int cy) {
        g.fillRect(cx*TILE+1, cy*TILE+1, TILE-2, TILE-2);
    }
    private void drawCell(Graphics g, int cx, int cy) {
        g.drawRect(cx*TILE+1, cy*TILE+1, TILE-2, TILE-2);
    }
    private void drawCenterText(Graphics g, String text, int size) {
        Font f = g.getFont().deriveFont(Font.BOLD, size);
        g.setFont(f);
        FontMetrics fm = g.getFontMetrics(f);
        int tx = (getWidth()-fm.stringWidth(text))/2;
        int ty = (getHeight()-fm.getHeight())/2 + fm.getAscent();
        g.setColor(Color.WHITE);
        g.drawString(text, tx, ty);
    }
}
