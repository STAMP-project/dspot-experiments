package hudson.model;


import BallColor.ABORTED_ANIME;
import BallColor.RED;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Kohsuke Kawaguchi
 */
public class BallColorTest {
    @Test
    public void htmlColor() {
        Assert.assertEquals("#EF2929", RED.getHtmlBaseColor());
    }

    @Test
    public void iconClassName() {
        Assert.assertEquals("icon-red", RED.getIconClassName());
        Assert.assertEquals("icon-aborted-anime", ABORTED_ANIME.getIconClassName());
    }
}

