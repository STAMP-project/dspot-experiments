package org.javaee7.jacc.contexts;


import java.io.IOException;
import org.javaee7.jaspic.common.ArquillianBase;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xml.sax.SAXException;


/**
 * This test demonstrates both how to obtain the {@link Subject} and then how to indirectly retrieve the roles from
 * this Subject.
 * <p>
 * This will be done by calling a Servlet that retrieves the {@link Subject} of the authenticated user.
 * Authentication happens via the {@link TestServerAuthModule} when the "doLogin" parameter is present in
 * the request.
 * <p>
 * The Subject (a "bag of principals") typically contains the user name principal that can also be obtained via
 * {@link HttpServletRequest#getUserPrincipal()} and may contain the roles this user has. But, it has never
 * been standardized in Java EE which principal (attribute) of the Subject represents the user name and
 * which ones represent the roles, so every container does it differently. On top of that JACC did not specify
 * that *the* Subject should be returned and implementations not rarely return a Subject that contains only the
 * user name principal instead of the real Subject with all principals.
 * <p>
 * Via a somewhat obscure workaround via JACC it's possible to fetch the roles from the Subject
 * in a standard way.
 * See <a href="https://blogs.oracle.com/monzillo/entry/using_jacc_to_determine_a">Using JACC to determine a caller's roles</a>
 * <p>
 *
 * @author Arjan Tijms
 */
@RunWith(Arquillian.class)
public class SubjectFromPolicyContextTest extends ArquillianBase {
    /**
     * Tests that we are able to obtain a reference to the {@link Subject} from a Servlet.
     */
    @Test
    public void testCanObtainRequestInServlet() throws IOException, SAXException {
        String response = getFromServerPath("subjectServlet?doLogin=true");
        Assert.assertTrue(response.contains("Obtained subject from context."));
    }

    /**
     * Tests that we are able to obtain a reference to the {@link Subject} from a Servlet and
     * use JACC to get the roles the user from its principals.
     */
    @Test
    public void testCanObtainRolesFromSubjectInServlet() throws IOException, SAXException {
        String response = getFromServerPath("subjectServlet?doLogin=true");
        // The role that was assigned to the user in TestServerAuthModule
        Assert.assertTrue(response.contains("User has role architect"));
        // Servlet 13.3; Every authenticated user should have this role and isUserInRole should return true
        // when tested.
        Assert.assertTrue(response.contains("User has role **"));
    }
}

