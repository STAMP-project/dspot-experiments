/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.security.zynamics.binnavi.disassembly;


import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntDeleteException;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntLoadDataException;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntSaveDataException;
import com.google.security.zynamics.binnavi.Database.Exceptions.LoadCancelledException;
import com.google.security.zynamics.binnavi.Database.Interfaces.SQLProvider;
import com.google.security.zynamics.binnavi.Database.MockClasses.MockSqlProvider;
import com.google.security.zynamics.binnavi.Tagging.TagType;
import com.google.security.zynamics.binnavi.debug.models.trace.TraceList;
import com.google.security.zynamics.binnavi.disassembly.AddressSpaces.CAddressSpace;
import com.google.security.zynamics.binnavi.disassembly.views.CViewFilter;
import com.google.security.zynamics.binnavi.disassembly.views.INaviView;
import com.google.security.zynamics.zylib.general.ListenerProvider;
import com.google.security.zynamics.zylib.types.lists.IFilledList;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class CProjectContentTest {
    private final INaviProject m_project = new MockProject();

    private final ListenerProvider<IProjectListener> m_listeners = new ListenerProvider<IProjectListener>();

    private final IProjectListener m_listener = new MockProjectListener();

    private final SQLProvider m_provider = new MockSqlProvider();

    private final List<CAddressSpace> m_addressSpaces = new ArrayList<CAddressSpace>();

    private final List<INaviView> m_views = new ArrayList<INaviView>();

    private final IFilledList<TraceList> m_traces = new com.google.security.zynamics.zylib.types.lists.FilledList<TraceList>();

    @Test
    public void testClose() throws CouldntLoadDataException, CouldntSaveDataException, LoadCancelledException {
        final CProjectContent projectContent = new CProjectContent(m_project, m_listeners, m_provider, m_addressSpaces, m_views, m_traces);
        Assert.assertNotNull(projectContent);
        final CAddressSpace spaceOne = projectContent.createAddressSpace("Address Space 1");
        spaceOne.load();
        final CAddressSpace spaceTwo = projectContent.createAddressSpace("Address Space 2");
        spaceTwo.load();
        final CAddressSpace spaceThree = projectContent.createAddressSpace("Address Space 3");
        spaceThree.load();
        final CAddressSpace spaceFour = projectContent.createAddressSpace("Address Space 4");
        spaceFour.load();
        @SuppressWarnings("unused")
        final INaviView viewOne = projectContent.createView(new MockView(m_provider), "View 1 Name", "View 1 description");
        projectContent.close();
    }

    @Test
    public void testCreateAddressSpace() throws CouldntLoadDataException, CouldntSaveDataException, LoadCancelledException {
        final CProjectContent projectContent = new CProjectContent(m_project, m_listeners, m_provider, m_addressSpaces, m_views, m_traces);
        final CAddressSpace spaceOne = projectContent.createAddressSpace("Address Space 1");
        spaceOne.load();
        try {
            projectContent.createAddressSpace(null);
            Assert.fail();
        } catch (final NullPointerException e) {
        }
    }

    @Test
    public void testCreateTrace() throws CouldntSaveDataException {
        final CProjectContent projectContent = new CProjectContent(m_project, m_listeners, m_provider, m_addressSpaces, m_views, m_traces);
        Assert.assertNotNull(projectContent);
        final TraceList trace = projectContent.createTrace("Trace Name", "Trace Description");
        Assert.assertNotNull(trace);
        try {
            projectContent.createTrace(null, null);
            Assert.fail();
        } catch (final NullPointerException e) {
        }
        try {
            projectContent.createTrace("", null);
            Assert.fail();
        } catch (final NullPointerException e) {
        }
    }

    @Test
    public void testCreateView() {
        final CProjectContent projectContent = new CProjectContent(m_project, m_listeners, m_provider, m_addressSpaces, m_views, m_traces);
        final INaviView view = new MockView(m_provider);
        final INaviView view2 = projectContent.createView(view, "Name", "Description");
        Assert.assertNotNull(view2);
        try {
            projectContent.createView(null, "Name", "Description");
            Assert.fail();
        } catch (final NullPointerException e) {
        }
        try {
            projectContent.createView(view, null, "Description");
            Assert.fail();
        } catch (final NullPointerException e) {
        }
        try {
            projectContent.createView(view, "Name", null);
            Assert.fail();
        } catch (final NullPointerException e) {
        }
        final INaviView wrongView = new MockView(new MockSqlProvider());
        try {
            projectContent.createView(wrongView, "Name", "test");
            Assert.fail();
        } catch (final IllegalArgumentException e) {
        }
    }

    @Test
    public void testCreateView2() {
        final CProjectContent projectContent = new CProjectContent(m_project, m_listeners, m_provider, m_addressSpaces, m_views, m_traces);
        @SuppressWarnings("unused")
        final INaviView view = new MockView(m_provider);
        final INaviView view2 = projectContent.createView("Name", "description");
        Assert.assertNotNull(view2);
        try {
            projectContent.createView(null, "description");
            Assert.fail();
        } catch (final NullPointerException e) {
        }
    }

    @Test
    public void testDeleteView() throws CouldntDeleteException {
        final CProjectContent projectContent = new CProjectContent(m_project, m_listeners, m_provider, m_addressSpaces, m_views, m_traces);
        @SuppressWarnings("unused")
        final INaviView view = new MockView(m_provider);
        final INaviView view2 = projectContent.createView("Name", "description");
        Assert.assertNotNull(view2);
        try {
            projectContent.deleteView(null);
            Assert.fail();
        } catch (final NullPointerException e) {
        }
        projectContent.deleteView(view2);
        final INaviView view3 = new MockView(new MockSqlProvider());
        try {
            projectContent.deleteView(view3);
            Assert.fail();
        } catch (final IllegalArgumentException e) {
        }
    }

    @Test
    public void testGetAddressSpaces() {
        final CProjectContent projectContent = new CProjectContent(m_project, m_listeners, m_provider, m_addressSpaces, m_views, m_traces);
        final List<INaviAddressSpace> addressSpaces = projectContent.getAddressSpaces();
        Assert.assertNotNull(addressSpaces);
        projectContent.close();
    }

    @Test
    public void testGetFlowgraphViews() {
        final CProjectContent projectContent = new CProjectContent(m_project, m_listeners, m_provider, m_addressSpaces, m_views, m_traces);
        @SuppressWarnings("unused")
        final INaviView view = new MockView(m_provider);
        @SuppressWarnings("unused")
        final INaviView view2 = projectContent.createView("Name", "description");
        Assert.assertNotNull(CViewFilter.getFlowgraphViewCount(projectContent.getViews()));
    }

    @Test
    public void testGetMixedGraphViews() {
        final CProjectContent projectContent = new CProjectContent(m_project, m_listeners, m_provider, m_addressSpaces, m_views, m_traces);
        @SuppressWarnings("unused")
        final INaviView view = new MockView(m_provider);
        @SuppressWarnings("unused")
        final INaviView view2 = projectContent.createView("Name", "description");
        Assert.assertNotNull(CViewFilter.getMixedgraphViewCount(projectContent.getViews()));
    }

    @Test
    public void testGetTaggedViews() {
        final CProjectContent projectContent = new CProjectContent(m_project, m_listeners, m_provider, m_addressSpaces, m_views, m_traces);
        @SuppressWarnings("unused")
        final INaviView view = new MockView(m_provider);
        @SuppressWarnings("unused")
        final INaviView view2 = projectContent.createView("Name", "description");
        Assert.assertNotNull(CViewFilter.getTaggedViews(projectContent.getViews()));
    }

    @Test
    public void testGetTaggedViews2() {
        final CProjectContent projectContent = new CProjectContent(m_project, m_listeners, m_provider, m_addressSpaces, m_views, m_traces);
        @SuppressWarnings("unused")
        final INaviView view = new MockView(m_provider);
        @SuppressWarnings("unused")
        final INaviView view2 = projectContent.createView("Name", "description");
        Assert.assertNotNull(CViewFilter.getTaggedViews(projectContent.getViews(), new com.google.security.zynamics.binnavi.Tagging.CTag(4, "foo", "bar", TagType.VIEW_TAG, m_provider)));
    }

    @Test
    public void testGetTraceCount() {
        final CProjectContent projectContent = new CProjectContent(m_project, m_listeners, m_provider, m_addressSpaces, m_views, m_traces);
        Assert.assertEquals(0, projectContent.getTraceCount());
    }

    @Test
    public void testGetTraces() {
        final CProjectContent projectContent = new CProjectContent(m_project, m_listeners, m_provider, m_addressSpaces, m_views, m_traces);
        Assert.assertNotNull(projectContent.getTraces());
    }

    @Test
    public void testGetViews() {
        final CProjectContent projectContent = new CProjectContent(m_project, m_listeners, m_provider, m_addressSpaces, m_views, m_traces);
        @SuppressWarnings("unused")
        final INaviView view = new MockView(m_provider);
        @SuppressWarnings("unused")
        final INaviView view2 = projectContent.createView("Name", "description");
        final List<INaviView> views = projectContent.getViews();
        Assert.assertEquals(1, views.size());
    }

    @Test
    public void testRemoveAddressSpace() throws CouldntDeleteException, CouldntLoadDataException, CouldntSaveDataException, LoadCancelledException {
        final CProjectContent projectContent = new CProjectContent(m_project, m_listeners, m_provider, m_addressSpaces, m_views, m_traces);
        @SuppressWarnings("unused")
        final INaviView view = new MockView(m_provider);
        @SuppressWarnings("unused")
        final INaviView view2 = projectContent.createView("Name", "description");
        Assert.assertNotNull(CViewFilter.getTaggedViews(projectContent.getViews(), new com.google.security.zynamics.binnavi.Tagging.CTag(4, "foo", "bar", TagType.VIEW_TAG, m_provider)));
        final CAddressSpace spaceOne = projectContent.createAddressSpace("Address Space 1");
        spaceOne.load();
        final CAddressSpace spaceTwo = projectContent.createAddressSpace("Address Space 2");
        spaceTwo.load();
        final CAddressSpace spaceThree = projectContent.createAddressSpace("Address Space 3");
        spaceThree.load();
        final CAddressSpace spaceFour = projectContent.createAddressSpace("Address Space 4");
        spaceFour.load();
        m_project.load();
        try {
            Assert.assertFalse(projectContent.removeAddressSpace(spaceThree));
            Assert.fail();
        } catch (final IllegalStateException e) {
        }
        spaceThree.close();
        Assert.assertTrue(projectContent.removeAddressSpace(spaceThree));
        try {
            Assert.assertFalse(projectContent.removeAddressSpace(spaceThree));
            Assert.fail();
        } catch (final IllegalArgumentException e) {
        }
        try {
            Assert.assertFalse(projectContent.removeAddressSpace(null));
            Assert.fail();
        } catch (final NullPointerException e) {
        }
        m_project.close();
        try {
            Assert.assertFalse(projectContent.removeAddressSpace(spaceFour));
            Assert.fail();
        } catch (final IllegalStateException e) {
        }
    }

    @Test
    public void testRemoveTrace() throws CouldntDeleteException, CouldntLoadDataException, CouldntSaveDataException, LoadCancelledException {
        final CProjectContent projectContent = new CProjectContent(m_project, m_listeners, m_provider, m_addressSpaces, m_views, m_traces);
        @SuppressWarnings("unused")
        final INaviView view = new MockView(m_provider);
        @SuppressWarnings("unused")
        final INaviView view2 = projectContent.createView("Name", "description");
        Assert.assertNotNull(CViewFilter.getTaggedViews(projectContent.getViews(), new com.google.security.zynamics.binnavi.Tagging.CTag(4, "foo", "bar", TagType.VIEW_TAG, m_provider)));
        final CAddressSpace spaceOne = projectContent.createAddressSpace("Address Space 1");
        spaceOne.load();
        final CAddressSpace spaceTwo = projectContent.createAddressSpace("Address Space 2");
        spaceTwo.load();
        final CAddressSpace spaceThree = projectContent.createAddressSpace("Address Space 3");
        spaceThree.load();
        final CAddressSpace spaceFour = projectContent.createAddressSpace("Address Space 4");
        spaceFour.load();
        m_project.load();
        final TraceList trace = new TraceList(3, "name", "desc", m_provider);
        projectContent.removeTrace(trace);
        try {
            projectContent.removeTrace(null);
            Assert.fail();
        } catch (final NullPointerException e) {
        }
        final TraceList trace2 = new TraceList(3, "name", "desc", new MockSqlProvider());
        try {
            projectContent.removeTrace(trace2);
            Assert.fail();
        } catch (final Exception e) {
        }
    }

    @Test
    public void testSimple1() {
        @SuppressWarnings("unused")
        final CProjectContent projectContent = new CProjectContent(m_project, m_listeners, m_provider, m_addressSpaces, m_views, m_traces);
        try {
            new CProjectContent(null, m_listeners, m_provider, m_addressSpaces, m_views, m_traces);
            Assert.fail();
        } catch (final NullPointerException e) {
        }
        try {
            new CProjectContent(m_project, null, m_provider, m_addressSpaces, m_views, m_traces);
            Assert.fail();
        } catch (final NullPointerException e) {
        }
        try {
            new CProjectContent(m_project, m_listeners, null, m_addressSpaces, m_views, m_traces);
            Assert.fail();
        } catch (final NullPointerException e) {
        }
        try {
            new CProjectContent(m_project, m_listeners, m_provider, null, m_views, m_traces);
            Assert.fail();
        } catch (final NullPointerException e) {
        }
        try {
            new CProjectContent(m_project, m_listeners, m_provider, m_addressSpaces, null, m_traces);
            Assert.fail();
        } catch (final NullPointerException e) {
        }
        try {
            new CProjectContent(m_project, m_listeners, m_provider, m_addressSpaces, m_views, null);
            Assert.fail();
        } catch (final NullPointerException e) {
        }
    }
}

