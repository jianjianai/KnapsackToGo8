package cn.jjaw.ktg8.server.event;

/***
 * 可取消事件
 */
public interface CancelEvent {
    /**
     * 设置这个事件是否取消
     */
    void setCancel(boolean cancel);

    /**
     * 事件是否被取消了
     */
    boolean isCancel();
}
