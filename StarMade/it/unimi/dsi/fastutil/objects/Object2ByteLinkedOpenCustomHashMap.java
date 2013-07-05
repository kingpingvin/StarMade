/*      */ package it.unimi.dsi.fastutil.objects;
/*      */ 
/*      */ import it.unimi.dsi.fastutil.Hash;
/*      */ import it.unimi.dsi.fastutil.Hash.Strategy;
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
/*      */ public class Object2ByteLinkedOpenCustomHashMap<K> extends AbstractObject2ByteSortedMap<K>
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
/*      */   protected Hash.Strategy<K> strategy;
/*      */ 
/*      */   public Object2ByteLinkedOpenCustomHashMap(int expected, float f, Hash.Strategy<K> strategy)
/*      */   {
/*  154 */     this.strategy = strategy;
/*  155 */     if ((f <= 0.0F) || (f > 1.0F)) throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
/*  156 */     if (expected < 0) throw new IllegalArgumentException("The expected number of elements must be nonnegative");
/*  157 */     this.f = f;
/*  158 */     this.n = HashCommon.arraySize(expected, f);
/*  159 */     this.mask = (this.n - 1);
/*  160 */     this.maxFill = HashCommon.maxFill(this.n, f);
/*  161 */     this.key = ((Object[])new Object[this.n]);
/*  162 */     this.value = new byte[this.n];
/*  163 */     this.used = new boolean[this.n];
/*  164 */     this.link = new long[this.n];
/*      */   }
/*      */ 
/*      */   public Object2ByteLinkedOpenCustomHashMap(int expected, Hash.Strategy<K> strategy)
/*      */   {
/*  172 */     this(expected, 0.75F, strategy);
/*      */   }
/*      */ 
/*      */   public Object2ByteLinkedOpenCustomHashMap(Hash.Strategy<K> strategy)
/*      */   {
/*  179 */     this(16, 0.75F, strategy);
/*      */   }
/*      */ 
/*      */   public Object2ByteLinkedOpenCustomHashMap(Map<? extends K, ? extends Byte> m, float f, Hash.Strategy<K> strategy)
/*      */   {
/*  188 */     this(m.size(), f, strategy);
/*  189 */     putAll(m);
/*      */   }
/*      */ 
/*      */   public Object2ByteLinkedOpenCustomHashMap(Map<? extends K, ? extends Byte> m, Hash.Strategy<K> strategy)
/*      */   {
/*  197 */     this(m, 0.75F, strategy);
/*      */   }
/*      */ 
/*      */   public Object2ByteLinkedOpenCustomHashMap(Object2ByteMap<K> m, float f, Hash.Strategy<K> strategy)
/*      */   {
/*  206 */     this(m.size(), f, strategy);
/*  207 */     putAll(m);
/*      */   }
/*      */ 
/*      */   public Object2ByteLinkedOpenCustomHashMap(Object2ByteMap<K> m, Hash.Strategy<K> strategy)
/*      */   {
/*  215 */     this(m, 0.75F, strategy);
/*      */   }
/*      */ 
/*      */   public Object2ByteLinkedOpenCustomHashMap(K[] k, byte[] v, float f, Hash.Strategy<K> strategy)
/*      */   {
/*  226 */     this(k.length, f, strategy);
/*  227 */     if (k.length != v.length) throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
/*  228 */     for (int i = 0; i < k.length; i++) put(k[i], v[i]);
/*      */   }
/*      */ 
/*      */   public Object2ByteLinkedOpenCustomHashMap(K[] k, byte[] v, Hash.Strategy<K> strategy)
/*      */   {
/*  238 */     this(k, v, 0.75F, strategy);
/*      */   }
/*      */ 
/*      */   public Hash.Strategy<K> strategy()
/*      */   {
/*  245 */     return this.strategy;
/*      */   }
/*      */ 
/*      */   public byte put(K k, byte v)
/*      */   {
/*  253 */     int pos = HashCommon.murmurHash3(this.strategy.hashCode(k)) & this.mask;
/*      */ 
/*  255 */     while (this.used[pos] != 0) {
/*  256 */       if (this.strategy.equals(this.key[pos], k)) {
/*  257 */         byte oldValue = this.value[pos];
/*  258 */         this.value[pos] = v;
/*  259 */         return oldValue;
/*      */       }
/*  261 */       pos = pos + 1 & this.mask;
/*      */     }
/*  263 */     this.used[pos] = true;
/*  264 */     this.key[pos] = k;
/*  265 */     this.value[pos] = v;
/*  266 */     if (this.size == 0) {
/*  267 */       this.first = (this.last = pos);
/*      */ 
/*  269 */       this.link[pos] = -1L;
/*      */     }
/*      */     else {
/*  272 */       this.link[this.last] ^= (this.link[this.last] ^ pos & 0xFFFFFFFF) & 0xFFFFFFFF;
/*  273 */       this.link[pos] = ((this.last & 0xFFFFFFFF) << 32 | 0xFFFFFFFF);
/*  274 */       this.last = pos;
/*      */     }
/*  276 */     if (++this.size >= this.maxFill) rehash(HashCommon.arraySize(this.size + 1, this.f));
/*      */ 
/*  278 */     return this.defRetValue;
/*      */   }
/*      */   public Byte put(K ok, Byte ov) {
/*  281 */     byte v = ov.byteValue();
/*  282 */     Object k = ok;
/*      */ 
/*  284 */     int pos = HashCommon.murmurHash3(this.strategy.hashCode(k)) & this.mask;
/*      */ 
/*  286 */     while (this.used[pos] != 0) {
/*  287 */       if (this.strategy.equals(this.key[pos], k)) {
/*  288 */         Byte oldValue = Byte.valueOf(this.value[pos]);
/*  289 */         this.value[pos] = v;
/*  290 */         return oldValue;
/*      */       }
/*  292 */       pos = pos + 1 & this.mask;
/*      */     }
/*  294 */     this.used[pos] = true;
/*  295 */     this.key[pos] = k;
/*  296 */     this.value[pos] = v;
/*  297 */     if (this.size == 0) {
/*  298 */       this.first = (this.last = pos);
/*      */ 
/*  300 */       this.link[pos] = -1L;
/*      */     }
/*      */     else {
/*  303 */       this.link[this.last] ^= (this.link[this.last] ^ pos & 0xFFFFFFFF) & 0xFFFFFFFF;
/*  304 */       this.link[pos] = ((this.last & 0xFFFFFFFF) << 32 | 0xFFFFFFFF);
/*  305 */       this.last = pos;
/*      */     }
/*  307 */     if (++this.size >= this.maxFill) rehash(HashCommon.arraySize(this.size + 1, this.f));
/*      */ 
/*  309 */     return null;
/*      */   }
/*      */ 
/*      */   public byte add(K k, byte incr)
/*      */   {
/*  324 */     int pos = HashCommon.murmurHash3(this.strategy.hashCode(k)) & this.mask;
/*      */ 
/*  326 */     while (this.used[pos] != 0) {
/*  327 */       if (this.strategy.equals(this.key[pos], k)) {
/*  328 */         byte oldValue = this.value[pos];
/*      */         int tmp60_59 = pos;
/*      */         byte[] tmp60_56 = this.value; tmp60_56[tmp60_59] = ((byte)(tmp60_56[tmp60_59] + incr));
/*  330 */         return oldValue;
/*      */       }
/*  332 */       pos = pos + 1 & this.mask;
/*      */     }
/*  334 */     this.used[pos] = true;
/*  335 */     this.key[pos] = k;
/*  336 */     this.value[pos] = ((byte)(this.defRetValue + incr));
/*  337 */     if (this.size == 0) {
/*  338 */       this.first = (this.last = pos);
/*      */ 
/*  340 */       this.link[pos] = -1L;
/*      */     }
/*      */     else {
/*  343 */       this.link[this.last] ^= (this.link[this.last] ^ pos & 0xFFFFFFFF) & 0xFFFFFFFF;
/*  344 */       this.link[pos] = ((this.last & 0xFFFFFFFF) << 32 | 0xFFFFFFFF);
/*  345 */       this.last = pos;
/*      */     }
/*  347 */     if (++this.size >= this.maxFill) rehash(HashCommon.arraySize(this.size + 1, this.f));
/*      */ 
/*  349 */     return this.defRetValue;
/*      */   }
/*      */ 
/*      */   protected final int shiftKeys(int pos)
/*      */   {
/*      */     int last;
/*      */     while (true)
/*      */     {
/*  361 */       pos = (last = pos) + 1 & this.mask;
/*  362 */       while (this.used[pos] != 0) {
/*  363 */         int slot = HashCommon.murmurHash3(this.strategy.hashCode(this.key[pos])) & this.mask;
/*  364 */         if (last <= pos ? (last < slot) && (slot <= pos) : (last >= slot) && (slot > pos)) break;
/*  365 */         pos = pos + 1 & this.mask;
/*      */       }
/*  367 */       if (this.used[pos] == 0) break;
/*  368 */       this.key[last] = this.key[pos];
/*  369 */       this.value[last] = this.value[pos];
/*  370 */       fixPointers(pos, last);
/*      */     }
/*  372 */     this.used[last] = false;
/*  373 */     this.key[last] = null;
/*  374 */     return last;
/*      */   }
/*      */ 
/*      */   public byte removeByte(Object k)
/*      */   {
/*  379 */     int pos = HashCommon.murmurHash3(this.strategy.hashCode(k)) & this.mask;
/*      */ 
/*  381 */     while (this.used[pos] != 0) {
/*  382 */       if (this.strategy.equals(this.key[pos], k)) {
/*  383 */         this.size -= 1;
/*  384 */         fixPointers(pos);
/*  385 */         byte v = this.value[pos];
/*  386 */         shiftKeys(pos);
/*  387 */         return v;
/*      */       }
/*  389 */       pos = pos + 1 & this.mask;
/*      */     }
/*  391 */     return this.defRetValue;
/*      */   }
/*      */ 
/*      */   public Byte remove(Object ok) {
/*  395 */     Object k = ok;
/*      */ 
/*  397 */     int pos = HashCommon.murmurHash3(this.strategy.hashCode(k)) & this.mask;
/*      */ 
/*  399 */     while (this.used[pos] != 0) {
/*  400 */       if (this.strategy.equals(this.key[pos], k)) {
/*  401 */         this.size -= 1;
/*  402 */         fixPointers(pos);
/*  403 */         byte v = this.value[pos];
/*  404 */         shiftKeys(pos);
/*  405 */         return Byte.valueOf(v);
/*      */       }
/*  407 */       pos = pos + 1 & this.mask;
/*      */     }
/*  409 */     return null;
/*      */   }
/*      */ 
/*      */   public byte removeFirstByte()
/*      */   {
/*  416 */     if (this.size == 0) throw new NoSuchElementException();
/*  417 */     this.size -= 1;
/*  418 */     int pos = this.first;
/*      */ 
/*  420 */     this.first = ((int)this.link[pos]);
/*  421 */     if (0 <= this.first)
/*      */     {
/*  423 */       this.link[this.first] |= -4294967296L;
/*      */     }
/*  425 */     byte v = this.value[pos];
/*  426 */     shiftKeys(pos);
/*  427 */     return v;
/*      */   }
/*      */ 
/*      */   public byte removeLastByte()
/*      */   {
/*  434 */     if (this.size == 0) throw new NoSuchElementException();
/*  435 */     this.size -= 1;
/*  436 */     int pos = this.last;
/*      */ 
/*  438 */     this.last = ((int)(this.link[pos] >>> 32));
/*  439 */     if (0 <= this.last)
/*      */     {
/*  441 */       this.link[this.last] |= 4294967295L;
/*      */     }
/*  443 */     byte v = this.value[pos];
/*  444 */     shiftKeys(pos);
/*  445 */     return v;
/*      */   }
/*      */   private void moveIndexToFirst(int i) {
/*  448 */     if ((this.size == 1) || (this.first == i)) return;
/*  449 */     if (this.last == i) {
/*  450 */       this.last = ((int)(this.link[i] >>> 32));
/*      */ 
/*  452 */       this.link[this.last] |= 4294967295L;
/*      */     }
/*      */     else {
/*  455 */       long linki = this.link[i];
/*  456 */       int prev = (int)(linki >>> 32);
/*  457 */       int next = (int)linki;
/*  458 */       this.link[prev] ^= (this.link[prev] ^ linki & 0xFFFFFFFF) & 0xFFFFFFFF;
/*  459 */       this.link[next] ^= (this.link[next] ^ linki & 0x0) & 0x0;
/*      */     }
/*  461 */     this.link[this.first] ^= (this.link[this.first] ^ (i & 0xFFFFFFFF) << 32) & 0x0;
/*  462 */     this.link[i] = (0x0 | this.first & 0xFFFFFFFF);
/*  463 */     this.first = i;
/*      */   }
/*      */   private void moveIndexToLast(int i) {
/*  466 */     if ((this.size == 1) || (this.last == i)) return;
/*  467 */     if (this.first == i) {
/*  468 */       this.first = ((int)this.link[i]);
/*      */ 
/*  470 */       this.link[this.first] |= -4294967296L;
/*      */     }
/*      */     else {
/*  473 */       long linki = this.link[i];
/*  474 */       int prev = (int)(linki >>> 32);
/*  475 */       int next = (int)linki;
/*  476 */       this.link[prev] ^= (this.link[prev] ^ linki & 0xFFFFFFFF) & 0xFFFFFFFF;
/*  477 */       this.link[next] ^= (this.link[next] ^ linki & 0x0) & 0x0;
/*      */     }
/*  479 */     this.link[this.last] ^= (this.link[this.last] ^ i & 0xFFFFFFFF) & 0xFFFFFFFF;
/*  480 */     this.link[i] = ((this.last & 0xFFFFFFFF) << 32 | 0xFFFFFFFF);
/*  481 */     this.last = i;
/*      */   }
/*      */ 
/*      */   public byte getAndMoveToFirst(K k)
/*      */   {
/*  489 */     Object[] key = this.key;
/*  490 */     boolean[] used = this.used;
/*  491 */     int mask = this.mask;
/*      */ 
/*  493 */     int pos = HashCommon.murmurHash3(this.strategy.hashCode(k)) & mask;
/*      */ 
/*  495 */     while (used[pos] != 0) {
/*  496 */       if (this.strategy.equals(k, key[pos])) {
/*  497 */         moveIndexToFirst(pos);
/*  498 */         return this.value[pos];
/*      */       }
/*  500 */       pos = pos + 1 & mask;
/*      */     }
/*  502 */     return this.defRetValue;
/*      */   }
/*      */ 
/*      */   public byte getAndMoveToLast(K k)
/*      */   {
/*  510 */     Object[] key = this.key;
/*  511 */     boolean[] used = this.used;
/*  512 */     int mask = this.mask;
/*      */ 
/*  514 */     int pos = HashCommon.murmurHash3(this.strategy.hashCode(k)) & mask;
/*      */ 
/*  516 */     while (used[pos] != 0) {
/*  517 */       if (this.strategy.equals(k, key[pos])) {
/*  518 */         moveIndexToLast(pos);
/*  519 */         return this.value[pos];
/*      */       }
/*  521 */       pos = pos + 1 & mask;
/*      */     }
/*  523 */     return this.defRetValue;
/*      */   }
/*      */ 
/*      */   public byte putAndMoveToFirst(K k, byte v)
/*      */   {
/*  532 */     Object[] key = this.key;
/*  533 */     boolean[] used = this.used;
/*  534 */     int mask = this.mask;
/*      */ 
/*  536 */     int pos = HashCommon.murmurHash3(this.strategy.hashCode(k)) & mask;
/*      */ 
/*  538 */     while (used[pos] != 0) {
/*  539 */       if (this.strategy.equals(k, key[pos])) {
/*  540 */         byte oldValue = this.value[pos];
/*  541 */         this.value[pos] = v;
/*  542 */         moveIndexToFirst(pos);
/*  543 */         return oldValue;
/*      */       }
/*  545 */       pos = pos + 1 & mask;
/*      */     }
/*  547 */     used[pos] = true;
/*  548 */     key[pos] = k;
/*  549 */     this.value[pos] = v;
/*  550 */     if (this.size == 0) {
/*  551 */       this.first = (this.last = pos);
/*      */ 
/*  553 */       this.link[pos] = -1L;
/*      */     }
/*      */     else {
/*  556 */       this.link[this.first] ^= (this.link[this.first] ^ (pos & 0xFFFFFFFF) << 32) & 0x0;
/*  557 */       this.link[pos] = (0x0 | this.first & 0xFFFFFFFF);
/*  558 */       this.first = pos;
/*      */     }
/*  560 */     if (++this.size >= this.maxFill) rehash(HashCommon.arraySize(this.size, this.f));
/*      */ 
/*  562 */     return this.defRetValue;
/*      */   }
/*      */ 
/*      */   public byte putAndMoveToLast(K k, byte v)
/*      */   {
/*  571 */     Object[] key = this.key;
/*  572 */     boolean[] used = this.used;
/*  573 */     int mask = this.mask;
/*      */ 
/*  575 */     int pos = HashCommon.murmurHash3(this.strategy.hashCode(k)) & mask;
/*      */ 
/*  577 */     while (used[pos] != 0) {
/*  578 */       if (this.strategy.equals(k, key[pos])) {
/*  579 */         byte oldValue = this.value[pos];
/*  580 */         this.value[pos] = v;
/*  581 */         moveIndexToLast(pos);
/*  582 */         return oldValue;
/*      */       }
/*  584 */       pos = pos + 1 & mask;
/*      */     }
/*  586 */     used[pos] = true;
/*  587 */     key[pos] = k;
/*  588 */     this.value[pos] = v;
/*  589 */     if (this.size == 0) {
/*  590 */       this.first = (this.last = pos);
/*      */ 
/*  592 */       this.link[pos] = -1L;
/*      */     }
/*      */     else {
/*  595 */       this.link[this.last] ^= (this.link[this.last] ^ pos & 0xFFFFFFFF) & 0xFFFFFFFF;
/*  596 */       this.link[pos] = ((this.last & 0xFFFFFFFF) << 32 | 0xFFFFFFFF);
/*  597 */       this.last = pos;
/*      */     }
/*  599 */     if (++this.size >= this.maxFill) rehash(HashCommon.arraySize(this.size, this.f));
/*      */ 
/*  601 */     return this.defRetValue;
/*      */   }
/*      */ 
/*      */   public byte getByte(Object k)
/*      */   {
/*  606 */     int pos = HashCommon.murmurHash3(this.strategy.hashCode(k)) & this.mask;
/*      */ 
/*  608 */     while (this.used[pos] != 0) {
/*  609 */       if (this.strategy.equals(this.key[pos], k)) return this.value[pos];
/*  610 */       pos = pos + 1 & this.mask;
/*      */     }
/*  612 */     return this.defRetValue;
/*      */   }
/*      */ 
/*      */   public boolean containsKey(Object k)
/*      */   {
/*  617 */     int pos = HashCommon.murmurHash3(this.strategy.hashCode(k)) & this.mask;
/*      */ 
/*  619 */     while (this.used[pos] != 0) {
/*  620 */       if (this.strategy.equals(this.key[pos], k)) return true;
/*  621 */       pos = pos + 1 & this.mask;
/*      */     }
/*  623 */     return false;
/*      */   }
/*      */   public boolean containsValue(byte v) {
/*  626 */     byte[] value = this.value;
/*  627 */     boolean[] used = this.used;
/*  628 */     for (int i = this.n; i-- != 0; return true) label16: if ((used[i] == 0) || (value[i] != v))
/*      */         break label16; return false;
/*      */   }
/*      */ 
/*      */   public void clear()
/*      */   {
/*  638 */     if (this.size == 0) return;
/*  639 */     this.size = 0;
/*  640 */     BooleanArrays.fill(this.used, false);
/*      */ 
/*  642 */     ObjectArrays.fill(this.key, null);
/*  643 */     this.first = (this.last = -1);
/*      */   }
/*      */   public int size() {
/*  646 */     return this.size;
/*      */   }
/*      */   public boolean isEmpty() {
/*  649 */     return this.size == 0;
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
/*  666 */     return 16;
/*      */   }
/*      */ 
/*      */   protected void fixPointers(int i)
/*      */   {
/*  716 */     if (this.size == 0) {
/*  717 */       this.first = (this.last = -1);
/*  718 */       return;
/*      */     }
/*  720 */     if (this.first == i) {
/*  721 */       this.first = ((int)this.link[i]);
/*  722 */       if (0 <= this.first)
/*      */       {
/*  724 */         this.link[this.first] |= -4294967296L;
/*      */       }
/*  726 */       return;
/*      */     }
/*  728 */     if (this.last == i) {
/*  729 */       this.last = ((int)(this.link[i] >>> 32));
/*  730 */       if (0 <= this.last)
/*      */       {
/*  732 */         this.link[this.last] |= 4294967295L;
/*      */       }
/*  734 */       return;
/*      */     }
/*  736 */     long linki = this.link[i];
/*  737 */     int prev = (int)(linki >>> 32);
/*  738 */     int next = (int)linki;
/*  739 */     this.link[prev] ^= (this.link[prev] ^ linki & 0xFFFFFFFF) & 0xFFFFFFFF;
/*  740 */     this.link[next] ^= (this.link[next] ^ linki & 0x0) & 0x0;
/*      */   }
/*      */ 
/*      */   protected void fixPointers(int s, int d)
/*      */   {
/*  751 */     if (this.size == 1) {
/*  752 */       this.first = (this.last = d);
/*      */ 
/*  754 */       this.link[d] = -1L;
/*  755 */       return;
/*      */     }
/*  757 */     if (this.first == s) {
/*  758 */       this.first = d;
/*  759 */       this.link[((int)this.link[s])] ^= (this.link[((int)this.link[s])] ^ (d & 0xFFFFFFFF) << 32) & 0x0;
/*  760 */       this.link[d] = this.link[s];
/*  761 */       return;
/*      */     }
/*  763 */     if (this.last == s) {
/*  764 */       this.last = d;
/*  765 */       this.link[((int)(this.link[s] >>> 32))] ^= (this.link[((int)(this.link[s] >>> 32))] ^ d & 0xFFFFFFFF) & 0xFFFFFFFF;
/*  766 */       this.link[d] = this.link[s];
/*  767 */       return;
/*      */     }
/*  769 */     long links = this.link[s];
/*  770 */     int prev = (int)(links >>> 32);
/*  771 */     int next = (int)links;
/*  772 */     this.link[prev] ^= (this.link[prev] ^ d & 0xFFFFFFFF) & 0xFFFFFFFF;
/*  773 */     this.link[next] ^= (this.link[next] ^ (d & 0xFFFFFFFF) << 32) & 0x0;
/*  774 */     this.link[d] = links;
/*      */   }
/*      */ 
/*      */   public K firstKey()
/*      */   {
/*  781 */     if (this.size == 0) throw new NoSuchElementException();
/*  782 */     return this.key[this.first];
/*      */   }
/*      */ 
/*      */   public K lastKey()
/*      */   {
/*  789 */     if (this.size == 0) throw new NoSuchElementException();
/*  790 */     return this.key[this.last];
/*      */   }
/*  792 */   public Comparator<? super K> comparator() { return null; } 
/*  793 */   public Object2ByteSortedMap<K> tailMap(K from) { throw new UnsupportedOperationException(); } 
/*  794 */   public Object2ByteSortedMap<K> headMap(K to) { throw new UnsupportedOperationException(); } 
/*  795 */   public Object2ByteSortedMap<K> subMap(K from, K to) { throw new UnsupportedOperationException(); }
/*      */ 
/*      */ 
/*      */   public Object2ByteSortedMap.FastSortedEntrySet<K> object2ByteEntrySet()
/*      */   {
/* 1036 */     if (this.entries == null) this.entries = new MapEntrySet(null);
/* 1037 */     return this.entries;
/*      */   }
/*      */ 
/*      */   public ObjectSortedSet<K> keySet()
/*      */   {
/* 1088 */     if (this.keys == null) this.keys = new KeySet(null);
/* 1089 */     return this.keys;
/*      */   }
/*      */ 
/*      */   public ByteCollection values()
/*      */   {
/* 1109 */     if (this.values == null) this.values = new AbstractByteCollection() {
/*      */         public ByteIterator iterator() {
/* 1111 */           return new Object2ByteLinkedOpenCustomHashMap.ValueIterator(Object2ByteLinkedOpenCustomHashMap.this);
/*      */         }
/*      */         public int size() {
/* 1114 */           return Object2ByteLinkedOpenCustomHashMap.this.size;
/*      */         }
/*      */         public boolean contains(byte v) {
/* 1117 */           return Object2ByteLinkedOpenCustomHashMap.this.containsValue(v);
/*      */         }
/*      */         public void clear() {
/* 1120 */           Object2ByteLinkedOpenCustomHashMap.this.clear();
/*      */         }
/*      */       };
/* 1123 */     return this.values;
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public boolean rehash()
/*      */   {
/* 1137 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean trim()
/*      */   {
/* 1152 */     int l = HashCommon.arraySize(this.size, this.f);
/* 1153 */     if (l >= this.n) return true; try
/*      */     {
/* 1155 */       rehash(l);
/*      */     } catch (OutOfMemoryError cantDoIt) {
/* 1157 */       return false;
/* 1158 */     }return true;
/*      */   }
/*      */ 
/*      */   public boolean trim(int n)
/*      */   {
/* 1179 */     int l = HashCommon.nextPowerOfTwo((int)Math.ceil(n / this.f));
/* 1180 */     if (this.n <= l) return true; try
/*      */     {
/* 1182 */       rehash(l);
/*      */     } catch (OutOfMemoryError cantDoIt) {
/* 1184 */       return false;
/* 1185 */     }return true;
/*      */   }
/*      */ 
/*      */   protected void rehash(int newN)
/*      */   {
/* 1198 */     int i = this.first; int prev = -1; int newPrev = -1;
/*      */ 
/* 1200 */     Object[] key = this.key;
/* 1201 */     byte[] value = this.value;
/* 1202 */     int newMask = newN - 1;
/* 1203 */     Object[] newKey = (Object[])new Object[newN];
/* 1204 */     byte[] newValue = new byte[newN];
/* 1205 */     boolean[] newUsed = new boolean[newN];
/* 1206 */     long[] link = this.link;
/* 1207 */     long[] newLink = new long[newN];
/* 1208 */     this.first = -1;
/* 1209 */     for (int j = this.size; j-- != 0; ) {
/* 1210 */       Object k = key[i];
/* 1211 */       int pos = HashCommon.murmurHash3(this.strategy.hashCode(k)) & newMask;
/* 1212 */       while (newUsed[pos] != 0) pos = pos + 1 & newMask;
/* 1213 */       newUsed[pos] = true;
/* 1214 */       newKey[pos] = k;
/* 1215 */       newValue[pos] = value[i];
/* 1216 */       if (prev != -1) {
/* 1217 */         newLink[newPrev] ^= (newLink[newPrev] ^ pos & 0xFFFFFFFF) & 0xFFFFFFFF;
/* 1218 */         newLink[pos] ^= (newLink[pos] ^ (newPrev & 0xFFFFFFFF) << 32) & 0x0;
/* 1219 */         newPrev = pos;
/*      */       }
/*      */       else {
/* 1222 */         newPrev = this.first = pos;
/*      */ 
/* 1224 */         newLink[pos] = -1L;
/*      */       }
/* 1226 */       int t = i;
/* 1227 */       i = (int)link[i];
/* 1228 */       prev = t;
/*      */     }
/* 1230 */     this.n = newN;
/* 1231 */     this.mask = newMask;
/* 1232 */     this.maxFill = HashCommon.maxFill(this.n, this.f);
/* 1233 */     this.key = newKey;
/* 1234 */     this.value = newValue;
/* 1235 */     this.used = newUsed;
/* 1236 */     this.link = newLink;
/* 1237 */     this.last = newPrev;
/* 1238 */     if (newPrev != -1)
/*      */     {
/* 1240 */       newLink[newPrev] |= 4294967295L;
/*      */     }
/*      */   }
/*      */ 
/*      */   public Object2ByteLinkedOpenCustomHashMap<K> clone()
/*      */   {
/*      */     Object2ByteLinkedOpenCustomHashMap c;
/*      */     try
/*      */     {
/* 1253 */       c = (Object2ByteLinkedOpenCustomHashMap)super.clone();
/*      */     }
/*      */     catch (CloneNotSupportedException cantHappen) {
/* 1256 */       throw new InternalError();
/*      */     }
/* 1258 */     c.keys = null;
/* 1259 */     c.values = null;
/* 1260 */     c.entries = null;
/* 1261 */     c.key = ((Object[])this.key.clone());
/* 1262 */     c.value = ((byte[])this.value.clone());
/* 1263 */     c.used = ((boolean[])this.used.clone());
/* 1264 */     c.link = ((long[])this.link.clone());
/* 1265 */     c.strategy = this.strategy;
/* 1266 */     return c;
/*      */   }
/*      */ 
/*      */   public int hashCode()
/*      */   {
/* 1278 */     int h = 0;
/* 1279 */     int j = this.size; int i = 0; for (int t = 0; j-- != 0; ) {
/* 1280 */       while (this.used[i] == 0) i++;
/* 1281 */       if (this != this.key[i])
/* 1282 */         t = this.strategy.hashCode(this.key[i]);
/* 1283 */       t ^= this.value[i];
/* 1284 */       h += t;
/* 1285 */       i++;
/*      */     }
/* 1287 */     return h;
/*      */   }
/*      */   private void writeObject(ObjectOutputStream s) throws IOException {
/* 1290 */     Object[] key = this.key;
/* 1291 */     byte[] value = this.value;
/* 1292 */     MapIterator i = new MapIterator(null);
/* 1293 */     s.defaultWriteObject();
/* 1294 */     for (int j = this.size; j-- != 0; ) {
/* 1295 */       int e = i.nextEntry();
/* 1296 */       s.writeObject(key[e]);
/* 1297 */       s.writeByte(value[e]);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
/* 1302 */     s.defaultReadObject();
/* 1303 */     this.n = HashCommon.arraySize(this.size, this.f);
/* 1304 */     this.maxFill = HashCommon.maxFill(this.n, this.f);
/* 1305 */     this.mask = (this.n - 1);
/* 1306 */     Object[] key = this.key = (Object[])new Object[this.n];
/* 1307 */     byte[] value = this.value = new byte[this.n];
/* 1308 */     boolean[] used = this.used = new boolean[this.n];
/* 1309 */     long[] link = this.link = new long[this.n];
/* 1310 */     int prev = -1;
/* 1311 */     this.first = (this.last = -1);
/*      */ 
/* 1314 */     int i = this.size; for (int pos = 0; i-- != 0; ) {
/* 1315 */       Object k = s.readObject();
/* 1316 */       byte v = s.readByte();
/* 1317 */       pos = HashCommon.murmurHash3(this.strategy.hashCode(k)) & this.mask;
/* 1318 */       while (used[pos] != 0) pos = pos + 1 & this.mask;
/* 1319 */       used[pos] = true;
/* 1320 */       key[pos] = k;
/* 1321 */       value[pos] = v;
/* 1322 */       if (this.first != -1) {
/* 1323 */         link[prev] ^= (link[prev] ^ pos & 0xFFFFFFFF) & 0xFFFFFFFF;
/* 1324 */         link[pos] ^= (link[pos] ^ (prev & 0xFFFFFFFF) << 32) & 0x0;
/* 1325 */         prev = pos;
/*      */       }
/*      */       else {
/* 1328 */         prev = this.first = pos;
/*      */ 
/* 1330 */         link[pos] |= -4294967296L;
/*      */       }
/*      */     }
/* 1333 */     this.last = prev;
/* 1334 */     if (prev != -1)
/*      */     {
/* 1336 */       link[prev] |= 4294967295L;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void checkTable()
/*      */   {
/*      */   }
/*      */ 
/*      */   private final class ValueIterator extends Object2ByteLinkedOpenCustomHashMap.MapIterator
/*      */     implements ByteListIterator
/*      */   {
/*      */     public byte previousByte()
/*      */     {
/* 1098 */       return Object2ByteLinkedOpenCustomHashMap.this.value[previousEntry()]; } 
/* 1099 */     public Byte previous() { return Byte.valueOf(Object2ByteLinkedOpenCustomHashMap.this.value[previousEntry()]); } 
/* 1100 */     public void set(Byte ok) { throw new UnsupportedOperationException(); } 
/* 1101 */     public void add(Byte ok) { throw new UnsupportedOperationException(); } 
/* 1102 */     public void set(byte v) { throw new UnsupportedOperationException(); } 
/* 1103 */     public void add(byte v) { throw new UnsupportedOperationException(); } 
/* 1104 */     public ValueIterator() { super(null); } 
/* 1105 */     public byte nextByte() { return Object2ByteLinkedOpenCustomHashMap.this.value[nextEntry()]; } 
/* 1106 */     public Byte next() { return Byte.valueOf(Object2ByteLinkedOpenCustomHashMap.this.value[nextEntry()]); }
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
/* 1055 */       return new Object2ByteLinkedOpenCustomHashMap.KeyIterator(Object2ByteLinkedOpenCustomHashMap.this, from);
/*      */     }
/*      */     public ObjectListIterator<K> iterator() {
/* 1058 */       return new Object2ByteLinkedOpenCustomHashMap.KeyIterator(Object2ByteLinkedOpenCustomHashMap.this);
/*      */     }
/*      */     public int size() {
/* 1061 */       return Object2ByteLinkedOpenCustomHashMap.this.size;
/*      */     }
/*      */     public boolean contains(Object k) {
/* 1064 */       return Object2ByteLinkedOpenCustomHashMap.this.containsKey(k);
/*      */     }
/*      */     public boolean remove(Object k) {
/* 1067 */       int oldSize = Object2ByteLinkedOpenCustomHashMap.this.size;
/* 1068 */       Object2ByteLinkedOpenCustomHashMap.this.remove(k);
/* 1069 */       return Object2ByteLinkedOpenCustomHashMap.this.size != oldSize;
/*      */     }
/*      */     public void clear() {
/* 1072 */       Object2ByteLinkedOpenCustomHashMap.this.clear();
/*      */     }
/*      */     public K first() {
/* 1075 */       if (Object2ByteLinkedOpenCustomHashMap.this.size == 0) throw new NoSuchElementException();
/* 1076 */       return Object2ByteLinkedOpenCustomHashMap.this.key[Object2ByteLinkedOpenCustomHashMap.this.first];
/*      */     }
/*      */     public K last() {
/* 1079 */       if (Object2ByteLinkedOpenCustomHashMap.this.size == 0) throw new NoSuchElementException();
/* 1080 */       return Object2ByteLinkedOpenCustomHashMap.this.key[Object2ByteLinkedOpenCustomHashMap.this.last];
/*      */     }
/* 1082 */     public Comparator<? super K> comparator() { return null; } 
/* 1083 */     public final ObjectSortedSet<K> tailSet(K from) { throw new UnsupportedOperationException(); } 
/* 1084 */     public final ObjectSortedSet<K> headSet(K to) { throw new UnsupportedOperationException(); } 
/* 1085 */     public final ObjectSortedSet<K> subSet(K from, K to) { throw new UnsupportedOperationException(); }
/*      */ 
/*      */   }
/*      */ 
/*      */   private final class KeyIterator extends Object2ByteLinkedOpenCustomHashMap<K>.MapIterator
/*      */     implements ObjectListIterator<K>
/*      */   {
/*      */     public KeyIterator()
/*      */     {
/* 1046 */       super(k, null); } 
/* 1047 */     public K previous() { return Object2ByteLinkedOpenCustomHashMap.this.key[previousEntry()]; } 
/* 1048 */     public void set(K k) { throw new UnsupportedOperationException(); } 
/* 1049 */     public void add(K k) { throw new UnsupportedOperationException(); } 
/* 1050 */     public KeyIterator() { super(null); } 
/* 1051 */     public K next() { return Object2ByteLinkedOpenCustomHashMap.this.key[nextEntry()]; }
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
/*  974 */       return new Object2ByteLinkedOpenCustomHashMap.EntryIterator(Object2ByteLinkedOpenCustomHashMap.this);
/*      */     }
/*  976 */     public Comparator<? super Object2ByteMap.Entry<K>> comparator() { return null; } 
/*  977 */     public ObjectSortedSet<Object2ByteMap.Entry<K>> subSet(Object2ByteMap.Entry<K> fromElement, Object2ByteMap.Entry<K> toElement) { throw new UnsupportedOperationException(); } 
/*  978 */     public ObjectSortedSet<Object2ByteMap.Entry<K>> headSet(Object2ByteMap.Entry<K> toElement) { throw new UnsupportedOperationException(); } 
/*  979 */     public ObjectSortedSet<Object2ByteMap.Entry<K>> tailSet(Object2ByteMap.Entry<K> fromElement) { throw new UnsupportedOperationException(); } 
/*      */     public Object2ByteMap.Entry<K> first() {
/*  981 */       if (Object2ByteLinkedOpenCustomHashMap.this.size == 0) throw new NoSuchElementException();
/*  982 */       return new Object2ByteLinkedOpenCustomHashMap.MapEntry(Object2ByteLinkedOpenCustomHashMap.this, Object2ByteLinkedOpenCustomHashMap.this.first);
/*      */     }
/*      */     public Object2ByteMap.Entry<K> last() {
/*  985 */       if (Object2ByteLinkedOpenCustomHashMap.this.size == 0) throw new NoSuchElementException();
/*  986 */       return new Object2ByteLinkedOpenCustomHashMap.MapEntry(Object2ByteLinkedOpenCustomHashMap.this, Object2ByteLinkedOpenCustomHashMap.this.last);
/*      */     }
/*      */ 
/*      */     public boolean contains(Object o) {
/*  990 */       if (!(o instanceof Map.Entry)) return false;
/*  991 */       Map.Entry e = (Map.Entry)o;
/*  992 */       Object k = e.getKey();
/*      */ 
/*  994 */       int pos = HashCommon.murmurHash3(Object2ByteLinkedOpenCustomHashMap.this.strategy.hashCode(k)) & Object2ByteLinkedOpenCustomHashMap.this.mask;
/*      */ 
/*  996 */       while (Object2ByteLinkedOpenCustomHashMap.this.used[pos] != 0) {
/*  997 */         if (Object2ByteLinkedOpenCustomHashMap.this.strategy.equals(Object2ByteLinkedOpenCustomHashMap.this.key[pos], k)) return Object2ByteLinkedOpenCustomHashMap.this.value[pos] == ((Byte)e.getValue()).byteValue();
/*  998 */         pos = pos + 1 & Object2ByteLinkedOpenCustomHashMap.this.mask;
/*      */       }
/* 1000 */       return false;
/*      */     }
/*      */ 
/*      */     public boolean remove(Object o) {
/* 1004 */       if (!(o instanceof Map.Entry)) return false;
/* 1005 */       Map.Entry e = (Map.Entry)o;
/* 1006 */       Object k = e.getKey();
/*      */ 
/* 1008 */       int pos = HashCommon.murmurHash3(Object2ByteLinkedOpenCustomHashMap.this.strategy.hashCode(k)) & Object2ByteLinkedOpenCustomHashMap.this.mask;
/*      */ 
/* 1010 */       while (Object2ByteLinkedOpenCustomHashMap.this.used[pos] != 0) {
/* 1011 */         if (Object2ByteLinkedOpenCustomHashMap.this.strategy.equals(Object2ByteLinkedOpenCustomHashMap.this.key[pos], k)) {
/* 1012 */           Object2ByteLinkedOpenCustomHashMap.this.remove(e.getKey());
/* 1013 */           return true;
/*      */         }
/* 1015 */         pos = pos + 1 & Object2ByteLinkedOpenCustomHashMap.this.mask;
/*      */       }
/* 1017 */       return false;
/*      */     }
/*      */     public int size() {
/* 1020 */       return Object2ByteLinkedOpenCustomHashMap.this.size;
/*      */     }
/*      */     public void clear() {
/* 1023 */       Object2ByteLinkedOpenCustomHashMap.this.clear();
/*      */     }
/*      */     public ObjectBidirectionalIterator<Object2ByteMap.Entry<K>> iterator(Object2ByteMap.Entry<K> from) {
/* 1026 */       return new Object2ByteLinkedOpenCustomHashMap.EntryIterator(Object2ByteLinkedOpenCustomHashMap.this, from.getKey());
/*      */     }
/*      */     public ObjectBidirectionalIterator<Object2ByteMap.Entry<K>> fastIterator() {
/* 1029 */       return new Object2ByteLinkedOpenCustomHashMap.FastEntryIterator(Object2ByteLinkedOpenCustomHashMap.this);
/*      */     }
/*      */     public ObjectBidirectionalIterator<Object2ByteMap.Entry<K>> fastIterator(Object2ByteMap.Entry<K> from) {
/* 1032 */       return new Object2ByteLinkedOpenCustomHashMap.FastEntryIterator(Object2ByteLinkedOpenCustomHashMap.this, from.getKey());
/*      */     }
/*      */   }
/*      */ 
/*      */   private class FastEntryIterator extends Object2ByteLinkedOpenCustomHashMap<K>.MapIterator
/*      */     implements ObjectListIterator<Object2ByteMap.Entry<K>>
/*      */   {
/*  952 */     final AbstractObject2ByteMap.BasicEntry<K> entry = new AbstractObject2ByteMap.BasicEntry(null, (byte)0);
/*      */ 
/*  953 */     public FastEntryIterator() { super(null); } 
/*      */     public FastEntryIterator() {
/*  955 */       super(from, null);
/*      */     }
/*      */     public AbstractObject2ByteMap.BasicEntry<K> next() {
/*  958 */       int e = nextEntry();
/*  959 */       this.entry.key = Object2ByteLinkedOpenCustomHashMap.this.key[e];
/*  960 */       this.entry.value = Object2ByteLinkedOpenCustomHashMap.this.value[e];
/*  961 */       return this.entry;
/*      */     }
/*      */     public AbstractObject2ByteMap.BasicEntry<K> previous() {
/*  964 */       int e = previousEntry();
/*  965 */       this.entry.key = Object2ByteLinkedOpenCustomHashMap.this.key[e];
/*  966 */       this.entry.value = Object2ByteLinkedOpenCustomHashMap.this.value[e];
/*  967 */       return this.entry;
/*      */     }
/*  969 */     public void set(Object2ByteMap.Entry<K> ok) { throw new UnsupportedOperationException(); } 
/*  970 */     public void add(Object2ByteMap.Entry<K> ok) { throw new UnsupportedOperationException(); }
/*      */ 
/*      */   }
/*      */ 
/*      */   private class EntryIterator extends Object2ByteLinkedOpenCustomHashMap<K>.MapIterator
/*      */     implements ObjectListIterator<Object2ByteMap.Entry<K>>
/*      */   {
/*      */     private Object2ByteLinkedOpenCustomHashMap<K>.MapEntry entry;
/*      */ 
/*      */     public EntryIterator()
/*      */     {
/*  933 */       super(null);
/*      */     }
/*  935 */     public EntryIterator() { super(from, null); }
/*      */ 
/*      */     public Object2ByteLinkedOpenCustomHashMap<K>.MapEntry next() {
/*  938 */       return this.entry = new Object2ByteLinkedOpenCustomHashMap.MapEntry(Object2ByteLinkedOpenCustomHashMap.this, nextEntry());
/*      */     }
/*      */     public Object2ByteLinkedOpenCustomHashMap<K>.MapEntry previous() {
/*  941 */       return this.entry = new Object2ByteLinkedOpenCustomHashMap.MapEntry(Object2ByteLinkedOpenCustomHashMap.this, previousEntry());
/*      */     }
/*      */ 
/*      */     public void remove() {
/*  945 */       super.remove();
/*  946 */       Object2ByteLinkedOpenCustomHashMap.MapEntry.access$202(this.entry, -1);
/*      */     }
/*  948 */     public void set(Object2ByteMap.Entry<K> ok) { throw new UnsupportedOperationException(); } 
/*  949 */     public void add(Object2ByteMap.Entry<K> ok) { throw new UnsupportedOperationException(); }
/*      */ 
/*      */   }
/*      */ 
/*      */   private class MapIterator
/*      */   {
/*  804 */     int prev = -1;
/*      */ 
/*  806 */     int next = -1;
/*      */ 
/*  808 */     int curr = -1;
/*      */ 
/*  810 */     int index = -1;
/*      */ 
/*  812 */     private MapIterator() { this.next = Object2ByteLinkedOpenCustomHashMap.this.first;
/*  813 */       this.index = 0; }
/*      */ 
/*      */     private MapIterator() {
/*  816 */       if (Object2ByteLinkedOpenCustomHashMap.this.strategy.equals(Object2ByteLinkedOpenCustomHashMap.this.key[Object2ByteLinkedOpenCustomHashMap.this.last], from)) {
/*  817 */         this.prev = Object2ByteLinkedOpenCustomHashMap.this.last;
/*  818 */         this.index = Object2ByteLinkedOpenCustomHashMap.this.size;
/*      */       }
/*      */       else
/*      */       {
/*  822 */         int pos = HashCommon.murmurHash3(Object2ByteLinkedOpenCustomHashMap.this.strategy.hashCode(from)) & Object2ByteLinkedOpenCustomHashMap.this.mask;
/*      */ 
/*  824 */         while (Object2ByteLinkedOpenCustomHashMap.this.used[pos] != 0) {
/*  825 */           if (Object2ByteLinkedOpenCustomHashMap.this.strategy.equals(Object2ByteLinkedOpenCustomHashMap.this.key[pos], from))
/*      */           {
/*  827 */             this.next = ((int)Object2ByteLinkedOpenCustomHashMap.this.link[pos]);
/*  828 */             this.prev = pos;
/*  829 */             return;
/*      */           }
/*  831 */           pos = pos + 1 & Object2ByteLinkedOpenCustomHashMap.this.mask;
/*      */         }
/*  833 */         throw new NoSuchElementException("The key " + from + " does not belong to this map.");
/*      */       }
/*      */     }
/*  836 */     public boolean hasNext() { return this.next != -1; } 
/*  837 */     public boolean hasPrevious() { return this.prev != -1; } 
/*      */     private final void ensureIndexKnown() {
/*  839 */       if (this.index >= 0) return;
/*  840 */       if (this.prev == -1) {
/*  841 */         this.index = 0;
/*  842 */         return;
/*      */       }
/*  844 */       if (this.next == -1) {
/*  845 */         this.index = Object2ByteLinkedOpenCustomHashMap.this.size;
/*  846 */         return;
/*      */       }
/*  848 */       int pos = Object2ByteLinkedOpenCustomHashMap.this.first;
/*  849 */       this.index = 1;
/*  850 */       while (pos != this.prev) {
/*  851 */         pos = (int)Object2ByteLinkedOpenCustomHashMap.this.link[pos];
/*  852 */         this.index += 1;
/*      */       }
/*      */     }
/*      */ 
/*  856 */     public int nextIndex() { ensureIndexKnown();
/*  857 */       return this.index; }
/*      */ 
/*      */     public int previousIndex() {
/*  860 */       ensureIndexKnown();
/*  861 */       return this.index - 1;
/*      */     }
/*      */     public int nextEntry() {
/*  864 */       if (!hasNext()) return Object2ByteLinkedOpenCustomHashMap.this.size();
/*  865 */       this.curr = this.next;
/*  866 */       this.next = ((int)Object2ByteLinkedOpenCustomHashMap.this.link[this.curr]);
/*  867 */       this.prev = this.curr;
/*  868 */       if (this.index >= 0) this.index += 1;
/*  869 */       return this.curr;
/*      */     }
/*      */     public int previousEntry() {
/*  872 */       if (!hasPrevious()) return -1;
/*  873 */       this.curr = this.prev;
/*  874 */       this.prev = ((int)(Object2ByteLinkedOpenCustomHashMap.this.link[this.curr] >>> 32));
/*  875 */       this.next = this.curr;
/*  876 */       if (this.index >= 0) this.index -= 1;
/*  877 */       return this.curr;
/*      */     }
/*      */ 
/*      */     public void remove() {
/*  881 */       ensureIndexKnown();
/*  882 */       if (this.curr == -1) throw new IllegalStateException();
/*  883 */       if (this.curr == this.prev)
/*      */       {
/*  886 */         this.index -= 1;
/*  887 */         this.prev = ((int)(Object2ByteLinkedOpenCustomHashMap.this.link[this.curr] >>> 32));
/*      */       }
/*      */       else {
/*  890 */         this.next = ((int)Object2ByteLinkedOpenCustomHashMap.this.link[this.curr]);
/*  891 */       }Object2ByteLinkedOpenCustomHashMap.this.size -= 1;
/*      */ 
/*  894 */       if (this.prev == -1) Object2ByteLinkedOpenCustomHashMap.this.first = this.next;
/*      */       else
/*  896 */         Object2ByteLinkedOpenCustomHashMap.this.link[this.prev] ^= (Object2ByteLinkedOpenCustomHashMap.this.link[this.prev] ^ this.next & 0xFFFFFFFF) & 0xFFFFFFFF;
/*  897 */       if (this.next == -1) Object2ByteLinkedOpenCustomHashMap.this.last = this.prev;
/*      */       else
/*  899 */         Object2ByteLinkedOpenCustomHashMap.this.link[this.next] ^= (Object2ByteLinkedOpenCustomHashMap.this.link[this.next] ^ (this.prev & 0xFFFFFFFF) << 32) & 0x0; int pos = this.curr;
/*      */       int last;
/*      */       while (true) {
/*  903 */         pos = (last = pos) + 1 & Object2ByteLinkedOpenCustomHashMap.this.mask;
/*  904 */         while (Object2ByteLinkedOpenCustomHashMap.this.used[pos] != 0) {
/*  905 */           int slot = HashCommon.murmurHash3(Object2ByteLinkedOpenCustomHashMap.this.strategy.hashCode(Object2ByteLinkedOpenCustomHashMap.this.key[pos])) & Object2ByteLinkedOpenCustomHashMap.this.mask;
/*  906 */           if (last <= pos ? (last < slot) && (slot <= pos) : (last >= slot) && (slot > pos)) break;
/*  907 */           pos = pos + 1 & Object2ByteLinkedOpenCustomHashMap.this.mask;
/*      */         }
/*  909 */         if (Object2ByteLinkedOpenCustomHashMap.this.used[pos] == 0) break;
/*  910 */         Object2ByteLinkedOpenCustomHashMap.this.key[last] = Object2ByteLinkedOpenCustomHashMap.this.key[pos];
/*  911 */         Object2ByteLinkedOpenCustomHashMap.this.value[last] = Object2ByteLinkedOpenCustomHashMap.this.value[pos];
/*  912 */         if (this.next == pos) this.next = last;
/*  913 */         if (this.prev == pos) this.prev = last;
/*  914 */         Object2ByteLinkedOpenCustomHashMap.this.fixPointers(pos, last);
/*      */       }
/*  916 */       Object2ByteLinkedOpenCustomHashMap.this.used[last] = false;
/*  917 */       Object2ByteLinkedOpenCustomHashMap.this.key[last] = null;
/*  918 */       this.curr = -1;
/*      */     }
/*      */     public int skip(int n) {
/*  921 */       int i = n;
/*  922 */       while ((i-- != 0) && (hasNext())) nextEntry();
/*  923 */       return n - i - 1;
/*      */     }
/*      */     public int back(int n) {
/*  926 */       int i = n;
/*  927 */       while ((i-- != 0) && (hasPrevious())) previousEntry();
/*  928 */       return n - i - 1;
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
/*  676 */       this.index = index;
/*      */     }
/*      */     public K getKey() {
/*  679 */       return Object2ByteLinkedOpenCustomHashMap.this.key[this.index];
/*      */     }
/*      */     public Byte getValue() {
/*  682 */       return Byte.valueOf(Object2ByteLinkedOpenCustomHashMap.this.value[this.index]);
/*      */     }
/*      */     public byte getByteValue() {
/*  685 */       return Object2ByteLinkedOpenCustomHashMap.this.value[this.index];
/*      */     }
/*      */     public byte setValue(byte v) {
/*  688 */       byte oldValue = Object2ByteLinkedOpenCustomHashMap.this.value[this.index];
/*  689 */       Object2ByteLinkedOpenCustomHashMap.this.value[this.index] = v;
/*  690 */       return oldValue;
/*      */     }
/*      */     public Byte setValue(Byte v) {
/*  693 */       return Byte.valueOf(setValue(v.byteValue()));
/*      */     }
/*      */ 
/*      */     public boolean equals(Object o) {
/*  697 */       if (!(o instanceof Map.Entry)) return false;
/*  698 */       Map.Entry e = (Map.Entry)o;
/*  699 */       return (Object2ByteLinkedOpenCustomHashMap.this.strategy.equals(Object2ByteLinkedOpenCustomHashMap.this.key[this.index], e.getKey())) && (Object2ByteLinkedOpenCustomHashMap.this.value[this.index] == ((Byte)e.getValue()).byteValue());
/*      */     }
/*      */     public int hashCode() {
/*  702 */       return Object2ByteLinkedOpenCustomHashMap.this.strategy.hashCode(Object2ByteLinkedOpenCustomHashMap.this.key[this.index]) ^ Object2ByteLinkedOpenCustomHashMap.this.value[this.index];
/*      */     }
/*      */     public String toString() {
/*  705 */       return Object2ByteLinkedOpenCustomHashMap.this.key[this.index] + "=>" + Object2ByteLinkedOpenCustomHashMap.this.value[this.index];
/*      */     }
/*      */   }
/*      */ }

/* Location:           C:\Users\Raul\Desktop\StarMade\StarMade.jar
 * Qualified Name:     it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenCustomHashMap
 * JD-Core Version:    0.6.2
 */