package main;

import geometry.IMeshFactory;
import geometry.Mesh;
import geometry.MeshFactory;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;
import utils.MatrixUtils;
import utils.ShaderUtils;

import java.awt.Frame;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Application implements Runnable {

    private final Frame frame;
    private final Canvas canvas;

    private boolean fullscreen;
    private int displayWidth;
    private int displayHeight;

    private boolean isRunning;
    private Timer timer;

    private IMeshFactory meshFactory;
    private Mesh stalkerModel;
    private Mesh lingerieModel;
    private float angle;

    public Application(Frame f, Canvas c, int w, int h, boolean isFullscreen) {
        frame = f;
        canvas = c;
        fullscreen = isFullscreen;
        displayWidth = w;
        displayHeight = h;

        timer = new Timer(60);
        meshFactory = new MeshFactory();
    }

    private void initializeGLContext() throws LWJGLException {

        if (canvas == null) throw new IllegalStateException("Unable to find canvas!");

        Display.setParent(canvas);

        Display.setDisplayMode(new DisplayMode(displayWidth, displayHeight));
        Display.setFullscreen(fullscreen);
        PixelFormat pixelFormat = new PixelFormat();
        pixelFormat.withDepthBits(32);

        Display.create(pixelFormat);

        GL11.glClearColor(0f, 0.8f, 0.85f, 1f);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        ShaderUtils.initializeShaderProgram();
        MatrixUtils.setPerspective(60f, (float) displayWidth / (float) displayHeight, 0.1f, 256f);
    }

    private void initializeScene() {
        lingerieModel = meshFactory.create("SentinelLingerie.obj");
        stalkerModel = meshFactory.create("Stalker.obj");
        //rawModel = meshFactory.create("Cobble.obj");
        //rawModel = meshFactory.create("Turret.obj");
    }

    public void run() {
        try {

            try {
                initializeGLContext();
            } catch (LWJGLException ex) {
                ex.printStackTrace();
            }
            isRunning = true;
            initializeScene();
            while (isRunning) {
                if (Display.isCloseRequested()) shutdown();
                Display.update();
                Display.sync(60);

                GL11.glClear(0x4100);

                MatrixUtils.pushMatrix();
                MatrixUtils.translate(-2f, -2f, -8f);
                angle = Math.min(angle + 5f * timer.getDeltaTime(), 360f);
                MatrixUtils.rotate(angle, 0f, 1f, 0f);
                lingerieModel.getRenderer().render();
                MatrixUtils.popMatrix();

                MatrixUtils.pushMatrix();
                MatrixUtils.translate(1f, -0.9f, -3.5f);
                MatrixUtils.rotate(angle, 0f, 1f, 0f);
                stalkerModel.getRenderer().render();
                MatrixUtils.popMatrix();

                timer.update();
                updateDisplayDimension();
            }
        } catch (RuntimeException runtimeEx) {
            runtimeEx.printStackTrace();
        } finally {
            Display.destroy();
            shutdown();
        }


    }

    private void updateDisplayDimension() {
        if (canvas == null || Display.getWidth() == canvas.getWidth() || Display.getHeight() == canvas.getHeight())
            return;

        try {
            displayWidth = canvas.getWidth();
            displayHeight = canvas.getHeight();

            Display.setDisplayMode(new DisplayMode(displayWidth, displayHeight));

            MatrixUtils.setPerspective(60f, (float) displayWidth / (float) displayHeight, 0.1f, 256f);
        } catch (LWJGLException ex) {
            ex.printStackTrace();
        }

    }

    public void shutdown() {
        isRunning = false;
    }


    public static void runApplication() {

        final Frame frame = new Frame("GC");
        final Canvas canvas = new Canvas();
        final int displayWidthStandard = 960;
        final int displayHeightStandard = 540;

        canvas.setPreferredSize(new Dimension(displayWidthStandard, displayHeightStandard));
        frame.setLayout(new BorderLayout());
        frame.add(canvas, "Center");
        frame.pack();
        frame.setLocationRelativeTo(null);

        final Application app = new Application(frame, canvas, displayWidthStandard, displayHeightStandard, false);
        final Thread theThread = new Thread(app, "TheThread");

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                app.shutdown();
                try {
                    theThread.join();
                    System.exit(0);
                } catch (InterruptedException interruptedEx) {
                    interruptedEx.printStackTrace();
                }
            }
        });
        frame.setVisible(true);
        theThread.start();

    }
}
