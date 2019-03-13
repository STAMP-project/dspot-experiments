/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.cdi.example;


import javax.inject.Inject;
import org.drools.cdi.CDITestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(CDITestRunner.class)
public class CDIExamplesTest {
    @Inject
    private Message defaultMsg;

    @Inject
    @Msg1
    private Message2 m1;

    @Inject
    @Msg2
    private Message2 m2;

    @Inject
    @Msg1
    private String msg1;

    @Inject
    @Msg2
    private String msg2;

    @Inject
    @Msg("named1")
    private String msgNamed1;

    @Inject
    @Msg("named2")
    private String msgNamed2;

    @Inject
    @Msg("chained1")
    private String msgChained1;

    @Inject
    @Msg("chained2")
    private String msgChained2;

    @Test
    public void testDefaultInjection() {
        Assert.assertEquals("default.msg", defaultMsg.getText());
    }

    @Test
    public void testSimpleQualifiedInjection() {
        Assert.assertEquals("msg.1", msg1);
        Assert.assertEquals("msg.2", msg2);
    }

    @Test
    public void testQualiferWithValueInjection() {
        Assert.assertEquals("msg.named1", msgNamed1);
        Assert.assertEquals("msg.named2", msgNamed2);
    }

    @Test
    public void testChained1Injection() {
        Assert.assertEquals("chained.1 msg.1", msgChained1);
    }

    @Test
    public void testChained2Injection() {
        Assert.assertEquals("chained.2 default.msg msg.1 msg.named1", msgChained2);
    }

    @Test
    public void testNoProducers() {
        Assert.assertEquals("msg2 - 1", m1.getText());
        Assert.assertEquals("msg2 - 2", m2.getText());
    }
}

