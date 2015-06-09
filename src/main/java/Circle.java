import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glEnd;

/**
 * Created by andy6_000 on 2015-06-07.
 */
public class Circle {
    float area = 0.1f;
    float cx;
    float cy;
    float prevcx;
    float prevcy;
    float radius = getRadius();
    float distance;
    float prevradius;

    //Generate Random Circle with area @a
    public Circle() {
        generatePoints();
        distance = (float) Math.sqrt(((cx - prevcx) * (cx - prevcx)) + ((cy - prevcy) * (cy - prevcy)));
        if (distance > (prevradius + radius)) {
            System.out.println("okay");
        } else {
            System.out.println("overlap");
        }
        //      area = (float) Math.random()*0.1f;
    }

    public void generatePoints() {
        cx = (float) Math.random() * 2 - 1;
        cy = (float) Math.random() * 2 - 1;
    }

    void DrawCircle() {
        float theta = 2 * (float) 3.1415926 / (float) 30;
        float c = (float) Math.cos(theta);//precalculate the sine and cosine
        float s = (float) Math.sin(theta);
        float t;

        float x = getRadius();//we start at angle = 0
        float y = 0;

        glBegin(GL_LINE_LOOP);
        glColor3f(0.0f, 0.0f, 0.0f);
        for (int ii = 0; ii < 30; ii++) {
            glVertex2f(x + cx, y + cy);//output vertex

            //apply the rotation matrix
            t = x;
            x = c * x - s * y;
            y = s * t + c * y;
        }


        glEnd();
    }

    float getRadius() {
        return (float) Math.sqrt((area / Math.PI));
    }

    float getX() {
        return cx;
    }

    float getY() {
        return cy;
    }

    float getArea() {
        return area;
    }

    void setX(float x) {
        this.cx = x;
    }

    void setY(float y) {
        this.cy = y;
    }

    void setArea(float Area) {
        this.area = area;
    }

}
