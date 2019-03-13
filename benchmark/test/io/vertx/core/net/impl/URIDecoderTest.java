/**
 * Copyright (c) 2014 Red Hat, Inc. and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package io.vertx.core.net.impl;


import java.net.URLEncoder;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author <a href="mailto:plopes@redhat.com">Paulo Lopes</a>
 */
public class URIDecoderTest {
    @Test
    public void testDecode() throws Exception {
        String original = "ein verr+++?ckter text mit Leerzeichen, Plus und Umlauten";
        String encoded = URLEncoder.encode(original, "UTF-8");
        Assert.assertEquals(original, URIDecoder.decodeURIComponent(encoded, true));
    }

    @Test
    public void testPlusAsSpace() {
        Assert.assertEquals("foo bar", URIDecoder.decodeURIComponent("foo+bar"));
    }

    @Test
    public void testPlusAsPlus() {
        Assert.assertEquals("foo+bar", URIDecoder.decodeURIComponent("foo+bar", false));
    }

    @Test
    public void testSpaces() {
        Assert.assertEquals("foo bar", URIDecoder.decodeURIComponent("foo%20bar"));
    }

    @Test
    public void testSingleDecode() {
        Assert.assertEquals("../blah", URIDecoder.decodeURIComponent("%2E%2E%2Fblah"));
        Assert.assertEquals("%20", URIDecoder.decodeURIComponent("%2520"));
    }

    @Test
    public void testFromRFC() {
        Assert.assertEquals("/ !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~", URIDecoder.decodeURIComponent("/%20%21%22%23%24%25%26%27%28%29%2A%2B%2C%2D%2E%2F%30%31%32%33%34%35%36%37%38%39%3A%3B%3C%3D%3E%3F%40%41%42%43%44%45%46%47%48%49%4A%4B%4C%4D%4E%4F%50%51%52%53%54%55%56%57%58%59%5A%5B%5C%5D%5E%5F%60%61%62%63%64%65%66%67%68%69%6A%6B%6C%6D%6E%6F%70%71%72%73%74%75%76%77%78%79%7A%7B%7C%7D%7E", false));
    }

    @Test
    public void testNonLatin() {
        Assert.assertEquals("/foo/?/blah/????/eek/?", URIDecoder.decodeURIComponent("/foo/%C3%B1/blah/%E5%A9%B4%E5%84%BF%E6%9C%8D%E9%A5%B0/eek/%E0%B8%8C"));
        Assert.assertEquals("/foo/\u00f1/blah/\u5a74\u513f\u670d\u9970/eek/\u0e0c", URIDecoder.decodeURIComponent("/foo/%C3%B1/blah/%E5%A9%B4%E5%84%BF%E6%9C%8D%E9%A5%B0/eek/%E0%B8%8C", false));
    }

    @Test
    public void testIncomplete() {
        try {
            URIDecoder.decodeURIComponent("a%");
            Assert.fail("should fail");
        } catch (RuntimeException e) {
            // expected
        }
    }

    @Test
    public void testCaseInsensitive() {
        Assert.assertEquals("../blah", URIDecoder.decodeURIComponent("%2e%2e%2fblah"));
    }
}

