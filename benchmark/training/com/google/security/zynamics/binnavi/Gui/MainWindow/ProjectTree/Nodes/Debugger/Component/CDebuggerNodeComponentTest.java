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
package com.google.security.zynamics.binnavi.Gui.MainWindow.ProjectTree.Nodes.Debugger.Component;


import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntSaveDataException;
import com.google.security.zynamics.binnavi.Database.MockClasses.MockDatabase;
import com.google.security.zynamics.binnavi.Database.MockClasses.MockSqlProvider;
import com.google.security.zynamics.binnavi.debug.debugger.DebuggerTemplate;
import java.util.LinkedHashSet;
import javax.swing.JButton;
import javax.swing.JTextField;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class CDebuggerNodeComponentTest {
    private CDebuggerNodeComponent m_component;

    @SuppressWarnings("unused")
    private MockDatabase m_database;

    private final MockSqlProvider m_provider = new MockSqlProvider();

    private final DebuggerTemplate m_template = new DebuggerTemplate(1, "Foo", "Bar", 0, m_provider);

    private JTextField m_nameTextField;

    private JTextField m_hostTextField;

    private JTextField m_portTextField;

    private JButton m_saveButton;

    private LinkedHashSet<?> m_templateListeners;

    @Test
    public void testChangingHost() throws CouldntSaveDataException, IllegalArgumentException, SecurityException {
        Assert.assertFalse(m_saveButton.isEnabled());
        m_hostTextField.setText("Hannes");
        Assert.assertTrue(m_saveButton.isEnabled());
        m_template.setHost("Hannes");
        Assert.assertFalse(m_saveButton.isEnabled());
        m_template.setHost("Hannes2");
        Assert.assertEquals("Hannes2", m_hostTextField.getText());
        Assert.assertFalse(m_saveButton.isEnabled());
        testListenersRemoved();
    }

    @Test
    public void testChangingName() throws CouldntSaveDataException, IllegalArgumentException, SecurityException {
        Assert.assertFalse(m_saveButton.isEnabled());
        m_nameTextField.setText("Hannes");
        Assert.assertTrue(m_saveButton.isEnabled());
        m_template.setName("Hannes");
        Assert.assertFalse(m_saveButton.isEnabled());
        m_template.setName("Hannes2");
        Assert.assertEquals("Hannes2", m_nameTextField.getText());
        Assert.assertFalse(m_saveButton.isEnabled());
        testListenersRemoved();
    }

    @Test
    public void testChangingPort() throws CouldntSaveDataException, IllegalArgumentException, SecurityException {
        Assert.assertFalse(m_saveButton.isEnabled());
        m_portTextField.setText("999");
        Assert.assertTrue(m_saveButton.isEnabled());
        m_template.setPort(999);
        Assert.assertFalse(m_saveButton.isEnabled());
        m_template.setPort(123);
        Assert.assertEquals("123", m_portTextField.getText());
        Assert.assertFalse(m_saveButton.isEnabled());
        testListenersRemoved();
    }

    @Test
    public void testListenersRemoved() {
        m_component.dispose();
        Assert.assertTrue(m_templateListeners.isEmpty());
    }

    @Test
    public void testSaveButton() {
        m_nameTextField.setText("New Name");
        m_portTextField.setText("999");
        m_hostTextField.setText("Hannes");
        Assert.assertTrue(m_saveButton.isEnabled());
        m_saveButton.getAction().actionPerformed(null);
        Assert.assertFalse(m_saveButton.isEnabled());
        Assert.assertEquals("New Name", m_template.getName());
        Assert.assertEquals(999, m_template.getPort());
        Assert.assertEquals("Hannes", m_template.getHost());
        testListenersRemoved();
    }
}

