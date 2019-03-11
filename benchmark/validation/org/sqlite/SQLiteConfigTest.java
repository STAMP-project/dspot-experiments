package org.sqlite;


import SQLiteConfig.DateClass.REAL;
import SQLiteConfig.DatePrecision.SECONDS;
import SQLiteConfig.Pragma.DATE_CLASS;
import SQLiteConfig.Pragma.DATE_PRECISION;
import SQLiteConfig.Pragma.DATE_STRING_FORMAT;
import java.sql.SQLException;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;


public class SQLiteConfigTest {
    @Test
    public void toProperites() throws SQLException {
        SQLiteConfig config = new SQLiteConfig();
        config.setReadOnly(true);
        config.setDateStringFormat("yyyy/mm/dd");
        config.setDatePrecision("seconds");
        config.setDateClass("real");
        Properties properties = config.toProperties();
        Assert.assertEquals("yyyy/mm/dd", properties.getProperty(DATE_STRING_FORMAT.getPragmaName()));
        Assert.assertEquals(SECONDS.name(), properties.getProperty(DATE_PRECISION.getPragmaName()));
        Assert.assertEquals(REAL.name(), properties.getProperty(DATE_CLASS.getPragmaName()));
    }
}

