/**
 * -
 * #%L
 * rapidoid-render
 * %%
 * Copyright (C) 2014 - 2018 Nikolche Mihajlovski and contributors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.rapidoid.render;


import org.junit.jupiter.api.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.u.U;


@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
@SuppressWarnings("unchecked")
public class RenderTest extends AbstractRenderTest {
    private static final Object[] VALUES = new Object[]{ "abc", 123, 3.14, true, false, U.list(), U.set(1, 2), U.map(1, "one", "two", 2) };

    private static final Object[] TRUE_VALUES = new Object[]{ "abc", 0, 123, 3.14, true, U.list("a", true, false), U.set(false, 2), U.map(1, "one", "two", 2) };

    private static final Object[] FALSE_VALUES = new Object[]{ "", null, false, U.set(), U.list(), U.map() };

    private static final String IF_TEMPLATE = "{{?x}}[${x}]{{/x}}";

    private static final String IF_NOT_TEMPLATE = "{{^x}}[${x}]{{/x}}";

    private static final String IF_NOT_TEMPLATE2 = "{{!x}}[${x}]{{/x}}";

    @Test
    public void testPrint() {
        for (Object x : RenderTest.VALUES) {
            eq(Render.template("${.}").model(x), ("" + x));
        }
    }

    @Test
    public void testPrintUnescaped() {
        for (Object x : RenderTest.VALUES) {
            eq(Render.template("${.}").model(x), ("" + x));
        }
    }

    @Test
    public void testIf() {
        for (Object x : RenderTest.TRUE_VALUES) {
            eq(Render.template(RenderTest.IF_TEMPLATE).model(U.map("x", x)), (("[" + (view(x))) + "]"));
        }
        for (Object x : RenderTest.FALSE_VALUES) {
            eq(Render.template(RenderTest.IF_TEMPLATE).model(U.map("x", x)), "");
        }
    }

    @Test
    public void testIfNot() {
        for (Object x : RenderTest.FALSE_VALUES) {
            eq(Render.template(RenderTest.IF_NOT_TEMPLATE).model(U.map("x", x)), (("[" + (view(x))) + "]"));
        }
        for (Object x : RenderTest.TRUE_VALUES) {
            eq(Render.template(RenderTest.IF_NOT_TEMPLATE).model(U.map("x", x)), "");
        }
    }

    @Test
    public void testIfNot2() {
        for (Object x : RenderTest.FALSE_VALUES) {
            eq(Render.template(RenderTest.IF_NOT_TEMPLATE2).model(U.map("x", x)), (("[" + (view(x))) + "]"));
        }
        for (Object x : RenderTest.TRUE_VALUES) {
            eq(Render.template(RenderTest.IF_NOT_TEMPLATE2).model(U.map("x", x)), "");
        }
    }

    @Test
    public void testIteration() {
        eq(Render.template("{{#.}}(${length}){{/.}}").model(U.list("aaa", "bb")), "(3)(2)");
    }

    @Test
    public void testScopes() {
        eq(Render.template("${x}").multiModel(U.map("x", 1), U.map("x", 2)), "2");
        eq(Render.template("${x}").multiModel(U.map("x", 1), U.map("y", 2)), "1");
        eq(Render.template("${y}").multiModel(U.map("x", 1), U.map("y", 2)), "2");
        eq(Render.template("${z}").multiModel(U.map("x", 1), U.map("y", 2)), view(null));
    }

    @Test
    public void testEscaping() {
        String s = "a\r\n\tb";
        String t = "<p id=\"foo\"><b ng-click=\"f(1, \'x\', 2);\">abc</b></p>";
        String t2 = "&lt;p id=&quot;foo&quot;&gt;&lt;b ng-click=&quot;f(1, &apos;x&apos;, 2);&quot;&gt;abc&lt;/b&gt;&lt;/p&gt;";
        eq(Render.template("${.}").model(s), s);
        eq(Render.template("@{.}").model(s), s);
        eq(Render.template("${.}").model(t), t2);
        eq(Render.template("@{.}").model(t), t);
    }

    @Test
    public void testUTF8() {
        eq(Render.template("????-X-123").model(U.map()), "????-X-123");
        eq(Render.template("?????-${x}-foo").model(U.map("x", "??????")), "?????-??????-foo");
        // 2-byte UTF-8 encoding
        eq(Render.template("${x}:?:${x}").model(U.map("x", "?")), "?:?:?");
        // 3-byte UTF-8 encoding
        eq(Render.template("${y}:??:${y}").model(U.map("y", "?")), "?:??:?");
    }

    @Test
    public void testUTF8Surrogates() {
        String a = "\ud852\udf62";
        String b = "\ud852\udf63";
        eq(a.length(), 2);
        eq(b.length(), 2);
        // 4-byte (surrogate) UTF-8 encoding
        eq(Render.template((("${x}:" + a) + ":${x}")).model(U.map("x", b)), ((((b + ":") + a) + ":") + b));
        // mix of 2-byte, 3-byte and 4-byte (surrogate) UTF-8 encoding
        eq(Render.template("\u0448\u0448\u20acf\u20ac\u20ac\ud852\udf62:${x}").model(U.map("x", "4\u20ac\u0448f3\ud852\udf63")), "\u0448\u0448\u20acf\u20ac\u20ac\ud852\udf62:4\u20ac\u0448f3\ud852\udf63");
    }

    @Test
    public void testUTF8MalformedSurrogates() {
        String missing = "\ud852";// [high]

        eq(Render.template((("${x}:" + missing) + ":${x}")).model(U.map("x", missing)), "?:?:?");
        String wrongOrder = "\udf62\ud852";// [low, high]

        eq(Render.template((("${x}:" + wrongOrder) + ":${x}")).model(U.map("x", wrongOrder)), "??:??:??");
    }
}

