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


import com.liulishuo.okdownload.DownloadTask;
import java.io.File;
import org.junit.Test;
import org.mockito.Mockito;


public class BreakpointInfoTest {
    @Test
    public void getPath() {
        BreakpointInfo info = new BreakpointInfo(0, "", new File(""), null);
        assertThat(info.getFile()).isNull();
        final String parentPath = "/sdcard";
        final String filename = "abc";
        info = new BreakpointInfo(0, "", new File(parentPath), filename);
        assertThat(info.getFile()).isEqualTo(new File(parentPath, filename));
    }

    @Test
    public void copyNotClone() {
        BreakpointInfo info = new BreakpointInfo(0, "", new File(""), null);
        info.addBlock(new BlockInfo(0, 0));
        info.setChunked(true);
        final BreakpointInfo copy = info.copy();
        assertThat(info.getBlock(0)).isNotEqualTo(copy.getBlock(0));
        assertThat(copy.isChunked()).isTrue();
    }

    @Test
    public void getTotalOffset() {
        BreakpointInfo info = new BreakpointInfo(0, "", new File(""), null);
        info.addBlock(new BlockInfo(0, 10, 10));
        info.addBlock(new BlockInfo(10, 18, 18));
        info.addBlock(new BlockInfo(28, 66, 66));
        assertThat(info.getTotalOffset()).isEqualTo(94);
    }

    @Test
    public void getTotalLength_chunked() {
        BreakpointInfo info = Mockito.spy(new BreakpointInfo(0, "", new File(""), null));
        Mockito.when(info.isChunked()).thenReturn(true);
        Mockito.doReturn(1L).when(info).getTotalOffset();
        assertThat(info.getTotalLength()).isEqualTo(1L);
    }

    @Test
    public void getTotalLength() {
        BreakpointInfo info = new BreakpointInfo(0, "", new File(""), null);
        info.addBlock(new BlockInfo(0, 10));
        info.addBlock(new BlockInfo(10, 18, 2));
        info.addBlock(new BlockInfo(28, 66, 20));
        assertThat(info.getTotalLength()).isEqualTo(94);
    }

    @Test
    public void isSameFrom() {
        BreakpointInfo info = new BreakpointInfo(1, "url", new File("p-path"), "filename");
        DownloadTask task = Mockito.mock(DownloadTask.class);
        // no filename -> false
        Mockito.when(task.getUrl()).thenReturn("url");
        Mockito.when(task.getParentFile()).thenReturn(new File("p-path"));
        assertThat(info.isSameFrom(task)).isFalse();
        // same filename -> true
        Mockito.when(task.getFilename()).thenReturn("filename");
        assertThat(info.isSameFrom(task)).isTrue();
        // is directory but provided same filename -> true
        Mockito.when(task.isFilenameFromResponse()).thenReturn(true);
        assertThat(info.isSameFrom(task)).isTrue();
        info = new BreakpointInfo(1, "url", new File("p-path"), null);
        assertThat(info.isSameFrom(task)).isFalse();
        // not directory with filename -> false (don't know whether same yet)
        Mockito.when(task.isFilenameFromResponse()).thenReturn(false);
        assertThat(info.isSameFrom(task)).isFalse();
        // is directory and no filename -> true
        Mockito.when(task.getFilename()).thenReturn(null);
        Mockito.when(task.isFilenameFromResponse()).thenReturn(true);
        assertThat(info.isSameFrom(task)).isTrue();
        // not same url -> false
        Mockito.when(task.getUrl()).thenReturn("not-same-url");
        assertThat(info.isSameFrom(task)).isFalse();
    }

    @Test
    public void copyWithReplaceId() {
        BreakpointInfo info = new BreakpointInfo(1, "url", new File("/p-path/"), "filename");
        final BlockInfo oldBlockInfo = new BlockInfo(0, 1);
        info.addBlock(oldBlockInfo);
        info.setChunked(true);
        BreakpointInfo anotherInfo = info.copyWithReplaceId(2);
        assertThat(anotherInfo).isNotEqualTo(info);
        assertThat(anotherInfo.id).isEqualTo(2);
        assertThat(anotherInfo.getUrl()).isEqualTo("url");
        assertThat(anotherInfo.getFile()).isEqualTo(new File("/p-path/filename"));
        assertThat(anotherInfo.getBlockCount()).isEqualTo(1);
        assertThat(anotherInfo.isChunked()).isTrue();
        final BlockInfo newBlockInfo = anotherInfo.getBlock(0);
        assertThat(newBlockInfo).isNotEqualTo(oldBlockInfo);
        assertThat(newBlockInfo.getRangeLeft()).isEqualTo(oldBlockInfo.getRangeLeft());
        assertThat(newBlockInfo.getRangeRight()).isEqualTo(oldBlockInfo.getRangeRight());
    }

    @Test
    public void copyWithReplaceIdAndUrl() {
        BreakpointInfo info = new BreakpointInfo(1, "url", new File("/p-path/"), "filename");
        final BlockInfo oldBlockInfo = new BlockInfo(0, 1);
        info.addBlock(oldBlockInfo);
        info.setChunked(true);
        BreakpointInfo anotherInfo = info.copyWithReplaceIdAndUrl(2, "anotherUrl");
        assertThat(anotherInfo).isNotEqualTo(info);
        assertThat(anotherInfo.id).isEqualTo(2);
        assertThat(anotherInfo.getUrl()).isEqualTo("anotherUrl");
        assertThat(anotherInfo.getFile()).isEqualTo(new File("/p-path/filename"));
        assertThat(anotherInfo.getBlockCount()).isEqualTo(1);
        assertThat(anotherInfo.isChunked()).isTrue();
        final BlockInfo newBlockInfo = anotherInfo.getBlock(0);
        assertThat(newBlockInfo).isNotEqualTo(oldBlockInfo);
        assertThat(newBlockInfo.getRangeLeft()).isEqualTo(oldBlockInfo.getRangeLeft());
        assertThat(newBlockInfo.getRangeRight()).isEqualTo(oldBlockInfo.getRangeRight());
    }
}

