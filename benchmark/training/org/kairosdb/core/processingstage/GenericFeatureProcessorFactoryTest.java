package org.kairosdb.core.processingstage;


import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import java.lang.reflect.InvocationTargetException;
import org.junit.Assert;
import org.junit.Test;
import org.kairosdb.core.aggregator.AggregatorFactory;
import org.kairosdb.core.annotatedAggregator.AAggregator;
import org.kairosdb.plugin.Aggregator;


public class GenericFeatureProcessorFactoryTest {
    private static FeatureProcessingFactory<Aggregator> factory;

    @Test(expected = IllegalStateException.class)
    public void factory_generation_invalid_metadata() throws ClassNotFoundException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Injector injector = Guice.createInjector(((Module) (( binder) -> binder.bind(.class))));
        FeatureProcessingFactory<Aggregator> factory = new AggregatorFactory(injector);
    }

    @Test(expected = NullPointerException.class)
    public void factory_generation_invalid_injector() throws ClassNotFoundException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        FeatureProcessingFactory<?> factory = new AggregatorFactory(null);
    }

    @Test
    public void factory_getter_query_processor_family() {
        Assert.assertEquals("FeatureComponent family don't match", Aggregator.class, GenericFeatureProcessorFactoryTest.factory.getFeature());
    }

    @Test
    public void factory_getter_query_processor_metadata() throws ClassNotFoundException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        GenericFeatureProcessorFactoryTest.assertQueryProcessors(ImmutableList.copyOf(GenericFeatureProcessorFactoryTest.factory_valid_metadata_generator()), GenericFeatureProcessorFactoryTest.factory.getFeatureProcessorMetadata());
    }

    @Test
    public void factory_new_query_processor() {
        Assert.assertEquals("FeatureComponent created was invalid", AAggregator.class, GenericFeatureProcessorFactoryTest.factory.createFeatureProcessor("A").getClass());
    }
}

