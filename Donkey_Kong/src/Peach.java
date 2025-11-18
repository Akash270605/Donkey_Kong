/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Leveno
 */
public class Peach {
    private double x;
    private double y;
    
    public Peach(double x, double y){
        this.x = x;
        this.y = y;
    }
    
    public double getX(){
        return x;
    }
    
    public double getY(){
        return y;
    }
    
    public void draw(){
        PennDraw.picture(x, y, "src/assets/peach.png", -42, 38);
    }
}
