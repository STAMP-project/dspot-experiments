/**
 * Copyright 2017 Robert Winkler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.swagger2markup;


import MarkupLanguage.MARKDOWN;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import io.github.swagger2markup.assertions.DiffUtils;
import io.github.swagger2markup.builder.Swagger2MarkupConfigBuilder;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MarkdownConverterTest {
    private static final Logger LOG = LoggerFactory.getLogger(MarkdownConverterTest.class);

    private static final String[] EXPECTED_FILES = new String[]{ "definitions.md", "overview.md", "paths.md", "security.md" };

    private List<String> expectedFiles;

    @Test
    public void testToFolder() throws IOException, URISyntaxException {
        // Given
        Path file = Paths.get(MarkdownConverterTest.class.getResource("/yaml/swagger_petstore.yaml").toURI());
        Path outputDirectory = Paths.get("build/test/markdown/to_folder");
        FileUtils.deleteQuietly(outputDirectory.toFile());
        // When
        Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder().withMarkupLanguage(MARKDOWN).build();
        Swagger2MarkupConverter.from(file).withConfig(config).build().toFolder(outputDirectory);
        // Then
        String[] files = outputDirectory.toFile().list();
        assertThat(files).hasSize(4).containsAll(expectedFiles);
        Path expectedFilesDirectory = Paths.get(AsciidocConverterTest.class.getResource("/expected/markdown/to_folder").toURI());
        DiffUtils.assertThatAllFilesAreEqual(expectedFilesDirectory, outputDirectory, "testToFolder.html");
    }

    @Test
    public void testWithMinMaxItems() throws IOException, URISyntaxException {
        // Given
        Path file = Paths.get(MarkdownConverterTest.class.getResource("/yaml/swagger_petstore_min_max_items.yaml").toURI());
        Path outputDirectory = Paths.get("build/test/markdown/to_folder");
        FileUtils.deleteQuietly(outputDirectory.toFile());
        // When
        Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder().withMarkupLanguage(MARKDOWN).build();
        Swagger2MarkupConverter.from(file).withConfig(config).build().toFolder(outputDirectory);
        // Then
        String[] files = outputDirectory.toFile().list();
        assertThat(files).hasSize(4).containsAll(expectedFiles);
        Path expectedFilesDirectory = Paths.get(AsciidocConverterTest.class.getResource("/expected/markdown/min_max_items").toURI());
        DiffUtils.assertThatAllFilesAreEqual(expectedFilesDirectory, outputDirectory, "min_max_items.html");
    }

    @Test
    public void testWithInterDocumentCrossReferences() throws IOException, URISyntaxException {
        // Given
        Path file = Paths.get(AsciidocConverterTest.class.getResource("/yaml/swagger_petstore.yaml").toURI());
        Path outputDirectory = Paths.get("build/test/markdown/idxref");
        FileUtils.deleteQuietly(outputDirectory.toFile());
        // When
        Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder().withMarkupLanguage(MARKDOWN).withInterDocumentCrossReferences().build();
        Swagger2MarkupConverter.from(file).withConfig(config).build().toFolder(outputDirectory);
        // Then
        String[] files = outputDirectory.toFile().list();
        assertThat(files).hasSize(4).containsAll(expectedFiles);
        Path expectedFilesDirectory = Paths.get(AsciidocConverterTest.class.getResource("/expected/markdown/idxref").toURI());
        DiffUtils.assertThatAllFilesAreEqual(expectedFilesDirectory, outputDirectory, "testWithInterDocumentCrossReferences.html");
    }

    @Test
    public void testWithSeparatedDefinitions() throws IOException, URISyntaxException {
        // Given
        Path file = Paths.get(MarkdownConverterTest.class.getResource("/yaml/swagger_petstore.yaml").toURI());
        Path outputDirectory = Paths.get("build/test/markdown/generated");
        FileUtils.deleteQuietly(outputDirectory.toFile());
        // When
        Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder().withSeparatedDefinitions().withMarkupLanguage(MARKDOWN).build();
        Swagger2MarkupConverter.from(file).withConfig(config).build().toFolder(outputDirectory);
        // Then
        String[] files = outputDirectory.toFile().list();
        expectedFiles.add("definitions");
        assertThat(files).hasSize(5).containsAll(expectedFiles);
        Path definitionsDirectory = outputDirectory.resolve("definitions");
        String[] definitions = definitionsDirectory.toFile().list();
        assertThat(definitions).hasSize(5).containsAll(Arrays.asList("Category.md", "Order.md", "Pet.md", "Tag.md", "User.md"));
    }

    @Test
    public void testWithResponseHeaders() throws IOException, URISyntaxException {
        // Given
        Path file = Paths.get(AsciidocConverterTest.class.getResource("/yaml/swagger_response_headers.yaml").toURI());
        Path outputDirectory = Paths.get("build/test/markdown/response_headers");
        FileUtils.deleteQuietly(outputDirectory.toFile());
        // When
        Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder().withMarkupLanguage(MARKDOWN).build();
        Swagger2MarkupConverter.from(file).withConfig(config).build().toFolder(outputDirectory);
        // Then
        String[] files = outputDirectory.toFile().list();
        assertThat(files).hasSize(4).containsAll(expectedFiles);
        Path expectedFilesDirectory = Paths.get(AsciidocConverterTest.class.getResource("/expected/markdown/response_headers").toURI());
        DiffUtils.assertThatAllFilesAreEqual(expectedFilesDirectory, outputDirectory, "testWithResponseHeaders.html");
    }

    @Test
    public void testHandlesComposition() throws IOException, URISyntaxException {
        // Given
        Path file = Paths.get(MarkdownConverterTest.class.getResource("/yaml/swagger_petstore.yaml").toURI());
        Path outputDirectory = Paths.get("build/test/markdown/generated");
        FileUtils.deleteQuietly(outputDirectory.toFile());
        // When
        Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder().withSeparatedDefinitions().withMarkupLanguage(MARKDOWN).build();
        Swagger2MarkupConverter.from(file).withConfig(config).build().toFolder(outputDirectory);
        // Then
        String[] files = outputDirectory.toFile().list();
        expectedFiles.add("definitions");
        assertThat(files).hasSize(5).containsAll(expectedFiles);
        Path definitionsDirectory = outputDirectory.resolve("definitions");
        MarkdownConverterTest.verifyMarkdownContainsFieldsInTables(definitionsDirectory.resolve("User.md").toFile(), ImmutableMap.<String, Set<String>>builder().put("User", ImmutableSet.of("id", "username", "firstName", "lastName", "email", "password", "phone", "userStatus")).build());
    }

    @Test
    public void testFreeFormWithEmptyCurlyBracket() throws IOException, URISyntaxException {
        // Given
        Path file = Paths.get(MarkdownConverterTest.class.getResource("/yaml/swagger_freeform_with_emtpy_curly_brackets.yaml").toURI());
        Path outputDirectory = Paths.get("build/test/markdown/freeform");
        FileUtils.deleteQuietly(outputDirectory.toFile());
        // When
        Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder().withSeparatedDefinitions().withMarkupLanguage(MARKDOWN).build();
        Swagger2MarkupConverter.from(file).withConfig(config).build().toFolder(outputDirectory);
        // Then
        String[] files = outputDirectory.toFile().list();
        expectedFiles.add("definitions");
        assertThat(files).hasSize(5).containsAll(expectedFiles);
        Path expectedFilesDirectory = Paths.get(MarkdownConverterTest.class.getResource("/expected/markdown/freeform").toURI());
        DiffUtils.assertThatAllFilesAreEqual(expectedFilesDirectory, outputDirectory, "freeform.html");
    }
}

