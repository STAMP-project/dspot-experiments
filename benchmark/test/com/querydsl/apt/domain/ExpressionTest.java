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


import QAnimalTest_Animal.animal;
import QAnimalTest_Cat.cat;
import QConstructorTest_Category.category;
import QConstructorTest_ClassWithConstructor.classWithConstructor;
import QEmbeddableTest_EntityWithEmbedded.entityWithEmbedded;
import QEntityTest_Entity1.entity1;
import QEntityTest_Entity2.entity2;
import QEntityTest_Entity3.entity3;
import QGenericTest_GenericType.genericType;
import QGenericTest_ItemType.itemType;
import QInterfaceTypeTest_InterfaceType.interfaceType;
import QInterfaceTypeTest_InterfaceType2.interfaceType2;
import QInterfaceTypeTest_InterfaceType3.interfaceType3;
import QInterfaceTypeTest_InterfaceType4.interfaceType4;
import QInterfaceTypeTest_InterfaceType5.interfaceType5;
import QJodaTimeSupportTest_JodaTimeSupport.jodaTimeSupport;
import QQueryInitTest_PEntity.pEntity;
import QQueryInitTest_PEntity2.pEntity2;
import QQueryInitTest_PEntity3.pEntity3;
import QQueryInitTest_PEntity4.pEntity4;
import QQueryTypeTest_QueryTypeEntity.queryTypeEntity;
import QRelationTest_Reference.reference;
import QRelationTest_RelationType.relationType;
import QReservedNamesTest_ReservedNames.reservedNames;
import QSimpleTypesTest_SimpleTypes.simpleTypes;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringExpression;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.junit.Test;


public class ExpressionTest {
    @Test
    public void test() throws Throwable {
        List<Expression<?>> exprs = new java.util.ArrayList<Expression<?>>();
        exprs.add(animal);
        exprs.add(cat);
        exprs.add(category);
        exprs.add(classWithConstructor);
        exprs.add(entity1);
        exprs.add(entity2);
        exprs.add(entity3);
        exprs.add(entityWithEmbedded);
        exprs.add(genericType);
        exprs.add(interfaceType);
        exprs.add(interfaceType2);
        exprs.add(interfaceType3);
        exprs.add(interfaceType4);
        exprs.add(interfaceType5);
        exprs.add(itemType);
        exprs.add(jodaTimeSupport);
        exprs.add(pEntity);
        exprs.add(pEntity2);
        exprs.add(pEntity3);
        exprs.add(pEntity4);
        exprs.add(queryTypeEntity);
        exprs.add(reference);
        exprs.add(relationType);
        exprs.add(reservedNames);
        exprs.add(simpleTypes);
        exprs.add(ConstantImpl.create("Hello World!"));
        exprs.add(ConstantImpl.create(1000));
        exprs.add(ConstantImpl.create(10L));
        exprs.add(ConstantImpl.create(true));
        exprs.add(ConstantImpl.create(false));
        Set<Expression<?>> toVisit = new java.util.HashSet<Expression<?>>();
        // all entities
        toVisit.addAll(exprs);
        // and all their direct properties
        for (Expression<?> expr : exprs) {
            for (Field field : expr.getClass().getFields()) {
                Object rv = field.get(expr);
                if (rv instanceof Expression) {
                    if (rv instanceof StringExpression) {
                        StringExpression str = ((StringExpression) (rv));
                        toVisit.add(str.toLowerCase());
                        toVisit.add(str.charAt(0));
                        toVisit.add(str.isEmpty());
                    } else
                        if (rv instanceof BooleanExpression) {
                            BooleanExpression b = ((BooleanExpression) (rv));
                            toVisit.add(b.not());
                        }

                    toVisit.add(((Expression<?>) (rv)));
                }
            }
        }
        Set<String> failures = new TreeSet<String>();
        for (Expression<?> expr : toVisit) {
            for (Method method : expr.getClass().getMethods()) {
                if (method.getName().equals("getParameter")) {
                    continue;
                }
                if (method.getName().equals("getArg")) {
                    continue;
                }
                if (((method.getReturnType()) != (void.class)) && (!(method.getReturnType().isPrimitive()))) {
                    Class<?>[] types = method.getParameterTypes();
                    Object[] args;
                    if ((types.length) == 0) {
                        args = new Object[0];
                    } else
                        if ((types.length) == 1) {
                            if ((types[0]) == (int.class)) {
                                args = new Object[]{ 1 };
                            } else
                                if ((types[0]) == (boolean.class)) {
                                    args = new Object[]{ Boolean.TRUE };
                                } else {
                                    continue;
                                }

                        } else {
                            continue;
                        }

                    Object rv = method.invoke(expr, args);
                    if ((method.invoke(expr, args)) != rv) {
                        failures.add(((((expr.getClass().getSimpleName()) + ".") + (method.getName())) + " is unstable"));
                    }
                }
            }
        }
        if ((failures.size()) > 0) {
            System.err.println((("Got " + (failures.size())) + " failures\n"));
        }
        for (String failure : failures) {
            System.err.println(failure);
        }
        // assertTrue("Got "+failures.size()+" failures",failures.isEmpty());
    }
}

