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
package com.facebook.litho.specmodels.model;


import ClassNames.COMPONENT;
import TypeName.INT;
import java.util.List;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests {@link SimpleNameDelegateValidation}
 */
public class SimpleNameDelegateValidationTest {
    private LayoutSpecModel mSpecModel;

    private PropModel mPropModel;

    @Test
    public void testNoDelegate() {
        Mockito.when(mPropModel.getName()).thenReturn("child");
        Mockito.when(mPropModel.getTypeName()).thenReturn(COMPONENT);
        Mockito.when(mSpecModel.getSimpleNameDelegate()).thenReturn("");
        List<SpecModelValidationError> validationErrors = SimpleNameDelegateValidation.validate(mSpecModel);
        assertThat(validationErrors).isEmpty();
    }

    @Test
    public void testCorrectUsage() {
        Mockito.when(mPropModel.getName()).thenReturn("child");
        Mockito.when(mPropModel.getTypeName()).thenReturn(COMPONENT);
        Mockito.when(mSpecModel.getSimpleNameDelegate()).thenReturn("child");
        List<SpecModelValidationError> validationErrors = SimpleNameDelegateValidation.validate(mSpecModel);
        assertThat(validationErrors).isEmpty();
    }

    @Test
    public void testMissingProp() {
        Mockito.when(mPropModel.getName()).thenReturn("delegate");
        Mockito.when(mPropModel.getTypeName()).thenReturn(COMPONENT);
        Mockito.when(mSpecModel.getSimpleNameDelegate()).thenReturn("child");
        List<SpecModelValidationError> validationErrors = SimpleNameDelegateValidation.validate(mSpecModel);
        assertThat(validationErrors).hasSize(1);
        assertThat(validationErrors.get(0).message).contains("Did not find a @Prop named 'child'");
    }

    @Test
    public void testIncorrectPropType() {
        Mockito.when(mPropModel.getName()).thenReturn("child");
        Mockito.when(mPropModel.getTypeName()).thenReturn(INT);
        Mockito.when(mSpecModel.getSimpleNameDelegate()).thenReturn("child");
        List<SpecModelValidationError> validationErrors = SimpleNameDelegateValidation.validate(mSpecModel);
        assertThat(validationErrors).hasSize(1);
        assertThat(validationErrors.get(0).message).contains("@Prop 'child' has type int");
    }
}

