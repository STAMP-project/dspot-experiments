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


import QEmbeddableInterfaceTest_EmbeddableInterface.embeddableInterface.name;
import QEmbeddableInterfaceTest_EntityClass.entityClass.children;
import java.util.Collection;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import org.junit.Assert;
import org.junit.Test;


public class EmbeddableInterfaceTest {
    @Entity
    public static class EntityClass {
        @ElementCollection(targetClass = EmbeddableInterfaceTest.EmbeddableClass.class)
        Collection<EmbeddableInterfaceTest.EmbeddableInterface> children;
    }

    @Embeddable
    public interface EmbeddableInterface {
        String getName();
    }

    @Embeddable
    public static class EmbeddableClass implements EmbeddableInterfaceTest.EmbeddableInterface {
        @Override
        public String getName() {
            return null;
        }
    }

    @Test
    public void type() {
        Assert.assertEquals(QEmbeddableInterfaceTest_EmbeddableInterface.class, children.any().getClass());
    }

    @Test
    public void properties() {
        Assert.assertNotNull(name);
        Assert.assertNotNull(QEmbeddableInterfaceTest_EmbeddableClass.embeddableClass.name);
    }
}

