package cucumber.runtime;


import cucumber.runtime.io.Resource;
import cucumber.runtime.io.ResourceLoader;
import io.cucumber.core.logging.LogRecordListener;
import io.cucumber.core.model.FeaturePath;
import io.cucumber.core.options.FeatureOptions;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class FeaturePathFeatureSupplierTest {
    private LogRecordListener logRecordListener;

    @Test
    public void logs_message_if_no_features_are_found() {
        ResourceLoader resourceLoader = Mockito.mock(ResourceLoader.class);
        Mockito.when(resourceLoader.resources(URI.create("file:does/not/exist"), ".feature")).thenReturn(Collections.<Resource>emptyList());
        FeatureOptions featureOptions = new FeatureOptions() {
            @Override
            public List<URI> getFeaturePaths() {
                return Collections.singletonList(FeaturePath.parse("does/not/exist"));
            }
        };
        FeaturePathFeatureSupplier supplier = new FeaturePathFeatureSupplier(new cucumber.runtime.model.FeatureLoader(resourceLoader), featureOptions);
        supplier.get();
        Assert.assertThat(logRecordListener.getLogRecords().get(1).getMessage(), CoreMatchers.containsString("No features found at file:does/not/exist"));
    }

    @Test
    public void logs_message_if_no_feature_paths_are_given() {
        ResourceLoader resourceLoader = Mockito.mock(ResourceLoader.class);
        FeatureOptions featureOptions = new FeatureOptions() {
            @Override
            public List<URI> getFeaturePaths() {
                return Collections.emptyList();
            }
        };
        FeaturePathFeatureSupplier supplier = new FeaturePathFeatureSupplier(new cucumber.runtime.model.FeatureLoader(resourceLoader), featureOptions);
        supplier.get();
        Assert.assertThat(logRecordListener.getLogRecords().get(1).getMessage(), CoreMatchers.containsString("Got no path to feature directory or feature file"));
    }
}

