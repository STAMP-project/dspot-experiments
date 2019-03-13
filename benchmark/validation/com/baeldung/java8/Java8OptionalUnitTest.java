package com.baeldung.java8;


import com.baeldung.java_8_features.CustomException;
import com.baeldung.java_8_features.OptionalUser;
import com.baeldung.java_8_features.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;


public class Java8OptionalUnitTest {
    private List<String> list;

    @Test
    public void checkOptional_whenAsExpected_thenCorrect() {
        Optional<String> optionalEmpty = Optional.empty();
        Assert.assertFalse(optionalEmpty.isPresent());
        String str = "value";
        Optional<String> optional = Optional.of(str);
        Assert.assertEquals(optional.get(), "value");
        Optional<String> optionalNullable = Optional.ofNullable(str);
        Optional<String> optionalNull = Optional.ofNullable(null);
        Assert.assertEquals(optionalNullable.get(), "value");
        Assert.assertFalse(optionalNull.isPresent());
        List<String> listOpt = Optional.of(list).orElse(new ArrayList<>());
        List<String> listNull = null;
        List<String> listOptNull = Optional.ofNullable(listNull).orElse(new ArrayList<>());
        Assert.assertTrue((listOpt == (list)));
        Assert.assertTrue(listOptNull.isEmpty());
        Optional<User> user = Optional.ofNullable(getUser());
        String result = user.map(User::getAddress).map(Address::getStreet).orElse("not specified");
        Assert.assertEquals(result, "1st Avenue");
        Optional<OptionalUser> optionalUser = Optional.ofNullable(getOptionalUser());
        String resultOpt = optionalUser.flatMap(OptionalUser::getAddress).flatMap(OptionalAddress::getStreet).orElse("not specified");
        Assert.assertEquals(resultOpt, "1st Avenue");
        Optional<User> userNull = Optional.ofNullable(getUserNull());
        String resultNull = userNull.map(User::getAddress).map(Address::getStreet).orElse("not specified");
        Assert.assertEquals(resultNull, "not specified");
        Optional<OptionalUser> optionalUserNull = Optional.ofNullable(getOptionalUserNull());
        String resultOptNull = optionalUserNull.flatMap(OptionalUser::getAddress).flatMap(OptionalAddress::getStreet).orElse("not specified");
        Assert.assertEquals(resultOptNull, "not specified");
    }

    @Test(expected = CustomException.class)
    public void callMethod_whenCustomException_thenCorrect() {
        User user = new User();
        String result = user.getOrThrow();
    }
}

