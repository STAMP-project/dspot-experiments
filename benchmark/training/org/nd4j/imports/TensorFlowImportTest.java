package org.nd4j.imports;


import ExecutionMode.SEQUENTIAL;
import OpExecutioner.ProfilingMode.DISABLED;
import OutputMode.IMPLICIT;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.autodiff.execution.NativeGraphExecutioner;
import org.nd4j.autodiff.execution.conf.ExecutorConfiguration;
import org.nd4j.autodiff.samediff.SameDiff;
import org.nd4j.graph.FlatGraph;
import org.nd4j.imports.converters.DifferentialFunctionClassHolder;
import org.nd4j.imports.graphmapper.tf.TFGraphMapper;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.impl.controlflow.If;
import org.nd4j.linalg.exception.ND4JIllegalStateException;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;
import org.nd4j.linalg.io.ClassPathResource;
import org.nd4j.linalg.util.HashUtil;
import org.tensorflow.framework.GraphDef;


@Slf4j
@Ignore
@RunWith(Parameterized.class)
public class TensorFlowImportTest extends BaseNd4jTest {
    private static ExecutorConfiguration configuration = ExecutorConfiguration.builder().executionMode(SEQUENTIAL).profilingMode(DISABLED).gatherTimings(true).outputMode(IMPLICIT).build();

    public TensorFlowImportTest(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testClassHolder() {
        DifferentialFunctionClassHolder.getInstance();
    }

    @Test
    public void testIfStatementNodes() throws Exception {
        // /home/agibsonccc/code/dl4j-test-resources/src/main/resources/tf_graphs/examples/simple_cond/frozen_graph.pbtxt
        val resourceInputStream = new ClassPathResource("/tf_graphs/examples/simple_cond/frozen_model.pb").getInputStream();
        val mapper = TFGraphMapper.getInstance();
        val readGraph = TFGraphMapper.getInstance().parseGraphFrom(resourceInputStream);
        val nodes = mapper.nodesByName(readGraph);
        /**
         * Work backwards starting fom the condition id (usually a name containing condid/pred_id:
         */
        val firstInput = nodes.get("cond5/Merge");
        val ifNodes = mapper.nodesForIf(firstInput, readGraph);
        Assert.assertEquals(5, ifNodes.getFalseNodes().size());
        Assert.assertEquals(5, ifNodes.getTrueNodes().size());
        Assert.assertEquals(10, ifNodes.getCondNodes().size());
        val secondInput = nodes.get("cond6/Merge");
        val ifNodesTwo = mapper.nodesForIf(secondInput, readGraph);
        Assert.assertEquals(5, ifNodesTwo.getFalseNodes().size());
        Assert.assertEquals(5, ifNodesTwo.getTrueNodes().size());
        Assert.assertEquals(6, ifNodesTwo.getCondNodes().size());
        val parentContext = SameDiff.create();
        val ifStatement = new If();
        ifStatement.initFromTensorFlow(firstInput, parentContext, Collections.emptyMap(), readGraph);
        Assert.assertNotNull(ifStatement.getLoopBodyExecution());
        Assert.assertNotNull(ifStatement.getFalseBodyExecution());
        Assert.assertNotNull(ifStatement.getPredicateExecution());
    }

    @Test
    public void testHashEquality1() {
        long hash = HashUtil.getLongHash("Conv2D");
        Assert.assertEquals((-1637140380760460323L), hash);
    }

    @Test
    public void testHashEquality2() {
        long hash = HashUtil.getLongHash("switch");
        Assert.assertEquals((-1988317239813741487L), hash);
    }

    @Test
    public void testCustomOps1() {
        val map = Nd4j.getExecutioner().getCustomOperations();
        Assert.assertTrue(((map.size()) > 0));
    }

    @Test
    public void testLenet() throws Exception {
        /**
         * Produced with:
         * python  ~/anaconda2/lib/python2.7/site-packages/tensorflow/python/tools/freeze_graph.py  --input_graph=graph2.pb.txt  --input_checkpoint=test3.ckpt  --output_graph=graph_frozen2.pb  --output_node_name=output/BiasAdd --input_binary=False
         */
        Nd4j.create(1);
        val rawGraph = GraphDef.parseFrom(new ClassPathResource("tf_graphs/lenet_cnn.pb").getInputStream());
        val nodeNames = rawGraph.getNodeList().stream().map(( node) -> node.getName()).collect(Collectors.toList());
        System.out.println(nodeNames);
        val tg = TFGraphMapper.getInstance().importGraph(new ClassPathResource("tf_graphs/lenet_cnn.pb").getInputStream());
        val convNode = tg.getVariable("conv2d/kernel");
        Assert.assertNotNull(convNode.getArr());
        val shape = convNode.getShape();
        System.out.println(Arrays.toString(shape));
        // this is NHWC weights. will be changed soon.
        BaseNd4jTest.assertArrayEquals(new int[]{ 5, 5, 1, 32 }, shape);
        System.out.println(convNode);
    }

    @Test
    public void testIntermediate2() throws Exception {
        Nd4j.create(1);
        val tg = TFGraphMapper.getInstance().importGraph(new ClassPathResource("tf_graphs/max_lstm.pb").getInputStream());
    }

    @Test
    public void testIntermediate1() throws Exception {
        Nd4j.create(1);
        val tg = TFGraphMapper.getInstance().importGraph(new ClassPathResource("tf_graphs/tensorflow_inception_graph.pb").getInputStream());
        Assert.assertTrue(((tg.getVariable("input")) != null));
        // assertTrue(tg.getVariableSpace().getVariable("input").isPlaceholder());
        val ipod = Nd4j.read(new DataInputStream(new ClassPathResource("tf_graphs/ipod.nd4").getInputStream()));
        tg.updateVariable("input", ipod);
        val buffer = tg.asFlatBuffers();
        Assert.assertNotNull(buffer);
    }

    @Test
    public void testIntermediateLoop1() throws Exception {
        Nd4j.create(1);
        val tg = TFGraphMapper.getInstance().importGraph(new ClassPathResource("tf_graphs/simple_while.pb.txt").getInputStream());
        Assert.assertNotNull(tg);
        val graph = FlatGraph.getRootAsFlatGraph(tg.asFlatBuffers());
        Assert.assertEquals(6, graph.variablesLength());
        // assertEquals("alpha/Assign", graph.nodes(0).name());
    }

    @Test
    public void testIntermediateLoop3() throws Exception {
        Nd4j.create(1);
        val tg = TFGraphMapper.getInstance().importGraph(new ClassPathResource("tf_graphs/nested_while.pb.txt").getInputStream());
        Assert.assertNotNull(tg);
        // now converting to FlatBuffer
        val fb = tg.asFlatBuffers();
        Assert.assertNotNull(fb);
        val graph = FlatGraph.getRootAsFlatGraph(fb);
        Assert.assertEquals(15, graph.variablesLength());
        // assertEquals("phi/Assign", graph.nodes(0).name());
        // assertEquals("alpha/Assign", graph.nodes(1).name());
        Assert.assertEquals(2, graph.nodes(0).inputPairedLength());
        Assert.assertEquals(2, graph.nodes(1).inputPairedLength());
        // tg.asFlatFile(new File("../../../libnd4j/tests_cpu/resources/nested_while.fb"));
    }

    @Test
    public void testIntermediateReduction() throws Exception {
        Nd4j.create(1);
        val tg = TFGraphMapper.getInstance().importGraph(new ClassPathResource("tf_graphs/reduce_dim.pb.txt").getInputStream());
        val sumResultVar = tg.getVariable("Sum");
        /* val func = tg.getFunctionForVertexId(sumResultVar.getVertexId());
        assertEquals(0,func.getDimensions()[0]);
        assertEquals(3,tg.variables().size());
        assertNotNull(sumResultVar);
        assertNotNull(tg.getFunctionForVertexId(sumResultVar.getVertexId()));
        System.out.println(tg.variables());

        assertNotNull(func.getDimensions());
        assertEquals(0,func.getDimensions()[0]);
         */
        val fb = tg.asFlatBuffers();
        Assert.assertNotNull(fb);
        val graph = FlatGraph.getRootAsFlatGraph(fb);
        Assert.assertEquals(1, graph.nodesLength());
        Assert.assertEquals(2, graph.variablesLength());
        Assert.assertEquals("Sum", graph.nodes(0).name());
        val nodeSum = graph.nodes(0);
        Assert.assertEquals(2, nodeSum.inputPairedLength());
        // we expect these inputs to be 1:0 and 2:0 respectively
        // where 1 (or 2) is a graph node id
        // and :0 is graph node output index, which is 0 because that's predefined variables
        val in0 = nodeSum.inputPaired(0);
        val in1 = nodeSum.inputPaired(1);
        Assert.assertEquals(1, in0.first());
        Assert.assertEquals(0, in0.second());
        Assert.assertEquals(2, in1.first());
        Assert.assertEquals(0, in1.second());
        Assert.assertEquals(1, nodeSum.dimensions(1));
        // log.info("nodeSum inputs length: {}; inputPaired length: {}",nodeSum.inputLength(), nodeSum.inputPairedLength());
        // tg.asFlatFile(new File("../../../libnd4j/tests_cpu/resources/reduce_dim.fb"));
        val executioner = new NativeGraphExecutioner();
        val exp = Nd4j.create(3, 1).assign(3);
        val results = executioner.executeGraph(tg, TensorFlowImportTest.configuration);
        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.length);
        Assert.assertEquals(exp, results[0]);
    }

    @Test
    public void testDefaultArgs() {
        val op = Nd4j.getOpFactory().getOpByName("relu");
        val extras = op.extraArgs();
        Assert.assertTrue(((extras.length) == 1));
        val value = ((Double) (extras[0]));
        Assert.assertEquals(0.0F, value.floatValue(), 1.0E-5F);
    }

    @Test
    public void testInferShape() throws IOException {
        /**
         * node {
         * name: "input"
         * op: "Placeholder"
         * attr {
         * key: "dtype"
         * value {
         * type: DT_FLOAT
         * }
         * }
         * attr {
         * key: "shape"
         * value {
         * shape {
         * dim {
         * size: -1
         * }
         * dim {
         * size: 4
         * }
         * }
         * }
         * }
         * }
         * node {
         * name: "bias"
         * op: "Const"
         * attr {
         * key: "dtype"
         * value {
         * type: DT_FLOAT
         * }
         * }
         * attr {
         * key: "value"
         * value {
         * tensor {
         * dtype: DT_FLOAT
         * tensor_shape {
         * dim {
         * size: 4
         * }
         * }
         * tensor_content: "\000\000\200?\000\000\000@\000\000@@\000\000\200@"
         * }
         * }
         * }
         * }
         * node {
         * name: "bias/read"
         * op: "Identity"
         * input: "bias"
         * attr {
         * key: "_class"
         * value {
         * list {
         * s: "loc:@bias"
         * }
         * }
         * }
         * attr {
         * key: "T"
         * value {
         * type: DT_FLOAT
         * }
         * }
         * }
         * node {
         * name: "output"
         * op: "BiasAdd"
         * input: "input"
         * input: "bias/read"
         * attr {
         * key: "data_format"
         * value {
         * s: "NHWC"
         * }
         * }
         * attr {
         * key: "T"
         * value {
         * type: DT_FLOAT
         * }
         * }
         * }
         * library {
         * }
         */
        SameDiff graph = TFGraphMapper.getInstance().importGraph(new ClassPathResource("tf_graphs/examples/bias_add/frozen_model.pb").getInputStream());
        Assert.assertNotNull(graph);
        INDArray input = Nd4j.linspace(1, 40, 40).reshape(10, 4);
        INDArray expectedOutput = Nd4j.linspace(1, 40, 40).reshape(10, 4).addRowVector(Nd4j.linspace(1, 4, 4));
        INDArray actual = graph.execWithPlaceHolderAndEndResult(Collections.singletonMap("input", input));
        Assert.assertEquals(input, graph.getVariable("input").getArr());
        BaseNd4jTest.assertArrayEquals(input.shape(), graph.getShapeForVarName(graph.getVariable("input").getVarName()));
        Assert.assertEquals(expectedOutput, actual);
    }

    @Test
    public void testImportMapping1() throws Exception {
        Nd4j.create(1);
        val tg = TFGraphMapper.getInstance().importGraph(new ClassPathResource("tf_graphs/examples/ae_00/frozen_model.pb").getInputStream());
        val variables = new HashMap<String, org.nd4j.autodiff.samediff.SDVariable>();
        for (val var : tg.variables()) {
            variables.put(var.getVarName(), var);
        }
        val functions = new HashMap<String, org.nd4j.autodiff.functions.DifferentialFunction>();
        for (val func : tg.functions()) {
            val ownName = func.getOwnName();
            val outName = func.outputVariables()[0].getVarName();
            Assert.assertTrue((("Missing ownName: [" + ownName) + "]"), variables.containsKey(ownName));
            Assert.assertEquals(ownName, outName);
        }
    }

    @Test
    public void testCondMapping1() throws Exception {
        Nd4j.create(1);
        val tg = TFGraphMapper.getInstance().importGraph(new ClassPathResource("tf_graphs/examples/simpleif_0/frozen_model.pb").getInputStream());
        Assert.assertNotNull(tg);
        tg.asFlatFile(new File("../../../libnd4j/tests_cpu/resources/simpleif_0_1.fb"));
        /* //log.info("{}", tg.asFlatPrint());
        val array = tg.execAndEndResult();
        val exp = Nd4j.create(2, 2).assign(-2);
        assertNotNull(array);
        assertEquals(exp, array);
         */
    }

    @Test
    public void testCondMapping2() throws Exception {
        Nd4j.create(1);
        val tg = TFGraphMapper.getInstance().importGraph(new ClassPathResource("tf_graphs/examples/simpleif_0/frozen_model.pb").getInputStream());
        Assert.assertNotNull(tg);
        val input = Nd4j.create(2, 2).assign((-1));
        tg.associateArrayWithVariable(input, tg.getVariable("input_0"));
        // tg.asFlatFile(new File("../../../libnd4j/tests_cpu/resources/simpleif_0.fb"));
        // log.info("{}", tg.asFlatPrint());
        val array = tg.execAndEndResult();
        val exp = Nd4j.create(2, 2).assign(1);
        Assert.assertNotNull(array);
        Assert.assertEquals(exp, array);
    }

    @Test
    public void testWhileMapping1() throws Exception {
        Nd4j.create(1);
        val tg = TFGraphMapper.getInstance().importGraph(new ClassPathResource("tf_graphs/examples/simplewhile_0/frozen_model.pb").getInputStream());
        Assert.assertNotNull(tg);
        val input = Nd4j.create(2, 2).assign(1);
        tg.associateArrayWithVariable(input, tg.getVariable("input_0"));
        // tg.asFlatFile(new File("../../../libnd4j/tests_cpu/resources/simplewhile_0_3.fb"));
        // log.info("{}", tg.asFlatPrint());
        val array = tg.execAndEndResult();
        val exp = Nd4j.create(2, 2).assign(1);
        Assert.assertNotNull(array);
        Assert.assertEquals(exp, array);
    }

    @Test
    public void testWhileMapping2() throws Exception {
        Nd4j.create(1);
        val tg = TFGraphMapper.getInstance().importGraph(new ClassPathResource("tf_graphs/examples/simplewhile_0/frozen_model.pb").getInputStream());
        Assert.assertNotNull(tg);
        val input = Nd4j.trueScalar(4.0);
        tg.associateArrayWithVariable(input, tg.getVariable("input_1"));
        tg.asFlatFile(new File("../../../libnd4j/tests_cpu/resources/simplewhile_0_4.fb"));
        // log.info("{}", tg.asFlatPrint());
        /* val array = tg.execAndEndResult();
        val exp = Nd4j.create(2, 2).assign(2);
        assertNotNull(array);
        assertEquals(exp, array);
         */
    }

    @Test
    public void testWhileMapping3() throws Exception {
        Nd4j.create(1);
        val tg = TFGraphMapper.getInstance().importGraph(new ClassPathResource("tf_graphs/examples/simplewhile_0/frozen_model.pb").getInputStream());
        Assert.assertNotNull(tg);
        val input = Nd4j.trueScalar(9.0);
        tg.associateArrayWithVariable(input, tg.getVariable("input_1"));
        // tg.asFlatFile(new File("../../../libnd4j/tests_cpu/resources/simplewhile_0.fb"));
        // log.info("{}", tg.asFlatPrint());
        val array = tg.execAndEndResult();
        val exp = Nd4j.create(2, 2).assign(4);
        Assert.assertNotNull(array);
        Assert.assertEquals(exp, array);
    }

    @Test
    public void testWhileDualMapping1() throws Exception {
        Nd4j.create(1);
        val tg = TFGraphMapper.getInstance().importGraph(new ClassPathResource("tf_graphs/examples/simplewhile_1/frozen_model.pb").getInputStream());
        Assert.assertNotNull(tg);
        val input0 = Nd4j.create(2, 2).assign((-4.0));
        val input1 = Nd4j.trueScalar(1.0);
        tg.associateArrayWithVariable(input0, tg.getVariable("input_0"));
        tg.associateArrayWithVariable(input1, tg.getVariable("input_1"));
        // tg.asFlatFile(new File("../../../libnd4j/tests_cpu/resources/simplewhile_1.fb"));
        // log.info("{}", tg.asFlatPrint());
        val array = tg.execAndEndResult();
        val exp = Nd4j.create(2, 2).assign((-1));
        Assert.assertNotNull(array);
        Assert.assertEquals(exp, array);
    }

    @Test
    public void testWhileDualMapping2() throws Exception {
        Nd4j.create(1);
        val tg = TFGraphMapper.getInstance().importGraph(new ClassPathResource("tf_graphs/examples/simplewhile_1/frozen_model.pb").getInputStream());
        Assert.assertNotNull(tg);
        val input0 = Nd4j.create(2, 2).assign((-9.0));
        val input1 = Nd4j.trueScalar(1.0);
        tg.associateArrayWithVariable(input0, tg.getVariable("input_0"));
        tg.associateArrayWithVariable(input1, tg.getVariable("input_1"));
        // tg.asFlatFile(new File("../../../libnd4j/tests_cpu/resources/simplewhile_1.fb"));
        // log.info("{}", tg.asFlatPrint());
        val array = tg.execAndEndResult();
        val exp = Nd4j.create(2, 2).assign((-3));
        Assert.assertNotNull(array);
        Assert.assertEquals(exp, array);
    }

    @Test
    public void testMixedWhileCond1() throws Exception {
        Nd4j.create(1);
        val tg = TFGraphMapper.getInstance().importGraph(new ClassPathResource("tf_graphs/examples/simplewhile_nested/frozen_model.pb").getInputStream());
        Assert.assertNotNull(tg);
        val input0 = Nd4j.create(2, 2).assign(1.0);
        val input1 = Nd4j.create(3, 3).assign(2.0);
        tg.associateArrayWithVariable(input0, tg.getVariable("input_0"));
        tg.associateArrayWithVariable(input1, tg.getVariable("input_1"));
        // tg.asFlatFile(new File("../../../libnd4j/tests_cpu/resources/simplewhile_nested.fb"));
        // log.info("{}", tg.asFlatPrint());
        val array = tg.execAndEndResult();
        // val array = tg.getVariable("output").getArr();
        val exp = Nd4j.create(2, 2).assign(15.0);
        Assert.assertNotNull(array);
        Assert.assertEquals(exp, array);
    }

    @Test(expected = ND4JIllegalStateException.class)
    public void testNonFrozenGraph1() throws Exception {
        val tg = TFGraphMapper.getInstance().importGraph(new ClassPathResource("tf_graphs/examples/unfrozen_simple_ae.pb").getInputStream());
    }
}

