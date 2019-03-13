package org.drools.compiler.builder.impl;


import java.io.File;
import java.io.IOException;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class KnowledgeBuilderImplTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testCreateDumpDrlGeneratedFileRemovingInvalidCharacters() throws Exception {
        final File dumpDir = temporaryFolder.getRoot();
        Assert.assertThat(KnowledgeBuilderImpl.createDumpDrlFile(dumpDir, "xxx", ".drl"), KnowledgeBuilderImplTest.fileEndsWith(((File.separator) + "xxx.drl")));
        Assert.assertThat(KnowledgeBuilderImpl.createDumpDrlFile(dumpDir, "x?x?", ".drl"), KnowledgeBuilderImplTest.fileEndsWith(((File.separator) + "x_x_.drl")));
        Assert.assertThat(KnowledgeBuilderImpl.createDumpDrlFile(dumpDir, "x/x/", ".drl"), KnowledgeBuilderImplTest.fileEndsWith(((File.separator) + "x_x_.drl")));
        Assert.assertThat(KnowledgeBuilderImpl.createDumpDrlFile(dumpDir, "x\\x\\", ".drl"), KnowledgeBuilderImplTest.fileEndsWith(((File.separator) + "x_x_.drl")));
        Assert.assertThat(KnowledgeBuilderImpl.createDumpDrlFile(dumpDir, "x*x*", ".drl"), KnowledgeBuilderImplTest.fileEndsWith(((File.separator) + "x_x_.drl")));
        Assert.assertThat(KnowledgeBuilderImpl.createDumpDrlFile(dumpDir, "aa.AA01-_", ".drl"), KnowledgeBuilderImplTest.fileEndsWith(((File.separator) + "aa.AA01-_.drl")));
    }

    private static class FileEndsWithMatcher extends BaseMatcher<File> {
        private final String endsWithString;

        private FileEndsWithMatcher(String endsWithString) {
            this.endsWithString = endsWithString;
        }

        @Override
        public boolean matches(Object item) {
            if (item instanceof File) {
                try {
                    return ((File) (item)).getCanonicalPath().endsWith(endsWithString);
                } catch (IOException e) {
                    return false;
                }
            } else {
                return false;
            }
        }

        @Override
        public void describeTo(Description description) {
            description.appendValue(endsWithString);
        }
    }
}

