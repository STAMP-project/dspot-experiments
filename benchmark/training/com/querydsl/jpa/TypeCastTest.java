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
package com.querydsl.jpa;


import com.querydsl.jpa.domain.Animal;
import com.querydsl.jpa.domain.Cat;
import com.querydsl.jpa.domain.InheritedProperties;
import org.junit.Assert;
import org.junit.Test;

import static QAnimal.animal;
import static QCat.cat;
import static QInheritedProperties.inheritedProperties;


public class TypeCastTest {
    @Test
    public void mappedSuperclass() {
        QInheritedProperties subClass = inheritedProperties;
        QSuperclass superClass = subClass._super;
        Assert.assertEquals(InheritedProperties.class, superClass.getType());
        // assertEquals(InheritedProperties.class.getSimpleName(), superClass.getEntityName());
        Assert.assertEquals("inheritedProperties", superClass.toString());
    }

    // @Test
    // public void mappedSuperclass2() {
    // QInheritedProperties subClass = QInheritedProperties.inheritedProperties;
    // QSuperclass superClass = new QSuperclass(subClass.getMetadata());
    // 
    // assertEquals(Superclass.class, superClass.getType());
    // assertEquals(Superclass.class.getSimpleName(), superClass.getEntityName());
    // assertEquals("inheritedProperties", superClass.toString());
    // }
    @Test
    public void subClassToSuper() {
        QCat cat = cat;
        QAnimal animal = new QAnimal(cat);
        Assert.assertEquals(Cat.class, animal.getType());
        // assertEquals(Cat.class.getSimpleName(), animal.getEntityName());
        Assert.assertEquals("cat", animal.toString());
    }

    @Test
    public void subClassToSuper2() {
        QCat cat = cat;
        QAnimal animal = new QAnimal(cat.getMetadata());
        Assert.assertEquals(Animal.class, animal.getType());
        // assertEquals(Animal.class.getSimpleName(), animal.getEntityName());
        Assert.assertEquals("cat", animal.toString());
    }

    @Test
    public void superClassToSub() {
        QAnimal animal = animal;
        QCat cat = new QCat(animal.getMetadata());
        Assert.assertEquals(Cat.class, cat.getType());
        // assertEquals(Cat.class.getSimpleName(), cat.getEntityName());
        Assert.assertEquals("animal", cat.toString());
    }
}

