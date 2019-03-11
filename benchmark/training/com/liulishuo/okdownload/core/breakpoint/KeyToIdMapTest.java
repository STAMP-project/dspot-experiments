/**
 * Copyright (c) 2018 LingoChamp Inc.
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
package com.liulishuo.okdownload.core.breakpoint;


import android.util.SparseArray;
import com.liulishuo.okdownload.DownloadTask;
import java.util.HashMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = NONE)
public class KeyToIdMapTest {
    private KeyToIdMap map;

    private HashMap<String, Integer> keyToIdMap;

    private SparseArray<String> idToKeyMap;

    private DownloadTask task;

    @Test
    public void get() {
        assertThat(map.get(task)).isNull();
        keyToIdMap.put(map.generateKey(task), 1);
        assertThat(map.get(task)).isEqualTo(1);
    }

    @Test
    public void remove() {
        idToKeyMap.put(1, map.generateKey(task));
        keyToIdMap.put(map.generateKey(task), 1);
        map.remove(1);
        assertThat(keyToIdMap).isEmpty();
        assertThat(idToKeyMap.size()).isZero();
    }

    @Test
    public void add() {
        final String key = map.generateKey(task);
        map.add(task, 1);
        assertThat(keyToIdMap).containsKeys(key);
        assertThat(keyToIdMap).containsValues(1);
        assertThat(idToKeyMap.size()).isOne();
        assertThat(idToKeyMap.get(1)).isEqualTo(key);
    }
}

