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
    int electrons = 1;
    int element = 1;
    float x;
    double vx = 0;
    double vy = 0;
    double ax = 0;
    double ay = 0;

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
            area = (float) PeriodicSystem.getAtomicRadius(1)/10;
            cx = b;
            cy = c;
        }

    public void tick(){
        if( cx > 2.5 || cx < -1.5) HelloWorld.removal.add(this);
        if( cy > 2.5 || cy < -1.5) HelloWorld.removal.add(this);
        Circle p = HelloWorld.Circles.get(0);
        if(p.equals(this))return;
        if(isElectron()){
            double dist = Math.sqrt(Math.pow(this.cx-p.cx,2) + Math.pow(this.cy-p.cy,2));
             ax = HelloWorld.Electronegativity.get(element) * (1/dist) * -0.00001 * ((this.cx-p.cx)) * -(p.electrons - p.element)  + (Math.random()-0.5) * 0.00001  ;
            ay = HelloWorld.Electronegativity.get(element)* (1/dist) * - 0.00001 * ((this.cy-p.cy))* -(p.electrons - p.element)+ (Math.random()-0.5) * 0.00001 ;
        }
        calcPhys();
    }

    public void calcPhys(){
        cx += vx;
        cy += vy;
        vx += ax;
        vy += ay;
    }

    public boolean isElectron(){
        if(area < 0.0002f) return true;
        else return false;
    }

    public float getDist(Circle c){
        float distance = (float) Math.sqrt(((cx - c.cx) * (cx - c.cx)) + ((cy - c.cy) * (cy - c.cy)));
        return distance;
    }
    public void generatePoints() {
        cx = (float) Math.random() * 2 - 1;
        cy = (float) Math.random() * 2 - 1;
    }

    public boolean canEatElectron(){
        //ANDY IMPLEMENT

        return false;
    }

    public void eat(Circle c){
       // if(c.equals(HelloWorld.Circles.get(0))){
       //     HelloWorld.restart = true;
       //     HelloWorld.gameloop = false;
       // }
        if(c.isElectron() && !canEatElectron() ) return;
        HelloWorld.Circles.remove(c);
        if(c.area > 0.0001f)
        element++;
        else
        electrons++;
        this.area = (float) PeriodicSystem.getAtomicRadius(element)/10;

    }
    void DrawCircle(boolean first) {


        float theta = 2 * (float) 3.1415926 / (float) 30;
        float c = (float) Math.cos(theta);//precalculate the sine and cosine
        float s = (float) Math.sin(theta);
        float t;


        glDisable( GL_DEPTH_TEST );
        x = getRadius();
        float y = 0;
        glBegin(GL_TRIANGLE_FAN);

        if(x > 0.01f)
            glColor3f(1f, 1f, 0.0f);
        else
            glColor3f(0.0f, 1.0f, 1.0f);
        for (int ii = 0; ii < 30; ii++) {
            glVertex2f(x + cx, y + cy);//output vertex

            //apply the rotation matrix
            t = x;
            x = c * x - s * y;
            y = s * t + c * y;
        }

        glEnd();
        if(first){

        glColor3f(1f, 0.0f, 0.0f);
        String name = PeriodicSystem.getName(element);
        String charge = "0";
        if(electrons > element ) charge = "-" + (electrons-element);
        else charge = "+" + (element-electrons);
        HelloWorld.drawString(name,HelloWorld.fontTexture, 8, cx-(0.05f * (name.length()/2)), cy, 0.05f,0.1f );
        HelloWorld.drawString(charge,HelloWorld.fontTexture, 8, cx-(0.03f * (charge.length()/2)), cy-0.02f, 0.03f,0.04f );
        glEnable( GL_DEPTH_TEST );
        }

    }
    public void Protons(){
       area = (float) PeriodicSystem.getAtomicRadius(1)/10;
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
