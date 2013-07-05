/*     */ package it.unimi.dsi.fastutil.objects;
/*     */ 
/*     */ import java.util.Comparator;
/*     */ 
/*     */ public class ObjectHeaps
/*     */ {
/*     */   public static <K> int downHeap(K[] heap, int size, int i, Comparator<? super K> c)
/*     */   {
/*  64 */     if (i >= size) throw new IllegalArgumentException("Heap position (" + i + ") is larger than or equal to heap size (" + size + ")");
/*  65 */     Object e = heap[i];
/*     */ 
/*  67 */     if (c == null)
/*     */     {
/*     */       int child;
/*  68 */       while ((child = 2 * i + 1) < size) {
/*  69 */         if ((child + 1 < size) && (((Comparable)heap[(child + 1)]).compareTo(heap[child]) < 0)) child++;
/*  70 */         if (((Comparable)e).compareTo(heap[child]) <= 0) break;
/*  71 */         heap[i] = heap[child];
/*  72 */         i = child;
/*     */       }
/*     */     }
/*     */     int child;
/*  75 */     while ((child = 2 * i + 1) < size) {
/*  76 */       if ((child + 1 < size) && (c.compare(heap[(child + 1)], heap[child]) < 0)) child++;
/*  77 */       if (c.compare(e, heap[child]) <= 0) break;
/*  78 */       heap[i] = heap[child];
/*  79 */       i = child;
/*     */     }
/*  81 */     heap[i] = e;
/*  82 */     return i;
/*     */   }
/*     */ 
/*     */   public static <K> int upHeap(K[] heap, int size, int i, Comparator<K> c)
/*     */   {
/*  94 */     if (i >= size) throw new IllegalArgumentException("Heap position (" + i + ") is larger than or equal to heap size (" + size + ")");
/*  95 */     Object e = heap[i];
/*     */ 
/*  97 */     if (c == null)
/*     */     {
/*     */       int parent;
/*  98 */       while ((i != 0) && ((parent = (i - 1) / 2) >= 0) && 
/*  99 */         (((Comparable)heap[parent]).compareTo(e) > 0)) {
/* 100 */         heap[i] = heap[parent];
/* 101 */         i = parent;
/*     */       }
/*     */     }
/*     */     int parent;
/* 104 */     while ((i != 0) && ((parent = (i - 1) / 2) >= 0) && 
/* 105 */       (c.compare(heap[parent], e) > 0)) {
/* 106 */       heap[i] = heap[parent];
/* 107 */       i = parent;
/*     */     }
/* 109 */     heap[i] = e;
/* 110 */     return i;
/*     */   }
/*     */ 
/*     */   public static <K> void makeHeap(K[] heap, int size, Comparator<K> c)
/*     */   {
/* 119 */     int i = size / 2;
/* 120 */     while (i-- != 0) downHeap(heap, size, i, c);
/*     */   }
/*     */ }

/* Location:           C:\Users\Raul\Desktop\StarMade\StarMade.jar
 * Qualified Name:     it.unimi.dsi.fastutil.objects.ObjectHeaps
 * JD-Core Version:    0.6.2
 */