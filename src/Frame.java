
import javax.swing.*;
import java.awt.*;

/**
 * A játék keretéért felelős
 */
public class Frame extends JFrame {
    private final CardLayout cl = new CardLayout();
    private final JPanel cards = new JPanel();

    /**
     * Létrehozza a framet és behozza a menüt. A Layoutmanager: CardLayout.
     */
    public Frame() {
        this.setTitle("Nine men's morris");

        cards.setLayout(cl);

        JPanel menu = new Menu(this);
        cards.add(menu, "MENU_PANEL");
        cl.show(cards, "MENU_PANEL");
        add(cards);

        setSize(600, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Megjeleníti a következő kártyát
     */
    public void nextCard() {
        cl.next(cards);
    }

    /**
     * Hozzáadja a kártyákhoz az adott panelt és panel nevét
     * @param panel
     * @param n: panel neve
     */
    public void addToCards(JPanel panel, String n){
        cards.add(panel, n);
    }
}
