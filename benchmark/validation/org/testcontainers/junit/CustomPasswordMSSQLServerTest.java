package org.testcontainers.junit;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.testcontainers.containers.MSSQLServerContainer;


/**
 * Tests if the password passed to the container satisfied the password policy described at
 * https://docs.microsoft.com/en-us/sql/relational-databases/security/password-policy?view=sql-server-2017
 *
 * @author Enrico Costanzi
 */
@RunWith(Parameterized.class)
public class CustomPasswordMSSQLServerTest {
    private static String UPPER_CASE_LETTERS = "ABCDE";

    private static String LOWER_CASE_LETTERS = "abcde";

    private static String NUMBERS = "12345";

    private static String SPECIAL_CHARS = "_(!)_";

    private String password;

    private Boolean valid;

    public CustomPasswordMSSQLServerTest(String password, Boolean valid) {
        this.password = password;
        this.valid = valid;
    }

    @Test
    public void runPasswordTests() {
        try {
            new MSSQLServerContainer().withPassword(this.password);
            if (!(valid))
                fail((("Password " + (this.password)) + " is not valid. Expected exception"));

        } catch (IllegalArgumentException e) {
            if (valid)
                fail((("Password " + (this.password)) + " should have been validated"));

        }
    }
}

