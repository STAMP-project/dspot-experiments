package dev.morphia.mapping.validation.fieldrules;


import dev.morphia.TestBase;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Reference;
import dev.morphia.annotations.Serialized;
import dev.morphia.mapping.validation.ConstraintViolationException;
import dev.morphia.testutil.TestEntity;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;


/**
 *
 *
 * @author Uwe Schaefer, (us@thomas-daily.de)
 */
public class MapKeyDifferentFromStringTest extends TestBase {
    @Test
    public void testCheck() {
        getMorphia().map(MapKeyDifferentFromStringTest.MapWithWrongKeyType1.class);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testInvalidKeyType() {
        getMorphia().map(MapKeyDifferentFromStringTest.MapWithWrongKeyType3.class);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testInvalidReferenceType() {
        getMorphia().map(MapKeyDifferentFromStringTest.MapWithWrongKeyType2.class);
    }

    public static class MapWithWrongKeyType1 extends TestEntity {
        @Serialized
        private Map<Integer, Integer> shouldBeOk = new HashMap<Integer, Integer>();
    }

    public static class MapWithWrongKeyType2 extends TestEntity {
        @Reference
        private Map<Integer, Integer> shouldBeOk = new HashMap<Integer, Integer>();
    }

    public static class MapWithWrongKeyType3 extends TestEntity {
        @Embedded
        private Map<BigDecimal, Integer> shouldBeOk = new HashMap<BigDecimal, Integer>();
    }
}

