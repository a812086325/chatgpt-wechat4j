package top.hualuo.chatgptwechat4j.service.impl;

import cn.hutool.core.util.*;
import cn.hutool.extra.qrcode.BufferedImageLuminanceSource;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.zxing.*;
import com.google.zxing.common.HybridBinarizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import top.hualuo.chatgptwechat4j.constant.ChatConstant;
import top.hualuo.chatgptwechat4j.constant.ChatUrlConstant;
import top.hualuo.chatgptwechat4j.dto.SyncUrlDTO;
import top.hualuo.chatgptwechat4j.instance.LoginInfo;
import top.hualuo.chatgptwechat4j.service.LoginService;
import top.hualuo.chatgptwechat4j.utils.ConsoleQrCodeUtil;
import top.hualuo.chatgptwechat4j.utils.HttpUtil;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author quentin
 * @date 2024/6/30 下午11:38
 */
@Service
public class LoginServiceImpl implements LoginService {
    private static final Logger log = LoggerFactory.getLogger(LoginServiceImpl.class);
    private LoginInfo loginInfo = LoginInfo.getInstance();

    /**
     * 获取登录的uuid
     *
     * @return
     */
    @Override
    public String getUuId() {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("appid", ChatConstant.UUID_APPID);
        paramMap.put("fun", "new");
        paramMap.put("lang", "zh_CN");
        paramMap.put("redirect_uri", "https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxnewloginpage?mod=desktop");

        String uuidResult = HttpUtil.get(ChatUrlConstant.UUID_URL, paramMap);
        String regEx = "window.QRLogin.code = (\\d+); window.QRLogin.uuid = \"(\\S+?)\";";
        String code = ReUtil.getGroup1(regEx, uuidResult);
        if (ObjectUtil.notEqual(Integer.parseInt(code), HttpStatus.HTTP_OK)) {
            log.error("获取uuid失败，程序退出。");
            System.exit(0);
        }
        return ReUtil.get(regEx, uuidResult,2);
    }

    /**
     * 获取登录二维码
     *
     * @param uuId
     */
    @Override
    public void getQrCode(String uuId) throws IOException, NotFoundException {
        String qcCodeUrl = ChatUrlConstant.QRCODE_URL + uuId;
        log.info("二维码地址：{}",qcCodeUrl);
        ConsoleQrCodeUtil.printQrCode(qcCodeUrl);

    }

    /**
     * 登录
     * @param uuId
     */
    @Override
    public void login(String uuId) throws Exception {
        while (!loginInfo.isLoginFlag()){
            HashMap<String, Object> paramMap = new HashMap<>();
            paramMap.put("tip", 0);
            paramMap.put("uuid", uuId);
            paramMap.put("r", -System.currentTimeMillis() / 1579L);
            paramMap.put("_", System.currentTimeMillis());
            paramMap.put("loginicon",true);

            String result = HttpUtil.get(ChatUrlConstant.LOGIN_URL, paramMap);
            switch (getLoginCode(result)) {
                case "200":
                    loginInfo.setLoginFlag(true);
                    log.info("登录成功,开始获取公参");
                    this.getRedirect(getRedirectUri(result));

                    break;
                case "201":
                    log.info("请点击微信确认按钮，进行登陆");
                    break;
                case "408":
                    log.info("二维码过期");
                    break;
                default:
                    log.info("其他情况");
                    break;
            }
        }

    }

    /**
     * 根据跳转链接获取公参
     * @param redirectUri
     */
    @Override
    public void getRedirect(String redirectUri) throws Exception {
        // 获取主域名
        String hostUrl = redirectUri.substring(0, redirectUri.indexOf('/', redirectUri.indexOf("://") + 3));
        loginInfo.setHostUrl(hostUrl);

        SyncUrlDTO syncUrlDTO = ChatConstant.syncKeyMap.get(hostUrl);
        if (ObjectUtil.isNotEmpty(syncUrlDTO)) {
            loginInfo.setFileUrl(syncUrlDTO.getSyncUrl());
            loginInfo.setSyncUrl(syncUrlDTO.getSyncUrl());
        }

        String result = HttpUtil.getHeader(redirectUri,null);

        // 创建自定义的DocumentBuilderFactory并禁用DOCTYPE声明

        // 使用自定义的DocumentBuilder解析XML文档
        Document document = XmlUtil.parseXml(result);

        if (document != null) {
            // 获取根元素
            Element root = document.getDocumentElement();

            // 获取skey
            String skey = XmlUtil.elementText(root, "skey");
            // 获取wxsid
            String wxsid = XmlUtil.elementText(root, "wxsid");
            // 获取wxuin
            String wxuin = XmlUtil.elementText(root, "wxuin");
            // 获取pass_ticket
            String pass_ticket = XmlUtil.elementText(root, "pass_ticket");

            Map<String, Object> loginPubParam = loginInfo.getLoginPubParam();
            loginPubParam.put("skey", skey);
            loginPubParam.put("wxsid", wxsid);
            loginPubParam.put("wxuin", wxuin);
            loginPubParam.put("pass_ticket", pass_ticket);

            log.info("公参获取成功：{}", loginPubParam);
        }
    }

    /**
     * 初始化微信
     */
    @Override
    public void wxInit() {
        String url = StrUtil.format(ChatUrlConstant.INIT_URL, loginInfo.getHostUrl(),-System.currentTimeMillis() / 1579, loginInfo.getLoginPubParam().get("pass_ticket"));
        Map<String, Object> paramMap = new HashMap<>();
        Map<String, Object> baseRequestMap = new HashMap<>();
        // 设置设备id
        loginInfo.setDeviceId("e" + RandomUtil.randomNumbers(15));

        baseRequestMap.put("Uin", loginInfo.getLoginPubParam().get("wxuin"));
        baseRequestMap.put("Sid", loginInfo.getLoginPubParam().get("wxsid"));
        baseRequestMap.put("Skey", loginInfo.getLoginPubParam().get("skey"));
        baseRequestMap.put("DeviceID", loginInfo.getDeviceId() );
        paramMap.put("BaseRequest", baseRequestMap);

        String result = HttpUtil.post(url, paramMap);
        if (JSONUtil.isJson(result)) {
            JSONObject jsonObject = JSONUtil.parseObj(result);
            log.info("初始化微信结果：{},0为成功", jsonObject.getByPath("BaseResponse.Ret"));

            loginInfo.setSyncKey(jsonObject.getJSONObject("SyncKey"));
            JSONObject userObj = jsonObject.getJSONObject("User");
            loginInfo.setUserName(userObj.getStr("UserName"));
            loginInfo.setNickName(userObj.getStr("NickName"));
            loginInfo.setHeadImgUrl(userObj.getStr("HeadImgUrl"));

            log.info("初始化微信成功，昵称：{},头像地址：{}", loginInfo.getNickName(), loginInfo.getHeadImgUrl());
        }

    }

    /**
     * 微信状态通知
     */
    @Override
    public void wxStatusNotify() {
        String url = StrUtil.format(ChatUrlConstant.STATUS_NOTIFY_URL, loginInfo.getLoginPubParam().get("pass_ticket"));
        Map<String, Object> paramMap = new HashMap<>();
        Map<String, Object> baseRequestMap = new HashMap<>();
        baseRequestMap.put("Uin", loginInfo.getLoginPubParam().get("wxuin"));
        baseRequestMap.put("Sid", loginInfo.getLoginPubParam().get("wxsid"));
        baseRequestMap.put("Skey", loginInfo.getLoginPubParam().get("skey"));
        baseRequestMap.put("DeviceID",  loginInfo.getDeviceId());
        paramMap.put("BaseRequest", baseRequestMap);
        paramMap.put("Code", 3);
        paramMap.put("FromUserName", loginInfo.getUserName());
        paramMap.put("ToUserName", loginInfo.getUserName());
        paramMap.put("ClientMsgId", System.currentTimeMillis() / 1000);

        HttpUtil.post(url, paramMap);
    }

    /**
     * 获取登录code
     */
    private String getLoginCode(String result) {
        String regEx = "window.code=(\\d+)";
        return ReUtil.getGroup1(regEx, result);
    }

    /**
     * 获取登录redirect_uri
     */
    private String getRedirectUri(String result) {
        String regEx = "window.redirect_uri=\"(\\S+)\";";
        return ReUtil.getGroup1(regEx, result);
    }
}
