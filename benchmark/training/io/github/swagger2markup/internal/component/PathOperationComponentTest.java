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
import io.github.swagger2markup.Swagger2MarkupConfig;
import io.github.swagger2markup.Swagger2MarkupConverter;
import io.github.swagger2markup.assertions.DiffUtils;
import io.github.swagger2markup.builder.Swagger2MarkupConfigBuilder;
import io.github.swagger2markup.internal.utils.PathUtils;
import io.github.swagger2markup.markup.builder.MarkupDocBuilder;
import io.github.swagger2markup.model.PathOperation;
import io.swagger.models.Swagger;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.junit.Test;


public class PathOperationComponentTest extends AbstractComponentTest {
    @Test
    public void testPathOperationComponent() throws URISyntaxException {
        String COMPONENT_NAME = "path_operation";
        Path outputDirectory = getOutputFile(COMPONENT_NAME);
        FileUtils.deleteQuietly(outputDirectory.toFile());
        // Given
        Path file = Paths.get(PathOperationComponentTest.class.getResource("/yaml/swagger_petstore.yaml").toURI());
        Swagger2MarkupConverter converter = Swagger2MarkupConverter.from(file).build();
        Swagger swagger = converter.getContext().getSwagger();
        io.swagger.models.Path path = swagger.getPaths().get("/pets");
        List<PathOperation> pathOperations = PathUtils.toPathOperationsList("/pets", path);
        Swagger2MarkupConverter.Context context = converter.getContext();
        MarkupDocBuilder markupDocBuilder = context.createMarkupDocBuilder();
        // When
        markupDocBuilder = new PathOperationComponent(context, new io.github.swagger2markup.internal.resolver.DefinitionDocumentResolverFromOperation(context), new io.github.swagger2markup.internal.resolver.SecurityDocumentResolver(context)).apply(markupDocBuilder, PathOperationComponent.parameters(pathOperations.get(0)));
        markupDocBuilder.writeToFileWithoutExtension(outputDirectory, StandardCharsets.UTF_8);
        // Then
        Path expectedFile = getExpectedFile(COMPONENT_NAME);
        DiffUtils.assertThatFileIsEqual(expectedFile, outputDirectory, getReportName(COMPONENT_NAME));
    }

    @Test
    public void testInlineSchema() throws URISyntaxException {
        String COMPONENT_NAME = "path_operation_inline_schema";
        Path outputDirectory = getOutputFile(COMPONENT_NAME);
        FileUtils.deleteQuietly(outputDirectory.toFile());
        // Given
        Path file = Paths.get(PathOperationComponentTest.class.getResource("/yaml/swagger_inlineSchema.yaml").toURI());
        Swagger2MarkupConverter converter = Swagger2MarkupConverter.from(file).build();
        Swagger swagger = converter.getContext().getSwagger();
        io.swagger.models.Path path = swagger.getPaths().get("/LaunchCommand");
        List<PathOperation> pathOperations = PathUtils.toPathOperationsList("/LaunchCommand", path);
        Swagger2MarkupConverter.Context context = converter.getContext();
        MarkupDocBuilder markupDocBuilder = context.createMarkupDocBuilder();
        // When
        markupDocBuilder = new PathOperationComponent(context, new io.github.swagger2markup.internal.resolver.DefinitionDocumentResolverFromOperation(context), new io.github.swagger2markup.internal.resolver.SecurityDocumentResolver(context)).apply(markupDocBuilder, PathOperationComponent.parameters(pathOperations.get(0)));
        markupDocBuilder.writeToFileWithoutExtension(outputDirectory, StandardCharsets.UTF_8);
        // Then
        Path expectedFile = getExpectedFile(COMPONENT_NAME);
        DiffUtils.assertThatFileIsEqual(expectedFile, outputDirectory, getReportName(COMPONENT_NAME));
    }

    @Test
    public void testWithPathParamExample() throws URISyntaxException {
        String COMPONENT_NAME = "path_operation_with_path_param_example";
        Path outputDirectory = getOutputFile(COMPONENT_NAME);
        FileUtils.deleteQuietly(outputDirectory.toFile());
        Map<String, String> configMap = new HashMap<>();
        configMap.put("swagger2markup.generatedExamplesEnabled", "true");// enable example

        // Given
        Path file = Paths.get(PathOperationComponentTest.class.getResource("/yaml/swagger_petstore.yaml").toURI());
        Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder(configMap).build();
        Swagger2MarkupConverter converter = Swagger2MarkupConverter.from(file).withConfig(config).build();
        Swagger swagger = converter.getContext().getSwagger();
        io.swagger.models.Path path = swagger.getPaths().get("/pets/{petId}");
        List<PathOperation> pathOperations = PathUtils.toPathOperationsList("/pets/{petId}", path);
        Swagger2MarkupConverter.Context context = converter.getContext();
        MarkupDocBuilder markupDocBuilder = context.createMarkupDocBuilder();
        // When
        markupDocBuilder = new PathOperationComponent(context, new io.github.swagger2markup.internal.resolver.DefinitionDocumentResolverFromOperation(context), new io.github.swagger2markup.internal.resolver.SecurityDocumentResolver(context)).apply(markupDocBuilder, PathOperationComponent.parameters(pathOperations.get(0)));
        markupDocBuilder.writeToFileWithoutExtension(outputDirectory, StandardCharsets.UTF_8);
        // Then
        Path expectedFile = getExpectedFile(COMPONENT_NAME);
        DiffUtils.assertThatFileIsEqual(expectedFile, outputDirectory, getReportName(COMPONENT_NAME));
    }

    @Test
    public void testWithQueryParamExample() throws URISyntaxException {
        String COMPONENT_NAME = "path_operation_with_query_param_example";
        Path outputDirectory = getOutputFile(COMPONENT_NAME);
        FileUtils.deleteQuietly(outputDirectory.toFile());
        Map<String, String> configMap = new HashMap<>();
        configMap.put("swagger2markup.generatedExamplesEnabled", "true");// enable example

        // Given
        Path file = Paths.get(PathOperationComponentTest.class.getResource("/yaml/swagger_petstore.yaml").toURI());
        Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder(configMap).build();
        Swagger2MarkupConverter converter = Swagger2MarkupConverter.from(file).withConfig(config).build();
        Swagger swagger = converter.getContext().getSwagger();
        io.swagger.models.Path path = swagger.getPaths().get("/pets/findByTags");
        List<PathOperation> pathOperations = PathUtils.toPathOperationsList("/pets/findByTags", path);
        Swagger2MarkupConverter.Context context = converter.getContext();
        MarkupDocBuilder markupDocBuilder = context.createMarkupDocBuilder();
        // When
        markupDocBuilder = new PathOperationComponent(context, new io.github.swagger2markup.internal.resolver.DefinitionDocumentResolverFromOperation(context), new io.github.swagger2markup.internal.resolver.SecurityDocumentResolver(context)).apply(markupDocBuilder, PathOperationComponent.parameters(pathOperations.get(0)));
        markupDocBuilder.writeToFileWithoutExtension(outputDirectory, StandardCharsets.UTF_8);
        // Then
        Path expectedFile = getExpectedFile(COMPONENT_NAME);
        DiffUtils.assertThatFileIsEqual(expectedFile, outputDirectory, getReportName(COMPONENT_NAME));
    }

    @Test
    public void testWithBodyParamExample() throws URISyntaxException {
        String COMPONENT_NAME = "path_operation_with_body_param_example";
        Path outputDirectory = getOutputFile(COMPONENT_NAME);
        FileUtils.deleteQuietly(outputDirectory.toFile());
        Map<String, String> configMap = new HashMap<>();
        configMap.put("swagger2markup.generatedExamplesEnabled", "true");// enable example

        // Given
        Path file = Paths.get(PathOperationComponentTest.class.getResource("/yaml/swagger_petstore_body_examples.yaml").toURI());
        Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder(configMap).build();
        Swagger2MarkupConverter converter = Swagger2MarkupConverter.from(file).withConfig(config).build();
        Swagger swagger = converter.getContext().getSwagger();
        io.swagger.models.Path path = swagger.getPaths().get("/users");
        List<PathOperation> pathOperations = PathUtils.toPathOperationsList("/users", path);
        Swagger2MarkupConverter.Context context = converter.getContext();
        MarkupDocBuilder markupDocBuilder = context.createMarkupDocBuilder();
        // When
        markupDocBuilder = new PathOperationComponent(context, new io.github.swagger2markup.internal.resolver.DefinitionDocumentResolverFromOperation(context), new io.github.swagger2markup.internal.resolver.SecurityDocumentResolver(context)).apply(markupDocBuilder, PathOperationComponent.parameters(pathOperations.get(0)));
        markupDocBuilder.writeToFileWithoutExtension(outputDirectory, StandardCharsets.UTF_8);
        // Then
        Path expectedFile = getExpectedFile(COMPONENT_NAME);
        DiffUtils.assertThatFileIsEqual(expectedFile, outputDirectory, getReportName(COMPONENT_NAME));
    }
}

