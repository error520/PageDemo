package com.kinco.kmlink.EventBusUtils;

/**
 * 消息事件
 */
public class MessageEvent {
//    public final static String

    public MessageEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String message;
}
