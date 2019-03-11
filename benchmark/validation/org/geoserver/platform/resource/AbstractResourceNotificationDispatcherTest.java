/**
 * Copyright (c) 2015 OpenPlans - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.platform.resource;


import Kind.ENTRY_CREATE;
import Kind.ENTRY_DELETE;
import Kind.ENTRY_MODIFY;
import java.util.Collections;
import java.util.List;
import org.geoserver.platform.resource.ResourceNotification.Event;
import org.geoserver.platform.resource.ResourceNotification.Kind;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 *
 *
 * @author Niels Charlier
 */
public abstract class AbstractResourceNotificationDispatcherTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    protected FileSystemResourceStore store;

    protected ResourceNotificationDispatcher watcher;

    protected static class CheckingResourceListener implements ResourceListener {
        private boolean checked = false;

        private Kind kind;

        public CheckingResourceListener(Kind kind) {
            this.kind = kind;
        }

        @Override
        public void changed(ResourceNotification notify) {
            if ((kind) == (notify.getKind())) {
                checked = true;
            }
        }

        public boolean isChecked() {
            return checked;
        }
    }

    @Test
    public void testDeleteNotification() {
        Resource res = store.get("DirA");
        final AbstractResourceNotificationDispatcherTest.CheckingResourceListener chkDirA = new AbstractResourceNotificationDispatcherTest.CheckingResourceListener(Kind.ENTRY_DELETE);
        final AbstractResourceNotificationDispatcherTest.CheckingResourceListener chkDirC = new AbstractResourceNotificationDispatcherTest.CheckingResourceListener(Kind.ENTRY_DELETE);
        final AbstractResourceNotificationDispatcherTest.CheckingResourceListener chkFileA1 = new AbstractResourceNotificationDispatcherTest.CheckingResourceListener(Kind.ENTRY_DELETE);
        final AbstractResourceNotificationDispatcherTest.CheckingResourceListener chkFileA2 = new AbstractResourceNotificationDispatcherTest.CheckingResourceListener(Kind.ENTRY_DELETE);
        final AbstractResourceNotificationDispatcherTest.CheckingResourceListener chkFileC1 = new AbstractResourceNotificationDispatcherTest.CheckingResourceListener(Kind.ENTRY_DELETE);
        final AbstractResourceNotificationDispatcherTest.CheckingResourceListener chkFileC2 = new AbstractResourceNotificationDispatcherTest.CheckingResourceListener(Kind.ENTRY_DELETE);
        watcher.addListener(res.path(), chkDirA);
        watcher.addListener(res.get("FileA1").path(), chkFileA1);
        watcher.addListener(res.get("FileA2").path(), chkFileA2);
        watcher.addListener(res.get("DirC").path(), chkDirC);
        watcher.addListener(res.get("DirC/FileC1").path(), chkFileC1);
        watcher.addListener(res.get("DirC/FileC2").path(), chkFileC2);
        List<Event> events = SimpleResourceNotificationDispatcher.createEvents(res, ENTRY_DELETE);
        watcher.changed(new ResourceNotification("DirA", Kind.ENTRY_DELETE, System.currentTimeMillis(), events));
        // test that listeners received events
        Assert.assertTrue(chkDirA.isChecked());
        Assert.assertTrue(chkFileA1.isChecked());
        Assert.assertTrue(chkFileA2.isChecked());
        Assert.assertTrue(chkDirC.isChecked());
        Assert.assertTrue(chkFileC1.isChecked());
        Assert.assertTrue(chkFileC2.isChecked());
        // remove listeners
        Assert.assertTrue(watcher.removeListener(res.path(), chkDirA));
        Assert.assertTrue(watcher.removeListener(res.get("FileA1").path(), chkFileA1));
        Assert.assertTrue(watcher.removeListener(res.get("FileA2").path(), chkFileA2));
        Assert.assertTrue(watcher.removeListener(res.get("DirC").path(), chkDirC));
        Assert.assertTrue(watcher.removeListener(res.get("DirC/FileC1").path(), chkFileC1));
        Assert.assertTrue(watcher.removeListener(res.get("DirC/FileC2").path(), chkFileC2));
    }

    @Test
    public void testDeleteWhileListening() {
        Resource res = store.get("DirA");
        final ResourceListener deletingListener = new ResourceListener() {
            @Override
            public void changed(ResourceNotification notify) {
                Assert.assertTrue(watcher.removeListener(notify.getPath(), this));
            }
        };
        watcher.addListener(res.path(), deletingListener);
        watcher.changed(new ResourceNotification("DirA", Kind.ENTRY_DELETE, System.currentTimeMillis(), Collections.emptyList()));
        // verify already deleted
        Assert.assertFalse(watcher.removeListener(res.path(), deletingListener));
    }

    @Test
    public void testModifyNotification() {
        Resource res = store.get("DirA/DirC/FileC1");
        final AbstractResourceNotificationDispatcherTest.CheckingResourceListener chkDirA = new AbstractResourceNotificationDispatcherTest.CheckingResourceListener(Kind.ENTRY_MODIFY);
        final AbstractResourceNotificationDispatcherTest.CheckingResourceListener chkDirC = new AbstractResourceNotificationDispatcherTest.CheckingResourceListener(Kind.ENTRY_MODIFY);
        final AbstractResourceNotificationDispatcherTest.CheckingResourceListener chkFileC1 = new AbstractResourceNotificationDispatcherTest.CheckingResourceListener(Kind.ENTRY_MODIFY);
        watcher.addListener(res.path(), chkFileC1);
        watcher.addListener(store.get("DirA/DirC").path(), chkDirC);
        watcher.addListener(store.get("DirA").path(), chkDirA);
        List<Event> events = SimpleResourceNotificationDispatcher.createEvents(res, ENTRY_MODIFY);
        watcher.changed(new ResourceNotification("DirA/DirC/FileC1", Kind.ENTRY_MODIFY, System.currentTimeMillis(), events));
        // test that listeners received events
        Assert.assertFalse(chkDirA.isChecked());
        Assert.assertTrue(chkDirC.isChecked());
        Assert.assertTrue(chkFileC1.isChecked());
        // remove listeners
        Assert.assertTrue(watcher.removeListener(res.path(), chkFileC1));
        Assert.assertTrue(watcher.removeListener(store.get("DirA/DirC").path(), chkDirC));
        Assert.assertTrue(watcher.removeListener(store.get("DirA").path(), chkDirA));
    }

    @Test
    public void testCreateNotification() {
        Resource res = store.get("DirA/DirC/DirD/FileQ");
        final AbstractResourceNotificationDispatcherTest.CheckingResourceListener chkDirA = new AbstractResourceNotificationDispatcherTest.CheckingResourceListener(Kind.ENTRY_MODIFY);
        final AbstractResourceNotificationDispatcherTest.CheckingResourceListener chkDirC = new AbstractResourceNotificationDispatcherTest.CheckingResourceListener(Kind.ENTRY_MODIFY);
        final AbstractResourceNotificationDispatcherTest.CheckingResourceListener chkDirD = new AbstractResourceNotificationDispatcherTest.CheckingResourceListener(Kind.ENTRY_CREATE);
        final AbstractResourceNotificationDispatcherTest.CheckingResourceListener chkFileQ = new AbstractResourceNotificationDispatcherTest.CheckingResourceListener(Kind.ENTRY_CREATE);
        watcher.addListener(res.path(), chkFileQ);
        watcher.addListener(store.get("DirA/DirC/DirD").path(), chkDirD);
        watcher.addListener(store.get("DirA/DirC").path(), chkDirC);
        watcher.addListener(store.get("DirA").path(), chkDirA);
        List<Event> events = SimpleResourceNotificationDispatcher.createEvents(res, ENTRY_CREATE);
        watcher.changed(new ResourceNotification("DirA/DirC/DirD/FileQ", Kind.ENTRY_CREATE, System.currentTimeMillis(), events));
        // test that listeners received events
        Assert.assertFalse(chkDirA.isChecked());
        Assert.assertTrue(chkDirC.isChecked());
        Assert.assertTrue(chkDirD.isChecked());
        Assert.assertTrue(chkFileQ.isChecked());
        // remove listeners
        Assert.assertTrue(watcher.removeListener(res.path(), chkFileQ));
        Assert.assertTrue(watcher.removeListener(store.get("DirA/DirC/DirD").path(), chkDirD));
        Assert.assertTrue(watcher.removeListener(store.get("DirA/DirC").path(), chkDirC));
        Assert.assertTrue(watcher.removeListener(store.get("DirA").path(), chkDirA));
    }
}

