/**
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.nestedbeans;


import java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.ap.test.nestedbeans.recursive.RecursionMapper;
import org.mapstruct.ap.test.nestedbeans.recursive.TreeRecursionMapper;
import org.mapstruct.ap.testutil.IssueKey;
import org.mapstruct.ap.testutil.WithClasses;
import org.mapstruct.ap.testutil.runner.AnnotationProcessorTestRunner;


@RunWith(AnnotationProcessorTestRunner.class)
public class RecursionTest {
    @WithClasses({ RecursionMapper.class })
    @Test
    @IssueKey("1103")
    public void testRecursiveAutoMap() {
        RecursionMapper.RootDto rootDto = new RecursionMapper.RootDto(new RecursionMapper.ChildDto("Sub Root", new RecursionMapper.ChildDto("Sub child", null)));
        RecursionMapper.Root root = RecursionMapper.INSTANCE.mapRoot(rootDto);
        assertRootEquals(rootDto, root);
    }

    @WithClasses({ TreeRecursionMapper.class })
    @Test
    @IssueKey("1103")
    public void testRecursiveTreeAutoMap() {
        TreeRecursionMapper.RootDto rootDto = new TreeRecursionMapper.RootDto(Collections.singletonList(new TreeRecursionMapper.ChildDto("Sub Root", Collections.singletonList(new TreeRecursionMapper.ChildDto("Sub child", null)))));
        TreeRecursionMapper.Root root = TreeRecursionMapper.INSTANCE.mapRoot(rootDto);
        assertTreeRootEquals(rootDto, root);
    }
}

