package cn.kanyun.upload.qiniu;

import cn.kanyun.upload.spi.AbstractUploader;
import cn.kanyun.upload.ActualCloud;
import cn.kanyun.upload.handler.PushCallback;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Kanyun
 * @date on 2019/12/16  13:48
 */
@Slf4j
public class QiniuUploader extends AbstractUploader {

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
        log.info(" [{}] 类 push([{},{}])", this.getClass().getName(), sourcePath, targetPath);
        try {
            invokePush(sourcePath, targetPath);
        } catch (QiniuException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void push(String sourcePath, String targetPath, PushCallback callback) {
        log.info(" [{}] 类 带回调的push([{},{}]) ", this.getClass().getName(), sourcePath, targetPath);
        callback.setTargetStorageEnum(ActualCloud.QINIU_CLOUD);
        try {
            invokePush(sourcePath, targetPath);
            callback.onSuccess(sourcePath, targetPath);
        } catch (QiniuException e) {
            e.printStackTrace();
            callback.onError(sourcePath, targetPath, e);
        }
    }


    private DefaultPutRet invokePush(String sourcePath, String targetPath) throws QiniuException {
        upToken = QiniuUploaderFactoryBinder.getToken();
        Response response = uploadManager.put(sourcePath, targetPath, upToken);
        //解析上传成功的结果
        DefaultPutRet putRet = response.jsonToObject(DefaultPutRet.class);
        return putRet;
    }
}
