package com.gi.rhapp.utilities;


import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BeanUtils implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    public static Object getBean (String beanId) {
        return applicationContext.getBean(beanId);
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        BeanUtils.applicationContext = applicationContext;
    }
}
