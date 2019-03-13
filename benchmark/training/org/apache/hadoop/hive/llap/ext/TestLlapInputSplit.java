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
package org.apache.hadoop.hive.llap.ext;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import org.apache.hadoop.hive.llap.FieldDesc;
import org.apache.hadoop.hive.llap.LlapInputSplit;
import org.apache.hadoop.hive.llap.Schema;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;
import org.apache.hadoop.mapred.SplitLocationInfo;
import org.junit.Assert;
import org.junit.Test;


public class TestLlapInputSplit {
    @Test
    public void testWritable() throws Exception {
        int splitNum = 88;
        byte[] planBytes = "0123456789987654321".getBytes();
        byte[] fragmentBytes = "abcdefghijklmnopqrstuvwxyz".getBytes();
        SplitLocationInfo[] locations = new SplitLocationInfo[]{ new SplitLocationInfo("location1", false), new SplitLocationInfo("location2", false) };
        ArrayList<FieldDesc> colDescs = new ArrayList<FieldDesc>();
        colDescs.add(new FieldDesc("col1", TypeInfoFactory.stringTypeInfo));
        colDescs.add(new FieldDesc("col2", TypeInfoFactory.intTypeInfo));
        Schema schema = new Schema(colDescs);
        byte[] tokenBytes = new byte[]{ 1 };
        LlapInputSplit split1 = new LlapInputSplit(splitNum, planBytes, fragmentBytes, null, locations, schema, "hive", tokenBytes);
        ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
        DataOutputStream dataOut = new DataOutputStream(byteOutStream);
        split1.write(dataOut);
        ByteArrayInputStream byteInStream = new ByteArrayInputStream(byteOutStream.toByteArray());
        DataInputStream dataIn = new DataInputStream(byteInStream);
        LlapInputSplit split2 = new LlapInputSplit();
        split2.readFields(dataIn);
        // Did we read all the data?
        Assert.assertEquals(0, byteInStream.available());
        TestLlapInputSplit.checkLlapSplits(split1, split2);
    }
}

