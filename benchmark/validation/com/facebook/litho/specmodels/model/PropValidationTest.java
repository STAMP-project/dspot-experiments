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


import ClassNames.COMPONENT_LAYOUT;
import ClassNames.DIMENSION;
import ClassNames.LIST;
import ClassNames.PX;
import PropValidation.COMMON_PROP_NAMES;
import PropValidation.VALID_COMMON_PROPS;
import ResType.BOOL;
import ResType.DIMEN_OFFSET;
import ResType.DIMEN_SIZE;
import ResType.NONE;
import TypeName.BOOLEAN;
import TypeName.INT;
import TypeName.OBJECT;
import com.facebook.litho.specmodels.internal.ImmutableList;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import java.util.ArrayList;
import java.util.List;
import javax.lang.model.element.Modifier;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests {@link PropValidation}
 */
public class PropValidationTest {
    private final SpecModel mSpecModel = Mockito.mock(SpecModel.class);

    private final PropModel mPropModel1 = Mockito.mock(PropModel.class);

    private final PropModel mPropModel2 = Mockito.mock(PropModel.class);

    private final Object mRepresentedObject1 = new Object();

    private final Object mRepresentedObject2 = new Object();

    @Test
    public void testTwoPropsWithSameNameButDifferentType() {
        Mockito.when(mPropModel1.getName()).thenReturn("sameName");
        Mockito.when(mPropModel2.getName()).thenReturn("sameName");
        Mockito.when(mPropModel1.getTypeName()).thenReturn(BOOLEAN);
        Mockito.when(mPropModel2.getTypeName()).thenReturn(INT);
        List<SpecModelValidationError> validationErrors = PropValidation.validate(mSpecModel, COMMON_PROP_NAMES, VALID_COMMON_PROPS);
        assertThat(validationErrors).hasSize(1);
        assertThat(validationErrors.get(0).element).isEqualTo(mRepresentedObject1);
        assertThat(validationErrors.get(0).message).isEqualTo(("The prop sameName is defined differently in different methods. Ensure that each " + ("instance of this prop is declared in the same way (this means having the same type, " + "resType and values for isOptional, isCommonProp and overrideCommonPropBehavior).")));
    }

    @Test
    public void testTwoPropsWithSameNameButDifferentIsOptional() {
        Mockito.when(mPropModel1.getName()).thenReturn("sameName");
        Mockito.when(mPropModel2.getName()).thenReturn("sameName");
        Mockito.when(mPropModel1.getTypeName()).thenReturn(INT);
        Mockito.when(mPropModel2.getTypeName()).thenReturn(INT);
        Mockito.when(mPropModel1.isOptional()).thenReturn(true);
        List<SpecModelValidationError> validationErrors = PropValidation.validate(mSpecModel, COMMON_PROP_NAMES, VALID_COMMON_PROPS);
        assertThat(validationErrors).hasSize(1);
        assertThat(validationErrors.get(0).element).isEqualTo(mRepresentedObject1);
        assertThat(validationErrors.get(0).message).isEqualTo(("The prop sameName is defined differently in different methods. Ensure that each " + ("instance of this prop is declared in the same way (this means having the same type, " + "resType and values for isOptional, isCommonProp and overrideCommonPropBehavior).")));
    }

    @Test
    public void testTwoPropsWithSameNameButDifferentResType() {
        Mockito.when(mPropModel1.getName()).thenReturn("sameName");
        Mockito.when(mPropModel2.getName()).thenReturn("sameName");
        Mockito.when(mPropModel1.getTypeName()).thenReturn(INT);
        Mockito.when(mPropModel2.getTypeName()).thenReturn(INT);
        Mockito.when(mPropModel1.getResType()).thenReturn(ResType.INT);
        List<SpecModelValidationError> validationErrors = PropValidation.validate(mSpecModel, COMMON_PROP_NAMES, VALID_COMMON_PROPS);
        assertThat(validationErrors).hasSize(1);
        assertThat(validationErrors.get(0).element).isEqualTo(mRepresentedObject1);
        assertThat(validationErrors.get(0).message).isEqualTo(("The prop sameName is defined differently in different methods. Ensure that each " + ("instance of this prop is declared in the same way (this means having the same type, " + "resType and values for isOptional, isCommonProp and overrideCommonPropBehavior).")));
    }

    @Test
    public void testPropWithReservedName() {
        Mockito.when(mPropModel1.getName()).thenReturn("layoutDirection");
        List<SpecModelValidationError> validationErrors = PropValidation.validate(mSpecModel, COMMON_PROP_NAMES, VALID_COMMON_PROPS);
        assertThat(validationErrors).hasSize(1);
        assertThat(validationErrors.get(0).element).isEqualTo(mRepresentedObject1);
        assertThat(validationErrors.get(0).message).isEqualTo(("'layoutDirection' is a reserved prop name used by the component's builder. Please" + " use another name or add \"isCommonProp\" to the Prop\'s definition."));
    }

    @Test
    public void testPropMarkedCommonWithoutCommonName() {
        Mockito.when(mPropModel1.getName()).thenReturn("badName");
        Mockito.when(mPropModel1.isCommonProp()).thenReturn(true);
        List<SpecModelValidationError> validationErrors = PropValidation.validate(mSpecModel, COMMON_PROP_NAMES, VALID_COMMON_PROPS);
        assertThat(validationErrors).hasSize(1);
        assertThat(validationErrors.get(0).element).isEqualTo(mRepresentedObject1);
        assertThat(validationErrors.get(0).message).isEqualTo("Prop with isCommonProp and name badName is incorrectly defined - see PropValidation.java for a list of common props that may be used.");
    }

    @Test
    public void testPropMarkedCommonWithWrongType() {
        Mockito.when(mPropModel1.getName()).thenReturn("focusable");
        Mockito.when(mPropModel1.isCommonProp()).thenReturn(true);
        Mockito.when(mPropModel1.getTypeName()).thenReturn(OBJECT);
        List<SpecModelValidationError> validationErrors = PropValidation.validate(mSpecModel, COMMON_PROP_NAMES, VALID_COMMON_PROPS);
        assertThat(validationErrors).hasSize(1);
        assertThat(validationErrors.get(0).element).isEqualTo(mRepresentedObject1);
        assertThat(validationErrors.get(0).message).isEqualTo("A common prop with name focusable must have type of: boolean");
    }

    @Test
    public void testPropMarkedOverrideCommonButNotCommon() {
        Mockito.when(mPropModel1.overrideCommonPropBehavior()).thenReturn(true);
        Mockito.when(mPropModel1.isCommonProp()).thenReturn(false);
        List<SpecModelValidationError> validationErrors = PropValidation.validate(mSpecModel, COMMON_PROP_NAMES, VALID_COMMON_PROPS);
        assertThat(validationErrors).hasSize(1);
        assertThat(validationErrors.get(0).element).isEqualTo(mRepresentedObject1);
        assertThat(validationErrors.get(0).message).isEqualTo("overrideCommonPropBehavior may only be true is isCommonProp is true.");
    }

    @Test
    public void testPropWithReservedType() {
        Mockito.when(mPropModel1.getTypeName()).thenReturn(COMPONENT_LAYOUT);
        List<SpecModelValidationError> validationErrors = PropValidation.validate(mSpecModel, COMMON_PROP_NAMES, VALID_COMMON_PROPS);
        assertThat(validationErrors).hasSize(1);
        assertThat(validationErrors.get(0).element).isEqualTo(mRepresentedObject1);
        assertThat(validationErrors.get(0).message).isEqualTo(("Props may not be declared with the following argument types: " + (("[com.facebook.litho.ComponentLayout, " + "com.facebook.litho.Component.Builder, ") + "com.facebook.litho.reference.Reference.Builder].")));
    }

    @Test
    public void testOptionalPropWithDefault() {
        Mockito.when(mPropModel1.isOptional()).thenReturn(false);
        Mockito.when(mPropModel1.hasDefault(ArgumentMatchers.any(ImmutableList.class))).thenReturn(true);
        List<SpecModelValidationError> validationErrors = PropValidation.validate(mSpecModel, COMMON_PROP_NAMES, VALID_COMMON_PROPS);
        assertThat(validationErrors).hasSize(1);
        assertThat(validationErrors.get(0).element).isEqualTo(mRepresentedObject1);
        assertThat(validationErrors.get(0).message).isEqualTo("name1 is not optional so it should not be declared with a default value.");
    }

    @Test
    public void testIncorrectTypeForResType() {
        Mockito.when(mPropModel1.getResType()).thenReturn(BOOL);
        Mockito.when(mPropModel1.getTypeName()).thenReturn(INT);
        List<SpecModelValidationError> validationErrors = PropValidation.validate(mSpecModel, COMMON_PROP_NAMES, VALID_COMMON_PROPS);
        assertThat(validationErrors).hasSize(1);
        assertThat(validationErrors.get(0).element).isEqualTo(mRepresentedObject1);
        assertThat(validationErrors.get(0).message).isEqualTo(("A prop declared with resType BOOL must be one of the following types: " + "[boolean, java.lang.Boolean]."));
    }

    @Test
    public void testDefaultDefinedWithNoCorrespondingProp() {
        Object propDefaultObject1 = new Object();
        Object propDefaultObject2 = new Object();
        PropDefaultModel propDefault1 = new PropDefaultModel(TypeName.CHAR, "name1", ImmutableList.<Modifier>of(), propDefaultObject1);
        PropDefaultModel propDefault2 = new PropDefaultModel(TypeName.CHAR, "notAPropName", ImmutableList.<Modifier>of(), propDefaultObject2);
        Mockito.when(mSpecModel.getPropDefaults()).thenReturn(ImmutableList.of(propDefault1, propDefault2));
        List<SpecModelValidationError> validationErrors = PropValidation.validate(mSpecModel, COMMON_PROP_NAMES, VALID_COMMON_PROPS);
        assertThat(validationErrors).hasSize(2);
        assertThat(validationErrors.get(0).element).isEqualTo(propDefaultObject1);
        assertThat(validationErrors.get(0).message).isEqualTo("PropDefault name1 of type char should be of type boolean");
        assertThat(validationErrors.get(1).element).isEqualTo(propDefaultObject2);
        assertThat(validationErrors.get(1).message).isEqualTo("PropDefault notAPropName of type char does not correspond to any defined prop");
    }

    @Test
    public void testVarArgPropMustHaveListType() {
        Mockito.when(mPropModel1.getResType()).thenReturn(NONE);
        Mockito.when(mPropModel1.getVarArgsSingleName()).thenReturn("test");
        Mockito.when(mPropModel1.hasVarArgs()).thenReturn(true);
        Mockito.when(mPropModel1.getTypeName()).thenReturn(TypeName.get(String.class));
        List<SpecModelValidationError> validationErrors = PropValidation.validate(mSpecModel, COMMON_PROP_NAMES, VALID_COMMON_PROPS);
        assertThat(validationErrors).hasSize(1);
        assertThat(validationErrors.get(0).element).isEqualTo(mRepresentedObject1);
        assertThat(validationErrors.get(0).message).isEqualTo("name1 is a variable argument, and thus requires a parameterized List type.");
    }

    @Test
    public void testVarArgPropMustHaveParameterizedListType() {
        Mockito.when(mPropModel1.getResType()).thenReturn(NONE);
        Mockito.when(mPropModel1.getVarArgsSingleName()).thenReturn("test");
        Mockito.when(mPropModel1.hasVarArgs()).thenReturn(true);
        Mockito.when(mPropModel1.getTypeName()).thenReturn(ParameterizedTypeName.get(ArrayList.class, String.class));
        List<SpecModelValidationError> validationErrors = PropValidation.validate(mSpecModel, COMMON_PROP_NAMES, VALID_COMMON_PROPS);
        assertThat(validationErrors).hasSize(1);
        assertThat(validationErrors.get(0).element).isEqualTo(mRepresentedObject1);
        assertThat(validationErrors.get(0).message).isEqualTo("name1 is a variable argument, and thus should be a List<> type.");
    }

    @Test
    public void testIncorrectTypeForResTypeWithVarArg() {
        Mockito.when(mPropModel1.getResType()).thenReturn(BOOL);
        Mockito.when(mPropModel1.hasVarArgs()).thenReturn(true);
        Mockito.when(mPropModel1.getTypeName()).thenReturn(ParameterizedTypeName.get(LIST, INT.box()));
        List<SpecModelValidationError> validationErrors = PropValidation.validate(mSpecModel, COMMON_PROP_NAMES, VALID_COMMON_PROPS);
        assertThat(validationErrors).hasSize(1);
        assertThat(validationErrors.get(0).element).isEqualTo(mRepresentedObject1);
        assertThat(validationErrors.get(0).message).isEqualTo(("A variable argument declared with resType BOOL must be one of the following types: " + "[java.util.List<java.lang.Boolean>]."));
    }

    @Test
    public void testResTypeDimenMustNotHavePxOrDimensionAnnotations() {
        Mockito.when(mPropModel1.getResType()).thenReturn(DIMEN_OFFSET);
        Mockito.when(mPropModel1.getTypeName()).thenReturn(INT);
        Mockito.when(mPropModel1.getExternalAnnotations()).thenReturn(ImmutableList.of(AnnotationSpec.builder(PX).build()));
        Mockito.when(mPropModel2.getResType()).thenReturn(DIMEN_SIZE);
        Mockito.when(mPropModel2.getTypeName()).thenReturn(INT);
        Mockito.when(mPropModel2.getExternalAnnotations()).thenReturn(ImmutableList.of(AnnotationSpec.builder(DIMENSION).build()));
        List<SpecModelValidationError> validationErrors = PropValidation.validate(mSpecModel, COMMON_PROP_NAMES, VALID_COMMON_PROPS);
        assertThat(validationErrors).hasSize(2);
        assertThat(validationErrors.get(0).element).isEqualTo(mRepresentedObject1);
        assertThat(validationErrors.get(0).message).isEqualTo(("Props with resType DIMEN_OFFSET should not be annotated with " + (("androidx.annotation.Px or androidx.annotation.Dimension, since " + "these annotations will automatically be added to the relevant builder methods ") + "in the generated code.")));
        assertThat(validationErrors.get(1).element).isEqualTo(mRepresentedObject2);
        assertThat(validationErrors.get(1).message).isEqualTo(("Props with resType DIMEN_SIZE should not be annotated with " + (("androidx.annotation.Px or androidx.annotation.Dimension, since " + "these annotations will automatically be added to the relevant builder methods ") + "in the generated code.")));
    }
}

