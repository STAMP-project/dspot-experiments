/**
 * Copyright (c) 2015 OpenPlans - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.platform.resource;


import Kind.ENTRY_CREATE;
import Kind.ENTRY_DELETE;
import Kind.ENTRY_MODIFY;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import org.geoserver.platform.resource.ResourceNotification.Event;
import org.geoserver.platform.resource.ResourceNotification.Kind;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Niels Charlier
 */
public class SimpleResourceNotificationDispatcherTest extends AbstractResourceNotificationDispatcherTest {
    @Test
    public void testRenameEvents() {
        Resource src = store.get("DirA");
        Resource dest = store.get("DirB");
        List<Event> events = SimpleResourceNotificationDispatcher.createRenameEvents(src, dest);
        Assert.assertEquals(6, events.size());
        Set<String> set = new HashSet<String>();
        set.add("DirB");
        set.add("DirB/FileA1");
        set.add("DirB/FileA2");
        set.add("DirB/DirC");
        set.add("DirB/DirC/FileC1");
        set.add("DirB/DirC/FileC2");
        for (Event event : events) {
            String path = event.getPath();
            Assert.assertEquals(((path.equals("DirB")) || (path.equals("DirB/FileA2")) ? Kind.ENTRY_MODIFY : Kind.ENTRY_CREATE), event.getKind());
            Assert.assertTrue(set.remove(path));
        }
        Assert.assertTrue(set.isEmpty());
    }

    @Test
    public void testDeleteEvents() {
        Resource res = store.get("DirA");
        List<Event> events = SimpleResourceNotificationDispatcher.createEvents(res, ENTRY_DELETE);
        Assert.assertEquals(6, events.size());
        Set<String> set = new HashSet<String>();
        set.add("DirA");
        set.add("DirA/FileA1");
        set.add("DirA/FileA2");
        set.add("DirA/DirC");
        set.add("DirA/DirC/FileC1");
        set.add("DirA/DirC/FileC2");
        for (Event event : events) {
            Assert.assertEquals(ENTRY_DELETE, event.getKind());
            Assert.assertTrue(set.remove(event.getPath()));
        }
        Assert.assertTrue(set.isEmpty());
    }

    @Test
    public void testCreateEvents() {
        Resource res = store.get("DirD/DirE/DirF/FileQ");
        List<Event> events = SimpleResourceNotificationDispatcher.createEvents(res, ENTRY_CREATE);
        Assert.assertEquals(4, events.size());
        Set<String> set = new HashSet<String>();
        set.add("DirD");
        set.add("DirD/DirE");
        set.add("DirD/DirE/DirF");
        set.add("DirD/DirE/DirF/FileQ");
        for (Event event : events) {
            Assert.assertEquals(ENTRY_CREATE, event.getKind());
            Assert.assertTrue(set.remove(event.getPath()));
        }
        Assert.assertTrue(set.isEmpty());
    }

    @Test
    public void testPropagation() throws IOException {
        SimpleResourceNotificationDispatcher dispatch = new SimpleResourceNotificationDispatcher();
        AtomicReference<ResourceNotification> dirEvent = new AtomicReference<>();
        dispatch.addListener("DirB", new ResourceListener() {
            @Override
            public void changed(ResourceNotification notify) {
                dirEvent.set(notify);
            }
        });
        dispatch.changed(new ResourceNotification("DirB/DirNew/FileNew", Kind.ENTRY_CREATE, System.currentTimeMillis(), SimpleResourceNotificationDispatcher.createEvents(store.get("DirB/DirNew/FileNew"), ENTRY_CREATE)));
        Assert.assertNotNull(dirEvent.get());
        Assert.assertEquals(ENTRY_MODIFY, dirEvent.get().getKind());
        Assert.assertEquals("DirB", dirEvent.get().getPath());
        Assert.assertEquals(2, dirEvent.get().events().size());
        Assert.assertEquals(ENTRY_CREATE, dirEvent.get().events().get(0).getKind());
        Assert.assertEquals("DirNew/FileNew", dirEvent.get().events().get(0).getPath());
        Assert.assertEquals(ENTRY_CREATE, dirEvent.get().events().get(1).getKind());
        Assert.assertEquals("DirNew", dirEvent.get().events().get(1).getPath());
    }
}

