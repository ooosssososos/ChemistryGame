import org.lwjgl.Sys;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import java.nio.ByteBuffer;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

public class HelloWorld {

    // We need to strongly reference callback instances.
    private GLFWErrorCallback errorCallback;
    private GLFWKeyCallback   keyCallback;

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
        if ( glfwInit() != GL11.GL_TRUE )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure our window
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // the window will be resizable

        int WIDTH = 800;
        int HEIGHT = 800;

        // Create the window
        window = glfwCreateWindow(WIDTH, HEIGHT, "Hello World!", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                    glfwSetWindowShouldClose(window, GL_TRUE); // We will detect this in our rendering loop
                if(key == GLFW_KEY_UP) {
                    if(action == GLFW_PRESS)
                        keys[0] = true;
                    if(action == GLFW_RELEASE)
                        keys[0] = false;
                }
                if(key == GLFW_KEY_DOWN) {
                    if(action == GLFW_PRESS)
                        keys[1] = true;
                    if(action == GLFW_RELEASE)
                        keys[1] = false;
                }
                if(key == GLFW_KEY_LEFT) {
                    if(action == GLFW_PRESS)
                        keys[2] = true;
                    if(action == GLFW_RELEASE)
                        keys[2] = false;
                }
                if(key == GLFW_KEY_RIGHT) {
                    if(action == GLFW_PRESS)
                        keys[3] = true;
                    if(action == GLFW_RELEASE)
                        keys[3] = false;
                }
                if (key == GLFW_KEY_P){
                    if (action == GLFW_RELEASE)
                        area+= 0.01f;
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

    float x = 0;
    float y = 0;
    float MAX_V = 0;
    float vel = 0;
    float acc = 0;
    float area = 0.1f;

    public void Circles(){

    }

    float getRadius(float area){
        return (float) Math.sqrt((area/Math.PI));
    }


    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the ContextCapabilities instance and makes the OpenGL
        // bindings available for use.
        GLContext.createFromCurrent();

        // Set the clear color
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( glfwWindowShouldClose(window) == GL_FALSE ) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            if(keys[0]){
                if (vel<=MAX_V) {
                    y += vel;
                    vel += acc;
                }else{
                    vel = MAX_V;
                    y += vel;
                }
            }
            if(keys[1]){
                if(vel<=MAX_V) {
                    y -= vel;
                    vel += acc;
                }else{
                    vel = MAX_V;
                    y -= vel;
                }
            }
            if(keys[2]){
                if(vel<=MAX_V) {
                    x -= vel;
                    vel += acc;
                }else{
                    vel = MAX_V;
                    x -= vel;
                }
            }
            if(keys[3]){
                if(vel<=MAX_V) {
                    x += vel;
                    vel += acc;
                }else{
                    vel = MAX_V;
                    x += vel;
                }
            }
            DrawCircle(x,y,getRadius(area),50);
            acc = 1/(area/0.03f);
            MAX_V = 1/(area/0.0015f);
            System.out.println(x + " " + y + " " + getRadius(area));



            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }
    }
    void DrawCircle(float cx, float cy, float r, int num_segments)
    {
        float theta = 2 * (float)3.1415926 / (float) num_segments;
        float c = (float)Math.cos(theta);//precalculate the sine and cosine
        float s = (float)Math.sin(theta);
        float t;

        float x = r;//we start at angle = 0
        float y = 0;
        glBegin(GL_LINE_LOOP);
        glColor3f(0.1f, 0.2f, 0.3f);
        for(int ii = 0; ii < num_segments; ii++)
        {
            glVertex2f(x + cx, y + cy);//output vertex

            //apply the rotation matrix
            t = x;
            x = c * x - s * y;
            y = s * t + c * y;
        }
        glEnd();

    }

    public static void main(String[] args) {
        new HelloWorld().run();
    }

}