package me.hupeng.java.video2text;

import com.baidu.speech.serviceapi.BaiduVoice;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by HUPENG on 2017/4/3.
 * @author HUPENG
 * 视频文件处理类
 */
public class VideoUtil{
    /**
     * 默认的ffmpeg.exe的文件存储位置
     * */
    private String ffmpegPath = ".\\ffmpeg\\bin\\ffmpeg.exe";

    /**
     * 默认分割的视频长度<br>
     *
     * */
    private int videoSplitLength = 60;

    /**
     * 默认构造函数
     * */
    public VideoUtil(){}


    public void setFfmpegPath(String ffmpegPath){
        this.ffmpegPath = ffmpegPath;
    }

    public void setVideoSplitLength(int videoSplitLength){
        this.videoSplitLength = videoSplitLength;
    }

    /**
     * 运行命令行，并返回结果
     * */
    private List<String> runShell(String cmd){
        Process process = null;
        List<String> processList = new ArrayList<String>();
        try {
            if (cmd!=null) {
                process = Runtime.getRuntime().exec(cmd);
                BufferedReader input = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));
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
        //TODO:

        return null;
    }


    /**
     * 利用ffmpeg将视频文件转化成音频文件
     * @param inFile        视频文件的名称
     * @param outFile       输出文件的名称
     * @return true         转换成功
     *          false        转换失败
     * */
    private boolean convertVideoToMP3Audio(String inFile, String outFile){
        convertVideoToMP3Audio("d:\\tmp", inFile,outFile);
        return false;
    }


    private boolean convertVideoToMP3Audio(String dir, String inFile, String outFile){
        File file = new File(dir);
        if (!file.exists()){
            file.mkdirs();
        }
        dir = dir.replace("/", "\\");
        if (!dir.endsWith("\\")){
            dir += "\\";
        }

        List<String> list = runShell( "cmd /c "+
                ffmpegPath +
                " -i " +
                dir + inFile +
                "  -f mp3 " + dir + outFile + " -y" +
                " >nul 2>nul");
        file = new File(dir + outFile);
        return file.exists();
    }


    /**
     * 对于视频文件进行分割
     * .\ffmpeg.exe  -ss 0 -i d:\tmp\1.mp3 -t 60  -f s16be -ar 8000 -acodec pcm_s16be -vn  -ac 1 d:\tmp\audio_1.pcm
     * */
    private List<String> videoOrAudioSplit(String dir,String inFile){
        //TODO:

        dir = dir.replace("/", "\\");
        if (!dir.endsWith("\\")){
            dir += "\\";
        }

        File file = new File(dir + inFile);
        if (!file.exists()){
            return null;
        }

        int videoTime = getVideoOrAudioTime(dir + inFile);
        List<String> list = new ArrayList<>();
        int j = 0;
        for (int i = 0 ; i < videoTime-videoSplitLength ; j++, i=i+videoSplitLength){
            runShell("cmd /c "+
                    ffmpegPath +
                    " -ss " +
                    i +
                    " -i "+
                    dir +
                    inFile +
                    " -t " +
                    videoSplitLength +
                    "  -f s16be -ar 8000 -acodec pcm_s16be -vn  -ac 1 " +
                    dir +
                    "audio_" +
                    j +
                    ".pcm -y"
            );
//            System.err.println("start:" + i + " end:" + (i + videoSplitLength));
            list.add("d:\\tmp\\audio_" + j + ".pcm");
        }
        runShell("cmd /c "+
                ffmpegPath +
                " -ss " +
                ((videoTime-1)/videoSplitLength)*videoSplitLength +
                " -i "+
                dir +
                inFile +
                " -t " +
                videoSplitLength +
                "  -f s16be -ar 8000 -acodec pcm_s16be -vn  -ac 1 " +
                dir +
                "audio_" +
                j +
                ".pcm -y"
        );
//        System.err.println("start:" + ((videoTime-1)/videoSplitLength)*videoSplitLength + " end:" + videoTime);
        String shell = "";
        return list;
    }



    /**
     * 获取视频总时间
     * @param videoPath    视频路径
     * @return
     */
    private int getVideoOrAudioTime(String videoPath) {
        List<String> commands = new java.util.ArrayList<String>();
        commands.add(ffmpegPath);
        commands.add("-i");
        commands.add(videoPath);
        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(commands);
            final Process p = builder.start();

            //从输入流中读取视频信息
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();

            //从视频信息中解析时长
            String regexDuration = "Duration: (.*?), start: (.*?), bitrate: (\\d*) kb\\/s";
            Pattern pattern = Pattern.compile(regexDuration);
            Matcher m = pattern.matcher(sb.toString());
            if (m.find()) {
                int time = getTimelen(m.group(1));
                System.err.println(videoPath+",视频时长："+time+", 开始时间："+m.group(2)+",比特率："+m.group(3)+"kb/s");
                return time;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 格式:"00:00:10.68"
     *
     * */
    private int getTimelen(String timelen){
        int min=0;
        String strs[] = timelen.split(":");
        if (strs[0].compareTo("0") > 0) {
            min+=Integer.valueOf(strs[0])*60*60;//秒
        }
        if(strs[1].compareTo("0")>0){
            min+=Integer.valueOf(strs[1])*60;
        }
        if(strs[2].compareTo("0")>0){
            min+=Math.round(Float.valueOf(strs[2]));
        }
        return min;
    }


    public static void main(String[] args){
        new VideoUtil().convertVideoToMP3Audio("4.mp4","4.mp3");
       List<String> list  = new VideoUtil().videoOrAudioSplit("d:\\tmp\\","4.mp3");
        for (String i: list) {
            System.err.println(i);
        }
        try {
            String text = BaiduVoice.audioToText("d:\\tmp\\audio_0.pcm");
            System.out.println(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}