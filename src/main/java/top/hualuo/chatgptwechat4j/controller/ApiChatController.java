package top.hualuo.chatgptwechat4j.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 * @author quentin
 * @date 2024/7/2 16:00
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat")
@Tag(name = "AI对话接口")
public class ApiChatController {
    private final ChatModel chatModel;

    @GetMapping
    @Operation(summary = "普通聊天，等待回复完返回")
    public String completions(String msg) {
       return chatModel.call(msg);
    }

}
