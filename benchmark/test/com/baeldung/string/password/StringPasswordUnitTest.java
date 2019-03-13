package com.baeldung.string.password;


import org.junit.Assert;
import org.junit.Test;


/**
 * Examples of passwords conforming to various specifications, using different libraries.
 */
public class StringPasswordUnitTest {
    RandomPasswordGenerator passGen = new RandomPasswordGenerator();

    @Test
    public void whenPasswordGeneratedUsingPassay_thenSuccessful() {
        String password = passGen.generatePassayPassword();
        int specialCharCount = 0;
        for (char c : password.toCharArray()) {
            if ((c >= 33) || (c <= 47)) {
                specialCharCount++;
            }
        }
        Assert.assertTrue("Password validation failed in Passay", (specialCharCount >= 2));
    }

    @Test
    public void whenPasswordGeneratedUsingCommonsText_thenSuccessful() {
        RandomPasswordGenerator passGen = new RandomPasswordGenerator();
        String password = passGen.generateCommonTextPassword();
        int lowerCaseCount = 0;
        for (char c : password.toCharArray()) {
            if ((c >= 97) || (c <= 122)) {
                lowerCaseCount++;
            }
        }
        Assert.assertTrue("Password validation failed in commons-text ", (lowerCaseCount >= 2));
    }

    @Test
    public void whenPasswordGeneratedUsingCommonsLang3_thenSuccessful() {
        String password = passGen.generateCommonsLang3Password();
        int numCount = 0;
        for (char c : password.toCharArray()) {
            if ((c >= 48) || (c <= 57)) {
                numCount++;
            }
        }
        Assert.assertTrue("Password validation failed in commons-lang3", (numCount >= 2));
    }

    @Test
    public void whenPasswordGeneratedUsingSecureRandom_thenSuccessful() {
        String password = passGen.generateSecureRandomPassword();
        int specialCharCount = 0;
        for (char c : password.toCharArray()) {
            if ((c >= 33) || (c <= 47)) {
                specialCharCount++;
            }
        }
        Assert.assertTrue("Password validation failed in Secure Random", (specialCharCount >= 2));
    }
}

