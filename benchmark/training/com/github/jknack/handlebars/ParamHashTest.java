/**
 * Copyright (c) 2012 Edgar Espina
 * This file is part of Handlebars.java.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
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


public class ParamHashTest extends AbstractTest {
    @Test
    public void truthParam() throws IOException {
        AbstractTest.Hash helpers = AbstractTest.$("helper", new Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                Assert.assertEquals(true, options.param(0));
                return "ok";
            }
        });
        shouldCompileTo("{{helper . true}}", new Object(), helpers, "ok");
    }

    @Test
    public void falsyParam() throws IOException {
        AbstractTest.Hash helpers = AbstractTest.$("helper", new Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                Assert.assertEquals(false, options.param(0));
                return "ok";
            }
        });
        shouldCompileTo("{{helper . false}}", new Object(), helpers, "ok");
    }

    @Test
    public void truthHash() throws IOException {
        AbstractTest.Hash helpers = AbstractTest.$("helper", new Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                Assert.assertEquals(true, options.hash("b"));
                return "ok";
            }
        });
        shouldCompileTo("{{helper . b=true}}", new Object(), helpers, "ok");
    }

    @Test
    public void falsyHash() throws IOException {
        AbstractTest.Hash helpers = AbstractTest.$("helper", new Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                Assert.assertEquals(false, options.hash("b"));
                return "ok";
            }
        });
        shouldCompileTo("{{helper . b=false}}", new Object(), helpers, "ok");
    }

    @Test
    public void intHash() throws IOException {
        shouldCompileTo("{{var h=9}}", AbstractTest.$, "Integer:9");
    }

    @Test
    public void intParam() throws IOException {
        shouldCompileTo("{{varp . 9}}", AbstractTest.$, "Integer:9");
    }

    @Test
    public void stringHash() throws IOException {
        shouldCompileTo("{{var h=\"Hey!\"}}", AbstractTest.$, "String:Hey!");
    }

    @Test
    public void stringParam() throws IOException {
        shouldCompileTo("{{varp . \"Hey!\"}}", AbstractTest.$, "String:Hey!");
    }

    @Test
    public void charsHash() throws IOException {
        shouldCompileTo("{{var h='Hey!' }}", AbstractTest.$, "String:Hey!");
    }

    @Test
    public void boolHash() throws IOException {
        shouldCompileTo("{{var h=true}}", AbstractTest.$, "Boolean:true");
        shouldCompileTo("{{var h=false}}", AbstractTest.$, "Boolean:false");
    }

    @Test
    public void boolParam() throws IOException {
        shouldCompileTo("{{varp . true}}", AbstractTest.$, "Boolean:true");
        shouldCompileTo("{{varp . false}}", AbstractTest.$, "Boolean:false");
    }

    @Test
    public void referenceHash() throws IOException {
        shouldCompileTo("{{var h=ref}}", AbstractTest.$("ref", "."), "String:.");
    }

    @Test
    public void referenceParam() throws IOException {
        shouldCompileTo("{{varp . ref}}", AbstractTest.$("ref", "."), "String:.");
    }
}

