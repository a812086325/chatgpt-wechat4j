package top.hualuo.chatgptwechat4j.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author quentin
 * @date 2024/7/1 下午3:49
 */
@Getter
@AllArgsConstructor
public enum RetCodeEnum {
    /**
     * 成功
     */
    SUCCESS("0", "成功"),
    TICKET_ERROR("-14", "ticket错误"),
    PARAM_ERROR("1", "传入参数错误"),
    NOT_LOGIN_WARN("1100", "未登录提示"),
    NOT_LOGIN_CHECK("1101", "未检测到登录"),
    COOKIE_INVALID_ERROR("1102", "cookie值无效"),
    LOGIN_ENV_ERROR("1203", "当前登录环境异常，为了安全起见请不要在web端进行登录"),
    TOO_OFEN("1205", "操作频繁");

    private String code;
    private String type;

    /**
     * 根据code获取对应的RetCodeEnum
     * @param code 状态码
     * @return 对应的RetCodeEnum
     */
    public static RetCodeEnum getRetCodeEnum(String code) {
        for (RetCodeEnum retCodeEnum : RetCodeEnum.values()) {
            if (retCodeEnum.getCode().equals(code)) {
                return retCodeEnum;
            }
        }
        return null;
    }

}
