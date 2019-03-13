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


import OverviewDocument.SECTION_TITLE_LEVEL;
import Swagger2MarkupConverter.Context;
import io.github.swagger2markup.Swagger2MarkupConverter;
import io.github.swagger2markup.assertions.DiffUtils;
import io.github.swagger2markup.helper.ContextUtils;
import io.github.swagger2markup.markup.builder.MarkupDocBuilder;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;


public class ProducesComponentTest extends AbstractComponentTest {
    private static final String COMPONENT_NAME = "produces";

    private Path outputDirectory;

    @Test
    public void testProducesComponent() throws URISyntaxException {
        List<String> produces = new ArrayList<>();
        produces.add("application/json");
        produces.add("application/xml");
        Swagger2MarkupConverter.Context context = ContextUtils.createContext();
        MarkupDocBuilder markupDocBuilder = context.createMarkupDocBuilder();
        markupDocBuilder = new ProducesComponent(context).apply(markupDocBuilder, ProducesComponent.parameters(produces, SECTION_TITLE_LEVEL));
        markupDocBuilder.writeToFileWithoutExtension(outputDirectory, StandardCharsets.UTF_8);
        Path expectedFile = getExpectedFile(ProducesComponentTest.COMPONENT_NAME);
        DiffUtils.assertThatFileIsEqual(expectedFile, outputDirectory, getReportName(ProducesComponentTest.COMPONENT_NAME));
    }
}

