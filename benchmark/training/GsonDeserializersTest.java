

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import saulmm.avengers.entities.CollectionItem;
import saulmm.avengers.entities.MarvelCharacter;
import saulmm.avengers.rest.utils.deserializers.MarvelResultsDeserializer;


public class GsonDeserializersTest {
    @Test
    public void testThatACharacterDeseralizersDeserializesACharacter() throws Exception {
        MarvelResultsDeserializer<MarvelCharacter> marvelResultsDeserializer = new MarvelResultsDeserializer();
        JsonElement characterElement = new JsonParser().parse(getCharacterJsonString());
        Type t = new TypeToken<List<MarvelCharacter>>() {}.getType();
        List<MarvelCharacter> characterList = marvelResultsDeserializer.deserialize(characterElement, t, Mockito.mock(JsonDeserializationContext.class));
        MatcherAssert.assertThat(characterList.isEmpty(), CoreMatchers.is(false));
        MatcherAssert.assertThat(characterList.get(0).getName(), CoreMatchers.is("3-D Man"));
    }

    @Test
    public void testThatACollectionDeserialzierDeserializesACollection() throws Exception {
        MarvelResultsDeserializer<CollectionItem> marvelResultsDeserializer = new MarvelResultsDeserializer();
        JsonElement collectionElement = new JsonParser().parse(getComicsCollectionJsonString());
        Type t = new TypeToken<List<CollectionItem>>() {}.getType();
        List<CollectionItem> collectionList = marvelResultsDeserializer.deserialize(collectionElement, t, Mockito.mock(JsonDeserializationContext.class));
        MatcherAssert.assertThat(collectionList.isEmpty(), CoreMatchers.is(false));
        Assert.assertNotNull(collectionList.get(0).getThumbnail());
    }
}

