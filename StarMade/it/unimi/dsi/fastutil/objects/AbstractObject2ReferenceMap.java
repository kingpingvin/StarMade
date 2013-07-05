/*     */ package it.unimi.dsi.fastutil.objects;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ 
/*     */ public abstract class AbstractObject2ReferenceMap<K, V> extends AbstractObject2ReferenceFunction<K, V>
/*     */   implements Object2ReferenceMap<K, V>, Serializable
/*     */ {
/*     */   public static final long serialVersionUID = -4940583368468432370L;
/*     */ 
/*     */   public boolean containsValue(Object v)
/*     */   {
/*  68 */     return values().contains(v);
/*     */   }
/*     */ 
/*     */   public boolean containsKey(Object k) {
/*  72 */     return keySet().contains(k);
/*     */   }
/*     */ 
/*     */   public void putAll(Map<? extends K, ? extends V> m)
/*     */   {
/*  82 */     int n = m.size();
/*  83 */     Iterator i = m.entrySet().iterator();
/*  84 */     if ((m instanceof Object2ReferenceMap))
/*     */     {
/*  86 */       while (n-- != 0) {
/*  87 */         Object2ReferenceMap.Entry e = (Object2ReferenceMap.Entry)i.next();
/*  88 */         put(e.getKey(), e.getValue());
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/*  93 */       while (n-- != 0) {
/*  94 */         Map.Entry e = (Map.Entry)i.next();
/*  95 */         put(e.getKey(), e.getValue());
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/* 100 */   public boolean isEmpty() { return size() == 0; }
/*     */ 
/*     */ 
/*     */   public ObjectSet<K> keySet()
/*     */   {
/* 150 */     return new AbstractObjectSet() {
/* 151 */       public boolean contains(Object k) { return AbstractObject2ReferenceMap.this.containsKey(k); } 
/* 152 */       public int size() { return AbstractObject2ReferenceMap.this.size(); } 
/* 153 */       public void clear() { AbstractObject2ReferenceMap.this.clear(); } 
/*     */       public ObjectIterator<K> iterator() {
/* 155 */         return new AbstractObjectIterator() {
/* 156 */           final ObjectIterator<Map.Entry<K, V>> i = AbstractObject2ReferenceMap.this.entrySet().iterator();
/*     */ 
/* 157 */           public K next() { return ((Object2ReferenceMap.Entry)this.i.next()).getKey(); } 
/* 158 */           public boolean hasNext() { return this.i.hasNext(); }
/*     */ 
/*     */         };
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public ReferenceCollection<V> values()
/*     */   {
/* 175 */     return new AbstractReferenceCollection() {
/* 176 */       public boolean contains(Object k) { return AbstractObject2ReferenceMap.this.containsValue(k); } 
/* 177 */       public int size() { return AbstractObject2ReferenceMap.this.size(); } 
/* 178 */       public void clear() { AbstractObject2ReferenceMap.this.clear(); } 
/*     */       public ObjectIterator<V> iterator() {
/* 180 */         return new AbstractObjectIterator() {
/* 181 */           final ObjectIterator<Map.Entry<K, V>> i = AbstractObject2ReferenceMap.this.entrySet().iterator();
/*     */ 
/* 182 */           public V next() { return ((Object2ReferenceMap.Entry)this.i.next()).getValue(); } 
/* 183 */           public boolean hasNext() { return this.i.hasNext(); }
/*     */         };
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public ObjectSet<Map.Entry<K, V>> entrySet() {
/* 190 */     return object2ReferenceEntrySet();
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 199 */     int h = 0; int n = size();
/* 200 */     ObjectIterator i = entrySet().iterator();
/* 201 */     while (n-- != 0) h += ((Map.Entry)i.next()).hashCode();
/* 202 */     return h;
/*     */   }
/*     */   public boolean equals(Object o) {
/* 205 */     if (o == this) return true;
/* 206 */     if (!(o instanceof Map)) return false;
/* 207 */     Map m = (Map)o;
/* 208 */     if (m.size() != size()) return false;
/* 209 */     return entrySet().containsAll(m.entrySet());
/*     */   }
/*     */   public String toString() {
/* 212 */     StringBuilder s = new StringBuilder();
/* 213 */     ObjectIterator i = entrySet().iterator();
/* 214 */     int n = size();
/*     */ 
/* 216 */     boolean first = true;
/* 217 */     s.append("{");
/* 218 */     while (n-- != 0) {
/* 219 */       if (first) first = false; else
/* 220 */         s.append(", ");
/* 221 */       Object2ReferenceMap.Entry e = (Object2ReferenceMap.Entry)i.next();
/* 222 */       if (this == e.getKey()) s.append("(this map)"); else
/* 223 */         s.append(String.valueOf(e.getKey()));
/* 224 */       s.append("=>");
/* 225 */       if (this == e.getValue()) s.append("(this map)"); else
/* 226 */         s.append(String.valueOf(e.getValue()));
/*     */     }
/* 228 */     s.append("}");
/* 229 */     return s.toString();
/*     */   }
/*     */ 
/*     */   public static class BasicEntry<K, V>
/*     */     implements Object2ReferenceMap.Entry<K, V>
/*     */   {
/*     */     protected K key;
/*     */     protected V value;
/*     */ 
/*     */     public BasicEntry(K key, V value)
/*     */     {
/* 114 */       this.key = key;
/* 115 */       this.value = value;
/*     */     }
/*     */     public K getKey() {
/* 118 */       return this.key;
/*     */     }
/*     */     public V getValue() {
/* 121 */       return this.value;
/*     */     }
/*     */     public V setValue(V value) {
/* 124 */       throw new UnsupportedOperationException();
/*     */     }
/*     */     public boolean equals(Object o) {
/* 127 */       if (!(o instanceof Map.Entry)) return false;
/* 128 */       Map.Entry e = (Map.Entry)o;
/* 129 */       return (this.key == null ? e.getKey() == null : this.key.equals(e.getKey())) && (this.value == e.getValue());
/*     */     }
/*     */     public int hashCode() {
/* 132 */       return (this.key == null ? 0 : this.key.hashCode()) ^ (this.value == null ? 0 : System.identityHashCode(this.value));
/*     */     }
/*     */     public String toString() {
/* 135 */       return this.key + "->" + this.value;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Raul\Desktop\StarMade\StarMade.jar
 * Qualified Name:     it.unimi.dsi.fastutil.objects.AbstractObject2ReferenceMap
 * JD-Core Version:    0.6.2
 */