package ru.otus.core.web.services;

public interface UserAuthService {
    boolean authenticate(String login, String password);
}
