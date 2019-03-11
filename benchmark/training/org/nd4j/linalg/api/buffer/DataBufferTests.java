package org.nd4j.linalg.api.buffer;


import AllocationPolicy.STRICT;
import DataType.DOUBLE;
import DataType.FLOAT;
import DataType.INT;
import DataType.LONG;
import LearningPolicy.NONE;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.memory.MemoryWorkspace;
import org.nd4j.linalg.api.memory.conf.WorkspaceConfiguration;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;

import static DataType.COMPRESSED;
import static DataType.UNKNOWN;
import static DataType.UTF8;


@Slf4j
@RunWith(Parameterized.class)
public class DataBufferTests extends BaseNd4jTest {
    public DataBufferTests(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testNoArgCreateBufferFromArray() {
        // Tests here:
        // 1. Create from JVM array
        // 2. Create from JVM array with offset -> does this even make sense?
        // 3. Create detached buffer
        WorkspaceConfiguration initialConfig = WorkspaceConfiguration.builder().initialSize(((10 * 1024L) * 1024L)).policyAllocation(STRICT).policyLearning(NONE).build();
        MemoryWorkspace workspace = Nd4j.getWorkspaceManager().createNewWorkspace(initialConfig, "WorkspaceId");
        for (boolean useWs : new boolean[]{ false, true }) {
            try (MemoryWorkspace ws = (useWs) ? workspace.notifyScopeEntered() : null) {
                // Float
                DataBuffer f = Nd4j.createBuffer(new float[]{ 1, 2, 3 });
                DataBufferTests.checkTypes(FLOAT, f, 3);
                Assert.assertEquals(useWs, f.isAttached());
                DataBufferTests.testDBOps(f);
                f = Nd4j.createBuffer(new float[]{ 1, 2, 3 }, 0);
                DataBufferTests.checkTypes(FLOAT, f, 3);
                Assert.assertEquals(useWs, f.isAttached());
                DataBufferTests.testDBOps(f);
                f = Nd4j.createBufferDetached(new float[]{ 1, 2, 3 });
                DataBufferTests.checkTypes(FLOAT, f, 3);
                Assert.assertFalse(f.isAttached());
                DataBufferTests.testDBOps(f);
                // Double
                DataBuffer d = Nd4j.createBuffer(new double[]{ 1, 2, 3 });
                DataBufferTests.checkTypes(DOUBLE, d, 3);
                Assert.assertEquals(useWs, d.isAttached());
                DataBufferTests.testDBOps(d);
                d = Nd4j.createBuffer(new double[]{ 1, 2, 3 }, 0);
                DataBufferTests.checkTypes(DOUBLE, d, 3);
                Assert.assertEquals(useWs, d.isAttached());
                DataBufferTests.testDBOps(d);
                d = Nd4j.createBufferDetached(new double[]{ 1, 2, 3 });
                DataBufferTests.checkTypes(DOUBLE, d, 3);
                Assert.assertFalse(d.isAttached());
                DataBufferTests.testDBOps(d);
                // Int
                DataBuffer i = Nd4j.createBuffer(new int[]{ 1, 2, 3 });
                DataBufferTests.checkTypes(INT, i, 3);
                Assert.assertEquals(useWs, i.isAttached());
                DataBufferTests.testDBOps(i);
                i = Nd4j.createBuffer(new int[]{ 1, 2, 3 }, 0);
                DataBufferTests.checkTypes(INT, i, 3);
                Assert.assertEquals(useWs, i.isAttached());
                DataBufferTests.testDBOps(i);
                i = Nd4j.createBufferDetached(new int[]{ 1, 2, 3 });
                DataBufferTests.checkTypes(INT, i, 3);
                Assert.assertFalse(i.isAttached());
                DataBufferTests.testDBOps(i);
                // Long
                DataBuffer l = Nd4j.createBuffer(new long[]{ 1, 2, 3 });
                DataBufferTests.checkTypes(LONG, l, 3);
                Assert.assertEquals(useWs, l.isAttached());
                DataBufferTests.testDBOps(l);
                l = Nd4j.createBuffer(new long[]{ 1, 2, 3 });
                DataBufferTests.checkTypes(LONG, l, 3);
                Assert.assertEquals(useWs, l.isAttached());
                DataBufferTests.testDBOps(l);
                l = Nd4j.createBufferDetached(new long[]{ 1, 2, 3 });
                DataBufferTests.checkTypes(LONG, l, 3);
                Assert.assertFalse(l.isAttached());
                DataBufferTests.testDBOps(l);
                // byte
                // DataBuffer b = Nd4j.createBuffer(new byte[]{1, 2, 3});
                // checkTypes(DataType.BYTE, b, 3);
                // testDBOps(b);
                // 
                // b = Nd4j.createBuffer(new byte[]{1, 2, 3}, 0);
                // checkTypes(DataType.BYTE, b, 3);
                // testDBOps(b);
                // 
                // b = Nd4j.createBufferDetached(new byte[]{1,2,3});
                // checkTypes(DataType.BYTE, b, 3);
                // testDBOps(b);
                // short
                // TODO
            }
        }
    }

    @Test
    public void testCreateTypedBuffer() {
        WorkspaceConfiguration initialConfig = WorkspaceConfiguration.builder().initialSize(((10 * 1024L) * 1024L)).policyAllocation(STRICT).policyLearning(NONE).build();
        MemoryWorkspace workspace = Nd4j.getWorkspaceManager().createNewWorkspace(initialConfig, "WorkspaceId");
        for (String sourceType : new String[]{ "int", "long", "float", "double", "short", "byte", "boolean" }) {
            for (DataType dt : DataType.values()) {
                if (((dt == (UTF8)) || (dt == (COMPRESSED))) || (dt == (UNKNOWN))) {
                    continue;
                }
                for (boolean useWs : new boolean[]{ false, true }) {
                    try (MemoryWorkspace ws = (useWs) ? workspace.notifyScopeEntered() : null) {
                        DataBuffer db1;
                        DataBuffer db2;
                        switch (sourceType) {
                            case "int" :
                                db1 = Nd4j.createTypedBuffer(new int[]{ 1, 2, 3 }, dt);
                                db2 = Nd4j.createTypedBufferDetached(new int[]{ 1, 2, 3 }, dt);
                                break;
                            case "long" :
                                db1 = Nd4j.createTypedBuffer(new long[]{ 1, 2, 3 }, dt);
                                db2 = Nd4j.createTypedBufferDetached(new long[]{ 1, 2, 3 }, dt);
                                break;
                            case "float" :
                                db1 = Nd4j.createTypedBuffer(new float[]{ 1, 2, 3 }, dt);
                                db2 = Nd4j.createTypedBufferDetached(new float[]{ 1, 2, 3 }, dt);
                                break;
                            case "double" :
                                db1 = Nd4j.createTypedBuffer(new double[]{ 1, 2, 3 }, dt);
                                db2 = Nd4j.createTypedBufferDetached(new double[]{ 1, 2, 3 }, dt);
                                break;
                            case "short" :
                                db1 = Nd4j.createTypedBuffer(new short[]{ 1, 2, 3 }, dt);
                                db2 = Nd4j.createTypedBufferDetached(new short[]{ 1, 2, 3 }, dt);
                                break;
                            case "byte" :
                                db1 = Nd4j.createTypedBuffer(new byte[]{ 1, 2, 3 }, dt);
                                db2 = Nd4j.createTypedBufferDetached(new byte[]{ 1, 2, 3 }, dt);
                                break;
                            case "boolean" :
                                db1 = Nd4j.createTypedBuffer(new boolean[]{ true, false, true }, dt);
                                db2 = Nd4j.createTypedBufferDetached(new boolean[]{ true, false, true }, dt);
                                break;
                            default :
                                throw new RuntimeException();
                        }
                        DataBufferTests.checkTypes(dt, db1, 3);
                        DataBufferTests.checkTypes(dt, db2, 3);
                        Assert.assertEquals(useWs, db1.isAttached());
                        Assert.assertFalse(db2.isAttached());
                        if (!(sourceType.equals("boolean"))) {
                            log.info("Testing source [{}]; target: [{}]", sourceType, dt);
                            DataBufferTests.testDBOps(db1);
                            DataBufferTests.testDBOps(db2);
                        }
                    }
                }
            }
        }
    }
}

