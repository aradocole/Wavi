/*==============================================================================

name:       Foo - example GWT javascript library

purpose:    The examples given in the JsInterop v1.0 spec.

            See   "https://docs.google.com/document/d/"
                  "10fmlEYIHcyead_4R1S5wKGs1t2I7Fnp_PaNaa7XTEk0/"
                  "edit#heading=h.o7amqk9edhb9"

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
import jsinterop.annotations.JsType;
                                       // Foo ================================//
@JsType
public class Foo
{
public int x;
public int y;

public int sum()
{
   return x + y;
}
}//====================================// end class Foo ----------------------//
