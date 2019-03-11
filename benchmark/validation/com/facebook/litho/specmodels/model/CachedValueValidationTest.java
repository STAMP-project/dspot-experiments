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
import ClassNames.COMPONENT_CONTEXT;
import com.facebook.litho.annotations.OnCalculateCachedValue;
import com.facebook.litho.specmodels.internal.ImmutableList;
import com.squareup.javapoet.TypeName;
import java.lang.annotation.Annotation;
import java.util.List;
import org.junit.Test;
import org.mockito.Mockito;

import static ClassNames.COMPONENT;


/**
 * Tests {@link CachedValueValidation}
 */
public class CachedValueValidationTest {
    private final SpecModel mSpecModel = Mockito.mock(SpecModel.class);

    private final CachedValueParamModel mCachedValue1 = Mockito.mock(CachedValueParamModel.class);

    private final Object mRepresentedObject1 = new Object();

    private final Object mDelegateMethodRepresentedObject1 = new Object();

    @Test
    public void testCachedValueWithNoCorrespondingCalculateMethod() {
        Mockito.when(mSpecModel.getDelegateMethods()).thenReturn(ImmutableList.<SpecMethodModel<DelegateMethod, Void>>of());
        List<SpecModelValidationError> validationErrors = CachedValueValidation.validate(mSpecModel);
        assertThat(validationErrors).hasSize(1);
        assertThat(validationErrors.get(0).element).isEqualTo(mRepresentedObject1);
        assertThat(validationErrors.get(0).message).isEqualTo("The cached value must have a corresponding @OnCalculateCachedValue method that has the same name.");
    }

    @Test
    public void testCachedValueWithDifferentReturnTypeInCalculateMethod() {
        SpecMethodModel<DelegateMethod, Void> delegateMethod = SpecMethodModel.<DelegateMethod, Void>builder().annotations(ImmutableList.<Annotation>of(new OnCalculateCachedValue() {
            @Override
            public String name() {
                return "name1";
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return OnCalculateCachedValue.class;
            }
        })).modifiers(ImmutableList.<javax.lang.model.element.Modifier>of()).name("onCalculateName1").returnTypeSpec(new TypeSpec(TypeName.INT)).typeVariables(ImmutableList.of()).methodParams(ImmutableList.<MethodParamModel>of()).representedObject(mDelegateMethodRepresentedObject1).build();
        Mockito.when(mSpecModel.getDelegateMethods()).thenReturn(ImmutableList.of(delegateMethod));
        List<SpecModelValidationError> validationErrors = CachedValueValidation.validate(mSpecModel);
        assertThat(validationErrors).hasSize(1);
        assertThat(validationErrors.get(0).element).isEqualTo(mRepresentedObject1);
        assertThat(validationErrors.get(0).message).isEqualTo("CachedValue param types and the return type of the corresponding @OnCalculateCachedValue method must be the same.");
    }

    @Test
    public void testCachedValueWithComponentType() {
        Mockito.when(mCachedValue1.getTypeName()).thenReturn(COMPONENT);
        SpecMethodModel<DelegateMethod, Void> delegateMethod = SpecMethodModel.<DelegateMethod, Void>builder().annotations(ImmutableList.<Annotation>of(new OnCalculateCachedValue() {
            @Override
            public String name() {
                return "name1";
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return OnCalculateCachedValue.class;
            }
        })).modifiers(ImmutableList.<javax.lang.model.element.Modifier>of()).name("onCalculateName1").returnTypeSpec(new TypeSpec(COMPONENT)).typeVariables(ImmutableList.of()).methodParams(ImmutableList.<MethodParamModel>of()).representedObject(mDelegateMethodRepresentedObject1).build();
        Mockito.when(mSpecModel.getDelegateMethods()).thenReturn(ImmutableList.of(delegateMethod));
        List<SpecModelValidationError> validationErrors = CachedValueValidation.validate(mSpecModel);
        assertThat(validationErrors).hasSize(1);
        assertThat(validationErrors.get(0).element).isEqualTo(mRepresentedObject1);
        assertThat(validationErrors.get(0).message).isEqualTo("Cached values must not be Components, since Components are stateful. Just create the Component as normal.");
    }

    @Test
    public void testOnCalculateCachedValueWithBadParams() {
        Object paramObject = new Object();
        SpecMethodModel<DelegateMethod, Void> delegateMethod = SpecMethodModel.<DelegateMethod, Void>builder().annotations(ImmutableList.<Annotation>of(new OnCalculateCachedValue() {
            @Override
            public String name() {
                return "name1";
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return OnCalculateCachedValue.class;
            }
        })).modifiers(ImmutableList.<javax.lang.model.element.Modifier>of()).name("onCalculateName1").returnTypeSpec(new TypeSpec(TypeName.BOOLEAN)).typeVariables(ImmutableList.of()).methodParams(ImmutableList.of(com.facebook.litho.testing.specmodels.MockMethodParamModel.newBuilder().name("c").type(COMPONENT_CONTEXT).representedObject(paramObject).build())).representedObject(mDelegateMethodRepresentedObject1).build();
        Mockito.when(mSpecModel.getDelegateMethods()).thenReturn(ImmutableList.of(delegateMethod));
        List<SpecModelValidationError> validationErrors = CachedValueValidation.validate(mSpecModel);
        assertThat(validationErrors).hasSize(1);
        assertThat(validationErrors.get(0).element).isEqualTo(paramObject);
        assertThat(validationErrors.get(0).message).isEqualTo("@OnCalculateCachedValue methods may only take Props, @InjectProps and State as params.");
    }
}

