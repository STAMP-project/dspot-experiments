/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.ui.database.event;


import DataHandler.connectionMap;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.database.DatabaseInterface;
import org.pentaho.ui.xul.XulComponent;
import org.pentaho.ui.xul.XulDomContainer;
import org.pentaho.ui.xul.XulException;
import org.pentaho.ui.xul.components.XulMessageBox;
import org.pentaho.ui.xul.components.XulTextbox;
import org.pentaho.ui.xul.containers.XulListbox;
import org.pentaho.ui.xul.dom.Document;


/**
 * Unit tests for FragmentHandler.
 */
public class FragmentHandlerTest {
    FragmentHandler fragmentHandler;

    Document document;

    XulDomContainer xulDomContainer;

    @Test
    public void testRefreshOptions() throws Exception {
        XulListbox connectionBox = Mockito.mock(XulListbox.class);
        Mockito.when(document.getElementById("connection-type-list")).thenReturn(connectionBox);
        Mockito.when(connectionBox.getSelectedItem()).thenReturn("myDb");
        XulListbox accessBox = Mockito.mock(XulListbox.class);
        Mockito.when(document.getElementById("access-type-list")).thenReturn(accessBox);
        Mockito.when(accessBox.getSelectedItem()).thenReturn("Native");
        DataHandler dataHandler = Mockito.mock(DataHandler.class);
        Mockito.when(xulDomContainer.getEventHandler("dataHandler")).thenReturn(dataHandler);
        DatabaseInterface dbInterface = Mockito.mock(DatabaseInterface.class);
        Mockito.when(dbInterface.getDefaultDatabasePort()).thenReturn(5309);
        connectionMap.put("myDb", dbInterface);
        XulComponent component = Mockito.mock(XulComponent.class);
        XulComponent parent = Mockito.mock(XulComponent.class);
        Mockito.when(component.getParent()).thenReturn(parent);
        Mockito.when(document.getElementById("database-options-box")).thenReturn(component);
        XulDomContainer fragmentContainer = Mockito.mock(XulDomContainer.class);
        Document mockDoc = Mockito.mock(Document.class);
        XulComponent firstChild = Mockito.mock(XulComponent.class);
        Mockito.when(mockDoc.getFirstChild()).thenReturn(firstChild);
        Mockito.when(fragmentContainer.getDocumentRoot()).thenReturn(mockDoc);
        Mockito.when(xulDomContainer.loadFragment(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object.class))).thenReturn(fragmentContainer);
        XulTextbox portBox = Mockito.mock(XulTextbox.class);
        Mockito.when(document.getElementById("port-number-text")).thenReturn(portBox);
        fragmentHandler.refreshOptions();
        // Iterate through the other database access types
        Mockito.when(accessBox.getSelectedItem()).thenReturn("JNDI");
        fragmentHandler.refreshOptions();
        Mockito.when(accessBox.getSelectedItem()).thenReturn("ODBC");
        fragmentHandler.refreshOptions();
        Mockito.when(accessBox.getSelectedItem()).thenReturn("OCI");
        fragmentHandler.refreshOptions();
        Mockito.when(accessBox.getSelectedItem()).thenReturn("Plugin");
        fragmentHandler.refreshOptions();
    }

    @Test
    public void testGetSetData() throws Exception {
        // This feature is basically disabled, get returns null and set does nothing
        Assert.assertNull(fragmentHandler.getData());
        Object o = new Object();
        fragmentHandler.setData(o);
        Assert.assertNull(fragmentHandler.getData());
    }

    @Test
    public void testLoadDatabaseOptionsFragment() throws Exception {
        XulComponent component = Mockito.mock(XulComponent.class);
        XulComponent parent = Mockito.mock(XulComponent.class);
        Mockito.when(component.getParent()).thenReturn(parent);
        Mockito.when(document.getElementById("database-options-box")).thenReturn(component);
        XulDomContainer fragmentContainer = Mockito.mock(XulDomContainer.class);
        Document mockDoc = Mockito.mock(Document.class);
        XulComponent firstChild = Mockito.mock(XulComponent.class);
        Mockito.when(mockDoc.getFirstChild()).thenReturn(firstChild);
        Mockito.when(fragmentContainer.getDocumentRoot()).thenReturn(mockDoc);
        Mockito.when(xulDomContainer.loadFragment(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object.class))).thenReturn(fragmentContainer);
        fragmentHandler.loadDatabaseOptionsFragment(null);
    }

    @Test(expected = XulException.class)
    public void testLoadDatabaseOptionsFragmentWithException() throws Exception {
        XulComponent component = Mockito.mock(XulComponent.class);
        XulComponent parent = Mockito.mock(XulComponent.class);
        Mockito.when(component.getParent()).thenReturn(parent);
        Mockito.when(document.getElementById("database-options-box")).thenReturn(component);
        Mockito.when(xulDomContainer.loadFragment(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object.class))).thenThrow(new XulException());
        fragmentHandler.loadDatabaseOptionsFragment(null);
    }

    @Test
    public void testGetFragment() throws Exception {
        DatabaseInterface dbInterface = Mockito.mock(DatabaseInterface.class);
        Assert.assertEquals("org/pentaho/ui/database/", fragmentHandler.getFragment(dbInterface, null, null, null));
        Mockito.when(dbInterface.getXulOverlayFile()).thenReturn("overlay.xul");
        // In real life the xul file should be available in the classpath as a resource, but during testing it won't be.
        // So instead of expecting FragmentHandler.packagePath + overlay.xul, it's just the package path
        Assert.assertEquals("org/pentaho/ui/database/", fragmentHandler.getFragment(dbInterface, null, null, null));
    }

    @Test
    public void testShowMessage() throws Exception {
        XulMessageBox messageBox = Mockito.mock(XulMessageBox.class);
        Mockito.when(document.createElement("messagebox")).thenReturn(messageBox);
        fragmentHandler.showMessage(null);
        // Generate exception, should see a message in standard output
        Mockito.when(document.createElement("messagebox")).thenThrow(new XulException());
        fragmentHandler.showMessage("");
    }
}

