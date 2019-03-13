/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2019 the original author or authors.
 */
package org.assertj.core.util;


import java.io.File;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


/* Tests for {@link URLs#contentOf(File, Charset)} and {@link URLs#contentOf(File, String)}.

@author Turbo87
@author dorzey
 */
public class URLs_contentOf_Test {
    private final URL sampleResourceURL = ClassLoader.getSystemResource("utf8.txt");

    private final String expectedContent = "A text file encoded in UTF-8, with diacritics:\n\u00e9 \u00e0";

    @Test
    public void should_throw_exception_if_url_not_found() {
        File missingFile = new File("missing.txt");
        Assertions.assertThat(missingFile.exists()).isFalse();
        Assertions.assertThatExceptionOfType(UncheckedIOException.class).isThrownBy(() -> URLs.contentOf(missingFile.toURI().toURL(), Charset.defaultCharset()));
    }

    @Test
    public void should_load_resource_from_url_using_charset() {
        Assertions.assertThat(URLs.contentOf(sampleResourceURL, StandardCharsets.UTF_8)).isEqualTo(expectedContent);
    }

    @Test
    public void should_load_resource_from_url_using_charset_name() {
        Assertions.assertThat(URLs.contentOf(sampleResourceURL, "UTF-8")).isEqualTo(expectedContent);
    }
}

