package chc.fortumo.interceptor;

import chc.fortumo.controller.CounterSessionData;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
public class CounterInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(true);

        if (session.getAttribute("counter") == null) {
            session.setAttribute("counter", new CounterSessionData());
        }

        return true;
    }
}
