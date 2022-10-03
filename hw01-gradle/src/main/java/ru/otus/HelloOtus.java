package ru.otus;

import com.google.common.base.CaseFormat;

import java.util.logging.Logger;


public class HelloOtus {

    static Logger log = Logger.getLogger(HelloOtus.class.getName());
    public static void main(String[] args) {
        log.info("hello otus from jar");
    }

    public String convertFromLowerCaseToLowerUnderscore(String input) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, input);
    }

}
