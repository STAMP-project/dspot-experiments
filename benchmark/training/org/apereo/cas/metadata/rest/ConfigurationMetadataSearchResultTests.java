package org.apereo.cas.metadata.rest;


import lombok.val;
import org.apereo.cas.metadata.CasConfigurationMetadataRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link ConfigurationMetadataSearchResultTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class ConfigurationMetadataSearchResultTests {
    @Test
    public void verifyAction() {
        val repository = new CasConfigurationMetadataRepository();
        val properties = repository.getRepository().getAllProperties();
        val prop = properties.get("server.port");
        Assertions.assertNotNull(prop);
        val r = new ConfigurationMetadataSearchResult(prop, repository);
        Assertions.assertEquals(prop.getDefaultValue(), r.getDefaultValue());
        Assertions.assertEquals(prop.getId(), r.getId());
        Assertions.assertEquals(prop.getName(), r.getName());
        Assertions.assertEquals(prop.getType(), r.getType());
        Assertions.assertEquals(prop.getShortDescription(), r.getShortDescription());
        Assertions.assertEquals(prop.getDescription(), r.getDescription());
        Assertions.assertEquals(prop.getDefaultValue(), r.getDefaultValue());
        Assertions.assertNotNull(r.getGroup());
    }
}

