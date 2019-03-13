/**
 * Copyright 2004-present Facebook. All Rights Reserved.
 */
package com.instagram.common.json.annotation.processor;


import com.fasterxml.jackson.core.JsonGenerator;
import com.instagram.common.json.annotation.processor.noserializers.NoSerializerGlobalUUT;
import com.instagram.common.json.annotation.processor.noserializers.NoSerializerGlobalUUT__JsonHelper;
import com.instagram.common.json.annotation.processor.uut.NoSerializerClassUUT;
import com.instagram.common.json.annotation.processor.uut.NoSerializerClassUUT__JsonHelper;
import com.instagram.common.json.annotation.processor.uut.SimpleParseUUT;
import com.instagram.common.json.annotation.processor.uut.SimpleParseUUT__JsonHelper;
import java.lang.reflect.Method;
import junit.framework.Assert;
import org.junit.Test;


public class SelectiveGenerationTest {
    @Test
    public void testMissingSerializersGlobalSwitch() throws Exception {
        Method serializeMethod = SimpleParseUUT__JsonHelper.class.getMethod("serializeToJson", SimpleParseUUT.class);
        Assert.assertNotNull(serializeMethod);
        serializeMethod = SimpleParseUUT__JsonHelper.class.getMethod("serializeToJson", JsonGenerator.class, SimpleParseUUT.class, boolean.class);
        Assert.assertNotNull(serializeMethod);
        try {
            serializeMethod = null;
            serializeMethod = NoSerializerGlobalUUT__JsonHelper.class.getMethod("serializeToJson", NoSerializerGlobalUUT.class);
        } catch (NoSuchMethodException ignored) {
        }
        Assert.assertNull(serializeMethod);
        try {
            serializeMethod = null;
            serializeMethod = NoSerializerGlobalUUT__JsonHelper.class.getMethod("serializeToJson", JsonGenerator.class, NoSerializerGlobalUUT.class, boolean.class);
        } catch (NoSuchMethodException ignored) {
        }
        Assert.assertNull(serializeMethod);
    }

    @Test
    public void testMissingSerializersClasSwitch() throws Exception {
        Method serializeMethod = SimpleParseUUT__JsonHelper.class.getMethod("serializeToJson", SimpleParseUUT.class);
        Assert.assertNotNull(serializeMethod);
        serializeMethod = SimpleParseUUT__JsonHelper.class.getMethod("serializeToJson", JsonGenerator.class, SimpleParseUUT.class, boolean.class);
        Assert.assertNotNull(serializeMethod);
        try {
            serializeMethod = null;
            serializeMethod = NoSerializerClassUUT__JsonHelper.class.getMethod("serializeToJson", NoSerializerClassUUT.class);
        } catch (NoSuchMethodException ignored) {
        }
        Assert.assertNull(serializeMethod);
        try {
            serializeMethod = null;
            serializeMethod = NoSerializerClassUUT__JsonHelper.class.getMethod("serializeToJson", JsonGenerator.class, NoSerializerClassUUT.class, boolean.class);
        } catch (NoSuchMethodException ignored) {
        }
        Assert.assertNull(serializeMethod);
    }
}

