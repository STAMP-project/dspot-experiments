package org.nd4j.autodiff.samediff;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.nd4j.autodiff.functions.DifferentialFunction;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;


@Slf4j
public class FlatBufferSerdeTest {
    @Rule
    public TemporaryFolder testDir = new TemporaryFolder();

    @Test
    public void testBasic() throws Exception {
        SameDiff sd = SameDiff.create();
        INDArray arr = Nd4j.linspace(1, 12, 12).reshape(3, 4);
        SDVariable in = sd.placeHolder("in", arr.dataType(), arr.shape());
        SDVariable tanh = sd.nn().tanh("out", in);
        ByteBuffer bb = sd.asFlatBuffers();
        File f = testDir.newFile();
        f.delete();
        try (FileChannel fc = new FileOutputStream(f, false).getChannel()) {
            fc.write(bb);
        }
        byte[] bytes;
        try (InputStream is = new BufferedInputStream(new FileInputStream(f))) {
            bytes = IOUtils.toByteArray(is);
        }
        ByteBuffer bbIn = ByteBuffer.wrap(bytes);
        FlatGraph fg = FlatGraph.getRootAsFlatGraph(bbIn);
        int numNodes = fg.nodesLength();
        int numVars = fg.variablesLength();
        List<FlatNode> nodes = new ArrayList<>(numNodes);
        for (int i = 0; i < numNodes; i++) {
            nodes.add(fg.nodes(i));
        }
        List<FlatVariable> vars = new ArrayList<>(numVars);
        for (int i = 0; i < numVars; i++) {
            vars.add(fg.variables(i));
        }
        FlatConfiguration conf = fg.configuration();
        int numOutputs = fg.outputsLength();
        List<IntPair> outputs = new ArrayList<>(numOutputs);
        for (int i = 0; i < numOutputs; i++) {
            outputs.add(fg.outputs(i));
        }
        Assert.assertEquals(2, numVars);
        Assert.assertEquals(1, numNodes);
        // Check placeholders:
        Assert.assertEquals(1, fg.placeholdersLength());
        Assert.assertEquals("in", fg.placeholders(0));
    }

    @Test
    public void testSimple() throws Exception {
        for (int i = 8; i < 10; i++) {
            for (boolean execFirst : new boolean[]{ false, true }) {
                log.info("Starting test: i={}, execFirst={}", i, execFirst);
                SameDiff sd = SameDiff.create();
                INDArray arr = Nd4j.linspace(1, 12, 12).reshape(3, 4);
                SDVariable in = sd.placeHolder("in", arr.dataType(), arr.shape());
                SDVariable x;
                switch (i) {
                    case 0 :
                        // Custom op
                        x = sd.cumsum("out", in, false, false, 1);
                        break;
                    case 1 :
                        // Transform
                        x = sd.nn().tanh("out", in);
                        break;
                    case 2 :
                    case 3 :
                        // Reduction
                        x = sd.mean("x", in, (i == 2), 1);
                        break;
                    case 4 :
                        // Transform
                        x = sd.math().square(in);
                        break;
                    case 5 :
                    case 6 :
                        // Index reduction
                        x = sd.argmax("x", in, (i == 5), 1);
                        break;
                    case 7 :
                        // Scalar:
                        x = in.add(10);
                        break;
                    case 8 :
                        // Reduce 3:
                        SDVariable y = sd.var("in2", Nd4j.linspace(1, 12, 12).muli(0.1).addi(0.5).reshape(3, 4));
                        x = sd.math().cosineSimilarity(in, y);
                        break;
                    case 9 :
                        // Reduce 3 (along dim)
                        SDVariable z = sd.var("in2", Nd4j.linspace(1, 12, 12).muli(0.1).addi(0.5).reshape(3, 4));
                        x = sd.math().cosineSimilarity(in, z, 1);
                        break;
                    default :
                        throw new RuntimeException();
                }
                if (execFirst) {
                    sd.exec(Collections.singletonMap("in", arr), Collections.singletonList(x.getVarName()));
                }
                File f = testDir.newFile();
                f.delete();
                sd.asFlatFile(f);
                SameDiff restored = SameDiff.fromFlatFile(f);
                List<SDVariable> varsOrig = sd.variables();
                List<SDVariable> varsRestored = restored.variables();
                Assert.assertEquals(varsOrig.size(), varsRestored.size());
                for (int j = 0; j < (varsOrig.size()); j++) {
                    Assert.assertEquals(varsOrig.get(j).getVarName(), varsRestored.get(j).getVarName());
                }
                DifferentialFunction[] fOrig = sd.functions();
                DifferentialFunction[] fRestored = restored.functions();
                Assert.assertEquals(fOrig.length, fRestored.length);
                for (int j = 0; j < (sd.functions().length); j++) {
                    Assert.assertEquals(fOrig[j].getClass(), fRestored[j].getClass());
                }
                Map<String, INDArray> m = sd.exec(Collections.singletonMap("in", arr), Collections.singletonList(x.getVarName()));
                INDArray outOrig = m.get(x.getVarName());
                Map<String, INDArray> m2 = restored.exec(Collections.singletonMap("in", arr), Collections.singletonList(x.getVarName()));
                INDArray outRestored = m2.get(x.getVarName());
                Assert.assertEquals(String.valueOf(i), outOrig, outRestored);
                // Check placeholders
                Map<String, SDVariable> vBefore = sd.variableMap();
                Map<String, SDVariable> vAfter = sd.variableMap();
                Assert.assertEquals(vBefore.keySet(), vAfter.keySet());
                for (String s : vBefore.keySet()) {
                    Assert.assertEquals(s, vBefore.get(s).isPlaceHolder(), vAfter.get(s).isPlaceHolder());
                    Assert.assertEquals(s, vBefore.get(s).isConstant(), vAfter.get(s).isConstant());
                }
            }
        }
    }
}

