package ru.nsu.ekazakova.utils;

import lombok.experimental.UtilityClass;

import java.nio.channels.SelectionKey;
import java.util.Objects;

@UtilityClass
public class SelectionKeyUtils {
    public static void turnOnWriteOption( SelectionKey key) {
        Objects.requireNonNull(key, "Key cant be null");
        key.interestOpsOr(SelectionKey.OP_WRITE);
    }

    public static void turnOffWriteOption( SelectionKey key) {
        Objects.requireNonNull(key, "Key cant be null");
        key.interestOpsAnd(~SelectionKey.OP_WRITE);
    }

    public static void turnOnReadOption(SelectionKey key) {
        Objects.requireNonNull(key, "Key cant be null");
        key.interestOpsOr(SelectionKey.OP_READ);
    }

    public static void turnOffReadOption( SelectionKey key) {
        Objects.requireNonNull(key, "Key cant be null");
        key.interestOpsAnd(~SelectionKey.OP_READ);
    }
}