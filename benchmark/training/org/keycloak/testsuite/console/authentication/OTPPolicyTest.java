/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.keycloak.testsuite.console.authentication;


import Digits.EIGHT;
import Digits.SIX;
import OTPHashAlg.SHA1;
import OTPHashAlg.SHA256;
import OTPHashAlg.SHA512;
import OTPType.COUNTER_BASED;
import OTPType.TIME_BASED;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.testsuite.console.AbstractConsoleTest;
import org.keycloak.testsuite.console.page.authentication.otppolicy.OTPPolicy;


/**
 *
 *
 * @author <a href="mailto:vramik@redhat.com">Vlastislav Ramik</a>
 */
public class OTPPolicyTest extends AbstractConsoleTest {
    @Page
    private OTPPolicy otpPolicyPage;

    @Test
    public void otpPolicyTest() {
        otpPolicyPage.form().setValues(COUNTER_BASED, SHA256, EIGHT, "10", "50");
        assertAlertSuccess();
        RealmRepresentation realm = testRealmResource().toRepresentation();
        Assert.assertEquals("hotp", realm.getOtpPolicyType());
        Assert.assertEquals("HmacSHA256", realm.getOtpPolicyAlgorithm());
        Assert.assertEquals(Integer.valueOf(8), realm.getOtpPolicyDigits());
        Assert.assertEquals(Integer.valueOf(10), realm.getOtpPolicyLookAheadWindow());
        Assert.assertEquals(Integer.valueOf(50), realm.getOtpPolicyInitialCounter());
        otpPolicyPage.form().setValues(TIME_BASED, SHA512, EIGHT, "10", "40");
        assertAlertSuccess();
        realm = testRealmResource().toRepresentation();
        Assert.assertEquals(Integer.valueOf(40), realm.getOtpPolicyPeriod());
    }

    @Test
    public void invalidValuesTest() {
        otpPolicyPage.form().setValues(TIME_BASED, SHA1, SIX, "", "30");
        assertAlertDanger();
        otpPolicyPage.navigateTo();// workaround: input.clear() doesn't work when <input type="number" ...

        otpPolicyPage.form().setValues(TIME_BASED, SHA1, SIX, " ", "30");
        assertAlertDanger();
        otpPolicyPage.navigateTo();
        otpPolicyPage.form().setValues(TIME_BASED, SHA1, SIX, "no number", "30");
        assertAlertDanger();
        otpPolicyPage.navigateTo();
        RealmRepresentation realm = testRealmResource().toRepresentation();
        Assert.assertEquals(Integer.valueOf(1), realm.getOtpPolicyLookAheadWindow());
        otpPolicyPage.form().setValues(TIME_BASED, SHA1, SIX, "1", "");
        assertAlertDanger();
        otpPolicyPage.navigateTo();
        otpPolicyPage.form().setValues(TIME_BASED, SHA1, SIX, "1", " ");
        assertAlertDanger();
        otpPolicyPage.navigateTo();
        otpPolicyPage.form().setValues(TIME_BASED, SHA1, SIX, "1", "no number");
        assertAlertDanger();
        otpPolicyPage.navigateTo();
        realm = testRealmResource().toRepresentation();
        Assert.assertEquals(Integer.valueOf(30), realm.getOtpPolicyPeriod());
        otpPolicyPage.form().setValues(COUNTER_BASED, SHA1, SIX, "1", "");
        assertAlertDanger();
        otpPolicyPage.navigateTo();
        otpPolicyPage.form().setValues(COUNTER_BASED, SHA1, SIX, "1", " ");
        assertAlertDanger();
        otpPolicyPage.navigateTo();
        otpPolicyPage.form().setValues(COUNTER_BASED, SHA1, SIX, "1", "no number");
        assertAlertDanger();
        otpPolicyPage.navigateTo();
        realm = testRealmResource().toRepresentation();
        Assert.assertEquals(Integer.valueOf(0), realm.getOtpPolicyInitialCounter());
    }
}

