/**
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.source.constants;


import java.text.ParseException;
import java.util.Arrays;
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
public class SourceConstantsTest {
    @Test
    @IssueKey("187, 305")
    @WithClasses({ Source.class, Source2.class, Target.class, CountryEnum.class, SourceTargetMapper.class, StringListMapper.class })
    public void shouldMapSameSourcePropertyToSeveralTargetProperties() throws ParseException {
        Source source = new Source();
        source.setPropertyThatShouldBeMapped("SomeProperty");
        Target target = SourceTargetMapper.INSTANCE.sourceToTarget(source);
        assertThat(target).isNotNull();
        assertThat(target.getPropertyThatShouldBeMapped()).isEqualTo("SomeProperty");
        assertThat(target.getStringConstant()).isEqualTo("stringConstant");
        assertThat(target.getEmptyStringConstant()).isEqualTo("");
        assertThat(target.getIntegerConstant()).isEqualTo(14);
        assertThat(target.getLongWrapperConstant()).isEqualTo(new Long(3001L));
        assertThat(target.getDateConstant()).isEqualTo(getDate("dd-MM-yyyy", "09-01-2014"));
        assertThat(target.getNameConstants()).isEqualTo(Arrays.asList("jack", "jill", "tom"));
        assertThat(target.getCountry()).isEqualTo(CountryEnum.THE_NETHERLANDS);
    }

    @Test
    @IssueKey("187")
    @WithClasses({ Source.class, Target.class, CountryEnum.class, SourceTargetMapper.class, StringListMapper.class })
    public void shouldMapTargetToSourceWithoutWhining() throws ParseException {
        Target target = new Target();
        target.setPropertyThatShouldBeMapped("SomeProperty");
        Source source = SourceTargetMapper.INSTANCE.targetToSource(target);
        assertThat(source).isNotNull();
        assertThat(target.getPropertyThatShouldBeMapped()).isEqualTo("SomeProperty");
    }

    @Test
    @IssueKey("255")
    @WithClasses({ Source1.class, Source2.class, Target2.class, SourceTargetMapperSeveralSources.class })
    public void shouldMapSameSourcePropertyToSeveralTargetPropertiesFromSeveralSources() throws ParseException {
        Source1 source1 = new Source1();
        source1.setSomeProp("someProp");
        Source2 source2 = new Source2();
        source2.setAnotherProp("anotherProp");
        Target2 target = SourceTargetMapperSeveralSources.INSTANCE.sourceToTarget(source1, source2);
        assertThat(target).isNotNull();
        assertThat(target.getSomeProp()).isEqualTo("someProp");
        assertThat(target.getAnotherProp()).isEqualTo("anotherProp");
        assertThat(target.getSomeConstant()).isEqualTo("stringConstant");
    }
}

