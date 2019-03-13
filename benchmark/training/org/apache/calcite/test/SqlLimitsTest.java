/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.calcite.test;


import SqlTypeName.Limit.OVERFLOW;
import SqlTypeName.Limit.UNDERFLOW;
import SqlTypeName.Limit.ZERO;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeSystem;
import org.junit.Test;


/**
 * Unit test for SQL limits.
 */
public class SqlLimitsTest {
    // ~ Static fields/initializers ---------------------------------------------
    // ~ Constructors -----------------------------------------------------------
    public SqlLimitsTest() {
    }

    @Test
    public void testPrintLimits() {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        final List<RelDataType> types = SqlLimitsTest.getTypes(new org.apache.calcite.jdbc.JavaTypeFactoryImpl(RelDataTypeSystem.DEFAULT));
        for (RelDataType type : types) {
            pw.println(type.toString());
            printLimit(pw, "  min - epsilon:          ", type, false, OVERFLOW, true);
            printLimit(pw, "  min:                    ", type, false, OVERFLOW, false);
            printLimit(pw, "  zero - delta:           ", type, false, UNDERFLOW, false);
            printLimit(pw, "  zero - delta + epsilon: ", type, false, UNDERFLOW, true);
            printLimit(pw, "  zero:                   ", type, false, ZERO, false);
            printLimit(pw, "  zero + delta - epsilon: ", type, true, UNDERFLOW, true);
            printLimit(pw, "  zero + delta:           ", type, true, UNDERFLOW, false);
            printLimit(pw, "  max:                    ", type, true, OVERFLOW, false);
            printLimit(pw, "  max + epsilon:          ", type, true, OVERFLOW, true);
            pw.println();
        }
        pw.flush();
        getDiffRepos().assertEquals("output", "${output}", sw.toString());
    }
}

/**
 * End SqlLimitsTest.java
 */
