/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Leveno
 */
public class DonkeyKong {
    private double x;
    private double y;
    
    public DonkeyKong(double x, double y){
        this.x = x;
        this.y = y;
    }
    
    public double getX(){
        return x;
    }
    
    public double getY(){
        return y;
    }
    
    public void drawLeft(){
        PennDraw.picture(x, y, "src/assets/donkeyLeft.png", 60, 47);
    }
    
    public void drawCenter(){
        PennDraw.picture(x, y, "src/assets/donkey.png", 60, 47);
    }
    
    public void drawRight(){
        PennDraw.picture(x, y, "src/assets/donkeyRight.png", 60, 47);
    }
    
    public void drawOriginal(){
        PennDraw.picture(x, y, "src/assets/donkeyCenter.png", 60, 47);
    }
     
}
