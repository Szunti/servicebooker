package hu.progmasters.servicebooker.repository;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Simply wraps exceptions in RuntimeException
 */
public class CyclicBarrierWrapper extends CyclicBarrier {
    public CyclicBarrierWrapper(int parties, Runnable barrierAction) {
        super(parties, barrierAction);
    }

    public CyclicBarrierWrapper(int parties) {
        super(parties);
    }

    @Override
    public int await() {
        try {
            return super.await();
        } catch(InterruptedException | BrokenBarrierException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public int await(long timeout, TimeUnit unit) {
        try {
            return super.await(timeout, unit);
        } catch(InterruptedException | BrokenBarrierException | TimeoutException exception) {
            throw new RuntimeException(exception);
        }
    }
}
