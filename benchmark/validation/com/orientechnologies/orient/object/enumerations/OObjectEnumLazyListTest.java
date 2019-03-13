package com.orientechnologies.orient.object.enumerations;


import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author JN <a href="mailto:jn@brain-activit.com">Julian Neuhaus</a>
 * @since 15.08.2014
 */
public class OObjectEnumLazyListTest {
    private OObjectDatabaseTx databaseTx;

    @Test
    public void containsTest() {
        OObjectEnumLazyListTest.EntityWithEnumList hasListWithEnums = getTestObject();
        Assert.assertTrue(hasListWithEnums.getEnumList().contains(OObjectEnumLazyListTest.TESTENUM.TEST_VALUE_1));
        Assert.assertTrue(hasListWithEnums.getEnumList().contains(OObjectEnumLazyListTest.TESTENUM.TEST_VALUE_2));
        Assert.assertTrue(hasListWithEnums.getEnumList().contains(OObjectEnumLazyListTest.TESTENUM.TEST_VALUE_3));
        Assert.assertFalse(hasListWithEnums.getEnumList().contains(OObjectEnumLazyListTest.WRONG_TESTENUM.TEST_VALUE_1));
        Assert.assertFalse(hasListWithEnums.getEnumList().contains(OObjectEnumLazyListTest.WRONG_TESTENUM.TEST_VALUE_2));
        Assert.assertFalse(hasListWithEnums.getEnumList().contains(OObjectEnumLazyListTest.WRONG_TESTENUM.TEST_VALUE_3));
        Assert.assertFalse(hasListWithEnums.getEnumList().contains(null));
        Assert.assertFalse(hasListWithEnums.getEnumList().contains("INVALID TYPE"));
    }

    @Test
    public void indexOfTest() {
        OObjectEnumLazyListTest.EntityWithEnumList hasListWithEnums = getTestObject();
        Assert.assertTrue(((hasListWithEnums.getEnumList().indexOf(OObjectEnumLazyListTest.TESTENUM.TEST_VALUE_1)) == 0));
        Assert.assertTrue(((hasListWithEnums.getEnumList().indexOf(OObjectEnumLazyListTest.TESTENUM.TEST_VALUE_2)) == 1));
        Assert.assertTrue(((hasListWithEnums.getEnumList().indexOf(OObjectEnumLazyListTest.TESTENUM.TEST_VALUE_3)) == 2));
        Assert.assertTrue(((hasListWithEnums.getEnumList().indexOf(OObjectEnumLazyListTest.WRONG_TESTENUM.TEST_VALUE_1)) == (-1)));
        Assert.assertTrue(((hasListWithEnums.getEnumList().indexOf(OObjectEnumLazyListTest.WRONG_TESTENUM.TEST_VALUE_2)) == (-1)));
        Assert.assertTrue(((hasListWithEnums.getEnumList().indexOf(OObjectEnumLazyListTest.WRONG_TESTENUM.TEST_VALUE_3)) == (-1)));
        Assert.assertTrue(((hasListWithEnums.getEnumList().indexOf(null)) == (-1)));
        Assert.assertTrue(((hasListWithEnums.getEnumList().indexOf("INVALID TYPE")) == (-1)));
    }

    @Test
    public void lastIndexOfTest() {
        OObjectEnumLazyListTest.EntityWithEnumList hasListWithEnums = getTestObject();
        Assert.assertTrue(((hasListWithEnums.getEnumList().lastIndexOf(OObjectEnumLazyListTest.TESTENUM.TEST_VALUE_1)) == 3));
        Assert.assertTrue(((hasListWithEnums.getEnumList().lastIndexOf(OObjectEnumLazyListTest.TESTENUM.TEST_VALUE_2)) == 4));
        Assert.assertTrue(((hasListWithEnums.getEnumList().lastIndexOf(OObjectEnumLazyListTest.TESTENUM.TEST_VALUE_3)) == 5));
        Assert.assertTrue(((hasListWithEnums.getEnumList().lastIndexOf(OObjectEnumLazyListTest.WRONG_TESTENUM.TEST_VALUE_1)) == (-1)));
        Assert.assertTrue(((hasListWithEnums.getEnumList().lastIndexOf(OObjectEnumLazyListTest.WRONG_TESTENUM.TEST_VALUE_2)) == (-1)));
        Assert.assertTrue(((hasListWithEnums.getEnumList().lastIndexOf(OObjectEnumLazyListTest.WRONG_TESTENUM.TEST_VALUE_3)) == (-1)));
        Assert.assertTrue(((hasListWithEnums.getEnumList().lastIndexOf(null)) == (-1)));
        Assert.assertTrue(((hasListWithEnums.getEnumList().lastIndexOf("INVALID TYPE")) == (-1)));
    }

    public enum TESTENUM {

        TEST_VALUE_1,
        TEST_VALUE_2,
        TEST_VALUE_3;}

    public enum WRONG_TESTENUM {

        TEST_VALUE_1,
        TEST_VALUE_2,
        TEST_VALUE_3;}

    public class EntityWithEnumList {
        private List<OObjectEnumLazyListTest.TESTENUM> enumList;

        public EntityWithEnumList() {
            super();
        }

        public List<OObjectEnumLazyListTest.TESTENUM> getEnumList() {
            return enumList;
        }

        public void setEnumList(List<OObjectEnumLazyListTest.TESTENUM> enumList) {
            this.enumList = enumList;
        }
    }
}

