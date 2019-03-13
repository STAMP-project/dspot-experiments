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


import Response.Status.BAD_REQUEST;
import Response.Status.NO_CONTENT;
import java.util.concurrent.CountDownLatch;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.opengrok.indexer.configuration.Configuration;
import org.opengrok.indexer.configuration.RuntimeEnvironment;
import org.opengrok.indexer.web.DummyHttpServletRequest;
import org.opengrok.indexer.web.PageConfig;
import org.opengrok.web.api.v1.suggester.provider.service.SuggesterService;


public class ConfigurationControllerTest extends JerseyTest {
    private RuntimeEnvironment env = RuntimeEnvironment.getInstance();

    @Mock
    private SuggesterService suggesterService;

    @Test
    public void testApplySetAndGetBasicConfig() {
        Configuration config = new Configuration();
        String srcRoot = "/foo";
        config.setSourceRoot(srcRoot);
        String configStr = config.getXMLRepresentationAsString();
        target("configuration").request().put(Entity.xml(configStr));
        Assert.assertEquals(env.getSourceRootPath(), srcRoot);
        String returnedConfig = target("configuration").request().get(String.class);
        Assert.assertEquals(configStr, returnedConfig);
    }

    @Test
    public void testApplySetInvalidMethod() {
        Response r = setValue("noMethodExists", "1000");
        Assert.assertEquals(BAD_REQUEST.getStatusCode(), r.getStatus());
    }

    @Test
    public void testApplyGetInvalidMethod() {
        Response r = target("configuration").path("FooBar").request().get();
        Assert.assertEquals(BAD_REQUEST.getStatusCode(), r.getStatus());
    }

    @Test
    public void testApplySetInvalidMethodParameter() {
        Response r = setValue("setDefaultProjects", "1000");// expecting Set

        Assert.assertEquals(BAD_REQUEST.getStatusCode(), r.getStatus());
    }

    @Test
    public void testApplySetOptionInteger() {
        Assert.assertEquals(25, env.getHitsPerPage());
        setValue("hitsPerPage", "1000");
        Assert.assertEquals(1000, env.getHitsPerPage());
        env.setHitsPerPage(25);
    }

    @Test
    public void testApplySetOptionInvalidInteger() {
        Response r = setValue("hitsPerPage", "abcd");
        Assert.assertEquals(BAD_REQUEST.getStatusCode(), r.getStatus());
    }

    @Test
    public void testApplySetOptionBooleanTrue() {
        testSetChattyStatusPageTrue("true");
        testSetChattyStatusPageTrue("on");
        testSetChattyStatusPageTrue("1");
    }

    @Test
    public void testApplySetOptionBooleanFalse() {
        testSetChattyStatusPageFalse("false");
        testSetChattyStatusPageFalse("off");
        testSetChattyStatusPageFalse("0");
    }

    @Test
    public void testApplySetOptionInvalidBoolean1() {
        Response r = setValue("chattyStatusPage", "1000");// only 1 is accepted as true

        Assert.assertEquals(BAD_REQUEST.getStatusCode(), r.getStatus());
    }

    @Test
    public void testApplySetOptionInvalidBoolean2() {
        Response r = setValue("chattyStatusPage", "anything");
        Assert.assertEquals(BAD_REQUEST.getStatusCode(), r.getStatus());
    }

    @Test
    public void testApplySetOptionString() {
        String old = env.getUserPage();
        setValue("userPage", "http://users.portal.com?user=");
        Assert.assertEquals("http://users.portal.com?user=", env.getUserPage());
        setValue("userPage", "some complicated \"string\" with &#~\u0110`[\u0111\\ characters");
        Assert.assertEquals("some complicated \"string\" with &#~\u0110`[\u0111\\ characters", env.getUserPage());
        env.setUserPage(old);
    }

    @Test
    public void testApplyGetOptionString() {
        env.setSourceRoot("/foo/bar");
        String response = target("configuration").path("sourceRoot").request().get(String.class);
        Assert.assertEquals(response, env.getSourceRootPath());
    }

    @Test
    public void testApplyGetOptionInteger() {
        int hitsPerPage = target("configuration").path("hitsPerPage").request().get(int.class);
        Assert.assertEquals(env.getHitsPerPage(), hitsPerPage);
    }

    @Test
    public void testApplyGetOptionBoolean() {
        boolean response = target("configuration").path("historyCache").request().get(boolean.class);
        Assert.assertEquals(env.isHistoryCache(), response);
    }

    @Test
    public void testSuggesterServiceNotifiedOnConfigurationFieldChange() {
        Mockito.reset(suggesterService);
        setValue("sourceRoot", "test");
        Mockito.verify(suggesterService).refresh();
    }

    @Test
    public void testSuggesterServiceNotifiedOnConfigurationChange() {
        Mockito.reset(suggesterService);
        target("configuration").request().put(Entity.xml(new Configuration().getXMLRepresentationAsString()));
        Mockito.verify(suggesterService).refresh();
    }

    @Test
    public void testConfigValueSetVsThread() throws InterruptedException {
        int origValue = env.getHitsPerPage();
        final int[] threadValue = new int[1];
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch endLatch = new CountDownLatch(1);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpServletRequest req = new DummyHttpServletRequest();
                PageConfig pageConfig = PageConfig.get(req);
                RuntimeEnvironment e = pageConfig.getEnv();
                startLatch.countDown();
                // Wait for hint of termination, save the value and exit.
                try {
                    endLatch.await();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                threadValue[0] = e.getHitsPerPage();
            }
        });
        thread.start();
        startLatch.await();
        // Set brand new configuration.
        int newValue = origValue + 42;
        Configuration config = new Configuration();
        config.setHitsPerPage(newValue);
        String configStr = config.getXMLRepresentationAsString();
        Response res = target("configuration").request().put(Entity.xml(configStr));
        Assert.assertEquals(NO_CONTENT.getStatusCode(), res.getStatus());
        // Unblock the thread.
        endLatch.countDown();
        thread.join();
        // Check thread's view of the variable.
        Assert.assertEquals(newValue, threadValue[0]);
        // Revert the value back to the default.
        env.setHitsPerPage(origValue);
    }
}

