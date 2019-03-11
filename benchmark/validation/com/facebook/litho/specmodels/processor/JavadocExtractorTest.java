/**
 * Copyright 2014-present Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.litho.specmodels.processor;


import com.facebook.litho.specmodels.internal.ImmutableList;
import com.facebook.litho.specmodels.model.PropJavadocModel;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests {@link JavadocExtractor}
 */
public class JavadocExtractorTest {
    private final Elements mElements = Mockito.mock(Elements.class);

    private final TypeElement mTypeElement = Mockito.mock(TypeElement.class);

    @Test
    public void testClassJavadoc() {
        String classJavadoc = JavadocExtractor.getClassJavadoc(mElements, mTypeElement);
        assertThat(classJavadoc).isEqualTo("Test javadoc\n");
    }

    @Test
    public void testPropsJavadoc() {
        ImmutableList<PropJavadocModel> propJavadocs = JavadocExtractor.getPropJavadocs(mElements, mTypeElement);
        assertThat(propJavadocs).hasSize(1);
        assertThat(propJavadocs.get(0).propName).isEqualTo("testProp");
        assertThat(propJavadocs.get(0).javadoc).isEqualTo("prop for testing");
    }
}

