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
package com.google.security.zynamics.binnavi.standardplugins.pathfinder;


import EdgeType.JumpConditionalFalse;
import EdgeType.JumpConditionalTrue;
import EdgeType.JumpUnconditional;
import com.google.security.zynamics.binnavi.API.disassembly.BasicBlock;
import com.google.security.zynamics.binnavi.API.disassembly.CouldntLoadDataException;
import com.google.security.zynamics.binnavi.API.disassembly.Function;
import com.google.security.zynamics.binnavi.API.disassembly.Module;
import com.google.security.zynamics.binnavi.API.disassembly.PartialLoadException;
import com.google.security.zynamics.binnavi.API.disassembly.View;
import com.google.security.zynamics.binnavi.API.disassembly.ViewEdge;
import com.google.security.zynamics.binnavi.API.disassembly.ViewNode;
import java.awt.Color;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class PathFinderTest {
    private Module m_notepad;

    private Module m_kernel32;

    @Test
    public void testFirstBlock() throws CouldntLoadDataException, PartialLoadException {
        // Tests 100337E -> 1005179 -> 1007568 where all calls are in the first block
        // of the respective functions.
        // Tests path finding from the beginning to the end of a single function
        final Function startFunction = PathFinderTest.findFunction(m_notepad, 16790398);
        final BasicBlock startBlock = PathFinderTest.findBlock(startFunction, 16790466);
        final Function endFunction = PathFinderTest.findFunction(m_notepad, 16807272);
        final BasicBlock endBlock = PathFinderTest.findBlock(endFunction, 16807272);
        final View view = PathFinder.createPath(m_notepad, startBlock, endBlock, null, null);
        Assert.assertEquals(3, view.getGraph().nodeCount());
        Assert.assertEquals(2, view.getGraph().edgeCount());
    }

    // @Test
    // public void testFoo() throws CouldntLoadDataException, CouldntSaveDataException
    // {
    // // TODO: Bring this test back in msw3prt.idb
    // 
    // final Function startFunction = findFunction(m_foo, 0x5FEF8426);
    // final BasicBlock startBlock = findBlock(startFunction, 0x5FEF8426);
    // 
    // final Function endFunction = findFunction(m_foo, 0x5FEFF06D);
    // final BasicBlock endBlock = findBlock(endFunction, 0x5FEFF0DB);
    // 
    // final View view = PathFinder.createPath(m_foo, startBlock, endBlock, null, null);
    // 
    // assertEquals(46, view.getGraph().nodeCount());
    // assertEquals(49, view.getGraph().edgeCount());
    // }
    @Test
    public void testInsideFunction() throws CouldntLoadDataException, PartialLoadException {
        // Tests path finding from the beginning to the end of a single function
        final Function startFunction = PathFinderTest.findFunction(m_notepad, 16788359);
        final BasicBlock startBlock = PathFinderTest.findBlock(startFunction, 16788359);
        final BasicBlock endBlock = PathFinderTest.findBlock(startFunction, 16790378);
        final View view = PathFinder.createPath(m_notepad, startBlock, endBlock, null, null);
        Assert.assertEquals(96, view.getGraph().nodeCount());
        Assert.assertEquals(150, view.getGraph().edgeCount());
    }

    @Test
    public void testInsideFunctionPartial() throws CouldntLoadDataException, PartialLoadException {
        // Tests path finding somewhere inside a function
        final Function startFunction = PathFinderTest.findFunction(m_notepad, 16786514);
        final BasicBlock startBlock = PathFinderTest.findBlock(startFunction, 16786626);
        final BasicBlock endBlock = PathFinderTest.findBlock(startFunction, 16787195);
        final View view = PathFinder.createPath(m_notepad, startBlock, endBlock, null, null);
        Assert.assertEquals(9, view.getGraph().nodeCount());
        Assert.assertEquals(11, view.getGraph().edgeCount());
        final List<ViewEdge> edges = view.getGraph().getEdges();
        final List<ViewNode> nodes = view.getGraph().getNodes();
        Assert.assertEquals(JumpConditionalFalse, PathFinderTest.findEdge(edges, 16786626, 16786723).getType());
        Assert.assertEquals(JumpConditionalTrue, PathFinderTest.findEdge(edges, 16786626, 16786745).getType());
        Assert.assertEquals(JumpUnconditional, PathFinderTest.findEdge(edges, 16786751, 16787193).getType());
        Assert.assertEquals(Color.GREEN, PathFinderTest.findNode(nodes, 16786626).getColor());
        Assert.assertEquals(Color.YELLOW, PathFinderTest.findNode(nodes, 16787195).getColor());
    }

    @Test
    public void testPassingFunctionContinue() throws CouldntLoadDataException, PartialLoadException {
        // Tests pathfinding from one function to another function while passing one function
        // and having a target block that is NOT a RETURN block.
        // 
        // What should happen here is that multiple paths are generated because the target
        // block is not necessarily hit the first time the function is entered.
        // 0x1004565 -> 0x1003C92 -> 0x100398D
        final Function startFunction = PathFinderTest.findFunction(m_notepad, 16794981);
        final BasicBlock startBlock = PathFinderTest.findBlock(startFunction, 16795177);
        final Function endFunction = PathFinderTest.findFunction(m_notepad, 16791949);
        final BasicBlock endBlock = PathFinderTest.findBlock(endFunction, 16792017);
        final View view = PathFinder.createPath(m_notepad, startBlock, endBlock, null, null);
        Assert.assertEquals(37, view.getGraph().nodeCount());
        Assert.assertEquals(64, view.getGraph().edgeCount());
    }

    @Test
    public void testPassingFunctionReturn() throws CouldntLoadDataException, PartialLoadException {
        // Tests pathfinding from one function to another function while passing one function
        // and having a target block that is a RETURN block.
        // 
        // What should happen here is that the pathfinding algorithm stops when it reaches
        // the RETURN node. That is consecutive calls to the target function should not
        // be part of the pathfinding result.
        // 0x1004565 -> 0x1003C92 -> 0x100398D
        final Function startFunction = PathFinderTest.findFunction(m_notepad, 16794981);
        final BasicBlock startBlock = PathFinderTest.findBlock(startFunction, 16795177);
        final Function endFunction = PathFinderTest.findFunction(m_notepad, 16791949);
        final BasicBlock endBlock = PathFinderTest.findBlock(endFunction, 16792025);
        final View view = PathFinder.createPath(m_notepad, startBlock, endBlock, null, null);
        Assert.assertEquals(14, view.getGraph().nodeCount());
        Assert.assertEquals(19, view.getGraph().edgeCount());
    }

    @Test
    public void testRecursivePath() throws CouldntLoadDataException, PartialLoadException {
        // Tests pathfinding from a simple function to a simple function through
        // a recursive path
        final Function startFunction = PathFinderTest.findFunction(m_kernel32, 2088954034);// GetVolumePathNameA

        final BasicBlock startBlock = PathFinderTest.findBlock(startFunction, 2088954034);
        final Function endFunction = PathFinderTest.findFunction(m_kernel32, 2088800944);
        final BasicBlock endBlock = PathFinderTest.findBlock(endFunction, 2088800944);
        final View view = PathFinder.createPath(m_kernel32, startBlock, endBlock, null, null);
        Assert.assertEquals(1247, view.getGraph().nodeCount());
        Assert.assertEquals(1988, view.getGraph().edgeCount());
    }

    @Test
    public void testRecursiveTarget() throws CouldntLoadDataException, PartialLoadException {
        // Tests pathfinding from a simple function to a self-recursive function
        final Function startFunction = PathFinderTest.findFunction(m_kernel32, 2089184891);// SetCommConfig

        final BasicBlock startBlock = PathFinderTest.findBlock(startFunction, 2089185011);
        final Function endFunction = PathFinderTest.findFunction(m_kernel32, 2089180694);// SetCommState

        final BasicBlock endBlock = PathFinderTest.findBlock(endFunction, 2089181446);
        final View view = PathFinder.createPath(m_kernel32, startBlock, endBlock, null, null);
        /**
         * split blocks *
         */
        Assert.assertEquals(((2/**
         * calling function *
         */
         + 66)/**
         * called function *
         */
         + 3), view.getGraph().nodeCount());
        /**
         * recursive calls and returns *
         */
        Assert.assertEquals((((99/**
         * called function *
         */
         + 1)/**
         * calling target function *
         */
         + 3) + 3), view.getGraph().edgeCount());
    }

    @Test
    public void testRegularFunction() throws CouldntLoadDataException, PartialLoadException {
        // Tests pathfinding between two simple functions
        // 0x1004565
        // 0x1003CD7
        final Function startFunction = PathFinderTest.findFunction(m_notepad, 16794981);
        final BasicBlock startBlock = PathFinderTest.findBlock(startFunction, 16795177);
        final Function endFunction = PathFinderTest.findFunction(m_notepad, 16792722);
        final BasicBlock endBlock = PathFinderTest.findBlock(endFunction, 16792791);
        final View view = PathFinder.createPath(m_notepad, startBlock, endBlock, null, null);
        Assert.assertEquals(7, view.getGraph().nodeCount());
        Assert.assertEquals(8, view.getGraph().edgeCount());
    }

    @Test
    public void testToImportedFunction() throws CouldntLoadDataException, PartialLoadException {
        // Tests from the beginning of a function to an imported function
        final Function startFunction = PathFinderTest.findFunction(m_notepad, 16791949);
        final BasicBlock startBlock = PathFinderTest.findBlock(startFunction, 16791949);
        final Function endFunction = PathFinderTest.findFunction(m_notepad, 16781312);
        endFunction.load();
        final View view = PathFinder.createPath(m_notepad, startBlock, null, null, endFunction);
        Assert.assertEquals(3, view.getGraph().nodeCount());
        Assert.assertEquals(2, view.getGraph().edgeCount());
    }
}

