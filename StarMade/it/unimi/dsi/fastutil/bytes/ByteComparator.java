package it.unimi.dsi.fastutil.bytes;

import java.util.Comparator;

public abstract interface ByteComparator extends Comparator<Byte>
{
  public abstract int compare(byte paramByte1, byte paramByte2);
}

/* Location:           C:\Users\Raul\Desktop\StarMade\StarMade.jar
 * Qualified Name:     it.unimi.dsi.fastutil.bytes.ByteComparator
 * JD-Core Version:    0.6.2
 */