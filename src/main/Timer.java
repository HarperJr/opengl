package main;


import org.lwjgl.Sys;

public class Timer {
    private float timerSpeed;
    private long prevTimeMills;
    private long currentTimeMills;
    private float deltaTime;

    public Timer(float speed) {
        prevTimeMills = System.currentTimeMillis();
        currentTimeMills = 0L;
        timerSpeed = speed;
        deltaTime = 0f;
    }

    public void update() {
        currentTimeMills = System.currentTimeMillis();
        float delta = (float) (currentTimeMills - prevTimeMills) / 1000000L;

        deltaTime = delta * timerSpeed;
        prevTimeMills = currentTimeMills;
    }


    public float getDeltaTime() {
        return deltaTime;
    }
}
