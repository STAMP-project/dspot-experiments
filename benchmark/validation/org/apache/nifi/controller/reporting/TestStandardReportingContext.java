/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.controller.reporting;


import org.apache.nifi.admin.service.AuditService;
import org.apache.nifi.authorization.AbstractPolicyBasedAuthorizer;
import org.apache.nifi.bundle.Bundle;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.components.PropertyValue;
import org.apache.nifi.controller.DummyScheduledReportingTask;
import org.apache.nifi.controller.FlowController;
import org.apache.nifi.controller.ReportingTaskNode;
import org.apache.nifi.controller.repository.FlowFileEventRepository;
import org.apache.nifi.encrypt.StringEncryptor;
import org.apache.nifi.nar.ExtensionDiscoveringManager;
import org.apache.nifi.registry.VariableRegistry;
import org.apache.nifi.registry.flow.FlowRegistryClient;
import org.apache.nifi.reporting.BulletinRepository;
import org.apache.nifi.util.NiFiProperties;
import org.junit.Assert;
import org.junit.Test;


public class TestStandardReportingContext {
    private static final String DEFAULT_SENSITIVE_PROPS_KEY = "nififtw!";

    private FlowController controller;

    private AbstractPolicyBasedAuthorizer authorizer;

    private FlowFileEventRepository flowFileEventRepo;

    private AuditService auditService;

    private StringEncryptor encryptor;

    private NiFiProperties nifiProperties;

    private Bundle systemBundle;

    private ExtensionDiscoveringManager extensionManager;

    private BulletinRepository bulletinRepo;

    private VariableRegistry variableRegistry;

    private FlowRegistryClient flowRegistry;

    private volatile String propsFile = TestStandardReportingContext.class.getResource("/flowcontrollertest.nifi.properties").getFile();

    @Test
    public void testGetPropertyReportingTask() throws ReportingTaskInstantiationException {
        ReportingTaskNode reportingTask = controller.getFlowManager().createReportingTask(DummyScheduledReportingTask.class.getName(), systemBundle.getBundleDetails().getCoordinate());
        PropertyDescriptor TEST_WITHOUT_DEFAULT_VALUE = new PropertyDescriptor.Builder().name("Test without default value").build();
        PropertyDescriptor TEST_WITH_DEFAULT_VALUE = new PropertyDescriptor.Builder().name("Test with default value").build();
        PropertyValue defaultValue = reportingTask.getReportingContext().getProperty(TEST_WITH_DEFAULT_VALUE);
        Assert.assertEquals("nifi", defaultValue.getValue());
        PropertyValue value = reportingTask.getReportingContext().getProperty(TEST_WITHOUT_DEFAULT_VALUE);
        Assert.assertEquals(null, value.getValue());
    }
}

