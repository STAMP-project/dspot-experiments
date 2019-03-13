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
package org.opengrok.indexer.web.messages;


import MessagesContainer.AcceptedMessage;
import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


public class MessagesContainerTest {
    private MessagesContainer container;

    @Test
    public void addAndGetTest() {
        Message m = new Message("test", Collections.singleton("test"), "info", Duration.ofMinutes(10));
        container.addMessage(m);
        Assert.assertEquals(m, container.getMessages("test").first().getMessage());
    }

    @Test(expected = IllegalArgumentException.class)
    public void addNullTest() {
        container.addMessage(null);
    }

    @Test
    public void removeNullTest() {
        // the call should not throw an exception
        container.removeAnyMessage(null);
    }

    @Test
    public void parallelAddTest() throws Exception {
        container.setMessageLimit(5000);
        parallelAddMessages();
        Assert.assertEquals(1000, container.getMessages("test").size());
        Assert.assertEquals(1000, getMessagesInTheSystem());
    }

    @Test
    public void parallelAddLimitTest() throws Exception {
        container.setMessageLimit(10);
        parallelAddMessages();
        Assert.assertEquals(10, container.getMessages("test").size());
        Assert.assertEquals(10, getMessagesInTheSystem());
    }

    @Test
    public void expirationTest() {
        Message m = new Message("test", Collections.singleton("test"), "info", Duration.ofMillis(10));
        container.addMessage(m);
        await().atMost(2, TimeUnit.SECONDS).until(() -> container.getMessages("info").isEmpty());
    }

    @Test
    public void removeTest() {
        Message m = new Message("test", Collections.singleton("test"), "info", Duration.ofMillis(10));
        container.addMessage(m);
        container.removeAnyMessage(Collections.singleton("test"));
        Assert.assertTrue(container.getMessages("test").isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getMessagesNullTest() {
        container.getMessages(null);
    }

    /**
     * tests serialization of MessagesContainer.AcceptedMessage
     */
    @Test
    public void testJSON() throws IOException {
        Message m = new Message("testJSON", Collections.singleton("testJSON"), "info", Duration.ofMinutes(10));
        container.addMessage(m);
        MessagesContainer.AcceptedMessage am = container.getMessages("testJSON").first();
        String jsonString = am.toJSON();
        Assert.assertEquals(new HashSet<>(Arrays.asList("tags", "expired", "created", "expiration", "cssClass", "text")), JSONUtils.getTopLevelJSONFields(jsonString));
    }
}

