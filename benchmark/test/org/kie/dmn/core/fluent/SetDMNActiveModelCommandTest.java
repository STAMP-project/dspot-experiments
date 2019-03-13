/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.dmn.core.fluent;


import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.internal.command.RegistryContext;


public class SetDMNActiveModelCommandTest {
    static KieServices ks;

    static ReleaseId releaseId;

    static Resource resource;

    static KieContainer kieContainer;

    RegistryContext registryContext;

    DMNRuntime dmnRuntime;

    @Test
    public void execute() {
        String namespace = "http://www.trisotech.com/definitions/_99ccd4df-41ac-43c3-a563-d58f43149829";
        String modelName = "typecheck in DT";
        SetDMNActiveModelCommand setDMNActiveModelCommand = new SetDMNActiveModelCommand(namespace, modelName);
        assertThatThrownBy(() -> setDMNActiveModelCommand.execute(registryContext)).isInstanceOf(IllegalStateException.class).hasMessage("There is no DMNRuntime available");
        registryContext.register(DMNRuntime.class, dmnRuntime);
        DMNModel dmnModel = setDMNActiveModelCommand.execute(registryContext);
        Assert.assertEquals(namespace, dmnModel.getNamespace());
        Assert.assertEquals(modelName, dmnModel.getName());
    }

    @Test
    public void executeWithResource() {
        SetDMNActiveModelCommand setDMNActiveModelCommand = new SetDMNActiveModelCommand(SetDMNActiveModelCommandTest.resource.getSourcePath());
        assertThatThrownBy(() -> setDMNActiveModelCommand.execute(registryContext)).isInstanceOf(IllegalStateException.class).hasMessage("There is no DMNRuntime available");
        registryContext.register(DMNRuntime.class, dmnRuntime);
        DMNModel dmnModel = setDMNActiveModelCommand.execute(registryContext);
        Assert.assertEquals(SetDMNActiveModelCommandTest.resource.getSourcePath(), dmnModel.getResource().getSourcePath());
    }
}

