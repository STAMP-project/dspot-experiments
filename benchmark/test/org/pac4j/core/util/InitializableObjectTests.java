package org.pac4j.core.util;


import org.junit.Assert;
import org.junit.Test;


/**
 * This class tests the {@link InitializableObject} class.
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public final class InitializableObjectTests {
    @Test
    public void testInit() {
        CounterInitializableObject counterInitializableObject = new CounterInitializableObject();
        Assert.assertEquals(0, counterInitializableObject.getCounter());
        init();
        Assert.assertEquals(1, counterInitializableObject.getCounter());
        init();
        Assert.assertEquals(1, counterInitializableObject.getCounter());
    }
}

