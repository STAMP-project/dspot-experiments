/**
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License (the "License").
 * You may not use this file except in compliance with the License.
 *
 * See LICENSE.txt included in this distribution for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at LICENSE.txt.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */
/**
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 */
package org.opengrok.web.api.v1.controller;


import MessagesContainer.MESSAGES_MAIN_PAGE_TAG;
import Response.Status.BAD_REQUEST;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.test.JerseyTest;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.opengrok.indexer.configuration.RuntimeEnvironment;
import org.opengrok.indexer.web.messages.Message;
import org.opengrok.indexer.web.messages.MessagesContainer.AcceptedMessage;


public class MessagesControllerTest extends JerseyTest {
    private static final GenericType<List<MessagesControllerTest.AcceptedMessageModel>> messagesType = new GenericType<List<MessagesControllerTest.AcceptedMessageModel>>() {};

    private final RuntimeEnvironment env = RuntimeEnvironment.getInstance();

    private static class AcceptedMessageModel {
        public String created;

        public String expiration;

        public boolean expired;

        public String text;

        public String cssClass;

        public Set<String> tags;
    }

    @Test
    public void addMessageTest() {
        addMessage("test message");
        Assert.assertFalse(env.getMessages().isEmpty());
        AcceptedMessage msg = env.getMessages().first();
        Assert.assertEquals("test&nbsp;message", msg.getMessage().getText());
    }

    @Test
    public void removeMessageTest() {
        env.addMessage(new Message("test", Collections.singleton(MESSAGES_MAIN_PAGE_TAG), "test", Duration.ofMinutes(10)));
        Assert.assertFalse(env.getMessages().isEmpty());
        removeMessages(MESSAGES_MAIN_PAGE_TAG);
        Assert.assertTrue(env.getMessages().isEmpty());
    }

    @Test
    public void addAndRemoveTest() {
        addMessage("test", "test");
        addMessage("test", "test");
        Assert.assertEquals(2, env.getMessages("test").size());
        removeMessages("test");
        Assert.assertTrue(env.getMessages("test").isEmpty());
    }

    @Test
    public void addAndRemoveDifferentTagsTest() {
        addMessage("test", "tag1");
        addMessage("test", "tag2");
        Assert.assertEquals(1, env.getMessages("tag1").size());
        Assert.assertEquals(1, env.getMessages("tag2").size());
        removeMessages("tag1");
        Assert.assertEquals(0, env.getMessages("tag1").size());
        Assert.assertEquals(1, env.getMessages("tag2").size());
        removeMessages("tag2");
        Assert.assertTrue(env.getMessages("tag2").isEmpty());
    }

    @Test
    public void addMessageNegativeDurationTest() throws Exception {
        Message m = new Message("text", Collections.singleton("test"), "cssClass", Duration.ofMinutes(1));
        setDuration(m, Duration.ofMinutes((-10)));
        Response r = target("messages").request().post(Entity.json(m));
        Assert.assertEquals(BAD_REQUEST.getStatusCode(), r.getStatus());
    }

    @Test
    public void addEmptyMessageTest() throws Exception {
        Message m = new Message("text", Collections.singleton("test"), "cssClass", Duration.ofMinutes(1));
        setText(m, "");
        Response r = target("messages").request().post(Entity.json(m));
        Assert.assertEquals(BAD_REQUEST.getStatusCode(), r.getStatus());
    }

    @Test
    public void getAllMessagesTest() {
        addMessage("text1", "info");
        addMessage("text2", "main");
        List<MessagesControllerTest.AcceptedMessageModel> allMessages = target("messages").request().get(MessagesControllerTest.messagesType);
        Assert.assertEquals(2, allMessages.size());
    }

    @Test
    public void getSpecificMessageTest() {
        addMessage("text", "info");
        List<MessagesControllerTest.AcceptedMessageModel> messages = target("messages").queryParam("tag", "info").request().get(MessagesControllerTest.messagesType);
        Assert.assertEquals(1, messages.size());
        Assert.assertEquals("text", messages.get(0).text);
        MatcherAssert.assertThat(messages.get(0).tags, Matchers.contains("info"));
    }

    @Test
    public void multipleTagsTest() {
        addMessage("test", "info", "main");
        List<MessagesControllerTest.AcceptedMessageModel> allMessages = target("messages").request().get(MessagesControllerTest.messagesType);
        Assert.assertEquals(1, allMessages.size());
    }

    @Test
    public void multipleMessageAndTagsTest() {
        addMessage("test1", "tag1", "tag2");
        addMessage("test2", "tag3", "tag4");
        List<MessagesControllerTest.AcceptedMessageModel> allMessages = target("messages").queryParam("tag", "tag3").request().get(MessagesControllerTest.messagesType);
        Assert.assertEquals(1, allMessages.size());
    }
}

