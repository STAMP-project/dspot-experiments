/**
 * Copyright (C) 2018 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.squareup.moshi;


import JsonAdapter.Factory;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import org.junit.Test;


public final class DeferredAdapterTest {
    /**
     * When a type's JsonAdapter is circularly-dependent, Moshi creates a 'deferred adapter' to make
     * the cycle work. It's important that any adapters that depend on this deferred adapter don't
     * leak out until it's ready.
     *
     * <p>This test sets up a circular dependency [BlueNode -> GreenNode -> BlueNode] and then tries
     * to use a GreenNode JSON adapter before the BlueNode JSON adapter is built. It creates a
     * similar cycle [BlueNode -> RedNode -> BlueNode] so the order adapters are retrieved is
     * insignificant.
     *
     * <p>This used to trigger a crash because we'd incorrectly put the GreenNode JSON adapter in the
     * cache even though it depended upon an incomplete BlueNode JSON adapter.
     */
    @Test
    public void concurrentSafe() {
        final List<Throwable> failures = new ArrayList<>();
        JsonAdapter.Factory factory = new JsonAdapter.Factory() {
            int redAndGreenCount = 0;

            @Override
            @Nullable
            public JsonAdapter<?> create(Type type, Set<? extends Annotation> annotations, final Moshi moshi) {
                if (((type == (DeferredAdapterTest.RedNode.class)) || (type == (DeferredAdapterTest.GreenNode.class))) && (((redAndGreenCount)++) == 1)) {
                    doInAnotherThread(new Runnable() {
                        @Override
                        public void run() {
                            DeferredAdapterTest.GreenNode greenBlue = new DeferredAdapterTest.GreenNode(new DeferredAdapterTest.BlueNode(null, null));
                            assertThat(moshi.adapter(DeferredAdapterTest.GreenNode.class).toJson(greenBlue)).isEqualTo("{\"blue\":{}}");
                            DeferredAdapterTest.RedNode redBlue = new DeferredAdapterTest.RedNode(new DeferredAdapterTest.BlueNode(null, null));
                            assertThat(moshi.adapter(DeferredAdapterTest.RedNode.class).toJson(redBlue)).isEqualTo("{\"blue\":{}}");
                        }
                    });
                }
                return null;
            }
        };
        Moshi moshi = new Moshi.Builder().add(factory).build();
        JsonAdapter<DeferredAdapterTest.BlueNode> jsonAdapter = moshi.adapter(DeferredAdapterTest.BlueNode.class);
        assertThat(jsonAdapter.toJson(new DeferredAdapterTest.BlueNode(new DeferredAdapterTest.GreenNode(new DeferredAdapterTest.BlueNode(null, null)), null))).isEqualTo("{\"green\":{\"blue\":{}}}");
        assertThat(failures).isEmpty();
    }

    static class BlueNode {
        @Nullable
        DeferredAdapterTest.GreenNode green;

        @Nullable
        DeferredAdapterTest.RedNode red;

        BlueNode(@Nullable
        DeferredAdapterTest.GreenNode green, @Nullable
        DeferredAdapterTest.RedNode red) {
            this.green = green;
            this.red = red;
        }
    }

    static class RedNode {
        @Nullable
        DeferredAdapterTest.BlueNode blue;

        RedNode(@Nullable
        DeferredAdapterTest.BlueNode blue) {
            this.blue = blue;
        }
    }

    static class GreenNode {
        @Nullable
        DeferredAdapterTest.BlueNode blue;

        GreenNode(@Nullable
        DeferredAdapterTest.BlueNode blue) {
            this.blue = blue;
        }
    }
}

