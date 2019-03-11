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


import com.google.common.collect.Lists;
import com.google.security.zynamics.binnavi.CUtilityFunctions;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntLoadDataException;
import com.google.security.zynamics.binnavi.Database.Exceptions.LoadCancelledException;
import com.google.security.zynamics.binnavi.Database.MockClasses.MockDatabase;
import com.google.security.zynamics.binnavi.Database.MockClasses.MockSqlProvider;
import com.google.security.zynamics.binnavi.Tagging.MockTagManager;
import com.google.security.zynamics.binnavi.Tagging.TagType;
import com.google.security.zynamics.binnavi.disassembly.CFunction;
import com.google.security.zynamics.binnavi.disassembly.CFunctionContainerHelper;
import com.google.security.zynamics.binnavi.disassembly.INaviFunction;
import com.google.security.zynamics.binnavi.disassembly.MockView;
import com.google.security.zynamics.binnavi.disassembly.Modules.CModule;
import com.google.security.zynamics.binnavi.disassembly.Modules.MockModule;
import com.google.security.zynamics.zylib.disassembly.CAddress;
import com.google.security.zynamics.zylib.disassembly.FunctionType;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class ModuleHelpersTest {
    @Test
    public void testGetFunction_1() {
        final Database database = new Database(new MockDatabase());
        @SuppressWarnings("unused")
        final MockModule mockModule = new MockModule();
        final MockSqlProvider provider = new MockSqlProvider();
        final CModule internalModule = new CModule(1, "", "", new Date(), new Date(), "00000000000000000000000000000000", "0000000000000000000000000000000000000000", 0, 0, new CAddress(0), new CAddress(0), null, null, Integer.MAX_VALUE, false, provider);
        try {
            internalModule.load();
        } catch (final CouldntLoadDataException exception) {
            CUtilityFunctions.logException(exception);
        } catch (final LoadCancelledException exception) {
            CUtilityFunctions.logException(exception);
        }
        @SuppressWarnings("unused")
        final CFunction parentFunction = new CFunction(internalModule, new MockView(), new CAddress(291), "Mock Function", "Mock Function", "Mock Description", 0, 0, 0, 0, FunctionType.NORMAL, "", 0, null, null, null, provider);
        final TagManager nodeTagManager = new TagManager(new MockTagManager(TagType.NODE_TAG));
        final TagManager viewTagManager = new TagManager(new MockTagManager(TagType.VIEW_TAG));
        final Module module = new Module(database, internalModule, nodeTagManager, viewTagManager);
        Assert.assertEquals(module.getFunctions().get(0), ModuleHelpers.getFunction(module, 291));
        Assert.assertNull(ModuleHelpers.getFunction(module, 4661));
        try {
            ModuleHelpers.getFunction(null, (-1));
            Assert.fail();
        } catch (final NullPointerException e) {
        }
        try {
            ModuleHelpers.getFunction(module, (-1));
            Assert.fail();
        } catch (final IllegalArgumentException e) {
        }
    }

    @Test
    public void testGetFunction_2() throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        final Database database = new Database(new MockDatabase());
        final MockModule mockModule = new MockModule();
        final MockSqlProvider provider = new MockSqlProvider();
        final CModule internalModule = new CModule(1, "", "", new Date(), new Date(), "00000000000000000000000000000000", "0000000000000000000000000000000000000000", 0, 0, new CAddress(0), new CAddress(0), null, null, Integer.MAX_VALUE, false, provider);
        final CFunction parentFunction = new CFunction(internalModule, new MockView(), new CAddress(291), "Mock Function", "Mock Function", "Mock Description", 0, 0, 0, 0, FunctionType.NORMAL, "", 0, null, null, null, provider);
        CFunctionContainerHelper.addFunction(mockModule.getContent().getFunctionContainer(), parentFunction);
        final TagManager nodeTagManager = new TagManager(new MockTagManager(TagType.NODE_TAG));
        final TagManager viewTagManager = new TagManager(new MockTagManager(TagType.VIEW_TAG));
        final Module module = new Module(database, mockModule, nodeTagManager, viewTagManager);
        Assert.assertEquals(module.getFunctions().get(0), ModuleHelpers.getFunction(module, new Address(291)));
        Assert.assertNull(ModuleHelpers.getFunction(module, new Address(4661)));
        try {
            ModuleHelpers.getFunction(null, ((Address) (null)));
            Assert.fail();
        } catch (final NullPointerException e) {
        }
        try {
            ModuleHelpers.getFunction(module, ((Address) (null)));
            Assert.fail();
        } catch (final NullPointerException e) {
        }
    }

    @Test
    public void testGetFunction_3() throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        final Database database = new Database(new MockDatabase());
        final MockModule mockModule = new MockModule();
        final MockSqlProvider provider = new MockSqlProvider();
        final CModule internalModule = new CModule(1, "", "", new Date(), new Date(), "00000000000000000000000000000000", "0000000000000000000000000000000000000000", 0, 0, new CAddress(0), new CAddress(0), null, null, Integer.MAX_VALUE, false, provider);
        final CFunction parentFunction = new CFunction(internalModule, new MockView(), new CAddress(291), "Mock Function", "Mock Function", "Mock Description", 0, 0, 0, 0, FunctionType.NORMAL, "", 0, null, null, null, provider);
        new com.google.security.zynamics.binnavi.disassembly.Modules.CFunctionContainer(mockModule, Lists.<INaviFunction>newArrayList(parentFunction));
        CFunctionContainerHelper.addFunction(mockModule.getContent().getFunctionContainer(), parentFunction);
        final TagManager nodeTagManager = new TagManager(new MockTagManager(TagType.NODE_TAG));
        final TagManager viewTagManager = new TagManager(new MockTagManager(TagType.VIEW_TAG));
        final Module module = new Module(database, mockModule, nodeTagManager, viewTagManager);
        Assert.assertEquals(module.getFunctions().get(0), ModuleHelpers.getFunction(module, "Mock Function"));
        Assert.assertNull(ModuleHelpers.getFunction(module, ""));
        try {
            ModuleHelpers.getFunction(null, ((String) (null)));
            Assert.fail();
        } catch (final NullPointerException e) {
        }
        try {
            ModuleHelpers.getFunction(module, ((String) (null)));
            Assert.fail();
        } catch (final NullPointerException e) {
        }
    }
}

