package hu.progmasters.servicebooker.various;

public enum ThreadOrder {
    FIRST, SECOND;

    private static final ThreadLocal<ThreadOrder> myOrder = new ThreadLocal<>();

    public static ThreadOrder getMyOrder() {
        return myOrder.get();
    }

    public static void setMyOrder(ThreadOrder order) {
        myOrder.set(order);
    }
}
