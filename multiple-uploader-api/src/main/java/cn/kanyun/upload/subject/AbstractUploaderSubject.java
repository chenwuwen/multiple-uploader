package cn.kanyun.upload.subject;

import cn.kanyun.upload.spi.Uploader;

import java.util.Set;

/**
 * 抽象的主题
 * @author Kanyun
 * @date on 2019/12/16  13:39
 */
public interface AbstractUploaderSubject {

    /**
     * 增加观察者
     * @param observer
     */
    void addObserver(Uploader observer);

    /**
     * 观察者的数量
     * @return
     */
    int observerCount();

    /**
     * 得到观察者集合
     * @return
     */
    Set<Uploader> getObservers();

    /**
     * 通知观察者
     * 典型的推模型：
     * 主题对象向观察者推送主题的详细信息，不管观察者是否需要，推送的信息通常是主题对象的全部或部分数据
     * 推模式、拉模式区别：https://www.iteye.com/blog/raychase-1337015
     * @param sourcePath
     * @param targetPath
     */
    void notify(String sourcePath, String targetPath);
}
