/*      */ package it.unimi.dsi.fastutil.ints;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.ObjectOutputStream;
/*      */ import java.io.Serializable;
/*      */ import java.util.Collection;
/*      */ import java.util.Comparator;
/*      */ import java.util.Iterator;
/*      */ import java.util.NoSuchElementException;
/*      */ import java.util.SortedSet;
/*      */ 
/*      */ public class IntAVLTreeSet extends AbstractIntSortedSet
/*      */   implements Serializable, Cloneable, IntSortedSet
/*      */ {
/*      */   protected transient Entry tree;
/*      */   protected int count;
/*      */   protected transient Entry firstEntry;
/*      */   protected transient Entry lastEntry;
/*      */   protected Comparator<? super Integer> storedComparator;
/*      */   protected transient IntComparator actualComparator;
/*      */   public static final long serialVersionUID = -7046029254386353130L;
/*      */   private static final boolean ASSERTS = false;
/*      */   private transient boolean[] dirPath;
/*      */ 
/*      */   public IntAVLTreeSet()
/*      */   {
/*   74 */     allocatePaths();
/*      */ 
/*   79 */     this.tree = null;
/*   80 */     this.count = 0;
/*      */   }
/*      */ 
/*      */   private void setActualComparator()
/*      */   {
/*   94 */     if ((this.storedComparator == null) || ((this.storedComparator instanceof IntComparator))) this.actualComparator = ((IntComparator)this.storedComparator); else
/*   95 */       this.actualComparator = new IntComparator() {
/*      */         public int compare(int k1, int k2) {
/*   97 */           return IntAVLTreeSet.this.storedComparator.compare(Integer.valueOf(k1), Integer.valueOf(k2));
/*      */         }
/*      */         public int compare(Integer ok1, Integer ok2) {
/*  100 */           return IntAVLTreeSet.this.storedComparator.compare(ok1, ok2);
/*      */         }
/*      */       };
/*      */   }
/*      */ 
/*      */   public IntAVLTreeSet(Comparator<? super Integer> c)
/*      */   {
/*  109 */     this();
/*  110 */     this.storedComparator = c;
/*  111 */     setActualComparator();
/*      */   }
/*      */ 
/*      */   public IntAVLTreeSet(Collection<? extends Integer> c)
/*      */   {
/*  119 */     this();
/*  120 */     addAll(c);
/*      */   }
/*      */ 
/*      */   public IntAVLTreeSet(SortedSet<Integer> s)
/*      */   {
/*  129 */     this(s.comparator());
/*  130 */     addAll(s);
/*      */   }
/*      */ 
/*      */   public IntAVLTreeSet(IntCollection c)
/*      */   {
/*  139 */     this();
/*  140 */     addAll(c);
/*      */   }
/*      */ 
/*      */   public IntAVLTreeSet(IntSortedSet s)
/*      */   {
/*  149 */     this(s.comparator());
/*  150 */     addAll(s);
/*      */   }
/*      */ 
/*      */   public IntAVLTreeSet(IntIterator i)
/*      */   {
/*   74 */     allocatePaths();
/*      */ 
/*  160 */     while (i.hasNext()) add(i.nextInt());
/*      */   }
/*      */ 
/*      */   public IntAVLTreeSet(Iterator<? extends Integer> i)
/*      */   {
/*  171 */     this(IntIterators.asIntIterator(i));
/*      */   }
/*      */ 
/*      */   public IntAVLTreeSet(int[] a, int offset, int length, Comparator<? super Integer> c)
/*      */   {
/*  184 */     this(c);
/*  185 */     IntArrays.ensureOffsetLength(a, offset, length);
/*  186 */     for (int i = 0; i < length; i++) add(a[(offset + i)]);
/*      */   }
/*      */ 
/*      */   public IntAVLTreeSet(int[] a, int offset, int length)
/*      */   {
/*  198 */     this(a, offset, length, null);
/*      */   }
/*      */ 
/*      */   public IntAVLTreeSet(int[] a)
/*      */   {
/*  208 */     this();
/*  209 */     int i = a.length;
/*  210 */     while (i-- != 0) add(a[i]);
/*      */   }
/*      */ 
/*      */   public IntAVLTreeSet(int[] a, Comparator<? super Integer> c)
/*      */   {
/*  221 */     this(c);
/*  222 */     int i = a.length;
/*  223 */     while (i-- != 0) add(a[i]);
/*      */   }
/*      */ 
/*      */   final int compare(int k1, int k2)
/*      */   {
/*  253 */     return this.actualComparator == null ? 1 : k1 == k2 ? 0 : k1 < k2 ? -1 : this.actualComparator.compare(k1, k2);
/*      */   }
/*      */ 
/*      */   private Entry findKey(int k)
/*      */   {
/*  265 */     Entry e = this.tree;
/*      */     int cmp;
/*  268 */     while ((e != null) && ((cmp = compare(k, e.key)) != 0)) {
/*  269 */       e = cmp < 0 ? e.left() : e.right();
/*      */     }
/*  271 */     return e;
/*      */   }
/*      */ 
/*      */   final Entry locateKey(int k)
/*      */   {
/*  282 */     Entry e = this.tree; Entry last = this.tree;
/*  283 */     int cmp = 0;
/*      */ 
/*  285 */     while ((e != null) && ((cmp = compare(k, e.key)) != 0)) {
/*  286 */       last = e;
/*  287 */       e = cmp < 0 ? e.left() : e.right();
/*      */     }
/*      */ 
/*  290 */     return cmp == 0 ? e : last;
/*      */   }
/*      */ 
/*      */   private void allocatePaths()
/*      */   {
/*  299 */     this.dirPath = new boolean[48];
/*      */   }
/*      */ 
/*      */   public boolean add(int k)
/*      */   {
/*  305 */     if (this.tree == null) {
/*  306 */       this.count += 1;
/*  307 */       this.tree = (this.lastEntry = this.firstEntry = new Entry(k));
/*      */     }
/*      */     else {
/*  310 */       Entry p = this.tree; Entry q = null; Entry y = this.tree; Entry z = null; Entry e = null; Entry w = null;
/*  311 */       int i = 0;
/*      */       while (true)
/*      */       {
/*  314 */         int cmp;
/*  314 */         if ((cmp = compare(k, p.key)) == 0) return false;
/*      */ 
/*  316 */         if (p.balance() != 0) {
/*  317 */           i = 0;
/*  318 */           z = q;
/*  319 */           y = p;
/*      */         }
/*      */ 
/*  322 */         if ((this.dirPath[(i++)] = cmp > 0 ? 1 : 0) != 0) {
/*  323 */           if (p.succ()) {
/*  324 */             this.count += 1;
/*  325 */             e = new Entry(k);
/*      */ 
/*  327 */             if (p.right == null) this.lastEntry = e;
/*      */ 
/*  329 */             e.left = p;
/*  330 */             e.right = p.right;
/*      */ 
/*  332 */             p.right(e);
/*      */ 
/*  334 */             break;
/*      */           }
/*      */ 
/*  337 */           q = p;
/*  338 */           p = p.right;
/*      */         }
/*      */         else {
/*  341 */           if (p.pred()) {
/*  342 */             this.count += 1;
/*  343 */             e = new Entry(k);
/*      */ 
/*  345 */             if (p.left == null) this.firstEntry = e;
/*      */ 
/*  347 */             e.right = p;
/*  348 */             e.left = p.left;
/*      */ 
/*  350 */             p.left(e);
/*      */ 
/*  352 */             break;
/*      */           }
/*      */ 
/*  355 */           q = p;
/*  356 */           p = p.left;
/*      */         }
/*      */       }
/*      */ 
/*  360 */       p = y;
/*  361 */       i = 0;
/*      */ 
/*  363 */       while (p != e) {
/*  364 */         if (this.dirPath[i] != 0) p.incBalance(); else {
/*  365 */           p.decBalance();
/*      */         }
/*  367 */         p = this.dirPath[(i++)] != 0 ? p.right : p.left;
/*      */       }
/*      */ 
/*  370 */       if (y.balance() == -2) {
/*  371 */         Entry x = y.left;
/*      */ 
/*  373 */         if (x.balance() == -1) {
/*  374 */           w = x;
/*  375 */           if (x.succ()) {
/*  376 */             x.succ(false);
/*  377 */             y.pred(x);
/*      */           } else {
/*  379 */             y.left = x.right;
/*      */           }
/*  381 */           x.right = y;
/*  382 */           x.balance(0);
/*  383 */           y.balance(0);
/*      */         }
/*      */         else
/*      */         {
/*  388 */           w = x.right;
/*  389 */           x.right = w.left;
/*  390 */           w.left = x;
/*  391 */           y.left = w.right;
/*  392 */           w.right = y;
/*  393 */           if (w.balance() == -1) {
/*  394 */             x.balance(0);
/*  395 */             y.balance(1);
/*      */           }
/*  397 */           else if (w.balance() == 0) {
/*  398 */             x.balance(0);
/*  399 */             y.balance(0);
/*      */           }
/*      */           else {
/*  402 */             x.balance(-1);
/*  403 */             y.balance(0);
/*      */           }
/*  405 */           w.balance(0);
/*      */ 
/*  408 */           if (w.pred()) {
/*  409 */             x.succ(w);
/*  410 */             w.pred(false);
/*      */           }
/*  412 */           if (w.succ()) {
/*  413 */             y.pred(w);
/*  414 */             w.succ(false);
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*  419 */       else if (y.balance() == 2) {
/*  420 */         Entry x = y.right;
/*      */ 
/*  422 */         if (x.balance() == 1) {
/*  423 */           w = x;
/*  424 */           if (x.pred()) {
/*  425 */             x.pred(false);
/*  426 */             y.succ(x);
/*      */           } else {
/*  428 */             y.right = x.left;
/*      */           }
/*  430 */           x.left = y;
/*  431 */           x.balance(0);
/*  432 */           y.balance(0);
/*      */         }
/*      */         else
/*      */         {
/*  437 */           w = x.left;
/*  438 */           x.left = w.right;
/*  439 */           w.right = x;
/*  440 */           y.right = w.left;
/*  441 */           w.left = y;
/*  442 */           if (w.balance() == 1) {
/*  443 */             x.balance(0);
/*  444 */             y.balance(-1);
/*      */           }
/*  446 */           else if (w.balance() == 0) {
/*  447 */             x.balance(0);
/*  448 */             y.balance(0);
/*      */           }
/*      */           else {
/*  451 */             x.balance(1);
/*  452 */             y.balance(0);
/*      */           }
/*  454 */           w.balance(0);
/*      */ 
/*  457 */           if (w.pred()) {
/*  458 */             y.succ(w);
/*  459 */             w.pred(false);
/*      */           }
/*  461 */           if (w.succ()) {
/*  462 */             x.pred(w);
/*  463 */             w.succ(false);
/*      */           }
/*      */         }
/*      */       }
/*      */       else {
/*  468 */         return true;
/*      */       }
/*  470 */       if (z == null) this.tree = w;
/*  472 */       else if (z.left == y) z.left = w; else {
/*  473 */         z.right = w;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  478 */     return true;
/*      */   }
/*      */ 
/*      */   private Entry parent(Entry e)
/*      */   {
/*  491 */     if (e == this.tree) return null;
/*      */     Entry y;
/*  494 */     Entry x = y = e;
/*      */     while (true)
/*      */     {
/*  497 */       if (y.succ()) {
/*  498 */         Entry p = y.right;
/*  499 */         if ((p == null) || (p.left != e)) {
/*  500 */           while (!x.pred()) x = x.left;
/*  501 */           p = x.left;
/*      */         }
/*  503 */         return p;
/*      */       }
/*  505 */       if (x.pred()) {
/*  506 */         Entry p = x.left;
/*  507 */         if ((p == null) || (p.right != e)) {
/*  508 */           while (!y.succ()) y = y.right;
/*  509 */           p = y.right;
/*      */         }
/*  511 */         return p;
/*      */       }
/*      */ 
/*  514 */       x = x.left;
/*  515 */       y = y.right;
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean remove(int k)
/*      */   {
/*  522 */     if (this.tree == null) return false;
/*      */ 
/*  525 */     Entry p = this.tree; Entry q = null;
/*  526 */     boolean dir = false;
/*  527 */     int kk = k;
/*      */     int cmp;
/*  530 */     while ((cmp = compare(kk, p.key)) != 0) {
/*  531 */       if ((dir = cmp > 0 ? 1 : 0) != 0) {
/*  532 */         q = p;
/*  533 */         if ((p = p.right()) == null) return false; 
/*      */       }
/*      */       else
/*      */       {
/*  536 */         q = p;
/*  537 */         if ((p = p.left()) == null) return false;
/*      */       }
/*      */     }
/*      */ 
/*  541 */     if (p.left == null) this.firstEntry = p.next();
/*  542 */     if (p.right == null) this.lastEntry = p.prev();
/*      */ 
/*  544 */     if (p.succ()) {
/*  545 */       if (p.pred()) {
/*  546 */         if (q != null) {
/*  547 */           if (dir) q.succ(p.right); else
/*  548 */             q.pred(p.left);
/*      */         }
/*  550 */         else this.tree = (dir ? p.right : p.left); 
/*      */       }
/*      */       else
/*      */       {
/*  553 */         p.prev().right = p.right;
/*      */ 
/*  555 */         if (q != null) {
/*  556 */           if (dir) q.right = p.left; else
/*  557 */             q.left = p.left;
/*      */         }
/*  559 */         else this.tree = p.left; 
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*  563 */       Entry r = p.right;
/*      */ 
/*  565 */       if (r.pred()) {
/*  566 */         r.left = p.left;
/*  567 */         r.pred(p.pred());
/*  568 */         if (!r.pred()) r.prev().right = r;
/*  569 */         if (q != null) {
/*  570 */           if (dir) q.right = r; else
/*  571 */             q.left = r;
/*      */         }
/*  573 */         else this.tree = r;
/*      */ 
/*  575 */         r.balance(p.balance());
/*  576 */         q = r;
/*  577 */         dir = true;
/*      */       }
/*      */       else
/*      */       {
/*      */         Entry s;
/*      */         while (true)
/*      */         {
/*  584 */           s = r.left;
/*  585 */           if (s.pred()) break;
/*  586 */           r = s;
/*      */         }
/*      */ 
/*  589 */         if (s.succ()) r.pred(s); else {
/*  590 */           r.left = s.right;
/*      */         }
/*  592 */         s.left = p.left;
/*      */ 
/*  594 */         if (!p.pred()) {
/*  595 */           p.prev().right = s;
/*  596 */           s.pred(false);
/*      */         }
/*      */ 
/*  599 */         s.right = p.right;
/*  600 */         s.succ(false);
/*      */ 
/*  602 */         if (q != null) {
/*  603 */           if (dir) q.right = s; else
/*  604 */             q.left = s;
/*      */         }
/*  606 */         else this.tree = s;
/*      */ 
/*  608 */         s.balance(p.balance());
/*  609 */         q = r;
/*  610 */         dir = false;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  616 */     while (q != null) {
/*  617 */       Entry y = q;
/*  618 */       q = parent(y);
/*      */ 
/*  620 */       if (!dir) {
/*  621 */         dir = (q != null) && (q.left != y);
/*  622 */         y.incBalance();
/*      */ 
/*  624 */         if (y.balance() == 1) break;
/*  625 */         if (y.balance() == 2)
/*      */         {
/*  627 */           Entry x = y.right;
/*      */ 
/*  630 */           if (x.balance() == -1)
/*      */           {
/*  635 */             Entry w = x.left;
/*  636 */             x.left = w.right;
/*  637 */             w.right = x;
/*  638 */             y.right = w.left;
/*  639 */             w.left = y;
/*      */ 
/*  641 */             if (w.balance() == 1) {
/*  642 */               x.balance(0);
/*  643 */               y.balance(-1);
/*      */             }
/*  645 */             else if (w.balance() == 0) {
/*  646 */               x.balance(0);
/*  647 */               y.balance(0);
/*      */             }
/*      */             else
/*      */             {
/*  652 */               x.balance(1);
/*  653 */               y.balance(0);
/*      */             }
/*      */ 
/*  656 */             w.balance(0);
/*      */ 
/*  658 */             if (w.pred()) {
/*  659 */               y.succ(w);
/*  660 */               w.pred(false);
/*      */             }
/*  662 */             if (w.succ()) {
/*  663 */               x.pred(w);
/*  664 */               w.succ(false);
/*      */             }
/*      */ 
/*  667 */             if (q != null) {
/*  668 */               if (dir) q.right = w; else
/*  669 */                 q.left = w;
/*      */             }
/*  671 */             else this.tree = w; 
/*      */           }
/*      */           else
/*      */           {
/*  674 */             if (q != null) {
/*  675 */               if (dir) q.right = x; else
/*  676 */                 q.left = x;
/*      */             }
/*  678 */             else this.tree = x;
/*      */ 
/*  680 */             if (x.balance() == 0) {
/*  681 */               y.right = x.left;
/*  682 */               x.left = y;
/*  683 */               x.balance(-1);
/*  684 */               y.balance(1);
/*  685 */               break;
/*      */             }
/*      */ 
/*  690 */             if (x.pred()) {
/*  691 */               y.succ(true);
/*  692 */               x.pred(false);
/*      */             } else {
/*  694 */               y.right = x.left;
/*      */             }
/*  696 */             x.left = y;
/*  697 */             y.balance(0);
/*  698 */             x.balance(0);
/*      */           }
/*      */         }
/*      */       }
/*      */       else {
/*  703 */         dir = (q != null) && (q.left != y);
/*  704 */         y.decBalance();
/*      */ 
/*  706 */         if (y.balance() == -1) break;
/*  707 */         if (y.balance() == -2)
/*      */         {
/*  709 */           Entry x = y.left;
/*      */ 
/*  712 */           if (x.balance() == 1)
/*      */           {
/*  717 */             Entry w = x.right;
/*  718 */             x.right = w.left;
/*  719 */             w.left = x;
/*  720 */             y.left = w.right;
/*  721 */             w.right = y;
/*      */ 
/*  723 */             if (w.balance() == -1) {
/*  724 */               x.balance(0);
/*  725 */               y.balance(1);
/*      */             }
/*  727 */             else if (w.balance() == 0) {
/*  728 */               x.balance(0);
/*  729 */               y.balance(0);
/*      */             }
/*      */             else
/*      */             {
/*  734 */               x.balance(-1);
/*  735 */               y.balance(0);
/*      */             }
/*      */ 
/*  738 */             w.balance(0);
/*      */ 
/*  740 */             if (w.pred()) {
/*  741 */               x.succ(w);
/*  742 */               w.pred(false);
/*      */             }
/*  744 */             if (w.succ()) {
/*  745 */               y.pred(w);
/*  746 */               w.succ(false);
/*      */             }
/*      */ 
/*  749 */             if (q != null) {
/*  750 */               if (dir) q.right = w; else
/*  751 */                 q.left = w;
/*      */             }
/*  753 */             else this.tree = w; 
/*      */           }
/*      */           else
/*      */           {
/*  756 */             if (q != null) {
/*  757 */               if (dir) q.right = x; else
/*  758 */                 q.left = x;
/*      */             }
/*  760 */             else this.tree = x;
/*      */ 
/*  762 */             if (x.balance() == 0) {
/*  763 */               y.left = x.right;
/*  764 */               x.right = y;
/*  765 */               x.balance(1);
/*  766 */               y.balance(-1);
/*  767 */               break;
/*      */             }
/*      */ 
/*  772 */             if (x.succ()) {
/*  773 */               y.pred(true);
/*  774 */               x.succ(false);
/*      */             } else {
/*  776 */               y.left = x.right;
/*      */             }
/*  778 */             x.right = y;
/*  779 */             y.balance(0);
/*  780 */             x.balance(0);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  786 */     this.count -= 1;
/*      */ 
/*  788 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean contains(int k)
/*      */   {
/*  793 */     return findKey(k) != null;
/*      */   }
/*      */   public void clear() {
/*  796 */     this.count = 0;
/*  797 */     this.tree = null;
/*  798 */     this.firstEntry = (this.lastEntry = null);
/*      */   }
/*      */ 
/*      */   public int size()
/*      */   {
/*  996 */     return this.count;
/*      */   }
/*      */   public boolean isEmpty() {
/*  999 */     return this.count == 0;
/*      */   }
/*      */   public int firstInt() {
/* 1002 */     if (this.tree == null) throw new NoSuchElementException();
/* 1003 */     return this.firstEntry.key;
/*      */   }
/*      */   public int lastInt() {
/* 1006 */     if (this.tree == null) throw new NoSuchElementException();
/* 1007 */     return this.lastEntry.key;
/*      */   }
/*      */ 
/*      */   public IntBidirectionalIterator iterator()
/*      */   {
/* 1077 */     return new SetIterator();
/*      */   }
/*      */   public IntBidirectionalIterator iterator(int from) {
/* 1080 */     return new SetIterator(from);
/*      */   }
/*      */   public IntComparator comparator() {
/* 1083 */     return this.actualComparator;
/*      */   }
/*      */   public IntSortedSet headSet(int to) {
/* 1086 */     return new Subset(0, true, to, false);
/*      */   }
/*      */   public IntSortedSet tailSet(int from) {
/* 1089 */     return new Subset(from, false, 0, true);
/*      */   }
/*      */   public IntSortedSet subSet(int from, int to) {
/* 1092 */     return new Subset(from, false, to, false);
/*      */   }
/*      */ 
/*      */   public Object clone()
/*      */   {
/*      */     IntAVLTreeSet c;
/*      */     try
/*      */     {
/* 1284 */       c = (IntAVLTreeSet)super.clone();
/*      */     }
/*      */     catch (CloneNotSupportedException cantHappen) {
/* 1287 */       throw new InternalError();
/*      */     }
/* 1289 */     c.allocatePaths();
/* 1290 */     if (this.count != 0)
/*      */     {
/* 1292 */       Entry rp = new Entry(); Entry rq = new Entry();
/* 1293 */       Entry p = rp;
/* 1294 */       rp.left(this.tree);
/* 1295 */       Entry q = rq;
/* 1296 */       rq.pred(null);
/*      */       while (true) {
/* 1298 */         if (!p.pred()) {
/* 1299 */           Entry e = p.left.clone();
/* 1300 */           e.pred(q.left);
/* 1301 */           e.succ(q);
/* 1302 */           q.left(e);
/* 1303 */           p = p.left;
/* 1304 */           q = q.left;
/*      */         }
/*      */         else {
/* 1307 */           while (p.succ()) {
/* 1308 */             p = p.right;
/* 1309 */             if (p == null) {
/* 1310 */               q.right = null;
/* 1311 */               c.tree = rq.left;
/* 1312 */               c.firstEntry = c.tree;
/* 1313 */               while (c.firstEntry.left != null) c.firstEntry = c.firstEntry.left;
/* 1314 */               c.lastEntry = c.tree;
/* 1315 */               while (c.lastEntry.right != null) c.lastEntry = c.lastEntry.right;
/* 1316 */               return c;
/*      */             }
/* 1318 */             q = q.right;
/*      */           }
/* 1320 */           p = p.right;
/* 1321 */           q = q.right;
/*      */         }
/* 1323 */         if (!p.succ()) {
/* 1324 */           Entry e = p.right.clone();
/* 1325 */           e.succ(q.right);
/* 1326 */           e.pred(q);
/* 1327 */           q.right(e);
/*      */         }
/*      */       }
/*      */     }
/* 1331 */     return c;
/*      */   }
/*      */   private void writeObject(ObjectOutputStream s) throws IOException {
/* 1334 */     int n = this.count;
/* 1335 */     SetIterator i = new SetIterator();
/* 1336 */     s.defaultWriteObject();
/* 1337 */     while (n-- != 0) s.writeInt(i.nextInt());
/*      */   }
/*      */ 
/*      */   private Entry readTree(ObjectInputStream s, int n, Entry pred, Entry succ)
/*      */     throws IOException, ClassNotFoundException
/*      */   {
/* 1348 */     if (n == 1) {
/* 1349 */       Entry top = new Entry(s.readInt());
/* 1350 */       top.pred(pred);
/* 1351 */       top.succ(succ);
/* 1352 */       return top;
/*      */     }
/* 1354 */     if (n == 2)
/*      */     {
/* 1357 */       Entry top = new Entry(s.readInt());
/* 1358 */       top.right(new Entry(s.readInt()));
/* 1359 */       top.right.pred(top);
/* 1360 */       top.balance(1);
/* 1361 */       top.pred(pred);
/* 1362 */       top.right.succ(succ);
/* 1363 */       return top;
/*      */     }
/*      */ 
/* 1366 */     int rightN = n / 2; int leftN = n - rightN - 1;
/* 1367 */     Entry top = new Entry();
/* 1368 */     top.left(readTree(s, leftN, pred, top));
/* 1369 */     top.key = s.readInt();
/* 1370 */     top.right(readTree(s, rightN, top, succ));
/* 1371 */     if (n == (n & -n)) top.balance(1);
/* 1372 */     return top;
/*      */   }
/*      */   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
/* 1375 */     s.defaultReadObject();
/*      */ 
/* 1378 */     setActualComparator();
/* 1379 */     allocatePaths();
/* 1380 */     if (this.count != 0) {
/* 1381 */       this.tree = readTree(s, this.count, null, null);
/*      */ 
/* 1383 */       Entry e = this.tree;
/* 1384 */       while (e.left() != null) e = e.left();
/* 1385 */       this.firstEntry = e;
/* 1386 */       e = this.tree;
/* 1387 */       while (e.right() != null) e = e.right();
/* 1388 */       this.lastEntry = e;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static int checkTree(Entry e) {
/* 1393 */     return 0;
/*      */   }
/*      */ 
/*      */   private final class Subset extends AbstractIntSortedSet
/*      */     implements Serializable, IntSortedSet
/*      */   {
/*      */     public static final long serialVersionUID = -7046029254386353129L;
/*      */     int from;
/*      */     int to;
/*      */     boolean bottom;
/*      */     boolean top;
/*      */ 
/*      */     public Subset(int from, boolean bottom, int to, boolean top)
/*      */     {
/* 1121 */       if ((!bottom) && (!top) && (IntAVLTreeSet.this.compare(from, to) > 0)) throw new IllegalArgumentException(new StringBuilder().append("Start element (").append(from).append(") is larger than end element (").append(to).append(")").toString());
/* 1122 */       this.from = from;
/* 1123 */       this.bottom = bottom;
/* 1124 */       this.to = to;
/* 1125 */       this.top = top;
/*      */     }
/*      */     public void clear() {
/* 1128 */       SubsetIterator i = new SubsetIterator();
/* 1129 */       while (i.hasNext()) {
/* 1130 */         i.next();
/* 1131 */         i.remove();
/*      */       }
/*      */     }
/*      */ 
/*      */     final boolean in(int k)
/*      */     {
/* 1139 */       return ((this.bottom) || (IntAVLTreeSet.this.compare(k, this.from) >= 0)) && ((this.top) || (IntAVLTreeSet.this.compare(k, this.to) < 0));
/*      */     }
/*      */ 
/*      */     public boolean contains(int k)
/*      */     {
/* 1144 */       return (in(k)) && (IntAVLTreeSet.this.contains(k));
/*      */     }
/*      */     public boolean add(int k) {
/* 1147 */       if (!in(k)) throw new IllegalArgumentException(new StringBuilder().append("Element (").append(k).append(") out of range [").append(this.bottom ? "-" : String.valueOf(this.from)).append(", ").append(this.top ? "-" : String.valueOf(this.to)).append(")").toString());
/* 1148 */       return IntAVLTreeSet.this.add(k);
/*      */     }
/*      */ 
/*      */     public boolean remove(int k) {
/* 1152 */       if (!in(k)) return false;
/* 1153 */       return IntAVLTreeSet.this.remove(k);
/*      */     }
/*      */     public int size() {
/* 1156 */       SubsetIterator i = new SubsetIterator();
/* 1157 */       int n = 0;
/* 1158 */       while (i.hasNext()) {
/* 1159 */         n++;
/* 1160 */         i.next();
/*      */       }
/* 1162 */       return n;
/*      */     }
/*      */     public boolean isEmpty() {
/* 1165 */       return !new SubsetIterator().hasNext();
/*      */     }
/*      */     public IntComparator comparator() {
/* 1168 */       return IntAVLTreeSet.this.actualComparator;
/*      */     }
/*      */     public IntBidirectionalIterator iterator() {
/* 1171 */       return new SubsetIterator();
/*      */     }
/*      */     public IntBidirectionalIterator iterator(int from) {
/* 1174 */       return new SubsetIterator(from);
/*      */     }
/*      */     public IntSortedSet headSet(int to) {
/* 1177 */       if (this.top) return new Subset(IntAVLTreeSet.this, this.from, this.bottom, to, false);
/* 1178 */       return IntAVLTreeSet.this.compare(to, this.to) < 0 ? new Subset(IntAVLTreeSet.this, this.from, this.bottom, to, false) : this;
/*      */     }
/*      */     public IntSortedSet tailSet(int from) {
/* 1181 */       if (this.bottom) return new Subset(IntAVLTreeSet.this, from, false, this.to, this.top);
/* 1182 */       return IntAVLTreeSet.this.compare(from, this.from) > 0 ? new Subset(IntAVLTreeSet.this, from, false, this.to, this.top) : this;
/*      */     }
/*      */     public IntSortedSet subSet(int from, int to) {
/* 1185 */       if ((this.top) && (this.bottom)) return new Subset(IntAVLTreeSet.this, from, false, to, false);
/* 1186 */       if (!this.top) to = IntAVLTreeSet.this.compare(to, this.to) < 0 ? to : this.to;
/* 1187 */       if (!this.bottom) from = IntAVLTreeSet.this.compare(from, this.from) > 0 ? from : this.from;
/* 1188 */       if ((!this.top) && (!this.bottom) && (from == this.from) && (to == this.to)) return this;
/* 1189 */       return new Subset(IntAVLTreeSet.this, from, false, to, false);
/*      */     }
/*      */ 
/*      */     public IntAVLTreeSet.Entry firstEntry()
/*      */     {
/* 1196 */       if (IntAVLTreeSet.this.tree == null) return null;
/* 1199 */       IntAVLTreeSet.Entry e;
/*      */       IntAVLTreeSet.Entry e;
/* 1199 */       if (this.bottom) { e = IntAVLTreeSet.this.firstEntry;
/*      */       } else {
/* 1201 */         e = IntAVLTreeSet.this.locateKey(this.from);
/*      */ 
/* 1203 */         if (IntAVLTreeSet.this.compare(e.key, this.from) < 0) e = e.next();
/*      */       }
/*      */ 
/* 1206 */       if ((e == null) || ((!this.top) && (IntAVLTreeSet.this.compare(e.key, this.to) >= 0))) return null;
/* 1207 */       return e;
/*      */     }
/*      */ 
/*      */     public IntAVLTreeSet.Entry lastEntry()
/*      */     {
/* 1214 */       if (IntAVLTreeSet.this.tree == null) return null;
/* 1217 */       IntAVLTreeSet.Entry e;
/*      */       IntAVLTreeSet.Entry e;
/* 1217 */       if (this.top) { e = IntAVLTreeSet.this.lastEntry;
/*      */       } else {
/* 1219 */         e = IntAVLTreeSet.this.locateKey(this.to);
/*      */ 
/* 1221 */         if (IntAVLTreeSet.this.compare(e.key, this.to) >= 0) e = e.prev();
/*      */       }
/*      */ 
/* 1224 */       if ((e == null) || ((!this.bottom) && (IntAVLTreeSet.this.compare(e.key, this.from) < 0))) return null;
/* 1225 */       return e;
/*      */     }
/*      */     public int firstInt() {
/* 1228 */       IntAVLTreeSet.Entry e = firstEntry();
/* 1229 */       if (e == null) throw new NoSuchElementException();
/* 1230 */       return e.key;
/*      */     }
/*      */     public int lastInt() {
/* 1233 */       IntAVLTreeSet.Entry e = lastEntry();
/* 1234 */       if (e == null) throw new NoSuchElementException();
/* 1235 */       return e.key;
/*      */     }
/*      */ 
/*      */     private final class SubsetIterator extends IntAVLTreeSet.SetIterator
/*      */     {
/*      */       SubsetIterator()
/*      */       {
/* 1245 */         super();
/* 1246 */         this.next = IntAVLTreeSet.Subset.this.firstEntry();
/*      */       }
/*      */       SubsetIterator(int k) {
/* 1249 */         this();
/* 1250 */         if (this.next != null)
/* 1251 */           if ((!IntAVLTreeSet.Subset.this.bottom) && (IntAVLTreeSet.this.compare(k, this.next.key) < 0)) { this.prev = null;
/* 1252 */           } else if ((!IntAVLTreeSet.Subset.this.top) && (IntAVLTreeSet.this.compare(k, (this.prev = IntAVLTreeSet.Subset.this.lastEntry()).key) >= 0)) { this.next = null;
/*      */           } else {
/* 1254 */             this.next = IntAVLTreeSet.this.locateKey(k);
/* 1255 */             if (IntAVLTreeSet.this.compare(this.next.key, k) <= 0) {
/* 1256 */               this.prev = this.next;
/* 1257 */               this.next = this.next.next();
/*      */             } else {
/* 1259 */               this.prev = this.next.prev();
/*      */             }
/*      */           }
/*      */       }
/*      */ 
/* 1264 */       void updatePrevious() { this.prev = this.prev.prev();
/* 1265 */         if ((!IntAVLTreeSet.Subset.this.bottom) && (this.prev != null) && (IntAVLTreeSet.this.compare(this.prev.key, IntAVLTreeSet.Subset.this.from) < 0)) this.prev = null;  } 
/*      */       void updateNext()
/*      */       {
/* 1268 */         this.next = this.next.next();
/* 1269 */         if ((!IntAVLTreeSet.Subset.this.top) && (this.next != null) && (IntAVLTreeSet.this.compare(this.next.key, IntAVLTreeSet.Subset.this.to) >= 0)) this.next = null;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private class SetIterator extends AbstractIntListIterator
/*      */   {
/*      */     IntAVLTreeSet.Entry prev;
/*      */     IntAVLTreeSet.Entry next;
/*      */     IntAVLTreeSet.Entry curr;
/* 1021 */     int index = 0;
/*      */ 
/* 1023 */     SetIterator() { this.next = IntAVLTreeSet.this.firstEntry; }
/*      */ 
/*      */     SetIterator(int k) {
/* 1026 */       if ((this.next = IntAVLTreeSet.this.locateKey(k)) != null)
/* 1027 */         if (IntAVLTreeSet.this.compare(this.next.key, k) <= 0) {
/* 1028 */           this.prev = this.next;
/* 1029 */           this.next = this.next.next();
/*      */         } else {
/* 1031 */           this.prev = this.next.prev(); }  
/*      */     }
/*      */ 
/* 1034 */     public boolean hasNext() { return this.next != null; } 
/* 1035 */     public boolean hasPrevious() { return this.prev != null; } 
/*      */     void updateNext() {
/* 1037 */       this.next = this.next.next();
/*      */     }
/*      */     IntAVLTreeSet.Entry nextEntry() {
/* 1040 */       if (!hasNext()) throw new NoSuchElementException();
/* 1041 */       this.curr = (this.prev = this.next);
/* 1042 */       this.index += 1;
/* 1043 */       updateNext();
/* 1044 */       return this.curr;
/*      */     }
/* 1046 */     public int nextInt() { return nextEntry().key; } 
/* 1047 */     public int previousInt() { return previousEntry().key; } 
/*      */     void updatePrevious() {
/* 1049 */       this.prev = this.prev.prev();
/*      */     }
/*      */     IntAVLTreeSet.Entry previousEntry() {
/* 1052 */       if (!hasPrevious()) throw new NoSuchElementException();
/* 1053 */       this.curr = (this.next = this.prev);
/* 1054 */       this.index -= 1;
/* 1055 */       updatePrevious();
/* 1056 */       return this.curr;
/*      */     }
/*      */     public int nextIndex() {
/* 1059 */       return this.index;
/*      */     }
/*      */     public int previousIndex() {
/* 1062 */       return this.index - 1;
/*      */     }
/*      */     public void remove() {
/* 1065 */       if (this.curr == null) throw new IllegalStateException();
/*      */ 
/* 1068 */       if (this.curr == this.prev) this.index -= 1;
/* 1069 */       this.next = (this.prev = this.curr);
/* 1070 */       updatePrevious();
/* 1071 */       updateNext();
/* 1072 */       IntAVLTreeSet.this.remove(this.curr.key);
/* 1073 */       this.curr = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static final class Entry
/*      */     implements Cloneable
/*      */   {
/*      */     private static final int SUCC_MASK = -2147483648;
/*      */     private static final int PRED_MASK = 1073741824;
/*      */     private static final int BALANCE_MASK = 255;
/*      */     int key;
/*      */     Entry left;
/*      */     Entry right;
/*      */     int info;
/*      */ 
/*      */     Entry()
/*      */     {
/*      */     }
/*      */ 
/*      */     Entry(int k)
/*      */     {
/*  827 */       this.key = k;
/*  828 */       this.info = -1073741824;
/*      */     }
/*      */ 
/*      */     Entry left()
/*      */     {
/*  836 */       return (this.info & 0x40000000) != 0 ? null : this.left;
/*      */     }
/*      */ 
/*      */     Entry right()
/*      */     {
/*  844 */       return (this.info & 0x80000000) != 0 ? null : this.right;
/*      */     }
/*      */ 
/*      */     boolean pred()
/*      */     {
/*  850 */       return (this.info & 0x40000000) != 0;
/*      */     }
/*      */ 
/*      */     boolean succ()
/*      */     {
/*  856 */       return (this.info & 0x80000000) != 0;
/*      */     }
/*      */ 
/*      */     void pred(boolean pred)
/*      */     {
/*  862 */       if (pred) this.info |= 1073741824; else
/*  863 */         this.info &= -1073741825;
/*      */     }
/*      */ 
/*      */     void succ(boolean succ)
/*      */     {
/*  869 */       if (succ) this.info |= -2147483648; else
/*  870 */         this.info &= 2147483647;
/*      */     }
/*      */ 
/*      */     void pred(Entry pred)
/*      */     {
/*  876 */       this.info |= 1073741824;
/*  877 */       this.left = pred;
/*      */     }
/*      */ 
/*      */     void succ(Entry succ)
/*      */     {
/*  883 */       this.info |= -2147483648;
/*  884 */       this.right = succ;
/*      */     }
/*      */ 
/*      */     void left(Entry left)
/*      */     {
/*  890 */       this.info &= -1073741825;
/*  891 */       this.left = left;
/*      */     }
/*      */ 
/*      */     void right(Entry right)
/*      */     {
/*  897 */       this.info &= 2147483647;
/*  898 */       this.right = right;
/*      */     }
/*      */ 
/*      */     int balance()
/*      */     {
/*  904 */       return (byte)this.info;
/*      */     }
/*      */ 
/*      */     void balance(int level)
/*      */     {
/*  910 */       this.info &= -256;
/*  911 */       this.info |= level & 0xFF;
/*      */     }
/*      */ 
/*      */     void incBalance() {
/*  915 */       this.info = (this.info & 0xFFFFFF00 | (byte)this.info + 1 & 0xFF);
/*      */     }
/*      */ 
/*      */     protected void decBalance() {
/*  919 */       this.info = (this.info & 0xFFFFFF00 | (byte)this.info - 1 & 0xFF);
/*      */     }
/*      */ 
/*      */     Entry next()
/*      */     {
/*  926 */       Entry next = this.right;
/*  927 */       for ((this.info & 0x80000000) != 0; (next.info & 0x40000000) == 0; next = next.left);
/*  928 */       return next;
/*      */     }
/*      */ 
/*      */     Entry prev()
/*      */     {
/*  935 */       Entry prev = this.left;
/*  936 */       for ((this.info & 0x40000000) != 0; (prev.info & 0x80000000) == 0; prev = prev.right);
/*  937 */       return prev;
/*      */     }
/*      */ 
/*      */     public Entry clone() {
/*      */       Entry c;
/*      */       try {
/*  943 */         c = (Entry)super.clone();
/*      */       }
/*      */       catch (CloneNotSupportedException cantHappen) {
/*  946 */         throw new InternalError();
/*      */       }
/*  948 */       c.key = this.key;
/*  949 */       c.info = this.info;
/*  950 */       return c;
/*      */     }
/*      */     public boolean equals(Object o) {
/*  953 */       if (!(o instanceof Entry)) return false;
/*  954 */       Entry e = (Entry)o;
/*  955 */       return this.key == e.key;
/*      */     }
/*      */     public int hashCode() {
/*  958 */       return this.key;
/*      */     }
/*      */     public String toString() {
/*  961 */       return String.valueOf(this.key);
/*      */     }
/*      */   }
/*      */ }

/* Location:           C:\Users\Raul\Desktop\StarMade\StarMade.jar
 * Qualified Name:     it.unimi.dsi.fastutil.ints.IntAVLTreeSet
 * JD-Core Version:    0.6.2
 */