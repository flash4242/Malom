

/**
 * A robot játékost valósítja meg
 */
public class AIPlayer extends Player {

    public String getType(){
        return "bot";
    }
    /**
     * Az ős ctorával létrehozza az AIPlayer osztálypéldányt
     * @param n: játékos neve, nem lehet null
     * @param c: játékos korongjainak színe, nem lehet null
     */
    public AIPlayer(String n, ManColor c){
        super(n, c);
    }

    /**
     * A robot lépéséért felel. Az első fázisban egy random üres mezőre lép.
     * A második és harmadik fázisban egy olyan korongot választ, amivel biztosan tud lépni, ezután a lépési lehetőségek közül választ egyet random
     */
    public void move(){
        if(gameplay.getActualPhase()== GamePlay.PHASE.phase1){
            step=table.randomEmptyField();
            step.setManColor(this.getManColor());
            table.addToTable(step);
            placed_men++;
        }
        else{
            selectedMan=table.randomMan(this); //lehet, hogy a selected-del nem lehet mozdulni, addig ne adjuk vissza neki, amíg nem olyat választ, amivel léphet is
            step=table.randomAvailable(selectedMan);
            step.setManColor(this.getManColor());
            table.neutralize(selectedMan);
            table.addToTable(step);
        }

    }

    /**
     * Amikor malmot alakított ki a robot, akkor választ egy korongot az ellenfél korongjai közül random,
     *  amit majd a gamePlay validál (ha nem levehető a bábu, akkor új bábu választására szólítja fel a robotot)
     * @return: az ellenfél korongjai közül egy véletlenszerű bábu
     */
    public Field getSelectedMan(){  //ha malmot hoz létre a robot, akkor hívódik
        selectedMan=table.randomMan(gameplay.otherPlayer()); //és majd a do while gondoskodik róla, hogy removable-t fogadjon el csak
        return selectedMan;
    }
}
