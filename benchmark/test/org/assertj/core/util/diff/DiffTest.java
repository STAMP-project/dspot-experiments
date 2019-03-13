/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2019 the original author or authors.
 */
package org.assertj.core.util.diff;


import java.util.Collections;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;


public class DiffTest {
    @Test
    public void testDiff_Insert() {
        Patch<String> patch = DiffUtils.diff(Lists.newArrayList("hhh"), Lists.newArrayList("hhh", "jjj", "kkk"));
        List<Delta<String>> deltas = patch.getDeltas();
        Assertions.assertThat(deltas.size()).isEqualTo(1);
        Delta<String> delta = deltas.get(0);
        Assertions.assertThat(delta).isInstanceOf(InsertDelta.class);
        Assertions.assertThat(delta.getOriginal()).isEqualTo(new Chunk(1, Collections.emptyList()));
        Assertions.assertThat(delta.getRevised()).isEqualTo(new Chunk(1, Lists.newArrayList("jjj", "kkk")));
    }

    @Test
    public void testDiff_Delete() {
        Patch<String> patch = DiffUtils.diff(Lists.newArrayList("ddd", "fff", "ggg"), Lists.newArrayList("ggg"));
        List<Delta<String>> deltas = patch.getDeltas();
        Assertions.assertThat(deltas.size()).isEqualTo(1);
        Delta<String> delta = deltas.get(0);
        Assertions.assertThat(delta).isInstanceOf(DeleteDelta.class);
        Assertions.assertThat(delta.getOriginal()).isEqualTo(new Chunk(0, Lists.newArrayList("ddd", "fff")));
        Assertions.assertThat(delta.getRevised()).isEqualTo(new Chunk(0, Collections.emptyList()));
    }

    @Test
    public void testDiff_Change() {
        List<String> changeTest_from = Lists.newArrayList("aaa", "bbb", "ccc");
        List<String> changeTest_to = Lists.newArrayList("aaa", "zzz", "ccc");
        Patch<String> patch = DiffUtils.diff(changeTest_from, changeTest_to);
        List<Delta<String>> deltas = patch.getDeltas();
        Assertions.assertThat(deltas.size()).isEqualTo(1);
        Delta<String> delta = deltas.get(0);
        Assertions.assertThat(delta).isInstanceOf(ChangeDelta.class);
        Assertions.assertThat(delta.getOriginal()).isEqualTo(new Chunk(1, Lists.newArrayList("bbb")));
        Assertions.assertThat(delta.getRevised()).isEqualTo(new Chunk(1, Lists.newArrayList("zzz")));
    }

    @Test
    public void testDiff_EmptyList() {
        Patch<Object> patch = DiffUtils.diff(Collections.emptyList(), Collections.emptyList());
        Assertions.assertThat(patch.getDeltas().size()).isEqualTo(0);
    }

    @Test
    public void testDiff_EmptyListWithNonEmpty() {
        List<String> emptyList = Collections.emptyList();
        Patch<String> patch = DiffUtils.diff(emptyList, Lists.newArrayList("aaa"));
        List<Delta<String>> deltas = patch.getDeltas();
        Assertions.assertThat(deltas.size()).isEqualTo(1);
        Assertions.assertThat(deltas.get(0)).isInstanceOf(InsertDelta.class);
    }
}

