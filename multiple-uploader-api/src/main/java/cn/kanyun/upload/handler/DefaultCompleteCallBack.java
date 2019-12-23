package cn.kanyun.upload.handler;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * 默认的完成回调
 *
 * @author Kanyun
 * @date on 2019/12/23  11:37
 */
@Slf4j
public class DefaultCompleteCallBack extends CompleteCallback {

    public DefaultCompleteCallBack(String sourcePath, String targetPath) {
        super(sourcePath, targetPath);
    }

    @Override
    public void handler(String sourcePath, String targetPath) {
        log.info("[{}][{}]默认的上传完成回调方法,该方法在一次上传中只会调用一次,并且一定会回调,需要注意的是回调里是阻塞执行的", this.getClass().getName(), LocalDateTime.now());
    }
}
