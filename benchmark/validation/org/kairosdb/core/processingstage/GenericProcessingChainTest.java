package org.kairosdb.core.processingstage;


import com.google.common.collect.ImmutableList;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;
import org.kairosdb.core.aggregator.AggregatorFactory;
import org.kairosdb.plugin.Aggregator;
import org.kairosdb.plugin.GroupBy;


public class GenericProcessingChainTest {
    private static FeatureProcessor processingChain;

    @Test(expected = IllegalArgumentException.class)
    public void chain_generation_empty_list() throws ClassNotFoundException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        new TestKairosDBProcessor(new ArrayList<>());
    }

    @Test(expected = NullPointerException.class)
    public void chain_generation_null_list() {
        new TestKairosDBProcessor(null);
    }

    @Test
    public void chain_getter_factory_with_name() {
        FeatureProcessingFactory<?> factory = GenericProcessingChainTest.processingChain.getFeatureProcessingFactory(Aggregator.class);
        Assert.assertEquals("Invalid type of FeatureProcessingFactory", AggregatorFactory.class, factory.getClass());
    }

    @Test
    public void chain_getter_factory_with_name_failure() {
        FeatureProcessingFactory<?> factory = GenericProcessingChainTest.processingChain.getFeatureProcessingFactory(GroupBy.class);
        Assert.assertEquals("Invalid type of FeatureProcessingFactory", null, factory);
    }

    @Test
    public void chain_getter_factory_with_class() {
        FeatureProcessingFactory<?> factory = GenericProcessingChainTest.processingChain.getFeatureProcessingFactory("aggregators");
        Assert.assertEquals("Invalid type of FeatureProcessingFactory", AggregatorFactory.class, factory.getClass());
    }

    @Test
    public void chain_getter_factory_with_class_failure() {
        FeatureProcessingFactory<?> factory = GenericProcessingChainTest.processingChain.getFeatureProcessingFactory("groupby");
        Assert.assertEquals("Invalid type of FeatureProcessingFactory", null, factory);
    }

    @Test
    public void chain_getter_metadata() throws ClassNotFoundException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        GenericProcessingChainTest.assertQueryProcessorFactories(ImmutableList.copyOf(GenericProcessingChainTest.chain_valid_metadata_generator()), this.processingChain.getFeatureProcessingMetadata());
    }
}

