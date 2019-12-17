package cn.kanyun.upload.spi;

/**
 * 观察者接口
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

    void push(String sourcePath, String targetPath);
}
