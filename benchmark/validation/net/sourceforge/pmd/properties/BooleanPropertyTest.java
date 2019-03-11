/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.properties;


import org.junit.Test;


/**
 *
 *
 * @author Brian Remedios
 */
public class BooleanPropertyTest extends AbstractPropertyDescriptorTester<Boolean> {
    public BooleanPropertyTest() {
        super("Boolean");
    }

    @Override
    @Test
    public void testErrorForBadSingle() {
        // override, cannot create a 'bad' boolean per se
    }

    @Override
    @Test
    public void testErrorForBadMulti() {
        // override, cannot create a 'bad' boolean per se
    }
}

