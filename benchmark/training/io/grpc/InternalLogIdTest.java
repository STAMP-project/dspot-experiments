/**
 * Copyright 2018 The gRPC Authors
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
package io.grpc;


import com.google.common.truth.Truth;
import java.io.Serializable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class InternalLogIdTest {
    private static final String NO_DETAILS = null;

    @Test
    public void shortName() {
        InternalLogId name = InternalLogId.allocate("foo", InternalLogIdTest.NO_DETAILS);
        Truth.assertThat(name.shortName()).matches("foo<\\d+>");
    }

    @Test
    public void toString_includesDetails() {
        InternalLogId name = InternalLogId.allocate("foo", "deets");
        Truth.assertThat(name.toString()).matches("foo<\\d+>: \\(deets\\)");
    }

    @Test
    public void shortClassName() {
        InternalLogId name = InternalLogId.allocate(getClass(), "deets");
        Truth.assertThat(name.toString()).matches("InternalLogIdTest<\\d+>: \\(deets\\)");
    }

    @Test
    public void shortAnonymousClassName() {
        InternalLogId name = InternalLogId.allocate(new Serializable() {}.getClass(), "deets");
        Truth.assertThat(name.toString()).matches("InternalLogIdTest\\$\\d+<\\d+>: \\(deets\\)");
    }
}

