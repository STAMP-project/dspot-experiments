/**
 * Copyright 2014-2019 Real Logic Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.aeron;


import FrameDescriptor.BEGIN_FRAG_FLAG;
import FrameDescriptor.END_FRAG_FLAG;
import FrameDescriptor.UNFRAGMENTED;
import io.aeron.logbuffer.FragmentHandler;
import io.aeron.logbuffer.Header;
import io.aeron.logbuffer.LogBufferDescriptor;
import org.agrona.concurrent.UnsafeBuffer;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class FragmentAssemblerTest {
    private static final int SESSION_ID = 777;

    private static final int INITIAL_TERM_ID = 3;

    private final FragmentHandler delegateFragmentHandler = Mockito.mock(FragmentHandler.class);

    private final UnsafeBuffer termBuffer = Mockito.mock(UnsafeBuffer.class);

    private final Header header = Mockito.spy(new Header(FragmentAssemblerTest.INITIAL_TERM_ID, LogBufferDescriptor.TERM_MIN_LENGTH));

    private final FragmentAssembler adapter = new FragmentAssembler(delegateFragmentHandler);

    @Test
    public void shouldPassThroughUnfragmentedMessage() {
        Mockito.when(header.flags()).thenReturn(UNFRAGMENTED);
        final UnsafeBuffer srcBuffer = new UnsafeBuffer(new byte[128]);
        final int offset = 8;
        final int length = 32;
        adapter.onFragment(srcBuffer, offset, length, header);
        Mockito.verify(delegateFragmentHandler, Mockito.times(1)).onFragment(srcBuffer, offset, length, header);
    }

    @Test
    public void shouldAssembleTwoPartMessage() {
        Mockito.when(header.flags()).thenReturn(BEGIN_FRAG_FLAG).thenReturn(END_FRAG_FLAG);
        final UnsafeBuffer srcBuffer = new UnsafeBuffer(new byte[1024]);
        final int offset = 0;
        final int length = (srcBuffer.capacity()) / 2;
        srcBuffer.setMemory(0, length, ((byte) (65)));
        srcBuffer.setMemory(length, length, ((byte) (66)));
        adapter.onFragment(srcBuffer, offset, length, header);
        adapter.onFragment(srcBuffer, length, length, header);
        final ArgumentCaptor<UnsafeBuffer> bufferArg = ArgumentCaptor.forClass(UnsafeBuffer.class);
        final ArgumentCaptor<Header> headerArg = ArgumentCaptor.forClass(Header.class);
        Mockito.verify(delegateFragmentHandler, Mockito.times(1)).onFragment(bufferArg.capture(), ArgumentMatchers.eq(offset), ArgumentMatchers.eq((length * 2)), headerArg.capture());
        final UnsafeBuffer capturedBuffer = bufferArg.getValue();
        for (int i = 0; i < (srcBuffer.capacity()); i++) {
            Assert.assertThat(("same at i=" + i), capturedBuffer.getByte(i), CoreMatchers.is(srcBuffer.getByte(i)));
        }
        final Header capturedHeader = headerArg.getValue();
        Assert.assertThat(capturedHeader.sessionId(), CoreMatchers.is(FragmentAssemblerTest.SESSION_ID));
        Assert.assertThat(capturedHeader.flags(), CoreMatchers.is(END_FRAG_FLAG));
    }

    @Test
    public void shouldAssembleFourPartMessage() {
        Mockito.when(header.flags()).thenReturn(BEGIN_FRAG_FLAG).thenReturn(((byte) (0))).thenReturn(((byte) (0))).thenReturn(END_FRAG_FLAG);
        final UnsafeBuffer srcBuffer = new UnsafeBuffer(new byte[1024]);
        final int offset = 0;
        final int length = (srcBuffer.capacity()) / 4;
        for (int i = 0; i < 4; i++) {
            srcBuffer.setMemory((i * length), length, ((byte) (65 + i)));
        }
        adapter.onFragment(srcBuffer, offset, length, header);
        adapter.onFragment(srcBuffer, (offset + length), length, header);
        adapter.onFragment(srcBuffer, (offset + (length * 2)), length, header);
        adapter.onFragment(srcBuffer, (offset + (length * 3)), length, header);
        final ArgumentCaptor<UnsafeBuffer> bufferArg = ArgumentCaptor.forClass(UnsafeBuffer.class);
        final ArgumentCaptor<Header> headerArg = ArgumentCaptor.forClass(Header.class);
        Mockito.verify(delegateFragmentHandler, Mockito.times(1)).onFragment(bufferArg.capture(), ArgumentMatchers.eq(offset), ArgumentMatchers.eq((length * 4)), headerArg.capture());
        final UnsafeBuffer capturedBuffer = bufferArg.getValue();
        for (int i = 0; i < (srcBuffer.capacity()); i++) {
            Assert.assertThat(("same at i=" + i), capturedBuffer.getByte(i), CoreMatchers.is(srcBuffer.getByte(i)));
        }
        final Header capturedHeader = headerArg.getValue();
        Assert.assertThat(capturedHeader.sessionId(), CoreMatchers.is(FragmentAssemblerTest.SESSION_ID));
        Assert.assertThat(capturedHeader.flags(), CoreMatchers.is(END_FRAG_FLAG));
    }

    @Test
    public void shouldFreeSessionBuffer() {
        Mockito.when(header.flags()).thenReturn(BEGIN_FRAG_FLAG).thenReturn(END_FRAG_FLAG);
        final UnsafeBuffer srcBuffer = new UnsafeBuffer(new byte[1024]);
        final int offset = 0;
        final int length = (srcBuffer.capacity()) / 2;
        srcBuffer.setMemory(0, length, ((byte) (65)));
        srcBuffer.setMemory(length, length, ((byte) (66)));
        Assert.assertFalse(adapter.freeSessionBuffer(FragmentAssemblerTest.SESSION_ID));
        adapter.onFragment(srcBuffer, offset, length, header);
        adapter.onFragment(srcBuffer, length, length, header);
        Assert.assertTrue(adapter.freeSessionBuffer(FragmentAssemblerTest.SESSION_ID));
        Assert.assertFalse(adapter.freeSessionBuffer(FragmentAssemblerTest.SESSION_ID));
    }

    @Test
    public void shouldDoNotingIfEndArrivesWithoutBegin() {
        Mockito.when(header.flags()).thenReturn(END_FRAG_FLAG);
        final UnsafeBuffer srcBuffer = new UnsafeBuffer(new byte[1024]);
        final int offset = 0;
        final int length = (srcBuffer.capacity()) / 2;
        adapter.onFragment(srcBuffer, offset, length, header);
        Mockito.verify(delegateFragmentHandler, Mockito.never()).onFragment(ArgumentMatchers.any(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any());
    }

    @Test
    public void shouldDoNotingIfMidArrivesWithoutBegin() {
        Mockito.when(header.flags()).thenReturn(((byte) (0))).thenReturn(END_FRAG_FLAG);
        final UnsafeBuffer srcBuffer = new UnsafeBuffer(new byte[1024]);
        final int offset = 0;
        final int length = (srcBuffer.capacity()) / 2;
        adapter.onFragment(srcBuffer, offset, length, header);
        adapter.onFragment(srcBuffer, offset, length, header);
        Mockito.verify(delegateFragmentHandler, Mockito.never()).onFragment(ArgumentMatchers.any(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any());
    }
}

