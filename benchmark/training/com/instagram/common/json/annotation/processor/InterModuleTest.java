/**
 * Copyright 2004-present Facebook. All Rights Reserved.
 */
package com.instagram.common.json.annotation.processor;


import com.instagram.common.json.annotation.processor.dependent.SubclassUUT;
import com.instagram.common.json.annotation.processor.dependent.SubclassUUT__JsonHelper;
import com.instagram.common.json.annotation.processor.dependent.SubclassWithAbstractParentUUT;
import com.instagram.common.json.annotation.processor.dependent.SubclassWithAbstractParentUUT__JsonHelper;
import com.instagram.common.json.annotation.processor.dependent.WrapperClassUUT;
import com.instagram.common.json.annotation.processor.dependent.WrapperClassUUT__JsonHelper;
import com.instagram.common.json.annotation.processor.parent.ParentUUT;
import java.io.IOException;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;


/**
 * Try to do stuff across modules.
 */
public class InterModuleTest {
    /**
     * Subclasses a java object in a different class.
     */
    @Test
    public void subclassingTest() throws IOException, JSONException {
        final int intValue = 25;
        final String stringValue = "hello world\r\n\'\"";
        final int subIntValue = 30;
        SubclassUUT uut = new SubclassUUT();
        uut.parentInt = intValue;
        uut.subclassInt = subIntValue;
        uut.parentString = stringValue;
        // serialize it
        String serialized = SubclassUUT__JsonHelper.serializeToJson(uut);
        SubclassUUT parsed = SubclassUUT__JsonHelper.parseFromJson(serialized);
        Assert.assertEquals(uut.parentInt, parsed.parentInt);
        Assert.assertEquals(uut.parentString, parsed.parentString);
        Assert.assertEquals(uut.subclassInt, parsed.subclassInt);
    }

    /**
     * Subclasses an abstract java object in a different class.
     */
    @Test
    public void abstractSubclassingTest() throws IOException, JSONException {
        final int intValue = 25;
        final String stringValue = "hello world\r\n\'\"";
        final int subIntValue = 30;
        SubclassWithAbstractParentUUT uut = new SubclassWithAbstractParentUUT();
        uut.parentInt = intValue;
        uut.subclassInt = subIntValue;
        uut.parentString = stringValue;
        // serialize it
        String serialized = SubclassWithAbstractParentUUT__JsonHelper.serializeToJson(uut);
        SubclassWithAbstractParentUUT parsed = SubclassWithAbstractParentUUT__JsonHelper.parseFromJson(serialized);
        Assert.assertEquals(uut.parentInt, parsed.parentInt);
        Assert.assertEquals(uut.parentString, parsed.parentString);
        Assert.assertEquals(uut.subclassInt, parsed.subclassInt);
    }

    /**
     * Includes a java object in a different class.
     */
    @Test
    public void wrapperTest() throws IOException, JSONException {
        final int intValue = 25;
        final String stringValue = "hello world\r\n\'\"";
        WrapperClassUUT uut = new WrapperClassUUT();
        uut.parent = new ParentUUT();
        uut.parent.parentInt = intValue;
        uut.parent.parentString = stringValue;
        // serialize it
        String serialized = WrapperClassUUT__JsonHelper.serializeToJson(uut);
        WrapperClassUUT parsed = WrapperClassUUT__JsonHelper.parseFromJson(serialized);
        Assert.assertEquals(uut.parent.parentInt, parsed.parent.parentInt);
        Assert.assertEquals(uut.parent.parentString, parsed.parent.parentString);
    }
}

