

/**
 * A játék keretét foglalja magában
 */
public class Game {
    private final Frame frame = new Frame();
    public Game(){}

    /**
     * A játék ezen függvény hívásával indul
     */
    public void run() {
        frame.setVisible(true);
    }
}
