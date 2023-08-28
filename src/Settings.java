

import javax.swing.*;
import java.awt.*;

/**
 * A beállításokat kezeli: játékosok itt adhatják meg a nevüket, korongjaik színét
 */
public class Settings extends JPanel {
    private final Frame frame;
    private final String gameMode;
    private final JTextField namePlayer1 = new JTextField("Egyik játékos neve", 20);
    private final JTextField namePlayer2 = new JTextField("Másik játékos neve", 20);
    private static final ManColor[] man_color = ManColor.values();
    private final JComboBox<ManColor> colourPlayer1 = new JComboBox<>(man_color);
    private final JComboBox<ManColor> colourPlayer2 = new JComboBox<>(man_color);

    /**
     * Tárolja, hogy milyen játékmódot (gameMode) választott a játékos és, hogy melyik keret (frame) felelős a játékért
     * @param frame: nem lehet null
     * @param gameMode: nem lehet null
     */
    public Settings(Frame frame, String gameMode) {
        this.frame = frame;
        this.gameMode = gameMode;
        add(namePlayer1, BorderLayout.NORTH);
        add(namePlayer2, BorderLayout.NORTH);
        add(colourPlayer1, BorderLayout.NORTH);
        add(colourPlayer2, BorderLayout.NORTH);
        JButton start = new JButton("Start Game");
        start.addActionListener(e -> startGame()); // e->startGame()-ből létrejön 1 osztály (ez egy ActionListener példány), aminek csak 1 fv-e van
        add(start, BorderLayout.SOUTH);
    }

    /**
     * A "Start Game" hatására kialakítja a két játékost, ha különböző a nevük és a színük is, majd megjeleníti a GamePlay-t
     * Ha nem különböző valamelyik, üzenetet ír nekik, hogy figyeljenek erre.
     */
    public void startGame() {
        if (colourPlayer1.getSelectedItem() != colourPlayer2.getSelectedItem() && !namePlayer1.getText().equals("") && !namePlayer2.getText().equals("")
                && !namePlayer1.getText().equals(namePlayer2.getText())) {
            Player p1 = new HumanPlayer(namePlayer1.getText(), (ManColor) colourPlayer1.getSelectedItem());
            Player p2 = gameMode.equals("PvP") ?
                    new HumanPlayer(namePlayer2.getText(), (ManColor) colourPlayer2.getSelectedItem()) :
                    new AIPlayer(namePlayer2.getText(), (ManColor) colourPlayer2.getSelectedItem());
            GamePlay gp = new GamePlay(p1, p2, this.frame, p1, false);
            frame.addToCards(gp, "GAMEPLAY_PANEL");
            frame.nextCard();
        } else {
            add(new JTextField("Figyelj, hogy a nevetek és színetek is különböző legyen."), BorderLayout.CENTER);
            revalidate();
        }
    }
}