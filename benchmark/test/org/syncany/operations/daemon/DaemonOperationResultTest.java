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


import DaemonResultCode.NOK;
import DaemonResultCode.OK;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;
import org.syncany.config.to.FolderTO;
import org.syncany.operations.daemon.DaemonOperationResult.DaemonResultCode;


/**
 * Unit tests for the {@link DaemonOperationResult} class.
 *
 * @author Niels Spruit
 */
public class DaemonOperationResultTest {
    private DaemonOperationResult result;

    private FolderTO folder;

    private ArrayList<FolderTO> watchList;

    @Test
    public void testGetResultCode() {
        DaemonResultCode res = result.getResultCode();
        Assert.assertNotNull(res);
        Assert.assertEquals(OK, res);
    }

    @Test
    public void testSetResultCode() {
        result.setResultCode(NOK);
        DaemonResultCode res = result.getResultCode();
        Assert.assertNotNull(res);
        Assert.assertEquals(NOK, res);
    }

    @Test
    public void testGetWatchList() {
        ArrayList<FolderTO> res = result.getWatchList();
        Assert.assertNotNull(res);
        Assert.assertFalse(res.isEmpty());
        Assert.assertTrue(res.contains(folder));
    }

    @Test
    public void setWatchList() {
        ArrayList<FolderTO> newList = new ArrayList<FolderTO>();
        result.setWatchList(newList);
        ArrayList<FolderTO> res = result.getWatchList();
        Assert.assertNotNull(res);
        Assert.assertTrue(res.isEmpty());
    }
}

