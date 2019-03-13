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
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntDeleteException;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntLoadDataException;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntSaveDataException;
import com.google.security.zynamics.binnavi.Database.PostgreSQL.Functions.PostgreSQLCommentFunctions;
import com.google.security.zynamics.binnavi.Database.PostgreSQL.UniqueTestUserGenerator;
import com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.Interfaces.IComment;
import com.google.security.zynamics.binnavi.Gui.Users.Interfaces.IUser;
import com.google.security.zynamics.binnavi.disassembly.CCodeNode;
import com.google.security.zynamics.binnavi.disassembly.types.ExpensiveBaseTest;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class PostgreSQLLocalCodeNodeCommentTests extends ExpensiveBaseTest {
    private CCodeNode codeNode;

    @Test(expected = NullPointerException.class)
    public void appendLocalCodeNodeComment1() throws CouldntSaveDataException {
        getProvider().appendLocalCodeNodeComment(null, null, null);
    }

    @Test(expected = NullPointerException.class)
    public void appendLocalCodeNodeComment2() throws CouldntSaveDataException {
        getProvider().appendLocalCodeNodeComment(codeNode, null, null);
    }

    @Test(expected = NullPointerException.class)
    public void appendLocalCodeNodeComment3() throws CouldntSaveDataException {
        getProvider().appendLocalCodeNodeComment(codeNode, " FAIL ", null);
    }

    @Test
    public void appendLocalCodeNodeComment4() throws CouldntSaveDataException {
        final IUser user = new UniqueTestUserGenerator(getProvider()).nextActiveUser();
        getProvider().appendLocalCodeNodeComment(codeNode, " PASS LOCAL CODE NODE COMMENT ", user.getUserId());
    }

    @Test
    public void appendLocalCodeNodeComment5() throws CouldntLoadDataException, CouldntSaveDataException {
        final IUser user = new UniqueTestUserGenerator(getProvider()).nextActiveUser();
        final List<IComment> storedComments = ((codeNode.getComments().getLocalCodeNodeComment()) == null) ? new ArrayList<IComment>() : codeNode.getComments().getLocalCodeNodeComment();
        final int numberOfComments = storedComments.size();
        final IComment lastComment = (storedComments.isEmpty()) ? null : Iterables.getLast(storedComments);
        final String firstCommentString = " PASS LOCAL CODE NODE COMMENT 1 ";
        final int firstCommentId = getProvider().appendLocalCodeNodeComment(codeNode, firstCommentString, user.getUserId());
        final IComment firstComment = new com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.CComment(firstCommentId, user, lastComment, firstCommentString);
        final String secondCommentString = " PASS LOCAL CODE NODE COMMENT 2 ";
        final int secondCommentId = getProvider().appendLocalCodeNodeComment(codeNode, secondCommentString, user.getUserId());
        final IComment secondComment = new com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.CComment(secondCommentId, user, firstComment, secondCommentString);
        final ArrayList<IComment> commentsFromDatabase = getProvider().loadCommentById(secondCommentId);
        Assert.assertNotNull(commentsFromDatabase);
        Assert.assertEquals((numberOfComments + 2), commentsFromDatabase.size());
        Assert.assertTrue(commentsFromDatabase.contains(firstComment));
        Assert.assertTrue(commentsFromDatabase.contains(secondComment));
    }

    @Test(expected = NullPointerException.class)
    public void deleteLocalCodeNodeComment1() throws CouldntDeleteException {
        getProvider().deleteLocalCodeNodeComment(null, null, null);
    }

    @Test(expected = NullPointerException.class)
    public void deleteLocalCodeNodeComment2() throws CouldntDeleteException {
        getProvider().deleteLocalCodeNodeComment(codeNode, null, null);
    }

    @Test(expected = NullPointerException.class)
    public void deleteLocalCodeNodeComment3() throws CouldntDeleteException {
        getProvider().deleteLocalCodeNodeComment(codeNode, 1, null);
    }

    @Test
    public void deleteLocalCodeNodeComment4() throws CouldntDeleteException, CouldntLoadDataException, CouldntSaveDataException {
        final List<IComment> comments = ((codeNode.getComments().getLocalCodeNodeComment()) == null) ? new ArrayList<IComment>() : codeNode.getComments().getLocalCodeNodeComment();
        final IComment lastComment = ((comments.size()) == 0) ? null : Iterables.getLast(comments);
        final String commentString = " TEST DELETE LOCAL CODE NODE COMMENT ";
        final IUser user = new UniqueTestUserGenerator(getProvider()).nextActiveUser();
        final int commentId = getProvider().appendLocalCodeNodeComment(codeNode, commentString, user.getUserId());
        final IComment newComment = new com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.CComment(commentId, user, lastComment, commentString);
        final ArrayList<IComment> storedComments = getProvider().loadCommentById(commentId);
        Assert.assertNotNull(storedComments);
        Assert.assertEquals(((comments.size()) + 1), storedComments.size());
        Assert.assertEquals(newComment, Iterables.getLast(storedComments));
        getProvider().deleteLocalCodeNodeComment(codeNode, commentId, newComment.getUser().getUserId());
        final ArrayList<IComment> commentsAfterDelete = getProvider().loadCommentById(commentId);
        Assert.assertNotNull(commentsAfterDelete);
        Assert.assertTrue(commentsAfterDelete.isEmpty());
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
    public void deleteLocalCodeNodeComment5() throws CouldntDeleteException, CouldntLoadDataException, CouldntSaveDataException {
        final IUser user = new UniqueTestUserGenerator(getProvider()).nextActiveUser();
        final List<IComment> storedComments = ((codeNode.getComments().getLocalCodeNodeComment()) == null) ? new ArrayList<IComment>() : codeNode.getComments().getLocalCodeNodeComment();
        final IComment lastComment = (storedComments.isEmpty()) ? null : Iterables.getLast(storedComments);
        final String comment1String = " Comment 1: ";
        final int comment1Id = getProvider().appendLocalCodeNodeComment(codeNode, comment1String, user.getUserId());
        final IComment comment1 = new com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.CComment(comment1Id, user, lastComment, comment1String);
        final String comment2String = " Comment 2: ";
        final int comment2Id = getProvider().appendLocalCodeNodeComment(codeNode, comment2String, user.getUserId());
        final IComment comment2 = new com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.CComment(comment2Id, user, comment1, comment2String);
        final String comment3String = " Comment 3: ";
        final int comment3Id = getProvider().appendLocalCodeNodeComment(codeNode, comment3String, user.getUserId());
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
        getProvider().deleteLocalCodeNodeComment(codeNode, comment3Id, user.getUserId());
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
    public void deleteLocalCodeNodeComment6() throws CouldntDeleteException, CouldntLoadDataException, CouldntSaveDataException {
        final IUser user = new UniqueTestUserGenerator(getProvider()).nextActiveUser();
        final List<IComment> storedComments = ((codeNode.getComments().getLocalCodeNodeComment()) == null) ? new ArrayList<IComment>() : codeNode.getComments().getLocalCodeNodeComment();
        final IComment lastComment = (storedComments.isEmpty()) ? null : Iterables.getLast(storedComments);
        final String comment1String = " Comment 1: ";
        final int comment1Id = getProvider().appendLocalCodeNodeComment(codeNode, comment1String, user.getUserId());
        final IComment comment1 = new com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.CComment(comment1Id, user, lastComment, comment1String);
        final String comment2String = " Comment 2: ";
        final int comment2Id = getProvider().appendLocalCodeNodeComment(codeNode, comment2String, user.getUserId());
        final IComment comment2 = new com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.CComment(comment2Id, user, comment1, comment2String);
        final String comment3String = " Comment 3: ";
        final int comment3Id = getProvider().appendLocalCodeNodeComment(codeNode, comment3String, user.getUserId());
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
        getProvider().deleteLocalCodeNodeComment(codeNode, comment2Id, user.getUserId());
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
    public void deleteLocalCodeNodeComment7() throws CouldntDeleteException, CouldntLoadDataException, CouldntSaveDataException {
        final IUser user = new UniqueTestUserGenerator(getProvider()).nextActiveUser();
        final List<IComment> storedComments = ((codeNode.getComments().getLocalCodeNodeComment()) == null) ? new ArrayList<IComment>() : codeNode.getComments().getLocalCodeNodeComment();
        final IComment lastComment = (storedComments.isEmpty()) ? null : Iterables.getLast(storedComments);
        final String comment1String = " Comment 1: ";
        final int comment1Id = getProvider().appendLocalCodeNodeComment(codeNode, comment1String, user.getUserId());
        final IComment comment1 = new com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.CComment(comment1Id, user, lastComment, comment1String);
        final String comment2String = " Comment 2: ";
        final int comment2Id = getProvider().appendLocalCodeNodeComment(codeNode, comment2String, user.getUserId());
        final IComment comment2 = new com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.CComment(comment2Id, user, comment1, comment2String);
        final String comment3String = " Comment 3: ";
        final int comment3Id = getProvider().appendLocalCodeNodeComment(codeNode, comment3String, user.getUserId());
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
        getProvider().deleteLocalCodeNodeComment(codeNode, comment1Id, user.getUserId());
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
    public void editLocalCodeNodeComment1() throws CouldntSaveDataException {
        getProvider().editLocalCodeNodeComment(null, null, null, null);
    }

    @Test(expected = NullPointerException.class)
    public void editLocalCodeNodeComment2() throws CouldntSaveDataException {
        getProvider().editLocalCodeNodeComment(codeNode, null, null, null);
    }

    @Test(expected = NullPointerException.class)
    public void editLocalCodeNodeComment3() throws CouldntSaveDataException {
        getProvider().editLocalCodeNodeComment(codeNode, 1, null, null);
    }

    @Test(expected = NullPointerException.class)
    public void editLocalCodeNodeComment4() throws CouldntSaveDataException {
        getProvider().editLocalCodeNodeComment(codeNode, 1, 1, null);
    }

    @Test
    public void editLocalCodeNodeComment5() throws CouldntLoadDataException, CouldntSaveDataException {
        final List<IComment> comments = ((codeNode.getComments().getLocalCodeNodeComment()) == null) ? new ArrayList<IComment>() : codeNode.getComments().getLocalCodeNodeComment();
        final IComment lastComment = ((comments.size()) == 0) ? null : Iterables.getLast(comments);
        final IUser user = new UniqueTestUserGenerator(getProvider()).nextActiveUser();
        final String commentText = " CODE NODE COMMENT TEST BEFORE EDIT ";
        final Integer commentId = getProvider().appendLocalCodeNodeComment(codeNode, commentText, user.getUserId());
        final IComment newComment = new com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.CComment(commentId, user, lastComment, commentText);
        final ArrayList<IComment> newComments = getProvider().loadCommentById(commentId);
        Assert.assertNotNull(newComments);
        Assert.assertEquals(((comments.size()) + 1), newComments.size());
        Assert.assertEquals(newComment, Iterables.getLast(newComments));
        final String commentAfterEdit = " CODE NODE COMMENT TEST AFTER EDIT ";
        getProvider().editLocalCodeNodeComment(codeNode, commentId, user.getUserId(), commentAfterEdit);
        final ArrayList<IComment> commentsAfterEdit = PostgreSQLCommentFunctions.loadCommentByCommentId(getProvider(), commentId);
        Assert.assertEquals(commentAfterEdit, Iterables.getLast(commentsAfterEdit).getComment());
        Assert.assertEquals(commentsAfterEdit.size(), newComments.size());
    }

    @Test(expected = CouldntSaveDataException.class)
    public void editLocalCodeNodeComment6() throws CouldntLoadDataException, CouldntSaveDataException {
        final IUser user = new UniqueTestUserGenerator(getProvider()).nextActiveUser();
        final List<IComment> storedComments = ((codeNode.getComments().getLocalCodeNodeComment()) == null) ? new ArrayList<IComment>() : codeNode.getComments().getLocalCodeNodeComment();
        final IComment lastComment = (storedComments.isEmpty()) ? null : Iterables.getLast(storedComments);
        final String beforeEditCommentString = " EDIT LOCAL COMMENT BEFORE EDIT ";
        final int editedCommentId = getProvider().appendLocalCodeNodeComment(codeNode, beforeEditCommentString, user.getUserId());
        final IComment commentBeforeEdit = new com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.CComment(editedCommentId, user, lastComment, beforeEditCommentString);
        final ArrayList<IComment> commentsFromDatabase = getProvider().loadCommentById(editedCommentId);
        Assert.assertNotNull(commentsFromDatabase);
        Assert.assertTrue(commentsFromDatabase.contains(commentBeforeEdit));
        Assert.assertEquals(((storedComments.size()) + 1), commentsFromDatabase.size());
        final IUser wrongUser = new UniqueTestUserGenerator(getProvider()).nextActiveUser();
        getProvider().editLocalCodeNodeComment(codeNode, editedCommentId, wrongUser.getUserId(), " FAIL ");
    }
}

