/**
 * Copyright 2012-2018 Chronicle Map Contributors
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
package net.openhft.chronicle.map;


import java.io.Externalizable;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class RecursiveRefereneChMapTest {
    public static final String TMP = System.getProperty("java.io.tmpdir");

    @Test
    public void testRecursive() throws IOException {
        File file = new File(((((RecursiveRefereneChMapTest.TMP) + "/test.") + (System.nanoTime())) + ".tmp"));
        file.deleteOnExit();
        Map<String, RecursiveRefereneChMapTest.StupidCycle> map = ChronicleMapBuilder.of(String.class, RecursiveRefereneChMapTest.StupidCycle.class).averageKey("Test").averageValue(new RecursiveRefereneChMapTest.StupidCycle()).entries(64).create();
        map.put("Test", new RecursiveRefereneChMapTest.StupidCycle());
        map.put("Test2", new RecursiveRefereneChMapTest.StupidCycle2());
        RecursiveRefereneChMapTest.StupidCycle cycle = ((RecursiveRefereneChMapTest.StupidCycle) (map.get("Test")));
        Assert.assertSame(cycle, cycle.cycle[0]);
        RecursiveRefereneChMapTest.StupidCycle cycle2 = ((RecursiveRefereneChMapTest.StupidCycle) (map.get("Test2")));
        Assert.assertSame(cycle2, cycle2.cycle[0]);
    }

    public static class StupidCycle implements Serializable {
        int dummy;

        Object[] cycle = new Object[]{ this };
    }

    public static class StupidCycle2 extends RecursiveRefereneChMapTest.StupidCycle implements Externalizable {
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeInt(dummy);
            out.writeObject(cycle);
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            dummy = in.readInt();
            cycle = ((Object[]) (in.readObject()));
        }
    }
}

