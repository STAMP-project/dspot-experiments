/**
 * Copyright 2012, Google Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the
 * distribution.
 *     * Neither the name of Google Inc. nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.jf.dexlib2.dexbacked;


import org.junit.Test;


public class BaseDexReaderTest {
    @Test
    public void testSizedInt() {
        performSizedIntTest(0, new byte[]{ 0 });
        performSizedIntTest(0, new byte[]{ 0, 0 });
        performSizedIntTest(0, new byte[]{ 0, 0, 0 });
        performSizedIntTest(0, new byte[]{ 0, 0, 0, 0 });
        performSizedIntTest(1, new byte[]{ 1 });
        performSizedIntTest(1, new byte[]{ 1, 0, 0, 0 });
        performSizedIntTest(64, new byte[]{ 64 });
        performSizedIntTest(127, new byte[]{ 127 });
        performSizedIntTest(-128, new byte[]{ ((byte) (128)) });
        performSizedIntTest((-1), new byte[]{ ((byte) (255)) });
        performSizedIntTest(256, new byte[]{ 0, 1 });
        performSizedIntTest(272, new byte[]{ 16, 1 });
        performSizedIntTest(32513, new byte[]{ 1, 127 });
        performSizedIntTest(-32767, new byte[]{ 1, ((byte) (128)) });
        performSizedIntTest(-240, new byte[]{ 16, ((byte) (255)) });
        performSizedIntTest(69633, new byte[]{ 1, 16, 1 });
        performSizedIntTest(8323344, new byte[]{ 16, 1, 127 });
        performSizedIntTest(-8384511, new byte[]{ 1, 16, ((byte) (128)) });
        performSizedIntTest(-65264, new byte[]{ 16, 1, ((byte) (255)) });
        performSizedIntTest(16789506, new byte[]{ 2, 48, 0, 1 });
        performSizedIntTest(2131821104, new byte[]{ 48, 2, 17, 127 });
        performSizedIntTest(-2146360781, new byte[]{ 51, 34, 17, ((byte) (128)) });
        performSizedIntTest((-1), new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)) });
    }

    @Test
    public void testSizedIntFailure() {
        // wrong size
        performSizedIntFailureTest(new byte[]{  });
        performSizedIntFailureTest(new byte[]{ 0, 0, 0, 0, 0 });
        performSizedIntFailureTest(new byte[]{ 0, 0, 0, 0, 0, 0 });
        performSizedIntFailureTest(new byte[]{ 18, 52, 86, 18, 52, 86, 120 });
    }

    @Test
    public void testSizedSmallUint() {
        performSizedSmallUintTest(0, new byte[]{ 0 });
        performSizedSmallUintTest(0, new byte[]{ 0, 0 });
        performSizedSmallUintTest(0, new byte[]{ 0, 0, 0 });
        performSizedSmallUintTest(0, new byte[]{ 0, 0, 0, 0 });
        performSizedSmallUintTest(1, new byte[]{ 1 });
        performSizedSmallUintTest(1, new byte[]{ 1, 0, 0, 0 });
        performSizedSmallUintTest(64, new byte[]{ 64 });
        performSizedSmallUintTest(127, new byte[]{ 127 });
        performSizedSmallUintTest(128, new byte[]{ ((byte) (128)) });
        performSizedSmallUintTest(255, new byte[]{ ((byte) (255)) });
        performSizedSmallUintTest(256, new byte[]{ 0, 1 });
        performSizedSmallUintTest(272, new byte[]{ 16, 1 });
        performSizedSmallUintTest(32513, new byte[]{ 1, 127 });
        performSizedSmallUintTest(32769, new byte[]{ 1, ((byte) (128)) });
        performSizedSmallUintTest(65296, new byte[]{ 16, ((byte) (255)) });
        performSizedSmallUintTest(69633, new byte[]{ 1, 16, 1 });
        performSizedSmallUintTest(8323344, new byte[]{ 16, 1, 127 });
        performSizedSmallUintTest(8392705, new byte[]{ 1, 16, ((byte) (128)) });
        performSizedSmallUintTest(16711952, new byte[]{ 16, 1, ((byte) (255)) });
        performSizedSmallUintTest(16789506, new byte[]{ 2, 48, 0, 1 });
        performSizedSmallUintTest(2131821104, new byte[]{ 48, 2, 17, 127 });
        performSizedSmallUintTest(Integer.MAX_VALUE, new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), 127 });
    }

    @Test
    public void testSizedSmallUintFailure() {
        // wrong size
        performSizedSmallUintFailureTest(new byte[]{  });
        performSizedSmallUintFailureTest(new byte[]{ 0, 0, 0, 0, 0 });
        performSizedSmallUintFailureTest(new byte[]{ 0, 0, 0, 0, 0, 0 });
        performSizedSmallUintFailureTest(new byte[]{ 18, 52, 86, 18, 52, 86, 120 });
        // MSB set
        performSizedSmallUintFailureTest(new byte[]{ 0, 0, 0, ((byte) (128)) });
        performSizedSmallUintFailureTest(new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)) });
    }

    @Test
    public void testSizedRightExtendedInt() {
        performSizedRightExtendedIntTest(0, new byte[]{ 0 });
        performSizedRightExtendedIntTest(0, new byte[]{ 0, 0 });
        performSizedRightExtendedIntTest(0, new byte[]{ 0, 0, 0 });
        performSizedRightExtendedIntTest(0, new byte[]{ 0, 0, 0, 0 });
        performSizedRightExtendedIntTest(16777216, new byte[]{ 1 });
        performSizedRightExtendedIntTest(2130706432, new byte[]{ 127 });
        performSizedRightExtendedIntTest(-2147483648, new byte[]{ ((byte) (128)) });
        performSizedRightExtendedIntTest(-268435456, new byte[]{ ((byte) (240)) });
        performSizedRightExtendedIntTest(-16777216, new byte[]{ ((byte) (255)) });
        performSizedRightExtendedIntTest(65536, new byte[]{ 1, 0 });
        performSizedRightExtendedIntTest(17825792, new byte[]{ 16, 1 });
        performSizedRightExtendedIntTest(2131755008, new byte[]{ 16, 127 });
        performSizedRightExtendedIntTest(-2146435072, new byte[]{ 16, ((byte) (128)) });
        performSizedRightExtendedIntTest(-267386880, new byte[]{ 16, ((byte) (240)) });
        performSizedRightExtendedIntTest(-15728640, new byte[]{ 16, ((byte) (255)) });
        performSizedRightExtendedIntTest(-16777216, new byte[]{ 0, ((byte) (255)) });
        performSizedRightExtendedIntTest(256, new byte[]{ 1, 0, 0 });
        performSizedRightExtendedIntTest(17829888, new byte[]{ 16, 16, 1 });
        performSizedRightExtendedIntTest(2131759104, new byte[]{ 16, 16, 127 });
        performSizedRightExtendedIntTest(-2146430976, new byte[]{ 16, 16, ((byte) (128)) });
        performSizedRightExtendedIntTest(-267382784, new byte[]{ 16, 16, ((byte) (240)) });
        performSizedRightExtendedIntTest(-15724544, new byte[]{ 16, 16, ((byte) (255)) });
        performSizedRightExtendedIntTest(-16777216, new byte[]{ 0, 0, ((byte) (255)) });
        performSizedRightExtendedIntTest(1, new byte[]{ 1, 0, 0, 0 });
        performSizedRightExtendedIntTest(128, new byte[]{ ((byte) (128)), 0, 0, 0 });
        performSizedRightExtendedIntTest(255, new byte[]{ ((byte) (255)), 0, 0, 0 });
        performSizedRightExtendedIntTest(17829904, new byte[]{ 16, 16, 16, 1 });
        performSizedRightExtendedIntTest(2131759120, new byte[]{ 16, 16, 16, 127 });
        performSizedRightExtendedIntTest(-2146430960, new byte[]{ 16, 16, 16, ((byte) (128)) });
        performSizedRightExtendedIntTest(-267382768, new byte[]{ 16, 16, 16, ((byte) (240)) });
        performSizedRightExtendedIntTest(-15724528, new byte[]{ 16, 16, 16, ((byte) (255)) });
        performSizedRightExtendedIntTest(-16777216, new byte[]{ 0, 0, 0, ((byte) (255)) });
    }

    @Test
    public void testSizedRightExtendedIntFailure() {
        // wrong size
        performSizedRightExtendedIntFailureTest(new byte[]{  });
        performSizedRightExtendedIntFailureTest(new byte[]{ 0, 0, 0, 0, 0 });
        performSizedRightExtendedIntFailureTest(new byte[]{ 0, 0, 0, 0, 0, 0 });
        performSizedRightExtendedIntFailureTest(new byte[]{ 18, 52, 86, 18, 52, 86, 120 });
    }

    @Test
    public void testSizedRightExtendedLong() {
        performSizedRightExtendedLongTest(0, new byte[]{ 0 });
        performSizedRightExtendedLongTest(0, new byte[]{ 0, 0 });
        performSizedRightExtendedLongTest(0, new byte[]{ 0, 0, 0 });
        performSizedRightExtendedLongTest(0, new byte[]{ 0, 0, 0, 0 });
        performSizedRightExtendedLongTest(0, new byte[]{ 0, 0, 0, 0, 0 });
        performSizedRightExtendedLongTest(0, new byte[]{ 0, 0, 0, 0, 0, 0 });
        performSizedRightExtendedLongTest(0, new byte[]{ 0, 0, 0, 0, 0, 0, 0 });
        performSizedRightExtendedLongTest(0, new byte[]{ 0, 0, 0, 0, 0, 0, 0, 0 });
        performSizedRightExtendedLongTest(72057594037927936L, new byte[]{ 1 });
        performSizedRightExtendedLongTest(9151314442816847872L, new byte[]{ 127 });
        performSizedRightExtendedLongTest(-9223372036854775808L, new byte[]{ ((byte) (128)) });
        performSizedRightExtendedLongTest(-1152921504606846976L, new byte[]{ ((byte) (240)) });
        performSizedRightExtendedLongTest(-72057594037927936L, new byte[]{ ((byte) (255)) });
        performSizedRightExtendedLongTest(281474976710656L, new byte[]{ 1, 0 });
        performSizedRightExtendedLongTest(76561193665298432L, new byte[]{ 16, 1 });
        performSizedRightExtendedLongTest(9155818042444218368L, new byte[]{ 16, 127 });
        performSizedRightExtendedLongTest(-9218868437227405312L, new byte[]{ 16, ((byte) (128)) });
        performSizedRightExtendedLongTest(-1148417904979476480L, new byte[]{ 16, ((byte) (240)) });
        performSizedRightExtendedLongTest(-67553994410557440L, new byte[]{ 16, ((byte) (255)) });
        performSizedRightExtendedLongTest(-72057594037927936L, new byte[]{ 0, ((byte) (255)) });
        performSizedRightExtendedLongTest(9223090561878065152L, new byte[]{ ((byte) (255)), ((byte) (127)) });
        performSizedRightExtendedLongTest(1099511627776L, new byte[]{ 1, 0, 0 });
        performSizedRightExtendedLongTest(76578785851342848L, new byte[]{ 16, 16, 1 });
        performSizedRightExtendedLongTest(9155835634630262784L, new byte[]{ 16, 16, 127 });
        performSizedRightExtendedLongTest(-9218850845041360896L, new byte[]{ 16, 16, ((byte) (128)) });
        performSizedRightExtendedLongTest(-1148400312793432064L, new byte[]{ 16, 16, ((byte) (240)) });
        performSizedRightExtendedLongTest(-67536402224513024L, new byte[]{ 16, 16, ((byte) (255)) });
        performSizedRightExtendedLongTest(-72057594037927936L, new byte[]{ 0, 0, ((byte) (255)) });
        performSizedRightExtendedLongTest(9223370937343148032L, new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (127)) });
        performSizedRightExtendedLongTest(4294967296L, new byte[]{ 1, 0, 0, 0 });
        performSizedRightExtendedLongTest(76578854570819584L, new byte[]{ 16, 16, 16, 1 });
        performSizedRightExtendedLongTest(9155835703349739520L, new byte[]{ 16, 16, 16, 127 });
        performSizedRightExtendedLongTest(-9218850776321884160L, new byte[]{ 16, 16, 16, ((byte) (128)) });
        performSizedRightExtendedLongTest(-1148400244073955328L, new byte[]{ 16, 16, 16, ((byte) (240)) });
        performSizedRightExtendedLongTest(-67536333505036288L, new byte[]{ 16, 16, 16, ((byte) (255)) });
        performSizedRightExtendedLongTest(-72057594037927936L, new byte[]{ 0, 0, 0, ((byte) (255)) });
        performSizedRightExtendedLongTest(9223372032559808512L, new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (127)) });
        performSizedRightExtendedLongTest(16777216L, new byte[]{ 1, 0, 0, 0, 0 });
        performSizedRightExtendedLongTest(76578854839255040L, new byte[]{ 16, 16, 16, 16, 1 });
        performSizedRightExtendedLongTest(9155835703618174976L, new byte[]{ 16, 16, 16, 16, 127 });
        performSizedRightExtendedLongTest(-9218850776053448704L, new byte[]{ 16, 16, 16, 16, ((byte) (128)) });
        performSizedRightExtendedLongTest(-1148400243805519872L, new byte[]{ 16, 16, 16, 16, ((byte) (240)) });
        performSizedRightExtendedLongTest(-67536333236600832L, new byte[]{ 16, 16, 16, 16, ((byte) (255)) });
        performSizedRightExtendedLongTest(-72057594037927936L, new byte[]{ 0, 0, 0, 0, ((byte) (255)) });
        performSizedRightExtendedLongTest(9223372036837998592L, new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (127)) });
        performSizedRightExtendedLongTest(65536L, new byte[]{ 1, 0, 0, 0, 0, 0 });
        performSizedRightExtendedLongTest(76578854840303616L, new byte[]{ 16, 16, 16, 16, 16, 1 });
        performSizedRightExtendedLongTest(9155835703619223552L, new byte[]{ 16, 16, 16, 16, 16, 127 });
        performSizedRightExtendedLongTest(-9218850776052400128L, new byte[]{ 16, 16, 16, 16, 16, ((byte) (128)) });
        performSizedRightExtendedLongTest(-1148400243804471296L, new byte[]{ 16, 16, 16, 16, 16, ((byte) (240)) });
        performSizedRightExtendedLongTest(-67536333235552256L, new byte[]{ 16, 16, 16, 16, 16, ((byte) (255)) });
        performSizedRightExtendedLongTest(-72057594037927936L, new byte[]{ 0, 0, 0, 0, 0, ((byte) (255)) });
        performSizedRightExtendedLongTest(9223372036854710272L, new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (127)) });
        performSizedRightExtendedLongTest(256L, new byte[]{ 1, 0, 0, 0, 0, 0, 0 });
        performSizedRightExtendedLongTest(76578854840307712L, new byte[]{ 16, 16, 16, 16, 16, 16, 1 });
        performSizedRightExtendedLongTest(9155835703619227648L, new byte[]{ 16, 16, 16, 16, 16, 16, 127 });
        performSizedRightExtendedLongTest(-9218850776052396032L, new byte[]{ 16, 16, 16, 16, 16, 16, ((byte) (128)) });
        performSizedRightExtendedLongTest(-1148400243804467200L, new byte[]{ 16, 16, 16, 16, 16, 16, ((byte) (240)) });
        performSizedRightExtendedLongTest(-67536333235548160L, new byte[]{ 16, 16, 16, 16, 16, 16, ((byte) (255)) });
        performSizedRightExtendedLongTest(-72057594037927936L, new byte[]{ 0, 0, 0, 0, 0, 0, ((byte) (255)) });
        performSizedRightExtendedLongTest(9223372036854775552L, new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (127)) });
        performSizedRightExtendedLongTest(1L, new byte[]{ 1, 0, 0, 0, 0, 0, 0, 0 });
        performSizedRightExtendedLongTest(76578854840307728L, new byte[]{ 16, 16, 16, 16, 16, 16, 16, 1 });
        performSizedRightExtendedLongTest(9155835703619227664L, new byte[]{ 16, 16, 16, 16, 16, 16, 16, 127 });
        performSizedRightExtendedLongTest(-9218850776052396016L, new byte[]{ 16, 16, 16, 16, 16, 16, 16, ((byte) (128)) });
        performSizedRightExtendedLongTest(-1148400243804467184L, new byte[]{ 16, 16, 16, 16, 16, 16, 16, ((byte) (240)) });
        performSizedRightExtendedLongTest(-67536333235548144L, new byte[]{ 16, 16, 16, 16, 16, 16, 16, ((byte) (255)) });
        performSizedRightExtendedLongTest(-72057594037927936L, new byte[]{ 0, 0, 0, 0, 0, 0, 0, ((byte) (255)) });
        performSizedRightExtendedLongTest(Long.MAX_VALUE, new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (127)) });
        performSizedRightExtendedLongTest(Long.MIN_VALUE, new byte[]{ 0, 0, 0, 0, 0, 0, 0, ((byte) (128)) });
        performSizedRightExtendedLongTest((-1), new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)) });
    }

    @Test
    public void testSizedRightExtendedLongFailure() {
        // wrong size
        performSizedRightExtendedLongFailureTest(new byte[]{  });
        performSizedRightExtendedLongFailureTest(new byte[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0 });
        performSizedRightExtendedLongFailureTest(new byte[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });
        performSizedRightExtendedLongFailureTest(new byte[]{ 18, 52, 86, 18, 52, 86, 120, ((byte) (137)), ((byte) (144)), 1 });
    }

    @Test
    public void testSizedLong() {
        performSizedLongTest(0, new byte[]{ 0 });
        performSizedLongTest(0, new byte[]{ 0, 0 });
        performSizedLongTest(0, new byte[]{ 0, 0, 0 });
        performSizedLongTest(0, new byte[]{ 0, 0, 0, 0 });
        performSizedLongTest(0, new byte[]{ 0, 0, 0, 0, 0 });
        performSizedLongTest(0, new byte[]{ 0, 0, 0, 0, 0, 0 });
        performSizedLongTest(0, new byte[]{ 0, 0, 0, 0, 0, 0, 0 });
        performSizedLongTest(0, new byte[]{ 0, 0, 0, 0, 0, 0, 0, 0 });
        performSizedLongTest(1L, new byte[]{ 1 });
        performSizedLongTest(127L, new byte[]{ 127 });
        performSizedLongTest(-128L, new byte[]{ ((byte) (128)) });
        performSizedLongTest(-16L, new byte[]{ ((byte) (240)) });
        performSizedLongTest(-1L, new byte[]{ ((byte) (255)) });
        performSizedLongTest(1L, new byte[]{ 1, 0 });
        performSizedLongTest(272L, new byte[]{ 16, 1 });
        performSizedLongTest(32528L, new byte[]{ 16, 127 });
        performSizedLongTest(-32752L, new byte[]{ 16, ((byte) (128)) });
        performSizedLongTest(-4080L, new byte[]{ 16, ((byte) (240)) });
        performSizedLongTest(-240L, new byte[]{ 16, ((byte) (255)) });
        performSizedLongTest(-256L, new byte[]{ 0, ((byte) (255)) });
        performSizedLongTest(32767L, new byte[]{ ((byte) (255)), ((byte) (127)) });
        performSizedLongTest(1L, new byte[]{ 1, 0, 0 });
        performSizedLongTest(69648L, new byte[]{ 16, 16, 1 });
        performSizedLongTest(8327184L, new byte[]{ 16, 16, 127 });
        performSizedLongTest(-8384496L, new byte[]{ 16, 16, ((byte) (128)) });
        performSizedLongTest(-1044464L, new byte[]{ 16, 16, ((byte) (240)) });
        performSizedLongTest(-61424L, new byte[]{ 16, 16, ((byte) (255)) });
        performSizedLongTest(-65536L, new byte[]{ 0, 0, ((byte) (255)) });
        performSizedLongTest(8388607L, new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (127)) });
        performSizedLongTest(1L, new byte[]{ 1, 0, 0, 0 });
        performSizedLongTest(17829904L, new byte[]{ 16, 16, 16, 1 });
        performSizedLongTest(2131759120L, new byte[]{ 16, 16, 16, 127 });
        performSizedLongTest(-2146430960L, new byte[]{ 16, 16, 16, ((byte) (128)) });
        performSizedLongTest(-267382768L, new byte[]{ 16, 16, 16, ((byte) (240)) });
        performSizedLongTest(-15724528L, new byte[]{ 16, 16, 16, ((byte) (255)) });
        performSizedLongTest(-16777216L, new byte[]{ 0, 0, 0, ((byte) (255)) });
        performSizedLongTest(2147483647L, new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (127)) });
        performSizedLongTest(1, new byte[]{ 1, 0, 0, 0, 0 });
        performSizedLongTest(4564455440L, new byte[]{ 16, 16, 16, 16, 1 });
        performSizedLongTest(545730334736L, new byte[]{ 16, 16, 16, 16, 127 });
        performSizedLongTest(-549486325744L, new byte[]{ 16, 16, 16, 16, ((byte) (128)) });
        performSizedLongTest(-68449988592L, new byte[]{ 16, 16, 16, 16, ((byte) (240)) });
        performSizedLongTest(-4025479152L, new byte[]{ 16, 16, 16, 16, ((byte) (255)) });
        performSizedLongTest(-4294967296L, new byte[]{ 0, 0, 0, 0, ((byte) (255)) });
        performSizedLongTest(549755813887L, new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (127)) });
        performSizedLongTest(1L, new byte[]{ 1, 0, 0, 0, 0, 0 });
        performSizedLongTest(1168500592656L, new byte[]{ 16, 16, 16, 16, 16, 1 });
        performSizedLongTest(139706965692432L, new byte[]{ 16, 16, 16, 16, 16, 127 });
        performSizedLongTest(-140668499390448L, new byte[]{ 16, 16, 16, 16, 16, ((byte) (128)) });
        performSizedLongTest(-17523197079536L, new byte[]{ 16, 16, 16, 16, 16, ((byte) (240)) });
        performSizedLongTest(-1030522662896L, new byte[]{ 16, 16, 16, 16, 16, ((byte) (255)) });
        performSizedLongTest(-1099511627776L, new byte[]{ 0, 0, 0, 0, 0, ((byte) (255)) });
        performSizedLongTest(140737488355327L, new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (127)) });
        performSizedLongTest(1L, new byte[]{ 1, 0, 0, 0, 0, 0, 0 });
        performSizedLongTest(299136151719952L, new byte[]{ 16, 16, 16, 16, 16, 16, 1 });
        performSizedLongTest(35764983217262608L, new byte[]{ 16, 16, 16, 16, 16, 16, 127 });
        performSizedLongTest(-36011135843954672L, new byte[]{ 16, 16, 16, 16, 16, 16, ((byte) (128)) });
        performSizedLongTest(-4485938452361200L, new byte[]{ 16, 16, 16, 16, 16, 16, ((byte) (240)) });
        performSizedLongTest(-263813801701360L, new byte[]{ 16, 16, 16, 16, 16, 16, ((byte) (255)) });
        performSizedLongTest(-281474976710656L, new byte[]{ 0, 0, 0, 0, 0, 0, ((byte) (255)) });
        performSizedLongTest(36028797018963967L, new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (127)) });
        performSizedLongTest(1L, new byte[]{ 1, 0, 0, 0, 0, 0, 0, 0 });
        performSizedLongTest(76578854840307728L, new byte[]{ 16, 16, 16, 16, 16, 16, 16, 1 });
        performSizedLongTest(9155835703619227664L, new byte[]{ 16, 16, 16, 16, 16, 16, 16, 127 });
        performSizedLongTest(-9218850776052396016L, new byte[]{ 16, 16, 16, 16, 16, 16, 16, ((byte) (128)) });
        performSizedLongTest(-1148400243804467184L, new byte[]{ 16, 16, 16, 16, 16, 16, 16, ((byte) (240)) });
        performSizedLongTest(-67536333235548144L, new byte[]{ 16, 16, 16, 16, 16, 16, 16, ((byte) (255)) });
        performSizedLongTest(-72057594037927936L, new byte[]{ 0, 0, 0, 0, 0, 0, 0, ((byte) (255)) });
        performSizedLongTest(Long.MAX_VALUE, new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (127)) });
        performSizedLongTest(Long.MIN_VALUE, new byte[]{ 0, 0, 0, 0, 0, 0, 0, ((byte) (128)) });
        performSizedLongTest((-1), new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)) });
    }

    @Test
    public void testSizedLongFailure() {
        // wrong size
        performSizedLongFailureTest(new byte[]{  });
        performSizedLongFailureTest(new byte[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0 });
        performSizedLongFailureTest(new byte[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });
        performSizedLongFailureTest(new byte[]{ 18, 52, 86, 18, 52, 86, 120, ((byte) (137)), ((byte) (144)), 1 });
    }
}

