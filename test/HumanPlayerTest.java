import org.junit.Assert;
import org.junit.Test;

import java.awt.*;

public class HumanPlayerTest {

    @Test
    public void CtorTest(){
        HumanPlayer p= new HumanPlayer("kati", ManColor.WhiteHouse);
        Assert.assertEquals(p.men_colour, p.getManColor());
        Assert.assertEquals(p.name, p.getName());
    }
    @Test
    public void getNameTest(){
        HumanPlayer p= new HumanPlayer("kati", ManColor.WhiteHouse);
        Assert.assertEquals("kati", p.getName());
    }
    @Test
    public void getManColorTest(){
        HumanPlayer p= new HumanPlayer("kati", ManColor.WhiteHouse);
        Assert.assertEquals(ManColor.WhiteHouse, p.getManColor());
    }
    @Test
    public void getStepTest(){
        HumanPlayer p= new HumanPlayer("kati", ManColor.WhiteHouse);
        Field f=new Field(new Point(20,30));
        p.setStep(f);
        Assert.assertEquals(f, p.getStep());
    }
    @Test
    public void setGamePlayTest(){
        HumanPlayer p= new HumanPlayer("kati", ManColor.WhiteHouse);
        HumanPlayer p1= new HumanPlayer("kati", ManColor.PinkPanther);
        Frame f = new Frame();
        GamePlay gp = new GamePlay(p1,p, f, p1, false);
        p.setGamePlay(gp);
        Assert.assertEquals(gp, p.gameplay);
    }
    @Test
    public void setSelectedManTest(){
        HumanPlayer p= new HumanPlayer("kati", ManColor.WhiteHouse);
        Field f=new Field(new Point(25,35));
        p.setSelectedMan(f);
        Assert.assertEquals(f, p.selectedMan);
    }
}
