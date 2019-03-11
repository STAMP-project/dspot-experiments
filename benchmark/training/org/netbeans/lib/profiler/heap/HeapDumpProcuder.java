/**
 * Copyright 2014 Alexey Ragozin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.netbeans.lib.profiler.heap;


import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;


public class HeapDumpProcuder {
    private static int PID;

    static {
        String pid = ManagementFactory.getRuntimeMXBean().getName();
        HeapDumpProcuder.PID = Integer.valueOf(pid.substring(0, pid.indexOf('@')));
    }

    private static String HEAP_DUMP_PATH = "target/dump/testdump.hprof";

    private static String HEAP_DUMP_GZ_PATH = "target/dump/testdump.hprof.gz";

    public interface HotSpotDiagnostic {
        public void dumpHeap(String fileName, boolean live);
    }

    // Called manually from IDE to clean cached dump
    @Test
    public void cleanDump() {
        new File(HeapDumpProcuder.HEAP_DUMP_PATH).delete();
        new File(HeapDumpProcuder.HEAP_DUMP_GZ_PATH).delete();
    }

    static HeapDumpProcuder.Holder holder = new HeapDumpProcuder.Holder();

    static class Holder {
        List<DummyA> dummyA = new ArrayList<DummyA>();

        List<DummyB> dummyB = new ArrayList<DummyB>();

        DummyC dummyC = new DummyC();

        DummyD dummyD = new DummyD();

        {
            dummyD.nestedArray = new DummyD.Sub[2];
            dummyD.nestedArray[1] = new DummyD.Sub();
            dummyD.nestedArray[1].value = "somevalue";
        }

        DummyP[] dummyP = new DummyP[]{ new DummyP("A", "X"), new DummyP("B", null), new DummyP("C", 1), new DummyP("D", null), new DummyP("E", new Object[0]) };

        Object[] dummyN;

        {
            DummyN a = new DummyN("dummyA");
            DummyN b = new DummyN("dummyB");
            dummyN = new Object[]{ a.newInner("A.1"), a.newInner("A.2"), a.newInner("A.3"), b.newInner("B.1"), a.newInner("A.4") };
        }

        DummyS dummyS = new DummyS();

        public void initTestHeap() {
            for (int i = 0; i != 50; ++i) {
                dummyA.add(new DummyA());
            }
            for (int i = 0; i != 50; ++i) {
                DummyB dmb = new DummyB();
                dmb.seqNo = String.valueOf(i);
                for (int j = 0; j != i; ++j) {
                    dmb.list.add(String.valueOf(j));
                    dmb.map.put(("k" + (String.valueOf(j))), ("v" + (String.valueOf(j))));
                }
                dummyB.add(dmb);
            }
        }
    }
}

