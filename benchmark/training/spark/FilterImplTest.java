package spark;


import RouteImpl.DEFAULT_ACCEPT_TYPE;
import org.junit.Assert;
import org.junit.Test;


public class FilterImplTest {
    public String PATH_TEST;

    public String ACCEPT_TYPE_TEST;

    public FilterImpl filter;

    @Test
    public void testConstructor() {
        FilterImpl filter = new FilterImpl(PATH_TEST, ACCEPT_TYPE_TEST) {
            @Override
            public void handle(Request request, Response response) throws Exception {
            }
        };
        Assert.assertEquals("Should return path specified", PATH_TEST, filter.getPath());
        Assert.assertEquals("Should return accept type specified", ACCEPT_TYPE_TEST, filter.getAcceptType());
    }

    @Test
    public void testGets_thenReturnGetPathAndGetAcceptTypeSuccessfully() throws Exception {
        filter = FilterImpl.create(PATH_TEST, ACCEPT_TYPE_TEST, null);
        Assert.assertEquals("Should return path specified", PATH_TEST, filter.getPath());
        Assert.assertEquals("Should return accept type specified", ACCEPT_TYPE_TEST, filter.getAcceptType());
    }

    @Test
    public void testCreate_whenOutAssignAcceptTypeInTheParameters_thenReturnPathAndAcceptTypeSuccessfully() {
        filter = FilterImpl.create(PATH_TEST, null);
        Assert.assertEquals("Should return path specified", PATH_TEST, filter.getPath());
        Assert.assertEquals("Should return accept type specified", DEFAULT_ACCEPT_TYPE, filter.getAcceptType());
    }

    @Test
    public void testCreate_whenAcceptTypeNullValueInTheParameters_thenReturnPathAndAcceptTypeSuccessfully() {
        filter = FilterImpl.create(PATH_TEST, null, null);
        Assert.assertEquals("Should return path specified", PATH_TEST, filter.getPath());
        Assert.assertEquals("Should return accept type specified", DEFAULT_ACCEPT_TYPE, filter.getAcceptType());
    }
}

