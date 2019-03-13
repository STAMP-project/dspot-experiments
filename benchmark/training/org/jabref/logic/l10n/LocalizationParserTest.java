package org.jabref.logic.l10n;


import java.util.Arrays;
import org.junit.jupiter.api.Test;


public class LocalizationParserTest {
    @Test
    public void testKeyParsingCode() {
        assertLocalizationKeyParsing("Localization.lang(\"one per line\")", "one\\ per\\ line");
        assertLocalizationKeyParsing("Localization.lang(\n            \"Copy \\\\cite{BibTeX key}\")", "Copy\\ \\cite{BibTeX\\ key}");
        assertLocalizationKeyParsing("Localization.lang(\"two per line\") Localization.lang(\"two per line\")", Arrays.asList("two\\ per\\ line", "two\\ per\\ line"));
        assertLocalizationKeyParsing("Localization.lang(\"multi \" + \n\"line\")", "multi\\ line");
        assertLocalizationKeyParsing("Localization.lang(\"one per line with var\", var)", "one\\ per\\ line\\ with\\ var");
        assertLocalizationKeyParsing("Localization.lang(\"Search %0\", \"Springer\")", "Search\\ %0");
        assertLocalizationKeyParsing("Localization.lang(\"Reset preferences (key1,key2,... or \'all\')\")", "Reset\\ preferences\\ (key1,key2,...\\ or\\ \'all\')");
        assertLocalizationKeyParsing("Localization.lang(\"Multiple entries selected. Do you want to change the type of all these to \'%0\'?\")", "Multiple\\ entries\\ selected.\\ Do\\ you\\ want\\ to\\ change\\ the\\ type\\ of\\ all\\ these\\ to\\ \'%0\'?");
        assertLocalizationKeyParsing("Localization.lang(\"Run fetcher, e.g. \\\"--fetch=Medline:cancer\\\"\");", "Run\\ fetcher,\\ e.g.\\ \"--fetch\\=Medline\\:cancer\"");
    }

    @Test
    public void testParameterParsingCode() {
        assertLocalizationParameterParsing("Localization.lang(\"one per line\")", "\"one per line\"");
        assertLocalizationParameterParsing("Localization.lang(\"one per line\" + var)", "\"one per line\" + var");
        assertLocalizationParameterParsing("Localization.lang(var + \"one per line\")", "var + \"one per line\"");
        assertLocalizationParameterParsing("Localization.lang(\"Search %0\", \"Springer\")", "\"Search %0\", \"Springer\"");
    }
}

