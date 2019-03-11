/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.properties;


import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


/**
 * Evaluates the functionality of the EnumeratedProperty descriptor by testing
 * its ability to catch creation errors (illegal args), flag invalid selections,
 * and serialize/deserialize selection options.
 *
 * @author Brian Remedios
 */
@Deprecated
public class SimpleEnumeratedPropertyTest extends AbstractPropertyDescriptorTester<SimpleEnumeratedPropertyTest.Foo> {
    private static final String[] KEYS = new String[]{ "bar", "na", "bee", "coo" };

    private static final SimpleEnumeratedPropertyTest.Foo[] VALUES = new SimpleEnumeratedPropertyTest.Foo[]{ SimpleEnumeratedPropertyTest.Foo.BAR, SimpleEnumeratedPropertyTest.Foo.NA, SimpleEnumeratedPropertyTest.Foo.BEE, SimpleEnumeratedPropertyTest.Foo.COO };

    private static final Map<String, SimpleEnumeratedPropertyTest.Foo> MAPPINGS;

    static {
        Map<String, SimpleEnumeratedPropertyTest.Foo> map = new HashMap<>();
        map.put("bar", SimpleEnumeratedPropertyTest.Foo.BAR);
        map.put("na", SimpleEnumeratedPropertyTest.Foo.NA);
        map.put("bee", SimpleEnumeratedPropertyTest.Foo.BEE);
        map.put("coo", SimpleEnumeratedPropertyTest.Foo.COO);
        MAPPINGS = Collections.unmodifiableMap(map);
    }

    public SimpleEnumeratedPropertyTest() {
        super("Enum");
    }

    @Test
    public void testMappings() {
        EnumeratedPropertyDescriptor<SimpleEnumeratedPropertyTest.Foo, SimpleEnumeratedPropertyTest.Foo> prop = ((EnumeratedPropertyDescriptor<SimpleEnumeratedPropertyTest.Foo, SimpleEnumeratedPropertyTest.Foo>) (createProperty()));
        EnumeratedPropertyDescriptor<SimpleEnumeratedPropertyTest.Foo, List<SimpleEnumeratedPropertyTest.Foo>> multi = ((EnumeratedPropertyDescriptor<SimpleEnumeratedPropertyTest.Foo, List<SimpleEnumeratedPropertyTest.Foo>>) (createMultiProperty()));
        Assert.assertEquals(SimpleEnumeratedPropertyTest.MAPPINGS, prop.mappings());
        Assert.assertEquals(SimpleEnumeratedPropertyTest.MAPPINGS, multi.mappings());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDefaultIndexOutOfBounds() {
        new EnumeratedMultiProperty("testEnumerations", "Test enumerations with simple type", SimpleEnumeratedPropertyTest.KEYS, SimpleEnumeratedPropertyTest.VALUES, new int[]{ 99 }, SimpleEnumeratedPropertyTest.Foo.class, 1.0F);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoMappingForDefault() {
        new EnumeratedMultiProperty("testEnumerations", "Test enumerations with simple type", SimpleEnumeratedPropertyTest.MAPPINGS, Collections.singletonList(SimpleEnumeratedPropertyTest.Foo.IGNORED), SimpleEnumeratedPropertyTest.Foo.class, 1.0F);
    }

    @Test
    public void creationTest() {
        PropertyDescriptor<SimpleEnumeratedPropertyTest.Foo> prop = createProperty();
        PropertyDescriptor<List<SimpleEnumeratedPropertyTest.Foo>> multi = createMultiProperty();
        for (Map.Entry<String, SimpleEnumeratedPropertyTest.Foo> e : SimpleEnumeratedPropertyTest.MAPPINGS.entrySet()) {
            Assert.assertEquals(e.getValue(), prop.valueFrom(e.getKey()));
            Assert.assertTrue(multi.valueFrom(e.getKey()).contains(e.getValue()));
        }
    }

    @Override
    @Test
    public void testFactorySingleValue() {
        Assume.assumeTrue("The EnumeratedProperty factory is not implemented yet", false);
    }

    @Override
    @Test
    public void testFactoryMultiValueCustomDelimiter() {
        Assume.assumeTrue("The EnumeratedProperty factory is not implemented yet", false);
    }

    @Override
    @Test
    public void testFactoryMultiValueDefaultDelimiter() {
        Assume.assumeTrue("The EnumeratedProperty factory is not implemented yet", false);
    }

    enum Foo {

        BAR,
        NA,
        BEE,
        COO,
        IGNORED;}
}

