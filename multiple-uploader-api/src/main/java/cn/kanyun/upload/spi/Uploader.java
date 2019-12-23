package cn.kanyun.upload.spi;

import cn.kanyun.upload.handler.CompleteCallback;
import cn.kanyun.upload.handler.PushCallback;

/**
 * 观察者接口
 *
 * @author Kanyun
 * @date on 2019/12/16  13:41
 */
public interface Uploader {

    /**
     * 返回实现此Uploader的实例
     *
     * @return
     */
    String getName();

    /**
     * 上传
     *
     * @param sourcePath 源文件地址
     * @param targetPath 目标文件地址
     */
    void push(String sourcePath, String targetPath);


    /**
     * 带回调上传
     *
     * @param sourcePath 源文件地址
     * @param targetPath 目标文件地址
     */
    void push(String sourcePath, String targetPath, PushCallback callback);

    /**
     * 基础的异步上传操作
     *
     * @param sourcePath
     * @param targetPath
     */
    void asyncPush(String sourcePath, String targetPath);

    /**
     * 带上传完成回调的异步上传操作
     *
     * @param sourcePath
     * @param targetPath
     */
    void asyncPush(String sourcePath, String targetPath, CompleteCallback completeCallback);

    /**
     * 带上传是否成功回调的异步上传操作
     *
     * @param sourcePath
     * @param targetPath
     */
    void asyncPush(String sourcePath, String targetPath, PushCallback pushCallback);

    /**
     * 带上传是否成功回调/上传完成回调的异步上传操作
     *
     * @param sourcePath
     * @param targetPath
     * @param completeCallback
     * @param pushCallback
     */
    void asyncPush(String sourcePath, String targetPath, CompleteCallback completeCallback, PushCallback pushCallback);

}
