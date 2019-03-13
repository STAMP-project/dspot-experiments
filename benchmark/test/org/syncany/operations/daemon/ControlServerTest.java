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


import ControlCommand.RELOAD;
import ControlCommand.SHUTDOWN;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.input.Tailer;
import org.junit.Test;
import org.mockito.Mockito;
import org.syncany.config.LocalEventBus;


/**
 * Unit tests for the {@link org.syncany.operations.daemon.ControlServer} class,
 * using Mockito for mocking its instance variables.
 *
 * @author Niels Spruit
 */
public class ControlServerTest {
    private ControlServer ctrlServer;

    private File ctrlFile;

    private Tailer ctrlFileTailer;

    private LocalEventBus eventBus;

    @Test
    public void testEnterLoop() throws IOException, ServiceAlreadyStartedException {
        // invoke method
        ctrlServer.enterLoop();
        // verify interactions
        Mockito.verify(ctrlFile).delete();
        Mockito.verify(ctrlFile).createNewFile();
        Mockito.verify(ctrlFile).deleteOnExit();
        Mockito.verify(ctrlFileTailer).run();
    }

    @Test
    public void testHandleShutdown() {
        ctrlServer.handle("SHUTDOWN");
        Mockito.verifyZeroInteractions(ctrlFile);
        Mockito.verify(eventBus).post(SHUTDOWN);
        Mockito.verifyNoMoreInteractions(eventBus);
        Mockito.verify(ctrlFileTailer).stop();
        Mockito.verifyNoMoreInteractions(ctrlFileTailer);
    }

    @Test
    public void testHandleReload() {
        ctrlServer.handle("RELOAD");
        Mockito.verifyZeroInteractions(ctrlFile);
        Mockito.verifyZeroInteractions(ctrlFileTailer);
        Mockito.verify(eventBus).post(RELOAD);
        Mockito.verifyNoMoreInteractions(eventBus);
    }

    @Test
    public void testHandleUnknownCommand() {
        ctrlServer.handle("UnknownCommand");
        Mockito.verifyZeroInteractions(ctrlFile);
        Mockito.verifyZeroInteractions(ctrlFileTailer);
        Mockito.verifyZeroInteractions(eventBus);
    }

    @Test(expected = RuntimeException.class)
    public void testFileNotFound() {
        ctrlServer.fileNotFound();
    }

    @Test(expected = RuntimeException.class)
    public void testHandle() {
        ctrlServer.handle(new Exception());
    }
}

