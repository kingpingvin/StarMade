/*     */ package it.unimi.dsi.fastutil.doubles;
/*     */ 
/*     */ import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
/*     */ import java.io.Serializable;
/*     */ import java.util.Comparator;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.NoSuchElementException;
/*     */ 
/*     */ public class Double2ShortSortedMaps
/*     */ {
/* 103 */   public static final EmptySortedMap EMPTY_MAP = new EmptySortedMap();
/*     */ 
/*     */   public static Comparator<? super Map.Entry<Double, ?>> entryComparator(DoubleComparator comparator)
/*     */   {
/*  64 */     return new Comparator() {
/*     */       public int compare(Map.Entry<Double, ?> x, Map.Entry<Double, ?> y) {
/*  66 */         return this.val$comparator.compare(x.getKey(), y.getKey());
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static Double2ShortSortedMap singleton(Double key, Short value)
/*     */   {
/* 173 */     return new Singleton(key.doubleValue(), value.shortValue());
/*     */   }
/*     */ 
/*     */   public static Double2ShortSortedMap singleton(Double key, Short value, DoubleComparator comparator)
/*     */   {
/* 187 */     return new Singleton(key.doubleValue(), value.shortValue(), comparator);
/*     */   }
/*     */ 
/*     */   public static Double2ShortSortedMap singleton(double key, short value)
/*     */   {
/* 202 */     return new Singleton(key, value);
/*     */   }
/*     */ 
/*     */   public static Double2ShortSortedMap singleton(double key, short value, DoubleComparator comparator)
/*     */   {
/* 216 */     return new Singleton(key, value, comparator);
/*     */   }
/*     */ 
/*     */   public static Double2ShortSortedMap synchronize(Double2ShortSortedMap m)
/*     */   {
/* 272 */     return new SynchronizedSortedMap(m);
/*     */   }
/*     */ 
/*     */   public static Double2ShortSortedMap synchronize(Double2ShortSortedMap m, Object sync)
/*     */   {
/* 282 */     return new SynchronizedSortedMap(m, sync);
/*     */   }
/*     */ 
/*     */   public static Double2ShortSortedMap unmodifiable(Double2ShortSortedMap m)
/*     */   {
/* 332 */     return new UnmodifiableSortedMap(m);
/*     */   }
/*     */ 
/*     */   public static class UnmodifiableSortedMap extends Double2ShortMaps.UnmodifiableMap
/*     */     implements Double2ShortSortedMap, Serializable
/*     */   {
/*     */     public static final long serialVersionUID = -7046029254386353129L;
/*     */     protected final Double2ShortSortedMap sortedMap;
/*     */ 
/*     */     protected UnmodifiableSortedMap(Double2ShortSortedMap m)
/*     */     {
/* 296 */       super();
/* 297 */       this.sortedMap = m;
/*     */     }
/*     */     public DoubleComparator comparator() {
/* 300 */       return this.sortedMap.comparator();
/*     */     }
/* 302 */     public ObjectSortedSet<Double2ShortMap.Entry> double2ShortEntrySet() { if (this.entries == null) this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.double2ShortEntrySet()); return (ObjectSortedSet)this.entries; } 
/*     */     public ObjectSortedSet<Map.Entry<Double, Short>> entrySet() {
/* 304 */       return double2ShortEntrySet(); } 
/* 305 */     public DoubleSortedSet keySet() { if (this.keys == null) this.keys = DoubleSortedSets.unmodifiable(this.sortedMap.keySet()); return (DoubleSortedSet)this.keys; } 
/*     */     public Double2ShortSortedMap subMap(double from, double to) {
/* 307 */       return new UnmodifiableSortedMap(this.sortedMap.subMap(from, to)); } 
/* 308 */     public Double2ShortSortedMap headMap(double to) { return new UnmodifiableSortedMap(this.sortedMap.headMap(to)); } 
/* 309 */     public Double2ShortSortedMap tailMap(double from) { return new UnmodifiableSortedMap(this.sortedMap.tailMap(from)); } 
/*     */     public double firstDoubleKey() {
/* 311 */       return this.sortedMap.firstDoubleKey(); } 
/* 312 */     public double lastDoubleKey() { return this.sortedMap.lastDoubleKey(); }
/*     */ 
/*     */     public Double firstKey() {
/* 315 */       return (Double)this.sortedMap.firstKey(); } 
/* 316 */     public Double lastKey() { return (Double)this.sortedMap.lastKey(); } 
/*     */     public Double2ShortSortedMap subMap(Double from, Double to) {
/* 318 */       return new UnmodifiableSortedMap(this.sortedMap.subMap(from, to)); } 
/* 319 */     public Double2ShortSortedMap headMap(Double to) { return new UnmodifiableSortedMap(this.sortedMap.headMap(to)); } 
/* 320 */     public Double2ShortSortedMap tailMap(Double from) { return new UnmodifiableSortedMap(this.sortedMap.tailMap(from)); }
/*     */ 
/*     */   }
/*     */ 
/*     */   public static class SynchronizedSortedMap extends Double2ShortMaps.SynchronizedMap
/*     */     implements Double2ShortSortedMap, Serializable
/*     */   {
/*     */     public static final long serialVersionUID = -7046029254386353129L;
/*     */     protected final Double2ShortSortedMap sortedMap;
/*     */ 
/*     */     protected SynchronizedSortedMap(Double2ShortSortedMap m, Object sync)
/*     */     {
/* 231 */       super(sync);
/* 232 */       this.sortedMap = m;
/*     */     }
/*     */ 
/*     */     protected SynchronizedSortedMap(Double2ShortSortedMap m) {
/* 236 */       super();
/* 237 */       this.sortedMap = m;
/*     */     }
/*     */     public DoubleComparator comparator() {
/* 240 */       synchronized (this.sync) { return this.sortedMap.comparator(); } 
/*     */     }
/* 242 */     public ObjectSortedSet<Double2ShortMap.Entry> double2ShortEntrySet() { if (this.entries == null) this.entries = ObjectSortedSets.synchronize(this.sortedMap.double2ShortEntrySet(), this.sync); return (ObjectSortedSet)this.entries; } 
/*     */     public ObjectSortedSet<Map.Entry<Double, Short>> entrySet() {
/* 244 */       return double2ShortEntrySet(); } 
/* 245 */     public DoubleSortedSet keySet() { if (this.keys == null) this.keys = DoubleSortedSets.synchronize(this.sortedMap.keySet(), this.sync); return (DoubleSortedSet)this.keys; } 
/*     */     public Double2ShortSortedMap subMap(double from, double to) {
/* 247 */       return new SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync); } 
/* 248 */     public Double2ShortSortedMap headMap(double to) { return new SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync); } 
/* 249 */     public Double2ShortSortedMap tailMap(double from) { return new SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync); } 
/*     */     public double firstDoubleKey() {
/* 251 */       synchronized (this.sync) { return this.sortedMap.firstDoubleKey(); }  } 
/* 252 */     public double lastDoubleKey() { synchronized (this.sync) { return this.sortedMap.lastDoubleKey(); } }
/*     */ 
/*     */     public Double firstKey() {
/* 255 */       synchronized (this.sync) { return (Double)this.sortedMap.firstKey(); }  } 
/* 256 */     public Double lastKey() { synchronized (this.sync) { return (Double)this.sortedMap.lastKey(); }  } 
/*     */     public Double2ShortSortedMap subMap(Double from, Double to) {
/* 258 */       return new SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync); } 
/* 259 */     public Double2ShortSortedMap headMap(Double to) { return new SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync); } 
/* 260 */     public Double2ShortSortedMap tailMap(Double from) { return new SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync); }
/*     */ 
/*     */   }
/*     */ 
/*     */   public static class Singleton extends Double2ShortMaps.Singleton
/*     */     implements Double2ShortSortedMap, Serializable, Cloneable
/*     */   {
/*     */     public static final long serialVersionUID = -7046029254386353129L;
/*     */     protected final DoubleComparator comparator;
/*     */ 
/*     */     protected Singleton(double key, short value, DoubleComparator comparator)
/*     */     {
/* 119 */       super(value);
/* 120 */       this.comparator = comparator;
/*     */     }
/*     */ 
/*     */     protected Singleton(double key, short value) {
/* 124 */       this(key, value, null);
/*     */     }
/*     */ 
/*     */     final int compare(double k1, double k2)
/*     */     {
/* 129 */       return this.comparator == null ? 1 : k1 == k2 ? 0 : k1 < k2 ? -1 : this.comparator.compare(k1, k2);
/*     */     }
/*     */     public DoubleComparator comparator() {
/* 132 */       return this.comparator;
/*     */     }
/*     */     public ObjectSortedSet<Double2ShortMap.Entry> double2ShortEntrySet() {
/* 135 */       if (this.entries == null) this.entries = ObjectSortedSets.singleton(new Double2ShortMaps.Singleton.SingletonEntry(this), Double2ShortSortedMaps.entryComparator(this.comparator)); return (ObjectSortedSet)this.entries;
/*     */     }
/* 137 */     public ObjectSortedSet<Map.Entry<Double, Short>> entrySet() { return double2ShortEntrySet(); } 
/*     */     public DoubleSortedSet keySet() {
/* 139 */       if (this.keys == null) this.keys = DoubleSortedSets.singleton(this.key, this.comparator); return (DoubleSortedSet)this.keys;
/*     */     }
/*     */     public Double2ShortSortedMap subMap(double from, double to) {
/* 142 */       if ((compare(from, this.key) <= 0) && (compare(this.key, to) < 0)) return this; return Double2ShortSortedMaps.EMPTY_MAP;
/*     */     }
/*     */     public Double2ShortSortedMap headMap(double to) {
/* 145 */       if (compare(this.key, to) < 0) return this; return Double2ShortSortedMaps.EMPTY_MAP;
/*     */     }
/*     */     public Double2ShortSortedMap tailMap(double from) {
/* 148 */       if (compare(from, this.key) <= 0) return this; return Double2ShortSortedMaps.EMPTY_MAP;
/*     */     }
/* 150 */     public double firstDoubleKey() { return this.key; } 
/* 151 */     public double lastDoubleKey() { return this.key; }
/*     */ 
/*     */     public Double2ShortSortedMap headMap(Double oto) {
/* 154 */       return headMap(oto.doubleValue()); } 
/* 155 */     public Double2ShortSortedMap tailMap(Double ofrom) { return tailMap(ofrom.doubleValue()); } 
/* 156 */     public Double2ShortSortedMap subMap(Double ofrom, Double oto) { return subMap(ofrom.doubleValue(), oto.doubleValue()); } 
/*     */     public Double firstKey() {
/* 158 */       return Double.valueOf(firstDoubleKey()); } 
/* 159 */     public Double lastKey() { return Double.valueOf(lastDoubleKey()); }
/*     */ 
/*     */   }
/*     */ 
/*     */   public static class EmptySortedMap extends Double2ShortMaps.EmptyMap
/*     */     implements Double2ShortSortedMap, Serializable, Cloneable
/*     */   {
/*     */     public static final long serialVersionUID = -7046029254386353129L;
/*     */ 
/*     */     public DoubleComparator comparator()
/*     */     {
/*  78 */       return null;
/*     */     }
/*  80 */     public ObjectSortedSet<Double2ShortMap.Entry> double2ShortEntrySet() { return ObjectSortedSets.EMPTY_SET; } 
/*     */     public ObjectSortedSet<Map.Entry<Double, Short>> entrySet() {
/*  82 */       return ObjectSortedSets.EMPTY_SET;
/*     */     }
/*  84 */     public DoubleSortedSet keySet() { return DoubleSortedSets.EMPTY_SET; } 
/*     */     public Double2ShortSortedMap subMap(double from, double to) {
/*  86 */       return Double2ShortSortedMaps.EMPTY_MAP;
/*     */     }
/*  88 */     public Double2ShortSortedMap headMap(double to) { return Double2ShortSortedMaps.EMPTY_MAP; } 
/*     */     public Double2ShortSortedMap tailMap(double from) {
/*  90 */       return Double2ShortSortedMaps.EMPTY_MAP; } 
/*  91 */     public double firstDoubleKey() { throw new NoSuchElementException(); } 
/*  92 */     public double lastDoubleKey() { throw new NoSuchElementException(); } 
/*  93 */     public Double2ShortSortedMap headMap(Double oto) { return headMap(oto.doubleValue()); } 
/*  94 */     public Double2ShortSortedMap tailMap(Double ofrom) { return tailMap(ofrom.doubleValue()); } 
/*  95 */     public Double2ShortSortedMap subMap(Double ofrom, Double oto) { return subMap(ofrom.doubleValue(), oto.doubleValue()); } 
/*  96 */     public Double firstKey() { return Double.valueOf(firstDoubleKey()); } 
/*  97 */     public Double lastKey() { return Double.valueOf(lastDoubleKey()); }
/*     */ 
/*     */   }
/*     */ }

/* Location:           C:\Users\Raul\Desktop\StarMade\StarMade.jar
 * Qualified Name:     it.unimi.dsi.fastutil.doubles.Double2ShortSortedMaps
 * JD-Core Version:    0.6.2
 */