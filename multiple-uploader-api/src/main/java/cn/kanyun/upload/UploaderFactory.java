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

import java.util.Iterator;
import java.util.ServiceLoader;
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

    /**
     * 主题对象
     */
    private static AbstractUploaderSubject concreteUploaderSubject = new ConcreteUploaderSubject();

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
//                    使用SPI代替反射,首先SPI是寻找服务实现类更为正统的方式,其次SPI没有显示的异常需要捕获,SPI更加优雅
//                    不过使用SPI需要实现类,编辑META-INF/services/下的文件,但最终还是决定使用SPI,因为这种方式可以更优雅的获取接口(服务)实现
//                    bindUploaderObserversByReflection();
                    bindUploaderObserversBySpi();
                }
            }
        }
    }

    /**
     * 反射获取Classpath下实现UploaderFactoryBinder接口的类
     * 初始化观察者(绑定)
     */
    static void bindUploaderObserversByReflection() {
//        获取UploaderFactoryBinder子类
        Set<Class<? extends UploaderFactoryBinder>> subTypes = reflections.getSubTypesOf(UploaderFactoryBinder.class);
        log.info("利用反射开始加载[{}]的子类,共找到[{}]个", UploaderFactoryBinder.class.getName(), subTypes.size());
        for (Class<? extends UploaderFactoryBinder> subType : subTypes) {
            try {
//                Class.newInstance()方法会调用类的无参构造函数,但是这个API,在java9之后不被推荐了,使用clazz.getDeclaredConstructor().newInstance()来代替,为了确保兼容性,还是用老方法
                UploaderFactoryBinder binder = subType.newInstance();
                binder.init();
                IUploaderFactory uploaderFactory = binder.getUploaderFactory();
                log.info("查找到IUploaderFactory实现类：[{}] ", binder.getUploaderFactoryClassStr());
                concreteUploaderSubject.addObserver(uploaderFactory.getUploader());
            } catch (InitUploaderException e) {
//                CDN配置类加载出错
                log.error("multiple-uploader 初始化时 [{}]捕捉到自定义异常,异常信息：[{}] 跳过...", UploaderFactory.class.getName(), e.getMessage());
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                log.error("multiple-uploader 初始化时 [{}]捕捉到反射异常,异常信息：[{}] 跳过...", UploaderFactory.class.getName(), e.getMessage());
                e.printStackTrace();
            } catch (InstantiationException e) {
                log.error("multiple-uploader 初始化时 [{}]捕捉到反射异常,异常信息：[{}] 跳过...", UploaderFactory.class.getName(), e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                log.error("multiple-uploader 初始化时 [{}]捕捉到其他异常,异常信息：[{}] 跳过...", UploaderFactory.class.getName(), e.getMessage());
                e.printStackTrace();

            }
        }
        INITIALIZATION_STATE = SUCCESSFUL_INITIALIZATION;
        log.info("multiple-uploader共找到[{}]个观察者", concreteUploaderSubject.observerCount());
        log.info("包括[{}]", concreteUploaderSubject.getObservers());
    }

    /**
     * 使用SPI获取Classpath下实现UploaderFactoryBinder接口的类
     * 初始化观察者(绑定)
     */
    static void bindUploaderObserversBySpi() {
        log.info("使用SPI机制进行查找UploaderFactoryBinder接口的实现类");
//      利用ServiceLoader获取UploaderFactoryBinder子类,需要注意的是使用ServiceLoader,唯一强制要求的是，提供者类(子类)必须具有不带参数的构造方法，以便它们可以在加载中被实例化
        ServiceLoader<UploaderFactoryBinder> uploaderFactoryBinderServiceLoader = ServiceLoader.load(UploaderFactoryBinder.class);
        Iterator<UploaderFactoryBinder> it = uploaderFactoryBinderServiceLoader.iterator();
        int count = 0;
        while (it.hasNext()) {
            count++;
            try {
//                it.next()[it是ServiceLoader对象],调用的next()方法,会调用ServiceLoader的nextService()方法,最后利用反射调用了实现类的无参构造方法
//                需要注意的是,如果这一步出现错误,会throw Error的子类对象,因此是无法使用Exception来捕获异常的,因此实现类的构造方法,不应该进行一些可能会抛出异常的操作
                UploaderFactoryBinder binder = it.next();
                binder.init();
                IUploaderFactory uploaderFactory = binder.getUploaderFactory();
                log.info("查找到IUploaderFactory实现类：[{}] ", binder.getUploaderFactoryClassStr());
                concreteUploaderSubject.addObserver(uploaderFactory.getUploader());
            } catch (InitUploaderException e) {
//                CDN配置类加载出错
                log.error("multiple-uploader 初始化时 [{}]捕捉到自定义异常,异常信息：[{}] 跳过...", UploaderFactory.class.getName(), e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                log.error("multiple-uploader 初始化时 [{}]捕捉到其他异常,异常信息：[{}] 跳过...", UploaderFactory.class.getName(), e.getMessage());
                e.printStackTrace();
            }
        }
        log.info("ServiceLoader开始加载[{}]的子类,共找到[{}]个", UploaderFactoryBinder.class.getName(), count);
        INITIALIZATION_STATE = SUCCESSFUL_INITIALIZATION;
        log.info("multiple-uploader共找到[{}]个观察者", concreteUploaderSubject.observerCount());
        log.info("包括[{}]", concreteUploaderSubject.getObservers());
    }
}
