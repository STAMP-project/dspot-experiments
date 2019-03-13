/**
 * (C) 2007-2012 Alibaba Group Holding Limited.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Authors:
 *   wuhua <wq163@163.com> , boyan <killme2008@gmail.com>
 */
package com.taobao.metamorphosis.server.transaction.store;


import org.junit.Assert;
import org.junit.Test;


public class CheckpointUnitTest {
    private Checkpoint checkpoint;

    String path;

    @Test
    public void testCheckGetCloseGet() throws Exception {
        Assert.assertNull(this.checkpoint.getRecentCheckpoint());
        final JournalLocation location = new JournalLocation(1, 0);
        this.checkpoint.check(location);
        Assert.assertEquals(location, this.checkpoint.getRecentCheckpoint());
        // close and reopen
        this.checkpoint.close();
        this.checkpoint = new Checkpoint(this.path, 3);
        Assert.assertNotNull(this.checkpoint.getRecentCheckpoint());
        Assert.assertEquals(location, this.checkpoint.getRecentCheckpoint());
        Assert.assertEquals(1, this.checkpoint.getCheckpoints().size());
    }

    @Test
    public void testCheckEqualOrOlder() throws Exception {
        Assert.assertNull(this.checkpoint.getRecentCheckpoint());
        final JournalLocation location = new JournalLocation(1, 0);
        this.checkpoint.check(location);
        Assert.assertEquals(location, this.checkpoint.getRecentCheckpoint());
        this.checkpoint.check(location);
        Assert.assertEquals(location, this.checkpoint.getRecentCheckpoint());
        this.checkpoint.check(new JournalLocation(0, 0));
        Assert.assertEquals(location, this.checkpoint.getRecentCheckpoint());
        // close and reopen
        this.checkpoint.close();
        this.checkpoint = new Checkpoint(this.path, 3);
        Assert.assertNotNull(this.checkpoint.getRecentCheckpoint());
        Assert.assertEquals(location, this.checkpoint.getRecentCheckpoint());
        Assert.assertEquals(1, this.checkpoint.getCheckpoints().size());
    }

    @Test
    public void testCheckCheckGetCloseGet() throws Exception {
        Assert.assertNull(this.checkpoint.getRecentCheckpoint());
        final JournalLocation location = new JournalLocation(1, 0);
        this.checkpoint.check(location);
        Assert.assertEquals(location, this.checkpoint.getRecentCheckpoint());
        final JournalLocation newLocation = new JournalLocation(1, 1024);
        this.checkpoint.check(newLocation);
        Assert.assertEquals(newLocation, this.checkpoint.getRecentCheckpoint());
        // close and reopen
        this.checkpoint.close();
        this.checkpoint = new Checkpoint(this.path, 3);
        Assert.assertNotNull(this.checkpoint.getRecentCheckpoint());
        Assert.assertEquals(newLocation, this.checkpoint.getRecentCheckpoint());
        Assert.assertEquals(2, this.checkpoint.getCheckpoints().size());
    }

    @Test
    public void testCheckRollGetCloseGet() throws Exception {
        Assert.assertNull(this.checkpoint.getRecentCheckpoint());
        this.checkpoint.check(new JournalLocation(1, 0));
        Assert.assertEquals(new JournalLocation(1, 0), this.checkpoint.getRecentCheckpoint());
        this.checkpoint.check(new JournalLocation(1, 1024));
        Assert.assertEquals(new JournalLocation(1, 1024), this.checkpoint.getRecentCheckpoint());
        this.checkpoint.check(new JournalLocation(3, 1024));
        Assert.assertEquals(new JournalLocation(3, 1024), this.checkpoint.getRecentCheckpoint());
        this.checkpoint.check(new JournalLocation(3, 2048));
        Assert.assertEquals(new JournalLocation(3, 2048), this.checkpoint.getRecentCheckpoint());
        // close and reopen
        this.checkpoint.close();
        this.checkpoint = new Checkpoint(this.path, 3);
        Assert.assertNotNull(this.checkpoint.getRecentCheckpoint());
        Assert.assertEquals(new JournalLocation(3, 2048), this.checkpoint.getRecentCheckpoint());
        Assert.assertEquals(3, this.checkpoint.getCheckpoints().size());
    }
}

