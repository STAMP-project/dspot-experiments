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


import ClassNames.STATE_VALUE;
import TypeName.BOOLEAN;
import TypeName.INT;
import com.facebook.litho.annotations.InjectProp;
import com.facebook.litho.annotations.Param;
import com.facebook.litho.annotations.Prop;
import com.facebook.litho.annotations.ResType;
import com.facebook.litho.annotations.TreeProp;
import com.facebook.litho.specmodels.internal.ImmutableList;
import com.facebook.litho.testing.specmodels.MockMethodParamModel;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeVariableName;
import com.squareup.javapoet.WildcardTypeName;
import java.lang.annotation.Annotation;
import java.util.List;
import javax.lang.model.element.Modifier;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests {@link StateValidation}
 */
public class StateValidationTest {
    private final SpecModel mSpecModel = Mockito.mock(SpecModel.class);

    private final PropModel mPropModel = Mockito.mock(PropModel.class);

    private final StateParamModel mStateParamModel = Mockito.mock(StateParamModel.class);

    private final Object mRepresentedObject1 = new Object();

    private final Object mRepresentedObject2 = new Object();

    private final Object mRepresentedObject3 = new Object();

    private final Object mRepresentedObject4 = new Object();

    private final Object mRepresentedObject5 = new Object();

    @Test
    public void testTwoStateValuesWithSameNameButDifferentType() {
        StateParamModel stateValue1 = Mockito.mock(StateParamModel.class);
        StateParamModel stateValue2 = Mockito.mock(StateParamModel.class);
        Mockito.when(stateValue1.getName()).thenReturn("sameName");
        Mockito.when(stateValue2.getName()).thenReturn("sameName");
        Mockito.when(stateValue1.getTypeName()).thenReturn(BOOLEAN);
        Mockito.when(stateValue2.getTypeName()).thenReturn(INT);
        Mockito.when(stateValue2.getRepresentedObject()).thenReturn(mRepresentedObject2);
        Mockito.when(mSpecModel.getStateValues()).thenReturn(ImmutableList.of(stateValue1, stateValue2));
        Mockito.when(mSpecModel.getProps()).thenReturn(ImmutableList.<PropModel>of());
        Mockito.when(mSpecModel.getInjectProps()).thenReturn(ImmutableList.<InjectPropModel>of());
        Mockito.when(mSpecModel.getTreeProps()).thenReturn(ImmutableList.<TreePropModel>of());
        List<SpecModelValidationError> validationErrors = StateValidation.validateStateValues(mSpecModel);
        assertThat(validationErrors).hasSize(1);
        assertThat(validationErrors.get(0).element).isEqualTo(mRepresentedObject2);
        assertThat(validationErrors.get(0).message).isEqualTo("State values with the same name must have the same type.");
    }

    @Test
    public void testTwoStateValuesWithSameNameButDifferentCanUpdateLazily() {
        StateParamModel stateValue1 = Mockito.mock(StateParamModel.class);
        StateParamModel stateValue2 = Mockito.mock(StateParamModel.class);
        Mockito.when(stateValue1.getName()).thenReturn("sameName");
        Mockito.when(stateValue2.getName()).thenReturn("sameName");
        Mockito.when(stateValue1.getTypeName()).thenReturn(BOOLEAN);
        Mockito.when(stateValue2.getTypeName()).thenReturn(BOOLEAN);
        Mockito.when(stateValue1.canUpdateLazily()).thenReturn(false);
        Mockito.when(stateValue1.canUpdateLazily()).thenReturn(true);
        Mockito.when(stateValue2.getRepresentedObject()).thenReturn(mRepresentedObject2);
        Mockito.when(mSpecModel.getStateValues()).thenReturn(ImmutableList.of(stateValue1, stateValue2));
        Mockito.when(mSpecModel.getProps()).thenReturn(ImmutableList.<PropModel>of());
        Mockito.when(mSpecModel.getInjectProps()).thenReturn(ImmutableList.<InjectPropModel>of());
        Mockito.when(mSpecModel.getTreeProps()).thenReturn(ImmutableList.<TreePropModel>of());
        List<SpecModelValidationError> validationErrors = StateValidation.validateStateValues(mSpecModel);
        assertThat(validationErrors).hasSize(1);
        assertThat(validationErrors.get(0).element).isEqualTo(mRepresentedObject2);
        assertThat(validationErrors.get(0).message).isEqualTo(("State values with the same name must have the same annotated value for " + "canUpdateLazily()."));
    }

    @Test
    public void testOnUpdateStateParamSameNameAsPropAndState() {
        MethodParamModel methodParamModel1 = MockMethodParamModel.newBuilder().name("propName").annotations(ImmutableList.of(Param.class)).representedObject(mRepresentedObject1).build();
        MethodParamModel methodParamModel2 = MockMethodParamModel.newBuilder().name("stateName").annotations(ImmutableList.of(Param.class)).representedObject(mRepresentedObject2).build();
        SpecMethodModel<UpdateStateMethod, Void> updateStateMethodModel = SpecMethodModel.<UpdateStateMethod, Void>builder().annotations(null).modifiers(ImmutableList.of(Modifier.STATIC)).name(null).returnTypeSpec(null).typeVariables(ImmutableList.of()).methodParams(ImmutableList.of(methodParamModel1, methodParamModel2)).representedObject(mRepresentedObject3).typeModel(null).build();
        Mockito.when(mPropModel.getName()).thenReturn("propName");
        Mockito.when(mStateParamModel.getName()).thenReturn("stateName");
        Mockito.when(mSpecModel.getProps()).thenReturn(ImmutableList.of(mPropModel));
        Mockito.when(mSpecModel.getStateValues()).thenReturn(ImmutableList.of(mStateParamModel));
        List<SpecModelValidationError> validationErrors = StateValidation.validateOnUpdateStateMethod(mSpecModel, updateStateMethodModel);
        assertThat(validationErrors).hasSize(2);
        assertThat(validationErrors.get(0).element).isSameAs(mRepresentedObject1);
        assertThat(validationErrors.get(0).message).isEqualTo("Parameters annotated with @Param should not have the same name as a @Prop.");
        assertThat(validationErrors.get(1).element).isSameAs(mRepresentedObject2);
        assertThat(validationErrors.get(1).message).isEqualTo(("Parameters annotated with @Param should not have the same name as a @State " + "value."));
    }

    @Test
    public void testOnUpdateStateStateParamsNotValid() {
        MethodParamModel methodParamModel1 = MockMethodParamModel.newBuilder().type(INT).name("name1").representedObject(mRepresentedObject1).build();
        MethodParamModel methodParamModel2 = MockMethodParamModel.newBuilder().type(ParameterizedTypeName.get(ClassName.bestGuess("com.facebook.litho.Output"), TypeVariableName.get("T"))).name("name2").representedObject(mRepresentedObject2).build();
        MethodParamModel methodParamModel3 = MockMethodParamModel.newBuilder().type(ParameterizedTypeName.get(STATE_VALUE, TypeVariableName.get("S"), TypeVariableName.get("T"))).name("name3").representedObject(mRepresentedObject3).build();
        MethodParamModel methodParamModel4 = MockMethodParamModel.newBuilder().type(ParameterizedTypeName.get(STATE_VALUE, WildcardTypeName.subtypeOf(ClassName.bestGuess("java.lang.Object")))).name("name4").representedObject(mRepresentedObject4).build();
        SpecMethodModel<UpdateStateMethod, Void> updateStateMethodModel = SpecMethodModel.<UpdateStateMethod, Void>builder().annotations(null).modifiers(ImmutableList.of(Modifier.STATIC)).name("methodName").returnTypeSpec(null).methodParams(ImmutableList.of(methodParamModel1, methodParamModel2, methodParamModel3, methodParamModel4)).representedObject(mRepresentedObject5).build();
        List<SpecModelValidationError> validationErrors = StateValidation.validateOnUpdateStateMethod(mSpecModel, updateStateMethodModel);
        assertThat(validationErrors).hasSize(4);
        assertThat(validationErrors.get(0).element).isSameAs(mRepresentedObject1);
        assertThat(validationErrors.get(0).message).isEqualTo(("Only state parameters and parameters annotated with @Param are permitted in " + ("@OnUpdateState method, and all state parameters must be of type " + "com.facebook.litho.StateValue, but name1 is of type int.")));
        assertThat(validationErrors.get(1).element).isSameAs(mRepresentedObject2);
        assertThat(validationErrors.get(1).message).isEqualTo(("Only state parameters and parameters annotated with @Param are permitted in " + (("@OnUpdateState method, and all state parameters must be of type " + "com.facebook.litho.StateValue, but name2 is of ") + "type com.facebook.litho.Output<T>.")));
        assertThat(validationErrors.get(2).element).isSameAs(mRepresentedObject3);
        assertThat(validationErrors.get(2).message).isEqualTo(("All parameters of type com.facebook.litho.StateValue must define a type " + "argument, name3 in method methodName does not."));
        assertThat(validationErrors.get(3).element).isSameAs(mRepresentedObject4);
        assertThat(validationErrors.get(3).message).isEqualTo(("All parameters of type com.facebook.litho.StateValue must define a type " + "argument, name4 in method methodName does not."));
    }

    @Test
    public void testOnUpdateStateStateParamsNotDefinedElsewhere() {
        MethodParamModel methodParamModel4 = MockMethodParamModel.newBuilder().type(ParameterizedTypeName.get(STATE_VALUE, ClassName.bestGuess("java.lang.Object"))).name("name").representedObject(mRepresentedObject1).build();
        SpecMethodModel<UpdateStateMethod, Void> updateStateMethodModel = SpecMethodModel.<UpdateStateMethod, Void>builder().annotations(null).modifiers(ImmutableList.of(Modifier.STATIC)).name("methodName").returnTypeSpec(null).typeVariables(ImmutableList.of()).methodParams(ImmutableList.of(methodParamModel4)).representedObject(mRepresentedObject2).typeModel(null).build();
        List<SpecModelValidationError> validationErrors = StateValidation.validateOnUpdateStateMethod(mSpecModel, updateStateMethodModel);
        assertThat(validationErrors).hasSize(1);
        assertThat(validationErrors.get(0).element).isSameAs(mRepresentedObject1);
        assertThat(validationErrors.get(0).message).isEqualTo(("Names of parameters of type StateValue must match the name and type of a " + "parameter annotated with @State."));
    }

    @Test
    public void testOnUpdateStateNotStatic() {
        SpecMethodModel<UpdateStateMethod, Void> updateStateMethodModel = SpecMethodModel.<UpdateStateMethod, Void>builder().annotations(null).modifiers(ImmutableList.<Modifier>of()).name("methodName").returnTypeSpec(null).typeVariables(ImmutableList.of()).methodParams(ImmutableList.<MethodParamModel>of()).representedObject(mRepresentedObject1).typeModel(null).build();
        List<SpecModelValidationError> validationErrors = StateValidation.validateOnUpdateStateMethod(mSpecModel, updateStateMethodModel);
        assertThat(validationErrors).hasSize(1);
        assertThat(validationErrors.get(0).element).isSameAs(mRepresentedObject1);
        assertThat(validationErrors.get(0).message).isEqualTo("Methods in a spec must be static.");
    }

    @Test
    public void testStateAndPropWithSameName() {
        final StateParamModel stateValue = Mockito.mock(StateParamModel.class);
        Mockito.when(stateValue.getName()).thenReturn("sameName");
        final PropModel prop = Mockito.mock(PropModel.class);
        Mockito.when(prop.getName()).thenReturn("sameName");
        Mockito.when(prop.getRepresentedObject()).thenReturn(mRepresentedObject1);
        final Prop propAnnotation = new Prop() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return Prop.class;
            }

            @Override
            public boolean optional() {
                return false;
            }

            @Override
            public ResType resType() {
                return null;
            }

            @Override
            public String docString() {
                return null;
            }

            @Override
            public String varArg() {
                return null;
            }

            @Override
            public boolean isCommonProp() {
                return false;
            }

            @Override
            public boolean overrideCommonPropBehavior() {
                return false;
            }
        };
        Mockito.when(prop.getAnnotations()).thenReturn(ImmutableList.<Annotation>of(propAnnotation));
        Mockito.when(mSpecModel.getStateValues()).thenReturn(ImmutableList.of(stateValue));
        Mockito.when(mSpecModel.getProps()).thenReturn(ImmutableList.of(prop));
        Mockito.when(mSpecModel.getInjectProps()).thenReturn(ImmutableList.<InjectPropModel>of());
        Mockito.when(mSpecModel.getTreeProps()).thenReturn(ImmutableList.<TreePropModel>of());
        final List<SpecModelValidationError> validationErrors = StateValidation.validateStateValues(mSpecModel);
        assertThat(validationErrors).hasSize(1);
        assertThat(validationErrors.get(0).element).isEqualTo(mRepresentedObject1);
        assertThat(validationErrors.get(0).message).isEqualTo("The parameter name of @Prop \"sameName\" and @State \"sameName\" collide!");
    }

    @Test
    public void testStateAndInjectPropWithSameName() {
        final StateParamModel stateValue = Mockito.mock(StateParamModel.class);
        Mockito.when(stateValue.getName()).thenReturn("sameName");
        final InjectPropModel injectProp = Mockito.mock(InjectPropModel.class);
        Mockito.when(injectProp.getName()).thenReturn("sameName");
        Mockito.when(injectProp.getRepresentedObject()).thenReturn(mRepresentedObject1);
        final InjectProp injectPropAnnotation = new InjectProp() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return InjectProp.class;
            }

            @Override
            public boolean isLazy() {
                return false;
            }
        };
        Mockito.when(injectProp.getAnnotations()).thenReturn(ImmutableList.<Annotation>of(injectPropAnnotation));
        Mockito.when(mSpecModel.getStateValues()).thenReturn(ImmutableList.of(stateValue));
        Mockito.when(mSpecModel.getProps()).thenReturn(ImmutableList.<PropModel>of());
        Mockito.when(mSpecModel.getInjectProps()).thenReturn(ImmutableList.of(injectProp));
        Mockito.when(mSpecModel.getTreeProps()).thenReturn(ImmutableList.<TreePropModel>of());
        final List<SpecModelValidationError> validationErrors = StateValidation.validateStateValues(mSpecModel);
        assertThat(validationErrors).hasSize(1);
        assertThat(validationErrors.get(0).element).isEqualTo(mRepresentedObject1);
        assertThat(validationErrors.get(0).message).isEqualTo("The parameter name of @InjectProp \"sameName\" and @State \"sameName\" collide!");
    }

    @Test
    public void testStateAndTreePropWithSameName() {
        final StateParamModel stateValue = Mockito.mock(StateParamModel.class);
        Mockito.when(stateValue.getName()).thenReturn("sameName");
        final TreePropModel treeProp = Mockito.mock(TreePropModel.class);
        Mockito.when(treeProp.getName()).thenReturn("sameName");
        Mockito.when(treeProp.getRepresentedObject()).thenReturn(mRepresentedObject1);
        final TreeProp treePropAnnotation = new TreeProp() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return TreeProp.class;
            }
        };
        Mockito.when(treeProp.getAnnotations()).thenReturn(ImmutableList.<Annotation>of(treePropAnnotation));
        Mockito.when(mSpecModel.getStateValues()).thenReturn(ImmutableList.of(stateValue));
        Mockito.when(mSpecModel.getProps()).thenReturn(ImmutableList.<PropModel>of());
        Mockito.when(mSpecModel.getInjectProps()).thenReturn(ImmutableList.<InjectPropModel>of());
        Mockito.when(mSpecModel.getTreeProps()).thenReturn(ImmutableList.of(treeProp));
        final List<SpecModelValidationError> validationErrors = StateValidation.validateStateValues(mSpecModel);
        assertThat(validationErrors).hasSize(1);
        assertThat(validationErrors.get(0).element).isEqualTo(mRepresentedObject1);
        assertThat(validationErrors.get(0).message).isEqualTo("The parameter name of @TreeProp \"sameName\" and @State \"sameName\" collide!");
    }
}

