package cn.kanyun.upload.upyun;

import cn.kanyun.upload.spi.Uploader;
import com.UpYun;
import com.upyun.UpException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

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
        try {
            boolean state = invokePush(sourcePath, targetPath);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UpException e) {
            e.printStackTrace();
        }
    }


    private boolean invokePush(String sourcePath, String targetPath) throws IOException, UpException {
        return upyun.writeFile(sourcePath, targetPath);
    }

}
