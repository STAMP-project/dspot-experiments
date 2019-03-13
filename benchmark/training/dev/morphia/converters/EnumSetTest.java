package dev.morphia.converters;


import dev.morphia.TestBase;
import dev.morphia.query.Query;
import dev.morphia.testutil.TestEntity;
import java.util.EnumSet;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Uwe Schaefer, (us@thomas-daily.de)
 */
public class EnumSetTest extends TestBase {
    @Test
    public void testNastyEnumPersistence() throws Exception {
        EnumSetTest.NastyEnumEntity n = new EnumSetTest.NastyEnumEntity();
        getDs().save(n);
        n = getDs().get(n);
        Assert.assertNull(n.isNull);
        Assert.assertNotNull(n.empty);
        Assert.assertNotNull(n.in);
        Assert.assertNotNull(n.out);
        Assert.assertEquals(0, n.empty.size());
        Assert.assertEquals(3, n.in.size());
        Assert.assertEquals(1, n.out.size());
        Assert.assertTrue(n.in.contains(EnumSetTest.NastyEnum.B));
        Assert.assertTrue(n.in.contains(EnumSetTest.NastyEnum.C));
        Assert.assertTrue(n.in.contains(EnumSetTest.NastyEnum.D));
        Assert.assertFalse(n.in.contains(EnumSetTest.NastyEnum.A));
        Assert.assertTrue(n.out.contains(EnumSetTest.NastyEnum.A));
        Assert.assertFalse(n.out.contains(EnumSetTest.NastyEnum.B));
        Assert.assertFalse(n.out.contains(EnumSetTest.NastyEnum.C));
        Assert.assertFalse(n.out.contains(EnumSetTest.NastyEnum.D));
        Query<EnumSetTest.NastyEnumEntity> q = getDs().find(EnumSetTest.NastyEnumEntity.class).filter("in", EnumSetTest.NastyEnum.C);
        Assert.assertEquals(1, q.count());
        q = getDs().find(EnumSetTest.NastyEnumEntity.class).filter("out", EnumSetTest.NastyEnum.C);
        Assert.assertEquals(0, q.count());
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
        },
        C,
        D;}

    public static class NastyEnumEntity extends TestEntity {
        private final EnumSet<EnumSetTest.NastyEnum> in = EnumSet.of(EnumSetTest.NastyEnum.B, EnumSetTest.NastyEnum.C, EnumSetTest.NastyEnum.D);

        private final EnumSet<EnumSetTest.NastyEnum> out = EnumSet.of(EnumSetTest.NastyEnum.A);

        private final EnumSet<EnumSetTest.NastyEnum> empty = EnumSet.noneOf(EnumSetTest.NastyEnum.class);

        private EnumSet<EnumSetTest.NastyEnum> isNull;
    }
}

