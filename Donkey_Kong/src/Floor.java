/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Leveno
 */
public class Floor {
    private double x;
    private double y;
    
    private static double halfWidth = 0.4;
    private static double halfHeight = 0.01;
    
    public Floor(double x, double y){
        this.x = x;
        this.y = y;
    }
    
    public double getX(){
        return x;
    }
    
    public double getY(){
        return y;
    }
    
    public static double getWidth(){
        return halfWidth;
    }
    
    public static double getHeight(){
        return halfHeight;
    }
    
    public void draw(){
        PennDraw.picture(x, y, "src/assets/floor.png", 2 * halfWidth * 512, 
                2 * halfHeight * 512);
    }
    
    public boolean collision(Mario mario){
        return (mario.getY() - mario.getHalfHeight() <= y + halfHeight &&
                mario.getY() >= y && mario.getX() <= x + halfWidth &&
                mario.getX() >=  x - halfWidth);
    }
    
    public boolean collision(Barrel barrel){
        return (barrel.getY() - barrel.getRadius() <= y + halfHeight &&
                barrel.getY() >= y && barrel.getX() <= x + halfWidth &&
                barrel.getX() >= x - halfWidth);
    }
}
