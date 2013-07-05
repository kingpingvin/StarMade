/*     */ package it.unimi.dsi.fastutil.doubles;
/*     */ 
/*     */ import it.unimi.dsi.fastutil.ints.IntArraySet;
/*     */ import it.unimi.dsi.fastutil.ints.IntArrays;
/*     */ import it.unimi.dsi.fastutil.ints.IntCollection;
/*     */ import it.unimi.dsi.fastutil.ints.IntCollections;
/*     */ import it.unimi.dsi.fastutil.objects.AbstractObjectIterator;
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
/*     */ public class Double2IntArrayMap extends AbstractDouble2IntMap
/*     */   implements Serializable, Cloneable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private transient double[] key;
/*     */   private transient int[] value;
/*     */   private int size;
/*     */ 
/*     */   public Double2IntArrayMap(double[] key, int[] value)
/*     */   {
/*  77 */     this.key = key;
/*  78 */     this.value = value;
/*  79 */     this.size = key.length;
/*  80 */     if (key.length != value.length) throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
/*     */   }
/*     */ 
/*     */   public Double2IntArrayMap()
/*     */   {
/*  85 */     this.key = DoubleArrays.EMPTY_ARRAY;
/*  86 */     this.value = IntArrays.EMPTY_ARRAY;
/*     */   }
/*     */ 
/*     */   public Double2IntArrayMap(int capacity)
/*     */   {
/*  93 */     this.key = new double[capacity];
/*  94 */     this.value = new int[capacity];
/*     */   }
/*     */ 
/*     */   public Double2IntArrayMap(Double2IntMap m)
/*     */   {
/* 101 */     this(m.size());
/* 102 */     putAll(m);
/*     */   }
/*     */ 
/*     */   public Double2IntArrayMap(Map<? extends Double, ? extends Integer> m)
/*     */   {
/* 109 */     this(m.size());
/* 110 */     putAll(m);
/*     */   }
/*     */ 
/*     */   public Double2IntArrayMap(double[] key, int[] value, int size)
/*     */   {
/* 121 */     this.key = key;
/* 122 */     this.value = value;
/* 123 */     this.size = size;
/* 124 */     if (key.length != value.length) throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
/* 125 */     if (size > key.length) throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the backing-arrays size (" + key.length + ")");
/*     */   }
/*     */ 
/*     */   public Double2IntMap.FastEntrySet double2IntEntrySet()
/*     */   {
/* 170 */     return new EntrySet(null);
/*     */   }
/*     */   private int findKey(double k) {
/* 173 */     double[] key = this.key;
/* 174 */     for (int i = this.size; i-- != 0; ) if (key[i] == k) return i;
/* 175 */     return -1;
/*     */   }
/*     */ 
/*     */   public int get(double k)
/*     */   {
/* 184 */     double[] key = this.key;
/* 185 */     for (int i = this.size; i-- != 0; ) if (key[i] == k) return this.value[i];
/* 186 */     return this.defRetValue;
/*     */   }
/*     */ 
/*     */   public int size() {
/* 190 */     return this.size;
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 195 */     this.size = 0;
/*     */   }
/*     */ 
/*     */   public boolean containsKey(double k) {
/* 199 */     return findKey(k) != -1;
/*     */   }
/*     */ 
/*     */   public boolean containsValue(int v)
/*     */   {
/* 204 */     for (int i = this.size; i-- != 0; ) if (this.value[i] == v) return true;
/* 205 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isEmpty() {
/* 209 */     return this.size == 0;
/*     */   }
/*     */ 
/*     */   public int put(double k, int v)
/*     */   {
/* 214 */     int oldKey = findKey(k);
/* 215 */     if (oldKey != -1) {
/* 216 */       int oldValue = this.value[oldKey];
/* 217 */       this.value[oldKey] = v;
/* 218 */       return oldValue;
/*     */     }
/* 220 */     if (this.size == this.key.length) {
/* 221 */       double[] newKey = new double[this.size == 0 ? 2 : this.size * 2];
/* 222 */       int[] newValue = new int[this.size == 0 ? 2 : this.size * 2];
/* 223 */       for (int i = this.size; i-- != 0; ) {
/* 224 */         newKey[i] = this.key[i];
/* 225 */         newValue[i] = this.value[i];
/*     */       }
/* 227 */       this.key = newKey;
/* 228 */       this.value = newValue;
/*     */     }
/* 230 */     this.key[this.size] = k;
/* 231 */     this.value[this.size] = v;
/* 232 */     this.size += 1;
/* 233 */     return this.defRetValue;
/*     */   }
/*     */ 
/*     */   public int remove(double k)
/*     */   {
/* 238 */     int oldPos = findKey(k);
/* 239 */     if (oldPos == -1) return this.defRetValue;
/* 240 */     int oldValue = this.value[oldPos];
/* 241 */     int tail = this.size - oldPos - 1;
/* 242 */     for (int i = 0; i < tail; i++) {
/* 243 */       this.key[(oldPos + i)] = this.key[(oldPos + i + 1)];
/* 244 */       this.value[(oldPos + i)] = this.value[(oldPos + i + 1)];
/*     */     }
/* 246 */     this.size -= 1;
/* 247 */     return oldValue;
/*     */   }
/*     */ 
/*     */   public DoubleSet keySet()
/*     */   {
/* 252 */     return new DoubleArraySet(this.key, this.size);
/*     */   }
/*     */ 
/*     */   public IntCollection values() {
/* 256 */     return IntCollections.unmodifiable(new IntArraySet(this.value, this.size));
/*     */   }
/*     */ 
/*     */   public Double2IntArrayMap clone()
/*     */   {
/*     */     Double2IntArrayMap c;
/*     */     try
/*     */     {
/* 269 */       c = (Double2IntArrayMap)super.clone();
/*     */     }
/*     */     catch (CloneNotSupportedException cantHappen) {
/* 272 */       throw new InternalError();
/*     */     }
/* 274 */     c.key = ((double[])this.key.clone());
/* 275 */     c.value = ((int[])this.value.clone());
/* 276 */     return c;
/*     */   }
/*     */   private void writeObject(ObjectOutputStream s) throws IOException {
/* 279 */     s.defaultWriteObject();
/* 280 */     for (int i = 0; i < this.size; i++) {
/* 281 */       s.writeDouble(this.key[i]);
/* 282 */       s.writeInt(this.value[i]);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
/* 287 */     s.defaultReadObject();
/* 288 */     this.key = new double[this.size];
/* 289 */     this.value = new int[this.size];
/* 290 */     for (int i = 0; i < this.size; i++) {
/* 291 */       this.key[i] = s.readDouble();
/* 292 */       this.value[i] = s.readInt();
/*     */     }
/*     */   }
/*     */ 
/*     */   private final class EntrySet extends AbstractObjectSet<Double2IntMap.Entry>
/*     */     implements Double2IntMap.FastEntrySet
/*     */   {
/*     */     private EntrySet()
/*     */     {
/*     */     }
/*     */ 
/*     */     public ObjectIterator<Double2IntMap.Entry> iterator()
/*     */     {
/* 130 */       return new AbstractObjectIterator() {
/* 131 */         int next = 0;
/*     */ 
/* 133 */         public boolean hasNext() { return this.next < Double2IntArrayMap.this.size; }
/*     */ 
/*     */         public Double2IntMap.Entry next()
/*     */         {
/* 137 */           if (!hasNext()) throw new NoSuchElementException();
/* 138 */           return new AbstractDouble2IntMap.BasicEntry(Double2IntArrayMap.this.key[this.next], Double2IntArrayMap.this.value[(this.next++)]);
/*     */         } } ;
/*     */     }
/*     */ 
/*     */     public ObjectIterator<Double2IntMap.Entry> fastIterator() {
/* 143 */       return new AbstractObjectIterator() {
/* 144 */         int next = 0;
/* 145 */         final AbstractDouble2IntMap.BasicEntry entry = new AbstractDouble2IntMap.BasicEntry(0.0D, 0);
/*     */ 
/* 147 */         public boolean hasNext() { return this.next < Double2IntArrayMap.this.size; }
/*     */ 
/*     */         public Double2IntMap.Entry next()
/*     */         {
/* 151 */           if (!hasNext()) throw new NoSuchElementException();
/* 152 */           this.entry.key = Double2IntArrayMap.this.key[this.next];
/* 153 */           this.entry.value = Double2IntArrayMap.this.value[(this.next++)];
/* 154 */           return this.entry;
/*     */         } } ;
/*     */     }
/*     */ 
/*     */     public int size() {
/* 159 */       return Double2IntArrayMap.this.size;
/*     */     }
/*     */ 
/*     */     public boolean contains(Object o) {
/* 163 */       if (!(o instanceof Map.Entry)) return false;
/* 164 */       Map.Entry e = (Map.Entry)o;
/* 165 */       double k = ((Double)e.getKey()).doubleValue();
/* 166 */       return (Double2IntArrayMap.this.containsKey(k)) && (Double2IntArrayMap.this.get(k) == ((Integer)e.getValue()).intValue());
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Raul\Desktop\StarMade\StarMade.jar
 * Qualified Name:     it.unimi.dsi.fastutil.doubles.Double2IntArrayMap
 * JD-Core Version:    0.6.2
 */