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


import SpecElementType.JAVA_CLASS;
import SpecElementType.KOTLIN_SINGLETON;
import com.google.testing.compile.CompilationRule;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import org.junit.Rule;
import org.junit.Test;


/**
 * Tests {@link SpecElementTypeDeterminator}
 */
public class SpecElementTypeDeterminatorTest {
    @Rule
    public CompilationRule mCompilationRule = new CompilationRule();

    /**
     * We can't test with a real kotlin object here, so we'll replicate the structure that we test
     * for.
     */
    public static class FakeKotlinSingleton {
        public static final SpecElementTypeDeterminatorTest.FakeKotlinSingleton INSTANCE = null;
    }

    @Test
    public void testNormalClass() {
        final Elements elements = mCompilationRule.getElements();
        final TypeElement typeElement = elements.getTypeElement(String.class.getName());
        assertThat(SpecElementTypeDeterminator.determine(typeElement)).isEqualTo(JAVA_CLASS);
    }

    @Test
    public void testKotlinSingleton() {
        final Elements elements = mCompilationRule.getElements();
        final TypeElement typeElement = elements.getTypeElement(SpecElementTypeDeterminatorTest.FakeKotlinSingleton.class.getCanonicalName());
        assertThat(SpecElementTypeDeterminator.determine(typeElement)).isEqualTo(KOTLIN_SINGLETON);
    }
}

