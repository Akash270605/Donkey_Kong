/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Leveno
 */
public class Mario {
    private double x;
    private double y;
    private double velX = 0.02;
    private double velY;
    private double jumpVel = 0.012;
    private double accelG = 0.01;
    private static final double halfHeight = 0.025;
    private static final double halfWidth = 0.01;
    private boolean isAlive = true;
    private double floorLevel;
    
    public Mario(double x, double y){
        this.x = x;
        this.y = y;
    }
    
    public double getX(){
        return x;
    }
    
    public double getY(){
        return y;
    }
    
    public double getVelY(){
        return velY;
    }
    
    public double getHalfHeight(){
        return halfHeight;
    }
    
    public boolean isAlive(){
        return isAlive;
    }
    
    public void moveRight(){
        x += velX;
    }
    
    public void moveLeft(){
        x -= velX;
    }
    
    public void setY(double y){
        this.y = y;
    }
    
    public void drawLeft(int dir){
        if(dir%3 == 0){
            PennDraw.picture(x, y + 0.01, "src/assets/marioStand.png", 35, 35);
        }else if(dir%3 == 1){
            PennDraw.picture(x, y + 0.01, "src/assets/marioRun1.png", 35, 35);
        }else if(dir%3 == 2){
            PennDraw.picture(x, y + 0.01, "src/assets/marioRun2.png", 35, 35);
        }
    }
    
    public void drawRight(int dir){
        if(dir%3 == 0){
            PennDraw.picture(x, y + 0.01, "src/assets/marioStand.png", -35, 35);
        }else if(dir%3 == 1){
            PennDraw.picture(x, y + 0.01, "src/assets/marioRun1.png", -35, 35);
        }else if(dir%3 == 2){
            PennDraw.picture(x, y + 0.01, "src/assets/marioRun2.png", -35, 35);
        }
    }
    
    public void draw(boolean facing){
        if(facing){
            PennDraw.picture(x, y, "src/assets/marioStand.png", -35, 35);
        }else{
            PennDraw.picture(x, y, "src/assets/marioStand.png", 35, 35);
        }
    }
    
    public void pDrawRight(int dir){
        if(dir % 3 == 0){
            PennDraw.picture(x, y, "src/assets/pickachu1.png", 35, 35);
        }else if(dir % 3 == 1){
            PennDraw.picture(x, y, "src/assets/pickachu2.png", 35, 35);
        }else if(dir % 3 == 2){
            PennDraw.picture(x, y, "src/assets/pickachu3.png", 35, 35);
        }
    }
    
    public void pDrawLeft(int dir){
        if(dir % 3 == 0){
            PennDraw.picture(x, y, "src/assets/pickachu1.png", - 35, 35);
        }else if(dir % 3 == 1){
            PennDraw.picture(x, y, "src/assets/pickachu2.png", -35, 35);
        }else if(dir % 3 == 2){
            PennDraw.picture(x, y, "src/assets/pickachu3.png", -35, 35);
        }
    }
    
    public void pDraw(boolean facing){
        if(facing){
            PennDraw.picture(x, y, "src/assets/pickachu1.png", 35, 35);
        }else{
            PennDraw.picture(x, y, "src/assets/pickachu1.png", -35, 35);
        }
    }
    
    public void drawClimbing(int dir){
        if(dir % 2 == 0){
            PennDraw.picture(x, y, "src/assets/climbingMario.png", 35, 35);
        }else if(dir % 2 == 1){
            PennDraw.picture(x, y, "src/assets/climbingMario.png", -35, 35);
        }
    }
    
    public void lightning(double x, double y){
        PennDraw.picture(x, y, "src/assets/lightning.PNG", 46, 350);
    }
    
    public void drawJump(boolean facing){
        if(facing){
            PennDraw.picture(x, y, "src/assets/marioRun2.png", -35, 35);
        }else {
            PennDraw.picture(x, y, "src/assets/marioRun2.png", 35, 35);
        }
    }
    
    public void updateY(){
        y += velY;
    }
    
    public void jump(){
        velY = jumpVel;
    }
    
    public void fall(){
        velY -= 0.001;
    }
    
    public void stop(Floor[] f){
        double min = Double.POSITIVE_INFINITY;
        double closest = 0;
        
        for(int i = 0; i<f.length; i++){
            double temp = Math.abs(y - f[i].getY());
            if(temp < min){
                closest = f[i].getY();
                min = temp;
            }
        }
        
        y = closest + halfHeight + Floor.getHeight();
        velY = 0.0;
    }
    
    public boolean floorCollision(Floor[] f){
        boolean floorCollide = false;
        
        for(int i=0; i<f.length; i++){
            if(f[i].collision(this)){
                floorCollide = true;
            }
        }
        
        return floorCollide;
    }
    
    public boolean ladderCollision(Ladder[] l){
        for(int i=0; i<l.length; i++){
            if(l[i].getX() - 0.015 < x && x < l[i].getX() + 0.014){
                if(l[i].getY() - 0.075 < y && y < l[i].getY() + 0.1){
                    return true;
                }
            }
        }
        return false;
    }
    
    public void moveUp(){
         y += 0.015;
    }
    
    public void moveDown(){
        y -= 0.015;
    }
    
    public boolean hasWon(Peach peach){
        if(peach.getX() < x + 0.01 && x - 0.01 < peach.getX()){
            if(peach.getY() < y + 0.015 && y - 0.015 < peach.getY()){
                return true;
            }
        }
        return false;
    }
    
    public void barrelCollision(LinkedList<Barrel> b){
        int counter = 0;
        
        while(counter < b.size()){
            if(b.get(counter).getX() < x + 0.02 &&
                    x - 0.02 < b.get(counter).getX()){
                if(b.get(counter).getY() < y + 0.03 &&
                        y - 0.03 < b.get(counter).getY())
                    isAlive = false;
            }
            counter++;
        }
    }
    
    public void checkPosition(){
        if(x > 0.97){
            x = 0.97;
        }else if(x < 0.03){
            x = 0.03;
        }
        
        if(y < -0.05){
            isAlive = false;
        }
    }
}
