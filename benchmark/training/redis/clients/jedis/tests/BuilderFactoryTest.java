package redis.clients.jedis.tests;


import BuilderFactory.DOUBLE;
import org.junit.Assert;
import org.junit.Test;


public class BuilderFactoryTest extends Assert {
    @Test
    public void buildDouble() {
        Double build = DOUBLE.build("1.0".getBytes());
        Assert.assertEquals(new Double(1.0), build);
    }
}

