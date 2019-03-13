package org.keycloak.performance.iteration;


import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.performance.AbstractTest;


/**
 *
 *
 * @author tkyjovsk
 */
public class RandomBooleansTest extends AbstractTest {
    Random r;

    // @Ignore
    @Test
    public void testRandoms() {
        int seed1 = r.nextInt();
        int seed2 = r.nextInt();
        List<Boolean> list1 = new RandomBooleans(seed1, 50).stream().limit(10).collect(Collectors.toList());
        List<Boolean> list2 = new RandomBooleans(seed1, 50).stream().limit(10).collect(Collectors.toList());
        List<Boolean> list3 = new RandomBooleans(seed2, 50).stream().limit(10).collect(Collectors.toList());
        logger.info(String.format("List1(seed: %s): %s", seed1, list1));
        logger.info(String.format("List2(seed: %s): %s", seed1, list2));
        logger.info(String.format("List3(seed: %s): %s", seed2, list3));
        Assert.assertFalse(list1.isEmpty());
        Assert.assertFalse(list2.isEmpty());
        Assert.assertFalse(list3.isEmpty());
        Assert.assertEquals(list1, list2);
        Assert.assertNotEquals(list2, list3);
    }
}

