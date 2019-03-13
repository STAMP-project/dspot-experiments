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
package com.google.security.zynamics.binnavi.Database.PostgreSQL.Notifications.parsers;


import com.google.common.collect.Iterables;
import com.google.security.zynamics.binnavi.Database.CDatabase;
import com.google.security.zynamics.binnavi.Database.Exceptions.CPartialLoadException;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntDeleteException;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntLoadDataException;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntSaveDataException;
import com.google.security.zynamics.binnavi.Database.Exceptions.LoadCancelledException;
import com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.Interfaces.IComment;
import com.google.security.zynamics.binnavi.disassembly.ICallgraphView;
import com.google.security.zynamics.binnavi.disassembly.INaviCodeNode;
import com.google.security.zynamics.binnavi.disassembly.INaviEdge;
import com.google.security.zynamics.binnavi.disassembly.INaviFunction;
import com.google.security.zynamics.binnavi.disassembly.INaviFunctionNode;
import com.google.security.zynamics.binnavi.disassembly.INaviGroupNode;
import com.google.security.zynamics.binnavi.disassembly.INaviModule;
import com.google.security.zynamics.binnavi.disassembly.INaviTextNode;
import com.google.security.zynamics.binnavi.disassembly.INaviViewNode;
import com.google.security.zynamics.binnavi.disassembly.algorithms.CViewInserter;
import com.google.security.zynamics.binnavi.disassembly.views.CView;
import com.google.security.zynamics.binnavi.disassembly.views.INaviView;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test class for all tests related to notification synchronization between multiple
 * instances of BinNavi.
 */
@RunWith(JUnit4.class)
public class PostgreSQLNotificationProviderTest {
    private CDatabase databaseOne;

    private INaviModule databaseOneModuleTwo;

    private INaviFunction databaseOneFunction;

    private INaviView databaseOneView;

    private CDatabase databaseTwo;

    private INaviModule databaseTwoModuleTwo;

    private INaviFunction databaseTwoFunction;

    private INaviView databaseTwoView;

    private final CountDownLatch lock = new CountDownLatch(1);

    private ICallgraphView databaseOneCallGraph;

    private ICallgraphView databaseTwoCallGraph;

    @Test
    public void testAppendFunctionCommentSync() throws CouldntLoadDataException, CouldntSaveDataException, InterruptedException {
        final List<IComment> oneBefore = ((databaseOneFunction.getGlobalComment()) == null) ? new ArrayList<IComment>() : databaseOneFunction.getGlobalComment();
        final List<IComment> twoBefore = ((databaseTwoFunction.getGlobalComment()) == null) ? new ArrayList<IComment>() : databaseTwoFunction.getGlobalComment();
        Assert.assertEquals(oneBefore, twoBefore);
        databaseOneFunction.appendGlobalComment(" TEST NOTIFICATION PROVIDER TESTS (GLOBAL FUNCTION COMMENT) ");
        // The wait is necessary to have the poll function complete and propagate the changes from
        // database one to two over the PostgreSQL back end.
        synchronized(lock) {
            lock.await(1000, TimeUnit.MILLISECONDS);
        }
        final List<IComment> oneAfter = databaseOneFunction.getGlobalComment();
        final List<IComment> twoAfter = databaseTwoFunction.getGlobalComment();
        Assert.assertNotNull(oneAfter);
        Assert.assertNotNull(twoAfter);
        Assert.assertEquals(((oneBefore.size()) + 1), oneAfter.size());
        Assert.assertEquals(((twoBefore.size()) + 1), twoAfter.size());
        Assert.assertEquals(oneAfter, twoAfter);
    }

    @Test
    public void testAppendFunctionNodeCommentSync() throws CPartialLoadException, CouldntLoadDataException, CouldntSaveDataException, LoadCancelledException, InterruptedException {
        databaseOneCallGraph = databaseOneModuleTwo.getContent().getViewContainer().getNativeCallgraphView();
        databaseOneCallGraph.load();
        final INaviFunctionNode databaseOneFunctionNode = ((INaviFunctionNode) (databaseOneCallGraph.getGraph().getNodes().get(1)));
        databaseTwoCallGraph = databaseTwoModuleTwo.getContent().getViewContainer().getNativeCallgraphView();
        databaseTwoCallGraph.load();
        final INaviFunctionNode databaseTwoFunctionNode = ((INaviFunctionNode) (databaseTwoCallGraph.getGraph().getNodes().get(1)));
        final List<IComment> oneBefore = ((databaseOneFunctionNode.getLocalFunctionComment()) == null) ? new ArrayList<IComment>() : databaseOneFunctionNode.getLocalFunctionComment();
        final List<IComment> twoBefore = ((databaseTwoFunctionNode.getLocalFunctionComment()) == null) ? new ArrayList<IComment>() : databaseTwoFunctionNode.getLocalFunctionComment();
        Assert.assertEquals(oneBefore, twoBefore);
        databaseOneFunctionNode.appendLocalFunctionComment(" TEST NOTIFICATION PROVIDER TESTS (LOCAL FUNCTION NODE COMMENT) ");
        // The wait is necessary to have the poll function complete and propagate the changes from
        // database one to two over the PostgreSQL back end.
        synchronized(lock) {
            lock.await(1000, TimeUnit.MILLISECONDS);
        }
        final List<IComment> oneAfter = databaseOneFunctionNode.getLocalFunctionComment();
        final List<IComment> twoAfter = databaseTwoFunctionNode.getLocalFunctionComment();
        Assert.assertNotNull(oneAfter);
        Assert.assertNotNull(twoAfter);
        Assert.assertEquals(((oneBefore.size()) + 1), oneAfter.size());
        Assert.assertEquals(((twoBefore.size()) + 1), twoAfter.size());
        Assert.assertEquals(oneAfter, twoAfter);
    }

    @Test
    public void testAppendGlobalCodeNodeCommentSync() throws CouldntLoadDataException, CouldntSaveDataException, InterruptedException {
        final INaviCodeNode databaseOnecodeNode = databaseOneView.getContent().getBasicBlocks().get(1);
        final INaviCodeNode databaseTwocodeNode = databaseTwoView.getContent().getBasicBlocks().get(1);
        final List<IComment> oneBefore = ((databaseOnecodeNode.getComments().getGlobalCodeNodeComment()) == null) ? new ArrayList<IComment>() : databaseOnecodeNode.getComments().getGlobalCodeNodeComment();
        final List<IComment> twoBefore = ((databaseTwocodeNode.getComments().getGlobalCodeNodeComment()) == null) ? new ArrayList<IComment>() : databaseTwocodeNode.getComments().getGlobalCodeNodeComment();
        Assert.assertEquals(oneBefore, twoBefore);
        databaseOnecodeNode.getComments().appendGlobalCodeNodeComment(" TEST NOTIFICATION PROVIDER TESTS (GLOBAL CODE NODE COMMENT) ");
        // The wait is necessary to have the poll function complete and propagate the changes from
        // database one to two over the PostgreSQL back end.
        synchronized(lock) {
            lock.await(1000, TimeUnit.MILLISECONDS);
        }
        final List<IComment> oneAfter = databaseOnecodeNode.getComments().getGlobalCodeNodeComment();
        final List<IComment> twoAfter = databaseTwocodeNode.getComments().getGlobalCodeNodeComment();
        Assert.assertNotNull(oneAfter);
        Assert.assertNotNull(twoAfter);
        Assert.assertEquals(((oneBefore.size()) + 1), oneAfter.size());
        Assert.assertEquals(((twoBefore.size()) + 1), twoAfter.size());
        Assert.assertEquals(oneAfter, twoAfter);
    }

    @Test
    public void testAppendGlobalEdgeCommentSync() throws CouldntLoadDataException, CouldntSaveDataException, InterruptedException {
        final INaviEdge databaseOneEdge = databaseOneView.getContent().getGraph().getEdges().get(3);
        final INaviEdge databaseTwoEdge = databaseTwoView.getContent().getGraph().getEdges().get(3);
        final List<IComment> oneBefore = ((databaseOneEdge.getGlobalComment()) == null) ? new ArrayList<IComment>() : databaseOneEdge.getGlobalComment();
        final List<IComment> twoBefore = ((databaseTwoEdge.getGlobalComment()) == null) ? new ArrayList<IComment>() : databaseTwoEdge.getGlobalComment();
        Assert.assertEquals(oneBefore, twoBefore);
        databaseOneEdge.appendGlobalComment(" TEST NOTIFICATION PROVIDER TESTS (GLOBAL CODE NODE COMMENT) ");
        // The wait is necessary to have the poll function complete and propagate the changes from
        // database one to two over the PostgreSQL back end.
        synchronized(lock) {
            lock.await(1000, TimeUnit.MILLISECONDS);
        }
        final List<IComment> oneAfter = databaseOneEdge.getGlobalComment();
        final List<IComment> twoAfter = databaseTwoEdge.getGlobalComment();
        Assert.assertNotNull(oneAfter);
        Assert.assertNotNull(twoAfter);
        Assert.assertEquals(((oneBefore.size()) + 1), oneAfter.size());
        Assert.assertEquals(((twoBefore.size()) + 1), twoAfter.size());
        Assert.assertEquals(oneAfter, twoAfter);
    }

    @Test
    public void testAppendGlobalInstructionCommentSync() throws CouldntLoadDataException, CouldntSaveDataException, InterruptedException {
        final INaviCodeNode databaseOnecodeNode = databaseOneView.getContent().getBasicBlocks().get(3);
        final INaviCodeNode databaseTwocodeNode = databaseTwoView.getContent().getBasicBlocks().get(3);
        final List<IComment> oneBefore = ((Iterables.getFirst(databaseOnecodeNode.getInstructions(), null).getGlobalComment()) == null) ? new ArrayList<IComment>() : Iterables.getFirst(databaseOnecodeNode.getInstructions(), null).getGlobalComment();
        final List<IComment> twoBefore = ((Iterables.getFirst(databaseTwocodeNode.getInstructions(), null).getGlobalComment()) == null) ? new ArrayList<IComment>() : Iterables.getFirst(databaseTwocodeNode.getInstructions(), null).getGlobalComment();
        Assert.assertEquals(oneBefore, twoBefore);
        Iterables.getFirst(databaseOnecodeNode.getInstructions(), null).appendGlobalComment(" TEST NOTIFICATION PROVIDER TESTS (GLOBAL INSTRUCTION COMMENT)");
        // The wait is necessary to have the poll function complete and propagate the changes from
        // database one to two over the PostgreSQL back end.
        synchronized(lock) {
            lock.await(1000, TimeUnit.MILLISECONDS);
        }
        final List<IComment> oneAfter = Iterables.getFirst(databaseOnecodeNode.getInstructions(), null).getGlobalComment();
        final List<IComment> twoAfter = Iterables.getFirst(databaseTwocodeNode.getInstructions(), null).getGlobalComment();
        Assert.assertNotNull(oneAfter);
        Assert.assertNotNull(twoAfter);
        Assert.assertEquals(((oneBefore.size()) + 1), oneAfter.size());
        Assert.assertEquals(((twoBefore.size()) + 1), twoAfter.size());
        Assert.assertEquals(oneAfter, twoAfter);
    }

    @Test
    public void testAppendGroupNodeCommentSync() throws CPartialLoadException, CouldntLoadDataException, CouldntSaveDataException, LoadCancelledException, InterruptedException {
        final CView databaseOneGroupNodeView = databaseOneModuleTwo.getContent().getViewContainer().createView(" GROUP NODE TESTING VIEW ", "");
        CViewInserter.insertView(databaseOneView, databaseOneGroupNodeView);
        final INaviGroupNode databaseOneGroupNode = databaseOneGroupNodeView.getContent().createGroupNode(databaseOneGroupNodeView.getGraph().getNodes());
        databaseOneGroupNodeView.save();
        databaseTwoModuleTwo.close();
        databaseTwoModuleTwo.load();
        databaseTwoView.load();
        final INaviView databaseTwoGroupNodeView = Iterables.getLast(databaseTwoModuleTwo.getContent().getViewContainer().getUserViews());
        INaviGroupNode databaseTwoGroupNode = null;
        Assert.assertEquals(databaseOneGroupNodeView.getName(), databaseTwoGroupNodeView.getName());
        databaseTwoGroupNodeView.load();
        for (final INaviViewNode node : databaseTwoGroupNodeView.getContent().getGraph().getNodes()) {
            if (node instanceof INaviGroupNode) {
                databaseTwoGroupNode = ((INaviGroupNode) (node));
            }
        }
        Assert.assertNotNull(databaseTwoGroupNode);
        Assert.assertEquals(databaseTwoGroupNode.getId(), databaseOneGroupNode.getId());
        databaseOneGroupNode.appendComment(" TEST NOTIFICATION PROVIDER TESTS (GROUP NODE COMMENT) ");
        synchronized(lock) {
            lock.await(1000, TimeUnit.MILLISECONDS);
        }
        final List<IComment> oneAfter = databaseOneGroupNode.getComments();
        final List<IComment> twoAfter = databaseTwoGroupNode.getComments();
        Assert.assertNotNull(oneAfter);
        Assert.assertNotNull(twoAfter);
        Assert.assertEquals(1, oneAfter.size());
        Assert.assertEquals(1, twoAfter.size());
        Assert.assertEquals(oneAfter, twoAfter);
    }

    @Test
    public void testAppendLocalCodeNodeCommentSync() throws CouldntLoadDataException, CouldntSaveDataException, InterruptedException {
        final INaviCodeNode databaseOnecodeNode = databaseOneView.getContent().getBasicBlocks().get(2);
        final INaviCodeNode databaseTwocodeNode = databaseTwoView.getContent().getBasicBlocks().get(2);
        final List<IComment> oneBefore = ((databaseOnecodeNode.getComments().getLocalCodeNodeComment()) == null) ? new ArrayList<IComment>() : databaseOnecodeNode.getComments().getLocalCodeNodeComment();
        final List<IComment> twoBefore = ((databaseTwocodeNode.getComments().getLocalCodeNodeComment()) == null) ? new ArrayList<IComment>() : databaseTwocodeNode.getComments().getLocalCodeNodeComment();
        Assert.assertEquals(oneBefore, twoBefore);
        databaseOnecodeNode.getComments().appendLocalCodeNodeComment(" TEST NOTIFICATION PROVIDER TESTS (LOCAL CODE NODE COMMENT)");
        // The wait is necessary to have the poll function complete and propagate the changes from
        // database one to two over the PostgreSQL back end.
        synchronized(lock) {
            lock.await(1000, TimeUnit.MILLISECONDS);
        }
        final List<IComment> oneAfter = databaseOnecodeNode.getComments().getLocalCodeNodeComment();
        final List<IComment> twoAfter = databaseTwocodeNode.getComments().getLocalCodeNodeComment();
        Assert.assertNotNull(oneAfter);
        Assert.assertNotNull(twoAfter);
        Assert.assertEquals(((oneBefore.size()) + 1), oneAfter.size());
        Assert.assertEquals(((twoBefore.size()) + 1), twoAfter.size());
        Assert.assertEquals(oneAfter, twoAfter);
    }

    @Test
    public void testAppendLocalEdgeCommentSync() throws CouldntLoadDataException, CouldntSaveDataException, InterruptedException {
        final INaviEdge databaseOneEdge = databaseOneView.getContent().getGraph().getEdges().get(3);
        final INaviEdge databaseTwoEdge = databaseTwoView.getContent().getGraph().getEdges().get(3);
        final List<IComment> oneBefore = ((databaseOneEdge.getLocalComment()) == null) ? new ArrayList<IComment>() : databaseOneEdge.getLocalComment();
        final List<IComment> twoBefore = ((databaseTwoEdge.getLocalComment()) == null) ? new ArrayList<IComment>() : databaseTwoEdge.getLocalComment();
        Assert.assertEquals(oneBefore, twoBefore);
        databaseOneEdge.appendLocalComment(" TEST NOTIFICATION PROVIDER TESTS (LOCAL CODE NODE COMMENT) ");
        // The wait is necessary to have the poll function complete and propagate the changes from
        // database one to two over the PostgreSQL back end.
        synchronized(lock) {
            lock.await(1000, TimeUnit.MILLISECONDS);
        }
        final List<IComment> oneAfter = databaseOneEdge.getLocalComment();
        final List<IComment> twoAfter = databaseTwoEdge.getLocalComment();
        Assert.assertNotNull(oneAfter);
        Assert.assertNotNull(twoAfter);
        Assert.assertEquals(((oneBefore.size()) + 1), oneAfter.size());
        Assert.assertEquals(((twoBefore.size()) + 1), twoAfter.size());
        Assert.assertEquals(oneAfter, twoAfter);
    }

    @Test
    public void testAppendLocalInstructionCommentSync() throws CouldntLoadDataException, CouldntSaveDataException, InterruptedException {
        final INaviCodeNode databaseOnecodeNode = databaseOneView.getContent().getBasicBlocks().get(3);
        final INaviCodeNode databaseTwocodeNode = databaseTwoView.getContent().getBasicBlocks().get(3);
        final List<IComment> oneBefore = ((databaseOnecodeNode.getComments().getLocalInstructionComment(databaseOnecodeNode.getLastInstruction())) == null) ? new ArrayList<IComment>() : databaseOnecodeNode.getComments().getLocalInstructionComment(databaseOnecodeNode.getLastInstruction());
        final List<IComment> twoBefore = ((databaseTwocodeNode.getComments().getLocalInstructionComment(databaseTwocodeNode.getLastInstruction())) == null) ? new ArrayList<IComment>() : databaseTwocodeNode.getComments().getLocalInstructionComment(databaseTwocodeNode.getLastInstruction());
        Assert.assertEquals(oneBefore, twoBefore);
        databaseOnecodeNode.getComments().appendLocalInstructionComment(databaseOnecodeNode.getLastInstruction(), " TEST NOTIFICATION PROVIDER TESTS (GLOBAL INSTRUCTION COMMENT)");
        // The wait is necessary to have the poll function complete and propagate the changes from
        // database one to two over the PostgreSQL back end.
        synchronized(lock) {
            lock.await(1000, TimeUnit.MILLISECONDS);
        }
        final List<IComment> oneAfter = databaseOnecodeNode.getComments().getLocalInstructionComment(databaseOnecodeNode.getLastInstruction());
        final List<IComment> twoAfter = databaseTwocodeNode.getComments().getLocalInstructionComment(databaseTwocodeNode.getLastInstruction());
        Assert.assertNotNull(oneAfter);
        Assert.assertNotNull(twoAfter);
        Assert.assertEquals(((oneBefore.size()) + 1), oneAfter.size());
        Assert.assertEquals(((twoBefore.size()) + 1), twoAfter.size());
        Assert.assertEquals(oneAfter, twoAfter);
    }

    @Test
    public void testAppendTextNodeCommentSync() throws CPartialLoadException, CouldntLoadDataException, CouldntSaveDataException, LoadCancelledException, InterruptedException {
        final CView databaseOneTextNodeView = databaseOneModuleTwo.getContent().getViewContainer().createView(" TEXT NODE TESTING VIEW ", "");
        CViewInserter.insertView(databaseOneView, databaseOneTextNodeView);
        final INaviTextNode databaseOneTextNode = databaseOneTextNodeView.getContent().createTextNode(new ArrayList<IComment>());
        databaseOneTextNodeView.save();
        databaseTwoModuleTwo.close();
        databaseTwoModuleTwo.load();
        databaseTwoView.load();
        final INaviView databaseTwoTextNodeView = Iterables.getLast(databaseTwoModuleTwo.getContent().getViewContainer().getUserViews());
        INaviTextNode databaseTwoTextNode = null;
        Assert.assertEquals(databaseOneTextNodeView.getName(), databaseTwoTextNodeView.getName());
        databaseTwoTextNodeView.load();
        for (final INaviViewNode node : databaseTwoTextNodeView.getContent().getGraph().getNodes()) {
            if (node instanceof INaviTextNode) {
                databaseTwoTextNode = ((INaviTextNode) (node));
            }
        }
        Assert.assertNotNull(databaseTwoTextNode);
        Assert.assertEquals(databaseTwoTextNode.getId(), databaseOneTextNode.getId());
        databaseOneTextNode.appendComment(" TEST NOTIFICATION PROVIDER TESTS (TEXT NODE COMMENT) ");
        synchronized(lock) {
            lock.await(1000, TimeUnit.MILLISECONDS);
        }
        final List<IComment> oneAfter = databaseOneTextNode.getComments();
        final List<IComment> twoAfter = databaseTwoTextNode.getComments();
        Assert.assertNotNull(oneAfter);
        Assert.assertNotNull(twoAfter);
        Assert.assertEquals(1, oneAfter.size());
        Assert.assertEquals(1, twoAfter.size());
        Assert.assertEquals(oneAfter, twoAfter);
    }

    @Test
    public void testDeleteFunctionCommentSync() throws CouldntDeleteException, CouldntLoadDataException, CouldntSaveDataException, InterruptedException {
        final List<IComment> oneOne = ((databaseOneFunction.getGlobalComment()) == null) ? new ArrayList<IComment>() : databaseOneFunction.getGlobalComment();
        final List<IComment> twoOne = ((databaseTwoFunction.getGlobalComment()) == null) ? new ArrayList<IComment>() : databaseTwoFunction.getGlobalComment();
        Assert.assertEquals(oneOne, twoOne);
        final List<IComment> comments = databaseOneFunction.appendGlobalComment(" TEST NOTIFICATION PROVIDER TESTS (GLOBAL FUNCTION COMMENT) DELETE ");
        // The wait is necessary to have the poll function complete and propagate the changes from
        // database one to two over the PostgreSQL back end.
        synchronized(lock) {
            lock.await(1000, TimeUnit.MILLISECONDS);
        }
        final List<IComment> oneTwo = databaseOneFunction.getGlobalComment();
        final List<IComment> twoTwo = databaseTwoFunction.getGlobalComment();
        Assert.assertNotNull(oneTwo);
        Assert.assertNotNull(twoTwo);
        Assert.assertEquals(((oneOne.size()) + 1), oneTwo.size());
        Assert.assertEquals(((twoOne.size()) + 1), twoTwo.size());
        Assert.assertEquals(oneTwo, twoTwo);
        final int oneTwoSize = oneTwo.size();
        final int twoTwoSize = twoTwo.size();
        databaseOneFunction.deleteGlobalComment(Iterables.getLast(comments));
        // The wait is necessary to have the poll function complete and propagate the changes from
        // database one to two over the PostgreSQL back end.
        synchronized(lock) {
            lock.await(1000, TimeUnit.MILLISECONDS);
        }
        final List<IComment> oneThree = databaseOneFunction.getGlobalComment();
        final List<IComment> twoThree = databaseTwoFunction.getGlobalComment();
        Assert.assertEquals((oneTwoSize - 1), oneThree.size());
        Assert.assertEquals((twoTwoSize - 1), twoThree.size());
        Assert.assertEquals(oneThree, twoThree);
    }

    @Test
    public void testDeleteFunctionNodeCommentSync() throws CPartialLoadException, CouldntDeleteException, CouldntLoadDataException, CouldntSaveDataException, LoadCancelledException, InterruptedException {
        databaseOneCallGraph = databaseOneModuleTwo.getContent().getViewContainer().getNativeCallgraphView();
        databaseOneCallGraph.load();
        final INaviFunctionNode databaseOneFunctionNode = ((INaviFunctionNode) (databaseOneCallGraph.getGraph().getNodes().get(1)));
        databaseTwoCallGraph = databaseTwoModuleTwo.getContent().getViewContainer().getNativeCallgraphView();
        databaseTwoCallGraph.load();
        final INaviFunctionNode databaseTwoFunctionNode = ((INaviFunctionNode) (databaseTwoCallGraph.getGraph().getNodes().get(1)));
        final List<IComment> oneBefore = ((databaseOneFunctionNode.getLocalFunctionComment()) == null) ? new ArrayList<IComment>() : databaseOneFunctionNode.getLocalFunctionComment();
        final List<IComment> twoBefore = ((databaseTwoFunctionNode.getLocalFunctionComment()) == null) ? new ArrayList<IComment>() : databaseTwoFunctionNode.getLocalFunctionComment();
        Assert.assertEquals(oneBefore, twoBefore);
        final List<IComment> comments = databaseOneFunctionNode.appendLocalFunctionComment(" TEST NOTIFICATION PROVIDER TESTS (LOCAL FUNCTION NODE COMMENT) ");
        // The wait is necessary to have the poll function complete and propagate the changes from
        // database one to two over the PostgreSQL back end.
        synchronized(lock) {
            lock.await(1000, TimeUnit.MILLISECONDS);
        }
        final List<IComment> oneAfter = databaseOneFunctionNode.getLocalFunctionComment();
        final List<IComment> twoAfter = databaseTwoFunctionNode.getLocalFunctionComment();
        Assert.assertNotNull(oneAfter);
        Assert.assertNotNull(twoAfter);
        Assert.assertEquals(((oneBefore.size()) + 1), oneAfter.size());
        Assert.assertEquals(((twoBefore.size()) + 1), twoAfter.size());
        Assert.assertEquals(oneAfter, twoAfter);
        final int oneTwoSize = oneAfter.size();
        final int twoTwoSize = twoAfter.size();
        databaseOneFunctionNode.deleteLocalFunctionComment(Iterables.getLast(comments));
        // The wait is necessary to have the poll function complete and propagate the changes from
        // database one to two over the PostgreSQL back end.
        synchronized(lock) {
            lock.await(1000, TimeUnit.MILLISECONDS);
        }
        final List<IComment> oneThree = databaseOneFunctionNode.getLocalFunctionComment();
        final List<IComment> twoThree = databaseTwoFunctionNode.getLocalFunctionComment();
        Assert.assertEquals((oneTwoSize - 1), oneThree.size());
        Assert.assertEquals((twoTwoSize - 1), twoThree.size());
        Assert.assertEquals(oneThree, twoThree);
    }

    @Test
    public void testDeleteGlobalCodeNodeCommentSync() throws CouldntDeleteException, CouldntLoadDataException, CouldntSaveDataException, InterruptedException {
        final INaviCodeNode databaseOnecodeNode = databaseOneView.getContent().getBasicBlocks().get(1);
        final INaviCodeNode databaseTwocodeNode = databaseTwoView.getContent().getBasicBlocks().get(1);
        final List<IComment> oneBefore = ((databaseOnecodeNode.getComments().getGlobalCodeNodeComment()) == null) ? new ArrayList<IComment>() : databaseOnecodeNode.getComments().getGlobalCodeNodeComment();
        final List<IComment> twoBefore = ((databaseTwocodeNode.getComments().getGlobalCodeNodeComment()) == null) ? new ArrayList<IComment>() : databaseTwocodeNode.getComments().getGlobalCodeNodeComment();
        Assert.assertEquals(oneBefore, twoBefore);
        databaseOnecodeNode.getComments().appendGlobalCodeNodeComment(" TEST NOTIFICATION PROVIDER TESTS (GLOBAL CODE NODE COMMENT) ");
        // The wait is necessary to have the poll function complete and propagate the changes from
        // database one to two over the PostgreSQL back end.
        synchronized(lock) {
            lock.await(1000, TimeUnit.MILLISECONDS);
        }
        final List<IComment> oneAfter = databaseOnecodeNode.getComments().getGlobalCodeNodeComment();
        final List<IComment> twoAfter = databaseTwocodeNode.getComments().getGlobalCodeNodeComment();
        Assert.assertNotNull(oneAfter);
        Assert.assertNotNull(twoAfter);
        Assert.assertEquals(((oneBefore.size()) + 1), oneAfter.size());
        Assert.assertEquals(((twoBefore.size()) + 1), twoAfter.size());
        Assert.assertEquals(oneAfter, twoAfter);
        final int oneTwoSize = oneAfter.size();
        final int twoTwoSize = twoAfter.size();
        databaseOnecodeNode.getComments().deleteGlobalCodeNodeComment(Iterables.getLast(databaseOnecodeNode.getComments().getGlobalCodeNodeComment()));
        // The wait is necessary to have the poll function complete and propagate the changes from
        // database one to two over the PostgreSQL back end.
        synchronized(lock) {
            lock.await(1000, TimeUnit.MILLISECONDS);
        }
        final List<IComment> oneThree = databaseOnecodeNode.getComments().getGlobalCodeNodeComment();
        final List<IComment> twoThree = databaseTwocodeNode.getComments().getGlobalCodeNodeComment();
        Assert.assertEquals((oneTwoSize - 1), oneThree.size());
        Assert.assertEquals((twoTwoSize - 1), twoThree.size());
        Assert.assertEquals(oneThree, twoThree);
    }

    @Test
    public void testDeleteGlobalEdgeCommentSync() throws CouldntDeleteException, CouldntLoadDataException, CouldntSaveDataException, InterruptedException {
        final INaviEdge databaseOneEdge = databaseOneView.getContent().getGraph().getEdges().get(3);
        final INaviEdge databaseTwoEdge = databaseTwoView.getContent().getGraph().getEdges().get(3);
        final List<IComment> oneBefore = ((databaseOneEdge.getGlobalComment()) == null) ? new ArrayList<IComment>() : databaseOneEdge.getGlobalComment();
        final List<IComment> twoBefore = ((databaseTwoEdge.getGlobalComment()) == null) ? new ArrayList<IComment>() : databaseTwoEdge.getGlobalComment();
        Assert.assertEquals(oneBefore, twoBefore);
        databaseOneEdge.appendGlobalComment(" TEST NOTIFICATION PROVIDER TESTS (GLOBAL CODE NODE COMMENT) ");
        // The wait is necessary to have the poll function complete and propagate the changes from
        // database one to two over the PostgreSQL back end.
        synchronized(lock) {
            lock.await(1000, TimeUnit.MILLISECONDS);
        }
        final List<IComment> oneAfter = databaseOneEdge.getGlobalComment();
        final List<IComment> twoAfter = databaseTwoEdge.getGlobalComment();
        Assert.assertNotNull(oneAfter);
        Assert.assertNotNull(twoAfter);
        Assert.assertEquals(((oneBefore.size()) + 1), oneAfter.size());
        Assert.assertEquals(((twoBefore.size()) + 1), twoAfter.size());
        Assert.assertEquals(oneAfter, twoAfter);
        final int oneTwoSize = oneAfter.size();
        final int twoTwoSize = twoAfter.size();
        databaseOneEdge.deleteGlobalComment(Iterables.getLast(databaseOneEdge.getGlobalComment()));
        // The wait is necessary to have the poll function complete and propagate the changes from
        // database one to two over the PostgreSQL back end.
        synchronized(lock) {
            lock.await(1000, TimeUnit.MILLISECONDS);
        }
        final List<IComment> oneThree = databaseOneEdge.getGlobalComment();
        final List<IComment> twoThree = databaseTwoEdge.getGlobalComment();
        Assert.assertEquals((oneTwoSize - 1), oneThree.size());
        Assert.assertEquals((twoTwoSize - 1), twoThree.size());
        Assert.assertEquals(oneThree, twoThree);
    }

    @Test
    public void testDeleteGlobalInstructionCommentSync() throws CouldntDeleteException, CouldntLoadDataException, CouldntSaveDataException, InterruptedException {
        final INaviCodeNode databaseOnecodeNode = databaseOneView.getContent().getBasicBlocks().get(3);
        final INaviCodeNode databaseTwocodeNode = databaseTwoView.getContent().getBasicBlocks().get(3);
        final List<IComment> oneBefore = ((Iterables.getFirst(databaseOnecodeNode.getInstructions(), null).getGlobalComment()) == null) ? new ArrayList<IComment>() : Iterables.getFirst(databaseOnecodeNode.getInstructions(), null).getGlobalComment();
        final List<IComment> twoBefore = ((Iterables.getFirst(databaseTwocodeNode.getInstructions(), null).getGlobalComment()) == null) ? new ArrayList<IComment>() : Iterables.getFirst(databaseTwocodeNode.getInstructions(), null).getGlobalComment();
        Assert.assertEquals(oneBefore, twoBefore);
        Iterables.getFirst(databaseOnecodeNode.getInstructions(), null).appendGlobalComment(" TEST NOTIFICATION PROVIDER TESTS (GLOBAL INSTRUCTION COMMENT)");
        // The wait is necessary to have the poll function complete and propagate the changes from
        // database one to two over the PostgreSQL back end.
        synchronized(lock) {
            lock.await(1000, TimeUnit.MILLISECONDS);
        }
        final List<IComment> oneAfter = Iterables.getFirst(databaseOnecodeNode.getInstructions(), null).getGlobalComment();
        final List<IComment> twoAfter = Iterables.getFirst(databaseTwocodeNode.getInstructions(), null).getGlobalComment();
        Assert.assertNotNull(oneAfter);
        Assert.assertNotNull(twoAfter);
        Assert.assertEquals(((oneBefore.size()) + 1), oneAfter.size());
        Assert.assertEquals(((twoBefore.size()) + 1), twoAfter.size());
        Assert.assertEquals(oneAfter, twoAfter);
        final int oneTwoSize = oneAfter.size();
        final int twoTwoSize = twoAfter.size();
        Iterables.getFirst(databaseOnecodeNode.getInstructions(), null).deleteGlobalComment(Iterables.getLast(Iterables.getFirst(databaseOnecodeNode.getInstructions(), null).getGlobalComment()));
        // The wait is necessary to have the poll function complete and propagate the changes from
        // database one to two over the PostgreSQL back end.
        synchronized(lock) {
            lock.await(1000, TimeUnit.MILLISECONDS);
        }
        final List<IComment> oneThree = Iterables.getFirst(databaseOnecodeNode.getInstructions(), null).getGlobalComment();
        final List<IComment> twoThree = Iterables.getFirst(databaseTwocodeNode.getInstructions(), null).getGlobalComment();
        Assert.assertEquals((oneTwoSize - 1), oneThree.size());
        Assert.assertEquals((twoTwoSize - 1), twoThree.size());
        Assert.assertEquals(oneThree, twoThree);
    }

    @Test
    public void testDeleteGroupNodeComment() throws CPartialLoadException, CouldntDeleteException, CouldntLoadDataException, CouldntSaveDataException, LoadCancelledException, InterruptedException {
        final CView databaseOneGroupNodeView = databaseOneModuleTwo.getContent().getViewContainer().createView(" GROUP NODE TESTING VIEW ", "");
        CViewInserter.insertView(databaseOneView, databaseOneGroupNodeView);
        final INaviGroupNode databaseOneGroupNode = databaseOneGroupNodeView.getContent().createGroupNode(databaseOneGroupNodeView.getGraph().getNodes());
        databaseOneGroupNodeView.save();
        databaseTwoModuleTwo.close();
        databaseTwoModuleTwo.load();
        databaseTwoView.load();
        final INaviView databaseTwoGroupNodeView = Iterables.getLast(databaseTwoModuleTwo.getContent().getViewContainer().getUserViews());
        INaviGroupNode databaseTwoGroupNode = null;
        Assert.assertEquals(databaseOneGroupNodeView.getName(), databaseTwoGroupNodeView.getName());
        databaseTwoGroupNodeView.load();
        for (final INaviViewNode node : databaseTwoGroupNodeView.getContent().getGraph().getNodes()) {
            if (node instanceof INaviGroupNode) {
                databaseTwoGroupNode = ((INaviGroupNode) (node));
            }
        }
        Assert.assertNotNull(databaseTwoGroupNode);
        Assert.assertEquals(databaseTwoGroupNode.getId(), databaseOneGroupNode.getId());
        final List<IComment> comments = databaseOneGroupNode.appendComment(" TEST NOTIFICATION PROVIDER TESTS (GROUP NODE COMMENT) ");
        synchronized(lock) {
            lock.await(1000, TimeUnit.MILLISECONDS);
        }
        final List<IComment> oneAfter = databaseOneGroupNode.getComments();
        final List<IComment> twoAfter = databaseTwoGroupNode.getComments();
        Assert.assertNotNull(oneAfter);
        Assert.assertNotNull(twoAfter);
        Assert.assertEquals(1, oneAfter.size());
        Assert.assertEquals(1, twoAfter.size());
        Assert.assertEquals(oneAfter, twoAfter);
        final int oneTwoSize = oneAfter.size();
        final int twoTwoSize = twoAfter.size();
        databaseOneGroupNode.deleteComment(Iterables.getLast(comments));
        // The wait is necessary to have the poll function complete and propagate the changes from
        // database one to two over the PostgreSQL back end.
        synchronized(lock) {
            lock.await(1000, TimeUnit.MILLISECONDS);
        }
        final List<IComment> oneThree = databaseOneGroupNode.getComments();
        final List<IComment> twoThree = databaseTwoGroupNode.getComments();
        Assert.assertEquals((oneTwoSize - 1), oneThree.size());
        Assert.assertEquals((twoTwoSize - 1), twoThree.size());
        Assert.assertEquals(oneThree, twoThree);
    }

    @Test
    public void testDeleteLocalCodeNodeCommentSync() throws CouldntDeleteException, CouldntLoadDataException, CouldntSaveDataException, InterruptedException {
        final INaviCodeNode databaseOnecodeNode = databaseOneView.getContent().getBasicBlocks().get(2);
        final INaviCodeNode databaseTwocodeNode = databaseTwoView.getContent().getBasicBlocks().get(2);
        final List<IComment> oneBefore = ((databaseOnecodeNode.getComments().getLocalCodeNodeComment()) == null) ? new ArrayList<IComment>() : databaseOnecodeNode.getComments().getLocalCodeNodeComment();
        final List<IComment> twoBefore = ((databaseTwocodeNode.getComments().getLocalCodeNodeComment()) == null) ? new ArrayList<IComment>() : databaseTwocodeNode.getComments().getLocalCodeNodeComment();
        Assert.assertEquals(oneBefore, twoBefore);
        final List<IComment> comments = databaseOnecodeNode.getComments().appendLocalCodeNodeComment(" TEST NOTIFICATION PROVIDER TESTS (LOCAL CODE NODE COMMENT)");
        // The wait is necessary to have the poll function complete and propagate the changes from
        // database one to two over the PostgreSQL back end.
        synchronized(lock) {
            lock.await(1000, TimeUnit.MILLISECONDS);
        }
        final List<IComment> oneAfter = databaseOnecodeNode.getComments().getLocalCodeNodeComment();
        final List<IComment> twoAfter = databaseTwocodeNode.getComments().getLocalCodeNodeComment();
        Assert.assertNotNull(oneAfter);
        Assert.assertNotNull(twoAfter);
        Assert.assertEquals(((oneBefore.size()) + 1), oneAfter.size());
        Assert.assertEquals(((twoBefore.size()) + 1), twoAfter.size());
        Assert.assertEquals(oneAfter, twoAfter);
        final int oneTwoSize = oneAfter.size();
        final int twoTwoSize = twoAfter.size();
        databaseOnecodeNode.getComments().deleteLocalCodeNodeComment(Iterables.getLast(comments));
        // The wait is necessary to have the poll function complete and propagate the changes from
        // database one to two over the PostgreSQL back end.
        synchronized(lock) {
            lock.await(1000, TimeUnit.MILLISECONDS);
        }
        final List<IComment> oneThree = databaseOnecodeNode.getComments().getLocalCodeNodeComment();
        final List<IComment> twoThree = databaseTwocodeNode.getComments().getLocalCodeNodeComment();
        Assert.assertEquals((oneTwoSize - 1), oneThree.size());
        Assert.assertEquals((twoTwoSize - 1), twoThree.size());
        Assert.assertEquals(oneThree, twoThree);
    }

    @Test
    public void testDeleteLocalEdgeCommentSync() throws CouldntDeleteException, CouldntLoadDataException, CouldntSaveDataException, InterruptedException {
        final INaviEdge databaseOneEdge = databaseOneView.getContent().getGraph().getEdges().get(3);
        final INaviEdge databaseTwoEdge = databaseTwoView.getContent().getGraph().getEdges().get(3);
        final List<IComment> oneBefore = ((databaseOneEdge.getLocalComment()) == null) ? new ArrayList<IComment>() : databaseOneEdge.getLocalComment();
        final List<IComment> twoBefore = ((databaseTwoEdge.getLocalComment()) == null) ? new ArrayList<IComment>() : databaseTwoEdge.getLocalComment();
        Assert.assertEquals(oneBefore, twoBefore);
        final List<IComment> comments = databaseOneEdge.appendLocalComment(" TEST NOTIFICATION PROVIDER TESTS (LOCAL CODE NODE COMMENT) ");
        // The wait is necessary to have the poll function complete and propagate the changes from
        // database one to two over the PostgreSQL back end.
        synchronized(lock) {
            lock.await(1000, TimeUnit.MILLISECONDS);
        }
        final List<IComment> oneAfter = databaseOneEdge.getLocalComment();
        final List<IComment> twoAfter = databaseTwoEdge.getLocalComment();
        Assert.assertNotNull(oneAfter);
        Assert.assertNotNull(twoAfter);
        Assert.assertEquals(((oneBefore.size()) + 1), oneAfter.size());
        Assert.assertEquals(((twoBefore.size()) + 1), twoAfter.size());
        Assert.assertEquals(oneAfter, twoAfter);
        final int oneTwoSize = oneAfter.size();
        final int twoTwoSize = twoAfter.size();
        databaseOneEdge.deleteLocalComment(Iterables.getLast(comments));
        // The wait is necessary to have the poll function complete and propagate the changes from
        // database one to two over the PostgreSQL back end.
        synchronized(lock) {
            lock.await(1000, TimeUnit.MILLISECONDS);
        }
        final List<IComment> oneThree = databaseOneEdge.getLocalComment();
        final List<IComment> twoThree = databaseTwoEdge.getLocalComment();
        Assert.assertEquals((oneTwoSize - 1), oneThree.size());
        Assert.assertEquals((twoTwoSize - 1), twoThree.size());
        Assert.assertEquals(oneThree, twoThree);
    }

    @Test
    public void testDeleteLocalInstructionCommentSync() throws CouldntDeleteException, CouldntLoadDataException, CouldntSaveDataException, InterruptedException {
        final INaviCodeNode databaseOnecodeNode = databaseOneView.getContent().getBasicBlocks().get(3);
        final INaviCodeNode databaseTwocodeNode = databaseTwoView.getContent().getBasicBlocks().get(3);
        final List<IComment> oneBefore = ((databaseOnecodeNode.getComments().getLocalInstructionComment(databaseOnecodeNode.getLastInstruction())) == null) ? new ArrayList<IComment>() : databaseOnecodeNode.getComments().getLocalInstructionComment(databaseOnecodeNode.getLastInstruction());
        final List<IComment> twoBefore = ((databaseTwocodeNode.getComments().getLocalInstructionComment(databaseTwocodeNode.getLastInstruction())) == null) ? new ArrayList<IComment>() : databaseTwocodeNode.getComments().getLocalInstructionComment(databaseTwocodeNode.getLastInstruction());
        Assert.assertEquals(oneBefore, twoBefore);
        final List<IComment> comments = databaseOnecodeNode.getComments().appendLocalInstructionComment(databaseOnecodeNode.getLastInstruction(), " TEST NOTIFICATION PROVIDER TESTS (GLOBAL INSTRUCTION COMMENT)");
        // The wait is necessary to have the poll function complete and propagate the changes from
        // database one to two over the PostgreSQL back end.
        synchronized(lock) {
            lock.await(1000, TimeUnit.MILLISECONDS);
        }
        final List<IComment> oneAfter = databaseOnecodeNode.getComments().getLocalInstructionComment(databaseOnecodeNode.getLastInstruction());
        final List<IComment> twoAfter = databaseTwocodeNode.getComments().getLocalInstructionComment(databaseTwocodeNode.getLastInstruction());
        Assert.assertNotNull(oneAfter);
        Assert.assertNotNull(twoAfter);
        Assert.assertEquals(((oneBefore.size()) + 1), oneAfter.size());
        Assert.assertEquals(((twoBefore.size()) + 1), twoAfter.size());
        Assert.assertEquals(oneAfter, twoAfter);
        final int oneTwoSize = oneAfter.size();
        final int twoTwoSize = twoAfter.size();
        databaseOnecodeNode.getComments().deleteLocalInstructionComment(databaseOnecodeNode.getLastInstruction(), Iterables.getLast(comments));
        // The wait is necessary to have the poll function complete and propagate the changes from
        // database one to two over the PostgreSQL back end.
        synchronized(lock) {
            lock.await(1000, TimeUnit.MILLISECONDS);
        }
        final List<IComment> oneThree = databaseOnecodeNode.getComments().getLocalInstructionComment(databaseOnecodeNode.getLastInstruction());
        final List<IComment> twoThree = databaseTwocodeNode.getComments().getLocalInstructionComment(databaseTwocodeNode.getLastInstruction());
        Assert.assertEquals((oneTwoSize - 1), oneThree.size());
        Assert.assertEquals((twoTwoSize - 1), twoThree.size());
        Assert.assertEquals(oneThree, twoThree);
    }

    @Test
    public void testDeleteTextNodeCommentSync() throws CPartialLoadException, CouldntDeleteException, CouldntLoadDataException, CouldntSaveDataException, LoadCancelledException, InterruptedException {
        final CView databaseOneTextNodeView = databaseOneModuleTwo.getContent().getViewContainer().createView(" TEXT NODE TESTING VIEW ", "");
        CViewInserter.insertView(databaseOneView, databaseOneTextNodeView);
        final INaviTextNode databaseOneTextNode = databaseOneTextNodeView.getContent().createTextNode(new ArrayList<IComment>());
        databaseOneTextNodeView.save();
        databaseTwoModuleTwo.close();
        databaseTwoModuleTwo.load();
        databaseTwoView.load();
        final INaviView databaseTwoTextNodeView = Iterables.getLast(databaseTwoModuleTwo.getContent().getViewContainer().getUserViews());
        INaviTextNode databaseTwoTextNode = null;
        Assert.assertEquals(databaseOneTextNodeView.getName(), databaseTwoTextNodeView.getName());
        databaseTwoTextNodeView.load();
        for (final INaviViewNode node : databaseTwoTextNodeView.getContent().getGraph().getNodes()) {
            if (node instanceof INaviTextNode) {
                databaseTwoTextNode = ((INaviTextNode) (node));
            }
        }
        Assert.assertNotNull(databaseTwoTextNode);
        Assert.assertEquals(databaseTwoTextNode.getId(), databaseOneTextNode.getId());
        final List<IComment> comments = databaseOneTextNode.appendComment(" TEST NOTIFICATION PROVIDER TESTS (TEXT NODE COMMENT) ");
        synchronized(lock) {
            lock.await(1000, TimeUnit.MILLISECONDS);
        }
        final List<IComment> oneAfter = databaseOneTextNode.getComments();
        final List<IComment> twoAfter = databaseTwoTextNode.getComments();
        Assert.assertNotNull(oneAfter);
        Assert.assertNotNull(twoAfter);
        Assert.assertEquals(1, oneAfter.size());
        Assert.assertEquals(1, twoAfter.size());
        Assert.assertEquals(oneAfter, twoAfter);
        final int oneTwoSize = oneAfter.size();
        final int twoTwoSize = twoAfter.size();
        databaseOneTextNode.deleteComment(Iterables.getLast(comments));
        // The wait is necessary to have the poll function complete and propagate the changes from
        // database one to two over the PostgreSQL back end.
        synchronized(lock) {
            lock.await(1000, TimeUnit.MILLISECONDS);
        }
        final List<IComment> oneThree = databaseOneTextNode.getComments();
        final List<IComment> twoThree = databaseTwoTextNode.getComments();
        Assert.assertEquals((oneTwoSize - 1), oneThree.size());
        Assert.assertEquals((twoTwoSize - 1), twoThree.size());
        Assert.assertEquals(oneThree, twoThree);
    }

    @Test
    public void testEditFunctionCommentSync() throws CouldntLoadDataException, CouldntSaveDataException, InterruptedException {
        final List<IComment> oneOne = ((databaseOneFunction.getGlobalComment()) == null) ? new ArrayList<IComment>() : databaseOneFunction.getGlobalComment();
        final List<IComment> twoOne = ((databaseTwoFunction.getGlobalComment()) == null) ? new ArrayList<IComment>() : databaseTwoFunction.getGlobalComment();
        Assert.assertEquals(oneOne, twoOne);
        final List<IComment> comments = databaseOneFunction.appendGlobalComment(" TEST NOTIFICATION PROVIDER TESTS (GLOBAL FUNCTION COMMENT) BEFORE ");
        // The wait is necessary to have the poll function complete and propagate the changes from
        // database one to two over the PostgreSQL back end.
        synchronized(lock) {
            lock.await(1000, TimeUnit.MILLISECONDS);
        }
        final List<IComment> oneTwo = databaseOneFunction.getGlobalComment();
        final List<IComment> twoTwo = databaseTwoFunction.getGlobalComment();
        Assert.assertNotNull(oneTwo);
        Assert.assertNotNull(twoTwo);
        Assert.assertEquals(((oneOne.size()) + 1), oneTwo.size());
        Assert.assertEquals(((twoOne.size()) + 1), twoTwo.size());
        Assert.assertEquals(oneTwo, twoTwo);
        final int oneTwoSize = oneTwo.size();
        final int twoTwoSize = twoTwo.size();
        databaseOneFunction.editGlobalComment(Iterables.getLast(comments), " TEST NOTIFICATION PROVIDER TESTS (GLOBAL FUNCTION COMMENT) AFTER ");
        // The wait is necessary to have the poll function complete and propagate the changes from
        // database one to two over the PostgreSQL back end.
        synchronized(lock) {
            lock.await(1000, TimeUnit.MILLISECONDS);
        }
        final List<IComment> oneThree = databaseOneFunction.getGlobalComment();
        final List<IComment> twoThree = databaseTwoFunction.getGlobalComment();
        Assert.assertEquals(oneTwoSize, oneThree.size());
        Assert.assertEquals(twoTwoSize, twoThree.size());
        Assert.assertEquals(oneThree, twoThree);
        Assert.assertEquals(" TEST NOTIFICATION PROVIDER TESTS (GLOBAL FUNCTION COMMENT) AFTER ", Iterables.getLast(oneThree).getComment());
        Assert.assertEquals(" TEST NOTIFICATION PROVIDER TESTS (GLOBAL FUNCTION COMMENT) AFTER ", Iterables.getLast(twoThree).getComment());
    }

    @Test
    public void testEditFunctionNodeCommentSync() throws CPartialLoadException, CouldntLoadDataException, CouldntSaveDataException, LoadCancelledException, InterruptedException {
        databaseOneCallGraph = databaseOneModuleTwo.getContent().getViewContainer().getNativeCallgraphView();
        databaseOneCallGraph.load();
        final INaviFunctionNode databaseOneFunctionNode = ((INaviFunctionNode) (databaseOneCallGraph.getGraph().getNodes().get(1)));
        databaseTwoCallGraph = databaseTwoModuleTwo.getContent().getViewContainer().getNativeCallgraphView();
        databaseTwoCallGraph.load();
        final INaviFunctionNode databaseTwoFunctionNode = ((INaviFunctionNode) (databaseTwoCallGraph.getGraph().getNodes().get(1)));
        final List<IComment> oneBefore = ((databaseOneFunctionNode.getLocalFunctionComment()) == null) ? new ArrayList<IComment>() : databaseOneFunctionNode.getLocalFunctionComment();
        final List<IComment> twoBefore = ((databaseTwoFunctionNode.getLocalFunctionComment()) == null) ? new ArrayList<IComment>() : databaseTwoFunctionNode.getLocalFunctionComment();
        Assert.assertEquals(oneBefore, twoBefore);
        final List<IComment> comments = databaseOneFunctionNode.appendLocalFunctionComment(" TEST NOTIFICATION PROVIDER TESTS (LOCAL FUNCTION NODE COMMENT) BEFORE ");
        // The wait is necessary to have the poll function complete and propagate the changes from
        // database one to two over the PostgreSQL back end.
        synchronized(lock) {
            lock.await(1000, TimeUnit.MILLISECONDS);
        }
        final List<IComment> oneAfter = databaseOneFunctionNode.getLocalFunctionComment();
        final List<IComment> twoAfter = databaseTwoFunctionNode.getLocalFunctionComment();
        Assert.assertNotNull(oneAfter);
        Assert.assertNotNull(twoAfter);
        Assert.assertEquals(((oneBefore.size()) + 1), oneAfter.size());
        Assert.assertEquals(((twoBefore.size()) + 1), twoAfter.size());
        Assert.assertEquals(oneAfter, twoAfter);
        final int oneTwoSize = oneAfter.size();
        final int twoTwoSize = twoAfter.size();
        databaseOneFunctionNode.editLocalFunctionComment(Iterables.getLast(comments), " TEST NOTIFICATION PROVIDER TESTS (LOCAL FUNCTION NODE COMMENT) AFTER ");
        // The wait is necessary to have the poll function complete and propagate the changes from
        // database one to two over the PostgreSQL back end.
        synchronized(lock) {
            lock.await(1000, TimeUnit.MILLISECONDS);
        }
        final List<IComment> oneThree = databaseOneFunctionNode.getLocalFunctionComment();
        final List<IComment> twoThree = databaseTwoFunctionNode.getLocalFunctionComment();
        Assert.assertEquals(oneTwoSize, oneThree.size());
        Assert.assertEquals(twoTwoSize, twoThree.size());
        Assert.assertEquals(oneThree, twoThree);
        Assert.assertEquals(" TEST NOTIFICATION PROVIDER TESTS (LOCAL FUNCTION NODE COMMENT) AFTER ", Iterables.getLast(oneThree).getComment());
        Assert.assertEquals(" TEST NOTIFICATION PROVIDER TESTS (LOCAL FUNCTION NODE COMMENT) AFTER ", Iterables.getLast(twoThree).getComment());
    }

    @Test
    public void testEditGlobalCodeNodeCommentSync() throws CouldntLoadDataException, CouldntSaveDataException, InterruptedException {
        final INaviCodeNode databaseOnecodeNode = databaseOneView.getContent().getBasicBlocks().get(1);
        final INaviCodeNode databaseTwocodeNode = databaseTwoView.getContent().getBasicBlocks().get(1);
        final List<IComment> oneBefore = ((databaseOnecodeNode.getComments().getGlobalCodeNodeComment()) == null) ? new ArrayList<IComment>() : databaseOnecodeNode.getComments().getGlobalCodeNodeComment();
        final List<IComment> twoBefore = ((databaseTwocodeNode.getComments().getGlobalCodeNodeComment()) == null) ? new ArrayList<IComment>() : databaseTwocodeNode.getComments().getGlobalCodeNodeComment();
        Assert.assertEquals(oneBefore, twoBefore);
        databaseOnecodeNode.getComments().appendGlobalCodeNodeComment(" TEST NOTIFICATION PROVIDER TESTS (GLOBAL CODE NODE COMMENT) BEFORE ");
        // The wait is necessary to have the poll function complete and propagate the changes from
        // database one to two over the PostgreSQL back end.
        synchronized(lock) {
            lock.await(1000, TimeUnit.MILLISECONDS);
        }
        final List<IComment> oneAfter = databaseOnecodeNode.getComments().getGlobalCodeNodeComment();
        final List<IComment> twoAfter = databaseTwocodeNode.getComments().getGlobalCodeNodeComment();
        Assert.assertNotNull(oneAfter);
        Assert.assertNotNull(twoAfter);
        Assert.assertEquals(((oneBefore.size()) + 1), oneAfter.size());
        Assert.assertEquals(((twoBefore.size()) + 1), twoAfter.size());
        Assert.assertEquals(oneAfter, twoAfter);
        final int oneTwoSize = oneAfter.size();
        final int twoTwoSize = twoAfter.size();
        databaseOnecodeNode.getComments().editGlobalCodeNodeComment(Iterables.getLast(databaseOnecodeNode.getComments().getGlobalCodeNodeComment()), " TEST NOTIFICATION PROVIDER TESTS (GLOBAL CODE NODE COMMENT) AFTER ");
        // The wait is necessary to have the poll function complete and propagate the changes from
        // database one to two over the PostgreSQL back end.
        synchronized(lock) {
            lock.await(1000, TimeUnit.MILLISECONDS);
        }
        final List<IComment> oneThree = databaseOnecodeNode.getComments().getGlobalCodeNodeComment();
        final List<IComment> twoThree = databaseTwocodeNode.getComments().getGlobalCodeNodeComment();
        Assert.assertEquals(oneTwoSize, oneThree.size());
        Assert.assertEquals(twoTwoSize, twoThree.size());
        Assert.assertEquals(oneThree, twoThree);
        Assert.assertEquals(" TEST NOTIFICATION PROVIDER TESTS (GLOBAL CODE NODE COMMENT) AFTER ", Iterables.getLast(oneThree).getComment());
        Assert.assertEquals(" TEST NOTIFICATION PROVIDER TESTS (GLOBAL CODE NODE COMMENT) AFTER ", Iterables.getLast(twoThree).getComment());
    }

    @Test
    public void testEditGlobalEdgeCommentSync() throws CouldntLoadDataException, CouldntSaveDataException, InterruptedException {
        final INaviEdge databaseOneEdge = databaseOneView.getContent().getGraph().getEdges().get(3);
        final INaviEdge databaseTwoEdge = databaseTwoView.getContent().getGraph().getEdges().get(3);
        final List<IComment> oneBefore = ((databaseOneEdge.getGlobalComment()) == null) ? new ArrayList<IComment>() : databaseOneEdge.getGlobalComment();
        final List<IComment> twoBefore = ((databaseTwoEdge.getGlobalComment()) == null) ? new ArrayList<IComment>() : databaseTwoEdge.getGlobalComment();
        Assert.assertEquals(oneBefore, twoBefore);
        databaseOneEdge.appendGlobalComment(" TEST NOTIFICATION PROVIDER TESTS (GLOBAL EDGE COMMENT) BEFORE ");
        // The wait is necessary to have the poll function complete and propagate the changes from
        // database one to two over the PostgreSQL back end.
        synchronized(lock) {
            lock.await(1000, TimeUnit.MILLISECONDS);
        }
        final List<IComment> oneAfter = databaseOneEdge.getGlobalComment();
        final List<IComment> twoAfter = databaseTwoEdge.getGlobalComment();
        Assert.assertNotNull(oneAfter);
        Assert.assertNotNull(twoAfter);
        Assert.assertEquals(((oneBefore.size()) + 1), oneAfter.size());
        Assert.assertEquals(((twoBefore.size()) + 1), twoAfter.size());
        Assert.assertEquals(oneAfter, twoAfter);
        final int oneTwoSize = oneAfter.size();
        final int twoTwoSize = twoAfter.size();
        databaseOneEdge.editGlobalComment(Iterables.getLast(databaseOneEdge.getGlobalComment()), " TEST NOTIFICATION PROVIDER TESTS (GLOBAL EDGE COMMENT) AFTER ");
        // The wait is necessary to have the poll function complete and propagate the changes from
        // database one to two over the PostgreSQL back end.
        synchronized(lock) {
            lock.await(1000, TimeUnit.MILLISECONDS);
        }
        final List<IComment> oneThree = databaseOneEdge.getGlobalComment();
        final List<IComment> twoThree = databaseTwoEdge.getGlobalComment();
        Assert.assertEquals(oneTwoSize, oneThree.size());
        Assert.assertEquals(twoTwoSize, twoThree.size());
        Assert.assertEquals(oneThree, twoThree);
        Assert.assertEquals(" TEST NOTIFICATION PROVIDER TESTS (GLOBAL EDGE COMMENT) AFTER ", Iterables.getLast(oneThree).getComment());
        Assert.assertEquals(" TEST NOTIFICATION PROVIDER TESTS (GLOBAL EDGE COMMENT) AFTER ", Iterables.getLast(twoThree).getComment());
    }

    @Test
    public void testEditGlobalInstructionCommentSync() throws CouldntLoadDataException, CouldntSaveDataException, InterruptedException {
        final INaviCodeNode databaseOnecodeNode = databaseOneView.getContent().getBasicBlocks().get(3);
        final INaviCodeNode databaseTwocodeNode = databaseTwoView.getContent().getBasicBlocks().get(3);
        final List<IComment> oneBefore = ((Iterables.getFirst(databaseOnecodeNode.getInstructions(), null).getGlobalComment()) == null) ? new ArrayList<IComment>() : Iterables.getFirst(databaseOnecodeNode.getInstructions(), null).getGlobalComment();
        final List<IComment> twoBefore = ((Iterables.getFirst(databaseTwocodeNode.getInstructions(), null).getGlobalComment()) == null) ? new ArrayList<IComment>() : Iterables.getFirst(databaseTwocodeNode.getInstructions(), null).getGlobalComment();
        Assert.assertEquals(oneBefore, twoBefore);
        Iterables.getFirst(databaseOnecodeNode.getInstructions(), null).appendGlobalComment(" TEST NOTIFICATION PROVIDER TESTS (GLOBAL INSTRUCTION COMMENT) BEFORE ");
        // The wait is necessary to have the poll function complete and propagate the changes from
        // database one to two over the PostgreSQL back end.
        synchronized(lock) {
            lock.await(1000, TimeUnit.MILLISECONDS);
        }
        final List<IComment> oneAfter = Iterables.getFirst(databaseOnecodeNode.getInstructions(), null).getGlobalComment();
        final List<IComment> twoAfter = Iterables.getFirst(databaseTwocodeNode.getInstructions(), null).getGlobalComment();
        Assert.assertNotNull(oneAfter);
        Assert.assertNotNull(twoAfter);
        Assert.assertEquals(((oneBefore.size()) + 1), oneAfter.size());
        Assert.assertEquals(((twoBefore.size()) + 1), twoAfter.size());
        Assert.assertEquals(oneAfter, twoAfter);
        final int oneTwoSize = oneAfter.size();
        final int twoTwoSize = twoAfter.size();
        Iterables.getFirst(databaseOnecodeNode.getInstructions(), null).editGlobalComment(Iterables.getLast(Iterables.getFirst(databaseOnecodeNode.getInstructions(), null).getGlobalComment()), " TEST NOTIFICATION PROVIDER TESTS (GLOBAL INSTRUCTION COMMENT) AFTER ");
        // The wait is necessary to have the poll function complete and propagate the changes from
        // database one to two over the PostgreSQL back end.
        synchronized(lock) {
            lock.await(1000, TimeUnit.MILLISECONDS);
        }
        final List<IComment> oneThree = Iterables.getFirst(databaseOnecodeNode.getInstructions(), null).getGlobalComment();
        final List<IComment> twoThree = Iterables.getFirst(databaseTwocodeNode.getInstructions(), null).getGlobalComment();
        Assert.assertEquals(oneTwoSize, oneThree.size());
        Assert.assertEquals(twoTwoSize, twoThree.size());
        Assert.assertEquals(oneThree, twoThree);
        Assert.assertEquals(" TEST NOTIFICATION PROVIDER TESTS (GLOBAL INSTRUCTION COMMENT) AFTER ", Iterables.getLast(oneThree).getComment());
        Assert.assertEquals(" TEST NOTIFICATION PROVIDER TESTS (GLOBAL INSTRUCTION COMMENT) AFTER ", Iterables.getLast(twoThree).getComment());
    }

    @Test
    public void testEditGroupNodeComment() throws CPartialLoadException, CouldntLoadDataException, CouldntSaveDataException, LoadCancelledException, InterruptedException {
        final CView databaseOneGroupNodeView = databaseOneModuleTwo.getContent().getViewContainer().createView(" GROUP NODE TESTING VIEW ", "");
        CViewInserter.insertView(databaseOneView, databaseOneGroupNodeView);
        final INaviGroupNode databaseOneGroupNode = databaseOneGroupNodeView.getContent().createGroupNode(databaseOneGroupNodeView.getGraph().getNodes());
        databaseOneGroupNodeView.save();
        databaseTwoModuleTwo.close();
        databaseTwoModuleTwo.load();
        databaseTwoView.load();
        final INaviView databaseTwoGroupNodeView = Iterables.getLast(databaseTwoModuleTwo.getContent().getViewContainer().getUserViews());
        INaviGroupNode databaseTwoGroupNode = null;
        Assert.assertEquals(databaseOneGroupNodeView.getName(), databaseTwoGroupNodeView.getName());
        databaseTwoGroupNodeView.load();
        for (final INaviViewNode node : databaseTwoGroupNodeView.getContent().getGraph().getNodes()) {
            if (node instanceof INaviGroupNode) {
                databaseTwoGroupNode = ((INaviGroupNode) (node));
            }
        }
        Assert.assertNotNull(databaseTwoGroupNode);
        Assert.assertEquals(databaseTwoGroupNode.getId(), databaseOneGroupNode.getId());
        final List<IComment> comments = databaseOneGroupNode.appendComment(" TEST NOTIFICATION PROVIDER TESTS (GROUP NODE COMMENT) BEFORE ");
        synchronized(lock) {
            lock.await(1000, TimeUnit.MILLISECONDS);
        }
        final List<IComment> oneAfter = databaseOneGroupNode.getComments();
        final List<IComment> twoAfter = databaseTwoGroupNode.getComments();
        Assert.assertNotNull(oneAfter);
        Assert.assertNotNull(twoAfter);
        Assert.assertEquals(1, oneAfter.size());
        Assert.assertEquals(1, twoAfter.size());
        Assert.assertEquals(oneAfter, twoAfter);
        final int oneTwoSize = oneAfter.size();
        final int twoTwoSize = twoAfter.size();
        databaseOneGroupNode.editComment(Iterables.getLast(comments), " TEST NOTIFICATION PROVIDER TESTS (GROUP NODE COMMENT) AFTER ");
        // The wait is necessary to have the poll function complete and propagate the changes from
        // database one to two over the PostgreSQL back end.
        synchronized(lock) {
            lock.await(1000, TimeUnit.MILLISECONDS);
        }
        final List<IComment> oneThree = databaseOneGroupNode.getComments();
        final List<IComment> twoThree = databaseTwoGroupNode.getComments();
        Assert.assertEquals(oneTwoSize, oneThree.size());
        Assert.assertEquals(twoTwoSize, twoThree.size());
        Assert.assertEquals(oneThree, twoThree);
        Assert.assertEquals(" TEST NOTIFICATION PROVIDER TESTS (GROUP NODE COMMENT) AFTER ", Iterables.getLast(oneThree).getComment());
        Assert.assertEquals(" TEST NOTIFICATION PROVIDER TESTS (GROUP NODE COMMENT) AFTER ", Iterables.getLast(twoThree).getComment());
    }

    @Test
    public void testEditLocalCodeNodeCommentSync() throws CouldntLoadDataException, CouldntSaveDataException, InterruptedException {
        final INaviCodeNode databaseOnecodeNode = databaseOneView.getContent().getBasicBlocks().get(2);
        final INaviCodeNode databaseTwocodeNode = databaseTwoView.getContent().getBasicBlocks().get(2);
        final List<IComment> oneBefore = ((databaseOnecodeNode.getComments().getLocalCodeNodeComment()) == null) ? new ArrayList<IComment>() : databaseOnecodeNode.getComments().getLocalCodeNodeComment();
        final List<IComment> twoBefore = ((databaseTwocodeNode.getComments().getLocalCodeNodeComment()) == null) ? new ArrayList<IComment>() : databaseTwocodeNode.getComments().getLocalCodeNodeComment();
        Assert.assertEquals(oneBefore, twoBefore);
        final List<IComment> comments = databaseOnecodeNode.getComments().appendLocalCodeNodeComment(" TEST NOTIFICATION PROVIDER TESTS (LOCAL CODE NODE COMMENT) BEFORE ");
        // The wait is necessary to have the poll function complete and propagate the changes from
        // database one to two over the PostgreSQL back end.
        synchronized(lock) {
            lock.await(1000, TimeUnit.MILLISECONDS);
        }
        final List<IComment> oneAfter = databaseOnecodeNode.getComments().getLocalCodeNodeComment();
        final List<IComment> twoAfter = databaseTwocodeNode.getComments().getLocalCodeNodeComment();
        Assert.assertNotNull(oneAfter);
        Assert.assertNotNull(twoAfter);
        Assert.assertEquals(((oneBefore.size()) + 1), oneAfter.size());
        Assert.assertEquals(((twoBefore.size()) + 1), twoAfter.size());
        Assert.assertEquals(oneAfter, twoAfter);
        final int oneTwoSize = oneAfter.size();
        final int twoTwoSize = twoAfter.size();
        databaseOnecodeNode.getComments().editLocalCodeNodeComment(Iterables.getLast(comments), " TEST NOTIFICATION PROVIDER TESTS (LOCAL CODE NODE COMMENT) AFTER ");
        // The wait is necessary to have the poll function complete and propagate the changes from
        // database one to two over the PostgreSQL back end.
        synchronized(lock) {
            lock.await(1000, TimeUnit.MILLISECONDS);
        }
        final List<IComment> oneThree = databaseOnecodeNode.getComments().getLocalCodeNodeComment();
        final List<IComment> twoThree = databaseTwocodeNode.getComments().getLocalCodeNodeComment();
        Assert.assertEquals(oneTwoSize, oneThree.size());
        Assert.assertEquals(twoTwoSize, twoThree.size());
        Assert.assertEquals(oneThree, twoThree);
        Assert.assertEquals(" TEST NOTIFICATION PROVIDER TESTS (LOCAL CODE NODE COMMENT) AFTER ", Iterables.getLast(oneThree).getComment());
        Assert.assertEquals(" TEST NOTIFICATION PROVIDER TESTS (LOCAL CODE NODE COMMENT) AFTER ", Iterables.getLast(twoThree).getComment());
    }

    @Test
    public void testEditLocalEdgeCommentSync() throws CouldntLoadDataException, CouldntSaveDataException, InterruptedException {
        final INaviEdge databaseOneEdge = databaseOneView.getContent().getGraph().getEdges().get(3);
        final INaviEdge databaseTwoEdge = databaseTwoView.getContent().getGraph().getEdges().get(3);
        final List<IComment> oneBefore = ((databaseOneEdge.getLocalComment()) == null) ? new ArrayList<IComment>() : databaseOneEdge.getLocalComment();
        final List<IComment> twoBefore = ((databaseTwoEdge.getLocalComment()) == null) ? new ArrayList<IComment>() : databaseTwoEdge.getLocalComment();
        Assert.assertEquals(oneBefore, twoBefore);
        final List<IComment> comments = databaseOneEdge.appendLocalComment(" TEST NOTIFICATION PROVIDER TESTS (LOCAL EDGE COMMENT) BEFORE ");
        // The wait is necessary to have the poll function complete and propagate the changes from
        // database one to two over the PostgreSQL back end.
        synchronized(lock) {
            lock.await(1000, TimeUnit.MILLISECONDS);
        }
        final List<IComment> oneAfter = databaseOneEdge.getLocalComment();
        final List<IComment> twoAfter = databaseTwoEdge.getLocalComment();
        Assert.assertNotNull(oneAfter);
        Assert.assertNotNull(twoAfter);
        Assert.assertEquals(((oneBefore.size()) + 1), oneAfter.size());
        Assert.assertEquals(((twoBefore.size()) + 1), twoAfter.size());
        Assert.assertEquals(oneAfter, twoAfter);
        final int oneTwoSize = oneAfter.size();
        final int twoTwoSize = twoAfter.size();
        databaseOneEdge.editLocalComment(Iterables.getLast(comments), " TEST NOTIFICATION PROVIDER TESTS (LOCAL EDGE COMMENT) AFTER ");
        // The wait is necessary to have the poll function complete and propagate the changes from
        // database one to two over the PostgreSQL back end.
        synchronized(lock) {
            lock.await(1000, TimeUnit.MILLISECONDS);
        }
        final List<IComment> oneThree = databaseOneEdge.getLocalComment();
        final List<IComment> twoThree = databaseTwoEdge.getLocalComment();
        Assert.assertEquals(oneTwoSize, oneThree.size());
        Assert.assertEquals(twoTwoSize, twoThree.size());
        Assert.assertEquals(oneThree, twoThree);
        Assert.assertEquals(" TEST NOTIFICATION PROVIDER TESTS (LOCAL EDGE COMMENT) AFTER ", Iterables.getLast(oneThree).getComment());
        Assert.assertEquals(" TEST NOTIFICATION PROVIDER TESTS (LOCAL EDGE COMMENT) AFTER ", Iterables.getLast(twoThree).getComment());
    }

    @Test
    public void testEditLocalInstructionCommentSync() throws CouldntLoadDataException, CouldntSaveDataException, InterruptedException {
        final INaviCodeNode databaseOnecodeNode = databaseOneView.getContent().getBasicBlocks().get(3);
        final INaviCodeNode databaseTwocodeNode = databaseTwoView.getContent().getBasicBlocks().get(3);
        final List<IComment> oneBefore = ((databaseOnecodeNode.getComments().getLocalInstructionComment(databaseOnecodeNode.getLastInstruction())) == null) ? new ArrayList<IComment>() : databaseOnecodeNode.getComments().getLocalInstructionComment(databaseOnecodeNode.getLastInstruction());
        final List<IComment> twoBefore = ((databaseTwocodeNode.getComments().getLocalInstructionComment(databaseTwocodeNode.getLastInstruction())) == null) ? new ArrayList<IComment>() : databaseTwocodeNode.getComments().getLocalInstructionComment(databaseTwocodeNode.getLastInstruction());
        Assert.assertEquals(oneBefore, twoBefore);
        final List<IComment> comments = databaseOnecodeNode.getComments().appendLocalInstructionComment(databaseOnecodeNode.getLastInstruction(), " TEST NOTIFICATION PROVIDER TESTS (GLOBAL INSTRUCTION COMMENT) BEFORE ");
        // The wait is necessary to have the poll function complete and propagate the changes from
        // database one to two over the PostgreSQL back end.
        synchronized(lock) {
            lock.await(1000, TimeUnit.MILLISECONDS);
        }
        final List<IComment> oneAfter = databaseOnecodeNode.getComments().getLocalInstructionComment(databaseOnecodeNode.getLastInstruction());
        final List<IComment> twoAfter = databaseTwocodeNode.getComments().getLocalInstructionComment(databaseTwocodeNode.getLastInstruction());
        Assert.assertNotNull(oneAfter);
        Assert.assertNotNull(twoAfter);
        Assert.assertEquals(((oneBefore.size()) + 1), oneAfter.size());
        Assert.assertEquals(((twoBefore.size()) + 1), twoAfter.size());
        Assert.assertEquals(oneAfter, twoAfter);
        final int oneTwoSize = oneAfter.size();
        final int twoTwoSize = twoAfter.size();
        databaseOnecodeNode.getComments().editLocalInstructionComment(databaseOnecodeNode.getLastInstruction(), Iterables.getLast(comments), " TEST NOTIFICATION PROVIDER TESTS (GLOBAL INSTRUCTION COMMENT) AFTER ");
        // The wait is necessary to have the poll function complete and propagate the changes from
        // database one to two over the PostgreSQL back end.
        synchronized(lock) {
            lock.await(1000, TimeUnit.MILLISECONDS);
        }
        final List<IComment> oneThree = databaseOnecodeNode.getComments().getLocalInstructionComment(databaseOnecodeNode.getLastInstruction());
        final List<IComment> twoThree = databaseTwocodeNode.getComments().getLocalInstructionComment(databaseTwocodeNode.getLastInstruction());
        Assert.assertEquals(oneTwoSize, oneThree.size());
        Assert.assertEquals(twoTwoSize, twoThree.size());
        Assert.assertEquals(oneThree, twoThree);
        Assert.assertEquals(" TEST NOTIFICATION PROVIDER TESTS (GLOBAL INSTRUCTION COMMENT) AFTER ", Iterables.getLast(oneThree).getComment());
        Assert.assertEquals(" TEST NOTIFICATION PROVIDER TESTS (GLOBAL INSTRUCTION COMMENT) AFTER ", Iterables.getLast(twoThree).getComment());
    }

    @Test
    public void testEditTextNodeCommentSync() throws CPartialLoadException, CouldntLoadDataException, CouldntSaveDataException, LoadCancelledException, InterruptedException {
        final CView databaseOneTextNodeView = databaseOneModuleTwo.getContent().getViewContainer().createView(" TEXT NODE TESTING VIEW ", "");
        CViewInserter.insertView(databaseOneView, databaseOneTextNodeView);
        final INaviTextNode databaseOneTextNode = databaseOneTextNodeView.getContent().createTextNode(new ArrayList<IComment>());
        databaseOneTextNodeView.save();
        databaseTwoModuleTwo.close();
        databaseTwoModuleTwo.load();
        databaseTwoView.load();
        final INaviView databaseTwoTextNodeView = Iterables.getLast(databaseTwoModuleTwo.getContent().getViewContainer().getUserViews());
        INaviTextNode databaseTwoTextNode = null;
        Assert.assertEquals(databaseOneTextNodeView.getName(), databaseTwoTextNodeView.getName());
        databaseTwoTextNodeView.load();
        for (final INaviViewNode node : databaseTwoTextNodeView.getContent().getGraph().getNodes()) {
            if (node instanceof INaviTextNode) {
                databaseTwoTextNode = ((INaviTextNode) (node));
            }
        }
        Assert.assertNotNull(databaseTwoTextNode);
        Assert.assertEquals(databaseTwoTextNode.getId(), databaseOneTextNode.getId());
        final List<IComment> comments = databaseOneTextNode.appendComment(" TEST NOTIFICATION PROVIDER TESTS (TEXT NODE COMMENT) BEFORE ");
        synchronized(lock) {
            lock.await(1000, TimeUnit.MILLISECONDS);
        }
        final List<IComment> oneAfter = databaseOneTextNode.getComments();
        final List<IComment> twoAfter = databaseTwoTextNode.getComments();
        Assert.assertNotNull(oneAfter);
        Assert.assertNotNull(twoAfter);
        Assert.assertEquals(1, oneAfter.size());
        Assert.assertEquals(1, twoAfter.size());
        Assert.assertEquals(oneAfter, twoAfter);
        final int oneTwoSize = oneAfter.size();
        final int twoTwoSize = twoAfter.size();
        databaseOneTextNode.editComment(Iterables.getLast(comments), " TEST NOTIFICATION PROVIDER TESTS (TEXT NODE COMMENT) AFTER ");
        // The wait is necessary to have the poll function complete and propagate the changes from
        // database one to two over the PostgreSQL back end.
        synchronized(lock) {
            lock.await(1000, TimeUnit.MILLISECONDS);
        }
        final List<IComment> oneThree = databaseOneTextNode.getComments();
        final List<IComment> twoThree = databaseTwoTextNode.getComments();
        Assert.assertEquals(oneTwoSize, oneThree.size());
        Assert.assertEquals(twoTwoSize, twoThree.size());
        Assert.assertEquals(oneThree, twoThree);
        Assert.assertEquals(" TEST NOTIFICATION PROVIDER TESTS (TEXT NODE COMMENT) AFTER ", Iterables.getLast(oneThree).getComment());
        Assert.assertEquals(" TEST NOTIFICATION PROVIDER TESTS (TEXT NODE COMMENT) AFTER ", Iterables.getLast(twoThree).getComment());
    }
}

