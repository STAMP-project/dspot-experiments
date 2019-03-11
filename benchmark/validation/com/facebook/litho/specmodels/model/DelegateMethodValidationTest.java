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


import ClassNames.COMPONENT_CONTEXT;
import ClassNames.OBJECT;
import DelegateMethodDescriptions.INTER_STAGE_INPUTS_MAP;
import SpecElementType.JAVA_CLASS;
import SpecElementType.KOTLIN_SINGLETON;
import TypeName.BOOLEAN;
import TypeName.INT;
import com.facebook.litho.annotations.FromBind;
import com.facebook.litho.annotations.FromPrepare;
import com.facebook.litho.annotations.OnBind;
import com.facebook.litho.annotations.OnCreateLayout;
import com.facebook.litho.annotations.OnCreateLayoutWithSizeSpec;
import com.facebook.litho.annotations.OnEvent;
import com.facebook.litho.annotations.OnMount;
import com.facebook.litho.annotations.OnPrepare;
import com.facebook.litho.annotations.OnUnmount;
import com.facebook.litho.specmodels.internal.ImmutableList;
import com.facebook.litho.testing.specmodels.MockMethodParamModel;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.lang.model.element.Modifier;
import org.junit.Test;
import org.mockito.Mockito;

import static ClassNames.COMPONENT;
import static ClassNames.COMPONENT_LAYOUT;


/**
 * Tests {@link DelegateMethodValidation}
 */
public class DelegateMethodValidationTest {
    private final LayoutSpecModel mLayoutSpecModel = Mockito.mock(LayoutSpecModel.class);

    private final MountSpecModel mMountSpecModel = Mockito.mock(MountSpecModel.class);

    private final Object mModelRepresentedObject = new Object();

    private final Object mMountSpecObject = new Object();

    private final Object mDelegateMethodObject1 = new Object();

    private final Object mDelegateMethodObject2 = new Object();

    private final Object mMethodParamObject1 = new Object();

    private final Object mMethodParamObject2 = new Object();

    private final Object mMethodParamObject3 = new Object();

    private SpecMethodModel<DelegateMethod, Void> mOnCreateMountContent;

    private final Object mOnCreateMountContentObject = new Object();

    @Test
    public void testNoDelegateMethods() {
        Mockito.when(mLayoutSpecModel.getDelegateMethods()).thenReturn(ImmutableList.of());
        final List<SpecModelValidationError> validationErrors = DelegateMethodValidation.validateLayoutSpecModel(mLayoutSpecModel);
        assertThat(validationErrors).hasSize(1);
        assertThat(validationErrors.get(0).element).isEqualTo(mModelRepresentedObject);
        assertThat(validationErrors.get(0).message).isEqualTo(("You need to have a method annotated with either @OnCreateLayout " + ("or @OnCreateLayoutWithSizeSpec in your spec. In most cases, @OnCreateLayout " + "is what you want.")));
    }

    @Test
    public void testOnCreateLayoutAndOnCreateLayoutWithSizeSpec() {
        Mockito.when(mLayoutSpecModel.getDelegateMethods()).thenReturn(ImmutableList.of(SpecMethodModel.<DelegateMethod, Void>builder().annotations(ImmutableList.of(((Annotation) (() -> OnCreateLayout.class)))).modifiers(ImmutableList.of(Modifier.STATIC)).name("name").returnTypeSpec(new TypeSpec(COMPONENT)).typeVariables(ImmutableList.of()).methodParams(ImmutableList.of(MockMethodParamModel.newBuilder().name("c").type(COMPONENT_CONTEXT).representedObject(new Object()).build())).representedObject(new Object()).typeModel(null).build(), SpecMethodModel.<DelegateMethod, Void>builder().annotations(ImmutableList.of(((Annotation) (() -> OnCreateLayoutWithSizeSpec.class)))).modifiers(ImmutableList.of(Modifier.STATIC)).name("name").returnTypeSpec(new TypeSpec(COMPONENT)).typeVariables(ImmutableList.of()).methodParams(ImmutableList.of(MockMethodParamModel.newBuilder().name("c").type(COMPONENT_CONTEXT).representedObject(new Object()).build(), MockMethodParamModel.newBuilder().name("widthSpec").type(INT).representedObject(new Object()).build(), MockMethodParamModel.newBuilder().name("heightSpec").type(INT).representedObject(new Object()).build())).representedObject(new Object()).typeModel(null).build()));
        final List<SpecModelValidationError> validationErrors = DelegateMethodValidation.validateLayoutSpecModel(mLayoutSpecModel);
        assertThat(validationErrors).hasSize(1);
        assertThat(validationErrors.get(0).element).isEqualTo(mModelRepresentedObject);
        assertThat(validationErrors.get(0).message).isEqualTo(("Your LayoutSpec should have a method annotated with either @OnCreateLayout " + ("or @OnCreateLayoutWithSizeSpec, but not both. In most cases, @OnCreateLayout " + "is what you want.")));
    }

    @Test
    public void testDelegateMethodDoesNotDefineEnoughParams() {
        Mockito.when(mLayoutSpecModel.getDelegateMethods()).thenReturn(ImmutableList.of(SpecMethodModel.<DelegateMethod, Void>builder().annotations(ImmutableList.of(((Annotation) (() -> OnCreateLayoutWithSizeSpec.class)))).modifiers(ImmutableList.of(Modifier.STATIC)).name("name").returnTypeSpec(new TypeSpec(COMPONENT)).typeVariables(ImmutableList.of()).methodParams(ImmutableList.of()).representedObject(mDelegateMethodObject1).typeModel(null).build()));
        final List<SpecModelValidationError> validationErrors = DelegateMethodValidation.validateLayoutSpecModel(mLayoutSpecModel);
        assertThat(validationErrors).hasSize(1);
        assertThat(validationErrors.get(0).element).isEqualTo(mDelegateMethodObject1);
        assertThat(validationErrors.get(0).message).isEqualTo(("Methods annotated with interface " + (("com.facebook.litho.annotations.OnCreateLayoutWithSizeSpec " + "must have at least 3 parameters, and they should be of type ") + "com.facebook.litho.ComponentContext, int, int.")));
    }

    @Test
    public void testDelegateMethodStartsWithDunder() {
        Mockito.when(mLayoutSpecModel.getDelegateMethods()).thenReturn(ImmutableList.of(SpecMethodModel.<DelegateMethod, Void>builder().annotations(ImmutableList.of(((Annotation) (() -> OnEvent.class)))).modifiers(ImmutableList.of(Modifier.STATIC)).name("__someEventHandler").returnTypeSpec(new TypeSpec(TypeName.VOID)).typeVariables(ImmutableList.of()).methodParams(ImmutableList.of()).representedObject(null).typeModel(null).build()));
        final List<SpecModelValidationError> validationErrors = DelegateMethodValidation.validateLayoutSpecModel(mLayoutSpecModel);
        assertThat(validationErrors).hasSize(2);
        assertThat(validationErrors.get(0).message).isEqualTo(("Methods in a component must not start with '__' as they are reserved" + " for internal use. Method '__someEventHandler' violates this contract."));
    }

    @Test
    public void testDelegateMethodDoesNotDefinedParamsOfCorrectType() {
        Mockito.when(mLayoutSpecModel.getDelegateMethods()).thenReturn(ImmutableList.of(SpecMethodModel.<DelegateMethod, Void>builder().annotations(ImmutableList.of(((Annotation) (() -> OnCreateLayout.class)))).modifiers(ImmutableList.of(Modifier.STATIC)).name("name").returnTypeSpec(new TypeSpec(COMPONENT)).typeVariables(ImmutableList.of()).methodParams(ImmutableList.of(MockMethodParamModel.newBuilder().type(BOOLEAN).representedObject(mMethodParamObject1).build())).representedObject(mDelegateMethodObject1).typeModel(null).build()));
        final List<SpecModelValidationError> validationErrors = DelegateMethodValidation.validateLayoutSpecModel(mLayoutSpecModel);
        assertThat(validationErrors).hasSize(1);
        assertThat(validationErrors.get(0).element).isEqualTo(mMethodParamObject1);
        assertThat(validationErrors.get(0).message).isEqualTo(("Parameter in position 0 of a method annotated with interface " + ("com.facebook.litho.annotations.OnCreateLayout should be of type " + "com.facebook.litho.ComponentContext.")));
    }

    @Test
    public void testDelegateMethodHasIncorrectOptionalParams() {
        Mockito.when(mLayoutSpecModel.getDelegateMethods()).thenReturn(ImmutableList.of(SpecMethodModel.<DelegateMethod, Void>builder().annotations(ImmutableList.of(((Annotation) (() -> OnCreateLayout.class)))).modifiers(ImmutableList.of(Modifier.STATIC)).name("name").returnTypeSpec(new TypeSpec(COMPONENT)).typeVariables(ImmutableList.of()).methodParams(ImmutableList.of(MockMethodParamModel.newBuilder().type(COMPONENT_CONTEXT).representedObject(mMethodParamObject1).build(), MockMethodParamModel.newBuilder().type(INT).representedObject(mMethodParamObject2).build())).representedObject(mDelegateMethodObject1).typeModel(null).build()));
        final List<SpecModelValidationError> validationErrors = DelegateMethodValidation.validateLayoutSpecModel(mLayoutSpecModel);
        assertThat(validationErrors).hasSize(1);
        assertThat(validationErrors.get(0).element).isEqualTo(mMethodParamObject2);
        assertThat(validationErrors.get(0).message).isEqualTo("Not a valid parameter, should be one of the following: @Prop T somePropName. @TreeProp T someTreePropName. @State T someStateName. @InjectProp T someInjectPropName. @CachedValue T value, where the cached value has a corresponding @OnCalculateCachedValue method. ");
    }

    @Test
    public void testDelegateMethodIsNotStatic() {
        Mockito.when(mLayoutSpecModel.getSpecElementType()).thenReturn(JAVA_CLASS);
        Mockito.when(mLayoutSpecModel.getDelegateMethods()).thenReturn(ImmutableList.of(SpecMethodModel.<DelegateMethod, Void>builder().annotations(ImmutableList.of(((Annotation) (() -> OnCreateLayout.class)))).modifiers(ImmutableList.of()).name("name").returnTypeSpec(new TypeSpec(COMPONENT)).typeVariables(ImmutableList.of()).methodParams(ImmutableList.of(MockMethodParamModel.newBuilder().type(COMPONENT_CONTEXT).representedObject(mMethodParamObject1).build())).representedObject(mDelegateMethodObject1).typeModel(null).build()));
        final List<SpecModelValidationError> validationErrors = DelegateMethodValidation.validateLayoutSpecModel(mLayoutSpecModel);
        assertThat(validationErrors).hasSize(1);
        assertThat(validationErrors.get(0).element).isEqualTo(mDelegateMethodObject1);
        assertThat(validationErrors.get(0).message).isEqualTo("Methods in a spec must be static.");
    }

    @Test
    public void testDelegateMethodIsNotStaticWithKotlinSingleton() {
        Mockito.when(mLayoutSpecModel.getSpecElementType()).thenReturn(KOTLIN_SINGLETON);
        Mockito.when(mLayoutSpecModel.getDelegateMethods()).thenReturn(ImmutableList.of(SpecMethodModel.<DelegateMethod, Void>builder().annotations(ImmutableList.of(((Annotation) (() -> OnCreateLayout.class)))).modifiers(ImmutableList.of()).name("name").returnTypeSpec(new TypeSpec(COMPONENT)).typeVariables(ImmutableList.of()).methodParams(ImmutableList.of(MockMethodParamModel.newBuilder().type(COMPONENT_CONTEXT).representedObject(mMethodParamObject1).build())).representedObject(mDelegateMethodObject1).typeModel(null).build()));
        final List<SpecModelValidationError> validationErrors = DelegateMethodValidation.validateLayoutSpecModel(mLayoutSpecModel);
        assertThat(validationErrors).isEmpty();
    }

    @Test
    public void testDelegateMethodHasIncorrectReturnType() {
        Mockito.when(mLayoutSpecModel.getDelegateMethods()).thenReturn(ImmutableList.of(SpecMethodModel.<DelegateMethod, Void>builder().annotations(ImmutableList.of(((Annotation) (() -> OnCreateLayout.class)))).modifiers(ImmutableList.of(Modifier.STATIC)).name("name").returnTypeSpec(new TypeSpec(COMPONENT_LAYOUT)).typeVariables(ImmutableList.of()).methodParams(ImmutableList.of(MockMethodParamModel.newBuilder().type(COMPONENT_CONTEXT).representedObject(mMethodParamObject1).build())).representedObject(mDelegateMethodObject1).typeModel(null).build()));
        final List<SpecModelValidationError> validationErrors = DelegateMethodValidation.validateLayoutSpecModel(mLayoutSpecModel);
        assertThat(validationErrors).hasSize(1);
        assertThat(validationErrors.get(0).element).isEqualTo(mDelegateMethodObject1);
        assertThat(validationErrors.get(0).message).isEqualTo(("A method annotated with @OnCreateLayout needs to return " + (("com.facebook.litho.Component. Note that even if your return value is a " + "subclass of com.facebook.litho.Component, you should still use ") + "com.facebook.litho.Component as the return type.")));
    }

    @Test
    public void testMountSpecHasOnCreateMountContent() {
        Mockito.when(mMountSpecModel.getDelegateMethods()).thenReturn(ImmutableList.of());
        final List<SpecModelValidationError> validationErrors = DelegateMethodValidation.validateMountSpecModel(mMountSpecModel);
        assertThat(validationErrors).hasSize(1);
        assertThat(validationErrors.get(0).element).isEqualTo(mMountSpecObject);
        assertThat(validationErrors.get(0).message).isEqualTo("All MountSpecs need to have a method annotated with @OnCreateMountContent.");
    }

    @Test
    public void testSecondParameter() {
        Mockito.when(mMountSpecModel.getDelegateMethods()).thenReturn(ImmutableList.of(mOnCreateMountContent, SpecMethodModel.<DelegateMethod, Void>builder().annotations(ImmutableList.of(((Annotation) (() -> OnMount.class)))).modifiers(ImmutableList.of(Modifier.STATIC)).name("onMount").returnTypeSpec(new TypeSpec(TypeName.VOID)).typeVariables(ImmutableList.of()).methodParams(ImmutableList.of(MockMethodParamModel.newBuilder().type(COMPONENT_CONTEXT).representedObject(mMethodParamObject1).build(), MockMethodParamModel.newBuilder().type(OBJECT).representedObject(mMethodParamObject2).build())).representedObject(mDelegateMethodObject1).typeModel(null).build(), SpecMethodModel.<DelegateMethod, Void>builder().annotations(ImmutableList.of(((Annotation) (() -> OnBind.class)))).modifiers(ImmutableList.of(Modifier.STATIC)).name("onBind").returnTypeSpec(new TypeSpec(TypeName.VOID)).typeVariables(ImmutableList.of()).methodParams(ImmutableList.of(MockMethodParamModel.newBuilder().type(COMPONENT_CONTEXT).representedObject(mMethodParamObject1).build(), MockMethodParamModel.newBuilder().type(OBJECT).representedObject(mMethodParamObject2).build())).representedObject(mDelegateMethodObject2).typeModel(null).build()));
        final List<SpecModelValidationError> validationErrors = DelegateMethodValidation.validateMountSpecModel(mMountSpecModel);
        assertThat(validationErrors).hasSize(2);
        assertThat(validationErrors.get(0).element).isEqualTo(mDelegateMethodObject1);
        assertThat(validationErrors.get(0).message).isEqualTo(("The second parameter of a method annotated with interface " + (("com.facebook.litho.annotations.OnMount must have the same type as the " + "return type of the method annotated with @OnCreateMountContent (i.e. ") + "java.lang.MadeUpClass).")));
        assertThat(validationErrors.get(1).element).isEqualTo(mDelegateMethodObject2);
        assertThat(validationErrors.get(1).message).isEqualTo(("The second parameter of a method annotated with interface " + (("com.facebook.litho.annotations.OnBind must have the same type as the " + "return type of the method annotated with @OnCreateMountContent (i.e. ") + "java.lang.MadeUpClass).")));
    }

    @Test
    public void testInterStageInputParamAnnotationNotValid() {
        final InterStageInputParamModel interStageInputParamModel = Mockito.mock(InterStageInputParamModel.class);
        Mockito.when(interStageInputParamModel.getAnnotations()).thenReturn(ImmutableList.of(((Annotation) (() -> FromBind.class))));
        Mockito.when(interStageInputParamModel.getRepresentedObject()).thenReturn(mMethodParamObject3);
        Mockito.when(mMountSpecModel.getDelegateMethods()).thenReturn(ImmutableList.of(mOnCreateMountContent, SpecMethodModel.<DelegateMethod, Void>builder().annotations(ImmutableList.of(((Annotation) (() -> OnUnmount.class)))).modifiers(ImmutableList.of(Modifier.STATIC)).name("onUnmount").returnTypeSpec(new TypeSpec(TypeName.VOID)).typeVariables(ImmutableList.of()).methodParams(ImmutableList.of(MockMethodParamModel.newBuilder().type(COMPONENT_CONTEXT).representedObject(mMethodParamObject1).build(), MockMethodParamModel.newBuilder().type(ClassName.bestGuess("java.lang.MadeUpClass")).representedObject(mMethodParamObject2).build(), interStageInputParamModel)).representedObject(mDelegateMethodObject1).typeModel(null).build()));
        final List<SpecModelValidationError> validationErrors = DelegateMethodValidation.validateMountSpecModel(mMountSpecModel);
        assertThat(validationErrors).hasSize(1);
        assertThat(validationErrors.get(0).element).isEqualTo(mMethodParamObject3);
        assertThat(validationErrors.get(0).message).isEqualTo(("Inter-stage input annotation is not valid for methods annotated with interface " + (((("com.facebook.litho.annotations.OnUnmount; please use one of the " + "following: [interface com.facebook.litho.annotations.FromPrepare, ") + "interface com.facebook.litho.annotations.FromMeasure, ") + "interface com.facebook.litho.annotations.FromMeasureBaseline, ") + "interface com.facebook.litho.annotations.FromBoundsDefined]")));
    }

    @Test
    public void testInterStageInputMethodDoesNotExist() {
        final InterStageInputParamModel interStageInputParamModel = Mockito.mock(InterStageInputParamModel.class);
        Mockito.when(interStageInputParamModel.getAnnotations()).thenReturn(ImmutableList.of(((Annotation) (() -> FromPrepare.class))));
        Mockito.when(interStageInputParamModel.getName()).thenReturn("interStageInput");
        Mockito.when(interStageInputParamModel.getTypeName()).thenReturn(INT);
        Mockito.when(interStageInputParamModel.getRepresentedObject()).thenReturn(mMethodParamObject3);
        Mockito.when(mMountSpecModel.getDelegateMethods()).thenReturn(ImmutableList.of(mOnCreateMountContent, SpecMethodModel.<DelegateMethod, Void>builder().annotations(ImmutableList.of(((Annotation) (() -> OnMount.class)))).modifiers(ImmutableList.of(Modifier.STATIC)).name("onMount").returnTypeSpec(new TypeSpec(TypeName.VOID)).typeVariables(ImmutableList.of()).methodParams(ImmutableList.of(MockMethodParamModel.newBuilder().type(COMPONENT_CONTEXT).representedObject(mMethodParamObject1).build(), MockMethodParamModel.newBuilder().type(ClassName.bestGuess("java.lang.MadeUpClass")).representedObject(mMethodParamObject2).build(), interStageInputParamModel)).representedObject(mDelegateMethodObject1).typeModel(null).build()));
        final List<SpecModelValidationError> validationErrors = DelegateMethodValidation.validateMountSpecModel(mMountSpecModel);
        for (SpecModelValidationError validationError : validationErrors) {
            System.out.println(validationError.message);
        }
        assertThat(validationErrors).hasSize(1);
        assertThat(validationErrors.get(0).element).isEqualTo(mMethodParamObject3);
        assertThat(validationErrors.get(0).message).isEqualTo(("To use interface com.facebook.litho.annotations.FromPrepare on param interStageInput " + (("you must have a method annotated with interface " + "com.facebook.litho.annotations.OnPrepare that has a param Output<java.lang.Integer> ") + "interStageInput")));
    }

    @Test
    public void testInterStageInputOutputDoesNotExist() {
        final InterStageInputParamModel interStageInputParamModel = Mockito.mock(InterStageInputParamModel.class);
        Mockito.when(interStageInputParamModel.getAnnotations()).thenReturn(ImmutableList.of(((Annotation) (() -> FromPrepare.class))));
        Mockito.when(interStageInputParamModel.getName()).thenReturn("interStageInput");
        Mockito.when(interStageInputParamModel.getTypeName()).thenReturn(INT);
        Mockito.when(interStageInputParamModel.getRepresentedObject()).thenReturn(mMethodParamObject3);
        Mockito.when(mMountSpecModel.getDelegateMethods()).thenReturn(ImmutableList.of(mOnCreateMountContent, SpecMethodModel.<DelegateMethod, Void>builder().annotations(ImmutableList.of(((Annotation) (() -> OnMount.class)))).modifiers(ImmutableList.of(Modifier.STATIC)).name("onMount").returnTypeSpec(new TypeSpec(TypeName.VOID)).typeVariables(ImmutableList.of()).methodParams(ImmutableList.of(MockMethodParamModel.newBuilder().name("c").type(COMPONENT_CONTEXT).representedObject(mMethodParamObject1).build(), MockMethodParamModel.newBuilder().name("param").type(ClassName.bestGuess("java.lang.MadeUpClass")).representedObject(mMethodParamObject2).build(), interStageInputParamModel)).representedObject(mDelegateMethodObject1).typeModel(null).build(), SpecMethodModel.<DelegateMethod, Void>builder().annotations(ImmutableList.of(((Annotation) (() -> OnPrepare.class)))).modifiers(ImmutableList.of(Modifier.STATIC)).name("onPrepare").returnTypeSpec(new TypeSpec(TypeName.VOID)).typeVariables(ImmutableList.of()).methodParams(ImmutableList.of(MockMethodParamModel.newBuilder().name("c").type(COMPONENT_CONTEXT).representedObject(mMethodParamObject1).build())).representedObject(mDelegateMethodObject2).typeModel(null).build()));
        final List<SpecModelValidationError> validationErrors = DelegateMethodValidation.validateMountSpecModel(mMountSpecModel);
        for (final SpecModelValidationError validationError : validationErrors) {
            System.out.println(validationError.message);
        }
        assertThat(validationErrors).hasSize(1);
        assertThat(validationErrors.get(0).element).isEqualTo(mMethodParamObject3);
        assertThat(validationErrors.get(0).message).isEqualTo(("To use interface com.facebook.litho.annotations.FromPrepare on param interStageInput " + ("your method annotated with interface com.facebook.litho.annotations.OnPrepare must " + "have a param Output<java.lang.Integer> interStageInput")));
    }

    @Test
    public void testDelegateMethodWithOptionalParameters() throws Exception {
        final Map<Class<? extends Annotation>, DelegateMethodDescription> map = new HashMap<>();
        map.put(OnCreateLayout.class, DelegateMethodDescription.fromDelegateMethodDescription(DelegateMethodDescriptions.LAYOUT_SPEC_DELEGATE_METHODS_MAP.get(OnCreateLayout.class)).optionalParameters(ImmutableList.of(MethodParamModelFactory.createSimpleMethodParamModel(new TypeSpec(INT.box()), "matched", new Object()), MethodParamModelFactory.createSimpleMethodParamModel(new TypeSpec(TypeName.CHAR), "unmatched", new Object()))).build());
        Mockito.when(mLayoutSpecModel.getDelegateMethods()).thenReturn(ImmutableList.of(SpecMethodModel.<DelegateMethod, Void>builder().annotations(ImmutableList.of(((Annotation) (() -> OnCreateLayout.class)))).modifiers(ImmutableList.of(Modifier.STATIC)).name("name").returnTypeSpec(new TypeSpec(COMPONENT)).typeVariables(ImmutableList.of()).methodParams(ImmutableList.of(MockMethodParamModel.newBuilder().type(COMPONENT_CONTEXT).representedObject(mMethodParamObject1).build(), MethodParamModelFactory.createSimpleMethodParamModel(new TypeSpec(INT.box()), "matched", mMethodParamObject2), MethodParamModelFactory.createSimpleMethodParamModel(new TypeSpec(TypeName.OBJECT), "unmatched", mMethodParamObject3))).representedObject(mDelegateMethodObject1).typeModel(null).build()));
        final List<SpecModelValidationError> validationErrors = DelegateMethodValidation.validateMethods(mLayoutSpecModel, map, INTER_STAGE_INPUTS_MAP);
        assertThat(validationErrors).hasSize(1);
        assertThat(validationErrors.get(0).element).isEqualTo(mMethodParamObject3);
        assertThat(validationErrors.get(0).message).isEqualTo("Not a valid parameter, should be one of the following: @Prop T somePropName. @TreeProp T someTreePropName. @State T someStateName. @InjectProp T someInjectPropName. @CachedValue T value, where the cached value has a corresponding @OnCalculateCachedValue method. Or one of the following, where no annotations should be added to the parameter: java.lang.Integer matched. char unmatched. ");
    }
}

