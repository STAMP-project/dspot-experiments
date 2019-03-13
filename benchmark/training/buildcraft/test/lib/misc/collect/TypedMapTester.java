package buildcraft.test.lib.misc.collect;


import buildcraft.lib.misc.collect.TypedMap;
import org.junit.Assert;
import org.junit.Test;


public class TypedMapTester {
    interface IRandomInterface {
        int getRandom();
    }

    interface INamedInterface {
        String getName();
    }

    class Independant {}

    enum RandomE implements TypedMapTester.IRandomInterface {

        A,
        B;
        @Override
        public int getRandom() {
            return ordinal();
        }
    }

    enum Both implements TypedMapTester.INamedInterface , TypedMapTester.IRandomInterface {

        A,
        B;
        @Override
        public String getName() {
            return name();
        }

        @Override
        public int getRandom() {
            return 15 + (ordinal());
        }
    }

    @Test
    public void testDirect() {
        TypedMap<Object> map = new buildcraft.lib.misc.collect.TypedMapDirect();
        map.put(TypedMapTester.RandomE.A);
        map.put(TypedMapTester.Both.A);
        TypedMapTester.Independant i = new TypedMapTester.Independant();
        map.put(i);
        Assert.assertEquals(null, map.get(TypedMapTester.IRandomInterface.class));
        Assert.assertEquals(null, map.get(TypedMapTester.INamedInterface.class));
        Assert.assertEquals(i, map.get(TypedMapTester.Independant.class));
        Assert.assertEquals(TypedMapTester.RandomE.A, map.get(TypedMapTester.RandomE.class));
        Assert.assertEquals(TypedMapTester.Both.A, map.get(TypedMapTester.Both.class));
        map.put(TypedMapTester.RandomE.B);
        Assert.assertEquals(TypedMapTester.RandomE.B, map.get(TypedMapTester.RandomE.class));
        map.remove(TypedMapTester.RandomE.B);
        Assert.assertEquals(null, map.get(TypedMapTester.RandomE.class));
    }

    @Test
    public void testMult() {
        TypedMap<Object> map = new buildcraft.lib.misc.collect.TypedMapHierarchy();
        map.put(TypedMapTester.RandomE.A);
        map.put(TypedMapTester.Both.A);
        TypedMapTester.Independant i = new TypedMapTester.Independant();
        map.put(i);
        Assert.assertNotEquals(null, map.get(TypedMapTester.IRandomInterface.class));
        Assert.assertEquals(TypedMapTester.Both.A, map.get(TypedMapTester.INamedInterface.class));
        Assert.assertEquals(i, map.get(TypedMapTester.Independant.class));
        Assert.assertEquals(TypedMapTester.RandomE.A, map.get(TypedMapTester.RandomE.class));
        Assert.assertEquals(TypedMapTester.Both.A, map.get(TypedMapTester.Both.class));
    }
}

