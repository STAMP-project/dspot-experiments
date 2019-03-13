package org.nd4j.linalg.nativ;


import Op.Type;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.Test;
import org.nd4j.autodiff.samediff.SameDiff;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.primitives.Pair;
import org.nd4j.nativeblas.NativeOpsHolder;


/**
 * This unit contains tests for c++ --- java ops mapping
 *
 * @author raver119@gmail.com
 */
@Slf4j
public class OpsMappingTests {
    @Test
    public void testCustomOpsMapping() {
        Nd4j.create(1);
    }

    @Test
    public void testLegacyOpsMapping() {
        Nd4j.create(1);
        val str = NativeOpsHolder.getInstance().getDeviceNativeOps().getAllOperations().replaceAll("simdOps::", "").replaceAll("randomOps::", "");
        val missing = new ArrayList<String>();
        // parsing individual groups first
        val groups = str.split(">>");
        for (val group : groups) {
            val line = group.split(" ");
            val bt = Integer.valueOf(line[0]).byteValue();
            val ops = line[1].split("<<");
            val type = SameDiff.getTypeFromByte(bt);
            val list = getOperations(type);
            for (val op : ops) {
                val args = op.split(":");
                val hash = Long.valueOf(args[0]).longValue();
                val opNum = Long.valueOf(args[1]).longValue();
                val name = args[2];
                // log.info("group: {}; hash: {}; name: {};", SameDiff.getTypeFromByte(bt), hash, name);
                val needle = new OpsMappingTests.Operation((type == (Type.CUSTOM) ? -1 : opNum), name.toLowerCase());
                if (!(opMapped(list, needle)))
                    missing.add((((type.toString()) + " ") + name));

            }
        }
        if ((missing.size()) > 0) {
            log.info("{} ops missing!", missing.size());
            log.info("{}", missing);
            // assertTrue(false);
        }
    }

    protected static class Operation extends Pair<Long, String> {
        protected Operation(Long opNum, String name) {
            super(opNum, name);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof OpsMappingTests.Operation))
                return false;

            OpsMappingTests.Operation op = ((OpsMappingTests.Operation) (o));
            return op.key.equals(this.key);
        }
    }
}

