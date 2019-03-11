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


import ClassNames.DIFF;
import DiffValidation.MISSING_TYPE_PARAMETER_ERROR;
import DiffValidation.PROP_MISMATCH_ERROR;
import DiffValidation.STATE_MISMATCH_ERROR;
import TypeName.BOOLEAN;
import TypeName.INT;
import com.facebook.litho.annotations.Prop;
import com.facebook.litho.annotations.State;
import com.facebook.litho.specmodels.internal.ImmutableList;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import java.util.List;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests {@link DiffValidation}
 */
public class DiffValidationTest {
    private SpecModel mSpecModel;

    private StateParamModel mStateModel;

    private PropModel mPropModel;

    private RenderDataDiffModel mDiffModel;

    private Object mDiffRepresentedObject;

    @Test
    public void testNameDoesntExist() {
        Mockito.when(mDiffModel.getName()).thenReturn("doesNotExist");
        List<SpecModelValidationError> validationErrors = DiffValidation.validate(mSpecModel);
        assertSingleError(validationErrors, STATE_MISMATCH_ERROR);
    }

    @Test
    public void testDiffModelHasNoTypeParameter() {
        Mockito.when(mDiffModel.getTypeName()).thenReturn(DIFF.annotated(AnnotationSpec.builder(State.class).build()));
        List<SpecModelValidationError> validationErrors = DiffValidation.validate(mSpecModel);
        assertSingleError(validationErrors, MISSING_TYPE_PARAMETER_ERROR);
    }

    @Test
    public void testDiffModelHasDifferentParameterFromState() {
        Mockito.when(mDiffModel.getTypeName()).thenReturn(ParameterizedTypeName.get(DIFF, BOOLEAN.box()).annotated(AnnotationSpec.builder(State.class).build()));
        List<SpecModelValidationError> validationErrors = DiffValidation.validate(mSpecModel);
        assertSingleError(validationErrors, STATE_MISMATCH_ERROR);
    }

    @Test
    public void testDiffModelHasDifferentParameterFromProp() {
        Mockito.when(mDiffModel.getName()).thenReturn("propName");
        Mockito.when(mDiffModel.getTypeName()).thenReturn(ParameterizedTypeName.get(DIFF, BOOLEAN.box()).annotated(AnnotationSpec.builder(Prop.class).build()));
        Mockito.when(mDiffModel.getAnnotations()).thenReturn(ImmutableList.of(DiffValidationTest.annotation(Prop.class)));
        List<SpecModelValidationError> validationErrors = DiffValidation.validate(mSpecModel);
        assertSingleError(validationErrors, PROP_MISMATCH_ERROR);
    }

    @Test
    public void testNoErrorState() {
        List<SpecModelValidationError> validationErrors = DiffValidation.validate(mSpecModel);
        assertThat(validationErrors).hasSize(0);
    }

    @Test
    public void testNoErrorProp() {
        Mockito.when(mDiffModel.getName()).thenReturn("propName");
        Mockito.when(mDiffModel.getTypeName()).thenReturn(ParameterizedTypeName.get(DIFF, INT.box()).annotated(AnnotationSpec.builder(Prop.class).build()));
        Mockito.when(mDiffModel.getAnnotations()).thenReturn(ImmutableList.of(DiffValidationTest.annotation(Prop.class)));
        List<SpecModelValidationError> validationErrors = DiffValidation.validate(mSpecModel);
        assertThat(validationErrors).hasSize(0);
    }
}

