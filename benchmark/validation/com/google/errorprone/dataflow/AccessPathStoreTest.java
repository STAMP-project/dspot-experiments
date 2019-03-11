/**
 * Copyright 2018 The Error Prone Authors.
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
package com.google.errorprone.dataflow;


import Nullness.BOTTOM;
import Nullness.NONNULL;
import Nullness.NULL;
import com.google.errorprone.dataflow.nullnesspropagation.Nullness;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


/**
 *
 *
 * @author bennostein@google.com (Benno Stein)
 */
@RunWith(JUnit4.class)
public class AccessPathStoreTest {
    @Test
    public void leastUpperBoundEmpty() {
        assertThat(AccessPathStoreTest.newStore().leastUpperBound(AccessPathStoreTest.newStore())).isEqualTo(AccessPathStoreTest.newStore());
    }

    @Test
    public void buildAndGet() {
        AccessPathStore.Builder<Nullness> builder = AccessPathStoreTest.newStore().toBuilder();
        AccessPath path1 = Mockito.mock(AccessPath.class);
        AccessPath path2 = Mockito.mock(AccessPath.class);
        builder.setInformation(path1, NULL);
        builder.setInformation(path2, NONNULL);
        assertThat(builder.build().valueOfAccessPath(path1, BOTTOM)).isEqualTo(NULL);
        assertThat(builder.build().valueOfAccessPath(path2, BOTTOM)).isEqualTo(NONNULL);
        assertThat(AccessPathStoreTest.newStore().heap()).isEmpty();
    }
}

