/**
 * Copyright 2018 The gRPC Authors
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
package io.grpc.alts.internal;


import AltsTsiFrameProtector.Unprotector;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.ReferenceCounted;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link AltsTsiFrameProtector}.
 */
@RunWith(JUnit4.class)
public class AltsTsiFrameProtectorTest {
    private static final int FRAME_MIN_SIZE = (AltsTsiFrameProtector.getHeaderTypeFieldBytes()) + (FakeChannelCrypter.getTagBytes());

    private final List<ReferenceCounted> references = new ArrayList<>();

    private final ByteBufTestUtils.RegisterRef ref = new ByteBufTestUtils.RegisterRef() {
        @Override
        public ByteBuf register(ByteBuf buf) {
            if (buf != null) {
                references.add(buf);
            }
            return buf;
        }
    };

    @Test
    public void parserHeader_frameLengthNegativeFails() throws GeneralSecurityException {
        ByteBufAllocator alloc = ByteBufAllocator.DEFAULT;
        List<Object> out = new ArrayList<>();
        FakeChannelCrypter crypter = new FakeChannelCrypter();
        AltsTsiFrameProtector.Unprotector unprotector = new AltsTsiFrameProtector.Unprotector(crypter, alloc);
        ByteBuf in = ByteBufTestUtils.getDirectBuffer(AltsTsiFrameProtector.getHeaderBytes(), ref);
        in.writeIntLE((-1));
        in.writeIntLE(6);
        try {
            unprotector.unprotect(in, out, alloc);
            Assert.fail("Exception expected");
        } catch (IllegalArgumentException ex) {
            assertThat(ex).hasMessageThat().contains("Invalid header field: frame size too small");
        }
        unprotector.destroy();
    }

    @Test
    public void parserHeader_frameTooSmall() throws GeneralSecurityException {
        ByteBufAllocator alloc = ByteBufAllocator.DEFAULT;
        List<Object> out = new ArrayList<>();
        FakeChannelCrypter crypter = new FakeChannelCrypter();
        AltsTsiFrameProtector.Unprotector unprotector = new AltsTsiFrameProtector.Unprotector(crypter, alloc);
        ByteBuf in = ByteBufTestUtils.getDirectBuffer(((AltsTsiFrameProtector.getHeaderBytes()) + (FakeChannelCrypter.getTagBytes())), ref);
        in.writeIntLE(((AltsTsiFrameProtectorTest.FRAME_MIN_SIZE) - 1));
        in.writeIntLE(6);
        try {
            unprotector.unprotect(in, out, alloc);
            Assert.fail("Exception expected");
        } catch (IllegalArgumentException ex) {
            assertThat(ex).hasMessageThat().contains("Invalid header field: frame size too small");
        }
        unprotector.destroy();
    }

    @Test
    public void parserHeader_frameTooLarge() throws GeneralSecurityException {
        ByteBufAllocator alloc = ByteBufAllocator.DEFAULT;
        List<Object> out = new ArrayList<>();
        FakeChannelCrypter crypter = new FakeChannelCrypter();
        AltsTsiFrameProtector.Unprotector unprotector = new AltsTsiFrameProtector.Unprotector(crypter, alloc);
        ByteBuf in = ByteBufTestUtils.getDirectBuffer(((AltsTsiFrameProtector.getHeaderBytes()) + (FakeChannelCrypter.getTagBytes())), ref);
        in.writeIntLE((((AltsTsiFrameProtector.getLimitMaxAllowedFrameBytes()) - (AltsTsiFrameProtector.getHeaderLenFieldBytes())) + 1));
        in.writeIntLE(6);
        try {
            unprotector.unprotect(in, out, alloc);
            Assert.fail("Exception expected");
        } catch (IllegalArgumentException ex) {
            assertThat(ex).hasMessageThat().contains("Invalid header field: frame size too large");
        }
        unprotector.destroy();
    }

    @Test
    public void parserHeader_frameTypeInvalid() throws GeneralSecurityException {
        ByteBufAllocator alloc = ByteBufAllocator.DEFAULT;
        List<Object> out = new ArrayList<>();
        FakeChannelCrypter crypter = new FakeChannelCrypter();
        AltsTsiFrameProtector.Unprotector unprotector = new AltsTsiFrameProtector.Unprotector(crypter, alloc);
        ByteBuf in = ByteBufTestUtils.getDirectBuffer(((AltsTsiFrameProtector.getHeaderBytes()) + (FakeChannelCrypter.getTagBytes())), ref);
        in.writeIntLE(AltsTsiFrameProtectorTest.FRAME_MIN_SIZE);
        in.writeIntLE(5);
        try {
            unprotector.unprotect(in, out, alloc);
            Assert.fail("Exception expected");
        } catch (IllegalArgumentException ex) {
            assertThat(ex).hasMessageThat().contains("Invalid header field: frame type");
        }
        unprotector.destroy();
    }

    @Test
    public void parserHeader_frameZeroOk() throws GeneralSecurityException {
        ByteBufAllocator alloc = ByteBufAllocator.DEFAULT;
        List<Object> out = new ArrayList<>();
        FakeChannelCrypter crypter = new FakeChannelCrypter();
        AltsTsiFrameProtector.Unprotector unprotector = new AltsTsiFrameProtector.Unprotector(crypter, alloc);
        ByteBuf in = ByteBufTestUtils.getDirectBuffer(((AltsTsiFrameProtector.getHeaderBytes()) + (FakeChannelCrypter.getTagBytes())), ref);
        in.writeIntLE(AltsTsiFrameProtectorTest.FRAME_MIN_SIZE);
        in.writeIntLE(6);
        unprotector.unprotect(in, out, alloc);
        assertThat(in.readableBytes()).isEqualTo(0);
        unprotector.destroy();
    }

    @Test
    public void parserHeader_EmptyUnprotectNoRetain() throws GeneralSecurityException {
        ByteBufAllocator alloc = ByteBufAllocator.DEFAULT;
        List<Object> out = new ArrayList<>();
        FakeChannelCrypter crypter = new FakeChannelCrypter();
        AltsTsiFrameProtector.Unprotector unprotector = new AltsTsiFrameProtector.Unprotector(crypter, alloc);
        ByteBuf emptyBuf = ByteBufTestUtils.getDirectBuffer(0, ref);
        unprotector.unprotect(emptyBuf, out, alloc);
        assertThat(emptyBuf.refCnt()).isEqualTo(1);
        unprotector.destroy();
    }

    @Test
    public void parserHeader_frameMaxOk() throws GeneralSecurityException {
        ByteBufAllocator alloc = ByteBufAllocator.DEFAULT;
        List<Object> out = new ArrayList<>();
        FakeChannelCrypter crypter = new FakeChannelCrypter();
        AltsTsiFrameProtector.Unprotector unprotector = new AltsTsiFrameProtector.Unprotector(crypter, alloc);
        ByteBuf in = ByteBufTestUtils.getDirectBuffer(((AltsTsiFrameProtector.getHeaderBytes()) + (FakeChannelCrypter.getTagBytes())), ref);
        in.writeIntLE(((AltsTsiFrameProtector.getLimitMaxAllowedFrameBytes()) - (AltsTsiFrameProtector.getHeaderLenFieldBytes())));
        in.writeIntLE(6);
        unprotector.unprotect(in, out, alloc);
        assertThat(in.readableBytes()).isEqualTo(0);
        unprotector.destroy();
    }

    @Test
    public void parserHeader_frameOkFragment() throws GeneralSecurityException {
        ByteBufAllocator alloc = ByteBufAllocator.DEFAULT;
        List<Object> out = new ArrayList<>();
        FakeChannelCrypter crypter = new FakeChannelCrypter();
        AltsTsiFrameProtector.Unprotector unprotector = new AltsTsiFrameProtector.Unprotector(crypter, alloc);
        ByteBuf in = ByteBufTestUtils.getDirectBuffer(((AltsTsiFrameProtector.getHeaderBytes()) + (FakeChannelCrypter.getTagBytes())), ref);
        in.writeIntLE(AltsTsiFrameProtectorTest.FRAME_MIN_SIZE);
        in.writeIntLE(6);
        ByteBuf in1 = in.readSlice(((AltsTsiFrameProtector.getHeaderBytes()) - 1));
        ByteBuf in2 = in.readSlice(1);
        unprotector.unprotect(in1, out, alloc);
        assertThat(in1.readableBytes()).isEqualTo(0);
        unprotector.unprotect(in2, out, alloc);
        assertThat(in2.readableBytes()).isEqualTo(0);
        unprotector.destroy();
    }

    @Test
    public void parseHeader_frameFailFragment() throws GeneralSecurityException {
        ByteBufAllocator alloc = ByteBufAllocator.DEFAULT;
        List<Object> out = new ArrayList<>();
        FakeChannelCrypter crypter = new FakeChannelCrypter();
        AltsTsiFrameProtector.Unprotector unprotector = new AltsTsiFrameProtector.Unprotector(crypter, alloc);
        ByteBuf in = ByteBufTestUtils.getDirectBuffer(((AltsTsiFrameProtector.getHeaderBytes()) + (FakeChannelCrypter.getTagBytes())), ref);
        in.writeIntLE(((AltsTsiFrameProtectorTest.FRAME_MIN_SIZE) - 1));
        in.writeIntLE(6);
        ByteBuf in1 = in.readSlice(((AltsTsiFrameProtector.getHeaderBytes()) - 1));
        ByteBuf in2 = in.readSlice(1);
        unprotector.unprotect(in1, out, alloc);
        assertThat(in1.readableBytes()).isEqualTo(0);
        try {
            unprotector.unprotect(in2, out, alloc);
            Assert.fail("Exception expected");
        } catch (IllegalArgumentException ex) {
            assertThat(ex).hasMessageThat().contains("Invalid header field: frame size too small");
        }
        assertThat(in2.readableBytes()).isEqualTo(0);
        unprotector.destroy();
    }

    @Test
    public void parseFrame_oneFrameNoFragment() throws GeneralSecurityException {
        int payloadBytes = 1024;
        ByteBufAllocator alloc = ByteBufAllocator.DEFAULT;
        List<Object> out = new ArrayList<>();
        FakeChannelCrypter crypter = new FakeChannelCrypter();
        AltsTsiFrameProtector.Unprotector unprotector = new AltsTsiFrameProtector.Unprotector(crypter, alloc);
        ByteBuf plain = ByteBufTestUtils.getRandom(payloadBytes, ref);
        ByteBuf outFrame = ByteBufTestUtils.getDirectBuffer((((AltsTsiFrameProtector.getHeaderBytes()) + payloadBytes) + (FakeChannelCrypter.getTagBytes())), ref);
        outFrame.writeIntLE((((AltsTsiFrameProtector.getHeaderTypeFieldBytes()) + payloadBytes) + (FakeChannelCrypter.getTagBytes())));
        outFrame.writeIntLE(6);
        List<ByteBuf> framePlain = Collections.singletonList(plain);
        ByteBuf frameOut = ByteBufTestUtils.writeSlice(outFrame, (payloadBytes + (FakeChannelCrypter.getTagBytes())));
        crypter.encrypt(frameOut, framePlain);
        plain.readerIndex(0);
        unprotector.unprotect(outFrame, out, alloc);
        assertThat(outFrame.readableBytes()).isEqualTo(0);
        assertThat(out.size()).isEqualTo(1);
        ByteBuf out1 = ref(((ByteBuf) (out.get(0))));
        assertThat(out1).isEqualTo(plain);
        unprotector.destroy();
    }

    @Test
    public void parseFrame_twoFramesNoFragment() throws GeneralSecurityException {
        int payloadBytes = 1536;
        int payloadBytes1 = 1024;
        int payloadBytes2 = payloadBytes - payloadBytes1;
        ByteBufAllocator alloc = ByteBufAllocator.DEFAULT;
        List<Object> out = new ArrayList<>();
        FakeChannelCrypter crypter = new FakeChannelCrypter();
        AltsTsiFrameProtector.Unprotector unprotector = new AltsTsiFrameProtector.Unprotector(crypter, alloc);
        ByteBuf plain = ByteBufTestUtils.getRandom(payloadBytes, ref);
        ByteBuf outFrame = ByteBufTestUtils.getDirectBuffer(((2 * ((AltsTsiFrameProtector.getHeaderBytes()) + (FakeChannelCrypter.getTagBytes()))) + payloadBytes), ref);
        outFrame.writeIntLE((((AltsTsiFrameProtector.getHeaderTypeFieldBytes()) + payloadBytes1) + (FakeChannelCrypter.getTagBytes())));
        outFrame.writeIntLE(6);
        List<ByteBuf> framePlain1 = Collections.singletonList(plain.readSlice(payloadBytes1));
        ByteBuf frameOut1 = ByteBufTestUtils.writeSlice(outFrame, (payloadBytes1 + (FakeChannelCrypter.getTagBytes())));
        outFrame.writeIntLE((((AltsTsiFrameProtector.getHeaderTypeFieldBytes()) + payloadBytes2) + (FakeChannelCrypter.getTagBytes())));
        outFrame.writeIntLE(6);
        List<ByteBuf> framePlain2 = Collections.singletonList(plain);
        ByteBuf frameOut2 = ByteBufTestUtils.writeSlice(outFrame, (payloadBytes2 + (FakeChannelCrypter.getTagBytes())));
        crypter.encrypt(frameOut1, framePlain1);
        crypter.encrypt(frameOut2, framePlain2);
        plain.readerIndex(0);
        unprotector.unprotect(outFrame, out, alloc);
        assertThat(out.size()).isEqualTo(1);
        ByteBuf out1 = ref(((ByteBuf) (out.get(0))));
        assertThat(out1).isEqualTo(plain);
        assertThat(outFrame.refCnt()).isEqualTo(1);
        assertThat(outFrame.readableBytes()).isEqualTo(0);
        unprotector.destroy();
    }

    @Test
    public void parseFrame_twoFramesNoFragment_Leftover() throws GeneralSecurityException {
        int payloadBytes = 1536;
        int payloadBytes1 = 1024;
        int payloadBytes2 = payloadBytes - payloadBytes1;
        ByteBufAllocator alloc = ByteBufAllocator.DEFAULT;
        List<Object> out = new ArrayList<>();
        FakeChannelCrypter crypter = new FakeChannelCrypter();
        AltsTsiFrameProtector.Unprotector unprotector = new AltsTsiFrameProtector.Unprotector(crypter, alloc);
        ByteBuf plain = ByteBufTestUtils.getRandom(payloadBytes, ref);
        ByteBuf protectedBuf = ByteBufTestUtils.getDirectBuffer((((2 * ((AltsTsiFrameProtector.getHeaderBytes()) + (FakeChannelCrypter.getTagBytes()))) + payloadBytes) + (AltsTsiFrameProtector.getHeaderBytes())), ref);
        protectedBuf.writeIntLE((((AltsTsiFrameProtector.getHeaderTypeFieldBytes()) + payloadBytes1) + (FakeChannelCrypter.getTagBytes())));
        protectedBuf.writeIntLE(6);
        List<ByteBuf> framePlain1 = Collections.singletonList(plain.readSlice(payloadBytes1));
        ByteBuf frameOut1 = ByteBufTestUtils.writeSlice(protectedBuf, (payloadBytes1 + (FakeChannelCrypter.getTagBytes())));
        protectedBuf.writeIntLE((((AltsTsiFrameProtector.getHeaderTypeFieldBytes()) + payloadBytes2) + (FakeChannelCrypter.getTagBytes())));
        protectedBuf.writeIntLE(6);
        List<ByteBuf> framePlain2 = Collections.singletonList(plain);
        ByteBuf frameOut2 = ByteBufTestUtils.writeSlice(protectedBuf, (payloadBytes2 + (FakeChannelCrypter.getTagBytes())));
        // This is an invalid header length field, make sure it triggers an error
        // when the remainder of the header is given.
        protectedBuf.writeIntLE(((byte) (-1)));
        crypter.encrypt(frameOut1, framePlain1);
        crypter.encrypt(frameOut2, framePlain2);
        plain.readerIndex(0);
        unprotector.unprotect(protectedBuf, out, alloc);
        assertThat(out.size()).isEqualTo(1);
        ByteBuf out1 = ref(((ByteBuf) (out.get(0))));
        assertThat(out1).isEqualTo(plain);
        // The protectedBuf is buffered inside the unprotector.
        assertThat(protectedBuf.readableBytes()).isEqualTo(0);
        assertThat(protectedBuf.refCnt()).isEqualTo(2);
        protectedBuf.writeIntLE(6);
        try {
            unprotector.unprotect(protectedBuf, out, alloc);
            Assert.fail("Exception expected");
        } catch (IllegalArgumentException ex) {
            assertThat(ex).hasMessageThat().contains("Invalid header field: frame size too small");
        }
        unprotector.destroy();
        // Make sure that unprotector does not hold onto buffered ByteBuf instance after destroy.
        assertThat(protectedBuf.refCnt()).isEqualTo(1);
        // Make sure that destroying twice does not throw.
        unprotector.destroy();
    }

    @Test
    public void parseFrame_twoFramesFragmentSecond() throws GeneralSecurityException {
        int payloadBytes = 1536;
        int payloadBytes1 = 1024;
        int payloadBytes2 = payloadBytes - payloadBytes1;
        ByteBufAllocator alloc = ByteBufAllocator.DEFAULT;
        List<Object> out = new ArrayList<>();
        FakeChannelCrypter crypter = new FakeChannelCrypter();
        AltsTsiFrameProtector.Unprotector unprotector = new AltsTsiFrameProtector.Unprotector(crypter, alloc);
        ByteBuf plain = ByteBufTestUtils.getRandom(payloadBytes, ref);
        ByteBuf protectedBuf = ByteBufTestUtils.getDirectBuffer((((2 * ((AltsTsiFrameProtector.getHeaderBytes()) + (FakeChannelCrypter.getTagBytes()))) + payloadBytes) + (AltsTsiFrameProtector.getHeaderBytes())), ref);
        protectedBuf.writeIntLE((((AltsTsiFrameProtector.getHeaderTypeFieldBytes()) + payloadBytes1) + (FakeChannelCrypter.getTagBytes())));
        protectedBuf.writeIntLE(6);
        List<ByteBuf> framePlain1 = Collections.singletonList(plain.readSlice(payloadBytes1));
        ByteBuf frameOut1 = ByteBufTestUtils.writeSlice(protectedBuf, (payloadBytes1 + (FakeChannelCrypter.getTagBytes())));
        protectedBuf.writeIntLE((((AltsTsiFrameProtector.getHeaderTypeFieldBytes()) + payloadBytes2) + (FakeChannelCrypter.getTagBytes())));
        protectedBuf.writeIntLE(6);
        List<ByteBuf> framePlain2 = Collections.singletonList(plain);
        ByteBuf frameOut2 = ByteBufTestUtils.writeSlice(protectedBuf, (payloadBytes2 + (FakeChannelCrypter.getTagBytes())));
        crypter.encrypt(frameOut1, framePlain1);
        crypter.encrypt(frameOut2, framePlain2);
        plain.readerIndex(0);
        unprotector.unprotect(protectedBuf.readSlice((((payloadBytes + (AltsTsiFrameProtector.getHeaderBytes())) + (FakeChannelCrypter.getTagBytes())) + (AltsTsiFrameProtector.getHeaderBytes()))), out, alloc);
        assertThat(out.size()).isEqualTo(1);
        ByteBuf out1 = ref(((ByteBuf) (out.get(0))));
        assertThat(out1).isEqualTo(plain.readSlice(payloadBytes1));
        assertThat(protectedBuf.refCnt()).isEqualTo(2);
        unprotector.unprotect(protectedBuf, out, alloc);
        assertThat(out.size()).isEqualTo(2);
        ByteBuf out2 = ref(((ByteBuf) (out.get(1))));
        assertThat(out2).isEqualTo(plain);
        assertThat(protectedBuf.refCnt()).isEqualTo(1);
        unprotector.destroy();
    }
}

