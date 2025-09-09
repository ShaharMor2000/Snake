import javax.swing.*;
import java.awt.*; // For BorderLayout, Dimension

public class Main {
    public static final int WINDOW_WIDTH = 900;
    public static final int WINDOW_HEIGHT = 640;

    public static void main(String[] args) {
        JFrame window = new JFrame("Snake (Class Style)");
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setResizable(false);

        // Proper layout
        window.setLayout(new BorderLayout());
        window.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));

        // Use existing constructors (with parameters)
        MenuPanel menuPanel = new MenuPanel(0, 0, WINDOW_WIDTH / 4, WINDOW_HEIGHT);
        menuPanel.setPreferredSize(new Dimension(WINDOW_WIDTH / 4, WINDOW_HEIGHT));

        ScenePanel scenePanel = new ScenePanel(0, 0, (WINDOW_WIDTH * 3) / 4, WINDOW_HEIGHT);
        scenePanel.setPreferredSize(new Dimension((WINDOW_WIDTH * 3) / 4, WINDOW_HEIGHT));

        // Wire menu to scene (start/pause/stop callbacks)
        menuPanel.setScenePanel(scenePanel);

        // Add to frame
        window.add(menuPanel, BorderLayout.WEST);
        window.add(scenePanel, BorderLayout.CENTER);

        // Finalize frame
        window.pack();                 // Instead of setSize
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        // Focus game panel
        scenePanel.setFocusable(true);
        scenePanel.requestFocusInWindow();
    }
}
