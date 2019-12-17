package cn.kanyun.upload;

import cn.kanyun.upload.spi.Uploader;
import cn.kanyun.upload.subject.AbstractUploaderSubject;
import cn.kanyun.upload.subject.ConcreteUploaderSubject;

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
