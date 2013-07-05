/*     */ package org.newdawn.slick.font;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.newdawn.slick.SlickException;
/*     */ import org.newdawn.slick.font.effects.ConfigurableEffect;
/*     */ import org.newdawn.slick.font.effects.ConfigurableEffect.Value;
/*     */ import org.newdawn.slick.util.ResourceLoader;
/*     */ 
/*     */ public class HieroSettings
/*     */ {
/*  28 */   private int fontSize = 12;
/*     */ 
/*  30 */   private boolean bold = false;
/*     */ 
/*  32 */   private boolean italic = false;
/*     */   private int paddingTop;
/*     */   private int paddingLeft;
/*     */   private int paddingBottom;
/*     */   private int paddingRight;
/*     */   private int paddingAdvanceX;
/*     */   private int paddingAdvanceY;
/*  46 */   private int glyphPageWidth = 512;
/*     */ 
/*  48 */   private int glyphPageHeight = 512;
/*     */ 
/*  50 */   private final List effects = new ArrayList();
/*     */ 
/*     */   public HieroSettings()
/*     */   {
/*     */   }
/*     */ 
/*     */   public HieroSettings(String hieroFileRef)
/*     */     throws SlickException
/*     */   {
/*  65 */     this(ResourceLoader.getResourceAsStream(hieroFileRef));
/*     */   }
/*     */ 
/*     */   public HieroSettings(InputStream in)
/*     */     throws SlickException
/*     */   {
/*     */     try
/*     */     {
/*  76 */       BufferedReader reader = new BufferedReader(new InputStreamReader(in));
/*     */       while (true) {
/*  78 */         String line = reader.readLine();
/*  79 */         if (line == null) break;
/*  80 */         line = line.trim();
/*  81 */         if (line.length() != 0) {
/*  82 */           String[] pieces = line.split("=", 2);
/*  83 */           String name = pieces[0].trim();
/*  84 */           String value = pieces[1];
/*  85 */           if (name.equals("font.size")) {
/*  86 */             this.fontSize = Integer.parseInt(value);
/*  87 */           } else if (name.equals("font.bold")) {
/*  88 */             this.bold = Boolean.valueOf(value).booleanValue();
/*  89 */           } else if (name.equals("font.italic")) {
/*  90 */             this.italic = Boolean.valueOf(value).booleanValue();
/*  91 */           } else if (name.equals("pad.top")) {
/*  92 */             this.paddingTop = Integer.parseInt(value);
/*  93 */           } else if (name.equals("pad.right")) {
/*  94 */             this.paddingRight = Integer.parseInt(value);
/*  95 */           } else if (name.equals("pad.bottom")) {
/*  96 */             this.paddingBottom = Integer.parseInt(value);
/*  97 */           } else if (name.equals("pad.left")) {
/*  98 */             this.paddingLeft = Integer.parseInt(value);
/*  99 */           } else if (name.equals("pad.advance.x")) {
/* 100 */             this.paddingAdvanceX = Integer.parseInt(value);
/* 101 */           } else if (name.equals("pad.advance.y")) {
/* 102 */             this.paddingAdvanceY = Integer.parseInt(value);
/* 103 */           } else if (name.equals("glyph.page.width")) {
/* 104 */             this.glyphPageWidth = Integer.parseInt(value);
/* 105 */           } else if (name.equals("glyph.page.height")) {
/* 106 */             this.glyphPageHeight = Integer.parseInt(value);
/* 107 */           } else if (name.equals("effect.class")) {
/*     */             try {
/* 109 */               this.effects.add(Class.forName(value).newInstance());
/*     */             } catch (Exception ex) {
/* 111 */               throw new SlickException("Unable to create effect instance: " + value, ex);
/*     */             }
/* 113 */           } else if (name.startsWith("effect."))
/*     */           {
/* 115 */             name = name.substring(7);
/* 116 */             ConfigurableEffect effect = (ConfigurableEffect)this.effects.get(this.effects.size() - 1);
/* 117 */             List values = effect.getValues();
/* 118 */             for (Iterator iter = values.iterator(); iter.hasNext(); ) {
/* 119 */               ConfigurableEffect.Value effectValue = (ConfigurableEffect.Value)iter.next();
/* 120 */               if (effectValue.getName().equals(name)) {
/* 121 */                 effectValue.setString(value);
/* 122 */                 break;
/*     */               }
/*     */             }
/* 125 */             effect.setValues(values);
/*     */           }
/*     */         }
/*     */       }
/* 128 */       reader.close();
/*     */     } catch (Exception ex) {
/* 130 */       throw new SlickException("Unable to load Hiero font file", ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getPaddingTop()
/*     */   {
/* 140 */     return this.paddingTop;
/*     */   }
/*     */ 
/*     */   public void setPaddingTop(int paddingTop)
/*     */   {
/* 149 */     this.paddingTop = paddingTop;
/*     */   }
/*     */ 
/*     */   public int getPaddingLeft()
/*     */   {
/* 158 */     return this.paddingLeft;
/*     */   }
/*     */ 
/*     */   public void setPaddingLeft(int paddingLeft)
/*     */   {
/* 167 */     this.paddingLeft = paddingLeft;
/*     */   }
/*     */ 
/*     */   public int getPaddingBottom()
/*     */   {
/* 176 */     return this.paddingBottom;
/*     */   }
/*     */ 
/*     */   public void setPaddingBottom(int paddingBottom)
/*     */   {
/* 185 */     this.paddingBottom = paddingBottom;
/*     */   }
/*     */ 
/*     */   public int getPaddingRight()
/*     */   {
/* 194 */     return this.paddingRight;
/*     */   }
/*     */ 
/*     */   public void setPaddingRight(int paddingRight)
/*     */   {
/* 203 */     this.paddingRight = paddingRight;
/*     */   }
/*     */ 
/*     */   public int getPaddingAdvanceX()
/*     */   {
/* 212 */     return this.paddingAdvanceX;
/*     */   }
/*     */ 
/*     */   public void setPaddingAdvanceX(int paddingAdvanceX)
/*     */   {
/* 221 */     this.paddingAdvanceX = paddingAdvanceX;
/*     */   }
/*     */ 
/*     */   public int getPaddingAdvanceY()
/*     */   {
/* 230 */     return this.paddingAdvanceY;
/*     */   }
/*     */ 
/*     */   public void setPaddingAdvanceY(int paddingAdvanceY)
/*     */   {
/* 239 */     this.paddingAdvanceY = paddingAdvanceY;
/*     */   }
/*     */ 
/*     */   public int getGlyphPageWidth()
/*     */   {
/* 248 */     return this.glyphPageWidth;
/*     */   }
/*     */ 
/*     */   public void setGlyphPageWidth(int glyphPageWidth)
/*     */   {
/* 257 */     this.glyphPageWidth = glyphPageWidth;
/*     */   }
/*     */ 
/*     */   public int getGlyphPageHeight()
/*     */   {
/* 266 */     return this.glyphPageHeight;
/*     */   }
/*     */ 
/*     */   public void setGlyphPageHeight(int glyphPageHeight)
/*     */   {
/* 275 */     this.glyphPageHeight = glyphPageHeight;
/*     */   }
/*     */ 
/*     */   public int getFontSize()
/*     */   {
/* 285 */     return this.fontSize;
/*     */   }
/*     */ 
/*     */   public void setFontSize(int fontSize)
/*     */   {
/* 295 */     this.fontSize = fontSize;
/*     */   }
/*     */ 
/*     */   public boolean isBold()
/*     */   {
/* 305 */     return this.bold;
/*     */   }
/*     */ 
/*     */   public void setBold(boolean bold)
/*     */   {
/* 315 */     this.bold = bold;
/*     */   }
/*     */ 
/*     */   public boolean isItalic()
/*     */   {
/* 325 */     return this.italic;
/*     */   }
/*     */ 
/*     */   public void setItalic(boolean italic)
/*     */   {
/* 335 */     this.italic = italic;
/*     */   }
/*     */ 
/*     */   public List getEffects()
/*     */   {
/* 344 */     return this.effects;
/*     */   }
/*     */ 
/*     */   public void save(File file)
/*     */     throws IOException
/*     */   {
/* 354 */     PrintStream out = new PrintStream(new FileOutputStream(file));
/* 355 */     out.println("font.size=" + this.fontSize);
/* 356 */     out.println("font.bold=" + this.bold);
/* 357 */     out.println("font.italic=" + this.italic);
/* 358 */     out.println();
/* 359 */     out.println("pad.top=" + this.paddingTop);
/* 360 */     out.println("pad.right=" + this.paddingRight);
/* 361 */     out.println("pad.bottom=" + this.paddingBottom);
/* 362 */     out.println("pad.left=" + this.paddingLeft);
/* 363 */     out.println("pad.advance.x=" + this.paddingAdvanceX);
/* 364 */     out.println("pad.advance.y=" + this.paddingAdvanceY);
/* 365 */     out.println();
/* 366 */     out.println("glyph.page.width=" + this.glyphPageWidth);
/* 367 */     out.println("glyph.page.height=" + this.glyphPageHeight);
/* 368 */     out.println();
/* 369 */     for (Iterator iter = this.effects.iterator(); iter.hasNext(); ) {
/* 370 */       ConfigurableEffect effect = (ConfigurableEffect)iter.next();
/* 371 */       out.println("effect.class=" + effect.getClass().getName());
/* 372 */       for (Iterator iter2 = effect.getValues().iterator(); iter2.hasNext(); ) {
/* 373 */         ConfigurableEffect.Value value = (ConfigurableEffect.Value)iter2.next();
/* 374 */         out.println("effect." + value.getName() + "=" + value.getString());
/*     */       }
/* 376 */       out.println();
/*     */     }
/* 378 */     out.close();
/*     */   }
/*     */ }

/* Location:           C:\Users\Raul\Desktop\StarMade\StarMade.jar
 * Qualified Name:     org.newdawn.slick.font.HieroSettings
 * JD-Core Version:    0.6.2
 */