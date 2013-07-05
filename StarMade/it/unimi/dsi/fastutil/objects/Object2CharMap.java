package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.chars.CharCollection;
import java.util.Map;
import java.util.Map.Entry;

public abstract interface Object2CharMap<K> extends Object2CharFunction<K>, Map<K, Character>
{
  public abstract ObjectSet<Map.Entry<K, Character>> entrySet();

  public abstract ObjectSet<Entry<K>> object2CharEntrySet();

  public abstract ObjectSet<K> keySet();

  public abstract CharCollection values();

  public abstract boolean containsValue(char paramChar);

  public static abstract interface Entry<K> extends Map.Entry<K, Character>
  {
    public abstract char setValue(char paramChar);

    public abstract char getCharValue();
  }

  public static abstract interface FastEntrySet<K> extends ObjectSet<Object2CharMap.Entry<K>>
  {
    public abstract ObjectIterator<Object2CharMap.Entry<K>> fastIterator();
  }
}

/* Location:           C:\Users\Raul\Desktop\StarMade\StarMade.jar
 * Qualified Name:     it.unimi.dsi.fastutil.objects.Object2CharMap
 * JD-Core Version:    0.6.2
 */