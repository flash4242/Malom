

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;


/**
 * A menüt kezeli
 */
public class Menu extends JPanel {
    private final Frame frame;
    private Settings settings;

    /**
     * Létrehozza a menühöz kellő komponenseket és kezeli, ha esemény van rajtuk
     * @param frame: a frame amihez tartozik, null nem lehet
     */
    public Menu(Frame frame) {
        this.frame=frame;

        JTextField options = new JTextField("Choose your game mode", 50);
        options.setEditable(false);
        add(options, BorderLayout.NORTH);
        
        //Buttons: Pvp or PvBot
        JButton PvP_button = new JButton("Player vs. Player");
        add(PvP_button, BorderLayout.SOUTH);
        
        JButton PvBot_button = new JButton("Player vs. Bot");
        add(PvBot_button, BorderLayout.SOUTH);

        PvP_button.setActionCommand("PvP");
        PvP_button.addActionListener(this::goToSettings);
        PvBot_button.setActionCommand("PvBot");
        PvBot_button.addActionListener(this::goToSettings);
    }

    /**
     * A megfelelő gombok lenyomásánka hatására átvisz a Settings-be
     * @param e: az esemény amit végrehajtottak, null nem lehet
     */
    public void goToSettings(ActionEvent e) {
        String gameMode = e.getActionCommand();
        settings = new Settings(frame, gameMode);
        frame.addToCards(settings, "SETTINGS_PANEL");
        frame.nextCard();
    }
}
