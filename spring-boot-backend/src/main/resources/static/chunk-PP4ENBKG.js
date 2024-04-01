import{M as b,Nb as F,P,Pb as R,T as I,Vb as H,Zb as A,a as z,b as D,cc as N,d as t,e as a,f as g,g as $,jc as C,lc as O,mc as S,o as L,q as U,vb as k,ya as x}from"./chunk-63EQNU4F.js";var B={production:!0,apiUrl:"/api/"};var K=B.apiUrl,Z="languageClasses",q="users",G="login",J="register";var M=(()=>{var h,m,r;let p=class p{constructor(){a(this,h,I(H));a(this,m,I(R));a(this,r,{"":`${O}/user.jpg`,french:`${S}/french.jpg`,english:`${S}/english.jpg`,german:`${S}/german.jpg`,italian:`${S}/italian.jpg`,polish:`${S}/polish.jpg`,spanish:`${S}/spanish.jpg`})}setCachedImage(l,n){t(this,r)[l]=n}getCachedImage(l){return t(this,r)[l]}getImage(l){let n=l?l.toLowerCase():"",f=t(this,r)[n];return f?(console.log("saved"),L(f)):t(this,m).get(l,{responseType:"blob"}).pipe(U(c=>{console.log("http");let u=t(this,h).bypassSecurityTrustUrl(URL.createObjectURL(c));return t(this,r)[n]=u,u}))}};h=new WeakMap,m=new WeakMap,r=new WeakMap,p.\u0275fac=function(n){return new(n||p)},p.\u0275prov=P({token:p,factory:p.\u0275fac,providedIn:"root"});let _=p;return _})();var bt=(()=>{var h,m,r,p,d,l,n,f,c,u,w,j,v,T,E,Q;let y=class y{constructor(){a(this,v);a(this,E);a(this,h,void 0);a(this,m,void 0);a(this,r,void 0);a(this,p,void 0);a(this,d,void 0);a(this,l,void 0);a(this,n,void 0);a(this,f,void 0);a(this,c,void 0);a(this,u,void 0);a(this,w,void 0);a(this,j,void 0);g(this,h,I(R)),g(this,m,I(H)),g(this,r,I(M)),g(this,p,I(A)),g(this,d,K),g(this,l,t(this,d)+"follow"),g(this,n,t(this,d)+"profiles"),g(this,f,x(!1)),g(this,c,x(null)),g(this,u,x(void 0)),g(this,w,x(void 0)),g(this,j,x(void 0)),this.currentUser=k(t(this,c)),this.profile=k(t(this,u)),this.photo=k(t(this,w)),this.loggedInUserPhoto=k(t(this,j))}loadCurrentUser(){let o=sessionStorage.getItem(C);if(o===null)return t(this,c).set(null),t(this,f).set(!1),L(null);let e=new F;return e=e.set("Authorization",`Bearer ${o}`),t(this,h).get(t(this,d)+q,{headers:e}).pipe(U(s=>{let i=s.data;i?(sessionStorage.setItem(C,i.token),t(this,r).getImage(i?.photoUrl).subscribe(V=>{i.photoUrl=V}),$(this,v,T).call(this,i)):(t(this,f).set(!1),t(this,c).set(null))}))}isAuthenticatedUser(){return t(this,f).call(this)}login(o){return t(this,h).post(t(this,d)+G,o).pipe(b(e=>{let s=e.data;s&&(sessionStorage.setItem(C,s.token),$(this,v,T).call(this,s))}))}register(o){return t(this,h).post(t(this,d)+J,o).pipe(b(e=>{let s=e.data;s&&(sessionStorage.setItem(C,s.token),$(this,v,T).call(this,s))}))}logout(){sessionStorage.removeItem(C),t(this,c).set(null),t(this,f).set(!1),t(this,p).navigate([N])}profile$(o){return t(this,h).get(t(this,n)+"/"+o).pipe(b(e=>{t(this,r).getImage(e.data.photoUrl).subscribe(s=>{t(this,w).set(s)}),e.data.followers.forEach(s=>{t(this,r).getImage(s.photoUrl).subscribe(i=>s.photoUrl=i)}),e.data.followings.forEach(s=>{t(this,r).getImage(s.photoUrl).subscribe(i=>s.photoUrl=i)}),t(this,u).set(e.data)}))}updateProfile(o,e){return t(this,h).put(t(this,n)+"/"+o,e).pipe(U(s=>s.data),b(s=>{t(this,c).update(i=>D(z({},i),{fullName:s.fullName})),t(this,u).set(s)}))}uploadPhoto(o){return t(this,h).put(t(this,n)+"/photo",o).pipe(U(e=>e.data),b(e=>{let s=z({},this.currentUser());s.photoUrl=e,$(this,E,Q).call(this,e).subscribe(i=>{t(this,w).set(i),t(this,j).set(i)}),t(this,c).set(s),t(this,u).update(i=>D(z({},i),{photoUrl:e}))}))}followUser(o){return t(this,h).post(t(this,l)+"/"+o,null).pipe(U(e=>e.data),b(e=>{e.followers.forEach(s=>{t(this,r).getImage(s.photoUrl).subscribe(i=>s.photoUrl=i)}),e.followings.forEach(s=>{t(this,r).getImage(s.photoUrl).subscribe(i=>s.photoUrl=i)}),t(this,u).set(e)}))}unFollowUser(o){return t(this,h).delete(t(this,l)+"/"+o).pipe(U(e=>e.data),b(e=>{e.followers.forEach(s=>{t(this,r).getImage(s.photoUrl).subscribe(i=>s.photoUrl=i)}),e.followings.forEach(s=>{t(this,r).getImage(s.photoUrl).subscribe(i=>s.photoUrl=i)}),t(this,u).set(e)}))}};h=new WeakMap,m=new WeakMap,r=new WeakMap,p=new WeakMap,d=new WeakMap,l=new WeakMap,n=new WeakMap,f=new WeakMap,c=new WeakMap,u=new WeakMap,w=new WeakMap,j=new WeakMap,v=new WeakSet,T=function(o){t(this,c).set(o),t(this,r).getImage(o.photoUrl).subscribe(e=>{t(this,j).set(e)}),t(this,f).set(!0)},E=new WeakSet,Q=function(o){return t(this,h).get(o,{responseType:"blob"}).pipe(U(e=>t(this,m).bypassSecurityTrustUrl(URL.createObjectURL(e))),b(e=>t(this,r).setCachedImage(o,e)))},y.\u0275fac=function(e){return new(e||y)},y.\u0275prov=P({token:y,factory:y.\u0275fac,providedIn:"root"});let _=y;return _})();export{K as a,Z as b,M as c,bt as d};
