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


import FieldValueResolver.INSTANCE;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class ArrayTest extends AbstractTest {
    private static class Letter {
        private char letter;

        public Letter(final char letter) {
            this.letter = letter;
        }

        @Override
        public String toString() {
            return (letter) + "";
        }
    }

    @Test
    public void stringArray() throws IOException {
        AbstractTest.Hash hash = AbstractTest.$("list", new String[]{ "w", "o", "r", "l", "d" });
        shouldCompileTo("Hello {{#list}}{{this}}{{/list}}!", hash, "Hello world!");
    }

    @Test
    public void objectArray() throws IOException {
        AbstractTest.Hash hash = AbstractTest.$("list", new Object[]{ "w", "o", "r", "l", "d" });
        shouldCompileTo("Hello {{#list}}{{this}}{{/list}}!", hash, "Hello world!");
    }

    @Test
    public void eachArray() throws IOException {
        AbstractTest.Hash hash = AbstractTest.$("list", new Object[]{ "w", "o", "r", "l", "d" });
        shouldCompileTo("Hello {{#each list}}{{this}}{{/each}}!", hash, "Hello world!");
    }

    @Test
    public void letterArray() throws IOException {
        AbstractTest.Hash hash = AbstractTest.$("list", new ArrayTest.Letter[]{ new ArrayTest.Letter('w'), new ArrayTest.Letter('o'), new ArrayTest.Letter('r'), new ArrayTest.Letter('l'), new ArrayTest.Letter('d') });
        shouldCompileTo("Hello {{#list}}{{this}}{{/list}}!", hash, "Hello world!");
    }

    @Test
    public void arrayLength() throws IOException {
        Object[] array = new Object[]{ "1", 2, "3" };
        Assert.assertEquals("3", compile("{{this.length}}").apply(Context.newBuilder(array).resolver(INSTANCE).build()));
    }
}

