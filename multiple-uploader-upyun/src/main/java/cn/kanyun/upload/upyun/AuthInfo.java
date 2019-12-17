package cn.kanyun.upload.upyun;

/**
 * @author Kanyun
 * @date on 2019/12/16  15:19
 */
public enum AuthInfo {

    /**
     * CDN的一些认证信息的key
     */
    USERNAME("upyun.userName"), PASSWORD("upyun.password"), BUCKET("upyun.bucket");

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
