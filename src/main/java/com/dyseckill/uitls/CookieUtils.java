package com.dyseckill.uitls;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtils {


    private static final String COOKIE_DOMAIN = "localhost";
    private static final String COOKIE_NAME = "seckill_login_token";

    private static Logger logger = LoggerFactory.getLogger(CookieUtils.class);

    public static String readLoginToken(HttpServletRequest request){
        
        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            
            for (Cookie cookie : cookies) {
                logger.info("read cookieName:{},cookieValue:{}",cookie.getName(), cookie.getValue());
                
                if(StringUtils.equals(cookie.getName(),COOKIE_NAME)){
                    logger.info("return cookieName:{},cookieValue:{}",cookie.getName(), cookie.getValue());
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public static void writeLoginToken(HttpServletResponse response, String token){
        Cookie cookie = new Cookie(COOKIE_NAME, token);
        cookie.setDomain(COOKIE_DOMAIN);
        
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        
        
        
        cookie.setMaxAge(60 * 60 * 24 * 365);
        logger.info("write cookieName:{},cookieValue:{}", cookie.getName(),cookie.getValue());
        response.addCookie(cookie);
    }

    public static void delLoginToken(HttpServletRequest request, HttpServletResponse response){
        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for (Cookie cookie : cookies) {
                if(StringUtils.equals(cookie.getName(), COOKIE_NAME)){
                    cookie.setDomain(COOKIE_DOMAIN);
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    logger.info("del cookieName:{},cookieValue:{}",cookie.getName(),cookie.getValue());
                    response.addCookie(cookie);
                    return;
                }
            }
        }
    }
}
