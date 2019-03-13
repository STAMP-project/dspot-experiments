/**
 * Copyright 2012-2018 Chronicle Map Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.openhft.chronicle.map;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.stream.Stream;
import net.openhft.chronicle.core.OS;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class ExitHookTest {
    private static final int KEY = 1;

    private static final String PRE_SHUTDOWN_ACTION_EXECUTED = "PRE_SHUTDOWN_ACTION_EXECUTED";

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testExitHook() throws IOException, InterruptedException {
        if ((!(OS.isLinux())) && (!(OS.isMacOSX())))
            return;
        // This test runs only in Unix-like OSes

        File mapFile = folder.newFile();
        File preShutdownActionExecutionConfirmationFile = folder.newFile();
        // Create a process which opens the map, acquires the lock and "hangs" for 30 seconds
        Process process = startOtherProcess(mapFile, preShutdownActionExecutionConfirmationFile);
        // Let the other process actually reach the moment when it locks the map
        // (JVM startup and chronicle map creation are not instant)
        Thread.sleep(10000);
        // Interrupt that process to trigger Chronicle Map's shutdown hooks
        interruptProcess(ExitHookTest.getPidOfProcess(process));
        process.waitFor();
        int actual = process.exitValue();
        // clean shutdown
        if (actual != 0)
            Assert.assertEquals(130, actual);
        // 130 is exit code for SIGINT (interruption).

        ChronicleMap<Integer, Integer> map = ExitHookTest.createMapBuilder().createPersistedTo(mapFile);
        try (ExternalMapQueryContext<Integer, Integer, ?> c = map.queryContext(ExitHookTest.KEY)) {
            // Test that we are able to lock the segment, i. e. the lock was released in other
            // process thanks to shutdown hooks
            c.writeLock().lock();
        }
        try (Stream<String> lines = Files.lines(preShutdownActionExecutionConfirmationFile.toPath())) {
            Iterator<String> lineIterator = lines.iterator();
            Assert.assertTrue(lineIterator.hasNext());
            String line = lineIterator.next();
            Assert.assertEquals(ExitHookTest.PRE_SHUTDOWN_ACTION_EXECUTED, line);
            Assert.assertFalse(lineIterator.hasNext());
        }
    }
}

