/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.util;


import Notification.ADDED;
import Notification.CHANGED;
import Notification.REMOVED;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.util.Scanner.Notification;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;


public class ScannerTest {
    static File _directory;

    static Scanner _scanner;

    static BlockingQueue<ScannerTest.Event> _queue = new LinkedBlockingQueue<ScannerTest.Event>();

    static BlockingQueue<List<String>> _bulk = new LinkedBlockingQueue<List<String>>();

    static class Event {
        String _filename;

        Notification _notification;

        public Event(String filename, Notification notification) {
            _filename = filename;
            _notification = notification;
        }
    }

    // TODO: needs review
    // TODO: SLOW, needs review
    @Test
    @DisabledOnOs(OS.WINDOWS)
    @DisabledIfSystemProperty(named = "env", matches = "ci")
    public void testAddedChangeRemove() throws Exception {
        touch("a0");
        // takes 2 scans to notice a0 and check that it is stable
        ScannerTest._scanner.scan();
        ScannerTest._scanner.scan();
        ScannerTest.Event event = ScannerTest._queue.poll();
        Assertions.assertNotNull(event, "Event should not be null");
        Assertions.assertEquals(((ScannerTest._directory) + "/a0"), event._filename);
        Assertions.assertEquals(ADDED, event._notification);
        // add 3 more files
        Thread.sleep(1100);// make sure time in seconds changes

        touch("a1");
        touch("a2");
        touch("a3");
        // not stable after 1 scan so should not be seen yet.
        ScannerTest._scanner.scan();
        event = ScannerTest._queue.poll();
        Assertions.assertTrue((event == null));
        // Keep a2 unstable and remove a3 before it stabalized
        Thread.sleep(1100);// make sure time in seconds changes

        touch("a2");
        delete("a3");
        // only a1 is stable so it should be seen.
        ScannerTest._scanner.scan();
        event = ScannerTest._queue.poll();
        Assertions.assertTrue((event != null));
        Assertions.assertEquals(((ScannerTest._directory) + "/a1"), event._filename);
        Assertions.assertEquals(ADDED, event._notification);
        Assertions.assertTrue(ScannerTest._queue.isEmpty());
        // Now a2 is stable
        ScannerTest._scanner.scan();
        event = ScannerTest._queue.poll();
        Assertions.assertTrue((event != null));
        Assertions.assertEquals(((ScannerTest._directory) + "/a2"), event._filename);
        Assertions.assertEquals(ADDED, event._notification);
        Assertions.assertTrue(ScannerTest._queue.isEmpty());
        // We never see a3 as it was deleted before it stabalised
        // touch a1 and a2
        Thread.sleep(1100);// make sure time in seconds changes

        touch("a1");
        touch("a2");
        // not stable after 1scan so nothing should not be seen yet.
        ScannerTest._scanner.scan();
        event = ScannerTest._queue.poll();
        Assertions.assertTrue((event == null));
        // Keep a2 unstable
        Thread.sleep(1100);// make sure time in seconds changes

        touch("a2");
        // only a1 is stable so it should be seen.
        ScannerTest._scanner.scan();
        event = ScannerTest._queue.poll();
        Assertions.assertTrue((event != null));
        Assertions.assertEquals(((ScannerTest._directory) + "/a1"), event._filename);
        Assertions.assertEquals(CHANGED, event._notification);
        Assertions.assertTrue(ScannerTest._queue.isEmpty());
        // Now a2 is stable
        ScannerTest._scanner.scan();
        event = ScannerTest._queue.poll();
        Assertions.assertTrue((event != null));
        Assertions.assertEquals(((ScannerTest._directory) + "/a2"), event._filename);
        Assertions.assertEquals(CHANGED, event._notification);
        Assertions.assertTrue(ScannerTest._queue.isEmpty());
        // delete a1 and a2
        delete("a1");
        delete("a2");
        // not stable after 1scan so nothing should not be seen yet.
        ScannerTest._scanner.scan();
        event = ScannerTest._queue.poll();
        Assertions.assertTrue((event == null));
        // readd a2
        touch("a2");
        // only a1 is stable so it should be seen.
        ScannerTest._scanner.scan();
        event = ScannerTest._queue.poll();
        Assertions.assertTrue((event != null));
        Assertions.assertEquals(((ScannerTest._directory) + "/a1"), event._filename);
        Assertions.assertEquals(REMOVED, event._notification);
        Assertions.assertTrue(ScannerTest._queue.isEmpty());
        // Now a2 is stable and is a changed file rather than a remove
        ScannerTest._scanner.scan();
        event = ScannerTest._queue.poll();
        Assertions.assertTrue((event != null));
        Assertions.assertEquals(((ScannerTest._directory) + "/a2"), event._filename);
        Assertions.assertEquals(CHANGED, event._notification);
        Assertions.assertTrue(ScannerTest._queue.isEmpty());
    }

    // TODO: needs review
    @Test
    @DisabledOnOs(OS.WINDOWS)
    public void testSizeChange() throws Exception {
        touch("tsc0");
        ScannerTest._scanner.scan();
        ScannerTest._scanner.scan();
        // takes 2s to notice tsc0 and check that it is stable.  This syncs us with the scan
        ScannerTest.Event event = ScannerTest._queue.poll();
        Assertions.assertTrue((event != null));
        Assertions.assertEquals(((ScannerTest._directory) + "/tsc0"), event._filename);
        Assertions.assertEquals(ADDED, event._notification);
        // Create a new file by writing to it.
        long now = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
        File file = new File(ScannerTest._directory, "st");
        try (OutputStream out = new FileOutputStream(file, true)) {
            out.write('x');
            out.flush();
            file.setLastModified(now);
            // Not stable yet so no notification.
            ScannerTest._scanner.scan();
            event = ScannerTest._queue.poll();
            Assertions.assertTrue((event == null));
            // Modify size only
            out.write('x');
            out.flush();
            file.setLastModified(now);
            // Still not stable yet so no notification.
            ScannerTest._scanner.scan();
            event = ScannerTest._queue.poll();
            Assertions.assertTrue((event == null));
            // now stable so finally see the ADDED
            ScannerTest._scanner.scan();
            event = ScannerTest._queue.poll();
            Assertions.assertTrue((event != null));
            Assertions.assertEquals(((ScannerTest._directory) + "/st"), event._filename);
            Assertions.assertEquals(ADDED, event._notification);
            // Modify size only
            out.write('x');
            out.flush();
            file.setLastModified(now);
            // Still not stable yet so no notification.
            ScannerTest._scanner.scan();
            event = ScannerTest._queue.poll();
            Assertions.assertTrue((event == null));
            // now stable so finally see the ADDED
            ScannerTest._scanner.scan();
            event = ScannerTest._queue.poll();
            Assertions.assertTrue((event != null));
            Assertions.assertEquals(((ScannerTest._directory) + "/st"), event._filename);
            Assertions.assertEquals(CHANGED, event._notification);
        }
    }
}

