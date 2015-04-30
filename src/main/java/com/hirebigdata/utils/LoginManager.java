package com.hirebigdata.utils;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 15-4-13
 * Time: 上午9:35
 * To change this template use File | Settings | File Templates.
 */
public class LoginManager {
    private Logger logger = Logger.getLogger(LoginManager.class.getName());

    private final String baseUrl = "http://ehire.51job.com/";
    private final String mainPageUrl = "http://www.51job.com";
    private String mainPageContent = null;
    private final String companyPageUrl = "http://ehire.51job.com/MainLogin.aspx";
    private String companyPageContent = null;
    private final String LoginUrl = "https://ehirelogin.51job.com/Member/UserLogin.aspx";
    private String loginSuccessPageContent = null;
    private final String logoutUrl = "http://ehire.51job.com/LoginOut.aspx";

    private String forceOfflineUrl = "";            //强制下线的 url

    private String ctmName = "";//"成都数之联科技";
    private String userName = "";//"cdsz500";
    private String password = "";//"linxiaohua87";
    private String sc = "";
    private String ec = "";
    private String checkCode = "";
    private String isRememberMe = "false";
    private String oldAccessKey = "";
    private String langtype = "";

    private String __EVENTTARGET = "";          //登录成功后强制下线需要的参数
    private String __EVENTARGUMENT = "";        //登录成功后强制下线需要的参数
    private String __VIEWSTATE = "";            //登录成功后强制下线需要的参数

    private Map<String, String> CookiesMap = new LinkedHashMap<String, String>();

    /**
     * 登录（没有跳转）
     *
     * @param url            登录地址
     * @param CookiesMap     需要的cookies信息
     * @param loginParamsMap 登录参数
     */
    private String firstLogin(String url, Map<String, String> CookiesMap, Map<String, String> loginParamsMap) {
        logger.debug("开始登录");
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> loginParamsList = new ArrayList<NameValuePair>();
        if (loginParamsMap.size() > 0) {
            for (Map.Entry<String, String> en : loginParamsMap.entrySet()) {
                loginParamsList.add(new BasicNameValuePair(en.getKey(), en.getValue()));
            }
        }

        httpPost.addHeader("Cookie", cookiesMapToString(CookiesMap));
        httpPost.addHeader("Host", "ehirelogin.51job.com");
        httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.89 Safari/537.36");
        httpPost.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        httpPost.addHeader("Accept-Encoding", "deflate");
        httpPost.addHeader("Accept-Language", "zh-CN,zh;q=0.8");
        httpPost.addHeader("Cache-Control", "max-age=0");
        httpPost.addHeader("Connection", "keep-alive");
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
        httpPost.addHeader("Referer", "http://ehire.51job.com/MainLogin.aspx");
        httpPost.addHeader("Origin", "http://ehire.51job.com");

        String firstSkipLocation = "";
        HttpResponse response = null;
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(loginParamsList, "UTF-8"));
            response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY) {
                firstSkipLocation = response.getFirstHeader("Location").getValue();
                Header[] headers = response.getAllHeaders();
                getCookiesFromHeaders(headers);
            } else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                System.out.println("请求异常");
                logger.debug("请求异常");
            } else {
                System.out.println("请求异常");
                logger.debug("请求异常");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        httpPost.releaseConnection();
        return firstSkipLocation;
    }

    /**
     * 第一次跳转
     *
     * @param url        跳转url
     * @param CookiesMap 需要cookies参数
     * @return
     */
    private String secondLogin(String url, Map<String, String> CookiesMap) {
        if (url == null || url.equals("")) {
            return "";
        }
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);

        httpPost.addHeader("Cookie", cookiesMapToString(CookiesMap));
        httpPost.addHeader("Host", "ehire.51job.com");
        httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.89 Safari/537.36");
        httpPost.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        httpPost.addHeader("Accept-Encoding", "deflate, sdch");
        httpPost.addHeader("Accept-Language", "zh-CN,zh;q=0.8");
        httpPost.addHeader("Cache-Control", "max-age=0");
        httpPost.addHeader("Connection", "keep-alive");
        httpPost.addHeader("Referer", "http://ehire.51job.com/MainLogin.aspx");

        String secondSkipLocation = "";
        HttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY) {
                secondSkipLocation = response.getFirstHeader("Location").getValue();
                Header[] headers = response.getAllHeaders();
                getCookiesFromHeaders(headers);
            } else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                System.out.println("请求异常");
                logger.debug("请求异常");
            } else {
                System.out.println("请求异常");
                logger.debug("请求异常");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return secondSkipLocation;
    }

    /**
     * 第二次跳转
     *
     * @param url        跳转url
     * @param CookiesMap 需要cookies参数
     * @return
     */
    private String thirdLogin(String url, Map<String, String> CookiesMap) {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);

        httpGet.setHeader("Cookie", cookiesMapToString(CookiesMap));
        httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        httpGet.setHeader("Accept-Encoding", "deflate,sdch");
        httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
        httpGet.setHeader("Cache-Control", "max-age=0");
        httpGet.setHeader("Connection", "keep-alive");
        httpGet.setHeader("Host", "ehire.51job.com");
        httpGet.setHeader("Referer", "http://ehire.51job.com/MainLogin.aspx");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1985.125 Safari/537.36");

        HttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                loginSuccessPageContent = EntityUtils.toString(response.getEntity());
                Header[] headers = response.getAllHeaders();
                getCookiesFromHeaders(headers);
            } else {
                System.out.println("请求异常");
                logger.debug("请求异常");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.debug("登录成功");
        return loginSuccessPageContent;
    }

    /**
     * 强制下线
     *
     * @param cookiesMap 需要的cookies信息
     * @param url        url信息
     * @return
     */
    public String forceOffline(Map<String, String> cookiesMap, String url) {
        if (url == null || url.equals("")) {
            return "";
        }
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);

        List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
        paramsList.add(new BasicNameValuePair("__EVENTTARGET", this.__EVENTTARGET));
        paramsList.add(new BasicNameValuePair("__EVENTARGUMENT", this.__EVENTARGUMENT));
        paramsList.add(new BasicNameValuePair("__VIEWSTATE", this.__VIEWSTATE));

        httpPost.addHeader("Cookie", cookiesMapToString(cookiesMap));
        httpPost.addHeader("Host", "ehire.51job.com");
        httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.89 Safari/537.36");
        httpPost.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        httpPost.addHeader("Accept-Encoding", "deflate, sdch");
        httpPost.addHeader("Accept-Language", "zh-CN,zh;q=0.8");
        httpPost.addHeader("Cache-Control", "max-age=0");
        httpPost.addHeader("Connection", "keep-alive");
        httpPost.addHeader("Referer", "http://ehire.51job.com/Member/UserOffline.aspx?tokenId=67b59d88-567e-49b6-8e83-b82a05bc&errorFlag=0&dbid=4&val=26e383010d5a6415&isRememberMe=False&sc=f926ef3d532158d3&Lang=&Flag=1");
        httpPost.addHeader("Origin", "http://ehire.51job.com");
        httpPost.addHeader("RA-Sid", "65CC976D-20150123-010948-5f3a62-2cecd8");
        httpPost.addHeader("RA-Ver", "2.9.0");

        String forceOfflineSkipLocation = "";
        HttpResponse response = null;
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(paramsList));
            response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY) {
                forceOfflineSkipLocation = response.getFirstHeader("Location").getValue();
                Header[] headers = response.getAllHeaders();
                getCookiesFromHeaders(headers);
            } else {
                logger.debug("退出失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return forceOfflineSkipLocation;
    }

    /**
     * 退出登录
     *
     * @param CookiesMap cookies参数
     */
    public void logout(Map<String, String> CookiesMap) {
        logger.debug("开始退出登录成功");
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(logoutUrl);

        httpGet.setHeader("Cookie", cookiesMapToString(CookiesMap));
        httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        httpGet.setHeader("Accept-Encoding", "deflate,sdch");
        httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
        httpGet.setHeader("Connection", "keep-alive");
        httpGet.setHeader("Host", "ehire.51job.com");
        httpGet.setHeader("Referer", "http://ehire.51job.com/Navigate.aspx?ShowTips=11&PwdComplexity=N");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1985.125 Safari/537.36");

        HttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
//            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_MOVED_TEMPORARILY) {
//                System.out.println("请求异常");
//            logger.debug("请求异常");
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.debug("退出登录成功");
        httpGet.releaseConnection();
    }

    /**
     * 要用到的登录数据
     *
     * @return
     */
    private Map<String, String> getLoginParams() {
        Map<String, String> loginParamsMap = new LinkedHashMap<String, String>();
        loginParamsMap.put("ctmName", ctmName.trim());
        loginParamsMap.put("userName", userName.trim());
        loginParamsMap.put("password", password.trim());
        loginParamsMap.put("checkCode", checkCode.trim());
        loginParamsMap.put("oldAccessKey", oldAccessKey);
        loginParamsMap.put("langtype", langtype);
        loginParamsMap.put("isRememberMe", isRememberMe);

        Document doc = Jsoup.parse(companyPageContent);
        Element fkscByIdElement = doc.getElementById("fksc");
        sc = fkscByIdElement.val();
        loginParamsMap.put("sc", sc);
        Element hidEhireGuidByIdElement = doc.getElementById("hidEhireGuid");
        ec = hidEhireGuidByIdElement.val();
        loginParamsMap.put("ec", ec);

        return loginParamsMap;
    }

    /**
     * 进入首页返回的cookies
     *
     * @return
     */
    private Map<String, String> getMainPageCookies() {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(mainPageUrl);
        HttpResponse response = null;
        Map<String, String> mainPageCookiesMap = new LinkedHashMap<String, String>();
        try {
            response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                System.out.println("请求异常");
                logger.debug("请求异常");
                return null;
            }
            mainPageContent = EntityUtils.toString(response.getEntity(), "gb2312");
            List<Cookie> mainPageCookiesList = httpClient.getCookieStore().getCookies();
            for (Cookie c : mainPageCookiesList) {
                if (c.getValue() != null && !c.getValue().equals("")) {
                    mainPageCookiesMap.put(c.getName(), c.getValue());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mainPageCookiesMap;
    }

    /**
     * 点击企业入口后进入页面返回的cookies
     *
     * @return
     */
    private Map<String, String> getCompanyPageCookies() {
        CookiesMap = getMainPageCookies();

        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(companyPageUrl);

        httpGet.addHeader("Cookie", cookiesMapToString(CookiesMap));
        HttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                System.out.println("请求异常");
                logger.debug("请求异常");
                return null;
            }
            companyPageContent = EntityUtils.toString(response.getEntity());
            List<Cookie> companyPageCookiesList = httpClient.getCookieStore().getCookies();
            for (Cookie c : companyPageCookiesList) {
                if (c.getValue() != null && !c.getValue().equals("")) {
                    CookiesMap.put(c.getName(), c.getValue());
                    if (c.getName().equalsIgnoreCase("AccessKey")) {
                        oldAccessKey = c.getValue();
                    }
                    if (c.getName().equalsIgnoreCase("LangType")) {
                        langtype = c.getValue();
                    }
                }
            }
            Header[] headers = response.getAllHeaders();
            getCookiesFromHeaders(headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return CookiesMap;
    }

    /**
     * 强制下线后登录
     *
     * @param ctmName   会员名
     * @param userName  用户名
     * @param password  密码
     * @param checkCode 验证码
     * @return
     */
    public Map<String, String> loginByForceOffline(String ctmName, String userName, String password, String checkCode) {
        this.ctmName = ctmName;
        this.userName = userName;
        this.password = password;
        this.checkCode = checkCode;

        Map<String, String> loginCookiesMap = getCompanyPageCookies();

        String firstSkipLocation = firstLogin(LoginUrl, loginCookiesMap, getLoginParams());
        String secondSkipLocation = secondLogin(firstSkipLocation, CookiesMap);
        loginSuccessPageContent = thirdLogin(baseUrl + secondSkipLocation, CookiesMap);
        if (isForceOfflineAfterLoginSuccess(loginSuccessPageContent)) {
            forceOffline(CookiesMap, "http://ehire.51job.com/Member/" + this.forceOfflineUrl);
            CookiesMap = login(ctmName, userName, password, checkCode);
        }
        return CookiesMap;
    }

    /**
     * 整个登录的全部过程（包括多次跳转）
     *
     * @param ctmName   企业名
     * @param userName  用户名
     * @param password  密码
     * @param checkCode 验证码
     * @return cookies信息
     */
    public Map<String, String> login(String ctmName, String userName, String password, String checkCode) {
        this.ctmName = ctmName;
        this.userName = userName;
        this.password = password;
        this.checkCode = checkCode;
        Map<String, String> loginCookiesMap = getCompanyPageCookies();

        String firstSkipLocation = firstLogin(LoginUrl, loginCookiesMap, getLoginParams());
        String secondSkipLocation = secondLogin(firstSkipLocation, CookiesMap);
        loginSuccessPageContent = thirdLogin(baseUrl + secondSkipLocation, CookiesMap);
        return CookiesMap;
    }

    private String cookiesMapToString(Map<String, String> CookiesMap) {
        StringBuilder requestCookies = new StringBuilder();
        if (CookiesMap.size() > 0) {
            for (Map.Entry<String, String> e : CookiesMap.entrySet()) {
                requestCookies.append(e.getKey() + "=" + e.getValue() + ";");
            }
            return requestCookies.toString();
        } else {
            return "";
        }
    }

    private void getCookiesFromHeaders(Header[] headers) {
        for (Header h : headers) {
            if (h.getName().contains("Set-Cookie") && h.getValue() != null && !h.getValue().equals("")) {
                String key_value = h.getValue().split(";")[0];
                int i = key_value.indexOf("=");
                String key = key_value.substring(0, i);
                String value = key_value.substring(i + 1);
                if (value != null && !value.equals("")) {
                    CookiesMap.put(key, value);
                }
            }
        }
    }

    /**
     * 登录成功后判断是否需要强制下线
     *
     * @param loginSuccessPageContent 成功后的页面
     * @return
     */
    private Boolean isForceOfflineAfterLoginSuccess(String loginSuccessPageContent) {
        if (loginSuccessPageContent == null || loginSuccessPageContent.equals("")) {
            return null;
        }
        Document loginSuccessPageContentDoc = Jsoup.parse(loginSuccessPageContent);
        String formAction = loginSuccessPageContentDoc.getElementById("form1").attr("action");
        this.__EVENTTARGET = loginSuccessPageContentDoc.getElementById("__EVENTTARGET").val();
        this.__EVENTARGUMENT = loginSuccessPageContentDoc.getElementById("__EVENTARGUMENT").val();
        this.__VIEWSTATE = loginSuccessPageContentDoc.getElementById("__VIEWSTATE").val();

        if (formAction.startsWith("UserOffline.aspx")) {
            if (this.__EVENTTARGET == null || this.__EVENTTARGET.equals("")) {
                this.__EVENTTARGET = "gvOnLineUser";
            }
            if (this.__EVENTARGUMENT == null || this.__EVENTARGUMENT.equals("")) {
                this.__EVENTARGUMENT = "KickOut$0";
            }
            logger.debug("需要强制下线");
            this.forceOfflineUrl = formAction;
            return true;
        } else if (formAction.startsWith("Navigate.aspx")) {
            logger.debug("不需要强制下线");
            return false;
        } else {
            logger.debug("不能判断是否要强制下线");
            return false;
        }
    }


}
