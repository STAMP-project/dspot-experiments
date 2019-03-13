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


import DaemonAction.ADD;
import DaemonAction.LIST;
import DaemonAction.REMOVE;
import DaemonResultCode.OK;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.syncany.config.Config;


/**
 * Unit tests for the {@link DaemonOperation} class,
 * using Mockito for mocking its instance variable options.
 *
 * @author Niels Spruit
 */
public class DaemonOperationTest {
    private DaemonOperationOptions options;

    private DaemonOperation deamonOp;

    private static final String WATCH_ROOT_APP_FOLDER = "watch_root_folder/" + (Config.DIR_APPLICATION);

    private static File tempWatchRootAppFolder;

    @Test
    public void testExecuteList() throws Exception {
        Mockito.when(options.getAction()).thenReturn(LIST);
        DaemonOperationResult res = deamonOp.execute();
        Assert.assertNotNull(res);
        Assert.assertEquals(OK, res.getResultCode());
        Assert.assertNotNull(res.getWatchList());
    }

    @Test
    public void testExecuteAdd() throws Exception {
        Mockito.when(options.getAction()).thenReturn(ADD);
        List<String> watchRoots = new ArrayList<String>();
        watchRoots.add(DaemonOperationTest.tempWatchRootAppFolder.getParent());
        Mockito.when(options.getWatchRoots()).thenReturn(watchRoots);
        DaemonOperationResult res = deamonOp.execute();
        Assert.assertNotNull(res);
        Assert.assertEquals(OK, res.getResultCode());
        Assert.assertNotNull(res.getWatchList());
        Assert.assertEquals(1, res.getWatchList().size());
        Assert.assertEquals(DaemonOperationTest.tempWatchRootAppFolder.getParentFile().getAbsolutePath(), res.getWatchList().get(0).getPath());
    }

    @Test
    public void testExecuteRemove() throws Exception {
        Mockito.when(options.getAction()).thenReturn(REMOVE);
        List<String> watchRoots = new ArrayList<String>();
        watchRoots.add(DaemonOperationTest.tempWatchRootAppFolder.getParent());
        Mockito.when(options.getWatchRoots()).thenReturn(watchRoots);
        DaemonOperationResult res = deamonOp.execute();
        Assert.assertNotNull(res);
        Assert.assertEquals(OK, res.getResultCode());
        Assert.assertNotNull(res.getWatchList());
        Assert.assertTrue(res.getWatchList().isEmpty());
    }
}

