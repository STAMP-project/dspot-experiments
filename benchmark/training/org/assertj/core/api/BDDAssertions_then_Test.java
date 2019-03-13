/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2019 the original author or authors.
 */
package org.assertj.core.api;


import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link org.assertj.core.api.BDDAssertions#then(String)}</code>.
 *
 * @author Mariusz Smykula
 */
public class BDDAssertions_then_Test {
    AssertFactory<String, StringAssert> stringAssertFactory = new AssertFactory<String, StringAssert>() {
        @Override
        public StringAssert createAssert(String string) {
            return new StringAssert(string);
        }
    };

    AssertFactory<Integer, IntegerAssert> integerAssertFactory = new AssertFactory<Integer, IntegerAssert>() {
        @Override
        public IntegerAssert createAssert(Integer string) {
            return new IntegerAssert(string);
        }
    };

    @Test
    public void then_char() {
        BDDAssertions.then('z').isGreaterThan('a');
    }

    @Test
    public void then_Character() {
        BDDAssertions.then(new Character('A')).isEqualTo(new Character('A'));
    }

    @Test
    public void then_char_array() {
        BDDAssertions.then(new char[]{ 'a', 'b', 'c' }).contains('b');
    }

    @Test
    public void then_Charsequence() {
        BDDAssertions.then("abc".subSequence(0, 1)).contains("a");
    }

    @Test
    public void then_Class() {
        BDDAssertions.then("Foo".getClass()).isEqualTo(String.class);
    }

    @Test
    public void should_delegate_to_assert_comparable() {
        class IntBox implements Comparable<IntBox> {
            private final Integer number;

            IntBox(Integer number) {
                this.number = number;
            }

            @Override
            public int compareTo(IntBox o) {
                return number.compareTo(o.number);
            }
        }
        BDDAssertions.then(new IntBox(1)).isLessThan(new IntBox(2));
    }

    @Test
    public void then_Iterable() {
        Iterable<String> iterable = Arrays.asList("1");
        BDDAssertions.then(iterable).contains("1");
        BDDAssertions.then(iterable, StringAssert.class).first().startsWith("1");
        BDDAssertions.then(iterable, stringAssertFactory).first().startsWith("1");
    }

    @Test
    public void then_Iterator() {
        Iterator<String> iterator = Collections.singletonList("1").iterator();
        BDDAssertions.then(iterator).hasNext();
    }

    @Test
    public void then_double() {
        BDDAssertions.then(1.0).isNotZero();
    }

    @Test
    public void then_Double() {
        BDDAssertions.then(Double.valueOf(1.0)).isNotZero();
    }

    @Test
    public void then_double_array() {
        BDDAssertions.then(new double[]{ 1.0, 2.0 }).contains(2.0);
    }

    @Test
    public void then_float() {
        BDDAssertions.then(1.0F).isEqualTo(1.0F);
    }

    @Test
    public void then_Float() {
        BDDAssertions.then(Float.valueOf(1.0F)).isEqualTo(1.0F);
    }

    @Test
    public void then_float_array() {
        BDDAssertions.then(new float[]{ 1.0F, 2.0F }).contains(2.0F);
    }

    @Test
    public void then_long() {
        BDDAssertions.then(1L).isEqualTo(1L);
    }

    @Test
    public void then_Long() {
        BDDAssertions.then(Long.valueOf(1L)).isEqualTo(1L);
    }

    @Test
    public void then_long_array() {
        BDDAssertions.then(new long[]{ 1L, 2L }).contains(2L);
    }

    @Test
    public void then_Object() {
        BDDAssertions.then(new Object()).isNotNull();
    }

    @Test
    public void then_Object_array() {
        BDDAssertions.then(new Object[]{ new Object(), new Object() }).hasSize(2);
    }

    @Test
    public void then_short() {
        BDDAssertions.then(((short) (1))).isEqualTo(((short) (1)));
    }

    @Test
    public void then_Short() {
        BDDAssertions.then(Short.valueOf("1")).isEqualTo(((short) (1)));
    }

    @Test
    public void then_short_array() {
        BDDAssertions.then(new short[]{ ((short) (1)), ((short) (2)) }).contains(((short) (2)));
    }

    @Test
    public void then_Throwable() {
        BDDAssertions.then(new IllegalArgumentException("Foo")).hasMessage("Foo");
    }

    @Test
    public void then_BigDecimal() {
        BDDAssertions.then(BigDecimal.ONE).isEqualTo(BigDecimal.valueOf(1));
    }

    @Test
    public void then_boolean() {
        BDDAssertions.then(true).isEqualTo(Boolean.TRUE);
    }

    @Test
    public void then_Boolean() {
        BDDAssertions.then(Boolean.TRUE).isEqualTo(true);
    }

    @Test
    public void then_boolean_array() {
        BDDAssertions.then(new boolean[]{ true, false }).isEqualTo(new boolean[]{ true, false });
    }

    @Test
    public void then_byte() {
        BDDAssertions.then(((byte) (7))).isEqualTo(((byte) (7)));
    }

    @Test
    public void then_Byte() {
        BDDAssertions.then(Byte.valueOf(((byte) (8)))).isEqualTo(((byte) (8)));
    }

    @Test
    public void then_byte_array() {
        BDDAssertions.then(new byte[]{ 10, 11 }).contains(((byte) (11)));
    }

    @Test
    public void then_int() {
        BDDAssertions.then(1).isEqualTo(1);
    }

    @Test
    public void then_Integer() {
        BDDAssertions.then(Integer.valueOf(4)).isEqualTo(4);
    }

    @Test
    public void then_int_array() {
        BDDAssertions.then(new int[]{ 2, 3 }).isEqualTo(new int[]{ 2, 3 });
    }

    @Test
    public void then_List() {
        List<Integer> list = Arrays.asList(5, 6);
        BDDAssertions.then(list).hasSize(2);
        BDDAssertions.then(list, IntegerAssert.class).first().isLessThan(10);
        BDDAssertions.then(list, integerAssertFactory).first().isLessThan(10);
    }

    @Test
    public void then_String() {
        BDDAssertions.then("Foo").isEqualTo("Foo").isGreaterThan("Bar");
    }

    @Test
    public void then_Date() {
        BDDAssertions.then(new Date()).isNotNull();
    }

    @Test
    public void then_Map() {
        BDDAssertions.then(new HashMap()).isEmpty();
    }

    @Test
    public void should_build_ThrowableAssert_with_throwable_thrown() {
        BDDAssertions.thenThrownBy(new ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                throw new Throwable("something was wrong");
            }
        }).isInstanceOf(Throwable.class).hasMessage("something was wrong");
    }

    @Test
    public void then_explicit_Object() {
        BDDAssertions.thenObject(new LinkedList()).matches(( l) -> (l.peek()) == null);
    }

    @Test
    public void then_URI() throws URISyntaxException {
        BDDAssertions.then(new URI("http://assertj.org")).hasNoPort();
    }
}

