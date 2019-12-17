package cn.kanyun.upload.qiniu;

/**
 * @author Kanyun
 * @date on 2019/12/16  15:19
 */
public enum AuthInfo {

    /**
     * CDN的一些认证信息的key
     */
    ACCESS_KEY("qiniu.accessKey"), SECRET_KEY("qiniu.secretKey"), BUCKET("qiniu.bucket");

    /**
     * .properties 文件中的key
     */
    private String key;

    AuthInfo(String key) {
        this.key = key;
    }


    @Override
    public String toString() {
        return key;
    }
}
