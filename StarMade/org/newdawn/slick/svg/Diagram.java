/*     */ package org.newdawn.slick.svg;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Set;
/*     */ import org.newdawn.slick.geom.Shape;
/*     */ 
/*     */ public class Diagram
/*     */ {
/*  13 */   private ArrayList figures = new ArrayList();
/*     */ 
/*  15 */   private HashMap patterns = new HashMap();
/*     */ 
/*  17 */   private HashMap gradients = new HashMap();
/*     */ 
/*  19 */   private HashMap figureMap = new HashMap();
/*     */   private float width;
/*     */   private float height;
/*     */ 
/*     */   public Diagram(float width, float height)
/*     */   {
/*  33 */     this.width = width;
/*  34 */     this.height = height;
/*     */   }
/*     */ 
/*     */   public float getWidth()
/*     */   {
/*  43 */     return this.width;
/*     */   }
/*     */ 
/*     */   public float getHeight()
/*     */   {
/*  52 */     return this.height;
/*     */   }
/*     */ 
/*     */   public void addPatternDef(String name, String href)
/*     */   {
/*  62 */     this.patterns.put(name, href);
/*     */   }
/*     */ 
/*     */   public void addGradient(String name, Gradient gradient)
/*     */   {
/*  72 */     this.gradients.put(name, gradient);
/*     */   }
/*     */ 
/*     */   public String getPatternDef(String name)
/*     */   {
/*  82 */     return (String)this.patterns.get(name);
/*     */   }
/*     */ 
/*     */   public Gradient getGradient(String name)
/*     */   {
/*  92 */     return (Gradient)this.gradients.get(name);
/*     */   }
/*     */ 
/*     */   public String[] getPatternDefNames()
/*     */   {
/* 101 */     return (String[])this.patterns.keySet().toArray(new String[0]);
/*     */   }
/*     */ 
/*     */   public Figure getFigureByID(String id)
/*     */   {
/* 111 */     return (Figure)this.figureMap.get(id);
/*     */   }
/*     */ 
/*     */   public void addFigure(Figure figure)
/*     */   {
/* 120 */     this.figures.add(figure);
/* 121 */     this.figureMap.put(figure.getData().getAttribute("id"), figure);
/*     */ 
/* 123 */     String fillRef = figure.getData().getAsReference("fill");
/* 124 */     Gradient gradient = getGradient(fillRef);
/* 125 */     if ((gradient != null) && 
/* 126 */       (gradient.isRadial()))
/* 127 */       for (int i = 0; i < InkscapeLoader.RADIAL_TRIANGULATION_LEVEL; i++)
/* 128 */         figure.getShape().increaseTriangulation();
/*     */   }
/*     */ 
/*     */   public int getFigureCount()
/*     */   {
/* 140 */     return this.figures.size();
/*     */   }
/*     */ 
/*     */   public Figure getFigure(int index)
/*     */   {
/* 150 */     return (Figure)this.figures.get(index);
/*     */   }
/*     */ 
/*     */   public void removeFigure(Figure figure)
/*     */   {
/* 159 */     this.figures.remove(figure);
/* 160 */     this.figureMap.remove(figure.getData().getAttribute("id"));
/*     */   }
/*     */ }

/* Location:           C:\Users\Raul\Desktop\StarMade\StarMade.jar
 * Qualified Name:     org.newdawn.slick.svg.Diagram
 * JD-Core Version:    0.6.2
 */