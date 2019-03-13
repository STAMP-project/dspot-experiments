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
package com.google.security.zynamics.binnavi.Gui.MainWindow.ProjectTree.Nodes.Module.Component;


import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntSaveDataException;
import com.google.security.zynamics.binnavi.Database.MockClasses.MockDatabase;
import com.google.security.zynamics.binnavi.Database.MockClasses.MockSqlProvider;
import com.google.security.zynamics.binnavi.Gui.DebuggerComboBox.CDebuggerComboBox;
import com.google.security.zynamics.binnavi.Gui.StandardEditPanel.CStandardEditPanel;
import com.google.security.zynamics.binnavi.debug.debugger.DebuggerTemplate;
import com.google.security.zynamics.binnavi.disassembly.CProject;
import com.google.security.zynamics.binnavi.disassembly.INaviModule;
import com.google.security.zynamics.binnavi.disassembly.views.IViewContainer;
import com.google.security.zynamics.zylib.disassembly.CAddress;
import com.google.security.zynamics.zylib.reflection.ReflectionHelpers;
import java.util.Collection;
import javax.swing.JButton;
import javax.swing.JTextField;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class CModuleOverviewPanelTest {
    private JButton m_saveButton;

    private CStandardEditPanel m_editPanel;

    private CModuleOverviewPanel m_component;

    private MockDatabase m_database;

    private CProject m_project;

    private CDebuggerComboBox m_debuggerCombo;

    private final MockSqlProvider m_provider = new MockSqlProvider();

    private INaviModule m_module;

    private IViewContainer m_viewContainer;

    private JTextField m_fileBaseAddr;

    private JTextField m_imageBaseAddr;

    @Test
    public void testChangingDebugger() throws CouldntSaveDataException, IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        final DebuggerTemplate template1 = new DebuggerTemplate(1, "A", "", 0, m_provider);
        final DebuggerTemplate template2 = new DebuggerTemplate(1, "B", "", 0, m_provider);
        m_database.getContent().getDebuggerTemplateManager().addDebugger(template1);
        m_database.getContent().getDebuggerTemplateManager().addDebugger(template2);
        m_project.getConfiguration().addDebugger(template1);
        m_project.getConfiguration().addDebugger(template2);
        sleep();
        Assert.assertFalse(m_saveButton.isEnabled());
        m_debuggerCombo.setSelectedIndex(3);
        sleep();
        Assert.assertTrue(m_saveButton.isEnabled());
        m_module.getConfiguration().setDebuggerTemplate(template2);
        sleep();
        Assert.assertFalse(m_saveButton.isEnabled());
        m_module.getConfiguration().setDebuggerTemplate(template1);
        sleep();
        Assert.assertFalse(m_saveButton.isEnabled());
        testListenersRemoved();
    }

    @Test
    public void testChangingDescription() throws CouldntSaveDataException, IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        Assert.assertFalse(m_saveButton.isEnabled());
        m_editPanel.setDescription("Hannes");
        Assert.assertTrue(m_saveButton.isEnabled());
        m_module.getConfiguration().setDescription("Hannes");
        sleep();
        Assert.assertFalse(m_saveButton.isEnabled());
        m_module.getConfiguration().setDescription("Hannes2");
        sleep();
        Assert.assertEquals("Hannes2", m_editPanel.getDescription());
        Assert.assertFalse(m_saveButton.isEnabled());
        testListenersRemoved();
    }

    @Test
    public void testChangingFilebase() throws CouldntSaveDataException, IllegalAccessException, IllegalArgumentException, InterruptedException, NoSuchFieldException, SecurityException {
        Assert.assertFalse(m_saveButton.isEnabled());
        m_fileBaseAddr.setText("1");
        Thread.sleep(100);
        Assert.assertTrue(m_saveButton.isEnabled());
        m_module.getConfiguration().setFileBase(new CAddress(1));
        Thread.sleep(100);
        Assert.assertFalse(m_saveButton.isEnabled());
        m_module.getConfiguration().setFileBase(new CAddress(2));
        Thread.sleep(100);
        Assert.assertEquals("00000002", m_fileBaseAddr.getText());
        Assert.assertFalse(m_saveButton.isEnabled());
        testListenersRemoved();
    }

    @Test
    public void testChangingImagebase() throws CouldntSaveDataException, IllegalAccessException, IllegalArgumentException, InterruptedException, NoSuchFieldException, SecurityException {
        Assert.assertFalse(m_saveButton.isEnabled());
        m_imageBaseAddr.setText("1");
        Thread.sleep(100);
        Assert.assertTrue(m_saveButton.isEnabled());
        m_module.getConfiguration().setImageBase(new CAddress(1));
        Thread.sleep(100);
        Assert.assertFalse(m_saveButton.isEnabled());
        m_module.getConfiguration().setImageBase(new CAddress(2));
        Thread.sleep(100);
        Assert.assertEquals("00000002", m_imageBaseAddr.getText());
        Assert.assertFalse(m_saveButton.isEnabled());
        testListenersRemoved();
    }

    @Test
    public void testChangingName() throws CouldntSaveDataException, IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        Assert.assertFalse(m_saveButton.isEnabled());
        m_editPanel.setNameString("Hannes");
        sleep();
        Assert.assertTrue(m_saveButton.isEnabled());
        m_module.getConfiguration().setName("Hannes");
        sleep();
        Assert.assertFalse(m_saveButton.isEnabled());
        m_module.getConfiguration().setName("Hannes2");
        sleep();
        Assert.assertEquals("Hannes2", m_editPanel.getNameString());
        Assert.assertFalse(m_saveButton.isEnabled());
        testListenersRemoved();
    }

    @Test
    public void testListenersRemoved() throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        m_component.dispose();
        Assert.assertTrue((((Collection<?>) (ReflectionHelpers.getField(ReflectionHelpers.getField(m_database, "listeners"), "m_listeners"))) == null));
        Assert.assertTrue((((Collection<?>) (ReflectionHelpers.getField(ReflectionHelpers.getField(m_project, "m_listeners"), "m_listeners"))) == null));
    }

    @Test
    public void testSaveButton() throws IllegalAccessException, IllegalArgumentException, InterruptedException, NoSuchFieldException, SecurityException {
        m_editPanel.setNameString("New Name");
        m_editPanel.setDescription("New Description");
        Assert.assertTrue(m_saveButton.isEnabled());
        m_saveButton.getAction().actionPerformed(null);
        Thread.sleep(500);
        Assert.assertFalse(m_saveButton.isEnabled());
        Assert.assertEquals("New Name", m_module.getConfiguration().getName());
        Assert.assertEquals("New Description", m_module.getConfiguration().getDescription());
        testListenersRemoved();
    }
}

