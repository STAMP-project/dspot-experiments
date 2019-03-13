package com.reactnativenavigation.parse;


import LayoutNode.Type;
import LayoutNode.Type.Component;
import LayoutNode.Type.Stack;
import com.reactnativenavigation.BaseTest;
import com.reactnativenavigation.parse.parsers.LayoutNodeParser;
import org.junit.Test;


public class LayoutNodeParserTest extends BaseTest {
    @Test
    public void dto() throws Exception {
        LayoutNode node = new LayoutNode("the id", Type.Component);
        assertThat(node.id).isEqualTo("the id");
        assertThat(node.type).isEqualTo(Component);
        assertThat(node.data.keys()).isEmpty();
        assertThat(node.children).isEmpty();
    }

    @Test
    public void parseType() throws Exception {
        assertThat(Type.valueOf("Component")).isEqualTo(Component);
    }

    @Test(expected = RuntimeException.class)
    public void invalidType() throws Exception {
        Type.valueOf("some type");
    }

    @Test
    public void parseFromTree() throws Exception {
        JSONObject tree = new JSONObject(("{id: node1, " + (("type: Stack, " + "data: {dataKey: dataValue}, ") + "children: [{id: childId1, type: Component}]}")));
        LayoutNode result = LayoutNodeParser.parse(tree);
        assertThat(result).isNotNull();
        assertThat(result.id).isEqualTo("node1");
        assertThat(result.type).isEqualTo(Stack);
        assertThat(result.data.length()).isEqualTo(1);
        assertThat(result.data.getString("dataKey")).isEqualTo("dataValue");
        assertThat(result.children).hasSize(1);
        assertThat(result.children.get(0).id).isEqualTo("childId1");
        assertThat(result.children.get(0).type).isEqualTo(Component);
        assertThat(result.children.get(0).data.keys()).isEmpty();
        assertThat(result.children.get(0).children).isEmpty();
    }
}

