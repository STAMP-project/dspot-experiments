package org.bukkit.plugin;


import org.bukkit.TestServer;
import org.bukkit.event.Event;
import org.bukkit.event.TestEvent;
import org.junit.Assert;
import org.junit.Test;


public class PluginManagerTest {
    private class MutableObject {
        volatile Object value = null;
    }

    private static final PluginManager pm = TestServer.getInstance().getPluginManager();

    private final PluginManagerTest.MutableObject store = new PluginManagerTest.MutableObject();

    @Test
    public void testAsyncSameThread() {
        final Event event = new TestEvent(true);
        try {
            PluginManagerTest.pm.callEvent(event);
        } catch (IllegalStateException ex) {
            Assert.assertThat(((event.getEventName()) + " cannot be triggered asynchronously from primary server thread."), is(ex.getMessage()));
            return;
        }
        throw new IllegalStateException("No exception thrown");
    }

    @Test
    public void testSyncSameThread() {
        final Event event = new TestEvent(false);
        PluginManagerTest.pm.callEvent(event);
    }

    @Test
    public void testAsyncLocked() throws InterruptedException {
        final Event event = new TestEvent(true);
        Thread secondThread = new Thread(new Runnable() {
            public void run() {
                try {
                    synchronized(PluginManagerTest.pm) {
                        PluginManagerTest.pm.callEvent(event);
                    }
                } catch (Throwable ex) {
                    store.value = ex;
                }
            }
        });
        secondThread.start();
        secondThread.join();
        Assert.assertThat(store.value, is(instanceOf(IllegalStateException.class)));
        Assert.assertThat(((event.getEventName()) + " cannot be triggered asynchronously from inside synchronized code."), is(((Throwable) (store.value)).getMessage()));
    }

    @Test
    public void testAsyncUnlocked() throws InterruptedException {
        final Event event = new TestEvent(true);
        Thread secondThread = new Thread(new Runnable() {
            public void run() {
                try {
                    PluginManagerTest.pm.callEvent(event);
                } catch (Throwable ex) {
                    store.value = ex;
                }
            }
        });
        secondThread.start();
        secondThread.join();
        if ((store.value) != null) {
            throw new RuntimeException(((Throwable) (store.value)));
        }
    }

    @Test
    public void testSyncUnlocked() throws InterruptedException {
        final Event event = new TestEvent(false);
        Thread secondThread = new Thread(new Runnable() {
            public void run() {
                try {
                    PluginManagerTest.pm.callEvent(event);
                } catch (Throwable ex) {
                    store.value = ex;
                }
            }
        });
        secondThread.start();
        secondThread.join();
        if ((store.value) != null) {
            throw new RuntimeException(((Throwable) (store.value)));
        }
    }

    @Test
    public void testSyncLocked() throws InterruptedException {
        final Event event = new TestEvent(false);
        Thread secondThread = new Thread(new Runnable() {
            public void run() {
                try {
                    synchronized(PluginManagerTest.pm) {
                        PluginManagerTest.pm.callEvent(event);
                    }
                } catch (Throwable ex) {
                    store.value = ex;
                }
            }
        });
        secondThread.start();
        secondThread.join();
        if ((store.value) != null) {
            throw new RuntimeException(((Throwable) (store.value)));
        }
    }

    @Test
    public void testRemovePermissionByNameLower() {
        this.testRemovePermissionByName("lower");
    }

    @Test
    public void testRemovePermissionByNameUpper() {
        this.testRemovePermissionByName("UPPER");
    }

    @Test
    public void testRemovePermissionByNameCamel() {
        this.testRemovePermissionByName("CaMeL");
    }

    @Test
    public void testRemovePermissionByPermissionUpper() {
        this.testRemovePermissionByPermission("UPPER");
    }

    @Test
    public void testRemovePermissionByPermissionCamel() {
        this.testRemovePermissionByPermission("CaMeL");
    }
}

