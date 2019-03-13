package us.codecraft.webmagic;


import HttpConstant.Method.GET;
import HttpConstant.Method.POST;
import org.junit.Test;


/**
 *
 *
 * @author code4crafter@gmail.com
Date: 17/3/11
 */
public class RequestTest {
    @Test
    public void testEqualsAndHashCode() throws Exception {
        Request requestA = new Request("http://www.google.com/");
        Request requestB = new Request("http://www.google.com/");
        assertThat(requestA.hashCode()).isEqualTo(requestB.hashCode());
        assertThat(requestA).isEqualTo(requestB);
        requestA.setMethod(GET);
        requestA.setMethod(POST);
        assertThat(requestA).isNotEqualTo(requestB);
        assertThat(requestA.hashCode()).isNotEqualTo(requestB.hashCode());
    }
}

