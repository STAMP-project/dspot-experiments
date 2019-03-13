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
package com.google.security.zynamics.binnavi.Gui.GraphWindows.BottomPanel.CrossReferences;


import com.google.security.zynamics.binnavi.disassembly.MockFunction;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class CCrossReferencesModelTest {
    @Test
    public void test1Simple() {
        final CCrossReferencesModel model = new CCrossReferencesModel();
        final CCrossReference reference = new CCrossReference(new MockFunction(), new MockFunction());
        final List<CCrossReference> crossReferences = new ArrayList<CCrossReference>();
        crossReferences.add(reference);
        model.setCrossReferences(crossReferences);
    }

    @Test
    public void test2GetColumnCount() {
        final CCrossReferencesModel model = new CCrossReferencesModel();
        final CCrossReference reference = new CCrossReference(new MockFunction(), new MockFunction());
        final List<CCrossReference> crossReferences = new ArrayList<CCrossReference>();
        crossReferences.add(reference);
        model.setCrossReferences(crossReferences);
        Assert.assertEquals(2, model.getColumnCount());
    }

    @Test
    public void test3getCoulmName() {
        final CCrossReferencesModel model = new CCrossReferencesModel();
        final CCrossReference reference = new CCrossReference(new MockFunction(), new MockFunction());
        final List<CCrossReference> crossReferences = new ArrayList<CCrossReference>();
        crossReferences.add(reference);
        model.setCrossReferences(crossReferences);
        Assert.assertEquals("Called Function", model.getColumnName(0));
        Assert.assertEquals("Calling Function", model.getColumnName(1));
    }

    @Test
    public void test4getRowCount() {
        final CCrossReferencesModel model = new CCrossReferencesModel();
        final CCrossReference reference = new CCrossReference(new MockFunction(), new MockFunction());
        final List<CCrossReference> crossReferences = new ArrayList<CCrossReference>();
        crossReferences.add(reference);
        model.setCrossReferences(crossReferences);
        Assert.assertEquals(1, model.getRowCount());
    }

    @Test
    public void test5ValueAt() {
        final CCrossReferencesModel model = new CCrossReferencesModel();
        final CCrossReference reference = new CCrossReference(new MockFunction(), new MockFunction());
        final List<CCrossReference> crossReferences = new ArrayList<CCrossReference>();
        crossReferences.add(reference);
        model.setCrossReferences(crossReferences);
        Assert.assertEquals("Mock Function", model.getValueAt(0, 0));
        Assert.assertEquals("Mock Function", model.getValueAt(0, 1));
    }
}

