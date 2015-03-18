/*   1:    */ package android.support.v13.app;
/*   2:    */ 
/*   3:    */ import android.app.Fragment;
/*   4:    */ import android.app.FragmentManager;
/*   5:    */ import android.app.FragmentTransaction;
/*   6:    */ import android.os.Parcelable;
/*   7:    */ import android.support.v4.view.PagerAdapter;
/*   8:    */ import android.view.View;
/*   9:    */ 
/*  10:    */ public abstract class FragmentPagerAdapter
/*  11:    */   extends PagerAdapter
/*  12:    */ {
/*  13:    */   @SuppressWarnings("unused")
private static final String TAG = "FragmentPagerAdapter";
/*  14:    */   @SuppressWarnings("unused")
private static final boolean DEBUG = false;
/*  15:    */   private final FragmentManager mFragmentManager;
/*  16: 37 */   private FragmentTransaction mCurTransaction = null;
/*  17:    */   
/*  18:    */   public FragmentPagerAdapter(FragmentManager fm)
/*  19:    */   {
/*  20: 40 */     this.mFragmentManager = fm;
/*  21:    */   }
/*  22:    */   
/*  23:    */   public abstract Fragment getItem(int paramInt);
/*  24:    */   
/*  25:    */   public void startUpdate(View container) {}
/*  26:    */   
/*  27:    */   public Object instantiateItem(View container, int position)
/*  28:    */   {
/*  29: 54 */     if (this.mCurTransaction == null) {
/*  30: 55 */       this.mCurTransaction = this.mFragmentManager.beginTransaction();
/*  31:    */     }
/*  32: 59 */     String name = makeFragmentName(container.getId(), position);
/*  33: 60 */     Fragment fragment = this.mFragmentManager.findFragmentByTag(name);
/*  34: 61 */     if (fragment != null)
/*  35:    */     {
/*  36: 63 */       this.mCurTransaction.attach(fragment);
/*  37:    */     }
/*  38:    */     else
/*  39:    */     {
/*  40: 65 */       fragment = getItem(position);
/*  41:    */       
/*  42: 67 */       this.mCurTransaction.add(container.getId(), fragment, makeFragmentName(container.getId(), position));
/*  43:    */     }
/*  44: 71 */     return fragment;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public void destroyItem(View container, int position, Object object)
/*  48:    */   {
/*  49: 76 */     if (this.mCurTransaction == null) {
/*  50: 77 */       this.mCurTransaction = this.mFragmentManager.beginTransaction();
/*  51:    */     }
/*  52: 81 */     this.mCurTransaction.detach((Fragment)object);
/*  53:    */   }
/*  54:    */   
/*  55:    */   public void finishUpdate(View container)
/*  56:    */   {
/*  57: 86 */     if (this.mCurTransaction != null)
/*  58:    */     {
/*  59: 87 */       this.mCurTransaction.commit();
/*  60: 88 */       this.mCurTransaction = null;
/*  61: 89 */       this.mFragmentManager.executePendingTransactions();
/*  62:    */     }
/*  63:    */   }
/*  64:    */   
/*  65:    */   public boolean isViewFromObject(View view, Object object)
/*  66:    */   {
/*  67: 95 */     return ((Fragment)object).getView() == view;
/*  68:    */   }
/*  69:    */   
/*  70:    */   public Parcelable saveState()
/*  71:    */   {
/*  72:100 */     return null;
/*  73:    */   }
/*  74:    */   
/*  75:    */   public void restoreState(Parcelable state, ClassLoader loader) {}
/*  76:    */   
/*  77:    */   private static String makeFragmentName(int viewId, int index)
/*  78:    */   {
/*  79:108 */     return "android:switcher:" + viewId + ":" + index;
/*  80:    */   }
/*  81:    */ }


/* Location:           C:\Users\James\Downloads\Compressed\android-support-v13.jar
 * Qualified Name:     android.support.v13.app.FragmentPagerAdapter
 * JD-Core Version:    0.7.0.1
 */