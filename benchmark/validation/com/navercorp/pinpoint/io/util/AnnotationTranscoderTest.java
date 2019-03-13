/**
 * Copyright 2018 NAVER Corp.
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
package com.navercorp.pinpoint.io.util;


import AnnotationTranscoder.CODE_INT_BOOLEAN_INT_BOOLEAN;
import com.navercorp.pinpoint.common.util.IntBooleanIntBooleanValue;
import com.navercorp.pinpoint.thrift.dto.TIntBooleanIntBooleanValue;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author emeroad
 */
public class AnnotationTranscoderTest {
    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Test
    public void testDecode() throws Exception {
        typeCode("test");
        typeCode("");
        typeCode("adfesdfsesdfsdfserfsdfsdfe");
        typeCode(1);
        typeCode(0);
        typeCode((-1212));
        typeCode(((short) (4)));
        typeCode(((short) (-124)));
        typeCode(2L);
        typeCode((-22342342L));
        typeCode(268435455L);
        typeCode(268435456L);
        typeCode(34359738367L);
        typeCode(34359738368L);
        typeCode(Long.MAX_VALUE);
        typeCode(Long.MIN_VALUE);
        typeCode(3.0F);
        typeCode(123.3F);
        typeCode(4.0);
        typeCode((-124.0));
        typeCode(((byte) (4)));
        typeCode(((byte) (-14)));
        typeCode(true);
        typeCode(false);
        typeCode(null);
        typeUnsupportCode(new Date());
        typeBinaryCode(new byte[]{ 12, 3, 4, 1, 23, 4, 1, 2, 3, 4, 4 });
    }

    @Test
    public void testGetTypeCode() throws Exception {
        int i = 2 << 8;
        logger.debug("{}", i);
        write(i);
        int j = 3 << 8;
        logger.debug("{}", j);
        write(j);
        write(10);
        write(512);
        write(256);
    }

    @Test
    public void testIntString() {
        testIntString((-1), "");
        testIntString(0, "");
        testIntString(1, "");
        testIntString(Integer.MAX_VALUE, "test");
        testIntString(Integer.MIN_VALUE, "test");
        testIntString(2, null);
    }

    @Test
    public void testLongIntIntByteByteString() {
        testLongIntIntByteByteString(999999, 0, 123, ((byte) (99)), ((byte) (1)), "app7");
    }

    @Test
    public void testIntBooleanIntBoolean() {
        AnnotationTranscoder transcoder = new AnnotationTranscoder();
        TIntBooleanIntBooleanValue value = new TIntBooleanIntBooleanValue();
        value.setIntValue1(10);
        value.setBoolValue1(false);
        value.setIntValue2(5000);
        value.setBoolValue2(true);
        byte[] encode = transcoder.encode(value, CODE_INT_BOOLEAN_INT_BOOLEAN);
        IntBooleanIntBooleanValue decode = ((IntBooleanIntBooleanValue) (transcoder.decode(CODE_INT_BOOLEAN_INT_BOOLEAN, encode)));
        Assert.assertEquals(value.getIntValue1(), decode.getIntValue1());
        Assert.assertEquals(false, decode.isBooleanValue1());
        Assert.assertEquals(value.getIntValue2(), decode.getIntValue2());
        Assert.assertEquals(true, decode.isBooleanValue2());
    }
}

