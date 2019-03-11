/**
 * Copyright 2018-present Facebook, Inc.
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
package com.facebook.litho.specmodels.model;


import TypeName.BOOLEAN;
import TypeName.CHAR;
import TypeName.INT;
import TypeName.LONG;
import com.squareup.javapoet.FieldSpec;
import javax.lang.model.element.Modifier;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests {@link FieldsValidation}.
 */
public class FieldsValidationTest {
    private static final String FIELD_TEST_NAME = "fieldTestName";

    private final SpecModel layoutSpecModel = Mockito.mock(LayoutSpecModel.class);

    private final Object representedObject = new Object();

    private final FieldModel fieldPrivateFinal = new FieldModel(FieldSpec.builder(BOOLEAN, FieldsValidationTest.FIELD_TEST_NAME, Modifier.PRIVATE, Modifier.FINAL).build(), representedObject);

    private final FieldModel fieldPrivateStatic = new FieldModel(FieldSpec.builder(CHAR, FieldsValidationTest.FIELD_TEST_NAME, Modifier.PRIVATE, Modifier.STATIC).build(), representedObject);

    private final FieldModel fieldPrivateStaticFinal = new FieldModel(FieldSpec.builder(LONG, FieldsValidationTest.FIELD_TEST_NAME, Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL).build(), representedObject);

    private final FieldModel fieldPrivate = new FieldModel(FieldSpec.builder(INT, FieldsValidationTest.FIELD_TEST_NAME, Modifier.PRIVATE).build(), representedObject);

    @Test
    public void testNoFields() {
        verifyErrors(0);
    }

    @Test
    public void testNoStaticPresentFinal() {
        verifyErrors(1, fieldPrivateFinal, fieldPrivateStaticFinal);
    }

    @Test
    public void testPresentStaticNoFinal() {
        verifyErrors(1, fieldPrivateStatic, fieldPrivateStaticFinal);
    }

    @Test
    public void testPresentStaticFinal() {
        verifyErrors(0, fieldPrivateStaticFinal, fieldPrivateStaticFinal, fieldPrivateStaticFinal);
    }

    @Test
    public void testNoStaticNoFinal() {
        verifyErrors(3, fieldPrivateStatic, fieldPrivateFinal, fieldPrivate);
        verifyErrors(1, fieldPrivateFinal);
        verifyErrors(1, fieldPrivateStatic);
        verifyErrors(1, fieldPrivate);
    }
}

