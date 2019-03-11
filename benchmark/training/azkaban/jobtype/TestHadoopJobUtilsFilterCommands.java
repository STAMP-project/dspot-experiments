package azkaban.jobtype;


import HadoopJobUtils.MATCH_ALL_REGEX;
import HadoopJobUtils.MATCH_NONE_REGEX;
import java.util.Collections;
import java.util.List;
import junit.framework.Assert;
import org.apache.log4j.Logger;
import org.junit.Test;


/**
 * Test class for filterCommands method in HadoopJobUtils
 */
public class TestHadoopJobUtilsFilterCommands {
    private Logger logger = Logger.getRootLogger();

    private List<String> originalCommands;

    @Test
    public void testEmptyInputList() {
        List<String> filteredCommands = HadoopJobUtils.filterCommands(Collections.<String>emptyList(), MATCH_ALL_REGEX, MATCH_NONE_REGEX, logger);
        Assert.assertTrue("filtering output of an empty collection should be empty collection", filteredCommands.isEmpty());
    }

    @Test
    public void testNoCommandMatchCriteria() {
        List<String> filteredCommands = HadoopJobUtils.filterCommands(originalCommands, "hadoop.*", "hadoop.*", logger);
        Assert.assertTrue("filtering output of with no matching command should be empty collection", filteredCommands.isEmpty());
    }

    @Test
    public void testWhitelistCriteria() {
        List<String> filteredCommands = HadoopJobUtils.filterCommands(originalCommands, "hadoop.*", MATCH_NONE_REGEX, logger);
        Assert.assertEquals(filteredCommands.get(0), "hadoop fs -ls");
        Assert.assertEquals(filteredCommands.get(1), "hadoop fs -mkdir");
    }

    @Test
    public void testBlackListCriteria() {
        List<String> filteredCommands = HadoopJobUtils.filterCommands(originalCommands, MATCH_ALL_REGEX, ".*kinit.*", logger);
        Assert.assertEquals(filteredCommands.get(0), "hadoop fs -ls");
        Assert.assertEquals(filteredCommands.get(1), "hadoop fs -mkdir");
        Assert.assertEquals(filteredCommands.get(2), "kdestroy");
    }

    @Test
    public void testMultipleCriterias() {
        List<String> filteredCommands = HadoopJobUtils.filterCommands(originalCommands, "hadoop.*", ".*kinit.*", logger);
        Assert.assertEquals(filteredCommands.get(0), "hadoop fs -ls");
        Assert.assertEquals(filteredCommands.get(1), "hadoop fs -mkdir");
    }
}

