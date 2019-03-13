/**
 * Copyright 2017 TWO SIGMA OPEN SOURCE, LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.twosigma.beakerx.widget;


import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.MIMEContainerFactory;
import org.junit.Assert;
import org.junit.Test;


public class MIMEContainerFactoryBeakerxWidgetsTest {
    @Test
    public void shouldSend3MessagesForAllClassesWhichImplementInternalWidgetInterface() throws Exception {
        new BeakerxWidgetTestRunner().test(( clazz, groovyKernel) -> {
            // give
            Widget internalWidget = clazz.newInstance();
            // when
            MIMEContainerFactory.createMIMEContainers(internalWidget);
            // then
            Assert.assertEquals(("Should be 3 messages for " + clazz), groovyKernel.getPublishedMessages().size(), 3);
            assertThat(groovyKernel.getPublishedMessages().size()).isEqualTo(3);
            verifyOpenMsg(groovyKernel.getPublishedMessages().get(0), clazz);
            verifyModelMsg(groovyKernel.getPublishedMessages().get(1), clazz);
            verifyDisplayMsg(groovyKernel.getPublishedMessages().get(2), clazz);
        });
    }
}

