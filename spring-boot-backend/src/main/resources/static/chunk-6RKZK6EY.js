import{K as u,Kb as j,Lb as z,N as v,R as w,Rb as C,Vb as H,_b as R,a as S,b as k,d as t,e as n,f as h,fc as _,q as m,sb as y,va as I}from"./chunk-DSNOOFSA.js";var A={production:!0,apiUrl:"/api/"};var F=A.apiUrl,D="languageClasses",L="users",N="login",T="register",K="ws";var Y=(()=>{var o,b,x,l,g,f,r,p,U,a;let d=class d{constructor(){n(this,o,void 0);n(this,b,void 0);n(this,x,void 0);n(this,l,void 0);n(this,g,void 0);n(this,f,void 0);n(this,r,void 0);n(this,p,void 0);n(this,U,void 0);n(this,a,void 0);h(this,o,w(z)),h(this,b,w(C)),h(this,x,w(H)),h(this,l,F),h(this,g,t(this,l)+"follow"),h(this,f,t(this,l)+"profiles"),h(this,r,I(null)),h(this,p,I(void 0)),h(this,U,I(void 0)),h(this,a,I(!1)),this.currentUser=y(t(this,r)),this.profile=y(t(this,p)),this.photo=y(t(this,U))}loadCurrentUser(){let s=sessionStorage.getItem(_);if(s===null)return t(this,r).set(null),t(this,a).set(!1),null;let e=new j;return e=e.set("Authorization",`Bearer ${s}`),t(this,o).get(t(this,l)+L,{headers:e}).pipe(m(i=>{let c=i.data;c?(sessionStorage.setItem(_,c.token),t(this,r).set(c),this.loadPhoto(c?.photoUrl).subscribe(),t(this,a).set(!0)):(t(this,a).set(!1),t(this,r).set(null))}))}isAuthenticatedUser(){return t(this,a).call(this)}login(s){return t(this,o).post(t(this,l)+N,s).pipe(u(e=>{let i=e.data;i&&(sessionStorage.setItem(_,i.token),t(this,r).set(i),t(this,a).set(!0))}))}register(s){return t(this,o).post(t(this,l)+T,s).pipe(u(e=>{let i=e.data;i&&(sessionStorage.setItem(_,i.token),t(this,r).set(i),t(this,a).set(!0))}))}logout(){sessionStorage.removeItem(_),t(this,r).set(null),t(this,a).set(!1),t(this,x).navigate([R])}profile$(s){return t(this,o).get(t(this,f)+"/"+s).pipe(u(e=>{this.loadPhoto(e.data.photoUrl).subscribe(),t(this,p).set(e.data)}))}updateProfile(s,e){return t(this,o).put(t(this,f)+"/"+s,e).pipe(m(i=>i.data),u(i=>{t(this,r).update(c=>k(S({},c),{fullName:i.fullName})),t(this,p).set(i)}))}uploadPhoto(s){return t(this,o).put(t(this,f)+"/photo",s).pipe(m(e=>e.data),u(e=>{console.log(this.currentUser());let i=S({},this.currentUser());i.photoUrl=e,this.loadPhoto(e).subscribe(),t(this,r).set(i),console.log(this.currentUser()),t(this,p).update(c=>k(S({},c),{photoUrl:e}))}))}loadPhoto(s){return t(this,o).get(s,{responseType:"blob"}).pipe(m(e=>t(this,b).bypassSecurityTrustUrl(URL.createObjectURL(e))),u(e=>{t(this,U).set(e)}))}followUser(s){return t(this,o).post(t(this,g)+"/"+s,null).pipe(m(e=>e.data),u(e=>{t(this,p).set(e)}))}unFollowUser(s){return t(this,o).delete(t(this,g)+"/"+s).pipe(m(e=>e.data),u(e=>{t(this,p).set(e)}))}};o=new WeakMap,b=new WeakMap,x=new WeakMap,l=new WeakMap,g=new WeakMap,f=new WeakMap,r=new WeakMap,p=new WeakMap,U=new WeakMap,a=new WeakMap,d.\u0275fac=function(e){return new(e||d)},d.\u0275prov=v({token:d,factory:d.\u0275fac,providedIn:"root"});let P=d;return P})();export{F as a,D as b,K as c,Y as d};
