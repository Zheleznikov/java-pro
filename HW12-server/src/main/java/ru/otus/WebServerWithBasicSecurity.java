package ru.otus;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.LoginService;
import org.hibernate.cfg.Configuration;
import ru.otus.core.hibernate.dbmigrations.MigrationsExecutorFlyway;
import ru.otus.core.hibernate.repository.DataTemplateHibernate;
import ru.otus.core.hibernate.repository.HibernateUtils;
import ru.otus.core.hibernate.sessionmanager.TransactionManagerHibernate;
import ru.otus.dao.ClientDaoImpl;
import ru.otus.dao.InMemoryUserDao;
import ru.otus.dao.UserDao;
import ru.otus.core.web.helpers.FileSystemHelper;
import ru.otus.core.web.server.UsersWebServer;
import ru.otus.core.web.server.UsersWebServerWithBasicSecurity;
import ru.otus.core.web.services.TemplateProcessor;
import ru.otus.core.web.services.TemplateProcessorImpl;
import ru.otus.model.Address;
import ru.otus.model.Client;
import ru.otus.model.Phone;
import ru.otus.model.User;

public class WebServerWithBasicSecurity {
    private static final int WEB_SERVER_PORT = 8080;
    private static final String TEMPLATES_DIR = "/templates/";
    private static final String HASH_LOGIN_SERVICE_CONFIG_NAME = "realm.properties";
    private static final String REALM_NAME = "AnyRealm";

    public static void main(String[] args) throws Exception {
        var configuration = initDbConnection();

        var sessionFactory = HibernateUtils.buildSessionFactory(configuration, Client.class, Address.class, Phone.class);
        var transactionManager = new TransactionManagerHibernate(sessionFactory);

        var clientTemplate = new DataTemplateHibernate<>(Client.class);

        var clientDao = new ClientDaoImpl(transactionManager, clientTemplate);

        UserDao userDao = new InMemoryUserDao();
        Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
        TemplateProcessor templateProcessor = new TemplateProcessorImpl(TEMPLATES_DIR);

        String hashLoginServiceConfigPath = FileSystemHelper.localFileNameOrResourceNameToFullPath(HASH_LOGIN_SERVICE_CONFIG_NAME);
        LoginService loginService = new HashLoginService(REALM_NAME, hashLoginServiceConfigPath);
        //LoginService loginService = new InMemoryLoginServiceImpl(userDao);

        UsersWebServer usersWebServer = new UsersWebServerWithBasicSecurity(WEB_SERVER_PORT,
                loginService, userDao, clientDao, gson, templateProcessor);

        usersWebServer.start();
        usersWebServer.join();
    }

    private static Configuration initDbConnection() {
        var configuration = new Configuration().configure();

        var dbUrl = configuration.getProperty("hibernate.connection.url");
        var dbUserName = configuration.getProperty("hibernate.connection.username");
        var dbPassword = configuration.getProperty("hibernate.connection.password");

        new MigrationsExecutorFlyway(dbUrl, dbUserName, dbPassword).executeMigrations();

        return configuration;
    }

}
