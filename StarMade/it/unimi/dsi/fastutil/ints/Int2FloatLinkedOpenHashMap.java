/*      */ package it.unimi.dsi.fastutil.ints;
/*      */ 
/*      */ import it.unimi.dsi.fastutil.Hash;
/*      */ import it.unimi.dsi.fastutil.HashCommon;
/*      */ import it.unimi.dsi.fastutil.booleans.BooleanArrays;
/*      */ import it.unimi.dsi.fastutil.floats.AbstractFloatCollection;
/*      */ import it.unimi.dsi.fastutil.floats.FloatCollection;
/*      */ import it.unimi.dsi.fastutil.floats.FloatIterator;
/*      */ import it.unimi.dsi.fastutil.floats.FloatListIterator;
/*      */ import it.unimi.dsi.fastutil.objects.AbstractObjectSortedSet;
/*      */ import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
/*      */ import it.unimi.dsi.fastutil.objects.ObjectListIterator;
/*      */ import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
/*      */ import java.io.IOException;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.ObjectOutputStream;
/*      */ import java.io.Serializable;
/*      */ import java.util.Comparator;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.NoSuchElementException;
/*      */ 
/*      */ public class Int2FloatLinkedOpenHashMap extends AbstractInt2FloatSortedMap
/*      */   implements Serializable, Cloneable, Hash
/*      */ {
/*      */   public static final long serialVersionUID = 0L;
/*      */   private static final boolean ASSERTS = false;
/*      */   protected transient int[] key;
/*      */   protected transient float[] value;
/*      */   protected transient boolean[] used;
/*      */   protected final float f;
/*      */   protected transient int n;
/*      */   protected transient int maxFill;
/*      */   protected transient int mask;
/*      */   protected int size;
/*      */   protected volatile transient Int2FloatSortedMap.FastSortedEntrySet entries;
/*      */   protected volatile transient IntSortedSet keys;
/*      */   protected volatile transient FloatCollection values;
/*  130 */   protected transient int first = -1;
/*      */ 
/*  132 */   protected transient int last = -1;
/*      */   protected transient long[] link;
/*      */ 
/*      */   public Int2FloatLinkedOpenHashMap(int expected, float f)
/*      */   {
/*  151 */     if ((f <= 0.0F) || (f > 1.0F)) throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
/*  152 */     if (expected < 0) throw new IllegalArgumentException("The expected number of elements must be nonnegative");
/*  153 */     this.f = f;
/*  154 */     this.n = HashCommon.arraySize(expected, f);
/*  155 */     this.mask = (this.n - 1);
/*  156 */     this.maxFill = HashCommon.maxFill(this.n, f);
/*  157 */     this.key = new int[this.n];
/*  158 */     this.value = new float[this.n];
/*  159 */     this.used = new boolean[this.n];
/*  160 */     this.link = new long[this.n];
/*      */   }
/*      */ 
/*      */   public Int2FloatLinkedOpenHashMap(int expected)
/*      */   {
/*  167 */     this(expected, 0.75F);
/*      */   }
/*      */ 
/*      */   public Int2FloatLinkedOpenHashMap()
/*      */   {
/*  173 */     this(16, 0.75F);
/*      */   }
/*      */ 
/*      */   public Int2FloatLinkedOpenHashMap(Map<? extends Integer, ? extends Float> m, float f)
/*      */   {
/*  181 */     this(m.size(), f);
/*  182 */     putAll(m);
/*      */   }
/*      */ 
/*      */   public Int2FloatLinkedOpenHashMap(Map<? extends Integer, ? extends Float> m)
/*      */   {
/*  189 */     this(m, 0.75F);
/*      */   }
/*      */ 
/*      */   public Int2FloatLinkedOpenHashMap(Int2FloatMap m, float f)
/*      */   {
/*  197 */     this(m.size(), f);
/*  198 */     putAll(m);
/*      */   }
/*      */ 
/*      */   public Int2FloatLinkedOpenHashMap(Int2FloatMap m)
/*      */   {
/*  205 */     this(m, 0.75F);
/*      */   }
/*      */ 
/*      */   public Int2FloatLinkedOpenHashMap(int[] k, float[] v, float f)
/*      */   {
/*  215 */     this(k.length, f);
/*  216 */     if (k.length != v.length) throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
/*  217 */     for (int i = 0; i < k.length; i++) put(k[i], v[i]);
/*      */   }
/*      */ 
/*      */   public Int2FloatLinkedOpenHashMap(int[] k, float[] v)
/*      */   {
/*  226 */     this(k, v, 0.75F);
/*      */   }
/*      */ 
/*      */   public float put(int k, float v)
/*      */   {
/*  234 */     int pos = HashCommon.murmurHash3(k) & this.mask;
/*      */ 
/*  236 */     while (this.used[pos] != 0) {
/*  237 */       if (this.key[pos] == k) {
/*  238 */         float oldValue = this.value[pos];
/*  239 */         this.value[pos] = v;
/*  240 */         return oldValue;
/*      */       }
/*  242 */       pos = pos + 1 & this.mask;
/*      */     }
/*  244 */     this.used[pos] = true;
/*  245 */     this.key[pos] = k;
/*  246 */     this.value[pos] = v;
/*  247 */     if (this.size == 0) {
/*  248 */       this.first = (this.last = pos);
/*      */ 
/*  250 */       this.link[pos] = -1L;
/*      */     }
/*      */     else {
/*  253 */       this.link[this.last] ^= (this.link[this.last] ^ pos & 0xFFFFFFFF) & 0xFFFFFFFF;
/*  254 */       this.link[pos] = ((this.last & 0xFFFFFFFF) << 32 | 0xFFFFFFFF);
/*  255 */       this.last = pos;
/*      */     }
/*  257 */     if (++this.size >= this.maxFill) rehash(HashCommon.arraySize(this.size + 1, this.f));
/*      */ 
/*  259 */     return this.defRetValue;
/*      */   }
/*      */   public Float put(Integer ok, Float ov) {
/*  262 */     float v = ov.floatValue();
/*  263 */     int k = ok.intValue();
/*      */ 
/*  265 */     int pos = HashCommon.murmurHash3(k) & this.mask;
/*      */ 
/*  267 */     while (this.used[pos] != 0) {
/*  268 */       if (this.key[pos] == k) {
/*  269 */         Float oldValue = Float.valueOf(this.value[pos]);
/*  270 */         this.value[pos] = v;
/*  271 */         return oldValue;
/*      */       }
/*  273 */       pos = pos + 1 & this.mask;
/*      */     }
/*  275 */     this.used[pos] = true;
/*  276 */     this.key[pos] = k;
/*  277 */     this.value[pos] = v;
/*  278 */     if (this.size == 0) {
/*  279 */       this.first = (this.last = pos);
/*      */ 
/*  281 */       this.link[pos] = -1L;
/*      */     }
/*      */     else {
/*  284 */       this.link[this.last] ^= (this.link[this.last] ^ pos & 0xFFFFFFFF) & 0xFFFFFFFF;
/*  285 */       this.link[pos] = ((this.last & 0xFFFFFFFF) << 32 | 0xFFFFFFFF);
/*  286 */       this.last = pos;
/*      */     }
/*  288 */     if (++this.size >= this.maxFill) rehash(HashCommon.arraySize(this.size + 1, this.f));
/*      */ 
/*  290 */     return null;
/*      */   }
/*      */ 
/*      */   public float add(int k, float incr)
/*      */   {
/*  305 */     int pos = HashCommon.murmurHash3(k) & this.mask;
/*      */ 
/*  307 */     while (this.used[pos] != 0) {
/*  308 */       if (this.key[pos] == k) {
/*  309 */         float oldValue = this.value[pos];
/*  310 */         this.value[pos] += incr;
/*  311 */         return oldValue;
/*      */       }
/*  313 */       pos = pos + 1 & this.mask;
/*      */     }
/*  315 */     this.used[pos] = true;
/*  316 */     this.key[pos] = k;
/*  317 */     this.value[pos] = (this.defRetValue + incr);
/*  318 */     if (this.size == 0) {
/*  319 */       this.first = (this.last = pos);
/*      */ 
/*  321 */       this.link[pos] = -1L;
/*      */     }
/*      */     else {
/*  324 */       this.link[this.last] ^= (this.link[this.last] ^ pos & 0xFFFFFFFF) & 0xFFFFFFFF;
/*  325 */       this.link[pos] = ((this.last & 0xFFFFFFFF) << 32 | 0xFFFFFFFF);
/*  326 */       this.last = pos;
/*      */     }
/*  328 */     if (++this.size >= this.maxFill) rehash(HashCommon.arraySize(this.size + 1, this.f));
/*      */ 
/*  330 */     return this.defRetValue;
/*      */   }
/*      */ 
/*      */   protected final int shiftKeys(int pos)
/*      */   {
/*      */     int last;
/*      */     while (true)
/*      */     {
/*  342 */       pos = (last = pos) + 1 & this.mask;
/*  343 */       while (this.used[pos] != 0) {
/*  344 */         int slot = HashCommon.murmurHash3(this.key[pos]) & this.mask;
/*  345 */         if (last <= pos ? (last < slot) && (slot <= pos) : (last >= slot) && (slot > pos)) break;
/*  346 */         pos = pos + 1 & this.mask;
/*      */       }
/*  348 */       if (this.used[pos] == 0) break;
/*  349 */       this.key[last] = this.key[pos];
/*  350 */       this.value[last] = this.value[pos];
/*  351 */       fixPointers(pos, last);
/*      */     }
/*  353 */     this.used[last] = false;
/*  354 */     return last;
/*      */   }
/*      */ 
/*      */   public float remove(int k)
/*      */   {
/*  359 */     int pos = HashCommon.murmurHash3(k) & this.mask;
/*      */ 
/*  361 */     while (this.used[pos] != 0) {
/*  362 */       if (this.key[pos] == k) {
/*  363 */         this.size -= 1;
/*  364 */         fixPointers(pos);
/*  365 */         float v = this.value[pos];
/*  366 */         shiftKeys(pos);
/*  367 */         return v;
/*      */       }
/*  369 */       pos = pos + 1 & this.mask;
/*      */     }
/*  371 */     return this.defRetValue;
/*      */   }
/*      */ 
/*      */   public Float remove(Object ok) {
/*  375 */     int k = ((Integer)ok).intValue();
/*      */ 
/*  377 */     int pos = HashCommon.murmurHash3(k) & this.mask;
/*      */ 
/*  379 */     while (this.used[pos] != 0) {
/*  380 */       if (this.key[pos] == k) {
/*  381 */         this.size -= 1;
/*  382 */         fixPointers(pos);
/*  383 */         float v = this.value[pos];
/*  384 */         shiftKeys(pos);
/*  385 */         return Float.valueOf(v);
/*      */       }
/*  387 */       pos = pos + 1 & this.mask;
/*      */     }
/*  389 */     return null;
/*      */   }
/*      */ 
/*      */   public float removeFirstFloat()
/*      */   {
/*  396 */     if (this.size == 0) throw new NoSuchElementException();
/*  397 */     this.size -= 1;
/*  398 */     int pos = this.first;
/*      */ 
/*  400 */     this.first = ((int)this.link[pos]);
/*  401 */     if (0 <= this.first)
/*      */     {
/*  403 */       this.link[this.first] |= -4294967296L;
/*      */     }
/*  405 */     float v = this.value[pos];
/*  406 */     shiftKeys(pos);
/*  407 */     return v;
/*      */   }
/*      */ 
/*      */   public float removeLastFloat()
/*      */   {
/*  414 */     if (this.size == 0) throw new NoSuchElementException();
/*  415 */     this.size -= 1;
/*  416 */     int pos = this.last;
/*      */ 
/*  418 */     this.last = ((int)(this.link[pos] >>> 32));
/*  419 */     if (0 <= this.last)
/*      */     {
/*  421 */       this.link[this.last] |= 4294967295L;
/*      */     }
/*  423 */     float v = this.value[pos];
/*  424 */     shiftKeys(pos);
/*  425 */     return v;
/*      */   }
/*      */   private void moveIndexToFirst(int i) {
/*  428 */     if ((this.size == 1) || (this.first == i)) return;
/*  429 */     if (this.last == i) {
/*  430 */       this.last = ((int)(this.link[i] >>> 32));
/*      */ 
/*  432 */       this.link[this.last] |= 4294967295L;
/*      */     }
/*      */     else {
/*  435 */       long linki = this.link[i];
/*  436 */       int prev = (int)(linki >>> 32);
/*  437 */       int next = (int)linki;
/*  438 */       this.link[prev] ^= (this.link[prev] ^ linki & 0xFFFFFFFF) & 0xFFFFFFFF;
/*  439 */       this.link[next] ^= (this.link[next] ^ linki & 0x0) & 0x0;
/*      */     }
/*  441 */     this.link[this.first] ^= (this.link[this.first] ^ (i & 0xFFFFFFFF) << 32) & 0x0;
/*  442 */     this.link[i] = (0x0 | this.first & 0xFFFFFFFF);
/*  443 */     this.first = i;
/*      */   }
/*      */   private void moveIndexToLast(int i) {
/*  446 */     if ((this.size == 1) || (this.last == i)) return;
/*  447 */     if (this.first == i) {
/*  448 */       this.first = ((int)this.link[i]);
/*      */ 
/*  450 */       this.link[this.first] |= -4294967296L;
/*      */     }
/*      */     else {
/*  453 */       long linki = this.link[i];
/*  454 */       int prev = (int)(linki >>> 32);
/*  455 */       int next = (int)linki;
/*  456 */       this.link[prev] ^= (this.link[prev] ^ linki & 0xFFFFFFFF) & 0xFFFFFFFF;
/*  457 */       this.link[next] ^= (this.link[next] ^ linki & 0x0) & 0x0;
/*      */     }
/*  459 */     this.link[this.last] ^= (this.link[this.last] ^ i & 0xFFFFFFFF) & 0xFFFFFFFF;
/*  460 */     this.link[i] = ((this.last & 0xFFFFFFFF) << 32 | 0xFFFFFFFF);
/*  461 */     this.last = i;
/*      */   }
/*      */ 
/*      */   public float getAndMoveToFirst(int k)
/*      */   {
/*  469 */     int[] key = this.key;
/*  470 */     boolean[] used = this.used;
/*  471 */     int mask = this.mask;
/*      */ 
/*  473 */     int pos = HashCommon.murmurHash3(k) & mask;
/*      */ 
/*  475 */     while (used[pos] != 0) {
/*  476 */       if (k == key[pos]) {
/*  477 */         moveIndexToFirst(pos);
/*  478 */         return this.value[pos];
/*      */       }
/*  480 */       pos = pos + 1 & mask;
/*      */     }
/*  482 */     return this.defRetValue;
/*      */   }
/*      */ 
/*      */   public float getAndMoveToLast(int k)
/*      */   {
/*  490 */     int[] key = this.key;
/*  491 */     boolean[] used = this.used;
/*  492 */     int mask = this.mask;
/*      */ 
/*  494 */     int pos = HashCommon.murmurHash3(k) & mask;
/*      */ 
/*  496 */     while (used[pos] != 0) {
/*  497 */       if (k == key[pos]) {
/*  498 */         moveIndexToLast(pos);
/*  499 */         return this.value[pos];
/*      */       }
/*  501 */       pos = pos + 1 & mask;
/*      */     }
/*  503 */     return this.defRetValue;
/*      */   }
/*      */ 
/*      */   public float putAndMoveToFirst(int k, float v)
/*      */   {
/*  512 */     int[] key = this.key;
/*  513 */     boolean[] used = this.used;
/*  514 */     int mask = this.mask;
/*      */ 
/*  516 */     int pos = HashCommon.murmurHash3(k) & mask;
/*      */ 
/*  518 */     while (used[pos] != 0) {
/*  519 */       if (k == key[pos]) {
/*  520 */         float oldValue = this.value[pos];
/*  521 */         this.value[pos] = v;
/*  522 */         moveIndexToFirst(pos);
/*  523 */         return oldValue;
/*      */       }
/*  525 */       pos = pos + 1 & mask;
/*      */     }
/*  527 */     used[pos] = true;
/*  528 */     key[pos] = k;
/*  529 */     this.value[pos] = v;
/*  530 */     if (this.size == 0) {
/*  531 */       this.first = (this.last = pos);
/*      */ 
/*  533 */       this.link[pos] = -1L;
/*      */     }
/*      */     else {
/*  536 */       this.link[this.first] ^= (this.link[this.first] ^ (pos & 0xFFFFFFFF) << 32) & 0x0;
/*  537 */       this.link[pos] = (0x0 | this.first & 0xFFFFFFFF);
/*  538 */       this.first = pos;
/*      */     }
/*  540 */     if (++this.size >= this.maxFill) rehash(HashCommon.arraySize(this.size, this.f));
/*      */ 
/*  542 */     return this.defRetValue;
/*      */   }
/*      */ 
/*      */   public float putAndMoveToLast(int k, float v)
/*      */   {
/*  551 */     int[] key = this.key;
/*  552 */     boolean[] used = this.used;
/*  553 */     int mask = this.mask;
/*      */ 
/*  555 */     int pos = HashCommon.murmurHash3(k) & mask;
/*      */ 
/*  557 */     while (used[pos] != 0) {
/*  558 */       if (k == key[pos]) {
/*  559 */         float oldValue = this.value[pos];
/*  560 */         this.value[pos] = v;
/*  561 */         moveIndexToLast(pos);
/*  562 */         return oldValue;
/*      */       }
/*  564 */       pos = pos + 1 & mask;
/*      */     }
/*  566 */     used[pos] = true;
/*  567 */     key[pos] = k;
/*  568 */     this.value[pos] = v;
/*  569 */     if (this.size == 0) {
/*  570 */       this.first = (this.last = pos);
/*      */ 
/*  572 */       this.link[pos] = -1L;
/*      */     }
/*      */     else {
/*  575 */       this.link[this.last] ^= (this.link[this.last] ^ pos & 0xFFFFFFFF) & 0xFFFFFFFF;
/*  576 */       this.link[pos] = ((this.last & 0xFFFFFFFF) << 32 | 0xFFFFFFFF);
/*  577 */       this.last = pos;
/*      */     }
/*  579 */     if (++this.size >= this.maxFill) rehash(HashCommon.arraySize(this.size, this.f));
/*      */ 
/*  581 */     return this.defRetValue;
/*      */   }
/*      */   public Float get(Integer ok) {
/*  584 */     int k = ok.intValue();
/*      */ 
/*  586 */     int pos = HashCommon.murmurHash3(k) & this.mask;
/*      */ 
/*  588 */     while (this.used[pos] != 0) {
/*  589 */       if (this.key[pos] == k) return Float.valueOf(this.value[pos]);
/*  590 */       pos = pos + 1 & this.mask;
/*      */     }
/*  592 */     return null;
/*      */   }
/*      */ 
/*      */   public float get(int k)
/*      */   {
/*  597 */     int pos = HashCommon.murmurHash3(k) & this.mask;
/*      */ 
/*  599 */     while (this.used[pos] != 0) {
/*  600 */       if (this.key[pos] == k) return this.value[pos];
/*  601 */       pos = pos + 1 & this.mask;
/*      */     }
/*  603 */     return this.defRetValue;
/*      */   }
/*      */ 
/*      */   public boolean containsKey(int k)
/*      */   {
/*  608 */     int pos = HashCommon.murmurHash3(k) & this.mask;
/*      */ 
/*  610 */     while (this.used[pos] != 0) {
/*  611 */       if (this.key[pos] == k) return true;
/*  612 */       pos = pos + 1 & this.mask;
/*      */     }
/*  614 */     return false;
/*      */   }
/*      */   public boolean containsValue(float v) {
/*  617 */     float[] value = this.value;
/*  618 */     boolean[] used = this.used;
/*  619 */     for (int i = this.n; i-- != 0; return true) label16: if ((used[i] == 0) || (value[i] != v))
/*      */         break label16; return false;
/*      */   }
/*      */ 
/*      */   public void clear()
/*      */   {
/*  629 */     if (this.size == 0) return;
/*  630 */     this.size = 0;
/*  631 */     BooleanArrays.fill(this.used, false);
/*      */ 
/*  633 */     this.first = (this.last = -1);
/*      */   }
/*      */   public int size() {
/*  636 */     return this.size;
/*      */   }
/*      */   public boolean isEmpty() {
/*  639 */     return this.size == 0;
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void growthFactor(int growthFactor)
/*      */   {
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public int growthFactor()
/*      */   {
/*  656 */     return 16;
/*      */   }
/*      */ 
/*      */   protected void fixPointers(int i)
/*      */   {
/*  709 */     if (this.size == 0) {
/*  710 */       this.first = (this.last = -1);
/*  711 */       return;
/*      */     }
/*  713 */     if (this.first == i) {
/*  714 */       this.first = ((int)this.link[i]);
/*  715 */       if (0 <= this.first)
/*      */       {
/*  717 */         this.link[this.first] |= -4294967296L;
/*      */       }
/*  719 */       return;
/*      */     }
/*  721 */     if (this.last == i) {
/*  722 */       this.last = ((int)(this.link[i] >>> 32));
/*  723 */       if (0 <= this.last)
/*      */       {
/*  725 */         this.link[this.last] |= 4294967295L;
/*      */       }
/*  727 */       return;
/*      */     }
/*  729 */     long linki = this.link[i];
/*  730 */     int prev = (int)(linki >>> 32);
/*  731 */     int next = (int)linki;
/*  732 */     this.link[prev] ^= (this.link[prev] ^ linki & 0xFFFFFFFF) & 0xFFFFFFFF;
/*  733 */     this.link[next] ^= (this.link[next] ^ linki & 0x0) & 0x0;
/*      */   }
/*      */ 
/*      */   protected void fixPointers(int s, int d)
/*      */   {
/*  744 */     if (this.size == 1) {
/*  745 */       this.first = (this.last = d);
/*      */ 
/*  747 */       this.link[d] = -1L;
/*  748 */       return;
/*      */     }
/*  750 */     if (this.first == s) {
/*  751 */       this.first = d;
/*  752 */       this.link[((int)this.link[s])] ^= (this.link[((int)this.link[s])] ^ (d & 0xFFFFFFFF) << 32) & 0x0;
/*  753 */       this.link[d] = this.link[s];
/*  754 */       return;
/*      */     }
/*  756 */     if (this.last == s) {
/*  757 */       this.last = d;
/*  758 */       this.link[((int)(this.link[s] >>> 32))] ^= (this.link[((int)(this.link[s] >>> 32))] ^ d & 0xFFFFFFFF) & 0xFFFFFFFF;
/*  759 */       this.link[d] = this.link[s];
/*  760 */       return;
/*      */     }
/*  762 */     long links = this.link[s];
/*  763 */     int prev = (int)(links >>> 32);
/*  764 */     int next = (int)links;
/*  765 */     this.link[prev] ^= (this.link[prev] ^ d & 0xFFFFFFFF) & 0xFFFFFFFF;
/*  766 */     this.link[next] ^= (this.link[next] ^ (d & 0xFFFFFFFF) << 32) & 0x0;
/*  767 */     this.link[d] = links;
/*      */   }
/*      */ 
/*      */   public int firstIntKey()
/*      */   {
/*  774 */     if (this.size == 0) throw new NoSuchElementException();
/*  775 */     return this.key[this.first];
/*      */   }
/*      */ 
/*      */   public int lastIntKey()
/*      */   {
/*  782 */     if (this.size == 0) throw new NoSuchElementException();
/*  783 */     return this.key[this.last];
/*      */   }
/*  785 */   public IntComparator comparator() { return null; } 
/*  786 */   public Int2FloatSortedMap tailMap(int from) { throw new UnsupportedOperationException(); } 
/*  787 */   public Int2FloatSortedMap headMap(int to) { throw new UnsupportedOperationException(); } 
/*  788 */   public Int2FloatSortedMap subMap(int from, int to) { throw new UnsupportedOperationException(); }
/*      */ 
/*      */ 
/*      */   public Int2FloatSortedMap.FastSortedEntrySet int2FloatEntrySet()
/*      */   {
/* 1028 */     if (this.entries == null) this.entries = new MapEntrySet(null);
/* 1029 */     return this.entries;
/*      */   }
/*      */ 
/*      */   public IntSortedSet keySet()
/*      */   {
/* 1084 */     if (this.keys == null) this.keys = new KeySet(null);
/* 1085 */     return this.keys;
/*      */   }
/*      */ 
/*      */   public FloatCollection values()
/*      */   {
/* 1105 */     if (this.values == null) this.values = new AbstractFloatCollection() {
/*      */         public FloatIterator iterator() {
/* 1107 */           return new Int2FloatLinkedOpenHashMap.ValueIterator(Int2FloatLinkedOpenHashMap.this);
/*      */         }
/*      */         public int size() {
/* 1110 */           return Int2FloatLinkedOpenHashMap.this.size;
/*      */         }
/*      */         public boolean contains(float v) {
/* 1113 */           return Int2FloatLinkedOpenHashMap.this.containsValue(v);
/*      */         }
/*      */         public void clear() {
/* 1116 */           Int2FloatLinkedOpenHashMap.this.clear();
/*      */         }
/*      */       };
/* 1119 */     return this.values;
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public boolean rehash()
/*      */   {
/* 1133 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean trim()
/*      */   {
/* 1148 */     int l = HashCommon.arraySize(this.size, this.f);
/* 1149 */     if (l >= this.n) return true; try
/*      */     {
/* 1151 */       rehash(l);
/*      */     } catch (OutOfMemoryError cantDoIt) {
/* 1153 */       return false;
/* 1154 */     }return true;
/*      */   }
/*      */ 
/*      */   public boolean trim(int n)
/*      */   {
/* 1175 */     int l = HashCommon.nextPowerOfTwo((int)Math.ceil(n / this.f));
/* 1176 */     if (this.n <= l) return true; try
/*      */     {
/* 1178 */       rehash(l);
/*      */     } catch (OutOfMemoryError cantDoIt) {
/* 1180 */       return false;
/* 1181 */     }return true;
/*      */   }
/*      */ 
/*      */   protected void rehash(int newN)
/*      */   {
/* 1194 */     int i = this.first; int prev = -1; int newPrev = -1;
/*      */ 
/* 1196 */     int[] key = this.key;
/* 1197 */     float[] value = this.value;
/* 1198 */     int newMask = newN - 1;
/* 1199 */     int[] newKey = new int[newN];
/* 1200 */     float[] newValue = new float[newN];
/* 1201 */     boolean[] newUsed = new boolean[newN];
/* 1202 */     long[] link = this.link;
/* 1203 */     long[] newLink = new long[newN];
/* 1204 */     this.first = -1;
/* 1205 */     for (int j = this.size; j-- != 0; ) {
/* 1206 */       int k = key[i];
/* 1207 */       int pos = HashCommon.murmurHash3(k) & newMask;
/* 1208 */       while (newUsed[pos] != 0) pos = pos + 1 & newMask;
/* 1209 */       newUsed[pos] = true;
/* 1210 */       newKey[pos] = k;
/* 1211 */       newValue[pos] = value[i];
/* 1212 */       if (prev != -1) {
/* 1213 */         newLink[newPrev] ^= (newLink[newPrev] ^ pos & 0xFFFFFFFF) & 0xFFFFFFFF;
/* 1214 */         newLink[pos] ^= (newLink[pos] ^ (newPrev & 0xFFFFFFFF) << 32) & 0x0;
/* 1215 */         newPrev = pos;
/*      */       }
/*      */       else {
/* 1218 */         newPrev = this.first = pos;
/*      */ 
/* 1220 */         newLink[pos] = -1L;
/*      */       }
/* 1222 */       int t = i;
/* 1223 */       i = (int)link[i];
/* 1224 */       prev = t;
/*      */     }
/* 1226 */     this.n = newN;
/* 1227 */     this.mask = newMask;
/* 1228 */     this.maxFill = HashCommon.maxFill(this.n, this.f);
/* 1229 */     this.key = newKey;
/* 1230 */     this.value = newValue;
/* 1231 */     this.used = newUsed;
/* 1232 */     this.link = newLink;
/* 1233 */     this.last = newPrev;
/* 1234 */     if (newPrev != -1)
/*      */     {
/* 1236 */       newLink[newPrev] |= 4294967295L;
/*      */     }
/*      */   }
/*      */ 
/*      */   public Int2FloatLinkedOpenHashMap clone()
/*      */   {
/*      */     Int2FloatLinkedOpenHashMap c;
/*      */     try
/*      */     {
/* 1249 */       c = (Int2FloatLinkedOpenHashMap)super.clone();
/*      */     }
/*      */     catch (CloneNotSupportedException cantHappen) {
/* 1252 */       throw new InternalError();
/*      */     }
/* 1254 */     c.keys = null;
/* 1255 */     c.values = null;
/* 1256 */     c.entries = null;
/* 1257 */     c.key = ((int[])this.key.clone());
/* 1258 */     c.value = ((float[])this.value.clone());
/* 1259 */     c.used = ((boolean[])this.used.clone());
/* 1260 */     c.link = ((long[])this.link.clone());
/* 1261 */     return c;
/*      */   }
/*      */ 
/*      */   public int hashCode()
/*      */   {
/* 1273 */     int h = 0;
/* 1274 */     int j = this.size; int i = 0; for (int t = 0; j-- != 0; ) {
/* 1275 */       while (this.used[i] == 0) i++;
/* 1276 */       t = this.key[i];
/* 1277 */       t ^= HashCommon.float2int(this.value[i]);
/* 1278 */       h += t;
/* 1279 */       i++;
/*      */     }
/* 1281 */     return h;
/*      */   }
/*      */   private void writeObject(ObjectOutputStream s) throws IOException {
/* 1284 */     int[] key = this.key;
/* 1285 */     float[] value = this.value;
/* 1286 */     MapIterator i = new MapIterator(null);
/* 1287 */     s.defaultWriteObject();
/* 1288 */     for (int j = this.size; j-- != 0; ) {
/* 1289 */       int e = i.nextEntry();
/* 1290 */       s.writeInt(key[e]);
/* 1291 */       s.writeFloat(value[e]);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
/* 1296 */     s.defaultReadObject();
/* 1297 */     this.n = HashCommon.arraySize(this.size, this.f);
/* 1298 */     this.maxFill = HashCommon.maxFill(this.n, this.f);
/* 1299 */     this.mask = (this.n - 1);
/* 1300 */     int[] key = this.key = new int[this.n];
/* 1301 */     float[] value = this.value = new float[this.n];
/* 1302 */     boolean[] used = this.used = new boolean[this.n];
/* 1303 */     long[] link = this.link = new long[this.n];
/* 1304 */     int prev = -1;
/* 1305 */     this.first = (this.last = -1);
/*      */ 
/* 1308 */     int i = this.size; for (int pos = 0; i-- != 0; ) {
/* 1309 */       int k = s.readInt();
/* 1310 */       float v = s.readFloat();
/* 1311 */       pos = HashCommon.murmurHash3(k) & this.mask;
/* 1312 */       while (used[pos] != 0) pos = pos + 1 & this.mask;
/* 1313 */       used[pos] = true;
/* 1314 */       key[pos] = k;
/* 1315 */       value[pos] = v;
/* 1316 */       if (this.first != -1) {
/* 1317 */         link[prev] ^= (link[prev] ^ pos & 0xFFFFFFFF) & 0xFFFFFFFF;
/* 1318 */         link[pos] ^= (link[pos] ^ (prev & 0xFFFFFFFF) << 32) & 0x0;
/* 1319 */         prev = pos;
/*      */       }
/*      */       else {
/* 1322 */         prev = this.first = pos;
/*      */ 
/* 1324 */         link[pos] |= -4294967296L;
/*      */       }
/*      */     }
/* 1327 */     this.last = prev;
/* 1328 */     if (prev != -1)
/*      */     {
/* 1330 */       link[prev] |= 4294967295L;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void checkTable()
/*      */   {
/*      */   }
/*      */ 
/*      */   private final class ValueIterator extends Int2FloatLinkedOpenHashMap.MapIterator
/*      */     implements FloatListIterator
/*      */   {
/*      */     public float previousFloat()
/*      */     {
/* 1094 */       return Int2FloatLinkedOpenHashMap.this.value[previousEntry()]; } 
/* 1095 */     public Float previous() { return Float.valueOf(Int2FloatLinkedOpenHashMap.this.value[previousEntry()]); } 
/* 1096 */     public void set(Float ok) { throw new UnsupportedOperationException(); } 
/* 1097 */     public void add(Float ok) { throw new UnsupportedOperationException(); } 
/* 1098 */     public void set(float v) { throw new UnsupportedOperationException(); } 
/* 1099 */     public void add(float v) { throw new UnsupportedOperationException(); } 
/* 1100 */     public ValueIterator() { super(null); } 
/* 1101 */     public float nextFloat() { return Int2FloatLinkedOpenHashMap.this.value[nextEntry()]; } 
/* 1102 */     public Float next() { return Float.valueOf(Int2FloatLinkedOpenHashMap.this.value[nextEntry()]); }
/*      */ 
/*      */   }
/*      */ 
/*      */   private final class KeySet extends AbstractIntSortedSet
/*      */   {
/*      */     private KeySet()
/*      */     {
/*      */     }
/*      */ 
/*      */     public IntListIterator iterator(int from)
/*      */     {
/* 1051 */       return new Int2FloatLinkedOpenHashMap.KeyIterator(Int2FloatLinkedOpenHashMap.this, from);
/*      */     }
/*      */     public IntListIterator iterator() {
/* 1054 */       return new Int2FloatLinkedOpenHashMap.KeyIterator(Int2FloatLinkedOpenHashMap.this);
/*      */     }
/*      */     public int size() {
/* 1057 */       return Int2FloatLinkedOpenHashMap.this.size;
/*      */     }
/*      */     public boolean contains(int k) {
/* 1060 */       return Int2FloatLinkedOpenHashMap.this.containsKey(k);
/*      */     }
/*      */     public boolean remove(int k) {
/* 1063 */       int oldSize = Int2FloatLinkedOpenHashMap.this.size;
/* 1064 */       Int2FloatLinkedOpenHashMap.this.remove(k);
/* 1065 */       return Int2FloatLinkedOpenHashMap.this.size != oldSize;
/*      */     }
/*      */     public void clear() {
/* 1068 */       Int2FloatLinkedOpenHashMap.this.clear();
/*      */     }
/*      */     public int firstInt() {
/* 1071 */       if (Int2FloatLinkedOpenHashMap.this.size == 0) throw new NoSuchElementException();
/* 1072 */       return Int2FloatLinkedOpenHashMap.this.key[Int2FloatLinkedOpenHashMap.this.first];
/*      */     }
/*      */     public int lastInt() {
/* 1075 */       if (Int2FloatLinkedOpenHashMap.this.size == 0) throw new NoSuchElementException();
/* 1076 */       return Int2FloatLinkedOpenHashMap.this.key[Int2FloatLinkedOpenHashMap.this.last];
/*      */     }
/* 1078 */     public IntComparator comparator() { return null; } 
/* 1079 */     public final IntSortedSet tailSet(int from) { throw new UnsupportedOperationException(); } 
/* 1080 */     public final IntSortedSet headSet(int to) { throw new UnsupportedOperationException(); } 
/* 1081 */     public final IntSortedSet subSet(int from, int to) { throw new UnsupportedOperationException(); }
/*      */ 
/*      */   }
/*      */ 
/*      */   private final class KeyIterator extends Int2FloatLinkedOpenHashMap.MapIterator
/*      */     implements IntListIterator
/*      */   {
/*      */     public KeyIterator(int k)
/*      */     {
/* 1038 */       super(k, null); } 
/* 1039 */     public int previousInt() { return Int2FloatLinkedOpenHashMap.this.key[previousEntry()]; } 
/* 1040 */     public void set(int k) { throw new UnsupportedOperationException(); } 
/* 1041 */     public void add(int k) { throw new UnsupportedOperationException(); } 
/* 1042 */     public Integer previous() { return Integer.valueOf(Int2FloatLinkedOpenHashMap.this.key[previousEntry()]); } 
/* 1043 */     public void set(Integer ok) { throw new UnsupportedOperationException(); } 
/* 1044 */     public void add(Integer ok) { throw new UnsupportedOperationException(); } 
/* 1045 */     public KeyIterator() { super(null); } 
/* 1046 */     public int nextInt() { return Int2FloatLinkedOpenHashMap.this.key[nextEntry()]; } 
/* 1047 */     public Integer next() { return Integer.valueOf(Int2FloatLinkedOpenHashMap.this.key[nextEntry()]); }
/*      */ 
/*      */   }
/*      */ 
/*      */   private final class MapEntrySet extends AbstractObjectSortedSet<Int2FloatMap.Entry>
/*      */     implements Int2FloatSortedMap.FastSortedEntrySet
/*      */   {
/*      */     private MapEntrySet()
/*      */     {
/*      */     }
/*      */ 
/*      */     public ObjectBidirectionalIterator<Int2FloatMap.Entry> iterator()
/*      */     {
/*  966 */       return new Int2FloatLinkedOpenHashMap.EntryIterator(Int2FloatLinkedOpenHashMap.this);
/*      */     }
/*  968 */     public Comparator<? super Int2FloatMap.Entry> comparator() { return null; } 
/*  969 */     public ObjectSortedSet<Int2FloatMap.Entry> subSet(Int2FloatMap.Entry fromElement, Int2FloatMap.Entry toElement) { throw new UnsupportedOperationException(); } 
/*  970 */     public ObjectSortedSet<Int2FloatMap.Entry> headSet(Int2FloatMap.Entry toElement) { throw new UnsupportedOperationException(); } 
/*  971 */     public ObjectSortedSet<Int2FloatMap.Entry> tailSet(Int2FloatMap.Entry fromElement) { throw new UnsupportedOperationException(); } 
/*      */     public Int2FloatMap.Entry first() {
/*  973 */       if (Int2FloatLinkedOpenHashMap.this.size == 0) throw new NoSuchElementException();
/*  974 */       return new Int2FloatLinkedOpenHashMap.MapEntry(Int2FloatLinkedOpenHashMap.this, Int2FloatLinkedOpenHashMap.this.first);
/*      */     }
/*      */     public Int2FloatMap.Entry last() {
/*  977 */       if (Int2FloatLinkedOpenHashMap.this.size == 0) throw new NoSuchElementException();
/*  978 */       return new Int2FloatLinkedOpenHashMap.MapEntry(Int2FloatLinkedOpenHashMap.this, Int2FloatLinkedOpenHashMap.this.last);
/*      */     }
/*      */ 
/*      */     public boolean contains(Object o) {
/*  982 */       if (!(o instanceof Map.Entry)) return false;
/*  983 */       Map.Entry e = (Map.Entry)o;
/*  984 */       int k = ((Integer)e.getKey()).intValue();
/*      */ 
/*  986 */       int pos = HashCommon.murmurHash3(k) & Int2FloatLinkedOpenHashMap.this.mask;
/*      */ 
/*  988 */       while (Int2FloatLinkedOpenHashMap.this.used[pos] != 0) {
/*  989 */         if (Int2FloatLinkedOpenHashMap.this.key[pos] == k) return Int2FloatLinkedOpenHashMap.this.value[pos] == ((Float)e.getValue()).floatValue();
/*  990 */         pos = pos + 1 & Int2FloatLinkedOpenHashMap.this.mask;
/*      */       }
/*  992 */       return false;
/*      */     }
/*      */ 
/*      */     public boolean remove(Object o) {
/*  996 */       if (!(o instanceof Map.Entry)) return false;
/*  997 */       Map.Entry e = (Map.Entry)o;
/*  998 */       int k = ((Integer)e.getKey()).intValue();
/*      */ 
/* 1000 */       int pos = HashCommon.murmurHash3(k) & Int2FloatLinkedOpenHashMap.this.mask;
/*      */ 
/* 1002 */       while (Int2FloatLinkedOpenHashMap.this.used[pos] != 0) {
/* 1003 */         if (Int2FloatLinkedOpenHashMap.this.key[pos] == k) {
/* 1004 */           Int2FloatLinkedOpenHashMap.this.remove(e.getKey());
/* 1005 */           return true;
/*      */         }
/* 1007 */         pos = pos + 1 & Int2FloatLinkedOpenHashMap.this.mask;
/*      */       }
/* 1009 */       return false;
/*      */     }
/*      */     public int size() {
/* 1012 */       return Int2FloatLinkedOpenHashMap.this.size;
/*      */     }
/*      */     public void clear() {
/* 1015 */       Int2FloatLinkedOpenHashMap.this.clear();
/*      */     }
/*      */     public ObjectBidirectionalIterator<Int2FloatMap.Entry> iterator(Int2FloatMap.Entry from) {
/* 1018 */       return new Int2FloatLinkedOpenHashMap.EntryIterator(Int2FloatLinkedOpenHashMap.this, ((Integer)from.getKey()).intValue());
/*      */     }
/*      */     public ObjectBidirectionalIterator<Int2FloatMap.Entry> fastIterator() {
/* 1021 */       return new Int2FloatLinkedOpenHashMap.FastEntryIterator(Int2FloatLinkedOpenHashMap.this);
/*      */     }
/*      */     public ObjectBidirectionalIterator<Int2FloatMap.Entry> fastIterator(Int2FloatMap.Entry from) {
/* 1024 */       return new Int2FloatLinkedOpenHashMap.FastEntryIterator(Int2FloatLinkedOpenHashMap.this, ((Integer)from.getKey()).intValue());
/*      */     }
/*      */   }
/*      */ 
/*      */   private class FastEntryIterator extends Int2FloatLinkedOpenHashMap.MapIterator
/*      */     implements ObjectListIterator<Int2FloatMap.Entry>
/*      */   {
/*  944 */     final AbstractInt2FloatMap.BasicEntry entry = new AbstractInt2FloatMap.BasicEntry(0, 0.0F);
/*      */ 
/*  945 */     public FastEntryIterator() { super(null); } 
/*      */     public FastEntryIterator(int from) {
/*  947 */       super(from, null);
/*      */     }
/*      */     public AbstractInt2FloatMap.BasicEntry next() {
/*  950 */       int e = nextEntry();
/*  951 */       this.entry.key = Int2FloatLinkedOpenHashMap.this.key[e];
/*  952 */       this.entry.value = Int2FloatLinkedOpenHashMap.this.value[e];
/*  953 */       return this.entry;
/*      */     }
/*      */     public AbstractInt2FloatMap.BasicEntry previous() {
/*  956 */       int e = previousEntry();
/*  957 */       this.entry.key = Int2FloatLinkedOpenHashMap.this.key[e];
/*  958 */       this.entry.value = Int2FloatLinkedOpenHashMap.this.value[e];
/*  959 */       return this.entry;
/*      */     }
/*  961 */     public void set(Int2FloatMap.Entry ok) { throw new UnsupportedOperationException(); } 
/*  962 */     public void add(Int2FloatMap.Entry ok) { throw new UnsupportedOperationException(); }
/*      */ 
/*      */   }
/*      */ 
/*      */   private class EntryIterator extends Int2FloatLinkedOpenHashMap.MapIterator
/*      */     implements ObjectListIterator<Int2FloatMap.Entry>
/*      */   {
/*      */     private Int2FloatLinkedOpenHashMap.MapEntry entry;
/*      */ 
/*      */     public EntryIterator()
/*      */     {
/*  925 */       super(null);
/*      */     }
/*  927 */     public EntryIterator(int from) { super(from, null); }
/*      */ 
/*      */     public Int2FloatLinkedOpenHashMap.MapEntry next() {
/*  930 */       return this.entry = new Int2FloatLinkedOpenHashMap.MapEntry(Int2FloatLinkedOpenHashMap.this, nextEntry());
/*      */     }
/*      */     public Int2FloatLinkedOpenHashMap.MapEntry previous() {
/*  933 */       return this.entry = new Int2FloatLinkedOpenHashMap.MapEntry(Int2FloatLinkedOpenHashMap.this, previousEntry());
/*      */     }
/*      */ 
/*      */     public void remove() {
/*  937 */       super.remove();
/*  938 */       Int2FloatLinkedOpenHashMap.MapEntry.access$202(this.entry, -1);
/*      */     }
/*  940 */     public void set(Int2FloatMap.Entry ok) { throw new UnsupportedOperationException(); } 
/*  941 */     public void add(Int2FloatMap.Entry ok) { throw new UnsupportedOperationException(); }
/*      */ 
/*      */   }
/*      */ 
/*      */   private class MapIterator
/*      */   {
/*  797 */     int prev = -1;
/*      */ 
/*  799 */     int next = -1;
/*      */ 
/*  801 */     int curr = -1;
/*      */ 
/*  803 */     int index = -1;
/*      */ 
/*  805 */     private MapIterator() { this.next = Int2FloatLinkedOpenHashMap.this.first;
/*  806 */       this.index = 0; }
/*      */ 
/*      */     private MapIterator(int from) {
/*  809 */       if (Int2FloatLinkedOpenHashMap.this.key[Int2FloatLinkedOpenHashMap.this.last] == from) {
/*  810 */         this.prev = Int2FloatLinkedOpenHashMap.this.last;
/*  811 */         this.index = Int2FloatLinkedOpenHashMap.this.size;
/*      */       }
/*      */       else
/*      */       {
/*  815 */         int pos = HashCommon.murmurHash3(from) & Int2FloatLinkedOpenHashMap.this.mask;
/*      */ 
/*  817 */         while (Int2FloatLinkedOpenHashMap.this.used[pos] != 0) {
/*  818 */           if (Int2FloatLinkedOpenHashMap.this.key[pos] == from)
/*      */           {
/*  820 */             this.next = ((int)Int2FloatLinkedOpenHashMap.this.link[pos]);
/*  821 */             this.prev = pos;
/*  822 */             return;
/*      */           }
/*  824 */           pos = pos + 1 & Int2FloatLinkedOpenHashMap.this.mask;
/*      */         }
/*  826 */         throw new NoSuchElementException("The key " + from + " does not belong to this map.");
/*      */       }
/*      */     }
/*  829 */     public boolean hasNext() { return this.next != -1; } 
/*  830 */     public boolean hasPrevious() { return this.prev != -1; } 
/*      */     private final void ensureIndexKnown() {
/*  832 */       if (this.index >= 0) return;
/*  833 */       if (this.prev == -1) {
/*  834 */         this.index = 0;
/*  835 */         return;
/*      */       }
/*  837 */       if (this.next == -1) {
/*  838 */         this.index = Int2FloatLinkedOpenHashMap.this.size;
/*  839 */         return;
/*      */       }
/*  841 */       int pos = Int2FloatLinkedOpenHashMap.this.first;
/*  842 */       this.index = 1;
/*  843 */       while (pos != this.prev) {
/*  844 */         pos = (int)Int2FloatLinkedOpenHashMap.this.link[pos];
/*  845 */         this.index += 1;
/*      */       }
/*      */     }
/*      */ 
/*  849 */     public int nextIndex() { ensureIndexKnown();
/*  850 */       return this.index; }
/*      */ 
/*      */     public int previousIndex() {
/*  853 */       ensureIndexKnown();
/*  854 */       return this.index - 1;
/*      */     }
/*      */     public int nextEntry() {
/*  857 */       if (!hasNext()) return Int2FloatLinkedOpenHashMap.this.size();
/*  858 */       this.curr = this.next;
/*  859 */       this.next = ((int)Int2FloatLinkedOpenHashMap.this.link[this.curr]);
/*  860 */       this.prev = this.curr;
/*  861 */       if (this.index >= 0) this.index += 1;
/*  862 */       return this.curr;
/*      */     }
/*      */     public int previousEntry() {
/*  865 */       if (!hasPrevious()) return -1;
/*  866 */       this.curr = this.prev;
/*  867 */       this.prev = ((int)(Int2FloatLinkedOpenHashMap.this.link[this.curr] >>> 32));
/*  868 */       this.next = this.curr;
/*  869 */       if (this.index >= 0) this.index -= 1;
/*  870 */       return this.curr;
/*      */     }
/*      */ 
/*      */     public void remove() {
/*  874 */       ensureIndexKnown();
/*  875 */       if (this.curr == -1) throw new IllegalStateException();
/*  876 */       if (this.curr == this.prev)
/*      */       {
/*  879 */         this.index -= 1;
/*  880 */         this.prev = ((int)(Int2FloatLinkedOpenHashMap.this.link[this.curr] >>> 32));
/*      */       }
/*      */       else {
/*  883 */         this.next = ((int)Int2FloatLinkedOpenHashMap.this.link[this.curr]);
/*  884 */       }Int2FloatLinkedOpenHashMap.this.size -= 1;
/*      */ 
/*  887 */       if (this.prev == -1) Int2FloatLinkedOpenHashMap.this.first = this.next;
/*      */       else
/*  889 */         Int2FloatLinkedOpenHashMap.this.link[this.prev] ^= (Int2FloatLinkedOpenHashMap.this.link[this.prev] ^ this.next & 0xFFFFFFFF) & 0xFFFFFFFF;
/*  890 */       if (this.next == -1) Int2FloatLinkedOpenHashMap.this.last = this.prev;
/*      */       else
/*  892 */         Int2FloatLinkedOpenHashMap.this.link[this.next] ^= (Int2FloatLinkedOpenHashMap.this.link[this.next] ^ (this.prev & 0xFFFFFFFF) << 32) & 0x0; int pos = this.curr;
/*      */       int last;
/*      */       while (true) {
/*  896 */         pos = (last = pos) + 1 & Int2FloatLinkedOpenHashMap.this.mask;
/*  897 */         while (Int2FloatLinkedOpenHashMap.this.used[pos] != 0) {
/*  898 */           int slot = HashCommon.murmurHash3(Int2FloatLinkedOpenHashMap.this.key[pos]) & Int2FloatLinkedOpenHashMap.this.mask;
/*  899 */           if (last <= pos ? (last < slot) && (slot <= pos) : (last >= slot) && (slot > pos)) break;
/*  900 */           pos = pos + 1 & Int2FloatLinkedOpenHashMap.this.mask;
/*      */         }
/*  902 */         if (Int2FloatLinkedOpenHashMap.this.used[pos] == 0) break;
/*  903 */         Int2FloatLinkedOpenHashMap.this.key[last] = Int2FloatLinkedOpenHashMap.this.key[pos];
/*  904 */         Int2FloatLinkedOpenHashMap.this.value[last] = Int2FloatLinkedOpenHashMap.this.value[pos];
/*  905 */         if (this.next == pos) this.next = last;
/*  906 */         if (this.prev == pos) this.prev = last;
/*  907 */         Int2FloatLinkedOpenHashMap.this.fixPointers(pos, last);
/*      */       }
/*  909 */       Int2FloatLinkedOpenHashMap.this.used[last] = false;
/*  910 */       this.curr = -1;
/*      */     }
/*      */     public int skip(int n) {
/*  913 */       int i = n;
/*  914 */       while ((i-- != 0) && (hasNext())) nextEntry();
/*  915 */       return n - i - 1;
/*      */     }
/*      */     public int back(int n) {
/*  918 */       int i = n;
/*  919 */       while ((i-- != 0) && (hasPrevious())) previousEntry();
/*  920 */       return n - i - 1;
/*      */     }
/*      */   }
/*      */ 
/*      */   private final class MapEntry
/*      */     implements Int2FloatMap.Entry, Map.Entry<Integer, Float>
/*      */   {
/*      */     private int index;
/*      */ 
/*      */     MapEntry(int index)
/*      */     {
/*  666 */       this.index = index;
/*      */     }
/*      */     public Integer getKey() {
/*  669 */       return Integer.valueOf(Int2FloatLinkedOpenHashMap.this.key[this.index]);
/*      */     }
/*      */     public int getIntKey() {
/*  672 */       return Int2FloatLinkedOpenHashMap.this.key[this.index];
/*      */     }
/*      */     public Float getValue() {
/*  675 */       return Float.valueOf(Int2FloatLinkedOpenHashMap.this.value[this.index]);
/*      */     }
/*      */     public float getFloatValue() {
/*  678 */       return Int2FloatLinkedOpenHashMap.this.value[this.index];
/*      */     }
/*      */     public float setValue(float v) {
/*  681 */       float oldValue = Int2FloatLinkedOpenHashMap.this.value[this.index];
/*  682 */       Int2FloatLinkedOpenHashMap.this.value[this.index] = v;
/*  683 */       return oldValue;
/*      */     }
/*      */     public Float setValue(Float v) {
/*  686 */       return Float.valueOf(setValue(v.floatValue()));
/*      */     }
/*      */ 
/*      */     public boolean equals(Object o) {
/*  690 */       if (!(o instanceof Map.Entry)) return false;
/*  691 */       Map.Entry e = (Map.Entry)o;
/*  692 */       return (Int2FloatLinkedOpenHashMap.this.key[this.index] == ((Integer)e.getKey()).intValue()) && (Int2FloatLinkedOpenHashMap.this.value[this.index] == ((Float)e.getValue()).floatValue());
/*      */     }
/*      */     public int hashCode() {
/*  695 */       return Int2FloatLinkedOpenHashMap.this.key[this.index] ^ HashCommon.float2int(Int2FloatLinkedOpenHashMap.this.value[this.index]);
/*      */     }
/*      */     public String toString() {
/*  698 */       return Int2FloatLinkedOpenHashMap.this.key[this.index] + "=>" + Int2FloatLinkedOpenHashMap.this.value[this.index];
/*      */     }
/*      */   }
/*      */ }

/* Location:           C:\Users\Raul\Desktop\StarMade\StarMade.jar
 * Qualified Name:     it.unimi.dsi.fastutil.ints.Int2FloatLinkedOpenHashMap
 * JD-Core Version:    0.6.2
 */