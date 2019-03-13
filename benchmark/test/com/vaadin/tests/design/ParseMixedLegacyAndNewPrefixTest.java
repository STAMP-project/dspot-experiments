package com.vaadin.tests.design;


import com.vaadin.ui.declarative.Design;
import java.io.ByteArrayInputStream;
import org.junit.Test;


/**
 * Parse mixed content with legacy and new prefixes (not a required feature but
 * works).
 */
public class ParseMixedLegacyAndNewPrefixTest {
    @Test
    public void parseMixedContent() {
        Design.read(new ByteArrayInputStream("<v-vertical-layout><vaadin-label /></v-vertical-layout>".getBytes()));
    }
}

