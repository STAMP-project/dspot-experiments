

package com.twilio.security;


/**
 * Test class for {@link RequestValidator}.
 */
public class AmplRequestValidatorTest {
    @org.junit.Test
    public void testValidate() {
        com.twilio.security.RequestValidator validator = new com.twilio.security.RequestValidator("12345");
        java.lang.String url = "https://mycompany.com/myapp.php?foo=1&bar=2";
        java.util.Map<java.lang.String, java.lang.String> params = new java.util.HashMap<>();
        params.put("CallSid", "CA1234567890ABCDE");
        params.put("Caller", "+14158675309");
        params.put("Digits", "1234");
        params.put("From", "+14158675309");
        params.put("To", "+18005551212");
        java.lang.String signature = "RSOYDt4T1cUTdK1PDd93/VVr8B8=";
        org.junit.Assert.assertTrue("Request does not match provided signature", validator.validate(url, params, signature));
    }
}

