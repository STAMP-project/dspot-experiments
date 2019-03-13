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
import io.swagger.models.Info;
import io.swagger.models.License;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import org.junit.Test;


public class LicenseInfoComponentTest extends AbstractComponentTest {
    private static final String COMPONENT_NAME = "license_info";

    private Path outputDirectory;

    @Test
    public void testLicenseInfoComponent() throws URISyntaxException {
        Info info = new Info().license(new License().name("Apache 2.0").url("http://www.apache.org/licenses/LICENSE-2.0")).termsOfService("Bla bla bla");
        Swagger2MarkupConverter.Context context = ContextUtils.createContext();
        MarkupDocBuilder markupDocBuilder = context.createMarkupDocBuilder();
        markupDocBuilder = new LicenseInfoComponent(context).apply(markupDocBuilder, LicenseInfoComponent.parameters(info, SECTION_TITLE_LEVEL));
        markupDocBuilder.writeToFileWithoutExtension(outputDirectory, StandardCharsets.UTF_8);
        Path expectedFile = getExpectedFile(LicenseInfoComponentTest.COMPONENT_NAME);
        DiffUtils.assertThatFileIsEqual(expectedFile, outputDirectory, getReportName(LicenseInfoComponentTest.COMPONENT_NAME));
    }
}

