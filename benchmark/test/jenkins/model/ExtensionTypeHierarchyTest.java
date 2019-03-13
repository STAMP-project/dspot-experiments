package jenkins.model;


import hudson.ExtensionPoint;
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
public class ExtensionTypeHierarchyTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    public static interface Animal extends ExtensionPoint {}

    public static interface White extends ExtensionPoint {}

    @TestExtension
    public static class Swan implements ExtensionTypeHierarchyTest.Animal , ExtensionTypeHierarchyTest.White {}

    @TestExtension
    public static class Crow implements ExtensionTypeHierarchyTest.Animal {}

    /**
     * Swan is both white and animal, so a single swan instance gets listed to both.
     */
    @Test
    public void sameExtensionCanImplementMultipleExtensionPoints() {
        ExtensionTypeHierarchyTest.Animal[] animals = sort(j.jenkins.getExtensionList(ExtensionTypeHierarchyTest.Animal.class).toArray(new ExtensionTypeHierarchyTest.Animal[2]));
        Assert.assertTrue(((animals[0]) instanceof ExtensionTypeHierarchyTest.Crow));
        Assert.assertTrue(((animals[1]) instanceof ExtensionTypeHierarchyTest.Swan));
        Assert.assertEquals(2, animals.length);
        ExtensionTypeHierarchyTest.White[] whites = sort(j.jenkins.getExtensionList(ExtensionTypeHierarchyTest.White.class).toArray(new ExtensionTypeHierarchyTest.White[1]));
        Assert.assertTrue(((whites[0]) instanceof ExtensionTypeHierarchyTest.Swan));
        Assert.assertEquals(1, whites.length);
        Assert.assertSame(animals[1], whites[0]);
    }
}

