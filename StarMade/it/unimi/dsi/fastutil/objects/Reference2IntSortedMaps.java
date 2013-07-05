/*     */ package it.unimi.dsi.fastutil.objects;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.Comparator;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.NoSuchElementException;
/*     */ 
/*     */ public class Reference2IntSortedMaps
/*     */ {
/* 103 */   public static final EmptySortedMap EMPTY_MAP = new EmptySortedMap();
/*     */ 
/*     */   public static <K> Comparator<? super Map.Entry<K, ?>> entryComparator(Comparator<K> comparator)
/*     */   {
/*  63 */     return new Comparator() {
/*     */       public int compare(Map.Entry<K, ?> x, Map.Entry<K, ?> y) {
/*  65 */         return this.val$comparator.compare(x.getKey(), y.getKey());
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static <K> Reference2IntSortedMap<K> singleton(K key, Integer value)
/*     */   {
/* 162 */     return new Singleton(key, value.intValue());
/*     */   }
/*     */ 
/*     */   public static <K> Reference2IntSortedMap<K> singleton(K key, Integer value, Comparator<? super K> comparator)
/*     */   {
/* 174 */     return new Singleton(key, value.intValue(), comparator);
/*     */   }
/*     */ 
/*     */   public static <K> Reference2IntSortedMap<K> singleton(K key, int value)
/*     */   {
/* 185 */     return new Singleton(key, value);
/*     */   }
/*     */ 
/*     */   public static <K> Reference2IntSortedMap<K> singleton(K key, int value, Comparator<? super K> comparator)
/*     */   {
/* 197 */     return new Singleton(key, value, comparator);
/*     */   }
/*     */ 
/*     */   public static <K> Reference2IntSortedMap<K> synchronize(Reference2IntSortedMap<K> m)
/*     */   {
/* 228 */     return new SynchronizedSortedMap(m);
/*     */   }
/*     */ 
/*     */   public static <K> Reference2IntSortedMap<K> synchronize(Reference2IntSortedMap<K> m, Object sync)
/*     */   {
/* 236 */     return new SynchronizedSortedMap(m, sync);
/*     */   }
/*     */ 
/*     */   public static <K> Reference2IntSortedMap<K> unmodifiable(Reference2IntSortedMap<K> m)
/*     */   {
/* 262 */     return new UnmodifiableSortedMap(m);
/*     */   }
/*     */ 
/*     */   public static class UnmodifiableSortedMap<K> extends Reference2IntMaps.UnmodifiableMap<K>
/*     */     implements Reference2IntSortedMap<K>, Serializable
/*     */   {
/*     */     public static final long serialVersionUID = -7046029254386353129L;
/*     */     protected final Reference2IntSortedMap<K> sortedMap;
/*     */ 
/*     */     protected UnmodifiableSortedMap(Reference2IntSortedMap<K> m)
/*     */     {
/* 242 */       super();
/* 243 */       this.sortedMap = m;
/*     */     }
/* 245 */     public Comparator<? super K> comparator() { return this.sortedMap.comparator(); } 
/* 246 */     public ObjectSortedSet<Reference2IntMap.Entry<K>> reference2IntEntrySet() { if (this.entries == null) this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.reference2IntEntrySet()); return (ObjectSortedSet)this.entries; } 
/*     */     public ObjectSortedSet<Map.Entry<K, Integer>> entrySet() {
/* 248 */       return reference2IntEntrySet(); } 
/* 249 */     public ReferenceSortedSet<K> keySet() { if (this.keys == null) this.keys = ReferenceSortedSets.unmodifiable(this.sortedMap.keySet()); return (ReferenceSortedSet)this.keys; } 
/* 250 */     public Reference2IntSortedMap<K> subMap(K from, K to) { return new UnmodifiableSortedMap(this.sortedMap.subMap(from, to)); } 
/* 251 */     public Reference2IntSortedMap<K> headMap(K to) { return new UnmodifiableSortedMap(this.sortedMap.headMap(to)); } 
/* 252 */     public Reference2IntSortedMap<K> tailMap(K from) { return new UnmodifiableSortedMap(this.sortedMap.tailMap(from)); } 
/* 253 */     public K firstKey() { return this.sortedMap.firstKey(); } 
/* 254 */     public K lastKey() { return this.sortedMap.lastKey(); }
/*     */ 
/*     */   }
/*     */ 
/*     */   public static class SynchronizedSortedMap<K> extends Reference2IntMaps.SynchronizedMap<K>
/*     */     implements Reference2IntSortedMap<K>, Serializable
/*     */   {
/*     */     public static final long serialVersionUID = -7046029254386353129L;
/*     */     protected final Reference2IntSortedMap<K> sortedMap;
/*     */ 
/*     */     protected SynchronizedSortedMap(Reference2IntSortedMap<K> m, Object sync)
/*     */     {
/* 204 */       super(sync);
/* 205 */       this.sortedMap = m;
/*     */     }
/*     */     protected SynchronizedSortedMap(Reference2IntSortedMap<K> m) {
/* 208 */       super();
/* 209 */       this.sortedMap = m;
/*     */     }
/* 211 */     public Comparator<? super K> comparator() { synchronized (this.sync) { return this.sortedMap.comparator(); }  } 
/* 212 */     public ObjectSortedSet<Reference2IntMap.Entry<K>> reference2IntEntrySet() { if (this.entries == null) this.entries = ObjectSortedSets.synchronize(this.sortedMap.reference2IntEntrySet(), this.sync); return (ObjectSortedSet)this.entries; } 
/*     */     public ObjectSortedSet<Map.Entry<K, Integer>> entrySet() {
/* 214 */       return reference2IntEntrySet(); } 
/* 215 */     public ReferenceSortedSet<K> keySet() { if (this.keys == null) this.keys = ReferenceSortedSets.synchronize(this.sortedMap.keySet(), this.sync); return (ReferenceSortedSet)this.keys; } 
/* 216 */     public Reference2IntSortedMap<K> subMap(K from, K to) { return new SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync); } 
/* 217 */     public Reference2IntSortedMap<K> headMap(K to) { return new SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync); } 
/* 218 */     public Reference2IntSortedMap<K> tailMap(K from) { return new SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync); } 
/* 219 */     public K firstKey() { synchronized (this.sync) { return this.sortedMap.firstKey(); }  } 
/* 220 */     public K lastKey() { synchronized (this.sync) { return this.sortedMap.lastKey(); }
/*     */ 
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class Singleton<K> extends Reference2IntMaps.Singleton<K>
/*     */     implements Reference2IntSortedMap<K>, Serializable, Cloneable
/*     */   {
/*     */     public static final long serialVersionUID = -7046029254386353129L;
/*     */     protected final Comparator<? super K> comparator;
/*     */ 
/*     */     protected Singleton(K key, int value, Comparator<? super K> comparator)
/*     */     {
/* 119 */       super(value);
/* 120 */       this.comparator = comparator;
/*     */     }
/*     */ 
/*     */     protected Singleton(K key, int value) {
/* 124 */       this(key, value, null);
/*     */     }
/*     */ 
/*     */     final int compare(K k1, K k2)
/*     */     {
/* 129 */       return this.comparator == null ? ((Comparable)k1).compareTo(k2) : this.comparator.compare(k1, k2);
/*     */     }
/*     */     public Comparator<? super K> comparator() {
/* 132 */       return this.comparator;
/*     */     }
/*     */     public ObjectSortedSet<Reference2IntMap.Entry<K>> reference2IntEntrySet() {
/* 135 */       if (this.entries == null) this.entries = ObjectSortedSets.singleton(new Reference2IntMaps.Singleton.SingletonEntry(this), Reference2IntSortedMaps.entryComparator(this.comparator)); return (ObjectSortedSet)this.entries;
/*     */     }
/* 137 */     public ObjectSortedSet<Map.Entry<K, Integer>> entrySet() { return reference2IntEntrySet(); } 
/*     */     public ReferenceSortedSet<K> keySet() {
/* 139 */       if (this.keys == null) this.keys = ReferenceSortedSets.singleton(this.key, this.comparator); return (ReferenceSortedSet)this.keys;
/*     */     }
/*     */     public Reference2IntSortedMap<K> subMap(K from, K to) {
/* 142 */       if ((compare(from, this.key) <= 0) && (compare(this.key, to) < 0)) return this; return Reference2IntSortedMaps.EMPTY_MAP;
/*     */     }
/*     */     public Reference2IntSortedMap<K> headMap(K to) {
/* 145 */       if (compare(this.key, to) < 0) return this; return Reference2IntSortedMaps.EMPTY_MAP;
/*     */     }
/*     */     public Reference2IntSortedMap<K> tailMap(K from) {
/* 148 */       if (compare(from, this.key) <= 0) return this; return Reference2IntSortedMaps.EMPTY_MAP;
/*     */     }
/* 150 */     public K firstKey() { return this.key; } 
/* 151 */     public K lastKey() { return this.key; }
/*     */ 
/*     */   }
/*     */ 
/*     */   public static class EmptySortedMap<K> extends Reference2IntMaps.EmptyMap<K>
/*     */     implements Reference2IntSortedMap<K>, Serializable, Cloneable
/*     */   {
/*     */     public static final long serialVersionUID = -7046029254386353129L;
/*     */ 
/*     */     public Comparator<? super K> comparator()
/*     */     {
/*  77 */       return null;
/*     */     }
/*  79 */     public ObjectSortedSet<Reference2IntMap.Entry<K>> reference2IntEntrySet() { return ObjectSortedSets.EMPTY_SET; } 
/*     */     public ObjectSortedSet<Map.Entry<K, Integer>> entrySet() {
/*  81 */       return ObjectSortedSets.EMPTY_SET;
/*     */     }
/*  83 */     public ReferenceSortedSet<K> keySet() { return ReferenceSortedSets.EMPTY_SET; } 
/*     */     public Reference2IntSortedMap<K> subMap(K from, K to) {
/*  85 */       return Reference2IntSortedMaps.EMPTY_MAP;
/*     */     }
/*  87 */     public Reference2IntSortedMap<K> headMap(K to) { return Reference2IntSortedMaps.EMPTY_MAP; } 
/*     */     public Reference2IntSortedMap<K> tailMap(K from) {
/*  89 */       return Reference2IntSortedMaps.EMPTY_MAP; } 
/*  90 */     public K firstKey() { throw new NoSuchElementException(); } 
/*  91 */     public K lastKey() { throw new NoSuchElementException(); }
/*     */ 
/*     */   }
/*     */ }

/* Location:           C:\Users\Raul\Desktop\StarMade\StarMade.jar
 * Qualified Name:     it.unimi.dsi.fastutil.objects.Reference2IntSortedMaps
 * JD-Core Version:    0.6.2
 */