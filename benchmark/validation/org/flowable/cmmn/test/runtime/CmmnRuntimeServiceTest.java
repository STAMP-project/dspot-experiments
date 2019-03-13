/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flowable.cmmn.test.runtime;


import CallbackTypes.CASE_ADHOC_CHILD;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.cmmn.engine.test.CmmnDeployment;
import org.flowable.cmmn.engine.test.FlowableCmmnTestCase;
import org.flowable.cmmn.engine.test.impl.CmmnJobTestHelper;
import org.flowable.common.engine.api.FlowableIllegalArgumentException;
import org.flowable.common.engine.api.FlowableObjectNotFoundException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * This class tests {@link CmmnRuntimeServiceImpl} implementation
 */
public class CmmnRuntimeServiceTest extends FlowableCmmnTestCase {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    @CmmnDeployment(resources = "org/flowable/cmmn/test/runtime/oneTaskCase.cmmn")
    public void createCaseInstanceWithCallBacks() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCase").callbackId("testCallBackId").callbackType(CASE_ADHOC_CHILD).start();
        // in fact it must be possible to set any callbackType and Id
        Assert.assertThat(caseInstance.getCallbackType(), Is.is(CASE_ADHOC_CHILD));
        Assert.assertThat(caseInstance.getCallbackId(), Is.is("testCallBackId"));
    }

    @Test
    @CmmnDeployment(resources = "org/flowable/cmmn/test/runtime/oneTaskCase.cmmn")
    public void createCaseInstanceWithoutCallBacks() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCase").start();
        // default values for callbacks are null
        Assert.assertThat(caseInstance.getCallbackType(), CoreMatchers.nullValue());
        Assert.assertThat(caseInstance.getCallbackId(), CoreMatchers.nullValue());
    }

    @Test
    @CmmnDeployment(resources = "org/flowable/cmmn/test/runtime/oneTaskCase.cmmn")
    public void updateCaseName() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCase").start();
        // default name is empty
        Assert.assertThat(caseInstance.getName(), CoreMatchers.nullValue());
        cmmnRuntimeService.setCaseInstanceName(caseInstance.getId(), "My case name");
        caseInstance = cmmnRuntimeService.createCaseInstanceQuery().caseInstanceId(caseInstance.getId()).singleResult();
        Assert.assertEquals("My case name", caseInstance.getName());
    }

    @Test
    @CmmnDeployment(resources = "org/flowable/cmmn/test/runtime/oneTaskCase.cmmn")
    public void updateCaseNameSetEmpty() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCase").start();
        // default name is empty
        Assert.assertThat(caseInstance.getName(), CoreMatchers.nullValue());
        cmmnRuntimeService.setCaseInstanceName(caseInstance.getId(), "My case name");
        caseInstance = cmmnRuntimeService.createCaseInstanceQuery().caseInstanceId(caseInstance.getId()).singleResult();
        Assert.assertEquals("My case name", caseInstance.getName());
        cmmnRuntimeService.setCaseInstanceName(caseInstance.getId(), null);
        caseInstance = cmmnRuntimeService.createCaseInstanceQuery().caseInstanceId(caseInstance.getId()).singleResult();
        Assert.assertThat(caseInstance.getName(), CoreMatchers.nullValue());
    }

    @Test
    @CmmnDeployment(resources = "org/flowable/cmmn/test/runtime/oneTaskCase.cmmn")
    public void createCaseInstanceAsync() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCase").startAsync();
        Assert.assertThat(caseInstance, Is.is(CoreMatchers.notNullValue()));
        Assert.assertThat("Plan items are created asynchronously", this.cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).count(), Is.is(0L));
        CmmnJobTestHelper.waitForJobExecutorToProcessAllJobs(cmmnEngineConfiguration, 1000, 100, true);
        Assert.assertThat(this.cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).count(), Is.is(1L));
    }

    @Test
    public void createCaseInstanceAsyncWithoutDef() {
        expectedException.expect(FlowableIllegalArgumentException.class);
        expectedException.expectMessage("caseDefinitionKey and caseDefinitionId are null");
        cmmnRuntimeService.createCaseInstanceBuilder().startAsync();
    }

    @Test
    public void createCaseInstanceAsyncWithNonExistingDefKey() {
        expectedException.expect(FlowableObjectNotFoundException.class);
        expectedException.expectMessage("No case definition found for key nonExistingDefinition");
        cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("nonExistingDefinition").startAsync();
    }

    @Test
    public void createCaseInstanceAsyncWithNonExistingDefId() {
        expectedException.expect(FlowableObjectNotFoundException.class);
        expectedException.expectMessage("No case definition found for id nonExistingDefinition");
        cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionId("nonExistingDefinition").startAsync();
    }

    @Test
    @CmmnDeployment(resources = "org/flowable/cmmn/test/runtime/oneTaskCase.cmmn")
    public void createCaseInstanceWithFallback() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCase").tenantId("flowable").overrideCaseDefinitionTenantId("flowable").fallbackToDefaultTenant().start();
        Assert.assertThat(caseInstance, Is.is(CoreMatchers.notNullValue()));
        Assert.assertThat(caseInstance.getTenantId(), Is.is("flowable"));
    }

    @Test
    @CmmnDeployment(resources = "org/flowable/cmmn/test/runtime/oneTaskCase.cmmn", tenantId = "defaultFlowable")
    public void createCaseInstanceWithFallbackAndOverrideTenantId() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCase").tenantId("defaultFlowable").overrideCaseDefinitionTenantId("someTenant").start();
        Assert.assertThat(caseInstance, Is.is(CoreMatchers.notNullValue()));
        Assert.assertThat(caseInstance.getTenantId(), Is.is("someTenant"));
    }

    @Test
    @CmmnDeployment(resources = "org/flowable/cmmn/test/runtime/oneTaskCase.cmmn", tenantId = "defaultFlowable")
    public void createCaseInstanceWithGlobalFallbackAndDefaultTenantValue() {
        String originalDefaultTenantValue = cmmnEngineConfiguration.getDefaultTenantValue();
        cmmnEngineConfiguration.setFallbackToDefaultTenant(true);
        cmmnEngineConfiguration.setDefaultTenantValue("defaultFlowable");
        try {
            CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCase").tenantId("someTenant").start();
            Assert.assertThat(caseInstance, Is.is(CoreMatchers.notNullValue()));
            Assert.assertThat(caseInstance.getTenantId(), Is.is("someTenant"));
        } finally {
            cmmnEngineConfiguration.setFallbackToDefaultTenant(false);
            cmmnEngineConfiguration.setDefaultTenantValue(originalDefaultTenantValue);
        }
    }

    @Test
    public void createCaseInstanceWithFallbackDefinitionNotFound() {
        this.expectedException.expect(FlowableObjectNotFoundException.class);
        this.expectedException.expectMessage("Case definition was not found by key 'oneTaskCase'. Fallback to default tenant was also used.");
        cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCase").tenantId("flowable").fallbackToDefaultTenant().start();
    }

    @Test
    @CmmnDeployment(resources = "org/flowable/cmmn/test/runtime/oneTaskCase.cmmn", tenantId = "tenant1")
    public void createCaseInstanceWithGlobalFallbackDefinitionNotFound() {
        String originalDefaultTenantValue = cmmnEngineConfiguration.getDefaultTenantValue();
        cmmnEngineConfiguration.setFallbackToDefaultTenant(true);
        cmmnEngineConfiguration.setDefaultTenantValue("defaultFlowable");
        this.expectedException.expect(FlowableObjectNotFoundException.class);
        try {
            cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCase").tenantId("someTenant").start();
        } finally {
            cmmnEngineConfiguration.setFallbackToDefaultTenant(false);
            cmmnEngineConfiguration.setDefaultTenantValue(originalDefaultTenantValue);
        }
    }

    @Test
    @CmmnDeployment(resources = "org/flowable/cmmn/test/runtime/oneTaskCase.cmmn")
    public void createCaseInstanceAsyncWithFallback() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCase").tenantId("flowable").fallbackToDefaultTenant().startAsync();
        Assert.assertThat(caseInstance, Is.is(CoreMatchers.notNullValue()));
    }

    @Test
    public void createCaseInstanceAsyncWithFallbackDefinitionNotFound() {
        this.expectedException.expect(FlowableObjectNotFoundException.class);
        this.expectedException.expectMessage("Case definition was not found by key 'oneTaskCase'. Fallback to default tenant was also used.");
        cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCase").tenantId("flowable").fallbackToDefaultTenant().startAsync();
    }
}

