package org.nd4j.linalg.activations;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.factory.Nd4jBackend;


/**
 * Created by Alex on 30/12/2016.
 */
@RunWith(Parameterized.class)
public class TestActivationJson extends BaseNd4jTest {
    public TestActivationJson(Nd4jBackend backend) {
        super(backend);
    }

    private ObjectMapper mapper;

    @Test
    public void testJson() throws Exception {
        IActivation[] activations = new IActivation[]{ new ActivationCube(), new ActivationELU(0.25), new ActivationHardSigmoid(), new ActivationHardTanH(), new ActivationIdentity(), new ActivationLReLU(0.25), new ActivationRationalTanh(), new ActivationReLU(), new ActivationRReLU(0.25, 0.5), new ActivationSigmoid(), new ActivationSoftmax(), new ActivationSoftPlus(), new ActivationSoftSign(), new ActivationTanH() };
        String[][] expectedFields = new String[][]{ new String[]{ "@class" }// Cube
        // Cube
        // Cube
        , new String[]{ "@class", "alpha" }// ELU
        // ELU
        // ELU
        , new String[]{ "@class" }// Hard sigmoid
        // Hard sigmoid
        // Hard sigmoid
        , new String[]{ "@class" }// Hard TanH
        // Hard TanH
        // Hard TanH
        , new String[]{ "@class" }// Identity
        // Identity
        // Identity
        , new String[]{ "@class", "alpha" }// Leaky Relu
        // Leaky Relu
        // Leaky Relu
        , new String[]{ "@class" }// rational tanh
        // rational tanh
        // rational tanh
        , new String[]{ "@class" }// relu
        // relu
        // relu
        , new String[]{ "@class", "l", "u" }// rrelu
        // rrelu
        // rrelu
        , new String[]{ "@class" }// sigmoid
        // sigmoid
        // sigmoid
        , new String[]{ "@class" }// Softmax
        // Softmax
        // Softmax
        , new String[]{ "@class" }// Softplus
        // Softplus
        // Softplus
        , new String[]{ "@class" }// Softsign
        // Softsign
        // Softsign
        , new String[]{ "@class" }// Tanh
        // Tanh
        // Tanh
         };
        for (int i = 0; i < (activations.length); i++) {
            String asJson = mapper.writeValueAsString(activations[i]);
            System.out.println(asJson);
            JsonNode node = mapper.readTree(asJson);
            Iterator<String> fieldNamesIter = node.fieldNames();
            List<String> actualFieldsByName = new ArrayList<>();
            while (fieldNamesIter.hasNext()) {
                actualFieldsByName.add(fieldNamesIter.next());
            } 
            String[] expFields = expectedFields[i];
            String msg = ((((activations[i].toString()) + "\tExpected fields: ") + (Arrays.toString(expFields))) + "\tActual fields: ") + actualFieldsByName;
            Assert.assertEquals(msg, expFields.length, actualFieldsByName.size());
            for (String s : expFields) {
                msg = (("Expected field \"" + s) + "\", was not found in ") + (activations[i].toString());
                TestCase.assertTrue(msg, actualFieldsByName.contains(s));
            }
            // Test conversion from JSON:
            IActivation act = mapper.readValue(asJson, IActivation.class);
            Assert.assertEquals(activations[i], act);
        }
    }
}

