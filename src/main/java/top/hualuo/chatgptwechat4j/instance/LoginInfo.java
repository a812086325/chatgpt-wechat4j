package top.hualuo.chatgptwechat4j.instance;

import cn.hutool.json.JSONObject;
import lombok.Data;

import java.net.HttpCookie;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author quentin
 * @date 2024/7/1 上午11:53
 */
@Data
public class LoginInfo {
    private static LoginInfo instance;

    private LoginInfo() {
    }

    public static LoginInfo getInstance() {
        if (instance == null) {
            synchronized (LoginInfo.class) {
                instance = new LoginInfo();
            }
        }
        return instance;
    }

    /**
     * 是否登录
     */
    private boolean loginFlag = false;
    /**
     * @f4c054c78f40743b095b85409dbdc1b3,微信随机码，每个联系人和群都有，每次登录由微信端随机分配
     */
    private String userName;
    /**
     * 微信号的昵称
     */
    private String nickName;
    /**
     * 头像地址
     */
    private String headImgUrl;
    /**
     * 登录公参数据
     */
    private Map<String, Object> loginPubParam = new HashMap<>();

    /**
     * 登录后返回的syncKey数据
     */
    private JSONObject syncKey;

    /**
     * 登录设备ID
     */
    private String deviceId;

    /**
     * 登录后返回的cookie
     */
    private List<HttpCookie> cookies;

    /**
     * 登录后返回的redirectUrl的主域名
     */
    private String hostUrl;

    /**
     * 文件操作
     */
    private String fileUrl;
    /**
     * 检测消息url
     */
    private String syncUrl;
}
