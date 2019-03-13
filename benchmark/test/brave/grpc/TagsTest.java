package brave.grpc;


import brave.grpc.GrpcPropagation.Tags;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Test;


public class TagsTest {
    @Test
    public void extractTags_movesMethodToParentField() {
        Map<String, String> extracted = new LinkedHashMap<>();
        extracted.put("method", "helloworld.Greeter/SayHello");
        Tags tags = GrpcPropagation.extractTags(extracted);
        assertThat(tags.parentMethod).isEqualTo("helloworld.Greeter/SayHello");
        assertThat(tags.get("method")).isNull();
    }

    @Test
    public void inheritsParentMethod() {
        Map<String, String> extracted = new LinkedHashMap<>();
        extracted.put("method", "helloworld.Greeter/SayHello");
        Tags parent = GrpcPropagation.extractTags(extracted);
        Tags child = new Tags(parent);
        assertThat(child.parentMethod).isEqualTo(parent.parentMethod);
    }
}

