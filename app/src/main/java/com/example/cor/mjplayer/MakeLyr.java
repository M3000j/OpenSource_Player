package com.example.cor.mjplayer;

/**
 * Created by M3000j on 12/22/2017.
 */

public class MakeLyr {
    private String text = "";
    private boolean tick = false;

    public MakeLyr(String text, boolean tick) {
        this.text = text;
        this.tick = tick;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isTick() {
        return tick;
    }

    public void setTick(boolean tick) {
        this.tick = tick;
    }
}
