

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A játékmenetért felelős osztály. Játszatja az eltárolt játékosokat, kezeli a játék végét.
 * PHASE adattag publikus, mert a tábla is használja ezt az enumot
 */
public class GamePlay extends JPanel {
    private Frame frame;
    private final transient JTextField utasitasok = new JTextField("utasitasok"); //ujrabetöltés, kimenteni nem kell
    private Player player1; //ki kell ennek az adattagjait
    private Player player2; //-||-
    private Player actual_player; //csak egy index kimentésnél
    private Table table; //adattagokat
    private final Thread gameLoop = new Thread(this::playingTheGame);
    boolean volt_malom;

    public enum PHASE {phase1, phase2, phase3}

    private PHASE actual_phase; //kimenteni, egész szám, -> int act_phase_number=actual_phase.ordinal();


    /**
     * Eltárolja a Settingstől elkért játékosokat és létrehozza a kellő komponenseket (tábla, utasítások). Értesíti a játékosokat a teendőikről az "utasitasok" textfielddel
     *
     * @param p1: egyik játékos, nem lehet null
     * @param p2: másik játékos, nem lehet null
     */
    public GamePlay(Player p1, Player p2, Frame frame, Player act_player, boolean malom) {
        this.volt_malom=malom;
        this.frame = frame;
        this.setLayout(new BorderLayout());
        actual_phase = PHASE.phase1;

        player1 = p1;
        player2 = p2;
        player1.setGamePlay(this);
        player2.setGamePlay(this);
        actual_player = act_player;

        JPanel felso_panel = new JPanel();
        felso_panel.setLayout(new BoxLayout(felso_panel, BoxLayout.Y_AXIS));

        utasitasok.setEditable(false);
        utasitasok.setFont(new Font("Sans Serif", Font.BOLD, 25));
        felso_panel.add(utasitasok);

        table = new Table(this);
        player1.setTable(table);
        player2.setTable(table);

        JPanel also_panel = new JPanel();
        also_panel.add(table, BorderLayout.SOUTH);
        add(felso_panel, BorderLayout.NORTH);
        add(also_panel, BorderLayout.SOUTH);

        gameLoop.start();

        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        JMenu options_menu = new JMenu("Options");
        JMenuItem save_menuItem = new JMenuItem("save game");
        save_menuItem.addActionListener(this::save);
        JMenuItem load_menuItem = new JMenuItem("load game");
        load_menuItem.addActionListener(this::load);
        JMenuItem quit_menuItem = new JMenuItem("quit");
        quit_menuItem.addActionListener((e) -> frame.dispose());
        options_menu.add(save_menuItem);
        options_menu.add(load_menuItem);
        options_menu.add(quit_menuItem);
        menuBar.add(options_menu);
    }

    /**
     * Játékmentést valósítja meg
     * @param e: nem használt
     */
    public void save(ActionEvent e) {
        try {
            FileWriter fw = new FileWriter("filename.txt");
            PrintWriter pw = new PrintWriter(fw, true);

            //A 2 player kimentése
            pw.write(player1.getType() + "\n");
            pw.write(player1.getName() + "\n");
            pw.write(player1.getManColor().ordinal() + "\n");
            pw.write(player1.getPlaced_men() + "\n");
            if(player1.getStep()!=null) {
                pw.write(player1.getStep().getPoint().x + "\n");
                pw.write(player1.getStep().getPoint().y + "\n");
            }
            else{
                pw.write(-1 + "\n");
                pw.write(-1 + "\n");
            }
            pw.write(player2.getType() + "\n");
            pw.write(player2.getName() + "\n");
            pw.write(player2.getManColor().ordinal() + "\n");
            pw.write(player2.getPlaced_men() + "\n");
            if(player2.getStep()!=null) {
                pw.write(player2.getStep().getPoint().x + "\n");
                pw.write(player2.getStep().getPoint().y + "\n");
            }
            else{
                pw.write(-1 + "\n");
                pw.write(-1 + "\n");
            }
            pw.write(actual_phase.ordinal() + "\n");
            pw.write(utasitasok.getText() + "\n");
            if(actual_player.getStep()!=null)
                pw.write(table.isInMill(actual_player.getStep())+"\n"); //malomban volt-e
            else
                pw.write("false\n");
            if (actual_player == player1)
                pw.write("1\n");
            else
                pw.write("2\n");


            //table-n a korongok színének kimentése
            for (Field f : table.getFields()) {
                if (f.getManColor() != null)
                    pw.write(f.getManColor().ordinal() + "\n");
                else
                    pw.write("null\n");
            }
            fw.close();
        } catch (java.io.IOException i) {
            System.out.println("io exception");
        }
    }

    /**
     * A játékbetöltést valósítja meg
     * @param e: nem használt
     */
    public void load(ActionEvent e) {
        try {
            FileInputStream fis = new FileInputStream("filename.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            String p1type = br.readLine();
            String p1name = br.readLine();
            int p1color_int = Integer.parseInt(br.readLine());//whichNumber(p1color);
            int p1placedmen = Integer.parseInt(br.readLine());
            int p1step_x = Integer.parseInt(br.readLine());
            int p1step_y = Integer.parseInt(br.readLine());

            String p2type = br.readLine();
            String p2name = br.readLine();
            int p2color_int = Integer.parseInt(br.readLine()); //whichNumber(p2color);
            int p2placedmen = Integer.parseInt(br.readLine());
            int p2step_x = Integer.parseInt(br.readLine());
            int p2step_y = Integer.parseInt(br.readLine());
            int actualphase = Integer.parseInt(br.readLine());
            String utasitas = br.readLine();
            String volt_malom = br.readLine();
            String actualplayer = br.readLine();



            //playerek visszaállítása steppel együtt
            setPlayer(p1name, ManColor.values()[p1color_int], p1type, 1);
            setPlayer(p2name, ManColor.values()[p2color_int], p2type, 2);
            player1.setPlaced_men(p1placedmen);
            player1.setSelectedMan(null);
            player2.setPlaced_men(p2placedmen);
            player2.setSelectedMan(null);
            Field step1 = new Field(new Point(p1step_x, p1step_y));
            Field step2 = new Field(new Point(p2step_x, p2step_y));
            if(step1.getPoint().x>=0)
                player1.setStep(step1);
            if(step2.getPoint().x>=0)
                player2.setStep(step2);


            //table-n a korongok színének beolvasása
            List<Integer> korongok_szinei = new ArrayList<>();
            int line_counter = 0;
            while (line_counter < 24) {
                String szin = br.readLine();
                if(!Objects.equals(szin, "null")) korongok_szinei.add(Integer.parseInt(szin));
                else korongok_szinei.add(-1);
                line_counter++;
            }

            gameLoop.interrupt();
            //az új játékmenet létrehozása
            Player act_player;
            if (actualplayer.equals("1")) {
                act_player=player1;
            }
            else act_player=player2;

            boolean malom_van= Objects.equals(volt_malom, "true");
            GamePlay newGP = new GamePlay(player1, player2, this.frame,  act_player, malom_van);
            newGP.setActual_phase(PHASE.values()[actualphase]);
            newGP.setUtasitasok(utasitas);

            //table-n a korongok színének visszaállítása
            Table t = newGP.getTable().setMen(korongok_szinei);
            newGP.setTable(t);
            newGP.handlePhase();


            //Az új gameplay megjelenítése
            frame.addToCards(newGP, "GAMEPLAY_PANEL_NEW");
            frame.nextCard();

        } catch (IOException i) {
            System.out.println("io exception");
        }
    }


    private Table getTable() {
        return table;
    }

    /**
     * A load() használja a betöltendő játékosok létrehozására
     * @param pname: jétékos neve
     * @param color: korongjainak színe
     * @param type: játékos típusa (human vagy bot)
     * @param idx: melyik játékos
     *           Semmi nem lehet null
     */
    private void setPlayer(String pname, ManColor color, String type, int idx) {
        if (idx == 1) {
            if (Objects.equals(type, "human")) {
                player1 = new HumanPlayer(pname, color);
            } else
                player1 = new AIPlayer(pname, color);
        } else {
            if (Objects.equals(type, "human"))
                player2 = new HumanPlayer(pname, color);
            else
                player2 = new AIPlayer(pname, color);
        }
    }

    public void setTable(Table t) {
        this.table = t;
    }

    public void setActual_phase(PHASE actual_phase) {
        this.actual_phase = actual_phase;
    }

    public void setUtasitasok(String s) {
        utasitasok.setText(s);
    }


    /**
     * A játszatást valósítja meg. A játék végét MessageDialog-gal jelzi.
     */
    public void playingTheGame() {
        try {
            while (!somebodyWon()) {
                utasitasok.setText(actual_player.getName() + "'s turn");
                if(!volt_malom)actual_player.move();
                if ((actual_player.getStep() != null && table.isInMill(actual_player.getStep())) || volt_malom) {
                    handleMill();
                    volt_malom=false;
                }
                handlePhase();
                actual_player = otherPlayer();
            }
        } catch (InterruptedException iex) {
            System.out.println("uj jatekot toltottek be");
            // akkor fordul elő, ha új játékot töltenek be, ez elvárt működés. HumanPlayer move()-jának while()-ja dobja
        }
        if(somebodyWon()) { //betöltés miatt kell itt is a feltétel
            utasitasok.setText(otherPlayer().getName() + " won!");
            JOptionPane.showMessageDialog(this, utasitasok.getText());
        }
    }

    /**
     * Kezeli egy játékos malma következtében elinduló folyamatokat
     *
     * @throws InterruptedException: dobja, ha megszakítják a szálat
     */
    private void handleMill() throws InterruptedException {
        utasitasok.setText(actual_player.getName() + " has a mill! You can remove one of " + otherPlayer().getName() + "'s man.");
        List<Field> removeable = table.createRemoveable(otherPlayer());
        Field selected_man;
        do {
            selected_man = actual_player.getSelectedMan();
        } while (!removeable.contains(selected_man));
        table.neutralize(selected_man);
    }

    /**
     * Minden kör végén frissíti az aktuális fázist, ha kell
     */
    private void handlePhase() {
        if (player1.placed_men == 9 && player2.placed_men == 9) {
            if (table.getMen(player1).size() > 3 && table.getMen(player2).size() > 3)
                actual_phase = PHASE.phase2;
            else
                actual_phase = PHASE.phase3;
        }
    }

    /**
     * Visszaadja, hogy nyert-e valaki
     *
     * @return: értékétől függően nyert valaki, vagy sem. True: valaki nyert, false: nem nyert még senki
     */
    public boolean somebodyWon() {
        return actualPlayerStuck() || actualPlayerHaslessThanThreeMen();
    }

    /**
     * Az aktuális játékos nem tud mozdulni
     *
     * @return: true, ha nem tud mozdulni, false: ha tud mozdulni
     */
    public boolean actualPlayerStuck() {
        if (actual_phase != PHASE.phase1) {
            for (int i = 0; i < table.getMen(actual_player).size(); i++) {
                ArrayList<Field> freeNeighbours = table.getFreeNeighbours(table.getMen(actual_player).get(i));
                if (!freeNeighbours.isEmpty())
                    return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Az aktuális játékosnak kevesebb mint 3 korongja van a második/harmadik fázisban
     *
     * @return: true, ha kevesebb mint 3, false: ha legalább 3 korongja van
     */
    public boolean actualPlayerHaslessThanThreeMen() {
        return actual_phase != PHASE.phase1 && table.getMen(actual_player).size() < 3;
    }

    public PHASE getActualPhase() {
        return actual_phase;
    }

    public Player getActualPlayer() {
        return actual_player;
    }

    /**
     * visszaadja az aktuális játékoson kívüli játékost
     *
     * @return: a nem soron következő játékos
     */
    public Player otherPlayer() {
        return actual_player.equals(player1) ? player2 : player1;
    }
}
