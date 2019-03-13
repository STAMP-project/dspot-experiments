/**
 * The MIT License
 *
 * Copyright 2014 Jesse Glick.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package jenkins.model;


import hudson.Functions;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class RunIdMigratorTest {
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    private RunIdMigrator migrator;

    private File dir;

    @Test
    public void newJob() throws Exception {
        migrator.created(dir);
        Assert.assertEquals("{legacyIds=''}", summarize());
        Assert.assertEquals(0, migrator.findNumber("whatever"));
        migrator.delete(dir, "1");
        migrator = new RunIdMigrator();
        Assert.assertFalse(migrator.migrate(dir, null));
        Assert.assertEquals("{legacyIds=''}", summarize());
    }

    @Test
    public void legacy() throws Exception {
        Assume.assumeFalse("Symlinks don't work well on Windows", Functions.isWindows());
        write("2014-01-02_03-04-05/build.xml", "<?xml version=\'1.0\' encoding=\'UTF-8\'?>\n<run>\n  <stuff>ok</stuff>\n  <number>99</number>\n  <otherstuff>ok</otherstuff>\n</run>");
        link("99", "2014-01-02_03-04-05");
        link("lastFailedBuild", "-1");
        link("lastSuccessfulBuild", "99");
        Assert.assertEquals("{2014-01-02_03-04-05={build.xml=\'<?xml version=\'1.0\' encoding=\'UTF-8\'?>\n<run>\n  <stuff>ok</stuff>\n  <number>99</number>\n  <otherstuff>ok</otherstuff>\n</run>\'}, 99=\u21922014-01-02_03-04-05, lastFailedBuild=\u2192-1, lastSuccessfulBuild=\u219299}", summarize());
        Assert.assertTrue(migrator.migrate(dir, null));
        Assert.assertEquals("{99={build.xml=\'<?xml version=\'1.0\' encoding=\'UTF-8\'?>\n<run>\n  <stuff>ok</stuff>\n  <id>2014-01-02_03-04-05</id>\n  <timestamp>1388649845000</timestamp>\n  <otherstuff>ok</otherstuff>\n</run>\'}, lastFailedBuild=\u2192-1, lastSuccessfulBuild=\u219299, legacyIds=\'2014-01-02_03-04-05 99\n\'}", summarize());
        Assert.assertEquals(99, migrator.findNumber("2014-01-02_03-04-05"));
        migrator = new RunIdMigrator();
        Assert.assertFalse(migrator.migrate(dir, null));
        Assert.assertEquals(99, migrator.findNumber("2014-01-02_03-04-05"));
        migrator.delete(dir, "2014-01-02_03-04-05");
        FileUtils.deleteDirectory(new File(dir, "99"));
        new File(dir, "lastSuccessfulBuild").delete();
        Assert.assertEquals("{lastFailedBuild=?-1, legacyIds=''}", summarize());
    }

    @Test
    public void reRunMigration() throws Exception {
        Assume.assumeFalse("Symlinks don't work well on Windows", Functions.isWindows());
        write("2014-01-02_03-04-04/build.xml", "<run>\n  <number>98</number>\n</run>");
        link("98", "2014-01-02_03-04-04");
        write("99/build.xml", "<?xml version=\'1.0\' encoding=\'UTF-8\'?>\n<run>\n  <stuff>ok</stuff>\n  <timestamp>1388649845000</timestamp>\n  <otherstuff>ok</otherstuff>\n</run>");
        link("lastFailedBuild", "-1");
        link("lastSuccessfulBuild", "99");
        Assert.assertEquals("{2014-01-02_03-04-04={build.xml=\'<run>\n  <number>98</number>\n</run>\'}, 98=\u21922014-01-02_03-04-04, 99={build.xml=\'<?xml version=\'1.0\' encoding=\'UTF-8\'?>\n<run>\n  <stuff>ok</stuff>\n  <timestamp>1388649845000</timestamp>\n  <otherstuff>ok</otherstuff>\n</run>\'}, lastFailedBuild=\u2192-1, lastSuccessfulBuild=\u219299}", summarize());
        Assert.assertTrue(migrator.migrate(dir, null));
        Assert.assertEquals("{98={build.xml=\'<run>\n  <id>2014-01-02_03-04-04</id>\n  <timestamp>1388649844000</timestamp>\n</run>\'}, 99={build.xml=\'<?xml version=\'1.0\' encoding=\'UTF-8\'?>\n<run>\n  <stuff>ok</stuff>\n  <timestamp>1388649845000</timestamp>\n  <otherstuff>ok</otherstuff>\n</run>\'}, lastFailedBuild=\u2192-1, lastSuccessfulBuild=\u219299, legacyIds=\'2014-01-02_03-04-04 98\n\'}", summarize());
    }

    @Test
    public void reverseImmediately() throws Exception {
        Assume.assumeFalse("Symlinks don't work well on Windows", Functions.isWindows());
        File root = dir;
        dir = new File(dir, "jobs/somefolder/jobs/someproject/promotions/OK/builds");
        write("99/build.xml", "<?xml version=\'1.0\' encoding=\'UTF-8\'?>\n<run>\n  <stuff>ok</stuff>\n  <id>2014-01-02_03-04-05</id>\n  <timestamp>1388649845000</timestamp>\n  <otherstuff>ok</otherstuff>\n</run>");
        link("lastFailedBuild", "-1");
        link("lastSuccessfulBuild", "99");
        write("legacyIds", "2014-01-02_03-04-05 99\n");
        Assert.assertEquals("{99={build.xml=\'<?xml version=\'1.0\' encoding=\'UTF-8\'?>\n<run>\n  <stuff>ok</stuff>\n  <id>2014-01-02_03-04-05</id>\n  <timestamp>1388649845000</timestamp>\n  <otherstuff>ok</otherstuff>\n</run>\'}, lastFailedBuild=\u2192-1, lastSuccessfulBuild=\u219299, legacyIds=\'2014-01-02_03-04-05 99\n\'}", summarize());
        RunIdMigrator.main(root.getAbsolutePath());
        Assert.assertEquals("{2014-01-02_03-04-05={build.xml=\'<?xml version=\'1.0\' encoding=\'UTF-8\'?>\n<run>\n  <stuff>ok</stuff>\n  <number>99</number>\n  <otherstuff>ok</otherstuff>\n</run>\'}, 99=\u21922014-01-02_03-04-05, lastFailedBuild=\u2192-1, lastSuccessfulBuild=\u219299}", summarize());
    }

    @Test
    public void reverseAfterNewBuilds() throws Exception {
        Assume.assumeFalse("Symlinks don't work well on Windows", Functions.isWindows());
        File root = dir;
        dir = new File(dir, "jobs/someproject/modules/test$test/builds");
        write("1/build.xml", "<?xml version=\'1.0\' encoding=\'UTF-8\'?>\n<run>\n  <stuff>ok</stuff>\n  <timestamp>1388649845000</timestamp>\n  <otherstuff>ok</otherstuff>\n</run>");
        write("legacyIds", "");
        Assert.assertEquals("{1={build.xml=\'<?xml version=\'1.0\' encoding=\'UTF-8\'?>\n<run>\n  <stuff>ok</stuff>\n  <timestamp>1388649845000</timestamp>\n  <otherstuff>ok</otherstuff>\n</run>\'}, legacyIds=\'\'}", summarize());
        RunIdMigrator.main(root.getAbsolutePath());
        Assert.assertEquals("{1=\u21922014-01-02_03-04-05, 2014-01-02_03-04-05={build.xml=\'<?xml version=\'1.0\' encoding=\'UTF-8\'?>\n<run>\n  <stuff>ok</stuff>\n  <number>1</number>\n  <otherstuff>ok</otherstuff>\n</run>\'}}", summarize());
    }

    @Test
    public void reverseMatrixAfterNewBuilds() throws Exception {
        Assume.assumeFalse("Symlinks don't work well on Windows", Functions.isWindows());
        File root = dir;
        dir = new File(dir, "jobs/someproject/Environment=prod/builds");
        write("1/build.xml", "<?xml version=\'1.0\' encoding=\'UTF-8\'?>\n<run>\n  <stuff>ok</stuff>\n  <timestamp>1388649845000</timestamp>\n  <otherstuff>ok</otherstuff>\n</run>");
        write("legacyIds", "");
        Assert.assertEquals("{1={build.xml=\'<?xml version=\'1.0\' encoding=\'UTF-8\'?>\n<run>\n  <stuff>ok</stuff>\n  <timestamp>1388649845000</timestamp>\n  <otherstuff>ok</otherstuff>\n</run>\'}, legacyIds=\'\'}", summarize());
        RunIdMigrator.main(root.getAbsolutePath());
        Assert.assertEquals("{1=\u21922014-01-02_03-04-05, 2014-01-02_03-04-05={build.xml=\'<?xml version=\'1.0\' encoding=\'UTF-8\'?>\n<run>\n  <stuff>ok</stuff>\n  <number>1</number>\n  <otherstuff>ok</otherstuff>\n</run>\'}}", summarize());
    }

    @Test
    public void reverseMavenAfterNewBuilds() throws Exception {
        Assume.assumeFalse("Symlinks don't work well on Windows", Functions.isWindows());
        File root = dir;
        dir = new File(dir, "jobs/someproject/test$test/builds");
        write("1/build.xml", "<?xml version=\'1.0\' encoding=\'UTF-8\'?>\n<run>\n  <stuff>ok</stuff>\n  <timestamp>1388649845000</timestamp>\n  <otherstuff>ok</otherstuff>\n</run>");
        write("legacyIds", "");
        Assert.assertEquals("{1={build.xml=\'<?xml version=\'1.0\' encoding=\'UTF-8\'?>\n<run>\n  <stuff>ok</stuff>\n  <timestamp>1388649845000</timestamp>\n  <otherstuff>ok</otherstuff>\n</run>\'}, legacyIds=\'\'}", summarize());
        RunIdMigrator.main(root.getAbsolutePath());
        Assert.assertEquals("{1=\u21922014-01-02_03-04-05, 2014-01-02_03-04-05={build.xml=\'<?xml version=\'1.0\' encoding=\'UTF-8\'?>\n<run>\n  <stuff>ok</stuff>\n  <number>1</number>\n  <otherstuff>ok</otherstuff>\n</run>\'}}", summarize());
    }

    @Test
    public void move() throws Exception {
        File src = tmp.newFile();
        File dest = new File(tmp.getRoot(), "dest");
        RunIdMigrator.move(src, dest);
        File dest2 = tmp.newFile();
        try {
            RunIdMigrator.move(dest, dest2);
            Assert.fail();
        } catch (IOException x) {
            System.err.println(("Got expected move exception: " + x));
        }
    }
}

