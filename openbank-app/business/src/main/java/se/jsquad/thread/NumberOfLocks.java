package se.jsquad.thread;

public class NumberOfLocks {

    private NumberOfLocks() {}

    private static int countNumberOfLocks = 0;

    public static int getCountNumberOfLocks() {
        return countNumberOfLocks;
    }

    public static void increaseNumberOfLocks() {
        ++countNumberOfLocks;
    }

    public static void decreaseNumberOfLocks() {
        --countNumberOfLocks;
    }
}
