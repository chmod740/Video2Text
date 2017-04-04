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
     * 默认的临时目录
     * */
    private String tempDir = "D:\\tmp\\";

    /**
     * 默认构造函数
     * */
    public VideoUtil(){}

    /**
     * ffmpeg的bin目录
     * */
    public void setFfmpegPath(String ffmpegPath){
        this.ffmpegPath = ffmpegPath;
    }

    /**
     * 设置音频文件的切片长度
     * @param videoSplitLength
     *              按照长度将音频文件切片
     * */
    public void setVideoSplitLength(int videoSplitLength){
        this.videoSplitLength = videoSplitLength;
    }

    /**
     * 设置临时目录的文件夹，如果此文件夹不存在，则创建它
     * @param tempDir
     *              临时文件的目录名称
     * */
    public void setTempDir(String tempDir){
        tempDir.replace("/", "\\");
        File file = new File(tempDir);
        if (!file.exists()){
            file.mkdirs();
        }
        this.tempDir =  tempDir.endsWith("\\")? tempDir: tempDir + "\\";
    }

    /**
     * 运行命令行，并返回结果
     * @param cmd
     *              需要执行的命令，例如 whoami
     * @return
     *              命令的返回值
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

    /**
     * 将视频识别成文本
     * @param videoPath
     *              需要转化的视频的路径
     * */
    public String videoToText(String videoPath) {
        //TODO:
        File tempDirFile = new File(tempDir);
        if (!tempDirFile.exists()){
            tempDirFile.mkdirs();
        }
        // 复制到临时目录下
        String targetFile = tempDir + "video.mp4";
        boolean result = copyFile(videoPath,targetFile,true);
        if (!result){
            return null;
        }
        File audioFile = new File(tempDir + "audio.mp3");
        if (!audioFile.exists()){
            result = convertVideoToMP3Audio("video.mp4","audio.mp3");
            if (!result){
                return null;
            }
        }

        List<String> list  = videoOrAudioSplit(tempDir, "audio.mp3");
        String text = "";
        for (String i: list) {
            System.err.println(i);
            try {
                String s = BaiduVoice.audioToText(i);
                text += (s==null?"":s);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return text;
    }

    /**
     * 利用ffmpeg将视频文件转化成音频文件
     * @param inFile
     *                  视频文件的名称
     * @param outFile
     *                  输出文件的名称
     * @return
     *                  true：转换成功
     *                  false：转换失败
     * */
    private boolean convertVideoToMP3Audio(String inFile, String outFile){
        return convertVideoToMP3Audio(tempDir, inFile,outFile);
    }

    /**
     * 复制单个文件
     *
     * @param srcFileName
     *            待复制的文件名
     * @param destFileName
     *            目标文件名
     * @param overlay
     *            如果目标文件存在，是否覆盖
     * @return 如果复制成功返回true，否则返回false
     */
    public boolean copyFile(String srcFileName, String destFileName,
                                   boolean overlay) {
        File srcFile = new File(srcFileName);

        // 判断源文件是否存在
        if (!srcFile.exists()) {
            return false;
        } else if (!srcFile.isFile()) {
            return false;
        }

        // 判断目标文件是否存在
        File destFile = new File(destFileName);
        if (destFile.exists()) {
            // 如果目标文件存在并允许覆盖
            if (overlay) {
                // 删除已经存在的目标文件，无论目标文件是目录还是单个文件
                new File(destFileName).delete();
            }
        } else {
            // 如果目标文件所在目录不存在，则创建目录
            if (!destFile.getParentFile().exists()) {
                // 目标文件所在目录不存在
                if (!destFile.getParentFile().mkdirs()) {
                    // 复制文件失败：创建目标文件所在目录失败
                    return false;
                }
            }
        }
        // 复制文件
        int byteread = 0; // 读取的字节数
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(srcFile);
            out = new FileOutputStream(destFile);
            byte[] buffer = new byte[1024];

            while ((byteread = in.read(buffer)) != -1) {
                out.write(buffer, 0, byteread);
            }
            return true;
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        } finally {
            try {
                if (out != null)
                    out.close();
                if (in != null)
                    in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将视频转换成MP3格式的音频文件
     * @param dir
     *              输入输出文件的目录
     * @param inFile
     *              输入文件的文件名
     * @param outFile
     *              输出文件的文件名
     * @return
     *              true：转换成功
     *              false：转换失败
     * */
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
     * @param inFile
     *              输入文件的文件名
     * @param dir
     *              输入输出文件的的文件夹
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
                    "  -f wav -ar 8000  -vn  -ac 1 " +
                    dir +
                    "audio_" +
                    j +
                    ".pcm -y"
            );
//            System.err.println("start:" + i + " end:" + (i + videoSplitLength));
            list.add(tempDir + "audio_" + j + ".pcm");
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
                "  -f wav -ar 8000 -vn  -ac 1 " +
                dir +
                "audio_" +
                j +
                ".pcm -y"
        );
//        System.err.println("start:" + ((videoTime-1)/videoSplitLength)*videoSplitLength + " end:" + videoTime);
        return list;
    }


    /**
     * 获取视频总时间
     * @param videoPath
     *              视频路径
     * @return
     *              这个视频文件的播放时间
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
     * @param timelen
     *              格式:"00:00:10.68"
     * @return
     *              输入的值的格式化整型值
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

}