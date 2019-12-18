package cn.kanyun.upload.spi;

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
}
