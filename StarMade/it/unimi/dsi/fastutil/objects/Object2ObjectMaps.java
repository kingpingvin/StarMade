/*     */ package it.unimi.dsi.fastutil.objects;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class Object2ObjectMaps
/*     */ {
/*  98 */   public static final EmptyMap EMPTY_MAP = new EmptyMap();
/*     */ 
/*     */   public static <K, V> Object2ObjectMap<K, V> singleton(K key, V value)
/*     */   {
/* 164 */     return new Singleton(key, value);
/*     */   }
/*     */ 
/*     */   public static <K, V> Object2ObjectMap<K, V> synchronize(Object2ObjectMap<K, V> m)
/*     */   {
/* 207 */     return new SynchronizedMap(m);
/*     */   }
/*     */ 
/*     */   public static <K, V> Object2ObjectMap<K, V> synchronize(Object2ObjectMap<K, V> m, Object sync)
/*     */   {
/* 215 */     return new SynchronizedMap(m, sync);
/*     */   }
/*     */ 
/*     */   public static <K, V> Object2ObjectMap<K, V> unmodifiable(Object2ObjectMap<K, V> m)
/*     */   {
/* 251 */     return new UnmodifiableMap(m);
/*     */   }
/*     */ 
/*     */   public static class UnmodifiableMap<K, V> extends Object2ObjectFunctions.UnmodifiableFunction<K, V>
/*     */     implements Object2ObjectMap<K, V>, Serializable
/*     */   {
/*     */     public static final long serialVersionUID = -7046029254386353129L;
/*     */     protected final Object2ObjectMap<K, V> map;
/*     */     protected volatile transient ObjectSet<Object2ObjectMap.Entry<K, V>> entries;
/*     */     protected volatile transient ObjectSet<K> keys;
/*     */     protected volatile transient ObjectCollection<V> values;
/*     */ 
/*     */     protected UnmodifiableMap(Object2ObjectMap<K, V> m)
/*     */     {
/* 224 */       super();
/* 225 */       this.map = m;
/*     */     }
/* 227 */     public int size() { return this.map.size(); } 
/* 228 */     public boolean containsKey(Object k) { return this.map.containsKey(k); } 
/* 229 */     public boolean containsValue(Object v) { return this.map.containsValue(v); } 
/* 230 */     public V defaultReturnValue() { throw new UnsupportedOperationException(); } 
/* 231 */     public void defaultReturnValue(V defRetValue) { throw new UnsupportedOperationException(); } 
/* 232 */     public V put(K k, V v) { throw new UnsupportedOperationException(); } 
/*     */     public void putAll(Map<? extends K, ? extends V> m) {
/* 234 */       throw new UnsupportedOperationException(); } 
/* 235 */     public ObjectSet<Object2ObjectMap.Entry<K, V>> object2ObjectEntrySet() { if (this.entries == null) this.entries = ObjectSets.unmodifiable(this.map.object2ObjectEntrySet()); return this.entries; } 
/* 236 */     public ObjectSet<K> keySet() { if (this.keys == null) this.keys = ObjectSets.unmodifiable(this.map.keySet()); return this.keys; } 
/* 237 */     public ObjectCollection<V> values() { if (this.values == null) return ObjectCollections.unmodifiable(this.map.values()); return this.values; } 
/* 238 */     public void clear() { throw new UnsupportedOperationException(); } 
/* 239 */     public String toString() { return this.map.toString(); } 
/* 240 */     public V remove(Object k) { throw new UnsupportedOperationException(); } 
/* 241 */     public V get(Object k) { return this.map.get(k); } 
/* 242 */     public boolean isEmpty() { return this.map.isEmpty(); } 
/* 243 */     public ObjectSet<Map.Entry<K, V>> entrySet() { return ObjectSets.unmodifiable(this.map.entrySet()); }
/*     */ 
/*     */   }
/*     */ 
/*     */   public static class SynchronizedMap<K, V> extends Object2ObjectFunctions.SynchronizedFunction<K, V>
/*     */     implements Object2ObjectMap<K, V>, Serializable
/*     */   {
/*     */     public static final long serialVersionUID = -7046029254386353129L;
/*     */     protected final Object2ObjectMap<K, V> map;
/*     */     protected volatile transient ObjectSet<Object2ObjectMap.Entry<K, V>> entries;
/*     */     protected volatile transient ObjectSet<K> keys;
/*     */     protected volatile transient ObjectCollection<V> values;
/*     */ 
/*     */     protected SynchronizedMap(Object2ObjectMap<K, V> m, Object sync)
/*     */     {
/* 174 */       super(sync);
/* 175 */       this.map = m;
/*     */     }
/*     */     protected SynchronizedMap(Object2ObjectMap<K, V> m) {
/* 178 */       super();
/* 179 */       this.map = m;
/*     */     }
/* 181 */     public int size() { synchronized (this.sync) { return this.map.size(); }  } 
/* 182 */     public boolean containsKey(Object k) { synchronized (this.sync) { return this.map.containsKey(k); }  } 
/* 183 */     public boolean containsValue(Object v) { synchronized (this.sync) { return this.map.containsValue(v); }  } 
/* 184 */     public V defaultReturnValue() { synchronized (this.sync) { return this.map.defaultReturnValue(); }  } 
/* 185 */     public void defaultReturnValue(V defRetValue) { synchronized (this.sync) { this.map.defaultReturnValue(defRetValue); }  } 
/* 186 */     public V put(K k, V v) { synchronized (this.sync) { return this.map.put(k, v); }  } 
/*     */     public void putAll(Map<? extends K, ? extends V> m) {
/* 188 */       synchronized (this.sync) { this.map.putAll(m); }  } 
/* 189 */     public ObjectSet<Object2ObjectMap.Entry<K, V>> object2ObjectEntrySet() { if (this.entries == null) this.entries = ObjectSets.synchronize(this.map.object2ObjectEntrySet(), this.sync); return this.entries; } 
/* 190 */     public ObjectSet<K> keySet() { if (this.keys == null) this.keys = ObjectSets.synchronize(this.map.keySet(), this.sync); return this.keys; } 
/* 191 */     public ObjectCollection<V> values() { if (this.values == null) return ObjectCollections.synchronize(this.map.values(), this.sync); return this.values; } 
/* 192 */     public void clear() { synchronized (this.sync) { this.map.clear(); }  } 
/* 193 */     public String toString() { synchronized (this.sync) { return this.map.toString(); }  } 
/* 194 */     public V remove(Object k) { synchronized (this.sync) { return this.map.remove(k); }  } 
/* 195 */     public V get(Object k) { synchronized (this.sync) { return this.map.get(k); }  } 
/* 196 */     public boolean isEmpty() { synchronized (this.sync) { return this.map.isEmpty(); }  } 
/* 197 */     public ObjectSet<Map.Entry<K, V>> entrySet() { synchronized (this.sync) { return this.map.entrySet(); }  } 
/* 198 */     public int hashCode() { synchronized (this.sync) { return this.map.hashCode(); }  } 
/* 199 */     public boolean equals(Object o) { synchronized (this.sync) { return this.map.equals(o); }
/*     */ 
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class Singleton<K, V> extends Object2ObjectFunctions.Singleton<K, V>
/*     */     implements Object2ObjectMap<K, V>, Serializable, Cloneable
/*     */   {
/*     */     public static final long serialVersionUID = -7046029254386353129L;
/*     */     protected volatile transient ObjectSet<Object2ObjectMap.Entry<K, V>> entries;
/*     */     protected volatile transient ObjectSet<K> keys;
/*     */     protected volatile transient ObjectCollection<V> values;
/*     */ 
/*     */     protected Singleton(K key, V value)
/*     */     {
/* 116 */       super(value);
/*     */     }
/*     */     public boolean containsValue(Object v) {
/* 119 */       return this.value == null ? false : v == null ? true : this.value.equals(v);
/*     */     }
/*     */ 
/*     */     public void putAll(Map<? extends K, ? extends V> m)
/*     */     {
/* 124 */       throw new UnsupportedOperationException();
/*     */     }
/* 126 */     public ObjectSet<Object2ObjectMap.Entry<K, V>> object2ObjectEntrySet() { if (this.entries == null) this.entries = ObjectSets.singleton(new SingletonEntry()); return this.entries; } 
/* 127 */     public ObjectSet<K> keySet() { if (this.keys == null) this.keys = ObjectSets.singleton(this.key); return this.keys; } 
/* 128 */     public ObjectCollection<V> values() { if (this.values == null) this.values = ObjectSets.singleton(this.value); return this.values;
/*     */     }
/*     */ 
/*     */     public boolean isEmpty()
/*     */     {
/* 142 */       return false;
/*     */     }
/* 144 */     public ObjectSet<Map.Entry<K, V>> entrySet() { return object2ObjectEntrySet(); } 
/* 145 */     public int hashCode() { return (this.key == null ? 0 : this.key.hashCode()) ^ (this.value == null ? 0 : this.value.hashCode()); } 
/*     */     public boolean equals(Object o) {
/* 147 */       if (o == this) return true;
/* 148 */       if (!(o instanceof Map)) return false;
/* 149 */       Map m = (Map)o;
/* 150 */       if (m.size() != 1) return false;
/* 151 */       return ((Map.Entry)entrySet().iterator().next()).equals(m.entrySet().iterator().next());
/*     */     }
/* 153 */     public String toString() { return "{" + this.key + "=>" + this.value + "}"; }
/*     */ 
/*     */ 
/*     */     protected class SingletonEntry
/*     */       implements Object2ObjectMap.Entry<K, V>, Map.Entry<K, V>
/*     */     {
/*     */       protected SingletonEntry()
/*     */       {
/*     */       }
/*     */ 
/*     */       public K getKey()
/*     */       {
/* 131 */         return Object2ObjectMaps.Singleton.this.key; } 
/* 132 */       public V getValue() { return Object2ObjectMaps.Singleton.this.value; } 
/* 133 */       public V setValue(V value) { throw new UnsupportedOperationException(); } 
/*     */       public boolean equals(Object o) {
/* 135 */         if (!(o instanceof Map.Entry)) return false;
/* 136 */         Map.Entry e = (Map.Entry)o;
/* 137 */         return (Object2ObjectMaps.Singleton.this.key == null ? e.getKey() == null : Object2ObjectMaps.Singleton.this.key.equals(e.getKey())) && (Object2ObjectMaps.Singleton.this.value == null ? e.getValue() == null : Object2ObjectMaps.Singleton.this.value.equals(e.getValue()));
/*     */       }
/* 139 */       public int hashCode() { return (Object2ObjectMaps.Singleton.this.key == null ? 0 : Object2ObjectMaps.Singleton.this.key.hashCode()) ^ (Object2ObjectMaps.Singleton.this.value == null ? 0 : Object2ObjectMaps.Singleton.this.value.hashCode()); } 
/* 140 */       public String toString() { return Object2ObjectMaps.Singleton.this.key + "->" + Object2ObjectMaps.Singleton.this.value; }
/*     */ 
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class EmptyMap<K, V> extends Object2ObjectFunctions.EmptyFunction<K, V>
/*     */     implements Object2ObjectMap<K, V>, Serializable, Cloneable
/*     */   {
/*     */     public static final long serialVersionUID = -7046029254386353129L;
/*     */ 
/*     */     public boolean containsValue(Object v)
/*     */     {
/*  64 */       return false; } 
/*  65 */     public void putAll(Map<? extends K, ? extends V> m) { throw new UnsupportedOperationException(); } 
/*     */     public ObjectSet<Object2ObjectMap.Entry<K, V>> object2ObjectEntrySet() {
/*  67 */       return ObjectSets.EMPTY_SET;
/*     */     }
/*  69 */     public ObjectSet<K> keySet() { return ObjectSets.EMPTY_SET; } 
/*     */     public ObjectCollection<V> values() {
/*  71 */       return ObjectSets.EMPTY_SET;
/*     */     }
/*  73 */     private Object readResolve() { return Object2ObjectMaps.EMPTY_MAP; } 
/*     */     public Object clone() {
/*  75 */       return Object2ObjectMaps.EMPTY_MAP;
/*     */     }
/*  77 */     public boolean isEmpty() { return true; }
/*     */ 
/*     */     public ObjectSet<Map.Entry<K, V>> entrySet() {
/*  80 */       return object2ObjectEntrySet();
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
 * Qualified Name:     it.unimi.dsi.fastutil.objects.Object2ObjectMaps
 * JD-Core Version:    0.6.2
 */