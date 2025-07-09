package ch.janishuber.adapter.helpers;

import jakarta.enterprise.inject.spi.CDI;

public class CDIProviderHelper {
    public static <T> T getBean(Class<T> beanClass) {
        return CDI.current().select(beanClass).get();
    }
}

