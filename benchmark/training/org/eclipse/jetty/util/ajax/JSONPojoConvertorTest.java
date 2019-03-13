/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.util.ajax;


import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Test to converts POJOs to JSON and vice versa.
 */
public class JSONPojoConvertorTest {
    @Test
    public void testFoo() {
        JSON json = new JSON();
        json.addConvertor(JSONPojoConvertorTest.Foo.class, new JSONPojoConvertor(JSONPojoConvertorTest.Foo.class));
        json.addConvertor(JSONPojoConvertorTest.Bar.class, new JSONPojoConvertor(JSONPojoConvertorTest.Bar.class));
        json.addConvertor(JSONPojoConvertorTest.Baz.class, new JSONPojoConvertor(JSONPojoConvertorTest.Baz.class));
        // json.addConvertor(Enum.class, new JSONEnumConvertor(true));
        JSONPojoConvertorTest.Foo foo = new JSONPojoConvertorTest.Foo();
        foo._name = "Foo @ " + (TimeUnit.NANOSECONDS.toMillis(System.nanoTime()));
        foo._int1 = 1;
        foo._int2 = new Integer(2);
        foo._long1 = 1000001L;
        foo._long2 = new Long(1000002L);
        foo._float1 = 10.11F;
        foo._float2 = new Float(10.22F);
        foo._double1 = 10000.11111;
        foo._double2 = new Double(10000.22222);
        foo._char1 = 'a';
        foo._char2 = new Character('b');
        JSONPojoConvertorTest.Bar bar = new JSONPojoConvertorTest.Bar("Hello", true, new JSONPojoConvertorTest.Baz("World", Boolean.FALSE, foo), new JSONPojoConvertorTest.Baz[]{ new JSONPojoConvertorTest.Baz("baz0", Boolean.TRUE, null), new JSONPojoConvertorTest.Baz("baz1", Boolean.FALSE, null) });
        bar.setColor(JSONPojoConvertorTest.Color.Green);
        String s = json.toJSON(bar);
        Object obj = json.parse(new JSON.StringSource(s));
        Assertions.assertTrue((obj instanceof JSONPojoConvertorTest.Bar));
        JSONPojoConvertorTest.Bar br = ((JSONPojoConvertorTest.Bar) (obj));
        JSONPojoConvertorTest.Baz bz = br.getBaz();
        JSONPojoConvertorTest.Foo f = bz.getFoo();
        Assertions.assertEquals(foo, f);
        Assertions.assertTrue(((br.getBazs().length) == 2));
        Assertions.assertEquals(br.getBazs()[0].getMessage(), "baz0");
        Assertions.assertEquals(br.getBazs()[1].getMessage(), "baz1");
        Assertions.assertEquals(JSONPojoConvertorTest.Color.Green, br.getColor());
    }

    @Test
    public void testExclude() {
        JSON json = new JSON();
        json.addConvertor(JSONPojoConvertorTest.Foo.class, new JSONPojoConvertor(JSONPojoConvertorTest.Foo.class, new String[]{ "name", "long1", "int2" }));
        json.addConvertor(JSONPojoConvertorTest.Bar.class, new JSONPojoConvertor(JSONPojoConvertorTest.Bar.class, new String[]{ "title", "boolean1" }));
        json.addConvertor(JSONPojoConvertorTest.Baz.class, new JSONPojoConvertor(JSONPojoConvertorTest.Baz.class, new String[]{ "boolean2" }));
        JSONPojoConvertorTest.Foo foo = new JSONPojoConvertorTest.Foo();
        foo._name = "Foo @ " + (TimeUnit.NANOSECONDS.toMillis(System.nanoTime()));
        foo._int1 = 1;
        foo._int2 = new Integer(2);
        foo._long1 = 1000001L;
        foo._long2 = new Long(1000002L);
        foo._float1 = 10.11F;
        foo._float2 = new Float(10.22F);
        foo._double1 = 10000.11111;
        foo._double2 = new Double(10000.22222);
        foo._char1 = 'a';
        foo._char2 = new Character('b');
        JSONPojoConvertorTest.Bar bar = new JSONPojoConvertorTest.Bar("Hello", true, new JSONPojoConvertorTest.Baz("World", Boolean.FALSE, foo));
        // bar.setColor(Color.Blue);
        String s = json.toJSON(bar);
        Object obj = json.parse(new JSON.StringSource(s));
        Assertions.assertTrue((obj instanceof JSONPojoConvertorTest.Bar));
        JSONPojoConvertorTest.Bar br = ((JSONPojoConvertorTest.Bar) (obj));
        JSONPojoConvertorTest.Baz bz = br.getBaz();
        JSONPojoConvertorTest.Foo f = bz.getFoo();
        Assertions.assertNull(br.getTitle());
        Assertions.assertFalse(bar.getTitle().equals(br.getTitle()));
        Assertions.assertFalse(((br.isBoolean1()) == (bar.isBoolean1())));
        Assertions.assertNull(bz.isBoolean2());
        Assertions.assertFalse(bar.getBaz().isBoolean2().equals(bz.isBoolean2()));
        Assertions.assertFalse(((f.getLong1()) == (foo.getLong1())));
        Assertions.assertNull(f.getInt2());
        Assertions.assertFalse(foo.getInt2().equals(f.getInt2()));
        Assertions.assertNull(f.getName());
        Assertions.assertEquals(null, br.getColor());
    }

    enum Color {

        Red,
        Green,
        Blue;}

    public static class Bar {
        private String _title;

        private String _nullTest;

        private JSONPojoConvertorTest.Baz _baz;

        private boolean _boolean1;

        private JSONPojoConvertorTest.Baz[] _bazs;

        private JSONPojoConvertorTest.Color _color;

        public Bar() {
        }

        public Bar(String title, boolean boolean1, JSONPojoConvertorTest.Baz baz) {
            setTitle(title);
            setBoolean1(boolean1);
            setBaz(baz);
        }

        public Bar(String title, boolean boolean1, JSONPojoConvertorTest.Baz baz, JSONPojoConvertorTest.Baz[] bazs) {
            this(title, boolean1, baz);
            setBazs(bazs);
        }

        @Override
        public String toString() {
            return new StringBuffer().append("\n=== ").append(getClass().getSimpleName()).append(" ===").append("\ntitle: ").append(getTitle()).append("\nboolean1: ").append(isBoolean1()).append("\nnullTest: ").append(getNullTest()).append("\nbaz: ").append(getBaz()).append("\ncolor: ").append(_color).toString();
        }

        public void setTitle(String title) {
            _title = title;
        }

        public String getTitle() {
            return _title;
        }

        public void setNullTest(String nullTest) {
            assert nullTest == null;
            _nullTest = nullTest;
        }

        public String getNullTest() {
            return _nullTest;
        }

        public void setBaz(JSONPojoConvertorTest.Baz baz) {
            _baz = baz;
        }

        public JSONPojoConvertorTest.Baz getBaz() {
            return _baz;
        }

        public void setBoolean1(boolean boolean1) {
            _boolean1 = boolean1;
        }

        public boolean isBoolean1() {
            return _boolean1;
        }

        public void setBazs(JSONPojoConvertorTest.Baz[] bazs) {
            _bazs = bazs;
        }

        public JSONPojoConvertorTest.Baz[] getBazs() {
            return _bazs;
        }

        public JSONPojoConvertorTest.Color getColor() {
            return _color;
        }

        public void setColor(JSONPojoConvertorTest.Color color) {
            _color = color;
        }
    }

    public static class Baz {
        private String _message;

        private JSONPojoConvertorTest.Foo _foo;

        private Boolean _boolean2;

        public Baz() {
        }

        public Baz(String message, Boolean boolean2, JSONPojoConvertorTest.Foo foo) {
            setMessage(message);
            setBoolean2(boolean2);
            setFoo(foo);
        }

        @Override
        public String toString() {
            return new StringBuffer().append("\n=== ").append(getClass().getSimpleName()).append(" ===").append("\nmessage: ").append(getMessage()).append("\nboolean2: ").append(isBoolean2()).append("\nfoo: ").append(getFoo()).toString();
        }

        public void setMessage(String message) {
            _message = message;
        }

        public String getMessage() {
            return _message;
        }

        public void setFoo(JSONPojoConvertorTest.Foo foo) {
            _foo = foo;
        }

        public JSONPojoConvertorTest.Foo getFoo() {
            return _foo;
        }

        public void setBoolean2(Boolean boolean2) {
            _boolean2 = boolean2;
        }

        public Boolean isBoolean2() {
            return _boolean2;
        }
    }

    public static class Foo {
        private String _name;

        private int _int1;

        private Integer _int2;

        private long _long1;

        private Long _long2;

        private float _float1;

        private Float _float2;

        private double _double1;

        private Double _double2;

        private char _char1;

        private Character _char2;

        public Foo() {
        }

        @Override
        public String toString() {
            return new StringBuffer().append("\n=== ").append(getClass().getSimpleName()).append(" ===").append("\nname: ").append(_name).append("\nint1: ").append(_int1).append("\nint2: ").append(_int2).append("\nlong1: ").append(_long1).append("\nlong2: ").append(_long2).append("\nfloat1: ").append(_float1).append("\nfloat2: ").append(_float2).append("\ndouble1: ").append(_double1).append("\ndouble2: ").append(_double2).append("\nchar1: ").append(_char1).append("\nchar2: ").append(_char2).toString();
        }

        @Override
        public boolean equals(Object another) {
            if (another instanceof JSONPojoConvertorTest.Foo) {
                JSONPojoConvertorTest.Foo foo = ((JSONPojoConvertorTest.Foo) (another));
                return ((((((((((getName().equals(foo.getName())) && ((getInt1()) == (foo.getInt1()))) && (getInt2().equals(foo.getInt2()))) && ((getLong1()) == (foo.getLong1()))) && (getLong2().equals(foo.getLong2()))) && ((getFloat1()) == (foo.getFloat1()))) && (getFloat2().equals(foo.getFloat2()))) && ((getDouble1()) == (foo.getDouble1()))) && (getDouble2().equals(foo.getDouble2()))) && ((getChar1()) == (foo.getChar1()))) && (getChar2().equals(foo.getChar2()));
            }
            return false;
        }

        public String getName() {
            return _name;
        }

        public void setName(String name) {
            _name = name;
        }

        public int getInt1() {
            return _int1;
        }

        public void setInt1(int int1) {
            _int1 = int1;
        }

        public Integer getInt2() {
            return _int2;
        }

        public void setInt2(Integer int2) {
            _int2 = int2;
        }

        public long getLong1() {
            return _long1;
        }

        public void setLong1(long long1) {
            _long1 = long1;
        }

        public Long getLong2() {
            return _long2;
        }

        public void setLong2(Long long2) {
            _long2 = long2;
        }

        public float getFloat1() {
            return _float1;
        }

        public void setFloat1(float float1) {
            _float1 = float1;
        }

        public Float getFloat2() {
            return _float2;
        }

        public void setFloat2(Float float2) {
            _float2 = float2;
        }

        public double getDouble1() {
            return _double1;
        }

        public void setDouble1(double double1) {
            _double1 = double1;
        }

        public Double getDouble2() {
            return _double2;
        }

        public void setDouble2(Double double2) {
            _double2 = double2;
        }

        public char getChar1() {
            return _char1;
        }

        public void setChar1(char char1) {
            _char1 = char1;
        }

        public Character getChar2() {
            return _char2;
        }

        public void setChar2(Character char2) {
            _char2 = char2;
        }
    }
}

