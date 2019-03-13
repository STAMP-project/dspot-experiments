/**
 * Copyright 2016 Roland Huss
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.fabric8.maven.docker.service;


import ContainerTracker.ContainerShutdownDescriptor;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author roland
 * @since 14/02/16
 */
public class ContainerTrackerTest {
    private ContainerTracker tracker;

    @Test
    public void lookup() throws Exception {
        tracker.registerContainer("1234", getImageConfiguration("name", "alias"), getPomLabel("test1"));
        Assert.assertEquals("1234", tracker.lookupContainer("name"));
        Assert.assertEquals("1234", tracker.lookupContainer("alias"));
        Assert.assertNull(tracker.lookupContainer("blub"));
    }

    @Test
    public void removeContainer() throws Exception {
        String[][] data = new String[][]{ new String[]{ "1", "name1", "alias1", "100", "200", "stop1", "label1", "false" }, new String[]{ "2", "name2", "alias2", null, null, null, "label2", "true" } };
        List<ContainerTracker.ContainerShutdownDescriptor> descs = registerAtTracker(data);
        ContainerTracker.ContainerShutdownDescriptor desc = tracker.removeContainer("1");
        verifyDescriptor(data[0], desc);
        Assert.assertNull(tracker.lookupContainer("1"));
        Assert.assertNull(tracker.removeContainer("1"));
        Assert.assertTrue(tracker.removeShutdownDescriptors(getPomLabel("label1")).isEmpty());
        Assert.assertFalse(tracker.removeShutdownDescriptors(getPomLabel("label2")).isEmpty());
        Assert.assertTrue(tracker.removeShutdownDescriptors(getPomLabel("label2")).isEmpty());
    }

    @Test
    public void removeDescriptors() throws Exception {
        String[][] data = new String[][]{ new String[]{ "1", "name1", "alias1", "100", "200", "stop1", "label1", "true" }, new String[]{ "2", "name2", "alias2", null, null, null, "label1", "false" }, new String[]{ "3", "name3", null, null, null, null, "label2", "true" } };
        List<ContainerTracker.ContainerShutdownDescriptor> descs = registerAtTracker(data);
        Collection<ContainerTracker.ContainerShutdownDescriptor> removed = tracker.removeShutdownDescriptors(getPomLabel("label1"));
        Assert.assertEquals(2, removed.size());
        Iterator<ContainerTracker.ContainerShutdownDescriptor> it = removed.iterator();
        // Reverse order
        verifyDescriptor(data[1], it.next());
        verifyDescriptor(data[0], it.next());
        Assert.assertNull(tracker.lookupContainer("name1"));
        Assert.assertNull(tracker.lookupContainer("alias1"));
        Assert.assertNull(tracker.lookupContainer("name2"));
        Assert.assertNull(tracker.lookupContainer("alias2"));
        Assert.assertNotNull(tracker.lookupContainer("name3"));
    }

    @Test
    public void removeAll() throws Exception {
        String[][] data = new String[][]{ new String[]{ "1", "name1", "alias1", "100", "200", "stop1", "label1", "true" }, new String[]{ "2", "name2", "alias2", null, null, null, "label1", "false" }, new String[]{ "3", "name3", null, null, null, null, "label2", "false" } };
        List<ContainerTracker.ContainerShutdownDescriptor> descs = registerAtTracker(data);
        Collection<ContainerTracker.ContainerShutdownDescriptor> removed = tracker.removeShutdownDescriptors(null);
        Assert.assertEquals(3, removed.size());
        Iterator<ContainerTracker.ContainerShutdownDescriptor> it = removed.iterator();
        // Reverse order
        verifyDescriptor(data[2], it.next());
        verifyDescriptor(data[1], it.next());
        verifyDescriptor(data[0], it.next());
        Assert.assertEquals(0, tracker.removeShutdownDescriptors(null).size());
    }
}

