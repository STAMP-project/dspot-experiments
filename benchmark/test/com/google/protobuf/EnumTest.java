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


import ForeignEnum.FOREIGN_BAR;
import ForeignEnumLite.FOREIGN_LITE_BAR;
import TestAllTypes.OneofFieldCase;
import TestAllTypes.OneofFieldCase.ONEOF_NESTED_MESSAGE;
import com.google.protobuf.UnittestLite.ForeignEnumLite;
import com.google.protobuf.UnittestLite.TestAllTypesLite;
import junit.framework.TestCase;
import protobuf_unittest.UnittestProto.ForeignEnum;
import protobuf_unittest.UnittestProto.TestAllTypes;


public class EnumTest extends TestCase {
    public void testForNumber() {
        ForeignEnum e = ForeignEnum.forNumber(FOREIGN_BAR.getNumber());
        TestCase.assertEquals(FOREIGN_BAR, e);
        e = ForeignEnum.forNumber(1000);
        TestCase.assertEquals(null, e);
    }

    public void testForNumber_oneof() {
        TestAllTypes.OneofFieldCase e = OneofFieldCase.forNumber(ONEOF_NESTED_MESSAGE.getNumber());
        TestCase.assertEquals(ONEOF_NESTED_MESSAGE, e);
        e = OneofFieldCase.forNumber(1000);
        TestCase.assertEquals(null, e);
    }

    public void testForNumberLite() {
        ForeignEnumLite e = ForeignEnumLite.forNumber(FOREIGN_LITE_BAR.getNumber());
        TestCase.assertEquals(FOREIGN_LITE_BAR, e);
        e = ForeignEnumLite.forNumber(1000);
        TestCase.assertEquals(null, e);
    }

    public void testForNumberLite_oneof() {
        TestAllTypesLite.OneofFieldCase e = TestAllTypesLite.OneofFieldCase.forNumber(TestAllTypesLite.OneofFieldCase.ONEOF_NESTED_MESSAGE.getNumber());
        TestCase.assertEquals(TestAllTypesLite.OneofFieldCase.ONEOF_NESTED_MESSAGE, e);
        e = TestAllTypesLite.OneofFieldCase.forNumber(1000);
        TestCase.assertEquals(null, e);
    }
}

