/**
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.source.defaultExpressions.java;


import java.util.Date;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.ap.testutil.WithClasses;
import org.mapstruct.ap.testutil.runner.AnnotationProcessorTestRunner;


/**
 *
 *
 * @author Jeffrey Smyth
 */
@RunWith(AnnotationProcessorTestRunner.class)
public class JavaDefaultExpressionTest {
    @Test
    @WithClasses({ Source.class, Target.class, SourceTargetMapper.class })
    public void testJavaDefaultExpressionWithValues() {
        Source source = new Source();
        source.setId(123);
        source.setDate(new Date(0L));
        Target target = SourceTargetMapper.INSTANCE.sourceToTarget(source);
        assertThat(target).isNotNull();
        assertThat(target.getSourceId()).isEqualTo("123");
        assertThat(target.getSourceDate()).isEqualTo(source.getDate());
    }

    @Test
    @WithClasses({ Source.class, Target.class, SourceTargetMapper.class })
    public void testJavaDefaultExpressionWithNoValues() {
        Source source = new Source();
        Target target = SourceTargetMapper.INSTANCE.sourceToTarget(source);
        assertThat(target).isNotNull();
        assertThat(target.getSourceId()).isEqualTo("test");
        assertThat(target.getSourceDate()).isEqualTo(new Date(30L));
    }
}

