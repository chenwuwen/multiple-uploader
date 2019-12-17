package cn.kanyun.upload.upyun;

import cn.kanyun.upload.spi.IUploaderFactory;
import cn.kanyun.upload.spi.UploaderFactoryBinder;
import cn.kanyun.upload.exception.InitUploaderException;
import com.UpYun;
import com.google.common.io.Resources;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.Properties;

/**
 * @author Kanyun
 * @date on 2019/12/16  15:03
 */
@Slf4j
public class UpyunUploaderBinder implements UploaderFactoryBinder {


    /**
     * 初始化状态
     */
    private boolean initialized = false;

    private UpYunUploaderFactory defaultUploaderContext = new UpYunUploaderFactory();

    public UpyunUploaderBinder() throws InitUploaderException {
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
        log.info("multiple-uploader-upyun 又拍云桥接器开始进行初始化");
        String bucket = "";
        String userName = "";
        String password = "";
        try {
            URL url = Resources.getResource(CONFIG_FILE_NAME);
            Properties prop = new Properties();
            prop.load(url.openStream());
            userName = prop.getProperty(AuthInfo.USERNAME.toString());
            password = prop.getProperty(AuthInfo.PASSWORD.toString());
            bucket = prop.getProperty(AuthInfo.BUCKET.toString());
        } catch (Exception e) {
            log.error("[{}]类, Classpath下[{}] 文件,使用出错", this.getClass().getName(), CONFIG_FILE_NAME);
            throw new InitUploaderException(this.getClass().getName() + "类读取" + CONFIG_FILE_NAME + "配置文件出错：" + e.getMessage());
        }
        try {
//        初始化 UpYun
            UpYun upyun = new UpYun(bucket, userName, password);
//        是否开启 debug 模式：默认不开启
            upyun.setDebug(true);
//        手动设置超时时间：默认为30秒
            upyun.setTimeout(60);
//        选择最优的接入点
            upyun.setApiDomain(UpYun.ED_AUTO);
            defaultUploaderContext.setUpyun(upyun);
        } catch (Exception e) {
            log.error("[{}]类,初始化报错,[{}]", this.getClass().getName(), e);
            throw new InitUploaderException("初始化又拍云报错" + e.getMessage());
        }
    }
}
