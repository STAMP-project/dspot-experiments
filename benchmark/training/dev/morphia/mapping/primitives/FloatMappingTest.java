package dev.morphia.mapping.primitives;


import dev.morphia.TestBase;
import dev.morphia.annotations.Id;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


public class FloatMappingTest extends TestBase {
    @Test
    public void testMapping() throws Exception {
        getMorphia().map(FloatMappingTest.Floats.class);
        final FloatMappingTest.Floats ent = new FloatMappingTest.Floats();
        ent.listWrapperArray.add(new Float[]{ 1.1F, 2.2F });
        ent.listPrimitiveArray.add(new float[]{ 2.0F, 3.6F, 12.4F });
        ent.listWrapper.addAll(Arrays.asList(1.1F, 2.2F));
        ent.singlePrimitive = 100.0F;
        ent.singleWrapper = 40.7F;
        ent.primitiveArray = new float[]{ 5.0F, 93.5F };
        ent.wrapperArray = new Float[]{ 55.7F, 16.2F, 99.9999F };
        ent.nestedPrimitiveArray = new float[][]{ new float[]{  }, new float[]{ 5.0F, 93.5F } };
        ent.nestedWrapperArray = new Float[][]{ new Float[]{ 55.7F, 16.2F, 99.9999F }, new Float[]{  } };
        getDs().save(ent);
        final FloatMappingTest.Floats loaded = getDs().get(ent);
        Assert.assertNotNull(loaded.id);
        Assert.assertArrayEquals(ent.listWrapperArray.get(0), loaded.listWrapperArray.get(0));
        Assert.assertEquals(ent.listWrapper, loaded.listWrapper);
        Assert.assertArrayEquals(ent.listPrimitiveArray.get(0), loaded.listPrimitiveArray.get(0), 0.0F);
        Assert.assertEquals(ent.singlePrimitive, loaded.singlePrimitive, 0);
        Assert.assertEquals(ent.singleWrapper, loaded.singleWrapper, 0);
        Assert.assertArrayEquals(ent.primitiveArray, loaded.primitiveArray, 0.0F);
        Assert.assertArrayEquals(ent.wrapperArray, loaded.wrapperArray);
        Assert.assertArrayEquals(ent.nestedPrimitiveArray, loaded.nestedPrimitiveArray);
        Assert.assertArrayEquals(ent.nestedWrapperArray, loaded.nestedWrapperArray);
    }

    private static class Floats {
        private final List<Float[]> listWrapperArray = new ArrayList<Float[]>();

        private final List<float[]> listPrimitiveArray = new ArrayList<float[]>();

        private final List<Float> listWrapper = new ArrayList<Float>();

        @Id
        private ObjectId id;

        private float singlePrimitive;

        private Float singleWrapper;

        private float[] primitiveArray;

        private Float[] wrapperArray;

        private float[][] nestedPrimitiveArray;

        private Float[][] nestedWrapperArray;
    }
}

