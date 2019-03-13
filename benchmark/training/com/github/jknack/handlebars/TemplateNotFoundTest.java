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


import java.io.FileNotFoundException;
import java.io.IOException;
import org.junit.Test;


public class TemplateNotFoundTest {
    @Test(expected = FileNotFoundException.class)
    public void templateNotFound() throws IOException {
        Handlebars handlebars = new Handlebars();
        handlebars.compile("template.hbs");
    }

    @Test(expected = HandlebarsException.class)
    public void partialNotFound() throws IOException {
        Handlebars handlebars = new Handlebars();
        handlebars.compileInline("{{> text}}").apply(null);
    }
}

