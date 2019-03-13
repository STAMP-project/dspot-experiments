package cucumber.runtime.model;


import cucumber.runtime.io.ResourceLoader;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class CucumberFeatureTest {
    private static final String DOES_NOT_EXIST = "does/not/exist";

    @Test
    public void succeeds_if_no_features_are_found() {
        ResourceLoader resourceLoader = Mockito.mock(ResourceLoader.class);
        mockNonExistingResource(resourceLoader);
        load(feature("does/not/exist"));
    }

    @Test
    public void gives_error_message_if_path_does_not_exist() {
        ResourceLoader resourceLoader = Mockito.mock(ResourceLoader.class);
        mockFeaturePathToNotExist(resourceLoader, "path/bar.feature");
        try {
            load(feature("path/bar.feature"));
            Assert.fail("IllegalArgumentException was expected");
        } catch (IllegalArgumentException exception) {
            Assert.assertEquals("Not a file or directory: path/bar.feature", exception.getMessage());
        }
    }

    @Test
    public void gives_error_message_if_feature_on_class_path_does_not_exist() {
        ResourceLoader resourceLoader = Mockito.mock(ResourceLoader.class);
        mockFeaturePathToNotExist(resourceLoader, "classpath:path/bar.feature");
        try {
            load(feature("classpath:path/bar.feature"));
            Assert.fail("IllegalArgumentException was expected");
        } catch (IllegalArgumentException exception) {
            Assert.assertEquals("Feature not found: classpath:path/bar.feature", exception.getMessage());
        }
    }
}

