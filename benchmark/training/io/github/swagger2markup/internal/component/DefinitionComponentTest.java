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
package io.github.swagger2markup.internal.component;


import Swagger2MarkupConverter.Context;
import io.github.swagger2markup.Swagger2MarkupConverter;
import io.github.swagger2markup.assertions.DiffUtils;
import io.github.swagger2markup.markup.builder.MarkupDocBuilder;
import io.swagger.models.Model;
import io.swagger.models.Swagger;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;


public class DefinitionComponentTest extends AbstractComponentTest {
    private static final String COMPONENT_NAME = "definition";

    private Path outputDirectory;

    @Test
    public void testDefinitionComponent() throws URISyntaxException {
        // Given
        Path file = Paths.get(DefinitionComponentTest.class.getResource("/yaml/swagger_petstore.yaml").toURI());
        Swagger2MarkupConverter converter = Swagger2MarkupConverter.from(file).build();
        Swagger swagger = converter.getContext().getSwagger();
        Model petModel = swagger.getDefinitions().get("Pet");
        Swagger2MarkupConverter.Context context = converter.getContext();
        MarkupDocBuilder markupDocBuilder = context.createMarkupDocBuilder();
        // When
        markupDocBuilder = new DefinitionComponent(context, new io.github.swagger2markup.internal.resolver.DefinitionDocumentResolverFromDefinition(context)).apply(markupDocBuilder, DefinitionComponent.parameters("Pet", petModel, 2));
        markupDocBuilder.writeToFileWithoutExtension(outputDirectory, StandardCharsets.UTF_8);
        // Then
        Path expectedFile = getExpectedFile(DefinitionComponentTest.COMPONENT_NAME);
        DiffUtils.assertThatFileIsEqual(expectedFile, outputDirectory, getReportName(DefinitionComponentTest.COMPONENT_NAME));
    }
}

