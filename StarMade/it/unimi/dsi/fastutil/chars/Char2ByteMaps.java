/*     */ package it.unimi.dsi.fastutil.chars;
/*     */ 
/*     */ import it.unimi.dsi.fastutil.bytes.ByteCollection;
/*     */ import it.unimi.dsi.fastutil.bytes.ByteCollections;
/*     */ import it.unimi.dsi.fastutil.bytes.ByteSets;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectIterator;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectSet;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectSets;
/*     */ import java.io.Serializable;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class Char2ByteMaps
/*     */ {
/*  98 */   public static final EmptyMap EMPTY_MAP = new EmptyMap();
/*     */ 
/*     */   public static Char2ByteMap singleton(char key, byte value)
/*     */   {
/* 185 */     return new Singleton(key, value);
/*     */   }
/*     */ 
/*     */   public static Char2ByteMap singleton(Character key, Byte value)
/*     */   {
/* 200 */     return new Singleton(key.charValue(), value.byteValue());
/*     */   }
/*     */ 
/*     */   public static Char2ByteMap synchronize(Char2ByteMap m)
/*     */   {
/* 279 */     return new SynchronizedMap(m);
/*     */   }
/*     */ 
/*     */   public static Char2ByteMap synchronize(Char2ByteMap m, Object sync)
/*     */   {
/* 289 */     return new SynchronizedMap(m, sync);
/*     */   }
/*     */ 
/*     */   public static Char2ByteMap unmodifiable(Char2ByteMap m)
/*     */   {
/* 358 */     return new UnmodifiableMap(m);
/*     */   }
/*     */ 
/*     */   public static class UnmodifiableMap extends Char2ByteFunctions.UnmodifiableFunction
/*     */     implements Char2ByteMap, Serializable
/*     */   {
/*     */     public static final long serialVersionUID = -7046029254386353129L;
/*     */     protected final Char2ByteMap map;
/*     */     protected volatile transient ObjectSet<Char2ByteMap.Entry> entries;
/*     */     protected volatile transient CharSet keys;
/*     */     protected volatile transient ByteCollection values;
/*     */ 
/*     */     protected UnmodifiableMap(Char2ByteMap m)
/*     */     {
/* 306 */       super();
/* 307 */       this.map = m;
/*     */     }
/*     */     public int size() {
/* 310 */       return this.map.size(); } 
/* 311 */     public boolean containsKey(char k) { return this.map.containsKey(k); } 
/* 312 */     public boolean containsValue(byte v) { return this.map.containsValue(v); } 
/*     */     public byte defaultReturnValue() {
/* 314 */       throw new UnsupportedOperationException(); } 
/* 315 */     public void defaultReturnValue(byte defRetValue) { throw new UnsupportedOperationException(); } 
/*     */     public byte put(char k, byte v) {
/* 317 */       throw new UnsupportedOperationException();
/*     */     }
/*     */     public void putAll(Map<? extends Character, ? extends Byte> m) {
/* 320 */       throw new UnsupportedOperationException();
/*     */     }
/* 322 */     public ObjectSet<Char2ByteMap.Entry> char2ByteEntrySet() { if (this.entries == null) this.entries = ObjectSets.unmodifiable(this.map.char2ByteEntrySet()); return this.entries; } 
/* 323 */     public CharSet keySet() { if (this.keys == null) this.keys = CharSets.unmodifiable(this.map.keySet()); return this.keys; } 
/* 324 */     public ByteCollection values() { if (this.values == null) return ByteCollections.unmodifiable(this.map.values()); return this.values; } 
/*     */     public void clear() {
/* 326 */       throw new UnsupportedOperationException(); } 
/* 327 */     public String toString() { return this.map.toString(); }
/*     */ 
/*     */     public Byte put(Character k, Byte v) {
/* 330 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     public byte remove(char k) {
/* 334 */       throw new UnsupportedOperationException(); } 
/* 335 */     public byte get(char k) { return this.map.get(k); } 
/* 336 */     public boolean containsKey(Object ok) { return this.map.containsKey(ok); }
/*     */ 
/*     */     public boolean containsValue(Object ov)
/*     */     {
/* 340 */       return this.map.containsValue(ov);
/*     */     }
/*     */ 
/*     */     public boolean isEmpty()
/*     */     {
/* 348 */       return this.map.isEmpty(); } 
/* 349 */     public ObjectSet<Map.Entry<Character, Byte>> entrySet() { return ObjectSets.unmodifiable(this.map.entrySet()); }
/*     */ 
/*     */   }
/*     */ 
/*     */   public static class SynchronizedMap extends Char2ByteFunctions.SynchronizedFunction
/*     */     implements Char2ByteMap, Serializable
/*     */   {
/*     */     public static final long serialVersionUID = -7046029254386353129L;
/*     */     protected final Char2ByteMap map;
/*     */     protected volatile transient ObjectSet<Char2ByteMap.Entry> entries;
/*     */     protected volatile transient CharSet keys;
/*     */     protected volatile transient ByteCollection values;
/*     */ 
/*     */     protected SynchronizedMap(Char2ByteMap m, Object sync)
/*     */     {
/* 219 */       super(sync);
/* 220 */       this.map = m;
/*     */     }
/*     */ 
/*     */     protected SynchronizedMap(Char2ByteMap m) {
/* 224 */       super();
/* 225 */       this.map = m;
/*     */     }
/*     */     public int size() {
/* 228 */       synchronized (this.sync) { return this.map.size(); }  } 
/* 229 */     public boolean containsKey(char k) { synchronized (this.sync) { return this.map.containsKey(k); }  } 
/* 230 */     public boolean containsValue(byte v) { synchronized (this.sync) { return this.map.containsValue(v); }  } 
/*     */     public byte defaultReturnValue() {
/* 232 */       synchronized (this.sync) { return this.map.defaultReturnValue(); }  } 
/* 233 */     public void defaultReturnValue(byte defRetValue) { synchronized (this.sync) { this.map.defaultReturnValue(defRetValue); }  } 
/*     */     public byte put(char k, byte v) {
/* 235 */       synchronized (this.sync) { return this.map.put(k, v); } 
/*     */     }
/*     */ 
/* 238 */     public void putAll(Map<? extends Character, ? extends Byte> m) { synchronized (this.sync) { this.map.putAll(m); }  } 
/*     */     public ObjectSet<Char2ByteMap.Entry> char2ByteEntrySet() {
/* 240 */       if (this.entries == null) this.entries = ObjectSets.synchronize(this.map.char2ByteEntrySet(), this.sync); return this.entries; } 
/* 241 */     public CharSet keySet() { if (this.keys == null) this.keys = CharSets.synchronize(this.map.keySet(), this.sync); return this.keys; } 
/* 242 */     public ByteCollection values() { if (this.values == null) return ByteCollections.synchronize(this.map.values(), this.sync); return this.values; } 
/*     */     public void clear() {
/* 244 */       synchronized (this.sync) { this.map.clear(); }  } 
/* 245 */     public String toString() { synchronized (this.sync) { return this.map.toString(); } }
/*     */ 
/*     */     public Byte put(Character k, Byte v) {
/* 248 */       synchronized (this.sync) { return (Byte)this.map.put(k, v); }
/*     */     }
/*     */ 
/*     */     public byte remove(char k) {
/* 252 */       synchronized (this.sync) { return this.map.remove(k); }  } 
/* 253 */     public byte get(char k) { synchronized (this.sync) { return this.map.get(k); }  } 
/* 254 */     public boolean containsKey(Object ok) { synchronized (this.sync) { return this.map.containsKey(ok); } }
/*     */ 
/*     */     public boolean containsValue(Object ov)
/*     */     {
/* 258 */       synchronized (this.sync) { return this.map.containsValue(ov); }
/*     */ 
/*     */     }
/*     */ 
/*     */     public boolean isEmpty()
/*     */     {
/* 266 */       synchronized (this.sync) { return this.map.isEmpty(); }  } 
/* 267 */     public ObjectSet<Map.Entry<Character, Byte>> entrySet() { synchronized (this.sync) { return this.map.entrySet(); }  } 
/*     */     public int hashCode() {
/* 269 */       synchronized (this.sync) { return this.map.hashCode(); }  } 
/* 270 */     public boolean equals(Object o) { synchronized (this.sync) { return this.map.equals(o); }
/*     */ 
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class Singleton extends Char2ByteFunctions.Singleton
/*     */     implements Char2ByteMap, Serializable, Cloneable
/*     */   {
/*     */     public static final long serialVersionUID = -7046029254386353129L;
/*     */     protected volatile transient ObjectSet<Char2ByteMap.Entry> entries;
/*     */     protected volatile transient CharSet keys;
/*     */     protected volatile transient ByteCollection values;
/*     */ 
/*     */     protected Singleton(char key, byte value)
/*     */     {
/* 116 */       super(value);
/*     */     }
/*     */     public boolean containsValue(byte v) {
/* 119 */       return this.value == v;
/*     */     }
/* 121 */     public boolean containsValue(Object ov) { return ((Byte)ov).byteValue() == this.value; }
/*     */ 
/*     */     public void putAll(Map<? extends Character, ? extends Byte> m) {
/* 124 */       throw new UnsupportedOperationException();
/*     */     }
/* 126 */     public ObjectSet<Char2ByteMap.Entry> char2ByteEntrySet() { if (this.entries == null) this.entries = ObjectSets.singleton(new SingletonEntry()); return this.entries; } 
/* 127 */     public CharSet keySet() { if (this.keys == null) this.keys = CharSets.singleton(this.key); return this.keys; } 
/* 128 */     public ByteCollection values() { if (this.values == null) this.values = ByteSets.singleton(this.value); return this.values;
/*     */     }
/*     */ 
/*     */     public boolean isEmpty()
/*     */     {
/* 156 */       return false;
/*     */     }
/*     */     public ObjectSet<Map.Entry<Character, Byte>> entrySet() {
/* 159 */       return char2ByteEntrySet();
/*     */     }
/* 161 */     public int hashCode() { return this.key ^ this.value; }
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
/*     */       implements Char2ByteMap.Entry, Map.Entry<Character, Byte>
/*     */     {
/*     */       protected SingletonEntry()
/*     */       {
/*     */       }
/*     */ 
/*     */       public Character getKey()
/*     */       {
/* 131 */         return Character.valueOf(Char2ByteMaps.Singleton.this.key); } 
/* 132 */       public Byte getValue() { return Byte.valueOf(Char2ByteMaps.Singleton.this.value); }
/*     */ 
/*     */       public char getCharKey() {
/* 135 */         return Char2ByteMaps.Singleton.this.key;
/*     */       }
/*     */ 
/*     */       public byte getByteValue() {
/* 139 */         return Char2ByteMaps.Singleton.this.value; } 
/* 140 */       public byte setValue(byte value) { throw new UnsupportedOperationException(); }
/*     */ 
/*     */       public Byte setValue(Byte value) {
/* 143 */         throw new UnsupportedOperationException();
/*     */       }
/*     */       public boolean equals(Object o) {
/* 146 */         if (!(o instanceof Map.Entry)) return false;
/* 147 */         Map.Entry e = (Map.Entry)o;
/*     */ 
/* 149 */         return (Char2ByteMaps.Singleton.this.key == ((Character)e.getKey()).charValue()) && (Char2ByteMaps.Singleton.this.value == ((Byte)e.getValue()).byteValue());
/*     */       }
/*     */       public int hashCode() {
/* 152 */         return Char2ByteMaps.Singleton.this.key ^ Char2ByteMaps.Singleton.this.value; } 
/* 153 */       public String toString() { return Char2ByteMaps.Singleton.this.key + "->" + Char2ByteMaps.Singleton.this.value; }
/*     */ 
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class EmptyMap extends Char2ByteFunctions.EmptyFunction
/*     */     implements Char2ByteMap, Serializable, Cloneable
/*     */   {
/*     */     public static final long serialVersionUID = -7046029254386353129L;
/*     */ 
/*     */     public boolean containsValue(byte v)
/*     */     {
/*  67 */       return false; } 
/*  68 */     public void putAll(Map<? extends Character, ? extends Byte> m) { throw new UnsupportedOperationException(); } 
/*     */     public ObjectSet<Char2ByteMap.Entry> char2ByteEntrySet() {
/*  70 */       return ObjectSets.EMPTY_SET;
/*     */     }
/*  72 */     public CharSet keySet() { return CharSets.EMPTY_SET; } 
/*     */     public ByteCollection values() {
/*  74 */       return ByteSets.EMPTY_SET; } 
/*  75 */     public boolean containsValue(Object ov) { return false; } 
/*  76 */     private Object readResolve() { return Char2ByteMaps.EMPTY_MAP; } 
/*  77 */     public Object clone() { return Char2ByteMaps.EMPTY_MAP; } 
/*  78 */     public boolean isEmpty() { return true; } 
/*     */     public ObjectSet<Map.Entry<Character, Byte>> entrySet() {
/*  80 */       return char2ByteEntrySet();
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
 * Qualified Name:     it.unimi.dsi.fastutil.chars.Char2ByteMaps
 * JD-Core Version:    0.6.2
 */