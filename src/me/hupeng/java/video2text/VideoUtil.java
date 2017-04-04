package me.hupeng.java.video2text;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HUPENG on 2017/4/3.
 * @author HUPENG
 * 视频文件处理类
 */
public class VideoUtil{
    private List<String> runShell(String cmd){
        Process process = null;
        List<String> processList = new ArrayList<String>();
        try {
            if (cmd!=null) {
                process = Runtime.getRuntime().exec(cmd);
                BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line = "";
                while ((line = input.readLine()) != null) {
                    processList.add(line);
                }
                input.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return processList;
    }

    public String videoToText(String videoPath) {
        convertVideoToAudio(videoPath);
        return null;
    }


    /**
     * 利用ffmpeg将视频文件转化成音频文件
     * @param videoPath     视频文件的路径
     * */
    private boolean convertVideoToAudio(String videoPath){
        File file = new File("tmp");
        if (!file.exists()){
            file.mkdir();
        }
        List<String> list = runShell("./ffmpeg/bin/ffmpeg.exe -i " + videoPath + "  -f s16be -ar 8000 -acodec pcm_s16be -vn  -ac 1 .\\tmp\\audio.pcm");
//        for(String s: list){
//            System.out.println(s);
//        }
        file = new File("./tmp/audio.pcm");
        return file.exists();
    }

    public static void main(String[] args){
        new VideoUtil().convertVideoToAudio("d:\\test\\2.mp4");
    }
}
