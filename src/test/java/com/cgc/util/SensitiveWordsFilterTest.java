package com.cgc.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SensitiveWordsFilterTest {
    @Autowired
    private SensitiveWordsFilter sensitiveWordsFilter;

    @Test
    void filter() {
        String text="。。。。。吸!!毒，吸!!烟，嫖#@娼";
        System.out.println(sensitiveWordsFilter.filter(text));
    }
}