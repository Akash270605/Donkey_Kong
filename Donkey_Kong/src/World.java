/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Leveno
 */
public class World {
    public static void main(String[] args){
        
        PennDraw.clear();
        PennDraw.setFontSize(15);
        PennDraw.setPenColor(PennDraw.BLACK);
        PennDraw.text(0.5, 0.8, "Instructions:");
        PennDraw.text(0.5, 0.7, "use 'a' to move left, 'd' for right,");
        PennDraw.text(0.5, 0.6, "'w' to jump, and 's' to move down.");
        PennDraw.text(0.5, 0.5, "Climb the ladders to get to Princess Peach " + 
                      "to win");
        PennDraw.text(0.5, 0.4, "Avoid the barrels - you lose if one hits you");
        PennDraw.text(0.5, 0.27, "Press 'f' to activate the special power up " +
                      "once you reach the");
        PennDraw.text(0.5, 0.23, "third floor up.");
        PennDraw.setFontBold();
        PennDraw.text(0.5, 0.1, "Press 'y' to start the game");
        
        char c = 0;
        while(c != 'y'){
            if(PennDraw.hasNextKeyTyped())
                c = PennDraw.nextKeyTyped();
        }
        
        StdAudio.loop("/assets/bacmusic.wav");
        
        boolean playAgain = true;
        while(playAgain){
            int timer = 0;
            int lightningTimer = 0;
            
            int rightDir = 0;
            int leftDir = 0;
            
            int direction = 0;
            
            boolean lightning = false;
            
            boolean jumping = false;
            
            boolean climbing = false;
            int climb = 0;
            
            boolean facing = false;
            
            Floor[] floors = new Floor[6];
            
            for(int i=0; i<floors.length; i++){
                if(i%2 == 0){
                    floors[i] = new Floor(0.4, 0.8 - i * 0.15);
                }else{
                    floors[i] = new Floor(0.6, 0.65 - (i - 1) * 0.15);
                }
            }
            
            Ladder[] ladders = new Ladder[5];
            ladders[0] = new Ladder(0.4, 0.125);
            ladders[1] = new Ladder(0.7, 0.275);
            ladders[2] = new Ladder(0.3, 0.425);
            ladders[3] = new Ladder(0.6, 0.575);
            ladders[4] = new Ladder(0.45, 0.725);
            
            LinkedList<Barrel> barrels = new LinkedList<Barrel>();
            
            Mario mario = new Mario(0.5, floors[5].getY() +
                    Floor.getHeight() + 0.025);
            Peach peach = new Peach(0.70, floors[0].getY() +
                    Floor.getHeight() + 0.035);
            DonkeyKong donkey = new DonkeyKong(0.15, floors[0].getY()
            + Floor.getHeight() + 0.04);
            
            PennDraw.enableAnimation(30);
            boolean hasWon = false;
            
            while(mario.isAlive() && !hasWon){
                PennDraw.clear(PennDraw.BLACK);
                
                Barrel.draw4(floors);
                
                for(int i=0; i<floors.length; i++){
                    if(i < floors.length)
                        floors[i].draw();
                    if(i < ladders.length)
                        ladders[i].draw();
                }
                
                if(0 <= timer && timer < 145){
                    donkey.drawOriginal();
                }
                else if(145 <= timer && timer < 155){
                    donkey.drawLeft();
                }
                else if(155 <= timer && timer < 165){
                    donkey.drawCenter();
                }
                else if(165 <= timer && timer < 185){
                    donkey.drawRight();
                }
                else donkey.drawOriginal();
                
                peach.draw();
                mario.checkPosition();
                
                if(mario.getY() > 0.35){
                    if(direction == 1){
                        mario.pDrawRight(rightDir);
                    }else if(direction  == 2){
                        mario.pDrawLeft(leftDir);
                    }else {
                        mario.pDraw(facing);
                    }
                }else if(mario.ladderCollision(ladders) && climbing){
                    mario.drawClimbing(climb);
                }else if(direction == 1){
                    mario.drawRight(rightDir);
                }else if(direction == 2){
                    mario.drawLeft(leftDir);
                }else if(!(mario.floorCollision(floors)) &&
                        !(mario.ladderCollision(ladders))){
                    mario.drawJump(facing);
                }else{
                    mario.draw(facing);
                }
                
                if(lightningTimer >= 30){
                    lightningTimer = 0;
                    lightning = false;
                }
                
                direction = 0;
                
                if(PennDraw.hasNextKeyTyped()){
                    char dir = PennDraw.nextKeyTyped();
                    if(dir == 'a'){
                        if(!(mario.ladderCollision(ladders) &&
                                !mario.floorCollision(floors))){
                            mario.moveLeft();
                            leftDir ++;
                            rightDir = 0;
                            climbing = false;
                            facing = false;
                            direction = 2;
                        }
                    }
                    
                    else if(dir == 'd'){
                        if(!(mario.ladderCollision(ladders) &&
                                !mario.floorCollision(floors))){
                            mario.moveRight();
                            rightDir ++;
                            leftDir = 0;
                            climbing = false;
                            facing = true;
                            direction = 1;
                            
                        }
                    }
                    
                    if(dir == 'w'){
                        if(mario.ladderCollision(ladders)){
                            climbing = true;
                            climb++;
                            mario.moveUp();
                        }
                        
                        else if(mario.floorCollision(floors)){
                            StdAudio.play("/assets/jump.wav");
                            climbing = false;
                            jumping = true;
                            mario.jump();
                        }
                    }else if(dir == 's'){
                        if(mario.ladderCollision(ladders) &&
                                !mario.floorCollision(floors)){
                            climbing = true;
                            climb --;
                            mario.moveDown();
                        }
                    }
                    else if(dir == 'f' && mario.getY() > 0.35){
                        mario.lightning(mario.getX(), mario.getY() + 0.285);
                        lightningTimer++;
                        lightning = true;
                    }
                }
                
                if(lightningTimer > 0){
                    mario.lightning(mario.getX(), mario.getY() + 0.285);
                    lightningTimer++;
                }
                
                mario.updateY();
                
                int counter = 0;
                for(int i=0; i<floors.length; i++){
                    if((floors[i].collision(mario))){
                        counter ++;
                    }
                }
                
                if(counter <= 0.0 && !(mario.ladderCollision(ladders))){
                    mario.fall();
                }else if(mario.getVelY() < 0.0){
                    mario.stop(floors);
                }
                
                if(timer % 180 == 0){
                    barrels.add(new Barrel(0.2, floors[0].getY()+
                            floors[0].getHeight() + 0.025));
                }else if(barrels.size() > 5){
                    barrels.remove(0);
                }
                
                int counter1 = 0;
                while(counter1 < barrels.size()){
                    if(barrels.get(counter1).floorCollision(floors)){
                        if(barrels.get(counter1).getFloorLevel() % 2 == 0){
                            barrels.get(counter1).rollRight();
                        }else{
                            barrels.get(counter1).rollLeft();
                        }
                    }
                    
                    if(!(barrels.get(counter1).floorCollision(floors))){
                        barrels.get(counter1).fall();
                    }else if(barrels.get(counter1).getVelY() < 0.0){
                        int  temp = barrels.get(counter1).getFloorLevel();
                        barrels.get(counter1).setFloorLevel(temp + 1);
                        barrels.get(counter1).stop(floors);
                    }
                    
                    barrels.get(counter1).updateY();
                    counter1++;
                }
                
                int counter3 = 0;
                while(counter3 < barrels.size()){
                    if((barrels.get(counter3).getX() <= mario.getX() + 0.001 &&
                            mario.getX() - 0.001 <= barrels.get(counter3).getX())
                            && barrels.get(counter3).getY() > mario.getY()
                            && lightning == true){
                        barrels.remove(counter3);
                    }
                    counter3++;
                }
                
                int counter2 = 0;
                while(counter2 < barrels.size()){
                    barrels.get(counter2).draw();
                    counter2++;
                }
                
                mario.barrelCollision(barrels);
                timer++;
                if(timer >= 180){
                    timer = 0;
                }
                
                PennDraw.advance();
                hasWon = mario.hasWon(peach);
            }
            
            PennDraw.disableAnimation();
            
            if(hasWon){
                StdAudio.play("/assets/win1.wav");
                PennDraw.setPenColor(PennDraw.GREEN);
                PennDraw.setFontSize(100);
                PennDraw.text(0.5, 0.5, "YOU WON!");
            }
            else if(!mario.isAlive()){
                StdAudio.play("/assets/death.wav");
                PennDraw.setPenColor(PennDraw.RED);
                PennDraw.setFontSize(100);
                PennDraw.text(0.5, 0.5, "YOU LOST!");
            }
            
            PennDraw.setPenColor(PennDraw.WHITE);
            PennDraw.setFontSize(25);
            PennDraw.text(0.5, 0.4, "Press 'y' to play again or 'n' to not");
            
            char d = 0;
            while(d != 'y' && d!= 'n'){
                if(PennDraw.hasNextKeyTyped())
                    d = PennDraw.nextKeyTyped();
                if(d == 'n') 
                    System.exit(0);
            }
        }
    }
}
