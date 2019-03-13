package org.javaee7.validation.custom.constraint;


import javax.inject.Inject;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
public class CustomConstraintTest {
    @Inject
    MyBean bean;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void saveZipCodeforUs() {
        bean.saveZip("95051");
    }

    @Test
    public void saveZipCodeForIndia() {
        // thrown.equals(ConstraintViolationException.class);
        // thrown.expectMessage("javaee7.validation.custom.constraint.ZipCode");
        thrown.expectMessage("saveZipIndia.arg0");
        bean.saveZipIndia("95051");
    }
}

