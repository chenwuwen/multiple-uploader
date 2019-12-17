package cn.kanyun.upload;

import cn.kanyun.upload.exception.InitUploaderException;
import cn.kanyun.upload.spi.IUploaderFactory;
import cn.kanyun.upload.spi.Uploader;
import cn.kanyun.upload.spi.UploaderFactoryBinder;
import cn.kanyun.upload.subject.AbstractUploaderSubject;
import cn.kanyun.upload.subject.ConcreteUploaderSubject;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;

import java.util.Set;

/**
 * @author Kanyun
 * @date on 2019/12/16  13:55
 */
@Slf4j
public class UploaderFactory {

    /**
     * 定义扫描包
     */
    private static final String[] basePackages = {"cn.kanyun.upload"};

    /**
     * Reflections 是java下好用的反射类库
     * https://blog.csdn.net/qq_36838191/article/details/80507206
     * https://www.cnblogs.com/boothsun/p/11146827.html
     */
    private static Reflections reflections;


    /**
     * 未初始化
     */
    static final int UNINITIALIZED = 0;

    /**
     * 正在进行初始化
     */
    static final int ONGOING_INITIALIZATION = 1;

    /**
     * 初始化成功
     */
    static final int SUCCESSFUL_INITIALIZATION = 3;

    /**
     * 初始化状态（volatile修饰）
     */
    static volatile int INITIALIZATION_STATE = UNINITIALIZED;

    static {
//        初始化工具类
        reflections = new Reflections(new ConfigurationBuilder().forPackages(basePackages).addScanners(new SubTypesScanner()).addScanners(new FieldAnnotationsScanner()));
    }


    private UploaderFactory() {
    }

    /**
     * 得到Uploader
     * 在这里直接返回了DefaultUploader的实例
     * 是因为DefaultUploader中的方法,只是用来广播事件,DefaultUploader仅作为事件源
     * 具体实现,还是需要各个观察者去实现
     *
     * @return
     */
    public static Uploader getUploader() {
        init();
        return DefaultUploader.getInstance();
    }

    static void init() {
        if (INITIALIZATION_STATE == UNINITIALIZED) {
//            如果当前初始化状态是未初始化,则进行初始化
            synchronized (UploaderFactory.class) {
                if (INITIALIZATION_STATE == UNINITIALIZED) {
//                    先将初始化状态置为正在初始化
                    INITIALIZATION_STATE = ONGOING_INITIALIZATION;
                    bindUploaderObservers();
                }
            }
        }
    }

    /**
     * 反射获取Classpath下实现UploaderFactoryBinder接口的类
     * 初始化观察者(绑定)
     */
    static void bindUploaderObservers() {
        AbstractUploaderSubject concreteUploaderSubject = new ConcreteUploaderSubject();
//        获取UploaderFactoryBinder子类
        Set<Class<? extends UploaderFactoryBinder>> subTypes = reflections.getSubTypesOf(UploaderFactoryBinder.class);
        for (Class<? extends UploaderFactoryBinder> subType : subTypes) {
            try {
//                Class.newInstance()方法会调用类的无参构造函数,但是这个API,在java9之后不被推荐了,使用clazz.getDeclaredConstructor().newInstance()来代替,为了确保兼容性,还是用老方法
                UploaderFactoryBinder binder = subType.newInstance();
                IUploaderFactory uploaderFactory = binder.getUploaderFactory();
                log.info("查找到IUploaderFactory实现类：[{}] ", binder.getUploaderFactoryClassStr());
                concreteUploaderSubject.addObserver(uploaderFactory.getUploader());
            } catch (InitUploaderException e) {
//                CDN配置类加载出错
                log.error("multiple-uploader 初始化时 [{}]捕捉到自定义异常,异常信息：[{}] 跳过...", UploaderFactory.class.getName(), e.getMessage());
                e.printStackTrace();
                continue;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
        INITIALIZATION_STATE = SUCCESSFUL_INITIALIZATION;
        log.info("multiple-uploader共找到[{}]个观察者", concreteUploaderSubject.observerCount());
        log.info("包括[{}]", concreteUploaderSubject.getObservers());
    }
}
