package cn.kanyun.upload.upyun;

import cn.kanyun.upload.spi.AbstractUploader;
import cn.kanyun.upload.ActualCloud;
import cn.kanyun.upload.handler.PushCallback;
import com.UpYun;
import com.upyun.UpException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author Kanyun
 * @date on 2019/12/10  10:18
 */
@Slf4j
public class UpYunUploader extends AbstractUploader {

    private UpYun upyun;

    public UpYunUploader(UpYun upyun) {
        this.upyun = upyun;
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public void push(String sourcePath, String targetPath) {
        log.info(" [{}] 类 push([{},{}])", this.getClass().getName(), sourcePath, targetPath);
        try {
            boolean state = invokePush(sourcePath, targetPath);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UpException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void push(String sourcePath, String targetPath, PushCallback callback) {
        log.info(" [{}] 类 带回调的push([{},{}])", this.getClass().getName(), sourcePath, targetPath);
        callback.setTargetStorageEnum(ActualCloud.UP_CLOUD);
        try {
            boolean state = invokePush(sourcePath, targetPath);
            if (state) {
                callback.onSuccess(sourcePath, targetPath);
            } else {
                callback.onError(sourcePath, targetPath, new Exception("上传失败,但是不知道什么原因"));
            }
        } catch (IOException | UpException e) {
            e.printStackTrace();
            callback.onError(sourcePath, targetPath, e);
        } catch (Exception e) {
            e.printStackTrace();
            callback.onError(sourcePath, targetPath, e);
        }
    }


    private boolean invokePush(String sourcePath, String targetPath) throws IOException, UpException {
        return upyun.writeFile(sourcePath, targetPath);
    }

}
