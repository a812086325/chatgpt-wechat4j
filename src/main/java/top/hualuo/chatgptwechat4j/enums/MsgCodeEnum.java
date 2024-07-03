package top.hualuo.chatgptwechat4j.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author quentin
 * @date 2024/7/1 下午3:49
 */
@Getter
@AllArgsConstructor
public enum MsgCodeEnum {

	/**
	 * 文本消息类型
	 */
	MSG_TYPE_TEXT(1, "文本消息类型"),
	MSG_TYPE_IMAGE(3, "图片消息"),
	MSG_TYPE_VOICE(34, "语音消息"),
	MSG_TYPE_VIDEO(43, "小视频消息"),
	MSG_TYPE_MICRO_VIDEO(62, "短视频消息"),
	MSG_TYPE_EMOTICON(47, "表情消息"),
	MSG_TYPE_MEDIA(49, "多媒体消息"),
	MSG_TYPE_VOIP_MSG(50, ""),
	MSG_TYPE_VOIP_NOTIFY(52, ""),
	MSG_TYPE_VOIP_INVITE(53, ""),
	MSG_TYPE_LOCATION(48, ""),
	MSG_TYPE_STATUS_NOTIFY(51, ""),
	MSG_TYPE_SYS_NOTICE(9999, ""),
	MSG_TYPE_POSSIBLEFRIEND_MSG(40, ""),
	MSG_TYPE_VERIFY_MSG(37, "好友请求"),
	MSG_TYPE_SHARE_CARD(42, ""),
	MSG_TYPE_SYS(10000, "系统消息"),
	MSG_TYPE_RECALLED(10002, "")
	
	;

	private int code;
	private String type;

	/**
	 * 根据code获取枚举
	 */
	public static MsgCodeEnum getMsgCodeEnum(int code) {
		for (MsgCodeEnum msgCodeEnum : MsgCodeEnum.values()) {
			if (msgCodeEnum.getCode() == code) {
				return msgCodeEnum;
			}
		}
		return null;
	}

}
