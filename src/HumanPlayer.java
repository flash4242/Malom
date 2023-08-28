
/**
 * Az emberi játékost valósítja meg
 */
public class HumanPlayer extends Player {

    public String getType(){
        return "human";
    }
    /**
     * Az ős ctorával létrehozza a HumanPlayer osztálypéldányt
     * @param n: játékos neve, nem lehet null
     * @param c: játékos korongjainak színe, nem lehet null
     */
    public HumanPlayer(String n, ManColor c) {
        super(n, c);
    }

    /**
     * Az emberi játékos lépéséért felel. Alszik, amíg nem jön érvényes lépés
     * @throws InterruptedException: ha a szálat megszakítják, kivételt dob
     */
    synchronized public void move() throws InterruptedException {
        step = null;
        while (step == null) {
            wait();
        }
        if (gameplay.getActualPhase() == GamePlay.PHASE.phase1) {
            table.addToTable(step);
            placed_men++;
        } else if (selectedMan != null && table.getMen(this).contains(selectedMan)) {
            table.neutralize(selectedMan);
            table.addToTable(step);
        }
    }

    /**
     * Beállítja a játékos lépését, felébreszti a move()-ot
     * @param step: A beállítandó lépés. Nem lehet null
     */
    @Override
    synchronized public void setStep(Field step) {
        this.step = step;
        step.setManColor(this.getManColor());
        notifyAll();
    }

    /**
     * Visszaadja a játékos által választott korongot. Alszik, amíg nem választott a játékos érvényes mezőt.
     * @return selectedMan: A választott korong. Nem lehet null.
     * @throws InterruptedException: ha a szálat megszakítják, kivételt dob
     */
    @Override
    synchronized public Field getSelectedMan() throws InterruptedException {
        selectedMan = null;
        while (selectedMan == null) {
            wait();
        }
        return selectedMan;
    }

    /**
     * Beállítja a választott korongot. Felébreszti az alvó getSelectedMan()-t
     * @param man: A választott korong. Nem lehet null.
     */
    @Override
    synchronized public void setSelectedMan(Field man) {
        selectedMan = man;
        notifyAll();
    }
}
