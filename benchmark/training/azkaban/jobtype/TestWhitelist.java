/**
 * Copyright (C) 2016 LinkedIn Corp. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the
 * License at  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.
 */
package azkaban.jobtype;


import CommonJobProperties.SUBMIT_USER;
import azkaban.jobtype.javautils.Whitelist;
import azkaban.utils.Props;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import org.junit.Assert;
import org.junit.Test;


public class TestWhitelist {
    private static final String PROXY_USER_KEY = "user.to.proxy";

    private String[] whitelisted = new String[]{ "whitelisted_1", "whitelisted_2" };

    private File temp;

    private Whitelist whitelist;

    @Test
    public void testWhiteListed() throws IOException, URISyntaxException {
        for (String s : whitelisted) {
            whitelist.validateWhitelisted(s);
            Props props = new Props();
            props.put(TestWhitelist.PROXY_USER_KEY, s);
            whitelist.validateWhitelisted(props);
            props = new Props();
            props.put(SUBMIT_USER, s);
            whitelist.validateWhitelisted(props);
        }
    }

    @Test
    public void testNotWhiteListed() throws IOException, URISyntaxException {
        String id = "not_white_listed";
        try {
            whitelist.validateWhitelisted(id);
            Assert.fail("Should throw UnsupportedOperationException");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof UnsupportedOperationException));
        }
    }

    @Test
    public void testProxyUserWhitelisted() throws IOException, URISyntaxException {
        String notAuthorized = "not_white_listed";
        for (String s : whitelisted) {
            Props props = new Props();
            props.put(TestWhitelist.PROXY_USER_KEY, s);
            props.put(SUBMIT_USER, notAuthorized);
            whitelist.validateWhitelisted(props);
        }
    }

    @Test
    public void testProxyUserNotAuthorized() throws IOException, URISyntaxException {
        String notAuthorized = "not_white_listed";
        for (String authorized : whitelisted) {
            Props props = new Props();
            props.put(TestWhitelist.PROXY_USER_KEY, notAuthorized);
            props.put(SUBMIT_USER, authorized);
            try {
                whitelist.validateWhitelisted(props);
                Assert.fail("Should throw UnsupportedOperationException");
            } catch (Exception e) {
                Assert.assertTrue((e instanceof UnsupportedOperationException));
            }
        }
    }
}

