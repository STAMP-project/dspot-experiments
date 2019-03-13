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
package com.google.security.zynamics.binnavi.Gui.GraphWindows.BottomPanel.Variables;


import com.google.security.zynamics.binnavi.Gui.GraphWindows.BottomPanel.viewReferences.ViewReferencesTableModel;
import com.google.security.zynamics.binnavi.disassembly.types.TestTypeSystem;
import com.google.security.zynamics.binnavi.disassembly.views.INaviView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


// TODO(timkornau): Test if the tree is constructed and has all the elements it should have.
@RunWith(JUnit4.class)
public class ViewReferencesTableModelTest {
    TestTypeSystem typeSystem = null;

    INaviView view = null;

    @Test(expected = NullPointerException.class)
    public void testConstructor1() {
        new ViewReferencesTableModel(null);
    }
}

