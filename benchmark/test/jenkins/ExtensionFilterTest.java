package jenkins;


import hudson.ExtensionComponent;
import hudson.console.ConsoleAnnotatorFactory;
import hudson.model.PageDecorator;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.TestExtension;


/**
 *
 *
 * @author Kohsuke Kawaguchi
 */
public class ExtensionFilterTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    public void filter() {
        Assert.assertTrue(PageDecorator.all().isEmpty());
        Assert.assertTrue(ConsoleAnnotatorFactory.all().isEmpty());
    }

    @TestExtension("filter")
    public static class Impl extends ExtensionFilter {
        @Override
        public <T> boolean allows(Class<T> type, ExtensionComponent<T> component) {
            if (type == (ConsoleAnnotatorFactory.class))
                return false;

            if (component.isDescriptorOf(PageDecorator.class))
                return false;

            return true;
        }
    }
}

