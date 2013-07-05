/*     */ package it.unimi.dsi.fastutil.longs;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ 
/*     */ public class Long2ShortFunctions
/*     */ {
/*  73 */   public static final EmptyFunction EMPTY_FUNCTION = new EmptyFunction();
/*     */ 
/*     */   public static Long2ShortFunction singleton(long key, short value)
/*     */   {
/* 109 */     return new Singleton(key, value);
/*     */   }
/*     */ 
/*     */   public static Long2ShortFunction singleton(Long key, Short value)
/*     */   {
/* 124 */     return new Singleton(key.longValue(), value.shortValue());
/*     */   }
/*     */ 
/*     */   public static Long2ShortFunction synchronize(Long2ShortFunction f)
/*     */   {
/* 187 */     return new SynchronizedFunction(f);
/*     */   }
/*     */ 
/*     */   public static Long2ShortFunction synchronize(Long2ShortFunction f, Object sync)
/*     */   {
/* 197 */     return new SynchronizedFunction(f, sync);
/*     */   }
/*     */ 
/*     */   public static Long2ShortFunction unmodifiable(Long2ShortFunction f)
/*     */   {
/* 244 */     return new UnmodifiableFunction(f);
/*     */   }
/*     */ 
/*     */   public static class UnmodifiableFunction extends AbstractLong2ShortFunction
/*     */     implements Serializable
/*     */   {
/*     */     public static final long serialVersionUID = -7046029254386353129L;
/*     */     protected final Long2ShortFunction function;
/*     */ 
/*     */     protected UnmodifiableFunction(Long2ShortFunction f)
/*     */     {
/* 210 */       if (f == null) throw new NullPointerException();
/* 211 */       this.function = f;
/*     */     }
/*     */     public int size() {
/* 214 */       return this.function.size(); } 
/* 215 */     public boolean containsKey(long k) { return this.function.containsKey(k); } 
/*     */     public short defaultReturnValue() {
/* 217 */       return this.function.defaultReturnValue(); } 
/* 218 */     public void defaultReturnValue(short defRetValue) { throw new UnsupportedOperationException(); } 
/*     */     public short put(long k, short v) {
/* 220 */       throw new UnsupportedOperationException();
/*     */     }
/* 222 */     public void clear() { throw new UnsupportedOperationException(); } 
/* 223 */     public String toString() { return this.function.toString(); }
/*     */ 
/*     */     public short remove(long k) {
/* 226 */       throw new UnsupportedOperationException(); } 
/* 227 */     public short get(long k) { return this.function.get(k); } 
/* 228 */     public boolean containsKey(Object ok) { return this.function.containsKey(ok); }
/*     */ 
/*     */   }
/*     */ 
/*     */   public static class SynchronizedFunction extends AbstractLong2ShortFunction
/*     */     implements Serializable
/*     */   {
/*     */     public static final long serialVersionUID = -7046029254386353129L;
/*     */     protected final Long2ShortFunction function;
/*     */     protected final Object sync;
/*     */ 
/*     */     protected SynchronizedFunction(Long2ShortFunction f, Object sync)
/*     */     {
/* 140 */       if (f == null) throw new NullPointerException();
/* 141 */       this.function = f;
/* 142 */       this.sync = sync;
/*     */     }
/*     */ 
/*     */     protected SynchronizedFunction(Long2ShortFunction f) {
/* 146 */       if (f == null) throw new NullPointerException();
/* 147 */       this.function = f;
/* 148 */       this.sync = this;
/*     */     }
/*     */     public int size() {
/* 151 */       synchronized (this.sync) { return this.function.size(); }  } 
/* 152 */     public boolean containsKey(long k) { synchronized (this.sync) { return this.function.containsKey(k); }  } 
/*     */     public short defaultReturnValue() {
/* 154 */       synchronized (this.sync) { return this.function.defaultReturnValue(); }  } 
/* 155 */     public void defaultReturnValue(short defRetValue) { synchronized (this.sync) { this.function.defaultReturnValue(defRetValue); }  } 
/*     */     public short put(long k, short v) {
/* 157 */       synchronized (this.sync) { return this.function.put(k, v); } 
/*     */     }
/* 159 */     public void clear() { synchronized (this.sync) { this.function.clear(); }  } 
/* 160 */     public String toString() { synchronized (this.sync) { return this.function.toString(); } }
/*     */ 
/*     */     public Short put(Long k, Short v) {
/* 163 */       synchronized (this.sync) { return (Short)this.function.put(k, v); }  } 
/* 164 */     public Short get(Object k) { synchronized (this.sync) { return (Short)this.function.get(k); }  } 
/* 165 */     public Short remove(Object k) { synchronized (this.sync) { return (Short)this.function.remove(k); } }
/*     */ 
/*     */     public short remove(long k)
/*     */     {
/* 169 */       synchronized (this.sync) { return this.function.remove(k); }  } 
/* 170 */     public short get(long k) { synchronized (this.sync) { return this.function.get(k); }  } 
/* 171 */     public boolean containsKey(Object ok) { synchronized (this.sync) { return this.function.containsKey(ok); }
/*     */ 
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class Singleton extends AbstractLong2ShortFunction
/*     */     implements Serializable, Cloneable
/*     */   {
/*     */     public static final long serialVersionUID = -7046029254386353129L;
/*     */     protected final long key;
/*     */     protected final short value;
/*     */ 
/*     */     protected Singleton(long key, short value)
/*     */     {
/*  86 */       this.key = key;
/*  87 */       this.value = value;
/*     */     }
/*     */     public boolean containsKey(long k) {
/*  90 */       return this.key == k;
/*     */     }
/*  92 */     public short get(long k) { if (this.key == k) return this.value; return this.defRetValue; } 
/*     */     public int size() {
/*  94 */       return 1;
/*     */     }
/*  96 */     public Object clone() { return this; }
/*     */ 
/*     */   }
/*     */ 
/*     */   public static class EmptyFunction extends AbstractLong2ShortFunction
/*     */     implements Serializable, Cloneable
/*     */   {
/*     */     public static final long serialVersionUID = -7046029254386353129L;
/*     */ 
/*     */     public short get(long k)
/*     */     {
/*  61 */       return 0; } 
/*  62 */     public boolean containsKey(long k) { return false; } 
/*  63 */     public short defaultReturnValue() { return 0; } 
/*  64 */     public void defaultReturnValue(short defRetValue) { throw new UnsupportedOperationException(); } 
/*  65 */     public Short get(Object k) { return null; } 
/*  66 */     public int size() { return 0; } 
/*     */     public void clear() {  } 
/*  68 */     private Object readResolve() { return Long2ShortFunctions.EMPTY_FUNCTION; } 
/*  69 */     public Object clone() { return Long2ShortFunctions.EMPTY_FUNCTION; }
/*     */ 
/*     */   }
/*     */ }

/* Location:           C:\Users\Raul\Desktop\StarMade\StarMade.jar
 * Qualified Name:     it.unimi.dsi.fastutil.longs.Long2ShortFunctions
 * JD-Core Version:    0.6.2
 */