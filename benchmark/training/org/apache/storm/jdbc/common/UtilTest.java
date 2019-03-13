/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.  The ASF licenses this file to you under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package org.apache.storm.jdbc.common;


import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import org.junit.Assert;
import org.junit.Test;


public class UtilTest {
    @Test
    public void testBasic() {
        Assert.assertEquals(String.class, Util.getJavaType(Types.CHAR));
        Assert.assertEquals(String.class, Util.getJavaType(Types.VARCHAR));
        Assert.assertEquals(String.class, Util.getJavaType(Types.LONGVARCHAR));
        Assert.assertEquals(byte[].class, Util.getJavaType(Types.BINARY));
        Assert.assertEquals(byte[].class, Util.getJavaType(Types.VARBINARY));
        Assert.assertEquals(byte[].class, Util.getJavaType(Types.LONGVARBINARY));
        Assert.assertEquals(Boolean.class, Util.getJavaType(Types.BIT));
        Assert.assertEquals(Short.class, Util.getJavaType(Types.TINYINT));
        Assert.assertEquals(Short.class, Util.getJavaType(Types.SMALLINT));
        Assert.assertEquals(Integer.class, Util.getJavaType(Types.INTEGER));
        Assert.assertEquals(Long.class, Util.getJavaType(Types.BIGINT));
        Assert.assertEquals(Float.class, Util.getJavaType(Types.REAL));
        Assert.assertEquals(Double.class, Util.getJavaType(Types.DOUBLE));
        Assert.assertEquals(Double.class, Util.getJavaType(Types.FLOAT));
        Assert.assertEquals(Date.class, Util.getJavaType(Types.DATE));
        Assert.assertEquals(Time.class, Util.getJavaType(Types.TIME));
        Assert.assertEquals(Timestamp.class, Util.getJavaType(Types.TIMESTAMP));
    }

    @Test
    public void testError() {
        // This test is rather ugly, but it is the only way to see if the error messages are working correctly.
        try {
            Util.getJavaType(Types.REF);
            Assert.fail("didn't throw like expected");
        } catch (Exception e) {
            Assert.assertEquals("We do not support tables with SqlType: REF", e.getMessage());
        }
        try {
            Util.getJavaType((-1000));
            Assert.fail("didn't throw like expected");
        } catch (Exception e) {
            Assert.assertEquals("Unknown sqlType -1000", e.getMessage());
        }
    }
}

