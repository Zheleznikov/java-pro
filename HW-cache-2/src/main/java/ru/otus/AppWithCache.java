package ru.otus;

import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.cachehw.HwListener;
import ru.otus.cachehw.ListenerImpl;
import ru.otus.cachehw.MyCache;
import ru.otus.crm.dbmigrations.MigrationsExecutorFlyway;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;
import ru.otus.crm.service.DbServiceClientCacheImpl;
import ru.otus.hibernate.repository.DataTemplateHibernate;
import ru.otus.hibernate.repository.HibernateUtils;
import ru.otus.hibernate.sessionmanager.TransactionManagerHibernate;

import java.util.Collections;

public class AppWithCache {

    private static final Logger log = LoggerFactory.getLogger(AppWithCache.class);

    public static final String HIBERNATE_CFG_FILE = "hibernate.cfg.xml";

    public static void main(String[] args) {
        var configuration = new Configuration().configure(HIBERNATE_CFG_FILE);

        var dbUrl = configuration.getProperty("hibernate.connection.url");
        var dbUserName = configuration.getProperty("hibernate.connection.username");
        var dbPassword = configuration.getProperty("hibernate.connection.password");

        new MigrationsExecutorFlyway(dbUrl, dbUserName, dbPassword).executeMigrations();

        var sessionFactory = HibernateUtils.buildSessionFactory(configuration, Client.class, Address.class, Phone.class);

        var transactionManager = new TransactionManagerHibernate(sessionFactory);
///
        var clientTemplate = new DataTemplateHibernate<>(Client.class);


        var cache = new MyCache<String, Client>();
//        var cache = new MyCache<String, Client>(1); - вариант чтобы при получении всех пользователей запрос пошел в БД
        HwListener<String, Client> listener = new ListenerImpl<>();

        var dbServiceClient = new DbServiceClientCacheImpl(transactionManager, clientTemplate, cache, listener);

        dbServiceClient
                .saveClient(new Client()
                        .setName("dbServiceFirst")
                        .setAddress(new Address(null, "love street"))
                        .setPhone(Collections.singletonList(new Phone(null, "8800"))));



        var clientSecond = dbServiceClient
                .saveClient(new Client()
                        .setName("dbServiceSecond")
                        .setAddress(new Address(null, "love street"))
                        .setPhone(Collections.singletonList(new Phone(null, "8800555"))));

        var clientSecondSelected = dbServiceClient.getClient(clientSecond.getId())
                .orElseThrow(() -> new RuntimeException("Client not found, id:" + clientSecond.getId()));
        log.info("clientSecondSelected:{}", clientSecondSelected);

        dbServiceClient.saveClient(clientSecond.clone().setName("dbServiceSecondUpdated"));

        var clientUpdated = dbServiceClient.getClient(clientSecondSelected.getId())
                .orElseThrow(() -> new RuntimeException("Client not found, id:" + clientSecondSelected.getId()));
        log.info("clientUpdated:{}", clientUpdated);

        log.info("All clients");
        dbServiceClient.findAll().forEach(client -> log.info("client:{}", client));
    }

}