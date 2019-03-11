package cc.blynk.server.core.dao.functions;


import org.junit.Assert;
import org.junit.Test;


/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 22.07.17.
 */
public class MedianGraphFunctionTest {
    @Test
    public void testMedianFunction() {
        MedianGraphFunction medianFunction = new MedianGraphFunction();
        medianFunction.apply(0);
        Assert.assertEquals(0, medianFunction.getResult(), 1.0E-4);
        medianFunction.apply(1);
        Assert.assertEquals(0.5, medianFunction.getResult(), 1.0E-4);
        medianFunction.apply(2);
        Assert.assertEquals(1, medianFunction.getResult(), 1.0E-4);
        medianFunction.apply(3);
        Assert.assertEquals(1.5, medianFunction.getResult(), 1.0E-4);
    }

    @Test
    public void testMedianFunction2() {
        MedianGraphFunction medianFunction = new MedianGraphFunction();
        medianFunction.apply(0);
        medianFunction.apply(0);
        medianFunction.apply(0);
        medianFunction.apply(0);
        medianFunction.apply(0);
        medianFunction.apply(0);
        Assert.assertEquals(0, medianFunction.getResult(), 1.0E-4);
    }
}

