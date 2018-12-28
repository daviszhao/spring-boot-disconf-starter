package com.github.daviszhao.springboot.disconf.client;

class DisconfStateHolder {
    private static final ThreadLocal<String> state = new ThreadLocal<>();

    private static void resetState() {
        state.remove();
    }

    public static String getState() {
        return state.get();
    }

    public static void setState(String newState) {
        if (newState == null) {
            resetState();
            return;
        }
        state.set(newState);
    }
}

