/*     */ package org.jasypt.util.binary;
/*     */ 
/*     */ import org.jasypt.encryption.pbe.StandardPBEByteEncryptor;
/*     */ 
/*     */ public final class BasicBinaryEncryptor
/*     */   implements BinaryEncryptor
/*     */ {
/*     */   private final StandardPBEByteEncryptor encryptor;
/*     */ 
/*     */   public BasicBinaryEncryptor()
/*     */   {
/*  69 */     this.encryptor = new StandardPBEByteEncryptor();
/*  70 */     this.encryptor.setAlgorithm("PBEWithMD5AndDES");
/*     */   }
/*     */ 
/*     */   public void setPassword(String password)
/*     */   {
/*  80 */     this.encryptor.setPassword(password);
/*     */   }
/*     */ 
/*     */   public void setPasswordCharArray(char[] password)
/*     */   {
/*  91 */     this.encryptor.setPasswordCharArray(password);
/*     */   }
/*     */ 
/*     */   public byte[] encrypt(byte[] binary)
/*     */   {
/* 102 */     return this.encryptor.encrypt(binary);
/*     */   }
/*     */ 
/*     */   public byte[] decrypt(byte[] encryptedBinary)
/*     */   {
/* 113 */     return this.encryptor.decrypt(encryptedBinary);
/*     */   }
/*     */ }

/* Location:           C:\Users\Raul\Desktop\StarMade\StarMade.jar
 * Qualified Name:     org.jasypt.util.binary.BasicBinaryEncryptor
 * JD-Core Version:    0.6.2
 */