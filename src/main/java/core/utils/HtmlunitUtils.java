package core.utils;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.JavaScriptEngine;
import com.gargoylesoftware.htmlunit.util.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public class HtmlunitUtils {
    private static Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);

    public static String getPageStr(String url) {
        return getPageStr(url, null, null);
    }

    public static String getPageStr(String url, Cookie cookie, Map<String, String> header) {
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setTimeout(5000);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setUseInsecureSSL(true);//忽略ssl认证
        webClient.setJavaScriptEngine(new JavaScriptEngine(webClient));
        webClient.setJavaScriptTimeout(5000);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());// 设置ajax代理
        if (header != null && header.size() > 0) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                webClient.addRequestHeader(entry.getKey(), entry.getValue());
            }
        }
        if (cookie != null) {
            webClient.getCookieManager().addCookie(cookie);
        }

        try {
            logger.info("BROWSER " + url);
            HtmlPage page = webClient.getPage(url);
            String pageStr = page.asText();
            return pageStr;
        } catch (IOException e) {
            logger.error("IO异常", e);
            return "";
        } finally {
            webClient.close();
        }
    }
}
