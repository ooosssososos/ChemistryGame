import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glEnd;

/**
 * Created by andy6_000 on 2015-06-07.
 */
public class Circle {
    float area = 0.1f;
    float x;
    float y;

    public Circle (){

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
        glColor3f(0.0f, 0.0f, 0.0f);
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
    float getRadius(float area){
        return (float) Math.sqrt((area/Math.PI));
    }
    float getX(){return x;}
    float getY(){return y;}
    float getArea(){return area;}
    void setX(float x){this.x = x;}
    void setY(float y){this.y = y;}
    void setArea(float Area){this.area = area;}

}
