package top.hualuo.chatgptwechat4j.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author quentin
 * @date 2024/7/2 上午11:54
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SyncUrlDTO {
    /**
     * 文件主域名
     */
    private String fileUrl;

    /**
     * 同步域名
     */
    private String syncUrl;
}
