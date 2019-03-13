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


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.hive.hcatalog.data.transfer.ReaderContext;
import org.apache.hive.hcatalog.data.transfer.WriterContext;
import org.apache.hive.hcatalog.mapreduce.HCatBaseTest;
import org.junit.Test;


public class TestReaderWriter extends HCatBaseTest {
    @Test
    public void test() throws IOException, ClassNotFoundException, Exception {
        driver.run("drop table mytbl");
        driver.run("create table mytbl (a string, b int)");
        Iterator<Map.Entry<String, String>> itr = hiveConf.iterator();
        Map<String, String> map = new HashMap<String, String>();
        while (itr.hasNext()) {
            Map.Entry<String, String> kv = itr.next();
            map.put(kv.getKey(), kv.getValue());
        } 
        WriterContext cntxt = runsInMaster(map);
        File writeCntxtFile = File.createTempFile("hcat-write", "temp");
        writeCntxtFile.deleteOnExit();
        // Serialize context.
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(writeCntxtFile));
        oos.writeObject(cntxt);
        oos.flush();
        oos.close();
        // Now, deserialize it.
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(writeCntxtFile));
        cntxt = ((WriterContext) (ois.readObject()));
        ois.close();
        runsInSlave(cntxt);
        commit(map, true, cntxt);
        ReaderContext readCntxt = runsInMaster(map, false);
        File readCntxtFile = File.createTempFile("hcat-read", "temp");
        readCntxtFile.deleteOnExit();
        oos = new ObjectOutputStream(new FileOutputStream(readCntxtFile));
        oos.writeObject(readCntxt);
        oos.flush();
        oos.close();
        ois = new ObjectInputStream(new FileInputStream(readCntxtFile));
        readCntxt = ((ReaderContext) (ois.readObject()));
        ois.close();
        for (int i = 0; i < (readCntxt.numSplits()); i++) {
            runsInSlave(readCntxt, i);
        }
    }

    private static class HCatRecordItr implements Iterator<HCatRecord> {
        int i = 0;

        @Override
        public boolean hasNext() {
            return ((i)++) < 100 ? true : false;
        }

        @Override
        public HCatRecord next() {
            return TestReaderWriter.getRecord(i);
        }

        @Override
        public void remove() {
            throw new RuntimeException();
        }
    }
}

