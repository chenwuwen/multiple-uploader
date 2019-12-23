package cn.kanyun.upload.handler;

import lombok.AllArgsConstructor;

import java.util.function.BiConsumer;

/**
 * 上传完成回调接口
 * @author Kanyun
 * @date on 2019/12/23  11:15
 */
@AllArgsConstructor
public abstract class CompleteCallback implements BiConsumer<Void, Throwable> {

    private String sourcePath;
    private String targetPath;


    @Override
    public void accept(Void aVoid, Throwable throwable) {
        handler(sourcePath, targetPath);
    }

    /**
     * 上传完成(不管失败还是成功)时处理 抽象方法
     *
     * @param sourcePath
     * @param targetPath
     */
    public abstract void handler(String sourcePath, String targetPath);
}
