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
package com.twosigma.beakerx.table;


import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.kernel.handler.CommMsgHandler;
import com.twosigma.beakerx.message.Message;
import org.junit.Test;


public class TableDisplayActionsTest {
    private KernelTest kernel;

    private CommMsgHandler commMsgHandler;

    private TableDisplay tableDisplay;

    @Test
    public void actionDetailsShouldHaveDetails() throws Exception {
        // given
        Message message = actionDetailsMessage();
        // when
        commMsgHandler.handle(message);
        // then
        assertThat(tableDisplay.getDetails()).isNotNull();
    }

    @Test
    public void contextMenuActionShouldHaveDetails() throws Exception {
        // given
        Message message = contextMenuMessage();
        // when
        commMsgHandler.handle(message);
        // then
        assertThat(tableDisplay.getDetails()).isNotNull();
    }

    @Test
    public void doubleClickActionShouldHaveDetails() throws Exception {
        // given
        Message message = doubleClickActionMessage();
        // when
        commMsgHandler.handle(message);
        // then
        assertThat(tableDisplay.getDetails()).isNotNull();
    }
}

