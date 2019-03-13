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
import com.google.security.zynamics.binnavi.API.disassembly.FunctionType.Normal;
import com.google.security.zynamics.binnavi.API.reil.InternalTranslationException;
import com.google.security.zynamics.binnavi.Common.CommonTestObjects;
import com.google.security.zynamics.binnavi.Database.Interfaces.SQLProvider;
import com.google.security.zynamics.binnavi.Database.MockClasses.MockSqlProvider;
import com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.Interfaces.IComment;
import com.google.security.zynamics.binnavi.Gui.Users.CUserManager;
import com.google.security.zynamics.binnavi.Gui.Users.Interfaces.IUser;
import com.google.security.zynamics.binnavi.disassembly.CBlockNode;
import com.google.security.zynamics.binnavi.disassembly.CFunction;
import com.google.security.zynamics.binnavi.disassembly.INaviInstruction;
import com.google.security.zynamics.binnavi.disassembly.MockInstruction;
import com.google.security.zynamics.binnavi.disassembly.MockView;
import com.google.security.zynamics.binnavi.disassembly.Modules.CModule;
import com.google.security.zynamics.zylib.disassembly.CAddress;
import com.google.security.zynamics.zylib.disassembly.FunctionType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class FunctionTest {
    private CFunction m_internalFunction;

    private CFunction m_internalFunction2;

    private SQLProvider m_provider;

    @Test
    public void testAppendFunctionComment() throws CouldntLoadDataException, CouldntSaveDataException, com.google.security.zynamics.binnavi.Database.Exceptions.CouldntSaveDataException {
        final MockFunctionListener listener = new MockFunctionListener();
        final Function function = new Function(ModuleFactory.get(), m_internalFunction2);
        function.addListener(listener);
        final CUserManager userManager = CUserManager.get(m_provider);
        final IUser user = userManager.addUser(" SET FUNCTION COMMENT TEST ");
        userManager.setCurrentActiveUser(user);
        final List<IComment> appendedComments = function.appendComment("Hannes");
        Assert.assertEquals(appendedComments, function.getComment());
        Assert.assertEquals(appendedComments, m_internalFunction2.getGlobalComment());
        Assert.assertEquals("appendedComment;", listener.events);
        final ArrayList<IComment> comments = Lists.newArrayList();
        comments.add(new com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.CComment(12345, CommonTestObjects.TEST_USER_1, null, "FOO"));
        m_internalFunction2.initializeGlobalComment(comments);
        Assert.assertEquals(comments.get(0), function.getComment().get(0));
        Assert.assertEquals(comments.get(0), m_internalFunction2.getGlobalComment().get(0));
        Assert.assertEquals("appendedComment;initializedComments;", listener.events);
        function.removeListener(listener);
    }

    @Test
    public void testConstructors() {
        final Module m = ModuleFactory.get();
        final Function function = new Function(m, m_internalFunction);
        Assert.assertEquals(m, function.getModule());
        Assert.assertEquals(291, function.getAddress().toLong());
        Assert.assertEquals("Mock Function", function.getName());
        Assert.assertEquals("Mock Description", function.getDescription());
        Assert.assertEquals(null, function.getComment());
        Assert.assertEquals(Normal, function.getType());
        Assert.assertEquals("123 Mock Function", function.toString());
    }

    @Test
    public void testLoad() throws CouldntLoadDataException {
        final MockFunctionListener listener = new MockFunctionListener();
        final MockSqlProvider provider = new MockSqlProvider();
        final CModule internalModule = new CModule(1, "", "", new Date(), new Date(), "00000000000000000000000000000000", "0000000000000000000000000000000000000000", 0, 0, new CAddress(0), new CAddress(0), null, null, Integer.MAX_VALUE, false, provider);
        @SuppressWarnings("unused")
        final CFunction parentFunction = new CFunction(internalModule, new MockView(), new CAddress(291), "Mock Function", "Mock Function", "Mock Description", 0, 0, 0, 0, FunctionType.NORMAL, "", 0, null, null, null, provider);
        final List<INaviInstruction> instructions1 = new ArrayList<INaviInstruction>();
        instructions1.add(new MockInstruction(1234));
        final List<INaviInstruction> instructions2 = new ArrayList<INaviInstruction>();
        instructions2.add(new MockInstruction(1235));
        new CBlockNode(new com.google.security.zynamics.binnavi.disassembly.CBasicBlock(1, "", instructions1));
        new CBlockNode(new com.google.security.zynamics.binnavi.disassembly.CBasicBlock(1, "", instructions2));
        final Function function = new Function(ModuleFactory.get(), m_internalFunction);
        function.addListener(listener);
        function.load();
        Assert.assertEquals("loadedFunction;", listener.events);
        Assert.assertEquals(4, function.getEdgeCount());
        Assert.assertEquals(5, function.getBlockCount());
        Assert.assertEquals(1, function.getGraph().getNodes().get(0).getChildren().size());
        function.close();
        function.removeListener(listener);
        Assert.assertEquals("loadedFunction;closedFunction;", listener.events);
        Assert.assertFalse(function.isLoaded());
    }

    @Test
    public void testReil() throws CouldntLoadDataException, InternalTranslationException {
        final MockSqlProvider provider = new MockSqlProvider();
        final CModule internalModule = new CModule(1, "", "", new Date(), new Date(), "00000000000000000000000000000000", "0000000000000000000000000000000000000000", 0, 0, new CAddress(0), new CAddress(0), null, null, Integer.MAX_VALUE, false, provider);
        @SuppressWarnings("unused")
        final CFunction parentFunction = new CFunction(internalModule, new MockView(), new CAddress(291), "Mock Function", "Mock Function", "Mock Description", 0, 0, 0, 0, FunctionType.NORMAL, "", 0, null, null, null, provider);
        final List<INaviInstruction> instructions1 = new ArrayList<INaviInstruction>();
        instructions1.add(new MockInstruction(1234));
        final List<INaviInstruction> instructions2 = new ArrayList<INaviInstruction>();
        instructions2.add(new MockInstruction(1235));
        new CBlockNode(new com.google.security.zynamics.binnavi.disassembly.CBasicBlock(1, "", instructions1));
        new CBlockNode(new com.google.security.zynamics.binnavi.disassembly.CBasicBlock(1, "", instructions2));
        final Function function = new Function(ModuleFactory.get(), m_internalFunction);
        function.load();
        Assert.assertNotNull(function.getReilCode());
    }

    @Test
    public void testSetDescription() throws CouldntSaveDataException, com.google.security.zynamics.binnavi.Database.Exceptions.CouldntSaveDataException {
        final MockFunctionListener listener = new MockFunctionListener();
        final Function function = new Function(ModuleFactory.get(), m_internalFunction);
        function.addListener(listener);
        function.setDescription("Hannes");
        Assert.assertEquals("Hannes", function.getDescription());
        Assert.assertEquals("Hannes", m_internalFunction.getDescription());
        Assert.assertEquals("changedDescription;", listener.events);
        m_internalFunction.setDescription("Hannes 2");
        Assert.assertEquals("Hannes 2", function.getDescription());
        Assert.assertEquals("Hannes 2", m_internalFunction.getDescription());
        Assert.assertEquals("changedDescription;changedDescription;", listener.events);
        function.removeListener(listener);
    }

    @Test
    public void testSetName() throws CouldntSaveDataException, com.google.security.zynamics.binnavi.Database.Exceptions.CouldntSaveDataException {
        final MockFunctionListener listener = new MockFunctionListener();
        final Function function = new Function(ModuleFactory.get(), m_internalFunction);
        function.addListener(listener);
        function.setName("Hannes");
        Assert.assertEquals("Hannes", function.getName());
        Assert.assertEquals("Hannes", m_internalFunction.getName());
        Assert.assertEquals("changedName;", listener.events);
        m_internalFunction.setName("Hannes 2");
        Assert.assertEquals("Hannes 2", function.getName());
        Assert.assertEquals("Hannes 2", m_internalFunction.getName());
        Assert.assertEquals("changedName;changedName;", listener.events);
        function.removeListener(listener);
    }
}

