/*     */ package it.unimi.dsi.fastutil.floats;
/*     */ 
/*     */ import it.unimi.dsi.fastutil.HashCommon;
/*     */ import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
/*     */ import it.unimi.dsi.fastutil.doubles.AbstractDoubleIterator;
/*     */ import it.unimi.dsi.fastutil.doubles.DoubleCollection;
/*     */ import it.unimi.dsi.fastutil.doubles.DoubleIterator;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectIterator;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectSet;
/*     */ import java.io.Serializable;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ 
/*     */ public abstract class AbstractFloat2DoubleMap extends AbstractFloat2DoubleFunction
/*     */   implements Float2DoubleMap, Serializable
/*     */ {
/*     */   public static final long serialVersionUID = -4940583368468432370L;
/*     */ 
/*     */   public boolean containsValue(Object ov)
/*     */   {
/*  70 */     return containsValue(((Double)ov).doubleValue());
/*     */   }
/*     */ 
/*     */   public boolean containsValue(double v) {
/*  74 */     return values().contains(v);
/*     */   }
/*     */ 
/*     */   public boolean containsKey(float k) {
/*  78 */     return keySet().contains(k);
/*     */   }
/*     */ 
/*     */   public void putAll(Map<? extends Float, ? extends Double> m)
/*     */   {
/*  88 */     int n = m.size();
/*  89 */     Iterator i = m.entrySet().iterator();
/*  90 */     if ((m instanceof Serializable))
/*     */     {
/*  92 */       while (n-- != 0) {
/*  93 */         Float2DoubleMap.Entry e = (Float2DoubleMap.Entry)i.next();
/*  94 */         put(e.getFloatKey(), e.getDoubleValue());
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/*  99 */       while (n-- != 0) {
/* 100 */         Map.Entry e = (Map.Entry)i.next();
/* 101 */         put((Float)e.getKey(), (Double)e.getValue());
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/* 106 */   public boolean isEmpty() { return size() == 0; }
/*     */ 
/*     */ 
/*     */   public FloatSet keySet()
/*     */   {
/* 191 */     return new AbstractFloatSet() {
/*     */       public boolean contains(float k) {
/* 193 */         return AbstractFloat2DoubleMap.this.containsKey(k);
/*     */       }
/* 195 */       public int size() { return AbstractFloat2DoubleMap.this.size(); } 
/* 196 */       public void clear() { AbstractFloat2DoubleMap.this.clear(); }
/*     */ 
/*     */       public FloatIterator iterator() {
/* 199 */         return new AbstractFloatIterator() {
/* 200 */           final ObjectIterator<Map.Entry<Float, Double>> i = AbstractFloat2DoubleMap.this.entrySet().iterator();
/*     */ 
/* 202 */           public float nextFloat() { return ((Float2DoubleMap.Entry)this.i.next()).getFloatKey(); } 
/*     */           public boolean hasNext() {
/* 204 */             return this.i.hasNext();
/*     */           }
/*     */         };
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public DoubleCollection values()
/*     */   {
/* 224 */     return new AbstractDoubleCollection() {
/*     */       public boolean contains(double k) {
/* 226 */         return AbstractFloat2DoubleMap.this.containsValue(k);
/*     */       }
/* 228 */       public int size() { return AbstractFloat2DoubleMap.this.size(); } 
/* 229 */       public void clear() { AbstractFloat2DoubleMap.this.clear(); }
/*     */ 
/*     */       public DoubleIterator iterator() {
/* 232 */         return new AbstractDoubleIterator() {
/* 233 */           final ObjectIterator<Map.Entry<Float, Double>> i = AbstractFloat2DoubleMap.this.entrySet().iterator();
/*     */ 
/* 235 */           public double nextDouble() { return ((Float2DoubleMap.Entry)this.i.next()).getDoubleValue(); } 
/*     */           public boolean hasNext() {
/* 237 */             return this.i.hasNext();
/*     */           }
/*     */         };
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public ObjectSet<Map.Entry<Float, Double>> entrySet()
/*     */   {
/* 246 */     return float2DoubleEntrySet();
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 259 */     int h = 0; int n = size();
/* 260 */     ObjectIterator i = entrySet().iterator();
/*     */ 
/* 262 */     while (n-- != 0) h += ((Map.Entry)i.next()).hashCode();
/* 263 */     return h;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o) {
/* 267 */     if (o == this) return true;
/* 268 */     if (!(o instanceof Map)) return false;
/*     */ 
/* 270 */     Map m = (Map)o;
/* 271 */     if (m.size() != size()) return false;
/* 272 */     return entrySet().containsAll(m.entrySet());
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 277 */     StringBuilder s = new StringBuilder();
/* 278 */     ObjectIterator i = entrySet().iterator();
/* 279 */     int n = size();
/*     */ 
/* 281 */     boolean first = true;
/*     */ 
/* 283 */     s.append("{");
/*     */ 
/* 285 */     while (n-- != 0) {
/* 286 */       if (first) first = false; else {
/* 287 */         s.append(", ");
/*     */       }
/* 289 */       Float2DoubleMap.Entry e = (Float2DoubleMap.Entry)i.next();
/*     */ 
/* 294 */       s.append(String.valueOf(e.getFloatKey()));
/* 295 */       s.append("=>");
/*     */ 
/* 299 */       s.append(String.valueOf(e.getDoubleValue()));
/*     */     }
/*     */ 
/* 302 */     s.append("}");
/* 303 */     return s.toString();
/*     */   }
/*     */ 
/*     */   public static class BasicEntry
/*     */     implements Float2DoubleMap.Entry
/*     */   {
/*     */     protected float key;
/*     */     protected double value;
/*     */ 
/*     */     public BasicEntry(Float key, Double value)
/*     */     {
/* 118 */       this.key = key.floatValue();
/* 119 */       this.value = value.doubleValue();
/*     */     }
/*     */     public BasicEntry(float key, double value) {
/* 122 */       this.key = key;
/* 123 */       this.value = value;
/*     */     }
/*     */ 
/*     */     public Float getKey()
/*     */     {
/* 128 */       return Float.valueOf(this.key);
/*     */     }
/*     */ 
/*     */     public float getFloatKey()
/*     */     {
/* 133 */       return this.key;
/*     */     }
/*     */ 
/*     */     public Double getValue()
/*     */     {
/* 138 */       return Double.valueOf(this.value);
/*     */     }
/*     */ 
/*     */     public double getDoubleValue()
/*     */     {
/* 143 */       return this.value;
/*     */     }
/*     */ 
/*     */     public double setValue(double value)
/*     */     {
/* 148 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     public Double setValue(Double value)
/*     */     {
/* 154 */       return Double.valueOf(setValue(value.doubleValue()));
/*     */     }
/*     */ 
/*     */     public boolean equals(Object o)
/*     */     {
/* 160 */       if (!(o instanceof Map.Entry)) return false;
/* 161 */       Map.Entry e = (Map.Entry)o;
/*     */ 
/* 163 */       return (this.key == ((Float)e.getKey()).floatValue()) && (this.value == ((Double)e.getValue()).doubleValue());
/*     */     }
/*     */ 
/*     */     public int hashCode() {
/* 167 */       return HashCommon.float2int(this.key) ^ HashCommon.double2int(this.value);
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 172 */       return this.key + "->" + this.value;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Raul\Desktop\StarMade\StarMade.jar
 * Qualified Name:     it.unimi.dsi.fastutil.floats.AbstractFloat2DoubleMap
 * JD-Core Version:    0.6.2
 */