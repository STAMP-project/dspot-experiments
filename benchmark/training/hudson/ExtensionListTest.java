package hudson;


import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.util.DescriptorList;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.WithoutJenkins;


/**
 *
 *
 * @author Kohsuke Kawaguchi
 */
public class ExtensionListTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    // 
    // 
    // non-Descriptor extension point
    // 
    // 
    public interface Animal extends ExtensionPoint {}

    @Extension
    public static class Dog implements ExtensionListTest.Animal {}

    @Extension
    public static class Cat implements ExtensionListTest.Animal {}

    @Test
    public void autoDiscovery() throws Exception {
        ExtensionList<ExtensionListTest.Animal> list = ExtensionList.lookup(ExtensionListTest.Animal.class);
        Assert.assertEquals(2, list.size());
        Assert.assertNotNull(list.get(ExtensionListTest.Dog.class));
        Assert.assertNotNull(list.get(ExtensionListTest.Cat.class));
    }

    @Test
    @WithoutJenkins
    public void nullJenkinsInstance() throws Exception {
        ExtensionList<ExtensionListTest.Animal> list = ExtensionList.lookup(ExtensionListTest.Animal.class);
        Assert.assertEquals(0, list.size());
        Assert.assertFalse(list.iterator().hasNext());
    }

    @Test
    public void extensionListView() throws Exception {
        // this is how legacy list like UserNameResolver.LIST gets created.
        List<ExtensionListTest.Animal> LIST = ExtensionListView.createList(ExtensionListTest.Animal.class);
        // we should see auto-registered instances here
        Assert.assertEquals(2, LIST.size());
        Assert.assertTrue(hasInstanceOf(LIST, ExtensionListTest.Dog.class));
        Assert.assertTrue(hasInstanceOf(LIST, ExtensionListTest.Cat.class));
        ExtensionListTest.Animal lion = new ExtensionListTest.Animal() {};
        LIST.add(lion);
        Assert.assertEquals(3, LIST.size());
        Assert.assertTrue(LIST.contains(lion));
    }

    // 
    // 
    // Descriptor extension point
    // 
    // 
    public abstract static class FishDescriptor extends Descriptor<ExtensionListTest.Fish> {}

    public abstract static class Fish implements Describable<ExtensionListTest.Fish> {
        public Descriptor<ExtensionListTest.Fish> getDescriptor() {
            return jenkins.model.Jenkins.getInstance().getDescriptor(getClass());
        }
    }

    public static class Tai extends ExtensionListTest.Fish {
        @Extension
        public static final class DescriptorImpl extends ExtensionListTest.FishDescriptor {}
    }

    public static class Saba extends ExtensionListTest.Fish {
        @Extension
        public static final class DescriptorImpl extends ExtensionListTest.FishDescriptor {}
    }

    public static class Sishamo extends ExtensionListTest.Fish {
        public static final class DescriptorImpl extends ExtensionListTest.FishDescriptor {}
    }

    /**
     * Verifies that the automated {@link Descriptor} lookup works.
     */
    @Test
    public void descriptorLookup() throws Exception {
        Descriptor<ExtensionListTest.Fish> d = new ExtensionListTest.Sishamo().getDescriptor();
        DescriptorExtensionList<ExtensionListTest.Fish, Descriptor<ExtensionListTest.Fish>> list = j.jenkins.<ExtensionListTest.Fish, Descriptor<ExtensionListTest.Fish>>getDescriptorList(ExtensionListTest.Fish.class);
        Assert.assertSame(d, list.get(ExtensionListTest.Sishamo.DescriptorImpl.class));
        Assert.assertSame(d, j.jenkins.getDescriptor(ExtensionListTest.Sishamo.class));
    }

    @Test
    public void fishDiscovery() throws Exception {
        // imagine that this is a static instance, like it is in many LIST static field in Hudson.
        DescriptorList<ExtensionListTest.Fish> LIST = new DescriptorList<ExtensionListTest.Fish>(ExtensionListTest.Fish.class);
        DescriptorExtensionList<ExtensionListTest.Fish, Descriptor<ExtensionListTest.Fish>> list = j.jenkins.<ExtensionListTest.Fish, Descriptor<ExtensionListTest.Fish>>getDescriptorList(ExtensionListTest.Fish.class);
        Assert.assertEquals(2, list.size());
        Assert.assertNotNull(list.get(ExtensionListTest.Tai.DescriptorImpl.class));
        Assert.assertNotNull(list.get(ExtensionListTest.Saba.DescriptorImpl.class));
        // registration can happen later, and it should be still visible
        LIST.add(new ExtensionListTest.Sishamo.DescriptorImpl());
        Assert.assertEquals(3, list.size());
        Assert.assertNotNull(list.get(ExtensionListTest.Sishamo.DescriptorImpl.class));
        // all 3 should be visible from LIST, too
        Assert.assertEquals(3, LIST.size());
        Assert.assertNotNull(LIST.findByName(ExtensionListTest.Tai.class.getName()));
        Assert.assertNotNull(LIST.findByName(ExtensionListTest.Sishamo.class.getName()));
        Assert.assertNotNull(LIST.findByName(ExtensionListTest.Saba.class.getName()));
        // DescriptorList can be gone and new one created but it should still have the same list
        LIST = new DescriptorList<ExtensionListTest.Fish>(ExtensionListTest.Fish.class);
        Assert.assertEquals(3, LIST.size());
        Assert.assertNotNull(LIST.findByName(ExtensionListTest.Tai.class.getName()));
        Assert.assertNotNull(LIST.findByName(ExtensionListTest.Sishamo.class.getName()));
        Assert.assertNotNull(LIST.findByName(ExtensionListTest.Saba.class.getName()));
    }

    @Test
    public void legacyDescriptorList() throws Exception {
        // created in a legacy fashion without any tie to ExtensionList
        DescriptorList<ExtensionListTest.Fish> LIST = new DescriptorList<ExtensionListTest.Fish>();
        // we won't auto-discover anything
        Assert.assertEquals(0, LIST.size());
        // registration can happen later, and it should be still visible
        LIST.add(new ExtensionListTest.Sishamo.DescriptorImpl());
        Assert.assertEquals(1, LIST.size());
        Assert.assertNotNull(LIST.findByName(ExtensionListTest.Sishamo.class.getName()));
        // create a new list and it forgets everything.
        LIST = new DescriptorList<ExtensionListTest.Fish>();
        Assert.assertEquals(0, LIST.size());
    }

    public static class Car implements ExtensionPoint {
        final String name;

        public Car(String name) {
            this.name = name;
        }
    }

    @Extension(ordinal = 1)
    public static class Toyota extends ExtensionListTest.Car {
        public Toyota() {
            super("toyota");
        }
    }

    @Extension(ordinal = 2)
    public static final ExtensionListTest.Car mazda = new ExtensionListTest.Car("mazda");

    /**
     * Makes sure sorting of the components work as expected.
     */
    @Test
    public void ordinals() {
        ExtensionList<ExtensionListTest.Car> list = j.jenkins.getExtensionList(ExtensionListTest.Car.class);
        Assert.assertEquals("honda", list.get(0).name);
        Assert.assertEquals("mazda", list.get(1).name);
        Assert.assertEquals("toyota", list.get(2).name);
    }

    @Issue("JENKINS-39520")
    @Test
    public void removeAll() {
        ExtensionList<ExtensionListTest.Animal> list = ExtensionList.lookup(ExtensionListTest.Animal.class);
        Assert.assertTrue(list.removeAll(new java.util.ArrayList(list)));
        Assert.assertEquals(0, list.size());
        Assert.assertFalse(list.removeAll(new java.util.ArrayList(list)));
        Assert.assertEquals(0, list.size());
    }
}

