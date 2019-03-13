package com.github.neuralnetworks.input;


import com.github.neuralnetworks.tensor.TensorFactory;
import com.github.neuralnetworks.test.AbstractTest;
import com.github.neuralnetworks.training.TrainingInputDataImpl;
import com.github.neuralnetworks.util.Environment;
import com.github.neuralnetworks.util.RuntimeConfiguration;
import com.github.neuralnetworks.util.Util;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Created by chass on 02.12.14.
 */
@RunWith(Parameterized.class)
public class SimpleFileInputProviderTest extends AbstractTest {
    public SimpleFileInputProviderTest(RuntimeConfiguration conf) {
        super();
        Environment.getInstance().setRuntimeConfiguration(conf);
    }

    @Test
    public void testSimpleFileInputProvider() {
        SimpleFileInputProvider sip2 = null;
        try {
            SimpleInputProvider sip1 = new SimpleInputProvider(new float[][]{ new float[]{ 1, 2 }, new float[]{ 3, 4 }, new float[]{ 5, 6 } }, new float[][]{ new float[]{ 1, 2 }, new float[]{ 3, 4 }, new float[]{ 5, 6 } });
            Util.inputToFloat(sip1, "ip_input", "ip_target");
            sip1.reset();
            sip2 = new SimpleFileInputProvider("ip_input", "ip_target", sip1.getInputDimensions(), sip1.getTargetDimensions(), sip1.getInputSize());
            TrainingInputDataImpl ti1 = new TrainingInputDataImpl(TensorFactory.tensor(2, sip1.getInputDimensions()), TensorFactory.tensor(2, sip1.getTargetDimensions()));
            TrainingInputDataImpl ti2 = new TrainingInputDataImpl(TensorFactory.tensor(2, sip2.getInputDimensions()), TensorFactory.tensor(2, sip2.getTargetDimensions()));
            for (int i = 0; i < (sip2.getInputSize()); i += 2) {
                sip1.populateNext(ti1);
                sip2.populateNext(ti2);
                Assert.assertTrue(Arrays.equals(ti1.getInput().getElements(), ti2.getInput().getElements()));
                Assert.assertTrue(Arrays.equals(ti1.getTarget().getElements(), ti2.getTarget().getElements()));
            }
        } finally {
            try {
                sip2.close();
                Files.delete(Paths.get("ip_input"));
                Files.delete(Paths.get("ip_target"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

