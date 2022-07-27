package com.seanai.video.util;

/**
 * @Author: qizhiyuan
 * @CreateTime: 2022-07-08  15:31
 * @Description: TODO
 * @Version: 1.0
 */
import lombok.extern.slf4j.Slf4j;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.info.MultimediaInfo;
import ws.schild.jave.process.ProcessWrapper;
import ws.schild.jave.process.ffmpeg.DefaultFFMPEGLocator;

import java.io.*;
import java.net.URL;

@Slf4j
public class FfmpegUtil {

    /**
     * 获取音视频时长
     *
     *
     * @param sourcePath
     * @return
     * @throws EncoderException
     */
    public static long getFileDuration(String sourcePath) throws EncoderException {
        MultimediaObject multimediaObject = new MultimediaObject(new File(sourcePath));
        MultimediaInfo multimediaInfo = multimediaObject.getInfo();
        return multimediaInfo.getDuration();
    }


    /**
     * 剪切音视频
     *
     * @param sourcePath
     * @param targetPath
     * @param offetTime  起始时间，格式 00:00:00.000   小时:分:秒.毫秒
     * @param endTime    同上
     * @throws Exception
     */
    public static void cutAv(String sourcePath, String targetPath, String offetTime, String endTime) {
        try {
            ProcessWrapper ffmpeg = new DefaultFFMPEGLocator().createExecutor();
            ffmpeg.addArgument("-ss");
            ffmpeg.addArgument(offetTime);
            ffmpeg.addArgument("-t");
            ffmpeg.addArgument(endTime);
            ffmpeg.addArgument("-i");
            ffmpeg.addArgument(sourcePath);
            ffmpeg.addArgument("-vcodec");
            ffmpeg.addArgument("copy");
            ffmpeg.addArgument("-acodec");
            ffmpeg.addArgument("copy");
            ffmpeg.addArgument(targetPath);
            ffmpeg.execute();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(ffmpeg.getErrorStream()))) {
                blockFfmpeg(br);
            }
            log.info("切除视频成功={}", targetPath);
        } catch (IOException e) {
            //throw new CombineException(ResultCode.ERROR.getCode(), "剪切视频失败", null);
        }
    }

    /**
     * 等待命令执行成功，退出
     *
     * @param br
     * @throws IOException
     */
    private static void blockFfmpeg(BufferedReader br) throws IOException {
        String line;
        // 该方法阻塞线程，直至合成成功
        while ((line = br.readLine()) != null) {
            doNothing(line);
        }
    }

    /**
     * 打印日志，调试阶段可解开注释，观察执行情况
     *
     * @param line
     */
    private static void doNothing(String line) {
//    log.info(line);
    }


    /**
     * 合并两个视频
     *
     * @param firstFragmentPath  资源本地路径或者url
     * @param secondFragmentPath 资源本地路径或者url**
     * @param targetPath         目标存储位置
     * @throws Exception
     */
    public static void mergeAv(String firstFragmentPath, String secondFragmentPath,
                               String targetPath) {
        try {
            log.info("合并视频处理中firstFragmentPath={},secondFragmentPath={},请稍后.....", firstFragmentPath,
                    secondFragmentPath);
            ProcessWrapper ffmpeg = new DefaultFFMPEGLocator().createExecutor();
            ffmpeg.addArgument("-i");
            ffmpeg.addArgument(firstFragmentPath);
            ffmpeg.addArgument("-i");
            ffmpeg.addArgument(secondFragmentPath);
            ffmpeg.addArgument("-filter_complex");
            ffmpeg.addArgument(
                    "\"[0:v] [0:a] [1:v] [1:a] concat=n=2:v=1:a=1 [v] [a]\" -map \"[v]\" -map \"[a]\"");
            ffmpeg.addArgument(targetPath);
            ffmpeg.execute();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(ffmpeg.getErrorStream()))) {
                blockFfmpeg(br);
            }
            log.info("合并视频成功={}", targetPath);
        } catch (IOException e) {
            // throw new CombineException(ResultCode.ERROR.getCode(), "合并视频失败", null);
        }
    }


    /**
     * 获取视频原声
     *
     * @param sourcePath 本地路径或者url
     * @param targetPath 本地存储路径
     */
    public static String getAudio(String sourcePath, String targetPath, String taskId) {
        try {
            ProcessWrapper ffmpeg = new DefaultFFMPEGLocator().createExecutor();
            ffmpeg.addArgument("-i");
            ffmpeg.addArgument(sourcePath);
            ffmpeg.addArgument("-f");
            ffmpeg.addArgument("mp3");
            ffmpeg.addArgument("-vn");
            ffmpeg.addArgument(targetPath);
            ffmpeg.execute();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(ffmpeg.getErrorStream()))) {
                blockFfmpeg(br);
            }
            log.info("获取视频音频={}", targetPath);
        } catch (IOException e) {
            //throw new CombineException(ResultCode.ERROR.getCode(), "获取视频音频失败", taskId);
        }
        return targetPath;
    }

    /**
     * 合并音频
     *
     * @param originAudioPath 音频url或本地路径
     * @param magicAudioPath  音频url或本地路径
     * @param audioTargetPath 目标存储本地路径
     */
    public static void megerAudioAudio(String originAudioPath, String magicAudioPath,
                                       String audioTargetPath, String taskId) {
        try {
            ProcessWrapper ffmpeg = new DefaultFFMPEGLocator().createExecutor();
            ffmpeg.addArgument("-i");
            ffmpeg.addArgument(originAudioPath);
            ffmpeg.addArgument("-i");
            ffmpeg.addArgument(magicAudioPath);
            ffmpeg.addArgument("-filter_complex");
            ffmpeg.addArgument("amix=inputs=2:duration=first:dropout_transition=2");
            ffmpeg.addArgument("-f");
            ffmpeg.addArgument("mp3");
            ffmpeg.addArgument("-y");
            ffmpeg.addArgument(audioTargetPath);
            ffmpeg.execute();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(ffmpeg.getErrorStream()))) {
                blockFfmpeg(br);
            }
            log.info("合并音频={}", audioTargetPath);
        } catch (IOException e) {
            //throw new CombineException(ResultCode.ERROR.getCode(), "合并音频失败", taskId);
        }
    }


    /**
     * 视频加声音
     *
     * @param videoPath       视频
     * @param megerAudioPath  音频
     * @param videoTargetPath 目标地址
     * @param taskId          可忽略，自行删除taskid
     * @throws Exception
     */
    public static void mergeVideoAndAudio(String videoPath, String megerAudioPath,
                                          String videoTargetPath, String taskId) {
        try {
            ProcessWrapper ffmpeg = new DefaultFFMPEGLocator().createExecutor();
            ffmpeg.addArgument("-i");
            ffmpeg.addArgument(videoPath);
            ffmpeg.addArgument("-i");
            ffmpeg.addArgument(megerAudioPath);
            ffmpeg.addArgument("-codec");
            ffmpeg.addArgument("copy");
            ffmpeg.addArgument("-shortest");
            ffmpeg.addArgument(videoTargetPath);
            ffmpeg.execute();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(ffmpeg.getErrorStream()))) {
                blockFfmpeg(br);
            }
            log.info("获取视频(去除音频)={}", videoTargetPath);
        } catch (IOException e) {
            //throw new CombineException(ResultCode.ERROR.getCode(), "获取视频(去除音频)失败", taskId);
        }
    }

    /**
     * 视频增加字幕
     *
     * @param videoPath
     * @param sutitleVideoSavePath
     * @param wordPath             固定格式的srt文件地址或存储位置，百度即可
     * @return
     * @throws Exception
     */
    public static boolean addSubtitle(String videoPath, String sutitleVideoSavePath,
                                      String wordPath, String taskId) {
        try {
            log.info("开始合成字幕....");
            ProcessWrapper ffmpeg = new DefaultFFMPEGLocator().createExecutor();
            ffmpeg.addArgument("-i");
            ffmpeg.addArgument(videoPath);
            ffmpeg.addArgument("-vf");
            ffmpeg.addArgument("ass=");
            ffmpeg.addArgument(wordPath);
            ffmpeg.addArgument("-c");
            ffmpeg.addArgument("copy");
            ffmpeg.addArgument(sutitleVideoSavePath);
            ffmpeg.execute();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(ffmpeg.getErrorStream()))) {
                blockFfmpeg(br);
            }
            log.info("添加字幕成功={}", videoPath);
        } catch (IOException e) {
            //throw new CombineException(ResultCode.ERROR.getCode(), "添加字幕失败", taskId);
        }
        return true;
    }

    /**
     * 图片生成视频   帧率设置25，可自行修改
     *
     * @param videoPath
     * @param videoPath
     * @return
     * @throws Exception
     */
    public static boolean picToVideo(String imagesPath, String videoPath,String i,String k) {
        try {
            log.info("图片转视频中....");
            ProcessWrapper ffmpeg = new DefaultFFMPEGLocator().createExecutor();
            ffmpeg.addArgument("-f");
            ffmpeg.addArgument("image2");
            ffmpeg.addArgument("-framerate");
            ffmpeg.addArgument(i);
            ffmpeg.addArgument("-i");
            ffmpeg.addArgument(imagesPath);
            ffmpeg.addArgument("-b:v");
            ffmpeg.addArgument(k);
            ffmpeg.addArgument(videoPath);
            ffmpeg.execute();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(ffmpeg.getErrorStream()))) {
                blockFfmpeg(br);
            }
            log.info("图片转视频成功={}", videoPath);
        } catch (IOException e) {
            log.error("图片转视频失败={}", e.getMessage());
            //throw new PicException(ResultCode.ERROR.getCode(), "图片转视频失败", taskId);
        }
        return true;
    }


    /**
     * 获取视频信息
     *
     * @param url
     * @return
     */
    public static MultimediaInfo getVideoInfo(URL url) {
        try {
            MultimediaObject multimediaObject = new MultimediaObject(url);
            return multimediaObject.getInfo();
        } catch (EncoderException e) {
            log.error("获取视频信息报错={}", e.getMessage());
            // throw new BaseException(ResultCode.ERROR.getCode(), "获取视频信息报错");
        }
        return null;
    }


    /**
     * @Description ：视频添加水印方法
     * @param: [
     * logoPath:  水印地址,本地磁盘地址 如：C:\\video\\xxx.png
     * ， videoPath 视频地址,本地磁盘地址 如：D:\\video\\xxx.mp4
     * outVideoPath 输出视频地址
     * xValue X轴坐标
     * yValue Y轴坐标
     * ]
     *
     *
     */
    public static void  addVideoPngLogo( String videoPath, String logoPath, String outVideoPath, String xValue, String yValue,String startTime , String endTime) {

        try {

            //ffmpeg -hide_banner -i 特效合成1.mp4 -i heart.png  -filter_complex "overlay=enable='between(t,2,4):x=1920:y=1080 " out.mp4 -y
            String str= "overlay=enable='between(t,"+startTime+","+endTime+"):x="+xValue+":y="+yValue;
            log.info("开始添加水印....");
            ProcessWrapper ffmpeg = new DefaultFFMPEGLocator().createExecutor();
            ffmpeg.addArgument("-hide_banner");
            ffmpeg.addArgument("-i");
            ffmpeg.addArgument(videoPath);
            ffmpeg.addArgument("-i");
            ffmpeg.addArgument(logoPath);
            ffmpeg.addArgument("-filter_complex");
            ffmpeg.addArgument(str);
            ffmpeg.addArgument(outVideoPath);
            ffmpeg.addArgument("-y");

            ffmpeg.execute();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(ffmpeg.getErrorStream()))) {
                blockFfmpeg(br);
            }
            log.info("添加水印成功={}", videoPath);
        } catch (IOException e) {
            //throw new CombineException(ResultCode.ERROR.getCode(), "添加字幕失败", taskId);
        }
    }


    /**
     * @Description ：视频添加水印方法
     * @param: [
     * logoPath:  水印地址,本地磁盘地址 如：C:\\video\\xxx.gif
     * ， videoPath 视频地址,本地磁盘地址 如：D:\\video\\xxx.mp4
     * outVideoPath 输出视频地址
     * xValue X轴坐标
     * yValue Y轴坐标
     * ]
     *
     *
     */
    public static void  addVideoGifLogo( String videoPath, String logoPath, String outVideoPath, String xValue, String yValue,String startTime , String endTime,String logoWidth,String logoHeight) {

        try {

            //ffmpeg -hide_banner -i big_buck_bunny.mp4 -vf "movie=doggie3.gif:loop=0,setpts=N/FRAME_RATE/TB[out];[0:v][out]overlay=x=main_w-overlay_w:y=0" -shortest out.mp4 -y

           // ffmpeg -y -i 特效合成.mp4  -ignore_loop 0 -itsoffset 2  -i 3.gif  -filter_complex "[0:0]scale=iw:ih[a];[1:0]scale=900:800,fade=t=in:st=2:d=1:alpha=1[wm];[a][wm]overlay=y=if(gte(t,5), NAN, 100):x=400:shortest=1" s01.mp4

           // ffmpeg -y -i 特效合成.mp4  -ignore_loop 0 -itsoffset 3  -i 3.gif  -filter_complex "[0:0]scale=iw:ih[a];[1:0]scale=1000:1000[wm];[a][wm]overlay=y='if(gte(t,6), NAN, 400):x=600:shortest=1" s01.mp4
            String str= "\"[0:0]scale=iw:ih[a];[1:0]scale="+logoWidth+":"+logoHeight+"[wm];[a][wm]overlay=y='if(gte(t,"+endTime+"), NAN,"+ xValue+"):x="+yValue+":shortest=1\"";
            log.info("开始添加水印....");
            ProcessWrapper ffmpeg = new DefaultFFMPEGLocator().createExecutor();
            ffmpeg.addArgument("-y");
            ffmpeg.addArgument("-i");
            ffmpeg.addArgument(videoPath);
            ffmpeg.addArgument("-ignore_loop");
            ffmpeg.addArgument("0");
            ffmpeg.addArgument("-itsoffset");
            ffmpeg.addArgument(startTime);
            ffmpeg.addArgument("-i");
            ffmpeg.addArgument(logoPath);
            ffmpeg.addArgument("-filter_complex");
            ffmpeg.addArgument(str);
            ffmpeg.addArgument(outVideoPath);
            ffmpeg.execute();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(ffmpeg.getErrorStream()))) {
                blockFfmpeg(br);
            }
            log.info("添加水印成功={}", videoPath);
        } catch (IOException e) {
            //throw new CombineException(ResultCode.ERROR.getCode(), "添加字幕失败", taskId);
        }
    }

    /**
     * @Description ：视频转图片
     * @param: [
     * videoPath:  视频地址 如：C:\\video\\xxx.mp4
     * imagesPath: 输出图片地址,本地磁盘地址 如：D:\\img\\xxx.png
     * i : 每秒抽取几帧
     * ]
     *
     * ffmpeg -i 1.mp4 -r 5 -f image2 .\output\1_frame_%05d.bmp
     */
    public static void  videoToPng(String videoPath, String imagesPath,String i) {

        try {
            log.info("开始视频转图片....");
            ProcessWrapper ffmpeg = new DefaultFFMPEGLocator().createExecutor();
            ffmpeg.addArgument("-i");
            ffmpeg.addArgument(videoPath);
            ffmpeg.addArgument("-r");
            ffmpeg.addArgument(i);
            ffmpeg.addArgument("-f");
            ffmpeg.addArgument("image2");
            ffmpeg.addArgument(imagesPath);
            ffmpeg.execute();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(ffmpeg.getErrorStream()))) {
                blockFfmpeg(br);
            }
            log.info("添加视频转图片={}", videoPath);
        } catch (IOException e) {
            //throw new CombineException(ResultCode.ERROR.getCode(), "添加字幕失败", taskId);
        }
    }



     /**
   * @description: 图片批量增加图层
   * @author: qizhiyuan
   * @date: 2022/7/20 16:28
   * @param: [imagesPath, layersPath, outPath]
   * @return: void
   **/
    public static void picAddpic(String imagesPath, String layersPath, String outPath) {

        try {
            log.info("开始图片批量增加图层....");
            ProcessWrapper ffmpeg = new DefaultFFMPEGLocator().createExecutor();
            ffmpeg.addArgument("-i");
            ffmpeg.addArgument(imagesPath);
            ffmpeg.addArgument("-vf");
            String str = "\"movie=\""+layersPath+"\",scale=0:0[watermask];[in][watermask] overlay=0:0[out]\"";
            ffmpeg.addArgument(str);
            ffmpeg.addArgument("-y");
            ffmpeg.addArgument(outPath);
            ffmpeg.execute();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(ffmpeg.getErrorStream()))) {
                blockFfmpeg(br);
            }
            log.info("图片批量增加图层成功={}",outPath);
        } catch (IOException e) {
            //throw new CombineException(ResultCode.ERROR.getCode(), "添加字幕失败", taskId);
        }
    }



}
