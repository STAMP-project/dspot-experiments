/**
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.nestedproperties.simple;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.ap.test.nestedproperties.simple._target.TargetObject;
import org.mapstruct.ap.test.nestedproperties.simple.source.SourceProps;
import org.mapstruct.ap.test.nestedproperties.simple.source.SourceRoot;
import org.mapstruct.ap.testutil.IssueKey;
import org.mapstruct.ap.testutil.WithClasses;
import org.mapstruct.ap.testutil.runner.AnnotationProcessorTestRunner;


/**
 *
 *
 * @author Sebastian Hasait
 */
@WithClasses({ SourceRoot.class, SourceProps.class, TargetObject.class })
@IssueKey("407")
@RunWith(AnnotationProcessorTestRunner.class)
public class SimpleNestedPropertiesTest {
    @Test
    @WithClasses({ SimpleMapper.class })
    public void testNull() {
        TargetObject targetObject = SimpleMapper.MAPPER.toTargetObject(null);
        Assert.assertNull(targetObject);
    }

    @Test
    @WithClasses({ SimpleMapper.class })
    public void testViaNull() {
        SourceRoot sourceRoot = new SourceRoot();
        // sourceRoot.getProps() is null
        TargetObject targetObject = SimpleMapper.MAPPER.toTargetObject(sourceRoot);
        Assert.assertEquals(0L, targetObject.getPublicLongValue());
        Assert.assertEquals(0L, targetObject.getLongValue());
        Assert.assertEquals(0, targetObject.getIntValue());
        Assert.assertEquals(0.0, targetObject.getDoubleValue(), 0.01);
        Assert.assertEquals(0.0F, targetObject.getFloatValue(), 0.01F);
        Assert.assertEquals(0, targetObject.getShortValue());
        Assert.assertEquals(0, targetObject.getCharValue());
        Assert.assertEquals(0, targetObject.getByteValue());
        Assert.assertFalse(targetObject.isBooleanValue());
        Assert.assertNull(targetObject.getByteArray());
        Assert.assertNull(targetObject.getStringValue());
    }

    @Test
    @WithClasses({ SimpleMapper.class })
    public void testFilled() {
        SourceRoot sourceRoot = new SourceRoot();
        SourceProps sourceProps = new SourceProps();
        sourceRoot.setProps(sourceProps);
        sourceProps.publicLongValue = Long.MAX_VALUE;
        sourceProps.setLongValue(Long.MAX_VALUE);
        sourceProps.setIntValue(Integer.MAX_VALUE);
        sourceProps.setDoubleValue(Double.MAX_VALUE);
        sourceProps.setFloatValue(Float.MAX_VALUE);
        sourceProps.setShortValue(Short.MAX_VALUE);
        sourceProps.setCharValue(Character.MAX_VALUE);
        sourceProps.setByteValue(Byte.MAX_VALUE);
        sourceProps.setBooleanValue(true);
        String stringValue = "lorem ipsum";
        sourceProps.setByteArray(stringValue.getBytes());
        sourceProps.setStringValue(stringValue);
        TargetObject targetObject = SimpleMapper.MAPPER.toTargetObject(sourceRoot);
        Assert.assertEquals(Long.MAX_VALUE, targetObject.getPublicLongValue());
        Assert.assertEquals(Long.MAX_VALUE, targetObject.getLongValue());
        Assert.assertEquals(Integer.MAX_VALUE, targetObject.getIntValue());
        Assert.assertEquals(Double.MAX_VALUE, targetObject.getDoubleValue(), 0.01);
        Assert.assertEquals(Float.MAX_VALUE, targetObject.getFloatValue(), 0.01F);
        Assert.assertEquals(Short.MAX_VALUE, targetObject.getShortValue());
        Assert.assertEquals(Character.MAX_VALUE, targetObject.getCharValue());
        Assert.assertEquals(Byte.MAX_VALUE, targetObject.getByteValue());
        Assert.assertTrue(targetObject.isBooleanValue());
        Assert.assertArrayEquals(stringValue.getBytes(), targetObject.getByteArray());
        Assert.assertEquals(stringValue, targetObject.getStringValue());
    }
}

