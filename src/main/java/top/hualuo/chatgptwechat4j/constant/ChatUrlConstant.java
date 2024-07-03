package top.hualuo.chatgptwechat4j.constant;

/**
 * @author quentin
 * @date 2024/6/30 下午11:46
 */
public interface ChatUrlConstant {
    /**
     * 登录基础域名
     */
    String LOGIN_BASE_URL = "https://login.weixin.qq.com";

    /**
     * 微信网页基础域名
     */
    String WEB_BASE_URL = "https://wx.qq.com";

    /**
     * 获取登录UUID的URL
     */
    String UUID_URL = LOGIN_BASE_URL+"/jslogin";

    /**
     * 登录二维码的URL
     */
    String QRCODE_URL = LOGIN_BASE_URL+"/l/";

    /**
     * 登录URL
     */
    String LOGIN_URL = LOGIN_BASE_URL+"/cgi-bin/mmwebwx-bin/login";


    /**
     * 初始化URL
     */
    String INIT_URL = "{}/cgi-bin/mmwebwx-bin/webwxinit?r={}&pass_ticket={}";

    /**
     * 微信状态通知URL
     */
    String STATUS_NOTIFY_URL = WEB_BASE_URL+"/cgi-bin/mmwebwx-bin/webwxstatusnotify?lang=zh_CN&pass_ticket={}";

    /**
     * 检查是否有新消息URL
     */
    String CHECK_NEW_MSG_URL = "{}/cgi-bin/mmwebwx-bin/synccheck";

    /**
     * 获取最新消息URL
     */
    String SYNC_NEW_MSG_URL = "{}/cgi-bin/mmwebwx-bin/webwxsync?sid={}&skey={}&pass_ticket={}";

    /**
     * 发送消息URL
     */
    String SEND_MSG_URL = "{}/cgi-bin/mmwebwx-bin/webwxsendmsg?pass_ticket={}";
}
