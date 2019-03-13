package org.nd4j.imports;


import OnnxProto3.GraphProto;
import OnnxProto3.ModelProto;
import lombok.val;
import onnx.OnnxProto3;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.nd4j.imports.graphmapper.onnx.OnnxGraphMapper;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.io.ClassPathResource;


public class OnnxGraphMapperTests {
    @Test
    public void testMapper() throws Exception {
        try (val inputs = new ClassPathResource("onnx_graphs/embedding_only.onnx").getInputStream()) {
            OnnxProto3.GraphProto graphProto = ModelProto.parseFrom(inputs).getGraph();
            OnnxGraphMapper onnxGraphMapper = new OnnxGraphMapper();
            Assert.assertEquals(graphProto.getNodeList().size(), onnxGraphMapper.getNodeList(graphProto).size());
            Assert.assertEquals(4, onnxGraphMapper.variablesForGraph(graphProto).size());
            val initializer = graphProto.getInput(0).getType().getTensorType();
            INDArray arr = onnxGraphMapper.getNDArrayFromTensor(graphProto.getInitializer(0).getName(), initializer, graphProto);
            Assume.assumeNotNull(arr);
            for (val node : graphProto.getNodeList()) {
                Assert.assertEquals(node.getAttributeList().size(), onnxGraphMapper.getAttrMap(node).size());
            }
            val sameDiff = onnxGraphMapper.importGraph(graphProto);
            Assert.assertEquals(1, sameDiff.functions().length);
            System.out.println(sameDiff);
        }
    }

    @Test
    public void test1dCnn() throws Exception {
        val loadedFile = new ClassPathResource("onnx_graphs/sm_cnn.onnx").getInputStream();
        val mapped = OnnxGraphMapper.getInstance().importGraph(loadedFile);
        System.out.println(mapped.variables());
    }

    @Test
    public void testLoadResnet() throws Exception {
        val loadedFile = new ClassPathResource("onnx_graphs/resnet50/model.pb").getInputStream();
        val mapped = OnnxGraphMapper.getInstance().importGraph(loadedFile);
    }
}

