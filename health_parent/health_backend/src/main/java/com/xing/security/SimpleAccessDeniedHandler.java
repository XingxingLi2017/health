package com.xing.security;

import com.alibaba.fastjson.JSON;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Component
public class SimpleAccessDeniedHandler implements AccessDeniedHandler {
    /***
     * give error code and message when access is denied
     * @param httpServletRequest
     * @param httpServletResponse
     * @param e
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        Map<String, String> map = new HashMap<>();
        map.put("url" , httpServletRequest.getRequestURL().toString());
        map.put("status" , "403");
        map.put("message" , "You don't have the authority.");

        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);

        String content = JSON.toJSONString(map);
        PrintWriter writer = httpServletResponse.getWriter();
        writer.print(content);
        writer.flush();
        writer.close();
    }
}
