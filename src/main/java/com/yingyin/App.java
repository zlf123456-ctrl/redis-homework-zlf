package com.yingyin;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class App {

    public Jedis connectredis(){
        Jedis jedis=new Jedis("localhost");
        jedis.auth("123456");
        return jedis;
    }

    public static void main(String[] args){

    }
    public PageInfo<String> getpagieValue(String hashname, int pagenumber, int pagesize)
    {
        Map<String,String> usermap=connectredis().hgetAll(hashname);
        List list=new ArrayList();
        for (String key : usermap.keySet())
        {
            list.add("key= " + key + " and value= " + usermap.get(key));
        }
        PageInfo<String> pageInfo=new PageInfo<String>(list,pagesize);
        return pageInfo;
    }
    public void test1(){

        connectredis().hset("books","java","think in java");
        connectredis().lpush("class","math","English");
        connectredis().sadd("NBA","勇士","骑士");
        connectredis().zadd("english:scoreboard",90,"张三");
        connectredis().zadd("english:scoreboard",91,"李四");
        connectredis().zadd("english:scoreboard",92,"王五");
        String name = connectredis().get("name");
        connectredis().close();
    }

    public void test2(){
        User user= new User();
        user.setName("johnson");
        user.setSex("male");
        String userStr = JSON.toJSONString(user);
        connectredis().set("user",userStr);
        String user1 = connectredis().get("user");
        System.out.println(user1);
        User user2 = JSON.parseObject(user1, User.class);
        System.out.println(user2);
        connectredis().close();
    }

    public void save(){
        Post post = new Post();
        post.setAuthor("johnson");
        post.setContent("这是我的博客");
        post.setTitle("博客");
        Long postId = SavePost(post,connectredis());
        GetPost(postId,connectredis());
        Post post1 = updateTitle(postId, connectredis());
        System.out.println(post1);
        deleteBlog(postId,connectredis());
        connectredis().close();
    }

    //保存博客
    public Long SavePost(Post post,Jedis jedis){
        Long postId = jedis.incr("posts");
        String myPost = JSON.toJSONString(post);
        jedis.set("post:"+postId+":data",myPost);
        return postId;
    }

    //获取博客
    public Post GetPost(Long postId,Jedis jedis){
        String getPost = jedis.get("post:" + postId + ":data");
        jedis.incr("post:" + postId + ":page.view");
        Post parseObject = JSON.parseObject(getPost, Post.class);
        System.out.println("这是第"+postId+"篇文章"+parseObject);
        return parseObject;
    }

    //修改标题
    public Post updateTitle(Long postId,Jedis jedis){
        Post post = GetPost(postId, jedis);
        post.setTitle("更改后的标题");
        String myPost = JSON.toJSONString(post);
        jedis.set("post:"+postId+":data",myPost);
        System.out.println("修改完成");
        return post;
    }
    //删除文章
    public void deleteBlog(Long postId,Jedis jedis){
        jedis.del("post:" + postId + ":data");
        jedis.del("post:"+postId+":page.view");
        System.out.println("删除成功");
    }
}
