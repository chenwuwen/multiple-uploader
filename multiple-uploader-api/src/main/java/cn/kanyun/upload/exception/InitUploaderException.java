package cn.kanyun.upload.exception;

/**
 * CDN桥接器异常
 * @author Kanyun
 * @date on 2019/12/16  16:44
 */
public class InitUploaderException extends RuntimeException {

    private String msg;

    public InitUploaderException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public InitUploaderException() {
        super();
    }
}
