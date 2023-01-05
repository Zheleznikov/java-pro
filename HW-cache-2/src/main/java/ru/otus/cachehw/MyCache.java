package ru.otus.cachehw;


import java.util.*;

public class MyCache<K, V> implements HwCache<K, V> {
//Надо реализовать эти методы

    HashMap<K,V> cache = new HashMap<>();
    List<HwListener<K,V>> listeners = new ArrayList<>();

    private int size = 10;

    public MyCache() {
    }

    public MyCache(int size) {
        this.size = size;
    }

    @Override
    public void put(K key, V value) {
        cache.put(key, value);
        listeners.forEach(listener -> listener.notify(key, value, "put entity in cache"));
    }

    @Override
    public void remove(K key) {
        cache.remove(key);
        listeners.forEach(listener -> listener.notify(key, "remove entity in cache"));
    }

    @Override
    public V get(K key) {
        listeners.forEach(listener -> listener.notify(key, "get entity in cache"));
        return cache.get(key);
    }

    @Override
    public List<V> getAll() {
        listeners.forEach(listener -> listener.notify(null, "get all entity in cache"));
        return cache.values().stream().toList();
    }

    @Override
    public boolean isEmptyOrOverfull() {
        listeners.forEach(listener -> listener.notify(null, "check if cache is empty"));
        return cache.isEmpty() || cache.size() > size;
    }

    @Override
    public void addListener(HwListener<K, V> listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(HwListener<K, V> listener) {
        listeners.remove(listener);
    }
}
