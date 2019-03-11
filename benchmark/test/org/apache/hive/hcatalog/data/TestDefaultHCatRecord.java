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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.hive.hcatalog.data;


import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.apache.hive.hcatalog.common.HCatException;
import org.apache.hive.hcatalog.data.schema.HCatSchema;
import org.apache.hive.hcatalog.data.schema.HCatSchemaUtils;


public class TestDefaultHCatRecord extends TestCase {
    /**
     * test that we properly serialize/deserialize HCatRecordS
     *
     * @throws IOException
     * 		
     */
    public void testRYW() throws IOException {
        File f = new File("binary.dat");
        f.delete();
        f.createNewFile();
        f.deleteOnExit();
        OutputStream fileOutStream = new FileOutputStream(f);
        DataOutput outStream = new DataOutputStream(fileOutStream);
        HCatRecord[] recs = getHCatRecords();
        for (int i = 0; i < (recs.length); i++) {
            recs[i].write(outStream);
        }
        fileOutStream.flush();
        fileOutStream.close();
        InputStream fInStream = new FileInputStream(f);
        DataInput inpStream = new DataInputStream(fInStream);
        for (int i = 0; i < (recs.length); i++) {
            HCatRecord rec = new DefaultHCatRecord();
            rec.readFields(inpStream);
            StringBuilder msg = new StringBuilder((((((("recs[" + i) + "]='") + (recs[i])) + "' rec='") + rec) + "'"));
            boolean isEqual = HCatDataCheckUtil.recordsEqual(recs[i], rec, msg);
            Assert.assertTrue(msg.toString(), isEqual);
        }
        Assert.assertEquals(fInStream.available(), 0);
        fInStream.close();
    }

    public void testCompareTo() {
        HCatRecord[] recs = getHCatRecords();
        Assert.assertTrue(((HCatDataCheckUtil.compareRecords(recs[0], recs[1])) == 0));
        Assert.assertTrue(((HCatDataCheckUtil.compareRecords(recs[4], recs[5])) == 0));
    }

    public void testEqualsObject() {
        HCatRecord[] recs = getHCatRecords();
        Assert.assertTrue(HCatDataCheckUtil.recordsEqual(recs[0], recs[1]));
        Assert.assertTrue(HCatDataCheckUtil.recordsEqual(recs[4], recs[5]));
    }

    /**
     * Test get and set calls with type
     *
     * @throws HCatException
     * 		
     */
    public void testGetSetByType1() throws HCatException {
        HCatRecord inpRec = getHCatRecords()[0];
        HCatRecord newRec = new DefaultHCatRecord(inpRec.size());
        HCatSchema hsch = HCatSchemaUtils.getHCatSchema("a:tinyint,b:smallint,c:int,d:bigint,e:float,f:double,g:boolean,h:string,i:binary,j:string");
        newRec.setByte("a", hsch, inpRec.getByte("a", hsch));
        newRec.setShort("b", hsch, inpRec.getShort("b", hsch));
        newRec.setInteger("c", hsch, inpRec.getInteger("c", hsch));
        newRec.setLong("d", hsch, inpRec.getLong("d", hsch));
        newRec.setFloat("e", hsch, inpRec.getFloat("e", hsch));
        newRec.setDouble("f", hsch, inpRec.getDouble("f", hsch));
        newRec.setBoolean("g", hsch, inpRec.getBoolean("g", hsch));
        newRec.setString("h", hsch, inpRec.getString("h", hsch));
        newRec.setByteArray("i", hsch, inpRec.getByteArray("i", hsch));
        newRec.setString("j", hsch, inpRec.getString("j", hsch));
        Assert.assertTrue(HCatDataCheckUtil.recordsEqual(newRec, inpRec));
    }

    /**
     * Test get and set calls with type
     *
     * @throws HCatException
     * 		
     */
    public void testGetSetByType2() throws HCatException {
        HCatRecord inpRec = getGetSet2InpRec();
        HCatRecord newRec = new DefaultHCatRecord(inpRec.size());
        HCatSchema hsch = HCatSchemaUtils.getHCatSchema("a:binary,b:map<string,string>,c:array<int>,d:struct<i:int>");
        newRec.setByteArray("a", hsch, inpRec.getByteArray("a", hsch));
        newRec.setMap("b", hsch, inpRec.getMap("b", hsch));
        newRec.setList("c", hsch, inpRec.getList("c", hsch));
        newRec.setStruct("d", hsch, inpRec.getStruct("d", hsch));
        Assert.assertTrue(HCatDataCheckUtil.recordsEqual(newRec, inpRec));
    }

    /**
     * Test type specific get/set methods on HCatRecord types added in Hive 13
     *
     * @throws HCatException
     * 		
     */
    public void testGetSetByType3() throws HCatException {
        HCatRecord inpRec = TestDefaultHCatRecord.getHCat13TypesRecord();
        HCatRecord newRec = new DefaultHCatRecord(inpRec.size());
        HCatSchema hsch = HCatSchemaUtils.getHCatSchema("a:decimal(5,2),b:char(10),c:varchar(20),d:date,e:timestamp");
        newRec.setDecimal("a", hsch, inpRec.getDecimal("a", hsch));
        newRec.setChar("b", hsch, inpRec.getChar("b", hsch));
        newRec.setVarchar("c", hsch, inpRec.getVarchar("c", hsch));
        newRec.setDate("d", hsch, inpRec.getDate("d", hsch));
        newRec.setTimestamp("e", hsch, inpRec.getTimestamp("e", hsch));
    }
}

