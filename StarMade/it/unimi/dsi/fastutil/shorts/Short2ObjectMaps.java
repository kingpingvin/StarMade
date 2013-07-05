/*     */ package it.unimi.dsi.fastutil.shorts;
/*     */ 
/*     */ import it.unimi.dsi.fastutil.objects.ObjectCollection;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectCollections;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectIterator;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectSet;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectSets;
/*     */ import java.io.Serializable;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class Short2ObjectMaps
/*     */ {
/*  98 */   public static final EmptyMap EMPTY_MAP = new EmptyMap();
/*     */ 
/*     */   public static <V> Short2ObjectMap<V> singleton(short key, V value)
/*     */   {
/* 185 */     return new Singleton(key, value);
/*     */   }
/*     */ 
/*     */   public static <V> Short2ObjectMap<V> singleton(Short key, V value)
/*     */   {
/* 200 */     return new Singleton(key.shortValue(), value);
/*     */   }
/*     */ 
/*     */   public static <V> Short2ObjectMap<V> synchronize(Short2ObjectMap<V> m)
/*     */   {
/* 266 */     return new SynchronizedMap(m);
/*     */   }
/*     */ 
/*     */   public static <V> Short2ObjectMap<V> synchronize(Short2ObjectMap<V> m, Object sync)
/*     */   {
/* 274 */     return new SynchronizedMap(m, sync);
/*     */   }
/*     */ 
/*     */   public static <V> Short2ObjectMap<V> unmodifiable(Short2ObjectMap<V> m)
/*     */   {
/* 313 */     return new UnmodifiableMap(m);
/*     */   }
/*     */ 
/*     */   public static class UnmodifiableMap<V> extends Short2ObjectFunctions.UnmodifiableFunction<V>
/*     */     implements Short2ObjectMap<V>, Serializable
/*     */   {
/*     */     public static final long serialVersionUID = -7046029254386353129L;
/*     */     protected final Short2ObjectMap<V> map;
/*     */     protected volatile transient ObjectSet<Short2ObjectMap.Entry<V>> entries;
/*     */     protected volatile transient ShortSet keys;
/*     */     protected volatile transient ObjectCollection<V> values;
/*     */ 
/*     */     protected UnmodifiableMap(Short2ObjectMap<V> m)
/*     */     {
/* 283 */       super();
/* 284 */       this.map = m;
/*     */     }
/* 286 */     public int size() { return this.map.size(); } 
/* 287 */     public boolean containsKey(short k) { return this.map.containsKey(k); } 
/* 288 */     public boolean containsValue(Object v) { return this.map.containsValue(v); } 
/* 289 */     public V defaultReturnValue() { throw new UnsupportedOperationException(); } 
/* 290 */     public void defaultReturnValue(V defRetValue) { throw new UnsupportedOperationException(); } 
/* 291 */     public V put(short k, V v) { throw new UnsupportedOperationException(); } 
/*     */     public void putAll(Map<? extends Short, ? extends V> m) {
/* 293 */       throw new UnsupportedOperationException(); } 
/* 294 */     public ObjectSet<Short2ObjectMap.Entry<V>> short2ObjectEntrySet() { if (this.entries == null) this.entries = ObjectSets.unmodifiable(this.map.short2ObjectEntrySet()); return this.entries; } 
/* 295 */     public ShortSet keySet() { if (this.keys == null) this.keys = ShortSets.unmodifiable(this.map.keySet()); return this.keys; } 
/* 296 */     public ObjectCollection<V> values() { if (this.values == null) return ObjectCollections.unmodifiable(this.map.values()); return this.values; } 
/* 297 */     public void clear() { throw new UnsupportedOperationException(); } 
/* 298 */     public String toString() { return this.map.toString(); } 
/* 299 */     public V remove(short k) { throw new UnsupportedOperationException(); } 
/* 300 */     public V get(short k) { return this.map.get(k); } 
/* 301 */     public boolean containsKey(Object ok) { return this.map.containsKey(ok); } 
/* 302 */     public V remove(Object k) { throw new UnsupportedOperationException(); } 
/* 303 */     public V get(Object k) { return this.map.get(k); } 
/* 304 */     public boolean isEmpty() { return this.map.isEmpty(); } 
/* 305 */     public ObjectSet<Map.Entry<Short, V>> entrySet() { return ObjectSets.unmodifiable(this.map.entrySet()); }
/*     */ 
/*     */   }
/*     */ 
/*     */   public static class SynchronizedMap<V> extends Short2ObjectFunctions.SynchronizedFunction<V>
/*     */     implements Short2ObjectMap<V>, Serializable
/*     */   {
/*     */     public static final long serialVersionUID = -7046029254386353129L;
/*     */     protected final Short2ObjectMap<V> map;
/*     */     protected volatile transient ObjectSet<Short2ObjectMap.Entry<V>> entries;
/*     */     protected volatile transient ShortSet keys;
/*     */     protected volatile transient ObjectCollection<V> values;
/*     */ 
/*     */     protected SynchronizedMap(Short2ObjectMap<V> m, Object sync)
/*     */     {
/* 219 */       super(sync);
/* 220 */       this.map = m;
/*     */     }
/*     */ 
/*     */     protected SynchronizedMap(Short2ObjectMap<V> m) {
/* 224 */       super();
/* 225 */       this.map = m;
/*     */     }
/*     */     public int size() {
/* 228 */       synchronized (this.sync) { return this.map.size(); }  } 
/* 229 */     public boolean containsKey(short k) { synchronized (this.sync) { return this.map.containsKey(k); }  } 
/* 230 */     public boolean containsValue(Object v) { synchronized (this.sync) { return this.map.containsValue(v); }  } 
/*     */     public V defaultReturnValue() {
/* 232 */       synchronized (this.sync) { return this.map.defaultReturnValue(); }  } 
/* 233 */     public void defaultReturnValue(V defRetValue) { synchronized (this.sync) { this.map.defaultReturnValue(defRetValue); }  } 
/*     */     public V put(short k, V v) {
/* 235 */       synchronized (this.sync) { return this.map.put(k, v); } 
/*     */     }
/*     */ 
/* 238 */     public void putAll(Map<? extends Short, ? extends V> m) { synchronized (this.sync) { this.map.putAll(m); }  } 
/*     */     public ObjectSet<Short2ObjectMap.Entry<V>> short2ObjectEntrySet() {
/* 240 */       if (this.entries == null) this.entries = ObjectSets.synchronize(this.map.short2ObjectEntrySet(), this.sync); return this.entries; } 
/* 241 */     public ShortSet keySet() { if (this.keys == null) this.keys = ShortSets.synchronize(this.map.keySet(), this.sync); return this.keys; } 
/* 242 */     public ObjectCollection<V> values() { if (this.values == null) return ObjectCollections.synchronize(this.map.values(), this.sync); return this.values; } 
/*     */     public void clear() {
/* 244 */       synchronized (this.sync) { this.map.clear(); }  } 
/* 245 */     public String toString() { synchronized (this.sync) { return this.map.toString(); } }
/*     */ 
/*     */     public V put(Short k, V v) {
/* 248 */       synchronized (this.sync) { return this.map.put(k, v); }
/*     */     }
/*     */ 
/*     */     public V remove(short k) {
/* 252 */       synchronized (this.sync) { return this.map.remove(k); }  } 
/* 253 */     public V get(short k) { synchronized (this.sync) { return this.map.get(k); }  } 
/* 254 */     public boolean containsKey(Object ok) { synchronized (this.sync) { return this.map.containsKey(ok); }  } 
/* 255 */     public boolean isEmpty() { synchronized (this.sync) { return this.map.isEmpty(); }  } 
/* 256 */     public ObjectSet<Map.Entry<Short, V>> entrySet() { synchronized (this.sync) { return this.map.entrySet(); }  } 
/* 257 */     public int hashCode() { synchronized (this.sync) { return this.map.hashCode(); }  } 
/* 258 */     public boolean equals(Object o) { synchronized (this.sync) { return this.map.equals(o); }
/*     */ 
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class Singleton<V> extends Short2ObjectFunctions.Singleton<V>
/*     */     implements Short2ObjectMap<V>, Serializable, Cloneable
/*     */   {
/*     */     public static final long serialVersionUID = -7046029254386353129L;
/*     */     protected volatile transient ObjectSet<Short2ObjectMap.Entry<V>> entries;
/*     */     protected volatile transient ShortSet keys;
/*     */     protected volatile transient ObjectCollection<V> values;
/*     */ 
/*     */     protected Singleton(short key, V value)
/*     */     {
/* 116 */       super(value);
/*     */     }
/*     */     public boolean containsValue(Object v) {
/* 119 */       return this.value == null ? false : v == null ? true : this.value.equals(v);
/*     */     }
/*     */ 
/*     */     public void putAll(Map<? extends Short, ? extends V> m)
/*     */     {
/* 124 */       throw new UnsupportedOperationException();
/*     */     }
/* 126 */     public ObjectSet<Short2ObjectMap.Entry<V>> short2ObjectEntrySet() { if (this.entries == null) this.entries = ObjectSets.singleton(new SingletonEntry()); return this.entries; } 
/* 127 */     public ShortSet keySet() { if (this.keys == null) this.keys = ShortSets.singleton(this.key); return this.keys; } 
/* 128 */     public ObjectCollection<V> values() { if (this.values == null) this.values = ObjectSets.singleton(this.value); return this.values;
/*     */     }
/*     */ 
/*     */     public boolean isEmpty()
/*     */     {
/* 156 */       return false;
/*     */     }
/*     */     public ObjectSet<Map.Entry<Short, V>> entrySet() {
/* 159 */       return short2ObjectEntrySet();
/*     */     }
/* 161 */     public int hashCode() { return this.key ^ (this.value == null ? 0 : this.value.hashCode()); }
/*     */ 
/*     */     public boolean equals(Object o) {
/* 164 */       if (o == this) return true;
/* 165 */       if (!(o instanceof Map)) return false;
/*     */ 
/* 167 */       Map m = (Map)o;
/* 168 */       if (m.size() != 1) return false;
/* 169 */       return ((Map.Entry)entrySet().iterator().next()).equals(m.entrySet().iterator().next());
/*     */     }
/*     */     public String toString() {
/* 172 */       return "{" + this.key + "=>" + this.value + "}";
/*     */     }
/*     */ 
/*     */     protected class SingletonEntry
/*     */       implements Short2ObjectMap.Entry<V>, Map.Entry<Short, V>
/*     */     {
/*     */       protected SingletonEntry()
/*     */       {
/*     */       }
/*     */ 
/*     */       public Short getKey()
/*     */       {
/* 131 */         return Short.valueOf(Short2ObjectMaps.Singleton.this.key); } 
/* 132 */       public V getValue() { return Short2ObjectMaps.Singleton.this.value; }
/*     */ 
/*     */       public short getShortKey() {
/* 135 */         return Short2ObjectMaps.Singleton.this.key;
/*     */       }
/*     */ 
/*     */       public V setValue(V value)
/*     */       {
/* 143 */         throw new UnsupportedOperationException();
/*     */       }
/*     */       public boolean equals(Object o) {
/* 146 */         if (!(o instanceof Map.Entry)) return false;
/* 147 */         Map.Entry e = (Map.Entry)o;
/*     */ 
/* 149 */         return (Short2ObjectMaps.Singleton.this.key == ((Short)e.getKey()).shortValue()) && (Short2ObjectMaps.Singleton.this.value == null ? e.getValue() == null : Short2ObjectMaps.Singleton.this.value.equals(e.getValue()));
/*     */       }
/*     */       public int hashCode() {
/* 152 */         return Short2ObjectMaps.Singleton.this.key ^ (Short2ObjectMaps.Singleton.this.value == null ? 0 : Short2ObjectMaps.Singleton.this.value.hashCode()); } 
/* 153 */       public String toString() { return Short2ObjectMaps.Singleton.this.key + "->" + Short2ObjectMaps.Singleton.this.value; }
/*     */ 
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class EmptyMap<V> extends Short2ObjectFunctions.EmptyFunction<V>
/*     */     implements Short2ObjectMap<V>, Serializable, Cloneable
/*     */   {
/*     */     public static final long serialVersionUID = -7046029254386353129L;
/*     */ 
/*     */     public boolean containsValue(Object v)
/*     */     {
/*  65 */       return false; } 
/*  66 */     public void putAll(Map<? extends Short, ? extends V> m) { throw new UnsupportedOperationException(); } 
/*     */     public ObjectSet<Short2ObjectMap.Entry<V>> short2ObjectEntrySet() {
/*  68 */       return ObjectSets.EMPTY_SET;
/*     */     }
/*  70 */     public ShortSet keySet() { return ShortSets.EMPTY_SET; } 
/*     */     public ObjectCollection<V> values() {
/*  72 */       return ObjectSets.EMPTY_SET; } 
/*  73 */     private Object readResolve() { return Short2ObjectMaps.EMPTY_MAP; } 
/*     */     public Object clone() {
/*  75 */       return Short2ObjectMaps.EMPTY_MAP;
/*     */     }
/*  77 */     public boolean isEmpty() { return true; }
/*     */ 
/*     */     public ObjectSet<Map.Entry<Short, V>> entrySet() {
/*  80 */       return short2ObjectEntrySet();
/*     */     }
/*  82 */     public int hashCode() { return 0; }
/*     */ 
/*     */     public boolean equals(Object o) {
/*  85 */       if (!(o instanceof Map)) return false;
/*     */ 
/*  87 */       return ((Map)o).isEmpty();
/*     */     }
/*     */     public String toString() {
/*  90 */       return "{}";
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Raul\Desktop\StarMade\StarMade.jar
 * Qualified Name:     it.unimi.dsi.fastutil.shorts.Short2ObjectMaps
 * JD-Core Version:    0.6.2
 */