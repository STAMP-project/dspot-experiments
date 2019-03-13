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
package com.twosigma.beakerx.clojure.kernel;


import com.twosigma.beakerx.KernelExecutionTest;
import com.twosigma.beakerx.MessageFactoryTest;
import com.twosigma.beakerx.message.Message;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class ClojureKernelTest extends KernelExecutionTest {
    @Test
    public void evaluateFibSeq() throws Exception {
        // given
        String code = "" + (((("(def fib-seq-lazy \n" + "  ((fn rfib [a b] \n") + "     (lazy-seq (cons a (rfib b (+ a b)))))\n") + "   0 1))\n") + "(take 20 fib-seq-lazy)");
        Message message = MessageFactoryTest.getExecuteRequestMessage(code);
        // when
        kernelSocketsService.handleMsg(message);
        // then
        Optional<Message> idleMessage = waitForIdleMessage(kernelSocketsService.getKernelSockets());
        assertThat(idleMessage).isPresent();
        Optional<Message> result = waitForResult(kernelSocketsService.getKernelSockets());
        checkResultForErrors(result, code);
        verifyResult(result.get());
        verifyPublishedMsgs(kernelSocketsService);
        waitForSentMessage(kernelSocketsService.getKernelSockets());
        verifySentMsgs(kernelSocketsService);
    }

    @Test
    public void shouldDisplayTableDisplay() throws Exception {
        // given
        String code = "[{:foo 1}{:foo 2}]";
        Message message = MessageFactoryTest.getExecuteRequestMessage(code);
        // when
        kernelSocketsService.handleMsg(message);
        // then
        waitForIdleMessage(kernelSocketsService.getKernelSockets());
        verifyTableDisplay(kernelSocketsService);
    }

    @Test
    public void shouldDisplayPlot() throws Exception {
        // given
        String code = "" + (((((((("(import \'[com.twosigma.beakerx.chart.xychart Plot]\n" + "        \'[com.twosigma.beakerx.chart.xychart.plotitem Line])\n") + "(doto (Plot.)\n") + "            (.setTitle \"We Will Control the Title\")\n") + "            (.setXLabel \"Horizontal\")\n") + "            (.setYLabel \"Vertical\")\n") + "            (.add (doto (Line.)\n") + "                        (.setX [0, 1, 2, 3, 4, 5])\n") + "                        (.setY [0, 1, 6, 5, 2, 8]))))");
        Message message = MessageFactoryTest.getExecuteRequestMessage(code);
        // when
        kernelSocketsService.handleMsg(message);
        // then
        Optional<Message> idle = waitForIdleMessage(kernelSocketsService.getKernelSockets());
        Assertions.assertThat(idle).isPresent();
        verifyPlot(kernelSocketsService);
    }

    @Test
    public void shouldImportDemoClassWithWildcardByMagicCommand() throws Exception {
        // clojure doesn't support wildcard
    }
}

