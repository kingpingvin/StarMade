/*    */ package org.lwjgl.opengl;
/*    */ 
/*    */ import java.nio.LongBuffer;
/*    */ 
/*    */ public final class ARBTimerQuery
/*    */ {
/*    */   public static final int GL_TIME_ELAPSED = 35007;
/*    */   public static final int GL_TIMESTAMP = 36392;
/*    */ 
/*    */   public static void glQueryCounter(int id, int target)
/*    */   {
/* 26 */     GL33.glQueryCounter(id, target);
/*    */   }
/*    */ 
/*    */   public static void glGetQueryObject(int id, int pname, LongBuffer params) {
/* 30 */     GL33.glGetQueryObject(id, pname, params);
/*    */   }
/*    */ 
/*    */   public static long glGetQueryObjecti64(int id, int pname)
/*    */   {
/* 35 */     return GL33.glGetQueryObjecti64(id, pname);
/*    */   }
/*    */ 
/*    */   public static void glGetQueryObjectu(int id, int pname, LongBuffer params) {
/* 39 */     GL33.glGetQueryObjectu(id, pname, params);
/*    */   }
/*    */ 
/*    */   public static long glGetQueryObjectui64(int id, int pname)
/*    */   {
/* 44 */     return GL33.glGetQueryObjectui64(id, pname);
/*    */   }
/*    */ }

/* Location:           C:\Users\Raul\Desktop\StarMade\StarMade.jar
 * Qualified Name:     org.lwjgl.opengl.ARBTimerQuery
 * JD-Core Version:    0.6.2
 */