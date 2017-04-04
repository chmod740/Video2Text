package me.hupeng.java.video2text;

/**
 * Created by HUPENG on 2017/4/4.
 * 添加一个谷歌风格Builder
 */
public class VideoUtilBuilder {
    private VideoUtil videoUtil;

    public VideoUtilBuilder(){
        videoUtil = new VideoUtil();
    }

    /**
     * 设置ffmpeg.exe的文件存储位置
     * */
    public VideoUtilBuilder setFfmpegPath(String ffmpegPath){
        videoUtil.setFfmpegPath(ffmpegPath);
        return this;
    }

    /**
     * 设置视频分割的长度
     * */
    public VideoUtilBuilder setVideoSplitLength(int videoSplitLength){
        videoUtil.setVideoSplitLength(videoSplitLength);
        return this;
    }

    /**
     * 设置临时目录
     * */
    public VideoUtilBuilder setTempDir(String tempDir){
        videoUtil.setTempDir(tempDir);
        return this;
    }

    /**
     * 返回实例对象
     * */
    public VideoUtil build(){
        return videoUtil;
    }
}
