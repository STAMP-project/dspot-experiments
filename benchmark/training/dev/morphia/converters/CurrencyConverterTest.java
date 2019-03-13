/**
 * Copyright 2017 MongoDB, Inc.
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
import java.util.Currency;
import java.util.Locale;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


public class CurrencyConverterTest extends ConverterTest<Currency, String> {
    private final CurrencyConverter converter = new CurrencyConverter();

    public CurrencyConverterTest() {
        super(new CurrencyConverter());
    }

    @Test
    public void convertNull() {
        Assert.assertNull(getConverter().decode(null, null));
        Assert.assertNull(getConverter().encode(null));
    }

    @Test
    public void decodes() {
        Assert.assertEquals(Currency.getInstance("USD"), converter.decode(Currency.class, "USD"));
        Assert.assertEquals(Currency.getInstance(Locale.CHINA), converter.decode(Currency.class, Locale.CHINA));
    }

    @Test
    public void testConversion() {
        compare(Currency.class, Currency.getInstance("USD"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadCurrencyCode() {
        converter.decode(Currency.class, "BLAH");
    }

    @Test
    public void testEntity() {
        final CurrencyConverterTest.Foo foo = new CurrencyConverterTest.Foo();
        foo.setCurrency(Currency.getInstance("USD"));
        getDs().save(foo);
        Assert.assertEquals(foo, getDs().find(CurrencyConverterTest.Foo.class).find(new FindOptions().limit(1)).next());
    }

    @Entity
    private static class Foo {
        @Id
        private ObjectId id;

        private Currency currency;

        public ObjectId getId() {
            return id;
        }

        public void setId(final ObjectId id) {
            this.id = id;
        }

        public Currency getCurrency() {
            return currency;
        }

        public void setCurrency(final Currency currency) {
            this.currency = currency;
        }

        @Override
        public boolean equals(final Object o) {
            if ((this) == o) {
                return true;
            }
            if (!(o instanceof CurrencyConverterTest.Foo)) {
                return false;
            }
            final CurrencyConverterTest.Foo foo = ((CurrencyConverterTest.Foo) (o));
            if ((getId()) != null ? !(getId().equals(foo.getId())) : (foo.getId()) != null) {
                return false;
            }
            return (getCurrency()) != null ? getCurrency().equals(foo.getCurrency()) : (foo.getCurrency()) == null;
        }

        @Override
        public int hashCode() {
            int result = ((getId()) != null) ? getId().hashCode() : 0;
            result = (31 * result) + ((getCurrency()) != null ? getCurrency().hashCode() : 0);
            return result;
        }
    }
}

