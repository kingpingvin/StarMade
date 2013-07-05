/*     */ package it.unimi.dsi.fastutil.floats;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ 
/*     */ public abstract class AbstractFloat2IntFunction
/*     */   implements Float2IntFunction, Serializable
/*     */ {
/*     */   public static final long serialVersionUID = -4940583368468432370L;
/*     */   protected int defRetValue;
/*     */ 
/*     */   public void defaultReturnValue(int rv)
/*     */   {
/*  72 */     this.defRetValue = rv;
/*     */   }
/*     */   public int defaultReturnValue() {
/*  75 */     return this.defRetValue;
/*     */   }
/*     */   public int put(float key, int value) {
/*  78 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   public int remove(float key) {
/*  81 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   public void clear() {
/*  84 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   public boolean containsKey(Object ok) {
/*  87 */     return containsKey(((Float)ok).floatValue());
/*     */   }
/*     */ 
/*     */   public Integer get(Object ok)
/*     */   {
/*  95 */     float k = ((Float)ok).floatValue();
/*  96 */     return containsKey(k) ? Integer.valueOf(get(k)) : null;
/*     */   }
/*     */ 
/*     */   public Integer put(Float ok, Integer ov)
/*     */   {
/* 104 */     float k = ok.floatValue();
/* 105 */     boolean containsKey = containsKey(k);
/* 106 */     int v = put(k, ov.intValue());
/* 107 */     return containsKey ? Integer.valueOf(v) : null;
/*     */   }
/*     */ 
/*     */   public Integer remove(Object ok)
/*     */   {
/* 115 */     float k = ((Float)ok).floatValue();
/* 116 */     boolean containsKey = containsKey(k);
/* 117 */     int v = remove(k);
/* 118 */     return containsKey ? Integer.valueOf(v) : null;
/*     */   }
/*     */ }

/* Location:           C:\Users\Raul\Desktop\StarMade\StarMade.jar
 * Qualified Name:     it.unimi.dsi.fastutil.floats.AbstractFloat2IntFunction
 * JD-Core Version:    0.6.2
 */