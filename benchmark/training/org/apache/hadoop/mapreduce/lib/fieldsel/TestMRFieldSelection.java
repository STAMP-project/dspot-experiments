/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.mapreduce.lib.fieldsel;


import java.text.NumberFormat;
import org.apache.hadoop.fs.Path;
import org.junit.Test;


public class TestMRFieldSelection {
    private static NumberFormat idFormat = NumberFormat.getInstance();

    static {
        TestMRFieldSelection.idFormat.setMinimumIntegerDigits(4);
        TestMRFieldSelection.idFormat.setGroupingUsed(false);
    }

    @Test
    public void testFieldSelection() throws Exception {
        TestMRFieldSelection.launch();
    }

    private static Path testDir = new Path(System.getProperty("test.build.data", "/tmp"), "field");
}

