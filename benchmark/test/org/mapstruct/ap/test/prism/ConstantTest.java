/**
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.prism;


import org.junit.Test;


/**
 * Test constants values
 *
 * @author Sjaak Derksen
 */
public class ConstantTest {
    @Test
    public void constantsShouldBeEqual() {
        assertThat(MappingConstants.ANY_REMAINING).isEqualTo(MappingConstantsPrism.ANY_REMAINING);
        assertThat(MappingConstants.ANY_UNMAPPED).isEqualTo(MappingConstantsPrism.ANY_UNMAPPED);
        assertThat(MappingConstants.NULL).isEqualTo(MappingConstantsPrism.NULL);
    }
}

