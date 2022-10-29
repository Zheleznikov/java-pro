package test.ru.otus.hw;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.otus.HelloOtus;

import java.util.logging.Logger;

public class HelloOtusTest {

    static Logger log = Logger.getLogger(HelloOtus.class.getName());

    @Test
    public void testHelloOtusClass() {
        log.info("start base test");
        HelloOtus helloOtus = new HelloOtus();
        Assertions.assertEquals(helloOtus.convertFromLowerCaseToLowerUnderscore("helloOtus"), "hello_otus", "should be with underscore");
    }
}
