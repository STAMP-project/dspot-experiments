/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processors.slack;


import PostSlack.ACCESS_TOKEN;
import PostSlack.CHANNEL;
import PostSlack.FILE_UPLOAD_URL;
import PostSlack.POST_MESSAGE_URL;
import PostSlack.TEXT;
import PostSlack.UPLOAD_FLOWFILE;
import PostSlack.UPLOAD_FLOWFILE_YES;
import org.apache.nifi.util.TestRunner;
import org.junit.Test;


public class PostSlackConfigValidationTest {
    private TestRunner testRunner;

    @Test
    public void validationShouldPassIfTheConfigIsFine() {
        testRunner.setProperty(ACCESS_TOKEN, "my-access-token");
        testRunner.setProperty(CHANNEL, "my-channel");
        testRunner.setProperty(TEXT, "my-text");
        testRunner.assertValid();
    }

    @Test
    public void validationShouldFailIfPostMessageUrlIsEmptyString() {
        testRunner.setProperty(POST_MESSAGE_URL, "");
        testRunner.setProperty(ACCESS_TOKEN, "my-access-token");
        testRunner.setProperty(CHANNEL, "my-channel");
        testRunner.setProperty(TEXT, "my-text");
        testRunner.assertNotValid();
    }

    @Test
    public void validationShouldFailIfPostMessageUrlIsNotValid() {
        testRunner.setProperty(POST_MESSAGE_URL, "not-url");
        testRunner.setProperty(ACCESS_TOKEN, "my-access-token");
        testRunner.setProperty(CHANNEL, "my-channel");
        testRunner.setProperty(TEXT, "my-text");
        testRunner.assertNotValid();
    }

    @Test
    public void validationShouldFailIfFileUploadUrlIsEmptyString() {
        testRunner.setProperty(FILE_UPLOAD_URL, "");
        testRunner.setProperty(ACCESS_TOKEN, "my-access-token");
        testRunner.setProperty(CHANNEL, "my-channel");
        testRunner.setProperty(TEXT, "my-text");
        testRunner.assertNotValid();
    }

    @Test
    public void validationShouldFailIfFileUploadUrlIsNotValid() {
        testRunner.setProperty(FILE_UPLOAD_URL, "not-url");
        testRunner.setProperty(ACCESS_TOKEN, "my-access-token");
        testRunner.setProperty(CHANNEL, "my-channel");
        testRunner.setProperty(TEXT, "my-text");
        testRunner.assertNotValid();
    }

    @Test
    public void validationShouldFailIfAccessTokenIsNotGiven() {
        testRunner.setProperty(CHANNEL, "my-channel");
        testRunner.setProperty(TEXT, "my-text");
        testRunner.assertNotValid();
    }

    @Test
    public void validationShouldFailIfAccessTokenIsEmptyString() {
        testRunner.setProperty(ACCESS_TOKEN, "");
        testRunner.setProperty(CHANNEL, "my-channel");
        testRunner.setProperty(TEXT, "my-text");
        testRunner.assertNotValid();
    }

    @Test
    public void validationShouldFailIfChannelIsNotGiven() {
        testRunner.setProperty(ACCESS_TOKEN, "my-access-token");
        testRunner.setProperty(TEXT, "my-text");
        testRunner.assertNotValid();
    }

    @Test
    public void validationShouldFailIfChannelIsEmptyString() {
        testRunner.setProperty(ACCESS_TOKEN, "my-access-token");
        testRunner.setProperty(CHANNEL, "");
        testRunner.setProperty(TEXT, "my-text");
        testRunner.assertNotValid();
    }

    @Test
    public void validationShouldFailIfTextIsNotGivenAndNoAttachmentSpecifiedNorFileUploadChosen() {
        testRunner.setProperty(ACCESS_TOKEN, "my-access-token");
        testRunner.setProperty(CHANNEL, "my-channel");
        testRunner.assertNotValid();
    }

    @Test
    public void validationShouldPassIfTextIsNotGivenButAttachmentSpecified() {
        testRunner.setProperty(ACCESS_TOKEN, "my-access-token");
        testRunner.setProperty(CHANNEL, "my-channel");
        testRunner.setProperty("attachment_01", "{\"my-attachment-key\": \"my-attachment-value\"}");
        testRunner.assertValid();
    }

    @Test
    public void validationShouldPassIfTextIsNotGivenButFileUploadChosen() {
        testRunner.setProperty(ACCESS_TOKEN, "my-access-token");
        testRunner.setProperty(CHANNEL, "my-channel");
        testRunner.setProperty(UPLOAD_FLOWFILE, UPLOAD_FLOWFILE_YES);
        testRunner.assertValid();
    }

    @Test
    public void validationShouldFailIfTextIsEmptyString() {
        testRunner.setProperty(ACCESS_TOKEN, "my-access-token");
        testRunner.setProperty(CHANNEL, "my-channel");
        testRunner.setProperty(TEXT, "");
        testRunner.setProperty("attachment_01", "{\"my-attachment-key\": \"my-attachment-value\"}");
        testRunner.setProperty(UPLOAD_FLOWFILE, UPLOAD_FLOWFILE_YES);
        testRunner.assertNotValid();
    }
}

