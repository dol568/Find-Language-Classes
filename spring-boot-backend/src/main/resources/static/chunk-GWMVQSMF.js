import{$b as R,L as u,Lb as j,Mb as C,O as v,S,Sb as z,Wb as H,a as I,b as k,d as t,e as h,f as n,gc as _,q as m,tb as w,wa as y}from"./chunk-OQYQIMMA.js";var T={production:!0,apiUrl:"/api/"};var A=T.apiUrl,D="languageClasses",F="users",L="login",N="register",K="http://localhost/ws";var Y=(()=>{var o,b,x,p,g,d,r,l,U,a;let f=class f{constructor(){h(this,o,void 0);h(this,b,void 0);h(this,x,void 0);h(this,p,void 0);h(this,g,void 0);h(this,d,void 0);h(this,r,void 0);h(this,l,void 0);h(this,U,void 0);h(this,a,void 0);n(this,o,S(C)),n(this,b,S(z)),n(this,x,S(H)),n(this,p,A),n(this,g,t(this,p)+"follow"),n(this,d,t(this,p)+"profiles"),n(this,r,y(null)),n(this,l,y(void 0)),n(this,U,y(void 0)),n(this,a,y(!1)),this.currentUser=w(t(this,r)),this.profile=w(t(this,l)),this.photo=w(t(this,U)),this.loadCurrentUser()}loadCurrentUser(){let s=sessionStorage.getItem(_);if(s===null)return t(this,r).set(null),t(this,a).set(!1),null;let e=new j;return e=e.set("Authorization",`Bearer ${s}`),t(this,o).get(t(this,p)+F,{headers:e}).pipe(m(i=>{let c=i.data;c?(sessionStorage.setItem(_,c.token),t(this,r).set(c),this.loadPhoto(c?.photoUrl).subscribe(),t(this,a).set(!0)):(t(this,a).set(!1),t(this,r).set(null))}))}isAuthenticatedUser(){return t(this,a).call(this)}login(s){return t(this,o).post(t(this,p)+L,s).pipe(u(e=>{let i=e.data;i&&(sessionStorage.setItem(_,i.token),t(this,r).set(i),t(this,a).set(!0))}))}register(s){return t(this,o).post(t(this,p)+N,s).pipe(u(e=>{let i=e.data;i&&(sessionStorage.setItem(_,i.token),t(this,r).set(i),t(this,a).set(!0))}))}logout(){sessionStorage.removeItem(_),t(this,r).set(null),t(this,a).set(!1),t(this,x).navigate([R])}profile$(s){return t(this,o).get(t(this,d)+"/"+s).pipe(u(e=>{this.loadPhoto(e.data.photoUrl).subscribe(),t(this,l).set(e.data)}))}updateProfile(s,e){return t(this,o).put(t(this,d)+"/"+s,e).pipe(m(i=>i.data),u(i=>{t(this,r).update(c=>k(I({},c),{fullName:i.fullName})),t(this,l).set(i)}))}uploadPhoto(s){return t(this,o).put(t(this,d)+"/photo",s).pipe(m(e=>e.data),u(e=>{console.log(this.currentUser());let i=I({},this.currentUser());i.photoUrl=e,this.loadPhoto(e).subscribe(),t(this,r).set(i),console.log(this.currentUser()),t(this,l).update(c=>k(I({},c),{photoUrl:e}))}))}loadPhoto(s){return t(this,o).get(s,{responseType:"blob"}).pipe(m(e=>t(this,b).bypassSecurityTrustUrl(URL.createObjectURL(e))),u(e=>{t(this,U).set(e)}))}loadPhoto2(s){return t(this,o).get(s,{responseType:"blob"})}followUser(s){return t(this,o).post(t(this,g)+"/"+s,null).pipe(m(e=>e.data),u(e=>{t(this,l).set(e)}))}unFollowUser(s){return t(this,o).delete(t(this,g)+"/"+s).pipe(m(e=>e.data),u(e=>{t(this,l).set(e)}))}};o=new WeakMap,b=new WeakMap,x=new WeakMap,p=new WeakMap,g=new WeakMap,d=new WeakMap,r=new WeakMap,l=new WeakMap,U=new WeakMap,a=new WeakMap,f.\u0275fac=function(e){return new(e||f)},f.\u0275prov=v({token:f,factory:f.\u0275fac,providedIn:"root"});let P=f;return P})();export{A as a,D as b,K as c,Y as d};
