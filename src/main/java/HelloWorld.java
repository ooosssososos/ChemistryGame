//periodic trends, entropy, enthalpy, electronetagity, nuclear chemistry
//focus on learning outcome
import chemaxon.struc.PeriodicSystem;
import com.jogamp.newt.Display;
import com.sun.deploy.perf.PerfRollup;
import org.lwjgl.BufferUtils;
import org.lwjgl.Sys;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import Graphics.Shader;
import Math.Matrix4f;
import Graphics.Camera;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;






public class HelloWorld {
    private GLFWCursorPosCallback cursorPos;

    // We need to strongly reference callback instances.
    private GLFWErrorCallback errorCallback;
    static HashMap<Integer, Double> Electronegativity = new HashMap<Integer, Double>();
    static HashMap<Integer, Integer> neutralCharge = new HashMap<Integer, Integer>();
    private GLFWKeyCallback keyCallback;
    public Camera camera = new Camera(new Matrix4f());
    public static ArrayList<Circle> removal = new ArrayList<Circle>();

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
    boolean spacedown = false;
    private void init() {
        initEle();
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        glfwSetErrorCallback(errorCallback = errorCallbackPrint(System.err));

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (glfwInit() != GL11.GL_TRUE)
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure our window
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GL_FALSE); // the window will be resizable

        int WIDTH = 900;
        int HEIGHT = 900;


        // Create the window
        window = glfwCreateWindow(WIDTH, HEIGHT, "Periodic Trend Simulator", NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");
        glfwSetCursorPosCallback(window, cursorPos = new MouseHandler());


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
                if (key == GLFW_KEY_SPACE) {
                }
            }
        });

        // Get the resolution of the primary monitor
        ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        // Center our window
        glfwSetWindowPos(
                window,
                (GLFWvidmode.width(vidmode) - WIDTH/2),
                (GLFWvidmode.height(vidmode) - HEIGHT/2)
        );

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
    }
    float MAX_V = 0;
    float vel = 0;
    float acc = 0;
    public static ArrayList<Circle> Circles; //ArrayList of only Circles
    // public ArrayList<Circle> Circles;

    float getRadius(float area) {
        return (float) Math.sqrt((area / Math.PI));
    }

    // Circles = new ArrayList<Circle>();
    public static boolean restart = false;
    public static boolean gameloop = true;

    private void loop() {
       Circles = new ArrayList<Circle>();
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the ContextCapabilities instance and makes the OpenGL
        // bindings available for use.
        GLContext.createFromCurrent();

        glDepthFunc( GL_NEVER );
        glDisable(GL_DEPTH_TEST);
        try {
            setupTextures();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(fontTexture);
        Circles.add(new Circle(0.025f,0f,0f));

        //generate first Circle
        // Set the clear color
        glClearColor(0f, 0f, 0f, 1f);
        /*glMatrixMode(GL_PROJECTION);
        glLoadIdentity();;
        glOrtho(0,900, 900,0,-1,1);
        glMatrixMode(GL_MODELVIEW);
        */
        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        gameloop: while (glfwWindowShouldClose(window) == GL_FALSE && gameloop == true) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
            glPushMatrix();
            int attempts = 0;
            closeloop:
            while (Circles.size() < 21) {
                generateCircles();
                if (++attempts > 10) {
                    break closeloop;
                }

            }
            playerMoveTick();
            checkIntersects();

            acc = 1 / (Circles.get(0).element / 3f);
            MAX_V = 1 / (Circles.get(0).element / 0.15f);
            //System.out.println(x + " " + y + " " + getRadius(area));


            for (Circle c : Circles) {

                c.tick();
                if(Circles.get(0).equals(c))
                c.DrawCircle(true);
                else c.DrawCircle(false);
            }

            // System.out.println(x + " " + y + " " + C.getRadius(C.area));


            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
            Circles.removeAll(removal);
            removal.clear();
        }
        if(restart == true){
            gameloop = true;
            restart = false;
            loop();
        }
    }
    public void update(){
        glfwPollEvents();

        camera.update();

    }
    public void checkIntersects(){
        Circle Player = Circles.get(0);
        Circle target = null;
        for (Circle c : Circles) {
            if (c.equals(Player)){continue;}
            if (Player.getDist(c) <= (Player.getRadius() + c.getRadius())){ target = c; break;}
        }
        if(target != null){
        //    if(target.area > Player.area)target.eat(Player);
        // else
           Player.eat(target);
        }

    }
    public void playerMoveTick(){
        Circle Player = Circles.get(0);
        if (keys[0]) {  //moving/acceleration of the cube.
            if (vel <= MAX_V) {
                Player.cy += vel;
                vel += acc;
            } else {
                vel = MAX_V;
                Player.cy += vel;
            }
        }
        if (keys[1]) {
            if (vel <= MAX_V) {
                Player.cy -= vel;
                vel += acc;
            } else {
                vel = MAX_V;
                Player.cy -= vel;
            }
        }
        if (keys[2]) {
            if (vel <= MAX_V) {
                Player.cx -= vel;
                vel += acc;
            } else {
                vel = MAX_V;
                Player.cx -= vel;
            }
        }
        if (keys[3]) {
            if (vel <= MAX_V) {
                Player.cx += vel;
                vel += acc;
            } else {
                vel = MAX_V;
                Player.cx += vel;
            }
        }
    }





    public void generateCircles() {
        Circle C = new Circle();
        boolean add = true;
        for (Circle c : Circles) {
            if (C.getDist(c) <= (C.getRadius() + c.getRadius())) add = false;
        }
        if(add) Circles.add(C);
    }

    static int fontTexture = 0;

    void setupTextures() throws IOException {
        BufferedImage img = ImageIO.read(new File("src\\main\\resources\\ExportedFont.png"));
        fontTexture = loadTexture(img);
    }

    private static final int BYTES_PER_PIXEL = 4;

    public static int loadTexture(BufferedImage image) {

        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * BYTES_PER_PIXEL); //4 for RGBA, 3 for RGB

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int pixel = pixels[y * image.getWidth() + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF));     // Red component
                buffer.put((byte) ((pixel >> 8) & 0xFF));      // Green component
                buffer.put((byte) (pixel & 0xFF));               // Blue component
                buffer.put((byte) ((pixel >> 24) & 0xFF));    // Alpha component. Only for RGBA
            }
        }

        buffer.flip(); //FOR THE LOVE OF GOD DO NOT FORGET THIS

        // You now have a ByteBuffer filled with the color data of each pixel.
        // Now just create a texture ID and bind it. Then you can load it using
        // whatever OpenGL method you want, for example:

        int textureID = glGenTextures(); //Generate texture ID
        glBindTexture(GL_TEXTURE_2D, textureID); //Bind texture ID

        //Send texel data to OpenGL
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        glBindTexture(GL_TEXTURE_2D, 0);
        //Return the texture ID so we can bind it later again
        return textureID;
    }

    static void drawString(String s, int textureObj, int gridsize, float x, float y, float charW, float charH) {
        s = s.toUpperCase();
        glPushAttrib(GL_TEXTURE_BIT | GL_ENABLE_BIT);
        glEnable(GL_CULL_FACE);
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, textureObj);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glPushMatrix();
        glTranslatef(x, y, 1);
        glBegin(GL_QUADS);

        for (int i = 0; i < s.length(); i++) {
            int ascii = (int) s.charAt(i);
            ascii -= 32;
            //.println(ascii);
            final float cellSize = 1f / gridsize;
            float cellX = ((int) (ascii % gridsize)) * cellSize;
            float cellY = ((int) (ascii / gridsize)) * cellSize;
            glTexCoord2f(cellX, cellY + cellSize);
            glVertex2f(i * charW, 0);
            glTexCoord2f(cellX + cellSize, cellY + cellSize);
            glVertex2f(i * charW + charW, 0);
            glTexCoord2f(cellX + cellSize, cellY);
            glVertex2f(i * charW + charW, 0 + charH);
            glTexCoord2f(cellX, cellY);
            glVertex2f(i * charW, 0 + charH);
        }
        glEnd();
        glPopMatrix();
        glPopAttrib();
    }

    public static void main(String[] args) {

        new HelloWorld().run();

    }
    public void initEle(){
        Electronegativity.put(1, 2.2);
        Electronegativity.put(2, 0d);
        Electronegativity.put(3, 0.98);
        Electronegativity.put(4, 1.57);
        Electronegativity.put(5, 2.04);
        Electronegativity.put(6, 2.55);
        Electronegativity.put(7, 3.04);
        Electronegativity.put(8, 3.44);
        Electronegativity.put(9, 3.98);
        Electronegativity.put(10, 0d);
        Electronegativity.put(11, 0.93);
        Electronegativity.put(12, 1.31);
        Electronegativity.put(13, 1.61);
        Electronegativity.put(14, 1.90);
        Electronegativity.put(15, 2.19);
        Electronegativity.put(16, 2.58);
        Electronegativity.put(17, 3.16);
        Electronegativity.put(18, 0d);
        Electronegativity.put(19, 0.82);
        Electronegativity.put(20, 1.0);
        Electronegativity.put(21, 1.36);
        Electronegativity.put(22, 1.54);
        Electronegativity.put(23, 1.63);
        Electronegativity.put(24, 1.66);
        Electronegativity.put(25, 1.55);
        Electronegativity.put(26, 1.83);
        Electronegativity.put(27, 1.88);
        Electronegativity.put(28, 1.91);
        Electronegativity.put(29, 1.90);
        Electronegativity.put(30, 1.65);
        Electronegativity.put(31, 1.81);
        Electronegativity.put(32, 2.01);
        Electronegativity.put(33, 2.18);
        Electronegativity.put(34, 2.55);
        Electronegativity.put(35, 2.96);
        Electronegativity.put(36, 3.00);
        Electronegativity.put(37, 0.82);
        Electronegativity.put(38, 0.95);
        Electronegativity.put(39, 1.22);
        Electronegativity.put(40, 1.33);
        Electronegativity.put(41, 1.6);
        Electronegativity.put(42, 2.16);
        Electronegativity.put(43, 1.9);
        Electronegativity.put(44, 2.25);
        Electronegativity.put(45, 2.28);
        Electronegativity.put(46, 2.20);
        Electronegativity.put(47, 1.93);
        Electronegativity.put(48, 1.69);
        Electronegativity.put(49, 1.78);
        Electronegativity.put(50, 1.96);
        Electronegativity.put(51, 2.05);
        Electronegativity.put(52, 2.1);
        Electronegativity.put(53, 2.66);
        Electronegativity.put(54, 2.60);
        Electronegativity.put(55, 0.79);
        Electronegativity.put(56, 0.89);
        Electronegativity.put(57, 1.1);
        Electronegativity.put(58, 1.12);
        Electronegativity.put(59, 1.13);
        Electronegativity.put(60, 1.14);
        Electronegativity.put(61, 1.13);
        Electronegativity.put(62, 1.17);
        Electronegativity.put(63, 1.2);
        Electronegativity.put(64, 1.2);
        Electronegativity.put(65, 1.1);
        Electronegativity.put(66, 1.22);
        Electronegativity.put(67, 1.23);
        Electronegativity.put(68, 1.24);
        Electronegativity.put(69, 1.25);
        Electronegativity.put(70, 1.1);
        Electronegativity.put(71, 1.27);
        Electronegativity.put(72, 1.3);
        Electronegativity.put(73, 1.5);
        Electronegativity.put(74, 2.36);
        Electronegativity.put(75, 1.9);
        Electronegativity.put(76, 2.2);
        Electronegativity.put(77, 2.20);
        Electronegativity.put(78, 2.28);
        Electronegativity.put(79, 2.54);
        Electronegativity.put(80, 2.00);
        Electronegativity.put(81, 1.62);
        Electronegativity.put(82, 2.33);
        Electronegativity.put(83, 2.02);
        Electronegativity.put(84, 2.0);
        Electronegativity.put(85, 2.2);
        Electronegativity.put(86, 2.2);
        Electronegativity.put(87, 0.7);
        Electronegativity.put(88, 0.9);
        Electronegativity.put(89, 1.1);
        Electronegativity.put(90, 1.3);
        Electronegativity.put(91, 1.5);
        Electronegativity.put(92, 1.38);
        Electronegativity.put(93, 1.36);
        Electronegativity.put(94, 1.28);
        Electronegativity.put(95, 1.13);
        Electronegativity.put(96, 1.28);
        Electronegativity.put(97, 1.3);
        Electronegativity.put(98, 1.3);
        Electronegativity.put(99, 1.3);
        Electronegativity.put(100, 1.3);
        Electronegativity.put(101, 1.3);
        Electronegativity.put(102, 1.3);
        Electronegativity.put(103, 1.291);




























        neutralCharge.put(1,0);
        neutralCharge.put(2,0);
        neutralCharge.put(3,1);
        neutralCharge.put(4,2);
        neutralCharge.put(5,3);
        neutralCharge.put(6,-4);
        neutralCharge.put(7,-3);
        neutralCharge.put(8,-2);
        neutralCharge.put(9,1);
        neutralCharge.put(10,0);
        neutralCharge.put(11,1);
        neutralCharge.put(12,2);
        neutralCharge.put(13,3);
        neutralCharge.put(14,-4);
        neutralCharge.put(15,-3);
        neutralCharge.put(16,-2);
        neutralCharge.put(17, -1);
        neutralCharge.put(18,0);
        neutralCharge.put(19,1);
        neutralCharge.put(20,2);
        neutralCharge.put(21,3);
        neutralCharge.put(22,2);
        neutralCharge.put(23,2);
        neutralCharge.put(24,2);
        neutralCharge.put(25,2);
        neutralCharge.put(26,2);
        neutralCharge.put(27,2);
        neutralCharge.put(28,2);
        neutralCharge.put(29,1);
        neutralCharge.put(30,2);
        neutralCharge.put(31,3);
        neutralCharge.put(32,2);
        neutralCharge.put(33,-3);
        neutralCharge.put(34,-2);
        neutralCharge.put(35,-1);
        neutralCharge.put(36,0);
        neutralCharge.put(37,1);
        neutralCharge.put(38,2);
        neutralCharge.put(39,3);
        neutralCharge.put(40,4);
        neutralCharge.put(41,3);
        neutralCharge.put(42,6);
        neutralCharge.put(43,4);
        neutralCharge.put(44,3);
        neutralCharge.put(45,3);
        neutralCharge.put(46,2);
        neutralCharge.put(47,1);
        neutralCharge.put(48,2);
        neutralCharge.put(49,1);
        neutralCharge.put(50,2);
        neutralCharge.put(51,-3);
        neutralCharge.put(52,-2);
        neutralCharge.put(53,-1);
        neutralCharge.put(54,0);
        neutralCharge.put(55,1);
        neutralCharge.put(56,2);
        neutralCharge.put(57,3);
        neutralCharge.put(58,3);
        neutralCharge.put(59,3);
        neutralCharge.put(60,3);
        neutralCharge.put(61,3);
        neutralCharge.put(62,2);
        neutralCharge.put(63,2);
        neutralCharge.put(64,3);
        neutralCharge.put(65,3);
        neutralCharge.put(66,3);
        neutralCharge.put(67,3);
        neutralCharge.put(68,3);
        neutralCharge.put(69,3);
        neutralCharge.put(70,2);
        neutralCharge.put(71,3);
        neutralCharge.put(72,4);
        neutralCharge.put(73,5);
        neutralCharge.put(74,6);
        neutralCharge.put(75,4);
        neutralCharge.put(76,3);
        neutralCharge.put(77,3);
        neutralCharge.put(78,2);
        neutralCharge.put(79,1);
        neutralCharge.put(80,1);
        neutralCharge.put(81,1);
        neutralCharge.put(82,2);
        neutralCharge.put(83,3);
        neutralCharge.put(84,2);
        neutralCharge.put(85,-1);
        neutralCharge.put(86,1);
        neutralCharge.put(87,2);
        neutralCharge.put(88,3);
        neutralCharge.put(89,4);
        neutralCharge.put(90,4);
        neutralCharge.put(91,3);
        neutralCharge.put(92,3);
        neutralCharge.put(93,3);
        neutralCharge.put(94,3);
        neutralCharge.put(95,3);
        neutralCharge.put(96,3);
        neutralCharge.put(97,3);
        neutralCharge.put(98,3);


    }


}