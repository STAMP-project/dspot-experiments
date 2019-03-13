/**
 * ***********************GO-LICENSE-START*********************************
 * Copyright 2016 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ************************GO-LICENSE-END**********************************
 */
package com.thoughtworks.go.util;


import com.thoughtworks.go.config.GoConfigSchema;
import com.thoughtworks.go.config.registry.ConfigElementImplementationRegistry;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.hamcrest.Matchers;
import org.jdom2.input.JDOMParseException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class XmlUtilsTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ConfigElementImplementationRegistry configElementImplementationRegistry;

    @Test
    public void shouldThrowExceptionWithTranslatedErrorMessage() throws Exception {
        String xmlContent = "<foo name='invalid'/>";
        InputStream inputStream = new ByteArrayInputStream(xmlContent.getBytes());
        try {
            XmlUtils.buildXmlDocument(inputStream, GoConfigSchema.getCurrentSchema(), configElementImplementationRegistry.xsds());
            Assert.fail("Should throw a XsdValidationException");
        } catch (Exception e) {
            Assert.assertThat(e, Matchers.is(Matchers.instanceOf(XsdValidationException.class)));
        }
    }

    @Test
    public void shouldThrowExceptionWhenXmlIsMalformed() throws Exception {
        expectedException.expect(JDOMParseException.class);
        expectedException.expectMessage(Matchers.containsString("Error on line 1: XML document structures must start and end within the same entity"));
        String xmlContent = "<foo name='invalid'";
        XmlUtils.buildXmlDocument(xmlContent, GoConfigSchema.getCurrentSchema());
    }

    @Test
    public void shouldDisableDocTypeDeclarationsWhenValidatingXmlDocuments() throws Exception {
        expectDOCTYPEDisallowedException();
        XmlUtils.buildXmlDocument(xxeFileContent(), GoConfigSchema.getCurrentSchema());
    }

    @Test
    public void shouldDisableDocTypeDeclarationsWhenValidatingXmlDocumentsWithExternalXsds() throws Exception {
        expectDOCTYPEDisallowedException();
        XmlUtils.buildXmlDocument(new ByteArrayInputStream(xxeFileContent().getBytes()), GoConfigSchema.getCurrentSchema(), configElementImplementationRegistry.xsds());
    }
}

