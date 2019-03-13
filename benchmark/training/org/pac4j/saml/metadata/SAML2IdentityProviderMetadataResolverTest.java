package org.pac4j.saml.metadata;


import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import org.junit.Assert;
import org.junit.Test;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;


public class SAML2IdentityProviderMetadataResolverTest {
    private SAML2IdentityProviderMetadataResolver metadataResolver;

    @Test
    public void resolveMetadataEntityId() throws Exception {
        MetadataResolver resolver = metadataResolver.resolve();
        CriteriaSet criteria = new CriteriaSet(new EntityIdCriterion("mmoayyed.example.net"));
        final EntityDescriptor entity = resolver.resolveSingle(criteria);
        Assert.assertEquals(entity.getEntityID(), "mmoayyed.example.net");
    }

    @Test
    public void resolveMetadataDocumentAsString() {
        final String metadata = metadataResolver.getMetadata();
        Assert.assertNotNull(metadata);
    }
}

