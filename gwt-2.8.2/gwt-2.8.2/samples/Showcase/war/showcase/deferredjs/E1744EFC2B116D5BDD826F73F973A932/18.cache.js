$wnd.showcase.runAsyncCallback18("function N0(){}\nfunction obc(a,b){$w(a.a,b)}\nfunction QJb(a,b){this.b=a;this.a=b}\nfunction SJb(a,b){this.b=a;this.a=b}\nfunction l1(a){return lqb(Y0,a)}\nfunction M0(){M0=xpb;L0=new N0}\nfunction Abc(){wbc();zbc.call(this,Mo($doc,'password'),'gwt-PasswordTextBox')}\nfunction KJb(a,b){z4b(b,'\\u0645\\u062E\\u062A\\u0627\\u0631\\u0629: '+a.$g()+', '+a._g())}\nfunction ejc(b){try{var c=b.document.selection.createRange();if(c.parentElement()!==b)return 0;return c.text.length}catch(a){return 0}}\nfunction djc(b){try{var c=b.document.selection.createRange();if(c.parentElement()!==b)return -1;return -c.move(uGc,-65535)}catch(a){return 0}}\nfunction IJb(a,b){var c,d;c=new s8b;c.e[CEc]=4;p8b(c,a);if(b){d=new D4b('\\u0645\\u062E\\u062A\\u0627\\u0631\\u0629: 0, 0');Hh(a,new QJb(a,d),(iu(),iu(),hu));Hh(a,new SJb(a,d),(Nt(),Nt(),Mt));p8b(c,d)}return c}\nfunction gjc(b){try{var c=b.document.selection.createRange();if(c.parentElement()!==b)return 0;var d=c.text.length;var e=0;var f=c.duplicate();f.moveEnd(uGc,-1);var g=f.text.length;while(g==d&&f.parentElement()==b&&c.compareEndPoints('StartToEnd',f)<=0){e+=2;f.moveEnd(uGc,-1);g=f.text.length}return d+e}catch(a){return 0}}\nfunction fjc(b){try{var c=b.document.selection.createRange();if(c.parentElement()!==b)return -1;var d=c.duplicate();d.moveToElementText(b);d.setEndPoint('EndToStart',c);var e=d.text.length;var f=0;var g=d.duplicate();g.moveEnd(uGc,-1);var h=g.text.length;while(h==e&&g.parentElement()==b){f+=2;g.moveEnd(uGc,-1);h=g.text.length}return e+f}catch(a){return 0}}\nfunction JJb(){var a,b,c,d,e,f;f=new Yhc;f.e[CEc]=5;d=new ybc;Hhc(d.hb,'','cwBasicText-textbox');obc(d,(M0(),M0(),L0));b=new ybc;Hhc(b.hb,'','cwBasicText-textbox-disabled');b.hb[qFc]=sGc;Zw(b.a);b.hb[uDc]=true;Vhc(f,new I4b('<b>\\u0645\\u0631\\u0628\\u0639 \\u0646\\u0635 \\u0639\\u0627\\u062F\\u064A:<\\/b>'));Vhc(f,IJb(d,true));Vhc(f,IJb(b,false));c=new Abc;Hhc(c.hb,'','cwBasicText-password');a=new Abc;Hhc(a.hb,'','cwBasicText-password-disabled');a.hb[qFc]=sGc;Zw(a.a);a.hb[uDc]=true;Vhc(f,new I4b('<br><br><b>\\u0645\\u0631\\u0628\\u0639 \\u0646\\u0635 \\u0643\\u0644\\u0645\\u0629 \\u0627\\u0644\\u0633\\u0631:<\\/b>'));Vhc(f,IJb(c,true));Vhc(f,IJb(a,false));e=new agc;Hhc(e.hb,'','cwBasicText-textarea');e.hb.rows=5;Vhc(f,new I4b('<br><br><b>\\u0645\\u0646\\u0637\\u0642\\u0629 \\u0627\\u0644\\u0646\\u0635:<\\/b>'));Vhc(f,IJb(e,true));return f}\nvar sGc='\\u0642\\u0631\\u0627\\u0621\\u0629 \\u0641\\u0642\\u0637',uGc='character';wpb(887,1205,{},N0);_.ve=function O0(a){return l1((f1(),a))?(KA(),JA):(KA(),IA)};var L0;var eab=zoc(CBc,'AnyRtlDirectionEstimator',887);wpb(459,1,fDc);_.Bc=function PJb(){Qrb(this.a,JJb())};wpb(460,1,tGc,QJb);_.Uc=function RJb(a){KJb(this.b,this.a)};var Afb=zoc(rDc,'CwBasicText/2',460);wpb(461,1,ZCc,SJb);_.Sc=function TJb(a){KJb(this.b,this.a)};var Bfb=zoc(rDc,'CwBasicText/3',461);wpb(766,264,IAc);_.$g=function rbc(){return djc(this.hb)};_._g=function sbc(){return ejc(this.hb)};wpb(343,50,IAc,Abc);var tkb=zoc(GAc,'PasswordTextBox',343);wpb(233,329,IAc);_.$g=function bgc(){return fjc(this.hb)};_._g=function cgc(){return gjc(this.hb)};Szc(wl)(18);\n//# sourceURL=showcase-18.js\n")
