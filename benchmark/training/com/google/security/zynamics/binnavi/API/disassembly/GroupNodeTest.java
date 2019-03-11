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


import GraphType.FLOWGRAPH;
import ViewType.NonNative;
import com.google.common.collect.Lists;
import com.google.security.zynamics.binnavi.Database.CModuleViewGenerator;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntSaveDataException;
import com.google.security.zynamics.binnavi.Database.Interfaces.SQLProvider;
import com.google.security.zynamics.binnavi.Database.MockClasses.MockDatabase;
import com.google.security.zynamics.binnavi.Database.MockClasses.MockSqlProvider;
import com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.Interfaces.IComment;
import com.google.security.zynamics.binnavi.Gui.Users.CUserManager;
import com.google.security.zynamics.binnavi.Gui.Users.Interfaces.IUser;
import com.google.security.zynamics.binnavi.Tagging.MockTagManager;
import com.google.security.zynamics.binnavi.Tagging.TagType;
import com.google.security.zynamics.binnavi.disassembly.CFunction;
import com.google.security.zynamics.binnavi.disassembly.CGroupNode;
import com.google.security.zynamics.binnavi.disassembly.INaviGroupNode;
import com.google.security.zynamics.binnavi.disassembly.MockInstruction;
import com.google.security.zynamics.binnavi.disassembly.MockView;
import com.google.security.zynamics.binnavi.disassembly.Modules.CModule;
import com.google.security.zynamics.binnavi.disassembly.Modules.MockModule;
import com.google.security.zynamics.binnavi.disassembly.views.INaviView;
import com.google.security.zynamics.zylib.disassembly.CAddress;
import com.google.security.zynamics.zylib.disassembly.FunctionType;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class GroupNodeTest {
    @Test
    public void testConstructor() {
        final Database database = new Database(new MockDatabase());
        final MockModule mockModule = new MockModule();
        final TagManager nodeTagManager = new TagManager(new MockTagManager(TagType.NODE_TAG));
        final TagManager viewTagManager = new TagManager(new MockTagManager(TagType.VIEW_TAG));
        final Module module = new Module(database, mockModule, nodeTagManager, viewTagManager);
        final MockView mockView = new MockView();
        final View view = new View(module, mockView, nodeTagManager, viewTagManager);
        final INaviGroupNode internalGroupNode = new CGroupNode(0, 0, 0, 0, 0, Color.RED, false, false, new HashSet<com.google.security.zynamics.binnavi.Tagging.CTag>(), new ArrayList<IComment>(), false, new MockSqlProvider());
        final GroupNode node = new GroupNode(view, internalGroupNode, viewTagManager);
        Assert.assertEquals("Group Node [0 elements]", node.toString());
    }

    @Test
    public void testElements() throws CouldntLoadDataException, PartialLoadException {
        final Database database = new Database(new MockDatabase());
        final SQLProvider mockProvider = new MockSqlProvider();
        final CModule mockModule = new CModule(1, "", "", new Date(), new Date(), "00000000000000000000000000000000", "0000000000000000000000000000000000000000", 0, 0, new CAddress(0), new CAddress(0), null, null, Integer.MAX_VALUE, false, mockProvider);
        final TagManager nodeTagManager = new TagManager(new MockTagManager(TagType.NODE_TAG));
        final TagManager viewTagManager = new TagManager(new MockTagManager(TagType.VIEW_TAG));
        final Module module = new Module(database, mockModule, nodeTagManager, viewTagManager);
        final CModuleViewGenerator generator = new CModuleViewGenerator(mockProvider, mockModule);
        final INaviView mockView = generator.generate(1, "", "", NonNative, FLOWGRAPH, new Date(), new Date(), 0, 0, new HashSet<com.google.security.zynamics.binnavi.Tagging.CTag>(), new HashSet<com.google.security.zynamics.binnavi.Tagging.CTag>(), false);
        final View view = new View(module, mockView, nodeTagManager, viewTagManager);
        view.load();
        final CModule internalModule = new CModule(1, "", "", new Date(), new Date(), "00000000000000000000000000000000", "0000000000000000000000000000000000000000", 0, 0, new CAddress(0), new CAddress(0), null, null, Integer.MAX_VALUE, false, mockProvider);
        final CFunction parentFunction = new CFunction(internalModule, new MockView(), new CAddress(291), "Mock Function", "Mock Function", "Mock Description", 0, 0, 0, 0, FunctionType.NORMAL, "", 0, null, null, null, mockProvider);
        final CodeNode codeNode = view.createCodeNode(new Function(module, parentFunction), Lists.newArrayList(new Instruction(new MockInstruction())));
        final CodeNode codeNode2 = view.createCodeNode(new Function(module, parentFunction), Lists.newArrayList(new Instruction(new MockInstruction())));
        final GroupNode node = view.createGroupNode("", Lists.newArrayList(((ViewNode) (codeNode2))));
        final MockGroupNodeListener listener = new MockGroupNodeListener();
        final MockViewNodeListener listener2 = new MockViewNodeListener();
        node.addListener(listener);
        codeNode.addListener(listener2);
        node.addNode(codeNode);
        Assert.assertEquals(2, node.getElements().size());
        Assert.assertEquals(codeNode, node.getElements().get(1));
        Assert.assertEquals(node, codeNode.getParentGroup());
        Assert.assertEquals("addedElement;", listener.events);
        Assert.assertEquals("changedParentGroup;", listener2.events);
        node.removeNode(codeNode);
        Assert.assertEquals(1, node.getElements().size());
        Assert.assertEquals(null, codeNode.getParentGroup());
        Assert.assertEquals("addedElement;removedElement;", listener.events);
        Assert.assertEquals("changedParentGroup;changedParentGroup;", listener2.events);
        node.removeListener(listener);
    }

    @Test
    public void testSetCollapsed() {
        final Database database = new Database(new MockDatabase());
        final MockModule mockModule = new MockModule();
        final TagManager nodeTagManager = new TagManager(new MockTagManager(TagType.NODE_TAG));
        final TagManager viewTagManager = new TagManager(new MockTagManager(TagType.VIEW_TAG));
        final Module module = new Module(database, mockModule, nodeTagManager, viewTagManager);
        final MockView mockView = new MockView();
        final View view = new View(module, mockView, nodeTagManager, viewTagManager);
        final INaviGroupNode internalGroupNode = new CGroupNode(0, 0, 0, 0, 0, Color.RED, false, false, new HashSet<com.google.security.zynamics.binnavi.Tagging.CTag>(), new ArrayList<IComment>(), false, new MockSqlProvider());
        final GroupNode node = new GroupNode(view, internalGroupNode, viewTagManager);
        final MockGroupNodeListener listener = new MockGroupNodeListener();
        node.addListener(listener);
        node.setCollapsed(true);
        Assert.assertEquals("changedState;", listener.events);
        Assert.assertTrue(node.isCollapsed());
        node.removeListener(listener);
    }

    @Test
    public void testSetText() throws CouldntLoadDataException, CouldntSaveDataException {
        final Database database = new Database(new MockDatabase());
        final SQLProvider provider = new MockSqlProvider();
        final MockModule mockModule = new MockModule();
        final TagManager nodeTagManager = new TagManager(new MockTagManager(TagType.NODE_TAG));
        final TagManager viewTagManager = new TagManager(new MockTagManager(TagType.VIEW_TAG));
        final Module module = new Module(database, mockModule, nodeTagManager, viewTagManager);
        final MockView mockView = new MockView();
        final View view = new View(module, mockView, nodeTagManager, viewTagManager);
        final INaviGroupNode internalGroupNode = new CGroupNode(0, 0, 0, 0, 0, Color.RED, false, false, new HashSet<com.google.security.zynamics.binnavi.Tagging.CTag>(), new ArrayList<IComment>(), false, provider);
        final GroupNode node = new GroupNode(view, internalGroupNode, viewTagManager);
        final MockGroupNodeListener listener = new MockGroupNodeListener();
        final CUserManager userManager = CUserManager.get(provider);
        final IUser user = userManager.addUser(" TEST APPEND GROUP NODE COMMENT ");
        userManager.setCurrentActiveUser(user);
        node.addListener(listener);
        final List<IComment> comments = node.appendComment("Hannes");
        Assert.assertEquals("appendedComment;", listener.events);
        Assert.assertEquals(comments, node.getComment());
        node.removeListener(listener);
    }
}

