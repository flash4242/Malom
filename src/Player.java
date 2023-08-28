

/**
 * A robot és az emberi játékos közös őse, a mindkét játékostípusban megtalálható tulajdonságokat tárolja.
 * Függvényei főként getterek és setterek. Ezek standardtől eltérő felelőssége a leszármazottakban definiált.
 */
public abstract class Player {
    transient Field selectedMan; //nem kell, majd kattint mégegyszer
    String name; //kiirni
    ManColor men_colour; //ezt is, int kimentett= men_colour.ordinal(), beolvasásnál: ManColor men_colour=ManColor.values()[kimentett];
    transient Table table; //table beolvasás után settelni kell, kiirni nem kell
    transient GamePlay gameplay;  //gameplay beolvasás után settelni kell, kiirni nem kell
    transient Field step = null; //nem kell, majd steppel újat
    int placed_men=0; //ki kell, ettől függ a fázis


    public int getPlaced_men(){
        return placed_men;
    }
    public void setPlaced_men(int d){
        placed_men=d;
    }
    public abstract String getType();
    /**
     *
     * @param n: a játékos neve, nem lehet null
     * @param c: a játékos korongjainak színe, nem lehet null
     */
    public Player(String n, ManColor c){

        name=n;
        men_colour=c;
    }

    /**
     * A játékos lépését valósítja meg
     * @throws InterruptedException: dobja, ha megszakítják a szálat
     */
    public abstract void move() throws InterruptedException;

    /**
     * Beállítja a játékos lépését
     * @param step: nem lehet null
     */
    public void setStep(Field step){}

    /**
     * Megadja, hogy melyik táblánál ül a játékos
     * @param t: nem lehet null
     */
    public void setTable(Table t){
        table=t;
    }

    /**
     * A játékos korongjainak színét adja vissza
     * @return: nem lehet null
     */
    public ManColor getManColor(){
        return men_colour;
    }

    /**
     * A játékos nevét adja vissza
     * @return: nem lehet null
     */
    public String getName(){
        return name;
    }

    /**
     * Beállítja, melyik játékmenethez tartozik a játékos
     * @param gp: nem lehet null
     */
    public void setGamePlay(GamePlay gp){gameplay=gp;}

    /**
     * Beállítja a kiválasztott korongot
     * @param man:  lehet null
     */
    public void setSelectedMan(Field man){}

    /**
     * Visszaadja a választott korongot
     * @return: A választott korong
     * @throws InterruptedException: Ha megszakítják a szál futását, akkor dobja
     */
    public abstract Field getSelectedMan() throws InterruptedException;

    /**
     * Visszaadja a játékos lépését
     * @return: visszaadhat null-t
     */
    public Field getStep(){
        return step;
    }
}
