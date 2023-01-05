package ru.otus.cachehw;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ListenerImpl<K, V> implements HwListener<K, V> {

    @Override
    public void notify(K key, V value, String action) {
        log.info("key:{}, value:{}, action: {}", key, value, action);
    }

    @Override
    public void notify( K key, String action) {
        log.info("key:{},action: {}", key,  action);
    }
}
