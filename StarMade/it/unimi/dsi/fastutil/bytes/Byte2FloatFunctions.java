/*     */ package it.unimi.dsi.fastutil.bytes;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ 
/*     */ public class Byte2FloatFunctions
/*     */ {
/*  73 */   public static final EmptyFunction EMPTY_FUNCTION = new EmptyFunction();
/*     */ 
/*     */   public static Byte2FloatFunction singleton(byte key, float value)
/*     */   {
/* 109 */     return new Singleton(key, value);
/*     */   }
/*     */ 
/*     */   public static Byte2FloatFunction singleton(Byte key, Float value)
/*     */   {
/* 124 */     return new Singleton(key.byteValue(), value.floatValue());
/*     */   }
/*     */ 
/*     */   public static Byte2FloatFunction synchronize(Byte2FloatFunction f)
/*     */   {
/* 187 */     return new SynchronizedFunction(f);
/*     */   }
/*     */ 
/*     */   public static Byte2FloatFunction synchronize(Byte2FloatFunction f, Object sync)
/*     */   {
/* 197 */     return new SynchronizedFunction(f, sync);
/*     */   }
/*     */ 
/*     */   public static Byte2FloatFunction unmodifiable(Byte2FloatFunction f)
/*     */   {
/* 244 */     return new UnmodifiableFunction(f);
/*     */   }
/*     */ 
/*     */   public static class UnmodifiableFunction extends AbstractByte2FloatFunction
/*     */     implements Serializable
/*     */   {
/*     */     public static final long serialVersionUID = -7046029254386353129L;
/*     */     protected final Byte2FloatFunction function;
/*     */ 
/*     */     protected UnmodifiableFunction(Byte2FloatFunction f)
/*     */     {
/* 210 */       if (f == null) throw new NullPointerException();
/* 211 */       this.function = f;
/*     */     }
/*     */     public int size() {
/* 214 */       return this.function.size(); } 
/* 215 */     public boolean containsKey(byte k) { return this.function.containsKey(k); } 
/*     */     public float defaultReturnValue() {
/* 217 */       return this.function.defaultReturnValue(); } 
/* 218 */     public void defaultReturnValue(float defRetValue) { throw new UnsupportedOperationException(); } 
/*     */     public float put(byte k, float v) {
/* 220 */       throw new UnsupportedOperationException();
/*     */     }
/* 222 */     public void clear() { throw new UnsupportedOperationException(); } 
/* 223 */     public String toString() { return this.function.toString(); }
/*     */ 
/*     */     public float remove(byte k) {
/* 226 */       throw new UnsupportedOperationException(); } 
/* 227 */     public float get(byte k) { return this.function.get(k); } 
/* 228 */     public boolean containsKey(Object ok) { return this.function.containsKey(ok); }
/*     */ 
/*     */   }
/*     */ 
/*     */   public static class SynchronizedFunction extends AbstractByte2FloatFunction
/*     */     implements Serializable
/*     */   {
/*     */     public static final long serialVersionUID = -7046029254386353129L;
/*     */     protected final Byte2FloatFunction function;
/*     */     protected final Object sync;
/*     */ 
/*     */     protected SynchronizedFunction(Byte2FloatFunction f, Object sync)
/*     */     {
/* 140 */       if (f == null) throw new NullPointerException();
/* 141 */       this.function = f;
/* 142 */       this.sync = sync;
/*     */     }
/*     */ 
/*     */     protected SynchronizedFunction(Byte2FloatFunction f) {
/* 146 */       if (f == null) throw new NullPointerException();
/* 147 */       this.function = f;
/* 148 */       this.sync = this;
/*     */     }
/*     */     public int size() {
/* 151 */       synchronized (this.sync) { return this.function.size(); }  } 
/* 152 */     public boolean containsKey(byte k) { synchronized (this.sync) { return this.function.containsKey(k); }  } 
/*     */     public float defaultReturnValue() {
/* 154 */       synchronized (this.sync) { return this.function.defaultReturnValue(); }  } 
/* 155 */     public void defaultReturnValue(float defRetValue) { synchronized (this.sync) { this.function.defaultReturnValue(defRetValue); }  } 
/*     */     public float put(byte k, float v) {
/* 157 */       synchronized (this.sync) { return this.function.put(k, v); } 
/*     */     }
/* 159 */     public void clear() { synchronized (this.sync) { this.function.clear(); }  } 
/* 160 */     public String toString() { synchronized (this.sync) { return this.function.toString(); } }
/*     */ 
/*     */     public Float put(Byte k, Float v) {
/* 163 */       synchronized (this.sync) { return (Float)this.function.put(k, v); }  } 
/* 164 */     public Float get(Object k) { synchronized (this.sync) { return (Float)this.function.get(k); }  } 
/* 165 */     public Float remove(Object k) { synchronized (this.sync) { return (Float)this.function.remove(k); } }
/*     */ 
/*     */     public float remove(byte k)
/*     */     {
/* 169 */       synchronized (this.sync) { return this.function.remove(k); }  } 
/* 170 */     public float get(byte k) { synchronized (this.sync) { return this.function.get(k); }  } 
/* 171 */     public boolean containsKey(Object ok) { synchronized (this.sync) { return this.function.containsKey(ok); }
/*     */ 
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class Singleton extends AbstractByte2FloatFunction
/*     */     implements Serializable, Cloneable
/*     */   {
/*     */     public static final long serialVersionUID = -7046029254386353129L;
/*     */     protected final byte key;
/*     */     protected final float value;
/*     */ 
/*     */     protected Singleton(byte key, float value)
/*     */     {
/*  86 */       this.key = key;
/*  87 */       this.value = value;
/*     */     }
/*     */     public boolean containsKey(byte k) {
/*  90 */       return this.key == k;
/*     */     }
/*  92 */     public float get(byte k) { if (this.key == k) return this.value; return this.defRetValue; } 
/*     */     public int size() {
/*  94 */       return 1;
/*     */     }
/*  96 */     public Object clone() { return this; }
/*     */ 
/*     */   }
/*     */ 
/*     */   public static class EmptyFunction extends AbstractByte2FloatFunction
/*     */     implements Serializable, Cloneable
/*     */   {
/*     */     public static final long serialVersionUID = -7046029254386353129L;
/*     */ 
/*     */     public float get(byte k)
/*     */     {
/*  61 */       return 0.0F; } 
/*  62 */     public boolean containsKey(byte k) { return false; } 
/*  63 */     public float defaultReturnValue() { return 0.0F; } 
/*  64 */     public void defaultReturnValue(float defRetValue) { throw new UnsupportedOperationException(); } 
/*  65 */     public Float get(Object k) { return null; } 
/*  66 */     public int size() { return 0; } 
/*     */     public void clear() {  } 
/*  68 */     private Object readResolve() { return Byte2FloatFunctions.EMPTY_FUNCTION; } 
/*  69 */     public Object clone() { return Byte2FloatFunctions.EMPTY_FUNCTION; }
/*     */ 
/*     */   }
/*     */ }

/* Location:           C:\Users\Raul\Desktop\StarMade\StarMade.jar
 * Qualified Name:     it.unimi.dsi.fastutil.bytes.Byte2FloatFunctions
 * JD-Core Version:    0.6.2
 */