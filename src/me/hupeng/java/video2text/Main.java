package me.hupeng.java.video2text;

/**
 * Created by HUPENG on 2017/4/4.
 */
public class Main {
    public static void main(String[] args){
        VideoUtil videoUtil = new VideoUtilBuilder().setTempDir("d:\\temp").build();
        String text = videoUtil.videoToText("d:\\2.mp4");
        System.out.println(text);
    }

}
