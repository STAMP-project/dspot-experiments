/**
 * Copyright 2018 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thoughtworks.go.config.parser;


import com.thoughtworks.go.config.ConfigCache;
import com.thoughtworks.go.config.preprocessor.ClassAttributeCache;
import com.thoughtworks.go.config.registry.ConfigElementImplementationRegistry;
import com.thoughtworks.go.security.GoCipher;
import org.jdom2.Element;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;


public class GoConfigClassLoaderTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private ConfigCache configCache;

    @Mock
    private GoCipher goCipher;

    @Mock
    private ConfigElementImplementationRegistry registry;

    @Mock
    private ConfigReferenceElements referenceElements;

    @Test
    public void shouldErrorOutIfElementDoesNotHaveConfigTagAnnotation() {
        final Element element = new Element("cruise");
        final GoConfigClassLoader<ConfigWithoutAnnotation> loader = GoConfigClassLoader.classParser(element, ConfigWithoutAnnotation.class, configCache, goCipher, registry, referenceElements);
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Unable to parse element <cruise> for class ConfigWithoutAnnotation");
        loader.parse();
    }

    @Test
    public void shouldContinueParsingWhenElementHasConfigTagAnnotation() {
        final Element element = new Element("example");
        Mockito.when(configCache.getFieldCache()).thenReturn(new ClassAttributeCache.FieldCache());
        final GoConfigClassLoader<ConfigWithConfigTagAnnotation> loader = GoConfigClassLoader.classParser(element, ConfigWithConfigTagAnnotation.class, configCache, goCipher, registry, referenceElements);
        final ConfigWithConfigTagAnnotation configWithConfigTagAnnotation = loader.parse();
        Assert.assertNotNull(configWithConfigTagAnnotation);
    }

    @Test
    public void shouldContinueParsingWhenConfigClassHasValidAttributeAwareConfigTagAnnotation() {
        final Element element = new Element("example");
        element.setAttribute("type", "example-type");
        Mockito.when(configCache.getFieldCache()).thenReturn(new ClassAttributeCache.FieldCache());
        final GoConfigClassLoader<ConfigWithAttributeAwareConfigTagAnnotation> loader = GoConfigClassLoader.classParser(element, ConfigWithAttributeAwareConfigTagAnnotation.class, configCache, goCipher, registry, referenceElements);
        final ConfigWithAttributeAwareConfigTagAnnotation configWithConfigTagAnnotation = loader.parse();
        Assert.assertNotNull(configWithConfigTagAnnotation);
    }

    @Test
    public void shouldErrorOutWhenConfigClassHasAttributeAwareConfigTagAnnotationButAttributeValueIsNotMatching() {
        final Element element = new Element("example");
        element.setAttribute("type", "foo-bar");
        Mockito.when(configCache.getFieldCache()).thenReturn(new ClassAttributeCache.FieldCache());
        final GoConfigClassLoader<ConfigWithAttributeAwareConfigTagAnnotation> loader = GoConfigClassLoader.classParser(element, ConfigWithAttributeAwareConfigTagAnnotation.class, configCache, goCipher, registry, referenceElements);
        thrown.expect(RuntimeException.class);
        thrown.expectMessage(("Unable to determine type to generate. Type: com.thoughtworks.go.config.parser.ConfigWithAttributeAwareConfigTagAnnotation Element: \n" + "\t<example type=\"foo-bar\" />"));
        loader.parse();
    }

    @Test
    public void shouldErrorOutWhenConfigClassHasAttributeAwareConfigTagAnnotationButAttributeIsNotPresent() {
        final Element element = new Element("example");
        Mockito.when(configCache.getFieldCache()).thenReturn(new ClassAttributeCache.FieldCache());
        final GoConfigClassLoader<ConfigWithAttributeAwareConfigTagAnnotation> loader = GoConfigClassLoader.classParser(element, ConfigWithAttributeAwareConfigTagAnnotation.class, configCache, goCipher, registry, referenceElements);
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Expected attribute `type` to be present for \n\t<example />");
        loader.parse();
    }

    @Test
    public void shouldErrorOutWhenConfigClassHasAttributeAwareConfigTagAnnotationButAttributeIsMissingInConfig() {
        final Element element = new Element("example");
        Mockito.when(configCache.getFieldCache()).thenReturn(new ClassAttributeCache.FieldCache());
        final GoConfigClassLoader<AttributeAwareConfigTagWithBlankAttributeValue> loader = GoConfigClassLoader.classParser(element, AttributeAwareConfigTagWithBlankAttributeValue.class, configCache, goCipher, registry, referenceElements);
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Type 'com.thoughtworks.go.config.parser.AttributeAwareConfigTagWithBlankAttributeValue' has invalid configuration for @AttributeAwareConfigTag. It must have `attribute` with non blank value.");
        loader.parse();
    }

    @Test
    public void shouldErrorOutWhenAttributeAwareConfigTagHasAttributeWithBlankValue() {
        final Element element = new Element("example");
        Mockito.when(configCache.getFieldCache()).thenReturn(new ClassAttributeCache.FieldCache());
        final GoConfigClassLoader<ConfigWithAttributeAwareConfigTagAnnotation> loader = GoConfigClassLoader.classParser(element, ConfigWithAttributeAwareConfigTagAnnotation.class, configCache, goCipher, registry, referenceElements);
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Expected attribute `type` to be present for \n\t<example />.");
        loader.parse();
    }

    @Test
    public void shouldErrorOutWhenAttributeAwareConfigTagClassHasConfigAttributeWithSameName() {
        final Element element = new Element("example");
        element.setAttribute("type", "example-type");
        Mockito.when(configCache.getFieldCache()).thenReturn(new ClassAttributeCache.FieldCache());
        final GoConfigClassLoader<AttributeAwareConfigTagHasConfigAttributeWithSameName> loader = GoConfigClassLoader.classParser(element, AttributeAwareConfigTagHasConfigAttributeWithSameName.class, configCache, goCipher, registry, referenceElements);
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Attribute `type` is not allowed in com.thoughtworks.go.config.parser.AttributeAwareConfigTagHasConfigAttributeWithSameName. You cannot use @ConfigAttribute  annotation with attribute name `type` when @AttributeAwareConfigTag is configured with same name.");
        loader.parse();
    }
}

