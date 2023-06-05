package com.zuul.filter;

import com.authcommon.utils.JwtUtils;
import com.common.utils.CookieUtils;
import com.ctc.wstx.util.StringUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.zuul.config.FilterProperties;
import com.zuul.config.JwtProperties;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 过滤器的类型：pre rount post error
 */

@Component
@EnableConfigurationProperties({JwtProperties.class, FilterProperties.class})
public class LoginFilter extends ZuulFilter {

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private FilterProperties filterProperties;

    @Override
    public String filterType() {
        return "pre";
    }

    /**
     * 执行顺序，返回值越小，优先级越高
     *
     * @return
     */
    @Override
    public int filterOrder() {
        return 10;
    }


    /**
     * 是否执行该过滤器
     * true：执行run方法
     * false：就不执行
     *
     * @return
     */
    @Override
    public boolean shouldFilter() {
        List<String> allowPaths = filterProperties.getAllowPaths();
        //初始化context上下文对象
        RequestContext currentContext = RequestContext.getCurrentContext();
        //获取request对象
        HttpServletRequest request = currentContext.getRequest();
        //获取url
        String url = request.getRequestURL().toString();
        for (String allowPath : allowPaths) {
            if (StringUtils.contains(url, allowPath)) {
                return false;
            }
        }
        return true;
    }


    /**
     * 编写过滤器的业务逻辑
     *
     * @return
     * @throws ZuulException
     */
    @Override
    public Object run() throws ZuulException {
        //初始化context上下文对象
        RequestContext currentContext = RequestContext.getCurrentContext();
        //获取request对象
        HttpServletRequest request = currentContext.getRequest();

        //获取参数
        String token = CookieUtils.getCookieValue(request, jwtProperties.getCookieName());

        try {
            JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
        } catch (Exception e) {
            //解析失败,返回到异常
            currentContext.setSendZuulResponse(false);
            currentContext.setResponseStatusCode(HttpStatus.SC_UNAUTHORIZED);
            e.printStackTrace();
        }

        return null;

       /* if (StringUtils.isBlank(token)){
            //拦截,不转发请求
            currentContext.setSendZuulResponse(false);
            //响应状态码，401身份未认证
            currentContext.setResponseStatusCode(HttpStatus.SC_UNAUTHORIZED);
            //设置相应的提示
            currentContext.setResponseBody("被过滤器过滤了，去把过滤器关了");
        }

        //返回值null,该过滤器什么都不做
        return null;*/
    }
}
