package marytts.features;


import marytts.unitselection.select.Target;
import marytts.util.string.ByteStringTranslator;
import org.junit.Assert;
import org.junit.Test;


public class TargetFeatureComputerTest {
    private TargetFeatureComputerTest.TestByteValuedFeatureProcessor processor;

    private TargetFeatureComputer computer;

    @Test
    public void testToStringValues() {
        String[] values = processor.getValues();
        ByteStringTranslator translator = new ByteStringTranslator(values);
        for (String expected : values) {
            byte feature = translator.get(expected);
            FeatureVector vector = new FeatureVector(new byte[]{ feature }, new short[]{  }, new float[]{  }, 0);
            String actual = computer.toStringValues(vector);
            Assert.assertEquals(expected, actual);
        }
    }

    public class TestByteValuedFeatureProcessor implements ByteValuedFeatureProcessor {
        private ByteStringTranslator values;

        public TestByteValuedFeatureProcessor() {
            this.values = new ByteStringTranslator(valueProvider());
        }

        @Override
        public String getName() {
            return "test_feature";
        }

        @Override
        public byte process(Target target) {
            return 0;
        }

        @Override
        public String[] getValues() {
            return values.getStringValues();
        }
    }
}

