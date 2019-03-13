/**
 * Copyright (c) 2012 Edgar Espina
 *
 * This file is part of Handlebars.java.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jknack.handlebars;


import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class ScalarContextTest {
    private String selector;

    public ScalarContextTest(final String selector) {
        this.selector = selector;
    }

    @Test
    public void integer() throws IOException {
        Handlebars handlebars = new Handlebars();
        Template template = handlebars.compileInline((("var i = {{" + (selector)) + "}};"));
        Assert.assertEquals("var i = 10;", template.apply(10));
    }

    @Test
    public void string() throws IOException {
        Handlebars handlebars = new Handlebars();
        Template template = handlebars.compileInline((("var s = '{{" + (selector)) + "}}';"));
        Assert.assertEquals("var s = 'Hello';", template.apply("Hello"));
    }

    @Test
    public void quoteParam() throws IOException {
        Handlebars handlebars = new Handlebars();
        handlebars.registerHelper("quote", new Helper<String>() {
            @Override
            public Object apply(final String context, final Options options) throws IOException {
                return context;
            }
        });
        Template template = handlebars.compileInline("{{{quote \"2\\\"secs\"}}}");
        Assert.assertEquals("2\"secs", template.apply(new Object()));
    }

    @Test
    public void quoteHash() throws IOException {
        Handlebars handlebars = new Handlebars();
        handlebars.registerHelper("quote", new Helper<String>() {
            @Override
            public Object apply(final String context, final Options options) throws IOException {
                return ((CharSequence) (options.hash.get("q")));
            }
        });
        Template template = handlebars.compileInline("{{{quote q=\"2\\\"secs\"}}}");
        Assert.assertEquals("2\"secs", template.apply(null));
    }

    @Test
    public void array() throws IOException {
        Handlebars handlebars = new Handlebars();
        Template template = handlebars.compileInline((((((("{{#" + (selector)) + "}}{{") + (selector)) + "}} {{/") + (selector)) + "}}"));
        Assert.assertEquals("1 2 3 ", template.apply(new Object[]{ 1, 2, 3 }));
    }

    @Test
    public void list() throws IOException {
        Handlebars handlebars = new Handlebars();
        Template template = handlebars.compileInline((((((("{{#" + (selector)) + "}}{{") + (selector)) + "}} {{/") + (selector)) + "}}"));
        Assert.assertEquals("1 2 3 ", template.apply(new Object[]{ 1, 2, 3 }));
    }

    @Test
    public void dontEscape() throws IOException {
        Handlebars handlebars = new Handlebars();
        Template template = handlebars.compileInline((("var s = '{{{" + (selector)) + "}}}';"));
        Assert.assertEquals("var s = '<div>';", template.apply("<div>"));
    }

    @Test
    public void safeString() throws IOException {
        Handlebars handlebars = new Handlebars();
        Template template = handlebars.compileInline((("var s = '{{" + (selector)) + "}}';"));
        Assert.assertEquals("var s = '<div>';", template.apply(new Handlebars.SafeString("<div>")));
    }

    @Test
    public void ch() throws IOException {
        Handlebars handlebars = new Handlebars();
        Template template = handlebars.compileInline((("var s = '{{" + (selector)) + "}}';"));
        Assert.assertEquals("var s = 'c';", template.apply('c'));
    }

    @Test
    public void chHtml() throws IOException {
        Handlebars handlebars = new Handlebars();
        Template template = handlebars.compileInline((("var s = '{{" + (selector)) + "}}';"));
        Assert.assertEquals("var s = '&lt;';", template.apply('<'));
    }

    @Test
    public void htmlString() throws IOException {
        Handlebars handlebars = new Handlebars();
        Template template = handlebars.compileInline((("var s = '{{" + (selector)) + "}}';"));
        Assert.assertEquals("var s = '&lt;div&gt;';", template.apply("<div>"));
    }

    @Test
    public void bool() throws IOException {
        Handlebars handlebars = new Handlebars();
        Template template = handlebars.compileInline((("if ({{" + (selector)) + "}})"));
        Assert.assertEquals("if (true)", template.apply(true));
    }

    @Test
    public void decimal() throws IOException {
        Handlebars handlebars = new Handlebars();
        Template template = handlebars.compileInline((("var d = {{" + (selector)) + "}};"));
        Assert.assertEquals("var d = 1.34;", template.apply(1.34));
    }
}

