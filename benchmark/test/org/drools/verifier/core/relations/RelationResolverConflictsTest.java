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
package org.drools.verifier.core.relations;


import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.keys.Key;
import org.drools.verifier.core.index.keys.UUIDKey;
import org.drools.verifier.core.maps.InspectorList;
import org.drools.verifier.core.maps.util.HasKeys;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class RelationResolverConflictsTest {
    private AnalyzerConfiguration configuration;

    private RelationResolver relationResolver;

    private InspectorList a;

    private InspectorList b;

    private RelationResolverConflictsTest.Person isConflicting;

    private RelationResolverConflictsTest.Person firstItemInA;

    @Test
    public void empty() throws Exception {
        relationResolver = new RelationResolver(new InspectorList(configuration));
        Assert.assertFalse(relationResolver.isConflicting(new InspectorList(configuration)));
    }

    @Test
    public void recheck() throws Exception {
        Assert.assertTrue(relationResolver.isConflicting(b));
        Mockito.verify(firstItemInA).conflicts(ArgumentMatchers.any());
        Mockito.reset(firstItemInA);
        Assert.assertTrue(relationResolver.isConflicting(b));
        Mockito.verify(firstItemInA, Mockito.never()).conflicts(ArgumentMatchers.any());
    }

    @Test
    public void recheckWithUpdate() throws Exception {
        Assert.assertTrue(relationResolver.isConflicting(b));
        Mockito.reset(firstItemInA);
        // UPDATE
        isConflicting.setAge(10);
        Assert.assertFalse(relationResolver.isConflicting(b));
        Mockito.verify(firstItemInA).conflicts(ArgumentMatchers.any());
    }

    @Test
    public void recheckConflictingItemRemoved() throws Exception {
        Assert.assertTrue(relationResolver.isConflicting(b));
        Mockito.reset(firstItemInA);
        // UPDATE
        a.remove(isConflicting);
        Assert.assertFalse(relationResolver.isConflicting(b));
        Mockito.verify(firstItemInA).conflicts(ArgumentMatchers.any());
    }

    @Test
    public void recheckOtherListBecomesEmpty() throws Exception {
        Assert.assertTrue(relationResolver.isConflicting(b));
        Mockito.reset(firstItemInA, isConflicting);
        // UPDATE
        b.clear();
        Assert.assertFalse(relationResolver.isConflicting(b));
        Mockito.verify(firstItemInA, Mockito.never()).conflicts(ArgumentMatchers.any());
        Mockito.verify(isConflicting, Mockito.never()).conflicts(ArgumentMatchers.any());
    }

    public class Person implements HasKeys , IsConflicting {
        int age;

        private UUIDKey uuidKey = configuration.getUUID(this);

        public Person(final int age) {
            this.age = age;
        }

        @Override
        public UUIDKey getUuidKey() {
            return uuidKey;
        }

        @Override
        public Key[] keys() {
            return new Key[]{ uuidKey };
        }

        public void setAge(final int age) {
            this.age = age;
        }

        @Override
        public boolean conflicts(final Object other) {
            if (other instanceof RelationResolverConflictsTest.Person) {
                return (age) != (((RelationResolverConflictsTest.Person) (other)).age);
            } else {
                return false;
            }
        }
    }
}

