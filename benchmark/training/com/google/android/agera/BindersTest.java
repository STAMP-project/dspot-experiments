/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.agera;


import com.google.android.agera.test.matchers.HasPrivateConstructor;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public final class BindersTest {
    private static final int VALUE = 44;

    private static final int SECOND_VALUE = 43;

    @Test
    public void shouldHandleCallsToNullBinder() {
        Binders.nullBinder().bind(BindersTest.VALUE, BindersTest.SECOND_VALUE);
    }

    @Test
    public void shouldHavePrivateConstructor() {
        MatcherAssert.assertThat(Binders.class, HasPrivateConstructor.hasPrivateConstructor());
    }
}

