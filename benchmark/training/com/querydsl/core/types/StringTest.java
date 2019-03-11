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
package com.querydsl.core.types;


import com.querydsl.core.alias.Alias;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class StringTest {
    private static class DummyTemplates extends Templates {}

    @SuppressWarnings("unchecked")
    @Test
    public void patternAvailability() throws IllegalAccessException, IllegalArgumentException {
        Templates ops = new StringTest.DummyTemplates();
        Set<Field> missing = new HashSet<Field>();
        for (Field field : Ops.class.getFields()) {
            if (field.getType().equals(Operator.class)) {
                Operator op = ((Operator) (field.get(null)));
                if ((ops.getTemplate(op)) == null) {
                    missing.add(field);
                }
            }
        }
        for (Class<?> cl : Ops.class.getClasses()) {
            for (Field field : cl.getFields()) {
                if (field.getType().equals(Operator.class)) {
                    Operator op = ((Operator) (field.get(null)));
                    if ((ops.getTemplate(op)) == null) {
                        missing.add(field);
                    }
                }
            }
        }
        if (!(missing.isEmpty())) {
            for (Field field : missing) {
                System.err.println(field.getName());
            }
            Assert.fail();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void toString_() {
        StringTest.SomeType alias = Alias.alias(StringTest.SomeType.class, "alias");
        // Path toString
        Assert.assertEquals("alias.name", Alias.$(alias.getName()).toString());
        Assert.assertEquals("alias.ref.name", Alias.$(alias.getRef().getName()).toString());
        Assert.assertEquals("alias.refs.get(0)", Alias.$(alias.getRefs().get(0)).toString());
        // Operation toString
        Assert.assertEquals("lower(alias.name)", Alias.$(alias.getName()).lower().toString());
        // ConstructorExpression
        ConstructorExpression<StringTest.SomeType> someType = new ConstructorExpression<StringTest.SomeType>(StringTest.SomeType.class, new Class<?>[]{ StringTest.SomeType.class }, Alias.$(alias));
        Assert.assertEquals("new SomeType(alias)", someType.toString());
        // ArrayConstructorExpression
        ArrayConstructorExpression<StringTest.SomeType> someTypeArray = new ArrayConstructorExpression<StringTest.SomeType>(StringTest.SomeType[].class, Alias.$(alias));
        Assert.assertEquals("new SomeType[](alias)", someTypeArray.toString());
    }

    public static class SomeType {
        public SomeType() {
        }

        public SomeType(StringTest.SomeType st) {
        }

        public String getName() {
            return "";
        }

        public StringTest.SomeType getRef() {
            return null;
        }

        public List<StringTest.SomeType> getRefs() {
            return null;
        }

        public int getAmount() {
            return 0;
        }
    }
}

