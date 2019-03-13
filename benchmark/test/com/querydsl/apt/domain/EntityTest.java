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


import QEntityTest_Entity1.entity1.entity1Ref.entity1Ref.entity1Field;
import QEntityTest_Entity4.entity4.supertypeField;
import com.querydsl.core.annotations.QueryEntity;
import com.querydsl.core.annotations.QueryInit;
import com.querydsl.core.annotations.QuerySupertype;
import org.junit.Assert;
import org.junit.Test;

import static QEntityTest_Entity3.entity3;


public class EntityTest extends AbstractTest {
    private static final QEntityTest_Entity3 entity3 = entity3;

    @QueryEntity
    public static class EntityNoReferences {}

    @QueryEntity
    public static class Entity1 {
        public String entity1Field;

        public EntityTest.Entity1 entity1Ref;
    }

    @QueryEntity
    public static class Entity2 extends EntityTest.Supertype {
        public String entity2Field;

        public EntityTest.Entity2 entity2Ref;
    }

    @QueryEntity
    public static class Entity3 extends EntityTest.Entity2 {
        public String entity3Field;

        @QueryInit("*")
        public EntityTest.Entity3 entity3Ref;
    }

    @QueryEntity
    public static class Entity4 extends EntityTest.Supertype2 {}

    @QuerySupertype
    public static class Supertype {
        public String supertypeField;

        @QueryInit("entity2Ref")
        public EntityTest.Entity2 superTypeEntityRef;
    }

    @QuerySupertype
    public static class Supertype2 extends EntityTest.Supertype {}

    @Test
    public void initialization_depth() {
        Assert.assertNotNull(entity1Field);
    }

    @Test
    public void inheritance() {
        Assert.assertNotNull(EntityTest.entity3.entity3Ref.entity2Ref);
        Assert.assertNotNull(EntityTest.entity3.entity3Ref.entity3Ref);
        // super
        Assert.assertNotNull(EntityTest.entity3.entity3Ref._super.entity2Ref);
    }

    @Test
    public void supertype_paths() {
        Assert.assertNotNull(EntityTest.entity3.superTypeEntityRef.entity2Ref);
        Assert.assertNotNull(EntityTest.entity3._super.superTypeEntityRef.entity2Ref);
        Assert.assertNotNull(EntityTest.entity3._super._super.superTypeEntityRef.entity2Ref);
        Assert.assertNotNull(supertypeField);
    }

    @Test
    public void constructors() throws NoSuchMethodException, SecurityException {
        Class<?>[] types = new Class<?>[]{ Class.class, PathMetadata.class, PathInits.class };
        QEntityTest_Entity1.class.getConstructor(types);
        QEntityTest_Entity2.class.getConstructor(types);
        QEntityTest_Entity3.class.getConstructor(types);
        QEntityTest_Entity4.class.getConstructor(types);
        QEntityTest_Supertype.class.getConstructor(types);
        QEntityTest_Supertype2.class.getConstructor(types);
    }

    @Test(expected = NoSuchMethodException.class)
    public void constructors2() throws NoSuchMethodException, SecurityException {
        Class<?>[] types = new Class<?>[]{ Class.class, PathMetadata.class, PathInits.class };
        QEntityTest_EntityNoReferences.class.getConstructor(types);
    }
}

