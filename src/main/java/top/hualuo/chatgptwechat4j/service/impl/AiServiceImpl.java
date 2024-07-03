package top.hualuo.chatgptwechat4j.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import top.hualuo.chatgptwechat4j.constant.ChatUrlConstant;
import top.hualuo.chatgptwechat4j.instance.LoginInfo;
import top.hualuo.chatgptwechat4j.service.AiService;
import top.hualuo.chatgptwechat4j.utils.HttpUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author quentin
 * @date 2024/7/1 下午3:34
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AiServiceImpl implements AiService {
    private final ChatModel chatModel;
    private LoginInfo loginInfo = LoginInfo.getInstance();
    @Value("${bot.systemPrompt}")
    private String systemPrompt;

    /**
     * AI根据聊天内容生成回复
     * @param content
     * @param fromUserName
     */
    @Override
    public void chatMsg(String content, String fromUserName) {
        if (StrUtil.isNotBlank(content)) {
            ChatResponse response = chatModel.call(getPrompt(content));
            this.sendTextMsg(response.getResult().getOutput().getContent(), fromUserName);
        }
    }


    /**
     * 根据参数获取prompt
     * @param content
     * @return
     */
    private Prompt getPrompt(String content){
        List<Message> messages = new ArrayList<>();
        Message systemMessage = new SystemPromptTemplate(systemPrompt).createMessage();
        UserMessage userMessage = new UserMessage(content);
        messages.add(systemMessage);
        messages.add(userMessage);
        return new Prompt(messages);
    }

    /**
     * 发送文本消息
     * @param content
     * @param toUserName
     */
    @Override
    public void sendTextMsg(String content, String toUserName) {

        String url = StrUtil.format(ChatUrlConstant.SEND_MSG_URL,loginInfo.getHostUrl(),loginInfo.getLoginPubParam().get("pass_ticket"));
        Map<String, Object> paramMap = new HashMap<>();
        Map<String, Object> baseRequestMap = new HashMap<>();
        Map<String, Object> msgMap = new HashMap<>();


        baseRequestMap.put("Uin", loginInfo.getLoginPubParam().get("wxuin"));
        baseRequestMap.put("Sid", loginInfo.getLoginPubParam().get("wxsid"));
        baseRequestMap.put("Skey", loginInfo.getLoginPubParam().get("skey"));
        baseRequestMap.put("DeviceID", loginInfo.getDeviceId() );

        msgMap.put("Type", 1);
        msgMap.put("Content", content);
        msgMap.put("FromUserName", loginInfo.getUserName());
        msgMap.put("ToUserName", toUserName);
        msgMap.put("LocalID", System.currentTimeMillis() * 10);
        msgMap.put("ClientMsgId", System.currentTimeMillis() * 10);

        paramMap.put("BaseRequest", baseRequestMap);
        paramMap.put("Msg", msgMap);
        paramMap.put("Scene",0);

        String result = HttpUtil.post(url, paramMap);
        if (JSONUtil.isJson(result)) {
            JSONObject jsonObject = JSONUtil.parseObj(result);
            if (ObjectUtil.equal(jsonObject.getByPath("BaseResponse.Ret"), 0)) {
                log.info("发送消息成功");
            } else {
                log.error("发送消息失败：{}", result);
            }
        }
    }
}
