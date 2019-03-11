/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.validation;


import org.geoserver.security.config.PasswordPolicyConfig;
import org.geoserver.test.GeoServerMockTestSupport;
import org.junit.Test;


public class PasswordValidatorTest extends GeoServerMockTestSupport {
    PasswordPolicyConfig config;

    PasswordValidatorImpl validator;

    @Test
    public void testPasswords() throws PasswordPolicyException.PasswordPolicyException {
        checkForException(null, IS_NULL);
        validator.validatePassword("".toCharArray());
        validator.validatePassword("a".toCharArray());
        checkForException("plain:a", RESERVED_PREFIX_$1, "plain:");
        checkForException("crypt1:a", RESERVED_PREFIX_$1, "crypt1:");
        checkForException("digest1:a", RESERVED_PREFIX_$1, "digest1:");
        validator.validatePassword("plain".toCharArray());
        validator.validatePassword("plaina".toCharArray());
        config.setMinLength(2);
        checkForException("a", MIN_LENGTH_$1, 2);
        validator.validatePassword("aa".toCharArray());
        config.setMaxLength(10);
        checkForException("01234567890", MAX_LENGTH_$1, 10);
        validator.validatePassword("0123456789".toCharArray());
        config.setDigitRequired(true);
        checkForException("abcdef", NO_DIGIT);
        validator.validatePassword("abcde4".toCharArray());
        config.setUppercaseRequired(true);
        checkForException("abcdef4", NO_UPPERCASE);
        validator.validatePassword("abcde4F".toCharArray());
        config.setLowercaseRequired(true);
        checkForException("ABCDE4F", NO_LOWERCASE);
        validator.validatePassword("abcde4F".toCharArray());
    }
}

