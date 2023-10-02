package cn.jjaw.ktg8.type.util;

public record RSRequestError(
        ErrorType code,
        String reason
) {

    public enum ErrorType{
        /**
         * 太长时间没有回复
         */
        timeOut,
        /**
         * 接收者在处理时发生异常
         */
        acceptThrowable,
        /**
         * 消息无法发出
         */
        sendError,
    }
}
