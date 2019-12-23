package cn.kanyun.upload;

import cn.kanyun.upload.handler.CompleteCallback;
import cn.kanyun.upload.handler.DefaultCompleteCallBack;
import cn.kanyun.upload.handler.DefaultPushCallback;
import cn.kanyun.upload.handler.PushCallback;
import cn.kanyun.upload.spi.Uploader;
import cn.kanyun.upload.subject.AbstractUploaderSubject;
import cn.kanyun.upload.subject.ConcreteUploaderSubject;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/**
 * 这个类的主要作用就是广播事件
 *
 * @author Kanyun
 * @date on 2019/12/16  14:33
 */
public class DefaultUploader implements Uploader {

    /**
     * 主题引用
     */
    AbstractUploaderSubject subject = new ConcreteUploaderSubject();


    private DefaultUploader() {
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public void push(String sourcePath, String targetPath) {
        subject.notify(sourcePath, targetPath);
    }

    @Override
    public void push(String sourcePath, String targetPath, PushCallback callback) {
        subject.notify(sourcePath, targetPath, callback);
    }


    @Override
    public void asyncPush(String sourcePath, String targetPath) {
        asyncPush(sourcePath, targetPath, new DefaultCompleteCallBack(sourcePath, targetPath), new DefaultPushCallback());
    }


    @Override
    public void asyncPush(String sourcePath, String targetPath, CompleteCallback completeCallback) {
        asyncPush(sourcePath, targetPath, completeCallback, new DefaultPushCallback());
    }


    @Override
    public void asyncPush(String sourcePath, String targetPath, PushCallback pushCallback) {
        asyncPush(sourcePath, targetPath, new DefaultCompleteCallBack(sourcePath, targetPath), pushCallback);
    }


    @Override
    public void asyncPush(String sourcePath, String targetPath, CompleteCallback completeCallback, PushCallback pushCallback) {
        CompletableFuture.runAsync(() -> {
            push(sourcePath, targetPath, pushCallback);
        }).whenComplete(completeCallback);
    }

    /**
     * 静态内部类实现单例
     */
    public static class SingleUploader {
        private static DefaultUploader INSTANCE = new DefaultUploader();
    }

    public static Uploader getInstance() {
        return SingleUploader.INSTANCE;
    }
}
