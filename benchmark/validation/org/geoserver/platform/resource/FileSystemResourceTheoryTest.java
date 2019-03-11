/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.platform.resource;


import Kind.ENTRY_CREATE;
import Kind.ENTRY_DELETE;
import Kind.ENTRY_MODIFY;
import Paths.BASE;
import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.geoserver.platform.resource.ResourceNotification.Event;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class FileSystemResourceTheoryTest extends ResourceTheoryTest {
    FileSystemResourceStore store;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void invalid() {
        try {
            Resource resource = store.get("..");
            Assert.assertNotNull(resource);
            Assert.fail(".. invalid");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void fileEvents() throws Exception {
        File fileD = Paths.toFile(store.baseDirectory, "DirC/FileD");
        FileSystemResourceTheoryTest.AwaitResourceListener listener = new FileSystemResourceTheoryTest.AwaitResourceListener();
        store.get("DirC/FileD").addListener(listener);
        store.watcher.schedule(30, TimeUnit.MILLISECONDS);
        long before = fileD.lastModified();
        long after = touch(fileD);
        Assert.assertTrue("touched", (after > before));
        ResourceNotification n = listener.await(5, TimeUnit.SECONDS);
        Assert.assertNotNull("detected event", n);
        Assert.assertEquals("file modified", ENTRY_MODIFY, n.getKind());
        Assert.assertTrue("Resource only", n.events().isEmpty());
        listener.reset();
        fileD.delete();
        n = listener.await(5, TimeUnit.SECONDS);
        Assert.assertEquals("file removed", ENTRY_DELETE, n.getKind());
        listener.reset();
        fileD.createNewFile();
        n = listener.await(5, TimeUnit.SECONDS);
        Assert.assertEquals("file created", ENTRY_CREATE, n.getKind());
        store.get("DirC/FileD").removeListener(listener);
    }

    @Test
    public void eventNotification() throws InterruptedException {
        FileSystemResourceTheoryTest.AwaitResourceListener listener = new FileSystemResourceTheoryTest.AwaitResourceListener();
        ResourceNotification n = listener.await(5, TimeUnit.SECONDS);// expect timeout as no events will be sent!

        Assert.assertNull("No events expected", n);
    }

    @Test
    public void directoryEvents() throws Exception {
        File fileA = Paths.toFile(store.baseDirectory, "FileA");
        File fileB = Paths.toFile(store.baseDirectory, "FileB");
        File dirC = Paths.toFile(store.baseDirectory, "DirC");
        File fileD = Paths.toFile(store.baseDirectory, "DirC/FileD");
        File dirE = Paths.toFile(store.baseDirectory, "DirE");
        FileSystemResourceTheoryTest.AwaitResourceListener listener = new FileSystemResourceTheoryTest.AwaitResourceListener();
        store.get(BASE).addListener(listener);
        store.watcher.schedule(30, TimeUnit.MILLISECONDS);
        long before = fileB.lastModified();
        long after = touch(fileB);
        Assert.assertTrue("touched", (after > before));
        ResourceNotification n = listener.await(5, TimeUnit.SECONDS);
        Assert.assertEquals(ENTRY_MODIFY, n.getKind());
        Assert.assertEquals(BASE, n.getPath());
        Assert.assertEquals(1, n.events().size());
        Event e = n.events().get(0);
        Assert.assertEquals(ENTRY_MODIFY, e.getKind());
        Assert.assertEquals("FileB", e.getPath());
        listener.reset();
        fileA.delete();
        n = listener.await(5, TimeUnit.SECONDS);
        Assert.assertEquals(ENTRY_MODIFY, n.getKind());
        Assert.assertEquals(BASE, n.getPath());
        e = n.events().get(0);
        Assert.assertEquals(ENTRY_DELETE, e.getKind());
        Assert.assertEquals("FileA", e.getPath());
        listener.reset();
        fileA.createNewFile();
        n = listener.await(5, TimeUnit.SECONDS);
        Assert.assertEquals(ENTRY_MODIFY, n.getKind());
        Assert.assertEquals(BASE, n.getPath());
        e = n.events().get(0);
        Assert.assertEquals(ENTRY_CREATE, e.getKind());
        Assert.assertEquals("FileA", e.getPath());
        store.get(BASE).removeListener(listener);
    }

    /**
     * ResourceListener that traps the next ResourceNotification for testing
     */
    static class AwaitResourceListener extends FileSystemResourceTheoryTest.Await<ResourceNotification> implements ResourceListener {
        @Override
        public void changed(ResourceNotification notify) {
            notify(notify);
        }
    }

    /**
     * Support class to efficiently wait for event notification.
     *
     * @author Jody Garnett (Boundless)
     * @param <T>
     * 		Event Type
     */
    abstract static class Await<T> {
        Lock lock = new ReentrantLock(true);

        Condition condition = lock.newCondition();

        private T event = null;

        public void notify(T notification) {
            // System.out.println("Arrived:"+notification);
            lock.lock();
            try {
                if ((this.event) == null) {
                    this.event = notification;
                }
                condition.signalAll();// wake up your event is ready

            } finally {
                lock.unlock();
            }
        }

        public T await() throws InterruptedException {
            return await(5, TimeUnit.SECONDS);
        }

        /**
         * Wait for event notification.
         *
         * <p>If the event has arrived already this method will return immediately, if not we will
         * wait for signal. If the event still has not arrived after five seconds null will be
         * returned.
         *
         * @return Notification event, or null if it does not arrive within 5 seconds
         * @throws InterruptedException
         * 		
         */
        public T await(long howlong, TimeUnit unit) throws InterruptedException {
            final long DELAY = unit.convert(howlong, TimeUnit.MILLISECONDS);
            lock.lock();
            try {
                if ((this.event) == null) {
                    long mark = System.currentTimeMillis();
                    while ((this.event) == null) {
                        long check = System.currentTimeMillis();
                        if ((mark + DELAY) < check) {
                            return null;// event did not show up!

                        }
                        boolean signal = condition.await(1, TimeUnit.SECONDS);
                        // System.out.println("check wait="+signal+" time="+check+"
                        // notify="+this.event);
                    } 
                }
            } finally {
                lock.unlock();
            }
            return this.event;
        }

        public void reset() {
            lock.lock();
            try {
                this.event = null;
            } finally {
                lock.unlock();
            }
        }

        @Override
        public String toString() {
            return ("Await [event=" + (event)) + "]";
        }
    }
}

