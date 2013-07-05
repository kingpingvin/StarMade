/*     */ package it.unimi.dsi.fastutil.objects;
/*     */ 
/*     */ import it.unimi.dsi.fastutil.Hash;
/*     */ import it.unimi.dsi.fastutil.HashCommon;
/*     */ import it.unimi.dsi.fastutil.booleans.BooleanArrays;
/*     */ import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
/*     */ import it.unimi.dsi.fastutil.longs.LongCollection;
/*     */ import it.unimi.dsi.fastutil.longs.LongIterator;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.NoSuchElementException;
/*     */ 
/*     */ public class Reference2LongOpenHashMap<K> extends AbstractReference2LongMap<K>
/*     */   implements Serializable, Cloneable, Hash
/*     */ {
/*     */   public static final long serialVersionUID = 0L;
/*     */   private static final boolean ASSERTS = false;
/*     */   protected transient K[] key;
/*     */   protected transient long[] value;
/*     */   protected transient boolean[] used;
/*     */   protected final float f;
/*     */   protected transient int n;
/*     */   protected transient int maxFill;
/*     */   protected transient int mask;
/*     */   protected int size;
/*     */   protected volatile transient Reference2LongMap.FastEntrySet<K> entries;
/*     */   protected volatile transient ReferenceSet<K> keys;
/*     */   protected volatile transient LongCollection values;
/*     */ 
/*     */   public Reference2LongOpenHashMap(int expected, float f)
/*     */   {
/* 106 */     if ((f <= 0.0F) || (f > 1.0F)) throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
/* 107 */     if (expected < 0) throw new IllegalArgumentException("The expected number of elements must be nonnegative");
/* 108 */     this.f = f;
/* 109 */     this.n = HashCommon.arraySize(expected, f);
/* 110 */     this.mask = (this.n - 1);
/* 111 */     this.maxFill = HashCommon.maxFill(this.n, f);
/* 112 */     this.key = ((Object[])new Object[this.n]);
/* 113 */     this.value = new long[this.n];
/* 114 */     this.used = new boolean[this.n];
/*     */   }
/*     */ 
/*     */   public Reference2LongOpenHashMap(int expected)
/*     */   {
/* 121 */     this(expected, 0.75F);
/*     */   }
/*     */ 
/*     */   public Reference2LongOpenHashMap()
/*     */   {
/* 127 */     this(16, 0.75F);
/*     */   }
/*     */ 
/*     */   public Reference2LongOpenHashMap(Map<? extends K, ? extends Long> m, float f)
/*     */   {
/* 135 */     this(m.size(), f);
/* 136 */     putAll(m);
/*     */   }
/*     */ 
/*     */   public Reference2LongOpenHashMap(Map<? extends K, ? extends Long> m)
/*     */   {
/* 143 */     this(m, 0.75F);
/*     */   }
/*     */ 
/*     */   public Reference2LongOpenHashMap(Reference2LongMap<K> m, float f)
/*     */   {
/* 151 */     this(m.size(), f);
/* 152 */     putAll(m);
/*     */   }
/*     */ 
/*     */   public Reference2LongOpenHashMap(Reference2LongMap<K> m)
/*     */   {
/* 159 */     this(m, 0.75F);
/*     */   }
/*     */ 
/*     */   public Reference2LongOpenHashMap(K[] k, long[] v, float f)
/*     */   {
/* 169 */     this(k.length, f);
/* 170 */     if (k.length != v.length) throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
/* 171 */     for (int i = 0; i < k.length; i++) put(k[i], v[i]);
/*     */   }
/*     */ 
/*     */   public Reference2LongOpenHashMap(K[] k, long[] v)
/*     */   {
/* 180 */     this(k, v, 0.75F);
/*     */   }
/*     */ 
/*     */   public long put(K k, long v)
/*     */   {
/* 188 */     int pos = (k == null ? 142593372 : HashCommon.murmurHash3(System.identityHashCode(k))) & this.mask;
/*     */ 
/* 190 */     while (this.used[pos] != 0) {
/* 191 */       if (this.key[pos] == k) {
/* 192 */         long oldValue = this.value[pos];
/* 193 */         this.value[pos] = v;
/* 194 */         return oldValue;
/*     */       }
/* 196 */       pos = pos + 1 & this.mask;
/*     */     }
/* 198 */     this.used[pos] = true;
/* 199 */     this.key[pos] = k;
/* 200 */     this.value[pos] = v;
/* 201 */     if (++this.size >= this.maxFill) rehash(HashCommon.arraySize(this.size + 1, this.f));
/*     */ 
/* 203 */     return this.defRetValue;
/*     */   }
/*     */   public Long put(K ok, Long ov) {
/* 206 */     long v = ov.longValue();
/* 207 */     Object k = ok;
/*     */ 
/* 209 */     int pos = (k == null ? 142593372 : HashCommon.murmurHash3(System.identityHashCode(k))) & this.mask;
/*     */ 
/* 211 */     while (this.used[pos] != 0) {
/* 212 */       if (this.key[pos] == k) {
/* 213 */         Long oldValue = Long.valueOf(this.value[pos]);
/* 214 */         this.value[pos] = v;
/* 215 */         return oldValue;
/*     */       }
/* 217 */       pos = pos + 1 & this.mask;
/*     */     }
/* 219 */     this.used[pos] = true;
/* 220 */     this.key[pos] = k;
/* 221 */     this.value[pos] = v;
/* 222 */     if (++this.size >= this.maxFill) rehash(HashCommon.arraySize(this.size + 1, this.f));
/*     */ 
/* 224 */     return null;
/*     */   }
/*     */ 
/*     */   public long add(K k, long incr)
/*     */   {
/* 239 */     int pos = (k == null ? 142593372 : HashCommon.murmurHash3(System.identityHashCode(k))) & this.mask;
/*     */ 
/* 241 */     while (this.used[pos] != 0) {
/* 242 */       if (this.key[pos] == k) {
/* 243 */         long oldValue = this.value[pos];
/* 244 */         this.value[pos] += incr;
/* 245 */         return oldValue;
/*     */       }
/* 247 */       pos = pos + 1 & this.mask;
/*     */     }
/* 249 */     this.used[pos] = true;
/* 250 */     this.key[pos] = k;
/* 251 */     this.value[pos] = (this.defRetValue + incr);
/* 252 */     if (++this.size >= this.maxFill) rehash(HashCommon.arraySize(this.size + 1, this.f));
/*     */ 
/* 254 */     return this.defRetValue;
/*     */   }
/*     */ 
/*     */   protected final int shiftKeys(int pos)
/*     */   {
/*     */     int last;
/*     */     while (true)
/*     */     {
/* 266 */       pos = (last = pos) + 1 & this.mask;
/* 267 */       while (this.used[pos] != 0) {
/* 268 */         int slot = (this.key[pos] == null ? 142593372 : HashCommon.murmurHash3(System.identityHashCode(this.key[pos]))) & this.mask;
/* 269 */         if (last <= pos ? (last < slot) && (slot <= pos) : (last >= slot) && (slot > pos)) break;
/* 270 */         pos = pos + 1 & this.mask;
/*     */       }
/* 272 */       if (this.used[pos] == 0) break;
/* 273 */       this.key[last] = this.key[pos];
/* 274 */       this.value[last] = this.value[pos];
/*     */     }
/* 276 */     this.used[last] = false;
/* 277 */     this.key[last] = null;
/* 278 */     return last;
/*     */   }
/*     */ 
/*     */   public long removeLong(Object k)
/*     */   {
/* 283 */     int pos = (k == null ? 142593372 : HashCommon.murmurHash3(System.identityHashCode(k))) & this.mask;
/*     */ 
/* 285 */     while (this.used[pos] != 0) {
/* 286 */       if (this.key[pos] == k) {
/* 287 */         this.size -= 1;
/* 288 */         long v = this.value[pos];
/* 289 */         shiftKeys(pos);
/* 290 */         return v;
/*     */       }
/* 292 */       pos = pos + 1 & this.mask;
/*     */     }
/* 294 */     return this.defRetValue;
/*     */   }
/*     */ 
/*     */   public Long remove(Object ok) {
/* 298 */     Object k = ok;
/*     */ 
/* 300 */     int pos = (k == null ? 142593372 : HashCommon.murmurHash3(System.identityHashCode(k))) & this.mask;
/*     */ 
/* 302 */     while (this.used[pos] != 0) {
/* 303 */       if (this.key[pos] == k) {
/* 304 */         this.size -= 1;
/* 305 */         long v = this.value[pos];
/* 306 */         shiftKeys(pos);
/* 307 */         return Long.valueOf(v);
/*     */       }
/* 309 */       pos = pos + 1 & this.mask;
/*     */     }
/* 311 */     return null;
/*     */   }
/*     */ 
/*     */   public long getLong(Object k)
/*     */   {
/* 316 */     int pos = (k == null ? 142593372 : HashCommon.murmurHash3(System.identityHashCode(k))) & this.mask;
/*     */ 
/* 318 */     while (this.used[pos] != 0) {
/* 319 */       if (this.key[pos] == k) return this.value[pos];
/* 320 */       pos = pos + 1 & this.mask;
/*     */     }
/* 322 */     return this.defRetValue;
/*     */   }
/*     */ 
/*     */   public boolean containsKey(Object k)
/*     */   {
/* 327 */     int pos = (k == null ? 142593372 : HashCommon.murmurHash3(System.identityHashCode(k))) & this.mask;
/*     */ 
/* 329 */     while (this.used[pos] != 0) {
/* 330 */       if (this.key[pos] == k) return true;
/* 331 */       pos = pos + 1 & this.mask;
/*     */     }
/* 333 */     return false;
/*     */   }
/*     */   public boolean containsValue(long v) {
/* 336 */     long[] value = this.value;
/* 337 */     boolean[] used = this.used;
/* 338 */     for (int i = this.n; i-- != 0; return true) label17: if ((used[i] == 0) || (value[i] != v))
/*     */         break label17; return false;
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 348 */     if (this.size == 0) return;
/* 349 */     this.size = 0;
/* 350 */     BooleanArrays.fill(this.used, false);
/*     */ 
/* 352 */     ObjectArrays.fill(this.key, null);
/*     */   }
/*     */   public int size() {
/* 355 */     return this.size;
/*     */   }
/*     */   public boolean isEmpty() {
/* 358 */     return this.size == 0;
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
/* 375 */     return 16;
/*     */   }
/*     */ 
/*     */   public Reference2LongMap.FastEntrySet<K> reference2LongEntrySet()
/*     */   {
/* 579 */     if (this.entries == null) this.entries = new MapEntrySet(null);
/* 580 */     return this.entries;
/*     */   }
/*     */ 
/*     */   public ReferenceSet<K> keySet()
/*     */   {
/* 612 */     if (this.keys == null) this.keys = new KeySet(null);
/* 613 */     return this.keys;
/*     */   }
/*     */ 
/*     */   public LongCollection values()
/*     */   {
/* 627 */     if (this.values == null) this.values = new AbstractLongCollection() {
/*     */         public LongIterator iterator() {
/* 629 */           return new Reference2LongOpenHashMap.ValueIterator(Reference2LongOpenHashMap.this);
/*     */         }
/*     */         public int size() {
/* 632 */           return Reference2LongOpenHashMap.this.size;
/*     */         }
/*     */         public boolean contains(long v) {
/* 635 */           return Reference2LongOpenHashMap.this.containsValue(v);
/*     */         }
/*     */         public void clear() {
/* 638 */           Reference2LongOpenHashMap.this.clear();
/*     */         }
/*     */       };
/* 641 */     return this.values;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public boolean rehash()
/*     */   {
/* 655 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean trim()
/*     */   {
/* 670 */     int l = HashCommon.arraySize(this.size, this.f);
/* 671 */     if (l >= this.n) return true; try
/*     */     {
/* 673 */       rehash(l);
/*     */     } catch (OutOfMemoryError cantDoIt) {
/* 675 */       return false;
/* 676 */     }return true;
/*     */   }
/*     */ 
/*     */   public boolean trim(int n)
/*     */   {
/* 697 */     int l = HashCommon.nextPowerOfTwo((int)Math.ceil(n / this.f));
/* 698 */     if (this.n <= l) return true; try
/*     */     {
/* 700 */       rehash(l);
/*     */     } catch (OutOfMemoryError cantDoIt) {
/* 702 */       return false;
/* 703 */     }return true;
/*     */   }
/*     */ 
/*     */   protected void rehash(int newN)
/*     */   {
/* 716 */     int i = 0;
/* 717 */     boolean[] used = this.used;
/*     */ 
/* 719 */     Object[] key = this.key;
/* 720 */     long[] value = this.value;
/* 721 */     int newMask = newN - 1;
/* 722 */     Object[] newKey = (Object[])new Object[newN];
/* 723 */     long[] newValue = new long[newN];
/* 724 */     boolean[] newUsed = new boolean[newN];
/* 725 */     for (int j = this.size; j-- != 0; ) {
/* 726 */       while (used[i] == 0) i++;
/* 727 */       Object k = key[i];
/* 728 */       int pos = (k == null ? 142593372 : HashCommon.murmurHash3(System.identityHashCode(k))) & newMask;
/* 729 */       while (newUsed[pos] != 0) pos = pos + 1 & newMask;
/* 730 */       newUsed[pos] = true;
/* 731 */       newKey[pos] = k;
/* 732 */       newValue[pos] = value[i];
/* 733 */       i++;
/*     */     }
/* 735 */     this.n = newN;
/* 736 */     this.mask = newMask;
/* 737 */     this.maxFill = HashCommon.maxFill(this.n, this.f);
/* 738 */     this.key = newKey;
/* 739 */     this.value = newValue;
/* 740 */     this.used = newUsed;
/*     */   }
/*     */ 
/*     */   public Reference2LongOpenHashMap<K> clone()
/*     */   {
/*     */     Reference2LongOpenHashMap c;
/*     */     try
/*     */     {
/* 753 */       c = (Reference2LongOpenHashMap)super.clone();
/*     */     }
/*     */     catch (CloneNotSupportedException cantHappen) {
/* 756 */       throw new InternalError();
/*     */     }
/* 758 */     c.keys = null;
/* 759 */     c.values = null;
/* 760 */     c.entries = null;
/* 761 */     c.key = ((Object[])this.key.clone());
/* 762 */     c.value = ((long[])this.value.clone());
/* 763 */     c.used = ((boolean[])this.used.clone());
/* 764 */     return c;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 776 */     int h = 0;
/* 777 */     int j = this.size; int i = 0; for (int t = 0; j-- != 0; ) {
/* 778 */       while (this.used[i] == 0) i++;
/* 779 */       if (this != this.key[i])
/* 780 */         t = this.key[i] == null ? 0 : System.identityHashCode(this.key[i]);
/* 781 */       t ^= HashCommon.long2int(this.value[i]);
/* 782 */       h += t;
/* 783 */       i++;
/*     */     }
/* 785 */     return h;
/*     */   }
/*     */   private void writeObject(ObjectOutputStream s) throws IOException {
/* 788 */     Object[] key = this.key;
/* 789 */     long[] value = this.value;
/* 790 */     MapIterator i = new MapIterator(null);
/* 791 */     s.defaultWriteObject();
/* 792 */     for (int j = this.size; j-- != 0; ) {
/* 793 */       int e = i.nextEntry();
/* 794 */       s.writeObject(key[e]);
/* 795 */       s.writeLong(value[e]);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
/* 800 */     s.defaultReadObject();
/* 801 */     this.n = HashCommon.arraySize(this.size, this.f);
/* 802 */     this.maxFill = HashCommon.maxFill(this.n, this.f);
/* 803 */     this.mask = (this.n - 1);
/* 804 */     Object[] key = this.key = (Object[])new Object[this.n];
/* 805 */     long[] value = this.value = new long[this.n];
/* 806 */     boolean[] used = this.used = new boolean[this.n];
/*     */ 
/* 809 */     int i = this.size; for (int pos = 0; i-- != 0; ) {
/* 810 */       Object k = s.readObject();
/* 811 */       long v = s.readLong();
/* 812 */       pos = (k == null ? 142593372 : HashCommon.murmurHash3(System.identityHashCode(k))) & this.mask;
/* 813 */       while (used[pos] != 0) pos = pos + 1 & this.mask;
/* 814 */       used[pos] = true;
/* 815 */       key[pos] = k;
/* 816 */       value[pos] = v;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void checkTable()
/*     */   {
/*     */   }
/*     */ 
/*     */   private final class ValueIterator extends Reference2LongOpenHashMap.MapIterator
/*     */     implements LongIterator
/*     */   {
/*     */     public ValueIterator()
/*     */     {
/* 622 */       super(null); } 
/* 623 */     public long nextLong() { return Reference2LongOpenHashMap.this.value[nextEntry()]; } 
/* 624 */     public Long next() { return Long.valueOf(Reference2LongOpenHashMap.this.value[nextEntry()]); }
/*     */ 
/*     */   }
/*     */ 
/*     */   private final class KeySet extends AbstractReferenceSet<K>
/*     */   {
/*     */     private KeySet()
/*     */     {
/*     */     }
/*     */ 
/*     */     public ObjectIterator<K> iterator()
/*     */     {
/* 594 */       return new Reference2LongOpenHashMap.KeyIterator(Reference2LongOpenHashMap.this);
/*     */     }
/*     */     public int size() {
/* 597 */       return Reference2LongOpenHashMap.this.size;
/*     */     }
/*     */     public boolean contains(Object k) {
/* 600 */       return Reference2LongOpenHashMap.this.containsKey(k);
/*     */     }
/*     */     public boolean remove(Object k) {
/* 603 */       int oldSize = Reference2LongOpenHashMap.this.size;
/* 604 */       Reference2LongOpenHashMap.this.remove(k);
/* 605 */       return Reference2LongOpenHashMap.this.size != oldSize;
/*     */     }
/*     */     public void clear() {
/* 608 */       Reference2LongOpenHashMap.this.clear();
/*     */     }
/*     */   }
/*     */ 
/*     */   private final class KeyIterator extends Reference2LongOpenHashMap<K>.MapIterator
/*     */     implements ObjectIterator<K>
/*     */   {
/*     */     public KeyIterator()
/*     */     {
/* 589 */       super(null); } 
/* 590 */     public K next() { return Reference2LongOpenHashMap.this.key[nextEntry()]; }
/*     */ 
/*     */   }
/*     */ 
/*     */   private final class MapEntrySet extends AbstractObjectSet<Reference2LongMap.Entry<K>>
/*     */     implements Reference2LongMap.FastEntrySet<K>
/*     */   {
/*     */     private MapEntrySet()
/*     */     {
/*     */     }
/*     */ 
/*     */     public ObjectIterator<Reference2LongMap.Entry<K>> iterator()
/*     */     {
/* 535 */       return new Reference2LongOpenHashMap.EntryIterator(Reference2LongOpenHashMap.this, null);
/*     */     }
/*     */     public ObjectIterator<Reference2LongMap.Entry<K>> fastIterator() {
/* 538 */       return new Reference2LongOpenHashMap.FastEntryIterator(Reference2LongOpenHashMap.this, null);
/*     */     }
/*     */ 
/*     */     public boolean contains(Object o) {
/* 542 */       if (!(o instanceof Map.Entry)) return false;
/* 543 */       Map.Entry e = (Map.Entry)o;
/* 544 */       Object k = e.getKey();
/*     */ 
/* 546 */       int pos = (k == null ? 142593372 : HashCommon.murmurHash3(System.identityHashCode(k))) & Reference2LongOpenHashMap.this.mask;
/*     */ 
/* 548 */       while (Reference2LongOpenHashMap.this.used[pos] != 0) {
/* 549 */         if (Reference2LongOpenHashMap.this.key[pos] == k) return Reference2LongOpenHashMap.this.value[pos] == ((Long)e.getValue()).longValue();
/* 550 */         pos = pos + 1 & Reference2LongOpenHashMap.this.mask;
/*     */       }
/* 552 */       return false;
/*     */     }
/*     */ 
/*     */     public boolean remove(Object o) {
/* 556 */       if (!(o instanceof Map.Entry)) return false;
/* 557 */       Map.Entry e = (Map.Entry)o;
/* 558 */       Object k = e.getKey();
/*     */ 
/* 560 */       int pos = (k == null ? 142593372 : HashCommon.murmurHash3(System.identityHashCode(k))) & Reference2LongOpenHashMap.this.mask;
/*     */ 
/* 562 */       while (Reference2LongOpenHashMap.this.used[pos] != 0) {
/* 563 */         if (Reference2LongOpenHashMap.this.key[pos] == k) {
/* 564 */           Reference2LongOpenHashMap.this.remove(e.getKey());
/* 565 */           return true;
/*     */         }
/* 567 */         pos = pos + 1 & Reference2LongOpenHashMap.this.mask;
/*     */       }
/* 569 */       return false;
/*     */     }
/*     */     public int size() {
/* 572 */       return Reference2LongOpenHashMap.this.size;
/*     */     }
/*     */     public void clear() {
/* 575 */       Reference2LongOpenHashMap.this.clear();
/*     */     }
/*     */   }
/*     */ 
/*     */   private class FastEntryIterator extends Reference2LongOpenHashMap<K>.MapIterator
/*     */     implements ObjectIterator<Reference2LongMap.Entry<K>>
/*     */   {
/* 525 */     final AbstractReference2LongMap.BasicEntry<K> entry = new AbstractReference2LongMap.BasicEntry(null, 0L);
/*     */ 
/*     */     private FastEntryIterator()
/*     */     {
/* 524 */       super(null);
/*     */     }
/*     */     public AbstractReference2LongMap.BasicEntry<K> next() {
/* 527 */       int e = nextEntry();
/* 528 */       this.entry.key = Reference2LongOpenHashMap.this.key[e];
/* 529 */       this.entry.value = Reference2LongOpenHashMap.this.value[e];
/* 530 */       return this.entry;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class EntryIterator extends Reference2LongOpenHashMap<K>.MapIterator
/*     */     implements ObjectIterator<Reference2LongMap.Entry<K>>
/*     */   {
/*     */     private Reference2LongOpenHashMap<K>.MapEntry entry;
/*     */ 
/*     */     private EntryIterator()
/*     */     {
/* 513 */       super(null);
/*     */     }
/*     */     public Reference2LongMap.Entry<K> next() {
/* 516 */       return this.entry = new Reference2LongOpenHashMap.MapEntry(Reference2LongOpenHashMap.this, nextEntry());
/*     */     }
/*     */ 
/*     */     public void remove() {
/* 520 */       super.remove();
/* 521 */       Reference2LongOpenHashMap.MapEntry.access$102(this.entry, -1);
/*     */     }
/*     */   }
/*     */ 
/*     */   private class MapIterator
/*     */   {
/*     */     int pos;
/*     */     int last;
/*     */     int c;
/*     */     ReferenceArrayList<K> wrapped;
/*     */ 
/*     */     private MapIterator()
/*     */     {
/* 421 */       this.pos = Reference2LongOpenHashMap.this.n;
/*     */ 
/* 424 */       this.last = -1;
/*     */ 
/* 426 */       this.c = Reference2LongOpenHashMap.this.size;
/*     */ 
/* 431 */       boolean[] used = Reference2LongOpenHashMap.this.used;
/* 432 */       while ((this.c != 0) && (used[(--this.pos)] == 0));
/*     */     }
/*     */ 
/*     */     public boolean hasNext()
/*     */     {
/* 435 */       return this.c != 0;
/*     */     }
/*     */     public int nextEntry() {
/* 438 */       if (!hasNext()) throw new NoSuchElementException();
/* 439 */       this.c -= 1;
/*     */ 
/* 441 */       if (this.pos < 0) {
/* 442 */         Object k = this.wrapped.get(-(this.last = --this.pos) - 2);
/*     */ 
/* 444 */         int pos = (k == null ? 142593372 : HashCommon.murmurHash3(System.identityHashCode(k))) & Reference2LongOpenHashMap.this.mask;
/*     */ 
/* 446 */         while (Reference2LongOpenHashMap.this.used[pos] != 0) {
/* 447 */           if (Reference2LongOpenHashMap.this.key[pos] == k) return pos;
/* 448 */           pos = pos + 1 & Reference2LongOpenHashMap.this.mask;
/*     */         }
/*     */       }
/* 451 */       this.last = this.pos;
/*     */ 
/* 453 */       if (this.c != 0) {
/* 454 */         boolean[] used = Reference2LongOpenHashMap.this.used;
/* 455 */         while ((this.pos-- != 0) && (used[this.pos] == 0));
/*     */       }
/* 458 */       return this.last;
/*     */     }
/*     */ 
/*     */     protected final int shiftKeys(int pos)
/*     */     {
/*     */       int last;
/*     */       while (true)
/*     */       {
/* 471 */         pos = (last = pos) + 1 & Reference2LongOpenHashMap.this.mask;
/* 472 */         while (Reference2LongOpenHashMap.this.used[pos] != 0) {
/* 473 */           int slot = (Reference2LongOpenHashMap.this.key[pos] == null ? 142593372 : HashCommon.murmurHash3(System.identityHashCode(Reference2LongOpenHashMap.this.key[pos]))) & Reference2LongOpenHashMap.this.mask;
/* 474 */           if (last <= pos ? (last < slot) && (slot <= pos) : (last >= slot) && (slot > pos)) break;
/* 475 */           pos = pos + 1 & Reference2LongOpenHashMap.this.mask;
/*     */         }
/* 477 */         if (Reference2LongOpenHashMap.this.used[pos] == 0) break;
/* 478 */         if (pos < last)
/*     */         {
/* 480 */           if (this.wrapped == null) this.wrapped = new ReferenceArrayList();
/* 481 */           this.wrapped.add(Reference2LongOpenHashMap.this.key[pos]);
/*     */         }
/* 483 */         Reference2LongOpenHashMap.this.key[last] = Reference2LongOpenHashMap.this.key[pos];
/* 484 */         Reference2LongOpenHashMap.this.value[last] = Reference2LongOpenHashMap.this.value[pos];
/*     */       }
/* 486 */       Reference2LongOpenHashMap.this.used[last] = false;
/* 487 */       Reference2LongOpenHashMap.this.key[last] = null;
/* 488 */       return last;
/*     */     }
/*     */ 
/*     */     public void remove() {
/* 492 */       if (this.last == -1) throw new IllegalStateException();
/* 493 */       if (this.pos < -1)
/*     */       {
/* 495 */         Reference2LongOpenHashMap.this.remove(this.wrapped.set(-this.pos - 2, null));
/* 496 */         this.last = -1;
/* 497 */         return;
/*     */       }
/* 499 */       Reference2LongOpenHashMap.this.size -= 1;
/* 500 */       if ((shiftKeys(this.last) == this.pos) && (this.c > 0)) {
/* 501 */         this.c += 1;
/* 502 */         nextEntry();
/*     */       }
/* 504 */       this.last = -1;
/*     */     }
/*     */ 
/*     */     public int skip(int n) {
/* 508 */       int i = n;
/* 509 */       while ((i-- != 0) && (hasNext())) nextEntry();
/* 510 */       return n - i - 1;
/*     */     }
/*     */   }
/*     */ 
/*     */   private final class MapEntry
/*     */     implements Reference2LongMap.Entry<K>, Map.Entry<K, Long>
/*     */   {
/*     */     private int index;
/*     */ 
/*     */     MapEntry(int index)
/*     */     {
/* 385 */       this.index = index;
/*     */     }
/*     */     public K getKey() {
/* 388 */       return Reference2LongOpenHashMap.this.key[this.index];
/*     */     }
/*     */     public Long getValue() {
/* 391 */       return Long.valueOf(Reference2LongOpenHashMap.this.value[this.index]);
/*     */     }
/*     */     public long getLongValue() {
/* 394 */       return Reference2LongOpenHashMap.this.value[this.index];
/*     */     }
/*     */     public long setValue(long v) {
/* 397 */       long oldValue = Reference2LongOpenHashMap.this.value[this.index];
/* 398 */       Reference2LongOpenHashMap.this.value[this.index] = v;
/* 399 */       return oldValue;
/*     */     }
/*     */     public Long setValue(Long v) {
/* 402 */       return Long.valueOf(setValue(v.longValue()));
/*     */     }
/*     */ 
/*     */     public boolean equals(Object o) {
/* 406 */       if (!(o instanceof Map.Entry)) return false;
/* 407 */       Map.Entry e = (Map.Entry)o;
/* 408 */       return (Reference2LongOpenHashMap.this.key[this.index] == e.getKey()) && (Reference2LongOpenHashMap.this.value[this.index] == ((Long)e.getValue()).longValue());
/*     */     }
/*     */     public int hashCode() {
/* 411 */       return (Reference2LongOpenHashMap.this.key[this.index] == null ? 0 : System.identityHashCode(Reference2LongOpenHashMap.this.key[this.index])) ^ HashCommon.long2int(Reference2LongOpenHashMap.this.value[this.index]);
/*     */     }
/*     */     public String toString() {
/* 414 */       return Reference2LongOpenHashMap.this.key[this.index] + "=>" + Reference2LongOpenHashMap.this.value[this.index];
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Raul\Desktop\StarMade\StarMade.jar
 * Qualified Name:     it.unimi.dsi.fastutil.objects.Reference2LongOpenHashMap
 * JD-Core Version:    0.6.2
 */