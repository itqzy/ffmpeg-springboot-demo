package com.seanai.video.controller;

import com.seanai.video.util.FfmpegUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * @Author: qizhiyuan
 * @CreateTime: 2022-07-20  14:06
 * @Description: TODO
 * @Version: 1.0
 */
@Controller
@RequestMapping("/videoOperate")
public class VideoOperateController {

  /**
   * @description: 剪切音视频
   * @author: qizhiyuan 
   * @date: 2022/7/20 14:38
   * @param: [params]
   * @return: java.lang.String
   **/
    @PostMapping("/cutAv")
    @ResponseBody
    public String cutAv(@RequestBody Map<String, String> params) {
        try {
            FfmpegUtil.cutAv(params.get("sourcePath"), params.get("targetPath"), params.get("offetTime"),params.get("endTime"));
            return "执行成功....请查看输出文件夹："+ params.get("targetPath");
        } catch (Exception e) {

            return "执行错误，请检查参数";
        }
    }

    /**
     * @description: 视频合并
     * @author: qizhiyuan
     * @date: 2022/7/20 14:38
     * @param: [params]
     * @return: java.lang.String
     **/
    @PostMapping("/mergeAv")
    @ResponseBody
    public String mergeAv(@RequestBody Map<String, String> params) {
        try {
            FfmpegUtil.mergeAv(params.get("firstFragmentPath"), params.get("secondFragmentPath"), params.get("targetPath"));
            return "执行成功....请查看输出文件夹："+ params.get("targetPath");
        } catch (Exception e) {

            return "执行错误，请检查参数";
        }
    }

    /**
     * @description: 视频转图片
     * @author: qizhiyuan
     * @date: 2022/7/20 14:38
     * @param: [params]
     * @return: java.lang.String
     **/
    @PostMapping("/videoToPng")
    @ResponseBody
    public String videoToPng(@RequestBody Map<String, String> params) {
        try {
            FfmpegUtil.videoToPng(params.get("videoPath"), params.get("imagesPath"), params.get("i"));
            return "执行成功....请查看输出文件夹："+ params.get("imagesPath");
        } catch (Exception e) {
            return "执行错误，请检查参数";
        }
    }

    /**
     * @description: 图片转视频
     * @author: qizhiyuan
     * @date: 2022/7/20 14:38
     * @param: [params]
     * @return: java.lang.String
     **/
    @PostMapping("/picToVideo")
    @ResponseBody
    public String picToVideo(@RequestBody Map<String, String> params) {
        try {
            FfmpegUtil.picToVideo(params.get("imagesPath"), params.get("videoPath"), params.get("i"),params.get("k"));
            return "执行成功....请查看输出文件夹："+ params.get("videoPath");
        } catch (Exception e) {
            return "执行错误，请检查参数";
        }
    }

    /**
     * @description: 图片批量增加图层
     * @author: qizhiyuan
     * @date: 2022/7/20 14:38
     * @param: [params]
     * @return: java.lang.String
     **/
    @PostMapping("/picAddpic")
    @ResponseBody
    public String picAddpic(@RequestBody Map<String, String> params) {
        try {
          //   FfmpegUtil.picAddpic(params.get("imagesPath"), params.get("layersPath"), params.get("outPath"));

            for (int i = 1; i < 127; i++) {
                if (i<10 ){
                    FfmpegUtil.picAddpic("./视频转图片/1_frame_0000"+i+".png", "./imgAll/normal.exr000"+i+".png", "./图片加图层/0000"+i+".png");
                }
                if (i<100 && i > 9){
                    FfmpegUtil.picAddpic("./视频转图片/1_frame_000"+i+".png","./imgAll/normal.exr00"+i+".png", "./图片加图层/000"+i+".png");
                }
                if (i <1000 && i > 99){
                    FfmpegUtil.picAddpic("./视频转图片/1_frame_00"+i+".png", "./imgAll/normal.exr0"+i+".png", "./图片加图层/00"+i+".png");
                }

            }
            return "执行成功....请查看输出文件夹："+ params.get("outPath");
        } catch (Exception e) {
            return "执行错误，请检查参数";
        }
    }


    /**
     * @description: 视频增加png图层
     * @author: qizhiyuan
     * @date: 2022/7/20 14:38
     * @param: [params]
     * @return: java.lang.String
     **/
    @PostMapping("/addVideoPngLogo")
    @ResponseBody
    public String addVideoPngLogo(@RequestBody Map<String, String> params) {
        try {
            FfmpegUtil.addVideoPngLogo(params.get("videoPath"), params.get("logoPath"),params.get("outVideoPath"),params.get("xValue"), params.get("yValue"),params.get("startTime"), params.get("endTime"));
            return "执行成功....请查看输出文件夹："+ params.get("outVideoPath");
        } catch (Exception e) {
            return "执行错误，请检查参数";
        }
    }

    /**
     * @description: 视频增加gif图层
     * @author: qizhiyuan
     * @date: 2022/7/20 14:38
     * @param: [params]
     * @return: java.lang.String
     **/
    @PostMapping("/addVideoGifLogo")
    @ResponseBody
    public String addVideoGifLogo(@RequestBody Map<String, String> params) {
        try {
            FfmpegUtil.addVideoGifLogo(params.get("videoPath"), params.get("logoPath"),params.get("outVideoPath"),params.get("xValue"), params.get("yValue"),params.get("startTime"), params.get("endTime"),params.get("logoWidth"), params.get("logoHeight"));
            return "执行成功....请查看输出文件夹："+ params.get("outVideoPath");
        } catch (Exception e) {
            return "执行错误，请检查参数";
        }
    }
}
