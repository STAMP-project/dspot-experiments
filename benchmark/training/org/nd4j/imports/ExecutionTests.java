package org.nd4j.imports;


import java.util.LinkedHashMap;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.imports.graphmapper.tf.TFGraphMapper;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;
import org.nd4j.linalg.io.ClassPathResource;


/**
 * This set of tests suited for validation of various graph execuction methods: flatbuffers, stored graphs reuse, one-by-one execution, etc
 *
 * @author raver119@gmail.com
 */
@Slf4j
@RunWith(Parameterized.class)
public class ExecutionTests extends BaseNd4jTest {
    public ExecutionTests(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testStoredGraph_1() throws Exception {
        Nd4j.create(1);
        val tg = TFGraphMapper.getInstance().importGraph(new ClassPathResource("tf_graphs/reduce_dim.pb.txt").getInputStream());
        val array0 = Nd4j.create(3, 3).assign(2.0);
        val map0 = new LinkedHashMap<String, org.nd4j.linalg.api.ndarray.INDArray>();
        map0.put("alpha", array0);
        val result_0 = tg.yetAnotherExecMethod(map0);
        val exp_0 = Nd4j.create(3, 1).assign(6.0);
        Assert.assertEquals(exp_0, result_0);
        val array1 = Nd4j.create(3, 3).assign(3.0);
        val map1 = new LinkedHashMap<String, org.nd4j.linalg.api.ndarray.INDArray>();
        map1.put("alpha", array1);
        val result_1 = tg.yetAnotherExecMethod(map1);
        val exp_1 = Nd4j.create(3, 1).assign(9.0);
        Assert.assertEquals(exp_1, result_1);
    }
}

