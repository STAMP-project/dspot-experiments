package io.confluent.ksql.function;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class UdfTemplateTest {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @SuppressWarnings("ConstantConditions")
    @Test
    public void testCoerceNumbers() {
        // Given:
        Object[] args = new Object[]{ 1, 1L, 1.0, 1.0F };
        // Then:
        for (int i = 0; i < (args.length); i++) {
            MatcherAssert.assertThat(UdfTemplate.coerce(args, int.class, i), Matchers.equalTo(1));
            MatcherAssert.assertThat(UdfTemplate.coerce(args, Integer.class, i), Matchers.equalTo(1));
            MatcherAssert.assertThat(UdfTemplate.coerce(args, long.class, i), Matchers.equalTo(1L));
            MatcherAssert.assertThat(UdfTemplate.coerce(args, Long.class, i), Matchers.equalTo(1L));
            MatcherAssert.assertThat(UdfTemplate.coerce(args, double.class, i), Matchers.equalTo(1.0));
            MatcherAssert.assertThat(UdfTemplate.coerce(args, Double.class, i), Matchers.equalTo(1.0));
        }
    }

    @Test
    public void testCoerceStrings() {
        // Given:
        Object[] args = new Object[]{ "1", "1.2", "true" };
        // Then:
        MatcherAssert.assertThat(UdfTemplate.coerce(args, int.class, 0), Matchers.equalTo(1));
        MatcherAssert.assertThat(UdfTemplate.coerce(args, Integer.class, 0), Matchers.equalTo(1));
        MatcherAssert.assertThat(UdfTemplate.coerce(args, long.class, 0), Matchers.equalTo(1L));
        MatcherAssert.assertThat(UdfTemplate.coerce(args, Long.class, 0), Matchers.equalTo(1L));
        MatcherAssert.assertThat(UdfTemplate.coerce(args, double.class, 1), Matchers.equalTo(1.2));
        MatcherAssert.assertThat(UdfTemplate.coerce(args, Double.class, 1), Matchers.equalTo(1.2));
        MatcherAssert.assertThat(UdfTemplate.coerce(args, boolean.class, 2), Matchers.is(true));
        MatcherAssert.assertThat(UdfTemplate.coerce(args, boolean.class, 2), Matchers.is(true));
    }

    @Test
    public void testCoerceBoxedNull() {
        // Given:
        Object[] args = new Object[]{ null };
        // Then:
        MatcherAssert.assertThat(UdfTemplate.coerce(args, Integer.class, 0), Matchers.nullValue());
        MatcherAssert.assertThat(UdfTemplate.coerce(args, Long.class, 0), Matchers.nullValue());
        MatcherAssert.assertThat(UdfTemplate.coerce(args, Double.class, 0), Matchers.nullValue());
        MatcherAssert.assertThat(UdfTemplate.coerce(args, String.class, 0), Matchers.nullValue());
        MatcherAssert.assertThat(UdfTemplate.coerce(args, Boolean.class, 0), Matchers.nullValue());
        MatcherAssert.assertThat(UdfTemplate.coerce(args, Map.class, 0), Matchers.nullValue());
        MatcherAssert.assertThat(UdfTemplate.coerce(args, List.class, 0), Matchers.nullValue());
    }

    @Test
    public void testCoercePrimitiveFailsNull() {
        // Given:
        Object[] args = new Object[]{ null };
        // Then:
        expectedException.expect(KsqlFunctionException.class);
        expectedException.expectMessage("from null to a primitive type");
        // When:
        UdfTemplate.coerce(args, int.class, 0);
    }

    @Test
    public void testCoerceObjects() {
        // Given:
        Object[] args = new Object[]{ new ArrayList<>(), new HashMap<>(), "" };
        // Then:
        MatcherAssert.assertThat(UdfTemplate.coerce(args, List.class, 0), Matchers.equalTo(new ArrayList()));
        MatcherAssert.assertThat(UdfTemplate.coerce(args, Map.class, 1), Matchers.equalTo(new HashMap()));
        MatcherAssert.assertThat(UdfTemplate.coerce(args, String.class, 2), Matchers.equalTo(""));
    }

    @Test
    public void testInvalidStringCoercion() {
        // Given:
        Object[] args = new Object[]{ "not a number" };
        // Then:
        expectedException.expect(KsqlFunctionException.class);
        expectedException.expectMessage("Couldn't coerce string");
        // When:
        UdfTemplate.coerce(args, int.class, 0);
    }

    @Test
    public void testInvalidNumberCoercion() {
        // Given:
        Object[] args = new Object[]{ 1 };
        // Then:
        expectedException.expect(KsqlFunctionException.class);
        expectedException.expectMessage("Couldn't coerce numeric");
        // When:
        UdfTemplate.coerce(args, Map.class, 0);
    }

    @Test
    public void testImpossibleCoercion() {
        // Given
        Object[] args = new Object[]{ ((Supplier) (() -> null)) };
        // Then:
        expectedException.expect(KsqlFunctionException.class);
        expectedException.expectMessage("Impossible to coerce");
        // When:
        UdfTemplate.coerce(args, int.class, 0);
    }
}

