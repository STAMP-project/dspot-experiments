package io.searchbox.core;


import ElasticsearchVersion.UNKNOWN;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Dogukan Sonmez
 */
public class ExplainTest {
    @Test
    public void explain() {
        Explain explain = new Explain.Builder("twitter", "tweet", "1", "query").build();
        Assert.assertEquals("POST", explain.getRestMethodName());
        Assert.assertEquals("twitter/tweet/1/_explain", explain.getURI(UNKNOWN));
        Assert.assertEquals("query", explain.getData(null));
    }

    @Test
    public void equals() {
        Explain explainUserKramer = new Explain.Builder("twitter", "tweet", "1", "{\"user\":\"kramer\"}").build();
        Explain explainUserKramerDuplicate = new Explain.Builder("twitter", "tweet", "1", "{\"user\":\"kramer\"}").build();
        Assert.assertEquals(explainUserKramer, explainUserKramerDuplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentQueries() {
        Explain explainUserKramer = new Explain.Builder("twitter", "tweet", "1", "{\"user\":\"kramer\"}").build();
        Explain explainUserJerry = new Explain.Builder("twitter", "tweet", "1", "{\"user\":\"jerry\"}").build();
        Assert.assertNotEquals(explainUserKramer, explainUserJerry);
    }
}

