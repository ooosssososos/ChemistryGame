/**
 * Created by andy6_000 on 2015-06-11.
 */
import org.lwjgl.glfw.GLFWCursorPosCallback;
public class MouseHandler extends GLFWCursorPosCallback {
static double x;
    static double y;
    @Override
    public void invoke(long window, double x, double y) {
        // TODO Auto-generated method stub
        // this basically just prints out the X and Y coordinates
        // of our mouse whenever it is in our window
        System.out.println("X: " + x + " Y: " + y);
        this.x = x;
        this.y = y;
    }
    public static int getX (){
        return (int) x;
    }
    public static int getY (){
        return (int) y;
    }
}
