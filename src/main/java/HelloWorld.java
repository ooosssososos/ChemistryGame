import javafx.scene.shape.*;
import org.lwjgl.Sys;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import java.awt.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Random;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

public class HelloWorld {

    // We need to strongly reference callback instances.
    private GLFWErrorCallback errorCallback;
    private GLFWKeyCallback keyCallback;

    // The window handle
    private long window;

    public void run() {
        System.out.println("Hello LWJGL " + Sys.getVersion() + "!");

        try {
            init();
            loop();

            // Release window and window callbacks
            glfwDestroyWindow(window);
            keyCallback.release();
        } finally {
            // Terminate GLFW and release the GLFWerrorfun
            glfwTerminate();
            errorCallback.release();
        }
    }

    boolean[] keys = new boolean[4]; // 0 = up, 1 =down, 2 = left, 3 = right

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        glfwSetErrorCallback(errorCallback = errorCallbackPrint(System.err));

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (glfwInit() != GL11.GL_TRUE)
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure our window
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // the window will be resizable

        int WIDTH = 800;
        int HEIGHT = 800;

        // Create the window
        window = glfwCreateWindow(WIDTH, HEIGHT, "Hello World!", NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                    glfwSetWindowShouldClose(window, GL_TRUE); // We will detect this in our rendering loop
                if (key == GLFW_KEY_UP) {
                    if (action == GLFW_PRESS)
                        keys[0] = true;
                    if (action == GLFW_RELEASE)
                        keys[0] = false;
                }
                if (key == GLFW_KEY_DOWN) {
                    if (action == GLFW_PRESS)
                        keys[1] = true;
                    if (action == GLFW_RELEASE)
                        keys[1] = false;
                }
                if (key == GLFW_KEY_LEFT) {
                    if (action == GLFW_PRESS)
                        keys[2] = true;
                    if (action == GLFW_RELEASE)
                        keys[2] = false;
                }
                if (key == GLFW_KEY_RIGHT) {
                    if (action == GLFW_PRESS)
                        keys[3] = true;
                    if (action == GLFW_RELEASE)
                        keys[3] = false;
                }
                if (key == GLFW_KEY_P) {
                    if (action == GLFW_RELEASE)
                        C.area += 0.01f;
                }
                if (key == GLFW_KEY_O) {
                    if (action == GLFW_RELEASE)
                        C.area -= 0.01f;
                }
            }
        });

        // Get the resolution of the primary monitor
        ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        // Center our window
        glfwSetWindowPos(
                window,
                (GLFWvidmode.width(vidmode) - WIDTH) / 2,
                (GLFWvidmode.height(vidmode) - HEIGHT) / 2
        );

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
    }

    Circle C = new Circle();
    float x = C.getX();
    float y = C.getY();
    float MAX_V = 0;
    float vel = 0;
    float acc = 0;
    float area = C.getArea();
    public ArrayList<Circle> Circles = new ArrayList(); //ArrayList of only Circles
    // public ArrayList<Circle> Circles;

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the ContextCapabilities instance and makes the OpenGL
        // bindings available for use.
        GLContext.createFromCurrent();

        // Set the clear color
        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (glfwWindowShouldClose(window) == GL_FALSE) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
            Circles = new ArrayList<Circle>();
            if (keys[0]) {  //moving/acceleration of the cube.
                if (vel <= MAX_V) {
                    y += vel;
                    vel += acc;
                } else {
                    vel = MAX_V;
                    y += vel;
                }
            }
            if (keys[1]) {
                if (vel <= MAX_V) {
                    y -= vel;
                    vel += acc;
                } else {
                    vel = MAX_V;
                    y -= vel;
                }
            }
            if (keys[2]) {
                if (vel <= MAX_V) {
                    x -= vel;
                    vel += acc;
                } else {
                    vel = MAX_V;
                    x -= vel;
                }
            }
            if (keys[3]) {
                if (vel <= MAX_V) {
                    x += vel;
                    vel += acc;
                } else {
                    vel = MAX_V;
                    x += vel;
                }
            }
            if (Circles.size() < 10) {

                generateCircles();
            }
            C.DrawCircle(x, y, C.getRadius(C.area), 50);

            acc = 1 / (C.area / 0.03f);
            MAX_V = 1 / (C.area / 0.0015f);
           // System.out.println(x + " " + y + " " + C.getRadius(C.area));


            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();

        }

    }

    public void generateCircles() {

        Circle C = new Circle();
        float minX = -1.0f;
        float maxX = 1.0f;
        float minY = -1.0f;
        float maxY = 1.0f;
        Random rand = new Random();
       C.x = (rand.nextFloat() * (maxX - minX) + minX);
        C.y =(rand.nextFloat() * (maxY - minY) + minY);
       // C.x = 1;
        //C.y = 1;
        C.DrawCircle(C.x, C.y, C.getRadius(C.area), 50);
        Circles.add(C);
        System.out.println(C.x + " " + C.y + " " + C.getRadius(C.area));
    }

    void drawString(String s, int textureObj, int gridsize, float x, float y, float charW, float charH) {
        glPushAttrib(GL_TEXTURE_BIT | GL_ENABLE_BIT);
        glEnable(GL_CULL_FACE);
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, textureObj);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE);
        glPushMatrix();
        glTranslatef(x, y, 0);
        glBegin(GL_QUADS);
        for (int i = 0; i < s.length(); i++) {
            int ascii = (int) s.charAt(i);
        }
    }

    public static void main(String[] args) {
        new HelloWorld().run();
    }


}