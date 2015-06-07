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
    final float MAX_V = 0;
    float vel = 0;

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

            if(keys[0]) y+=0.02f;
            if(keys[1]) y-=0.02f;
            if(keys[2]) x-=0.02f;
            if(keys[3]) x+=0.02f;
            DrawCircle(x,y,0.02f,50);


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
        for(int i = 0; i < num_segments; i++)
        {
            glVertex2f(x + cx, y + cy);//output vertex

            //apply the rotation matrix
            t = x;
            x = c * x - s * y;
            y = s * t + c * y;
        }
        glEnd();
    }

    void drawString(String s, int textureObj, int gridsize, float x, float y, float charW, float charH){
        glPushAttrib(GL_TEXTURE_BIT | GL_ENABLE_BIT);
        glEnable(GL_CULL_FACE);
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, textureObj);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE);
        glPushMatrix();
        glTranslatef(x,y,0);
        glBegin(GL_QUADS);
        for(int i = 0; i < s.length(); i++){
            int ascii = (int) s.charAt(i);
        }
    }
    public static void main(String[] args) {
        new HelloWorld().run();
    }

}