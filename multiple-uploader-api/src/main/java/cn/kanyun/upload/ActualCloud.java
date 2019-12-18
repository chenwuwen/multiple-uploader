package cn.kanyun.upload;

/**
 * 定义CDN的名称,新加一个CDN桥接模块,应该添加一个枚举变量
 *
 * @author Kanyun
 * @date on 2019/12/18  10:01
 */
public enum ActualCloud {

    /**
     * 目前支持的三方云
     */
    QINIU_CLOUD("七牛云"), UP_CLOUD("又拍云"), ALI_CLOUD("阿里云"), U_CLOUD("UCloud");

    /**
     * 云名称
     */
    private String cdnName;

    ActualCloud(String cdnName) {
        this.cdnName = cdnName;
    }

    @Override
    public String toString() {
        return cdnName;
    }
}
