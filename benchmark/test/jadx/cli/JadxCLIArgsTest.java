package jadx.cli;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JadxCLIArgsTest {
    private static final Logger LOG = LoggerFactory.getLogger(JadxCLIArgsTest.class);

    @Test
    public void testInvertedBooleanOption() {
        Assert.assertThat(parse("--no-replace-consts").isReplaceConsts(), Matchers.is(false));
        Assert.assertThat(parse("").isReplaceConsts(), Matchers.is(true));
    }

    @Test
    public void testEscapeUnicodeOption() {
        Assert.assertThat(parse("--escape-unicode").isEscapeUnicode(), Matchers.is(true));
        Assert.assertThat(parse("").isEscapeUnicode(), Matchers.is(false));
    }

    @Test
    public void testSrcOption() {
        Assert.assertThat(parse("--no-src").isSkipSources(), Matchers.is(true));
        Assert.assertThat(parse("-s").isSkipSources(), Matchers.is(true));
        Assert.assertThat(parse("").isSkipSources(), Matchers.is(false));
    }

    @Test
    public void testOptionsOverride() {
        Assert.assertThat(override(new JadxCLIArgs(), "--no-imports").isUseImports(), Matchers.is(false));
        Assert.assertThat(override(new JadxCLIArgs(), "").isUseImports(), Matchers.is(true));
        JadxCLIArgs args = new JadxCLIArgs();
        args.useImports = false;
        Assert.assertThat(override(args, "--no-imports").isUseImports(), Matchers.is(false));
        args = new JadxCLIArgs();
        args.useImports = false;
        Assert.assertThat(override(args, "").isUseImports(), Matchers.is(false));
    }
}

