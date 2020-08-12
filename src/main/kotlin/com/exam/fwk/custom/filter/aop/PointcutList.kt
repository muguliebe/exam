package com.exam.fwk.custom.filter.aop

import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut

@Aspect
class PointcutList {

    @Pointcut("execution(* com.exam.bank..controller..*.*(..))")
    fun allController() {
    }

    @Pointcut("execution(* com.exam.bank..service..*.*(..))")
    fun allServices() {
    }

}
