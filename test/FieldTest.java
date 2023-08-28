import org.junit.Assert;
import org.junit.Test;

import java.awt.*;

public class FieldTest {
    @Test
    public void getPointTest(){
        Field f= new Field(new Point(10,15));
        Assert.assertEquals(10, f.getPoint().x);
        Assert.assertEquals(15, f.getPoint().y);
    }

    @Test
    public void getManColorTest(){
        Field f = new Field(new Point(0,0));
        ManColor c1 = ManColor.PinkPanther;
        f.setManColor(c1);
        Assert.assertEquals(ManColor.PinkPanther, f.getManColor());
    }

    @Test
    public void getColorTest(){
        Field f = new Field(new Point(0,0));
        ManColor c1 = ManColor.PinkPanther;
        f.setManColor(c1);
        Assert.assertEquals(Color.PINK, f.getColor());
    }

}
