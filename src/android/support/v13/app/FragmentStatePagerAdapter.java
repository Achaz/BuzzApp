/*   1:    */ package android.support.v13.app;
/*   2:    */ 
/*   3:    */ import android.app.Fragment;
/*   5:    */ import android.app.FragmentManager;
/*   6:    */ import android.app.FragmentTransaction;
/*   7:    */ import android.os.Bundle;
/*   8:    */ import android.os.Parcelable;
/*   9:    */ import android.support.v4.view.PagerAdapter;
/*  10:    */ import android.util.Log;
/*  11:    */ import android.view.View;
/*  12:    */ import java.util.ArrayList;
/*  13:    */ 
/*  14:    */ public abstract class FragmentStatePagerAdapter
/*  15:    */   extends PagerAdapter
/*  16:    */ {
/*  17:    */   @SuppressWarnings("unused")
private static final String TAG = "FragmentStatePagerAdapter";
/*  18:    */   @SuppressWarnings("unused")
private static final boolean DEBUG = false;
/*  19:    */   private final FragmentManager mFragmentManager;
/*  20: 35 */   private FragmentTransaction mCurTransaction = null;
/*  21: 37 */   @SuppressWarnings({ "unchecked", "rawtypes" })
private ArrayList<Fragment.SavedState> mSavedState = new ArrayList();
/*  22: 38 */   @SuppressWarnings({ "unchecked", "rawtypes" })
private ArrayList<Fragment> mFragments = new ArrayList();
/*  23:    */   
/*  24:    */   public FragmentStatePagerAdapter(FragmentManager fm)
/*  25:    */   {
/*  26: 41 */     this.mFragmentManager = fm;
/*  27:    */   }
/*  28:    */   
/*  29:    */   public abstract Fragment getItem(int paramInt);
/*  30:    */   
/*  31:    */   public void startUpdate(View container) {}
/*  32:    */   
/*  33:    */   public Object instantiateItem(View container, int position)
/*  34:    */   {
/*  35: 59 */     if (this.mFragments.size() > position)
/*  36:    */     {
/*  37: 60 */       Fragment f = (Fragment)this.mFragments.get(position);
/*  38: 61 */       if (f != null) {
/*  39: 62 */         return f;
/*  40:    */       }
/*  41:    */     }
/*  42: 66 */     if (this.mCurTransaction == null) {
/*  43: 67 */       this.mCurTransaction = this.mFragmentManager.beginTransaction();
/*  44:    */     }
/*  45: 70 */     Fragment fragment = getItem(position);
/*  46: 72 */     if (this.mSavedState.size() > position)
/*  47:    */     {
/*  48: 73 */       Fragment.SavedState fss = (Fragment.SavedState)this.mSavedState.get(position);
/*  49: 74 */       if (fss != null) {
/*  50: 75 */         fragment.setInitialSavedState(fss);
/*  51:    */       }
/*  52:    */     }
/*  53: 78 */     while (this.mFragments.size() <= position) {
/*  54: 79 */       this.mFragments.add(null);
/*  55:    */     }
/*  56: 81 */     this.mFragments.set(position, fragment);
/*  57: 82 */     this.mCurTransaction.add(container.getId(), fragment);
/*  58:    */     
/*  59: 84 */     return fragment;
/*  60:    */   }
/*  61:    */   
/*  62:    */   public void destroyItem(View container, int position, Object object)
/*  63:    */   {
/*  64: 89 */     Fragment fragment = (Fragment)object;
/*  65: 91 */     if (this.mCurTransaction == null) {
/*  66: 92 */       this.mCurTransaction = this.mFragmentManager.beginTransaction();
/*  67:    */     }
/*  68: 96 */     while (this.mSavedState.size() <= position) {
/*  69: 97 */       this.mSavedState.add(null);
/*  70:    */     }
/*  71: 99 */     this.mSavedState.set(position, this.mFragmentManager.saveFragmentInstanceState(fragment));
/*  72:100 */     this.mFragments.set(position, null);
/*  73:    */     
/*  74:102 */     this.mCurTransaction.remove(fragment);
/*  75:    */   }
/*  76:    */   
/*  77:    */   public void finishUpdate(View container)
/*  78:    */   {
/*  79:107 */     if (this.mCurTransaction != null)
/*  80:    */     {
/*  81:108 */       this.mCurTransaction.commit();
/*  82:109 */       this.mCurTransaction = null;
/*  83:110 */       this.mFragmentManager.executePendingTransactions();
/*  84:    */     }
/*  85:    */   }
/*  86:    */   
/*  87:    */   public boolean isViewFromObject(View view, Object object)
/*  88:    */   {
/*  89:116 */     return ((Fragment)object).getView() == view;
/*  90:    */   }
/*  91:    */   
/*  92:    */   public Parcelable saveState()
/*  93:    */   {
/*  94:121 */     Bundle state = null;
/*  95:122 */     if (this.mSavedState.size() > 0)
/*  96:    */     {
/*  97:123 */       state = new Bundle();
/*  98:124 */       Fragment.SavedState[] fss = new Fragment.SavedState[this.mSavedState.size()];
/*  99:125 */       this.mSavedState.toArray(fss);
/* 100:126 */       state.putParcelableArray("states", fss);
/* 101:    */     }
/* 102:128 */     for (int i = 0; i < this.mFragments.size(); i++)
/* 103:    */     {
/* 104:129 */       Fragment f = (Fragment)this.mFragments.get(i);
/* 105:130 */       if (f != null)
/* 106:    */       {
/* 107:131 */         if (state == null) {
/* 108:132 */           state = new Bundle();
/* 109:    */         }
/* 110:134 */         String key = "f" + i;
/* 111:135 */         this.mFragmentManager.putFragment(state, key, f);
/* 112:    */       }
/* 113:    */     }
/* 114:138 */     return state;
/* 115:    */   }
/* 116:    */   
/* 117:    */   public void restoreState(Parcelable state, ClassLoader loader)
/* 118:    */   {
/* 119:    */     Bundle bundle;
/* 120:143 */     if (state != null)
/* 121:    */     {
/* 122:144 */       bundle = (Bundle)state;
/* 123:145 */       bundle.setClassLoader(loader);
/* 124:146 */       Parcelable[] fss = bundle.getParcelableArray("states");
/* 125:147 */       this.mSavedState.clear();
/* 126:148 */       this.mFragments.clear();
/* 127:149 */       if (fss != null) {
/* 128:150 */         for (int i = 0; i < fss.length; i++) {
/* 129:151 */           this.mSavedState.add((Fragment.SavedState)fss[i]);
/* 130:    */         }
/* 131:    */       }
/* 132:154 */       Iterable<String> keys = bundle.keySet();
/* 133:155 */       for (String key : keys) {
/* 134:156 */         if (key.startsWith("f"))
/* 135:    */         {
/* 136:157 */           int index = Integer.parseInt(key.substring(1));
/* 137:158 */           Fragment f = this.mFragmentManager.getFragment(bundle, key);
/* 138:159 */           if (f != null)
/* 139:    */           {
/* 140:160 */             while (this.mFragments.size() <= index) {
/* 141:161 */               this.mFragments.add(null);
/* 142:    */             }
/* 143:163 */             this.mFragments.set(index, f);
/* 144:    */           }
/* 145:    */           else
/* 146:    */           {
/* 147:165 */             Log.w("FragmentStatePagerAdapter", "Bad fragment at key " + key);
/* 148:    */           }
/* 149:    */         }
/* 150:    */       }
/* 151:    */     }
/* 152:    */   }
/* 153:    */ }
