import org.junit.Assert;
import org.junit.Test;

public class ManColorTest {
    @Test
    public void toStringTest(){
        ManColor c1 = ManColor.PinkPanther;
        ManColor c2 = ManColor.YellowStone;
        ManColor c3 = ManColor.WhiteHouse;

        Assert.assertEquals("PinkPanther", c1.toString());
        Assert.assertEquals("YellowStone", c2.toString());
        Assert.assertEquals("WhiteHouse", c3.toString());
    }
}
