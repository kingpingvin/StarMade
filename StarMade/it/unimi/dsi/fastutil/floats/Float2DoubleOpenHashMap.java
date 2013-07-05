/*     */ package it.unimi.dsi.fastutil.floats;
/*     */ 
/*     */ import it.unimi.dsi.fastutil.Hash;
/*     */ import it.unimi.dsi.fastutil.HashCommon;
/*     */ import it.unimi.dsi.fastutil.booleans.BooleanArrays;
/*     */ import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
/*     */ import it.unimi.dsi.fastutil.doubles.DoubleCollection;
/*     */ import it.unimi.dsi.fastutil.doubles.DoubleIterator;
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
/*     */ public class Float2DoubleOpenHashMap extends AbstractFloat2DoubleMap
/*     */   implements Serializable, Cloneable, Hash
/*     */ {
/*     */   public static final long serialVersionUID = 0L;
/*     */   private static final boolean ASSERTS = false;
/*     */   protected transient float[] key;
/*     */   protected transient double[] value;
/*     */   protected transient boolean[] used;
/*     */   protected final float f;
/*     */   protected transient int n;
/*     */   protected transient int maxFill;
/*     */   protected transient int mask;
/*     */   protected int size;
/*     */   protected volatile transient Float2DoubleMap.FastEntrySet entries;
/*     */   protected volatile transient FloatSet keys;
/*     */   protected volatile transient DoubleCollection values;
/*     */ 
/*     */   public Float2DoubleOpenHashMap(int expected, float f)
/*     */   {
/* 107 */     if ((f <= 0.0F) || (f > 1.0F)) throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
/* 108 */     if (expected < 0) throw new IllegalArgumentException("The expected number of elements must be nonnegative");
/* 109 */     this.f = f;
/* 110 */     this.n = HashCommon.arraySize(expected, f);
/* 111 */     this.mask = (this.n - 1);
/* 112 */     this.maxFill = HashCommon.maxFill(this.n, f);
/* 113 */     this.key = new float[this.n];
/* 114 */     this.value = new double[this.n];
/* 115 */     this.used = new boolean[this.n];
/*     */   }
/*     */ 
/*     */   public Float2DoubleOpenHashMap(int expected)
/*     */   {
/* 122 */     this(expected, 0.75F);
/*     */   }
/*     */ 
/*     */   public Float2DoubleOpenHashMap()
/*     */   {
/* 128 */     this(16, 0.75F);
/*     */   }
/*     */ 
/*     */   public Float2DoubleOpenHashMap(Map<? extends Float, ? extends Double> m, float f)
/*     */   {
/* 136 */     this(m.size(), f);
/* 137 */     putAll(m);
/*     */   }
/*     */ 
/*     */   public Float2DoubleOpenHashMap(Map<? extends Float, ? extends Double> m)
/*     */   {
/* 144 */     this(m, 0.75F);
/*     */   }
/*     */ 
/*     */   public Float2DoubleOpenHashMap(Float2DoubleMap m, float f)
/*     */   {
/* 152 */     this(m.size(), f);
/* 153 */     putAll(m);
/*     */   }
/*     */ 
/*     */   public Float2DoubleOpenHashMap(Float2DoubleMap m)
/*     */   {
/* 160 */     this(m, 0.75F);
/*     */   }
/*     */ 
/*     */   public Float2DoubleOpenHashMap(float[] k, double[] v, float f)
/*     */   {
/* 170 */     this(k.length, f);
/* 171 */     if (k.length != v.length) throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
/* 172 */     for (int i = 0; i < k.length; i++) put(k[i], v[i]);
/*     */   }
/*     */ 
/*     */   public Float2DoubleOpenHashMap(float[] k, double[] v)
/*     */   {
/* 181 */     this(k, v, 0.75F);
/*     */   }
/*     */ 
/*     */   public double put(float k, double v)
/*     */   {
/* 189 */     int pos = HashCommon.murmurHash3(HashCommon.float2int(k)) & this.mask;
/*     */ 
/* 191 */     while (this.used[pos] != 0) {
/* 192 */       if (this.key[pos] == k) {
/* 193 */         double oldValue = this.value[pos];
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
/*     */   public Double put(Float ok, Double ov) {
/* 207 */     double v = ov.doubleValue();
/* 208 */     float k = ok.floatValue();
/*     */ 
/* 210 */     int pos = HashCommon.murmurHash3(HashCommon.float2int(k)) & this.mask;
/*     */ 
/* 212 */     while (this.used[pos] != 0) {
/* 213 */       if (this.key[pos] == k) {
/* 214 */         Double oldValue = Double.valueOf(this.value[pos]);
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
/*     */   public double add(float k, double incr)
/*     */   {
/* 240 */     int pos = HashCommon.murmurHash3(HashCommon.float2int(k)) & this.mask;
/*     */ 
/* 242 */     while (this.used[pos] != 0) {
/* 243 */       if (this.key[pos] == k) {
/* 244 */         double oldValue = this.value[pos];
/* 245 */         this.value[pos] += incr;
/* 246 */         return oldValue;
/*     */       }
/* 248 */       pos = pos + 1 & this.mask;
/*     */     }
/* 250 */     this.used[pos] = true;
/* 251 */     this.key[pos] = k;
/* 252 */     this.value[pos] = (this.defRetValue + incr);
/* 253 */     if (++this.size >= this.maxFill) rehash(HashCommon.arraySize(this.size + 1, this.f));
/*     */ 
/* 255 */     return this.defRetValue;
/*     */   }
/*     */ 
/*     */   protected final int shiftKeys(int pos)
/*     */   {
/*     */     int last;
/*     */     while (true)
/*     */     {
/* 267 */       pos = (last = pos) + 1 & this.mask;
/* 268 */       while (this.used[pos] != 0) {
/* 269 */         int slot = HashCommon.murmurHash3(HashCommon.float2int(this.key[pos])) & this.mask;
/* 270 */         if (last <= pos ? (last < slot) && (slot <= pos) : (last >= slot) && (slot > pos)) break;
/* 271 */         pos = pos + 1 & this.mask;
/*     */       }
/* 273 */       if (this.used[pos] == 0) break;
/* 274 */       this.key[last] = this.key[pos];
/* 275 */       this.value[last] = this.value[pos];
/*     */     }
/* 277 */     this.used[last] = false;
/* 278 */     return last;
/*     */   }
/*     */ 
/*     */   public double remove(float k)
/*     */   {
/* 283 */     int pos = HashCommon.murmurHash3(HashCommon.float2int(k)) & this.mask;
/*     */ 
/* 285 */     while (this.used[pos] != 0) {
/* 286 */       if (this.key[pos] == k) {
/* 287 */         this.size -= 1;
/* 288 */         double v = this.value[pos];
/* 289 */         shiftKeys(pos);
/* 290 */         return v;
/*     */       }
/* 292 */       pos = pos + 1 & this.mask;
/*     */     }
/* 294 */     return this.defRetValue;
/*     */   }
/*     */ 
/*     */   public Double remove(Object ok) {
/* 298 */     float k = ((Float)ok).floatValue();
/*     */ 
/* 300 */     int pos = HashCommon.murmurHash3(HashCommon.float2int(k)) & this.mask;
/*     */ 
/* 302 */     while (this.used[pos] != 0) {
/* 303 */       if (this.key[pos] == k) {
/* 304 */         this.size -= 1;
/* 305 */         double v = this.value[pos];
/* 306 */         shiftKeys(pos);
/* 307 */         return Double.valueOf(v);
/*     */       }
/* 309 */       pos = pos + 1 & this.mask;
/*     */     }
/* 311 */     return null;
/*     */   }
/*     */   public Double get(Float ok) {
/* 314 */     float k = ok.floatValue();
/*     */ 
/* 316 */     int pos = HashCommon.murmurHash3(HashCommon.float2int(k)) & this.mask;
/*     */ 
/* 318 */     while (this.used[pos] != 0) {
/* 319 */       if (this.key[pos] == k) return Double.valueOf(this.value[pos]);
/* 320 */       pos = pos + 1 & this.mask;
/*     */     }
/* 322 */     return null;
/*     */   }
/*     */ 
/*     */   public double get(float k)
/*     */   {
/* 327 */     int pos = HashCommon.murmurHash3(HashCommon.float2int(k)) & this.mask;
/*     */ 
/* 329 */     while (this.used[pos] != 0) {
/* 330 */       if (this.key[pos] == k) return this.value[pos];
/* 331 */       pos = pos + 1 & this.mask;
/*     */     }
/* 333 */     return this.defRetValue;
/*     */   }
/*     */ 
/*     */   public boolean containsKey(float k)
/*     */   {
/* 338 */     int pos = HashCommon.murmurHash3(HashCommon.float2int(k)) & this.mask;
/*     */ 
/* 340 */     while (this.used[pos] != 0) {
/* 341 */       if (this.key[pos] == k) return true;
/* 342 */       pos = pos + 1 & this.mask;
/*     */     }
/* 344 */     return false;
/*     */   }
/*     */   public boolean containsValue(double v) {
/* 347 */     double[] value = this.value;
/* 348 */     boolean[] used = this.used;
/* 349 */     for (int i = this.n; i-- != 0; return true) label17: if ((used[i] == 0) || (value[i] != v))
/*     */         break label17; return false;
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 359 */     if (this.size == 0) return;
/* 360 */     this.size = 0;
/* 361 */     BooleanArrays.fill(this.used, false);
/*     */   }
/*     */ 
/*     */   public int size() {
/* 365 */     return this.size;
/*     */   }
/*     */   public boolean isEmpty() {
/* 368 */     return this.size == 0;
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
/* 385 */     return 16;
/*     */   }
/*     */ 
/*     */   public Float2DoubleMap.FastEntrySet float2DoubleEntrySet()
/*     */   {
/* 591 */     if (this.entries == null) this.entries = new MapEntrySet(null);
/* 592 */     return this.entries;
/*     */   }
/*     */ 
/*     */   public FloatSet keySet()
/*     */   {
/* 625 */     if (this.keys == null) this.keys = new KeySet(null);
/* 626 */     return this.keys;
/*     */   }
/*     */ 
/*     */   public DoubleCollection values()
/*     */   {
/* 640 */     if (this.values == null) this.values = new AbstractDoubleCollection() {
/*     */         public DoubleIterator iterator() {
/* 642 */           return new Float2DoubleOpenHashMap.ValueIterator(Float2DoubleOpenHashMap.this);
/*     */         }
/*     */         public int size() {
/* 645 */           return Float2DoubleOpenHashMap.this.size;
/*     */         }
/*     */         public boolean contains(double v) {
/* 648 */           return Float2DoubleOpenHashMap.this.containsValue(v);
/*     */         }
/*     */         public void clear() {
/* 651 */           Float2DoubleOpenHashMap.this.clear();
/*     */         }
/*     */       };
/* 654 */     return this.values;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public boolean rehash()
/*     */   {
/* 668 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean trim()
/*     */   {
/* 683 */     int l = HashCommon.arraySize(this.size, this.f);
/* 684 */     if (l >= this.n) return true; try
/*     */     {
/* 686 */       rehash(l);
/*     */     } catch (OutOfMemoryError cantDoIt) {
/* 688 */       return false;
/* 689 */     }return true;
/*     */   }
/*     */ 
/*     */   public boolean trim(int n)
/*     */   {
/* 710 */     int l = HashCommon.nextPowerOfTwo((int)Math.ceil(n / this.f));
/* 711 */     if (this.n <= l) return true; try
/*     */     {
/* 713 */       rehash(l);
/*     */     } catch (OutOfMemoryError cantDoIt) {
/* 715 */       return false;
/* 716 */     }return true;
/*     */   }
/*     */ 
/*     */   protected void rehash(int newN)
/*     */   {
/* 729 */     int i = 0;
/* 730 */     boolean[] used = this.used;
/*     */ 
/* 732 */     float[] key = this.key;
/* 733 */     double[] value = this.value;
/* 734 */     int newMask = newN - 1;
/* 735 */     float[] newKey = new float[newN];
/* 736 */     double[] newValue = new double[newN];
/* 737 */     boolean[] newUsed = new boolean[newN];
/* 738 */     for (int j = this.size; j-- != 0; ) {
/* 739 */       while (used[i] == 0) i++;
/* 740 */       float k = key[i];
/* 741 */       int pos = HashCommon.murmurHash3(HashCommon.float2int(k)) & newMask;
/* 742 */       while (newUsed[pos] != 0) pos = pos + 1 & newMask;
/* 743 */       newUsed[pos] = true;
/* 744 */       newKey[pos] = k;
/* 745 */       newValue[pos] = value[i];
/* 746 */       i++;
/*     */     }
/* 748 */     this.n = newN;
/* 749 */     this.mask = newMask;
/* 750 */     this.maxFill = HashCommon.maxFill(this.n, this.f);
/* 751 */     this.key = newKey;
/* 752 */     this.value = newValue;
/* 753 */     this.used = newUsed;
/*     */   }
/*     */ 
/*     */   public Float2DoubleOpenHashMap clone()
/*     */   {
/*     */     Float2DoubleOpenHashMap c;
/*     */     try
/*     */     {
/* 766 */       c = (Float2DoubleOpenHashMap)super.clone();
/*     */     }
/*     */     catch (CloneNotSupportedException cantHappen) {
/* 769 */       throw new InternalError();
/*     */     }
/* 771 */     c.keys = null;
/* 772 */     c.values = null;
/* 773 */     c.entries = null;
/* 774 */     c.key = ((float[])this.key.clone());
/* 775 */     c.value = ((double[])this.value.clone());
/* 776 */     c.used = ((boolean[])this.used.clone());
/* 777 */     return c;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 789 */     int h = 0;
/* 790 */     int j = this.size; int i = 0; for (int t = 0; j-- != 0; ) {
/* 791 */       while (this.used[i] == 0) i++;
/* 792 */       t = HashCommon.float2int(this.key[i]);
/* 793 */       t ^= HashCommon.double2int(this.value[i]);
/* 794 */       h += t;
/* 795 */       i++;
/*     */     }
/* 797 */     return h;
/*     */   }
/*     */   private void writeObject(ObjectOutputStream s) throws IOException {
/* 800 */     float[] key = this.key;
/* 801 */     double[] value = this.value;
/* 802 */     MapIterator i = new MapIterator(null);
/* 803 */     s.defaultWriteObject();
/* 804 */     for (int j = this.size; j-- != 0; ) {
/* 805 */       int e = i.nextEntry();
/* 806 */       s.writeFloat(key[e]);
/* 807 */       s.writeDouble(value[e]);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
/* 812 */     s.defaultReadObject();
/* 813 */     this.n = HashCommon.arraySize(this.size, this.f);
/* 814 */     this.maxFill = HashCommon.maxFill(this.n, this.f);
/* 815 */     this.mask = (this.n - 1);
/* 816 */     float[] key = this.key = new float[this.n];
/* 817 */     double[] value = this.value = new double[this.n];
/* 818 */     boolean[] used = this.used = new boolean[this.n];
/*     */ 
/* 821 */     int i = this.size; for (int pos = 0; i-- != 0; ) {
/* 822 */       float k = s.readFloat();
/* 823 */       double v = s.readDouble();
/* 824 */       pos = HashCommon.murmurHash3(HashCommon.float2int(k)) & this.mask;
/* 825 */       while (used[pos] != 0) pos = pos + 1 & this.mask;
/* 826 */       used[pos] = true;
/* 827 */       key[pos] = k;
/* 828 */       value[pos] = v;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void checkTable()
/*     */   {
/*     */   }
/*     */ 
/*     */   private final class ValueIterator extends Float2DoubleOpenHashMap.MapIterator
/*     */     implements DoubleIterator
/*     */   {
/*     */     public ValueIterator()
/*     */     {
/* 635 */       super(null); } 
/* 636 */     public double nextDouble() { return Float2DoubleOpenHashMap.this.value[nextEntry()]; } 
/* 637 */     public Double next() { return Double.valueOf(Float2DoubleOpenHashMap.this.value[nextEntry()]); }
/*     */ 
/*     */   }
/*     */ 
/*     */   private final class KeySet extends AbstractFloatSet
/*     */   {
/*     */     private KeySet()
/*     */     {
/*     */     }
/*     */ 
/*     */     public FloatIterator iterator()
/*     */     {
/* 607 */       return new Float2DoubleOpenHashMap.KeyIterator(Float2DoubleOpenHashMap.this);
/*     */     }
/*     */     public int size() {
/* 610 */       return Float2DoubleOpenHashMap.this.size;
/*     */     }
/*     */     public boolean contains(float k) {
/* 613 */       return Float2DoubleOpenHashMap.this.containsKey(k);
/*     */     }
/*     */     public boolean remove(float k) {
/* 616 */       int oldSize = Float2DoubleOpenHashMap.this.size;
/* 617 */       Float2DoubleOpenHashMap.this.remove(k);
/* 618 */       return Float2DoubleOpenHashMap.this.size != oldSize;
/*     */     }
/*     */     public void clear() {
/* 621 */       Float2DoubleOpenHashMap.this.clear();
/*     */     }
/*     */   }
/*     */ 
/*     */   private final class KeyIterator extends Float2DoubleOpenHashMap.MapIterator
/*     */     implements FloatIterator
/*     */   {
/*     */     public KeyIterator()
/*     */     {
/* 601 */       super(null); } 
/* 602 */     public float nextFloat() { return Float2DoubleOpenHashMap.this.key[nextEntry()]; } 
/* 603 */     public Float next() { return Float.valueOf(Float2DoubleOpenHashMap.this.key[nextEntry()]); }
/*     */ 
/*     */   }
/*     */ 
/*     */   private final class MapEntrySet extends AbstractObjectSet<Float2DoubleMap.Entry>
/*     */     implements Float2DoubleMap.FastEntrySet
/*     */   {
/*     */     private MapEntrySet()
/*     */     {
/*     */     }
/*     */ 
/*     */     public ObjectIterator<Float2DoubleMap.Entry> iterator()
/*     */     {
/* 547 */       return new Float2DoubleOpenHashMap.EntryIterator(Float2DoubleOpenHashMap.this, null);
/*     */     }
/*     */     public ObjectIterator<Float2DoubleMap.Entry> fastIterator() {
/* 550 */       return new Float2DoubleOpenHashMap.FastEntryIterator(Float2DoubleOpenHashMap.this, null);
/*     */     }
/*     */ 
/*     */     public boolean contains(Object o) {
/* 554 */       if (!(o instanceof Map.Entry)) return false;
/* 555 */       Map.Entry e = (Map.Entry)o;
/* 556 */       float k = ((Float)e.getKey()).floatValue();
/*     */ 
/* 558 */       int pos = HashCommon.murmurHash3(HashCommon.float2int(k)) & Float2DoubleOpenHashMap.this.mask;
/*     */ 
/* 560 */       while (Float2DoubleOpenHashMap.this.used[pos] != 0) {
/* 561 */         if (Float2DoubleOpenHashMap.this.key[pos] == k) return Float2DoubleOpenHashMap.this.value[pos] == ((Double)e.getValue()).doubleValue();
/* 562 */         pos = pos + 1 & Float2DoubleOpenHashMap.this.mask;
/*     */       }
/* 564 */       return false;
/*     */     }
/*     */ 
/*     */     public boolean remove(Object o) {
/* 568 */       if (!(o instanceof Map.Entry)) return false;
/* 569 */       Map.Entry e = (Map.Entry)o;
/* 570 */       float k = ((Float)e.getKey()).floatValue();
/*     */ 
/* 572 */       int pos = HashCommon.murmurHash3(HashCommon.float2int(k)) & Float2DoubleOpenHashMap.this.mask;
/*     */ 
/* 574 */       while (Float2DoubleOpenHashMap.this.used[pos] != 0) {
/* 575 */         if (Float2DoubleOpenHashMap.this.key[pos] == k) {
/* 576 */           Float2DoubleOpenHashMap.this.remove(e.getKey());
/* 577 */           return true;
/*     */         }
/* 579 */         pos = pos + 1 & Float2DoubleOpenHashMap.this.mask;
/*     */       }
/* 581 */       return false;
/*     */     }
/*     */     public int size() {
/* 584 */       return Float2DoubleOpenHashMap.this.size;
/*     */     }
/*     */     public void clear() {
/* 587 */       Float2DoubleOpenHashMap.this.clear();
/*     */     }
/*     */   }
/*     */ 
/*     */   private class FastEntryIterator extends Float2DoubleOpenHashMap.MapIterator
/*     */     implements ObjectIterator<Float2DoubleMap.Entry>
/*     */   {
/* 537 */     final AbstractFloat2DoubleMap.BasicEntry entry = new AbstractFloat2DoubleMap.BasicEntry(0.0F, 0.0D);
/*     */ 
/*     */     private FastEntryIterator()
/*     */     {
/* 536 */       super(null);
/*     */     }
/*     */     public AbstractFloat2DoubleMap.BasicEntry next() {
/* 539 */       int e = nextEntry();
/* 540 */       this.entry.key = Float2DoubleOpenHashMap.this.key[e];
/* 541 */       this.entry.value = Float2DoubleOpenHashMap.this.value[e];
/* 542 */       return this.entry;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class EntryIterator extends Float2DoubleOpenHashMap.MapIterator
/*     */     implements ObjectIterator<Float2DoubleMap.Entry>
/*     */   {
/*     */     private Float2DoubleOpenHashMap.MapEntry entry;
/*     */ 
/*     */     private EntryIterator()
/*     */     {
/* 525 */       super(null);
/*     */     }
/*     */     public Float2DoubleMap.Entry next() {
/* 528 */       return this.entry = new Float2DoubleOpenHashMap.MapEntry(Float2DoubleOpenHashMap.this, nextEntry());
/*     */     }
/*     */ 
/*     */     public void remove() {
/* 532 */       super.remove();
/* 533 */       Float2DoubleOpenHashMap.MapEntry.access$102(this.entry, -1);
/*     */     }
/*     */   }
/*     */ 
/*     */   private class MapIterator
/*     */   {
/*     */     int pos;
/*     */     int last;
/*     */     int c;
/*     */     FloatArrayList wrapped;
/*     */ 
/*     */     private MapIterator()
/*     */     {
/* 434 */       this.pos = Float2DoubleOpenHashMap.this.n;
/*     */ 
/* 437 */       this.last = -1;
/*     */ 
/* 439 */       this.c = Float2DoubleOpenHashMap.this.size;
/*     */ 
/* 444 */       boolean[] used = Float2DoubleOpenHashMap.this.used;
/* 445 */       while ((this.c != 0) && (used[(--this.pos)] == 0));
/*     */     }
/*     */ 
/*     */     public boolean hasNext()
/*     */     {
/* 448 */       return this.c != 0;
/*     */     }
/*     */     public int nextEntry() {
/* 451 */       if (!hasNext()) throw new NoSuchElementException();
/* 452 */       this.c -= 1;
/*     */ 
/* 454 */       if (this.pos < 0) {
/* 455 */         float k = this.wrapped.getFloat(-(this.last = --this.pos) - 2);
/*     */ 
/* 457 */         int pos = HashCommon.murmurHash3(HashCommon.float2int(k)) & Float2DoubleOpenHashMap.this.mask;
/*     */ 
/* 459 */         while (Float2DoubleOpenHashMap.this.used[pos] != 0) {
/* 460 */           if (Float2DoubleOpenHashMap.this.key[pos] == k) return pos;
/* 461 */           pos = pos + 1 & Float2DoubleOpenHashMap.this.mask;
/*     */         }
/*     */       }
/* 464 */       this.last = this.pos;
/*     */ 
/* 466 */       if (this.c != 0) {
/* 467 */         boolean[] used = Float2DoubleOpenHashMap.this.used;
/* 468 */         while ((this.pos-- != 0) && (used[this.pos] == 0));
/*     */       }
/* 471 */       return this.last;
/*     */     }
/*     */ 
/*     */     protected final int shiftKeys(int pos)
/*     */     {
/*     */       int last;
/*     */       while (true)
/*     */       {
/* 484 */         pos = (last = pos) + 1 & Float2DoubleOpenHashMap.this.mask;
/* 485 */         while (Float2DoubleOpenHashMap.this.used[pos] != 0) {
/* 486 */           int slot = HashCommon.murmurHash3(HashCommon.float2int(Float2DoubleOpenHashMap.this.key[pos])) & Float2DoubleOpenHashMap.this.mask;
/* 487 */           if (last <= pos ? (last < slot) && (slot <= pos) : (last >= slot) && (slot > pos)) break;
/* 488 */           pos = pos + 1 & Float2DoubleOpenHashMap.this.mask;
/*     */         }
/* 490 */         if (Float2DoubleOpenHashMap.this.used[pos] == 0) break;
/* 491 */         if (pos < last)
/*     */         {
/* 493 */           if (this.wrapped == null) this.wrapped = new FloatArrayList();
/* 494 */           this.wrapped.add(Float2DoubleOpenHashMap.this.key[pos]);
/*     */         }
/* 496 */         Float2DoubleOpenHashMap.this.key[last] = Float2DoubleOpenHashMap.this.key[pos];
/* 497 */         Float2DoubleOpenHashMap.this.value[last] = Float2DoubleOpenHashMap.this.value[pos];
/*     */       }
/* 499 */       Float2DoubleOpenHashMap.this.used[last] = false;
/* 500 */       return last;
/*     */     }
/*     */ 
/*     */     public void remove() {
/* 504 */       if (this.last == -1) throw new IllegalStateException();
/* 505 */       if (this.pos < -1)
/*     */       {
/* 507 */         Float2DoubleOpenHashMap.this.remove(this.wrapped.getFloat(-this.pos - 2));
/* 508 */         this.last = -1;
/* 509 */         return;
/*     */       }
/* 511 */       Float2DoubleOpenHashMap.this.size -= 1;
/* 512 */       if ((shiftKeys(this.last) == this.pos) && (this.c > 0)) {
/* 513 */         this.c += 1;
/* 514 */         nextEntry();
/*     */       }
/* 516 */       this.last = -1;
/*     */     }
/*     */ 
/*     */     public int skip(int n) {
/* 520 */       int i = n;
/* 521 */       while ((i-- != 0) && (hasNext())) nextEntry();
/* 522 */       return n - i - 1;
/*     */     }
/*     */   }
/*     */ 
/*     */   private final class MapEntry
/*     */     implements Float2DoubleMap.Entry, Map.Entry<Float, Double>
/*     */   {
/*     */     private int index;
/*     */ 
/*     */     MapEntry(int index)
/*     */     {
/* 395 */       this.index = index;
/*     */     }
/*     */     public Float getKey() {
/* 398 */       return Float.valueOf(Float2DoubleOpenHashMap.this.key[this.index]);
/*     */     }
/*     */     public float getFloatKey() {
/* 401 */       return Float2DoubleOpenHashMap.this.key[this.index];
/*     */     }
/*     */     public Double getValue() {
/* 404 */       return Double.valueOf(Float2DoubleOpenHashMap.this.value[this.index]);
/*     */     }
/*     */     public double getDoubleValue() {
/* 407 */       return Float2DoubleOpenHashMap.this.value[this.index];
/*     */     }
/*     */     public double setValue(double v) {
/* 410 */       double oldValue = Float2DoubleOpenHashMap.this.value[this.index];
/* 411 */       Float2DoubleOpenHashMap.this.value[this.index] = v;
/* 412 */       return oldValue;
/*     */     }
/*     */     public Double setValue(Double v) {
/* 415 */       return Double.valueOf(setValue(v.doubleValue()));
/*     */     }
/*     */ 
/*     */     public boolean equals(Object o) {
/* 419 */       if (!(o instanceof Map.Entry)) return false;
/* 420 */       Map.Entry e = (Map.Entry)o;
/* 421 */       return (Float2DoubleOpenHashMap.this.key[this.index] == ((Float)e.getKey()).floatValue()) && (Float2DoubleOpenHashMap.this.value[this.index] == ((Double)e.getValue()).doubleValue());
/*     */     }
/*     */     public int hashCode() {
/* 424 */       return HashCommon.float2int(Float2DoubleOpenHashMap.this.key[this.index]) ^ HashCommon.double2int(Float2DoubleOpenHashMap.this.value[this.index]);
/*     */     }
/*     */     public String toString() {
/* 427 */       return Float2DoubleOpenHashMap.this.key[this.index] + "=>" + Float2DoubleOpenHashMap.this.value[this.index];
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Raul\Desktop\StarMade\StarMade.jar
 * Qualified Name:     it.unimi.dsi.fastutil.floats.Float2DoubleOpenHashMap
 * JD-Core Version:    0.6.2
 */