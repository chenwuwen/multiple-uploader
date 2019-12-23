package cn.kanyun.upload.spi;

import cn.kanyun.upload.handler.CompleteCallback;
import cn.kanyun.upload.handler.PushCallback;

/**
 * 抽象的Uploader,主要做适配,真正的上传实现是不需要重写asyncPush方法的
 * 因此如果Uploader接口有哪些方法不需要实现类去实现,可以将该方法放到抽象类中实现
 * 所以真正的Uploader最好是继承AbstractUploader抽象类,而不是直接实现 Uploader接口
 * @author Kanyun
 * @date on 2019/12/23  13:38
 */
public abstract class AbstractUploader implements Uploader {
    @Override
    public void asyncPush(String sourcePath, String targetPath) {

    }

    @Override
    public void asyncPush(String sourcePath, String targetPath, CompleteCallback completeCallback) {

    }

    @Override
    public void asyncPush(String sourcePath, String targetPath, PushCallback pushCallback) {

    }

    @Override
    public void asyncPush(String sourcePath, String targetPath, CompleteCallback completeCallback, PushCallback pushCallback) {

    }
}
