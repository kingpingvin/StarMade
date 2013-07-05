/*     */ package it.unimi.dsi.fastutil.objects;
/*     */ 
/*     */ import it.unimi.dsi.fastutil.Hash;
/*     */ import it.unimi.dsi.fastutil.HashCommon;
/*     */ import it.unimi.dsi.fastutil.booleans.BooleanArrays;
/*     */ import it.unimi.dsi.fastutil.chars.AbstractCharCollection;
/*     */ import it.unimi.dsi.fastutil.chars.CharCollection;
/*     */ import it.unimi.dsi.fastutil.chars.CharIterator;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.NoSuchElementException;
/*     */ 
/*     */ public class Reference2CharOpenHashMap<K> extends AbstractReference2CharMap<K>
/*     */   implements Serializable, Cloneable, Hash
/*     */ {
/*     */   public static final long serialVersionUID = 0L;
/*     */   private static final boolean ASSERTS = false;
/*     */   protected transient K[] key;
/*     */   protected transient char[] value;
/*     */   protected transient boolean[] used;
/*     */   protected final float f;
/*     */   protected transient int n;
/*     */   protected transient int maxFill;
/*     */   protected transient int mask;
/*     */   protected int size;
/*     */   protected volatile transient Reference2CharMap.FastEntrySet<K> entries;
/*     */   protected volatile transient ReferenceSet<K> keys;
/*     */   protected volatile transient CharCollection values;
/*     */ 
/*     */   public Reference2CharOpenHashMap(int expected, float f)
/*     */   {
/* 106 */     if ((f <= 0.0F) || (f > 1.0F)) throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
/* 107 */     if (expected < 0) throw new IllegalArgumentException("The expected number of elements must be nonnegative");
/* 108 */     this.f = f;
/* 109 */     this.n = HashCommon.arraySize(expected, f);
/* 110 */     this.mask = (this.n - 1);
/* 111 */     this.maxFill = HashCommon.maxFill(this.n, f);
/* 112 */     this.key = ((Object[])new Object[this.n]);
/* 113 */     this.value = new char[this.n];
/* 114 */     this.used = new boolean[this.n];
/*     */   }
/*     */ 
/*     */   public Reference2CharOpenHashMap(int expected)
/*     */   {
/* 121 */     this(expected, 0.75F);
/*     */   }
/*     */ 
/*     */   public Reference2CharOpenHashMap()
/*     */   {
/* 127 */     this(16, 0.75F);
/*     */   }
/*     */ 
/*     */   public Reference2CharOpenHashMap(Map<? extends K, ? extends Character> m, float f)
/*     */   {
/* 135 */     this(m.size(), f);
/* 136 */     putAll(m);
/*     */   }
/*     */ 
/*     */   public Reference2CharOpenHashMap(Map<? extends K, ? extends Character> m)
/*     */   {
/* 143 */     this(m, 0.75F);
/*     */   }
/*     */ 
/*     */   public Reference2CharOpenHashMap(Reference2CharMap<K> m, float f)
/*     */   {
/* 151 */     this(m.size(), f);
/* 152 */     putAll(m);
/*     */   }
/*     */ 
/*     */   public Reference2CharOpenHashMap(Reference2CharMap<K> m)
/*     */   {
/* 159 */     this(m, 0.75F);
/*     */   }
/*     */ 
/*     */   public Reference2CharOpenHashMap(K[] k, char[] v, float f)
/*     */   {
/* 169 */     this(k.length, f);
/* 170 */     if (k.length != v.length) throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
/* 171 */     for (int i = 0; i < k.length; i++) put(k[i], v[i]);
/*     */   }
/*     */ 
/*     */   public Reference2CharOpenHashMap(K[] k, char[] v)
/*     */   {
/* 180 */     this(k, v, 0.75F);
/*     */   }
/*     */ 
/*     */   public char put(K k, char v)
/*     */   {
/* 188 */     int pos = (k == null ? 142593372 : HashCommon.murmurHash3(System.identityHashCode(k))) & this.mask;
/*     */ 
/* 190 */     while (this.used[pos] != 0) {
/* 191 */       if (this.key[pos] == k) {
/* 192 */         char oldValue = this.value[pos];
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
/*     */   public Character put(K ok, Character ov) {
/* 206 */     char v = ov.charValue();
/* 207 */     Object k = ok;
/*     */ 
/* 209 */     int pos = (k == null ? 142593372 : HashCommon.murmurHash3(System.identityHashCode(k))) & this.mask;
/*     */ 
/* 211 */     while (this.used[pos] != 0) {
/* 212 */       if (this.key[pos] == k) {
/* 213 */         Character oldValue = Character.valueOf(this.value[pos]);
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
/*     */   protected final int shiftKeys(int pos)
/*     */   {
/*     */     int last;
/*     */     while (true)
/*     */     {
/* 236 */       pos = (last = pos) + 1 & this.mask;
/* 237 */       while (this.used[pos] != 0) {
/* 238 */         int slot = (this.key[pos] == null ? 142593372 : HashCommon.murmurHash3(System.identityHashCode(this.key[pos]))) & this.mask;
/* 239 */         if (last <= pos ? (last < slot) && (slot <= pos) : (last >= slot) && (slot > pos)) break;
/* 240 */         pos = pos + 1 & this.mask;
/*     */       }
/* 242 */       if (this.used[pos] == 0) break;
/* 243 */       this.key[last] = this.key[pos];
/* 244 */       this.value[last] = this.value[pos];
/*     */     }
/* 246 */     this.used[last] = false;
/* 247 */     this.key[last] = null;
/* 248 */     return last;
/*     */   }
/*     */ 
/*     */   public char removeChar(Object k)
/*     */   {
/* 253 */     int pos = (k == null ? 142593372 : HashCommon.murmurHash3(System.identityHashCode(k))) & this.mask;
/*     */ 
/* 255 */     while (this.used[pos] != 0) {
/* 256 */       if (this.key[pos] == k) {
/* 257 */         this.size -= 1;
/* 258 */         char v = this.value[pos];
/* 259 */         shiftKeys(pos);
/* 260 */         return v;
/*     */       }
/* 262 */       pos = pos + 1 & this.mask;
/*     */     }
/* 264 */     return this.defRetValue;
/*     */   }
/*     */ 
/*     */   public Character remove(Object ok) {
/* 268 */     Object k = ok;
/*     */ 
/* 270 */     int pos = (k == null ? 142593372 : HashCommon.murmurHash3(System.identityHashCode(k))) & this.mask;
/*     */ 
/* 272 */     while (this.used[pos] != 0) {
/* 273 */       if (this.key[pos] == k) {
/* 274 */         this.size -= 1;
/* 275 */         char v = this.value[pos];
/* 276 */         shiftKeys(pos);
/* 277 */         return Character.valueOf(v);
/*     */       }
/* 279 */       pos = pos + 1 & this.mask;
/*     */     }
/* 281 */     return null;
/*     */   }
/*     */ 
/*     */   public char getChar(Object k)
/*     */   {
/* 286 */     int pos = (k == null ? 142593372 : HashCommon.murmurHash3(System.identityHashCode(k))) & this.mask;
/*     */ 
/* 288 */     while (this.used[pos] != 0) {
/* 289 */       if (this.key[pos] == k) return this.value[pos];
/* 290 */       pos = pos + 1 & this.mask;
/*     */     }
/* 292 */     return this.defRetValue;
/*     */   }
/*     */ 
/*     */   public boolean containsKey(Object k)
/*     */   {
/* 297 */     int pos = (k == null ? 142593372 : HashCommon.murmurHash3(System.identityHashCode(k))) & this.mask;
/*     */ 
/* 299 */     while (this.used[pos] != 0) {
/* 300 */       if (this.key[pos] == k) return true;
/* 301 */       pos = pos + 1 & this.mask;
/*     */     }
/* 303 */     return false;
/*     */   }
/*     */   public boolean containsValue(char v) {
/* 306 */     char[] value = this.value;
/* 307 */     boolean[] used = this.used;
/* 308 */     for (int i = this.n; i-- != 0; return true) label16: if ((used[i] == 0) || (value[i] != v))
/*     */         break label16; return false;
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 318 */     if (this.size == 0) return;
/* 319 */     this.size = 0;
/* 320 */     BooleanArrays.fill(this.used, false);
/*     */ 
/* 322 */     ObjectArrays.fill(this.key, null);
/*     */   }
/*     */   public int size() {
/* 325 */     return this.size;
/*     */   }
/*     */   public boolean isEmpty() {
/* 328 */     return this.size == 0;
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
/* 345 */     return 16;
/*     */   }
/*     */ 
/*     */   public Reference2CharMap.FastEntrySet<K> reference2CharEntrySet()
/*     */   {
/* 549 */     if (this.entries == null) this.entries = new MapEntrySet(null);
/* 550 */     return this.entries;
/*     */   }
/*     */ 
/*     */   public ReferenceSet<K> keySet()
/*     */   {
/* 582 */     if (this.keys == null) this.keys = new KeySet(null);
/* 583 */     return this.keys;
/*     */   }
/*     */ 
/*     */   public CharCollection values()
/*     */   {
/* 597 */     if (this.values == null) this.values = new AbstractCharCollection() {
/*     */         public CharIterator iterator() {
/* 599 */           return new Reference2CharOpenHashMap.ValueIterator(Reference2CharOpenHashMap.this);
/*     */         }
/*     */         public int size() {
/* 602 */           return Reference2CharOpenHashMap.this.size;
/*     */         }
/*     */         public boolean contains(char v) {
/* 605 */           return Reference2CharOpenHashMap.this.containsValue(v);
/*     */         }
/*     */         public void clear() {
/* 608 */           Reference2CharOpenHashMap.this.clear();
/*     */         }
/*     */       };
/* 611 */     return this.values;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public boolean rehash()
/*     */   {
/* 625 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean trim()
/*     */   {
/* 640 */     int l = HashCommon.arraySize(this.size, this.f);
/* 641 */     if (l >= this.n) return true; try
/*     */     {
/* 643 */       rehash(l);
/*     */     } catch (OutOfMemoryError cantDoIt) {
/* 645 */       return false;
/* 646 */     }return true;
/*     */   }
/*     */ 
/*     */   public boolean trim(int n)
/*     */   {
/* 667 */     int l = HashCommon.nextPowerOfTwo((int)Math.ceil(n / this.f));
/* 668 */     if (this.n <= l) return true; try
/*     */     {
/* 670 */       rehash(l);
/*     */     } catch (OutOfMemoryError cantDoIt) {
/* 672 */       return false;
/* 673 */     }return true;
/*     */   }
/*     */ 
/*     */   protected void rehash(int newN)
/*     */   {
/* 686 */     int i = 0;
/* 687 */     boolean[] used = this.used;
/*     */ 
/* 689 */     Object[] key = this.key;
/* 690 */     char[] value = this.value;
/* 691 */     int newMask = newN - 1;
/* 692 */     Object[] newKey = (Object[])new Object[newN];
/* 693 */     char[] newValue = new char[newN];
/* 694 */     boolean[] newUsed = new boolean[newN];
/* 695 */     for (int j = this.size; j-- != 0; ) {
/* 696 */       while (used[i] == 0) i++;
/* 697 */       Object k = key[i];
/* 698 */       int pos = (k == null ? 142593372 : HashCommon.murmurHash3(System.identityHashCode(k))) & newMask;
/* 699 */       while (newUsed[pos] != 0) pos = pos + 1 & newMask;
/* 700 */       newUsed[pos] = true;
/* 701 */       newKey[pos] = k;
/* 702 */       newValue[pos] = value[i];
/* 703 */       i++;
/*     */     }
/* 705 */     this.n = newN;
/* 706 */     this.mask = newMask;
/* 707 */     this.maxFill = HashCommon.maxFill(this.n, this.f);
/* 708 */     this.key = newKey;
/* 709 */     this.value = newValue;
/* 710 */     this.used = newUsed;
/*     */   }
/*     */ 
/*     */   public Reference2CharOpenHashMap<K> clone()
/*     */   {
/*     */     Reference2CharOpenHashMap c;
/*     */     try
/*     */     {
/* 723 */       c = (Reference2CharOpenHashMap)super.clone();
/*     */     }
/*     */     catch (CloneNotSupportedException cantHappen) {
/* 726 */       throw new InternalError();
/*     */     }
/* 728 */     c.keys = null;
/* 729 */     c.values = null;
/* 730 */     c.entries = null;
/* 731 */     c.key = ((Object[])this.key.clone());
/* 732 */     c.value = ((char[])this.value.clone());
/* 733 */     c.used = ((boolean[])this.used.clone());
/* 734 */     return c;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 746 */     int h = 0;
/* 747 */     int j = this.size; int i = 0; for (int t = 0; j-- != 0; ) {
/* 748 */       while (this.used[i] == 0) i++;
/* 749 */       if (this != this.key[i])
/* 750 */         t = this.key[i] == null ? 0 : System.identityHashCode(this.key[i]);
/* 751 */       t ^= this.value[i];
/* 752 */       h += t;
/* 753 */       i++;
/*     */     }
/* 755 */     return h;
/*     */   }
/*     */   private void writeObject(ObjectOutputStream s) throws IOException {
/* 758 */     Object[] key = this.key;
/* 759 */     char[] value = this.value;
/* 760 */     MapIterator i = new MapIterator(null);
/* 761 */     s.defaultWriteObject();
/* 762 */     for (int j = this.size; j-- != 0; ) {
/* 763 */       int e = i.nextEntry();
/* 764 */       s.writeObject(key[e]);
/* 765 */       s.writeChar(value[e]);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
/* 770 */     s.defaultReadObject();
/* 771 */     this.n = HashCommon.arraySize(this.size, this.f);
/* 772 */     this.maxFill = HashCommon.maxFill(this.n, this.f);
/* 773 */     this.mask = (this.n - 1);
/* 774 */     Object[] key = this.key = (Object[])new Object[this.n];
/* 775 */     char[] value = this.value = new char[this.n];
/* 776 */     boolean[] used = this.used = new boolean[this.n];
/*     */ 
/* 779 */     int i = this.size; for (int pos = 0; i-- != 0; ) {
/* 780 */       Object k = s.readObject();
/* 781 */       char v = s.readChar();
/* 782 */       pos = (k == null ? 142593372 : HashCommon.murmurHash3(System.identityHashCode(k))) & this.mask;
/* 783 */       while (used[pos] != 0) pos = pos + 1 & this.mask;
/* 784 */       used[pos] = true;
/* 785 */       key[pos] = k;
/* 786 */       value[pos] = v;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void checkTable()
/*     */   {
/*     */   }
/*     */ 
/*     */   private final class ValueIterator extends Reference2CharOpenHashMap.MapIterator
/*     */     implements CharIterator
/*     */   {
/*     */     public ValueIterator()
/*     */     {
/* 592 */       super(null); } 
/* 593 */     public char nextChar() { return Reference2CharOpenHashMap.this.value[nextEntry()]; } 
/* 594 */     public Character next() { return Character.valueOf(Reference2CharOpenHashMap.this.value[nextEntry()]); }
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
/* 564 */       return new Reference2CharOpenHashMap.KeyIterator(Reference2CharOpenHashMap.this);
/*     */     }
/*     */     public int size() {
/* 567 */       return Reference2CharOpenHashMap.this.size;
/*     */     }
/*     */     public boolean contains(Object k) {
/* 570 */       return Reference2CharOpenHashMap.this.containsKey(k);
/*     */     }
/*     */     public boolean remove(Object k) {
/* 573 */       int oldSize = Reference2CharOpenHashMap.this.size;
/* 574 */       Reference2CharOpenHashMap.this.remove(k);
/* 575 */       return Reference2CharOpenHashMap.this.size != oldSize;
/*     */     }
/*     */     public void clear() {
/* 578 */       Reference2CharOpenHashMap.this.clear();
/*     */     }
/*     */   }
/*     */ 
/*     */   private final class KeyIterator extends Reference2CharOpenHashMap<K>.MapIterator
/*     */     implements ObjectIterator<K>
/*     */   {
/*     */     public KeyIterator()
/*     */     {
/* 559 */       super(null); } 
/* 560 */     public K next() { return Reference2CharOpenHashMap.this.key[nextEntry()]; }
/*     */ 
/*     */   }
/*     */ 
/*     */   private final class MapEntrySet extends AbstractObjectSet<Reference2CharMap.Entry<K>>
/*     */     implements Reference2CharMap.FastEntrySet<K>
/*     */   {
/*     */     private MapEntrySet()
/*     */     {
/*     */     }
/*     */ 
/*     */     public ObjectIterator<Reference2CharMap.Entry<K>> iterator()
/*     */     {
/* 505 */       return new Reference2CharOpenHashMap.EntryIterator(Reference2CharOpenHashMap.this, null);
/*     */     }
/*     */     public ObjectIterator<Reference2CharMap.Entry<K>> fastIterator() {
/* 508 */       return new Reference2CharOpenHashMap.FastEntryIterator(Reference2CharOpenHashMap.this, null);
/*     */     }
/*     */ 
/*     */     public boolean contains(Object o) {
/* 512 */       if (!(o instanceof Map.Entry)) return false;
/* 513 */       Map.Entry e = (Map.Entry)o;
/* 514 */       Object k = e.getKey();
/*     */ 
/* 516 */       int pos = (k == null ? 142593372 : HashCommon.murmurHash3(System.identityHashCode(k))) & Reference2CharOpenHashMap.this.mask;
/*     */ 
/* 518 */       while (Reference2CharOpenHashMap.this.used[pos] != 0) {
/* 519 */         if (Reference2CharOpenHashMap.this.key[pos] == k) return Reference2CharOpenHashMap.this.value[pos] == ((Character)e.getValue()).charValue();
/* 520 */         pos = pos + 1 & Reference2CharOpenHashMap.this.mask;
/*     */       }
/* 522 */       return false;
/*     */     }
/*     */ 
/*     */     public boolean remove(Object o) {
/* 526 */       if (!(o instanceof Map.Entry)) return false;
/* 527 */       Map.Entry e = (Map.Entry)o;
/* 528 */       Object k = e.getKey();
/*     */ 
/* 530 */       int pos = (k == null ? 142593372 : HashCommon.murmurHash3(System.identityHashCode(k))) & Reference2CharOpenHashMap.this.mask;
/*     */ 
/* 532 */       while (Reference2CharOpenHashMap.this.used[pos] != 0) {
/* 533 */         if (Reference2CharOpenHashMap.this.key[pos] == k) {
/* 534 */           Reference2CharOpenHashMap.this.remove(e.getKey());
/* 535 */           return true;
/*     */         }
/* 537 */         pos = pos + 1 & Reference2CharOpenHashMap.this.mask;
/*     */       }
/* 539 */       return false;
/*     */     }
/*     */     public int size() {
/* 542 */       return Reference2CharOpenHashMap.this.size;
/*     */     }
/*     */     public void clear() {
/* 545 */       Reference2CharOpenHashMap.this.clear();
/*     */     }
/*     */   }
/*     */ 
/*     */   private class FastEntryIterator extends Reference2CharOpenHashMap<K>.MapIterator
/*     */     implements ObjectIterator<Reference2CharMap.Entry<K>>
/*     */   {
/* 495 */     final AbstractReference2CharMap.BasicEntry<K> entry = new AbstractReference2CharMap.BasicEntry(null, '\000');
/*     */ 
/*     */     private FastEntryIterator()
/*     */     {
/* 494 */       super(null);
/*     */     }
/*     */     public AbstractReference2CharMap.BasicEntry<K> next() {
/* 497 */       int e = nextEntry();
/* 498 */       this.entry.key = Reference2CharOpenHashMap.this.key[e];
/* 499 */       this.entry.value = Reference2CharOpenHashMap.this.value[e];
/* 500 */       return this.entry;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class EntryIterator extends Reference2CharOpenHashMap<K>.MapIterator
/*     */     implements ObjectIterator<Reference2CharMap.Entry<K>>
/*     */   {
/*     */     private Reference2CharOpenHashMap<K>.MapEntry entry;
/*     */ 
/*     */     private EntryIterator()
/*     */     {
/* 483 */       super(null);
/*     */     }
/*     */     public Reference2CharMap.Entry<K> next() {
/* 486 */       return this.entry = new Reference2CharOpenHashMap.MapEntry(Reference2CharOpenHashMap.this, nextEntry());
/*     */     }
/*     */ 
/*     */     public void remove() {
/* 490 */       super.remove();
/* 491 */       Reference2CharOpenHashMap.MapEntry.access$102(this.entry, -1);
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
/* 391 */       this.pos = Reference2CharOpenHashMap.this.n;
/*     */ 
/* 394 */       this.last = -1;
/*     */ 
/* 396 */       this.c = Reference2CharOpenHashMap.this.size;
/*     */ 
/* 401 */       boolean[] used = Reference2CharOpenHashMap.this.used;
/* 402 */       while ((this.c != 0) && (used[(--this.pos)] == 0));
/*     */     }
/*     */ 
/*     */     public boolean hasNext()
/*     */     {
/* 405 */       return this.c != 0;
/*     */     }
/*     */     public int nextEntry() {
/* 408 */       if (!hasNext()) throw new NoSuchElementException();
/* 409 */       this.c -= 1;
/*     */ 
/* 411 */       if (this.pos < 0) {
/* 412 */         Object k = this.wrapped.get(-(this.last = --this.pos) - 2);
/*     */ 
/* 414 */         int pos = (k == null ? 142593372 : HashCommon.murmurHash3(System.identityHashCode(k))) & Reference2CharOpenHashMap.this.mask;
/*     */ 
/* 416 */         while (Reference2CharOpenHashMap.this.used[pos] != 0) {
/* 417 */           if (Reference2CharOpenHashMap.this.key[pos] == k) return pos;
/* 418 */           pos = pos + 1 & Reference2CharOpenHashMap.this.mask;
/*     */         }
/*     */       }
/* 421 */       this.last = this.pos;
/*     */ 
/* 423 */       if (this.c != 0) {
/* 424 */         boolean[] used = Reference2CharOpenHashMap.this.used;
/* 425 */         while ((this.pos-- != 0) && (used[this.pos] == 0));
/*     */       }
/* 428 */       return this.last;
/*     */     }
/*     */ 
/*     */     protected final int shiftKeys(int pos)
/*     */     {
/*     */       int last;
/*     */       while (true)
/*     */       {
/* 441 */         pos = (last = pos) + 1 & Reference2CharOpenHashMap.this.mask;
/* 442 */         while (Reference2CharOpenHashMap.this.used[pos] != 0) {
/* 443 */           int slot = (Reference2CharOpenHashMap.this.key[pos] == null ? 142593372 : HashCommon.murmurHash3(System.identityHashCode(Reference2CharOpenHashMap.this.key[pos]))) & Reference2CharOpenHashMap.this.mask;
/* 444 */           if (last <= pos ? (last < slot) && (slot <= pos) : (last >= slot) && (slot > pos)) break;
/* 445 */           pos = pos + 1 & Reference2CharOpenHashMap.this.mask;
/*     */         }
/* 447 */         if (Reference2CharOpenHashMap.this.used[pos] == 0) break;
/* 448 */         if (pos < last)
/*     */         {
/* 450 */           if (this.wrapped == null) this.wrapped = new ReferenceArrayList();
/* 451 */           this.wrapped.add(Reference2CharOpenHashMap.this.key[pos]);
/*     */         }
/* 453 */         Reference2CharOpenHashMap.this.key[last] = Reference2CharOpenHashMap.this.key[pos];
/* 454 */         Reference2CharOpenHashMap.this.value[last] = Reference2CharOpenHashMap.this.value[pos];
/*     */       }
/* 456 */       Reference2CharOpenHashMap.this.used[last] = false;
/* 457 */       Reference2CharOpenHashMap.this.key[last] = null;
/* 458 */       return last;
/*     */     }
/*     */ 
/*     */     public void remove() {
/* 462 */       if (this.last == -1) throw new IllegalStateException();
/* 463 */       if (this.pos < -1)
/*     */       {
/* 465 */         Reference2CharOpenHashMap.this.remove(this.wrapped.set(-this.pos - 2, null));
/* 466 */         this.last = -1;
/* 467 */         return;
/*     */       }
/* 469 */       Reference2CharOpenHashMap.this.size -= 1;
/* 470 */       if ((shiftKeys(this.last) == this.pos) && (this.c > 0)) {
/* 471 */         this.c += 1;
/* 472 */         nextEntry();
/*     */       }
/* 474 */       this.last = -1;
/*     */     }
/*     */ 
/*     */     public int skip(int n) {
/* 478 */       int i = n;
/* 479 */       while ((i-- != 0) && (hasNext())) nextEntry();
/* 480 */       return n - i - 1;
/*     */     }
/*     */   }
/*     */ 
/*     */   private final class MapEntry
/*     */     implements Reference2CharMap.Entry<K>, Map.Entry<K, Character>
/*     */   {
/*     */     private int index;
/*     */ 
/*     */     MapEntry(int index)
/*     */     {
/* 355 */       this.index = index;
/*     */     }
/*     */     public K getKey() {
/* 358 */       return Reference2CharOpenHashMap.this.key[this.index];
/*     */     }
/*     */     public Character getValue() {
/* 361 */       return Character.valueOf(Reference2CharOpenHashMap.this.value[this.index]);
/*     */     }
/*     */     public char getCharValue() {
/* 364 */       return Reference2CharOpenHashMap.this.value[this.index];
/*     */     }
/*     */     public char setValue(char v) {
/* 367 */       char oldValue = Reference2CharOpenHashMap.this.value[this.index];
/* 368 */       Reference2CharOpenHashMap.this.value[this.index] = v;
/* 369 */       return oldValue;
/*     */     }
/*     */     public Character setValue(Character v) {
/* 372 */       return Character.valueOf(setValue(v.charValue()));
/*     */     }
/*     */ 
/*     */     public boolean equals(Object o) {
/* 376 */       if (!(o instanceof Map.Entry)) return false;
/* 377 */       Map.Entry e = (Map.Entry)o;
/* 378 */       return (Reference2CharOpenHashMap.this.key[this.index] == e.getKey()) && (Reference2CharOpenHashMap.this.value[this.index] == ((Character)e.getValue()).charValue());
/*     */     }
/*     */     public int hashCode() {
/* 381 */       return (Reference2CharOpenHashMap.this.key[this.index] == null ? 0 : System.identityHashCode(Reference2CharOpenHashMap.this.key[this.index])) ^ Reference2CharOpenHashMap.this.value[this.index];
/*     */     }
/*     */     public String toString() {
/* 384 */       return Reference2CharOpenHashMap.this.key[this.index] + "=>" + Reference2CharOpenHashMap.this.value[this.index];
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Raul\Desktop\StarMade\StarMade.jar
 * Qualified Name:     it.unimi.dsi.fastutil.objects.Reference2CharOpenHashMap
 * JD-Core Version:    0.6.2
 */