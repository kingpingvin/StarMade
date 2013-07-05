/*     */ package it.unimi.dsi.fastutil.doubles;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ 
/*     */ public class Double2BooleanFunctions
/*     */ {
/*  73 */   public static final EmptyFunction EMPTY_FUNCTION = new EmptyFunction();
/*     */ 
/*     */   public static Double2BooleanFunction singleton(double key, boolean value)
/*     */   {
/* 109 */     return new Singleton(key, value);
/*     */   }
/*     */ 
/*     */   public static Double2BooleanFunction singleton(Double key, Boolean value)
/*     */   {
/* 124 */     return new Singleton(key.doubleValue(), value.booleanValue());
/*     */   }
/*     */ 
/*     */   public static Double2BooleanFunction synchronize(Double2BooleanFunction f)
/*     */   {
/* 187 */     return new SynchronizedFunction(f);
/*     */   }
/*     */ 
/*     */   public static Double2BooleanFunction synchronize(Double2BooleanFunction f, Object sync)
/*     */   {
/* 197 */     return new SynchronizedFunction(f, sync);
/*     */   }
/*     */ 
/*     */   public static Double2BooleanFunction unmodifiable(Double2BooleanFunction f)
/*     */   {
/* 244 */     return new UnmodifiableFunction(f);
/*     */   }
/*     */ 
/*     */   public static class UnmodifiableFunction extends AbstractDouble2BooleanFunction
/*     */     implements Serializable
/*     */   {
/*     */     public static final long serialVersionUID = -7046029254386353129L;
/*     */     protected final Double2BooleanFunction function;
/*     */ 
/*     */     protected UnmodifiableFunction(Double2BooleanFunction f)
/*     */     {
/* 210 */       if (f == null) throw new NullPointerException();
/* 211 */       this.function = f;
/*     */     }
/*     */     public int size() {
/* 214 */       return this.function.size(); } 
/* 215 */     public boolean containsKey(double k) { return this.function.containsKey(k); } 
/*     */     public boolean defaultReturnValue() {
/* 217 */       return this.function.defaultReturnValue(); } 
/* 218 */     public void defaultReturnValue(boolean defRetValue) { throw new UnsupportedOperationException(); } 
/*     */     public boolean put(double k, boolean v) {
/* 220 */       throw new UnsupportedOperationException();
/*     */     }
/* 222 */     public void clear() { throw new UnsupportedOperationException(); } 
/* 223 */     public String toString() { return this.function.toString(); }
/*     */ 
/*     */     public boolean remove(double k) {
/* 226 */       throw new UnsupportedOperationException(); } 
/* 227 */     public boolean get(double k) { return this.function.get(k); } 
/* 228 */     public boolean containsKey(Object ok) { return this.function.containsKey(ok); }
/*     */ 
/*     */   }
/*     */ 
/*     */   public static class SynchronizedFunction extends AbstractDouble2BooleanFunction
/*     */     implements Serializable
/*     */   {
/*     */     public static final long serialVersionUID = -7046029254386353129L;
/*     */     protected final Double2BooleanFunction function;
/*     */     protected final Object sync;
/*     */ 
/*     */     protected SynchronizedFunction(Double2BooleanFunction f, Object sync)
/*     */     {
/* 140 */       if (f == null) throw new NullPointerException();
/* 141 */       this.function = f;
/* 142 */       this.sync = sync;
/*     */     }
/*     */ 
/*     */     protected SynchronizedFunction(Double2BooleanFunction f) {
/* 146 */       if (f == null) throw new NullPointerException();
/* 147 */       this.function = f;
/* 148 */       this.sync = this;
/*     */     }
/*     */     public int size() {
/* 151 */       synchronized (this.sync) { return this.function.size(); }  } 
/* 152 */     public boolean containsKey(double k) { synchronized (this.sync) { return this.function.containsKey(k); }  } 
/*     */     public boolean defaultReturnValue() {
/* 154 */       synchronized (this.sync) { return this.function.defaultReturnValue(); }  } 
/* 155 */     public void defaultReturnValue(boolean defRetValue) { synchronized (this.sync) { this.function.defaultReturnValue(defRetValue); }  } 
/*     */     public boolean put(double k, boolean v) {
/* 157 */       synchronized (this.sync) { return this.function.put(k, v); } 
/*     */     }
/* 159 */     public void clear() { synchronized (this.sync) { this.function.clear(); }  } 
/* 160 */     public String toString() { synchronized (this.sync) { return this.function.toString(); } }
/*     */ 
/*     */     public Boolean put(Double k, Boolean v) {
/* 163 */       synchronized (this.sync) { return (Boolean)this.function.put(k, v); }  } 
/* 164 */     public Boolean get(Object k) { synchronized (this.sync) { return (Boolean)this.function.get(k); }  } 
/* 165 */     public Boolean remove(Object k) { synchronized (this.sync) { return (Boolean)this.function.remove(k); } }
/*     */ 
/*     */     public boolean remove(double k)
/*     */     {
/* 169 */       synchronized (this.sync) { return this.function.remove(k); }  } 
/* 170 */     public boolean get(double k) { synchronized (this.sync) { return this.function.get(k); }  } 
/* 171 */     public boolean containsKey(Object ok) { synchronized (this.sync) { return this.function.containsKey(ok); }
/*     */ 
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class Singleton extends AbstractDouble2BooleanFunction
/*     */     implements Serializable, Cloneable
/*     */   {
/*     */     public static final long serialVersionUID = -7046029254386353129L;
/*     */     protected final double key;
/*     */     protected final boolean value;
/*     */ 
/*     */     protected Singleton(double key, boolean value)
/*     */     {
/*  86 */       this.key = key;
/*  87 */       this.value = value;
/*     */     }
/*     */     public boolean containsKey(double k) {
/*  90 */       return this.key == k;
/*     */     }
/*  92 */     public boolean get(double k) { if (this.key == k) return this.value; return this.defRetValue; } 
/*     */     public int size() {
/*  94 */       return 1;
/*     */     }
/*  96 */     public Object clone() { return this; }
/*     */ 
/*     */   }
/*     */ 
/*     */   public static class EmptyFunction extends AbstractDouble2BooleanFunction
/*     */     implements Serializable, Cloneable
/*     */   {
/*     */     public static final long serialVersionUID = -7046029254386353129L;
/*     */ 
/*     */     public boolean get(double k)
/*     */     {
/*  61 */       return false; } 
/*  62 */     public boolean containsKey(double k) { return false; } 
/*  63 */     public boolean defaultReturnValue() { return false; } 
/*  64 */     public void defaultReturnValue(boolean defRetValue) { throw new UnsupportedOperationException(); } 
/*  65 */     public Boolean get(Object k) { return null; } 
/*  66 */     public int size() { return 0; } 
/*     */     public void clear() {  } 
/*  68 */     private Object readResolve() { return Double2BooleanFunctions.EMPTY_FUNCTION; } 
/*  69 */     public Object clone() { return Double2BooleanFunctions.EMPTY_FUNCTION; }
/*     */ 
/*     */   }
/*     */ }

/* Location:           C:\Users\Raul\Desktop\StarMade\StarMade.jar
 * Qualified Name:     it.unimi.dsi.fastutil.doubles.Double2BooleanFunctions
 * JD-Core Version:    0.6.2
 */