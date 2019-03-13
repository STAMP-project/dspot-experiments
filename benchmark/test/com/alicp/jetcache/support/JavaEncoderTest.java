package com.alicp.jetcache.support;


import JavaValueEncoder.INSTANCE;
import KryoValueEncoder.IDENTITY_NUMBER;
import java.util.function.Function;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static JavaValueEncoder.INSTANCE;


/**
 * Created on 2016/10/8.
 *
 * @author <a href="mailto:areyouok@gmail.com">huangli</a>
 */
public class JavaEncoderTest extends AbstractEncoderTest {
    @Test
    public void test() {
        encoder = INSTANCE;
        decoder = JavaValueDecoder.INSTANCE;
        baseTest();
        encoder = new JavaValueEncoder(false);
        decoder = new JavaValueDecoder(false);
        baseTest();
    }

    @Test
    public void compoundTest() {
        encoder = ( p) -> INSTANCE.apply(INSTANCE.apply(p));
        decoder = ( p) -> JavaValueDecoder.INSTANCE.apply(((byte[]) (JavaValueDecoder.INSTANCE.apply(p))));
        baseTest();
        encoder = ( p) -> INSTANCE.apply(KryoValueEncoder.INSTANCE.apply(p));
        decoder = ( p) -> KryoValueDecoder.INSTANCE.apply(((byte[]) (JavaValueDecoder.INSTANCE.apply(p))));
        baseTest();
    }

    @Test
    public void compatibleTest() {
        encoder = INSTANCE;
        decoder = KryoValueDecoder.INSTANCE;
        baseTest();
    }

    @Test
    public void errorTest() {
        encoder = INSTANCE;
        decoder = JavaValueDecoder.INSTANCE;
        byte[] bytes = encoder.apply("12345");
        bytes[0] = 0;
        Assertions.assertThrows(CacheEncodeException.class, () -> decoder.apply(bytes));
        ((AbstractValueEncoder) (encoder)).writeHeader(bytes, IDENTITY_NUMBER);
        Assertions.assertThrows(CacheEncodeException.class, () -> decoder.apply(bytes));
        encoder = INSTANCE;
        decoder = new JavaValueDecoder(false);
        Assertions.assertThrows(CacheEncodeException.class, () -> decoder.apply(bytes));
        encoder = new JavaValueEncoder(false);
        decoder = JavaValueDecoder.INSTANCE;
        Assertions.assertThrows(CacheEncodeException.class, () -> decoder.apply(bytes));
    }

    @Test
    public void gcTest() {
        encoder = INSTANCE;
        decoder = JavaValueDecoder.INSTANCE;
        super.gcTest();
    }
}

