/*    */ package org.jaxen.expr;
/*    */ 
/*    */ class DefaultGreaterThanEqualExpr extends DefaultRelationalExpr
/*    */ {
/*    */   private static final long serialVersionUID = -7848747981787197470L;
/*    */ 
/*    */   DefaultGreaterThanEqualExpr(Expr lhs, Expr rhs)
/*    */   {
/* 58 */     super(lhs, rhs);
/*    */   }
/*    */ 
/*    */   public String getOperator()
/*    */   {
/* 63 */     return ">=";
/*    */   }
/*    */ 
/*    */   protected boolean evaluateDoubleDouble(Double lhs, Double rhs)
/*    */   {
/* 68 */     return lhs.compareTo(rhs) >= 0;
/*    */   }
/*    */ }

/* Location:           C:\Users\Raul\Desktop\StarMade\StarMade.jar
 * Qualified Name:     org.jaxen.expr.DefaultGreaterThanEqualExpr
 * JD-Core Version:    0.6.2
 */