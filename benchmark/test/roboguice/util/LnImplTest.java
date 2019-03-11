package roboguice.util;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class LnImplTest {
    @Test
    public void shouldFormatArgs_whenArgsAreNull() {
        // https://github.com/roboguice/roboguice/issues/223
        final String s = "Message: %s";
        final String expected = "Message: null";
        Assert.assertThat(new LnImpl().formatArgs(s, null), CoreMatchers.equalTo(expected));
    }

    @Test
    public void shouldFormatArgs_whenArgsIs0Length() {
        // https://github.com/roboguice/roboguice/issues/223
        final String s = "Message: %s";
        final String expected = "Message: %s";
        Assert.assertThat(new LnImpl().formatArgs(s), CoreMatchers.equalTo(expected));
    }
}

