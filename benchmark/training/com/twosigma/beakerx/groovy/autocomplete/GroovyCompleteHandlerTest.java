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
package com.twosigma.beakerx.groovy.autocomplete;


import com.twosigma.beakerx.groovy.kernel.GroovyKernelMock;
import com.twosigma.beakerx.handler.CompleteHandler;
import com.twosigma.beakerx.message.Message;
import org.junit.Test;


public class GroovyCompleteHandlerTest {
    private CompleteHandler completeHandler;

    private static GroovyKernelMock groovyKernel;

    @Test
    public void shouldSendCompleteReplyMsgForPrintln() throws Exception {
        // given
        Message message = autocompleteMsgFor(("//parentheses are optional\n" + ("System.out.printl \"hey!\"\n" + "println \"no System.out either!\"")), 44);
        // when
        completeHandler.handle(message);
        // then
        assertThat(getSentMessages().size()).isEqualTo(1);
        verifyAutocompleteMsg(getSentMessages().get(0), 38, 44);
    }

    @Test
    public void shouldSendCompleteReplyMsgForDef() throws Exception {
        // given
        String comment = "//parentheses are optional\n";
        Message message = autocompleteMsgFor((comment + "de"), ((comment.length()) + 2));
        // when
        completeHandler.handle(message);
        // then
        assertThat(getSentMessages().size()).isEqualTo(1);
        verifyAutocompleteMsg(getSentMessages().get(0), 27, ((comment.length()) + 2));
    }
}

