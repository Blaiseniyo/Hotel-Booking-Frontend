/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Filters;
import PackingBean.BackingBean;
import java.io.IOException;
 import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author hp
 */
public class LoginFilter implements Filter{

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
       
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        BackingBean session = (BackingBean) req.getSession().getAttribute("BB");
        String url = req.getRequestURI();
        
        
        if(session == null || !session.isLogged){
            if(url.indexOf("register.xhtml")>=0 ||url.indexOf("Home.xhtml") >=0){
                res.sendRedirect(req.getServletContext().getContextPath() +"/index.xhtml");
            }else{
                chain.doFilter(request, response);
            }
        }else{
            if ( url.indexOf("index.xhtml") >=0){
                res.sendRedirect(req.getServletContext().getContextPath() +"/Home.xhtml");
            }else if( url.indexOf("logout.xhtml") >= 0){
                req.getSession().removeAttribute("BB");
                res.sendRedirect(req.getServletContext().getContextPath() +"/index.xhtml");
            }else{
                chain.doFilter(request, response);
            }
        }
    }

    @Override
    public void destroy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
