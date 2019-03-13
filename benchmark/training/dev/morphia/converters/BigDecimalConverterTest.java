/**
 * Copyright 2016 MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.morphia.converters;


import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.query.FindOptions;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


public class BigDecimalConverterTest extends ConverterTest<BigDecimal, Decimal128> {
    private BigDecimalConverter converter = new BigDecimalConverter();

    public BigDecimalConverterTest() {
        super(new BigDecimalConverter());
    }

    @Test
    public void convertNull() {
        Assert.assertNull(getConverter().decode(null, null));
        Assert.assertNull(getConverter().encode(null));
    }

    @Test
    public void decodes() {
        compare(BigDecimal.class, new BigDecimal("12345678901234567890"));
        Assert.assertEquals(new BigDecimal(42L), converter.decode(BigDecimal.class, new BigDecimal(42L)));
        Assert.assertEquals(new BigDecimal("12345678901234567890"), converter.decode(BigDecimal.class, new BigInteger("12345678901234567890")));
        Assert.assertEquals(new BigDecimal(42L), converter.decode(BigDecimal.class, 42L));
        Assert.assertEquals(new BigDecimal(Long.valueOf(42)), converter.decode(BigDecimal.class, 42L));
        Assert.assertEquals(new BigDecimal(42.0), converter.decode(BigDecimal.class, 42.0));
        Assert.assertEquals(new BigDecimal(Double.valueOf(42)), converter.decode(BigDecimal.class, 42.0));
        Assert.assertEquals(new BigDecimal("12345678901234567890"), converter.decode(BigDecimal.class, "12345678901234567890"));
        Assert.assertEquals(new BigDecimal("1.2345678901234567890"), converter.decode(BigDecimal.class, "1.2345678901234567890"));
        Assert.assertEquals(new BigDecimal(Double.MAX_VALUE), converter.decode(BigDecimal.class, Double.MAX_VALUE));
        Assert.assertEquals(new BigDecimal(Double.MIN_VALUE), converter.decode(BigDecimal.class, Double.MIN_VALUE));
        Assert.assertEquals(new BigDecimal(Long.MAX_VALUE), converter.decode(BigDecimal.class, Long.MAX_VALUE));
        Assert.assertEquals(new BigDecimal(Long.MIN_VALUE), converter.decode(BigDecimal.class, Long.MIN_VALUE));
        Assert.assertEquals(new BigDecimal(0), converter.decode(BigDecimal.class, 0));
    }

    @Test
    public void testConversion() {
        compare(BigDecimal.class, new BigDecimal("12345678901234567890"));
        compare(BigDecimal.class, new BigDecimal(42L));
        compare(BigDecimal.class, new BigDecimal(Long.valueOf(42)));
        compare(BigDecimal.class, new BigDecimal(42.0));
        compare(BigDecimal.class, new BigDecimal(Double.valueOf(42)));
        compare(BigDecimal.class, new BigDecimal("12345678901234567890"));
        compare(BigDecimal.class, new BigDecimal("1.2345678901234567890"));
        compare(BigDecimal.class, new BigDecimal("0"));
        compare(BigDecimal.class, new BigDecimal("-0"));
        compare(BigDecimal.class, new BigDecimal(Long.MAX_VALUE));
        compare(BigDecimal.class, new BigDecimal(Long.MIN_VALUE));
        compare(BigDecimal.class, new BigDecimal(0));
        compare(BigDecimal.class, new BigDecimal((-0)));
    }

    @Test
    public void testEntity() {
        BigDecimalConverterTest.Foo foo = new BigDecimalConverterTest.Foo();
        foo.setNumber(new BigDecimal("0.92348237942346239"));
        getDs().save(foo);
        Assert.assertEquals(foo, getDs().find(BigDecimalConverterTest.Foo.class).find(new FindOptions().limit(1)).next());
    }

    @Entity
    private static class Foo {
        @Id
        private ObjectId id;

        private BigDecimal number;

        public ObjectId getId() {
            return id;
        }

        public void setId(final ObjectId id) {
            this.id = id;
        }

        public BigDecimal getNumber() {
            return number;
        }

        public void setNumber(final BigDecimal number) {
            this.number = number;
        }

        @Override
        public boolean equals(final Object o) {
            if ((this) == o) {
                return true;
            }
            if (!(o instanceof BigDecimalConverterTest.Foo)) {
                return false;
            }
            final BigDecimalConverterTest.Foo foo = ((BigDecimalConverterTest.Foo) (o));
            if ((getId()) != null ? !(getId().equals(foo.getId())) : (foo.getId()) != null) {
                return false;
            }
            return (getNumber()) != null ? getNumber().equals(foo.getNumber()) : (foo.getNumber()) == null;
        }

        @Override
        public int hashCode() {
            int result = ((getId()) != null) ? getId().hashCode() : 0;
            result = (31 * result) + ((getNumber()) != null ? getNumber().hashCode() : 0);
            return result;
        }
    }
}

