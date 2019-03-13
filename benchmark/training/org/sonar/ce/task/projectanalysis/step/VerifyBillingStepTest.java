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
package org.sonar.ce.task.projectanalysis.step;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.utils.MessageException;
import org.sonar.ce.task.projectanalysis.analysis.MutableAnalysisMetadataHolderRule;
import org.sonar.ce.task.projectanalysis.analysis.Organization;
import org.sonar.ce.task.step.TestComputationStepContext;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.organization.OrganizationTesting;
import org.sonar.server.organization.BillingValidations;
import org.sonar.server.organization.BillingValidations.BillingValidationsException;
import org.sonar.server.organization.BillingValidationsProxy;


public class VerifyBillingStepTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private OrganizationDto organization = OrganizationTesting.newOrganizationDto();

    @Rule
    public MutableAnalysisMetadataHolderRule analysisMetadata = new MutableAnalysisMetadataHolderRule().setOrganization(Organization.from(organization));

    private BillingValidationsProxy validations = Mockito.mock(BillingValidationsProxy.class);

    @Test
    public void execute_fails_with_MessageException_when_organization_is_not_allowed_to_execute_analysis() {
        Mockito.doThrow(new BillingValidationsException("This organization cannot execute project analysis")).when(validations).checkBeforeProjectAnalysis(ArgumentMatchers.any(Organization.class));
        VerifyBillingStep underTest = new VerifyBillingStep(analysisMetadata, validations);
        expectedException.expect(MessageException.class);
        expectedException.expectMessage("This organization cannot execute project analysis");
        underTest.execute(new TestComputationStepContext());
    }

    @Test
    public void execute_does_no_fail_when_organization_is_allowed_to_execute_analysis() {
        ArgumentCaptor<BillingValidations.Organization> orgCaptor = ArgumentCaptor.forClass(Organization.class);
        VerifyBillingStep underTest = new VerifyBillingStep(analysisMetadata, validations);
        underTest.execute(new TestComputationStepContext());
        Mockito.verify(validations).checkBeforeProjectAnalysis(orgCaptor.capture());
        BillingValidations.Organization calledOrg = orgCaptor.getValue();
        assertThat(calledOrg.getKey()).isEqualTo(organization.getKey());
        assertThat(calledOrg.getUuid()).isEqualTo(organization.getUuid());
        assertThat(calledOrg.getName()).isEqualTo(organization.getName());
    }
}

