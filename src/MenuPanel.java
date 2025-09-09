import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class MenuPanel extends JPanel {
    private ScenePanel scenePanel;

    private JComboBox<Integer> playersBox;
    private JComboBox<String> difficultyBox;
    private JButton startBtn, pauseBtn, stopBtn;

    private JButton[] colorBtns = new JButton[4];
    private Color[] playerColors = new Color[] {
            new Color(0x1abc9c), new Color(0xe74c3c),
            new Color(0x3498db), new Color(0xf1c40f)
    };

    public void setScenePanel(ScenePanel scenePanel) {
        this.scenePanel = scenePanel;
    }

    public MenuPanel(int x, int y, int width, int height) {
        // Bounds are kept for compatibility; layout managers decide the final size
        this.setBounds(x, y, width, height);

        // Main layout: instructions at top (NORTH), controls in CENTER
        this.setLayout(new BorderLayout(8, 8));
        this.setPreferredSize(new Dimension(width, height));
        this.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        // ========= Instructions (top) =========
        JTextArea howto = new JTextArea();
        howto.setEditable(false);
        howto.setLineWrap(true);
        howto.setWrapStyleWord(true);
        howto.setBackground(new Color(0xF5F5F5));
        howto.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        howto.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        howto.setFont(howto.getFont().deriveFont(Font.BOLD, 15f));
        howto.setText(
                "Welcome to the Snake game!\n" +
                        "This is a classic game where each player controls a snake in their chosen color.\n" +
                        "Eat food to grow, avoid collisions, and try to outlast your opponents.\n" +
                        "The goal is to be the longest snake or the last one remaining in the game.\n\n" +
                        "Game Instructions — Step by Step:\n" +
                        "1) Select the number of players.\n" +
                        "2) Choose a difficulty level.\n" +
                        "3) Click the color button to choose a color for each player.\n" +
                        "4) Press Start to begin.\n\n" +
                        "Objective: Eat food, grow longer, and avoid collisions.\n" +
                        "Each player competes to defeat the others — the last one alive or with the highest score wins.\n" +
                        "Do not crash into walls, yourself, or other players!\n\n" +
                        "Controls:\n" +
                        "• Player 1: Arrow keys ← ↑ → ↓\n" +
                        "• Player 2: W , A , S , D\n" +
                        "• Player 3: T , F , G , H\n" +
                        "• Player 4: I , J , K , L\n" +
                        "Space — Pause | Reset — Restart the game."
        );

        JScrollPane howtoScroll = new JScrollPane(howto);
        howtoScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Game instructions",
                javax.swing.border.TitledBorder.CENTER, // centered title
                javax.swing.border.TitledBorder.TOP,
                howto.getFont().deriveFont(Font.BOLD, 16f)
        ));
        howtoScroll.setPreferredSize(new Dimension(width, 230));

        // ========= Controls panel (buttons/combos) =========
        JPanel controls = new JPanel(new GridLayout(12, 1, 6, 6));
        controls.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JLabel title = new JLabel("Snake", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));

        playersBox = new JComboBox<>(new Integer[]{1, 2, 3, 4});
        difficultyBox = new JComboBox<>(new String[]{"EASY", "NORMAL", "HARD", "INSANE"});

        Font controlsFont = title.getFont().deriveFont(Font.PLAIN, 14f);
        playersBox.setFont(controlsFont);
        difficultyBox.setFont(controlsFont);

        startBtn = new JButton("Start");
        pauseBtn = new JButton("Pause (Space)");
        stopBtn = new JButton("Reset");
        startBtn.setFont(controlsFont);
        pauseBtn.setFont(controlsFont);
        stopBtn.setFont(controlsFont);

        // Color picker buttons for up to 4 players
        for (int i = 0; i < colorBtns.length; i++) {
            int idx = i;
            JButton b = new JButton("Click to choose color Player " + (i + 1) );
            b.setBackground(playerColors[i]);
            b.setForeground(contrast(playerColors[i]));
            b.setFont(controlsFont);
            b.addActionListener(e -> {
                Color c = JColorChooser.showDialog(this, "Choose a color for the player " + (idx + 1), playerColors[idx]);
                if (c != null) {
                    playerColors[idx] = c;
                    b.setBackground(c);
                    b.setForeground(contrast(c));
                }
            });
            colorBtns[i] = b;
        }

        // Actions wiring
        ActionListener startAction = e -> {
            if (this.scenePanel != null) {
                int players = (Integer) playersBox.getSelectedItem();
                String diff = (String) difficultyBox.getSelectedItem();
                this.scenePanel.startGame(players, diff, playerColors);
                this.scenePanel.requestFocusInWindow();
            }
        };
        startBtn.addActionListener(startAction);

        pauseBtn.addActionListener(e -> {
            if (this.scenePanel != null) this.scenePanel.togglePause();
            this.scenePanel.requestFocusInWindow();
        });

        stopBtn.addActionListener(e -> {
            if (this.scenePanel != null) this.scenePanel.resetGame();
            this.scenePanel.requestFocusInWindow();
        });

        // Controls layout (with centered labels)
        controls.add(title);

        JLabel playersLbl = new JLabel("Numbers of Players:", SwingConstants.CENTER);
        playersLbl.setFont(controlsFont);
        controls.add(playersLbl);
        controls.add(playersBox);

        JLabel diffLbl = new JLabel("Difficulty level:", SwingConstants.CENTER);
        diffLbl.setFont(controlsFont);
        controls.add(diffLbl);
        controls.add(difficultyBox);

        for (JButton b : colorBtns) controls.add(b);
        controls.add(startBtn);
        controls.add(pauseBtn);
        controls.add(stopBtn);

        // ========= Compose MenuPanel =========
        this.add(howtoScroll, BorderLayout.NORTH);   // instructions first
        this.add(controls, BorderLayout.CENTER);     // then settings
    }

    // Choose black/white text based on background color luminance
    private static Color contrast(Color c) {
        double yiq = ((c.getRed() * 299) + (c.getGreen() * 587) + (c.getBlue() * 114)) / 1000.0;
        return yiq >= 128 ? Color.BLACK : Color.WHITE;
    }
}
