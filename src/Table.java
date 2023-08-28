

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

/**
 * A tábla és a rajta végzett műveletek megjelenítéséért felelős
 */
public class Table extends JPanel {
    private final GamePlay gamePlay; //miután beolvasva a gameplay, ezt setteli a gameplay ctor
    private final Image malom_board = new ImageIcon("malom_board.png").getImage(); //nem kell kimenteni, újrabetöltés elég
    private Field clicked_field = null; //ezt sem kell kimenteni
    private final List<Field> fields = new ArrayList<>(); //ezt ki kell: mindnek a men_colourjét elég kiirni, visszaolvasásnál (men_colour indexeket olvasok), ezeket pedig settelem egyeséel
    private List<Field> availableFields = new ArrayList<>(); //kimenteni nem kell, függvényt elég hívni ami kiszámolja
    private final int diameterOfMan = 40; //korong átmérője, nem kell kimenteni


    public List<Field> getFields(){
        return fields;
    }
    /**
     * Létrehozza a táblát
     * @param gamePlay: Megadja, hogy a tábla melyik játékhoz tartozik. Nem lehet null.
     */
    public Table(GamePlay gamePlay) {
        this.gamePlay = gamePlay;
        table_fields_init(); // a tábla mezői, ahová a korongokat le lehet tenni. Mindnek a közepe: (x+offset, y+offset), offset=20
        addMouseListener(new Mouse());
        this.setPreferredSize(new Dimension(500, 500));
    }

    /**
     * Alacsonszíntű grafikai megjelenítést csinál. Kirajzolja a táblát, a pályán lévő korongokat és lépési lehetőségeket
     * @param g  the <code>Graphics</code> context in which to paint
     */
    @Override
    public void paint(Graphics g) {
        Player act_player = gamePlay.getActualPlayer();
        Graphics2D g2D = (Graphics2D) g;
        g2D.drawImage(malom_board, 0, 0, null);

        for (Field f : fields) {
            if (f.getManColor() != null) {
                g2D.setColor(f.getColor());
                g2D.fillOval(f.getPoint().x, f.getPoint().y, diameterOfMan, diameterOfMan);
            }
        }

        if (clicked_field != null && act_player.getManColor() == clicked_field.getManColor() && (gamePlay.getActualPhase() != GamePlay.PHASE.phase1)) {
            for (Field f : availableFields) {
                g2D.setColor(Color.lightGray);
                g2D.fillOval(f.getPoint().x, f.getPoint().y, diameterOfMan, diameterOfMan);
            }
        }
    }

    /**
     * Validál egy lépést. A valid lépés következtében fellépő folyamatokat elindítja
     * @param fieldOfTable: a tábla azon mezője, amely közelében kattintottak, nem lehet null
     */
    public void isValidStep(Field fieldOfTable) {
        Player act_player = gamePlay.getActualPlayer();
        Player other_player = gamePlay.otherPlayer();

        if (getMen(act_player).contains(fieldOfTable) || getMen(other_player).contains(fieldOfTable)) {
            act_player.setSelectedMan(fieldOfTable);
        }

        if (gamePlay.getActualPhase() == GamePlay.PHASE.phase1 && !isInMill(act_player.getStep())) { // ha phase1-ben korong néküli mezőre kattint, akkor valid
            if (fieldOfTable.getManColor() == null) { //színtelen tehát lehet ide rakni
                act_player.setStep(fieldOfTable);
            }
        } else { // phase2-ben/phase3-ban saját korongja kijelölése után available fieldre kattinthat
            if (act_player.getManColor() == fieldOfTable.getManColor() && !isInMill(act_player.getStep())) { //övé a bábu<=> azonos színű
                createAvailableFields(fieldOfTable);
            } else if (availableFields.contains(fieldOfTable) && !isInMill(act_player.getStep())) {
                act_player.setStep(fieldOfTable);
            }
        }
    }

    /**
     * Visszaadja, hogy a megadott korong malomban van-e
     * @param movedMan: adott korong, lehet null
     * @return: értéke megadja, hogy az adott korong malomban van-e, nem lehet null
     */
    public boolean isInMill(Field movedMan) { //ha az éppen mozgatott bábuval malmot zártak be, csikicsuki is ér
        if(movedMan!=null) {
            List<Field> neighbours = movedMan.getNeighbours(); //minden szomszédot megad, nem csak a saját színebelieket
            int same_x = 0, movedmansX = movedMan.getPoint().x;
            int same_y = 0, movedmansY = movedMan.getPoint().y;

            for (Field neighbour : neighbours) {
                if (neighbour.getManColor() != null && movedMan.getManColor() != null && movedMan.getManColor().equals(neighbour.getManColor())) {
                    //ha középsőt húztunk malomba
                    if (movedmansX == neighbour.getPoint().x) same_x++;
                    else if (movedmansY == neighbour.getPoint().y) same_y++;
                    if (same_x == 2 || same_y == 2) return true;

                    //ha szélsőt húztunk malomba
                    List<Field> neighbours_of_nbour = neighbour.getNeighbours();
                    for (Field nOfn : neighbours_of_nbour)
                        if (nOfn != movedMan && nOfn.getManColor() != null && movedMan.getManColor().equals(nOfn.getManColor())
                                && ((movedmansX == neighbour.getPoint().x && movedmansX == nOfn.getPoint().x)
                                || (movedmansY == neighbour.getPoint().y && movedmansY == nOfn.getPoint().y))) //vízszintes v. függőleges malom szélső lerakása után
                            return true;
                }
            }
        }
        return false;
    }

    /**
     * Létrehozza az adott mező függvényében az elérhető mezőket
     * @param fieldOfTable: adott mező, nem lehet null
     */
    public void createAvailableFields(Field fieldOfTable) {
        if (gamePlay.getActualPhase() != GamePlay.PHASE.phase1) {
            if (getMen(gamePlay.getActualPlayer()).size() > 3) {
                availableFields = getFreeNeighbours(fieldOfTable);
                repaint();
            } else {
                availableFields = emptyFields();
                repaint();
            }
        }
    }

    /**
     * Visszaadja egy játékos korongjainak listáját
     * @param player: adott játékos
     * @return: játékos listája, lehet üres
     */
    public List<Field> getMen(Player player) {
        List<Field> men = new ArrayList<>();
        for (Field f : fields)
            if (f.getManColor() != null && f.getManColor().equals(player.getManColor()))
                men.add(f);
        return men;
    }


    /**
     * Megadja egy adott játékos által levehető korongok listáját
     * @param player: adott játékos
     * @return: a levehető korongok lsitája, lehet üres
     */
    public List<Field> createRemoveable(Player player) {
        List<Field> removeableFields = new ArrayList<>();
        List<Field> player_men = getMen(player);
        int inMill = 0;
        for (Field man : player_men) {
            if (isInMill(man)) {
                inMill++;
            } else removeableFields.add(man);
        }
        if (inMill == player_men.size()) //ha mind malomban van, akkor bármelyik remove-olható
            removeableFields = player_men;
        return removeableFields;
    }


    /**
     * Megadja, hogy a kattintott pont melyik mezőnek felel meg a táblán
     * @param clickedPoint: a kattintott pont
     * @return: lehet null, ekkor semelyik mezőnek nem felel meg a kattintás
     */
    public Field whichField(Point clickedPoint) {
        for (Field f : fields) {
            if (f.getPoint().x <= clickedPoint.x && clickedPoint.x < f.getPoint().x + diameterOfMan
                    && f.getPoint().y <= clickedPoint.y && clickedPoint.y < f.getPoint().y + diameterOfMan) {
                clicked_field = f;
                return f;
            }
        }
        return null;
    }

    /**
     * Egy adott mező szabad (azaz, amin nincs korong) szomszédjainak listáját adja meg
     * @param field: adott mező
     * @return: szomszédok listája, lehet üres
     */
    public ArrayList<Field> getFreeNeighbours(Field field) {
        ArrayList<Field> free_neighbours = new ArrayList<>();
        List<Field> neighbours = field.getNeighbours();
        for (Field neighbour : neighbours)
            if (neighbour.getManColor() == null) //ha még színtelen, akkor nincs rajta korong
                free_neighbours.add(neighbour);
        return free_neighbours;
    }

    /**
     * Megadja a táblán lévő üres mezők listáját
     * @return: üres mezők listája, nem lehet null
     */
    public ArrayList<Field> emptyFields() {
        ArrayList<Field> emptyFields = new ArrayList<>();
        for (Field field : fields)
            if (field.getManColor() == null)
                emptyFields.add(field);
        return emptyFields;
    }

    /**
     * Visszaad egy véletlenszerűen választott üres mezőt (robot működéséhez kell)
     * @return: random üres mező
     */
    public Field randomEmptyField(){
        ArrayList<Field> emptyFields = emptyFields();
        int max=emptyFields.size()-1, min=0;
        int idx = (int)(Math.random() * (max - min + 1)) + min;
        return emptyFields.get(idx);
    }

    /**
     *  Visszaad egy random korongot a paraméterként kapott játékos korongjai közül (robot működéséhez)
     * @param p: megmondja, melyik játékosai közül választjuk a random korongot
     * @return random korong. Nem lehet null
     */
    public Field randomMan(Player p){
        Field randomMan=null;
        List<Field> men= getMen(p);
        int max=men.size()-1, min=0;
        int idx = (int) (Math.random() * (max - min + 1)) + min;
        if(p==gamePlay.getActualPlayer()){ //sajátjai közül választ. Csak olyat választhat, amivel mozdulni is lehet
            while(randomMan==null) {
                idx = (int) (Math.random() * (max - min + 1)) + min;
                randomMan = men.get(idx);
                createAvailableFields(randomMan); //kövi lépésben (step megválasztásánál) felhasználható az availableFields lista tartalma
                if(availableFields.size()==0) //csak olyat engedünk neki selectálni, amivel lehet mozdulni valahova
                    randomMan=null;
            }
        }
        else{ //removingnál van, ha ellenfél korongjai közül kell választani. A jóságát ellenőrzi majd a gamePlay
            randomMan=men.get(idx);
        }
        return randomMan;
    }

    /**
     * Visszaad egy random, a kapott koronggal elérhető, szabad mezőt
     * @param selectedMan: a kapott korong, nem lehet null
     * @return: a kapott koronggal elérhető mező, nem lehet null
     */
    public Field randomAvailable(Field selectedMan) {
        createAvailableFields(selectedMan);
        int max=availableFields.size()-1, min=0;
        int idx =(int) (Math.random() * (max - min + 1)) + min;
        return availableFields.get(idx);
    }

    /**
     * A táblához ad egy játékos által letett korongot
     * @param man: a korong amit a táblához kell adni, nem lehet null
     */
    public void addToTable(Field man) {
        int idx = fields.indexOf(man);
        fields.get(idx).setManColor(man.getManColor());
        repaint();
    }

    /**
     * Adott mezőt semlegesíti, azaz elveszi róla a korongot
     * @param f: adott mező, nem lehet null
     */
    public void neutralize(Field f) {
        int idx = fields.indexOf(f);
        fields.get(idx).setManColor(null);
        availableFields.clear();
        repaint();
    }

    public Table setMen(List<Integer> korongok_szinei) {
        for(int i=0; i<korongok_szinei.size(); i++){
            if(korongok_szinei.get(i)!=-1)
                fields.get(i).setManColor(ManColor.values()[korongok_szinei.get(i)]);
        }
        return this;
    }

    /**
     * Osztály létezésének indoka: hogy ne kelljen implementálni a MouseListener mouseClicked függvényén kívüli függvényeit
     */
    public class Mouse extends MouseAdapter {
        /**
         * Figyeli az egérkattintásokat, validálásra továbbadja, ha a kattintás eredménye valamilyen táblamező
         * @param e: a kattintás, mint esemény
         */
        @Override
        public void mouseClicked(MouseEvent e) {
            //clicked_point = e.getPoint();
            Field clicked_field = whichField(e.getPoint());
            if (clicked_field != null && !gamePlay.somebodyWon()) {
                isValidStep(clicked_field);
            }
        }
    }


    /**
     * A tábla mezőit rögzíti a malom_board.png pixelkiosztása alapján.
     */
    public void table_fields_init() {
        //külső négyzet pontjai felülről lefelé, balról jobbra
        Field p1 = new Field(new Point(2, 2));
        Field p2 = new Field(new Point(230, 2));
        Field p3 = new Field(new Point(457, 2));
        Field p4 = new Field(new Point(2, 230));
        Field p5 = new Field(new Point(457, 230));
        Field p6 = new Field(new Point(2, 457));
        Field p7 = new Field(new Point(230, 457));
        Field p8 = new Field(new Point(457, 457));

        //középső négyzet pontjai felülről lefelé, balról jobbra
        Field p9 = new Field(new Point(78, 78));
        Field p10 = new Field(new Point(230, 78));
        Field p11 = new Field(new Point(380, 78));
        Field p12 = new Field(new Point(78, 230));
        Field p13 = new Field(new Point(380, 230));
        Field p14 = new Field(new Point(78, 380));
        Field p15 = new Field(new Point(230, 380));
        Field p16 = new Field(new Point(380, 380));

        //belső négyzet pontjai felülről lefelé, balról jobbra
        Field p17 = new Field(new Point(157, 157));
        Field p18 = new Field(new Point(230, 157));
        Field p19 = new Field(new Point(306, 157));
        Field p20 = new Field(new Point(157, 230));
        Field p21 = new Field(new Point(306, 230));
        Field p22 = new Field(new Point(157, 306));
        Field p23 = new Field(new Point(230, 306));
        Field p24 = new Field(new Point(306, 306));

        //szomszédok hozzáadása
        p1.addNeighbour(Arrays.asList(p2, p4));
        p2.addNeighbour(Arrays.asList(p1, p3, p10));
        p3.addNeighbour(Arrays.asList(p2, p5));
        p4.addNeighbour(Arrays.asList(p1, p6, p12));
        p5.addNeighbour(Arrays.asList(p3, p8, p13));
        p6.addNeighbour(Arrays.asList(p4, p7));
        p7.addNeighbour(Arrays.asList(p6, p8, p15));
        p8.addNeighbour(Arrays.asList(p5, p7));

        p9.addNeighbour(Arrays.asList(p10, p12));
        p10.addNeighbour(Arrays.asList(p2, p9, p11, p18));
        p11.addNeighbour(Arrays.asList(p10, p13));
        p12.addNeighbour(Arrays.asList(p4, p9, p14, p20));
        p13.addNeighbour(Arrays.asList(p5, p11, p16, p21));
        p14.addNeighbour(Arrays.asList(p12, p15));
        p15.addNeighbour(Arrays.asList(p7, p14, p16, p23));
        p16.addNeighbour(Arrays.asList(p13, p15));

        p17.addNeighbour(Arrays.asList(p18, p20));
        p18.addNeighbour(Arrays.asList(p10, p17, p19));
        p19.addNeighbour(Arrays.asList(p18, p21));
        p20.addNeighbour(Arrays.asList(p12, p17, p22));
        p21.addNeighbour(Arrays.asList(p13, p19, p24));
        p22.addNeighbour(Arrays.asList(p20, p23));
        p23.addNeighbour(Arrays.asList(p15, p22, p24));
        p24.addNeighbour(Arrays.asList(p21, p23));

        fields.addAll(Arrays.asList(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22, p23, p24));
    }
}
