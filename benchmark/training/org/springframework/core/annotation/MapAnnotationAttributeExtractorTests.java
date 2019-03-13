/**
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.core.annotation;


import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link MapAnnotationAttributeExtractor}.
 *
 * @author Sam Brannen
 * @since 4.2.1
 */
@SuppressWarnings("serial")
public class MapAnnotationAttributeExtractorTests extends AbstractAliasAwareAnnotationAttributeExtractorTestCase {
    @Test
    public void enrichAndValidateAttributesWithImplicitAliasesAndMinimalAttributes() throws Exception {
        Map<String, Object> attributes = new HashMap<>();
        Map<String, Object> expectedAttributes = new HashMap<String, Object>() {
            {
                put("groovyScript", "");
                put("xmlFile", "");
                put("value", "");
                put("location1", "");
                put("location2", "");
                put("location3", "");
                put("nonAliasedAttribute", "");
                put("configClass", Object.class);
            }
        };
        assertEnrichAndValidateAttributes(attributes, expectedAttributes);
    }

    @Test
    public void enrichAndValidateAttributesWithImplicitAliases() throws Exception {
        Map<String, Object> attributes = new HashMap<String, Object>() {
            {
                put("groovyScript", "groovy!");
            }
        };
        Map<String, Object> expectedAttributes = new HashMap<String, Object>() {
            {
                put("groovyScript", "groovy!");
                put("xmlFile", "groovy!");
                put("value", "groovy!");
                put("location1", "groovy!");
                put("location2", "groovy!");
                put("location3", "groovy!");
                put("nonAliasedAttribute", "");
                put("configClass", Object.class);
            }
        };
        assertEnrichAndValidateAttributes(attributes, expectedAttributes);
    }

    @Test
    public void enrichAndValidateAttributesWithSingleElementThatOverridesAnArray() {
        Map<String, Object> attributes = new HashMap<String, Object>() {
            {
                // Intentionally storing 'value' as a single String instead of an array.
                // put("value", asArray("/foo"));
                put("value", "/foo");
                put("name", "test");
            }
        };
        Map<String, Object> expected = new HashMap<String, Object>() {
            {
                put("value", AnnotationUtilsTests.asArray("/foo"));
                put("path", AnnotationUtilsTests.asArray("/foo"));
                put("name", "test");
                put("method", new AnnotationUtilsTests.RequestMethod[0]);
            }
        };
        MapAnnotationAttributeExtractor extractor = new MapAnnotationAttributeExtractor(attributes, AnnotationUtilsTests.WebMapping.class, null);
        Map<String, Object> enriched = extractor.getSource();
        Assert.assertEquals("attribute map size", expected.size(), enriched.size());
        expected.forEach(( attr, expectedValue) -> Assert.assertThat((("for attribute '" + attr) + "'"), enriched.get(attr), is(expectedValue)));
    }
}

