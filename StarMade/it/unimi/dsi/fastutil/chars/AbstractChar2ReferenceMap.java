/*     */ package it.unimi.dsi.fastutil.chars;
/*     */ 
/*     */ import it.unimi.dsi.fastutil.objects.AbstractObjectIterator;
/*     */ import it.unimi.dsi.fastutil.objects.AbstractReferenceCollection;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectIterator;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectSet;
/*     */ import it.unimi.dsi.fastutil.objects.ReferenceCollection;
/*     */ import java.io.Serializable;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ 
/*     */ public abstract class AbstractChar2ReferenceMap<V> extends AbstractChar2ReferenceFunction<V>
/*     */   implements Char2ReferenceMap<V>, Serializable
/*     */ {
/*     */   public static final long serialVersionUID = -4940583368468432370L;
/*     */ 
/*     */   public boolean containsValue(Object v)
/*     */   {
/*  69 */     return values().contains(v);
/*     */   }
/*     */ 
/*     */   public boolean containsKey(char k) {
/*  73 */     return keySet().contains(k);
/*     */   }
/*     */ 
/*     */   public void putAll(Map<? extends Character, ? extends V> m)
/*     */   {
/*  83 */     int n = m.size();
/*  84 */     Iterator i = m.entrySet().iterator();
/*  85 */     if ((m instanceof Char2ReferenceMap))
/*     */     {
/*  87 */       while (n-- != 0) {
/*  88 */         Char2ReferenceMap.Entry e = (Char2ReferenceMap.Entry)i.next();
/*  89 */         put(e.getCharKey(), e.getValue());
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/*  94 */       while (n-- != 0) {
/*  95 */         Map.Entry e = (Map.Entry)i.next();
/*  96 */         put((Character)e.getKey(), e.getValue());
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/* 101 */   public boolean isEmpty() { return size() == 0; }
/*     */ 
/*     */ 
/*     */   public CharSet keySet()
/*     */   {
/* 174 */     return new AbstractCharSet() {
/* 175 */       public boolean contains(char k) { return AbstractChar2ReferenceMap.this.containsKey(k); } 
/* 176 */       public int size() { return AbstractChar2ReferenceMap.this.size(); } 
/* 177 */       public void clear() { AbstractChar2ReferenceMap.this.clear(); } 
/*     */       public CharIterator iterator() {
/* 179 */         return new AbstractCharIterator() {
/* 180 */           final ObjectIterator<Map.Entry<Character, V>> i = AbstractChar2ReferenceMap.this.entrySet().iterator();
/*     */ 
/* 181 */           public char nextChar() { return ((Char2ReferenceMap.Entry)this.i.next()).getCharKey(); } 
/* 182 */           public boolean hasNext() { return this.i.hasNext(); }
/*     */ 
/*     */         };
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public ReferenceCollection<V> values()
/*     */   {
/* 199 */     return new AbstractReferenceCollection() {
/* 200 */       public boolean contains(Object k) { return AbstractChar2ReferenceMap.this.containsValue(k); } 
/* 201 */       public int size() { return AbstractChar2ReferenceMap.this.size(); } 
/* 202 */       public void clear() { AbstractChar2ReferenceMap.this.clear(); } 
/*     */       public ObjectIterator<V> iterator() {
/* 204 */         return new AbstractObjectIterator() {
/* 205 */           final ObjectIterator<Map.Entry<Character, V>> i = AbstractChar2ReferenceMap.this.entrySet().iterator();
/*     */ 
/* 206 */           public V next() { return ((Char2ReferenceMap.Entry)this.i.next()).getValue(); } 
/* 207 */           public boolean hasNext() { return this.i.hasNext(); }
/*     */         };
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public ObjectSet<Map.Entry<Character, V>> entrySet() {
/* 214 */     return char2ReferenceEntrySet();
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 223 */     int h = 0; int n = size();
/* 224 */     ObjectIterator i = entrySet().iterator();
/* 225 */     while (n-- != 0) h += ((Map.Entry)i.next()).hashCode();
/* 226 */     return h;
/*     */   }
/*     */   public boolean equals(Object o) {
/* 229 */     if (o == this) return true;
/* 230 */     if (!(o instanceof Map)) return false;
/* 231 */     Map m = (Map)o;
/* 232 */     if (m.size() != size()) return false;
/* 233 */     return entrySet().containsAll(m.entrySet());
/*     */   }
/*     */   public String toString() {
/* 236 */     StringBuilder s = new StringBuilder();
/* 237 */     ObjectIterator i = entrySet().iterator();
/* 238 */     int n = size();
/*     */ 
/* 240 */     boolean first = true;
/* 241 */     s.append("{");
/* 242 */     while (n-- != 0) {
/* 243 */       if (first) first = false; else
/* 244 */         s.append(", ");
/* 245 */       Char2ReferenceMap.Entry e = (Char2ReferenceMap.Entry)i.next();
/* 246 */       s.append(String.valueOf(e.getCharKey()));
/* 247 */       s.append("=>");
/* 248 */       if (this == e.getValue()) s.append("(this map)"); else
/* 249 */         s.append(String.valueOf(e.getValue()));
/*     */     }
/* 251 */     s.append("}");
/* 252 */     return s.toString();
/*     */   }
/*     */ 
/*     */   public static class BasicEntry<V>
/*     */     implements Char2ReferenceMap.Entry<V>
/*     */   {
/*     */     protected char key;
/*     */     protected V value;
/*     */ 
/*     */     public BasicEntry(Character key, V value)
/*     */     {
/* 114 */       this.key = key.charValue();
/* 115 */       this.value = value;
/*     */     }
/*     */ 
/*     */     public BasicEntry(char key, V value)
/*     */     {
/* 121 */       this.key = key;
/* 122 */       this.value = value;
/*     */     }
/*     */ 
/*     */     public Character getKey()
/*     */     {
/* 128 */       return Character.valueOf(this.key);
/*     */     }
/*     */ 
/*     */     public char getCharKey()
/*     */     {
/* 133 */       return this.key;
/*     */     }
/*     */ 
/*     */     public V getValue()
/*     */     {
/* 138 */       return this.value;
/*     */     }
/*     */ 
/*     */     public V setValue(V value)
/*     */     {
/* 148 */       throw new UnsupportedOperationException();
/*     */     }
/*     */     public boolean equals(Object o) {
/* 151 */       if (!(o instanceof Map.Entry)) return false;
/* 152 */       Map.Entry e = (Map.Entry)o;
/* 153 */       return (this.key == ((Character)e.getKey()).charValue()) && (this.value == e.getValue());
/*     */     }
/*     */     public int hashCode() {
/* 156 */       return this.key ^ (this.value == null ? 0 : System.identityHashCode(this.value));
/*     */     }
/*     */     public String toString() {
/* 159 */       return this.key + "->" + this.value;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Raul\Desktop\StarMade\StarMade.jar
 * Qualified Name:     it.unimi.dsi.fastutil.chars.AbstractChar2ReferenceMap
 * JD-Core Version:    0.6.2
 */