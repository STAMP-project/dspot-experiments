package org.jabref.logic.formatter;


import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import org.jabref.logic.protectedterms.ProtectedTermsLoader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class FormatterTest {
    private static ProtectedTermsLoader protectedTermsLoader;

    /**
     * When a new formatter is added by copy and pasting another formatter, it may happen that the <code>getKey()</code>
     * method is not adapted. This results in duplicate keys, which this test tests for.
     */
    @Test
    public void allFormatterKeysAreUnique() {
        // idea for uniqueness checking by https://stackoverflow.com/a/44032568/873282
        Assertions.assertEquals(Collections.emptyList(), FormatterTest.getFormatters().collect(Collectors.groupingBy(( formatter) -> formatter.getKey(), Collectors.counting())).entrySet().stream().filter(( e) -> (e.getValue()) > 1).map(Map.Entry::getKey).collect(Collectors.toList()));
    }
}

