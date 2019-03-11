/**
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.querydsl.apt.domain;


import QAbstractClasses2Test_Grant.grant.id;
import com.querydsl.core.types.dsl.NumberPath;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings({ "rawtypes", "serial", "unchecked" })
public class AbstractClasses2Test {
    public interface Archetype<PK extends Serializable, DO extends Serializable> extends Serializable , Comparable<DO> {}

    @MappedSuperclass
    public abstract static class BaseArchetype<PK extends Serializable, DO extends Serializable> implements AbstractClasses2Test.Archetype<PK, DO> {
        @Id
        @GeneratedValue
        PK id;

        String name;

        String description;

        public BaseArchetype() {
        }

        public int compareTo(AbstractClasses2Test.BaseArchetype o) {
            return 0;
        }

        public boolean equals(Object o) {
            return o == (this);
        }
    }

    @Entity
    public static class Grant<P extends AbstractClasses2Test.Party, S extends AbstractClasses2Test.Party> extends AbstractClasses2Test.BaseArchetype<P, S> {
        public int compareTo(S o) {
            return 0;
        }

        public boolean equals(Object o) {
            return o == (this);
        }
    }

    @Entity
    public static class Party extends AbstractClasses2Test.BaseArchetype<Long, AbstractClasses2Test.Party> {
        @OneToMany
        Set<AbstractClasses2Test.PartyRole> roles = new HashSet<AbstractClasses2Test.PartyRole>();

        public Party() {
        }

        public int compareTo(AbstractClasses2Test.Party o) {
            return 0;
        }

        public boolean equals(Object o) {
            return o == (this);
        }
    }

    @Entity
    public static class PartyRole<P extends AbstractClasses2Test.Party> extends AbstractClasses2Test.BaseArchetype<Long, AbstractClasses2Test.PartyRole<P>> {
        @ManyToOne
        P party;

        public PartyRole() {
        }

        public int compareTo(AbstractClasses2Test.PartyRole o) {
            return 0;
        }

        public boolean equals(Object o) {
            return o == (this);
        }
    }

    @Test
    public void grant_id_type_and_class() {
        Assert.assertEquals(QAbstractClasses2Test_Party.class, id.getClass());
        Assert.assertEquals(AbstractClasses2Test.Party.class, id.getType());
    }

    @Test
    public void party_id_type_and_class() {
        Assert.assertEquals(NumberPath.class, QAbstractClasses2Test_Party.party.id.getClass());
        Assert.assertEquals(Long.class, QAbstractClasses2Test_Party.party.id.getType());
    }
}

