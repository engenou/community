package com.nowcoder.community;

import com.nowcoder.community.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Eugen
 * @creat 2022-05-05 18:38
 */
@SpringBootTest
public class SensitiveTest {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitive(){
        String text = "真的✔赌✔博✔不是吸毒啊";
        String textFilted = sensitiveFilter.filter(text);
        System.out.println(textFilted);
    }
}
