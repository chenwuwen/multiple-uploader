package cn.kanyun.upload.qiniu;

import cn.kanyun.upload.spi.Uploader;
import com.qiniu.storage.UploadManager;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Kanyun
 * @date on 2019/12/16  13:48
 */
@Slf4j
public class QiniuUploader implements Uploader {

    private String upToken;
    private UploadManager uploadManager;

    public QiniuUploader(String upToken, UploadManager uploadManager) {
        this.upToken = upToken;
        this.uploadManager = uploadManager;
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public void push(String sourcePath, String targetPath) {
        log.info(" [{}] 类 push([{},{}])", this.getClass().getName(),sourcePath, targetPath);
    }
}
