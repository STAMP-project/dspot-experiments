package com.orientechnologies.orient.graph.blueprints;


import com.orientechnologies.orient.core.exception.OTransactionException;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import org.junit.Test;


/**
 *
 *
 * @author Sergey Sitnikov
 * @since 23/03/16
 */
public class AutoTxTest {
    private static final String DB_URL = "memory:" + (AutoTxTest.class.getSimpleName());

    private static OrientGraph graph;

    @Test(expected = OTransactionException.class)
    public void testManualTxThrowsWhileAutoTxOn() {
        AutoTxTest.graph.setAutoStartTx(true);
        AutoTxTest.graph.begin();
    }

    @Test
    public void testManualTxNotThrowsWhileAutoTxOff() {
        AutoTxTest.graph.setAutoStartTx(false);
        AutoTxTest.graph.begin();
    }
}

