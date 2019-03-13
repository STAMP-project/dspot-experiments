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


public class RelationResolverSubsumptionTest {
    private AnalyzerConfiguration configuration;

    private RelationResolver relationResolver;

    private InspectorList a;

    private InspectorList b;

    private RelationResolverSubsumptionTest.Person firstItemInB;

    private RelationResolverSubsumptionTest.Person blockingItem;

    @Test
    public void empty() throws Exception {
        relationResolver = new RelationResolver(new InspectorList(configuration));
        Assert.assertTrue(relationResolver.subsumes(new InspectorList(configuration)));
    }

    @Test
    public void emptyListWithItemsSubsumesEmptyLists() throws Exception {
        Assert.assertTrue(relationResolver.subsumes(new InspectorList(configuration)));
    }

    @Test
    public void recheck() throws Exception {
        Assert.assertFalse(relationResolver.subsumes(b));
        Mockito.verify(firstItemInB).subsumes(ArgumentMatchers.any());
        Mockito.reset(firstItemInB);
        Assert.assertFalse(relationResolver.subsumes(b));
        Mockito.verify(firstItemInB, Mockito.never()).subsumes(ArgumentMatchers.any());
    }

    @Test
    public void recheckWithUpdate() throws Exception {
        Assert.assertFalse(relationResolver.subsumes(b));
        Mockito.reset(firstItemInB);
        // UPDATE
        blockingItem.setAge(15);
        Assert.assertTrue(relationResolver.subsumes(b));
        Mockito.verify(firstItemInB).subsumes(ArgumentMatchers.any());
    }

    @Test
    public void recheckConflictingItemRemoved() throws Exception {
        Assert.assertFalse(relationResolver.subsumes(b));
        Mockito.reset(firstItemInB);
        // UPDATE
        b.remove(blockingItem);
        Assert.assertTrue(relationResolver.subsumes(b));
        Mockito.verify(firstItemInB).subsumes(ArgumentMatchers.any());
    }

    @Test
    public void recheckOtherListBecomesEmpty() throws Exception {
        Assert.assertFalse(relationResolver.subsumes(b));
        Mockito.reset(firstItemInB, blockingItem);
        // UPDATE
        b.clear();
        Assert.assertTrue(relationResolver.subsumes(b));
        Mockito.verify(firstItemInB, Mockito.never()).subsumes(ArgumentMatchers.any());
        Mockito.verify(blockingItem, Mockito.never()).subsumes(ArgumentMatchers.any());
    }

    public class Person implements HasKeys , IsSubsuming {
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
        public boolean subsumes(final Object other) {
            if (other instanceof RelationResolverSubsumptionTest.Person) {
                return (age) == (((RelationResolverSubsumptionTest.Person) (other)).age);
            } else {
                return false;
            }
        }
    }
}

