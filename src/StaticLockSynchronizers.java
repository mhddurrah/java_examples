import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class StaticLockSynchronizers {

    public static void main(String[] args) {
        //cyclicBarrierEx();
        countDownLatchEx();
    }

    private static void countDownLatchEx() {
        StaticLock staticLock1 = new StaticLock(1, 10);
        StaticLock staticLock2 = new StaticLock(8, 15);
        CountDownLatch cdl = new CountDownLatch(2);
        new Thread(() -> {
            staticLock1.template();
            cdl.countDown();
        }, "Job1").start();
        new Thread(() -> {
            staticLock2.template();
            cdl.countDown();
        }, "Job2").start();

        try {
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(StaticLock.sharedMap.toString());
        assert StaticLock.sharedMap.size() == 15;
    }

    private static void cyclicBarrierEx() {
        StaticLock staticLock1 = new StaticLock(1, 10);
        StaticLock staticLock2 = new StaticLock(8, 15);
        CyclicBarrier cdl = new CyclicBarrier(2, () -> {
            System.out.println(StaticLock.sharedMap.toString());
            assert StaticLock.sharedMap.size() == 15;
        });
        new Thread(() -> {
            staticLock1.template();
            try {
                cdl.await();
            } catch (InterruptedException e) {

            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }, "Job1").start();
        new Thread(() -> {
            staticLock2.template();
            try {
                cdl.await();
            } catch (InterruptedException e) {

            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }, "Job2").start();
    }
}


class StaticLock {

    private final int end;
    private final int start;

    StaticLock(int start, int end) {
        this.start = start;
        this.end = end;
    }

    private static final Object lock = new Object();
    static Map<Integer, String> sharedMap = new ConcurrentHashMap<>();

    void template() {
        synchronized (lock) {
            lockedMethod();
        }
        nonLockedMethod();
    }

    private void lockedMethod() {

        for (int i = start; i <= end; i++) {
            System.out.println(Thread.currentThread().getName() + ":" + i);
            if (!sharedMap.containsKey(i)) {
                sharedMap.put(i, Thread.currentThread().getName());
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }
    }

    private void nonLockedMethod() {
        System.out.println(Thread.currentThread().getName());
    }
}
