package org.schema.schine.network;

import org.schema.schine.network.objects.Sendable;

public abstract interface ControllerInterface
{
  public abstract void onRemoveEntity(Sendable paramSendable);

  public abstract long calculateStartTime();
}

/* Location:           C:\Users\Raul\Desktop\StarMade\StarMade.jar
 * Qualified Name:     org.schema.schine.network.ControllerInterface
 * JD-Core Version:    0.6.2
 */