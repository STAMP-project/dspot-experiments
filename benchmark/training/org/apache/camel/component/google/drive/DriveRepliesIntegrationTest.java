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
package org.apache.camel.component.google.drive;


import com.google.api.services.drive.model.Comment;
import com.google.api.services.drive.model.CommentList;
import com.google.api.services.drive.model.CommentReply;
import com.google.api.services.drive.model.CommentReplyList;
import com.google.api.services.drive.model.File;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.component.google.drive.internal.DriveRepliesApiMethod;
import org.apache.camel.component.google.drive.internal.GoogleDriveApiCollection;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test class for com.google.api.services.drive.Drive$Replies APIs.
 */
public class DriveRepliesIntegrationTest extends AbstractGoogleDriveTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(DriveRepliesIntegrationTest.class);

    private static final String PATH_PREFIX = GoogleDriveApiCollection.getCollection().getApiName(DriveRepliesApiMethod.class).getName();

    @Test
    public void testReplyToComment() throws Exception {
        // 1. create test file
        File testFile = uploadTestFile();
        String fileId = testFile.getId();
        // 2. comment on that file
        Map<String, Object> headers = new HashMap<>();
        // parameter type is String
        headers.put("CamelGoogleDrive.fileId", fileId);
        // parameter type is com.google.api.services.drive.model.Comment
        Comment comment = new Comment();
        comment.setContent("Camel rocks!");
        headers.put("CamelGoogleDrive.content", comment);
        requestBodyAndHeaders("direct://INSERT_COMMENT", null, headers);
        // 3. get a list of comments on the file
        // using String message body for single parameter "fileId"
        CommentList result1 = requestBody("direct://LIST_COMMENTS", fileId);
        assertNotNull(result1.get("items"));
        DriveRepliesIntegrationTest.LOG.debug(("list: " + result1));
        Comment comment2 = result1.getItems().get(0);
        String commentId = comment2.getCommentId();
        // 4. add reply
        headers = new HashMap<>();
        // parameter type is String
        headers.put("CamelGoogleDrive.fileId", fileId);
        // parameter type is String
        headers.put("CamelGoogleDrive.commentId", commentId);
        // parameter type is com.google.api.services.drive.model.CommentReply
        CommentReply reply = new CommentReply();
        reply.setContent("I know :-)");
        headers.put("CamelGoogleDrive.content", reply);
        requestBodyAndHeaders("direct://INSERT", null, headers);
        // 5. list replies on comment to file
        headers = new HashMap<>();
        // parameter type is String
        headers.put("CamelGoogleDrive.fileId", fileId);
        // parameter type is String
        headers.put("CamelGoogleDrive.commentId", commentId);
        final CommentReplyList result = requestBodyAndHeaders("direct://LIST", null, headers);
        assertNotNull("list result", result);
        DriveRepliesIntegrationTest.LOG.debug(("list: " + result));
    }
}

