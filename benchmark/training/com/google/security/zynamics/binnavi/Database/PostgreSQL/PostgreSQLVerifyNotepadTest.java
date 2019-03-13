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
package com.google.security.zynamics.binnavi.Database.PostgreSQL;


import GraphType.CALLGRAPH;
import GraphType.FLOWGRAPH;
import ViewType.Native;
import com.google.common.collect.Iterables;
import com.google.security.zynamics.binnavi.Database.CDatabase;
import com.google.security.zynamics.binnavi.Database.Exceptions.CPartialLoadException;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntLoadDataException;
import com.google.security.zynamics.binnavi.Database.Exceptions.LoadCancelledException;
import com.google.security.zynamics.binnavi.Database.Interfaces.SQLProvider;
import com.google.security.zynamics.binnavi.disassembly.CommentManager;
import com.google.security.zynamics.binnavi.disassembly.IBlockNode;
import com.google.security.zynamics.binnavi.disassembly.INaviFunction;
import com.google.security.zynamics.binnavi.disassembly.INaviInstruction;
import com.google.security.zynamics.binnavi.disassembly.INaviModule;
import com.google.security.zynamics.binnavi.disassembly.views.INaviView;
import com.google.security.zynamics.zylib.disassembly.CAddress;
import com.google.security.zynamics.zylib.reflection.ReflectionHelpers;
import java.math.BigInteger;
import java.util.LinkedHashSet;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class PostgreSQLVerifyNotepadTest {
    private CDatabase m_database;

    @Test
    public void verifyFunctions() throws CouldntLoadDataException, LoadCancelledException {
        final INaviModule module = m_database.getContent().getModules().get(0);
        module.load();
        Assert.assertEquals("RegQueryValueExW", module.getContent().getFunctionContainer().getFunctions().get(0).getName());
        Assert.assertEquals("GlobalFree", module.getContent().getFunctionContainer().getFunctions().get(50).getName());
        Assert.assertEquals("DefWindowProcW", module.getContent().getFunctionContainer().getFunctions().get(100).getName());
        Assert.assertEquals("sub_10075F4", module.getContent().getFunctionContainer().getFunctions().get(285).getName());
        Assert.assertEquals(BigInteger.valueOf(16781312), module.getContent().getFunctionContainer().getFunctions().get(0).getAddress().toBigInteger());
        Assert.assertEquals(BigInteger.valueOf(16781524), module.getContent().getFunctionContainer().getFunctions().get(50).getAddress().toBigInteger());
        Assert.assertEquals(BigInteger.valueOf(16781732), module.getContent().getFunctionContainer().getFunctions().get(100).getAddress().toBigInteger());
        Assert.assertEquals(BigInteger.valueOf(16807412), module.getContent().getFunctionContainer().getFunctions().get(285).getAddress().toBigInteger());
        module.close();
    }

    @Test
    public void verifyInstructions() throws CouldntLoadDataException, LoadCancelledException {
        final INaviModule module = m_database.getContent().getModules().get(0);
        module.load();
        final INaviFunction function = module.getContent().getFunctionContainer().getFunction(new CAddress(BigInteger.valueOf(16783709)));
        function.load();
        final List<IBlockNode> blocks = function.getBasicBlocks();
        final IBlockNode block = blocks.get(0);
        Assert.assertEquals(BigInteger.valueOf(16783709), block.getAddress().toBigInteger());
        final Iterable<INaviInstruction> instructions = block.getInstructions();
        Iterables.get(instructions, 0).toString();
        Assert.assertEquals("0100195D mov edi, edi", Iterables.get(instructions, 0).toString());
        Assert.assertEquals("0100195F push ebp", Iterables.get(instructions, 1).toString());
        Assert.assertEquals("01001960 mov ebp, esp", Iterables.get(instructions, 2).toString());
        Assert.assertEquals("01001962 push ecx", Iterables.get(instructions, 3).toString());
        Assert.assertEquals("01001963 push 2", Iterables.get(instructions, 4).toString());
        Assert.assertEquals("01001965 lea eax, ss: [ebp + LCData]", Iterables.get(instructions, 5).toString());
        Assert.assertEquals("01001968 push eax", Iterables.get(instructions, 6).toString());
        Assert.assertEquals("01001969 push 13", Iterables.get(instructions, 7).toString());
        Assert.assertEquals("0100196B push 1024", Iterables.get(instructions, 8).toString());
        Assert.assertEquals("01001970 mov ds: [16819428], sub_1005F63", Iterables.get(instructions, 9).toString());
        Assert.assertEquals("0100197A mov ds: [16819436], 12", Iterables.get(instructions, 10).toString());
        Assert.assertEquals("01001984 call ds: [GetLocaleInfoW]", Iterables.get(instructions, 11).toString());
        Assert.assertEquals("0100198A cmp word ss: [ebp + LCData], word 49", Iterables.get(instructions, 12).toString());
        Assert.assertEquals("0100198F jnz loc_10019B1", Iterables.get(instructions, 13).toString());
        module.close();
    }

    @Test
    public void verifyReferences() throws CouldntLoadDataException, LoadCancelledException {
        final INaviModule module = m_database.getContent().getModules().get(0);
        module.load();
        final INaviFunction function = module.getContent().getFunctionContainer().getFunction(new CAddress(BigInteger.valueOf(16805648)));
        function.load();
        final List<IBlockNode> blocks = function.getBasicBlocks();
        final IBlockNode block = blocks.get(12);
        Assert.assertEquals(BigInteger.valueOf(16805931), block.getAddress().toBigInteger());
        final Iterable<INaviInstruction> instructions = block.getInstructions();
        Assert.assertEquals(1, Iterables.get(instructions, 10).getOperands().get(0).getRootNode().getChildren().get(0).getReferences().size());
        module.close();
    }

    @Test
    public void verifyViews() throws CPartialLoadException, CouldntLoadDataException, LoadCancelledException, IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        final SQLProvider provider = ((SQLProvider) (ReflectionHelpers.getField(m_database, "provider")));
        final INaviModule module = m_database.getContent().getModules().get(0);
        module.load();
        final List<INaviView> views = module.getContent().getViewContainer().getViews();
        final INaviView callgraph = views.get(0);
        Assert.assertEquals(null, module.getContent().getViewContainer().getFunction(callgraph));
        Assert.assertEquals(Native, callgraph.getType());
        Assert.assertEquals(CALLGRAPH, callgraph.getGraphType());
        Assert.assertEquals(287, callgraph.getNodeCount());
        Assert.assertEquals(848, callgraph.getEdgeCount());
        callgraph.load();
        Assert.assertEquals(287, callgraph.getNodeCount());
        Assert.assertEquals(848, callgraph.getEdgeCount());
        callgraph.close();
        final LinkedHashSet<?> cgListeners = ((LinkedHashSet<?>) (ReflectionHelpers.getField(ReflectionHelpers.getField(CommentManager.get(provider), "listeners"), "m_listeners")));
        Assert.assertEquals(((module.getContent().getFunctionContainer().getFunctions().size()) + 1), cgListeners.size());// the +1 here is the listener in the type instance container.

        for (int i = 1; i < (views.size()); i++) {
            final INaviView view = views.get(i);
            final INaviFunction function = module.getContent().getViewContainer().getFunction(view);
            Assert.assertEquals(view.getName(), function.getName());
            Assert.assertEquals(Native, view.getType());
            Assert.assertEquals(FLOWGRAPH, view.getGraphType());
            Assert.assertEquals(view.getNodeCount(), function.getBasicBlockCount());
            Assert.assertEquals(view.getEdgeCount(), function.getEdgeCount());
            view.load();
            function.load();
            Assert.assertEquals(view.getNodeCount(), function.getBasicBlockCount());
            Assert.assertEquals(view.getEdgeCount(), function.getEdgeCount());
            if (function.getAddress().toBigInteger().equals(BigInteger.valueOf(16783657))) {
                Assert.assertEquals(view.getNodeCount(), 5);
                Assert.assertEquals(view.getEdgeCount(), 6);
            } else
                if (function.getAddress().toBigInteger().equals(BigInteger.valueOf(16790571))) {
                    Assert.assertEquals(view.getNodeCount(), 92);
                    Assert.assertEquals(view.getEdgeCount(), 144);
                } else
                    if (i == 1) {
                        Assert.assertEquals(view.getNodeCount(), 0);
                        Assert.assertEquals(view.getEdgeCount(), 0);
                    }


            view.close();
            function.close();
        }
        module.close();
    }
}

