import chemaxon.struc.PeriodicSystem;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glEnd;

/**
 * Created by andy6_000 on 2015-06-07.
 */
public class Circle {
    float area;
    float cx;
    float cy;
    int PorE = 0;
    float x = getRadius();

    //Generate Random Circle with area @a
    public Circle() {
            PorE = (int) (Math.random()*2);
            if (PorE == 0){
                Protons();
            }else {
                Electrons();
            }
            generatePoints();
        //      area = (float) Math.random()*0.1f;
    }

    public Circle(float a, float b, float c){
        area = a;
        cx = b;
        cy = c;
    }
    public float getDist(Circle c){
        float distance = (float) Math.sqrt(((cx - c.cx) * (cx - c.cx)) + ((cy - c.cy) * (cy - c.cy)));
        return distance;
    }
    public void generatePoints() {
        cx = (float) Math.random() * 2 - 1;
        cy = (float) Math.random() * 2 - 1;
    }
    public void eat(Circle c){
        if(c.equals(HelloWorld.Circles.get(0))){
            HelloWorld.restart = true;
            HelloWorld.gameloop = false;
        }
        HelloWorld.Circles.remove(c);
        this.area += c.area;

    }
    void DrawCircle() {
        float theta = 2 * (float) 3.1415926 / (float) 30;
        float c = (float) Math.cos(theta);//precalculate the sine and cosine
        float s = (float) Math.sin(theta);
        float t;

         x = getRadius();//we start at angle = 0
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
    public void Protons(){
        area = 0.01f;
    }
    public void Electrons(){
        area = 0.0001f;
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
