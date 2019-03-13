package com.orientechnologies.orient.core;


import com.orientechnologies.orient.core.shutdown.OShutdownHandler;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by tglman on 01/06/16.
 */
public class OrientShutDownTest {
    private int test = 0;

    @Test
    public void testShutdownHandler() {
        Orient.instance().addShutdownHandler(new OShutdownHandler() {
            @Override
            public int getPriority() {
                return 0;
            }

            @Override
            public void shutdown() throws Exception {
                test += 1;
            }
        });
        Orient.instance().shutdown();
        Assert.assertEquals(1, test);
        Orient.instance().startup();
        Orient.instance().shutdown();
        Assert.assertEquals(1, test);
    }
}

