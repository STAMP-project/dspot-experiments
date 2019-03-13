/**
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.source.constants;


import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.ap.testutil.WithClasses;
import org.mapstruct.ap.testutil.runner.AnnotationProcessorTestRunner;
import org.mapstruct.ap.testutil.runner.GeneratedSource;


/**
 *
 *
 * @author Sjaak Derksen
 */
@RunWith(AnnotationProcessorTestRunner.class)
@WithClasses({ ConstantsMapper.class, ConstantsTarget.class })
public class ConstantsTest {
    @Rule
    public final GeneratedSource generatedSrc = new GeneratedSource().addComparisonToFixtureFor(ConstantsMapper.class);

    @Test
    public void testNumericConstants() {
        ConstantsTarget target = ConstantsMapper.INSTANCE.mapFromConstants("dummy");
        assertThat(target).isNotNull();
        assertThat(target.isBooleanValue()).isEqualTo(true);
        assertThat(target.getBooleanBoxed()).isEqualTo(false);
        assertThat(target.getCharValue()).isEqualTo('b');
        assertThat(target.getCharBoxed()).isEqualTo('a');
        assertThat(target.getByteValue()).isEqualTo(((byte) (20)));
        assertThat(target.getByteBoxed()).isEqualTo(((byte) (-128)));
        assertThat(target.getShortValue()).isEqualTo(((short) (1996)));
        assertThat(target.getShortBoxed()).isEqualTo(((short) (-1996)));
        assertThat(target.getIntValue()).isEqualTo((-1048575));
        assertThat(target.getIntBoxed()).isEqualTo(15);
        assertThat(target.getLongValue()).isEqualTo(9223372036854775807L);
        assertThat(target.getLongBoxed()).isEqualTo(3405691582L);
        assertThat(target.getFloatValue()).isEqualTo(1.4E-45F);
        assertThat(target.getFloatBoxed()).isEqualTo(3.4028235E38F);
        assertThat(target.getDoubleValue()).isEqualTo(1.0E137);
        assertThat(target.getDoubleBoxed()).isEqualTo(4.9E-324);
        assertThat(target.getDoubleBoxedZero()).isEqualTo(0.0);
    }
}

