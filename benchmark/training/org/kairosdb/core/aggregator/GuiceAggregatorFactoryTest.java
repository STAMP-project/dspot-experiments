package org.kairosdb.core.aggregator;


import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.kairosdb.core.annotatedAggregator.AAggregator;
import org.kairosdb.core.processingstage.metadata.FeatureProcessorMetadata;
import org.kairosdb.core.processingstage.metadata.FeaturePropertyMetadata;
import org.kairosdb.core.processingstage.metadata.FeatureValidationMetadata;


public class GuiceAggregatorFactoryTest {
    @Test
    public void test_inherited() throws ClassNotFoundException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Injector injector = Guice.createInjector(new Module() {
            @Override
            public void configure(Binder binder) {
                binder.bind(AAggregator.class);
            }
        });
        AggregatorFactory factory = new AggregatorFactory(injector);
        ImmutableList<FeatureProcessorMetadata> queryMetadata = factory.getFeatureProcessorMetadata();
        Assert.assertEquals(1, queryMetadata.size());
        FeatureProcessorMetadata metadata = queryMetadata.get(0);
        Assert.assertEquals(metadata.getName(), "A");
        Assert.assertEquals(metadata.getDescription(), "The A Aggregator");
        Assert.assertThat(metadata.getProperties().size(), CoreMatchers.equalTo(8));
        ImmutableList<FeaturePropertyMetadata> properties = metadata.getProperties();
        GuiceAggregatorFactoryTest.assertProperty(properties.get(0), "allAnnotation", "AllAnnotation", "This is allAnnotation", "int", "2", ImmutableList.copyOf(new FeatureValidationMetadata[]{ new FeatureValidationMetadata("value > 0", "js", "Value must be greater than 0.") }));
        GuiceAggregatorFactoryTest.assertProperty(properties.get(1), "inherited", "Inherited", "This is alpha", "int", "1", ImmutableList.copyOf(new ArrayList()));
        GuiceAggregatorFactoryTest.assertProperty(properties.get(2), "myBoolean", "MyBoolean", "This is myBoolean", "boolean", "false", ImmutableList.copyOf(new ArrayList()));
        GuiceAggregatorFactoryTest.assertProperty(properties.get(3), "myDouble", "MyDouble", "This is myDouble", "double", "0.0", ImmutableList.copyOf(new ArrayList()));
        GuiceAggregatorFactoryTest.assertProperty(properties.get(4), "myInt", "MyInt", "This is myInt", "int", "0", ImmutableList.copyOf(new ArrayList()));
        GuiceAggregatorFactoryTest.assertProperty(properties.get(5), "myLong", "MyLong", "This is myLong", "long", "0", ImmutableList.copyOf(new ArrayList()));
        GuiceAggregatorFactoryTest.assertProperty(properties.get(6), "myString", "MyString", "This is myString", "String", "", ImmutableList.copyOf(new ArrayList()));
        GuiceAggregatorFactoryTest.assertProperty(properties.get(7), "sampling", "Sampling");
        ImmutableList<FeaturePropertyMetadata> samplingProperties = properties.get(7).getProperties();
        GuiceAggregatorFactoryTest.assertProperty(samplingProperties.get(0), "value", "Value", "The number of units for the aggregation buckets", "long", "1", ImmutableList.copyOf(new FeatureValidationMetadata[]{ new FeatureValidationMetadata("value > 0", "js", "Value must be greater than 0.") }));
        GuiceAggregatorFactoryTest.assertProperty(samplingProperties.get(1), "unit", "Unit", "The time unit for the sampling rate", "enum", "MILLISECONDS", ImmutableList.copyOf(new ArrayList()));
    }
}

