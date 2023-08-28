

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Egy mező a táblán. Szomszédait ismeri és a saját színét, ami lehet null, ekkor rajta nincs korong.
 */
public class Field{
    private final Point point;
    private ManColor man_color=null; //csak ezt elég kisorosítani
    private List<Field> neighbours = new ArrayList<>();

    /**
     * A kapott p megadja, hogy hol van az adott Field a malom_board.png-n
     * @param p: nem lehet null
     */
    public Field(Point p){
        point=p;
    }

    /**
     * A szomszédok listáját állítja be
     * @param neighbour: szomszédok listája
     */
    public void addNeighbour(List<Field> neighbour){
        neighbours=neighbour;
    }
    public ManColor getManColor(){ //megkérdezhetem van-e rajta korong, ha null akkor nincs rajta
        return man_color;
    }
    public Color getColor(){
        return switch (man_color) {
            case PinkPanther -> Color.PINK;
            case YellowStone -> Color.YELLOW;
            case WhiteHouse -> Color.WHITE;
        };
    }
    public void setManColor(ManColor c){
        man_color=c;
    }
    public Point getPoint(){
        return point;
    }
    public List<Field> getNeighbours(){
        return neighbours;
    }
}
