package cn.kanyun.upload.handler;

import cn.kanyun.upload.ActualCloud;
import lombok.Data;

/**
 * 上传回调
 *
 * @author Kanyun
 * @date on 2019/12/18  8:53
 */
@Data
public abstract class PushCallback {

    /**
     * 存放的目的地枚举,也就是哪个CND
     */
    private ActualCloud targetStorageEnum;


    /**
     * 上传成功结束处理函数
     *
     * @param sourcePath
     * @param targetPath
     */
    public abstract void onSuccess(String sourcePath, String targetPath);


    /**
     * 上传失败结束处理函数
     *
     * @param sourcePath
     * @param targetPath
     * @param throwable
     */
    public abstract void onError(String sourcePath, String targetPath, Throwable throwable);
}
