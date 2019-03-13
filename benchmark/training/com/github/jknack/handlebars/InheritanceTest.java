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


import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class InheritanceTest {
    static Handlebars handlebars = new Handlebars(new ClassPathTemplateLoader("/inheritance"));

    static {
        InheritanceTest.handlebars.setPrettyPrint(true);
    }

    private String name;

    public InheritanceTest(final String name) {
        this.name = name;
    }

    @Test
    public void inheritance() throws IOException {
        try {
            Template template = InheritanceTest.handlebars.compile(name);
            CharSequence result = template.apply(new Object());
            String expected = FileUtils.readFileToString(new File((("src/test/resources/inheritance/" + (name)) + ".expected")));
            Assert.assertEquals(expected, result);
        } catch (HandlebarsException ex) {
            Handlebars.error(ex.getMessage());
            throw ex;
        }
    }
}

