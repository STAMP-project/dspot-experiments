/**
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.bigtable.admin.v2.models;


import Instance.State.READY;
import Instance.Type.PRODUCTION;
import com.google.bigtable.admin.v2.Instance;
import com.google.bigtable.admin.v2.Instance.State;
import com.google.bigtable.admin.v2.Instance.Type;
import com.google.common.collect.Lists;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class InstanceTest {
    @Test
    public void testFromProto() {
        Instance proto = com.google.bigtable.admin.v2.Instance.newBuilder().setName("projects/my-project/instances/my-instance").setDisplayName("my display name").setType(com.google.bigtable.admin.v2.Instance).setState(com.google.bigtable.admin.v2.Instance).putLabels("label1", "value1").putLabels("label2", "value2").build();
        Instance result = Instance.fromProto(proto);
        assertThat(result.getId()).isEqualTo("my-instance");
        assertThat(result.getDisplayName()).isEqualTo("my display name");
        assertThat(result.getType()).isEqualTo(PRODUCTION);
        assertThat(result.getState()).isEqualTo(READY);
        assertThat(result.getLabels()).containsExactly("label1", "value1", "label2", "value2");
    }

    @Test
    public void testRequiresName() {
        Instance proto = com.google.bigtable.admin.v2.Instance.newBuilder().setDisplayName("my display name").setType(com.google.bigtable.admin.v2.Instance).setState(com.google.bigtable.admin.v2.Instance).putLabels("label1", "value1").putLabels("label2", "value2").build();
        Exception actualException = null;
        try {
            Instance.fromProto(proto);
        } catch (Exception e) {
            actualException = e;
        }
        assertThat(actualException).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testTypeEnumUpToDate() {
        List<Type> validProtoValues = Lists.newArrayList(com.google.bigtable.admin.v2.Instance.Type.values());
        // TYPE_UNSPECIFIED is not surfaced
        validProtoValues.remove(com.google.bigtable.admin.v2.Instance.Type);
        Exception actualError = null;
        try {
            Instance.Type.fromProto(com.google.bigtable.admin.v2.Instance.Type);
        } catch (Exception e) {
            actualError = e;
        }
        assertThat(actualError).isInstanceOf(IllegalArgumentException.class);
        List<Instance.Type> validModelValues = Lists.newArrayList(Instance.Type.values());
        List<Instance.Type> actualModelValues = Lists.newArrayList();
        for (Type protoValue : validProtoValues) {
            actualModelValues.add(Instance.Type.fromProto(protoValue));
        }
        assertThat(actualModelValues).containsExactlyElementsIn(validModelValues);
    }

    @Test
    public void testStateEnumUpToDate() {
        List<State> validProtoValues = Lists.newArrayList(com.google.bigtable.admin.v2.Instance.State.values());
        List<Instance.State> validModelValues = Lists.newArrayList(Instance.State.values());
        List<Instance.State> actualModelValues = Lists.newArrayList();
        for (State protoValue : validProtoValues) {
            Instance.State modelValue = Instance.State.fromProto(protoValue);
            actualModelValues.add(modelValue);
        }
        assertThat(actualModelValues).containsExactlyElementsIn(validModelValues);
    }
}

