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


import java.io.File;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = NONE)
public class BreakpointInfoRowTest {
    private BreakpointInfoRow breakpointInfoRow;

    @Test
    public void construction() {
        assertThat(breakpointInfoRow.getId()).isEqualTo(10);
        assertThat(breakpointInfoRow.getUrl()).isEqualTo("url");
        assertThat(breakpointInfoRow.getEtag()).isEqualTo("etag");
        assertThat(breakpointInfoRow.getParentPath()).isEqualTo("p-path");
        assertThat(breakpointInfoRow.getFilename()).isNull();
        assertThat(breakpointInfoRow.isTaskOnlyProvidedParentPath()).isTrue();
        assertThat(breakpointInfoRow.isChunked()).isFalse();
    }

    @Test
    public void toInfo() {
        final BreakpointInfo info = breakpointInfoRow.toInfo();
        assertThat(info.id).isEqualTo(10);
        assertThat(info.getUrl()).isEqualTo("url");
        assertThat(info.getEtag()).isEqualTo("etag");
        assertThat(info.parentFile).isEqualTo(new File("p-path"));
        assertThat(info.getFilename()).isNull();
        assertThat(info.isTaskOnlyProvidedParentPath()).isTrue();
        assertThat(info.isChunked()).isFalse();
    }
}

