import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MovementListener implements KeyListener {
    private final ScenePanel scene;

    public MovementListener(ScenePanel scene) {
        this.scene = scene;
    }

    public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e) {
        if (!scene.isRunning()) return;

        int code = e.getKeyCode();

        // Pause / Resume
        if (code == KeyEvent.VK_SPACE) {
            scene.togglePause();
            return;
        }

        // Change directions by player
        // Player 1:  Arrow keys
        if (code == KeyEvent.VK_UP)    scene.setDirectionForPlayer(1, Snake.Direction.UP);
        if (code == KeyEvent.VK_DOWN)  scene.setDirectionForPlayer(1, Snake.Direction.DOWN);
        if (code == KeyEvent.VK_LEFT)  scene.setDirectionForPlayer(1, Snake.Direction.LEFT);
        if (code == KeyEvent.VK_RIGHT) scene.setDirectionForPlayer(1, Snake.Direction.RIGHT);

        // Player 2: WASD
        if (code == KeyEvent.VK_W) scene.setDirectionForPlayer(2, Snake.Direction.UP);
        if (code == KeyEvent.VK_S) scene.setDirectionForPlayer(2, Snake.Direction.DOWN);
        if (code == KeyEvent.VK_A) scene.setDirectionForPlayer(2, Snake.Direction.LEFT);
        if (code == KeyEvent.VK_D) scene.setDirectionForPlayer(2, Snake.Direction.RIGHT);

        // Player 3: TFGH
        if (code == KeyEvent.VK_T) scene.setDirectionForPlayer(3, Snake.Direction.UP);
        if (code == KeyEvent.VK_G) scene.setDirectionForPlayer(3, Snake.Direction.DOWN);
        if (code == KeyEvent.VK_F) scene.setDirectionForPlayer(3, Snake.Direction.LEFT);
        if (code == KeyEvent.VK_H) scene.setDirectionForPlayer(3, Snake.Direction.RIGHT);

        // Player 4: IJKL
        if (code == KeyEvent.VK_I) scene.setDirectionForPlayer(4, Snake.Direction.UP);
        if (code == KeyEvent.VK_K) scene.setDirectionForPlayer(4, Snake.Direction.DOWN);
        if (code == KeyEvent.VK_J) scene.setDirectionForPlayer(4, Snake.Direction.LEFT);
        if (code == KeyEvent.VK_L) scene.setDirectionForPlayer(4, Snake.Direction.RIGHT);
    }

    public void keyReleased(KeyEvent e) {}
}
