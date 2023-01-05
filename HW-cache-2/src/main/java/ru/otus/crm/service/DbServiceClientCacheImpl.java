package ru.otus.crm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.cachehw.HwCache;
import ru.otus.cachehw.HwListener;
import ru.otus.crm.model.Client;
import ru.otus.hibernate.repository.DataTemplate;
import ru.otus.hibernate.sessionmanager.TransactionManager;

import java.util.List;
import java.util.Optional;

public class DbServiceClientCacheImpl implements DBServiceClient {
    private static final Logger log = LoggerFactory.getLogger(DbServiceClientCacheImpl.class);

    private final DataTemplate<Client> clientDataTemplate;
    private final TransactionManager transactionManager;
    private final HwCache<String,Client> cache;
    HwListener<String, Client> listener;

    public DbServiceClientCacheImpl(TransactionManager transactionManager, DataTemplate<Client> clientDataTemplate,
                                    HwCache<String, Client> cache, HwListener<String, Client> listener) {

        this.transactionManager = transactionManager;
        this.clientDataTemplate = clientDataTemplate;

        this.cache = cache;
        this.listener = listener;
        this.cache.addListener(this.listener);
    }

    @Override
    public Client saveClient(Client client) {
        return transactionManager.doInTransaction(session -> {
            var clientCloned = client.clone();

            if (client.getId() == null) {
                clientDataTemplate.insert(session, clientCloned);
                log.info("created client: {}", clientCloned);

                cache.put(String.valueOf(clientCloned.getId()), clientCloned);
                return clientCloned;
            }
            clientDataTemplate.update(session, clientCloned);
            log.info("updated client: {}", clientCloned);

            cache.put(String.valueOf(clientCloned.getId()), clientCloned);
            return clientCloned;
        });
    }

    @Override
    public Optional<Client> getClient(long id) {
        return transactionManager.doInReadOnlyTransaction(session -> {

            if (cache.get(String.valueOf(id)) != null) {
                return Optional.of(cache.get(String.valueOf(id)));
            }

            var clientOptional = clientDataTemplate.findById(session, id);
            log.info("client: {}", clientOptional);
            return clientOptional;
        });
    }

    @Override
    public List<Client> findAll() {
        return transactionManager.doInReadOnlyTransaction(session -> {

            if (!cache.isEmptyOrOverfull()) {
                return cache.getAll();
            }

            var clientList = clientDataTemplate.findAll(session);
            log.info("clientList:{}", clientList);
            return clientList;
       });
    }
}