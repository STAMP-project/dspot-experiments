package dev.morphia.converters;


import dev.morphia.TestBase;
import dev.morphia.testutil.TestEntity;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Uwe Schaefer, (us@thomas-daily.de)
 */
public class NastyEnumTest extends TestBase {
    @Test
    public void testNastyEnumPersistence() throws Exception {
        NastyEnumTest.NastyEnumEntity n = new NastyEnumTest.NastyEnumEntity();
        getDs().save(n);
        n = getDs().get(n);
        Assert.assertSame(NastyEnumTest.NastyEnum.A, n.e1);
        Assert.assertSame(NastyEnumTest.NastyEnum.B, n.e2);
        Assert.assertNull(n.e3);
    }

    public enum NastyEnum {

        A() {
            @Override
            public String toString() {
                return "Never use toString for other purposes than debugging";
            }
        },
        B() {
            public String toString() {
                return "Never use toString for other purposes than debugging ";
            }
        };}

    public static class NastyEnumEntity extends TestEntity {
        private final NastyEnumTest.NastyEnum e1 = NastyEnumTest.NastyEnum.A;

        private final NastyEnumTest.NastyEnum e2 = NastyEnumTest.NastyEnum.B;

        private NastyEnumTest.NastyEnum e3;
    }
}

