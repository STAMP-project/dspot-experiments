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
package alluxio.master.meta;


import LockMode.READ;
import LockMode.WRITE;
import org.junit.Test;


/**
 * Unit tests for {@link InodeLockManager}.
 */
public class InodeLockManagerTest {
    @Test(timeout = 10000)
    public void lockInode() throws Exception {
        inodeLockTest(WRITE, READ, true);
        inodeLockTest(READ, WRITE, true);
        inodeLockTest(WRITE, WRITE, true);
        inodeLockTest(READ, READ, false);
    }

    @Test(timeout = 10000)
    public void lockEdge() throws Exception {
        edgeLockTest(WRITE, READ, true);
        edgeLockTest(READ, WRITE, true);
        edgeLockTest(WRITE, WRITE, true);
        edgeLockTest(READ, READ, false);
    }
}

