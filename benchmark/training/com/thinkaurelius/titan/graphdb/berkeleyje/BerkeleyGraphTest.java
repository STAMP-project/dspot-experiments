package com.thinkaurelius.titan.graphdb.berkeleyje;


import com.thinkaurelius.titan.core.TitanException;
import com.thinkaurelius.titan.core.util.TitanCleanup;
import com.thinkaurelius.titan.graphdb.TitanGraphTest;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BerkeleyGraphTest extends TitanGraphTest {
    @Rule
    public TestName methodNameRule = new TestName();

    private static final Logger log = LoggerFactory.getLogger(BerkeleyGraphTest.class);

    @Test
    public void testIDBlockAllocationTimeout() {
        config.set("ids.authority.wait-time", Duration.of(0L, ChronoUnit.NANOS));
        config.set("ids.renew-timeout", Duration.of(1L, ChronoUnit.MILLIS));
        close();
        TitanCleanup.clear(graph);
        open(config);
        try {
            graph.addVertex();
            Assert.fail();
        } catch (TitanException e) {
        }
        Assert.assertTrue(graph.isOpen());
        close();// must be able to close cleanly

        // Must be able to reopen
        open(config);
        Assert.assertEquals(0L, ((long) (graph.traversal().V().count().next())));
    }
}

