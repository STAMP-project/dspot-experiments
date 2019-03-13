/**
 * Copyright 2002-2015 the original author or authors.
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


import org.junit.Test;


/**
 * Abstract base class for tests involving concrete implementations of
 * {@link AbstractAliasAwareAnnotationAttributeExtractor}.
 *
 * @author Sam Brannen
 * @since 4.2.1
 */
public abstract class AbstractAliasAwareAnnotationAttributeExtractorTestCase {
    @Test
    public void getAttributeValueForImplicitAliases() throws Exception {
        assertGetAttributeValueForImplicitAliases(AnnotationUtilsTests.GroovyImplicitAliasesContextConfigClass.class, "groovyScript");
        assertGetAttributeValueForImplicitAliases(AnnotationUtilsTests.XmlImplicitAliasesContextConfigClass.class, "xmlFile");
        assertGetAttributeValueForImplicitAliases(AnnotationUtilsTests.ValueImplicitAliasesContextConfigClass.class, "value");
        assertGetAttributeValueForImplicitAliases(AnnotationUtilsTests.Location1ImplicitAliasesContextConfigClass.class, "location1");
        assertGetAttributeValueForImplicitAliases(AnnotationUtilsTests.Location2ImplicitAliasesContextConfigClass.class, "location2");
        assertGetAttributeValueForImplicitAliases(AnnotationUtilsTests.Location3ImplicitAliasesContextConfigClass.class, "location3");
    }
}

