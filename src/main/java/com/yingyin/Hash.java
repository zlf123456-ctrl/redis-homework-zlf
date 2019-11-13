package com.yingyin;

import com.alibaba.fastjson.JSON;
import redis.clients.jedis.Jedis;

import java.util.Map;

public class Hash {
    private static String postId;
    private static Map<String, String> blog;

    public static void main(String[] args) {
        Jedis jedis = new Jedis("localhost");
        Post post = new Post();
        post.setAuthor("smq");
        post.setContent("blog");
        post.setTitle("my blog");
        String postId = savePost(post, jedis);
        System.out.println("保存成功");
        Post myPost = getPost(postId, jedis);
        System.out.println(myPost);
    }

    private static Post getPost(String postId, Jedis jedis) {
        Map<String, String> myBlog = jedis.hgetAll("post:" + postId + ":data");
        Post post = new Post();
        post.setTitle(myBlog.get("title"));
        post.setContent(myBlog.get("content"));
        post.setAuthor(myBlog.get("author"));
        return post;
    }

    private static String savePost(Post post, Jedis jedis) {
        Map<String, String> myBlog = jedis.hgetAll("post:" + postId + ":data");
        blog.put("title", post.getTitle());
        blog.put("content", post.getContent());
        blog.put("author", post.getAuthor());
        jedis.hmset("post:" + postId + ":data", blog);
        return postId;

    }

    public Post updateTitle(String postId, Jedis jedis) {
        Post post = getPost(postId, jedis);
        post.setTitle("更改后的标题");
        String myPost = JSON.toJSONString(post);
        jedis.set("post:" + postId + ":data", myPost);
        System.out.println("修改完成");
        return post;
    }

    public void deleteBlog(Long postId, Jedis jedis) {
        jedis.del("post:" + postId + ":data");
        jedis.del("post:" + postId + ":page.view");
        System.out.println("删除成功");
    }
}
