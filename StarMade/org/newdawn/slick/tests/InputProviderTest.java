/*    */ package org.newdawn.slick.tests;
/*    */ 
/*    */ import org.newdawn.slick.AppGameContainer;
/*    */ import org.newdawn.slick.BasicGame;
/*    */ import org.newdawn.slick.GameContainer;
/*    */ import org.newdawn.slick.Graphics;
/*    */ import org.newdawn.slick.SlickException;
/*    */ import org.newdawn.slick.command.BasicCommand;
/*    */ import org.newdawn.slick.command.Command;
/*    */ import org.newdawn.slick.command.ControllerButtonControl;
/*    */ import org.newdawn.slick.command.ControllerDirectionControl;
/*    */ import org.newdawn.slick.command.InputProvider;
/*    */ import org.newdawn.slick.command.InputProviderListener;
/*    */ import org.newdawn.slick.command.KeyControl;
/*    */ import org.newdawn.slick.command.MouseButtonControl;
/*    */ 
/*    */ public class InputProviderTest extends BasicGame
/*    */   implements InputProviderListener
/*    */ {
/* 25 */   private Command attack = new BasicCommand("attack");
/*    */ 
/* 27 */   private Command jump = new BasicCommand("jump");
/*    */ 
/* 29 */   private Command run = new BasicCommand("run");
/*    */   private InputProvider provider;
/* 33 */   private String message = "";
/*    */ 
/*    */   public InputProviderTest()
/*    */   {
/* 39 */     super("InputProvider Test");
/*    */   }
/*    */ 
/*    */   public void init(GameContainer container)
/*    */     throws SlickException
/*    */   {
/* 46 */     this.provider = new InputProvider(container.getInput());
/* 47 */     this.provider.addListener(this);
/*    */ 
/* 49 */     this.provider.bindCommand(new KeyControl(203), this.run);
/* 50 */     this.provider.bindCommand(new KeyControl(30), this.run);
/* 51 */     this.provider.bindCommand(new ControllerDirectionControl(0, ControllerDirectionControl.LEFT), this.run);
/* 52 */     this.provider.bindCommand(new KeyControl(200), this.jump);
/* 53 */     this.provider.bindCommand(new KeyControl(17), this.jump);
/* 54 */     this.provider.bindCommand(new ControllerDirectionControl(0, ControllerDirectionControl.UP), this.jump);
/* 55 */     this.provider.bindCommand(new KeyControl(57), this.attack);
/* 56 */     this.provider.bindCommand(new MouseButtonControl(0), this.attack);
/* 57 */     this.provider.bindCommand(new ControllerButtonControl(0, 1), this.attack);
/*    */   }
/*    */ 
/*    */   public void render(GameContainer container, Graphics g)
/*    */   {
/* 64 */     g.drawString("Press A, W, Left, Up, space, mouse button 1,and gamepad controls", 10.0F, 50.0F);
/* 65 */     g.drawString(this.message, 100.0F, 150.0F);
/*    */   }
/*    */ 
/*    */   public void update(GameContainer container, int delta)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void controlPressed(Command command)
/*    */   {
/* 78 */     this.message = ("Pressed: " + command);
/*    */   }
/*    */ 
/*    */   public void controlReleased(Command command)
/*    */   {
/* 85 */     this.message = ("Released: " + command);
/*    */   }
/*    */ 
/*    */   public static void main(String[] argv)
/*    */   {
/*    */     try
/*    */     {
/* 95 */       AppGameContainer container = new AppGameContainer(new InputProviderTest());
/* 96 */       container.setDisplayMode(800, 600, false);
/* 97 */       container.start();
/*    */     } catch (SlickException e) {
/* 99 */       e.printStackTrace();
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Raul\Desktop\StarMade\StarMade.jar
 * Qualified Name:     org.newdawn.slick.tests.InputProviderTest
 * JD-Core Version:    0.6.2
 */