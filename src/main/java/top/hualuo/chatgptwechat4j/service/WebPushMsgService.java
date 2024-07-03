package top.hualuo.chatgptwechat4j.service;

/**
 * @author quentin
 * @date 2024/7/1 下午3:34
 */
public interface WebPushMsgService {
    /**
     * 监听消息
     */
    void listenMsg() throws InterruptedException;

    /**
     * 获取最新消息
     */
    void getNewMsg();


}
