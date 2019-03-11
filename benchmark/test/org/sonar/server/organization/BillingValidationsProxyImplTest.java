/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.organization;


import org.junit.Test;
import org.mockito.Mockito;


public class BillingValidationsProxyImplTest {
    private static final BillingValidations.Organization ORGANIZATION = new BillingValidations.Organization("ORGANIZATION_KEY", "ORGANIZATION_UUID", "ORGANIZATION_NAME");

    private BillingValidationsExtension billingValidationsExtension = Mockito.mock(BillingValidationsExtension.class);

    private BillingValidationsProxyImpl underTest;

    @Test
    public void checkOnProjectAnalysis_calls_extension_when_available() {
        underTest = new BillingValidationsProxyImpl(billingValidationsExtension);
        underTest.checkBeforeProjectAnalysis(BillingValidationsProxyImplTest.ORGANIZATION);
        Mockito.verify(billingValidationsExtension).checkBeforeProjectAnalysis(BillingValidationsProxyImplTest.ORGANIZATION);
    }

    @Test
    public void checkOnProjectAnalysis_does_nothing_when_no_extension_available() {
        underTest = new BillingValidationsProxyImpl();
        underTest.checkBeforeProjectAnalysis(BillingValidationsProxyImplTest.ORGANIZATION);
        Mockito.verifyZeroInteractions(billingValidationsExtension);
    }

    @Test
    public void checkCanUpdateProjectsVisibility_calls_extension_when_available() {
        underTest = new BillingValidationsProxyImpl(billingValidationsExtension);
        underTest.checkCanUpdateProjectVisibility(BillingValidationsProxyImplTest.ORGANIZATION, true);
        Mockito.verify(billingValidationsExtension).checkCanUpdateProjectVisibility(BillingValidationsProxyImplTest.ORGANIZATION, true);
    }

    @Test
    public void checkCanUpdateProjectsVisibility_does_nothing_when_no_extension_available() {
        underTest = new BillingValidationsProxyImpl();
        underTest.checkCanUpdateProjectVisibility(BillingValidationsProxyImplTest.ORGANIZATION, true);
        Mockito.verifyZeroInteractions(billingValidationsExtension);
    }

    @Test
    public void canUpdateProjectsVisibilityToPrivate_calls_extension_when_available() {
        underTest = new BillingValidationsProxyImpl(billingValidationsExtension);
        Mockito.when(billingValidationsExtension.canUpdateProjectVisibilityToPrivate(BillingValidationsProxyImplTest.ORGANIZATION)).thenReturn(false);
        boolean result = underTest.canUpdateProjectVisibilityToPrivate(BillingValidationsProxyImplTest.ORGANIZATION);
        assertThat(result).isFalse();
        Mockito.verify(billingValidationsExtension).canUpdateProjectVisibilityToPrivate(BillingValidationsProxyImplTest.ORGANIZATION);
    }

    @Test
    public void canUpdateProjectsVisibilityToPrivate_return_true_when_no_extension() {
        underTest = new BillingValidationsProxyImpl();
        boolean result = underTest.canUpdateProjectVisibilityToPrivate(BillingValidationsProxyImplTest.ORGANIZATION);
        assertThat(result).isTrue();
        Mockito.verifyZeroInteractions(billingValidationsExtension);
    }
}

