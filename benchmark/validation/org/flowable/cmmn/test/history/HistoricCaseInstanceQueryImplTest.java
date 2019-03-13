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
package org.flowable.cmmn.test.history;


import IdentityLinkType.CANDIDATE;
import IdentityLinkType.PARTICIPANT;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.cmmn.engine.test.FlowableCmmnTestCase;
import org.flowable.common.engine.impl.identity.Authentication;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNot;
import org.junit.Assert;
import org.junit.Test;


/**
 * This class tests {@link CaseInstanceQueryImpl} implementation
 */
public class HistoricCaseInstanceQueryImplTest extends FlowableCmmnTestCase {
    private String deplId;

    @Test
    public void getCaseInstanceByCaseDefinitionKey() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCase").start();
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().caseDefinitionKey("oneTaskCase").count(), Is.is(1L));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().caseDefinitionKey("oneTaskCase").list().get(0).getId(), Is.is(caseInstance.getId()));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().caseDefinitionKey("oneTaskCase").singleResult().getId(), Is.is(caseInstance.getId()));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().or().caseDefinitionKey("oneTaskCase").caseInstanceId("Undefined").endOr().count(), Is.is(1L));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().or().caseDefinitionKey("oneTaskCase").caseInstanceId("Undefined").endOr().list().get(0).getId(), Is.is(caseInstance.getId()));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().or().caseDefinitionKey("oneTaskCase").caseInstanceId("Undefined").endOr().singleResult().getId(), Is.is(caseInstance.getId()));
    }

    @Test
    public void getCaseInstanceByCaseInstanceName() {
        CaseInstance caseInstance1 = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCase").name("taskName1").start();
        cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCase").name("taskName2").start();
        cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCase").name("nameTask3").start();
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().caseInstanceNameLikeIgnoreCase("taskName%").count(), Is.is(2L));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().caseInstanceNameLikeIgnoreCase("%TASK3").count(), Is.is(1L));
    }

    @Test
    public void getCaseInstanceByCaseDefinitionKeys() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCase").start();
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().caseDefinitionKeys(Collections.singleton("oneTaskCase")).count(), Is.is(1L));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().caseDefinitionKeys(Collections.singleton("oneTaskCase")).list().get(0).getId(), Is.is(caseInstance.getId()));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().caseDefinitionKeys(Collections.singleton("oneTaskCase")).singleResult().getId(), Is.is(caseInstance.getId()));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().or().caseInstanceId("undefined").caseDefinitionKeys(Collections.singleton("oneTaskCase")).endOr().count(), Is.is(1L));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().or().caseInstanceId("undefined").caseDefinitionKeys(Collections.singleton("oneTaskCase")).endOr().list().get(0).getId(), Is.is(caseInstance.getId()));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().or().caseInstanceId("undefined").caseDefinitionKeys(Collections.singleton("oneTaskCase")).endOr().singleResult().getId(), Is.is(caseInstance.getId()));
    }

    @Test
    public void getCaseInstanceByCaseDefinitionCategory() {
        cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCase").start();
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().caseDefinitionCategory("http://flowable.org/cmmn").count(), Is.is(2L));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().caseDefinitionCategory("http://flowable.org/cmmn").list().size(), Is.is(2));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().or().caseDefinitionCategory("http://flowable.org/cmmn").caseInstanceId("undefined").endOr().count(), Is.is(2L));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().or().caseDefinitionCategory("http://flowable.org/cmmn").caseInstanceId("undefined").endOr().list().size(), Is.is(2));
    }

    @Test
    public void getCaseInstanceByCaseDefinitionName() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCase").start();
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().caseDefinitionName("oneTaskCaseName").count(), Is.is(1L));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().caseDefinitionName("oneTaskCaseName").list().get(0).getId(), Is.is(caseInstance.getId()));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().caseDefinitionName("oneTaskCaseName").singleResult().getId(), Is.is(caseInstance.getId()));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().or().caseDefinitionName("oneTaskCaseName").caseInstanceId("undefined").endOr().count(), Is.is(1L));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().or().caseDefinitionName("oneTaskCaseName").caseInstanceId("undefined").endOr().list().get(0).getId(), Is.is(caseInstance.getId()));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().or().caseDefinitionName("oneTaskCaseName").caseInstanceId("undefined").endOr().singleResult().getId(), Is.is(caseInstance.getId()));
    }

    @Test
    public void getCaseInstanceByCaseDefinitionId() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCase").start();
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().caseDefinitionId(caseInstance.getCaseDefinitionId()).count(), Is.is(1L));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().caseDefinitionId(caseInstance.getCaseDefinitionId()).list().get(0).getId(), Is.is(caseInstance.getId()));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().caseDefinitionId(caseInstance.getCaseDefinitionId()).singleResult().getId(), Is.is(caseInstance.getId()));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().or().caseDefinitionId(caseInstance.getCaseDefinitionId()).caseInstanceId("undefinedId").endOr().count(), Is.is(1L));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().or().caseDefinitionId(caseInstance.getCaseDefinitionId()).caseInstanceId("undefinedId").endOr().list().get(0).getId(), Is.is(caseInstance.getId()));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().or().caseDefinitionId(caseInstance.getCaseDefinitionId()).caseInstanceId("undefinedId").endOr().singleResult().getId(), Is.is(caseInstance.getId()));
    }

    @Test
    public void getCaseInstanceByCaseDefinitionVersion() {
        cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCase").start();
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().caseDefinitionVersion(1).count(), Is.is(2L));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().caseDefinitionVersion(1).list().size(), Is.is(2));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().or().caseDefinitionVersion(1).caseInstanceId("undefinedId").endOr().count(), Is.is(2L));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().or().caseDefinitionVersion(1).caseInstanceId("undefinedId").endOr().list().size(), Is.is(2));
    }

    @Test
    public void getCaseInstanceByCaseId() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCase").start();
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().caseInstanceId(caseInstance.getId()).count(), Is.is(1L));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().caseInstanceId(caseInstance.getId()).list().get(0).getId(), Is.is(caseInstance.getId()));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().caseInstanceId(caseInstance.getId()).singleResult().getId(), Is.is(caseInstance.getId()));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().or().caseInstanceId(caseInstance.getId()).caseDefinitionName("undefinedId").endOr().count(), Is.is(1L));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().or().caseInstanceId(caseInstance.getId()).caseDefinitionName("undefinedId").endOr().list().get(0).getId(), Is.is(caseInstance.getId()));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().or().caseInstanceId(caseInstance.getId()).caseDefinitionId("undefined").endOr().singleResult().getId(), Is.is(caseInstance.getId()));
    }

    @Test
    public void getCaseInstanceByBusinessKey() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCase").businessKey("businessKey").start();
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().caseInstanceBusinessKey("businessKey").count(), Is.is(1L));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().caseInstanceBusinessKey("businessKey").list().get(0).getId(), Is.is(caseInstance.getId()));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().caseInstanceBusinessKey("businessKey").singleResult().getId(), Is.is(caseInstance.getId()));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().or().caseInstanceBusinessKey("businessKey").caseDefinitionName("undefinedId").endOr().count(), Is.is(1L));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().or().caseInstanceBusinessKey("businessKey").caseDefinitionName("undefinedId").endOr().list().get(0).getId(), Is.is(caseInstance.getId()));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().or().caseInstanceBusinessKey("businessKey").caseDefinitionId("undefined").endOr().singleResult().getId(), Is.is(caseInstance.getId()));
    }

    @Test
    public void getCaseInstanceByStartedBefore() {
        cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCase").start();
        Calendar todayCal = new GregorianCalendar();
        Calendar dateCal = new GregorianCalendar(((todayCal.get(Calendar.YEAR)) + 1), todayCal.get(Calendar.MONTH), todayCal.get(Calendar.DAY_OF_YEAR));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().startedBefore(dateCal.getTime()).count(), Is.is(2L));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().startedBefore(dateCal.getTime()).list().size(), Is.is(2));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().or().startedBefore(dateCal.getTime()).caseDefinitionName("undefinedId").endOr().count(), Is.is(2L));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().or().startedBefore(dateCal.getTime()).caseDefinitionName("undefinedId").endOr().list().size(), Is.is(2));
    }

    @Test
    public void getCaseInstanceByStartedAfter() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCase").start();
        Calendar todayCal = new GregorianCalendar();
        Calendar dateCal = new GregorianCalendar(((todayCal.get(Calendar.YEAR)) - 1), todayCal.get(Calendar.MONTH), todayCal.get(Calendar.DAY_OF_YEAR));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().startedAfter(dateCal.getTime()).count(), Is.is(2L));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().startedAfter(dateCal.getTime()).list().size(), Is.is(2));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().or().startedAfter(dateCal.getTime()).caseDefinitionName("undefinedId").endOr().count(), Is.is(2L));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().or().startedAfter(dateCal.getTime()).caseDefinitionName("undefinedId").endOr().list().size(), Is.is(2));
    }

    @Test
    public void getCaseInstanceByStartedBy() {
        String authenticatedUserId = Authentication.getAuthenticatedUserId();
        try {
            Authentication.setAuthenticatedUserId("kermit");
            CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCase").start();
            Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().startedBy("kermit").count(), Is.is(1L));
            Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().startedBy("kermit").list().get(0).getId(), Is.is(caseInstance.getId()));
            Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().startedBy("kermit").singleResult().getId(), Is.is(caseInstance.getId()));
            Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().or().startedBy("kermit").caseDefinitionName("undefinedId").endOr().count(), Is.is(1L));
            Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().or().startedBy("kermit").caseDefinitionName("undefinedId").endOr().list().get(0).getId(), Is.is(caseInstance.getId()));
            Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().or().startedBy("kermit").caseDefinitionId("undefined").endOr().singleResult().getId(), Is.is(caseInstance.getId()));
        } finally {
            Authentication.setAuthenticatedUserId(authenticatedUserId);
        }
    }

    @Test
    public void getCaseInstanceByCallBackId() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCase").callbackId("callBackId").start();
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().caseInstanceCallbackId("callBackId").count(), Is.is(1L));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().caseInstanceCallbackId("callBackId").list().get(0).getId(), Is.is(caseInstance.getId()));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().caseInstanceCallbackId("callBackId").singleResult().getId(), Is.is(caseInstance.getId()));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().or().caseInstanceCallbackId("callBackId").caseDefinitionName("undefinedId").endOr().count(), Is.is(1L));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().or().caseInstanceCallbackId("callBackId").caseDefinitionName("undefinedId").endOr().list().get(0).getId(), Is.is(caseInstance.getId()));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().or().caseInstanceCallbackId("callBackId").caseDefinitionId("undefined").endOr().singleResult().getId(), Is.is(caseInstance.getId()));
    }

    @Test
    public void getCaseInstanceByCallBackType() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCase").callbackType("callBackType").start();
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().caseInstanceCallbackType("callBackType").count(), Is.is(1L));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().caseInstanceCallbackType("callBackType").list().get(0).getId(), Is.is(caseInstance.getId()));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().caseInstanceCallbackType("callBackType").singleResult().getId(), Is.is(caseInstance.getId()));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().or().caseInstanceCallbackType("callBackType").caseDefinitionName("undefinedId").endOr().count(), Is.is(1L));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().or().caseInstanceCallbackType("callBackType").caseDefinitionName("undefinedId").endOr().list().get(0).getId(), Is.is(caseInstance.getId()));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().or().caseInstanceCallbackType("callBackType").caseDefinitionId("undefined").endOr().singleResult().getId(), Is.is(caseInstance.getId()));
    }

    @Test
    public void getCaseInstanceByTenantId() {
        String tempDeploymentId = cmmnRepositoryService.createDeployment().addClasspathResource("org/flowable/cmmn/test/runtime/CaseTaskTest.testBasicBlocking.cmmn").addClasspathResource("org/flowable/cmmn/test/runtime/oneTaskCase.cmmn").tenantId("tenantId").deploy().getId();
        try {
            CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCase").tenantId("tenantId").start();
            Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().caseInstanceTenantId("tenantId").count(), Is.is(1L));
            Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().caseInstanceTenantId("tenantId").list().get(0).getId(), Is.is(caseInstance.getId()));
            Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().caseInstanceTenantId("tenantId").singleResult().getId(), Is.is(caseInstance.getId()));
            Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().or().caseInstanceTenantId("tenantId").caseDefinitionName("undefinedId").endOr().count(), Is.is(1L));
            Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().or().caseInstanceTenantId("tenantId").caseDefinitionName("undefinedId").endOr().list().get(0).getId(), Is.is(caseInstance.getId()));
            Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().or().caseInstanceTenantId("tenantId").caseDefinitionId("undefined").endOr().singleResult().getId(), Is.is(caseInstance.getId()));
        } finally {
            cmmnRepositoryService.deleteDeployment(tempDeploymentId, true);
        }
    }

    @Test
    public void getCaseInstanceWithoutTenantId() {
        String tempDeploymentId = cmmnRepositoryService.createDeployment().addClasspathResource("org/flowable/cmmn/test/runtime/CaseTaskTest.testBasicBlocking.cmmn").addClasspathResource("org/flowable/cmmn/test/runtime/oneTaskCase.cmmn").tenantId("tenantId").deploy().getId();
        try {
            CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCase").tenantId("tenantId").start();
            Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().caseInstanceWithoutTenantId().count(), Is.is(1L));
            Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().caseInstanceWithoutTenantId().list().get(0).getId(), Is.is(IsNot.not(caseInstance.getId())));
            Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().caseInstanceWithoutTenantId().singleResult().getId(), Is.is(IsNot.not(caseInstance.getId())));
            Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().or().caseInstanceWithoutTenantId().caseDefinitionName("undefinedId").endOr().count(), Is.is(1L));
            Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().or().caseInstanceWithoutTenantId().caseDefinitionName("undefinedId").endOr().list().get(0).getId(), Is.is(IsNot.not(caseInstance.getId())));
            Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().or().caseInstanceWithoutTenantId().caseDefinitionId("undefined").endOr().singleResult().getId(), Is.is(IsNot.not(caseInstance.getId())));
        } finally {
            cmmnRepositoryService.deleteDeployment(tempDeploymentId, true);
        }
    }

    @Test
    public void getCaseInstanceByInvolvedUser() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCase").start();
        cmmnRuntimeService.addUserIdentityLink(caseInstance.getId(), "kermit", PARTICIPANT);
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().involvedUser("kermit").count(), Is.is(1L));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().involvedUser("kermit").list().get(0).getId(), Is.is(caseInstance.getId()));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().involvedUser("kermit").singleResult().getId(), Is.is(caseInstance.getId()));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().or().involvedUser("kermit").caseDefinitionName("undefinedId").endOr().count(), Is.is(1L));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().or().involvedUser("kermit").caseDefinitionName("undefinedId").endOr().list().get(0).getId(), Is.is(caseInstance.getId()));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().or().involvedUser("kermit").caseDefinitionId("undefined").endOr().singleResult().getId(), Is.is(caseInstance.getId()));
    }

    @Test
    public void getCaseInstanceByInvolvedGroup() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCase").start();
        cmmnRuntimeService.addGroupIdentityLink(caseInstance.getId(), "testGroup", PARTICIPANT);
        cmmnRuntimeService.addGroupIdentityLink(caseInstance.getId(), "testGroup2", PARTICIPANT);
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().involvedGroups(Collections.singleton("testGroup")).count(), Is.is(1L));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().involvedGroups(Collections.singleton("testGroup")).list().get(0).getId(), Is.is(caseInstance.getId()));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().involvedGroups(Collections.singleton("testGroup")).singleResult().getId(), Is.is(caseInstance.getId()));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().or().involvedGroups(Collections.singleton("testGroup")).caseDefinitionName("undefinedId").endOr().count(), Is.is(1L));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().or().involvedGroups(Collections.singleton("testGroup")).caseDefinitionName("undefinedId").endOr().list().get(0).getId(), Is.is(caseInstance.getId()));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().or().involvedGroups(Collections.singleton("testGroup")).caseDefinitionId("undefined").endOr().singleResult().getId(), Is.is(caseInstance.getId()));
    }

    @Test
    public void getCaseInstanceByInvolvedGroupOrUser() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCase").start();
        cmmnRuntimeService.addGroupIdentityLink(caseInstance.getId(), "testGroup", PARTICIPANT);
        cmmnRuntimeService.addGroupIdentityLink(caseInstance.getId(), "testGroup2", PARTICIPANT);
        cmmnRuntimeService.addUserIdentityLink(caseInstance.getId(), "kermit", CANDIDATE);
        CaseInstance caseInstance2 = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCase").start();
        cmmnRuntimeService.addGroupIdentityLink(caseInstance2.getId(), "testGroup2", PARTICIPANT);
        CaseInstance caseInstance3 = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCase").start();
        cmmnRuntimeService.addUserIdentityLink(caseInstance3.getId(), "kermit", PARTICIPANT);
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().involvedGroups(Stream.of("testGroup", "testGroup2").collect(Collectors.toSet())).involvedUser("kermit").count(), Is.is(1L));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().involvedGroups(Stream.of("testGroup", "testGroup2").collect(Collectors.toSet())).involvedUser("kermit").list().get(0).getId(), Is.is(caseInstance.getId()));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().involvedGroups(Stream.of("testGroup", "testGroup2").collect(Collectors.toSet())).involvedUser("kermit").singleResult().getId(), Is.is(caseInstance.getId()));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().or().involvedGroups(Stream.of("testGroup", "testGroup2").collect(Collectors.toSet())).caseDefinitionName("undefinedId").endOr().count(), Is.is(2L));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().or().involvedGroups(Stream.of("testGroup", "testGroup2").collect(Collectors.toSet())).caseDefinitionName("undefinedId").endOr().list().size(), Is.is(2));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().or().involvedGroups(Stream.of("testGroup", "testGroup2").collect(Collectors.toSet())).involvedUser("kermit").caseDefinitionName("undefinedId").endOr().count(), Is.is(3L));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().or().involvedGroups(Stream.of("testGroup", "testGroup2").collect(Collectors.toSet())).involvedUser("kermit").caseDefinitionName("undefinedId").endOr().list().size(), Is.is(3));
    }

    @Test
    public void getCaseInstanceByVariable() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCase").variable("queryVariable", "queryVariableValue").start();
        CaseInstance caseInstance2 = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCase").variable("queryVariable", "queryVariableValue").variable("queryVariable2", "queryVariableValue2").start();
        CaseInstance caseInstance3 = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCase").variable("queryVariable", "queryVariableValue").variable("queryVariable3", "queryVariableValue3").start();
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueEquals("queryVariable", "queryVariableValue").variableValueEquals("queryVariable2", "queryVariableValue2").count(), Is.is(1L));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueEquals("queryVariable", "queryVariableValue").variableValueEquals("queryVariable2", "queryVariableValue2").list().get(0).getId(), Is.is(caseInstance2.getId()));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().variableValueEquals("queryVariable", "queryVariableValue").variableValueEquals("queryVariable2", "queryVariableValue2").singleResult().getId(), Is.is(caseInstance2.getId()));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().or().variableValueEquals("queryVariable", "queryVariableValue").variableValueEquals("queryVariable2", "queryVariableValue2").caseDefinitionName("undefinedId").endOr().count(), Is.is(3L));
        Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().or().variableValueEquals("queryVariable", "queryVariableValue").variableValueEquals("queryVariable2", "queryVariableValue2").caseDefinitionName("undefinedId").endOr().list().size(), Is.is(3));
    }

    @Test
    public void getCaseInstanceByIdWithoutTenant() {
        CaseInstance caseInstance1 = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCase").name("testCaseInstance1").start();
        CaseInstance caseInstance2 = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCase").name("testCaseInstance2").start();
        try {
            Assert.assertThat(cmmnHistoryService.createHistoricCaseInstanceQuery().caseInstanceId(caseInstance1.getId()).caseInstanceWithoutTenantId().count(), Is.is(1L));
        } finally {
            cmmnRuntimeService.terminateCaseInstance(caseInstance2.getId());
            cmmnHistoryService.deleteHistoricCaseInstance(caseInstance2.getId());
            cmmnRuntimeService.terminateCaseInstance(caseInstance1.getId());
            cmmnHistoryService.deleteHistoricCaseInstance(caseInstance1.getId());
        }
    }
}

