package me.hupeng.java.video2text;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HUPENG on 2017/4/3.
 */
public class AudioUtil {
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



    private void convertVideoToAudio(String videoPath){
        File file = new File("tmp");
        if (!file.exists()){
            file.mkdir();
        }
        List<String> list = runShell("./ffmpeg/bin/ffmpeg.exe -i " + videoPath + "  -f s16be -ar 8000 -acodec pcm_s16be -vn  -ac 1 .\\tmp\\audio.pcm");
        for(String s: list){
            System.out.println(s);
        }
    }

    public static void main(String[] args){
        new AudioUtil().convertVideoToAudio("d:\\test\\2.mp4");
    }
}
