package org.hibernate.property.access.spi;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
public class GetterFieldImplTest {
    @Test
    public void testGet() throws Exception {
        GetterFieldImplTest.Target target = new GetterFieldImplTest.Target();
        Assert.assertEquals(true, getter("active").get(target));
        Assert.assertEquals(((byte) (2)), getter("children").get(target));
        Assert.assertEquals('M', getter("gender").get(target));
        Assert.assertEquals(Integer.MAX_VALUE, getter("code").get(target));
        Assert.assertEquals(Long.MAX_VALUE, getter("id").get(target));
        Assert.assertEquals(((short) (34)), getter("age").get(target));
        Assert.assertEquals("John Doe", getter("name").get(target));
    }

    private static class Target {
        private boolean active = true;

        private byte children = 2;

        private char gender = 'M';

        private int code = Integer.MAX_VALUE;

        private long id = Long.MAX_VALUE;

        private short age = 34;

        private String name = "John Doe";
    }
}

