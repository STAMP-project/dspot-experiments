package com.baeldung.modelmapper;


import com.baeldung.modelmapper.dto.PostDto;
import com.baeldung.modelmapper.model.Post;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.modelmapper.ModelMapper;


public class PostDtoUnitTest {
    private ModelMapper modelMapper = new ModelMapper();

    @Test
    public void whenConvertPostEntityToPostDto_thenCorrect() {
        Post post = new Post();
        post.setId(Long.valueOf(1));
        post.setTitle(RandomStringUtils.randomAlphabetic(6));
        post.setUrl("www.test.com");
        PostDto postDto = modelMapper.map(post, PostDto.class);
        Assert.assertEquals(post.getId(), postDto.getId());
        Assert.assertEquals(post.getTitle(), postDto.getTitle());
        Assert.assertEquals(post.getUrl(), postDto.getUrl());
    }

    @Test
    public void whenConvertPostDtoToPostEntity_thenCorrect() {
        PostDto postDto = new PostDto();
        postDto.setId(Long.valueOf(1));
        postDto.setTitle(RandomStringUtils.randomAlphabetic(6));
        postDto.setUrl("www.test.com");
        Post post = modelMapper.map(postDto, Post.class);
        Assert.assertEquals(postDto.getId(), post.getId());
        Assert.assertEquals(postDto.getTitle(), post.getTitle());
        Assert.assertEquals(postDto.getUrl(), post.getUrl());
    }
}

