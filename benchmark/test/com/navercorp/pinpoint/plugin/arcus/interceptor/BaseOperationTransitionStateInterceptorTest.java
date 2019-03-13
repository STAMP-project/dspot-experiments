package com.navercorp.pinpoint.plugin.arcus.interceptor;


import OperationState.COMPLETE;
import net.spy.memcached.ops.OperationState;
import org.junit.Assert;
import org.junit.Test;


public class BaseOperationTransitionStateInterceptorTest {
    @Test
    public void testComplete() throws Exception {
        String complete = COMPLETE.toString();
        Assert.assertEquals("COMPLETE", complete);
    }

    @Test
    public void existArcusTimeoutState() throws Exception {
        // ???? ?? ???? ?? test? ??? ?? ??.
        if (!(isArcusExist())) {
            // arcus?? state??? ?? ???? ??? ????.
            return;
        }
        // Arcus OperationState.timedout? ??? ??? ????.
        OperationState[] values = OperationState.values();
        for (OperationState value : values) {
            if (value.toString().equals("TIMEDOUT")) {
                return;
            }
        }
        Assert.fail("OperationState.TIMEDOUT state not found");
    }
}

