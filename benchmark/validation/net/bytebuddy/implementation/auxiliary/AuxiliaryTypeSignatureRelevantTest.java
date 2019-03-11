package net.bytebuddy.implementation.auxiliary;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Modifier;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class AuxiliaryTypeSignatureRelevantTest {
    @Test
    public void testClassRetention() throws Exception {
        Assert.assertThat(AuxiliaryType.SignatureRelevant.class.getAnnotation(Retention.class).value(), CoreMatchers.is(RetentionPolicy.CLASS));
    }

    @Test
    public void testTypeTarget() throws Exception {
        Assert.assertThat(AuxiliaryType.SignatureRelevant.class.getAnnotation(Target.class).value(), CoreMatchers.is(new ElementType[]{ ElementType.TYPE }));
    }

    @Test
    public void testModifiers() throws Exception {
        Assert.assertThat(Modifier.isPublic(AuxiliaryType.SignatureRelevant.class.getModifiers()), CoreMatchers.is(true));
    }
}

