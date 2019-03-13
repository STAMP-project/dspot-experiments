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


import alluxio.master.journal.noop.NoopJournalSystem;
import org.junit.Rule;
import org.junit.Test;


/**
 * Tests for {@link AlluxioMasterProcess}.
 */
public final class AlluxioMasterProcessTest {
    @Rule
    public PortReservationRule mRpcPortRule = new PortReservationRule();

    @Rule
    public PortReservationRule mWebPortRule = new PortReservationRule();

    private int mRpcPort;

    private int mWebPort;

    @Test
    public void startStopPrimary() throws Exception {
        AlluxioMasterProcess master = new AlluxioMasterProcess(new NoopJournalSystem());
        Thread t = new Thread(() -> {
            try {
                master.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        t.start();
        startStopTest(master);
    }

    @Test
    public void startStopSecondary() throws Exception {
        FaultTolerantAlluxioMasterProcess master = new FaultTolerantAlluxioMasterProcess(new NoopJournalSystem(), new AlwaysSecondaryPrimarySelector());
        Thread t = new Thread(() -> {
            try {
                master.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        t.start();
        startStopTest(master);
    }
}

