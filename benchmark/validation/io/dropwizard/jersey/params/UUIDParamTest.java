package io.dropwizard.jersey.params;


import java.util.UUID;
import org.junit.jupiter.api.Test;


public class UUIDParamTest {
    @Test
    public void aUUIDStringReturnsAUUIDObject() {
        final String uuidString = "067e6162-3b6f-4ae2-a171-2470b63dff00";
        final UUID uuid = UUID.fromString(uuidString);
        final UUIDParam param = new UUIDParam(uuidString);
        assertThat(param.get()).isEqualTo(uuid);
    }

    @Test
    public void noSpaceUUID() {
        UuidParamNegativeTest("067e61623b6f4ae2a1712470b63dff00");
    }

    @Test
    public void tooLongUUID() {
        UuidParamNegativeTest("067e6162-3b6f-4ae2-a171-2470b63dff000");
    }

    @Test
    public void aNonUUIDThrowsAnException() {
        UuidParamNegativeTest("foo");
    }
}

