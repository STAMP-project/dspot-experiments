package handlebarsjs.spec;


import com.github.jknack.handlebars.AbstractTest;
import java.io.IOException;
import org.junit.Test;


public class HelperHashTest extends AbstractTest {
    @Test
    public void providingHelperHash() throws IOException {
        shouldCompileTo("Goodbye {{cruel}} {{world}}!", AbstractTest.$("cruel", "cruel"), AbstractTest.$("world", "world"), "Goodbye cruel world!", "helpers hash is available");
        shouldCompileTo("Goodbye {{#iter}}{{cruel}} {{world}}{{/iter}}!", AbstractTest.$("iter", new Object[]{ AbstractTest.$("cruel", "cruel") }), AbstractTest.$("world", "world"), "Goodbye cruel world!", "helpers hash is available inside other blocks");
    }
}

