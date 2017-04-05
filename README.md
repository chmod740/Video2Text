# Video2Text(V2T)
### 此项目是基于Java实现视频语音识别生成字幕功能</br>
### 可广泛应用于各种视频（包括电视剧，电影）的字幕文件的生成
## 项目成员：
#### 负责人：[hupeng](https://github.com/imu-hupeng)
#### 参与者：[yangyang](https://github.com/IMUDGES-YY)
## 开源协议：
#### MIT License
## 项目开始日期：
#### 20170403
## 当前状态：
#### 第一个可用版本完成
## 下一步计划:
#### 添讯飞的语音识别引擎
## 项目中所用到的第三方的程序：
#### 1.ffmpeg:一套可以用来记录、转换数字音频、视频，并能将其转化为流的开源计算机程序。
#### 2.百度语音识别引擎
## 运行平台：
#### 目前仅可运行于Windows平台
## 调用形式如下：
```java
public class Main {
    public static void main(String[] args){
        VideoUtil videoUtil = new VideoUtilBuilder()
        .setTempDir("d:\\temp")
        .build();
        String text = videoUtil.videoToText("d:\\2.mp4");
        System.out.println(text);
    }
}
```