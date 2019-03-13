package org.javaee7.jaxrs.resource.validation;


import java.net.URL;
import javax.ws.rs.client.WebTarget;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
public class NameAddResourceTest {
    @ArquillianResource
    private URL base;

    private WebTarget target;

    @Test
    public void shouldPassNameValidation() throws Exception {
        assertStatus(postName(startValidName()), OK);
    }

    @Test
    public void shouldFailAtFirstNameSizeValidation() throws Exception {
        Name name = startValidName();
        name.setFirstName("");
        assertFailedValidation(postName(name));
    }

    @Test
    public void shouldFailAtFirstNameNullValidation() throws Exception {
        Name name = startValidName();
        name.setFirstName(null);
        assertFailedValidation(postName(name));
    }

    @Test
    public void shouldFailAtLastNameSizeValidation() throws Exception {
        Name name = startValidName();
        name.setLastName("");
        assertFailedValidation(postName(name));
    }

    @Test
    public void shouldFailAtLastNameNullValidation() throws Exception {
        Name name = startValidName();
        name.setLastName(null);
        assertFailedValidation(postName(name));
    }

    @Test
    public void shouldFailAtEmailAtSymbolValidation() throws Exception {
        Name name = startValidName();
        name.setEmail("missing-at-symbol.com");
        assertFailedValidation(postName(name));
    }

    @Test
    public void shouldFailAtEmailComDomainValidation() throws Exception {
        Name name = startValidName();
        name.setEmail("other-than-com@domain.pl");
        assertFailedValidation(postName(name));
    }
}

