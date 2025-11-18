/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Leveno
 */

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.util.LinkedList;
import java.util.TreeSet;
import javax.imageio.ImageIO;
import javax.swing.*;

public final class PennDraw implements ActionListener, MouseListener, MouseMotionListener, KeyListener{
    public static final long VERSION = 2016011311;
    
    public static final Color BLACK = Color.BLACK;
    public static final Color BLUE = Color.BLUE;
    public static final Color CYAN = Color.CYAN;
    public static final Color DRAW_GRAY = Color.DARK_GRAY;
    public static final Color GRAY = Color.GRAY;
    public static final Color GREEN = Color.GREEN;
    public static final Color LIGHT_GRAY = Color.LIGHT_GRAY;
    public static final Color MAGENTA = Color.MAGENTA;
    public static final Color ORANGE = Color.ORANGE;
    public static final Color PINK = Color.PINK;
    public static final Color RED = Color.RED;
    public static final Color WHITE = Color.WHITE;
    public static final Color YELLOW = Color.YELLOW;
    
    public static final Color BOOK_BLUE = new Color (9, 90, 166);
    public static final Color BOOK_LIGHT_BLUE = new Color(103, 198, 243);
    
    public static final Color BOOK_RED = new Color(150, 35, 31);
    
    private static final Color DEFAULT_PEN_COLOR = BLACK;
    private static final Color DEFAULT_CLEAR_COLOR = WHITE;
    
    private static Color penColor;
    
    private static final int DEFAULT_SIZE = 512;
    private static int width = DEFAULT_SIZE;
    private static int height = DEFAULT_SIZE;
    
    private static final double DEFAULT_PEN_RADIUS = 0.002;
    
    private static double penRadius ;
    
    private static boolean defer = false;
    
    private static long nextDraw = -1;
    
    private static int animationSpeed = -1;
    
    private static final double BORDER = 0;
    private static final double DEFAULT_XMIN = 0.0;
    private static final double DEFAULT_XMAX = 1.0;
    private static final double DEFAULT_YMIN = 0.0;
    private static final double DEFAULT_YMAX = 1.0;
    private static double xmin, ymin, xmax , ymax;
    private static double xscale, yscale;
    
    private static Object mouseLock = new Object();
    private static Object keyLock = new Object();
    
    private static final Font DEFAULT_FONT = new Font("SansSerif", Font.PLAIN, 16);
    
    private static Font font;
    
    private static BufferedImage offscreenImage, onscreenImage;
    private static Graphics2D offscreen, onscreen;
    
    private static PennDraw std = new PennDraw();
    
    private static JFrame frame;
    
    private static boolean mousePressed = false;
    private static double mouseX = 0;
    private static double mouseY = 0;
    
    private static LinkedList<Character> keysTyped = new LinkedList<Character>();
    
    private static TreeSet<Integer> keysDown  = new TreeSet<Integer>();
    
    private PennDraw(){ }
    
    static { init(); }
    
    public static void setCanvasSize(){
        setCanvasSize(DEFAULT_SIZE, DEFAULT_SIZE);
    }
    
    public static void setCanvasSize(int w, int h){
        if(w < 1 || h < 1) throw new IllegalArgumentException("width and height must be positive");
        width = w;
        height = h;
        
        init();
    }
    
    private static void init(){
        if(frame != null) frame.setVisible(false);
        frame = new JFrame();
        offscreenImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        onscreenImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        offscreen = offscreenImage.createGraphics();
        onscreen = onscreenImage.createGraphics();
        setXscale();
        setYscale();
        offscreen.setColor(DEFAULT_CLEAR_COLOR);
        offscreen.fillRect(0, 0, width, height);
        setPenColor();
        setPenRadius();
        setFont();
        clear();
        
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        offscreen.addRenderingHints(hints);
        
        ImageIcon icon= new ImageIcon(onscreenImage);
        JLabel draw = new JLabel(icon);
        
        draw.addMouseListener(std);
        draw.addMouseMotionListener(std);
        
        frame.setContentPane(draw);
        frame.addKeyListener(std);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Standard Draw");
        frame.setJMenuBar(createMenuBar());
        frame.pack();
        frame.requestFocusInWindow();
        frame.setVisible(true);
    }
    
    private static JMenuBar createMenuBar(){
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        menuBar.add(menu);
        JMenuItem menuItem1 = new JMenuItem(" Save...   ");
        menuItem1.addActionListener(std);
        menuItem1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        menu.add(menuItem1);
        return menuBar;
    }
    
    public static void setXscale() { setXscale(DEFAULT_XMIN, DEFAULT_XMAX); } 
    
     public static void setYscale() { setYscale(DEFAULT_YMIN, DEFAULT_YMAX); }
     
    public static void setXscale(double min, double max){
        double size = max - min;
        synchronized(mouseLock){
            xmin = min - BORDER * size;
            xmax = max + BORDER * size;
            setTransform();
        }
    }
    
    public static void setYscale(double min, double max){
        double size = max - min;
        synchronized(mouseLock){
            ymin = min - BORDER * size;
            ymax = max + BORDER * size;
            setTransform();
        }
    }
    
    public static void setScale(double min, double max){
        double size = max - min;
        synchronized (mouseLock){
            xmin = min - BORDER * size;
            xmax = max + BORDER * size;
            ymin = min - BORDER * size;
            ymax = max + BORDER  * size;
            setTransform();
        }
    }
    
    private static void setTransform(){
        xscale = width / (xmax - xmin);
        yscale = height / (ymax - ymin);
    }
    
    private static double scaleX(double x) { return xscale * (x - xmin); }
    private static double scaleY(double y) { return yscale * (ymax - y); } 
    private static double factorX(double w) { return w * width / Math.abs(xmax - xmin); }
    private static double factorY(double h) { return h * height / Math.abs(ymax - ymin); }
    private static double userX(double x) { return xmin + x / xscale; }
    private static double userY(double y) { return ymax - y / yscale; }
    
    public static void clear() { clear(DEFAULT_CLEAR_COLOR); }  
    
    public static void clear(Color color){
        offscreen.setColor(color);
        filledRectangle(0.5 * (xmax + xmin), 0.5 * (ymax + ymin),
                        0.5 * (xmax - xmin), 0.5 * (ymax - ymin));
        offscreen.setColor(penColor);
        draw();
    }
    
    public static void clear(int red, int green, int blue){
        if(red < 0 || red >= 256) throw new IllegalArgumentException("amount of red must be between 0 and 255");
        if(green < 0 || green >= 256) throw new IllegalArgumentException("amount of green must be between 0 and 255");
        if(blue < 0 || blue >= 256) throw new IllegalArgumentException("amount of blue must be between 0 and 255");
        clear(new Color(red, green, blue));
    }
    
    public static void clear(int red, int green, int blue, int alpha){
        if(red < 0 || red >= 256) throw new IllegalArgumentException("amount of red must be between 0 and 255");
        if (green < 0 || green >= 256) throw new IllegalArgumentException("amount of green must be between 0 and 255");
        if (blue  < 0 || blue  >= 256) throw new IllegalArgumentException("amount of blue must be between 0 and 255");
        if (alpha < 0 || alpha >= 256) throw new IllegalArgumentException("amount of alpha must be between 0 and 255");
        clear(new Color(red, green, blue, alpha));
    }
    
    public static double getPenRadius() { return penRadius; }
    
    public static void setPenRadius() { setPenRadius(DEFAULT_PEN_RADIUS); }
    
    public static void setPenRadius(double r){
        if(r < 0) throw new IllegalArgumentException("pen radius must be nonnegative");
        penRadius = r;
        float scaledPenRadius = (float) (r * DEFAULT_SIZE);
        BasicStroke stroke = new BasicStroke(scaledPenRadius, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        offscreen.setStroke(stroke);
    }
    
    public static void setPenWidthInPixels(double w){
        if(w < 0) throw new IllegalArgumentException("pen radius must be nonnegative");
        setPenRadius(w / (2 * width));
    }
    
    public static void setPenWidthInPoints(double w){
        if(w < 0) throw new IllegalArgumentException("pen radius must be nonnegative");
        int dpi = frame.getToolkit().getScreenResolution();
        setPenRadius(dpi * w/ (144 * width));
    }
    
    public static Color getPenColor() { return penColor; }
    
    public static void setPenColor() { setPenColor(DEFAULT_PEN_COLOR); }
    
    public static void setPenColor(Color color){
        penColor = color;
        offscreen.setColor(penColor);
    }
    
    public static void setPenColor(int red, int green, int blue){
        if (red   < 0 || red   >= 256) throw new IllegalArgumentException("amount of red must be between 0 and 255");
        if (green < 0 || green >= 256) throw new IllegalArgumentException("amount of green must be between 0 and 255");
        if (blue  < 0 || blue  >= 256) throw new IllegalArgumentException("amount of blue must be between 0 and 255");
        setPenColor(new Color(red, green, blue));
    }
    
    public static void setPenColor(int red, int green, int blue, int alpha) {
        if (red   < 0 || red   >= 256) throw new IllegalArgumentException("amount of red must be between 0 and 255");
        if (green < 0 || green >= 256) throw new IllegalArgumentException("amount of green must be between 0 and 255");
        if (blue  < 0 || blue  >= 256) throw new IllegalArgumentException("amount of blue must be between 0 and 255");
        if (alpha < 0 || alpha >= 256) throw new IllegalArgumentException("amount of alpha must be between 0 and 255");
        setPenColor(new Color(red, green, blue, alpha));
    }
    
    public static Font getFont() { return font; }
    
    public static void listFonts(){
        String fonts[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        
        for(String s : fonts)
            System.out.println(s);
    }
    
    public static void setFont() { setFont(DEFAULT_FONT); }
    
    public static void setFont(Font f){
        font = f;
        offscreen.setFont(f);
    }
    
    public static void setFont(String fontName){
        setFont(new Font(fontName, font.getStyle(), font.getSize()));
    }
    
    public static void setFont(String fontName, double pointSize){
        setFont(fontName);
        setFont(font.deriveFont((float) pointSize));
    }
    
    public static void setFontSize(double pointSize){
        setFont(font.deriveFont((float) pointSize));
    }
    
    public static void setFontSizeInPixels(double pixelHeight){
        int dpi = frame.getToolkit().getScreenResolution();
        double pointSize = pixelHeight * dpi / 72;
        System.out.println(dpi);
        System.out.println(pointSize);
        setFont(font.deriveFont((float) pointSize));
    }
    
    public static void setFontPlain(){
        setFont(font.deriveFont(Font.PLAIN));
    }
    
    public static void setFontBold(){
        setFont(font.deriveFont(Font.BOLD));
    }
    
    public static void setFontItalic(){
        setFont(font.deriveFont(Font.ITALIC));
    }
    
    public static void setFontBoldItalic(){
        setFont(font.deriveFont(Font.BOLD | Font.ITALIC));
    }
    
    
    public static void line(double x0, double y0, double x1, double y1){
        offscreen.draw(new Line2D.Double(scaleX(x0), scaleY(y0), scaleX(x1), scaleY(y1)));
        draw();
    }
    
    private static void pixel(double x, double y){
        offscreen.fillRect((int) Math.round(scaleX(x)), (int) Math.round(scaleY(y)), 1, 1);
    }
    
    public static void point(double x, double y){
        double r = penRadius;
        float scaledPenRadius = (float) (r * DEFAULT_SIZE);
        
        if(scaledPenRadius <= 1) pixel(x, y);
        else offscreen.fill(new Ellipse2D.Double(scaleX(x) - scaledPenRadius/2,
        scaleY(y) - scaledPenRadius/2,
        scaledPenRadius, scaledPenRadius));
        draw();
    }
    
    public static void circle(double x, double y, double r){
        if(r < 0) throw new IllegalArgumentException("circle radius must be nonnegative");
        ellipse(x, y , r ,r);
    }
    
    public static void filledCircle(double x, double y, double r){
        if (r < 0) throw new IllegalArgumentException("circle radius must be nonnegative");
        filledEllipse(x, y, r, r);
    }
    
    public static void ellipse(double x, double y, double semiMajorAxis, double semiMinorAxis) {
        ellipse(x, y, semiMajorAxis, semiMinorAxis, false);
    }

    /**
     * Draw an ellipse with given semimajor and semiminor axes, centered on (x, y).
     * @param x the x-coordinate of the center of the ellipse
     * @param y the y-coordinate of the center of the ellipse
     * @param semiMajorAxis is the semimajor axis of the ellipse
     * @param semiMinorAxis is the semiminor axis of the ellipse
     * @param degrees counter-clockwise rotation by angle degrees around the center
     * @throws IllegalArgumentException if either of the axes are negative
     */
    public static void ellipse(double x, double y, double semiMajorAxis, double semiMinorAxis, double degrees) {
        AffineTransform t = (AffineTransform) offscreen.getTransform();
        offscreen.rotate(Math.toRadians(-degrees), scaleX(x), scaleY(y));
        ellipse(x, y, semiMajorAxis, semiMinorAxis);
        offscreen.setTransform(t);
    }

    /**
     * Draw a filled ellipse with given semimajor and semiminor axes, centered on (x, y).
     * @param x the x-coordinate of the center of the ellipse
     * @param y the y-coordinate of the center of the ellipse
     * @param semiMajorAxis is the semimajor axis of the ellipse
     * @param semiMinorAxis is the semiminor axis of the ellipse
     * @throws IllegalArgumentException if either of the axes are negative
     */
    public static void filledEllipse(double x, double y, double semiMajorAxis, double semiMinorAxis) {
        ellipse(x, y, semiMajorAxis, semiMinorAxis, true);
    }


    /**
     * Draw a filled ellipse with given semimajor and semiminor axes, centered on (x, y),
     * @param x the x-coordinate of the center of the ellipse
     * @param y the y-coordinate of the center of the ellipse
     * @param semiMajorAxis is the semimajor axis of the ellipse
     * @param semiMinorAxis is the semiminor axis of the ellipse
     * @param degrees counter-clockwise rotation by angle degrees around the center
     * @throws IllegalArgumentException if either of the axes are negative
     */
    public static void filledEllipse(double x, double y, double semiMajorAxis, double semiMinorAxis, double degrees) {
        AffineTransform t = (AffineTransform) offscreen.getTransform().clone();
        offscreen.rotate(Math.toRadians(-degrees), scaleX(x), scaleY(y));
        filledEllipse(x, y, semiMajorAxis, semiMinorAxis);
        offscreen.setTransform(t);
    }

    // helper function for drawing ellipses and filled ellipses
    private static void ellipse(double x, double y, double semiMajorAxis, double semiMinorAxis, boolean filled) {
        if (semiMajorAxis < 0) throw new IllegalArgumentException("ellipse semimajor axis must be nonnegative");
        if (semiMinorAxis < 0) throw new IllegalArgumentException("ellipse semiminor axis must be nonnegative");
        double xs = scaleX(x);
        double ys = scaleY(y);
        double ws = factorX(2 * semiMajorAxis);
        double hs = factorY(2 * semiMinorAxis);
        if (ws <= 1 && hs <= 1) pixel(x, y);
        else if (filled)        offscreen.fill(new Ellipse2D.Double(xs - ws / 2, ys - hs / 2, ws, hs));
        else                    offscreen.draw(new Ellipse2D.Double(xs - ws / 2, ys - hs / 2, ws, hs));
        draw();
    }

    /**
     * Draw an arc of radius r, centered on (x, y), from angle1 to angle2 (in degrees).
     * @param x the x-coordinate of the center of the circle
     * @param y the y-coordinate of the center of the circle
     * @param r the radius of the circle
     * @param angle1 the starting angle. 0 would mean an arc beginning at 3 o'clock.
     * @param angle2 the angle at the end of the arc. For example, if
     *        you want a 90 degree arc, then angle2 should be angle1 + 90.
     * @throws IllegalArgumentException if the radius of the circle is negative
     */
    public static void arc(double x, double y, double r, double angle1, double angle2) {
        arc(x, y, r, angle1, angle2, Arc2D.OPEN, false);
    }

    /**
     * Draw a closed arc of radius r, centered on (x, y), from angle1 to angle2 (in degrees).
     * @param x the x-coordinate of the center of the circle
     * @param y the y-coordinate of the center of the circle
     * @param r the radius of the circle
     * @param angle1 the starting angle. 0 would mean an arc beginning at 3 o'clock.
     * @param angle2 the angle at the end of the arc. For example, if
     *        you want a 90 degree arc, then angle2 should be angle1 + 90.
     * @throws IllegalArgumentException if the radius of the circle is negative
     */
    public static void closedArc(double x, double y, double r, double angle1, double angle2) {
        arc(x, y, r, angle1, angle2, Arc2D.CHORD, false);
    }

    /**
     * Draw a pie wedge of radius r, centered on (x, y), from angle1 to angle2 (in degrees).
     * @param x the x-coordinate of the center of the circle
     * @param y the y-coordinate of the center of the circle
     * @param r the radius of the circle
     * @param angle1 the starting angle. 0 would mean an arc beginning at 3 o'clock.
     * @param angle2 the angle at the end of the arc. For example, if
     *        you want a 90 degree arc, then angle2 should be angle1 + 90.
     * @throws IllegalArgumentException if the radius of the circle is negative
     */
    public static void pie(double x, double y, double r, double angle1, double angle2) {
        arc(x, y, r, angle1, angle2, Arc2D.PIE, false);
    }

    /**
     * Draw a filled pie wedge of radius r, centered on (x, y), from angle1 to angle2 (in degrees).
     * @param x the x-coordinate of the center of the circle
     * @param y the y-coordinate of the center of the circle
     * @param r the radius of the circle
     * @param angle1 the starting angle. 0 would mean an arc beginning at 3 o'clock.
     * @param angle2 the angle at the end of the arc. For example, if
     *        you want a 90 degree arc, then angle2 should be angle1 + 90.
     * @throws IllegalArgumentException if the radius of the circle is negative
     */
    public static void filledPie(double x, double y, double r, double angle1, double angle2) {
        arc(x, y, r, angle1, angle2, Arc2D.PIE, true);
    }

    /**
     * Draw a filled arc of radius r, centered on (x, y), from angle1 to angle2 (in degrees).
     * @param x the x-coordinate of the center of the circle
     * @param y the y-coordinate of the center of the circle
     * @param r the radius of the circle
     * @param angle1 the starting angle. 0 would mean an arc beginning at 3 o'clock.
     * @param angle2 the angle at the end of the arc. For example, if
     *        you want a 90 degree arc, then angle2 should be angle1 + 90.
     * @throws IllegalArgumentException if the radius of the circle is negative
     */
    public static void filledArc(double x, double y, double r, double angle1, double angle2) {
        arc(x, y, r, angle1, angle2, Arc2D.CHORD, true);
    }

    // common code for all arc functions
    private static void arc(double x, double y, double r, double angle1, double angle2, int pathType, boolean fill) {
        if (r < 0) throw new IllegalArgumentException("arc radius must be nonnegative");
        while (angle2 < angle1) angle2 += 360;
        double xs = scaleX(x);
        double ys = scaleY(y);
        double ws = factorX(2*r);
        double hs = factorY(2*r);
        if (ws <= 1 && hs <= 1) {
            pixel(x, y);
        } else {
            if (fill) offscreen.fill(new Arc2D.Double(xs - ws/2, ys - hs/2, ws, hs, angle1, angle2 - angle1, pathType));
            else      offscreen.draw(new Arc2D.Double(xs - ws/2, ys - hs/2, ws, hs, angle1, angle2 - angle1, pathType));
        }

        draw();
    }


    /**
     * Draw a square of side length 2r, centered on (x, y).
     * @param x the x-coordinate of the center of the square
     * @param y the y-coordinate of the center of the square
     * @param r radius is half the length of any side of the square
     * @throws IllegalArgumentException if r is negative
     */
    public static void square(double x, double y, double r) {
        if (r < 0) throw new IllegalArgumentException("square side length must be nonnegative");
        rectangle(x, y, r, r);
    }

    /**
     * Draw a square of side length 2r, centered on (x, y) and rotated counter-clockwise.
     * @param x the x-coordinate of the center of the square
     * @param y the y-coordinate of the center of the square
     * @param r radius is half the length of any side of the square
     * @param degrees counter-clockwise rotation by angle degrees around the center
     * @throws IllegalArgumentException if r is negative
     */
    public static void square(double x, double y, double r, double degrees) {
        if (r < 0) throw new IllegalArgumentException("square side length must be nonnegative");
        rectangle(x, y, r, r, degrees);
    }

    /**
     * Draw a filled square of side length 2r, centered on (x, y).
     * @param x the x-coordinate of the center of the square
     * @param y the y-coordinate of the center of the square
     * @param r radius is half the length of any side of the square
     * @throws IllegalArgumentException if r is negative
     */
    public static void filledSquare(double x, double y, double r) {
        if (r < 0) throw new IllegalArgumentException("square side length must be nonnegative");
        filledRectangle(x, y, r, r);
    }

    /**
     * Draw a filled square of side length 2r, centered on (x, y) and rotated counter-clockwise.
     * @param x the x-coordinate of the center of the square
     * @param y the y-coordinate of the center of the square
     * @param r radius is half the length of any side of the square
     * @param degrees counter-clockwise rotation by angle degrees around the center
     * @throws IllegalArgumentException if r is negative
     */
    public static void filledSquare(double x, double y, double r, double degrees) {
        if (r < 0) throw new IllegalArgumentException("square side length must be nonnegative");
        filledRectangle(x, y, r, r, degrees);
    }

    /**
     * Draw a rectangle of given half width and half height, centered on (x, y).
     * @param x the x-coordinate of the center of the rectangle
     * @param y the y-coordinate of the center of the rectangle
     * @param halfWidth is half the width of the rectangle
     * @param halfHeight is half the height of the rectangle
     * @throws IllegalArgumentException if halfWidth or halfHeight is negative
     */
    public static void rectangle(double x, double y, double halfWidth, double halfHeight) {
        rectangle(x, y, halfWidth, halfHeight, false);
    }

    /**
     * Draw a rectangle of given half width and half height, centered on (x, y) and rotated counter-clockwise.
     * @param x the x-coordinate of the center of the rectangle
     * @param y the y-coordinate of the center of the rectangle
     * @param halfWidth is half the width of the rectangle
     * @param halfHeight is half the height of the rectangle
     * @param degrees counter-clockwise rotation by angle degrees around the center
     * @throws IllegalArgumentException if halfWidth or halfHeight is negative
     */
    public static void rectangle(double x, double y, double halfWidth, double halfHeight, double degrees) {
        AffineTransform t = (AffineTransform) offscreen.getTransform().clone();

        offscreen.rotate(Math.toRadians(-degrees), scaleX(x), scaleY(y));
        rectangle(x, y, halfWidth, halfHeight);
        offscreen.setTransform(t);
    }

    /**
     * Draw a filled rectangle of given half width and half height, centered on (x, y).
     * @param x the x-coordinate of the center of the rectangle
     * @param y the y-coordinate of the center of the rectangle
     * @param halfWidth is half the width of the rectangle
     * @param halfHeight is half the height of the rectangle
     * @throws IllegalArgumentException if halfWidth or halfHeight is negative
     */
    public static void filledRectangle(double x, double y, double halfWidth, double halfHeight) {
        rectangle(x, y, halfWidth, halfHeight, true);
    }


    /**
     * Draw a rectangle of given half width and half height, centered on (x, y) and rotated counter-clockwise.
     * @param x the x-coordinate of the center of the rectangle
     * @param y the y-coordinate of the center of the rectangle
     * @param halfWidth is half the width of the rectangle
     * @param halfHeight is half the height of the rectangle
     * @param degrees counter-clockwise rotation by angle degrees around the center
     * @throws IllegalArgumentException if halfWidth or halfHeight is negative
     */
    public static void filledRectangle(double x, double y, double halfWidth, double halfHeight, double degrees) {
        AffineTransform t = (AffineTransform) offscreen.getTransform();

        offscreen.rotate(Math.toRadians(-degrees), scaleX(x), scaleY(y));
        filledRectangle(x, y, halfWidth, halfHeight);
        offscreen.setTransform(t);
    }

    private static void rectangle(double x, double y, double halfWidth, double halfHeight, boolean filled) {
        if (halfWidth  < 0) throw new IllegalArgumentException("half width must be nonnegative");
        if (halfHeight < 0) throw new IllegalArgumentException("half height must be nonnegative");

        double xs = scaleX(x);
        double ys = scaleY(y);
        double ws = factorX(2 * halfWidth);
        double hs = factorY(2 * halfHeight);

        if (ws <= 1 && hs <= 1) pixel(x, y);
        else if (filled)        offscreen.fill(new Rectangle2D.Double(xs - ws / 2, ys - hs / 2, ws, hs));
        else                    offscreen.draw(new Rectangle2D.Double(xs - ws / 2, ys - hs / 2, ws, hs));
        draw();
    }

    /**
     * Draw a polyline with the given (x[i], y[i]) coordinates.
     * @param x an array of all the x-coordindates of the polygon
     * @param y an array of all the y-coordindates of the polygon
     */
    public static void polyline(double[] x, double[] y) {
        polygon(x, y, false, false);
    }

    /**
     * Draw a polygon with the given (x[i], y[i]) coordinates.
     * @param x an array of all the x-coordindates of the polygon
     * @param y an array of all the y-coordindates of the polygon
     */
    public static void polygon(double[] x, double[] y) {
        polygon(x, y, true, false);
    }

    /**
     * Draw a filled polygon with the given (x[i], y[i]) coordinates.
     * @param x an array of all the x-coordindates of the polygon
     * @param y an array of all the y-coordindates of the polygon
     */
    public static void filledPolygon(double[] x, double[] y) {
        polygon(x, y, true, true);
    }

    private static void polygon(double[] x, double[] y, boolean close, boolean fill) {
        int N = x.length;

        if (y.length != N)
            throw new IllegalArgumentException("x[] and y[] must have the same number of elements.  " +
                                               "x[] has " + x.length + " elements, but y[] has " +
                                               y.length + " elements.");

        if ((close || fill) && N < 3)
            throw new IllegalArgumentException("You must specify at least three for a polygon.  " +
                                               "You have only provided " + N + " points.");
            
        Path2D.Double path = new Path2D.Double();
        path.moveTo(scaleX(x[0]), scaleY(y[0]));
        for (int i = 0; i < N; i++)
            path.lineTo(scaleX(x[i]), scaleY(y[i]));
        if (close || fill) path.closePath();
        if (fill) offscreen.fill(path);
        else      offscreen.draw(path);
        draw();
    }
    
    /**
     * Draw a polyline with the given coordinates.
     * @param coords an array of all the coordindates of the polygon (x1, y1, x2, y2, ...)
     * @throws IllegalArgumentException if the number of arguments is not even
     */
    public static void polyline(double ... coords) {
        polygon(false, false, coords);
    }

    /**
     * Draw a polygon with the given coordinates.
     * @param coords an array of all the coordindates of the polygon (x1, y1, x2, y2, ...)
     * @throws IllegalArgumentException if the number of arguments is not even and at least 6
     */
    public static void polygon(double ... coords) {
        polygon(true, false, coords);
    }

    /**
     * Draw a filled polygon with the given coordinates.
     * @param coords an array of all the coordindates of the polygon (x1, y1, x2, y2, ...)
     * @throws IllegalArgumentException if the number of arguments is not even and at least 6
     */
    public static void filledPolygon(double ... coords) {
        polygon(true, true, coords);
    }

    private static void polygon(boolean close, boolean fill, double ... coords) {
        int N = coords.length;

        if ((N % 2) != 0)
            throw new IllegalArgumentException("You must specify an even number of coordinates.  " +
                                               "You actually specified " + N + " coordinates.");

        // only need at least three vertices for closed/filled polygons
        if (close || fill)
            if (N < 6)
                throw new IllegalArgumentException("You must specify at least six coordinates (three points).  " +
                                                   "You only specified " + N + " coordinates.");

        Path2D.Double path = new Path2D.Double();
        path.moveTo(scaleX(coords[0]), scaleY(coords[1]));
        for (int i = 0; i < N; i += 2)
            path.lineTo(scaleX(coords[i]), scaleY(coords[i + 1]));

        if (close || fill) path.closePath();

        if (fill) offscreen.fill(path);
        else      offscreen.draw(path);

        draw();
    }

    
    private static Image getImage(String filename){
        ImageIcon icon = new ImageIcon(filename);
        
        if((icon == null) || (icon.getImageLoadStatus() != MediaTracker.COMPLETE)){
            try{
                URL url = new URL(filename);
                icon = new ImageIcon(url);
            }catch(Exception e){ }
        }
        
        if((icon == null) || (icon.getImageLoadStatus() != MediaTracker.COMPLETE)){
            URL url = PennDraw.class.getResource(filename);
            if(url == null) throw new IllegalArgumentException("image " + filename + " not found");
            icon = new ImageIcon(url);
        }
        return icon.getImage();
    }
    
    public static void picture(double x, double y, String s){
        picture(x, y, s, 0, 0, 0);
    }
    
    public static void picture(double x, double y, String s, double degrees){
        picture(x, y, s, 0, 0, degrees);
    }
    
    public static void picture(double x, double y, String s, double w, double h){
        picture(x, y, s, w, h, 0);
    }
    
    public static void picture(double x, double y, String s, double w, double h, double degrees){
       Image image = getImage(s);
       int iw = image.getWidth(null);
       int ih = image.getHeight(null);
       if(iw <= 0 || ih <= 0) throw new IllegalArgumentException("image "+ s+ " is corrupt");
       
       AffineTransform t = (AffineTransform) offscreen.getTransform();
       
       double xs = xscale * (x - xmin);
       double ys = height - yscale * (y - ymin);
       
       if(degrees != 0) offscreen.rotate(-Math.toRadians(degrees), xs, ys);
       
       if(w == 0 && h == 0)
           offscreen.drawImage(image, (int) Math.round(xs - 0.5 *  iw), (int) Math.round(ys - 0.5 * ih), null);
       else{
           if(w == 0) w = (iw * h) / ih;
           if(h==0) h = (ih * w)/ iw;
           
           offscreen.drawImage(image, (int) Math.round(xs - 0.5 * w), (int) Math.round(ys - 0.5 * h),
                   (int) Math.round(w), (int) Math.round(h), null);
       }
       
       if(degrees != 0) offscreen.setTransform(t);
       draw();
    }
    
    public static void text(double x, double y, String s){
        text(x, y, s, 0);
    }
    
    public static void text(double x, double y, String s, double degrees){
        text(x, y, s, degrees, -0.5);
    }
    
    public static void textLeft(double x, double y, String s){
        textLeft(x, y, s, 0);
    }
    
    public static void textLeft(double x, double y, String s, double degrees){
        text(x, y, s, degrees, 0);
    }
    
    public static void textRight(double x, double y, String s){
        textRight(x, y, s, 0);
    }
    
    public static void textRight(double x, double y, String s, double degrees){
        text(x, y, s, degrees, -1);
    }
    
    private static void text(double x, double y, String s, double degrees, double dw){
        AffineTransform t = (AffineTransform) offscreen.getTransform();
        
        FontMetrics metrics = offscreen.getFontMetrics();
        int w = metrics.stringWidth(s);
        int h = metrics.getDescent();
        
        double xs = scaleX(x);
        double ys = scaleY(y);
        
        if(degrees != 0) offscreen.rotate(-Math.toRadians(degrees), xs, ys);
        offscreen.drawString(s, (float) (xs + dw *w), (float) (ys + h));
        
        if(degrees != 0) offscreen.setTransform(t);
        draw();
    }
    
    public static void show(int t){
        long millis = System.currentTimeMillis();
        if(millis < nextDraw){
            try{
                Thread.sleep(nextDraw - millis);
            }
            catch(InterruptedException e) { System.out.println("Error sleeping"); }
            millis = nextDraw;
        }
        
        defer = false;
        draw();
        defer = true;
        
        nextDraw = millis + t;
    }
    
    public static void show(){
        defer = false;
        draw();
    }
    
    private static void draw(){
        if(defer) return;
        onscreen.drawImage(offscreenImage, 0, 0, null);
        frame.repaint();
    }
    
    public static void disableAnimation(){
        animationSpeed = -1;
        show();
    }
    
    public static void enableAnimation(double frameRate){
        if(frameRate < 0) throw new IllegalArgumentException("frameRate must be >= 0");
        animationSpeed = frameRate == 0 ? 0 : (int) Math.round(1000.0 / frameRate);
        show(0);
    }
    
    public static void advance(){
        if(animationSpeed < 0)
            throw new RuntimeException("You must call PennDraw.enableAnimation() to activation animation mode before calling PennDraw.advance()");
        
        show(animationSpeed);
    }
    
    public static void save(String filename){
    File file = new File(filename);
    String suffix = filename.substring(filename.lastIndexOf(' ') + 1);
    
    // png files
    if(suffix.toLowerCase().equals("png")){
        try{ ImageIO.write(onscreenImage, suffix, file);}
        catch(IOException e) { e.printStackTrace(); }
    }
    
    else if(suffix.toLowerCase().equals("jpg")){
        WritableRaster raster = onscreenImage.getRaster();
        WritableRaster newRaster;
        newRaster = raster.createWritableChild(0, 0, width, height, 0, 0, new int[] {0, 1, 2});
        DirectColorModel cm = (DirectColorModel) onscreenImage.getColorModel();
        DirectColorModel newCM = new DirectColorModel(cm.getPixelSize(),
        cm.getRedMask(),
        cm.getGreenMask(),
        cm.getBlueMask());
        BufferedImage rgbBuffer = new BufferedImage(newCM, newRaster, false, null);
        try{ ImageIO.write(rgbBuffer, suffix, file); }
        catch (IOException e) { e.printStackTrace(); }
    }
    else{
        System.out.println("Invalid image file type: "+ suffix);
    }
    }
    
    public void actionPerformed(ActionEvent e){
        FileDialog chooser = new FileDialog(PennDraw.frame, "Use a .png or .jpg extension", FileDialog.SAVE);
        chooser.setVisible(true);
        String filename = chooser.getFile();
        if(filename != null){
            PennDraw.save(chooser.getDirectory() + File.separator + chooser.getFile());
        }
    }
    
    public static boolean mousePressed(){
        synchronized (mouseLock){
            return mousePressed;
        }
    }
    
    public static double mouseX(){
        synchronized(mouseLock){
            return mouseX;
        }
    }
    
    public static double mouseY(){
        synchronized (mouseLock){
            return mouseY;
        }
    }
    
    public void mouseClicked(MouseEvent e){ }
    
    public void mouseEntered(MouseEvent e) { }
    
    public void mouseExited(MouseEvent e) { }
    
    public void mousePressed(MouseEvent e){
        synchronized (mouseLock){
            mouseX = userX(e.getX());
            mouseY = userY(e.getY());
            mousePressed  = true;
        }
    }
    
    public void mouseReleased(MouseEvent e){
        synchronized(mouseLock){
            mousePressed = false;
        }
    }
    
    public void mouseDragged(MouseEvent e){
        synchronized (mouseLock){
            mouseX = userX(e.getX());
            mouseY = userY(e.getY());
        }
    }
    
    public void mouseMoved(MouseEvent e){
        synchronized(mouseLock){
            mouseX = userX(e.getX());
            mouseY = userY(e.getY());
        }
    }
    
    public static boolean hasNextKeyTyped(){
        synchronized (keyLock){
            return !keysTyped.isEmpty();
        }
    }
    
    public static char nextKeyTyped(){
        synchronized (keyLock){
            return keysTyped.removeLast();
        }
    }
    
    public static boolean isKeyPressed(int keycode){
        synchronized (keyLock){
            return keysDown.contains(keycode);
        }
    }
    
    public void keyTyped(KeyEvent e){
        synchronized (keyLock){
            keysTyped.addFirst(e.getKeyChar());
        }
    }
    
    public void keyPressed(KeyEvent e){
        synchronized(keyLock){
            keysDown.add(e.getKeyCode());
        }
    }
    
    public void keyReleased(KeyEvent e){
        synchronized (keyLock){
            keysDown.remove(e.getKeyCode());
        }
    }
}
