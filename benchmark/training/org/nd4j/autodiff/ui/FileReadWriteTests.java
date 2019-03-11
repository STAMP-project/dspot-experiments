package org.nd4j.autodiff.ui;


import DataType.DOUBLE;
import LogFileWriter.StaticInfo;
import UIInfoType.START_EVENTS;
import com.google.flatbuffers.Table;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.nd4j.autodiff.samediff.SDVariable;
import org.nd4j.autodiff.samediff.SameDiff;
import org.nd4j.autodiff.samediff.VariableType;
import org.nd4j.autodiff.samediff.internal.SameDiffOp;
import org.nd4j.autodiff.samediff.internal.Variable;
import org.nd4j.autodiff.samediff.serde.FlatBuffersMapper;
import org.nd4j.graph.ui.LogFileWriter;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.primitives.Pair;


@Slf4j
public class FileReadWriteTests {
    @Rule
    public TemporaryFolder testDir = new TemporaryFolder();

    @Test
    public void testSimple() throws IOException {
        SameDiff sd = SameDiff.create();
        SDVariable v = sd.var("variable", DOUBLE, 3, 4);
        SDVariable sum = v.sum();
        File f = testDir.newFile();
        if (f.exists())
            f.delete();

        System.out.println(f.getAbsolutePath());
        LogFileWriter w = new LogFileWriter(f);
        long bytesWritten = w.writeGraphStructure(sd);
        long bytesWritten2 = w.writeFinishStaticMarker();
        Assert.assertTrue((bytesWritten > 0));
        Assert.assertTrue((bytesWritten2 > 0));
        LogFileWriter.StaticInfo read = w.readStatic();
        Assert.assertEquals(2, read.getData().size());
        long fileLength = f.length();
        Assert.assertEquals(fileLength, read.getFileOffset());
        // Check graph structure:
        // Inputs
        UIGraphStructure s = ((UIGraphStructure) (read.getData().get(0).getSecond()));
        List<String> l = new ArrayList(s.inputsLength());
        for (int i = 0; i < (s.inputsLength()); i++) {
            l.add(s.inputs(i));
        }
        Assert.assertEquals(sd.inputs(), l);
        // Outputs
        List<String> outputs = new ArrayList(s.outputsLength());
        for (int i = 0; i < (s.outputsLength()); i++) {
            outputs.add(s.outputs(i));
        }
        Assert.assertEquals(sd.outputs(), outputs);
        // Check variables
        int numVars = s.variablesLength();
        List<UIVariable> varsList = new ArrayList<>(numVars);
        Map<String, UIVariable> varsMap = new HashMap<>();
        for (int i = 0; i < numVars; i++) {
            UIVariable uivar = s.variables(i);
            varsList.add(uivar);
            String name = uivar.name();
            varsMap.put(name, uivar);
        }
        Map<String, Variable> sdVarsMap = sd.getVariables();
        Assert.assertEquals(sdVarsMap.keySet(), varsMap.keySet());
        for (String vName : sdVarsMap.keySet()) {
            VariableType vt = sdVarsMap.get(vName).getVariable().getVariableType();
            VariableType vt2 = FlatBuffersMapper.fromVarType(varsMap.get(vName).type());
            Assert.assertEquals(vt, vt2);
            // TODO check inputs to, output of, etc
        }
        // Check ops
        int numOps = s.opsLength();
        List<UIOp> opsList = new ArrayList<>(numVars);
        Map<String, UIOp> opMap = new HashMap<>();
        for (int i = 0; i < numOps; i++) {
            UIOp uiop = s.ops(i);
            opsList.add(uiop);
            String name = uiop.name();
            opMap.put(name, uiop);
        }
        Map<String, SameDiffOp> sdOpsMap = sd.getOps();
        Assert.assertEquals(sdOpsMap.keySet(), opMap.keySet());
        // TODO check inputs, outputs etc
        Assert.assertEquals(START_EVENTS, read.getData().get(1).getFirst().infoType());
        // Append a number of events
        w.registerEventName("accuracy");
        for (int iter = 0; iter < 3; iter++) {
            long t = System.currentTimeMillis();
            w.writeScalarEvent("accuracy", t, iter, 0, (0.5 + (0.1 * iter)));
        }
        // Read events back in...
        List<Pair<UIEvent, Table>> events = w.readEvents();
        Assert.assertEquals(4, events.size());
        // add name + 3 scalars
        UIAddName addName = ((UIAddName) (events.get(0).getRight()));
        Assert.assertEquals("accuracy", addName.name());
        for (int i = 1; i < 4; i++) {
            FlatArray fa = ((FlatArray) (events.get(i).getRight()));
            INDArray arr = Nd4j.createFromFlatArray(fa);
            INDArray exp = Nd4j.scalar((0.5 + ((i - 1) * 0.1)));
            Assert.assertEquals(exp, arr);
        }
    }
}

