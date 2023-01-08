package ru.otus.cachehw;


import java.util.*;

public class MyCache<K, V> implements HwCache<K, V> {
//Надо реализовать эти методы

    Map<K, V> cache = new WeakHashMap<>();
    private final List<HwListener<K, V>> listeners = new ArrayList<>();


    @Override
    public void put(K key, V value) {
        cache.put(key, value);
        invokeListener(key, value, "put entity in cache");
    }

    @Override
    public void remove(K key) {
        cache.remove(key);
        invokeListener(key, null, "remove entity in cache");
    }

    @Override
    public V get(K key) {
        invokeListener(key, null, "get entity from cache");
        return cache.get(key);
    }

    @Override
    public List<V> getAll() {
        invokeListener(null, null, "get all entities from cache");
        return cache.values().stream().toList();
    }

    @Override
    public boolean isEmpty() {
        invokeListener(null, null, "check if cache is empty");
        return cache.isEmpty();
    }

    @Override
    public void addListener(HwListener<K, V> listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(HwListener<K, V> listener) {
        listeners.remove(listener);
    }

    private void invokeListener(K key, V value, String action) {

        listeners.forEach(listener -> {
            try {
                listener.notify(key, value, action);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
