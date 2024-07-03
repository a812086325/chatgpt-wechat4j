package top.hualuo.chatgptwechat4j.constant;

import top.hualuo.chatgptwechat4j.dto.SyncUrlDTO;

import java.util.HashMap;
import java.util.Map;

/**
 * @author quentin
 * @date 2024/6/30 下午11:53
 */
public class ChatConstant {
    /**
     * 获取登录UUID固定的APPID
     */
    public static final String UUID_APPID = "wx782c26e4c19acffb";

    /**
     * 群聊标识
     */
    public static final String CHAT_ROOM_FLAG = "@@";


    /**
     * 同步url的map，并初始化数据
     */
    public static Map<String, SyncUrlDTO> syncKeyMap = new HashMap<>(){
        {
            put("https://wx2.qq.com", new SyncUrlDTO("https://file.wx2.qq.com","https://webpush.wx2.qq.com"));
            put("https://wx8.qq.com", new SyncUrlDTO("https://file.wx8.qq.com","https://webpush.wx8.qq.com"));
            put("https://wx.qq.com", new SyncUrlDTO("https://file.wx.qq.com","https://webpush.wx.qq.com"));
            put("https://web2.wechat.com", new SyncUrlDTO("https://file.web2.wechat.com","https://webpush.web2.wechat.com"));
            put("https://wechat.com", new SyncUrlDTO("https://file.web.wechat.com","https://webpush.web.wechat.com"));
        }
    };
}
