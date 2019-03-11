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
package io.github.swagger2markup.internal.resolver;


import MarkupLanguage.MARKDOWN;
import Swagger2MarkupConverter.Context;
import io.github.swagger2markup.Swagger2MarkupConfig;
import io.github.swagger2markup.Swagger2MarkupConverter;
import io.github.swagger2markup.builder.Swagger2MarkupConfigBuilder;
import io.github.swagger2markup.helper.ContextUtils;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import org.junit.Test;


public class DefinitionDocumentResolverFromOperationTest {
    private final String fileSeparator = FileSystems.getDefault().getSeparator();

    @Test
    public void testDefault() {
        Swagger2MarkupConverter.Context context = ContextUtils.createContext();
        assertThat(apply("DefinitionName")).isNull();
    }

    @Test
    public void testWithSeparatedDefinitions() {
        Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder().withSeparatedDefinitions().build();
        Swagger2MarkupConverter.Context context = ContextUtils.createContext(config);
        assertThat(apply("DefinitionName")).isNull();
    }

    @Test
    public void testWithSeparatedOperationsAndInterDocumentCrossReferences() {
        Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder().withSeparatedOperations().withInterDocumentCrossReferences().build();
        Swagger2MarkupConverter.Context context = ContextUtils.createContext(config);
        context.setOutputPath(Paths.get("/tmp"));
        assertThat(apply("DefinitionName")).isEqualTo(((".." + (fileSeparator)) + "definitions.adoc"));
    }

    @Test
    public void testWithSeparatedOperationsAndSeparatedDefinitionsAndInterDocumentCrossReferences() {
        Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder().withSeparatedOperations().withSeparatedDefinitions().withInterDocumentCrossReferences().build();
        Swagger2MarkupConverter.Context context = ContextUtils.createContext(config);
        context.setOutputPath(Paths.get("/tmp"));
        assertThat(apply("DefinitionName")).isEqualTo(((((".." + (fileSeparator)) + "definitions") + (fileSeparator)) + "DefinitionName.adoc"));
    }

    @Test
    public void testWithInterDocumentCrossReferences() {
        Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder().withInterDocumentCrossReferences().build();
        Swagger2MarkupConverter.Context context = ContextUtils.createContext(config);
        context.setOutputPath(Paths.get("/tmp"));
        assertThat(apply("DefinitionName")).isEqualTo("definitions.adoc");
    }

    @Test
    public void testWithInterDocumentCrossReferencesAndNoOutputPath() {
        Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder().withInterDocumentCrossReferences().build();
        Swagger2MarkupConverter.Context context = ContextUtils.createContext(config);
        assertThat(apply("DefinitionName")).isNull();
    }

    @Test
    public void testWithInterDocumentCrossReferencesAndPrefix() {
        Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder().withInterDocumentCrossReferences("prefix_").build();
        Swagger2MarkupConverter.Context context = ContextUtils.createContext(config);
        context.setOutputPath(Paths.get("/tmp"));
        assertThat(apply("DefinitionName")).isEqualTo("prefix_definitions.adoc");
    }

    @Test
    public void testWithInterDocumentCrossReferencesAndMarkdown() {
        Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder().withInterDocumentCrossReferences().withMarkupLanguage(MARKDOWN).build();
        Swagger2MarkupConverter.Context context = ContextUtils.createContext(config);
        context.setOutputPath(Paths.get("/tmp"));
        assertThat(apply("DefinitionName")).isEqualTo("definitions.md");
    }
}

