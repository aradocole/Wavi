/*==============================================================================

name:       Bar.java

purpose:    Tour Of Heroes Java App.

history:    Sat Apr 7, 2018 10:30:00 (Giavaneers - LBM) created

notes:

                  This program was created by Giavaneers
        and is the confidential and proprietary product of Giavaneers Inc.
      Any unauthorized use, reproduction or transfer is strictly prohibited.

                     COPYRIGHT 2018 BY GIAVANEERS, INC.
      (Subject to limited distribution and restricted disclosure only).
                           All rights reserved.


==============================================================================*/
                                       // package --------------------------- //
package jwave.acme;
                                       // imports --------------------------- //
import com.google.gwt.core.client.EntryPoint;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
                                       // Bar ================================//
public class Bar implements EntryPoint
{
                                       // class constants --------------------//
                                       // (none)                              //
                                       // class variables ------------------- //
                                       // (none)                              //
                                       // public instance variables --------- //
                                       // (none)                              //
                                       // protected instance variables -------//
                                       // (none)                              //
                                       // private instance variables -------- //
                                       // (none)                              //
/*------------------------------------------------------------------------------

@name       onModuleLoad - standard gwt entry point method
                                                                              */
                                                                             /**
            Standard gwt entry point method. This implementation specifies one
            of two different app configurations to lauch based on the url
            parameters.

@return     void

@history    Sat Apr 7, 2018 10:30:00 (Giavaneers - LBM) created

@notes
                                                                              */
//------------------------------------------------------------------------------
public void onModuleLoad()
{
                                       // signal the test script gwt is ready //
   new MyTestScript().onGWTReady();
}
/*==============================================================================

name:       MyTestScript - MyTestScript native interface

purpose:    MyTestScript native interface

history:    Sat Apr 7, 2018 10:30:00 (Giavaneers - LBM) created

notes:

==============================================================================*/
@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public static class MyTestScript
{
                                       // constants ------------------------- //
                                       // (none)                              //
                                       // class variables ------------------- //
                                       // (none)                              //
                                       // public instance variables --------- //
                                       // (none)                              //
                                       // protected instance variables -------//
                                       // (none)                              //
                                       // private instance variables -------- //
                                       // (none)                              //

/*------------------------------------------------------------------------------

@name       onGWTReady - onGWTReady handler
                                                                              */
                                                                             /**
            onGWTReady handler.

@return     void

@history    Sat Apr 7, 2018 10:30:00 (Giavaneers - LBM) created

@notes

                                                                              */
//------------------------------------------------------------------------------
public native void onGWTReady();

}//====================================// end MyTestScript -------------------//
}//====================================// end Bar ============================//
