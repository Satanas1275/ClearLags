package com.clearlags.handler;

public record GuiSettings(boolean enabled, int placement) {
    public static final GuiSettings DEFAULT = new GuiSettings(true, 0);
}
