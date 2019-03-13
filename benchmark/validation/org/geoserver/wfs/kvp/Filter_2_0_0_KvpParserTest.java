/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs.kvp;


import org.junit.Test;


/**
 * Tests for {@link Filter_2_0_0_KvpParser}, the parser for Filter 2.0 in KVP requests.
 *
 * @author Ben Caradoc-Davies (Transient Software Limited)
 */
public class Filter_2_0_0_KvpParserTest {
    /**
     * Test that Filter 2.0 {@code fes:PropertyIsLike} with an ASCII literal can be parsed from
     * percent-encoded form into a {@link PropertyIsLike} object.
     */
    @Test
    public void testPropertyIsLikeAsciiLiteral() throws Exception {
        Filter_2_0_0_KvpParserTest.parsePropertyIsLike("Illino*", "Illino*", null);
    }

    /**
     * Test that Filter 2.0 {@code fes:PropertyIsLike} with a non-ASCII literal can be parsed from
     * percent-encoded form into a {@link PropertyIsLike} object.
     */
    @Test
    public void testPropertyIsLikeNonAsciiLiteral() throws Exception {
        Filter_2_0_0_KvpParserTest.parsePropertyIsLike("?*", "%C3%BC*", null);
    }

    /**
     * Test that Filter 2.0 {@code fes:PropertyIsLike} with {@code matchCase="true"} can be parsed
     * from percent-encoded form into a {@link PropertyIsLike} object.
     */
    @Test
    public void testPropertyIsLikeMatchCaseTrue() throws Exception {
        Filter_2_0_0_KvpParserTest.parsePropertyIsLike("Illino*", "Illino*", true);
    }

    /**
     * Test that Filter 2.0 {@code fes:PropertyIsLike} with {@code matchCase="false"} can be parsed
     * from percent-encoded form into a {@link PropertyIsLike} object.
     */
    @Test
    public void testPropertyIsLikeMatchCaseFalse() throws Exception {
        Filter_2_0_0_KvpParserTest.parsePropertyIsLike("Illino*", "Illino*", false);
    }
}

