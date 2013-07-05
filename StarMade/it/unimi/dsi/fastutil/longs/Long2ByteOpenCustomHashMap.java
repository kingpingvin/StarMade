/*     */ package it.unimi.dsi.fastutil.longs;
/*     */ 
/*     */ import it.unimi.dsi.fastutil.Hash;
/*     */ import it.unimi.dsi.fastutil.HashCommon;
/*     */ import it.unimi.dsi.fastutil.booleans.BooleanArrays;
/*     */ import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
/*     */ import it.unimi.dsi.fastutil.bytes.ByteCollection;
/*     */ import it.unimi.dsi.fastutil.bytes.ByteIterator;
/*     */ import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectIterator;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.NoSuchElementException;
/*     */ 
/*     */ public class Long2ByteOpenCustomHashMap extends AbstractLong2ByteMap
/*     */   implements Serializable, Cloneable, Hash
/*     */ {
/*     */   public static final long serialVersionUID = 0L;
/*     */   private static final boolean ASSERTS = false;
/*     */   protected transient long[] key;
/*     */   protected transient byte[] value;
/*     */   protected transient boolean[] used;
/*     */   protected final float f;
/*     */   protected transient int n;
/*     */   protected transient int maxFill;
/*     */   protected transient int mask;
/*     */   protected int size;
/*     */   protected volatile transient Long2ByteMap.FastEntrySet entries;
/*     */   protected volatile transient LongSet keys;
/*     */   protected volatile transient ByteCollection values;
/*     */   protected LongHash.Strategy strategy;
/*     */ 
/*     */   public Long2ByteOpenCustomHashMap(int expected, float f, LongHash.Strategy strategy)
/*     */   {
/* 111 */     this.strategy = strategy;
/* 112 */     if ((f <= 0.0F) || (f > 1.0F)) throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
/* 113 */     if (expected < 0) throw new IllegalArgumentException("The expected number of elements must be nonnegative");
/* 114 */     this.f = f;
/* 115 */     this.n = HashCommon.arraySize(expected, f);
/* 116 */     this.mask = (this.n - 1);
/* 117 */     this.maxFill = HashCommon.maxFill(this.n, f);
/* 118 */     this.key = new long[this.n];
/* 119 */     this.value = new byte[this.n];
/* 120 */     this.used = new boolean[this.n];
/*     */   }
/*     */ 
/*     */   public Long2ByteOpenCustomHashMap(int expected, LongHash.Strategy strategy)
/*     */   {
/* 128 */     this(expected, 0.75F, strategy);
/*     */   }
/*     */ 
/*     */   public Long2ByteOpenCustomHashMap(LongHash.Strategy strategy)
/*     */   {
/* 135 */     this(16, 0.75F, strategy);
/*     */   }
/*     */ 
/*     */   public Long2ByteOpenCustomHashMap(Map<? extends Long, ? extends Byte> m, float f, LongHash.Strategy strategy)
/*     */   {
/* 144 */     this(m.size(), f, strategy);
/* 145 */     putAll(m);
/*     */   }
/*     */ 
/*     */   public Long2ByteOpenCustomHashMap(Map<? extends Long, ? extends Byte> m, LongHash.Strategy strategy)
/*     */   {
/* 153 */     this(m, 0.75F, strategy);
/*     */   }
/*     */ 
/*     */   public Long2ByteOpenCustomHashMap(Long2ByteMap m, float f, LongHash.Strategy strategy)
/*     */   {
/* 162 */     this(m.size(), f, strategy);
/* 163 */     putAll(m);
/*     */   }
/*     */ 
/*     */   public Long2ByteOpenCustomHashMap(Long2ByteMap m, LongHash.Strategy strategy)
/*     */   {
/* 171 */     this(m, 0.75F, strategy);
/*     */   }
/*     */ 
/*     */   public Long2ByteOpenCustomHashMap(long[] k, byte[] v, float f, LongHash.Strategy strategy)
/*     */   {
/* 182 */     this(k.length, f, strategy);
/* 183 */     if (k.length != v.length) throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
/* 184 */     for (int i = 0; i < k.length; i++) put(k[i], v[i]);
/*     */   }
/*     */ 
/*     */   public Long2ByteOpenCustomHashMap(long[] k, byte[] v, LongHash.Strategy strategy)
/*     */   {
/* 194 */     this(k, v, 0.75F, strategy);
/*     */   }
/*     */ 
/*     */   public LongHash.Strategy strategy()
/*     */   {
/* 201 */     return this.strategy;
/*     */   }
/*     */ 
/*     */   public byte put(long k, byte v)
/*     */   {
/* 209 */     int pos = HashCommon.murmurHash3(this.strategy.hashCode(k)) & this.mask;
/*     */ 
/* 211 */     while (this.used[pos] != 0) {
/* 212 */       if (this.strategy.equals(this.key[pos], k)) {
/* 213 */         byte oldValue = this.value[pos];
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
/* 224 */     return this.defRetValue;
/*     */   }
/*     */   public Byte put(Long ok, Byte ov) {
/* 227 */     byte v = ov.byteValue();
/* 228 */     long k = ok.longValue();
/*     */ 
/* 230 */     int pos = HashCommon.murmurHash3(this.strategy.hashCode(k)) & this.mask;
/*     */ 
/* 232 */     while (this.used[pos] != 0) {
/* 233 */       if (this.strategy.equals(this.key[pos], k)) {
/* 234 */         Byte oldValue = Byte.valueOf(this.value[pos]);
/* 235 */         this.value[pos] = v;
/* 236 */         return oldValue;
/*     */       }
/* 238 */       pos = pos + 1 & this.mask;
/*     */     }
/* 240 */     this.used[pos] = true;
/* 241 */     this.key[pos] = k;
/* 242 */     this.value[pos] = v;
/* 243 */     if (++this.size >= this.maxFill) rehash(HashCommon.arraySize(this.size + 1, this.f));
/*     */ 
/* 245 */     return null;
/*     */   }
/*     */ 
/*     */   public byte add(long k, byte incr)
/*     */   {
/* 260 */     int pos = HashCommon.murmurHash3(this.strategy.hashCode(k)) & this.mask;
/*     */ 
/* 262 */     while (this.used[pos] != 0) {
/* 263 */       if (this.strategy.equals(this.key[pos], k)) {
/* 264 */         byte oldValue = this.value[pos];
/*     */         int tmp65_63 = pos;
/*     */         byte[] tmp65_60 = this.value; tmp65_60[tmp65_63] = ((byte)(tmp65_60[tmp65_63] + incr));
/* 266 */         return tmp65_63;
/*     */       }
/* 268 */       pos = pos + 1 & this.mask;
/*     */     }
/* 270 */     this.used[pos] = true;
/* 271 */     this.key[pos] = k;
/* 272 */     this.value[pos] = ((byte)(this.defRetValue + incr));
/* 273 */     if (++this.size >= this.maxFill) rehash(HashCommon.arraySize(this.size + 1, this.f));
/*     */ 
/* 275 */     return this.defRetValue;
/*     */   }
/*     */ 
/*     */   protected final int shiftKeys(int pos)
/*     */   {
/*     */     int last;
/*     */     while (true)
/*     */     {
/* 287 */       pos = (last = pos) + 1 & this.mask;
/* 288 */       while (this.used[pos] != 0) {
/* 289 */         int slot = HashCommon.murmurHash3(this.strategy.hashCode(this.key[pos])) & this.mask;
/* 290 */         if (last <= pos ? (last < slot) && (slot <= pos) : (last >= slot) && (slot > pos)) break;
/* 291 */         pos = pos + 1 & this.mask;
/*     */       }
/* 293 */       if (this.used[pos] == 0) break;
/* 294 */       this.key[last] = this.key[pos];
/* 295 */       this.value[last] = this.value[pos];
/*     */     }
/* 297 */     this.used[last] = false;
/* 298 */     return last;
/*     */   }
/*     */ 
/*     */   public byte remove(long k)
/*     */   {
/* 303 */     int pos = HashCommon.murmurHash3(this.strategy.hashCode(k)) & this.mask;
/*     */ 
/* 305 */     while (this.used[pos] != 0) {
/* 306 */       if (this.strategy.equals(this.key[pos], k)) {
/* 307 */         this.size -= 1;
/* 308 */         byte v = this.value[pos];
/* 309 */         shiftKeys(pos);
/* 310 */         return v;
/*     */       }
/* 312 */       pos = pos + 1 & this.mask;
/*     */     }
/* 314 */     return this.defRetValue;
/*     */   }
/*     */ 
/*     */   public Byte remove(Object ok) {
/* 318 */     long k = ((Long)ok).longValue();
/*     */ 
/* 320 */     int pos = HashCommon.murmurHash3(this.strategy.hashCode(k)) & this.mask;
/*     */ 
/* 322 */     while (this.used[pos] != 0) {
/* 323 */       if (this.strategy.equals(this.key[pos], k)) {
/* 324 */         this.size -= 1;
/* 325 */         byte v = this.value[pos];
/* 326 */         shiftKeys(pos);
/* 327 */         return Byte.valueOf(v);
/*     */       }
/* 329 */       pos = pos + 1 & this.mask;
/*     */     }
/* 331 */     return null;
/*     */   }
/*     */   public Byte get(Long ok) {
/* 334 */     long k = ok.longValue();
/*     */ 
/* 336 */     int pos = HashCommon.murmurHash3(this.strategy.hashCode(k)) & this.mask;
/*     */ 
/* 338 */     while (this.used[pos] != 0) {
/* 339 */       if (this.strategy.equals(this.key[pos], k)) return Byte.valueOf(this.value[pos]);
/* 340 */       pos = pos + 1 & this.mask;
/*     */     }
/* 342 */     return null;
/*     */   }
/*     */ 
/*     */   public byte get(long k)
/*     */   {
/* 347 */     int pos = HashCommon.murmurHash3(this.strategy.hashCode(k)) & this.mask;
/*     */ 
/* 349 */     while (this.used[pos] != 0) {
/* 350 */       if (this.strategy.equals(this.key[pos], k)) return this.value[pos];
/* 351 */       pos = pos + 1 & this.mask;
/*     */     }
/* 353 */     return this.defRetValue;
/*     */   }
/*     */ 
/*     */   public boolean containsKey(long k)
/*     */   {
/* 358 */     int pos = HashCommon.murmurHash3(this.strategy.hashCode(k)) & this.mask;
/*     */ 
/* 360 */     while (this.used[pos] != 0) {
/* 361 */       if (this.strategy.equals(this.key[pos], k)) return true;
/* 362 */       pos = pos + 1 & this.mask;
/*     */     }
/* 364 */     return false;
/*     */   }
/*     */   public boolean containsValue(byte v) {
/* 367 */     byte[] value = this.value;
/* 368 */     boolean[] used = this.used;
/* 369 */     for (int i = this.n; i-- != 0; return true) label16: if ((used[i] == 0) || (value[i] != v))
/*     */         break label16; return false;
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 379 */     if (this.size == 0) return;
/* 380 */     this.size = 0;
/* 381 */     BooleanArrays.fill(this.used, false);
/*     */   }
/*     */ 
/*     */   public int size() {
/* 385 */     return this.size;
/*     */   }
/*     */   public boolean isEmpty() {
/* 388 */     return this.size == 0;
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
/* 405 */     return 16;
/*     */   }
/*     */ 
/*     */   public Long2ByteMap.FastEntrySet long2ByteEntrySet()
/*     */   {
/* 611 */     if (this.entries == null) this.entries = new MapEntrySet(null);
/* 612 */     return this.entries;
/*     */   }
/*     */ 
/*     */   public LongSet keySet()
/*     */   {
/* 645 */     if (this.keys == null) this.keys = new KeySet(null);
/* 646 */     return this.keys;
/*     */   }
/*     */ 
/*     */   public ByteCollection values()
/*     */   {
/* 660 */     if (this.values == null) this.values = new AbstractByteCollection() {
/*     */         public ByteIterator iterator() {
/* 662 */           return new Long2ByteOpenCustomHashMap.ValueIterator(Long2ByteOpenCustomHashMap.this);
/*     */         }
/*     */         public int size() {
/* 665 */           return Long2ByteOpenCustomHashMap.this.size;
/*     */         }
/*     */         public boolean contains(byte v) {
/* 668 */           return Long2ByteOpenCustomHashMap.this.containsValue(v);
/*     */         }
/*     */         public void clear() {
/* 671 */           Long2ByteOpenCustomHashMap.this.clear();
/*     */         }
/*     */       };
/* 674 */     return this.values;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public boolean rehash()
/*     */   {
/* 688 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean trim()
/*     */   {
/* 703 */     int l = HashCommon.arraySize(this.size, this.f);
/* 704 */     if (l >= this.n) return true; try
/*     */     {
/* 706 */       rehash(l);
/*     */     } catch (OutOfMemoryError cantDoIt) {
/* 708 */       return false;
/* 709 */     }return true;
/*     */   }
/*     */ 
/*     */   public boolean trim(int n)
/*     */   {
/* 730 */     int l = HashCommon.nextPowerOfTwo((int)Math.ceil(n / this.f));
/* 731 */     if (this.n <= l) return true; try
/*     */     {
/* 733 */       rehash(l);
/*     */     } catch (OutOfMemoryError cantDoIt) {
/* 735 */       return false;
/* 736 */     }return true;
/*     */   }
/*     */ 
/*     */   protected void rehash(int newN)
/*     */   {
/* 749 */     int i = 0;
/* 750 */     boolean[] used = this.used;
/*     */ 
/* 752 */     long[] key = this.key;
/* 753 */     byte[] value = this.value;
/* 754 */     int newMask = newN - 1;
/* 755 */     long[] newKey = new long[newN];
/* 756 */     byte[] newValue = new byte[newN];
/* 757 */     boolean[] newUsed = new boolean[newN];
/* 758 */     for (int j = this.size; j-- != 0; ) {
/* 759 */       while (used[i] == 0) i++;
/* 760 */       long k = key[i];
/* 761 */       int pos = HashCommon.murmurHash3(this.strategy.hashCode(k)) & newMask;
/* 762 */       while (newUsed[pos] != 0) pos = pos + 1 & newMask;
/* 763 */       newUsed[pos] = true;
/* 764 */       newKey[pos] = k;
/* 765 */       newValue[pos] = value[i];
/* 766 */       i++;
/*     */     }
/* 768 */     this.n = newN;
/* 769 */     this.mask = newMask;
/* 770 */     this.maxFill = HashCommon.maxFill(this.n, this.f);
/* 771 */     this.key = newKey;
/* 772 */     this.value = newValue;
/* 773 */     this.used = newUsed;
/*     */   }
/*     */ 
/*     */   public Long2ByteOpenCustomHashMap clone()
/*     */   {
/*     */     Long2ByteOpenCustomHashMap c;
/*     */     try
/*     */     {
/* 786 */       c = (Long2ByteOpenCustomHashMap)super.clone();
/*     */     }
/*     */     catch (CloneNotSupportedException cantHappen) {
/* 789 */       throw new InternalError();
/*     */     }
/* 791 */     c.keys = null;
/* 792 */     c.values = null;
/* 793 */     c.entries = null;
/* 794 */     c.key = ((long[])this.key.clone());
/* 795 */     c.value = ((byte[])this.value.clone());
/* 796 */     c.used = ((boolean[])this.used.clone());
/* 797 */     c.strategy = this.strategy;
/* 798 */     return c;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 810 */     int h = 0;
/* 811 */     int j = this.size; int i = 0; for (int t = 0; j-- != 0; ) {
/* 812 */       while (this.used[i] == 0) i++;
/* 813 */       t = this.strategy.hashCode(this.key[i]);
/* 814 */       t ^= this.value[i];
/* 815 */       h += t;
/* 816 */       i++;
/*     */     }
/* 818 */     return h;
/*     */   }
/*     */   private void writeObject(ObjectOutputStream s) throws IOException {
/* 821 */     long[] key = this.key;
/* 822 */     byte[] value = this.value;
/* 823 */     MapIterator i = new MapIterator(null);
/* 824 */     s.defaultWriteObject();
/* 825 */     for (int j = this.size; j-- != 0; ) {
/* 826 */       int e = i.nextEntry();
/* 827 */       s.writeLong(key[e]);
/* 828 */       s.writeByte(value[e]);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
/* 833 */     s.defaultReadObject();
/* 834 */     this.n = HashCommon.arraySize(this.size, this.f);
/* 835 */     this.maxFill = HashCommon.maxFill(this.n, this.f);
/* 836 */     this.mask = (this.n - 1);
/* 837 */     long[] key = this.key = new long[this.n];
/* 838 */     byte[] value = this.value = new byte[this.n];
/* 839 */     boolean[] used = this.used = new boolean[this.n];
/*     */ 
/* 842 */     int i = this.size; for (int pos = 0; i-- != 0; ) {
/* 843 */       long k = s.readLong();
/* 844 */       byte v = s.readByte();
/* 845 */       pos = HashCommon.murmurHash3(this.strategy.hashCode(k)) & this.mask;
/* 846 */       while (used[pos] != 0) pos = pos + 1 & this.mask;
/* 847 */       used[pos] = true;
/* 848 */       key[pos] = k;
/* 849 */       value[pos] = v;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void checkTable()
/*     */   {
/*     */   }
/*     */ 
/*     */   private final class ValueIterator extends Long2ByteOpenCustomHashMap.MapIterator
/*     */     implements ByteIterator
/*     */   {
/*     */     public ValueIterator()
/*     */     {
/* 655 */       super(null); } 
/* 656 */     public byte nextByte() { return Long2ByteOpenCustomHashMap.this.value[nextEntry()]; } 
/* 657 */     public Byte next() { return Byte.valueOf(Long2ByteOpenCustomHashMap.this.value[nextEntry()]); }
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
/* 627 */       return new Long2ByteOpenCustomHashMap.KeyIterator(Long2ByteOpenCustomHashMap.this);
/*     */     }
/*     */     public int size() {
/* 630 */       return Long2ByteOpenCustomHashMap.this.size;
/*     */     }
/*     */     public boolean contains(long k) {
/* 633 */       return Long2ByteOpenCustomHashMap.this.containsKey(k);
/*     */     }
/*     */     public boolean remove(long k) {
/* 636 */       int oldSize = Long2ByteOpenCustomHashMap.this.size;
/* 637 */       Long2ByteOpenCustomHashMap.this.remove(k);
/* 638 */       return Long2ByteOpenCustomHashMap.this.size != oldSize;
/*     */     }
/*     */     public void clear() {
/* 641 */       Long2ByteOpenCustomHashMap.this.clear();
/*     */     }
/*     */   }
/*     */ 
/*     */   private final class KeyIterator extends Long2ByteOpenCustomHashMap.MapIterator
/*     */     implements LongIterator
/*     */   {
/*     */     public KeyIterator()
/*     */     {
/* 621 */       super(null); } 
/* 622 */     public long nextLong() { return Long2ByteOpenCustomHashMap.this.key[nextEntry()]; } 
/* 623 */     public Long next() { return Long.valueOf(Long2ByteOpenCustomHashMap.this.key[nextEntry()]); }
/*     */ 
/*     */   }
/*     */ 
/*     */   private final class MapEntrySet extends AbstractObjectSet<Long2ByteMap.Entry>
/*     */     implements Long2ByteMap.FastEntrySet
/*     */   {
/*     */     private MapEntrySet()
/*     */     {
/*     */     }
/*     */ 
/*     */     public ObjectIterator<Long2ByteMap.Entry> iterator()
/*     */     {
/* 567 */       return new Long2ByteOpenCustomHashMap.EntryIterator(Long2ByteOpenCustomHashMap.this, null);
/*     */     }
/*     */     public ObjectIterator<Long2ByteMap.Entry> fastIterator() {
/* 570 */       return new Long2ByteOpenCustomHashMap.FastEntryIterator(Long2ByteOpenCustomHashMap.this, null);
/*     */     }
/*     */ 
/*     */     public boolean contains(Object o) {
/* 574 */       if (!(o instanceof Map.Entry)) return false;
/* 575 */       Map.Entry e = (Map.Entry)o;
/* 576 */       long k = ((Long)e.getKey()).longValue();
/*     */ 
/* 578 */       int pos = HashCommon.murmurHash3(Long2ByteOpenCustomHashMap.this.strategy.hashCode(k)) & Long2ByteOpenCustomHashMap.this.mask;
/*     */ 
/* 580 */       while (Long2ByteOpenCustomHashMap.this.used[pos] != 0) {
/* 581 */         if (Long2ByteOpenCustomHashMap.this.strategy.equals(Long2ByteOpenCustomHashMap.this.key[pos], k)) return Long2ByteOpenCustomHashMap.this.value[pos] == ((Byte)e.getValue()).byteValue();
/* 582 */         pos = pos + 1 & Long2ByteOpenCustomHashMap.this.mask;
/*     */       }
/* 584 */       return false;
/*     */     }
/*     */ 
/*     */     public boolean remove(Object o) {
/* 588 */       if (!(o instanceof Map.Entry)) return false;
/* 589 */       Map.Entry e = (Map.Entry)o;
/* 590 */       long k = ((Long)e.getKey()).longValue();
/*     */ 
/* 592 */       int pos = HashCommon.murmurHash3(Long2ByteOpenCustomHashMap.this.strategy.hashCode(k)) & Long2ByteOpenCustomHashMap.this.mask;
/*     */ 
/* 594 */       while (Long2ByteOpenCustomHashMap.this.used[pos] != 0) {
/* 595 */         if (Long2ByteOpenCustomHashMap.this.strategy.equals(Long2ByteOpenCustomHashMap.this.key[pos], k)) {
/* 596 */           Long2ByteOpenCustomHashMap.this.remove(e.getKey());
/* 597 */           return true;
/*     */         }
/* 599 */         pos = pos + 1 & Long2ByteOpenCustomHashMap.this.mask;
/*     */       }
/* 601 */       return false;
/*     */     }
/*     */     public int size() {
/* 604 */       return Long2ByteOpenCustomHashMap.this.size;
/*     */     }
/*     */     public void clear() {
/* 607 */       Long2ByteOpenCustomHashMap.this.clear();
/*     */     }
/*     */   }
/*     */ 
/*     */   private class FastEntryIterator extends Long2ByteOpenCustomHashMap.MapIterator
/*     */     implements ObjectIterator<Long2ByteMap.Entry>
/*     */   {
/* 557 */     final AbstractLong2ByteMap.BasicEntry entry = new AbstractLong2ByteMap.BasicEntry(0L, (byte)0);
/*     */ 
/*     */     private FastEntryIterator()
/*     */     {
/* 556 */       super(null);
/*     */     }
/*     */     public AbstractLong2ByteMap.BasicEntry next() {
/* 559 */       int e = nextEntry();
/* 560 */       this.entry.key = Long2ByteOpenCustomHashMap.this.key[e];
/* 561 */       this.entry.value = Long2ByteOpenCustomHashMap.this.value[e];
/* 562 */       return this.entry;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class EntryIterator extends Long2ByteOpenCustomHashMap.MapIterator
/*     */     implements ObjectIterator<Long2ByteMap.Entry>
/*     */   {
/*     */     private Long2ByteOpenCustomHashMap.MapEntry entry;
/*     */ 
/*     */     private EntryIterator()
/*     */     {
/* 545 */       super(null);
/*     */     }
/*     */     public Long2ByteMap.Entry next() {
/* 548 */       return this.entry = new Long2ByteOpenCustomHashMap.MapEntry(Long2ByteOpenCustomHashMap.this, nextEntry());
/*     */     }
/*     */ 
/*     */     public void remove() {
/* 552 */       super.remove();
/* 553 */       Long2ByteOpenCustomHashMap.MapEntry.access$102(this.entry, -1);
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
/* 454 */       this.pos = Long2ByteOpenCustomHashMap.this.n;
/*     */ 
/* 457 */       this.last = -1;
/*     */ 
/* 459 */       this.c = Long2ByteOpenCustomHashMap.this.size;
/*     */ 
/* 464 */       boolean[] used = Long2ByteOpenCustomHashMap.this.used;
/* 465 */       while ((this.c != 0) && (used[(--this.pos)] == 0));
/*     */     }
/*     */ 
/*     */     public boolean hasNext()
/*     */     {
/* 468 */       return this.c != 0;
/*     */     }
/*     */     public int nextEntry() {
/* 471 */       if (!hasNext()) throw new NoSuchElementException();
/* 472 */       this.c -= 1;
/*     */ 
/* 474 */       if (this.pos < 0) {
/* 475 */         long k = this.wrapped.getLong(-(this.last = --this.pos) - 2);
/*     */ 
/* 477 */         int pos = HashCommon.murmurHash3(Long2ByteOpenCustomHashMap.this.strategy.hashCode(k)) & Long2ByteOpenCustomHashMap.this.mask;
/*     */ 
/* 479 */         while (Long2ByteOpenCustomHashMap.this.used[pos] != 0) {
/* 480 */           if (Long2ByteOpenCustomHashMap.this.strategy.equals(Long2ByteOpenCustomHashMap.this.key[pos], k)) return pos;
/* 481 */           pos = pos + 1 & Long2ByteOpenCustomHashMap.this.mask;
/*     */         }
/*     */       }
/* 484 */       this.last = this.pos;
/*     */ 
/* 486 */       if (this.c != 0) {
/* 487 */         boolean[] used = Long2ByteOpenCustomHashMap.this.used;
/* 488 */         while ((this.pos-- != 0) && (used[this.pos] == 0));
/*     */       }
/* 491 */       return this.last;
/*     */     }
/*     */ 
/*     */     protected final int shiftKeys(int pos)
/*     */     {
/*     */       int last;
/*     */       while (true)
/*     */       {
/* 504 */         pos = (last = pos) + 1 & Long2ByteOpenCustomHashMap.this.mask;
/* 505 */         while (Long2ByteOpenCustomHashMap.this.used[pos] != 0) {
/* 506 */           int slot = HashCommon.murmurHash3(Long2ByteOpenCustomHashMap.this.strategy.hashCode(Long2ByteOpenCustomHashMap.this.key[pos])) & Long2ByteOpenCustomHashMap.this.mask;
/* 507 */           if (last <= pos ? (last < slot) && (slot <= pos) : (last >= slot) && (slot > pos)) break;
/* 508 */           pos = pos + 1 & Long2ByteOpenCustomHashMap.this.mask;
/*     */         }
/* 510 */         if (Long2ByteOpenCustomHashMap.this.used[pos] == 0) break;
/* 511 */         if (pos < last)
/*     */         {
/* 513 */           if (this.wrapped == null) this.wrapped = new LongArrayList();
/* 514 */           this.wrapped.add(Long2ByteOpenCustomHashMap.this.key[pos]);
/*     */         }
/* 516 */         Long2ByteOpenCustomHashMap.this.key[last] = Long2ByteOpenCustomHashMap.this.key[pos];
/* 517 */         Long2ByteOpenCustomHashMap.this.value[last] = Long2ByteOpenCustomHashMap.this.value[pos];
/*     */       }
/* 519 */       Long2ByteOpenCustomHashMap.this.used[last] = false;
/* 520 */       return last;
/*     */     }
/*     */ 
/*     */     public void remove() {
/* 524 */       if (this.last == -1) throw new IllegalStateException();
/* 525 */       if (this.pos < -1)
/*     */       {
/* 527 */         Long2ByteOpenCustomHashMap.this.remove(this.wrapped.getLong(-this.pos - 2));
/* 528 */         this.last = -1;
/* 529 */         return;
/*     */       }
/* 531 */       Long2ByteOpenCustomHashMap.this.size -= 1;
/* 532 */       if ((shiftKeys(this.last) == this.pos) && (this.c > 0)) {
/* 533 */         this.c += 1;
/* 534 */         nextEntry();
/*     */       }
/* 536 */       this.last = -1;
/*     */     }
/*     */ 
/*     */     public int skip(int n) {
/* 540 */       int i = n;
/* 541 */       while ((i-- != 0) && (hasNext())) nextEntry();
/* 542 */       return n - i - 1;
/*     */     }
/*     */   }
/*     */ 
/*     */   private final class MapEntry
/*     */     implements Long2ByteMap.Entry, Map.Entry<Long, Byte>
/*     */   {
/*     */     private int index;
/*     */ 
/*     */     MapEntry(int index)
/*     */     {
/* 415 */       this.index = index;
/*     */     }
/*     */     public Long getKey() {
/* 418 */       return Long.valueOf(Long2ByteOpenCustomHashMap.this.key[this.index]);
/*     */     }
/*     */     public long getLongKey() {
/* 421 */       return Long2ByteOpenCustomHashMap.this.key[this.index];
/*     */     }
/*     */     public Byte getValue() {
/* 424 */       return Byte.valueOf(Long2ByteOpenCustomHashMap.this.value[this.index]);
/*     */     }
/*     */     public byte getByteValue() {
/* 427 */       return Long2ByteOpenCustomHashMap.this.value[this.index];
/*     */     }
/*     */     public byte setValue(byte v) {
/* 430 */       byte oldValue = Long2ByteOpenCustomHashMap.this.value[this.index];
/* 431 */       Long2ByteOpenCustomHashMap.this.value[this.index] = v;
/* 432 */       return oldValue;
/*     */     }
/*     */     public Byte setValue(Byte v) {
/* 435 */       return Byte.valueOf(setValue(v.byteValue()));
/*     */     }
/*     */ 
/*     */     public boolean equals(Object o) {
/* 439 */       if (!(o instanceof Map.Entry)) return false;
/* 440 */       Map.Entry e = (Map.Entry)o;
/* 441 */       return (Long2ByteOpenCustomHashMap.this.strategy.equals(Long2ByteOpenCustomHashMap.this.key[this.index], ((Long)e.getKey()).longValue())) && (Long2ByteOpenCustomHashMap.this.value[this.index] == ((Byte)e.getValue()).byteValue());
/*     */     }
/*     */     public int hashCode() {
/* 444 */       return Long2ByteOpenCustomHashMap.this.strategy.hashCode(Long2ByteOpenCustomHashMap.this.key[this.index]) ^ Long2ByteOpenCustomHashMap.this.value[this.index];
/*     */     }
/*     */     public String toString() {
/* 447 */       return Long2ByteOpenCustomHashMap.this.key[this.index] + "=>" + Long2ByteOpenCustomHashMap.this.value[this.index];
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Raul\Desktop\StarMade\StarMade.jar
 * Qualified Name:     it.unimi.dsi.fastutil.longs.Long2ByteOpenCustomHashMap
 * JD-Core Version:    0.6.2
 */