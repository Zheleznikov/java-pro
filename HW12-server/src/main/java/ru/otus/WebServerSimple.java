package ru.otus;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.hibernate.cfg.Configuration;
import ru.otus.core.hibernate.repository.DataTemplateHibernate;
import ru.otus.core.hibernate.repository.HibernateUtils;
import ru.otus.dao.*;
import ru.otus.model.Address;
import ru.otus.model.Client;
import ru.otus.model.Phone;
import ru.otus.core.web.server.UsersWebServer;
import ru.otus.core.web.server.UsersWebServerSimple;
import ru.otus.core.web.services.*;
import ru.otus.core.hibernate.sessionmanager.TransactionManagerHibernate;
import ru.otus.core.hibernate.dbmigrations.MigrationsExecutorFlyway;

public class WebServerSimple {
    private static final int WEB_SERVER_PORT = 8080;
    private static final String TEMPLATES_DIR = "/templates/";

    public static void main(String[] args) throws Exception {
        var configuration = initDbConnection();

        var sessionFactory = HibernateUtils.buildSessionFactory(configuration, Client.class, Address.class, Phone.class);
        var transactionManager = new TransactionManagerHibernate(sessionFactory);

        var clientTemplate = new DataTemplateHibernate<>(Client.class);

        var clientDao = new ClientDaoImpl(transactionManager, clientTemplate);
        UserDao userDao = new InMemoryUserDao();
        Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();

        TemplateProcessor templateProcessor = new TemplateProcessorImpl(TEMPLATES_DIR);

        UsersWebServer usersWebServer = new UsersWebServerSimple(WEB_SERVER_PORT, userDao, clientDao,
                gson, templateProcessor);

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
