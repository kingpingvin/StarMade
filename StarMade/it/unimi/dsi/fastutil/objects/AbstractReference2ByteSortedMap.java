/*     */ package it.unimi.dsi.fastutil.objects;
/*     */ 
/*     */ import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
/*     */ import it.unimi.dsi.fastutil.bytes.AbstractByteIterator;
/*     */ import it.unimi.dsi.fastutil.bytes.ByteCollection;
/*     */ import it.unimi.dsi.fastutil.bytes.ByteIterator;
/*     */ import java.util.Comparator;
/*     */ import java.util.Map.Entry;
/*     */ 
/*     */ public abstract class AbstractReference2ByteSortedMap<K> extends AbstractReference2ByteMap<K>
/*     */   implements Reference2ByteSortedMap<K>
/*     */ {
/*     */   public static final long serialVersionUID = -1773560792952436569L;
/*     */ 
/*     */   public ReferenceSortedSet<K> keySet()
/*     */   {
/*  69 */     return new KeySet();
/*     */   }
/*     */ 
/*     */   public ByteCollection values()
/*     */   {
/* 112 */     return new ValuesCollection();
/*     */   }
/*     */ 
/*     */   public ObjectSortedSet<Map.Entry<K, Byte>> entrySet()
/*     */   {
/* 136 */     return reference2ByteEntrySet();
/*     */   }
/*     */ 
/*     */   protected static class ValuesIterator<K> extends AbstractByteIterator
/*     */   {
/*     */     protected final ObjectBidirectionalIterator<Map.Entry<K, Byte>> i;
/*     */ 
/*     */     public ValuesIterator(ObjectBidirectionalIterator<Map.Entry<K, Byte>> i)
/*     */     {
/* 129 */       this.i = i;
/*     */     }
/* 131 */     public byte nextByte() { return ((Byte)((Map.Entry)this.i.next()).getValue()).byteValue(); } 
/* 132 */     public boolean hasNext() { return this.i.hasNext(); }
/*     */ 
/*     */   }
/*     */ 
/*     */   protected class ValuesCollection extends AbstractByteCollection
/*     */   {
/*     */     protected ValuesCollection()
/*     */     {
/*     */     }
/*     */ 
/*     */     public ByteIterator iterator()
/*     */     {
/* 116 */       return new AbstractReference2ByteSortedMap.ValuesIterator(AbstractReference2ByteSortedMap.this.entrySet().iterator()); } 
/* 117 */     public boolean contains(byte k) { return AbstractReference2ByteSortedMap.this.containsValue(k); } 
/* 118 */     public int size() { return AbstractReference2ByteSortedMap.this.size(); } 
/* 119 */     public void clear() { AbstractReference2ByteSortedMap.this.clear(); }
/*     */ 
/*     */   }
/*     */ 
/*     */   protected static class KeySetIterator<K> extends AbstractObjectBidirectionalIterator<K>
/*     */   {
/*     */     protected final ObjectBidirectionalIterator<Map.Entry<K, Byte>> i;
/*     */ 
/*     */     public KeySetIterator(ObjectBidirectionalIterator<Map.Entry<K, Byte>> i)
/*     */     {
/*  93 */       this.i = i;
/*     */     }
/*  95 */     public K next() { return ((Map.Entry)this.i.next()).getKey(); } 
/*  96 */     public K previous() { return ((Map.Entry)this.i.previous()).getKey(); } 
/*  97 */     public boolean hasNext() { return this.i.hasNext(); } 
/*  98 */     public boolean hasPrevious() { return this.i.hasPrevious(); }
/*     */ 
/*     */   }
/*     */ 
/*     */   protected class KeySet extends AbstractReferenceSortedSet<K>
/*     */   {
/*     */     protected KeySet()
/*     */     {
/*     */     }
/*     */ 
/*     */     public boolean contains(Object k)
/*     */     {
/*  73 */       return AbstractReference2ByteSortedMap.this.containsKey(k); } 
/*  74 */     public int size() { return AbstractReference2ByteSortedMap.this.size(); } 
/*  75 */     public void clear() { AbstractReference2ByteSortedMap.this.clear(); } 
/*  76 */     public Comparator<? super K> comparator() { return AbstractReference2ByteSortedMap.this.comparator(); } 
/*  77 */     public K first() { return AbstractReference2ByteSortedMap.this.firstKey(); } 
/*  78 */     public K last() { return AbstractReference2ByteSortedMap.this.lastKey(); } 
/*  79 */     public ReferenceSortedSet<K> headSet(K to) { return AbstractReference2ByteSortedMap.this.headMap(to).keySet(); } 
/*  80 */     public ReferenceSortedSet<K> tailSet(K from) { return AbstractReference2ByteSortedMap.this.tailMap(from).keySet(); } 
/*  81 */     public ReferenceSortedSet<K> subSet(K from, K to) { return AbstractReference2ByteSortedMap.this.subMap(from, to).keySet(); } 
/*  82 */     public ObjectBidirectionalIterator<K> iterator(K from) { return new AbstractReference2ByteSortedMap.KeySetIterator(AbstractReference2ByteSortedMap.this.entrySet().iterator(new AbstractReference2ByteMap.BasicEntry(from, (byte)0))); } 
/*  83 */     public ObjectBidirectionalIterator<K> iterator() { return new AbstractReference2ByteSortedMap.KeySetIterator(AbstractReference2ByteSortedMap.this.entrySet().iterator()); }
/*     */ 
/*     */   }
/*     */ }

/* Location:           C:\Users\Raul\Desktop\StarMade\StarMade.jar
 * Qualified Name:     it.unimi.dsi.fastutil.objects.AbstractReference2ByteSortedMap
 * JD-Core Version:    0.6.2
 */