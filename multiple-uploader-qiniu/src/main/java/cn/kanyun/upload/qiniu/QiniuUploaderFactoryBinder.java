package cn.kanyun.upload.qiniu;

import cn.kanyun.upload.spi.IUploaderFactory;
import cn.kanyun.upload.spi.UploaderFactoryBinder;
import cn.kanyun.upload.exception.InitUploaderException;
import com.google.auto.service.AutoService;
import com.google.common.base.Strings;
import com.google.common.io.Resources;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.Properties;

/**
 * 使用@AutoService注解自动生成resources/META-INF/services下文件
 *
 * @author Kanyun
 * @date on 2019/12/16  14:40
 */
@Slf4j
@AutoService(UploaderFactoryBinder.class)
public class QiniuUploaderFactoryBinder implements UploaderFactoryBinder {

    private QiniuUploaderFactory defaultUploaderContext = new QiniuUploaderFactory();

    /**
     * 七牛云accessKey
     */
    private static String accessKey;
    /**
     * 七牛云secretKey
     */
    private static String secretKey;
    /**
     * 七牛云bucket
     */
    private static String bucket;
    /**
     * 七牛云token ,动态获得非文件读取
     */
    private static String upToken;
    /**
     * 七牛云token有效期
     * 默认配置为一天 单位：秒
     */
    private static final long expire = 60 * 60 * 24;

    /**
     * 七牛云token下次失效时间
     */
    private static long next_invalid_time = 0;

    /**
     * 初始化状态
     */
    private boolean initialized = false;


    public QiniuUploaderFactoryBinder() {
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

    @Override
    public void init() throws InitUploaderException {
        log.info("multiple-uploader-qiniu 七牛云桥接器开始进行初始化");
        try {
//          这里读取classpath下的storage.properties文件
            URL url = Resources.getResource(CONFIG_FILE_NAME);
            Properties prop = new Properties();
            prop.load(url.openStream());
            accessKey = prop.getProperty(AuthInfo.ACCESS_KEY.toString());
            secretKey = prop.getProperty(AuthInfo.SECRET_KEY.toString());
            bucket = prop.getProperty(AuthInfo.BUCKET.toString());
            checkConfigValue(accessKey, secretKey, bucket);
        } catch (Exception e) {
            log.error("[{}]类, Classpath下[{}] 文件,使用出错", this.getClass().getName(), CONFIG_FILE_NAME);
            throw new InitUploaderException(this.getClass().getName() + "类读取" + CONFIG_FILE_NAME + "配置文件出错：" + e.getMessage());
        }
        try {
//            构造一个带指定 Region 对象的配置类
            Configuration cfg = new Configuration(Region.huabei());
//             其他参数参考类注释
            UploadManager uploadManager = new UploadManager(cfg);
//            此处获取不获取token,问题都不大,主要是因为在上传时也会调用getToken()方法
            getToken();
            defaultUploaderContext.setUploadManager(uploadManager);
            defaultUploaderContext.setUpToken(upToken);
        } catch (Exception e) {
            log.error("[{}]类,初始化报错,详细信息 ⇓", this.getClass().getName(), e);
            throw new InitUploaderException("初始化七牛云报错：" + e.getMessage());
        }
    }


    /**
     * 检查配置文件中key的对应值是否为空
     *
     * @param accessKey
     * @param secretKey
     * @param bucket
     * @throws Exception
     */
    private void checkConfigValue(String accessKey, String secretKey, String bucket) throws Exception {
        if (Strings.isNullOrEmpty(bucket) || Strings.isNullOrEmpty(accessKey) || Strings.isNullOrEmpty(secretKey)) {
            throw new Exception("请检查" + CONFIG_FILE_NAME + "文件中[" + AuthInfo.SECRET_KEY.toString() + "," + AuthInfo.ACCESS_KEY.toString() + "," + AuthInfo.BUCKET.toString() + "]的配置");
        }
    }


    /**
     * 获取七牛云token
     */
    protected static String getToken() {
        long current_time = CurrentTimeMillisClock.getInstance().now();
        if (next_invalid_time == 0 || next_invalid_time < current_time) {
            log.info("七牛云由于初始化或者token到期,开始重新获取token");
//       生成上传凭证，然后准备上传
            Auth auth = Auth.create(accessKey, secretKey);
//        得到七牛云token
            upToken = auth.uploadToken(bucket, null, expire, null);
//        设定token下次失效时间
            next_invalid_time = current_time + expire;
            return upToken;
        }
        return upToken;
    }
}
