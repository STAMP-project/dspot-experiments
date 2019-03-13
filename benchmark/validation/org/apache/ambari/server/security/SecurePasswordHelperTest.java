/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.security;


import SecurePasswordHelper.DEFAULT_SECURE_PASSWORD_LENGTH;
import SecurePasswordHelper.DEFAULT_SECURE_PASSWORD_MIN_DIGITS;
import SecurePasswordHelper.DEFAULT_SECURE_PASSWORD_MIN_LOWERCASE_LETTERS;
import SecurePasswordHelper.DEFAULT_SECURE_PASSWORD_MIN_PUNCTUATION;
import SecurePasswordHelper.DEFAULT_SECURE_PASSWORD_MIN_UPPERCASE_LETTERS;
import SecurePasswordHelper.DEFAULT_SECURE_PASSWORD_MIN_WHITESPACE;
import junit.framework.Assert;
import org.junit.Test;


public class SecurePasswordHelperTest {
    private SecurePasswordHelper securePasswordHelper;

    @Test
    public void testCreateSecurePassword() throws Exception {
        String password1 = securePasswordHelper.createSecurePassword();
        Assert.assertNotNull(password1);
        Assert.assertEquals(DEFAULT_SECURE_PASSWORD_LENGTH, password1.length());
        String password2 = securePasswordHelper.createSecurePassword();
        Assert.assertNotNull(password2);
        Assert.assertEquals(DEFAULT_SECURE_PASSWORD_LENGTH, password2.length());
        // Make sure the passwords are different... if they are the same, that indicated the random
        // number generators are generating using the same pattern and that is not secure.
        Assert.assertFalse(password1.equals(password2));
    }

    @Test
    public void testCreateSecurePasswordWithRules() throws Exception {
        String password;
        // Default rules....
        password = securePasswordHelper.createSecurePassword(null, null, null, null, null, null);
        Assert.assertNotNull(password);
        Assert.assertEquals(DEFAULT_SECURE_PASSWORD_LENGTH, password.length());
        assertMinLowercaseLetters(DEFAULT_SECURE_PASSWORD_MIN_LOWERCASE_LETTERS, password);
        assertMinUppercaseLetters(DEFAULT_SECURE_PASSWORD_MIN_UPPERCASE_LETTERS, password);
        assertMinDigits(DEFAULT_SECURE_PASSWORD_MIN_DIGITS, password);
        assertMinPunctuation(DEFAULT_SECURE_PASSWORD_MIN_PUNCTUATION, password);
        assertMinWhitespace(DEFAULT_SECURE_PASSWORD_MIN_WHITESPACE, password);
        password = securePasswordHelper.createSecurePassword(10, null, null, null, null, null);
        Assert.assertNotNull(password);
        Assert.assertEquals(10, password.length());
        assertMinLowercaseLetters(DEFAULT_SECURE_PASSWORD_MIN_LOWERCASE_LETTERS, password);
        assertMinUppercaseLetters(DEFAULT_SECURE_PASSWORD_MIN_UPPERCASE_LETTERS, password);
        assertMinDigits(DEFAULT_SECURE_PASSWORD_MIN_DIGITS, password);
        assertMinPunctuation(DEFAULT_SECURE_PASSWORD_MIN_PUNCTUATION, password);
        assertMinWhitespace(DEFAULT_SECURE_PASSWORD_MIN_WHITESPACE, password);
        password = securePasswordHelper.createSecurePassword(0, null, null, null, null, null);
        Assert.assertNotNull(password);
        Assert.assertEquals(DEFAULT_SECURE_PASSWORD_LENGTH, password.length());
        assertMinLowercaseLetters(DEFAULT_SECURE_PASSWORD_MIN_LOWERCASE_LETTERS, password);
        assertMinUppercaseLetters(DEFAULT_SECURE_PASSWORD_MIN_UPPERCASE_LETTERS, password);
        assertMinDigits(DEFAULT_SECURE_PASSWORD_MIN_DIGITS, password);
        assertMinPunctuation(DEFAULT_SECURE_PASSWORD_MIN_PUNCTUATION, password);
        assertMinWhitespace(DEFAULT_SECURE_PASSWORD_MIN_WHITESPACE, password);
        password = securePasswordHelper.createSecurePassword((-20), null, null, null, null, null);
        Assert.assertNotNull(password);
        Assert.assertEquals(DEFAULT_SECURE_PASSWORD_LENGTH, password.length());
        assertMinLowercaseLetters(DEFAULT_SECURE_PASSWORD_MIN_LOWERCASE_LETTERS, password);
        assertMinUppercaseLetters(DEFAULT_SECURE_PASSWORD_MIN_UPPERCASE_LETTERS, password);
        assertMinDigits(DEFAULT_SECURE_PASSWORD_MIN_DIGITS, password);
        assertMinPunctuation(DEFAULT_SECURE_PASSWORD_MIN_PUNCTUATION, password);
        assertMinWhitespace(DEFAULT_SECURE_PASSWORD_MIN_WHITESPACE, password);
        password = securePasswordHelper.createSecurePassword(100, 30, 20, 10, 5, 2);
        Assert.assertNotNull(password);
        Assert.assertEquals(100, password.length());
        assertMinLowercaseLetters(30, password);
        assertMinUppercaseLetters(20, password);
        assertMinDigits(10, password);
        assertMinPunctuation(5, password);
        assertMinWhitespace(2, password);
        password = securePasswordHelper.createSecurePassword(100, 20, 20, 20, 20, 0);
        Assert.assertNotNull(password);
        Assert.assertEquals(100, password.length());
        assertMinLowercaseLetters(20, password);
        assertMinUppercaseLetters(20, password);
        assertMinDigits(20, password);
        assertMinPunctuation(20, password);
        assertMinWhitespace(0, password);
    }
}

