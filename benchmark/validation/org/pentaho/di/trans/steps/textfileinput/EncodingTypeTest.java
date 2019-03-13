/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.trans.steps.textfileinput;


import EncodingType.DOUBLE_BIG_ENDIAN;
import EncodingType.DOUBLE_LITTLE_ENDIAN;
import EncodingType.SINGLE;
import org.junit.Assert;
import org.junit.Test;


/**
 * User: Dzmitry Stsiapanau Date: 3/11/14 Time: 11:44 AM
 *
 * @deprecated replaced by implementation in the ...steps.fileinput.text package
 */
public class EncodingTypeTest {
    @Test
    public void testIsReturn() throws Exception {
        int lineFeed = '\n';
        int carriageReturn = '\r';
        Assert.assertTrue("SINGLE.isLineFeed is not line feed", SINGLE.isLinefeed(lineFeed));
        Assert.assertTrue("DOUBLE_BIG_ENDIAN is not line feed", DOUBLE_BIG_ENDIAN.isLinefeed(lineFeed));
        Assert.assertTrue("DOUBLE_LITTLE_ENDIAN.isLineFeed is not line feed", DOUBLE_LITTLE_ENDIAN.isLinefeed(lineFeed));
        Assert.assertFalse("SINGLE.isLineFeed is carriage return", SINGLE.isLinefeed(carriageReturn));
        Assert.assertFalse("DOUBLE_BIG_ENDIAN.isLineFeed is carriage return", DOUBLE_BIG_ENDIAN.isLinefeed(carriageReturn));
        Assert.assertFalse("DOUBLE_LITTLE_ENDIAN.isLineFeed is carriage return", DOUBLE_LITTLE_ENDIAN.isLinefeed(carriageReturn));
    }

    @Test
    public void testIsLinefeed() throws Exception {
        int lineFeed = '\n';
        int carriageReturn = '\r';
        Assert.assertFalse("SINGLE.isReturn is line feed", SINGLE.isReturn(lineFeed));
        Assert.assertFalse("DOUBLE_BIG_ENDIAN.isReturn is line feed", DOUBLE_BIG_ENDIAN.isReturn(lineFeed));
        Assert.assertFalse("DOUBLE_LITTLE_ENDIAN.isReturn is line feed", DOUBLE_LITTLE_ENDIAN.isReturn(lineFeed));
        Assert.assertTrue("SINGLE.isReturn is not carriage return", SINGLE.isReturn(carriageReturn));
        Assert.assertTrue("DOUBLE_BIG_ENDIAN.isReturn is not carriage return", DOUBLE_BIG_ENDIAN.isReturn(carriageReturn));
        Assert.assertTrue("DOUBLE_LITTLE_ENDIAN.isReturn is not carriage return", DOUBLE_LITTLE_ENDIAN.isReturn(carriageReturn));
    }
}

