package top.hualuo.chatgptwechat4j.service;

/**
 * @author quentin
 * @date 2024/7/1 下午3:34
 */
public interface AiService {

    /**
     * AI根据聊天内容回复消息
     * @param content
     * @param fromUserName
     */
    void chatMsg(String content, String fromUserName);

    /**
     * 发送文本消息
     * @param content
     * @param toUserName
     */
    void sendTextMsg(String content, String toUserName);
}
