package com.github.jknack.handlebars.helper;


import com.github.jknack.handlebars.AbstractTest;
import java.io.IOException;
import org.junit.Test;


public class NumberHelperTest extends AbstractTest {
    @Test
    public void isOdd() throws IOException {
        shouldCompileTo("{{isOdd 3}}", $, "odd");
    }

    @Test
    public void isOddWithCustomValue() throws IOException {
        shouldCompileTo("{{isOdd 3 \"rightBox\"}}", $, "rightBox");
    }

    @Test
    public void isOddWithNullValueMustReturnsEmptyString() throws IOException {
        shouldCompileTo("{{isOdd nullreference}}", $, "");
    }

    @Test
    public void isEven() throws IOException {
        shouldCompileTo("{{isEven 2}}", $, "even");
    }

    @Test
    public void isEvenWithCustomValue() throws IOException {
        shouldCompileTo("{{isEven 4 \"leftBox\"}}", $, "leftBox");
    }

    @Test
    public void isEvenWithNullValueMustReturnsEmptyString() throws IOException {
        shouldCompileTo("{{isEven nullreference}}", $, "");
    }

    @Test
    public void stripes() throws IOException {
        shouldCompileTo("{{stripes 2}}", $, "even");
        shouldCompileTo("{{stripes 3}}", $, "odd");
    }

    @Test
    public void stripesCustomValue() throws IOException {
        shouldCompileTo("{{stripes 2 \"row-even\"}}", $, "row-even");
        shouldCompileTo("{{stripes 3 \"row-even\"}}", $, "odd");
        shouldCompileTo("{{stripes 3 \"row-even\" \"row-odd\"}}", $, "row-odd");
    }

    @Test
    public void stripesWithNullReference() throws IOException {
        shouldCompileTo("{{stripes nullReference}}", $, "");
    }
}

