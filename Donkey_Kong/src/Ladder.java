/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Leveno
 */
public class Ladder { 
    private double x;
    private double y;
    private double halfHeight = 0.075;
    private double halfWidth = 0.015;
    
    /* Constructor: ladder at x, y
     * @param double x, double y
     * @return n/a
     */
    public Ladder(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    /* Description: draws ladder
     * @param n/a
     * @return n/a
     */
    public void draw() {
        PennDraw.picture(x, y, "/assets/ladder1.png", 0.09 * 256, 70);
    }
    
    /* Description: returns y
     * @param n/a
     * @return double y
     */
    public double getY() { 
        return y; 
    }
    
    /* Description: returns x
     * @param n/a
     * @return double x
     */
    public double getX() { 
        return x; 
    }
    
}
