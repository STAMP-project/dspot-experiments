package spark;


import RouteImpl.DEFAULT_ACCEPT_TYPE;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;


public class RouteImplTest {
    private static final String PATH_TEST = "/opt/test";

    private static final String ACCEPT_TYPE_TEST = "*/test";

    private RouteImpl route;

    @Test
    public void testConstructor() {
        route = new RouteImpl(RouteImplTest.PATH_TEST) {
            @Override
            public Object handle(Request request, Response response) throws Exception {
                return null;
            }
        };
        Assert.assertEquals("Should return path specified", RouteImplTest.PATH_TEST, route.getPath());
    }

    @Test
    public void testGets_thenReturnGetPathAndGetAcceptTypeSuccessfully() throws Exception {
        route = RouteImpl.create(RouteImplTest.PATH_TEST, RouteImplTest.ACCEPT_TYPE_TEST, null);
        Assert.assertEquals("Should return path specified", RouteImplTest.PATH_TEST, route.getPath());
        Assert.assertEquals("Should return accept type specified", RouteImplTest.ACCEPT_TYPE_TEST, route.getAcceptType());
    }

    @Test
    public void testCreate_whenOutAssignAcceptTypeInTheParameters_thenReturnPathAndAcceptTypeSuccessfully() {
        route = RouteImpl.create(RouteImplTest.PATH_TEST, null);
        Assert.assertEquals("Should return path specified", RouteImplTest.PATH_TEST, route.getPath());
        Assert.assertEquals("Should return the default accept type", DEFAULT_ACCEPT_TYPE, route.getAcceptType());
    }

    @Test
    public void testCreate_whenAcceptTypeNullValueInTheParameters_thenReturnPathAndAcceptTypeSuccessfully() {
        route = RouteImpl.create(RouteImplTest.PATH_TEST, null, null);
        Assert.assertEquals("Should return path specified", RouteImplTest.PATH_TEST, route.getPath());
        Assert.assertEquals("Should return the default accept type", DEFAULT_ACCEPT_TYPE, route.getAcceptType());
    }

    @Test
    public void testRender_whenElementParameterValid_thenReturnValidObject() throws Exception {
        String finalObjValue = "object_value";
        route = RouteImpl.create(RouteImplTest.PATH_TEST, null);
        Object value = route.render(finalObjValue);
        Assert.assertNotNull("Should return an Object because we configured it to have one", value);
        Assert.assertEquals("Should return a string object specified", finalObjValue, value.toString());
    }

    @Test
    public void testRender_whenElementParameterIsNull_thenReturnNull() throws Exception {
        route = RouteImpl.create(RouteImplTest.PATH_TEST, null);
        Object value = route.render(null);
        TestCase.assertNull("Should return null because the element from render is null", value);
    }
}

