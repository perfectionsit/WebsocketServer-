package cn.zhao.websocketserver;
import cn.zhao.websocketserver.pojo.MethodBean;
import cn.zhao.websocketserver.pojo.WsRequestBody;
import cn.zhao.websocketserver.pojo.WsToken;
import org.springframework.stereotype.Component;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
/**
 * Websocket信息处理端口
 */
@Component
@ServerEndpoint("/zhao")
public class WsServer {
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    /**
     * 事件，处理新连接
     * @param session 连接Session
     */
    @OnOpen
    public void onOpen(Session session) throws InvocationTargetException, IllegalAccessException {
        if (!WsConfiguration.onOpenEvent.isEmpty()) {
            WsConfiguration.onOpenEvent.getMethod().invoke(WsConfiguration.onOpenEvent.getBean(), session);
        }
    }
    /**
     * 事件，处理断开的连接
     * @param session 连接Session
     */
    @OnClose
    public void onClose(Session session) throws InvocationTargetException, IllegalAccessException {
        if (!WsConfiguration.onCloseEvent.isEmpty()) {
            WsConfiguration.onCloseEvent.getMethod().invoke(WsConfiguration.onCloseEvent.getBean(), session);
        }
    }
    /**
     * 处理请求
     * @param message 加密的请求
     * @param session 连接Session
     */
    @OnMessage
    public void onMessage(byte[] message, Session session) throws IOException, InvocationTargetException, IllegalAccessException {
        switch (message[0]) {
            //处理分片数据
            case 0 -> buffer.write(message, 1, message.length - 1);
            //处理完整数据
            case 1 -> {
                buffer.write(message, 1, message.length - 1);
                WsRequestBody wsRequestBody = WsUtil.requestDecode(buffer.toByteArray());
                assert wsRequestBody != null;
                buffer.reset();
                boolean authentication = true;
                //拦截器
                if (!WsConfiguration.authentication.isEmpty()) {
                    authentication = (boolean) WsConfiguration.authentication.getMethod().invoke(WsConfiguration.authentication.getBean(), session, wsRequestBody.getToken());
                }
                if (authentication&&wsRequestBody.getCount()!=-1) {
                    MethodBean methodBean;
                    if((methodBean=WsConfiguration.methodMap.get(wsRequestBody.getMethod()))!=null){
                        //根据映射方法入参类型与键名注入方法
                        ArrayList<Object> prams = new ArrayList<>();
                        Arrays.stream(methodBean.getParameters()).forEach(pram -> {
                            if (pram.getType().equals(WsRequestBody.class)) {
                                prams.add(wsRequestBody);
                            } else if (pram.getType().equals(byte[].class)) {
                                prams.add(wsRequestBody.getFile());
                            } else if (pram.getType().equals(WsToken.class)) {
                                prams.add(wsRequestBody.getToken());
                            } else {
                                prams.add((wsRequestBody.getData()).getObject(pram.getName(), pram.getType()));
                            }
                        });
                        if (session.isOpen())
                            WsUtil.sendResponse(session, wsRequestBody.getCount(), methodBean.getMethod().invoke(methodBean.getBean(), prams.toArray()));
                    }else{
                        throw new RuntimeException("请求没有找到对应映射方法路径");
                    }
                }
            }
            case 2 -> WsUtil.sendPong(session);
        }
    }
}