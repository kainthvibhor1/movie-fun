package org.superbiz.moviefun;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;

@SpringBootApplication
public class Application {

    public ServletRegistrationBean getRegistrationBean(ActionServlet servlet) {
        return new ServletRegistrationBean(servlet, "/moviefun/*");
    }
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
