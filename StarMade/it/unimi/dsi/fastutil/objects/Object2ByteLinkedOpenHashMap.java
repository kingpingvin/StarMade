/*      */ package it.unimi.dsi.fastutil.objects;
/*      */ 
/*      */ import it.unimi.dsi.fastutil.Hash;
/*      */ import it.unimi.dsi.fastutil.HashCommon;
/*      */ import it.unimi.dsi.fastutil.booleans.BooleanArrays;
/*      */ import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
/*      */ import it.unimi.dsi.fastutil.bytes.ByteCollection;
/*      */ import it.unimi.dsi.fastutil.bytes.ByteIterator;
/*      */ import it.unimi.dsi.fastutil.bytes.ByteListIterator;
/*      */ import java.io.IOException;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.ObjectOutputStream;
/*      */ import java.io.Serializable;
/*      */ import java.util.Comparator;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.NoSuchElementException;
/*      */ 
/*      */ public class Object2ByteLinkedOpenHashMap<K> extends AbstractObject2ByteSortedMap<K>
/*      */   implements Serializable, Cloneable, Hash
/*      */ {
/*      */   public static final long serialVersionUID = 0L;
/*      */   private static final boolean ASSERTS = false;
/*      */   protected transient K[] key;
/*      */   protected transient byte[] value;
/*      */   protected transient boolean[] used;
/*      */   protected final float f;
/*      */   protected transient int n;
/*      */   protected transient int maxFill;
/*      */   protected transient int mask;
/*      */   protected int size;
/*      */   protected volatile transient Object2ByteSortedMap.FastSortedEntrySet<K> entries;
/*      */   protected volatile transient ObjectSortedSet<K> keys;
/*      */   protected volatile transient ByteCollection values;
/*  130 */   protected transient int first = -1;
/*      */ 
/*  132 */   protected transient int last = -1;
/*      */   protected transient long[] link;
/*      */ 
/*      */   public Object2ByteLinkedOpenHashMap(int expected, float f)
/*      */   {
/*  151 */     if ((f <= 0.0F) || (f > 1.0F)) throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
/*  152 */     if (expected < 0) throw new IllegalArgumentException("The expected number of elements must be nonnegative");
/*  153 */     this.f = f;
/*  154 */     this.n = HashCommon.arraySize(expected, f);
/*  155 */     this.mask = (this.n - 1);
/*  156 */     this.maxFill = HashCommon.maxFill(this.n, f);
/*  157 */     this.key = ((Object[])new Object[this.n]);
/*  158 */     this.value = new byte[this.n];
/*  159 */     this.used = new boolean[this.n];
/*  160 */     this.link = new long[this.n];
/*      */   }
/*      */ 
/*      */   public Object2ByteLinkedOpenHashMap(int expected)
/*      */   {
/*  167 */     this(expected, 0.75F);
/*      */   }
/*      */ 
/*      */   public Object2ByteLinkedOpenHashMap()
/*      */   {
/*  173 */     this(16, 0.75F);
/*      */   }
/*      */ 
/*      */   public Object2ByteLinkedOpenHashMap(Map<? extends K, ? extends Byte> m, float f)
/*      */   {
/*  181 */     this(m.size(), f);
/*  182 */     putAll(m);
/*      */   }
/*      */ 
/*      */   public Object2ByteLinkedOpenHashMap(Map<? extends K, ? extends Byte> m)
/*      */   {
/*  189 */     this(m, 0.75F);
/*      */   }
/*      */ 
/*      */   public Object2ByteLinkedOpenHashMap(Object2ByteMap<K> m, float f)
/*      */   {
/*  197 */     this(m.size(), f);
/*  198 */     putAll(m);
/*      */   }
/*      */ 
/*      */   public Object2ByteLinkedOpenHashMap(Object2ByteMap<K> m)
/*      */   {
/*  205 */     this(m, 0.75F);
/*      */   }
/*      */ 
/*      */   public Object2ByteLinkedOpenHashMap(K[] k, byte[] v, float f)
/*      */   {
/*  215 */     this(k.length, f);
/*  216 */     if (k.length != v.length) throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
/*  217 */     for (int i = 0; i < k.length; i++) put(k[i], v[i]);
/*      */   }
/*      */ 
/*      */   public Object2ByteLinkedOpenHashMap(K[] k, byte[] v)
/*      */   {
/*  226 */     this(k, v, 0.75F);
/*      */   }
/*      */ 
/*      */   public byte put(K k, byte v)
/*      */   {
/*  234 */     int pos = (k == null ? 142593372 : HashCommon.murmurHash3(k.hashCode())) & this.mask;
/*      */ 
/*  236 */     while (this.used[pos] != 0) {
/*  237 */       if (this.key[pos] == null ? k == null : this.key[pos].equals(k)) {
/*  238 */         byte oldValue = this.value[pos];
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
/*      */   public Byte put(K ok, Byte ov) {
/*  262 */     byte v = ov.byteValue();
/*  263 */     Object k = ok;
/*      */ 
/*  265 */     int pos = (k == null ? 142593372 : HashCommon.murmurHash3(k.hashCode())) & this.mask;
/*      */ 
/*  267 */     while (this.used[pos] != 0) {
/*  268 */       if (this.key[pos] == null ? k == null : this.key[pos].equals(k)) {
/*  269 */         Byte oldValue = Byte.valueOf(this.value[pos]);
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
/*      */   public byte add(K k, byte incr)
/*      */   {
/*  305 */     int pos = (k == null ? 142593372 : HashCommon.murmurHash3(k.hashCode())) & this.mask;
/*      */ 
/*  307 */     while (this.used[pos] != 0) {
/*  308 */       if (this.key[pos] == null ? k == null : this.key[pos].equals(k)) {
/*  309 */         byte oldValue = this.value[pos];
/*      */         int tmp73_72 = pos;
/*      */         byte[] tmp73_69 = this.value; tmp73_69[tmp73_72] = ((byte)(tmp73_69[tmp73_72] + incr));
/*  311 */         return oldValue;
/*      */       }
/*  313 */       pos = pos + 1 & this.mask;
/*      */     }
/*  315 */     this.used[pos] = true;
/*  316 */     this.key[pos] = k;
/*  317 */     this.value[pos] = ((byte)(this.defRetValue + incr));
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
/*  344 */         int slot = (this.key[pos] == null ? 142593372 : HashCommon.murmurHash3(this.key[pos].hashCode())) & this.mask;
/*  345 */         if (last <= pos ? (last < slot) && (slot <= pos) : (last >= slot) && (slot > pos)) break;
/*  346 */         pos = pos + 1 & this.mask;
/*      */       }
/*  348 */       if (this.used[pos] == 0) break;
/*  349 */       this.key[last] = this.key[pos];
/*  350 */       this.value[last] = this.value[pos];
/*  351 */       fixPointers(pos, last);
/*      */     }
/*  353 */     this.used[last] = false;
/*  354 */     this.key[last] = null;
/*  355 */     return last;
/*      */   }
/*      */ 
/*      */   public byte removeByte(Object k)
/*      */   {
/*  360 */     int pos = (k == null ? 142593372 : HashCommon.murmurHash3(k.hashCode())) & this.mask;
/*      */ 
/*  362 */     while (this.used[pos] != 0) {
/*  363 */       if (this.key[pos] == null ? k == null : this.key[pos].equals(k)) {
/*  364 */         this.size -= 1;
/*  365 */         fixPointers(pos);
/*  366 */         byte v = this.value[pos];
/*  367 */         shiftKeys(pos);
/*  368 */         return v;
/*      */       }
/*  370 */       pos = pos + 1 & this.mask;
/*      */     }
/*  372 */     return this.defRetValue;
/*      */   }
/*      */ 
/*      */   public Byte remove(Object ok) {
/*  376 */     Object k = ok;
/*      */ 
/*  378 */     int pos = (k == null ? 142593372 : HashCommon.murmurHash3(k.hashCode())) & this.mask;
/*      */ 
/*  380 */     while (this.used[pos] != 0) {
/*  381 */       if (this.key[pos] == null ? k == null : this.key[pos].equals(k)) {
/*  382 */         this.size -= 1;
/*  383 */         fixPointers(pos);
/*  384 */         byte v = this.value[pos];
/*  385 */         shiftKeys(pos);
/*  386 */         return Byte.valueOf(v);
/*      */       }
/*  388 */       pos = pos + 1 & this.mask;
/*      */     }
/*  390 */     return null;
/*      */   }
/*      */ 
/*      */   public byte removeFirstByte()
/*      */   {
/*  397 */     if (this.size == 0) throw new NoSuchElementException();
/*  398 */     this.size -= 1;
/*  399 */     int pos = this.first;
/*      */ 
/*  401 */     this.first = ((int)this.link[pos]);
/*  402 */     if (0 <= this.first)
/*      */     {
/*  404 */       this.link[this.first] |= -4294967296L;
/*      */     }
/*  406 */     byte v = this.value[pos];
/*  407 */     shiftKeys(pos);
/*  408 */     return v;
/*      */   }
/*      */ 
/*      */   public byte removeLastByte()
/*      */   {
/*  415 */     if (this.size == 0) throw new NoSuchElementException();
/*  416 */     this.size -= 1;
/*  417 */     int pos = this.last;
/*      */ 
/*  419 */     this.last = ((int)(this.link[pos] >>> 32));
/*  420 */     if (0 <= this.last)
/*      */     {
/*  422 */       this.link[this.last] |= 4294967295L;
/*      */     }
/*  424 */     byte v = this.value[pos];
/*  425 */     shiftKeys(pos);
/*  426 */     return v;
/*      */   }
/*      */   private void moveIndexToFirst(int i) {
/*  429 */     if ((this.size == 1) || (this.first == i)) return;
/*  430 */     if (this.last == i) {
/*  431 */       this.last = ((int)(this.link[i] >>> 32));
/*      */ 
/*  433 */       this.link[this.last] |= 4294967295L;
/*      */     }
/*      */     else {
/*  436 */       long linki = this.link[i];
/*  437 */       int prev = (int)(linki >>> 32);
/*  438 */       int next = (int)linki;
/*  439 */       this.link[prev] ^= (this.link[prev] ^ linki & 0xFFFFFFFF) & 0xFFFFFFFF;
/*  440 */       this.link[next] ^= (this.link[next] ^ linki & 0x0) & 0x0;
/*      */     }
/*  442 */     this.link[this.first] ^= (this.link[this.first] ^ (i & 0xFFFFFFFF) << 32) & 0x0;
/*  443 */     this.link[i] = (0x0 | this.first & 0xFFFFFFFF);
/*  444 */     this.first = i;
/*      */   }
/*      */   private void moveIndexToLast(int i) {
/*  447 */     if ((this.size == 1) || (this.last == i)) return;
/*  448 */     if (this.first == i) {
/*  449 */       this.first = ((int)this.link[i]);
/*      */ 
/*  451 */       this.link[this.first] |= -4294967296L;
/*      */     }
/*      */     else {
/*  454 */       long linki = this.link[i];
/*  455 */       int prev = (int)(linki >>> 32);
/*  456 */       int next = (int)linki;
/*  457 */       this.link[prev] ^= (this.link[prev] ^ linki & 0xFFFFFFFF) & 0xFFFFFFFF;
/*  458 */       this.link[next] ^= (this.link[next] ^ linki & 0x0) & 0x0;
/*      */     }
/*  460 */     this.link[this.last] ^= (this.link[this.last] ^ i & 0xFFFFFFFF) & 0xFFFFFFFF;
/*  461 */     this.link[i] = ((this.last & 0xFFFFFFFF) << 32 | 0xFFFFFFFF);
/*  462 */     this.last = i;
/*      */   }
/*      */ 
/*      */   public byte getAndMoveToFirst(K k)
/*      */   {
/*  470 */     Object[] key = this.key;
/*  471 */     boolean[] used = this.used;
/*  472 */     int mask = this.mask;
/*      */ 
/*  474 */     int pos = (k == null ? 142593372 : HashCommon.murmurHash3(k.hashCode())) & mask;
/*      */ 
/*  476 */     while (used[pos] != 0) {
/*  477 */       if (k == null ? key[pos] == null : k.equals(key[pos])) {
/*  478 */         moveIndexToFirst(pos);
/*  479 */         return this.value[pos];
/*      */       }
/*  481 */       pos = pos + 1 & mask;
/*      */     }
/*  483 */     return this.defRetValue;
/*      */   }
/*      */ 
/*      */   public byte getAndMoveToLast(K k)
/*      */   {
/*  491 */     Object[] key = this.key;
/*  492 */     boolean[] used = this.used;
/*  493 */     int mask = this.mask;
/*      */ 
/*  495 */     int pos = (k == null ? 142593372 : HashCommon.murmurHash3(k.hashCode())) & mask;
/*      */ 
/*  497 */     while (used[pos] != 0) {
/*  498 */       if (k == null ? key[pos] == null : k.equals(key[pos])) {
/*  499 */         moveIndexToLast(pos);
/*  500 */         return this.value[pos];
/*      */       }
/*  502 */       pos = pos + 1 & mask;
/*      */     }
/*  504 */     return this.defRetValue;
/*      */   }
/*      */ 
/*      */   public byte putAndMoveToFirst(K k, byte v)
/*      */   {
/*  513 */     Object[] key = this.key;
/*  514 */     boolean[] used = this.used;
/*  515 */     int mask = this.mask;
/*      */ 
/*  517 */     int pos = (k == null ? 142593372 : HashCommon.murmurHash3(k.hashCode())) & mask;
/*      */ 
/*  519 */     while (used[pos] != 0) {
/*  520 */       if (k == null ? key[pos] == null : k.equals(key[pos])) {
/*  521 */         byte oldValue = this.value[pos];
/*  522 */         this.value[pos] = v;
/*  523 */         moveIndexToFirst(pos);
/*  524 */         return oldValue;
/*      */       }
/*  526 */       pos = pos + 1 & mask;
/*      */     }
/*  528 */     used[pos] = true;
/*  529 */     key[pos] = k;
/*  530 */     this.value[pos] = v;
/*  531 */     if (this.size == 0) {
/*  532 */       this.first = (this.last = pos);
/*      */ 
/*  534 */       this.link[pos] = -1L;
/*      */     }
/*      */     else {
/*  537 */       this.link[this.first] ^= (this.link[this.first] ^ (pos & 0xFFFFFFFF) << 32) & 0x0;
/*  538 */       this.link[pos] = (0x0 | this.first & 0xFFFFFFFF);
/*  539 */       this.first = pos;
/*      */     }
/*  541 */     if (++this.size >= this.maxFill) rehash(HashCommon.arraySize(this.size, this.f));
/*      */ 
/*  543 */     return this.defRetValue;
/*      */   }
/*      */ 
/*      */   public byte putAndMoveToLast(K k, byte v)
/*      */   {
/*  552 */     Object[] key = this.key;
/*  553 */     boolean[] used = this.used;
/*  554 */     int mask = this.mask;
/*      */ 
/*  556 */     int pos = (k == null ? 142593372 : HashCommon.murmurHash3(k.hashCode())) & mask;
/*      */ 
/*  558 */     while (used[pos] != 0) {
/*  559 */       if (k == null ? key[pos] == null : k.equals(key[pos])) {
/*  560 */         byte oldValue = this.value[pos];
/*  561 */         this.value[pos] = v;
/*  562 */         moveIndexToLast(pos);
/*  563 */         return oldValue;
/*      */       }
/*  565 */       pos = pos + 1 & mask;
/*      */     }
/*  567 */     used[pos] = true;
/*  568 */     key[pos] = k;
/*  569 */     this.value[pos] = v;
/*  570 */     if (this.size == 0) {
/*  571 */       this.first = (this.last = pos);
/*      */ 
/*  573 */       this.link[pos] = -1L;
/*      */     }
/*      */     else {
/*  576 */       this.link[this.last] ^= (this.link[this.last] ^ pos & 0xFFFFFFFF) & 0xFFFFFFFF;
/*  577 */       this.link[pos] = ((this.last & 0xFFFFFFFF) << 32 | 0xFFFFFFFF);
/*  578 */       this.last = pos;
/*      */     }
/*  580 */     if (++this.size >= this.maxFill) rehash(HashCommon.arraySize(this.size, this.f));
/*      */ 
/*  582 */     return this.defRetValue;
/*      */   }
/*      */ 
/*      */   public byte getByte(Object k)
/*      */   {
/*  587 */     int pos = (k == null ? 142593372 : HashCommon.murmurHash3(k.hashCode())) & this.mask;
/*      */ 
/*  589 */     while (this.used[pos] != 0) {
/*  590 */       if (this.key[pos] == null ? k == null : this.key[pos].equals(k)) return this.value[pos];
/*  591 */       pos = pos + 1 & this.mask;
/*      */     }
/*  593 */     return this.defRetValue;
/*      */   }
/*      */ 
/*      */   public boolean containsKey(Object k)
/*      */   {
/*  598 */     int pos = (k == null ? 142593372 : HashCommon.murmurHash3(k.hashCode())) & this.mask;
/*      */ 
/*  600 */     while (this.used[pos] != 0) {
/*  601 */       if (this.key[pos] == null ? k == null : this.key[pos].equals(k)) return true;
/*  602 */       pos = pos + 1 & this.mask;
/*      */     }
/*  604 */     return false;
/*      */   }
/*      */   public boolean containsValue(byte v) {
/*  607 */     byte[] value = this.value;
/*  608 */     boolean[] used = this.used;
/*  609 */     for (int i = this.n; i-- != 0; return true) label16: if ((used[i] == 0) || (value[i] != v))
/*      */         break label16; return false;
/*      */   }
/*      */ 
/*      */   public void clear()
/*      */   {
/*  619 */     if (this.size == 0) return;
/*  620 */     this.size = 0;
/*  621 */     BooleanArrays.fill(this.used, false);
/*      */ 
/*  623 */     ObjectArrays.fill(this.key, null);
/*  624 */     this.first = (this.last = -1);
/*      */   }
/*      */   public int size() {
/*  627 */     return this.size;
/*      */   }
/*      */   public boolean isEmpty() {
/*  630 */     return this.size == 0;
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
/*  647 */     return 16;
/*      */   }
/*      */ 
/*      */   protected void fixPointers(int i)
/*      */   {
/*  697 */     if (this.size == 0) {
/*  698 */       this.first = (this.last = -1);
/*  699 */       return;
/*      */     }
/*  701 */     if (this.first == i) {
/*  702 */       this.first = ((int)this.link[i]);
/*  703 */       if (0 <= this.first)
/*      */       {
/*  705 */         this.link[this.first] |= -4294967296L;
/*      */       }
/*  707 */       return;
/*      */     }
/*  709 */     if (this.last == i) {
/*  710 */       this.last = ((int)(this.link[i] >>> 32));
/*  711 */       if (0 <= this.last)
/*      */       {
/*  713 */         this.link[this.last] |= 4294967295L;
/*      */       }
/*  715 */       return;
/*      */     }
/*  717 */     long linki = this.link[i];
/*  718 */     int prev = (int)(linki >>> 32);
/*  719 */     int next = (int)linki;
/*  720 */     this.link[prev] ^= (this.link[prev] ^ linki & 0xFFFFFFFF) & 0xFFFFFFFF;
/*  721 */     this.link[next] ^= (this.link[next] ^ linki & 0x0) & 0x0;
/*      */   }
/*      */ 
/*      */   protected void fixPointers(int s, int d)
/*      */   {
/*  732 */     if (this.size == 1) {
/*  733 */       this.first = (this.last = d);
/*      */ 
/*  735 */       this.link[d] = -1L;
/*  736 */       return;
/*      */     }
/*  738 */     if (this.first == s) {
/*  739 */       this.first = d;
/*  740 */       this.link[((int)this.link[s])] ^= (this.link[((int)this.link[s])] ^ (d & 0xFFFFFFFF) << 32) & 0x0;
/*  741 */       this.link[d] = this.link[s];
/*  742 */       return;
/*      */     }
/*  744 */     if (this.last == s) {
/*  745 */       this.last = d;
/*  746 */       this.link[((int)(this.link[s] >>> 32))] ^= (this.link[((int)(this.link[s] >>> 32))] ^ d & 0xFFFFFFFF) & 0xFFFFFFFF;
/*  747 */       this.link[d] = this.link[s];
/*  748 */       return;
/*      */     }
/*  750 */     long links = this.link[s];
/*  751 */     int prev = (int)(links >>> 32);
/*  752 */     int next = (int)links;
/*  753 */     this.link[prev] ^= (this.link[prev] ^ d & 0xFFFFFFFF) & 0xFFFFFFFF;
/*  754 */     this.link[next] ^= (this.link[next] ^ (d & 0xFFFFFFFF) << 32) & 0x0;
/*  755 */     this.link[d] = links;
/*      */   }
/*      */ 
/*      */   public K firstKey()
/*      */   {
/*  762 */     if (this.size == 0) throw new NoSuchElementException();
/*  763 */     return this.key[this.first];
/*      */   }
/*      */ 
/*      */   public K lastKey()
/*      */   {
/*  770 */     if (this.size == 0) throw new NoSuchElementException();
/*  771 */     return this.key[this.last];
/*      */   }
/*  773 */   public Comparator<? super K> comparator() { return null; } 
/*  774 */   public Object2ByteSortedMap<K> tailMap(K from) { throw new UnsupportedOperationException(); } 
/*  775 */   public Object2ByteSortedMap<K> headMap(K to) { throw new UnsupportedOperationException(); } 
/*  776 */   public Object2ByteSortedMap<K> subMap(K from, K to) { throw new UnsupportedOperationException(); }
/*      */ 
/*      */ 
/*      */   public Object2ByteSortedMap.FastSortedEntrySet<K> object2ByteEntrySet()
/*      */   {
/* 1017 */     if (this.entries == null) this.entries = new MapEntrySet(null);
/* 1018 */     return this.entries;
/*      */   }
/*      */ 
/*      */   public ObjectSortedSet<K> keySet()
/*      */   {
/* 1069 */     if (this.keys == null) this.keys = new KeySet(null);
/* 1070 */     return this.keys;
/*      */   }
/*      */ 
/*      */   public ByteCollection values()
/*      */   {
/* 1090 */     if (this.values == null) this.values = new AbstractByteCollection() {
/*      */         public ByteIterator iterator() {
/* 1092 */           return new Object2ByteLinkedOpenHashMap.ValueIterator(Object2ByteLinkedOpenHashMap.this);
/*      */         }
/*      */         public int size() {
/* 1095 */           return Object2ByteLinkedOpenHashMap.this.size;
/*      */         }
/*      */         public boolean contains(byte v) {
/* 1098 */           return Object2ByteLinkedOpenHashMap.this.containsValue(v);
/*      */         }
/*      */         public void clear() {
/* 1101 */           Object2ByteLinkedOpenHashMap.this.clear();
/*      */         }
/*      */       };
/* 1104 */     return this.values;
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public boolean rehash()
/*      */   {
/* 1118 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean trim()
/*      */   {
/* 1133 */     int l = HashCommon.arraySize(this.size, this.f);
/* 1134 */     if (l >= this.n) return true; try
/*      */     {
/* 1136 */       rehash(l);
/*      */     } catch (OutOfMemoryError cantDoIt) {
/* 1138 */       return false;
/* 1139 */     }return true;
/*      */   }
/*      */ 
/*      */   public boolean trim(int n)
/*      */   {
/* 1160 */     int l = HashCommon.nextPowerOfTwo((int)Math.ceil(n / this.f));
/* 1161 */     if (this.n <= l) return true; try
/*      */     {
/* 1163 */       rehash(l);
/*      */     } catch (OutOfMemoryError cantDoIt) {
/* 1165 */       return false;
/* 1166 */     }return true;
/*      */   }
/*      */ 
/*      */   protected void rehash(int newN)
/*      */   {
/* 1179 */     int i = this.first; int prev = -1; int newPrev = -1;
/*      */ 
/* 1181 */     Object[] key = this.key;
/* 1182 */     byte[] value = this.value;
/* 1183 */     int newMask = newN - 1;
/* 1184 */     Object[] newKey = (Object[])new Object[newN];
/* 1185 */     byte[] newValue = new byte[newN];
/* 1186 */     boolean[] newUsed = new boolean[newN];
/* 1187 */     long[] link = this.link;
/* 1188 */     long[] newLink = new long[newN];
/* 1189 */     this.first = -1;
/* 1190 */     for (int j = this.size; j-- != 0; ) {
/* 1191 */       Object k = key[i];
/* 1192 */       int pos = (k == null ? 142593372 : HashCommon.murmurHash3(k.hashCode())) & newMask;
/* 1193 */       while (newUsed[pos] != 0) pos = pos + 1 & newMask;
/* 1194 */       newUsed[pos] = true;
/* 1195 */       newKey[pos] = k;
/* 1196 */       newValue[pos] = value[i];
/* 1197 */       if (prev != -1) {
/* 1198 */         newLink[newPrev] ^= (newLink[newPrev] ^ pos & 0xFFFFFFFF) & 0xFFFFFFFF;
/* 1199 */         newLink[pos] ^= (newLink[pos] ^ (newPrev & 0xFFFFFFFF) << 32) & 0x0;
/* 1200 */         newPrev = pos;
/*      */       }
/*      */       else {
/* 1203 */         newPrev = this.first = pos;
/*      */ 
/* 1205 */         newLink[pos] = -1L;
/*      */       }
/* 1207 */       int t = i;
/* 1208 */       i = (int)link[i];
/* 1209 */       prev = t;
/*      */     }
/* 1211 */     this.n = newN;
/* 1212 */     this.mask = newMask;
/* 1213 */     this.maxFill = HashCommon.maxFill(this.n, this.f);
/* 1214 */     this.key = newKey;
/* 1215 */     this.value = newValue;
/* 1216 */     this.used = newUsed;
/* 1217 */     this.link = newLink;
/* 1218 */     this.last = newPrev;
/* 1219 */     if (newPrev != -1)
/*      */     {
/* 1221 */       newLink[newPrev] |= 4294967295L;
/*      */     }
/*      */   }
/*      */ 
/*      */   public Object2ByteLinkedOpenHashMap<K> clone()
/*      */   {
/*      */     Object2ByteLinkedOpenHashMap c;
/*      */     try
/*      */     {
/* 1234 */       c = (Object2ByteLinkedOpenHashMap)super.clone();
/*      */     }
/*      */     catch (CloneNotSupportedException cantHappen) {
/* 1237 */       throw new InternalError();
/*      */     }
/* 1239 */     c.keys = null;
/* 1240 */     c.values = null;
/* 1241 */     c.entries = null;
/* 1242 */     c.key = ((Object[])this.key.clone());
/* 1243 */     c.value = ((byte[])this.value.clone());
/* 1244 */     c.used = ((boolean[])this.used.clone());
/* 1245 */     c.link = ((long[])this.link.clone());
/* 1246 */     return c;
/*      */   }
/*      */ 
/*      */   public int hashCode()
/*      */   {
/* 1258 */     int h = 0;
/* 1259 */     int j = this.size; int i = 0; for (int t = 0; j-- != 0; ) {
/* 1260 */       while (this.used[i] == 0) i++;
/* 1261 */       if (this != this.key[i])
/* 1262 */         t = this.key[i] == null ? 0 : this.key[i].hashCode();
/* 1263 */       t ^= this.value[i];
/* 1264 */       h += t;
/* 1265 */       i++;
/*      */     }
/* 1267 */     return h;
/*      */   }
/*      */   private void writeObject(ObjectOutputStream s) throws IOException {
/* 1270 */     Object[] key = this.key;
/* 1271 */     byte[] value = this.value;
/* 1272 */     MapIterator i = new MapIterator(null);
/* 1273 */     s.defaultWriteObject();
/* 1274 */     for (int j = this.size; j-- != 0; ) {
/* 1275 */       int e = i.nextEntry();
/* 1276 */       s.writeObject(key[e]);
/* 1277 */       s.writeByte(value[e]);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
/* 1282 */     s.defaultReadObject();
/* 1283 */     this.n = HashCommon.arraySize(this.size, this.f);
/* 1284 */     this.maxFill = HashCommon.maxFill(this.n, this.f);
/* 1285 */     this.mask = (this.n - 1);
/* 1286 */     Object[] key = this.key = (Object[])new Object[this.n];
/* 1287 */     byte[] value = this.value = new byte[this.n];
/* 1288 */     boolean[] used = this.used = new boolean[this.n];
/* 1289 */     long[] link = this.link = new long[this.n];
/* 1290 */     int prev = -1;
/* 1291 */     this.first = (this.last = -1);
/*      */ 
/* 1294 */     int i = this.size; for (int pos = 0; i-- != 0; ) {
/* 1295 */       Object k = s.readObject();
/* 1296 */       byte v = s.readByte();
/* 1297 */       pos = (k == null ? 142593372 : HashCommon.murmurHash3(k.hashCode())) & this.mask;
/* 1298 */       while (used[pos] != 0) pos = pos + 1 & this.mask;
/* 1299 */       used[pos] = true;
/* 1300 */       key[pos] = k;
/* 1301 */       value[pos] = v;
/* 1302 */       if (this.first != -1) {
/* 1303 */         link[prev] ^= (link[prev] ^ pos & 0xFFFFFFFF) & 0xFFFFFFFF;
/* 1304 */         link[pos] ^= (link[pos] ^ (prev & 0xFFFFFFFF) << 32) & 0x0;
/* 1305 */         prev = pos;
/*      */       }
/*      */       else {
/* 1308 */         prev = this.first = pos;
/*      */ 
/* 1310 */         link[pos] |= -4294967296L;
/*      */       }
/*      */     }
/* 1313 */     this.last = prev;
/* 1314 */     if (prev != -1)
/*      */     {
/* 1316 */       link[prev] |= 4294967295L;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void checkTable()
/*      */   {
/*      */   }
/*      */ 
/*      */   private final class ValueIterator extends Object2ByteLinkedOpenHashMap.MapIterator
/*      */     implements ByteListIterator
/*      */   {
/*      */     public byte previousByte()
/*      */     {
/* 1079 */       return Object2ByteLinkedOpenHashMap.this.value[previousEntry()]; } 
/* 1080 */     public Byte previous() { return Byte.valueOf(Object2ByteLinkedOpenHashMap.this.value[previousEntry()]); } 
/* 1081 */     public void set(Byte ok) { throw new UnsupportedOperationException(); } 
/* 1082 */     public void add(Byte ok) { throw new UnsupportedOperationException(); } 
/* 1083 */     public void set(byte v) { throw new UnsupportedOperationException(); } 
/* 1084 */     public void add(byte v) { throw new UnsupportedOperationException(); } 
/* 1085 */     public ValueIterator() { super(null); } 
/* 1086 */     public byte nextByte() { return Object2ByteLinkedOpenHashMap.this.value[nextEntry()]; } 
/* 1087 */     public Byte next() { return Byte.valueOf(Object2ByteLinkedOpenHashMap.this.value[nextEntry()]); }
/*      */ 
/*      */   }
/*      */ 
/*      */   private final class KeySet extends AbstractObjectSortedSet<K>
/*      */   {
/*      */     private KeySet()
/*      */     {
/*      */     }
/*      */ 
/*      */     public ObjectListIterator<K> iterator(K from)
/*      */     {
/* 1036 */       return new Object2ByteLinkedOpenHashMap.KeyIterator(Object2ByteLinkedOpenHashMap.this, from);
/*      */     }
/*      */     public ObjectListIterator<K> iterator() {
/* 1039 */       return new Object2ByteLinkedOpenHashMap.KeyIterator(Object2ByteLinkedOpenHashMap.this);
/*      */     }
/*      */     public int size() {
/* 1042 */       return Object2ByteLinkedOpenHashMap.this.size;
/*      */     }
/*      */     public boolean contains(Object k) {
/* 1045 */       return Object2ByteLinkedOpenHashMap.this.containsKey(k);
/*      */     }
/*      */     public boolean remove(Object k) {
/* 1048 */       int oldSize = Object2ByteLinkedOpenHashMap.this.size;
/* 1049 */       Object2ByteLinkedOpenHashMap.this.remove(k);
/* 1050 */       return Object2ByteLinkedOpenHashMap.this.size != oldSize;
/*      */     }
/*      */     public void clear() {
/* 1053 */       Object2ByteLinkedOpenHashMap.this.clear();
/*      */     }
/*      */     public K first() {
/* 1056 */       if (Object2ByteLinkedOpenHashMap.this.size == 0) throw new NoSuchElementException();
/* 1057 */       return Object2ByteLinkedOpenHashMap.this.key[Object2ByteLinkedOpenHashMap.this.first];
/*      */     }
/*      */     public K last() {
/* 1060 */       if (Object2ByteLinkedOpenHashMap.this.size == 0) throw new NoSuchElementException();
/* 1061 */       return Object2ByteLinkedOpenHashMap.this.key[Object2ByteLinkedOpenHashMap.this.last];
/*      */     }
/* 1063 */     public Comparator<? super K> comparator() { return null; } 
/* 1064 */     public final ObjectSortedSet<K> tailSet(K from) { throw new UnsupportedOperationException(); } 
/* 1065 */     public final ObjectSortedSet<K> headSet(K to) { throw new UnsupportedOperationException(); } 
/* 1066 */     public final ObjectSortedSet<K> subSet(K from, K to) { throw new UnsupportedOperationException(); }
/*      */ 
/*      */   }
/*      */ 
/*      */   private final class KeyIterator extends Object2ByteLinkedOpenHashMap<K>.MapIterator
/*      */     implements ObjectListIterator<K>
/*      */   {
/*      */     public KeyIterator()
/*      */     {
/* 1027 */       super(k, null); } 
/* 1028 */     public K previous() { return Object2ByteLinkedOpenHashMap.this.key[previousEntry()]; } 
/* 1029 */     public void set(K k) { throw new UnsupportedOperationException(); } 
/* 1030 */     public void add(K k) { throw new UnsupportedOperationException(); } 
/* 1031 */     public KeyIterator() { super(null); } 
/* 1032 */     public K next() { return Object2ByteLinkedOpenHashMap.this.key[nextEntry()]; }
/*      */ 
/*      */   }
/*      */ 
/*      */   private final class MapEntrySet extends AbstractObjectSortedSet<Object2ByteMap.Entry<K>>
/*      */     implements Object2ByteSortedMap.FastSortedEntrySet<K>
/*      */   {
/*      */     private MapEntrySet()
/*      */     {
/*      */     }
/*      */ 
/*      */     public ObjectBidirectionalIterator<Object2ByteMap.Entry<K>> iterator()
/*      */     {
/*  955 */       return new Object2ByteLinkedOpenHashMap.EntryIterator(Object2ByteLinkedOpenHashMap.this);
/*      */     }
/*  957 */     public Comparator<? super Object2ByteMap.Entry<K>> comparator() { return null; } 
/*  958 */     public ObjectSortedSet<Object2ByteMap.Entry<K>> subSet(Object2ByteMap.Entry<K> fromElement, Object2ByteMap.Entry<K> toElement) { throw new UnsupportedOperationException(); } 
/*  959 */     public ObjectSortedSet<Object2ByteMap.Entry<K>> headSet(Object2ByteMap.Entry<K> toElement) { throw new UnsupportedOperationException(); } 
/*  960 */     public ObjectSortedSet<Object2ByteMap.Entry<K>> tailSet(Object2ByteMap.Entry<K> fromElement) { throw new UnsupportedOperationException(); } 
/*      */     public Object2ByteMap.Entry<K> first() {
/*  962 */       if (Object2ByteLinkedOpenHashMap.this.size == 0) throw new NoSuchElementException();
/*  963 */       return new Object2ByteLinkedOpenHashMap.MapEntry(Object2ByteLinkedOpenHashMap.this, Object2ByteLinkedOpenHashMap.this.first);
/*      */     }
/*      */     public Object2ByteMap.Entry<K> last() {
/*  966 */       if (Object2ByteLinkedOpenHashMap.this.size == 0) throw new NoSuchElementException();
/*  967 */       return new Object2ByteLinkedOpenHashMap.MapEntry(Object2ByteLinkedOpenHashMap.this, Object2ByteLinkedOpenHashMap.this.last);
/*      */     }
/*      */ 
/*      */     public boolean contains(Object o) {
/*  971 */       if (!(o instanceof Map.Entry)) return false;
/*  972 */       Map.Entry e = (Map.Entry)o;
/*  973 */       Object k = e.getKey();
/*      */ 
/*  975 */       int pos = (k == null ? 142593372 : HashCommon.murmurHash3(k.hashCode())) & Object2ByteLinkedOpenHashMap.this.mask;
/*      */ 
/*  977 */       while (Object2ByteLinkedOpenHashMap.this.used[pos] != 0) {
/*  978 */         if (Object2ByteLinkedOpenHashMap.this.key[pos] == null ? k == null : Object2ByteLinkedOpenHashMap.this.key[pos].equals(k)) return Object2ByteLinkedOpenHashMap.this.value[pos] == ((Byte)e.getValue()).byteValue();
/*  979 */         pos = pos + 1 & Object2ByteLinkedOpenHashMap.this.mask;
/*      */       }
/*  981 */       return false;
/*      */     }
/*      */ 
/*      */     public boolean remove(Object o) {
/*  985 */       if (!(o instanceof Map.Entry)) return false;
/*  986 */       Map.Entry e = (Map.Entry)o;
/*  987 */       Object k = e.getKey();
/*      */ 
/*  989 */       int pos = (k == null ? 142593372 : HashCommon.murmurHash3(k.hashCode())) & Object2ByteLinkedOpenHashMap.this.mask;
/*      */ 
/*  991 */       while (Object2ByteLinkedOpenHashMap.this.used[pos] != 0) {
/*  992 */         if (Object2ByteLinkedOpenHashMap.this.key[pos] == null ? k == null : Object2ByteLinkedOpenHashMap.this.key[pos].equals(k)) {
/*  993 */           Object2ByteLinkedOpenHashMap.this.remove(e.getKey());
/*  994 */           return true;
/*      */         }
/*  996 */         pos = pos + 1 & Object2ByteLinkedOpenHashMap.this.mask;
/*      */       }
/*  998 */       return false;
/*      */     }
/*      */     public int size() {
/* 1001 */       return Object2ByteLinkedOpenHashMap.this.size;
/*      */     }
/*      */     public void clear() {
/* 1004 */       Object2ByteLinkedOpenHashMap.this.clear();
/*      */     }
/*      */     public ObjectBidirectionalIterator<Object2ByteMap.Entry<K>> iterator(Object2ByteMap.Entry<K> from) {
/* 1007 */       return new Object2ByteLinkedOpenHashMap.EntryIterator(Object2ByteLinkedOpenHashMap.this, from.getKey());
/*      */     }
/*      */     public ObjectBidirectionalIterator<Object2ByteMap.Entry<K>> fastIterator() {
/* 1010 */       return new Object2ByteLinkedOpenHashMap.FastEntryIterator(Object2ByteLinkedOpenHashMap.this);
/*      */     }
/*      */     public ObjectBidirectionalIterator<Object2ByteMap.Entry<K>> fastIterator(Object2ByteMap.Entry<K> from) {
/* 1013 */       return new Object2ByteLinkedOpenHashMap.FastEntryIterator(Object2ByteLinkedOpenHashMap.this, from.getKey());
/*      */     }
/*      */   }
/*      */ 
/*      */   private class FastEntryIterator extends Object2ByteLinkedOpenHashMap<K>.MapIterator
/*      */     implements ObjectListIterator<Object2ByteMap.Entry<K>>
/*      */   {
/*  933 */     final AbstractObject2ByteMap.BasicEntry<K> entry = new AbstractObject2ByteMap.BasicEntry(null, (byte)0);
/*      */ 
/*  934 */     public FastEntryIterator() { super(null); } 
/*      */     public FastEntryIterator() {
/*  936 */       super(from, null);
/*      */     }
/*      */     public AbstractObject2ByteMap.BasicEntry<K> next() {
/*  939 */       int e = nextEntry();
/*  940 */       this.entry.key = Object2ByteLinkedOpenHashMap.this.key[e];
/*  941 */       this.entry.value = Object2ByteLinkedOpenHashMap.this.value[e];
/*  942 */       return this.entry;
/*      */     }
/*      */     public AbstractObject2ByteMap.BasicEntry<K> previous() {
/*  945 */       int e = previousEntry();
/*  946 */       this.entry.key = Object2ByteLinkedOpenHashMap.this.key[e];
/*  947 */       this.entry.value = Object2ByteLinkedOpenHashMap.this.value[e];
/*  948 */       return this.entry;
/*      */     }
/*  950 */     public void set(Object2ByteMap.Entry<K> ok) { throw new UnsupportedOperationException(); } 
/*  951 */     public void add(Object2ByteMap.Entry<K> ok) { throw new UnsupportedOperationException(); }
/*      */ 
/*      */   }
/*      */ 
/*      */   private class EntryIterator extends Object2ByteLinkedOpenHashMap<K>.MapIterator
/*      */     implements ObjectListIterator<Object2ByteMap.Entry<K>>
/*      */   {
/*      */     private Object2ByteLinkedOpenHashMap<K>.MapEntry entry;
/*      */ 
/*      */     public EntryIterator()
/*      */     {
/*  914 */       super(null);
/*      */     }
/*  916 */     public EntryIterator() { super(from, null); }
/*      */ 
/*      */     public Object2ByteLinkedOpenHashMap<K>.MapEntry next() {
/*  919 */       return this.entry = new Object2ByteLinkedOpenHashMap.MapEntry(Object2ByteLinkedOpenHashMap.this, nextEntry());
/*      */     }
/*      */     public Object2ByteLinkedOpenHashMap<K>.MapEntry previous() {
/*  922 */       return this.entry = new Object2ByteLinkedOpenHashMap.MapEntry(Object2ByteLinkedOpenHashMap.this, previousEntry());
/*      */     }
/*      */ 
/*      */     public void remove() {
/*  926 */       super.remove();
/*  927 */       Object2ByteLinkedOpenHashMap.MapEntry.access$202(this.entry, -1);
/*      */     }
/*  929 */     public void set(Object2ByteMap.Entry<K> ok) { throw new UnsupportedOperationException(); } 
/*  930 */     public void add(Object2ByteMap.Entry<K> ok) { throw new UnsupportedOperationException(); }
/*      */ 
/*      */   }
/*      */ 
/*      */   private class MapIterator
/*      */   {
/*  785 */     int prev = -1;
/*      */ 
/*  787 */     int next = -1;
/*      */ 
/*  789 */     int curr = -1;
/*      */ 
/*  791 */     int index = -1;
/*      */ 
/*  793 */     private MapIterator() { this.next = Object2ByteLinkedOpenHashMap.this.first;
/*  794 */       this.index = 0; }
/*      */ 
/*      */     private MapIterator() {
/*  797 */       if (Object2ByteLinkedOpenHashMap.this.key[Object2ByteLinkedOpenHashMap.this.last] == null ? from == null : Object2ByteLinkedOpenHashMap.this.key[Object2ByteLinkedOpenHashMap.this.last].equals(from)) {
/*  798 */         this.prev = Object2ByteLinkedOpenHashMap.this.last;
/*  799 */         this.index = Object2ByteLinkedOpenHashMap.this.size;
/*      */       }
/*      */       else
/*      */       {
/*  803 */         int pos = (from == null ? 142593372 : HashCommon.murmurHash3(from.hashCode())) & Object2ByteLinkedOpenHashMap.this.mask;
/*      */ 
/*  805 */         while (Object2ByteLinkedOpenHashMap.this.used[pos] != 0) {
/*  806 */           if (Object2ByteLinkedOpenHashMap.this.key[pos] == null ? from == null : Object2ByteLinkedOpenHashMap.this.key[pos].equals(from))
/*      */           {
/*  808 */             this.next = ((int)Object2ByteLinkedOpenHashMap.this.link[pos]);
/*  809 */             this.prev = pos;
/*  810 */             return;
/*      */           }
/*  812 */           pos = pos + 1 & Object2ByteLinkedOpenHashMap.this.mask;
/*      */         }
/*  814 */         throw new NoSuchElementException("The key " + from + " does not belong to this map.");
/*      */       }
/*      */     }
/*  817 */     public boolean hasNext() { return this.next != -1; } 
/*  818 */     public boolean hasPrevious() { return this.prev != -1; } 
/*      */     private final void ensureIndexKnown() {
/*  820 */       if (this.index >= 0) return;
/*  821 */       if (this.prev == -1) {
/*  822 */         this.index = 0;
/*  823 */         return;
/*      */       }
/*  825 */       if (this.next == -1) {
/*  826 */         this.index = Object2ByteLinkedOpenHashMap.this.size;
/*  827 */         return;
/*      */       }
/*  829 */       int pos = Object2ByteLinkedOpenHashMap.this.first;
/*  830 */       this.index = 1;
/*  831 */       while (pos != this.prev) {
/*  832 */         pos = (int)Object2ByteLinkedOpenHashMap.this.link[pos];
/*  833 */         this.index += 1;
/*      */       }
/*      */     }
/*      */ 
/*  837 */     public int nextIndex() { ensureIndexKnown();
/*  838 */       return this.index; }
/*      */ 
/*      */     public int previousIndex() {
/*  841 */       ensureIndexKnown();
/*  842 */       return this.index - 1;
/*      */     }
/*      */     public int nextEntry() {
/*  845 */       if (!hasNext()) return Object2ByteLinkedOpenHashMap.this.size();
/*  846 */       this.curr = this.next;
/*  847 */       this.next = ((int)Object2ByteLinkedOpenHashMap.this.link[this.curr]);
/*  848 */       this.prev = this.curr;
/*  849 */       if (this.index >= 0) this.index += 1;
/*  850 */       return this.curr;
/*      */     }
/*      */     public int previousEntry() {
/*  853 */       if (!hasPrevious()) return -1;
/*  854 */       this.curr = this.prev;
/*  855 */       this.prev = ((int)(Object2ByteLinkedOpenHashMap.this.link[this.curr] >>> 32));
/*  856 */       this.next = this.curr;
/*  857 */       if (this.index >= 0) this.index -= 1;
/*  858 */       return this.curr;
/*      */     }
/*      */ 
/*      */     public void remove() {
/*  862 */       ensureIndexKnown();
/*  863 */       if (this.curr == -1) throw new IllegalStateException();
/*  864 */       if (this.curr == this.prev)
/*      */       {
/*  867 */         this.index -= 1;
/*  868 */         this.prev = ((int)(Object2ByteLinkedOpenHashMap.this.link[this.curr] >>> 32));
/*      */       }
/*      */       else {
/*  871 */         this.next = ((int)Object2ByteLinkedOpenHashMap.this.link[this.curr]);
/*  872 */       }Object2ByteLinkedOpenHashMap.this.size -= 1;
/*      */ 
/*  875 */       if (this.prev == -1) Object2ByteLinkedOpenHashMap.this.first = this.next;
/*      */       else
/*  877 */         Object2ByteLinkedOpenHashMap.this.link[this.prev] ^= (Object2ByteLinkedOpenHashMap.this.link[this.prev] ^ this.next & 0xFFFFFFFF) & 0xFFFFFFFF;
/*  878 */       if (this.next == -1) Object2ByteLinkedOpenHashMap.this.last = this.prev;
/*      */       else
/*  880 */         Object2ByteLinkedOpenHashMap.this.link[this.next] ^= (Object2ByteLinkedOpenHashMap.this.link[this.next] ^ (this.prev & 0xFFFFFFFF) << 32) & 0x0; int pos = this.curr;
/*      */       int last;
/*      */       while (true) {
/*  884 */         pos = (last = pos) + 1 & Object2ByteLinkedOpenHashMap.this.mask;
/*  885 */         while (Object2ByteLinkedOpenHashMap.this.used[pos] != 0) {
/*  886 */           int slot = (Object2ByteLinkedOpenHashMap.this.key[pos] == null ? 142593372 : HashCommon.murmurHash3(Object2ByteLinkedOpenHashMap.this.key[pos].hashCode())) & Object2ByteLinkedOpenHashMap.this.mask;
/*  887 */           if (last <= pos ? (last < slot) && (slot <= pos) : (last >= slot) && (slot > pos)) break;
/*  888 */           pos = pos + 1 & Object2ByteLinkedOpenHashMap.this.mask;
/*      */         }
/*  890 */         if (Object2ByteLinkedOpenHashMap.this.used[pos] == 0) break;
/*  891 */         Object2ByteLinkedOpenHashMap.this.key[last] = Object2ByteLinkedOpenHashMap.this.key[pos];
/*  892 */         Object2ByteLinkedOpenHashMap.this.value[last] = Object2ByteLinkedOpenHashMap.this.value[pos];
/*  893 */         if (this.next == pos) this.next = last;
/*  894 */         if (this.prev == pos) this.prev = last;
/*  895 */         Object2ByteLinkedOpenHashMap.this.fixPointers(pos, last);
/*      */       }
/*  897 */       Object2ByteLinkedOpenHashMap.this.used[last] = false;
/*  898 */       Object2ByteLinkedOpenHashMap.this.key[last] = null;
/*  899 */       this.curr = -1;
/*      */     }
/*      */     public int skip(int n) {
/*  902 */       int i = n;
/*  903 */       while ((i-- != 0) && (hasNext())) nextEntry();
/*  904 */       return n - i - 1;
/*      */     }
/*      */     public int back(int n) {
/*  907 */       int i = n;
/*  908 */       while ((i-- != 0) && (hasPrevious())) previousEntry();
/*  909 */       return n - i - 1;
/*      */     }
/*      */   }
/*      */ 
/*      */   private final class MapEntry
/*      */     implements Object2ByteMap.Entry<K>, Map.Entry<K, Byte>
/*      */   {
/*      */     private int index;
/*      */ 
/*      */     MapEntry(int index)
/*      */     {
/*  657 */       this.index = index;
/*      */     }
/*      */     public K getKey() {
/*  660 */       return Object2ByteLinkedOpenHashMap.this.key[this.index];
/*      */     }
/*      */     public Byte getValue() {
/*  663 */       return Byte.valueOf(Object2ByteLinkedOpenHashMap.this.value[this.index]);
/*      */     }
/*      */     public byte getByteValue() {
/*  666 */       return Object2ByteLinkedOpenHashMap.this.value[this.index];
/*      */     }
/*      */     public byte setValue(byte v) {
/*  669 */       byte oldValue = Object2ByteLinkedOpenHashMap.this.value[this.index];
/*  670 */       Object2ByteLinkedOpenHashMap.this.value[this.index] = v;
/*  671 */       return oldValue;
/*      */     }
/*      */     public Byte setValue(Byte v) {
/*  674 */       return Byte.valueOf(setValue(v.byteValue()));
/*      */     }
/*      */ 
/*      */     public boolean equals(Object o) {
/*  678 */       if (!(o instanceof Map.Entry)) return false;
/*  679 */       Map.Entry e = (Map.Entry)o;
/*  680 */       return (Object2ByteLinkedOpenHashMap.this.key[this.index] == null ? e.getKey() == null : Object2ByteLinkedOpenHashMap.this.key[this.index].equals(e.getKey())) && (Object2ByteLinkedOpenHashMap.this.value[this.index] == ((Byte)e.getValue()).byteValue());
/*      */     }
/*      */     public int hashCode() {
/*  683 */       return (Object2ByteLinkedOpenHashMap.this.key[this.index] == null ? 0 : Object2ByteLinkedOpenHashMap.this.key[this.index].hashCode()) ^ Object2ByteLinkedOpenHashMap.this.value[this.index];
/*      */     }
/*      */     public String toString() {
/*  686 */       return Object2ByteLinkedOpenHashMap.this.key[this.index] + "=>" + Object2ByteLinkedOpenHashMap.this.value[this.index];
/*      */     }
/*      */   }
/*      */ }

/* Location:           C:\Users\Raul\Desktop\StarMade\StarMade.jar
 * Qualified Name:     it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap
 * JD-Core Version:    0.6.2
 */