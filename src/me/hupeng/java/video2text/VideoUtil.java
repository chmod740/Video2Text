package me.hupeng.java.video2text;

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
    private String ffmpegPath = "./ffmpeg/bin/ffmpeg.exe";

    /**
     * 默认分割的视频长度<br>
     *
     * */
    private int videoSplitLength = 60;

    /**
     * 默认构造函数
     * */
    public VideoUtil(){}


    /**
     * 含参数的构造函数
     * 初始化ffmpeg的路径
     * */
    public VideoUtil(String ffmpegPath){
        setFfmpegPath(ffmpegPath);
    }

    private void setFfmpegPath(String ffmpegPath){
        this.ffmpegPath = ffmpegPath;
    }

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
//        convertVideoToAudio(videoPath);
        return null;
    }


    /**
     * 利用ffmpeg将视频文件转化成音频文件
     * @param videoPath     视频文件的路径
     * @return true         转换成功
     *          false        转换失败
     * */
    private boolean convertVideoToAudio(String videoPath, String outPath){
        //TODO:
        return false;
    }


    private boolean convertVideoToAudio(String dir, String inFile, String outFile){
        //TODO:
        File file = new File(dir);
        if (!file.exists()){
            file.mkdirs();
        }
        dir = dir.replace("/", "\\");

        List<String> list = runShell("./ffmpeg/bin/ffmpeg.exe -i " +
                dir + inFile +
                "  -f s16be -ar 8000 -acodec pcm_s16be -vn  -ac 1 .\\tmp\\audio.pcm");
//        for(String s: list){
//            System.out.println(s);
//        }
        file = new File("./tmp/audio.pcm");
        return file.exists();

    }

    /**
     * 对于音频文件进行分割
     * @param audioPath     音频文件的路径
     * */
    private boolean audioSplit(String audioPath){
        //TODO:
        File file = new File(audioPath);
        if (!file.exists()){
            return false;
        }
        String shell = "";
        return false;
    }


    /**
     * 对于视频文件进行分割
     * */
    private boolean videoSplit(String videoPath){
        //TODO:
        File file = new File(videoPath);
        if (!file.exists()){
            return false;
        }
        int videoTime = getVideoTime(videoPath);

        for (int i = 0 ; i < videoTime-videoSplitLength ; i=i+videoSplitLength){
            System.err.println("start:" + i + " end:" + (i + videoSplitLength));
        }
        System.err.println("start:" + ((videoTime-1)/videoSplitLength)*videoSplitLength + " end:" + videoTime);
        String shell = "";
        return false;
    }


    /**
     * 获取视频总时间
     * @param videoPath    视频路径
     * @return
     */
    private int getVideoTime(String videoPath) {
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
//        new VideoUtil().videoSplit("d:\\tmp\\1.mp4");
        String s = "1234";
        System.err.println(s.endsWith("134"));
    }
}

