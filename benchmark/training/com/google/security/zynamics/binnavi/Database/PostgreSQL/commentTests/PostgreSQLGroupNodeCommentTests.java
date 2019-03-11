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
package com.google.security.zynamics.binnavi.Database.PostgreSQL.commentTests;


import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.security.zynamics.binnavi.Database.Exceptions.CPartialLoadException;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntDeleteException;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntLoadDataException;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntSaveDataException;
import com.google.security.zynamics.binnavi.Database.Exceptions.LoadCancelledException;
import com.google.security.zynamics.binnavi.Database.PostgreSQL.Functions.PostgreSQLCommentFunctions;
import com.google.security.zynamics.binnavi.Database.PostgreSQL.UniqueTestUserGenerator;
import com.google.security.zynamics.binnavi.Exceptions.MaybeNullException;
import com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.Interfaces.IComment;
import com.google.security.zynamics.binnavi.Gui.Users.Interfaces.IUser;
import com.google.security.zynamics.binnavi.disassembly.INaviFunction;
import com.google.security.zynamics.binnavi.disassembly.INaviGroupNode;
import com.google.security.zynamics.binnavi.disassembly.INaviInstruction;
import com.google.security.zynamics.binnavi.disassembly.INaviModule;
import com.google.security.zynamics.binnavi.disassembly.INaviViewNode;
import com.google.security.zynamics.binnavi.disassembly.types.ExpensiveBaseTest;
import com.google.security.zynamics.binnavi.disassembly.views.INaviView;
import com.google.security.zynamics.binnavi.yfileswrap.Gui.GraphWindows.Loader.CGraphBuilder;
import com.google.security.zynamics.binnavi.yfileswrap.zygraph.ZyGraph;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class PostgreSQLGroupNodeCommentTests extends ExpensiveBaseTest {
    @Test(expected = NullPointerException.class)
    public void appendGroupNodeComment1() throws CouldntSaveDataException {
        getProvider().appendGroupNodeComment(null, null, null);
    }

    @Test(expected = NullPointerException.class)
    public void appendGroupNodeComment2() throws CPartialLoadException, CouldntLoadDataException, CouldntSaveDataException, LoadCancelledException, MaybeNullException {
        final INaviGroupNode groupNode = setupGroupNode();
        getProvider().appendGroupNodeComment(groupNode, null, null);
    }

    @Test(expected = NullPointerException.class)
    public void appendGroupNodeComment3() throws CPartialLoadException, CouldntLoadDataException, CouldntSaveDataException, LoadCancelledException, MaybeNullException {
        final INaviGroupNode groupNode = setupGroupNode();
        getProvider().appendGroupNodeComment(groupNode, " FAIL ", null);
    }

    @Test
    public void appendGroupNodeComment4() throws CPartialLoadException, CouldntLoadDataException, CouldntSaveDataException, LoadCancelledException, MaybeNullException {
        final INaviGroupNode groupNode = setupGroupNode();
        final IUser user = new UniqueTestUserGenerator(getProvider()).nextActiveUser();
        final String firstCommentString = " APPEND GROUP NODE COMMENT WITHOUT PARENT ID ";
        final Integer firstCommentId = getProvider().appendGroupNodeComment(groupNode, firstCommentString, user.getUserId());
        final IComment firstComment = new com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.CComment(firstCommentId, user, null, firstCommentString);
        final String secondCommentString = " APPEND GROUP NODE COMMENT WITH PARENT ID ";
        final Integer secondCommentId = getProvider().appendGroupNodeComment(groupNode, secondCommentString, user.getUserId());
        final IComment secondComment = new com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.CComment(secondCommentId, user, firstComment, secondCommentString);
        final ArrayList<IComment> commentsFromDatabase = getProvider().loadCommentById(secondCommentId);
        Assert.assertNotNull(commentsFromDatabase);
        Assert.assertEquals(2, commentsFromDatabase.size());
        Assert.assertTrue(commentsFromDatabase.contains(firstComment));
        Assert.assertTrue(commentsFromDatabase.contains(secondComment));
        // TODO (timkornau): check if the association to the group node did also work and not just the
        // appending under the comment id.
    }

    @Test
    public void appendInstructionCommentInGroupNode() throws CPartialLoadException, CouldntLoadDataException, CouldntSaveDataException, LoadCancelledException, MaybeNullException {
        final INaviModule module = getProvider().loadModules().get(0);
        module.load();
        final INaviFunction function = module.getContent().getFunctionContainer().getFunction("sub_1004565");
        final INaviView view = module.getContent().getViewContainer().getView(function);
        view.load();
        Assert.assertEquals(42, view.getNodeCount());
        final INaviViewNode node1 = view.getContent().getBasicBlocks().get(1);
        final INaviViewNode node2 = view.getContent().getBasicBlocks().get(2);
        view.getContent().createGroupNode(Lists.newArrayList(node1, node2));
        final ZyGraph graph = CGraphBuilder.buildGraph(view);
        final INaviView nonNativeView = graph.saveAs(new com.google.security.zynamics.binnavi.disassembly.Modules.CModuleContainer(getDatabase(), module), " TEST INSTRUCTION COMMENTS IN GROUP NODE ", " TESTING GROUP NODE COMMENTS ");
        final INaviInstruction instruction = view.getContent().getBasicBlocks().get(0).getLastInstruction();
        final IUser user = new UniqueTestUserGenerator(getProvider()).nextActiveUser();
        final String firstCommentString = "TEST INSTRUCTION COMMENT PROPAGATION";
        final int firstCommentId = getProvider().appendGlobalInstructionComment(instruction, firstCommentString, user.getUserId());
        final IComment firstComment = new com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.CComment(firstCommentId, user, null, firstCommentString);
        final ArrayList<IComment> commentsFromDatabase = getProvider().loadCommentById(firstCommentId);
        Assert.assertNotNull(commentsFromDatabase);
        Assert.assertEquals(1, commentsFromDatabase.size());
        Assert.assertTrue(commentsFromDatabase.contains(firstComment));
        final INaviInstruction instruction2 = nonNativeView.getBasicBlocks().get(0).getLastInstruction();
        Assert.assertEquals(1, instruction2.getGlobalComment().size());
    }

    @Test(expected = NullPointerException.class)
    public void deleteGroupNodeComment1() throws CouldntDeleteException {
        getProvider().deleteGroupNodeComment(null, null, null);
    }

    @Test(expected = NullPointerException.class)
    public void deleteGroupNodeComment2() throws CPartialLoadException, CouldntDeleteException, CouldntLoadDataException, CouldntSaveDataException, LoadCancelledException, MaybeNullException {
        final INaviGroupNode groupNode = setupGroupNode();
        getProvider().deleteGroupNodeComment(groupNode, null, null);
    }

    @Test(expected = NullPointerException.class)
    public void deleteGroupNodeComment3() throws CPartialLoadException, CouldntDeleteException, CouldntLoadDataException, CouldntSaveDataException, LoadCancelledException, MaybeNullException {
        final INaviGroupNode groupNode = setupGroupNode();
        getProvider().deleteGroupNodeComment(groupNode, 1, null);
    }

    @Test
    public void deleteGroupNodeComment4() throws CPartialLoadException, CouldntDeleteException, CouldntLoadDataException, CouldntSaveDataException, LoadCancelledException, MaybeNullException {
        final INaviGroupNode groupNode = setupGroupNode();
        final List<IComment> comments = ((groupNode.getComments()) == null) ? new ArrayList<IComment>() : groupNode.getComments();
        final IComment lastComment = ((comments.size()) == 0) ? null : Iterables.getLast(comments);
        final String commentString = " TEST DELETE GROUP NODE COMMENT ";
        final IUser user = new UniqueTestUserGenerator(getProvider()).nextActiveUser();
        final int commentId = getProvider().appendGroupNodeComment(groupNode, commentString, user.getUserId());
        final IComment newComment = new com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.CComment(commentId, user, lastComment, commentString);
        final ArrayList<IComment> storedComments = getProvider().loadCommentById(commentId);
        Assert.assertNotNull(storedComments);
        Assert.assertEquals(((comments.size()) + 1), storedComments.size());
        Assert.assertEquals(newComment, Iterables.getLast(storedComments));
        getProvider().deleteGroupNodeComment(groupNode, commentId, user.getUserId());
        final ArrayList<IComment> commentsAfterDelete = getProvider().loadCommentById(commentId);
        Assert.assertNotNull(commentsAfterDelete);
        Assert.assertTrue(commentsAfterDelete.isEmpty());
    }

    @Test(expected = CouldntDeleteException.class)
    public void deleteGroupNodeComment5() throws CPartialLoadException, CouldntDeleteException, CouldntLoadDataException, CouldntSaveDataException, LoadCancelledException, MaybeNullException {
        final INaviGroupNode groupNode = setupGroupNode();
        final List<IComment> comments = ((groupNode.getComments()) == null) ? new ArrayList<IComment>() : groupNode.getComments();
        final IComment lastComment = ((comments.size()) == 0) ? null : Iterables.getLast(comments);
        final String commentString = " TEST DELETE GROUP NODE COMMENT WRONG USER ";
        final IUser user = new UniqueTestUserGenerator(getProvider()).nextActiveUser();
        final int commentId = getProvider().appendGroupNodeComment(groupNode, commentString, user.getUserId());
        final IComment newComment = new com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.CComment(commentId, user, lastComment, commentString);
        final ArrayList<IComment> storedComments = getProvider().loadCommentById(commentId);
        Assert.assertNotNull(storedComments);
        Assert.assertEquals(((comments.size()) + 1), storedComments.size());
        Assert.assertEquals(newComment, Iterables.getLast(storedComments));
        final IUser wrongUser = new UniqueTestUserGenerator(getProvider()).nextActiveUser();
        getProvider().deleteGroupNodeComment(groupNode, commentId, wrongUser.getUserId());
    }

    /**
     * This test checks if the delete of a comment in a series of comments works if the comment is the
     * last comment.
     *
     * <pre>
     * Comment 1:      Comment 1:
     * Comment 2:  ->  Comment 2:
     * Comment 3:
     * </pre>
     */
    @Test
    public void deleteGroupNodeComment6() throws CPartialLoadException, CouldntDeleteException, CouldntLoadDataException, CouldntSaveDataException, LoadCancelledException, MaybeNullException {
        final INaviGroupNode groupNode = setupGroupNode();
        final List<IComment> storedComments = ((groupNode.getComments()) == null) ? new ArrayList<IComment>() : groupNode.getComments();
        final IComment lastComment = ((storedComments.size()) == 0) ? null : Iterables.getLast(storedComments);
        final IUser user = new UniqueTestUserGenerator(getProvider()).nextActiveUser();
        final String comment1String = " Comment 1: ";
        final int comment1Id = getProvider().appendGroupNodeComment(groupNode, comment1String, user.getUserId());
        final IComment comment1 = new com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.CComment(comment1Id, user, lastComment, comment1String);
        final String comment2String = " Comment 2: ";
        final int comment2Id = getProvider().appendGroupNodeComment(groupNode, comment2String, user.getUserId());
        final IComment comment2 = new com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.CComment(comment2Id, user, comment1, comment2String);
        final String comment3String = " Comment 3: ";
        final int comment3Id = getProvider().appendGroupNodeComment(groupNode, comment3String, user.getUserId());
        final IComment comment3 = new com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.CComment(comment3Id, user, comment2, comment3String);
        final ArrayList<IComment> commentsBeforeDelete = getProvider().loadCommentById(comment3Id);
        Assert.assertNotNull(commentsBeforeDelete);
        Assert.assertEquals(((storedComments.size()) + 3), commentsBeforeDelete.size());
        Assert.assertTrue(commentsBeforeDelete.contains(comment1));
        Assert.assertTrue(commentsBeforeDelete.contains(comment2));
        Assert.assertTrue(commentsBeforeDelete.contains(comment3));
        Assert.assertEquals(comment3, Iterables.getLast(commentsBeforeDelete));
        Assert.assertEquals(comment2, commentsBeforeDelete.get(((commentsBeforeDelete.size()) - 2)));
        Assert.assertEquals(comment1, commentsBeforeDelete.get(((commentsBeforeDelete.size()) - 3)));
        getProvider().deleteGroupNodeComment(groupNode, comment3Id, user.getUserId());
        final ArrayList<IComment> commentsAfterDelete1 = getProvider().loadCommentById(comment3Id);
        Assert.assertNotNull(commentsAfterDelete1);
        Assert.assertTrue(commentsAfterDelete1.isEmpty());
        final ArrayList<IComment> commentsAfterDelete2 = getProvider().loadCommentById(comment2Id);
        Assert.assertNotNull(commentsAfterDelete2);
        Assert.assertEquals(((storedComments.size()) + 2), commentsAfterDelete2.size());
        Assert.assertTrue(commentsAfterDelete2.contains(comment2));
        Assert.assertTrue(commentsAfterDelete2.contains(comment1));
        Assert.assertEquals(comment2, Iterables.getLast(commentsAfterDelete2));
        Assert.assertEquals(comment1, commentsAfterDelete2.get(((commentsAfterDelete2.size()) - 2)));
    }

    /**
     * This test checks if the delete of a comment in a series of comments works if the comment is a
     * comment in the middle.
     *
     * <pre>
     * Comment 1:      Comment 1:
     * Comment 2:  ->
     * Comment 3:      Comment 3:
     * </pre>
     */
    @Test
    public void deleteGroupNodeComment7() throws CPartialLoadException, CouldntDeleteException, CouldntLoadDataException, CouldntSaveDataException, LoadCancelledException, MaybeNullException {
        final INaviGroupNode groupNode = setupGroupNode();
        final List<IComment> storedComments = ((groupNode.getComments()) == null) ? new ArrayList<IComment>() : groupNode.getComments();
        final IComment lastComment = ((storedComments.size()) == 0) ? null : Iterables.getLast(storedComments);
        final IUser user = new UniqueTestUserGenerator(getProvider()).nextActiveUser();
        final String comment1String = " Comment 1: ";
        final int comment1Id = getProvider().appendGroupNodeComment(groupNode, comment1String, user.getUserId());
        final IComment comment1 = new com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.CComment(comment1Id, user, lastComment, comment1String);
        final String comment2String = " Comment 2: ";
        final int comment2Id = getProvider().appendGroupNodeComment(groupNode, comment2String, user.getUserId());
        final IComment comment2 = new com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.CComment(comment2Id, user, comment1, comment2String);
        final String comment3String = " Comment 3: ";
        final int comment3Id = getProvider().appendGroupNodeComment(groupNode, comment3String, user.getUserId());
        final IComment comment3 = new com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.CComment(comment3Id, user, comment2, comment3String);
        final ArrayList<IComment> commentsBeforeDelete = getProvider().loadCommentById(comment3Id);
        Assert.assertNotNull(commentsBeforeDelete);
        Assert.assertEquals(((storedComments.size()) + 3), commentsBeforeDelete.size());
        Assert.assertTrue(commentsBeforeDelete.contains(comment1));
        Assert.assertTrue(commentsBeforeDelete.contains(comment2));
        Assert.assertTrue(commentsBeforeDelete.contains(comment3));
        Assert.assertEquals(comment3, Iterables.getLast(commentsBeforeDelete));
        Assert.assertEquals(comment2, commentsBeforeDelete.get(((commentsBeforeDelete.size()) - 2)));
        Assert.assertEquals(comment1, commentsBeforeDelete.get(((commentsBeforeDelete.size()) - 3)));
        getProvider().deleteGroupNodeComment(groupNode, comment2Id, user.getUserId());
        final ArrayList<IComment> commentsAfterDelete1 = getProvider().loadCommentById(comment2Id);
        Assert.assertNotNull(commentsAfterDelete1);
        Assert.assertTrue(commentsAfterDelete1.isEmpty());
        final ArrayList<IComment> commentsAfterDelete2 = getProvider().loadCommentById(comment3Id);
        Assert.assertNotNull(commentsAfterDelete2);
        Assert.assertEquals(((storedComments.size()) + 2), commentsAfterDelete2.size());
        final IComment comment3AfterDelete = new com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.CComment(comment3Id, user, comment1, comment3String);
        Assert.assertTrue(commentsAfterDelete2.contains(comment3AfterDelete));
        Assert.assertTrue(commentsAfterDelete2.contains(comment1));
        Assert.assertEquals(comment3AfterDelete, Iterables.getLast(commentsAfterDelete2));
        Assert.assertEquals(comment1, commentsAfterDelete2.get(((commentsAfterDelete2.size()) - 2)));
    }

    /**
     * This test checks if the delete of a comment in a series of comments works if the comment is the
     * first comment.
     *
     * <pre>
     * Comment 1:
     * Comment 2:  ->  Comment 2:
     * Comment 3:      Comment 3:
     * </pre>
     */
    @Test
    public void deleteGroupNodeComment8() throws CPartialLoadException, CouldntDeleteException, CouldntLoadDataException, CouldntSaveDataException, LoadCancelledException, MaybeNullException {
        final INaviGroupNode groupNode = setupGroupNode();
        final List<IComment> storedComments = ((groupNode.getComments()) == null) ? new ArrayList<IComment>() : groupNode.getComments();
        final IComment lastComment = ((storedComments.size()) == 0) ? null : Iterables.getLast(storedComments);
        final IUser user = new UniqueTestUserGenerator(getProvider()).nextActiveUser();
        final String comment1String = " Comment 1: ";
        final int comment1Id = getProvider().appendGroupNodeComment(groupNode, comment1String, user.getUserId());
        final IComment comment1 = new com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.CComment(comment1Id, user, lastComment, comment1String);
        final String comment2String = " Comment 2: ";
        final int comment2Id = getProvider().appendGroupNodeComment(groupNode, comment2String, user.getUserId());
        final IComment comment2 = new com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.CComment(comment2Id, user, comment1, comment2String);
        final String comment3String = " Comment 3: ";
        final int comment3Id = getProvider().appendGroupNodeComment(groupNode, comment3String, user.getUserId());
        final IComment comment3 = new com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.CComment(comment3Id, user, comment2, comment3String);
        final ArrayList<IComment> commentsBeforeDelete = getProvider().loadCommentById(comment3Id);
        Assert.assertNotNull(commentsBeforeDelete);
        Assert.assertEquals(((storedComments.size()) + 3), commentsBeforeDelete.size());
        Assert.assertTrue(commentsBeforeDelete.contains(comment1));
        Assert.assertTrue(commentsBeforeDelete.contains(comment2));
        Assert.assertTrue(commentsBeforeDelete.contains(comment3));
        Assert.assertEquals(comment3, Iterables.getLast(commentsBeforeDelete));
        Assert.assertEquals(comment2, commentsBeforeDelete.get(((commentsBeforeDelete.size()) - 2)));
        Assert.assertEquals(comment1, commentsBeforeDelete.get(((commentsBeforeDelete.size()) - 3)));
        getProvider().deleteGroupNodeComment(groupNode, comment1Id, user.getUserId());
        final ArrayList<IComment> commentsAfterDelete1 = getProvider().loadCommentById(comment1Id);
        Assert.assertNotNull(commentsAfterDelete1);
        Assert.assertTrue(commentsAfterDelete1.isEmpty());
        final ArrayList<IComment> commentsAfterDelete2 = getProvider().loadCommentById(comment3Id);
        final IComment comment2AfterDelete = new com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.CComment(comment2Id, user, lastComment, comment2String);
        final IComment comment3AfterDelete = new com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.CComment(comment3Id, user, comment2AfterDelete, comment3String);
        Assert.assertNotNull(commentsAfterDelete2);
        Assert.assertEquals(((storedComments.size()) + 2), commentsAfterDelete2.size());
        Assert.assertTrue(commentsAfterDelete2.contains(comment3AfterDelete));
        Assert.assertTrue(commentsAfterDelete2.contains(comment2AfterDelete));
        Assert.assertEquals(comment3AfterDelete, Iterables.getLast(commentsAfterDelete2));
        Assert.assertEquals(comment2AfterDelete, commentsAfterDelete2.get(((commentsAfterDelete2.size()) - 2)));
    }

    @Test(expected = NullPointerException.class)
    public void editGroupNodeComment1() throws CouldntSaveDataException {
        getProvider().editGroupNodeComment(null, 1, 1, "");
    }

    @Test(expected = NullPointerException.class)
    public void editGroupNodeComment2() throws CPartialLoadException, CouldntLoadDataException, CouldntSaveDataException, LoadCancelledException, MaybeNullException {
        final INaviGroupNode groupNode = setupGroupNode();
        getProvider().editGroupNodeComment(groupNode, null, 1, "");
    }

    @Test(expected = NullPointerException.class)
    public void editGroupNodeComment3() throws CPartialLoadException, CouldntLoadDataException, CouldntSaveDataException, LoadCancelledException, MaybeNullException {
        final INaviGroupNode groupNode = setupGroupNode();
        getProvider().editGroupNodeComment(groupNode, 1, null, "");
    }

    @Test(expected = NullPointerException.class)
    public void editGroupNodeComment4() throws CPartialLoadException, CouldntLoadDataException, CouldntSaveDataException, LoadCancelledException, MaybeNullException {
        final INaviGroupNode groupNode = setupGroupNode();
        getProvider().editGroupNodeComment(groupNode, 1, 1, null);
    }

    @Test
    public void editGroupNodeComment5() throws CPartialLoadException, CouldntLoadDataException, CouldntSaveDataException, LoadCancelledException, MaybeNullException {
        final INaviGroupNode groupNode = setupGroupNode();
        final List<IComment> comments = ((groupNode.getComments()) == null) ? new ArrayList<IComment>() : groupNode.getComments();
        final IComment lastComment = ((comments.size()) == 0) ? null : Iterables.getLast(comments);
        final IUser user = new UniqueTestUserGenerator(getProvider()).nextActiveUser();
        final String commentText = " GROUP NODE COMMENT TEST BEFORE EDIT ";
        final Integer commentId = getProvider().appendGroupNodeComment(groupNode, commentText, user.getUserId());
        final IComment newComment = new com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.CComment(commentId, user, lastComment, commentText);
        final ArrayList<IComment> newComments = getProvider().loadCommentById(commentId);
        Assert.assertNotNull(newComments);
        Assert.assertEquals(((comments.size()) + 1), newComments.size());
        Assert.assertEquals(newComment, Iterables.getLast(newComments));
        final String commentAfterEdit = " GROUP NODE COMMENT TEST AFTER EDIT ";
        getProvider().editGroupNodeComment(groupNode, commentId, user.getUserId(), commentAfterEdit);
        final ArrayList<IComment> commentsAfterEdit = PostgreSQLCommentFunctions.loadCommentByCommentId(getProvider(), commentId);
        Assert.assertEquals(commentAfterEdit, Iterables.getLast(commentsAfterEdit).getComment());
        Assert.assertEquals(commentsAfterEdit.size(), newComments.size());
    }

    @Test(expected = CouldntSaveDataException.class)
    public void editGroupNodeComment6() throws CPartialLoadException, CouldntLoadDataException, CouldntSaveDataException, LoadCancelledException, MaybeNullException {
        final INaviGroupNode groupNode = setupGroupNode();
        final List<IComment> comments = ((groupNode.getComments()) == null) ? new ArrayList<IComment>() : groupNode.getComments();
        final IComment lastComment = ((comments.size()) == 0) ? null : Iterables.getLast(comments);
        final IUser user = new UniqueTestUserGenerator(getProvider()).nextActiveUser();
        final String commentText = " CODE NODE COMMENT TEST BEFORE EDIT ";
        final Integer commentId = getProvider().appendGroupNodeComment(groupNode, commentText, user.getUserId());
        final IComment newComment = new com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.CComment(commentId, user, lastComment, commentText);
        final ArrayList<IComment> newComments = getProvider().loadCommentById(commentId);
        Assert.assertNotNull(newComments);
        Assert.assertEquals(((comments.size()) + 1), newComments.size());
        Assert.assertEquals(newComment, Iterables.getLast(newComments));
        final IUser wrongUser = new UniqueTestUserGenerator(getProvider()).nextActiveUser();
        getProvider().editGroupNodeComment(groupNode, commentId, wrongUser.getUserId(), " FAIL ");
    }
}

