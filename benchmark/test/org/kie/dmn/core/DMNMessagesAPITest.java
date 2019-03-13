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
package org.kie.dmn.core;


import DMNMessageType.ERR_COMPILING_FEEL;
import KieServices.Factory;
import Message.Level.ERROR;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DMNMessagesAPITest {
    public static final Logger LOG = LoggerFactory.getLogger(DMNMessagesAPITest.class);

    @Test
    public void testAPIUsage() {
        // DROOLS-3335 Broken DMN resource should inhibit KJAR and report KieBuilder message
        final KieServices ks = Factory.get();
        final KieContainer kieContainer = DMNRuntimeUtil.getKieContainerIgnoringErrors(ks.newReleaseId("org.kie", ("dmn-test-" + (UUID.randomUUID())), "1.0"), ks.getResources().newClassPathResource("incomplete_expression.dmn", this.getClass()), ks.getResources().newClassPathResource("duff.drl", this.getClass()));
        Results verify = kieContainer.verify();
        List<Message> kie_messages = verify.getMessages();
        kie_messages.forEach(( m) -> LOG.info("{}", m));
        Assert.assertThat(kie_messages.size(), CoreMatchers.is(3));
        Assert.assertThat(kie_messages.stream().filter(( m) -> m.getPath().equals("duff.drl")).count(), CoreMatchers.is(2L));
        List<DMNMessage> dmnMessages = kie_messages.stream().filter(DMNMessage.class::isInstance).map(DMNMessage.class::cast).collect(Collectors.toList());
        Assert.assertThat(dmnMessages.size(), CoreMatchers.is(1));
        DMNMessage dmnMessage = dmnMessages.get(0);
        Assert.assertThat(dmnMessage.getSourceId(), CoreMatchers.is("_c990c3b2-e322-4ef9-931d-79bcdac99686"));
        Assert.assertThat(dmnMessage.getMessageType(), CoreMatchers.is(ERR_COMPILING_FEEL));
        Assert.assertThat(dmnMessage.getPath(), CoreMatchers.is("incomplete_expression.dmn"));
    }

    @Test(expected = IllegalStateException.class)
    public void testAPIUsageSnippetForDocumentation() {
        KieServices ks = Factory.get();
        ReleaseId releaseId = ks.newReleaseId("org.kie", ("dmn-test-" + (UUID.randomUUID())), "1.0");
        Resource dmnResource = ks.getResources().newClassPathResource("incomplete_expression.dmn", this.getClass());
        KieFileSystem kfs = ks.newKieFileSystem().generateAndWritePomXML(releaseId).write(dmnResource);
        KieBuilder kieBuilder = ks.newKieBuilder(kfs).buildAll();
        Results results = kieBuilder.getResults();
        if (results.hasMessages(ERROR)) {
            throw new IllegalStateException(results.getMessages(ERROR).toString());
        }
    }
}

