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
import io.swagger.models.Contact;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import org.junit.Test;


public class ContactInfoComponentTest extends AbstractComponentTest {
    private static final String COMPONENT_NAME = "contact_info";

    private Path outputDirectory;

    @Test
    public void testVersionInfoComponent() throws URISyntaxException {
        Contact contact = new Contact().name("TestName").email("test@test.de");
        Swagger2MarkupConverter.Context context = ContextUtils.createContext();
        MarkupDocBuilder markupDocBuilder = context.createMarkupDocBuilder();
        markupDocBuilder = new ContactInfoComponent(context).apply(markupDocBuilder, ContactInfoComponent.parameters(contact, SECTION_TITLE_LEVEL));
        markupDocBuilder.writeToFileWithoutExtension(outputDirectory, StandardCharsets.UTF_8);
        Path expectedFile = getExpectedFile(ContactInfoComponentTest.COMPONENT_NAME);
        DiffUtils.assertThatFileIsEqual(expectedFile, outputDirectory, getReportName(ContactInfoComponentTest.COMPONENT_NAME));
    }
}

