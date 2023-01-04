package ru.otus.core.web.server;

import com.google.gson.Gson;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import ru.otus.dao.ClientDao;
import ru.otus.dao.UserDao;
import ru.otus.core.web.services.TemplateProcessor;
import ru.otus.core.web.services.UserAuthService;
import ru.otus.core.web.servlet.AuthorizationFilter;
import ru.otus.core.web.servlet.LoginServlet;


import java.util.Arrays;

public class UsersWebServerWithFilterBasedSecurity extends UsersWebServerSimple {
    private final UserAuthService authService;

    public UsersWebServerWithFilterBasedSecurity(int port,
                                                 UserAuthService authService,
                                                 UserDao userDao,
                                                 ClientDao clientDao,
                                                 Gson gson,
                                                 TemplateProcessor templateProcessor) {
        super(port, userDao, clientDao, gson, templateProcessor);
        this.authService = authService;
    }

    @Override
    protected Handler applySecurity(ServletContextHandler servletContextHandler, String... paths) {
        servletContextHandler.addServlet(new ServletHolder(new LoginServlet(templateProcessor, authService)), "/login");
        AuthorizationFilter authorizationFilter = new AuthorizationFilter();
        Arrays.stream(paths).forEachOrdered(path -> servletContextHandler.addFilter(new FilterHolder(authorizationFilter), path, null));

        return servletContextHandler;
    }
}
