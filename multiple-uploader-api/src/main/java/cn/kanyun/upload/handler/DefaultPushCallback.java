package cn.kanyun.upload.handler;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * 默认的上传回调
 *
 * @author Kanyun
 * @date on 2019/12/18  8:53
 */
@Slf4j
public class DefaultPushCallback extends PushCallback {

    @Override
    public void onSuccess(String sourcePath, String targetPath) {
        log.info("[{}][{}]默认的上传成功方法,该方法在一次上传中可能会调用多次(取决于你打算上传到几处存储,其中成功了几处)", this.getClass().getName(), LocalDateTime.now());
    }

    @Override
    public void onError(String sourcePath, String targetPath, Throwable throwable) {
        log.info("[{}][{}]默认的上传失败方法,该方法在一次上传中可能会调用多次(取决于你打算上传到几处存储,其中失败了几处)", this.getClass().getName(), LocalDateTime.now());
    }
}
