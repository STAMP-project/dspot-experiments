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


import DataHandler.DatabaseTypeListener;
import DataHandler.connectionMap;
import DatabaseMeta.TYPE_ACCESS_JNDI;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.database.BaseDatabaseMeta;
import org.pentaho.di.core.database.DatabaseInterface;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.plugins.DatabasePluginType;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.ui.xul.XulDomContainer;
import org.pentaho.ui.xul.XulException;
import org.pentaho.ui.xul.components.XulCheckbox;
import org.pentaho.ui.xul.components.XulMessageBox;
import org.pentaho.ui.xul.components.XulTextbox;
import org.pentaho.ui.xul.containers.XulDeck;
import org.pentaho.ui.xul.containers.XulListbox;
import org.pentaho.ui.xul.containers.XulRoot;
import org.pentaho.ui.xul.dom.Document;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;


/**
 * Unit test for DataHandler.
 */
public class DataHandlerTest {
    DataHandler dataHandler;

    Document document;

    XulDomContainer xulDomContainer;

    XulListbox accessBox;

    XulListbox connectionBox;

    XulTextbox connectionNameBox;

    XulDeck dialogDeck;

    XulListbox deckOptionsBox;

    XulTextbox hostNameBox;

    XulTextbox databaseNameBox;

    XulTextbox portNumberBox;

    XulTextbox userNameBox;

    XulTextbox passwordBox;

    XulTextbox serverInstanceBox;

    XulTextbox webappName;

    XulMessageBox messageBox;

    XulRoot generalDatasourceWindow;

    @Test
    public void testLoadConnectionData() throws Exception {
        DatabaseInterface dbInterface = Mockito.mock(DatabaseInterface.class);
        Mockito.when(dbInterface.getDefaultDatabasePort()).thenReturn(5309);
        connectionMap.put("myDb", dbInterface);
        dataHandler.loadConnectionData();
        // Should immediately return if called again since the connectionBox will have been loaded
        dataHandler.loadConnectionData();
    }

    @Test
    public void testLoadConnectionDataWithSelectedItem() throws Exception {
        DatabaseInterface dbInterface = Mockito.mock(DatabaseInterface.class);
        Mockito.when(dbInterface.getDefaultDatabasePort()).thenReturn(5309);
        Mockito.when(connectionBox.getSelectedItem()).thenReturn("myDb");
        dataHandler.loadConnectionData();
    }

    @Test
    public void testLoadAccessData() throws Exception {
        Mockito.when(accessBox.getSelectedItem()).thenReturn("Native");
        DatabaseInterface dbInterface = Mockito.mock(DatabaseInterface.class);
        Mockito.when(dbInterface.getDefaultDatabasePort()).thenReturn(5309);
        connectionMap.put("myDb", dbInterface);
        dataHandler.loadAccessData();
        // Should immediately return if called again since the connectionBox will have been loaded
        dataHandler.loadAccessData();
    }

    @Test
    public void testLoadAccessDataWithSelectedItem() throws Exception {
        Mockito.when(accessBox.getSelectedItem()).thenReturn("ODBC");
        DatabaseInterface dbInterface = Mockito.mock(DatabaseInterface.class);
        DatabaseMeta databaseMeta = Mockito.mock(DatabaseMeta.class);
        Mockito.when(dbInterface.getAccessTypeList()).thenReturn(new int[]{ DatabaseMeta.TYPE_ACCESS_NATIVE, DatabaseMeta.TYPE_ACCESS_ODBC });
        Mockito.when(dbInterface.getDefaultDatabasePort()).thenReturn(5309);
        Mockito.when(connectionBox.getSelectedItem()).thenReturn("myDb");
        connectionMap.put("myDb", dbInterface);
        dataHandler.cache = databaseMeta;
        dataHandler.getData();
        dataHandler.loadAccessData();
    }

    @Test(expected = RuntimeException.class)
    public void testGetOptionHelpNoDatabase() throws Exception {
        Mockito.when(accessBox.getSelectedItem()).thenReturn("JNDI");
        Mockito.when(connectionBox.getSelectedItem()).thenReturn("MyDB");
        dataHandler.getOptionHelp();
    }

    @Test
    public void testGetOptionHelp() throws Exception {
        Mockito.when(accessBox.getSelectedItem()).thenReturn("JNDI");
        Mockito.when(connectionBox.getSelectedItem()).thenReturn("PostgreSQL");
        dataHandler.getOptionHelp();
    }

    @Test
    public void testGetSetData() throws Exception {
        Object data = dataHandler.getData();
        Assert.assertNotNull(data);
        Assert.assertTrue((data instanceof DatabaseMeta));
        DatabaseMeta initialDbMeta = ((DatabaseMeta) (data));
        DatabaseMeta dbMeta = Mockito.mock(DatabaseMeta.class);
        Mockito.when(dbMeta.getAccessType()).thenReturn(TYPE_ACCESS_JNDI);
        Properties props = new Properties();
        props.put(((BaseDatabaseMeta.ATTRIBUTE_PREFIX_EXTRA_OPTION) + "KettleThin.webappname"), "foo");
        Mockito.when(dbMeta.getAttributes()).thenReturn(props);
        Mockito.when(accessBox.getSelectedItem()).thenReturn("JNDI");
        Mockito.when(deckOptionsBox.getSelectedIndex()).thenReturn((-1));
        dataHandler.setData(dbMeta);
        Assert.assertEquals(dbMeta, dataHandler.getData());
        Assert.assertNotSame(initialDbMeta, dataHandler.getData());
        Assert.assertFalse(props.containsKey(((BaseDatabaseMeta.ATTRIBUTE_PREFIX_EXTRA_OPTION) + "KettleThin.webappname")));
        Mockito.verify(dbMeta).setDBName("foo");
        dataHandler.setData(null);
        Assert.assertEquals(dbMeta, dataHandler.getData());
    }

    @Test
    public void testPushPopCache() throws Exception {
        dataHandler.getData();
        dataHandler.pushCache();
        dataHandler.popCache();
        Mockito.verify(webappName).setValue("pentaho");
    }

    @Test
    public void testPushCacheUpdatesDatabaseInterface() throws Exception {
        DatabaseMeta databaseMeta = Mockito.mock(DatabaseMeta.class);
        Mockito.when(connectionBox.getSelectedItem()).thenReturn("test");
        dataHandler.cache = databaseMeta;
        dataHandler.getControls();
        dataHandler.getData();
        dataHandler.pushCache();
        Mockito.verify(databaseMeta).setDatabaseType("test");
    }

    @Test
    public void testGetControls() throws Exception {
        dataHandler.getControls();
        Assert.assertNotNull(dataHandler.hostNameBox);
        Assert.assertNotNull(dataHandler.portNumberBox);
        Assert.assertNotNull(dataHandler.userNameBox);
        Assert.assertNotNull(dataHandler.passwordBox);
    }

    @Test
    public void testDisablePortIfInstancePopulated() throws Exception {
        dataHandler.getControls();
        dataHandler.disablePortIfInstancePopulated();
        // Because portNumberBox is a mock, the setDisabled() will not persist, so the above call is for branch coverage
        Mockito.when(serverInstanceBox.getValue()).thenReturn(null);
        dataHandler.disablePortIfInstancePopulated();
        Assert.assertFalse(dataHandler.portNumberBox.isDisabled());
    }

    @Test
    public void testShowMessage() throws Exception {
        dataHandler.showMessage("MyMessage", false);
        dataHandler.showMessage("MyMessage", true);
        Mockito.when(document.createElement("messagebox")).thenThrow(new XulException());
        dataHandler.showMessage("MyMessage", false);
    }

    @Test
    public void testHandleUseSecurityCheckbox() throws Exception {
        dataHandler.handleUseSecurityCheckbox();
        // Now add the widget
        XulCheckbox useIntegratedSecurityCheck = Mockito.mock(XulCheckbox.class);
        Mockito.when(useIntegratedSecurityCheck.isChecked()).thenReturn(false);
        Mockito.when(document.getElementById("use-integrated-security-check")).thenReturn(useIntegratedSecurityCheck);
        dataHandler.getControls();
        dataHandler.handleUseSecurityCheckbox();
        Mockito.when(useIntegratedSecurityCheck.isChecked()).thenReturn(true);
        dataHandler.handleUseSecurityCheckbox();
    }

    @Test
    public void testDatabaseTypeListener() throws Exception {
        DataHandler.DatabaseTypeListener listener = Mockito.spy(new DataHandler.DatabaseTypeListener(PluginRegistry.getInstance()) {
            @Override
            public void databaseTypeAdded(String pluginName, DatabaseInterface databaseInterface) {
            }

            @Override
            public void databaseTypeRemoved(String pluginName) {
            }
        });
        Assert.assertNotNull(listener);
        PluginInterface pluginInterface = Mockito.mock(PluginInterface.class);
        Mockito.when(pluginInterface.getName()).thenReturn("Oracle");
        Mockito.doReturn(DatabaseInterface.class).when(pluginInterface).getMainType();
        Mockito.when(pluginInterface.getIds()).thenReturn(new String[]{ "oracle" });
        Mockito.doReturn(DatabasePluginType.class).when(pluginInterface).getPluginType();
        listener.pluginAdded(pluginInterface);
        // The test can't load the plugin, so databaseTypeAdded never gets called. Perhaps register a mock plugin
        Mockito.verify(listener, Mockito.never()).databaseTypeAdded(eq("Oracle"), any(DatabaseInterface.class));
        listener.pluginRemoved(pluginInterface);
        Mockito.verify(listener, Mockito.times(1)).databaseTypeRemoved("Oracle");
        // Changed calls removed then added
        listener.pluginChanged(pluginInterface);
        Mockito.verify(listener, Mockito.times(2)).databaseTypeRemoved("Oracle");
        Mockito.verify(listener, Mockito.never()).databaseTypeAdded(eq("Oracle"), any(DatabaseInterface.class));
    }
}

