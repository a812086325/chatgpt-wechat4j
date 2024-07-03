package top.hualuo.chatgptwechat4j;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ChatgptWechat4jApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatgptWechat4jApplication.class, args);
    }

}
