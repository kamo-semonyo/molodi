/*    */ package molodi.files;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.FileInputStream;
/*    */ import java.io.FileOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.ObjectInputStream;
/*    */ import java.io.ObjectOutputStream;
/*    */ import java.util.ArrayList;
/*    */ import java.util.logging.Level;
/*    */ import java.util.logging.Logger;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class fileReader
/*    */ {
/*    */   private ObjectInputStream in;
/*    */   private ObjectOutputStream out;
/* 28 */   private final String FILE = "path.txt";
/*    */   
/*    */   public String getPath()
/*    */   {
/* 32 */     String result = "";
/*    */     try {
/* 34 */       this.in = new ObjectInputStream(new FileInputStream(new File("path.txt")));
/* 35 */       result = this.in.readUTF();
/* 36 */       this.in.close();
/* 37 */       return result;
/*    */     } catch (IOException ex) {}
/* 39 */     return result;
/*    */   }
/*    */   
/*    */   public void savePath(String path)
/*    */   {
/*    */     try {
/* 45 */       this.out = new ObjectOutputStream(new FileOutputStream(new File("path.txt")));
/* 46 */       this.out.writeUTF(path);
/* 47 */       this.out.close();
/*    */     } catch (IOException ex) {
/* 49 */       Logger.getLogger(fileReader.class.getName()).log(Level.SEVERE, null, ex);
/*    */     }
/*    */   }
/*    */   
/*    */   public ArrayList<File> openFolder(String path) {
/* 54 */     File[] temp = null;
/* 55 */     ArrayList<File> items = new ArrayList();ArrayList<File> dir = new ArrayList();ArrayList<File> mid = new ArrayList();
/* 56 */     if (new File(path).isDirectory()) {
/* 57 */       temp = listItems(path);
/* 58 */       for (File file : temp) {
/* 59 */         if ((file.isDirectory()) && (!file.isHidden()) && (!file.getName().startsWith("."))) {
/* 60 */           dir.add(file);
/* 61 */         } else if (isMedia(file.getName()))
/* 62 */           mid.add(file);
/*    */       }
/*    */     }
/* 65 */     items.addAll(dir);
/* 66 */     items.addAll(mid);
/* 67 */     return items;
/*    */   }
/*    */   
/*    */   private File[] listItems(String path) {
/* 71 */     return new File(path).listFiles();
/*    */   }
/*    */   
/*    */   public boolean isMedia(String name) {
/* 75 */     return (name.endsWith(".mp3")) || (isVideo(name));
/*    */   }
/*    */   
/*    */   public boolean isVideo(String name) {
/* 79 */     return name.contains(".mp4");
/*    */   }
/*    */ }


/* Location:              C:\Users\hp-pc\Documents\NetBeansProjects\Molodi\dist\Molodi.jar!\molodi\files\fileReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */