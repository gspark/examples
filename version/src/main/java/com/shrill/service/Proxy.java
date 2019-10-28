package com.shrill.util;

import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;

public class Proxy {

    public static Object getTargetObject(Object proxy) throws Exception {
        if (AopUtils.isJdkDynamicProxy(proxy)) {
            return ((Advised) proxy).getTargetSource().getTarget();
        }
        if (AopUtils.isCglibProxy(proxy)) {
            return ((Advised) proxy).getTargetSource().getTarget();
        }
        if (AopUtils.isAopProxy(proxy)) {
            return ((Advised) proxy).getTargetSource().getTarget();
        } else {
            return proxy; // expected to be cglib proxy then, which is simply a specialized class
        }
    }
}
