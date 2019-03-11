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


import LinkType.index;
import LinkType.start;
import Media.print;
import Media.screen;
import java.io.PrintWriter;
import java.util.EnumSet;
import org.apache.hadoop.yarn.webapp.SubView;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestHamlet {
    @Test
    public void testHamlet() {
        Hamlet h = TestHamlet.newHamlet().title("test").h1("heading 1").p("#id.class").b("hello").em("world!")._().div("#footer")._("Brought to you by").a("http://hostname/", "Somebody")._();
        PrintWriter out = h.getWriter();
        out.flush();
        Assert.assertEquals(0, h.nestLevel);
        Mockito.verify(out).print("<title");
        Mockito.verify(out).print("test");
        Mockito.verify(out).print("</title>");
        Mockito.verify(out).print("<h1");
        Mockito.verify(out).print("heading 1");
        Mockito.verify(out).print("</h1>");
        Mockito.verify(out).print("<p");
        Mockito.verify(out).print(" id=\"id\"");
        Mockito.verify(out).print(" class=\"class\"");
        Mockito.verify(out).print("<b");
        Mockito.verify(out).print("hello");
        Mockito.verify(out).print("</b>");
        Mockito.verify(out).print("<em");
        Mockito.verify(out).print("world!");
        Mockito.verify(out).print("</em>");
        Mockito.verify(out).print("<div");
        Mockito.verify(out).print(" id=\"footer\"");
        Mockito.verify(out).print("Brought to you by");
        Mockito.verify(out).print("<a");
        Mockito.verify(out).print(" href=\"http://hostname/\"");
        Mockito.verify(out).print("Somebody");
        Mockito.verify(out).print("</a>");
        Mockito.verify(out).print("</div>");
        Mockito.verify(out, Mockito.never()).print("</p>");
    }

    @Test
    public void testTable() {
        Hamlet h = TestHamlet.newHamlet().title("test table").link("style.css");
        TABLE t = h.table("#id");
        for (int i = 0; i < 3; ++i) {
            t.tr().td("1").td("2")._();
        }
        t._();
        PrintWriter out = h.getWriter();
        out.flush();
        Assert.assertEquals(0, h.nestLevel);
        Mockito.verify(out).print("<table");
        Mockito.verify(out).print("</table>");
        Mockito.verify(out, Mockito.atLeast(1)).print("</td>");
        Mockito.verify(out, Mockito.atLeast(1)).print("</tr>");
    }

    @Test
    public void testEnumAttrs() {
        Hamlet h = TestHamlet.newHamlet().meta_http("Content-type", "text/html; charset=utf-8").title("test enum attrs").link().$rel("stylesheet").$media(EnumSet.of(screen, print)).$type("text/css").$href("style.css")._().link().$rel(EnumSet.of(index, start)).$href("index.html")._();
        h.div("#content")._("content")._();
        PrintWriter out = h.getWriter();
        out.flush();
        Assert.assertEquals(0, h.nestLevel);
        Mockito.verify(out).print(" media=\"screen, print\"");
        Mockito.verify(out).print(" rel=\"start index\"");
    }

    @Test
    public void testScriptStyle() {
        Hamlet h = TestHamlet.newHamlet().script("a.js").script("b.js").style("h1 { font-size: 1.2em }");
        PrintWriter out = h.getWriter();
        out.flush();
        Assert.assertEquals(0, h.nestLevel);
        Mockito.verify(out, Mockito.times(2)).print(" type=\"text/javascript\"");
        Mockito.verify(out).print(" type=\"text/css\"");
    }

    @Test
    public void testPreformatted() {
        Hamlet h = TestHamlet.newHamlet().div().i("inline before pre").pre()._("pre text1\npre text2").i("inline in pre")._("pre text after inline")._().i("inline after pre")._();
        PrintWriter out = h.getWriter();
        out.flush();
        Assert.assertEquals(5, h.indents);
    }

    static class TestView1 implements SubView {
        @Override
        public void renderPartial() {
        }
    }

    static class TestView2 implements SubView {
        @Override
        public void renderPartial() {
        }
    }

    @Test
    public void testSubViews() {
        Hamlet h = TestHamlet.newHamlet().title("test sub-views").div("#view1")._(TestHamlet.TestView1.class)._().div("#view2")._(TestHamlet.TestView2.class)._();
        PrintWriter out = h.getWriter();
        out.flush();
        Assert.assertEquals(0, h.nestLevel);
        Mockito.verify(out).print((("[" + (TestHamlet.TestView1.class.getName())) + "]"));
        Mockito.verify(out).print((("[" + (TestHamlet.TestView2.class.getName())) + "]"));
    }
}

