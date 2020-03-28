package chc.fortumo.test;

import chc.fortumo.controller.ProcessController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ApplicationContentLoadingTest {
    @Autowired
    private ProcessController processController;

    @Test
    public void contexLoads() throws Exception {
        assertThat(processController).isNotNull();
    }
}
