package cn.huanzi.qch.springbootjackson.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/me")
public class TestSessionController {
    @Value("${server.port}")
    private Integer projectPort;// 项目端口

    @ResponseBody
    @RequestMapping("/createSession")
    public String createSession(HttpSession session, String name) {
        session.setAttribute("name", name);
        return "当前项目端口：" + projectPort + " 当前sessionId :" + session.getId() + "在Session中存入成功！"+ "  获取的姓名:" + session.getAttribute("name");
    }

    @ResponseBody
    @GetMapping("/getSession")
    public String getSession(HttpSession session) {
        return "当前项目端口：" + projectPort + " 当前sessionId :" + session.getId() + "  获取的姓名:" + session.getAttribute("name");
    }
}
