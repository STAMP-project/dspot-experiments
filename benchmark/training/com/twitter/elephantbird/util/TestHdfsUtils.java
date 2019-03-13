package com.twitter.elephantbird.util;


import PathFilters.ACCEPT_ALL_PATHS_FILTER;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Alex Levenson
 */
public class TestHdfsUtils {
    private static final String SAMPLE_DIR_LOCATION = "src/test/resources/com/twitter/elephantbird/util/";

    private static final Pattern SAMPLE_DIR_PATTERN = Pattern.compile(((".*/" + (TestHdfsUtils.SAMPLE_DIR_LOCATION)) + "(.*)"));

    private static final Function<Path, String> PATH_TO_RELATIVE_STRING = new Function<Path, String>() {
        @Override
        public String apply(Path path) {
            Matcher m = TestHdfsUtils.SAMPLE_DIR_PATTERN.matcher(path.toString());
            m.matches();
            return m.group(1);
        }
    };

    private static final PathFilter SKIP_A_PATH_FILTER = new PathFilter() {
        @Override
        public boolean accept(Path path) {
            return !(path.getName().equals("a.txt"));
        }
    };

    @Test
    public void testCollectPathsWithDirs() throws Exception {
        List<Path> accumulator = Lists.newLinkedList();
        Path p = new Path(((TestHdfsUtils.SAMPLE_DIR_LOCATION) + "sample_dir"));
        HdfsUtils.collectPaths(p, p.getFileSystem(new Configuration()), TestHdfsUtils.SKIP_A_PATH_FILTER, accumulator);
        Set<String> expected = Sets.newHashSet("sample_dir", "sample_dir/b.txt", "sample_dir/nested", "sample_dir/nested/c.txt", "sample_dir/nested/d.txt", "sample_dir/nested/double_nested", "sample_dir/nested/double_nested/e.txt");
        Set<String> found = Sets.newHashSet(Iterables.transform(accumulator, TestHdfsUtils.PATH_TO_RELATIVE_STRING));
        Assert.assertEquals(expected, found);
    }

    @Test
    public void testCollectPathsWithoutDirs() throws Exception {
        List<Path> accumulator = Lists.newLinkedList();
        Configuration conf = new Configuration();
        Path p = new Path(((TestHdfsUtils.SAMPLE_DIR_LOCATION) + "sample_dir"));
        HdfsUtils.collectPaths(p, p.getFileSystem(conf), new PathFilters.CompositePathFilter(PathFilters.newExcludeDirectoriesFilter(conf), TestHdfsUtils.SKIP_A_PATH_FILTER), accumulator);
        Set<String> expected = Sets.newHashSet("sample_dir/b.txt", "sample_dir/nested/c.txt", "sample_dir/nested/d.txt", "sample_dir/nested/double_nested/e.txt");
        Set<String> found = Sets.newHashSet(Iterables.transform(accumulator, TestHdfsUtils.PATH_TO_RELATIVE_STRING));
        Assert.assertEquals(expected, found);
    }

    @Test
    public void testGetDirectorySize() throws Exception {
        Path p = new Path(((TestHdfsUtils.SAMPLE_DIR_LOCATION) + "sample_dir"));
        long size = HdfsUtils.getDirectorySize(p, p.getFileSystem(new Configuration()));
        Assert.assertEquals(18, size);
    }

    @Test
    public void testGetDirectorySizeWithFilter() throws Exception {
        Path p = new Path(((TestHdfsUtils.SAMPLE_DIR_LOCATION) + "sample_dir"));
        long size = HdfsUtils.getDirectorySize(p, p.getFileSystem(new Configuration()), ACCEPT_ALL_PATHS_FILTER);
        Assert.assertEquals(18, size);
        size = HdfsUtils.getDirectorySize(p, p.getFileSystem(new Configuration()), TestHdfsUtils.SKIP_A_PATH_FILTER);
        Assert.assertEquals(16, size);
    }

    @Test
    public void testExpandGlobs() throws Exception {
        List<Path> paths = HdfsUtils.expandGlobs(Lists.newArrayList(((TestHdfsUtils.SAMPLE_DIR_LOCATION) + "sample_dir/*.txt")), new Configuration());
        Assert.assertEquals(Lists.newArrayList("sample_dir/a.txt", "sample_dir/b.txt"), Lists.transform(paths, TestHdfsUtils.PATH_TO_RELATIVE_STRING));
        paths = HdfsUtils.expandGlobs(Lists.newArrayList(((TestHdfsUtils.SAMPLE_DIR_LOCATION) + "sample_dir/a.txt")), new Configuration());
        Assert.assertEquals(Lists.newArrayList("sample_dir/a.txt"), Lists.transform(paths, TestHdfsUtils.PATH_TO_RELATIVE_STRING));
        paths = HdfsUtils.expandGlobs(Lists.newArrayList(((TestHdfsUtils.SAMPLE_DIR_LOCATION) + "sample_dir/*.txt"), ((TestHdfsUtils.SAMPLE_DIR_LOCATION) + "sample_dir/*nes*/*.txt")), new Configuration());
        Assert.assertEquals(Lists.newArrayList("sample_dir/a.txt", "sample_dir/b.txt", "sample_dir/nested/c.txt", "sample_dir/nested/d.txt"), Lists.transform(paths, TestHdfsUtils.PATH_TO_RELATIVE_STRING));
    }
}

