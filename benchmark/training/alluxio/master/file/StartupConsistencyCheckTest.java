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
package alluxio.master.file;


import Status.COMPLETE;
import Status.DISABLED;
import Status.FAILED;
import Status.NOT_STARTED;
import Status.RUNNING;
import alluxio.AlluxioURI;
import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link StartupConsistencyCheck}.
 */
public final class StartupConsistencyCheckTest {
    @Test
    public void createCompleteCheck() {
        List<AlluxioURI> uris = ImmutableList.of(new AlluxioURI("/"), new AlluxioURI("/dir"));
        StartupConsistencyCheck check = StartupConsistencyCheck.complete(uris);
        Assert.assertEquals(COMPLETE, check.getStatus());
        Assert.assertEquals(uris, check.getInconsistentUris());
    }

    @Test
    public void createDisabledCheck() {
        StartupConsistencyCheck check = StartupConsistencyCheck.disabled();
        Assert.assertEquals(DISABLED, check.getStatus());
        Assert.assertEquals(Collections.EMPTY_LIST, check.getInconsistentUris());
    }

    @Test
    public void createNotStartedCheck() {
        StartupConsistencyCheck check = StartupConsistencyCheck.notStarted();
        Assert.assertEquals(NOT_STARTED, check.getStatus());
        Assert.assertEquals(Collections.EMPTY_LIST, check.getInconsistentUris());
    }

    @Test
    public void createFailedCheck() {
        StartupConsistencyCheck check = StartupConsistencyCheck.failed();
        Assert.assertEquals(FAILED, check.getStatus());
        Assert.assertEquals(Collections.EMPTY_LIST, check.getInconsistentUris());
    }

    @Test
    public void createRunningCheck() {
        StartupConsistencyCheck check = StartupConsistencyCheck.running();
        Assert.assertEquals(RUNNING, check.getStatus());
        Assert.assertEquals(Collections.EMPTY_LIST, check.getInconsistentUris());
    }
}

