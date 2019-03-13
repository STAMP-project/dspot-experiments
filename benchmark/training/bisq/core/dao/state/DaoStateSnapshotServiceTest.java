/**
 * This file is part of Bisq.
 *
 * Bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bisq. If not, see <http://www.gnu.org/licenses/>.
 */
package bisq.core.dao.state;


import bisq.core.dao.governance.period.CycleService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ DaoStateService.class, GenesisTxInfo.class, CycleService.class, DaoStateStorageService.class })
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*" })
public class DaoStateSnapshotServiceTest {
    private DaoStateSnapshotService daoStateSnapshotService;

    @Test
    public void testGetSnapshotHeight() {
        Assert.assertEquals(120, daoStateSnapshotService.getSnapshotHeight(102, 0, 10));
        Assert.assertEquals(120, daoStateSnapshotService.getSnapshotHeight(102, 100, 10));
        Assert.assertEquals(120, daoStateSnapshotService.getSnapshotHeight(102, 102, 10));
        Assert.assertEquals(120, daoStateSnapshotService.getSnapshotHeight(102, 119, 10));
        Assert.assertEquals(120, daoStateSnapshotService.getSnapshotHeight(102, 120, 10));
        Assert.assertEquals(120, daoStateSnapshotService.getSnapshotHeight(102, 121, 10));
        Assert.assertEquals(120, daoStateSnapshotService.getSnapshotHeight(102, 130, 10));
        Assert.assertEquals(120, daoStateSnapshotService.getSnapshotHeight(102, 139, 10));
        Assert.assertEquals(130, daoStateSnapshotService.getSnapshotHeight(102, 140, 10));
        Assert.assertEquals(130, daoStateSnapshotService.getSnapshotHeight(102, 141, 10));
        Assert.assertEquals(990, daoStateSnapshotService.getSnapshotHeight(102, 1000, 10));
    }

    @Test
    public void testSnapshotHeight() {
        Assert.assertFalse(daoStateSnapshotService.isSnapshotHeight(102, 0, 10));
        Assert.assertFalse(daoStateSnapshotService.isSnapshotHeight(102, 80, 10));
        Assert.assertFalse(daoStateSnapshotService.isSnapshotHeight(102, 90, 10));
        Assert.assertFalse(daoStateSnapshotService.isSnapshotHeight(102, 100, 10));
        Assert.assertFalse(daoStateSnapshotService.isSnapshotHeight(102, 119, 10));
        Assert.assertTrue(daoStateSnapshotService.isSnapshotHeight(102, 120, 10));
        Assert.assertTrue(daoStateSnapshotService.isSnapshotHeight(102, 130, 10));
        Assert.assertTrue(daoStateSnapshotService.isSnapshotHeight(102, 140, 10));
        Assert.assertTrue(daoStateSnapshotService.isSnapshotHeight(102, 200, 10));
        Assert.assertFalse(daoStateSnapshotService.isSnapshotHeight(102, 201, 10));
        Assert.assertFalse(daoStateSnapshotService.isSnapshotHeight(102, 199, 10));
    }
}

