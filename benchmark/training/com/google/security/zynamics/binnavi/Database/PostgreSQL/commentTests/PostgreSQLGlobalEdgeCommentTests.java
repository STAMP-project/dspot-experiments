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
import com.google.security.zynamics.binnavi.Database.Exceptions.CPartialLoadException;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntDeleteException;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntLoadDataException;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntSaveDataException;
import com.google.security.zynamics.binnavi.Database.Exceptions.LoadCancelledException;
import com.google.security.zynamics.binnavi.Database.PostgreSQL.UniqueTestUserGenerator;
import com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.Interfaces.IComment;
import com.google.security.zynamics.binnavi.Gui.Users.Interfaces.IUser;
import com.google.security.zynamics.binnavi.disassembly.CNaviViewEdge;
import com.google.security.zynamics.binnavi.disassembly.types.ExpensiveBaseTest;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class PostgreSQLGlobalEdgeCommentTests extends ExpensiveBaseTest {
    private CNaviViewEdge edge;

    @Test(expected = NullPointerException.class)
    public void appendGlobalEdgeComment1() throws CouldntSaveDataException {
        getProvider().appendGlobalEdgeComment(null, null, null);
    }

    @Test(expected = NullPointerException.class)
    public void appendGlobalEdgeComment2() throws CouldntSaveDataException {
        getProvider().appendGlobalEdgeComment(edge, null, null);
    }

    @Test(expected = NullPointerException.class)
    public void appendGlobalEdgeComment3() throws CouldntSaveDataException {
        getProvider().appendGlobalEdgeComment(edge, " FAIL ", null);
    }

    @Test
    public void appendGlobalEdgeComment4() throws CPartialLoadException, CouldntLoadDataException, CouldntSaveDataException, LoadCancelledException {
        final IUser user = new UniqueTestUserGenerator(getProvider()).nextActiveUser();
        final List<IComment> storedComments = ((edge.getGlobalComment()) == null) ? new ArrayList<IComment>() : edge.getGlobalComment();
        final IComment lastComment = (storedComments.isEmpty()) ? null : Iterables.getLast(storedComments);
        final String firstCommentString = " PASS GLOBAL EDGE COMMENT WITHOUT PARENT ID ";
        final int firstCommentId = getProvider().appendGlobalEdgeComment(edge, firstCommentString, user.getUserId());
        final IComment firstComment = new com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.CComment(firstCommentId, user, lastComment, firstCommentString);
        final String secondCommentString = " PASS GLOBAL EDGE COMMENT WITH PARENT ID ";
        final int secondCommentId = getProvider().appendGlobalEdgeComment(edge, secondCommentString, user.getUserId());
        final IComment secondComment = new com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.CComment(secondCommentId, user, firstComment, secondCommentString);
        final ArrayList<IComment> commentsFromDatabase = getProvider().loadCommentById(secondCommentId);
        Assert.assertNotNull(commentsFromDatabase);
        Assert.assertEquals(((storedComments.size()) + 2), commentsFromDatabase.size());
        Assert.assertTrue(commentsFromDatabase.contains(firstComment));
        Assert.assertTrue(commentsFromDatabase.contains(secondComment));
        final CNaviViewEdge callgraphEdge = ((CNaviViewEdge) (loadCallGraphEdge(getKernel32Module())));
        getProvider().appendGlobalEdgeComment(callgraphEdge, " PASS GLOBAL CALLGRAPH EDGE COMMENT WITHOUT PARENT ID ", user.getUserId());
    }

    @Test(expected = NullPointerException.class)
    public void deleteGlobalEdgeComment1() throws CouldntDeleteException {
        getProvider().deleteGlobalEdgeComment(null, null, null);
    }

    @Test(expected = NullPointerException.class)
    public void deleteGlobalEdgeComment2() throws CouldntDeleteException {
        getProvider().deleteGlobalEdgeComment(edge, null, null);
    }

    @Test(expected = NullPointerException.class)
    public void deleteGlobalEdgeComment3() throws CouldntDeleteException {
        getProvider().deleteGlobalEdgeComment(edge, 1, null);
    }

    /**
     * This test checks if the deletion of a single global edge comment works consistently.
     */
    @Test
    public void deleteGlobalEdgeComment4() throws CouldntDeleteException, CouldntLoadDataException, CouldntSaveDataException {
        final IUser user = new UniqueTestUserGenerator(getProvider()).nextActiveUser();
        final List<IComment> comments = ((edge.getGlobalComment()) == null) ? new ArrayList<IComment>() : edge.getGlobalComment();
        final IComment lastComment = (comments.isEmpty()) ? null : Iterables.getLast(comments);
        final String commentText = " COMMENT TO BE DELETED ";
        final Integer commentId = getProvider().appendGlobalEdgeComment(edge, commentText, user.getUserId());
        final IComment commentToBeDelete = new com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.CComment(commentId, user, lastComment, commentText);
        final ArrayList<IComment> commentsFromDatabase = getProvider().loadCommentById(commentId);
        Assert.assertNotNull(commentsFromDatabase);
        Assert.assertEquals(((comments.size()) + 1), commentsFromDatabase.size());
        Assert.assertTrue(commentsFromDatabase.contains(commentToBeDelete));
        getProvider().deleteGlobalEdgeComment(edge, commentId, user.getUserId());
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
    public void deleteGlobalEdgeComment5() throws CouldntDeleteException, CouldntLoadDataException, CouldntSaveDataException {
        final IUser user = new UniqueTestUserGenerator(getProvider()).nextActiveUser();
        final List<IComment> storedComments = ((edge.getGlobalComment()) == null) ? new ArrayList<IComment>() : edge.getGlobalComment();
        final IComment lastComment = (storedComments.isEmpty()) ? null : Iterables.getLast(storedComments);
        final String comment1String = " Comment 1: ";
        final int comment1Id = getProvider().appendGlobalEdgeComment(edge, comment1String, user.getUserId());
        final IComment comment1 = new com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.CComment(comment1Id, user, lastComment, comment1String);
        final String comment2String = " Comment 2: ";
        final int comment2Id = getProvider().appendGlobalEdgeComment(edge, comment2String, user.getUserId());
        final IComment comment2 = new com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.CComment(comment2Id, user, comment1, comment2String);
        final String comment3String = " Comment 3: ";
        final int comment3Id = getProvider().appendGlobalEdgeComment(edge, comment3String, user.getUserId());
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
        getProvider().deleteGlobalEdgeComment(edge, comment3Id, user.getUserId());
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
    public void deleteGlobalEdgeComment6() throws CouldntDeleteException, CouldntLoadDataException, CouldntSaveDataException {
        final IUser user = new UniqueTestUserGenerator(getProvider()).nextActiveUser();
        final List<IComment> storedComments = ((edge.getGlobalComment()) == null) ? new ArrayList<IComment>() : edge.getGlobalComment();
        final IComment lastComment = (storedComments.isEmpty()) ? null : Iterables.getLast(storedComments);
        final String comment1String = " Comment 1: ";
        final int comment1Id = getProvider().appendGlobalEdgeComment(edge, comment1String, user.getUserId());
        final IComment comment1 = new com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.CComment(comment1Id, user, lastComment, comment1String);
        final String comment2String = " Comment 2: ";
        final int comment2Id = getProvider().appendGlobalEdgeComment(edge, comment2String, user.getUserId());
        final IComment comment2 = new com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.CComment(comment2Id, user, comment1, comment2String);
        final String comment3String = " Comment 3: ";
        final int comment3Id = getProvider().appendGlobalEdgeComment(edge, comment3String, user.getUserId());
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
        getProvider().deleteGlobalEdgeComment(edge, comment2Id, user.getUserId());
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
    public void deleteGlobalEdgeComment7() throws CouldntDeleteException, CouldntLoadDataException, CouldntSaveDataException {
        final IUser user = new UniqueTestUserGenerator(getProvider()).nextActiveUser();
        final List<IComment> storedComments = ((edge.getGlobalComment()) == null) ? new ArrayList<IComment>() : edge.getGlobalComment();
        final IComment lastComment = (storedComments.isEmpty()) ? null : Iterables.getLast(storedComments);
        final String comment1String = " Comment 1: ";
        final int comment1Id = getProvider().appendGlobalEdgeComment(edge, comment1String, user.getUserId());
        final IComment comment1 = new com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.CComment(comment1Id, user, lastComment, comment1String);
        final String comment2String = " Comment 2: ";
        final int comment2Id = getProvider().appendGlobalEdgeComment(edge, comment2String, user.getUserId());
        final IComment comment2 = new com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.CComment(comment2Id, user, comment1, comment2String);
        final String comment3String = " Comment 3: ";
        final int comment3Id = getProvider().appendGlobalEdgeComment(edge, comment3String, user.getUserId());
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
        getProvider().deleteGlobalEdgeComment(edge, comment1Id, user.getUserId());
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

    /**
     * This tests checks that a comment from another user can not be deleted by a different user id.
     */
    @Test(expected = CouldntDeleteException.class)
    public void deleteGlobalEdgeComment8() throws CouldntDeleteException, CouldntLoadDataException, CouldntSaveDataException {
        final IUser user = new UniqueTestUserGenerator(getProvider()).nextActiveUser();
        final List<IComment> comments = ((edge.getGlobalComment()) == null) ? new ArrayList<IComment>() : edge.getGlobalComment();
        final IComment lastComment = (comments.isEmpty()) ? null : Iterables.getLast(comments);
        final String commentText = " COMMENT TO BE DELETED ";
        final Integer commentId = getProvider().appendGlobalEdgeComment(edge, commentText, user.getUserId());
        final IComment commentToBeDelete = new com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.CComment(commentId, user, lastComment, commentText);
        final ArrayList<IComment> commentsFromDatabase = getProvider().loadCommentById(commentId);
        Assert.assertNotNull(commentsFromDatabase);
        Assert.assertEquals(((comments.size()) + 1), commentsFromDatabase.size());
        Assert.assertTrue(commentsFromDatabase.contains(commentToBeDelete));
        final IUser wrongUser = new UniqueTestUserGenerator(getProvider()).nextActiveUser();
        Assert.assertFalse(wrongUser.equals(user));
        getProvider().deleteGlobalEdgeComment(edge, commentId, wrongUser.getUserId());
    }

    @Test(expected = NullPointerException.class)
    public void editGlobalEdgeComment1() throws CouldntSaveDataException {
        getProvider().editGlobalEdgeComment(null, null, null, null);
    }

    @Test(expected = NullPointerException.class)
    public void editGlobalEdgeComment2() throws CouldntSaveDataException {
        getProvider().editGlobalEdgeComment(edge, null, null, null);
    }

    @Test(expected = NullPointerException.class)
    public void editGlobalEdgeComment3() throws CouldntSaveDataException {
        getProvider().editGlobalEdgeComment(edge, 1, null, null);
    }

    @Test(expected = NullPointerException.class)
    public void editGlobalEdgeComment4() throws CouldntSaveDataException {
        getProvider().editGlobalEdgeComment(edge, 1, 1, null);
    }

    @Test
    public void editGlobalEdgeComment5() throws CouldntLoadDataException, CouldntSaveDataException {
        final IUser user = new UniqueTestUserGenerator(getProvider()).nextActiveUser();
        final List<IComment> storedComments = ((edge.getGlobalComment()) == null) ? new ArrayList<IComment>() : edge.getGlobalComment();
        final IComment lastComment = (storedComments.isEmpty()) ? null : Iterables.getLast(storedComments);
        final String beforeEditCommentString = " EDIT GLOBAL EDGE COMMENT BEFORE EDIT ";
        final int editedCommentId = getProvider().appendGlobalEdgeComment(edge, beforeEditCommentString, user.getUserId());
        final IComment commentBeforeEdit = new com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.CComment(editedCommentId, user, lastComment, beforeEditCommentString);
        final ArrayList<IComment> commentsFromDatabase = getProvider().loadCommentById(editedCommentId);
        Assert.assertNotNull(commentsFromDatabase);
        Assert.assertTrue(commentsFromDatabase.contains(commentBeforeEdit));
        Assert.assertEquals(((storedComments.size()) + 1), commentsFromDatabase.size());
        final String afterEditCommentString = " EDIT GLOBAL EDGE COMMENT AFTER EDIT ";
        getProvider().editGlobalEdgeComment(edge, editedCommentId, user.getUserId(), afterEditCommentString);
        final IComment commentAfterEdit = new com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.CComment(editedCommentId, user, lastComment, afterEditCommentString);
        final ArrayList<IComment> commentsAfterEdit = getProvider().loadCommentById(editedCommentId);
        Assert.assertNotNull(commentsAfterEdit);
        Assert.assertTrue(commentsAfterEdit.contains(commentAfterEdit));
    }

    @Test(expected = CouldntSaveDataException.class)
    public void editGlobalEdgeComment6() throws CouldntLoadDataException, CouldntSaveDataException {
        final IUser user = new UniqueTestUserGenerator(getProvider()).nextActiveUser();
        final List<IComment> storedComments = ((edge.getGlobalComment()) == null) ? new ArrayList<IComment>() : edge.getGlobalComment();
        final IComment lastComment = (storedComments.isEmpty()) ? null : Iterables.getLast(storedComments);
        final String beforeEditCommentString = " EDIT GLOBAL EDGE COMMENT BEFORE EDIT ";
        final int editedCommentId = getProvider().appendGlobalEdgeComment(edge, beforeEditCommentString, user.getUserId());
        final IComment commentBeforeEdit = new com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.CComment(editedCommentId, user, lastComment, beforeEditCommentString);
        final ArrayList<IComment> commentsFromDatabase = getProvider().loadCommentById(editedCommentId);
        Assert.assertNotNull(commentsFromDatabase);
        Assert.assertTrue(commentsFromDatabase.contains(commentBeforeEdit));
        Assert.assertEquals(((storedComments.size()) + 1), commentsFromDatabase.size());
        final IUser wrongUser = new UniqueTestUserGenerator(getProvider()).nextActiveUser();
        getProvider().editGlobalEdgeComment(edge, editedCommentId, wrongUser.getUserId(), " FAIL ");
    }
}

