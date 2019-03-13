/**
 * Protocol Buffers - Google's data interchange format
 */
/**
 * Copyright 2008 Google Inc.  All rights reserved.
 */
/**
 * https://developers.google.com/protocol-buffers/
 */
/**
 *
 */
/**
 * Redistribution and use in source and binary forms, with or without
 */
/**
 * modification, are permitted provided that the following conditions are
 */
/**
 * met:
 */
/**
 *
 */
/**
 * * Redistributions of source code must retain the above copyright
 */
/**
 * notice, this list of conditions and the following disclaimer.
 */
/**
 * * Redistributions in binary form must reproduce the above
 */
/**
 * copyright notice, this list of conditions and the following disclaimer
 */
/**
 * in the documentation and/or other materials provided with the
 */
/**
 * distribution.
 */
/**
 * * Neither the name of Google Inc. nor the names of its
 */
/**
 * contributors may be used to endorse or promote products derived from
 */
/**
 * this software without specific prior written permission.
 */
/**
 *
 */
/**
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 */
/**
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 */
/**
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 */
/**
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 */
/**
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 */
/**
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 */
/**
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 */
/**
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 */
/**
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 */
/**
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 */
/**
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.google.protobuf;


import ByteString.EMPTY;
import TestBadIdentifiersProto.Deprecated;
import TestBadIdentifiersProto.Override;
import TestBadIdentifiersProto.TestConflictingFieldNames;
import TestBadIdentifiersProto.TestConflictingFieldNames.int64FieldCount;
import TestBadIdentifiersProto.TestConflictingFieldNames.int64FieldList;
import junit.framework.TestCase;


/**
 * Tests that proto2 api generation doesn't cause compile errors when compiling protocol buffers
 * that have names that would otherwise conflict if not fully qualified (like @Deprecated
 * and @Override).
 *
 * <p>Forked from {@link TestBadIdentifiers}.
 *
 * @author jonp@google.com (Jon Perlow)
 */
public final class TestBadIdentifiersLite extends TestCase {
    public void testCompilation() {
        // If this compiles, it means the generation was correct.
        Deprecated.newBuilder();
        Override.newBuilder();
    }

    public void testConflictingFieldNames() throws Exception {
        TestBadIdentifiersProto.TestConflictingFieldNames message = TestConflictingFieldNames.getDefaultInstance();
        // Make sure generated accessors are properly named.
        TestCase.assertEquals(0, message.getInt32Field1Count());
        TestCase.assertEquals(0, message.getEnumField2Count());
        TestCase.assertEquals(0, message.getStringField3Count());
        TestCase.assertEquals(0, message.getBytesField4Count());
        TestCase.assertEquals(0, message.getMessageField5Count());
        TestCase.assertEquals(0, message.getInt32FieldCount11());
        TestCase.assertEquals(0, message.getEnumFieldCount12().getNumber());
        TestCase.assertEquals("", message.getStringFieldCount13());
        TestCase.assertEquals(EMPTY, message.getBytesFieldCount14());
        TestCase.assertEquals(0, message.getMessageFieldCount15().getSerializedSize());
        TestCase.assertEquals(0, message.getInt32Field21Count());
        TestCase.assertEquals(0, message.getEnumField22Count());
        TestCase.assertEquals(0, message.getStringField23Count());
        TestCase.assertEquals(0, message.getBytesField24Count());
        TestCase.assertEquals(0, message.getMessageField25Count());
        TestCase.assertEquals(0, message.getInt32Field1List().size());
        TestCase.assertEquals(0, message.getInt32FieldList31());
        TestCase.assertEquals(0, message.getInt64FieldCount());
        TestCase.assertEquals(0L, message.getExtension(int64FieldCount).longValue());
        TestCase.assertEquals(0L, message.getExtension(int64FieldList).longValue());
    }
}

