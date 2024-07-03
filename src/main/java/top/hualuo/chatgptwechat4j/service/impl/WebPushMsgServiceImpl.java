package top.hualuo.chatgptwechat4j.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import top.hualuo.chatgptwechat4j.constant.ChatConstant;
import top.hualuo.chatgptwechat4j.constant.ChatUrlConstant;
import top.hualuo.chatgptwechat4j.enums.MsgCodeEnum;
import top.hualuo.chatgptwechat4j.enums.RetCodeEnum;
import top.hualuo.chatgptwechat4j.enums.SelectorEnum;
import top.hualuo.chatgptwechat4j.instance.LoginInfo;
import top.hualuo.chatgptwechat4j.service.AiService;
import top.hualuo.chatgptwechat4j.service.WebPushMsgService;
import top.hualuo.chatgptwechat4j.utils.HttpUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author quentin
 * @date 2024/7/1 下午3:34
 */
@Service
public class WebPushMsgServiceImpl implements WebPushMsgService {
    private static final Logger log = LoggerFactory.getLogger(WebPushMsgServiceImpl.class);
    private LoginInfo loginInfo = LoginInfo.getInstance();
    @Autowired
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
    @Autowired
    private AiService aiService;
    @Value("${bot.atFlag}")
    private String atFlag;

    /**
     * 监听消息
     */
    @Override
    public void listenMsg() throws InterruptedException {
        // 开启新的线程，一直监听
        scheduledThreadPoolExecutor.scheduleWithFixedDelay(() -> {
            while (loginInfo.isLoginFlag()) {
                // 检测是否有新消息
                Map<String, Object> paramMap = new HashMap<>();

                paramMap.put("r", System.currentTimeMillis() * 1000);
                paramMap.put("uin", loginInfo.getLoginPubParam().get("wxuin"));
                paramMap.put("sid", loginInfo.getLoginPubParam().get("wxsid"));
                paramMap.put("skey", loginInfo.getLoginPubParam().get("skey"));
                paramMap.put("deviceid", loginInfo.getDeviceId());
                paramMap.put("synckey", getSyncKey());
                paramMap.put("_", System.currentTimeMillis());
                String result = HttpUtil.get(StrUtil.format(ChatUrlConstant.CHECK_NEW_MSG_URL,loginInfo.getSyncUrl()), paramMap);
                String regEx = "window.synccheck=\\{retcode:\"(\\d+)\",selector:\"(\\d+)\"\\}";
                String retCode = ReUtil.getGroup1(regEx, result);
                String selector = ReUtil.get(regEx, result,2);
                // 判断retCode是否等于RetCodeEnum.code，如果不等，则说明有错误，则根据retCodeEnum.msg进行相应的处理
                if (!StrUtil.equals(retCode, RetCodeEnum.SUCCESS.getCode())) {
                    log.error("监听消息错误：{}",RetCodeEnum.getRetCodeEnum(retCode).getType());
                    System.exit(0);
                }

                // 判断是否新消息
                if (StrUtil.equals(selector, SelectorEnum.NEW_MSG.getCode())) {
                    // 获取新消息
                    this.getNewMsg();
                }
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    /**
     * 获取新消息
     */
    @Override
    public void getNewMsg() {
        String url = StrUtil.format(ChatUrlConstant.SYNC_NEW_MSG_URL,loginInfo.getHostUrl(), loginInfo.getLoginPubParam().get("wxsid"), loginInfo.getLoginPubParam().get("skey"), loginInfo.getLoginPubParam().get("pass_ticket"));
        Map<String, Object> paramMap = new HashMap<>();
        Map<String, Object> baseRequestMap = new HashMap<>();

        baseRequestMap.put("Uin", loginInfo.getLoginPubParam().get("wxuin"));
        baseRequestMap.put("Sid", loginInfo.getLoginPubParam().get("wxsid"));
        baseRequestMap.put("Skey", loginInfo.getLoginPubParam().get("skey"));
        baseRequestMap.put("DeviceID", loginInfo.getDeviceId() );
        paramMap.put("BaseRequest", baseRequestMap);
        paramMap.put("SyncKey", loginInfo.getSyncKey());
        paramMap.put("rr",-System.currentTimeMillis() / 1000);

        String result = HttpUtil.post(url, paramMap);
        if (JSONUtil.isJson(result)) {
            JSONObject jsonObject = JSONUtil.parseObj(result);
            if (ObjectUtil.equal(jsonObject.getByPath("BaseResponse.Ret"), 0)) {
                JSONObject syncKeyObj = jsonObject.getJSONObject("SyncKey");
                loginInfo.setSyncKey(syncKeyObj);
                JSONArray msgArray = jsonObject.getJSONArray("AddMsgList");
                for (int i = 0; i < msgArray.size(); i++) {
                    JSONObject msgObj = msgArray.getJSONObject(i);
                    // 解析消息
                    parseMsg(msgObj);
                }
            } else {
                log.error("获取新消息失败：{}", result);
            }
        }

    }



    /**
     * 解析消息，判断类型执行不同的逻辑
     * @return
     */
    private void parseMsg(JSONObject msgObj) {
        String content = msgObj.getStr("Content");
        // 发消息的人
        String fromUserName = msgObj.getStr("FromUserName");
        // 收消息的人
        String toUserName = msgObj.getStr("ToUserName");
        // 服务端返回的消息id，可用于撤回接口参数。如果是图片，该参数还可以作为调用微信获取图片接口的参数之一
        String msgId = msgObj.getStr("MsgId");
        // 消息类型，1为文字，3为图片...，具体请参照消息类型表
        Integer msgType = msgObj.getInt("MsgType");

        log.info("收到{}消息：{}，来自：{}，消息id：{}",MsgCodeEnum.getMsgCodeEnum(msgType).getType(), content, fromUserName, msgId);

        if (ObjectUtil.equal(msgType, MsgCodeEnum.MSG_TYPE_TEXT.getCode())) {
            // 文本消息
            // 判断是私聊还是群聊
            if (StrUtil.startWith(fromUserName, ChatConstant.CHAT_ROOM_FLAG)) {
                // 群聊消息,需要处理一下消息内容
                content = content.substring(content.indexOf("<br/>") + "<br/>".length()).trim();

                // 判断是否是@我
                if (!StrUtil.contains(content,atFlag)) {
                    return;
                }
                content = StrUtil.replace(content, atFlag,"");
            }
            // AI回复消息
            aiService.chatMsg(content,fromUserName);
        }
    }

    private String getSyncKey() {
        JSONArray keys = loginInfo.getSyncKey().getJSONArray("List");
        if (keys.isEmpty()) {
            log.error("synckey为空,请重新登录，系统退出");
            System.exit(1);
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            JSONObject obj = keys.getJSONObject(i);
            sb.append(obj.getStr("Key")).append("_").append(obj.getStr("Val")).append("|");
        }
        return StrUtil.removeSuffix(sb.toString(), "|");
    }
}
