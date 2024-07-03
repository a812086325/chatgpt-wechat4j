package top.hualuo.chatgptwechat4j.utils;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import top.hualuo.chatgptwechat4j.instance.LoginInfo;

import java.net.HttpCookie;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author quentin
 * @date 2024/7/1 下午9:28
 */
public class HttpUtil {
    public static String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36";

    private static LoginInfo loginInfo = LoginInfo.getInstance();
    /**
     * 发送get请求
     * @param url
     * @param paramMap
     * @return
     */
    public static String get(String url, Map<String, Object> paramMap){
        return HttpRequest.get(url)
                .header(Header.USER_AGENT.getValue(),userAgent)
                .form(paramMap)
                .cookie(loginInfo.getCookies())
                .timeout(60000)
                .execute().body();
    }

    public static String getHeader(String url, Map<String, Object> paramMap){
        HttpResponse response = HttpRequest.get(url)
                .headerMap(getHeaders(), true)
                .form(paramMap)
                .cookie(loginInfo.getCookies())
                .timeout(20000)
                .execute();

        loginInfo.setCookies(response.getCookies());

        return response.body();
    }

    /**
     * 发送post请求
     * @param url
     * @param paramMap
     * @return
     */
    public static String post(String url, Map<String, Object> paramMap){
        return HttpRequest.post(url)
                .header(Header.USER_AGENT.getValue(),userAgent)
                .body(JSONUtil.toJsonStr(paramMap))
                .cookie(loginInfo.getCookies())
                .timeout(20000)
                .execute().body();
    }

    public static Map<String, String> getHeaders(){
        Map<String, String> headers = new HashMap<>();
        headers.put("client-version","2.0.0");
        headers.put("extspam","Go8FCIkFEokFCggwMDAwMDAwMRAGGvAESySibk50w5Wb3uTl2c2h64jVVrV7gNs06GFlWplHQbY/5FfiO++1yH4ykCyNPWKXmco+wfQzK5R98D3so7rJ5LmGFvBLjGceleySrc3SOf2Pc1gVehzJgODeS0lDL3/I/0S2SSE98YgKleq6Uqx6ndTy9yaL9qFxJL7eiA/R3SEfTaW1SBoSITIu+EEkXff+Pv8NHOk7N57rcGk1w0ZzRrQDkXTOXFN2iHYIzAAZPIOY45Lsh+A4slpgnDiaOvRtlQYCt97nmPLuTipOJ8Qc5pM7ZsOsAPPrCQL7nK0I7aPrFDF0q4ziUUKettzW8MrAaiVfmbD1/VkmLNVqqZVvBCtRblXb5FHmtS8FxnqCzYP4WFvz3T0TcrOqwLX1M/DQvcHaGGw0B0y4bZMs7lVScGBFxMj3vbFi2SRKbKhaitxHfYHAOAa0X7/MSS0RNAjdwoyGHeOepXOKY+h3iHeqCvgOH6LOifdHf/1aaZNwSkGotYnYScW8Yx63LnSwba7+hESrtPa/huRmB9KWvMCKbDThL/nne14hnL277EDCSocPu3rOSYjuB9gKSOdVmWsj9Dxb/iZIe+S6AiG29Esm+/eUacSba0k8wn5HhHg9d4tIcixrxveflc8vi2/wNQGVFNsGO6tB5WF0xf/plngOvQ1/ivGV/C1Qpdhzznh0ExAVJ6dwzNg7qIEBaw+BzTJTUuRcPk92Sn6QDn2Pu3mpONaEumacjW4w6ipPnPw+g2TfywJjeEcpSZaP4Q3YV5HG8D6UjWA4GSkBKculWpdCMadx0usMomsSS/74QgpYqcPkmamB4nVv1JxczYITIqItIKjD35IGKAUwAA==");
        headers.put(Header.USER_AGENT.getValue(),"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36");
        headers.put("referer","https://wx.qq.com/?&lang=zh_CN&target=t");
        return headers;
    }
}
