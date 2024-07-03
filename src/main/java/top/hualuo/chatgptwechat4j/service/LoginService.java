package top.hualuo.chatgptwechat4j.service;

import com.google.zxing.NotFoundException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * @author quentin
 * @date 2024/6/30 下午11:38
 */
public interface LoginService {
    /**
     * 获取UUID
     * @return UUID
     */
    String getUuId();

    /**
     * 获取登录二维码
     * @param uuId
     */
    void getQrCode(String uuId) throws IOException, NotFoundException;

    /**
     * 登录
     * @param uuId
     */
    void login(String uuId) throws Exception;

    /**
     * 根据跳转链接获取公参
     * @param redirectUri
     */
    void getRedirect(String redirectUri) throws Exception;

    /**
     * 微信初始化
     */
    void wxInit();

    /**
     * 微信状态通知
     */
    void wxStatusNotify();
}
