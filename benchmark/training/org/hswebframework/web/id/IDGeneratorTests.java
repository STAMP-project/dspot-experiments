package org.hswebframework.web.id;


import IDGenerator.MD5;
import IDGenerator.RANDOM;
import IDGenerator.SNOW_FLAKE;
import IDGenerator.SNOW_FLAKE_HEX;
import IDGenerator.UUID;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author zhouhao
 * @since 3.0
 */
public class IDGeneratorTests {
    @Test
    public void test() {
        System.setProperty("id-worker", "1");
        System.setProperty("id-datacenter", "1");
        Assert.assertNotNull(UUID.generate());
        Assert.assertNotNull(MD5.generate());
        Assert.assertNotNull(RANDOM.generate());
        Assert.assertNotNull(SNOW_FLAKE.generate());
        Assert.assertNotNull(SNOW_FLAKE_HEX.generate());
        for (int i = 0; i < 100; i++) {
            System.out.println(SNOW_FLAKE.generate());
        }
    }
}

