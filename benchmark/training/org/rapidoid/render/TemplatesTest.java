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


import TemplateParser.TAG;
import java.util.List;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.io.IO;
import org.rapidoid.u.U;


@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class TemplatesTest extends AbstractRenderTest {
    @Test
    public void testPatterns() {
        List<String> valid = IO.loadLines("tags-valid.txt", true, "#");
        List<String> invalid = IO.loadLines("tags-invalid.txt", true, "#");
        notNull(valid);
        notNull(invalid);
        for (String tag : valid) {
            tag = tag.replace("\\n", "\n").trim();
            if (!(Pattern.matches(TAG, tag))) {
                eq(tag, "EXPECTED TO BE VALID!");
            }
        }
        for (String tag : invalid) {
            tag = tag.replace("\\n", "\n").trim();
            if (Pattern.compile(TAG).matcher(tag).find()) {
                eq(tag, "EXPECTED TO BE INVALID!");
            }
        }
        eq(TemplateToCode.literal("a\"b"), "\"a\\\"b\"");
    }

    @Test
    public void testFileTemplatesAPI() {
        verify("tmpl", Templates.load("tmpl.html").render(tmplModel()));
    }

    @Test
    public void testStringTemplatesAPI() {
        eq(Templates.compile("${x}-${y}").render(U.map("x", 1, "y", "2")), "1-2");
    }

    @Test
    public void testRender() {
        eq(Render.template("${.}").model(123), "123");
    }
}

