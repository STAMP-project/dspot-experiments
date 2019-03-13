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
package com.google.security.zynamics.binnavi.Gui.MainWindow.ProjectTree.Nodes.AddressSpace.Component;


import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntDeleteException;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntLoadDataException;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntSaveDataException;
import com.google.security.zynamics.binnavi.Database.Exceptions.LoadCancelledException;
import com.google.security.zynamics.binnavi.Database.MockClasses.MockDatabase;
import com.google.security.zynamics.binnavi.Database.MockClasses.MockSqlProvider;
import com.google.security.zynamics.binnavi.Gui.DebuggerComboBox.CDebuggerComboBox;
import com.google.security.zynamics.binnavi.Gui.StandardEditPanel.CStandardEditPanel;
import com.google.security.zynamics.binnavi.debug.debugger.DebuggerTemplate;
import com.google.security.zynamics.binnavi.disassembly.AddressSpaces.CAddressSpace;
import com.google.security.zynamics.binnavi.disassembly.CProject;
import com.google.security.zynamics.binnavi.disassembly.Modules.CModule;
import com.google.security.zynamics.zylib.disassembly.CAddress;
import com.google.security.zynamics.zylib.reflection.ReflectionHelpers;
import java.util.Date;
import java.util.LinkedHashSet;
import javax.swing.JButton;
import javax.swing.border.TitledBorder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class CAddressSpaceNodeComponentTest {
    private JButton m_saveButton;

    private CStandardEditPanel m_editPanel;

    private CAddressSpaceNodeComponent m_component;

    private MockDatabase m_database;

    private CProject m_project;

    private CAddressSpace m_addressSpace;

    private TitledBorder m_titledBorder;

    private CDebuggerComboBox m_debuggerCombo;

    private final MockSqlProvider m_provider = new MockSqlProvider();

    @Test
    public void testChangingDebugger() throws CouldntSaveDataException, IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        final DebuggerTemplate template1 = new DebuggerTemplate(1, "", "", 0, m_provider);
        final DebuggerTemplate template2 = new DebuggerTemplate(1, "", "", 0, m_provider);
        m_database.getContent().getDebuggerTemplateManager().addDebugger(template1);
        m_database.getContent().getDebuggerTemplateManager().addDebugger(template2);
        m_project.getConfiguration().addDebugger(template1);
        m_project.getConfiguration().addDebugger(template2);
        Assert.assertFalse(m_saveButton.isEnabled());
        m_debuggerCombo.setSelectedIndex(2);
        Assert.assertTrue(m_saveButton.isEnabled());
        m_addressSpace.getConfiguration().setDebuggerTemplate(template2);
        Assert.assertFalse(m_saveButton.isEnabled());
        m_addressSpace.getConfiguration().setDebuggerTemplate(template1);
        Assert.assertFalse(m_saveButton.isEnabled());
        testListenersRemoved();
    }

    @Test
    public void testChangingDescription() throws CouldntSaveDataException, IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        Assert.assertFalse(m_saveButton.isEnabled());
        m_editPanel.setDescription("Hannes");
        Assert.assertTrue(m_saveButton.isEnabled());
        m_addressSpace.getConfiguration().setDescription("Hannes");
        sleep();
        Assert.assertFalse(m_saveButton.isEnabled());
        m_addressSpace.getConfiguration().setDescription("Hannes2");
        sleep();
        Assert.assertEquals("Hannes2", m_editPanel.getDescription());
        Assert.assertFalse(m_saveButton.isEnabled());
        testListenersRemoved();
    }

    @Test
    public void testChangingModules() throws CouldntDeleteException, CouldntLoadDataException, CouldntSaveDataException, LoadCancelledException, IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        m_addressSpace.load();
        final CModule module = new CModule(123, "Name", "Comment", new Date(), new Date(), "12345678123456781234567812345678", "1234567812345678123456781234567812345678", 55, 66, new CAddress(1365), new CAddress(1638), new DebuggerTemplate(1, "Mock Debugger", "localhaus", 88, m_provider), null, Integer.MAX_VALUE, false, m_provider);
        final String previousBorderTest = m_titledBorder.getTitle();
        Assert.assertSame(previousBorderTest, m_titledBorder.getTitle());
        m_addressSpace.getContent().addModule(module);
        Assert.assertNotSame(previousBorderTest, m_titledBorder.getTitle());
        m_addressSpace.getContent().removeModule(module);
        Assert.assertEquals(previousBorderTest, m_titledBorder.getTitle());
        testListenersRemoved();
    }

    @Test
    public void testChangingName() throws CouldntSaveDataException, IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        Assert.assertFalse(m_saveButton.isEnabled());
        m_editPanel.setNameString("Hannes");
        Assert.assertTrue(m_saveButton.isEnabled());
        m_addressSpace.getConfiguration().setName("Hannes");
        sleep();
        Assert.assertFalse(m_saveButton.isEnabled());
        m_addressSpace.getConfiguration().setName("Hannes2");
        sleep();
        Assert.assertEquals("Hannes2", m_editPanel.getNameString());
        Assert.assertFalse(m_saveButton.isEnabled());
        testListenersRemoved();
    }

    @Test
    public void testListenersRemoved() throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        m_component.dispose();
        Assert.assertTrue((((LinkedHashSet<?>) (ReflectionHelpers.getField(ReflectionHelpers.getField(m_database, "listeners"), "m_listeners"))) == null));
        Assert.assertTrue(((LinkedHashSet<?>) (ReflectionHelpers.getField(ReflectionHelpers.getField(m_project, "m_listeners"), "m_listeners"))).isEmpty());
        Assert.assertTrue(((LinkedHashSet<?>) (ReflectionHelpers.getField(ReflectionHelpers.getField(m_addressSpace, "m_listeners"), "m_listeners"))).isEmpty());
    }

    @Test
    public void testSaveButton() throws IllegalAccessException, IllegalArgumentException, InterruptedException, NoSuchFieldException, SecurityException {
        m_editPanel.setNameString("New Name");
        m_editPanel.setDescription("New Description");
        Assert.assertTrue(m_saveButton.isEnabled());
        m_saveButton.getAction().actionPerformed(null);
        Thread.sleep(500);
        Assert.assertFalse(m_saveButton.isEnabled());
        Assert.assertEquals("New Name", m_addressSpace.getConfiguration().getName());
        Assert.assertEquals("New Description", m_addressSpace.getConfiguration().getDescription());
        testListenersRemoved();
    }
}

