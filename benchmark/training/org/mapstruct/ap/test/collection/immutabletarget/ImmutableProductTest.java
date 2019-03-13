/**
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.collection.immutabletarget;


import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.ap.testutil.IssueKey;
import org.mapstruct.ap.testutil.WithClasses;
import org.mapstruct.ap.testutil.runner.AnnotationProcessorTestRunner;


/**
 *
 *
 * @author Sjaak Derksen
 */
@RunWith(AnnotationProcessorTestRunner.class)
@WithClasses({ CupboardDto.class, CupboardEntity.class, CupboardMapper.class })
@IssueKey("1126")
public class ImmutableProductTest {
    @Test
    public void shouldHandleImmutableTarget() {
        CupboardDto in = new CupboardDto();
        in.setContent(Arrays.asList("cups", "soucers"));
        CupboardEntity out = new CupboardEntity();
        out.setContent(Collections.<String>emptyList());
        CupboardMapper.INSTANCE.map(in, out);
        assertThat(out.getContent()).isNotNull();
        assertThat(out.getContent()).containsExactly("cups", "soucers");
    }
}

