package cn.hutool.core.lang;


import cn.hutool.core.exceptions.ValidateException;
import org.junit.Assert;
import org.junit.Test;


/**
 * ???????
 *
 * @author Looly
 */
public class ValidatorTest {
    @Test
    public void isNumberTest() {
        Assert.assertTrue(Validator.isNumber("45345365465"));
        Assert.assertTrue(Validator.isNumber("0004545435"));
        Assert.assertTrue(Validator.isNumber("5.222"));
        Assert.assertTrue(Validator.isNumber("0.33323"));
    }

    @Test
    public void isLetterTest() {
        Assert.assertTrue(Validator.isLetter("asfdsdsfds"));
        Assert.assertTrue(Validator.isLetter("asfdsdfdsfVCDFDFGdsfds"));
        Assert.assertTrue(Validator.isLetter("asfdsdf??dsfVCDFDFGdsfds"));
    }

    @Test
    public void isUperCaseTest() {
        Assert.assertTrue(Validator.isUpperCase("VCDFDFG"));
        Assert.assertTrue(Validator.isUpperCase("ASSFD"));
        Assert.assertFalse(Validator.isUpperCase("asfdsdsfds"));
        Assert.assertFalse(Validator.isUpperCase("ASSFD??"));
    }

    @Test
    public void isLowerCaseTest() {
        Assert.assertTrue(Validator.isLowerCase("asfdsdsfds"));
        Assert.assertFalse(Validator.isLowerCase("aaaa??"));
        Assert.assertFalse(Validator.isLowerCase("VCDFDFG"));
        Assert.assertFalse(Validator.isLowerCase("ASSFD"));
        Assert.assertFalse(Validator.isLowerCase("ASSFD??"));
    }

    @Test
    public void isBirthdayTest() {
        boolean b = Validator.isBirthday("20150101");
        Assert.assertTrue(b);
        boolean b2 = Validator.isBirthday("2015-01-01");
        Assert.assertTrue(b2);
        boolean b3 = Validator.isBirthday("2015.01.01");
        Assert.assertTrue(b3);
        boolean b4 = Validator.isBirthday("2015?01?01?");
        Assert.assertTrue(b4);
        boolean b5 = Validator.isBirthday("2015.01.01");
        Assert.assertTrue(b5);
        boolean b6 = Validator.isBirthday("2018-08-15");
        Assert.assertTrue(b6);
        // ?????
        Assert.assertFalse(Validator.isBirthday("2095.05.01"));
        // ?????
        Assert.assertFalse(Validator.isBirthday("2015.13.01"));
        // ?????
        Assert.assertFalse(Validator.isBirthday("2015.02.29"));
    }

    @Test
    public void isCitizenIdTest() {
        boolean b = Validator.isCitizenId("150218199012123389");
        Assert.assertTrue(b);
    }

    @Test(expected = ValidateException.class)
    public void validateTest() throws ValidateException {
        Validator.validateChinese("????zhongwen", "????????");
    }

    @Test
    public void isEmailTest() {
        boolean email = Validator.isEmail("abc_cde@163.com");
        Assert.assertTrue(email);
        boolean email1 = Validator.isEmail("abc_%cde@163.com");
        Assert.assertTrue(email1);
        boolean email2 = Validator.isEmail("abc_%cde@aaa.c");
        Assert.assertTrue(email2);
        boolean email3 = Validator.isEmail("xiaolei.lu@aaa.b");
        Assert.assertTrue(email3);
        boolean email4 = Validator.isEmail("xiaolei.Lu@aaa.b");
        Assert.assertTrue(email4);
    }

    @Test
    public void isMobileTest() {
        boolean m1 = Validator.isMobile("13900221432");
        Assert.assertTrue(m1);
        boolean m2 = Validator.isMobile("015100221432");
        Assert.assertTrue(m2);
        boolean m3 = Validator.isMobile("+8618600221432");
        Assert.assertTrue(m3);
    }
}

