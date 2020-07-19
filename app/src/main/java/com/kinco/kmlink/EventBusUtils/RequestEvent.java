package com.kinco.kmlink.EventBusUtils;

/**
 * 对蓝牙服务进行数据请求
 */
public class RequestEvent {
    private String[] request;

    public RequestEvent(String[] request){
        this.request = request;
    }
    public String[] getRequest() {
        return request;
    }

    public void setRequest(String[] request) {
        this.request = request;
    }
}
