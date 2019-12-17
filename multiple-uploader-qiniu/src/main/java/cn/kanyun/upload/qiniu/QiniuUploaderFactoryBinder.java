package cn.kanyun.upload.qiniu;

import cn.kanyun.upload.spi.IUploaderFactory;
import cn.kanyun.upload.spi.UploaderFactoryBinder;
import cn.kanyun.upload.exception.InitUploaderException;
import com.google.common.io.Resources;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.Properties;

/**
 * @author Kanyun
 * @date on 2019/12/16  14:40
 */
@Slf4j
public class QiniuUploaderFactoryBinder implements UploaderFactoryBinder {

    private QiniuUploaderFactory defaultUploaderContext = new QiniuUploaderFactory();

    /**
     * 初始化状态
     */
    private boolean initialized = false;

    public QiniuUploaderFactoryBinder() throws InitUploaderException {
        init();
    }

    @Override
    public IUploaderFactory getUploaderFactory() {
        if (!initialized) {
            return defaultUploaderContext;
        }
        return null;
    }

    @Override
    public String getUploaderFactoryClassStr() {
        return defaultUploaderContext.getClass().getName();
    }


    void init() throws InitUploaderException {
        log.info("common-uploader-qiniu 七牛云桥接器开始进行初始化");
//      todo 这里可以读取classpath下特殊的storage.properties文件
        String fileName = CONFIG_FILE_NAME;
        String accessKey = "ChZnwyjHOLonZxL1AMHdv-SGaFE8C9dpjUiEq1Pn";
        String secretKey = "9JuzUfV1RNAtEhDZ3vEHFR2x2ClJT36iKAp5XO0x";
        String bucket = "hanlang";
        try {
            URL url = Resources.getResource(fileName);
            Properties prop = new Properties();
            prop.load(url.openStream());
            accessKey = prop.getProperty(AuthInfo.ACCESS_KEY.toString());
            secretKey = prop.getProperty(AuthInfo.SECRET_KEY.toString());
            bucket = prop.getProperty(AuthInfo.BUCKET.toString());
        } catch (Exception e) {
            log.error("[{}]类, Classpath下[{}] 文件,使用出错", this.getClass().getName(), fileName);
            throw new InitUploaderException(this.getClass().getName() + "类读取" + CONFIG_FILE_NAME + "配置文件出错：" + e.getMessage());
        }
        try {
//        构造一个带指定 Region 对象的配置类
            Configuration cfg = new Configuration(Region.huabei());
//        其他参数参考类注释
            UploadManager uploadManager = new UploadManager(cfg);
//       生成上传凭证，然后准备上传
            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(bucket);
            defaultUploaderContext.setUploadManager(uploadManager);
            defaultUploaderContext.setUpToken(upToken);
        } catch (Exception e) {
            log.error("[{}]类,初始化报错,[{}]", this.getClass().getName(), e);
            throw new InitUploaderException("初始化七牛云报错" + e.getMessage());
        }
    }
}
