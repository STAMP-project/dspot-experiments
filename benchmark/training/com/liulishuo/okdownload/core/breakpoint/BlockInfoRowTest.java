/**
 * Copyright (c) 2017 LingoChamp Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.liulishuo.okdownload.core.breakpoint;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = NONE)
public class BlockInfoRowTest {
    private BlockInfoRow blockInfoRow;

    @Test
    public void construction() {
        assertThat(blockInfoRow.getBreakpointId()).isEqualTo(10);
        assertThat(blockInfoRow.getStartOffset()).isEqualTo(20);
        assertThat(blockInfoRow.getContentLength()).isEqualTo(30);
        assertThat(blockInfoRow.getCurrentOffset()).isEqualTo(5);
    }

    @Test
    public void toInfo() {
        final BlockInfo info = blockInfoRow.toInfo();
        assertThat(info.getStartOffset()).isEqualTo(20);
        assertThat(info.getContentLength()).isEqualTo(30);
        assertThat(info.getCurrentOffset()).isEqualTo(5);
    }
}

