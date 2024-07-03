package top.hualuo.chatgptwechat4j.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author quentin
 * @date 2024/7/1 下午3:49
 */
@Getter
@AllArgsConstructor
public enum SelectorEnum {
    /**
     * 正常
     */
    NORMAL("0", "正常"),
    NEW_MSG("2", "有新消息"),
    MOD_CONTACT("4", "有人修改了自己的昵称或你修改了别人的备注"),
    ADD_OR_DEL_CONTACT("6", "存在删除或者新增的好友信息"),
    ENTER_OR_LEAVE_CHAT("7", "进入或离开聊天界面");

    private String code;
    private String type;

    /**
     * 根据code获取对应的SelectorEnum
     * @param code 状态码
     * @return 对应的SelectorEnum
     */
    public static SelectorEnum getSelectorEnum(String code) {
        for (SelectorEnum retCodeEnum : SelectorEnum.values()) {
            if (retCodeEnum.getCode().equals(code)) {
                return retCodeEnum;
            }
        }
        return null;
    }

}
