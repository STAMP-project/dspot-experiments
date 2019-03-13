package com.baeldung.deepcopy;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import java.io.IOException;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.Test;


public class DeepCopyUnitTest {
    @Test
    public void whenCreatingDeepCopyWithCopyConstructor_thenObjectsShouldNotBeSame() {
        Address address = new Address("Downing St 10", "London", "England");
        User pm = new User("Prime", "Minister", address);
        User deepCopy = new User(pm);
        assertThat(deepCopy).isNotSameAs(pm);
    }

    @Test
    public void whenModifyingOriginalObject_thenConstructorCopyShouldNotChange() {
        Address address = new Address("Downing St 10", "London", "England");
        User pm = new User("Prime", "Minister", address);
        User deepCopy = new User(pm);
        address.setCountry("Great Britain");
        assertThat(deepCopy.getAddress().getCountry()).isNotEqualTo(pm.getAddress().getCountry());
    }

    @Test
    public void whenModifyingOriginalObject_thenCloneCopyShouldNotChange() {
        Address address = new Address("Downing St 10", "London", "England");
        User pm = new User("Prime", "Minister", address);
        User deepCopy = ((User) (pm.clone()));
        address.setCountry("Great Britain");
        assertThat(deepCopy.getAddress().getCountry()).isNotEqualTo(pm.getAddress().getCountry());
    }

    @Test
    public void whenModifyingOriginalObject_thenCommonsCloneShouldNotChange() {
        Address address = new Address("Downing St 10", "London", "England");
        User pm = new User("Prime", "Minister", address);
        User deepCopy = ((User) (SerializationUtils.clone(pm)));
        address.setCountry("Great Britain");
        assertThat(deepCopy.getAddress().getCountry()).isNotEqualTo(pm.getAddress().getCountry());
    }

    @Test
    public void whenModifyingOriginalObject_thenGsonCloneShouldNotChange() {
        Address address = new Address("Downing St 10", "London", "England");
        User pm = new User("Prime", "Minister", address);
        Gson gson = new Gson();
        User deepCopy = gson.fromJson(gson.toJson(pm), User.class);
        address.setCountry("Great Britain");
        assertThat(deepCopy.getAddress().getCountry()).isNotEqualTo(pm.getAddress().getCountry());
    }

    @Test
    public void whenModifyingOriginalObject_thenJacksonCopyShouldNotChange() throws IOException {
        Address address = new Address("Downing St 10", "London", "England");
        User pm = new User("Prime", "Minister", address);
        ObjectMapper objectMapper = new ObjectMapper();
        User deepCopy = objectMapper.readValue(objectMapper.writeValueAsString(pm), User.class);
        address.setCountry("Great Britain");
        assertThat(deepCopy.getAddress().getCountry()).isNotEqualTo(pm.getAddress().getCountry());
    }
}

