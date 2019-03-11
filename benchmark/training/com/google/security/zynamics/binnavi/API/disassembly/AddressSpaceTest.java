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
package com.google.security.zynamics.binnavi.API.disassembly;


import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntDeleteException;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntLoadDataException;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntSaveDataException;
import com.google.security.zynamics.binnavi.Database.Exceptions.LoadCancelledException;
import com.google.security.zynamics.binnavi.Database.MockClasses.MockDatabase;
import com.google.security.zynamics.binnavi.Database.MockClasses.MockSqlProvider;
import com.google.security.zynamics.binnavi.Tagging.MockTagManager;
import com.google.security.zynamics.binnavi.Tagging.TagType;
import com.google.security.zynamics.binnavi.debug.debugger.DebuggerTemplate;
import com.google.security.zynamics.binnavi.disassembly.AddressSpaces.CAddressSpace;
import com.google.security.zynamics.binnavi.disassembly.MockProject;
import com.google.security.zynamics.binnavi.disassembly.Modules.CModule;
import com.google.security.zynamics.zylib.disassembly.CAddress;
import java.util.Date;
import java.util.LinkedHashMap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class AddressSpaceTest {
    private AddressSpace addressSpace;

    private CAddressSpace internalAddressSpace;

    private Database database;

    @SuppressWarnings("unused")
    private final TagManager nodeManager = new TagManager(new MockTagManager(TagType.NODE_TAG));

    @SuppressWarnings("unused")
    private final TagManager viewManager = new TagManager(new MockTagManager(TagType.VIEW_TAG));

    private Date creationDate;

    private Date modificationDate;

    private Module module;

    @Test
    public void testConstructor() {
        Assert.assertEquals("Mock Space", addressSpace.getName());
        Assert.assertEquals("Mock Space Description", addressSpace.getDescription());
        Assert.assertEquals(creationDate, addressSpace.getCreationDate());
        Assert.assertEquals(modificationDate, addressSpace.getModificationDate());
        Assert.assertEquals("Address space Mock Space [unloaded, 0 modules]", addressSpace.toString());
    }

    @Test
    public void testConstructorAlternative() throws CouldntLoadDataException, CouldntSaveDataException, LoadCancelledException {
        final MockSqlProvider provider = new MockSqlProvider();
        final MockDatabase mockDb = new MockDatabase(provider);
        final Database database = new Database(mockDb);
        final DebuggerTemplate template = new DebuggerTemplate(1, "", "", 0, provider);
        mockDb.getContent().getDebuggerTemplateManager().addDebugger(template);
        final CModule internalModule = new CModule(123, "Name", "Comment", new Date(), new Date(), "12345678123456781234567812345678", "1234567812345678123456781234567812345678", 55, 66, new CAddress(1365), new CAddress(1638), new DebuggerTemplate(1, "Mock Debugger", "localhaus", 88, provider), null, Integer.MAX_VALUE, false, provider);
        mockDb.getContent().addModule(internalModule);
        final CAddressSpace internalAddressSpace = new CAddressSpace(1, "Mock Space", "Mock Space Description", new Date(), new Date(), new LinkedHashMap<com.google.security.zynamics.binnavi.disassembly.INaviModule, com.google.security.zynamics.zylib.disassembly.IAddress>(), null, provider, new MockProject());
        internalAddressSpace.load();
        internalAddressSpace.getConfiguration().setDebuggerTemplate(template);
        internalAddressSpace.getContent().addModule(internalModule);
        final Project project = ProjectFactory.get();
        final AddressSpace addressSpace = new AddressSpace(database, project, internalAddressSpace);
        Assert.assertEquals(1, addressSpace.getModules().size());
        Assert.assertNotNull(addressSpace.getDebuggerTemplate());
        Assert.assertNotNull(addressSpace.getDebugger());
    }

    @Test
    public void testGetImageBase() throws CouldntLoadDataException, CouldntSaveDataException {
        addressSpace.load();
        final MockAddressSpaceListener listener = new MockAddressSpaceListener();
        addressSpace.addModule(module);
        addressSpace.addListener(listener);
        addressSpace.setImageBase(module, new Address(4660));
        Assert.assertEquals(4660, addressSpace.getImagebase(module).toLong());
        Assert.assertEquals("changedImageBase;changedModificationDate;", listener.events);
        addressSpace.removeListener(listener);
    }

    @Test
    public void testLoad() throws CouldntLoadDataException, CouldntSaveDataException {
        final MockAddressSpaceListener listener = new MockAddressSpaceListener();
        addressSpace.addListener(listener);
        addressSpace.load();
        addressSpace.addModule(module);
        Assert.assertEquals("loaded;addedModule;changedImageBase;changedModificationDate;", listener.events);
        Assert.assertEquals(1, addressSpace.getModules().size());
        Assert.assertNotNull(addressSpace.toString());
        addressSpace.close();
        Assert.assertEquals("loaded;addedModule;changedImageBase;changedModificationDate;closing;closed;", listener.events);
    }

    @Test
    public void testModules() throws CouldntLoadDataException, CouldntSaveDataException, CouldntDeleteException, CouldntSaveDataException {
        addressSpace.load();
        final MockAddressSpaceListener listener = new MockAddressSpaceListener();
        addressSpace.addListener(listener);
        addressSpace.addModule(module);
        Assert.assertEquals("addedModule;changedImageBase;changedModificationDate;", listener.events);
        Assert.assertEquals(1, addressSpace.getModules().size());
        Assert.assertTrue(database.getModules().contains(addressSpace.getModules().get(0)));
        addressSpace.removeModule(module);
        Assert.assertEquals(0, addressSpace.getModules().size());
        Assert.assertEquals("addedModule;changedImageBase;changedModificationDate;removedModule;changedModificationDate;", listener.events);
        internalAddressSpace.getContent().addModule(module.getNative());
        Assert.assertEquals(1, addressSpace.getModules().size());
        Assert.assertEquals("addedModule;changedImageBase;changedModificationDate;removedModule;changedModificationDate;addedModule;", listener.events);
        internalAddressSpace.getContent().removeModule(module.getNative());
        Assert.assertEquals(0, addressSpace.getModules().size());
        Assert.assertEquals("addedModule;changedImageBase;changedModificationDate;removedModule;changedModificationDate;addedModule;removedModule;changedModificationDate;", listener.events);
        addressSpace.removeListener(listener);
    }

    @Test
    public void testSetDebuggerTemplate() throws CouldntLoadDataException, CouldntSaveDataException {
        addressSpace.load();
        final MockAddressSpaceListener listener = new MockAddressSpaceListener();
        addressSpace.addListener(listener);
        final DebuggerTemplate template = database.getDebuggerTemplateManager().createDebuggerTemplate("foo", "", 0);
        addressSpace.setDebuggerTemplate(template);
        Assert.assertEquals("changedDebugger;changedModificationDate;", listener.events);
        Assert.assertNotNull(addressSpace.getDebugger());
        Assert.assertEquals(template, addressSpace.getDebuggerTemplate());
        addressSpace.setDebuggerTemplate(null);
        Assert.assertEquals("changedDebugger;changedModificationDate;changedDebugger;changedModificationDate;", listener.events);
        Assert.assertNull(addressSpace.getDebugger());
        Assert.assertNull(addressSpace.getDebuggerTemplate());
        addressSpace.removeListener(listener);
    }

    @Test
    public void testSetDescription() throws CouldntSaveDataException {
        final MockAddressSpaceListener listener = new MockAddressSpaceListener();
        addressSpace.addListener(listener);
        addressSpace.setDescription("Foo");
        Assert.assertEquals("changedDescription;changedModificationDate;", listener.events);
        addressSpace.removeListener(listener);
    }

    @Test
    public void testSetName() throws CouldntSaveDataException {
        final MockAddressSpaceListener listener = new MockAddressSpaceListener();
        addressSpace.addListener(listener);
        addressSpace.setName("Foo");
        Assert.assertEquals("changedName;changedModificationDate;", listener.events);
        addressSpace.removeListener(listener);
    }
}

