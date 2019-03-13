package com.orm.helper;


import com.orm.app.ClientApp;
import com.orm.dsl.BuildConfig;
import com.orm.model.TestRecord;
import com.orm.util.ReflectionUtil;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricGradleTestRunner.class)
@Config(sdk = 18, constants = BuildConfig.class, application = ClientApp.class, packageName = "com.orm.model", manifest = Config.NONE)
public final class NamingHelperTest {
    @Test(expected = IllegalAccessException.class)
    public void testPrivateConstructor() throws Exception {
        NamingHelper.NamingHelper helper = NamingHelper.NamingHelper.class.getDeclaredConstructor().newInstance();
        Assert.assertNull(helper);
    }

    @Test
    public void testToSQLNameFromField() {
        List<Field> fieldList = ReflectionUtil.getTableFields(TestRecord.class);
        if ((null != fieldList) && (!(fieldList.isEmpty()))) {
            List<String> columnList = new ArrayList<>();
            for (Field field : fieldList) {
                columnList.add(toColumnName(field));
            }
            boolean isIdInList = inList(columnList, "ID");
            boolean isNameInList = inList(columnList, "NAME");
            Assert.assertTrue(isIdInList);
            Assert.assertTrue(isNameInList);
        }
    }

    @Test
    public void testToSQLNameFromClass() {
        Assert.assertEquals("TEST_RECORD", toTableName(TestRecord.class));
    }

    @Test
    public void testToSQLNameCaseConversion() throws Exception {
        NamingHelperTest.assertToSqlNameEquals("TESTLOWERCASE", "testlowercase");
        NamingHelperTest.assertToSqlNameEquals("TESTUPPERCASE", "TESTUPPERCASE");
    }

    @Test
    public void testToSQLNameUnderscore() {
        NamingHelperTest.assertToSqlNameEquals("TEST_UNDERSCORE", "testUnderscore");
        NamingHelperTest.assertToSqlNameEquals("AB_CD", "AbCd");
        NamingHelperTest.assertToSqlNameEquals("AB_CD", "ABCd");
        NamingHelperTest.assertToSqlNameEquals("AB_CD", "AbCD");
        NamingHelperTest.assertToSqlNameEquals("SOME_DETAILS_OBJECT", "SomeDetailsObject");
        NamingHelperTest.assertToSqlNameEquals("H_OL_A", "hOlA");
        NamingHelperTest.assertToSqlNameEquals("A", "a");
    }
}

