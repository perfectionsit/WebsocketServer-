package cn.zhao.websocketserver.pojo;
import com.alibaba.fastjson2.JSONObject;
/**
 * Websocket请求包POJO
 */
public class WsRequestBody {
    /**
     * 携带的token数据（可为空）
     */
    private WsToken token;
    /**
     * 方法映射路径（不可为空）
     */
    private String  method;
    /**
     * 方法标识（不可为空）
     */
    private int count;
    /**
     * 请求是否包含byte[]类型，String类型请求体、
     */
    private boolean hasFile, hasData;
    @Override
    public String toString() {
        return "WebsocketRequestBody{" +
                "token='" + token + '\'' +
                ", method='" + method + '\'' +
                ", count=" + count +
                ", hasFile=" + hasFile +
                ", hasData=" + hasData +
                ", file=" + file +
                ", data=" + data +
                '}';
    }
    private byte[] file;
    JSONObject data;
    public WsToken getToken() {
        return token;
    }
    public void setToken(WsToken token) {
        this.token = token;
    }
    public String getMethod() {
        return method;
    }
    public void setMethod(String method) {
        this.method = method;
    }
    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }
    public boolean isHasFile() {
        return hasFile;
    }
    public void setHasFile(boolean hasFile) {
        this.hasFile = hasFile;
    }
    public boolean isHasData() {
        return hasData;
    }
    public void setHasData(boolean hasData) {
        this.hasData = hasData;
    }
    public byte[] getFile() {
        return file;
    }
    public void setFile(byte[] file) {
        this.file = file;
    }
    public JSONObject getData() {
        return data;
    }
    public void setData(JSONObject data) {
        this.data = data;
    }
}