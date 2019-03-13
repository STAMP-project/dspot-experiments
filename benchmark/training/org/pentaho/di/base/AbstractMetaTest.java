/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.base;


import AbstractMeta.TYPE_UNDO_CHANGE;
import AbstractMeta.TYPE_UNDO_DELETE;
import AbstractMeta.TYPE_UNDO_NEW;
import AbstractMeta.TYPE_UNDO_POSITION;
import LogLevel.BASIC;
import LogLevel.DEBUG;
import PentahoDefaults.DATABASE_CONNECTION_ELEMENT_TYPE_NAME;
import PentahoDefaults.NAMESPACE;
import Props.STRING_ASK_ABOUT_REPLACING_DATABASES;
import Props.TYPE_PROPERTIES_EMPTY;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.NotePadMeta;
import org.pentaho.di.core.Props;
import org.pentaho.di.core.changed.ChangedFlagInterface;
import org.pentaho.di.core.changed.PDIObserver;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.gui.OverwritePrompter;
import org.pentaho.di.core.gui.Point;
import org.pentaho.di.core.listeners.ContentChangedListener;
import org.pentaho.di.core.listeners.CurrentDirectoryChangedListener;
import org.pentaho.di.core.listeners.FilenameChangedListener;
import org.pentaho.di.core.listeners.NameChangedListener;
import org.pentaho.di.core.logging.ChannelLogTable;
import org.pentaho.di.core.logging.LoggingObjectType;
import org.pentaho.di.core.osgi.api.MetastoreLocatorOsgi;
import org.pentaho.di.core.osgi.api.NamedClusterServiceOsgi;
import org.pentaho.di.core.parameters.NamedParams;
import org.pentaho.di.core.parameters.NamedParamsDefault;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.undo.TransAction;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.ObjectRevision;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.RepositoryObjectType;
import org.pentaho.di.shared.SharedObjects;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.named.cluster.NamedClusterEmbedManager;
import org.pentaho.metastore.api.IMetaStore;
import org.pentaho.metastore.api.IMetaStoreElement;
import org.pentaho.metastore.api.IMetaStoreElementType;


public class AbstractMetaTest {
    AbstractMeta meta;

    ObjectId objectId;

    Repository repo;

    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testGetParent() {
        Assert.assertNull(meta.getParent());
    }

    @Test
    public void testGetSetObjectId() throws Exception {
        Assert.assertNull(meta.getObjectId());
        meta.setObjectId(objectId);
        Assert.assertEquals(objectId, meta.getObjectId());
    }

    @Test
    public void testGetSetContainerObjectId() throws Exception {
        Assert.assertNull(meta.getContainerObjectId());
        meta.setCarteObjectId("myObjectId");
        Assert.assertEquals("myObjectId", meta.getContainerObjectId());
    }

    @Test
    public void testGetSetName() throws Exception {
        Assert.assertNull(meta.getName());
        meta.setName("myName");
        Assert.assertEquals("myName", meta.getName());
    }

    @Test
    public void testGetSetDescription() throws Exception {
        Assert.assertNull(meta.getDescription());
        meta.setDescription("I am a meta");
        Assert.assertEquals("I am a meta", meta.getDescription());
    }

    @Test
    public void testGetSetExtendedDescription() throws Exception {
        Assert.assertNull(meta.getExtendedDescription());
        meta.setExtendedDescription("I am a meta");
        Assert.assertEquals("I am a meta", meta.getExtendedDescription());
    }

    @Test
    public void testNameFromFilename() throws Exception {
        Assert.assertNull(meta.getName());
        Assert.assertNull(meta.getFilename());
        meta.nameFromFilename();
        Assert.assertNull(meta.getName());
        meta.setFilename("/path/to/my/file 2.ktr");
        meta.nameFromFilename();
        Assert.assertEquals("file 2", meta.getName());
    }

    @Test
    public void testGetSetFilename() throws Exception {
        Assert.assertNull(meta.getFilename());
        meta.setFilename("myfile");
        Assert.assertEquals("myfile", meta.getFilename());
    }

    @Test
    public void testGetSetRepositoryDirectory() throws Exception {
        Assert.assertNull(meta.getRepositoryDirectory());
        RepositoryDirectoryInterface dir = Mockito.mock(RepositoryDirectoryInterface.class);
        meta.setRepositoryDirectory(dir);
        Assert.assertEquals(dir, meta.getRepositoryDirectory());
    }

    @Test
    public void testGetSetRepository() throws Exception {
        Assert.assertNull(meta.getRepository());
        meta.setRepository(repo);
        Assert.assertEquals(repo, meta.getRepository());
    }

    @Test
    public void testGetSetDatabase() throws Exception {
        Assert.assertEquals(0, meta.nrDatabases());
        Assert.assertNull(meta.getDatabases());
        Assert.assertFalse(meta.haveConnectionsChanged());
        meta.clear();
        Assert.assertTrue(meta.getDatabases().isEmpty());
        Assert.assertEquals(0, meta.nrDatabases());
        Assert.assertFalse(meta.haveConnectionsChanged());
        DatabaseMeta db1 = Mockito.mock(DatabaseMeta.class);
        Mockito.when(db1.getName()).thenReturn("db1");
        Mockito.when(db1.getDisplayName()).thenReturn("db1");
        meta.addDatabase(db1);
        Assert.assertEquals(1, meta.nrDatabases());
        Assert.assertFalse(meta.getDatabases().isEmpty());
        Assert.assertTrue(meta.haveConnectionsChanged());
        DatabaseMeta db2 = Mockito.mock(DatabaseMeta.class);
        Mockito.when(db2.getName()).thenReturn("db2");
        Mockito.when(db2.getDisplayName()).thenReturn("db2");
        meta.addDatabase(db2);
        Assert.assertEquals(2, meta.nrDatabases());
        // Test replace
        meta.addDatabase(db1, true);
        Assert.assertEquals(2, meta.nrDatabases());
        meta.addOrReplaceDatabase(db1);
        Assert.assertEquals(2, meta.nrDatabases());
        // Test duplicate
        meta.addDatabase(db2, false);
        Assert.assertEquals(2, meta.nrDatabases());
        DatabaseMeta db3 = Mockito.mock(DatabaseMeta.class);
        Mockito.when(db3.getName()).thenReturn("db3");
        meta.addDatabase(db3, false);
        Assert.assertEquals(3, meta.nrDatabases());
        Assert.assertEquals(db1, meta.getDatabase(0));
        Assert.assertEquals(0, meta.indexOfDatabase(db1));
        Assert.assertEquals(db2, meta.getDatabase(1));
        Assert.assertEquals(1, meta.indexOfDatabase(db2));
        Assert.assertEquals(db3, meta.getDatabase(2));
        Assert.assertEquals(2, meta.indexOfDatabase(db3));
        DatabaseMeta db4 = Mockito.mock(DatabaseMeta.class);
        Mockito.when(db4.getName()).thenReturn("db4");
        meta.addDatabase(3, db4);
        Assert.assertEquals(4, meta.nrDatabases());
        Assert.assertEquals(db4, meta.getDatabase(3));
        Assert.assertEquals(3, meta.indexOfDatabase(db4));
        meta.removeDatabase(3);
        Assert.assertEquals(3, meta.nrDatabases());
        Assert.assertTrue(meta.haveConnectionsChanged());
        meta.clearChangedDatabases();
        Assert.assertFalse(meta.haveConnectionsChanged());
        List<DatabaseMeta> list = Arrays.asList(db2, db1);
        meta.setDatabases(list);
        Assert.assertEquals(2, meta.nrDatabases());
        Assert.assertEquals("db1", meta.getDatabaseNames()[0]);
        Assert.assertEquals(0, meta.indexOfDatabase(db1));
        meta.removeDatabase((-1));
        Assert.assertEquals(2, meta.nrDatabases());
        meta.removeDatabase(2);
        Assert.assertEquals(2, meta.nrDatabases());
        Assert.assertEquals(db1, meta.findDatabase("db1"));
        Assert.assertNull(meta.findDatabase(""));
    }

    @Test(expected = KettlePluginException.class)
    public void testGetSetImportMetaStore() throws Exception {
        Assert.assertNull(meta.getMetaStore());
        meta.importFromMetaStore();
        IMetaStore metastore = Mockito.mock(IMetaStore.class);
        meta.setMetaStore(metastore);
        Assert.assertEquals(metastore, meta.getMetaStore());
        meta.importFromMetaStore();
        IMetaStoreElementType elementType = Mockito.mock(IMetaStoreElementType.class);
        Mockito.when(metastore.getElementTypeByName(NAMESPACE, DATABASE_CONNECTION_ELEMENT_TYPE_NAME)).thenReturn(elementType);
        Mockito.when(metastore.getElements(NAMESPACE, elementType)).thenReturn(new ArrayList<IMetaStoreElement>());
        meta.importFromMetaStore();
        IMetaStoreElement element = Mockito.mock(IMetaStoreElement.class);
        Mockito.when(metastore.getElements(NAMESPACE, elementType)).thenReturn(Arrays.asList(element));
        meta.importFromMetaStore();
    }

    @Test
    public void testAddNameChangedListener() throws Exception {
        meta.fireNameChangedListeners("a", "a");
        meta.fireNameChangedListeners("a", "b");
        meta.addNameChangedListener(null);
        meta.fireNameChangedListeners("a", "b");
        NameChangedListener listener = Mockito.mock(NameChangedListener.class);
        meta.addNameChangedListener(listener);
        meta.fireNameChangedListeners("b", "a");
        Mockito.verify(listener, Mockito.times(1)).nameChanged(meta, "b", "a");
        meta.removeNameChangedListener(null);
        meta.removeNameChangedListener(listener);
        meta.fireNameChangedListeners("b", "a");
        Mockito.verifyNoMoreInteractions(listener);
    }

    @Test
    public void testAddFilenameChangedListener() throws Exception {
        meta.fireFilenameChangedListeners("a", "a");
        meta.fireFilenameChangedListeners("a", "b");
        meta.addFilenameChangedListener(null);
        meta.fireFilenameChangedListeners("a", "b");
        FilenameChangedListener listener = Mockito.mock(FilenameChangedListener.class);
        meta.addFilenameChangedListener(listener);
        meta.fireFilenameChangedListeners("b", "a");
        Mockito.verify(listener, Mockito.times(1)).filenameChanged(meta, "b", "a");
        meta.removeFilenameChangedListener(null);
        meta.removeFilenameChangedListener(listener);
        meta.fireFilenameChangedListeners("b", "a");
        Mockito.verifyNoMoreInteractions(listener);
    }

    @Test
    public void testAddRemoveFireContentChangedListener() throws Exception {
        Assert.assertTrue(meta.getContentChangedListeners().isEmpty());
        ContentChangedListener listener = Mockito.mock(ContentChangedListener.class);
        meta.addContentChangedListener(listener);
        Assert.assertFalse(meta.getContentChangedListeners().isEmpty());
        meta.fireContentChangedListeners();
        Mockito.verify(listener, Mockito.times(1)).contentChanged(ArgumentMatchers.anyObject());
        Mockito.verify(listener, Mockito.never()).contentSafe(ArgumentMatchers.anyObject());
        meta.fireContentChangedListeners(true);
        Mockito.verify(listener, Mockito.times(2)).contentChanged(ArgumentMatchers.anyObject());
        Mockito.verify(listener, Mockito.never()).contentSafe(ArgumentMatchers.anyObject());
        meta.fireContentChangedListeners(false);
        Mockito.verify(listener, Mockito.times(2)).contentChanged(ArgumentMatchers.anyObject());
        Mockito.verify(listener, Mockito.times(1)).contentSafe(ArgumentMatchers.anyObject());
        meta.removeContentChangedListener(listener);
        Assert.assertTrue(meta.getContentChangedListeners().isEmpty());
    }

    @Test
    public void testAddCurrentDirectoryChangedListener() throws Exception {
        meta.fireNameChangedListeners("a", "a");
        meta.fireNameChangedListeners("a", "b");
        meta.addCurrentDirectoryChangedListener(null);
        meta.fireCurrentDirectoryChanged("a", "b");
        CurrentDirectoryChangedListener listener = Mockito.mock(CurrentDirectoryChangedListener.class);
        meta.addCurrentDirectoryChangedListener(listener);
        meta.fireCurrentDirectoryChanged("b", "a");
        Mockito.verify(listener, Mockito.times(1)).directoryChanged(meta, "b", "a");
        meta.fireCurrentDirectoryChanged("a", "a");
        meta.removeCurrentDirectoryChangedListener(null);
        meta.removeCurrentDirectoryChangedListener(listener);
        meta.fireNameChangedListeners("b", "a");
        Mockito.verifyNoMoreInteractions(listener);
    }

    @Test
    public void testAddOrReplaceSlaveServer() throws Exception {
        // meta.addOrReplaceSlaveServer() right now will fail with an NPE
        Assert.assertNull(meta.getSlaveServers());
        List<SlaveServer> slaveServers = new ArrayList<>();
        meta.setSlaveServers(slaveServers);
        Assert.assertNotNull(meta.getSlaveServers());
        SlaveServer slaveServer = Mockito.mock(SlaveServer.class);
        meta.addOrReplaceSlaveServer(slaveServer);
        Assert.assertFalse(meta.getSlaveServers().isEmpty());
        meta.addOrReplaceSlaveServer(slaveServer);
        Assert.assertEquals(1, meta.getSlaveServerNames().length);
        Assert.assertNull(meta.findSlaveServer(null));
        Assert.assertNull(meta.findSlaveServer(""));
        Mockito.when(slaveServer.getName()).thenReturn("ss1");
        Assert.assertEquals(slaveServer, meta.findSlaveServer("ss1"));
    }

    @Test
    public void testAddRemoveViewUndo() throws Exception {
        // addUndo() right now will fail with an NPE
        Assert.assertEquals(0, meta.getUndoSize());
        meta.clearUndo();
        Assert.assertEquals(0, meta.getUndoSize());
        Assert.assertEquals(0, meta.getMaxUndo());
        meta.setMaxUndo(3);
        Assert.assertEquals(3, meta.getMaxUndo());
        // viewThisUndo() and viewPreviousUndo() have the same logic
        Assert.assertNull(meta.viewThisUndo());
        Assert.assertNull(meta.viewPreviousUndo());
        Assert.assertNull(meta.viewNextUndo());
        Assert.assertNull(meta.previousUndo());
        Assert.assertNull(meta.nextUndo());
        StepMeta fromMeta = Mockito.mock(StepMeta.class);
        StepMeta toMeta = Mockito.mock(StepMeta.class);
        Object[] from = new Object[]{ fromMeta };
        Object[] to = new Object[]{ toMeta };
        int[] pos = new int[0];
        Point[] prev = new Point[0];
        Point[] curr = new Point[0];
        meta.addUndo(from, to, pos, prev, curr, TYPE_UNDO_NEW, false);
        Assert.assertNotNull(meta.viewThisUndo());
        Assert.assertNotNull(meta.viewPreviousUndo());
        Assert.assertNull(meta.viewNextUndo());
        meta.addUndo(from, to, pos, prev, curr, TYPE_UNDO_CHANGE, false);
        Assert.assertNotNull(meta.viewThisUndo());
        Assert.assertNotNull(meta.viewPreviousUndo());
        Assert.assertNull(meta.viewNextUndo());
        TransAction action = meta.previousUndo();
        Assert.assertNotNull(action);
        Assert.assertEquals(TYPE_UNDO_CHANGE, action.getType());
        Assert.assertNotNull(meta.viewThisUndo());
        Assert.assertNotNull(meta.viewPreviousUndo());
        Assert.assertNotNull(meta.viewNextUndo());
        meta.addUndo(from, to, pos, prev, curr, TYPE_UNDO_DELETE, false);
        meta.addUndo(from, to, pos, prev, curr, TYPE_UNDO_POSITION, false);
        Assert.assertNotNull(meta.previousUndo());
        Assert.assertNotNull(meta.nextUndo());
        meta.setMaxUndo(1);
        Assert.assertEquals(1, meta.getUndoSize());
        meta.addUndo(from, to, pos, prev, curr, TYPE_UNDO_NEW, false);
    }

    @Test
    public void testGetSetAttributes() throws Exception {
        Assert.assertNull(meta.getAttributesMap());
        Map<String, Map<String, String>> attributesMap = new HashMap<>();
        meta.setAttributesMap(attributesMap);
        Assert.assertNull(meta.getAttributes("group1"));
        Map<String, String> group1Attributes = new HashMap<>();
        attributesMap.put("group1", group1Attributes);
        Assert.assertEquals(group1Attributes, meta.getAttributes("group1"));
        Assert.assertNull(meta.getAttribute("group1", "attr1"));
        group1Attributes.put("attr1", "value1");
        Assert.assertEquals("value1", meta.getAttribute("group1", "attr1"));
        Assert.assertNull(meta.getAttribute("group1", "attr2"));
        meta.setAttribute("group1", "attr2", "value2");
        Assert.assertEquals("value2", meta.getAttribute("group1", "attr2"));
        meta.setAttributes("group2", null);
        Assert.assertNull(meta.getAttributes("group2"));
        meta.setAttribute("group2", "attr3", "value3");
        Assert.assertNull(meta.getAttribute("group3", "attr4"));
    }

    @Test
    public void testNotes() throws Exception {
        Assert.assertNull(meta.getNotes());
        // most note methods will NPE at this point, so call clear() to create an empty note list
        meta.clear();
        Assert.assertNotNull(meta.getNotes());
        Assert.assertTrue(meta.getNotes().isEmpty());
        // Can't get a note from an empty list (i.e. no indices)
        Exception e = null;
        try {
            Assert.assertNull(meta.getNote(0));
        } catch (IndexOutOfBoundsException ioobe) {
            e = ioobe;
        }
        Assert.assertNotNull(e);
        Assert.assertNull(meta.getNote(20, 20));
        NotePadMeta note1 = Mockito.mock(NotePadMeta.class);
        meta.removeNote(0);
        Assert.assertFalse(meta.hasChanged());
        meta.addNote(note1);
        Assert.assertTrue(meta.hasChanged());
        NotePadMeta note2 = Mockito.mock(NotePadMeta.class);
        Mockito.when(note2.getLocation()).thenReturn(new Point(0, 0));
        Mockito.when(note2.isSelected()).thenReturn(true);
        meta.addNote(1, note2);
        Assert.assertEquals(note2, meta.getNote(0, 0));
        List<NotePadMeta> selectedNotes = meta.getSelectedNotes();
        Assert.assertNotNull(selectedNotes);
        Assert.assertEquals(1, selectedNotes.size());
        Assert.assertEquals(note2, selectedNotes.get(0));
        Assert.assertEquals(1, meta.indexOfNote(note2));
        meta.removeNote(2);
        Assert.assertEquals(2, meta.nrNotes());
        meta.removeNote(1);
        Assert.assertEquals(1, meta.nrNotes());
        Assert.assertTrue(meta.haveNotesChanged());
        meta.clearChanged();
        Assert.assertFalse(meta.haveNotesChanged());
        meta.addNote(1, note2);
        meta.lowerNote(1);
        Assert.assertTrue(meta.haveNotesChanged());
        meta.clearChanged();
        Assert.assertFalse(meta.haveNotesChanged());
        meta.raiseNote(0);
        Assert.assertTrue(meta.haveNotesChanged());
        meta.clearChanged();
        Assert.assertFalse(meta.haveNotesChanged());
        int[] indexes = meta.getNoteIndexes(Arrays.asList(note1, note2));
        Assert.assertNotNull(indexes);
        Assert.assertEquals(2, indexes.length);
    }

    @Test
    public void testCopyVariablesFrom() throws Exception {
        Assert.assertNull(meta.getVariable("var1"));
        VariableSpace vars = Mockito.mock(VariableSpace.class);
        Mockito.when(vars.getVariable("var1")).thenReturn("x");
        Mockito.when(vars.listVariables()).thenReturn(new String[]{ "var1" });
        meta.copyVariablesFrom(vars);
        Assert.assertEquals("x", meta.getVariable("var1", "y"));
    }

    @Test
    public void testEnvironmentSubstitute() throws Exception {
        // This is just a delegate method, verify it's called
        VariableSpace vars = Mockito.mock(VariableSpace.class);
        // This method is reused by the stub to set the mock as the variables object
        meta.setInternalKettleVariables(vars);
        meta.environmentSubstitute("${param}");
        Mockito.verify(vars, Mockito.times(1)).environmentSubstitute("${param}");
        String[] params = new String[]{ "${param}" };
        meta.environmentSubstitute(params);
        Mockito.verify(vars, Mockito.times(1)).environmentSubstitute(params);
    }

    @Test
    public void testFieldSubstitute() throws Exception {
        // This is just a delegate method, verify it's called
        VariableSpace vars = Mockito.mock(VariableSpace.class);
        // This method is reused by the stub to set the mock as the variables object
        meta.setInternalKettleVariables(vars);
        RowMetaInterface rowMeta = Mockito.mock(RowMetaInterface.class);
        Object[] data = new Object[0];
        meta.fieldSubstitute("?{param}", rowMeta, data);
        Mockito.verify(vars, Mockito.times(1)).fieldSubstitute("?{param}", rowMeta, data);
    }

    @Test
    public void testGetSetParentVariableSpace() throws Exception {
        Assert.assertNull(meta.getParentVariableSpace());
        VariableSpace variableSpace = Mockito.mock(VariableSpace.class);
        meta.setParentVariableSpace(variableSpace);
        Assert.assertEquals(variableSpace, meta.getParentVariableSpace());
    }

    @Test
    public void testGetSetVariable() throws Exception {
        Assert.assertNull(meta.getVariable("var1"));
        Assert.assertEquals("x", meta.getVariable("var1", "x"));
        meta.setVariable("var1", "y");
        Assert.assertEquals("y", meta.getVariable("var1", "x"));
    }

    @Test
    public void testGetSetParameterValue() throws Exception {
        Assert.assertNull(meta.getParameterValue("var1"));
        Assert.assertNull(meta.getParameterDefault("var1"));
        Assert.assertNull(meta.getParameterDescription("var1"));
        meta.setParameterValue("var1", "y");
        // Values for new parameters must be added by addParameterDefinition
        Assert.assertNull(meta.getParameterValue("var1"));
        Assert.assertNull(meta.getParameterDefault("var1"));
        Assert.assertNull(meta.getParameterDescription("var1"));
        meta.addParameterDefinition("var2", "z", "My Description");
        Assert.assertEquals("", meta.getParameterValue("var2"));
        Assert.assertEquals("z", meta.getParameterDefault("var2"));
        Assert.assertEquals("My Description", meta.getParameterDescription("var2"));
        meta.setParameterValue("var2", "y");
        Assert.assertEquals("y", meta.getParameterValue("var2"));
        Assert.assertEquals("z", meta.getParameterDefault("var2"));
        String[] params = meta.listParameters();
        Assert.assertNotNull(params);
        // clearParameters() just clears their values, not their presence
        meta.clearParameters();
        Assert.assertEquals("", meta.getParameterValue("var2"));
        // eraseParameters() clears the list of parameters
        meta.eraseParameters();
        Assert.assertNull(meta.getParameterValue("var1"));
        NamedParams newParams = new NamedParamsDefault();
        newParams.addParameterDefinition("var3", "default", "description");
        newParams.setParameterValue("var3", "a");
        meta.copyParametersFrom(newParams);
        meta.activateParameters();
        Assert.assertEquals("default", meta.getParameterDefault("var3"));
    }

    @Test
    public void testGetSetLogLevel() throws Exception {
        Assert.assertEquals(BASIC, meta.getLogLevel());
        meta.setLogLevel(DEBUG);
        Assert.assertEquals(DEBUG, meta.getLogLevel());
    }

    @Test
    public void testGetSetSharedObjectsFile() throws Exception {
        Assert.assertNull(meta.getSharedObjectsFile());
        meta.setSharedObjectsFile("mySharedObjects");
        Assert.assertEquals("mySharedObjects", meta.getSharedObjectsFile());
    }

    @Test
    public void testGetSetSharedObjects() throws Exception {
        SharedObjects sharedObjects = Mockito.mock(SharedObjects.class);
        meta.setSharedObjects(sharedObjects);
        Assert.assertEquals(sharedObjects, meta.getSharedObjects());
        meta.setSharedObjects(null);
        AbstractMeta spyMeta = Mockito.spy(meta);
        Mockito.doThrow(KettleException.class).when(spyMeta).environmentSubstitute(ArgumentMatchers.anyString());
        Assert.assertNull(spyMeta.getSharedObjects());
    }

    @Test
    public void testGetSetCreatedDate() throws Exception {
        Assert.assertNull(meta.getCreatedDate());
        Date now = Calendar.getInstance().getTime();
        meta.setCreatedDate(now);
        Assert.assertEquals(now, meta.getCreatedDate());
    }

    @Test
    public void testGetSetCreatedUser() throws Exception {
        Assert.assertNull(meta.getCreatedUser());
        meta.setCreatedUser("joe");
        Assert.assertEquals("joe", meta.getCreatedUser());
    }

    @Test
    public void testGetSetModifiedDate() throws Exception {
        Assert.assertNull(meta.getModifiedDate());
        Date now = Calendar.getInstance().getTime();
        meta.setModifiedDate(now);
        Assert.assertEquals(now, meta.getModifiedDate());
    }

    @Test
    public void testGetSetModifiedUser() throws Exception {
        Assert.assertNull(meta.getModifiedUser());
        meta.setModifiedUser("joe");
        Assert.assertEquals("joe", meta.getModifiedUser());
    }

    @Test
    public void testAddDeleteModifyObserver() throws Exception {
        PDIObserver observer = Mockito.mock(PDIObserver.class);
        meta.addObserver(observer);
        Object event = new Object();
        meta.notifyObservers(event);
        // Changed flag isn't set, so this won't be called
        Mockito.verify(observer, Mockito.never()).update(meta, event);
        meta.setChanged(true);
        meta.notifyObservers(event);
        Mockito.verify(observer, Mockito.times(1)).update(ArgumentMatchers.any(ChangedFlagInterface.class), ArgumentMatchers.anyObject());
    }

    @Test
    public void testGetRegistrationDate() throws Exception {
        Assert.assertNull(meta.getRegistrationDate());
    }

    @Test
    public void testGetObjectNameCopyRevision() throws Exception {
        Assert.assertNull(meta.getObjectName());
        meta.setName("x");
        Assert.assertEquals("x", meta.getObjectName());
        Assert.assertNull(meta.getObjectCopy());
        Assert.assertNull(meta.getObjectRevision());
        ObjectRevision rev = Mockito.mock(ObjectRevision.class);
        meta.setObjectRevision(rev);
        Assert.assertEquals(rev, meta.getObjectRevision());
    }

    @Test
    public void testHasMissingPlugins() throws Exception {
        Assert.assertFalse(meta.hasMissingPlugins());
    }

    @Test
    public void testGetSetPrivateDatabases() throws Exception {
        Assert.assertNull(meta.getPrivateDatabases());
        Set<String> dbs = new HashSet<>();
        meta.setPrivateDatabases(dbs);
        Assert.assertEquals(dbs, meta.getPrivateDatabases());
    }

    @Test
    public void testGetSetChannelLogTable() throws Exception {
        Assert.assertNull(meta.getChannelLogTable());
        ChannelLogTable table = Mockito.mock(ChannelLogTable.class);
        meta.setChannelLogTable(table);
        Assert.assertEquals(table, meta.getChannelLogTable());
    }

    @Test
    public void testGetEmbeddedMetaStore() {
        Assert.assertNotNull(meta.getEmbeddedMetaStore());
    }

    @Test
    public void testGetBooleanValueOfVariable() {
        Assert.assertFalse(meta.getBooleanValueOfVariable(null, false));
        Assert.assertTrue(meta.getBooleanValueOfVariable("", true));
        Assert.assertTrue(meta.getBooleanValueOfVariable("true", true));
        Assert.assertFalse(meta.getBooleanValueOfVariable("${myVar}", false));
        meta.setVariable("myVar", "Y");
        Assert.assertTrue(meta.getBooleanValueOfVariable("${myVar}", false));
    }

    @Test
    public void testInitializeShareInjectVariables() {
        meta.initializeVariablesFrom(null);
        VariableSpace parent = Mockito.mock(VariableSpace.class);
        Mockito.when(parent.getVariable("var1")).thenReturn("x");
        Mockito.when(parent.listVariables()).thenReturn(new String[]{ "var1" });
        meta.initializeVariablesFrom(parent);
        Assert.assertEquals("x", meta.getVariable("var1"));
        Assert.assertNotNull(meta.listVariables());
        VariableSpace newVars = Mockito.mock(VariableSpace.class);
        Mockito.when(newVars.getVariable("var2")).thenReturn("y");
        Mockito.when(newVars.listVariables()).thenReturn(new String[]{ "var2" });
        meta.shareVariablesWith(newVars);
        Assert.assertEquals("y", meta.getVariable("var2"));
        Map<String, String> props = new HashMap<>();
        props.put("var3", "a");
        props.put("var4", "b");
        meta.shareVariablesWith(new Variables());
        meta.injectVariables(props);
        // Need to "Activate" the injection, we can initialize from null
        meta.initializeVariablesFrom(null);
        Assert.assertEquals("a", meta.getVariable("var3"));
        Assert.assertEquals("b", meta.getVariable("var4"));
    }

    @Test
    public void testCanSave() {
        Assert.assertTrue(meta.canSave());
    }

    @Test
    public void testHasChanged() {
        meta.clear();
        Assert.assertFalse(meta.hasChanged());
        meta.setChanged(true);
        Assert.assertTrue(meta.hasChanged());
    }

    @Test
    public void testShouldOverwrite() {
        Assert.assertTrue(meta.shouldOverwrite(null, null, null, null));
        Props.init(TYPE_PROPERTIES_EMPTY);
        Assert.assertTrue(meta.shouldOverwrite(null, Props.getInstance(), "message", "remember"));
        Props.getInstance().setProperty(STRING_ASK_ABOUT_REPLACING_DATABASES, "Y");
        OverwritePrompter prompter = Mockito.mock(OverwritePrompter.class);
        Mockito.when(prompter.overwritePrompt("message", "remember", STRING_ASK_ABOUT_REPLACING_DATABASES)).thenReturn(false);
        Assert.assertFalse(meta.shouldOverwrite(prompter, Props.getInstance(), "message", "remember"));
    }

    @Test
    public void testGetSetNamedClusterServiceOsgi() throws Exception {
        Assert.assertNull(meta.getNamedClusterServiceOsgi());
        NamedClusterServiceOsgi mockNamedClusterOsgi = Mockito.mock(NamedClusterServiceOsgi.class);
        meta.setNamedClusterServiceOsgi(mockNamedClusterOsgi);
        Assert.assertEquals(mockNamedClusterOsgi, meta.getNamedClusterServiceOsgi());
    }

    @Test
    public void testGetNamedClusterEmbedManager() throws Exception {
        Assert.assertNull(meta.getNamedClusterEmbedManager());
        NamedClusterEmbedManager mockNamedClusterEmbedManager = Mockito.mock(NamedClusterEmbedManager.class);
        meta.namedClusterEmbedManager = mockNamedClusterEmbedManager;
        Assert.assertEquals(mockNamedClusterEmbedManager, meta.getNamedClusterEmbedManager());
    }

    @Test
    public void testGetSetEmbeddedMetastoreProviderKey() throws Exception {
        Assert.assertNull(meta.getEmbeddedMetastoreProviderKey());
        String keyValue = "keyValue";
        meta.setEmbeddedMetastoreProviderKey(keyValue);
        Assert.assertEquals(keyValue, meta.getEmbeddedMetastoreProviderKey());
    }

    @Test
    public void testGetSetMetastoreLocatorOsgi() throws Exception {
        Assert.assertNull(meta.getMetastoreLocatorOsgi());
        MetastoreLocatorOsgi mockMetastoreLocatorOsgi = Mockito.mock(MetastoreLocatorOsgi.class);
        meta.setMetastoreLocatorOsgi(mockMetastoreLocatorOsgi);
        Assert.assertEquals(mockMetastoreLocatorOsgi, meta.getMetastoreLocatorOsgi());
    }

    @Test
    public void testMultithreadHammeringOfListener() throws Exception {
        CountDownLatch latch = new CountDownLatch(3);
        AbstractMetaTest.AbstractMetaListenerThread th1 = new AbstractMetaTest.AbstractMetaListenerThread(meta, 2000, latch);// do 2k random add/delete/fire

        AbstractMetaTest.AbstractMetaListenerThread th2 = new AbstractMetaTest.AbstractMetaListenerThread(meta, 2000, latch);// do 2k random add/delete/fire

        AbstractMetaTest.AbstractMetaListenerThread th3 = new AbstractMetaTest.AbstractMetaListenerThread(meta, 2000, latch);// do 2k random add/delete/fire

        Thread t1 = new Thread(th1);
        Thread t2 = new Thread(th2);
        Thread t3 = new Thread(th3);
        try {
            t1.start();
            t2.start();
            t3.start();
            latch.await();// Will hang out waiting for each thread to complete...

        } catch (InterruptedException badTest) {
            throw badTest;
        }
        Assert.assertEquals("No exceptions encountered", th1.message);
        Assert.assertEquals("No exceptions encountered", th2.message);
        Assert.assertEquals("No exceptions encountered", th3.message);
    }

    /**
     * Stub class for AbstractMeta. No need to test the abstract methods here, they should be done in unit tests for
     * proper child classes.
     */
    public static class AbstractMetaStub extends AbstractMeta {
        // Reuse this method to set a mock internal variable space
        @Override
        public void setInternalKettleVariables(VariableSpace var) {
            this.variables = var;
        }

        @Override
        protected void setInternalFilenameKettleVariables(VariableSpace var) {
        }

        @Override
        protected void setInternalNameKettleVariable(VariableSpace var) {
        }

        @Override
        public String getXML() throws KettleException {
            return null;
        }

        @Override
        public String getFileType() {
            return null;
        }

        @Override
        public String[] getFilterNames() {
            return new String[0];
        }

        @Override
        public String[] getFilterExtensions() {
            return new String[0];
        }

        @Override
        public String getDefaultExtension() {
            return null;
        }

        @Override
        public void saveSharedObjects() throws KettleException {
        }

        @Override
        public String getLogChannelId() {
            return null;
        }

        @Override
        public LoggingObjectType getObjectType() {
            return null;
        }

        @Override
        public boolean isGatheringMetrics() {
            return false;
        }

        @Override
        public void setGatheringMetrics(boolean b) {
        }

        @Override
        public void setForcingSeparateLogging(boolean b) {
        }

        @Override
        public boolean isForcingSeparateLogging() {
            return false;
        }

        @Override
        public RepositoryObjectType getRepositoryElementType() {
            return null;
        }
    }

    private class AbstractMetaListenerThread implements Runnable {
        AbstractMeta metaToWork;

        int times;

        CountDownLatch whenDone;

        String message;

        AbstractMetaListenerThread(AbstractMeta aMeta, int times, CountDownLatch latch) {
            this.metaToWork = aMeta;
            this.times = times;
            this.whenDone = latch;
        }

        @Override
        public void run() {
            for (int i = 0; i < (times); i++) {
                int randomNum = ThreadLocalRandom.current().nextInt(0, 3);
                switch (randomNum) {
                    case 0 :
                        {
                            try {
                                metaToWork.addFilenameChangedListener(Mockito.mock(FilenameChangedListener.class));
                            } catch (Throwable ex) {
                                message = "Exception adding listener.";
                            }
                            break;
                        }
                    case 1 :
                        {
                            try {
                                metaToWork.removeFilenameChangedListener(Mockito.mock(FilenameChangedListener.class));
                            } catch (Throwable ex) {
                                message = "Exception removing listener.";
                            }
                            break;
                        }
                    default :
                        {
                            try {
                                metaToWork.fireFilenameChangedListeners("oldName", "newName");
                            } catch (Throwable ex) {
                                message = "Exception firing listeners.";
                            }
                            break;
                        }
                }
            }
            if ((message) == null) {
                message = "No exceptions encountered";
            }
            whenDone.countDown();// show success...

        }
    }
}

