/*     */ package it.unimi.dsi.fastutil.longs;
/*     */ 
/*     */ import it.unimi.dsi.fastutil.Hash;
/*     */ import it.unimi.dsi.fastutil.HashCommon;
/*     */ import it.unimi.dsi.fastutil.booleans.BooleanArrays;
/*     */ import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
/*     */ import it.unimi.dsi.fastutil.objects.AbstractReferenceCollection;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectArrays;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectIterator;
/*     */ import it.unimi.dsi.fastutil.objects.ReferenceCollection;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.NoSuchElementException;
/*     */ 
/*     */ public class Long2ReferenceOpenCustomHashMap<V> extends AbstractLong2ReferenceMap<V>
/*     */   implements Serializable, Cloneable, Hash
/*     */ {
/*     */   public static final long serialVersionUID = 0L;
/*     */   private static final boolean ASSERTS = false;
/*     */   protected transient long[] key;
/*     */   protected transient V[] value;
/*     */   protected transient boolean[] used;
/*     */   protected final float f;
/*     */   protected transient int n;
/*     */   protected transient int maxFill;
/*     */   protected transient int mask;
/*     */   protected int size;
/*     */   protected volatile transient Long2ReferenceMap.FastEntrySet<V> entries;
/*     */   protected volatile transient LongSet keys;
/*     */   protected volatile transient ReferenceCollection<V> values;
/*     */   protected LongHash.Strategy strategy;
/*     */ 
/*     */   public Long2ReferenceOpenCustomHashMap(int expected, float f, LongHash.Strategy strategy)
/*     */   {
/* 110 */     this.strategy = strategy;
/* 111 */     if ((f <= 0.0F) || (f > 1.0F)) throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
/* 112 */     if (expected < 0) throw new IllegalArgumentException("The expected number of elements must be nonnegative");
/* 113 */     this.f = f;
/* 114 */     this.n = HashCommon.arraySize(expected, f);
/* 115 */     this.mask = (this.n - 1);
/* 116 */     this.maxFill = HashCommon.maxFill(this.n, f);
/* 117 */     this.key = new long[this.n];
/* 118 */     this.value = ((Object[])new Object[this.n]);
/* 119 */     this.used = new boolean[this.n];
/*     */   }
/*     */ 
/*     */   public Long2ReferenceOpenCustomHashMap(int expected, LongHash.Strategy strategy)
/*     */   {
/* 127 */     this(expected, 0.75F, strategy);
/*     */   }
/*     */ 
/*     */   public Long2ReferenceOpenCustomHashMap(LongHash.Strategy strategy)
/*     */   {
/* 134 */     this(16, 0.75F, strategy);
/*     */   }
/*     */ 
/*     */   public Long2ReferenceOpenCustomHashMap(Map<? extends Long, ? extends V> m, float f, LongHash.Strategy strategy)
/*     */   {
/* 143 */     this(m.size(), f, strategy);
/* 144 */     putAll(m);
/*     */   }
/*     */ 
/*     */   public Long2ReferenceOpenCustomHashMap(Map<? extends Long, ? extends V> m, LongHash.Strategy strategy)
/*     */   {
/* 152 */     this(m, 0.75F, strategy);
/*     */   }
/*     */ 
/*     */   public Long2ReferenceOpenCustomHashMap(Long2ReferenceMap<V> m, float f, LongHash.Strategy strategy)
/*     */   {
/* 161 */     this(m.size(), f, strategy);
/* 162 */     putAll(m);
/*     */   }
/*     */ 
/*     */   public Long2ReferenceOpenCustomHashMap(Long2ReferenceMap<V> m, LongHash.Strategy strategy)
/*     */   {
/* 170 */     this(m, 0.75F, strategy);
/*     */   }
/*     */ 
/*     */   public Long2ReferenceOpenCustomHashMap(long[] k, V[] v, float f, LongHash.Strategy strategy)
/*     */   {
/* 181 */     this(k.length, f, strategy);
/* 182 */     if (k.length != v.length) throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
/* 183 */     for (int i = 0; i < k.length; i++) put(k[i], v[i]);
/*     */   }
/*     */ 
/*     */   public Long2ReferenceOpenCustomHashMap(long[] k, V[] v, LongHash.Strategy strategy)
/*     */   {
/* 193 */     this(k, v, 0.75F, strategy);
/*     */   }
/*     */ 
/*     */   public LongHash.Strategy strategy()
/*     */   {
/* 200 */     return this.strategy;
/*     */   }
/*     */ 
/*     */   public V put(long k, V v)
/*     */   {
/* 208 */     int pos = HashCommon.murmurHash3(this.strategy.hashCode(k)) & this.mask;
/*     */ 
/* 210 */     while (this.used[pos] != 0) {
/* 211 */       if (this.strategy.equals(this.key[pos], k)) {
/* 212 */         Object oldValue = this.value[pos];
/* 213 */         this.value[pos] = v;
/* 214 */         return oldValue;
/*     */       }
/* 216 */       pos = pos + 1 & this.mask;
/*     */     }
/* 218 */     this.used[pos] = true;
/* 219 */     this.key[pos] = k;
/* 220 */     this.value[pos] = v;
/* 221 */     if (++this.size >= this.maxFill) rehash(HashCommon.arraySize(this.size + 1, this.f));
/*     */ 
/* 223 */     return this.defRetValue;
/*     */   }
/*     */   public V put(Long ok, V ov) {
/* 226 */     Object v = ov;
/* 227 */     long k = ok.longValue();
/*     */ 
/* 229 */     int pos = HashCommon.murmurHash3(this.strategy.hashCode(k)) & this.mask;
/*     */ 
/* 231 */     while (this.used[pos] != 0) {
/* 232 */       if (this.strategy.equals(this.key[pos], k)) {
/* 233 */         Object oldValue = this.value[pos];
/* 234 */         this.value[pos] = v;
/* 235 */         return oldValue;
/*     */       }
/* 237 */       pos = pos + 1 & this.mask;
/*     */     }
/* 239 */     this.used[pos] = true;
/* 240 */     this.key[pos] = k;
/* 241 */     this.value[pos] = v;
/* 242 */     if (++this.size >= this.maxFill) rehash(HashCommon.arraySize(this.size + 1, this.f));
/*     */ 
/* 244 */     return this.defRetValue;
/*     */   }
/*     */ 
/*     */   protected final int shiftKeys(int pos)
/*     */   {
/*     */     int last;
/*     */     while (true)
/*     */     {
/* 256 */       pos = (last = pos) + 1 & this.mask;
/* 257 */       while (this.used[pos] != 0) {
/* 258 */         int slot = HashCommon.murmurHash3(this.strategy.hashCode(this.key[pos])) & this.mask;
/* 259 */         if (last <= pos ? (last < slot) && (slot <= pos) : (last >= slot) && (slot > pos)) break;
/* 260 */         pos = pos + 1 & this.mask;
/*     */       }
/* 262 */       if (this.used[pos] == 0) break;
/* 263 */       this.key[last] = this.key[pos];
/* 264 */       this.value[last] = this.value[pos];
/*     */     }
/* 266 */     this.used[last] = false;
/* 267 */     this.value[last] = null;
/* 268 */     return last;
/*     */   }
/*     */ 
/*     */   public V remove(long k)
/*     */   {
/* 273 */     int pos = HashCommon.murmurHash3(this.strategy.hashCode(k)) & this.mask;
/*     */ 
/* 275 */     while (this.used[pos] != 0) {
/* 276 */       if (this.strategy.equals(this.key[pos], k)) {
/* 277 */         this.size -= 1;
/* 278 */         Object v = this.value[pos];
/* 279 */         shiftKeys(pos);
/* 280 */         return v;
/*     */       }
/* 282 */       pos = pos + 1 & this.mask;
/*     */     }
/* 284 */     return this.defRetValue;
/*     */   }
/*     */ 
/*     */   public V remove(Object ok) {
/* 288 */     long k = ((Long)ok).longValue();
/*     */ 
/* 290 */     int pos = HashCommon.murmurHash3(this.strategy.hashCode(k)) & this.mask;
/*     */ 
/* 292 */     while (this.used[pos] != 0) {
/* 293 */       if (this.strategy.equals(this.key[pos], k)) {
/* 294 */         this.size -= 1;
/* 295 */         Object v = this.value[pos];
/* 296 */         shiftKeys(pos);
/* 297 */         return v;
/*     */       }
/* 299 */       pos = pos + 1 & this.mask;
/*     */     }
/* 301 */     return this.defRetValue;
/*     */   }
/*     */   public V get(Long ok) {
/* 304 */     long k = ok.longValue();
/*     */ 
/* 306 */     int pos = HashCommon.murmurHash3(this.strategy.hashCode(k)) & this.mask;
/*     */ 
/* 308 */     while (this.used[pos] != 0) {
/* 309 */       if (this.strategy.equals(this.key[pos], k)) return this.value[pos];
/* 310 */       pos = pos + 1 & this.mask;
/*     */     }
/* 312 */     return this.defRetValue;
/*     */   }
/*     */ 
/*     */   public V get(long k)
/*     */   {
/* 317 */     int pos = HashCommon.murmurHash3(this.strategy.hashCode(k)) & this.mask;
/*     */ 
/* 319 */     while (this.used[pos] != 0) {
/* 320 */       if (this.strategy.equals(this.key[pos], k)) return this.value[pos];
/* 321 */       pos = pos + 1 & this.mask;
/*     */     }
/* 323 */     return this.defRetValue;
/*     */   }
/*     */ 
/*     */   public boolean containsKey(long k)
/*     */   {
/* 328 */     int pos = HashCommon.murmurHash3(this.strategy.hashCode(k)) & this.mask;
/*     */ 
/* 330 */     while (this.used[pos] != 0) {
/* 331 */       if (this.strategy.equals(this.key[pos], k)) return true;
/* 332 */       pos = pos + 1 & this.mask;
/*     */     }
/* 334 */     return false;
/*     */   }
/*     */   public boolean containsValue(Object v) {
/* 337 */     Object[] value = this.value;
/* 338 */     boolean[] used = this.used;
/* 339 */     for (int i = this.n; i-- != 0; return true) label16: if ((used[i] == 0) || (value[i] != v))
/*     */         break label16; return false;
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 349 */     if (this.size == 0) return;
/* 350 */     this.size = 0;
/* 351 */     BooleanArrays.fill(this.used, false);
/*     */ 
/* 353 */     ObjectArrays.fill(this.value, null);
/*     */   }
/*     */   public int size() {
/* 356 */     return this.size;
/*     */   }
/*     */   public boolean isEmpty() {
/* 359 */     return this.size == 0;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public void growthFactor(int growthFactor)
/*     */   {
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public int growthFactor()
/*     */   {
/* 376 */     return 16;
/*     */   }
/*     */ 
/*     */   public Long2ReferenceMap.FastEntrySet<V> long2ReferenceEntrySet()
/*     */   {
/* 577 */     if (this.entries == null) this.entries = new MapEntrySet(null);
/* 578 */     return this.entries;
/*     */   }
/*     */ 
/*     */   public LongSet keySet()
/*     */   {
/* 611 */     if (this.keys == null) this.keys = new KeySet(null);
/* 612 */     return this.keys;
/*     */   }
/*     */ 
/*     */   public ReferenceCollection<V> values()
/*     */   {
/* 625 */     if (this.values == null) this.values = new AbstractReferenceCollection() {
/*     */         public ObjectIterator<V> iterator() {
/* 627 */           return new Long2ReferenceOpenCustomHashMap.ValueIterator(Long2ReferenceOpenCustomHashMap.this);
/*     */         }
/*     */         public int size() {
/* 630 */           return Long2ReferenceOpenCustomHashMap.this.size;
/*     */         }
/*     */         public boolean contains(Object v) {
/* 633 */           return Long2ReferenceOpenCustomHashMap.this.containsValue(v);
/*     */         }
/*     */         public void clear() {
/* 636 */           Long2ReferenceOpenCustomHashMap.this.clear();
/*     */         }
/*     */       };
/* 639 */     return this.values;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public boolean rehash()
/*     */   {
/* 653 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean trim()
/*     */   {
/* 668 */     int l = HashCommon.arraySize(this.size, this.f);
/* 669 */     if (l >= this.n) return true; try
/*     */     {
/* 671 */       rehash(l);
/*     */     } catch (OutOfMemoryError cantDoIt) {
/* 673 */       return false;
/* 674 */     }return true;
/*     */   }
/*     */ 
/*     */   public boolean trim(int n)
/*     */   {
/* 695 */     int l = HashCommon.nextPowerOfTwo((int)Math.ceil(n / this.f));
/* 696 */     if (this.n <= l) return true; try
/*     */     {
/* 698 */       rehash(l);
/*     */     } catch (OutOfMemoryError cantDoIt) {
/* 700 */       return false;
/* 701 */     }return true;
/*     */   }
/*     */ 
/*     */   protected void rehash(int newN)
/*     */   {
/* 714 */     int i = 0;
/* 715 */     boolean[] used = this.used;
/*     */ 
/* 717 */     long[] key = this.key;
/* 718 */     Object[] value = this.value;
/* 719 */     int newMask = newN - 1;
/* 720 */     long[] newKey = new long[newN];
/* 721 */     Object[] newValue = (Object[])new Object[newN];
/* 722 */     boolean[] newUsed = new boolean[newN];
/* 723 */     for (int j = this.size; j-- != 0; ) {
/* 724 */       while (used[i] == 0) i++;
/* 725 */       long k = key[i];
/* 726 */       int pos = HashCommon.murmurHash3(this.strategy.hashCode(k)) & newMask;
/* 727 */       while (newUsed[pos] != 0) pos = pos + 1 & newMask;
/* 728 */       newUsed[pos] = true;
/* 729 */       newKey[pos] = k;
/* 730 */       newValue[pos] = value[i];
/* 731 */       i++;
/*     */     }
/* 733 */     this.n = newN;
/* 734 */     this.mask = newMask;
/* 735 */     this.maxFill = HashCommon.maxFill(this.n, this.f);
/* 736 */     this.key = newKey;
/* 737 */     this.value = newValue;
/* 738 */     this.used = newUsed;
/*     */   }
/*     */ 
/*     */   public Long2ReferenceOpenCustomHashMap<V> clone()
/*     */   {
/*     */     Long2ReferenceOpenCustomHashMap c;
/*     */     try
/*     */     {
/* 751 */       c = (Long2ReferenceOpenCustomHashMap)super.clone();
/*     */     }
/*     */     catch (CloneNotSupportedException cantHappen) {
/* 754 */       throw new InternalError();
/*     */     }
/* 756 */     c.keys = null;
/* 757 */     c.values = null;
/* 758 */     c.entries = null;
/* 759 */     c.key = ((long[])this.key.clone());
/* 760 */     c.value = ((Object[])this.value.clone());
/* 761 */     c.used = ((boolean[])this.used.clone());
/* 762 */     c.strategy = this.strategy;
/* 763 */     return c;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 775 */     int h = 0;
/* 776 */     int j = this.size; int i = 0; for (int t = 0; j-- != 0; ) {
/* 777 */       while (this.used[i] == 0) i++;
/* 778 */       t = this.strategy.hashCode(this.key[i]);
/* 779 */       if (this != this.value[i])
/* 780 */         t ^= (this.value[i] == null ? 0 : System.identityHashCode(this.value[i]));
/* 781 */       h += t;
/* 782 */       i++;
/*     */     }
/* 784 */     return h;
/*     */   }
/*     */   private void writeObject(ObjectOutputStream s) throws IOException {
/* 787 */     long[] key = this.key;
/* 788 */     Object[] value = this.value;
/* 789 */     MapIterator i = new MapIterator(null);
/* 790 */     s.defaultWriteObject();
/* 791 */     for (int j = this.size; j-- != 0; ) {
/* 792 */       int e = i.nextEntry();
/* 793 */       s.writeLong(key[e]);
/* 794 */       s.writeObject(value[e]);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
/* 799 */     s.defaultReadObject();
/* 800 */     this.n = HashCommon.arraySize(this.size, this.f);
/* 801 */     this.maxFill = HashCommon.maxFill(this.n, this.f);
/* 802 */     this.mask = (this.n - 1);
/* 803 */     long[] key = this.key = new long[this.n];
/* 804 */     Object[] value = this.value = (Object[])new Object[this.n];
/* 805 */     boolean[] used = this.used = new boolean[this.n];
/*     */ 
/* 808 */     int i = this.size; for (int pos = 0; i-- != 0; ) {
/* 809 */       long k = s.readLong();
/* 810 */       Object v = s.readObject();
/* 811 */       pos = HashCommon.murmurHash3(this.strategy.hashCode(k)) & this.mask;
/* 812 */       while (used[pos] != 0) pos = pos + 1 & this.mask;
/* 813 */       used[pos] = true;
/* 814 */       key[pos] = k;
/* 815 */       value[pos] = v;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void checkTable()
/*     */   {
/*     */   }
/*     */ 
/*     */   private final class ValueIterator extends Long2ReferenceOpenCustomHashMap<V>.MapIterator
/*     */     implements ObjectIterator<V>
/*     */   {
/*     */     public ValueIterator()
/*     */     {
/* 621 */       super(null); } 
/* 622 */     public V next() { return Long2ReferenceOpenCustomHashMap.this.value[nextEntry()]; }
/*     */ 
/*     */   }
/*     */ 
/*     */   private final class KeySet extends AbstractLongSet
/*     */   {
/*     */     private KeySet()
/*     */     {
/*     */     }
/*     */ 
/*     */     public LongIterator iterator()
/*     */     {
/* 593 */       return new Long2ReferenceOpenCustomHashMap.KeyIterator(Long2ReferenceOpenCustomHashMap.this);
/*     */     }
/*     */     public int size() {
/* 596 */       return Long2ReferenceOpenCustomHashMap.this.size;
/*     */     }
/*     */     public boolean contains(long k) {
/* 599 */       return Long2ReferenceOpenCustomHashMap.this.containsKey(k);
/*     */     }
/*     */     public boolean remove(long k) {
/* 602 */       int oldSize = Long2ReferenceOpenCustomHashMap.this.size;
/* 603 */       Long2ReferenceOpenCustomHashMap.this.remove(k);
/* 604 */       return Long2ReferenceOpenCustomHashMap.this.size != oldSize;
/*     */     }
/*     */     public void clear() {
/* 607 */       Long2ReferenceOpenCustomHashMap.this.clear();
/*     */     }
/*     */   }
/*     */ 
/*     */   private final class KeyIterator extends Long2ReferenceOpenCustomHashMap.MapIterator
/*     */     implements LongIterator
/*     */   {
/*     */     public KeyIterator()
/*     */     {
/* 587 */       super(null); } 
/* 588 */     public long nextLong() { return Long2ReferenceOpenCustomHashMap.this.key[nextEntry()]; } 
/* 589 */     public Long next() { return Long.valueOf(Long2ReferenceOpenCustomHashMap.this.key[nextEntry()]); }
/*     */ 
/*     */   }
/*     */ 
/*     */   private final class MapEntrySet extends AbstractObjectSet<Long2ReferenceMap.Entry<V>>
/*     */     implements Long2ReferenceMap.FastEntrySet<V>
/*     */   {
/*     */     private MapEntrySet()
/*     */     {
/*     */     }
/*     */ 
/*     */     public ObjectIterator<Long2ReferenceMap.Entry<V>> iterator()
/*     */     {
/* 533 */       return new Long2ReferenceOpenCustomHashMap.EntryIterator(Long2ReferenceOpenCustomHashMap.this, null);
/*     */     }
/*     */     public ObjectIterator<Long2ReferenceMap.Entry<V>> fastIterator() {
/* 536 */       return new Long2ReferenceOpenCustomHashMap.FastEntryIterator(Long2ReferenceOpenCustomHashMap.this, null);
/*     */     }
/*     */ 
/*     */     public boolean contains(Object o) {
/* 540 */       if (!(o instanceof Map.Entry)) return false;
/* 541 */       Map.Entry e = (Map.Entry)o;
/* 542 */       long k = ((Long)e.getKey()).longValue();
/*     */ 
/* 544 */       int pos = HashCommon.murmurHash3(Long2ReferenceOpenCustomHashMap.this.strategy.hashCode(k)) & Long2ReferenceOpenCustomHashMap.this.mask;
/*     */ 
/* 546 */       while (Long2ReferenceOpenCustomHashMap.this.used[pos] != 0) {
/* 547 */         if (Long2ReferenceOpenCustomHashMap.this.strategy.equals(Long2ReferenceOpenCustomHashMap.this.key[pos], k)) return Long2ReferenceOpenCustomHashMap.this.value[pos] == e.getValue();
/* 548 */         pos = pos + 1 & Long2ReferenceOpenCustomHashMap.this.mask;
/*     */       }
/* 550 */       return false;
/*     */     }
/*     */ 
/*     */     public boolean remove(Object o) {
/* 554 */       if (!(o instanceof Map.Entry)) return false;
/* 555 */       Map.Entry e = (Map.Entry)o;
/* 556 */       long k = ((Long)e.getKey()).longValue();
/*     */ 
/* 558 */       int pos = HashCommon.murmurHash3(Long2ReferenceOpenCustomHashMap.this.strategy.hashCode(k)) & Long2ReferenceOpenCustomHashMap.this.mask;
/*     */ 
/* 560 */       while (Long2ReferenceOpenCustomHashMap.this.used[pos] != 0) {
/* 561 */         if (Long2ReferenceOpenCustomHashMap.this.strategy.equals(Long2ReferenceOpenCustomHashMap.this.key[pos], k)) {
/* 562 */           Long2ReferenceOpenCustomHashMap.this.remove(e.getKey());
/* 563 */           return true;
/*     */         }
/* 565 */         pos = pos + 1 & Long2ReferenceOpenCustomHashMap.this.mask;
/*     */       }
/* 567 */       return false;
/*     */     }
/*     */     public int size() {
/* 570 */       return Long2ReferenceOpenCustomHashMap.this.size;
/*     */     }
/*     */     public void clear() {
/* 573 */       Long2ReferenceOpenCustomHashMap.this.clear();
/*     */     }
/*     */   }
/*     */ 
/*     */   private class FastEntryIterator extends Long2ReferenceOpenCustomHashMap<V>.MapIterator
/*     */     implements ObjectIterator<Long2ReferenceMap.Entry<V>>
/*     */   {
/* 523 */     final AbstractLong2ReferenceMap.BasicEntry<V> entry = new AbstractLong2ReferenceMap.BasicEntry(0L, null);
/*     */ 
/*     */     private FastEntryIterator()
/*     */     {
/* 522 */       super(null);
/*     */     }
/*     */     public AbstractLong2ReferenceMap.BasicEntry<V> next() {
/* 525 */       int e = nextEntry();
/* 526 */       this.entry.key = Long2ReferenceOpenCustomHashMap.this.key[e];
/* 527 */       this.entry.value = Long2ReferenceOpenCustomHashMap.this.value[e];
/* 528 */       return this.entry;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class EntryIterator extends Long2ReferenceOpenCustomHashMap<V>.MapIterator
/*     */     implements ObjectIterator<Long2ReferenceMap.Entry<V>>
/*     */   {
/*     */     private Long2ReferenceOpenCustomHashMap<V>.MapEntry entry;
/*     */ 
/*     */     private EntryIterator()
/*     */     {
/* 511 */       super(null);
/*     */     }
/*     */     public Long2ReferenceMap.Entry<V> next() {
/* 514 */       return this.entry = new Long2ReferenceOpenCustomHashMap.MapEntry(Long2ReferenceOpenCustomHashMap.this, nextEntry());
/*     */     }
/*     */ 
/*     */     public void remove() {
/* 518 */       super.remove();
/* 519 */       Long2ReferenceOpenCustomHashMap.MapEntry.access$102(this.entry, -1);
/*     */     }
/*     */   }
/*     */ 
/*     */   private class MapIterator
/*     */   {
/*     */     int pos;
/*     */     int last;
/*     */     int c;
/*     */     LongArrayList wrapped;
/*     */ 
/*     */     private MapIterator()
/*     */     {
/* 419 */       this.pos = Long2ReferenceOpenCustomHashMap.this.n;
/*     */ 
/* 422 */       this.last = -1;
/*     */ 
/* 424 */       this.c = Long2ReferenceOpenCustomHashMap.this.size;
/*     */ 
/* 429 */       boolean[] used = Long2ReferenceOpenCustomHashMap.this.used;
/* 430 */       while ((this.c != 0) && (used[(--this.pos)] == 0));
/*     */     }
/*     */ 
/*     */     public boolean hasNext()
/*     */     {
/* 433 */       return this.c != 0;
/*     */     }
/*     */     public int nextEntry() {
/* 436 */       if (!hasNext()) throw new NoSuchElementException();
/* 437 */       this.c -= 1;
/*     */ 
/* 439 */       if (this.pos < 0) {
/* 440 */         long k = this.wrapped.getLong(-(this.last = --this.pos) - 2);
/*     */ 
/* 442 */         int pos = HashCommon.murmurHash3(Long2ReferenceOpenCustomHashMap.this.strategy.hashCode(k)) & Long2ReferenceOpenCustomHashMap.this.mask;
/*     */ 
/* 444 */         while (Long2ReferenceOpenCustomHashMap.this.used[pos] != 0) {
/* 445 */           if (Long2ReferenceOpenCustomHashMap.this.strategy.equals(Long2ReferenceOpenCustomHashMap.this.key[pos], k)) return pos;
/* 446 */           pos = pos + 1 & Long2ReferenceOpenCustomHashMap.this.mask;
/*     */         }
/*     */       }
/* 449 */       this.last = this.pos;
/*     */ 
/* 451 */       if (this.c != 0) {
/* 452 */         boolean[] used = Long2ReferenceOpenCustomHashMap.this.used;
/* 453 */         while ((this.pos-- != 0) && (used[this.pos] == 0));
/*     */       }
/* 456 */       return this.last;
/*     */     }
/*     */ 
/*     */     protected final int shiftKeys(int pos)
/*     */     {
/*     */       int last;
/*     */       while (true)
/*     */       {
/* 469 */         pos = (last = pos) + 1 & Long2ReferenceOpenCustomHashMap.this.mask;
/* 470 */         while (Long2ReferenceOpenCustomHashMap.this.used[pos] != 0) {
/* 471 */           int slot = HashCommon.murmurHash3(Long2ReferenceOpenCustomHashMap.this.strategy.hashCode(Long2ReferenceOpenCustomHashMap.this.key[pos])) & Long2ReferenceOpenCustomHashMap.this.mask;
/* 472 */           if (last <= pos ? (last < slot) && (slot <= pos) : (last >= slot) && (slot > pos)) break;
/* 473 */           pos = pos + 1 & Long2ReferenceOpenCustomHashMap.this.mask;
/*     */         }
/* 475 */         if (Long2ReferenceOpenCustomHashMap.this.used[pos] == 0) break;
/* 476 */         if (pos < last)
/*     */         {
/* 478 */           if (this.wrapped == null) this.wrapped = new LongArrayList();
/* 479 */           this.wrapped.add(Long2ReferenceOpenCustomHashMap.this.key[pos]);
/*     */         }
/* 481 */         Long2ReferenceOpenCustomHashMap.this.key[last] = Long2ReferenceOpenCustomHashMap.this.key[pos];
/* 482 */         Long2ReferenceOpenCustomHashMap.this.value[last] = Long2ReferenceOpenCustomHashMap.this.value[pos];
/*     */       }
/* 484 */       Long2ReferenceOpenCustomHashMap.this.used[last] = false;
/* 485 */       Long2ReferenceOpenCustomHashMap.this.value[last] = null;
/* 486 */       return last;
/*     */     }
/*     */ 
/*     */     public void remove() {
/* 490 */       if (this.last == -1) throw new IllegalStateException();
/* 491 */       if (this.pos < -1)
/*     */       {
/* 493 */         Long2ReferenceOpenCustomHashMap.this.remove(this.wrapped.getLong(-this.pos - 2));
/* 494 */         this.last = -1;
/* 495 */         return;
/*     */       }
/* 497 */       Long2ReferenceOpenCustomHashMap.this.size -= 1;
/* 498 */       if ((shiftKeys(this.last) == this.pos) && (this.c > 0)) {
/* 499 */         this.c += 1;
/* 500 */         nextEntry();
/*     */       }
/* 502 */       this.last = -1;
/*     */     }
/*     */ 
/*     */     public int skip(int n) {
/* 506 */       int i = n;
/* 507 */       while ((i-- != 0) && (hasNext())) nextEntry();
/* 508 */       return n - i - 1;
/*     */     }
/*     */   }
/*     */ 
/*     */   private final class MapEntry
/*     */     implements Long2ReferenceMap.Entry<V>, Map.Entry<Long, V>
/*     */   {
/*     */     private int index;
/*     */ 
/*     */     MapEntry(int index)
/*     */     {
/* 386 */       this.index = index;
/*     */     }
/*     */     public Long getKey() {
/* 389 */       return Long.valueOf(Long2ReferenceOpenCustomHashMap.this.key[this.index]);
/*     */     }
/*     */     public long getLongKey() {
/* 392 */       return Long2ReferenceOpenCustomHashMap.this.key[this.index];
/*     */     }
/*     */     public V getValue() {
/* 395 */       return Long2ReferenceOpenCustomHashMap.this.value[this.index];
/*     */     }
/*     */     public V setValue(V v) {
/* 398 */       Object oldValue = Long2ReferenceOpenCustomHashMap.this.value[this.index];
/* 399 */       Long2ReferenceOpenCustomHashMap.this.value[this.index] = v;
/* 400 */       return oldValue;
/*     */     }
/*     */ 
/*     */     public boolean equals(Object o) {
/* 404 */       if (!(o instanceof Map.Entry)) return false;
/* 405 */       Map.Entry e = (Map.Entry)o;
/* 406 */       return (Long2ReferenceOpenCustomHashMap.this.strategy.equals(Long2ReferenceOpenCustomHashMap.this.key[this.index], ((Long)e.getKey()).longValue())) && (Long2ReferenceOpenCustomHashMap.this.value[this.index] == e.getValue());
/*     */     }
/*     */     public int hashCode() {
/* 409 */       return Long2ReferenceOpenCustomHashMap.this.strategy.hashCode(Long2ReferenceOpenCustomHashMap.this.key[this.index]) ^ (Long2ReferenceOpenCustomHashMap.this.value[this.index] == null ? 0 : System.identityHashCode(Long2ReferenceOpenCustomHashMap.this.value[this.index]));
/*     */     }
/*     */     public String toString() {
/* 412 */       return Long2ReferenceOpenCustomHashMap.this.key[this.index] + "=>" + Long2ReferenceOpenCustomHashMap.this.value[this.index];
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Raul\Desktop\StarMade\StarMade.jar
 * Qualified Name:     it.unimi.dsi.fastutil.longs.Long2ReferenceOpenCustomHashMap
 * JD-Core Version:    0.6.2
 */