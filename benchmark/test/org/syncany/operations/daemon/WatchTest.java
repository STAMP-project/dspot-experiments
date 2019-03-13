/**
 * Syncany, www.syncany.org
 * Copyright (C) 2011-2016 Philipp C. Heckel <philipp.heckel@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.syncany.operations.daemon;


import java.io.File;
import org.junit.Assert;
import org.junit.Test;
import org.syncany.operations.daemon.Watch.SyncStatus;


/**
 * Unit tests for the {@link Watch} class.
 *
 * @author Niels Spruit
 */
public class WatchTest {
    private Watch watch;

    private File folder;

    private SyncStatus status;

    @Test
    public void testGetFolder() {
        File res = watch.getFolder();
        Assert.assertNotNull(res);
        Assert.assertEquals(folder, res);
    }

    @Test
    public void testGetStatus() {
        SyncStatus res = watch.getStatus();
        Assert.assertNotNull(res);
        Assert.assertEquals(status, res);
    }
}

