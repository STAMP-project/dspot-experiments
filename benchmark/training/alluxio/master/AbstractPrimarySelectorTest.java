/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.master;


import State.PRIMARY;
import State.SECONDARY;
import alluxio.Constants;
import alluxio.util.interfaces.Scoped;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for functionality of {@link AbstractPrimarySelector}.
 */
public final class AbstractPrimarySelectorTest {
    private static final int TIMEOUT = 10 * (Constants.SECOND_MS);

    private AbstractPrimarySelectorTest.TestSelector mSelector;

    private ScheduledExecutorService mExecutor;

    @Test
    public void getState() {
        Assert.assertEquals(SECONDARY, mSelector.getState());
        mSelector.setState(PRIMARY);
        Assert.assertEquals(PRIMARY, mSelector.getState());
        mSelector.setState(SECONDARY);
        Assert.assertEquals(SECONDARY, mSelector.getState());
    }

    @Test(timeout = AbstractPrimarySelectorTest.TIMEOUT)
    public void waitFor() throws Exception {
        mExecutor.schedule(() -> mSelector.setState(PRIMARY), 30, TimeUnit.MILLISECONDS);
        mSelector.waitForState(PRIMARY);
        Assert.assertEquals(PRIMARY, mSelector.getState());
        mExecutor.schedule(() -> mSelector.setState(SECONDARY), 30, TimeUnit.MILLISECONDS);
        mSelector.waitForState(SECONDARY);
        Assert.assertEquals(SECONDARY, mSelector.getState());
    }

    @Test(timeout = AbstractPrimarySelectorTest.TIMEOUT)
    public void onStateChange() {
        AtomicInteger primaryCounter = new AtomicInteger(0);
        AtomicInteger secondaryCounter = new AtomicInteger(0);
        Scoped listener = mSelector.onStateChange(( state) -> {
            if (state.equals(State.PRIMARY)) {
                primaryCounter.incrementAndGet();
            } else {
                secondaryCounter.incrementAndGet();
            }
        });
        for (int i = 0; i < 10; i++) {
            mSelector.setState(PRIMARY);
            mSelector.setState(SECONDARY);
        }
        Assert.assertEquals(10, primaryCounter.get());
        Assert.assertEquals(10, secondaryCounter.get());
        listener.close();
        mSelector.setState(PRIMARY);
        mSelector.setState(SECONDARY);
        Assert.assertEquals(10, primaryCounter.get());
        Assert.assertEquals(10, secondaryCounter.get());
    }

    static class TestSelector extends AbstractPrimarySelector {
        @Override
        public void start(InetSocketAddress localAddress) throws IOException {
        }

        @Override
        public void stop() throws IOException {
        }
    }
}

