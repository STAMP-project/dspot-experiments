/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.webapp.hamlet;


import java.io.PrintWriter;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestHamletImpl {
    /**
     * Test the generic implementation methods
     *
     * @see TestHamlet for Hamlet syntax
     */
    @Test
    public void testGeneric() {
        PrintWriter out = Mockito.spy(new PrintWriter(System.out));
        HamletImpl hi = new HamletImpl(out, 0, false);
        hi.root("start")._attr("name", "value")._("start text").elem("sub")._attr("name", "value")._("sub text")._().elem("sub1")._noEndTag()._attr("boolean", null)._("sub1text")._()._("start text2").elem("pre")._pre()._("pre text").elem("i")._inline()._("inline")._()._().elem("i")._inline()._("inline after pre")._()._("start text3").elem("sub2")._("sub2text")._().elem("sub3")._noEndTag()._("sub3text")._().elem("sub4")._noEndTag().elem("i")._inline()._("inline")._()._("sub4text")._()._();
        out.flush();
        Assert.assertEquals(0, hi.nestLevel);
        Assert.assertEquals(20, hi.indents);
        Mockito.verify(out).print("<start");
        Mockito.verify(out, Mockito.times(2)).print(" name=\"value\"");
        Mockito.verify(out).print(" boolean");
        Mockito.verify(out).print("</start>");
        Mockito.verify(out, Mockito.never()).print("</sub1>");
        Mockito.verify(out, Mockito.never()).print("</sub3>");
        Mockito.verify(out, Mockito.never()).print("</sub4>");
    }

    @Test
    public void testSetSelector() {
        CoreAttrs e = Mockito.mock(CoreAttrs.class);
        HamletImpl.setSelector(e, "#id.class");
        Mockito.verify(e).$id("id");
        Mockito.verify(e).$class("class");
        H1 t = Mockito.mock(H1.class);
        HamletImpl.setSelector(t, "#id.class")._("heading");
        Mockito.verify(t).$id("id");
        Mockito.verify(t).$class("class");
        Mockito.verify(t)._("heading");
    }

    @Test
    public void testSetLinkHref() {
        LINK link = Mockito.mock(LINK.class);
        HamletImpl.setLinkHref(link, "uri");
        HamletImpl.setLinkHref(link, "style.css");
        Mockito.verify(link).$href("uri");
        Mockito.verify(link).$rel("stylesheet");
        Mockito.verify(link).$href("style.css");
        Mockito.verifyNoMoreInteractions(link);
    }

    @Test
    public void testSetScriptSrc() {
        SCRIPT script = Mockito.mock(SCRIPT.class);
        HamletImpl.setScriptSrc(script, "uri");
        HamletImpl.setScriptSrc(script, "script.js");
        Mockito.verify(script).$src("uri");
        Mockito.verify(script).$type("text/javascript");
        Mockito.verify(script).$src("script.js");
        Mockito.verifyNoMoreInteractions(script);
    }
}

