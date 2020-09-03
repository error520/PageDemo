package com.kinco.kmlink.EventBusUtils;

/**
 * 对蓝牙服务进行数据或功能请求
 * {"功能", "参数", "参数"}
 */
public class RequestEvent {
    private String[] request;

    public RequestEvent(String[] request) {
        this.request = request;
    }

    public String[] getRequest() {
        return request;
    }

    public void setRequest(String[] request) {
        this.request = request;
    }
}
