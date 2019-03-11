/**
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.connector.mysql;


import java.math.BigDecimal;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Omar Al-Safi
 */
public class MySqlUnsignedIntegerConverterTest {
    @Test
    public void shouldConvertSignedBinlogTinyintToUnsigned() {
        Assert.assertEquals(((short) (255)), MySqlUnsignedIntegerConverter.convertUnsignedTinyint(((short) (-1))));
        Assert.assertEquals(((short) (255)), MySqlUnsignedIntegerConverter.convertUnsignedTinyint(((short) (255))));
    }

    @Test
    public void shouldConvertSignedBinlogSmallintToUnsigned() {
        Assert.assertEquals(65535, MySqlUnsignedIntegerConverter.convertUnsignedSmallint((-1)));
        Assert.assertEquals(65535, MySqlUnsignedIntegerConverter.convertUnsignedSmallint(65535));
    }

    @Test
    public void shouldConvertSignedBinlogMediumintToUnsigned() {
        Assert.assertEquals(16777215, MySqlUnsignedIntegerConverter.convertUnsignedMediumint((-1)));
        Assert.assertEquals(16777215, MySqlUnsignedIntegerConverter.convertUnsignedMediumint(16777215));
    }

    @Test
    public void shouldConvertSignedBinlogIntToUnsigned() {
        Assert.assertEquals(4294967295L, MySqlUnsignedIntegerConverter.convertUnsignedInteger((-1L)));
        Assert.assertEquals(4294967295L, MySqlUnsignedIntegerConverter.convertUnsignedInteger(4294967295L));
    }

    @Test
    public void shouldConvertSignedBinlogBigintToUnsigned() {
        Assert.assertEquals(new BigDecimal("18446744073709551615"), MySqlUnsignedIntegerConverter.convertUnsignedBigint(new BigDecimal("-1")));
        Assert.assertEquals(new BigDecimal("18446744073709551615"), MySqlUnsignedIntegerConverter.convertUnsignedBigint(new BigDecimal("18446744073709551615")));
    }
}

