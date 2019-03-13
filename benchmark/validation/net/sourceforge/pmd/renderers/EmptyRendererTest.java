/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.renderers;


import org.junit.Test;


public class EmptyRendererTest extends AbstractRendererTst {
    // Overriding the annotation from the super class, this renderer doesn't care, so no NPE.
    @Test
    @Override
    public void testNullPassedIn() throws Exception {
        super.testNullPassedIn();
    }
}

