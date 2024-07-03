package top.hualuo.chatgptwechat4j.runner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import top.hualuo.chatgptwechat4j.service.LoginService;
import top.hualuo.chatgptwechat4j.service.WebPushMsgService;

/**
 * @author quentin
 * @date 2024/6/30 下午11:35
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class LoginRunner implements ApplicationRunner {
    private final LoginService loginService;
    private final WebPushMsgService webPushMsgService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("1、开始获取UUID");
        String uuId = loginService.getUuId();
        log.info("UUID:{}", uuId);
        log.info("2、开始获取二维码");
        loginService.getQrCode(uuId);
        log.info("3、请扫描二维码图片，并在手机上确认");
        loginService.login(uuId);
        log.info("4、登录成功,微信初始化");
        loginService.wxInit();
        log.info("5、微信状态通知");
        loginService.wxStatusNotify();
        log.info("6、开始监听消息");
        webPushMsgService.listenMsg();

    }
}
