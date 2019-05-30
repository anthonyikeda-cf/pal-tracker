package test.pivotal.pal.tracker;

import io.pivotal.pal.tracker.WelcomeController;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class WelcomeControllerTest {


    @Test
    public void itSaysHello() throws Exception {
        WelcomeController controller = new WelcomeController("A welcome message");

        assertThat(controller.sayHello().getBody()).isEqualTo("A welcome message");
    }
}
