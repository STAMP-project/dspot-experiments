package roboguice.inject;


import javax.annotation.Nullable;
import javax.annotation.roboguice.inject.Nullable;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class NullableTest {
    @Test
    public void shouldRejectNullFields() throws Exception {
        Assert.assertThat(roboguice.inject.Nullable.isNullable(NullableTest.DummyClass.class.getDeclaredField("notNullable")), CoreMatchers.is(false));
    }

    @Test
    public void shouldAcceptNonNullFields() throws Exception {
        Assert.assertThat(roboguice.inject.Nullable.isNullable(NullableTest.DummyClass.class.getDeclaredField("nullable")), CoreMatchers.is(true));
    }

    public static class DummyClass {
        protected Object notNullable;

        @Nullable
        protected Object nullable;
    }
}

