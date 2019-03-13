/**
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.bugs._1111;


import java.util.Arrays;
import java.util.List;
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
@IssueKey("1111")
@WithClasses({ Issue1111Mapper.class })
@RunWith(AnnotationProcessorTestRunner.class)
public class Issue1111Test {
    @Test
    public void shouldCompile() {
        List<List<Issue1111Mapper.Source>> source = Arrays.asList(Arrays.asList(new Issue1111Mapper.Source()));
        List<List<Issue1111Mapper.Target>> target = Issue1111Mapper.INSTANCE.listList(source);
        assertThat(target).hasSize(1);
        assertThat(target.get(0)).hasSize(1);
        assertThat(target.get(0).get(0)).isInstanceOf(Issue1111Mapper.Target.class);
    }
}

