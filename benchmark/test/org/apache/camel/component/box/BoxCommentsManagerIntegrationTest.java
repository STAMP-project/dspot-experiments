/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.box;


import BoxComment.Info;
import com.box.sdk.BoxComment;
import com.box.sdk.BoxFile;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.camel.component.box.internal.BoxApiCollection;
import org.apache.camel.component.box.internal.BoxCommentsManagerApiMethod;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test class for
 * {@link BoxCommentsManager} APIs.
 */
public class BoxCommentsManagerIntegrationTest extends AbstractBoxTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(BoxCommentsManagerIntegrationTest.class);

    private static final String PATH_PREFIX = BoxApiCollection.getCollection().getApiName(BoxCommentsManagerApiMethod.class).getName();

    private static final String CAMEL_TEST_FILE = "/CamelTestFile.txt";

    private static final String CAMEL_TEST_FILE_NAME = "CamelTestFile.txt";

    private static final String CAMEL_TEST_FILE_COMMENT = "CamelTestFile comment.";

    private static final String CAMEL_TEST_FILE_CHANGED_COMMENT = "CamelTestFile changed comment.";

    private static final String CAMEL_TEST_FILE_REPLY_COMMENT = "CamelTestFile changed comment.";

    @Test
    public void testAddFileComment() throws Exception {
        final Map<String, Object> headers = new HashMap<>();
        // parameter type is String
        headers.put("CamelBox.fileId", testFile.getID());
        // parameter type is String
        headers.put("CamelBox.message", BoxCommentsManagerIntegrationTest.CAMEL_TEST_FILE_COMMENT);
        final BoxFile result = requestBodyAndHeaders("direct://ADDFILECOMMENT", null, headers);
        assertNotNull("addFileComment result", result);
        assertNotNull("addFileComment comments", result.getComments());
        assertTrue("changeCommentMessage comments size", ((result.getComments().size()) > 0));
        assertEquals("changeCommentMessage comment message", BoxCommentsManagerIntegrationTest.CAMEL_TEST_FILE_COMMENT, result.getComments().get(0).getMessage());
        BoxCommentsManagerIntegrationTest.LOG.debug(("addFileComment: " + result));
    }

    @Test
    public void testChangeCommentMessage() throws Exception {
        BoxComment.Info commentInfo = testFile.addComment(BoxCommentsManagerIntegrationTest.CAMEL_TEST_FILE_COMMENT);
        final Map<String, Object> headers = new HashMap<>();
        // parameter type is String
        headers.put("CamelBox.commentId", commentInfo.getID());
        // parameter type is String
        headers.put("CamelBox.message", BoxCommentsManagerIntegrationTest.CAMEL_TEST_FILE_CHANGED_COMMENT);
        final BoxComment result = requestBodyAndHeaders("direct://CHANGECOMMENTMESSAGE", null, headers);
        assertNotNull("changeCommentMessage result", result);
        assertNotNull("changeCommentMessage message", result.getInfo().getMessage());
        assertEquals("changeCommentMessage message", BoxCommentsManagerIntegrationTest.CAMEL_TEST_FILE_CHANGED_COMMENT, result.getInfo().getMessage());
        BoxCommentsManagerIntegrationTest.LOG.debug(("changeCommentMessage: " + result));
    }

    @Test
    public void testDeleteComment() throws Exception {
        BoxComment.Info commentInfo = testFile.addComment(BoxCommentsManagerIntegrationTest.CAMEL_TEST_FILE_COMMENT);
        // using String message body for single parameter "commentId"
        requestBody("direct://DELETECOMMENT", commentInfo.getID());
        List<BoxComment.Info> comments = testFile.getComments();
        assertNotNull("deleteComment comments", comments);
        assertEquals("deleteComment comments empty", 0, comments.size());
    }

    @Test
    public void testGetCommentInfo() throws Exception {
        BoxComment.Info commentInfo = testFile.addComment(BoxCommentsManagerIntegrationTest.CAMEL_TEST_FILE_COMMENT);
        // using String message body for single parameter "commentId"
        final BoxComment.Info result = requestBody("direct://GETCOMMENTINFO", commentInfo.getID());
        assertNotNull("getCommentInfo result", result);
        assertEquals("getCommentInfo message", BoxCommentsManagerIntegrationTest.CAMEL_TEST_FILE_COMMENT, result.getMessage());
        BoxCommentsManagerIntegrationTest.LOG.debug(("getCommentInfo: " + result));
    }

    @Test
    public void testGetFileComments() throws Exception {
        testFile.addComment(BoxCommentsManagerIntegrationTest.CAMEL_TEST_FILE_COMMENT);
        // using String message body for single parameter "fileId"
        @SuppressWarnings("rawtypes")
        final List result = requestBody("direct://GETFILECOMMENTS", testFile.getID());
        assertNotNull("getFileComments result", result);
        assertEquals("getFileComments size", 1, result.size());
        BoxCommentsManagerIntegrationTest.LOG.debug(("getFileComments: " + result));
    }

    @Test
    public void testReplyToComment() throws Exception {
        BoxComment.Info commentInfo = testFile.addComment(BoxCommentsManagerIntegrationTest.CAMEL_TEST_FILE_COMMENT);
        final Map<String, Object> headers = new HashMap<>();
        // parameter type is String
        headers.put("CamelBox.commentId", commentInfo.getID());
        // parameter type is String
        headers.put("CamelBox.message", BoxCommentsManagerIntegrationTest.CAMEL_TEST_FILE_REPLY_COMMENT);
        final BoxComment result = requestBodyAndHeaders("direct://REPLYTOCOMMENT", null, headers);
        assertNotNull("replyToComment result", result);
        assertEquals("replyToComment result", BoxCommentsManagerIntegrationTest.CAMEL_TEST_FILE_REPLY_COMMENT, result.getInfo().getMessage());
        BoxCommentsManagerIntegrationTest.LOG.debug(("replyToComment: " + result));
    }
}

