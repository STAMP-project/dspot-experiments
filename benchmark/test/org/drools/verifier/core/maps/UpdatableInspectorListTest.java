/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.drools.verifier.core.maps;


import java.util.ArrayList;
import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.keys.Key;
import org.drools.verifier.core.index.keys.UUIDKey;
import org.drools.verifier.core.maps.util.HasKeys;
import org.drools.verifier.core.maps.util.HasUUID;
import org.junit.Assert;
import org.junit.Test;


public class UpdatableInspectorListTest {
    private UpdatableInspectorList<HasUUID, UpdatableInspectorListTest.Item> list;

    private AnalyzerConfiguration configuration;

    @Test
    public void add() throws Exception {
        final ArrayList<UpdatableInspectorListTest.Item> updates = new ArrayList<>();
        updates.add(new UpdatableInspectorListTest.Item());
        list.update(updates);
        Assert.assertEquals(1, list.size());
    }

    @Test
    public void reAdd() throws Exception {
        final ArrayList<UpdatableInspectorListTest.Item> updates = new ArrayList<>();
        updates.add(new UpdatableInspectorListTest.Item());
        list.update(updates);
        Assert.assertEquals(1, list.size());
        list.update(updates);
        Assert.assertEquals(1, list.size());
    }

    @Test
    public void reAddNew() throws Exception {
        final ArrayList<UpdatableInspectorListTest.Item> updates = new ArrayList<>();
        updates.add(new UpdatableInspectorListTest.Item());
        list.update(updates);
        Assert.assertEquals(1, list.size());
        updates.add(new UpdatableInspectorListTest.Item());
        list.update(updates);
        Assert.assertEquals(2, list.size());
    }

    @Test
    public void remove() throws Exception {
        final ArrayList<UpdatableInspectorListTest.Item> updates = new ArrayList<>();
        updates.add(new UpdatableInspectorListTest.Item());
        final UpdatableInspectorListTest.Item removeMe = new UpdatableInspectorListTest.Item();
        updates.add(removeMe);
        list.update(updates);
        Assert.assertEquals(2, list.size());
        updates.remove(removeMe);
        list.update(updates);
        Assert.assertEquals(1, list.size());
    }

    private class Item implements HasKeys {
        private UUIDKey uuidKey = configuration.getUUID(this);

        @Override
        public Key[] keys() {
            return new Key[]{ uuidKey };
        }

        @Override
        public UUIDKey getUuidKey() {
            return uuidKey;
        }
    }
}

