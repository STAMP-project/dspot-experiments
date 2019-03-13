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
package com.google.security.zynamics.binnavi.Gui.Debug.EventLists;


import com.google.common.collect.Lists;
import com.google.security.zynamics.binnavi.Database.MockClasses.MockSqlProvider;
import com.google.security.zynamics.binnavi.Gui.FilterPanel.IFilter;
import com.google.security.zynamics.binnavi.debug.models.trace.TraceEventType;
import com.google.security.zynamics.binnavi.debug.models.trace.TraceList;
import com.google.security.zynamics.binnavi.debug.models.trace.interfaces.ITraceEvent;
import com.google.security.zynamics.binnavi.disassembly.INaviModule;
import com.google.security.zynamics.binnavi.disassembly.Modules.MockModule;
import com.google.security.zynamics.zylib.disassembly.CAddress;
import org.antlr.runtime.RecognitionException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class CEventTableModelTest {
    /**
     * General test whether the model is working as expected.
     *
     * @throws RecognitionException
     * 		
     */
    @Test
    public void testSimple() throws RecognitionException {
        final INaviModule mockModule = new MockModule();
        final INaviModule mockModule2 = new MockModule(true);
        final INaviModule mockModule3 = new MockModule();
        final CEventTableModel model = new CEventTableModel();
        final TraceList list = new TraceList(1, "", "", new MockSqlProvider());
        list.addEvent(new com.google.security.zynamics.binnavi.debug.models.trace.TraceEvent(1, new com.google.security.zynamics.binnavi.debug.models.breakpoints.BreakpointAddress(mockModule, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(291))), TraceEventType.REGULAR_BREAKPOINT, Lists.newArrayList(new com.google.security.zynamics.binnavi.debug.models.trace.TraceRegister("eax", new CAddress(291), new byte[]{ 5 }))));
        list.addEvent(new com.google.security.zynamics.binnavi.debug.models.trace.TraceEvent(1, new com.google.security.zynamics.binnavi.debug.models.breakpoints.BreakpointAddress(mockModule2, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(292))), TraceEventType.REGULAR_BREAKPOINT, Lists.newArrayList(new com.google.security.zynamics.binnavi.debug.models.trace.TraceRegister("eax", new CAddress(291), new byte[]{ 6 }))));
        list.addEvent(new com.google.security.zynamics.binnavi.debug.models.trace.TraceEvent(1, new com.google.security.zynamics.binnavi.debug.models.breakpoints.BreakpointAddress(mockModule3, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(293))), TraceEventType.REGULAR_BREAKPOINT, Lists.newArrayList(new com.google.security.zynamics.binnavi.debug.models.trace.TraceRegister("eax", new CAddress(291), new byte[]{ 7, 5 }))));
        Assert.assertEquals(0, model.getRowCount());
        model.setEventList(list);
        Assert.assertEquals(3, model.getRowCount());
        final IFilter<ITraceEvent> filter = new CTraceFilterCreator().createFilter("mem == 05");
        model.setFilter(filter);
        Assert.assertEquals(2, model.getRowCount());
        Assert.assertEquals(2, model.getEvents().size());
        Assert.assertEquals(291, model.getEvents().get(0).getOffset().getAddress().getAddress().toLong());
        Assert.assertEquals(293, model.getEvents().get(1).getOffset().getAddress().getAddress().toLong());
        model.setFilter(null);
        Assert.assertEquals(3, model.getRowCount());
        for (int i = 0; i < (model.getRowCount()); i++) {
            for (int j = 0; j < (model.getColumnCount()); j++) {
                model.getValueAt(i, j);
            }
        }
    }
}

