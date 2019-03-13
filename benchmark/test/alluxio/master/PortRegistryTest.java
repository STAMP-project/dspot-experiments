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


import alluxio.master.PortRegistry.Registry;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link PortRegistry}.
 */
public final class PortRegistryTest {
    private Registry mRegistry = new Registry();

    @Test
    public void lockOnce() {
        int port = -1;
        boolean locked = false;
        for (int i = 0; i < 100; i++) {
            port = PortRegistry.getFreePort();
            if (mRegistry.lockPort(port)) {
                locked = true;
                break;
            }
        }
        Assert.assertTrue(locked);
        for (int i = 0; i < 100; i++) {
            Assert.assertFalse(mRegistry.lockPort(port));
        }
    }

    @Test
    public void lockMany() {
        int numPorts = 20;
        Set<Integer> ports = new HashSet<>();
        for (int i = 0; i < numPorts; i++) {
            ports.add(mRegistry.reservePort());
        }
        Assert.assertEquals(numPorts, ports.size());
    }

    @Test
    public void lockAndRelease() {
        int port = PortRegistry.getFreePort();
        int successes = 0;
        for (int i = 0; i < 10; i++) {
            if (mRegistry.lockPort(port)) {
                successes++;
                mRegistry.release(port);
            }
        }
        // Other processes could interfere and steal the lock occasionally, so we only check > 50.
        Assert.assertThat(successes, Matchers.greaterThan(5));
    }

    @Test
    public void releaseDeletesFile() {
        int successes = 0;
        for (int i = 0; i < 5; i++) {
            int port = mRegistry.reservePort();
            File portFile = mRegistry.portFile(port);
            Assert.assertTrue(portFile.exists());
            mRegistry.release(port);
            if (!(portFile.exists())) {
                successes++;
            }
        }
        Assert.assertThat(successes, Matchers.greaterThan(2));
    }
}

