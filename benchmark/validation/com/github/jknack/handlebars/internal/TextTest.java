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
package com.github.jknack.handlebars.internal;


import com.github.jknack.handlebars.Handlebars;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for {@link Text}.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class TextTest {
    @Test
    public void newText() {
        Assert.assertEquals("a", text());
    }

    @Test
    public void newTextSequence() {
        Assert.assertEquals("abc", text());
    }

    @Test(expected = NullPointerException.class)
    public void newTextFail() {
        new Text(new Handlebars(), null);
    }
}

