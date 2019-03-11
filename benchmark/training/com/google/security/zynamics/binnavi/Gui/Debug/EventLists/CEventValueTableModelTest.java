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
import com.google.security.zynamics.binnavi.debug.models.trace.TraceEvent;
import com.google.security.zynamics.binnavi.debug.models.trace.TraceEventType;
import com.google.security.zynamics.binnavi.disassembly.INaviModule;
import com.google.security.zynamics.binnavi.disassembly.Modules.MockModule;
import com.google.security.zynamics.zylib.disassembly.CAddress;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class CEventValueTableModelTest {
    @Test
    public void foo() {
        final CEventValueTableModel model = new CEventValueTableModel();
        final INaviModule mockModule = new MockModule();
        final TraceEvent event = new TraceEvent(77, new com.google.security.zynamics.binnavi.debug.models.breakpoints.BreakpointAddress(mockModule, new com.google.security.zynamics.binnavi.disassembly.UnrelocatedAddress(new CAddress(291))), TraceEventType.REGULAR_BREAKPOINT, Lists.newArrayList(new com.google.security.zynamics.binnavi.debug.models.trace.TraceRegister("eax", new CAddress(291), new byte[]{ 5, 6, 7 })));
        Assert.assertEquals(0, model.getRowCount());
        model.setEvent(event);
        Assert.assertEquals(1, model.getRowCount());
        Assert.assertEquals("eax", model.getValueAt(0, 0));
        Assert.assertEquals("00000123", model.getValueAt(0, 1));
        Assert.assertEquals("05 06 07", model.getValueAt(0, 2));
    }
}

