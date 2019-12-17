package cn.kanyun.upload.upyun;

import cn.kanyun.upload.spi.Uploader;
import com.UpYun;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Kanyun
 * @date on 2019/12/10  10:18
 */
@Slf4j
public class UpYunUploader implements Uploader {

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
    }


}
