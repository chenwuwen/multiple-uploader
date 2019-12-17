package cn.kanyun.upload.subject;

import cn.kanyun.upload.spi.Uploader;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 具体的主题
 * @author Kanyun
 * @date on 2019/12/16  13:44
 */
@Slf4j
public class ConcreteUploaderSubject implements AbstractUploaderSubject {

    /**
     * 保存所有观察者集合
     */
    private static final Set<Uploader> UPLOADER_OBSERVER_SET = new LinkedHashSet<>();

    @Override
    public void addObserver(Uploader observer) {
        UPLOADER_OBSERVER_SET.add(observer);
    }

    @Override
    public int observerCount() {
        return UPLOADER_OBSERVER_SET.size();
    }

    @Override
    public Set<Uploader> getObservers() {
        return UPLOADER_OBSERVER_SET;
    }

    @Override
    public void notify(String sourcePath, String targetPath) {
        log.info("开始通知观察者,观察者列表：[{}]",UPLOADER_OBSERVER_SET);
        for (Uploader uploaderObserver : UPLOADER_OBSERVER_SET) {
            uploaderObserver.push(sourcePath, targetPath);
        }
    }
}
