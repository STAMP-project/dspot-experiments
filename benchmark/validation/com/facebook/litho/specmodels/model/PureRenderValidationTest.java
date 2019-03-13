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


import java.util.List;
import org.junit.Test;
import org.mockito.Mockito;


public class PureRenderValidationTest {
    interface PureRenderSpecModel extends HasPureRender , SpecModel {}

    private final Object mDelegateMethodRepresentedObject1 = new Object();

    private PureRenderValidationTest.PureRenderSpecModel mSpecModel = Mockito.mock(PureRenderValidationTest.PureRenderSpecModel.class);

    @Test
    public void testShouldUpdateDefinedButNotPureRender() {
        Mockito.when(isPureRender()).thenReturn(false);
        List<SpecModelValidationError> validationErrors = PureRenderValidation.validate(mSpecModel);
        assertThat(validationErrors).hasSize(1);
        assertThat(validationErrors.get(0).element).isEqualTo(mDelegateMethodRepresentedObject1);
        assertThat(validationErrors.get(0).message).isEqualTo(("Specs defining a method annotated with @ShouldUpdate should also set " + "isPureRender = true in the top-level spec annotation."));
    }
}

