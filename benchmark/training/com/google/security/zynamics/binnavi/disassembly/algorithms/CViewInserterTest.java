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
package com.google.security.zynamics.binnavi.disassembly.algorithms;


import EdgeType.JUMP_UNCONDITIONAL;
import GraphType.MIXED_GRAPH;
import ViewType.NonNative;
import com.google.common.collect.Lists;
import com.google.security.zynamics.binnavi.Common.CommonTestObjects;
import com.google.security.zynamics.binnavi.Database.CModuleViewGenerator;
import com.google.security.zynamics.binnavi.Database.Exceptions.CPartialLoadException;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntLoadDataException;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntSaveDataException;
import com.google.security.zynamics.binnavi.Database.Exceptions.LoadCancelledException;
import com.google.security.zynamics.binnavi.Database.MockClasses.MockSqlProvider;
import com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.Interfaces.IComment;
import com.google.security.zynamics.binnavi.Gui.Users.CUserManager;
import com.google.security.zynamics.binnavi.Gui.Users.Interfaces.IUser;
import com.google.security.zynamics.binnavi.config.ConfigManager;
import com.google.security.zynamics.binnavi.config.FileReadException;
import com.google.security.zynamics.binnavi.disassembly.CCodeNode;
import com.google.security.zynamics.binnavi.disassembly.CFunctionNode;
import com.google.security.zynamics.binnavi.disassembly.CGroupNode;
import com.google.security.zynamics.binnavi.disassembly.CNaviViewEdge;
import com.google.security.zynamics.binnavi.disassembly.CTextNode;
import com.google.security.zynamics.binnavi.disassembly.INaviModule;
import com.google.security.zynamics.binnavi.disassembly.INaviViewNode;
import com.google.security.zynamics.binnavi.disassembly.MockFunction;
import com.google.security.zynamics.binnavi.disassembly.MockInstruction;
import com.google.security.zynamics.binnavi.disassembly.Modules.MockModule;
import com.google.security.zynamics.binnavi.disassembly.views.CView;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class CViewInserterTest {
    @Test
    public void test() throws CPartialLoadException, CouldntLoadDataException, CouldntSaveDataException, LoadCancelledException, FileReadException {
        ConfigManager.instance().read();
        final INaviModule mockModule = new MockModule();
        final MockSqlProvider mockProvider = new MockSqlProvider();
        final CUserManager userManager = CUserManager.get(mockProvider);
        final IUser user = userManager.addUser(" VIEW INSERTER USER ");
        userManager.setCurrentActiveUser(user);
        final CModuleViewGenerator generator = new CModuleViewGenerator(mockProvider, mockModule);
        final CView view = generator.generate(1, "", "", NonNative, MIXED_GRAPH, new Date(), new Date(), 0, 0, new HashSet<com.google.security.zynamics.binnavi.Tagging.CTag>(), new HashSet<com.google.security.zynamics.binnavi.Tagging.CTag>(), false);
        view.load();
        final MockFunction mockFunction = new MockFunction(mockProvider);
        final CFunctionNode fnode1 = view.getContent().createFunctionNode(mockFunction);
        final CFunctionNode fnode2 = view.getContent().createFunctionNode(mockFunction);
        @SuppressWarnings("unused")
        final CNaviViewEdge edge1 = view.getContent().createEdge(fnode1, fnode2, JUMP_UNCONDITIONAL);
        final MockInstruction instruction1 = new MockInstruction();
        final CCodeNode cnode1 = view.getContent().createCodeNode(mockFunction, Lists.newArrayList(instruction1));
        final CCodeNode cnode2 = view.getContent().createCodeNode(mockFunction, Lists.newArrayList(instruction1));
        @SuppressWarnings("unused")
        final CNaviViewEdge edge2 = view.getContent().createEdge(cnode1, cnode2, JUMP_UNCONDITIONAL);
        final ArrayList<IComment> comments = Lists.<IComment>newArrayList(new com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.CComment(null, CommonTestObjects.TEST_USER_1, null, "Foo"));
        final CTextNode tnode1 = view.getContent().createTextNode(comments);
        @SuppressWarnings("unused")
        final CNaviViewEdge edge3 = view.getContent().createEdge(cnode1, tnode1, JUMP_UNCONDITIONAL);
        final CGroupNode gnode1 = view.getContent().createGroupNode(Lists.newArrayList(((INaviViewNode) (fnode1)), ((INaviViewNode) (fnode2))));
        gnode1.appendComment("TEST GROUP NODE COMMENT 1");
        final CView view2 = generator.generate(2, "", "", NonNative, MIXED_GRAPH, new Date(), new Date(), 0, 0, new HashSet<com.google.security.zynamics.binnavi.Tagging.CTag>(), new HashSet<com.google.security.zynamics.binnavi.Tagging.CTag>(), false);
        view2.load();
        CViewInserter.insertView(view, view2);
        final List<INaviViewNode> nodes = view2.getGraph().getNodes();
        Assert.assertEquals(view2.getNodeCount(), 6);
        Assert.assertEquals(mockFunction, getFunction());
        Assert.assertEquals(nodes.get(5), getParentGroup());
    }
}

