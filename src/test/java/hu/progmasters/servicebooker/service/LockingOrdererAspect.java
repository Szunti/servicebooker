package hu.progmasters.servicebooker.service;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.test.context.TestComponent;

import java.util.concurrent.CyclicBarrier;

@TestComponent
@Aspect
public class LockingOrdererAspect {
    private static final CyclicBarrier barrier = new CyclicBarrier(2);

    @Around("execution(public void hu.progmasters.servicebooker.repository.BooseRepository." +
            "lockForUpdate(hu.progmasters.servicebooker.domain.Boose))")
    public Object orderLocking(ProceedingJoinPoint pjp) throws Throwable {
        if (ThreadOrder.getMyOrder() == ThreadOrder.SECOND) {
            barrier.await();
        }
        Object result = pjp.proceed();
        if (ThreadOrder.getMyOrder() == ThreadOrder.FIRST) {
            barrier.await();
        }
        return result;
    }
}

