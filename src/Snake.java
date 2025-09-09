import java.awt.*;
import java.util.LinkedList;

public class Snake {
    public enum Direction { UP, DOWN, LEFT, RIGHT }

    private int id;
    private LinkedList<Point> body;
    private Direction dir;
    private boolean alive;
    private long speedMillis;
    private Color color;
    private int score;

    public Snake (int id, Point start, Direction dir, Color color, long speedMillis) {
        this.id = id;
        this.dir = dir;
        this.color = color;
        this.speedMillis = speedMillis;
        this.alive = true;
        this.score = 0;
        this.body = new LinkedList<>();
        this.body.add(start);
        for (int i = 0; i < 3; i++) this.body.addLast(new Point(start));
    }

    // Getters/Setters
    public int getId() { return id; }
    public LinkedList<Point> getBody() { return body; }
    public Point getHead() { return body.peekFirst(); }
    public Direction getDir() { return dir; }
    public void setDir(Direction dir) { this.dir = dir; }
    public boolean isAlive() { return alive; }
    public void setAlive(boolean alive) { this.alive = alive; }
    public long getSpeedMillis() { return speedMillis; }
    public void setSpeedMillis(long speedMillis) { this.speedMillis = speedMillis; }
    public Color getColor() { return color; }
    public void setColor(Color color) { this.color = color; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public static boolean isOpposite(Direction a, Direction b) {
        return (a==Direction.UP && b==Direction.DOWN) ||
                (a==Direction.DOWN && b==Direction.UP) ||
                (a==Direction.LEFT && b==Direction.RIGHT) ||
                (a==Direction.RIGHT && b==Direction.LEFT);
    }
}
