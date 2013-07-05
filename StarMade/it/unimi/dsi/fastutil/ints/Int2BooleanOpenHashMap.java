/*     */ package it.unimi.dsi.fastutil.ints;
/*     */ 
/*     */ import it.unimi.dsi.fastutil.Hash;
/*     */ import it.unimi.dsi.fastutil.HashCommon;
/*     */ import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
/*     */ import it.unimi.dsi.fastutil.booleans.BooleanArrays;
/*     */ import it.unimi.dsi.fastutil.booleans.BooleanCollection;
/*     */ import it.unimi.dsi.fastutil.booleans.BooleanIterator;
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
/*     */ public class Int2BooleanOpenHashMap extends AbstractInt2BooleanMap
/*     */   implements Serializable, Cloneable, Hash
/*     */ {
/*     */   public static final long serialVersionUID = 0L;
/*     */   private static final boolean ASSERTS = false;
/*     */   protected transient int[] key;
/*     */   protected transient boolean[] value;
/*     */   protected transient boolean[] used;
/*     */   protected final float f;
/*     */   protected transient int n;
/*     */   protected transient int maxFill;
/*     */   protected transient int mask;
/*     */   protected int size;
/*     */   protected volatile transient Int2BooleanMap.FastEntrySet entries;
/*     */   protected volatile transient IntSet keys;
/*     */   protected volatile transient BooleanCollection values;
/*     */ 
/*     */   public Int2BooleanOpenHashMap(int expected, float f)
/*     */   {
/* 107 */     if ((f <= 0.0F) || (f > 1.0F)) throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
/* 108 */     if (expected < 0) throw new IllegalArgumentException("The expected number of elements must be nonnegative");
/* 109 */     this.f = f;
/* 110 */     this.n = HashCommon.arraySize(expected, f);
/* 111 */     this.mask = (this.n - 1);
/* 112 */     this.maxFill = HashCommon.maxFill(this.n, f);
/* 113 */     this.key = new int[this.n];
/* 114 */     this.value = new boolean[this.n];
/* 115 */     this.used = new boolean[this.n];
/*     */   }
/*     */ 
/*     */   public Int2BooleanOpenHashMap(int expected)
/*     */   {
/* 122 */     this(expected, 0.75F);
/*     */   }
/*     */ 
/*     */   public Int2BooleanOpenHashMap()
/*     */   {
/* 128 */     this(16, 0.75F);
/*     */   }
/*     */ 
/*     */   public Int2BooleanOpenHashMap(Map<? extends Integer, ? extends Boolean> m, float f)
/*     */   {
/* 136 */     this(m.size(), f);
/* 137 */     putAll(m);
/*     */   }
/*     */ 
/*     */   public Int2BooleanOpenHashMap(Map<? extends Integer, ? extends Boolean> m)
/*     */   {
/* 144 */     this(m, 0.75F);
/*     */   }
/*     */ 
/*     */   public Int2BooleanOpenHashMap(Int2BooleanMap m, float f)
/*     */   {
/* 152 */     this(m.size(), f);
/* 153 */     putAll(m);
/*     */   }
/*     */ 
/*     */   public Int2BooleanOpenHashMap(Int2BooleanMap m)
/*     */   {
/* 160 */     this(m, 0.75F);
/*     */   }
/*     */ 
/*     */   public Int2BooleanOpenHashMap(int[] k, boolean[] v, float f)
/*     */   {
/* 170 */     this(k.length, f);
/* 171 */     if (k.length != v.length) throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
/* 172 */     for (int i = 0; i < k.length; i++) put(k[i], v[i]);
/*     */   }
/*     */ 
/*     */   public Int2BooleanOpenHashMap(int[] k, boolean[] v)
/*     */   {
/* 181 */     this(k, v, 0.75F);
/*     */   }
/*     */ 
/*     */   public boolean put(int k, boolean v)
/*     */   {
/* 189 */     int pos = HashCommon.murmurHash3(k) & this.mask;
/*     */ 
/* 191 */     while (this.used[pos] != 0) {
/* 192 */       if (this.key[pos] == k) {
/* 193 */         boolean oldValue = this.value[pos];
/* 194 */         this.value[pos] = v;
/* 195 */         return oldValue;
/*     */       }
/* 197 */       pos = pos + 1 & this.mask;
/*     */     }
/* 199 */     this.used[pos] = true;
/* 200 */     this.key[pos] = k;
/* 201 */     this.value[pos] = v;
/* 202 */     if (++this.size >= this.maxFill) rehash(HashCommon.arraySize(this.size + 1, this.f));
/*     */ 
/* 204 */     return this.defRetValue;
/*     */   }
/*     */   public Boolean put(Integer ok, Boolean ov) {
/* 207 */     boolean v = ov.booleanValue();
/* 208 */     int k = ok.intValue();
/*     */ 
/* 210 */     int pos = HashCommon.murmurHash3(k) & this.mask;
/*     */ 
/* 212 */     while (this.used[pos] != 0) {
/* 213 */       if (this.key[pos] == k) {
/* 214 */         Boolean oldValue = Boolean.valueOf(this.value[pos]);
/* 215 */         this.value[pos] = v;
/* 216 */         return oldValue;
/*     */       }
/* 218 */       pos = pos + 1 & this.mask;
/*     */     }
/* 220 */     this.used[pos] = true;
/* 221 */     this.key[pos] = k;
/* 222 */     this.value[pos] = v;
/* 223 */     if (++this.size >= this.maxFill) rehash(HashCommon.arraySize(this.size + 1, this.f));
/*     */ 
/* 225 */     return null;
/*     */   }
/*     */ 
/*     */   protected final int shiftKeys(int pos)
/*     */   {
/*     */     int last;
/*     */     while (true)
/*     */     {
/* 237 */       pos = (last = pos) + 1 & this.mask;
/* 238 */       while (this.used[pos] != 0) {
/* 239 */         int slot = HashCommon.murmurHash3(this.key[pos]) & this.mask;
/* 240 */         if (last <= pos ? (last < slot) && (slot <= pos) : (last >= slot) && (slot > pos)) break;
/* 241 */         pos = pos + 1 & this.mask;
/*     */       }
/* 243 */       if (this.used[pos] == 0) break;
/* 244 */       this.key[last] = this.key[pos];
/* 245 */       this.value[last] = this.value[pos];
/*     */     }
/* 247 */     this.used[last] = false;
/* 248 */     return last;
/*     */   }
/*     */ 
/*     */   public boolean remove(int k)
/*     */   {
/* 253 */     int pos = HashCommon.murmurHash3(k) & this.mask;
/*     */ 
/* 255 */     while (this.used[pos] != 0) {
/* 256 */       if (this.key[pos] == k) {
/* 257 */         this.size -= 1;
/* 258 */         boolean v = this.value[pos];
/* 259 */         shiftKeys(pos);
/* 260 */         return v;
/*     */       }
/* 262 */       pos = pos + 1 & this.mask;
/*     */     }
/* 264 */     return this.defRetValue;
/*     */   }
/*     */ 
/*     */   public Boolean remove(Object ok) {
/* 268 */     int k = ((Integer)ok).intValue();
/*     */ 
/* 270 */     int pos = HashCommon.murmurHash3(k) & this.mask;
/*     */ 
/* 272 */     while (this.used[pos] != 0) {
/* 273 */       if (this.key[pos] == k) {
/* 274 */         this.size -= 1;
/* 275 */         boolean v = this.value[pos];
/* 276 */         shiftKeys(pos);
/* 277 */         return Boolean.valueOf(v);
/*     */       }
/* 279 */       pos = pos + 1 & this.mask;
/*     */     }
/* 281 */     return null;
/*     */   }
/*     */   public Boolean get(Integer ok) {
/* 284 */     int k = ok.intValue();
/*     */ 
/* 286 */     int pos = HashCommon.murmurHash3(k) & this.mask;
/*     */ 
/* 288 */     while (this.used[pos] != 0) {
/* 289 */       if (this.key[pos] == k) return Boolean.valueOf(this.value[pos]);
/* 290 */       pos = pos + 1 & this.mask;
/*     */     }
/* 292 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean get(int k)
/*     */   {
/* 297 */     int pos = HashCommon.murmurHash3(k) & this.mask;
/*     */ 
/* 299 */     while (this.used[pos] != 0) {
/* 300 */       if (this.key[pos] == k) return this.value[pos];
/* 301 */       pos = pos + 1 & this.mask;
/*     */     }
/* 303 */     return this.defRetValue;
/*     */   }
/*     */ 
/*     */   public boolean containsKey(int k)
/*     */   {
/* 308 */     int pos = HashCommon.murmurHash3(k) & this.mask;
/*     */ 
/* 310 */     while (this.used[pos] != 0) {
/* 311 */       if (this.key[pos] == k) return true;
/* 312 */       pos = pos + 1 & this.mask;
/*     */     }
/* 314 */     return false;
/*     */   }
/*     */   public boolean containsValue(boolean v) {
/* 317 */     boolean[] value = this.value;
/* 318 */     boolean[] used = this.used;
/* 319 */     for (int i = this.n; i-- != 0; return true) label16: if ((used[i] == 0) || (value[i] != v))
/*     */         break label16; return false;
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 329 */     if (this.size == 0) return;
/* 330 */     this.size = 0;
/* 331 */     BooleanArrays.fill(this.used, false);
/*     */   }
/*     */ 
/*     */   public int size() {
/* 335 */     return this.size;
/*     */   }
/*     */   public boolean isEmpty() {
/* 338 */     return this.size == 0;
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
/* 355 */     return 16;
/*     */   }
/*     */ 
/*     */   public Int2BooleanMap.FastEntrySet int2BooleanEntrySet()
/*     */   {
/* 561 */     if (this.entries == null) this.entries = new MapEntrySet(null);
/* 562 */     return this.entries;
/*     */   }
/*     */ 
/*     */   public IntSet keySet()
/*     */   {
/* 595 */     if (this.keys == null) this.keys = new KeySet(null);
/* 596 */     return this.keys;
/*     */   }
/*     */ 
/*     */   public BooleanCollection values()
/*     */   {
/* 610 */     if (this.values == null) this.values = new AbstractBooleanCollection() {
/*     */         public BooleanIterator iterator() {
/* 612 */           return new Int2BooleanOpenHashMap.ValueIterator(Int2BooleanOpenHashMap.this);
/*     */         }
/*     */         public int size() {
/* 615 */           return Int2BooleanOpenHashMap.this.size;
/*     */         }
/*     */         public boolean contains(boolean v) {
/* 618 */           return Int2BooleanOpenHashMap.this.containsValue(v);
/*     */         }
/*     */         public void clear() {
/* 621 */           Int2BooleanOpenHashMap.this.clear();
/*     */         }
/*     */       };
/* 624 */     return this.values;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public boolean rehash()
/*     */   {
/* 638 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean trim()
/*     */   {
/* 653 */     int l = HashCommon.arraySize(this.size, this.f);
/* 654 */     if (l >= this.n) return true; try
/*     */     {
/* 656 */       rehash(l);
/*     */     } catch (OutOfMemoryError cantDoIt) {
/* 658 */       return false;
/* 659 */     }return true;
/*     */   }
/*     */ 
/*     */   public boolean trim(int n)
/*     */   {
/* 680 */     int l = HashCommon.nextPowerOfTwo((int)Math.ceil(n / this.f));
/* 681 */     if (this.n <= l) return true; try
/*     */     {
/* 683 */       rehash(l);
/*     */     } catch (OutOfMemoryError cantDoIt) {
/* 685 */       return false;
/* 686 */     }return true;
/*     */   }
/*     */ 
/*     */   protected void rehash(int newN)
/*     */   {
/* 699 */     int i = 0;
/* 700 */     boolean[] used = this.used;
/*     */ 
/* 702 */     int[] key = this.key;
/* 703 */     boolean[] value = this.value;
/* 704 */     int newMask = newN - 1;
/* 705 */     int[] newKey = new int[newN];
/* 706 */     boolean[] newValue = new boolean[newN];
/* 707 */     boolean[] newUsed = new boolean[newN];
/* 708 */     for (int j = this.size; j-- != 0; ) {
/* 709 */       while (used[i] == 0) i++;
/* 710 */       int k = key[i];
/* 711 */       int pos = HashCommon.murmurHash3(k) & newMask;
/* 712 */       while (newUsed[pos] != 0) pos = pos + 1 & newMask;
/* 713 */       newUsed[pos] = true;
/* 714 */       newKey[pos] = k;
/* 715 */       newValue[pos] = value[i];
/* 716 */       i++;
/*     */     }
/* 718 */     this.n = newN;
/* 719 */     this.mask = newMask;
/* 720 */     this.maxFill = HashCommon.maxFill(this.n, this.f);
/* 721 */     this.key = newKey;
/* 722 */     this.value = newValue;
/* 723 */     this.used = newUsed;
/*     */   }
/*     */ 
/*     */   public Int2BooleanOpenHashMap clone()
/*     */   {
/*     */     Int2BooleanOpenHashMap c;
/*     */     try
/*     */     {
/* 736 */       c = (Int2BooleanOpenHashMap)super.clone();
/*     */     }
/*     */     catch (CloneNotSupportedException cantHappen) {
/* 739 */       throw new InternalError();
/*     */     }
/* 741 */     c.keys = null;
/* 742 */     c.values = null;
/* 743 */     c.entries = null;
/* 744 */     c.key = ((int[])this.key.clone());
/* 745 */     c.value = ((boolean[])this.value.clone());
/* 746 */     c.used = ((boolean[])this.used.clone());
/* 747 */     return c;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 759 */     int h = 0;
/* 760 */     int j = this.size; int i = 0; for (int t = 0; j-- != 0; ) {
/* 761 */       while (this.used[i] == 0) i++;
/* 762 */       t = this.key[i];
/* 763 */       t ^= (this.value[i] != 0 ? 1231 : 1237);
/* 764 */       h += t;
/* 765 */       i++;
/*     */     }
/* 767 */     return h;
/*     */   }
/*     */   private void writeObject(ObjectOutputStream s) throws IOException {
/* 770 */     int[] key = this.key;
/* 771 */     boolean[] value = this.value;
/* 772 */     MapIterator i = new MapIterator(null);
/* 773 */     s.defaultWriteObject();
/* 774 */     for (int j = this.size; j-- != 0; ) {
/* 775 */       int e = i.nextEntry();
/* 776 */       s.writeInt(key[e]);
/* 777 */       s.writeBoolean(value[e]);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
/* 782 */     s.defaultReadObject();
/* 783 */     this.n = HashCommon.arraySize(this.size, this.f);
/* 784 */     this.maxFill = HashCommon.maxFill(this.n, this.f);
/* 785 */     this.mask = (this.n - 1);
/* 786 */     int[] key = this.key = new int[this.n];
/* 787 */     boolean[] value = this.value = new boolean[this.n];
/* 788 */     boolean[] used = this.used = new boolean[this.n];
/*     */ 
/* 791 */     int i = this.size; for (int pos = 0; i-- != 0; ) {
/* 792 */       int k = s.readInt();
/* 793 */       boolean v = s.readBoolean();
/* 794 */       pos = HashCommon.murmurHash3(k) & this.mask;
/* 795 */       while (used[pos] != 0) pos = pos + 1 & this.mask;
/* 796 */       used[pos] = true;
/* 797 */       key[pos] = k;
/* 798 */       value[pos] = v;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void checkTable()
/*     */   {
/*     */   }
/*     */ 
/*     */   private final class ValueIterator extends Int2BooleanOpenHashMap.MapIterator
/*     */     implements BooleanIterator
/*     */   {
/*     */     public ValueIterator()
/*     */     {
/* 605 */       super(null); } 
/* 606 */     public boolean nextBoolean() { return Int2BooleanOpenHashMap.this.value[nextEntry()]; } 
/* 607 */     public Boolean next() { return Boolean.valueOf(Int2BooleanOpenHashMap.this.value[nextEntry()]); }
/*     */ 
/*     */   }
/*     */ 
/*     */   private final class KeySet extends AbstractIntSet
/*     */   {
/*     */     private KeySet()
/*     */     {
/*     */     }
/*     */ 
/*     */     public IntIterator iterator()
/*     */     {
/* 577 */       return new Int2BooleanOpenHashMap.KeyIterator(Int2BooleanOpenHashMap.this);
/*     */     }
/*     */     public int size() {
/* 580 */       return Int2BooleanOpenHashMap.this.size;
/*     */     }
/*     */     public boolean contains(int k) {
/* 583 */       return Int2BooleanOpenHashMap.this.containsKey(k);
/*     */     }
/*     */     public boolean remove(int k) {
/* 586 */       int oldSize = Int2BooleanOpenHashMap.this.size;
/* 587 */       Int2BooleanOpenHashMap.this.remove(k);
/* 588 */       return Int2BooleanOpenHashMap.this.size != oldSize;
/*     */     }
/*     */     public void clear() {
/* 591 */       Int2BooleanOpenHashMap.this.clear();
/*     */     }
/*     */   }
/*     */ 
/*     */   private final class KeyIterator extends Int2BooleanOpenHashMap.MapIterator
/*     */     implements IntIterator
/*     */   {
/*     */     public KeyIterator()
/*     */     {
/* 571 */       super(null); } 
/* 572 */     public int nextInt() { return Int2BooleanOpenHashMap.this.key[nextEntry()]; } 
/* 573 */     public Integer next() { return Integer.valueOf(Int2BooleanOpenHashMap.this.key[nextEntry()]); }
/*     */ 
/*     */   }
/*     */ 
/*     */   private final class MapEntrySet extends AbstractObjectSet<Int2BooleanMap.Entry>
/*     */     implements Int2BooleanMap.FastEntrySet
/*     */   {
/*     */     private MapEntrySet()
/*     */     {
/*     */     }
/*     */ 
/*     */     public ObjectIterator<Int2BooleanMap.Entry> iterator()
/*     */     {
/* 517 */       return new Int2BooleanOpenHashMap.EntryIterator(Int2BooleanOpenHashMap.this, null);
/*     */     }
/*     */     public ObjectIterator<Int2BooleanMap.Entry> fastIterator() {
/* 520 */       return new Int2BooleanOpenHashMap.FastEntryIterator(Int2BooleanOpenHashMap.this, null);
/*     */     }
/*     */ 
/*     */     public boolean contains(Object o) {
/* 524 */       if (!(o instanceof Map.Entry)) return false;
/* 525 */       Map.Entry e = (Map.Entry)o;
/* 526 */       int k = ((Integer)e.getKey()).intValue();
/*     */ 
/* 528 */       int pos = HashCommon.murmurHash3(k) & Int2BooleanOpenHashMap.this.mask;
/*     */ 
/* 530 */       while (Int2BooleanOpenHashMap.this.used[pos] != 0) {
/* 531 */         if (Int2BooleanOpenHashMap.this.key[pos] == k) return Int2BooleanOpenHashMap.this.value[pos] == ((Boolean)e.getValue()).booleanValue();
/* 532 */         pos = pos + 1 & Int2BooleanOpenHashMap.this.mask;
/*     */       }
/* 534 */       return false;
/*     */     }
/*     */ 
/*     */     public boolean remove(Object o) {
/* 538 */       if (!(o instanceof Map.Entry)) return false;
/* 539 */       Map.Entry e = (Map.Entry)o;
/* 540 */       int k = ((Integer)e.getKey()).intValue();
/*     */ 
/* 542 */       int pos = HashCommon.murmurHash3(k) & Int2BooleanOpenHashMap.this.mask;
/*     */ 
/* 544 */       while (Int2BooleanOpenHashMap.this.used[pos] != 0) {
/* 545 */         if (Int2BooleanOpenHashMap.this.key[pos] == k) {
/* 546 */           Int2BooleanOpenHashMap.this.remove(e.getKey());
/* 547 */           return true;
/*     */         }
/* 549 */         pos = pos + 1 & Int2BooleanOpenHashMap.this.mask;
/*     */       }
/* 551 */       return false;
/*     */     }
/*     */     public int size() {
/* 554 */       return Int2BooleanOpenHashMap.this.size;
/*     */     }
/*     */     public void clear() {
/* 557 */       Int2BooleanOpenHashMap.this.clear();
/*     */     }
/*     */   }
/*     */ 
/*     */   private class FastEntryIterator extends Int2BooleanOpenHashMap.MapIterator
/*     */     implements ObjectIterator<Int2BooleanMap.Entry>
/*     */   {
/* 507 */     final AbstractInt2BooleanMap.BasicEntry entry = new AbstractInt2BooleanMap.BasicEntry(0, false);
/*     */ 
/*     */     private FastEntryIterator()
/*     */     {
/* 506 */       super(null);
/*     */     }
/*     */     public AbstractInt2BooleanMap.BasicEntry next() {
/* 509 */       int e = nextEntry();
/* 510 */       this.entry.key = Int2BooleanOpenHashMap.this.key[e];
/* 511 */       this.entry.value = Int2BooleanOpenHashMap.this.value[e];
/* 512 */       return this.entry;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class EntryIterator extends Int2BooleanOpenHashMap.MapIterator
/*     */     implements ObjectIterator<Int2BooleanMap.Entry>
/*     */   {
/*     */     private Int2BooleanOpenHashMap.MapEntry entry;
/*     */ 
/*     */     private EntryIterator()
/*     */     {
/* 495 */       super(null);
/*     */     }
/*     */     public Int2BooleanMap.Entry next() {
/* 498 */       return this.entry = new Int2BooleanOpenHashMap.MapEntry(Int2BooleanOpenHashMap.this, nextEntry());
/*     */     }
/*     */ 
/*     */     public void remove() {
/* 502 */       super.remove();
/* 503 */       Int2BooleanOpenHashMap.MapEntry.access$102(this.entry, -1);
/*     */     }
/*     */   }
/*     */ 
/*     */   private class MapIterator
/*     */   {
/*     */     int pos;
/*     */     int last;
/*     */     int c;
/*     */     IntArrayList wrapped;
/*     */ 
/*     */     private MapIterator()
/*     */     {
/* 404 */       this.pos = Int2BooleanOpenHashMap.this.n;
/*     */ 
/* 407 */       this.last = -1;
/*     */ 
/* 409 */       this.c = Int2BooleanOpenHashMap.this.size;
/*     */ 
/* 414 */       boolean[] used = Int2BooleanOpenHashMap.this.used;
/* 415 */       while ((this.c != 0) && (used[(--this.pos)] == 0));
/*     */     }
/*     */ 
/*     */     public boolean hasNext()
/*     */     {
/* 418 */       return this.c != 0;
/*     */     }
/*     */     public int nextEntry() {
/* 421 */       if (!hasNext()) throw new NoSuchElementException();
/* 422 */       this.c -= 1;
/*     */ 
/* 424 */       if (this.pos < 0) {
/* 425 */         int k = this.wrapped.getInt(-(this.last = --this.pos) - 2);
/*     */ 
/* 427 */         int pos = HashCommon.murmurHash3(k) & Int2BooleanOpenHashMap.this.mask;
/*     */ 
/* 429 */         while (Int2BooleanOpenHashMap.this.used[pos] != 0) {
/* 430 */           if (Int2BooleanOpenHashMap.this.key[pos] == k) return pos;
/* 431 */           pos = pos + 1 & Int2BooleanOpenHashMap.this.mask;
/*     */         }
/*     */       }
/* 434 */       this.last = this.pos;
/*     */ 
/* 436 */       if (this.c != 0) {
/* 437 */         boolean[] used = Int2BooleanOpenHashMap.this.used;
/* 438 */         while ((this.pos-- != 0) && (used[this.pos] == 0));
/*     */       }
/* 441 */       return this.last;
/*     */     }
/*     */ 
/*     */     protected final int shiftKeys(int pos)
/*     */     {
/*     */       int last;
/*     */       while (true)
/*     */       {
/* 454 */         pos = (last = pos) + 1 & Int2BooleanOpenHashMap.this.mask;
/* 455 */         while (Int2BooleanOpenHashMap.this.used[pos] != 0) {
/* 456 */           int slot = HashCommon.murmurHash3(Int2BooleanOpenHashMap.this.key[pos]) & Int2BooleanOpenHashMap.this.mask;
/* 457 */           if (last <= pos ? (last < slot) && (slot <= pos) : (last >= slot) && (slot > pos)) break;
/* 458 */           pos = pos + 1 & Int2BooleanOpenHashMap.this.mask;
/*     */         }
/* 460 */         if (Int2BooleanOpenHashMap.this.used[pos] == 0) break;
/* 461 */         if (pos < last)
/*     */         {
/* 463 */           if (this.wrapped == null) this.wrapped = new IntArrayList();
/* 464 */           this.wrapped.add(Int2BooleanOpenHashMap.this.key[pos]);
/*     */         }
/* 466 */         Int2BooleanOpenHashMap.this.key[last] = Int2BooleanOpenHashMap.this.key[pos];
/* 467 */         Int2BooleanOpenHashMap.this.value[last] = Int2BooleanOpenHashMap.this.value[pos];
/*     */       }
/* 469 */       Int2BooleanOpenHashMap.this.used[last] = false;
/* 470 */       return last;
/*     */     }
/*     */ 
/*     */     public void remove() {
/* 474 */       if (this.last == -1) throw new IllegalStateException();
/* 475 */       if (this.pos < -1)
/*     */       {
/* 477 */         Int2BooleanOpenHashMap.this.remove(this.wrapped.getInt(-this.pos - 2));
/* 478 */         this.last = -1;
/* 479 */         return;
/*     */       }
/* 481 */       Int2BooleanOpenHashMap.this.size -= 1;
/* 482 */       if ((shiftKeys(this.last) == this.pos) && (this.c > 0)) {
/* 483 */         this.c += 1;
/* 484 */         nextEntry();
/*     */       }
/* 486 */       this.last = -1;
/*     */     }
/*     */ 
/*     */     public int skip(int n) {
/* 490 */       int i = n;
/* 491 */       while ((i-- != 0) && (hasNext())) nextEntry();
/* 492 */       return n - i - 1;
/*     */     }
/*     */   }
/*     */ 
/*     */   private final class MapEntry
/*     */     implements Int2BooleanMap.Entry, Map.Entry<Integer, Boolean>
/*     */   {
/*     */     private int index;
/*     */ 
/*     */     MapEntry(int index)
/*     */     {
/* 365 */       this.index = index;
/*     */     }
/*     */     public Integer getKey() {
/* 368 */       return Integer.valueOf(Int2BooleanOpenHashMap.this.key[this.index]);
/*     */     }
/*     */     public int getIntKey() {
/* 371 */       return Int2BooleanOpenHashMap.this.key[this.index];
/*     */     }
/*     */     public Boolean getValue() {
/* 374 */       return Boolean.valueOf(Int2BooleanOpenHashMap.this.value[this.index]);
/*     */     }
/*     */     public boolean getBooleanValue() {
/* 377 */       return Int2BooleanOpenHashMap.this.value[this.index];
/*     */     }
/*     */     public boolean setValue(boolean v) {
/* 380 */       boolean oldValue = Int2BooleanOpenHashMap.this.value[this.index];
/* 381 */       Int2BooleanOpenHashMap.this.value[this.index] = v;
/* 382 */       return oldValue;
/*     */     }
/*     */     public Boolean setValue(Boolean v) {
/* 385 */       return Boolean.valueOf(setValue(v.booleanValue()));
/*     */     }
/*     */ 
/*     */     public boolean equals(Object o) {
/* 389 */       if (!(o instanceof Map.Entry)) return false;
/* 390 */       Map.Entry e = (Map.Entry)o;
/* 391 */       return (Int2BooleanOpenHashMap.this.key[this.index] == ((Integer)e.getKey()).intValue()) && (Int2BooleanOpenHashMap.this.value[this.index] == ((Boolean)e.getValue()).booleanValue());
/*     */     }
/*     */     public int hashCode() {
/* 394 */       return Int2BooleanOpenHashMap.this.key[this.index] ^ (Int2BooleanOpenHashMap.this.value[this.index] != 0 ? 1231 : 1237);
/*     */     }
/*     */     public String toString() {
/* 397 */       return Int2BooleanOpenHashMap.this.key[this.index] + "=>" + Int2BooleanOpenHashMap.this.value[this.index];
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Raul\Desktop\StarMade\StarMade.jar
 * Qualified Name:     it.unimi.dsi.fastutil.ints.Int2BooleanOpenHashMap
 * JD-Core Version:    0.6.2
 */